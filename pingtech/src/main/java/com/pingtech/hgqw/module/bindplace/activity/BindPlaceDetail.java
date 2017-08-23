package com.pingtech.hgqw.module.bindplace.activity;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.util.Pair;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.pingtech.R;
import com.pingtech.hgqw.activity.DutyPersonlistActivity;
import com.pingtech.hgqw.activity.MyActivity;
import com.pingtech.hgqw.activity.ShipDetailActivity;
import com.pingtech.hgqw.base.BaseApplication;
import com.pingtech.hgqw.entity.LoginUser;
import com.pingtech.hgqw.interf.OffLineResult;
import com.pingtech.hgqw.interf.OnHttpResult;
import com.pingtech.hgqw.module.bindplace.action.BindPlaceAction;
import com.pingtech.hgqw.module.bindplace.entity.BindPlace;
import com.pingtech.hgqw.module.bindplace.request.RequestForPlace;
import com.pingtech.hgqw.module.bindplace.utils.PullXmlBindPlace;
import com.pingtech.hgqw.module.exception.activity.Exceptioninfo;
import com.pingtech.hgqw.module.offline.base.utils.DbUtil;
import com.pingtech.hgqw.module.offline.base.utils.OffLineManager;
import com.pingtech.hgqw.module.offline.util.OffLineUtil;
import com.pingtech.hgqw.module.xtgl.service.OffDataDownload;
import com.pingtech.hgqw.module.xtgl.service.OffDataDownloadForBd;
import com.pingtech.hgqw.module.xunjian.action.XunJianAction;
import com.pingtech.hgqw.module.xunjian.utils.XcUtil;
import com.pingtech.hgqw.utils.BasicNameValuePair;
import com.pingtech.hgqw.utils.DateUtils;
import com.pingtech.hgqw.utils.Log;
import com.pingtech.hgqw.utils.NVPairTOMap;
import com.pingtech.hgqw.utils.StringUtils;
import com.pingtech.hgqw.utils.SystemSetting;
import com.pingtech.hgqw.web.NetWorkManager;
import com.pingtech.hgqw.widget.HgqwToast;

/**
 * 显示船舶详情界面，在该界面可以做船舶绑定、解除绑定、船舶抵港、船舶离港、查看执勤人员等操作
 * 
 * */
public class BindPlaceDetail extends MyActivity implements OnHttpResult, OffLineResult {
	private static final String TAG = "BindPlaceDetailActivity";

	/** 刷信息钉情况下发起的http请求的type */
	private static final int HTTPREQUEST_TYPE_FOR_BINDPLACE = 0;

	/** 执勤对象类型:船舶0 卡口(区域)1 码头2 泊位3 */
	private String zqdxlx = "";

	private String BINDPLACE = null;

	private Button bind = null;

	private Button unbindplace = null;

	private Button btnSaveNote = null;

	private boolean isDownload = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Intent intent = getIntent();
		BINDPLACE = intent.getStringExtra("BINDPLACE");
		isDownload = intent.getBooleanExtra("isDownload", false);
		Log.i(TAG, "onCreate()");
		super.onCreate(savedInstanceState, R.layout.bindplace_detail);

		find();

		if (BINDPLACE != null && "1".equals(BINDPLACE)) {
			unbindplace.setVisibility(View.VISIBLE);
			setMyActiveTitle(getString(R.string.xunchaxunjian) + ">" + getString(R.string.unbindPlace));
		} else {
			findViewById(R.id.btnExceptionRegist_object).setVisibility(View.VISIBLE);
			bind.setVisibility(View.VISIBLE);
			if (isDownload) {
				bind.setEnabled(true);
				bind.setText(R.string.bindPlace);
			}
			setMyActiveTitle(getString(R.string.xunchaxunjian) + ">" + getString(R.string.bindPlace));
			btnSaveNote.setVisibility(View.VISIBLE);
		}

		bindPlaceDetail();

