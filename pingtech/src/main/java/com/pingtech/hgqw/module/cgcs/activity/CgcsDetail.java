package com.pingtech.hgqw.module.cgcs.activity;


import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.pingtech.R;
import com.pingtech.hgqw.activity.MyActivity;
import com.pingtech.hgqw.base.BaseApplication;
import com.pingtech.hgqw.entity.FlagManagers;
import com.pingtech.hgqw.entity.LoginUser;
import com.pingtech.hgqw.entity.SavePassInfo;
import com.pingtech.hgqw.interf.OffLineResult;
import com.pingtech.hgqw.interf.OnHttpResult;
import com.pingtech.hgqw.module.cgcs.action.CgcsAction;
import com.pingtech.hgqw.module.cgcs.entity.Cgcs;
import com.pingtech.hgqw.module.exception.activity.Exceptioninfo;
import com.pingtech.hgqw.module.offline.base.utils.OffLineManager;
import com.pingtech.hgqw.pullxml.PullXmlSavePassInfo;
import com.pingtech.hgqw.utils.BasicNameValuePair;
import com.pingtech.hgqw.utils.DateUtils;
import com.pingtech.hgqw.utils.ImageFactory;
import com.pingtech.hgqw.utils.StringUtils;
import com.pingtech.hgqw.utils.SystemSetting;
import com.pingtech.hgqw.web.NetWorkManager;
import com.pingtech.hgqw.widget.HgqwToast;

/**
 * 
 *
 * 类描述：查岗查哨详情页面
 *
 * <p> Title: 江海港边检勤务-移动管理系统-CgcsDetail.java </p>
 * <p> Copyright: Copyright (c) 2012 </p>
 * <p> Company: 品恩科技 </p>
 * @author  娄高伟 
 * @version 1.0
 * @date  2013-8-16 下午3:15:32
 */
public class CgcsDetail extends MyActivity implements OnHttpResult,OffLineResult {
	private TextView cgcg_detail_xm;

	private TextView cgcg_detail_ssdw;

	private TextView cgcg_detail_zw;

	private TextView cgcg_detail_zqdd;

	private ImageView cgcg_detail_photo;

	private Button cgcg_detail_button_save_log;

	private ProgressDialog progressDialog = null;

	private SavePassInfo savePassInfo = new SavePassInfo();

	/**
	 * 进入保存人员信息（刷卡登记时，该卡未在平台注册过）
	 */
	private static final int HTTPREQUEST_TYPE_FOR_SAVE_LOG_INFO = 0;

