package com.pingtech.hgqw.module.xunjian.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;

import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.pingtech.R;
import com.pingtech.hgqw.activity.MyActivity;
import com.pingtech.hgqw.activity.SelectCountrylistActivity;
import com.pingtech.hgqw.base.BaseApplication;
import com.pingtech.hgqw.base.dialog.BaseDialogUtils;
import com.pingtech.hgqw.entity.CardInfo;
import com.pingtech.hgqw.entity.GlobalFlags;
import com.pingtech.hgqw.entity.LoginUser;
import com.pingtech.hgqw.entity.ManagerFlag;
import com.pingtech.hgqw.interf.OffLineResult;
import com.pingtech.hgqw.interf.OnHttpResult;
import com.pingtech.hgqw.module.kakou.utils.PullXmlUtil;
import com.pingtech.hgqw.module.offline.base.utils.OffLineManager;
import com.pingtech.hgqw.module.publices.utils.Utils;
import com.pingtech.hgqw.module.xtgl.activity.FunctionSetting;
import com.pingtech.hgqw.module.xunjian.action.XunJianAction;
import com.pingtech.hgqw.module.xunjian.utils.XcUtil;
import com.pingtech.hgqw.utils.BasicNameValuePair;
import com.pingtech.hgqw.utils.DataDictionary;
import com.pingtech.hgqw.utils.NVPairTOMap;
import com.pingtech.hgqw.utils.StringUtils;
import com.pingtech.hgqw.utils.SystemSetting;
import com.pingtech.hgqw.web.NetWorkManager;
import com.pingtech.hgqw.widget.HgqwToast;

public class XunjianClglSdbc extends MyActivity implements OnHttpResult, OffLineResult {
	private static final int GJDQ = 1;

	private static final int GJ = 2;

	/** 车牌号码 */
	private EditText cphm = null;

	/** 车牌号码 */
	private EditText fx = null;

	/** 发动机号 */
	private EditText fdjh = null;

	/** 国家地区 */
	private Button btn_gjdq = null;

	/** 车辆品牌 */
	private EditText clpp = null;

	/** 车辆颜色 */
	private EditText clys = null;

	/** 公司/拥有者 */
	private EditText gs_yyz = null;

	/** 使用范围 */
	private EditText syfw = null;

	/** 有效期 */
	private EditText yxq = null;

	/** 通行时间 */
	private EditText txsj = null;

	/** 驾驶员姓名 */
	private EditText jsyxm = null;

	/** 性别 */
	private EditText xb = null;

	/** 证件类型 */
	private Spinner zjzl = null;

	/** 车辆类型 */
	private Spinner cllx = null;

	/** 证件号码 */
	private EditText zjhm = null;

	/** 驾驶证号 */
	private EditText jszh = null;

	/** 车辆识别代号 */
	private EditText clsbdh = null;

	/** 国籍 */
	private Button btn_gj = null;

	/** 出生日期 */
	private DatePicker csrq = null;

	/** 联系方式 */
	private EditText lxfs = null;

	/** 所属单位 */
	private EditText ssdw = null;

	/** 车牌号码 */
	private String cphm_str = "";

	/** 发动机号 */
	private String fdjh_str = "";

	/** 车辆类型 */
	private String cllx_str = "";

	/** 国家地区 */
	private String gjdq_str = "";

	/** 车辆品牌 */
	private String clpp_str = "";

	/** 车辆颜色 */
	private String clys_str = "";

	/** 公司/拥有者 */
	private String gs_yyz_str = "";

	/** 使用范围 */
	private String syfw_str = "";

	/** 有效期 */
	private String yxq_str = "";

	/** 通行时间 */
	private String txsj_str = "";

	/** 驾驶员姓名 */
	private String jsyxm_str = "";

	/** 性别 */
	private String xb_str = "";

	/** 证件类型 */
	private String zjzl_str = "";

	/** 证件号码 */
	private String zjhm_str = "";

	/** 驾驶证号 */
	private String jszh_str = "";

	/** 国籍 */
	private String gj_str = "";

	/** 出生日期 */
	private String csrq_str = "";

	/** 联系方式 */
	private String lxfs_str = "";

	/** 车辆识别代号 */
	private String clsbdh_str = "";

	/** 所属单位 */
	private String ssdw_str = "";

	private String txfx_str = "";

	private Button btn_bc = null;