		if (!BaseApplication.instent.getWebState()) {
			unbindplace.setEnabled(false);
		}

	}

	private void find() {
		bind = (Button) findViewById(R.id.btnNoIDCard);
		unbindplace = (Button) findViewById(R.id.btnunbindplace);
		btnSaveNote = (Button) findViewById(R.id.save_note);
	}

	public void click(View v) {
		switch (v.getId()) {
		case R.id.btnunbindplace:
			showDialogForUnbind();
			break;
		case R.id.btnNoIDCard:
			bindOrUnBind();
			break;
		case R.id.btnExceptionRegist_object:
			exceptionRegist();
			break;
		case R.id.save_note:
			saveXj();
			break;
		default:
			break;
		}
	}

	private void bindOrUnBind() {
		if (isDownload) {
			// 先请求绑定接口
			new RequestForPlace(handler, this).requestBuildXcxjRelation(zqdxlx, SystemSetting.xunJianId, RequestForPlace.BUILD_XCXJ_RELATION_BIND);
		}
	}

	@Override
	public void onHttpResult(String str, int httpRequestType) {
		if (httpRequestType == ShipDetailActivity.SAVE_XJ) {
			if (str != null) {
				if ("-1".equals(str)) {
					HgqwToast.getToastView(getApplicationContext(), getString(R.string.save_failure)).show();
				} else if ("1".equals(str)) {
					HgqwToast.getToastView(getApplicationContext(), getString(R.string.save_success)).show();
				}
			} else {
				HgqwToast.getToastView(getApplicationContext(), getString(R.string.data_download_failure_info)).show();
			}
		} else if (HTTPREQUEST_TYPE_FOR_BINDPLACE == httpRequestType) {
			if (str != null) {
				BindPlace bindPlace = PullXmlBindPlace.parseXMLData(str, getApplicationContext());
				if (bindPlace.isResult()) {
					bindPlaceDetail();
				} else {
					if (progressDialog != null) {
						progressDialog.dismiss();
						progressDialog = null;
					}
					if (bindPlace.getInfo() != null && !"".equals(bindPlace.getInfo())) {
						HgqwToast.getToastView(getApplicationContext(), bindPlace.getInfo()).show();
					} else {
						HgqwToast.getToastView(getApplicationContext(), getString(R.string.data_download_failure_info)).show();
					}
				}
			} else {
				if (progressDialog != null) {
					progressDialog.dismiss();
					progressDialog = null;
				}
				HgqwToast.getToastView(getApplicationContext(), getString(R.string.data_download_failure_info)).show();
			}
		}
	}

	private void getInfoByCard() {
		// 电子标签
		String url = "getBaseInfoByCard";
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("type", SystemSetting.xunJianType));
		params.add(new BasicNameValuePair("id", SystemSetting.xunJianId));
		if (progressDialog != null) {
			return;
		}
		progressDialog = new ProgressDialog(this);
		progressDialog.setTitle(getString(R.string.Validing));
		progressDialog.setMessage(getString(R.string.waiting));
		progressDialog.setCancelable(false);
		progressDialog.setIndeterminate(false);
		progressDialog.show();
		// if (BaseApplication.instent.getWebState()) {
		// NetWorkManager.request(this, url, params,
		// HTTPREQUEST_TYPE_FOR_BINDPLACE);
		// } else {
		OffLineManager.request(this, new BindPlaceAction(), url, NVPairTOMap.nameValuePairTOMap(params), HTTPREQUEST_TYPE_FOR_BINDPLACE);
		// }
	}

	public void bindPlaceDetail() {
		if (SystemSetting.xunJianType != null) {
			if (SystemSetting.xunJianType.equals("bw")) {
				zqdxlx = "3";
				((TextView) findViewById(R.id.arg1)).setText(Html.fromHtml(getString(R.string.bw_name) + "<font color=\"#acacac\">"
						+ (SystemSetting.xunJianName == null ? "" : SystemSetting.xunJianName) + "</font>"));
				((TextView) findViewById(R.id.arg2)).setText(Html.fromHtml(getString(R.string.bw_ssmt) + "<font color=\"#acacac\">"
						+ (SystemSetting.xunJianMTname == null ? "" : SystemSetting.xunJianMTname) + "</font>"));
				((TextView) findViewById(R.id.arg3)).setText(Html.fromHtml(getString(R.string.bw_kbnl) + "<font color=\"#acacac\">"
						+ (SystemSetting.xunJianKbnl == null ? "" : SystemSetting.xunJianKbnl) + "</font>"));
				((TextView) findViewById(R.id.arg4)).setText(Html.fromHtml(getString(R.string.bw_zxhz) + "<font color=\"#acacac\">"
						+ (SystemSetting.xunJianZxhz == null ? "" : SystemSetting.xunJianZxhz) + "</font>"));
				((TextView) findViewById(R.id.arg5)).setVisibility(View.GONE);
			} else if (SystemSetting.xunJianType.equals("mt")) {
				zqdxlx = "2";
				((TextView) findViewById(R.id.arg1)).setText(Html.fromHtml(getString(R.string.mt_name) + "<font color=\"#acacac\">"
						+ (SystemSetting.xunJianName == null ? "" : SystemSetting.xunJianName) + "</font>"));
				((TextView) findViewById(R.id.arg2)).setText(Html.fromHtml(getString(R.string.mt_zax) + "<font color=\"#acacac\">"
						+ (SystemSetting.xunJianMtzax == null ? "" : SystemSetting.xunJianMtzax) + "</font>"));
				((TextView) findViewById(R.id.arg3)).setText(Html.fromHtml(getString(R.string.mt_gsgs) + "<font color=\"#acacac\">"
						+ (SystemSetting.xunJianMtgsgs == null ? "" : SystemSetting.xunJianMtgsgs) + "</font>"));
				((TextView) findViewById(R.id.arg4)).setText(Html.fromHtml(getString(R.string.mt_mtgm) + "<font color=\"#acacac\">"
						+ (SystemSetting.xunJianMtgm == null ? "" : SystemSetting.xunJianMtgm) + "</font>"));
				((TextView) findViewById(R.id.arg5)).setVisibility(View.GONE);
			} else if (SystemSetting.xunJianType.equals("md")) {
				zqdxlx = "3";
				((TextView) findViewById(R.id.arg1)).setText(Html.fromHtml(getString(R.string.md_name) + "<font color=\"#acacac\">"
						+ (SystemSetting.xunJianName == null ? "" : SystemSetting.xunJianName) + "</font>"));
				((TextView) findViewById(R.id.arg2)).setText(Html.fromHtml(getString(R.string.md_wz) + "<font color=\"#acacac\">"
						+ (SystemSetting.xunJianWz == null ? "" : SystemSetting.xunJianWz) + "</font>"));
				((TextView) findViewById(R.id.arg3)).setText(Html.fromHtml(getString(R.string.md_zdmbcbdw) + "<font color=\"#acacac\">"
						+ (SystemSetting.xunJianZdmbcbdw == null ? "" : SystemSetting.xunJianZdmbcbdw) + "</font>"));
				((TextView) findViewById(R.id.arg4)).setText(Html.fromHtml(getString(R.string.md_ss) + "<font color=\"#acacac\">"
						+ (SystemSetting.xunJianSs == null ? "" : SystemSetting.xunJianSs) + "</font>"));
				((TextView) findViewById(R.id.arg5)).setText(Html.fromHtml(getString(R.string.md_mbdsl) + "<font color=\"#acacac\">"
						+ (SystemSetting.xunJianMbdsl == null ? "" : SystemSetting.xunJianMbdsl) + "</font>"));
				((TextView) findViewById(R.id.arg5)).setVisibility(View.VISIBLE);
			} else if (SystemSetting.xunJianType.equals("qy")) {
				zqdxlx = "1";
				((TextView) findViewById(R.id.arg1)).setText(Html.fromHtml(getString(R.string.qy_name) + "<font color=\"#acacac\">"
						+ (SystemSetting.xunJianName == null ? "" : SystemSetting.xunJianName) + "</font>"));
				String temp = "";
				if ("kkqy".equalsIgnoreCase(SystemSetting.xunJianQylx)) {
					temp = "卡口区域";
				} else if ("卡口区域".equalsIgnoreCase(SystemSetting.xunJianQylx)) {
					temp = "卡口区域";
				} else if ("jkqy".equalsIgnoreCase(SystemSetting.xunJianQylx)) {
					temp = "监控区域";
				} else if ("监控区域".equalsIgnoreCase(SystemSetting.xunJianQylx)) {
					temp = "监控区域";
				}
				((TextView) findViewById(R.id.arg2))
						.setText(Html.fromHtml(getString(R.string.qy_lx) + "<font color=\"#acacac\">" + temp + "</font>"));
				((TextView) findViewById(R.id.arg3)).setText(Html.fromHtml(getString(R.string.qy_fw) + "<font color=\"#acacac\">"
						+ (SystemSetting.xunJianQyfw == null ? "" : SystemSetting.xunJianQyfw) + "</font>"));
				((TextView) findViewById(R.id.arg4)).setText(Html.fromHtml(getString(R.string.qy_xxdz) + "<font color=\"#acacac\">"
						+ (SystemSetting.xunJianXxxx == null ? "" : SystemSetting.xunJianXxxx) + "</font>"));
				((TextView) findViewById(R.id.arg5)).setVisibility(View.GONE);
				if (SystemSetting.xunJianQylx.equalsIgnoreCase("kkqy") || SystemSetting.xunJianQylx.equalsIgnoreCase("卡口区域")) {

					if (BINDPLACE != null && "1".equals(BINDPLACE)) {
						findViewById(R.id.btnDuty).setVisibility(View.GONE);
					} else {
						findViewById(R.id.btnDuty).setVisibility(View.VISIBLE);
						((Button) findViewById(R.id.btnDuty)).setOnClickListener(new OnClickListener() {
							public void onClick(View v) {
								Intent intent = new Intent();
								intent.putExtra("hc", SystemSetting.xunJianId);
								intent.putExtra("from", "3");
								intent.setClass(getApplicationContext(), DutyPersonlistActivity.class);
								startActivity(intent);
							}
						});
					}

				}
			} else if (SystemSetting.xunJianType.equals("ft")) {
				zqdxlx = "3";
				((TextView) findViewById(R.id.arg1)).setText(Html.fromHtml(getString(R.string.ft_name) + "<font color=\"#acacac\">"
						+ (SystemSetting.xunJianName == null ? "" : SystemSetting.xunJianName) + "</font>"));
				((TextView) findViewById(R.id.arg2)).setText(Html.fromHtml(getString(R.string.md_wz) + "<font color=\"#acacac\">"
						+ (SystemSetting.xunJianWz == null ? "" : SystemSetting.xunJianWz) + "</font>"));
				((TextView) findViewById(R.id.arg3)).setText(Html.fromHtml(getString(R.string.ft_zdgkcbdw) + "<font color=\"#acacac\">"
						+ (SystemSetting.xunJianZdgkcbdw == null ? "" : SystemSetting.xunJianZdgkcbdw) + "</font>"));
				((TextView) findViewById(R.id.arg4)).setText(Html.fromHtml(getString(R.string.md_ss) + "<font color=\"#acacac\">"
						+ (SystemSetting.xunJianSs == null ? "" : SystemSetting.xunJianSs) + "</font>"));
				((TextView) findViewById(R.id.arg5)).setVisibility(View.GONE);
			} else {
				zqdxlx = "";
				((TextView) findViewById(R.id.arg1)).setVisibility(View.GONE);
				((TextView) findViewById(R.id.arg2)).setVisibility(View.GONE);
				((TextView) findViewById(R.id.arg3)).setVisibility(View.GONE);
				((TextView) findViewById(R.id.arg4)).setVisibility(View.GONE);
				((TextView) findViewById(R.id.arg5)).setVisibility(View.GONE);
				findViewById(R.id.btnExceptionRegist).setVisibility(View.GONE);
			}
		}

	}

	private void exceptionRegist() {
		Intent intent = new Intent();
		intent.putExtra("objecttype", "05");
		intent.putExtra("windowtype", "03");
		if (SystemSetting.xunJianId != null && SystemSetting.xunJianId.length() > 0) {
			if (SystemSetting.xunJianType != null && SystemSetting.xunJianType.equals("bw")) {
				intent.putExtra("berthcode", SystemSetting.xunJianId);
				intent.putExtra("berthname", SystemSetting.xunJianName);
				intent.putExtra("dockcode", SystemSetting.xunJianMTid);
				intent.putExtra("dockname", SystemSetting.xunJianMTname);
				intent.putExtra("scene", "02");
			} else if (SystemSetting.xunJianType != null && SystemSetting.xunJianType.equals("mt")) {
				intent.putExtra("dockcode", SystemSetting.xunJianId);
				intent.putExtra("dockname", SystemSetting.xunJianName);
				intent.putExtra("scene", "03");
			} else if (SystemSetting.xunJianType != null && SystemSetting.xunJianType.equals("qy")) {
				intent.putExtra("areacode", SystemSetting.xunJianId);
				intent.putExtra("areaname", SystemSetting.xunJianName);
				intent.putExtra("scene", "04");
			}
		}
		intent.putExtra("from", "03");
		intent.setClass(getApplicationContext(), Exceptioninfo.class);
		startActivity(intent);
	}

	private void showDialogForUnbind() {

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(R.string.place_unbind_comfirm);
		builder.setTitle(R.string.info);
		builder.setPositiveButton(R.string.yes, new AlertDialog.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				unBind();
			}

		});
		builder.setNegativeButton(R.string.no, new AlertDialog.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();

			}
		});
		builder.create().show();

	}

	private void unBind( ) {
		new RequestForPlace(handler, this).requestBuildXcxjRelation(zqdxlx, SystemSetting.xunJianId, RequestForPlace.BUILD_XCXJ_RELATION_UNBIND);
	}

	protected void deleteHistory() {
		DbUtil.deleteForXjdd();
	}

	@Override
	public void offLineResult(Pair<Boolean, Object> res, int offLineRequestType) {
		// TODO Auto-generated method stub
		if (res.first) {
			onHttpResult("1", offLineRequestType);
		} else {
			onHttpResult("-1", offLineRequestType);
		}

	}

	private void saveXj() {
		String url = "saveXj";
		String placeName = SystemSetting.xunJianName;
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		// 地点绑定，“航次”字段传值 ，绑定地点id
		params.add(new BasicNameValuePair("voyageNumber", SystemSetting.xunJianId));

		String xjlx = SystemSetting.xunJianType;
		// 对象类型（人员01、车辆02、船舶03、设备04、区域05）
		if (StringUtils.isNotEmpty(xjlx)) {
			if ("qy".equals(xjlx)) {
				params.add(new BasicNameValuePair("dxlx", "05"));
			} else {
				params.add(new BasicNameValuePair("dxlx", xjlx));
			}
		}
		params.add(new BasicNameValuePair("userid", LoginUser.getCurrentLoginUser().getUserID()));
		params.add(new BasicNameValuePair("jcsj", DateUtils.dateToString(new Date())));
		// 附加经纬度信息
		params.add(new BasicNameValuePair("longitude", BaseApplication.instent.getLongitude()));// 经度
		params.add(new BasicNameValuePair("latitude", BaseApplication.instent.getLatitude()));// 纬度

		if (BaseApplication.instent.getWebState()) {
			NetWorkManager.request(BindPlaceDetail.this, url, params, ShipDetailActivity.SAVE_XJ);
		} else {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("jcsj", DateUtils.dateToString(new Date()));
			map.put("dxlx", "03");
			// map.put("kacbqkid", kacbqkid);
			map.put("jcr", LoginUser.getCurrentLoginUser().getUserID());
			// 地点绑定，“航次”字段传值 ，绑定地点id
			map.put("hc", SystemSetting.xunJianId);
			OffLineManager.request(BindPlaceDetail.this, new XunJianAction(), url, map, ShipDetailActivity.SAVE_XJ);
		}
	}

	private OffDataDownloadForBd dataDownload = null;

	private void downloadOfflineData() {
		dataDownload = new OffDataDownloadForBd(handler, null, OffLineUtil.DOWNLOAD_FOR_MT_OR_BW_QY, 3);
		dataDownload.requestAgain();
		if (BaseApplication.instent.progressDialog != null) {
			BaseApplication.instent.progressDialog.setMessage("正在下载离线所需数据");
		} else {
			BaseApplication.instent.progressDialog = new ProgressDialog(this);
			BaseApplication.instent.progressDialog.setMessage("正在下载离线所需数据");
			BaseApplication.instent.progressDialog.show();
		}
	}

	private StringBuilder stringBuilder = new StringBuilder();

	private ProgressDialog progressDialog = null;

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case OffDataDownload.WHAT_DOWNLOAD_SUCCESS_ONE:// 下载完成一个
				// 码头、泊位、区域、船舶、证件、船员
				String str = dataDownload.mapString.get(msg.arg1);
				stringBuilder.append(str + "，下载完成");
				stringBuilder.append("\n");
				if (BaseApplication.instent.progressDialog != null) {
					BaseApplication.instent.progressDialog.setMessage(stringBuilder.toString());
				}
				break;
			case OffDataDownload.WHAT_DOWNLOAD_ONE_RESULT_NULL:// 下载失败一个
			case OffDataDownload.WHAT_INSERT_DATA_FAILED_ONE:// 下载失败一个
				// 码头、泊位、区域、船舶、证件、船员
				String str1 = dataDownload.mapString.get(msg.arg1);
				stringBuilder.append(str1 + "，下载失败");
				stringBuilder.append("\n");
				if (BaseApplication.instent.progressDialog != null) {
					BaseApplication.instent.progressDialog.setCancelable(true);
					BaseApplication.instent.progressDialog.setMessage(stringBuilder.toString());
				}
				break;
			case OffDataDownload.WHAT_DOWNLOAD_ALL_SUCCESS:// 下载完成
				HgqwToast.toast("下载完成");
				if (BaseApplication.instent.progressDialog != null) {
					BaseApplication.instent.progressDialog.dismiss();
					BaseApplication.instent.progressDialog = null;
				}
				finish();
				break;
			case RequestForPlace.BIND_SUCCESS:
				downloadOfflineData();
				break;
			case RequestForPlace.UN_BIND_SUCCESS:
				unBindSuccess();
				break;
			default:
				break;
			}
		}
	};

	protected void unBindSuccess() {
		XcUtil.clearDdBindInfo();
		if (BaseApplication.instent.progressDialog != null) {
			BaseApplication.instent.progressDialog.dismiss();
			BaseApplication.instent.progressDialog = null;
		}
		finish();
		
	}

	@Override
	public void onBackPressed() {
		//清空内存
		if(isDownload){
			XcUtil.clearDdBindInfo();
		}
		super.onBackPressed();
	}

	
}