	/**
	 * 保存查岗查哨异常
	 */
	private static final int SAVE_CGCG_EXCEPTION = 1;
	private Cgcs cgcs=null; 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.cgcg_detail);
		setMyActiveTitle(getString(R.string.xunchaxunjian) + ">" + getString(R.string.chagangchashao));
		Log.i("日志", "CgcsDetail onCreate");
		this.pingtechFindViewById();
		Intent intent = getIntent();
		cgcs=(Cgcs)intent.getSerializableExtra("cgcs");
		this.pingtechSetContent();
	}

	/**
	 * 
	 * @方法名：pingtechSetContent
	 * @功能说明：界面元素赋值
	 * @author liums
	 * @date 2013-3-29 下午5:38:53
	 */
	private void pingtechSetContent() {
		cgcg_detail_xm.setText(cgcs.getXm()==null?"":cgcs.getXm());
		cgcg_detail_ssdw.setText(cgcs.getSsdw()==null?"":cgcs.getSsdw());
		cgcg_detail_zw.setText(cgcs.getZw()==null?"":cgcs.getZw());
		cgcg_detail_zqdd.setText(cgcs.getZqdd()==null?"":cgcs.getZqdd());
		if (cgcs.getIcpic()!=null&&cgcs.getIcpic().length>0) {
		Bitmap netWorkImage =ImageFactory.getBitmap(cgcs.getIcpic());
		if (netWorkImage != null) {
			LayoutParams para;
			para = cgcg_detail_photo.getLayoutParams();
			int height_be = netWorkImage.getHeight() / 130;
			int width_be = netWorkImage.getWidth() / 105;
			if (height_be > width_be) {
				para.height = 130 * 2;
				para.width = 130 * 2 * netWorkImage.getWidth() / netWorkImage.getHeight();
			} else {
				para.width = 105 * 2;
				para.height = 105 * 2 * netWorkImage.getHeight() / netWorkImage.getWidth();
			}
			cgcg_detail_photo.setLayoutParams(para);
			cgcg_detail_photo.setImageBitmap(netWorkImage);
		}
		}
	}


	/**
	 * 
	 * @方法名：pingtechFindViewById
	 * @功能说明：控件寻址
	 * @author liums
	 * @date 2013-3-29 下午5:39:47
	 */
	private void pingtechFindViewById() {
		cgcg_detail_xm = (TextView) this.findViewById(R.id.cgcg_detail_xm);
		cgcg_detail_ssdw = (TextView) this.findViewById(R.id.cgcg_detail_ssdw);
		cgcg_detail_zw = (TextView) this.findViewById(R.id.cgcg_detail_zw);
		cgcg_detail_zqdd = (TextView) this.findViewById(R.id.cgcg_detail_zqdd);
		cgcg_detail_photo = (ImageView) this.findViewById(R.id.cgcg_detail_photo);
		cgcg_detail_button_save_log = (Button) this.findViewById(R.id.cgcg_detail_button_save_log);
	}

	@Override
	public void onHttpResult(String str, int httpRequestType) {
		// 关闭等待框
		if (progressDialog != null) {
			progressDialog.dismiss();
			progressDialog = null;
		}
		switch (httpRequestType) {
		case HTTPREQUEST_TYPE_FOR_SAVE_LOG_INFO:// 保存查岗查哨记录

			// 解析XML
			savePassInfo = PullXmlSavePassInfo.pullXml(str);
			if (savePassInfo == null) {
				HgqwToast.makeText(this, R.string.data_download_failure_info, HgqwToast.LENGTH_SHORT).show();
				break;
			}

			if ("error".equals(savePassInfo.getResult())) {
				HgqwToast.makeText(this, savePassInfo.getInfo(), HgqwToast.LENGTH_SHORT).show();
			} else if ("success".equals(savePassInfo.getResult())) {
				cgcg_detail_button_save_log.setEnabled(false);
				HgqwToast.makeText(this, getString(R.string.save_success), HgqwToast.LENGTH_SHORT).show();
			}
			break;
		default:
			break;
		}
	}

	/**
	 * 
	 * @方法名：onButtonClick
	 * @功能说明：按钮点击事件
	 * @author liums
	 * @date 2013-3-29 下午5:38:34
	 * @param v
	 */
	public void onButtonClick(View v) {
		switch (v.getId()) {
		case R.id.cgcg_detail_button_save_log:
			saveLogInfo();
			break;
		case R.id.cgcg_detail_button_exception:
			saveException();
			break;
		case R.id.cgcg_detail_button_back:
			finish();
			break;

		default:
			break;
		}
	}

	/**
	 * 
	 * @方法名：saveException
	 * @功能说明：保存异常信息
	 * @author liums
	 * @date 2013-3-29 下午5:40:57
	 */
	private void saveException() {
		Intent intent = new Intent();
		intent.putExtra("name", cgcs.getXm() == null ? "" : cgcs.getXm());
		intent.putExtra("company", cgcs.getSsdw() == null ? "" : cgcs.getSsdw());
		intent.putExtra("cgcsid", savePassInfo.getCgcsid());
		intent.putExtra("from", FlagManagers.XCXJ_CGCS);
		intent.putExtra("objecttype", "06");
		intent.putExtra("windowtype", "03");
		intent.putExtra("zjhm", cgcs.getZjhm() == null ? "" : cgcs.getZjhm());
		intent.putExtra("cardnumber",
				cgcs.getZjhm() == null ? "" : cgcs.getZjhm());
		intent.putExtra("sbkid", cgcs.getSbkid() == null ? "" : cgcs.getSbkid());
		intent.putExtra("jcfs", "02");// 检查方式: (视频巡视01,现场巡查,02,人员检查03)
		HashMap<String, Object> ship = SystemSetting.getBindShip(2 + "");
		if (ship != null) {
			String tkwz = (String) ship.get("tkwz");
			String[] temp_str = null;
			if (tkwz != null) {
				temp_str = tkwz.split(",");
			}
			intent.putExtra("shipname", (String) ship.get("cbzwm"));
			intent.putExtra("jhhc", (String) ship.get("hc"));
			intent.putExtra("dockcode", (String) ship.get("tkmt"));
			if (temp_str != null && temp_str.length > 0) {
				intent.putExtra("dockname", temp_str[0]);
			} else {
				intent.putExtra("dockname", "");
			}
			intent.putExtra("berthcode", (String) ship.get("tkbw"));
			if (temp_str != null && temp_str.length > 1) {
				intent.putExtra("berthname", temp_str[1]);
			} else {
				intent.putExtra("berthname", "");
			}
		} else if (SystemSetting.xunJianId != null
				&& SystemSetting.xunJianId.length() > 0) {
			if (SystemSetting.xunJianType != null
					&& SystemSetting.xunJianType.equals("bw")) {
				/*
				 * intent.putExtra("berthcode", SystemSetting.xunJianId);
				 * intent.putExtra("berthname", SystemSetting.xunJianName);
				 * intent.putExtra("dockcode", SystemSetting.xunJianMTid);
				 * intent.putExtra("dockname", SystemSetting.xunJianMTname);
				 * intent.putExtra("scene", "02");
				 */
			} else if (SystemSetting.xunJianType != null
					&& SystemSetting.xunJianType.equals("mt")) {
				/*
				 * intent.putExtra("dockcode", SystemSetting.xunJianId);
				 * intent.putExtra("dockname", SystemSetting.xunJianName);
				 * intent.putExtra("scene", "03");
				 */
			} else if (SystemSetting.xunJianType != null
					&& SystemSetting.xunJianType.equals("qy")) {
				intent.putExtra("areacode", SystemSetting.xunJianId);
				intent.putExtra("areaname", SystemSetting.xunJianName);
				if ("kkqy".equalsIgnoreCase(SystemSetting.xunJianQylx)) {
					intent.putExtra("scene", "05");
				} else if ("卡口区域".equalsIgnoreCase(SystemSetting.xunJianQylx)) {
					intent.putExtra("scene", "05");
				} else if ("jkqy".equalsIgnoreCase(SystemSetting.xunJianQylx)) {
					intent.putExtra("scene", "04");
				} else if ("监控区域".equalsIgnoreCase(SystemSetting.xunJianQylx)) {
					intent.putExtra("scene", "04");
				}
			}
		}

		intent.setClass(getApplicationContext(), Exceptioninfo.class);
		startActivityForResult(intent, SAVE_CGCG_EXCEPTION);
	}

	/**
	 * 
	 * @方法名：saveLogInfo
	 * @功能说明：保存通行记录
	 * @author liums
	 * @date 2013-3-29 下午5:40:16
	 */
	private void saveLogInfo() {
		String url = "sendPassInfo";
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("comeFrom", FlagManagers.XCXJ_CGCS));
		params.add(new BasicNameValuePair("zjhm", cgcs.getZjhm() == null ? "" : cgcs.getZjhm()));
		params.add(new BasicNameValuePair("PDACode", SystemSetting.getPDACode()));
		params.add(new BasicNameValuePair("userID", LoginUser.getCurrentLoginUser().getUserID()));
		if (progressDialog != null) {
			return;
		}
		progressDialog = new ProgressDialog(this);
		progressDialog.setTitle(getString(R.string.Validing));
		progressDialog.setMessage(getString(R.string.waiting));
		progressDialog.setCancelable(false);
		progressDialog.setIndeterminate(false);
		progressDialog.show();
		if (BaseApplication.instent.getWebState()) {
			NetWorkManager.request(this, url, params, HTTPREQUEST_TYPE_FOR_SAVE_LOG_INFO);
		} else {
			Map<String, Object> ma = new HashMap<String, Object>();
			ma.put("zjhm", cgcs.getZjhm() == null ? "" : cgcs.getZjhm());
			ma.put("pdacode", SystemSetting.getPDACode());
			ma.put("jcr", LoginUser.getCurrentLoginUser().getUserID());
			ma.put("jcsj", DateUtils.dateToString(new Date()));
			ma.put("sjid", StringUtils.UIDGenerator());
			OffLineManager.request(CgcsDetail.this, new CgcsAction(), url, ma, HTTPREQUEST_TYPE_FOR_SAVE_LOG_INFO);
		}

	}

	@Override
	public void offLineResult(Pair<Boolean, Object> res, int offLineRequestType) {
		// 关闭等待框
		if (progressDialog != null) {
			progressDialog.dismiss();
			progressDialog = null;
		}
		switch (offLineRequestType) {
		case HTTPREQUEST_TYPE_FOR_SAVE_LOG_INFO:// 保存查岗查哨记录
			if (res.first) {
				HgqwToast.makeText(this, getString(R.string.save_success), HgqwToast.LENGTH_SHORT).show();
			} else if ("success".equals(savePassInfo.getResult())) {
				HgqwToast.makeText(this, getString(R.string.save_failure), HgqwToast.LENGTH_SHORT).show();
			}
			break;
		default:
			break;
		}

	}
}