	private int from;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		String title = getString(R.string.xunchaxunjian) + ">" + getString(R.string.readcard_cljc_kakou) + ">" + getString(R.string.sendXjInfo);
		super.onCreate(savedInstanceState, R.layout.kakou_clyz_sdbc);
		setMyActiveTitle(title);
		find();
		init();
	}

	protected void find() {
		/** 车牌号码 */
		cphm = (EditText) findViewById(R.id.cphm);

		/** 发动机号 */
		fdjh = (EditText) findViewById(R.id.fdjh);

		/** 车辆类型 */
		cllx = (Spinner) findViewById(R.id.cllx);

		/** 国家地区 */
		btn_gjdq = (Button) findViewById(R.id.btn_gjdq);

		/** 车辆品牌 */
		clpp = (EditText) findViewById(R.id.clpp);

		/** 车辆颜色 */
		clys = (EditText) findViewById(R.id.clys);

		/** 公司/拥有者 */
		gs_yyz = (EditText) findViewById(R.id.gs_yyz);

		/** 使用范围 */
		// ssfw = (EditText)view.findViewById(R.id.ssfw);

		/** 有效期 */
		// yxq = (EditText) view.findViewById(R.id.yxq);

		/** 通行时间 */
		// txsj = (EditText)view.findViewById(R.id.txsj);

		/** 驾驶员姓名 */
		jsyxm = (EditText) findViewById(R.id.jsyxm);

		/** 性别 */
		xb = (EditText) findViewById(R.id.xb);

		/** 证件类型 */
		zjzl = (Spinner) findViewById(R.id.zjzl);

		/** 证件号码 */
		zjhm = (EditText) findViewById(R.id.zjhm);

		/** 驾驶证号 */
		jszh = (EditText) findViewById(R.id.jszh);
		clsbdh = (EditText) findViewById(R.id.clsbdh);

		/** 国籍 */
		btn_gj = (Button) findViewById(R.id.btn_gj);

		/** 出生日期 */
		csrq = (DatePicker) findViewById(R.id.csrq);

		/** 联系方式 */
		lxfs = (EditText) findViewById(R.id.lxfs);

		/** 所属单位 */
		ssdw = (EditText) findViewById(R.id.ssdw);
		btn_bc = (Button) findViewById(R.id.btn_bc);
	}

	protected void init() {
		Intent intent = getIntent();
		from = intent.getIntExtra("from", ManagerFlag.PDA_KKGL_CLYZ);
		if (intent != null) {
			CardInfo cardInfo = (CardInfo) intent.getSerializableExtra("cardInfo");
			if (cardInfo != null) {
				cphm_str = cardInfo.getCphm();
				jszh_str = cardInfo.getJszbh_sfzh();
			}

			if (StringUtils.isEmpty(cphm_str)) {
				cphm_str = "";
			}
			cphm.setText(cphm_str);

			if (StringUtils.isEmpty(jszh_str)) {
				jszh_str = "";
			}
			jszh.setText(jszh_str);
		}

		// 国家地区
		btn_gjdq.setText(DataDictionary.getCountryName("CHN"));
		// 国家地区
		btn_gj.setText(DataDictionary.getCountryName("CHN"));

		Spinner spinner = (Spinner) findViewById(R.id.zjzl);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,
				DataDictionary.getDataDictionaryNameList(DataDictionary.DATADICTIONARY_TYPE_CERTIFICATES_TYPE));
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);

		// 卡车 3 ,小车2

		Spinner spinnerCllx = (Spinner) findViewById(R.id.cllx);
		ArrayAdapter<String> adapterCllx = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,
				DataDictionary.getDataDictionaryNameList(DataDictionary.DATADICTIONARY_TYPE_CHELLX_TYPE));
		adapterCllx.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerCllx.setAdapter(adapterCllx);
	}

	public void click(View v) {
		switch (v.getId()) {
		case R.id.btn_gjdq:
			gjdq(v);
			break;
		case R.id.btn_gj:
			gj(v);
			break;
		case R.id.btn_bc:
			bc();
			break;
		case R.id.cancel:
			finish();
			break;

		default:
			break;
		}
	}

	private void bc() {
		cphm_str = cphm.getText().toString();

		fdjh_str = fdjh.getText().toString();

		// cllx_str = cllx.getText().toString();

		clpp_str = clpp.getText().toString();

		clys_str = clys.getText().toString();

		gs_yyz_str = gs_yyz.getText().toString();
		clsbdh_str = clsbdh.getText().toString();

		// syfw_str = syfw.getText().toString();

		// yxq_str = yxq.getText().toString();

		// txsj_str = txsj.getText().toString();

		jsyxm_str = jsyxm.getText().toString();

		// xb_str = xb.getText().toString();

		zjhm_str = zjhm.getText().toString();

		jszh_str = jszh.getText().toString();

		lxfs_str = lxfs.getText().toString();

		ssdw_str = ssdw.getText().toString();

		// ////////
		gjdq_str = DataDictionary.getCountryCode(btn_gjdq.getText().toString());

		gj_str = DataDictionary.getCountryCode(btn_gj.getText().toString());

		zjzl_str = DataDictionary.getDataDictionaryCodeByIndex(zjzl.getSelectedItemPosition(), DataDictionary.DATADICTIONARY_TYPE_CERTIFICATES_TYPE);
		cllx_str = DataDictionary.getDataDictionaryCodeByIndex(cllx.getSelectedItemPosition(), DataDictionary.DATADICTIONARY_TYPE_CHELLX_TYPE);

		DatePicker checkdate = (DatePicker) findViewById(R.id.csrq);
		csrq_str = checkdate.getYear() + "-" + (checkdate.getMonth() + 1) + "-" + checkdate.getDayOfMonth();

		txfx_str = "";
		RadioGroup dire_rg = (RadioGroup) findViewById(R.id.direction_radio);
		if (dire_rg.getCheckedRadioButtonId() == R.id.radio_btn_up) {
			txfx_str = "0";
		} else {
			txfx_str = "1";
		}

		xb_str = "";
		RadioGroup xb_rg = (RadioGroup) findViewById(R.id.sex_radio);
		if (dire_rg.getCheckedRadioButtonId() == R.id.radio_btn_f) {
			xb_str = "0";
		} else {
			xb_str = "1";
		}
		sendPassInfo();
	}

	private void sendPassInfo() {
		if (!checkEmpty()) {
			return;
		}
		String url = "sendPassInfo";
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("jcr", BaseApplication.instent.gainUserID()));
		params.add(new BasicNameValuePair("xm", jsyxm_str));
		params.add(new BasicNameValuePair("fx", txfx_str));
		params.add(new BasicNameValuePair("PDACode", SystemSetting.getPDACode()));
		params.add(new BasicNameValuePair("objectType", "02"));// 06表示查岗查哨，01表示普通人员,
																// 车辆02
		params.add(new BasicNameValuePair("xb", xb_str));
		params.add(new BasicNameValuePair("gj", gj_str));
		params.add(new BasicNameValuePair("zjhm", zjhm_str));
		params.add(new BasicNameValuePair("zjzl", zjzl_str));// 船员证件种类暂时固定，后续再改

		params.add(new BasicNameValuePair("ssdw", ssdw_str));
		if (from == ManagerFlag.PDA_KKGL_CLYZ) {
			HashMap<String, Object> bindData = SystemSetting.getBindShip(GlobalFlags.LIST_TYPE_FROM_KAKOUMANAGER + "");
			if (bindData == null) {
				return;
			}
			params.add(new BasicNameValuePair("comeFrom", "3"));
			params.add(new BasicNameValuePair("kkID", (String) bindData.get("id")));
			params.add(new BasicNameValuePair("voyageNumber", ""));
			params.add(new BasicNameValuePair("type", ""));
			params.add(new BasicNameValuePair("ddID", ""));
		} else if (from == ManagerFlag.PDA_XCXJ_CLYZ) {
			params.add(new BasicNameValuePair("comeFrom", "2"));
			params.add(new BasicNameValuePair("kkID", ""));
			
			params.add(new BasicNameValuePair("voyageNumber", XcUtil.getXunjianHC()));
		}
		params.add(new BasicNameValuePair("csrq", csrq_str));
		params.add(new BasicNameValuePair("time", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(System.currentTimeMillis()))));
		params.add(new BasicNameValuePair("userID", LoginUser.getCurrentLoginUser().getUserID()));
		// 附加经纬度信息
		params.add(new BasicNameValuePair("longitude", BaseApplication.instent.getLongitude()));// 经度
		params.add(new BasicNameValuePair("latitude", BaseApplication.instent.getLatitude()));// 纬度

		params.add(new BasicNameValuePair("qylxbs", "1"));// 区域类型标识。（0人员、1车辆）
		params.add(new BasicNameValuePair("cphm", cphm_str));
		params.add(new BasicNameValuePair("clpp", clpp_str));
		params.add(new BasicNameValuePair("gsyyz", gs_yyz_str));
		params.add(new BasicNameValuePair("fdjh", fdjh_str));
		params.add(new BasicNameValuePair("cllx", cllx_str));
		params.add(new BasicNameValuePair("clsbdh", clsbdh_str));
		params.add(new BasicNameValuePair("clys", clys_str));
		params.add(new BasicNameValuePair("jszh", jszh_str));

		BaseDialogUtils.showRequestDialog(this, false);
		if (!Utils.getState(FunctionSetting.bdtxyz, true)) {
			 if (from == ManagerFlag.PDA_XCXJ_CLYZ){
				 String type = XcUtil.getXunjianTypeOnLine();
				 String ddid = XcUtil.getXunjianDdidOnLine();
				 params.add(new BasicNameValuePair("ddID", ddid));
					
				 params.add(new BasicNameValuePair("type", type));// 当前巡查地点，返回对应代码,口岸：ka，码头：mt，泊位：bw，区域：qy，浮筒：ft，锚地：md cb
			 }
			NetWorkManager.request(this, url, params, 0);
		} else {
			url = "sendClPassInfo";
			 if (from == ManagerFlag.PDA_XCXJ_CLYZ){
				 String type = XcUtil.getXunjianTypeForYcxx();
				 params.add(new BasicNameValuePair("type", type));// 检查地点(在船上01、在码头02、在泊位03、在区域04)
			 }
			OffLineManager.request(this, new XunJianAction(), url, NVPairTOMap.nameValuePairTOMap(params), 0);
		}
	}

	private boolean checkEmpty() {
		if (StringUtils.isEmpty(cphm_str)) {
			HgqwToast.toast("车牌号码不能为空！");
			return false;
		}
		if (StringUtils.isEmpty(cllx_str)) {
			HgqwToast.toast("车辆类型不能为空！");
			return false;
		}
		if (StringUtils.isEmpty(gjdq_str)) {
			HgqwToast.toast("国家地区不能为空！");
			return false;
		}
		if (StringUtils.isEmpty(jsyxm_str)) {
			HgqwToast.toast("驾驶员姓名不能为空！");
			return false;
		}
		if (StringUtils.isEmpty(xb_str)) {
			HgqwToast.toast("性别不能为空！");
			return false;
		}
		if (StringUtils.isEmpty(jszh_str)) {
			HgqwToast.toast("驾驶证编号不能为空！");
			return false;
		}
		if (StringUtils.isEmpty(gj_str)) {
			HgqwToast.toast("国籍不能为空！");
			return false;
		}
		return true;
	}

	private void gj(View v) {
		Intent intent = new Intent();
		intent.putExtra("type", "countrylist");
		intent.putExtra("selectitem", ((Button) v).getText().toString());
		intent.setClass(getApplicationContext(), SelectCountrylistActivity.class);
		startActivityForResult(intent, GJ);
	}

	private void gjdq(View v) {
		Intent intent = new Intent();
		intent.putExtra("type", "countrylist");
		intent.putExtra("selectitem", ((Button) v).getText().toString());
		intent.setClass(getApplicationContext(), SelectCountrylistActivity.class);
		startActivityForResult(intent, GJDQ);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case GJDQ:
			resultGjdq(resultCode, data);
			break;
		case GJ:
			resultGj(resultCode, data);
			break;

		default:
			break;
		}
	}

	private void resultGjdq(int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			int pos = data.getIntExtra("selectitem", 0);
			gjdq_str = DataDictionary.getCountryName(pos);
			btn_gjdq.setText(gjdq_str);
		}
	}

	private void resultGj(int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			int pos = data.getIntExtra("selectitem", 0);
			gj_str = DataDictionary.getCountryName(pos);
			btn_gj.setText(gj_str);
		}
	}

	@Override
	public void offLineResult(Pair<Boolean, Object> res, int offLineRequestType) {
		if (res.second != null) {
			onHttpResult(res.second.toString(), offLineRequestType);
		} else {
			BaseDialogUtils.dismissRequestDialog();
		}
	}

	@Override
	public void onHttpResult(String str, int httpRequestType) {
		BaseDialogUtils.dismissRequestDialog();
		if (StringUtils.isEmpty(str)) {
			HgqwToast.toast(R.string.save_failure);
			return;
		}
		if (new PullXmlUtil().pullSendPassInfo(str)) {
			HgqwToast.toast(R.string.save_success);
			finish();
			return;
		} else {
			HgqwToast.toast(R.string.save_failure);
		}
	}

}
