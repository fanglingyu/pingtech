package com.pingtech.hgqw.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.pingtech.R;
import com.pingtech.hgqw.entity.FlagManagers;
import com.pingtech.hgqw.entity.LoginUser;
import com.pingtech.hgqw.entity.SavePassInfo;
import com.pingtech.hgqw.interf.OnHttpResult;
import com.pingtech.hgqw.module.exception.activity.Exceptioninfo;
import com.pingtech.hgqw.pullxml.PullXmlSavePassInfo;
import com.pingtech.hgqw.utils.BasicNameValuePair;
import com.pingtech.hgqw.utils.SystemSetting;
import com.pingtech.hgqw.web.NetWorkManager;
import com.pingtech.hgqw.widget.HgqwToast;


/**
 * 
 * 类描述：巡查巡检---查岗查哨
 *
 * <p> Title: 江海港边检勤务综合管理系统-XCXJSearchPersonDetail.java </p>
 * <p> Copyright: Copyright (c) 2012 </p>
 * <p> Company: 品恩科技 </p>
 * @author  liums  
 * @version 1.0
 * @date  2013-4-10 下午12:21:40
 */
public class XCXJSearchPersonDetail extends MyActivity implements OnHttpResult {
	private TextView cgcg_detail_xm;

	private TextView cgcg_detail_ssdw;

	private TextView cgcg_detail_zw;

	private TextView cgcg_detail_zqdd;

	private ImageView cgcg_detail_photo;

	private Button cgcg_detail_button_save_log;

	// private Button cgcg_detail_button_exception;

	// private Button cgcg_detail_button_back;

	private String str_cgcg_detail_xm;

	private String str_cgcg_detail_ssdw;

	private String str_cgcg_detail_zw;

	private String str_cgcg_detail_zqdd;

	private String zjhm;

	private String sbkid;

	private byte[] str_cgcg_detail_photo;

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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.cgcg_detail);
		setMyActiveTitle(getString(R.string.xunchaxunjian) + ">" + getString(R.string.normalxunjian));
		Log.i("日志", "CgcsDetail onCreate");
		this.pingtechFindViewById();
		Intent intent = getIntent();
		this.pingtechGetContent(intent);
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
		cgcg_detail_xm.setText(str_cgcg_detail_xm);
		cgcg_detail_ssdw.setText(str_cgcg_detail_ssdw);
		cgcg_detail_zw.setText(str_cgcg_detail_zw);
		cgcg_detail_zqdd.setText(str_cgcg_detail_zqdd);
		if (str_cgcg_detail_photo != null) {
			cgcg_detail_photo.setImageBitmap(BitmapFactory.decodeByteArray(str_cgcg_detail_photo, 0, str_cgcg_detail_photo.length));
		}
	}

	/**
	 * 
	 * @方法名：pingtechGetContent
	 * @功能说明：从Intent接收传递的参数
	 * @author liums
	 * @date 2013-3-29 下午5:39:27
	 * @param intent
	 */
	private void pingtechGetContent(Intent intent) {
		str_cgcg_detail_xm = intent.getStringExtra("xm");
		str_cgcg_detail_ssdw = intent.getStringExtra("ssdw");
		str_cgcg_detail_zw = intent.getStringExtra("zw");
		str_cgcg_detail_zqdd = intent.getStringExtra("zqdd");
		str_cgcg_detail_photo = intent.getByteArrayExtra("photo");
		zjhm = intent.getStringExtra("zjhm");
		sbkid = intent.getStringExtra("sbkid");
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
		// cgcg_detail_button_exception = (Button) this.findViewById(R.id.cgcg_detail_button_exception);
		// cgcg_detail_button_back = (Button) this.findViewById(R.id.cgcg_detail_button_back);
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
		intent.putExtra("name", str_cgcg_detail_xm);
		intent.putExtra("company", str_cgcg_detail_ssdw);
		intent.putExtra("cgcsid", savePassInfo.getCgcsid());
		intent.putExtra("from", FlagManagers.XCXJ_CGCS);
		intent.putExtra("objecttype", "06");
		intent.putExtra("windowtype", "03");
		intent.putExtra("zjhm", zjhm);
		intent.putExtra("cardnumber", zjhm);
		intent.putExtra("sbkid", sbkid);
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
		}else if (SystemSetting.xunJianId != null && SystemSetting.xunJianId.length() > 0) {
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
		params.add(new BasicNameValuePair("zjhm", zjhm));
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
		NetWorkManager.request(this, url, params, HTTPREQUEST_TYPE_FOR_SAVE_LOG_INFO);
	}
}
