package com.pingtech.hgqw.module.publices.fragment;

import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.pingtech.R;
import com.pingtech.hgqw.base.BaseApplication;
import com.pingtech.hgqw.entity.CardInfo;
import com.pingtech.hgqw.entity.Clzjxx;
import com.pingtech.hgqw.entity.GlobalFlags;
import com.pingtech.hgqw.entity.ManagerFlag;
import com.pingtech.hgqw.module.exception.activity.Exceptioninfo;
import com.pingtech.hgqw.module.kakou.activity.KakouCljc;
import com.pingtech.hgqw.module.kakou.request.KakouRequest;
import com.pingtech.hgqw.module.xunjian.request.XunjianRequest;
import com.pingtech.hgqw.module.xunjian.utils.XcUtil;
import com.pingtech.hgqw.utils.DataDictionary;
import com.pingtech.hgqw.utils.ImageFactory;
import com.pingtech.hgqw.utils.StringUtils;
import com.pingtech.hgqw.utils.SystemSetting;
import com.pingtech.hgqw.widget.HgqwToast;

public class ClzjxxDetileFragment  implements OnClickListener {

	private static final String TAG = "ClInfoDetileFragment";

	private View mView = null;

	/** 验证结果 */
	private TextView yzjg = null;

	private TextView clsbdh = null;

	/** 验证结果 */
	private ImageView imageView_Traffic_direction = null;

	/** 通行方向 */
	private TextView txfx = null;

	/** 车牌号码 */
	private TextView cphm = null;

	/** 发动机号 */
	private TextView fdjh = null;

	/** 车辆类型 */
	private TextView cllx = null;

	/** 国家地区 */
	private TextView gjdq = null;

	/** 车辆品牌 */
	private TextView clpp = null;

	/** 车辆颜色 */
	private TextView clys = null;

	/** 公司/拥有者 */
	private TextView gs_yyz = null;

	/** 使用范围 */
	private TextView syfw = null;

	/** 有效期 */
	private TextView yxq = null;

	/** 通行时间 */
	private TextView txsj = null;

	/** 驾驶员姓名 */
	private TextView jsyxm = null;

	/** 性别 */
	private TextView xb = null;

	/** 证件类型 */
	private TextView zjzl = null;

	/** 证件号码 */
	private TextView zjhm = null;

	/** 驾驶证号 */
	private TextView jszh = null;

	/** 国籍 */
	private TextView gj = null;

	/** 出生日期 */
	private TextView csrq = null;

	/** 联系方式 */
	private TextView lxfs = null;

	/** 所属单位 */
	private TextView ssdw = null;

	/** 验证结果 */
	private String yzjg_title = null;

	/** 通行方向 */
	private String txfx_title = null;

	/** 车牌号码 */
	private String cphm_title = null;

	private String clsbdh_title = null;

	/** 发动机号 */
	private String fdjh_title = null;

	/** 车辆类型 */
	private String cllx_title = null;

	/** 国家地区 */
	private String gjdq_title = null;

	/** 车辆品牌 */
	private String clpp_title = null;

	/** 车辆颜色 */
	private String clys_title = null;

	/** 公司/拥有者 */
	private String gs_yyz_title = null;

	/** 使用范围 */
	private String syfw_title = null;

	/** 有效期 */
	private String yxq_title = null;

	/** 通行时间 */
	private String txsj_title = null;

	/** 驾驶员姓名 */
	private String jsyxm_title = null;

	/** 性别 */
	private String xb_title = null;

	/** 证件类型 */
	private String zjzl_title = null;

	/** 证件号码 */
	private String zjhm_title = null;

	/** 驾驶证号 */
	private String jszh_title = null;

	/** 国籍 */
	private String gj_title = null;

	/** 出生日期 */
	private String csrq_title = null;

	/** 联系方式 */
	private String lxfs_title = null;

	/** 所属单位 */
	private String ssdw_title = null;

	private Button jlyc = null;

	private Button back = null;

	private Button xgtxfx = null;

	private View lv_ry = null;

	private Button wpjc = null;

	private Button sewm = null;

	private ScrollView scrollview = null;

	private View view_bottom = null;

	private Handler handler = null;

	private Context context = null;

	private CardInfo cardInfo = null;

	private ImageView icpic = null;

	private int from = 0;

	private float x, y;

	private boolean isFromOffline = false;

	public ClzjxxDetileFragment(Context context, Handler handler, int from) {
		this.handler = handler;
		this.context = context;
		this.from = from;
	}

	

	private void init() {
		isFromOffline = false;
		jlyc.setOnClickListener(this);
		back.setOnClickListener(this);
		xgtxfx.setOnClickListener(this);
		wpjc.setOnClickListener(this);
		sewm.setOnClickListener(this);
	}

	private void updateInfo(boolean isXgtxfx) {
		Clzjxx clzjxx = cardInfo.getClzjxx();
		if (clzjxx == null) {
			return;
		}
		boolean isPass = cardInfo.isPass();
		/** 通行方向 */
		String fx = cardInfo.getFx();// 0上，1下
		if (StringUtils.isEmpty(fx)) {
			fx = "";
		} else if ("0".equals(fx)) {
			fx = BaseApplication.instent.getString(R.string.inkakou);
			imageView_Traffic_direction.setVisibility(View.VISIBLE);
			imageView_Traffic_direction.setImageResource(R.drawable.up);
		} else if ("1".equals(fx)) {
			imageView_Traffic_direction.setVisibility(View.VISIBLE);
			imageView_Traffic_direction.setImageResource(R.drawable.down);
			fx = BaseApplication.instent.getString(R.string.outkakou);
		} else {
			imageView_Traffic_direction.setVisibility(View.GONE);
		}

		switch (from) {
		case ManagerFlag.PDA_XCXJ_CLYZ:
			xgtxfx.setVisibility(View.GONE);
			imageView_Traffic_direction.setVisibility(View.GONE);
			txfx.setVisibility(View.GONE);
			lv_ry.setVisibility(View.GONE);
			txsj.setVisibility(View.GONE);
			view_bottom.setVisibility(View.GONE);
			break;
		default:
			if (!isPass) {
				txfx.setVisibility(View.GONE);
				xgtxfx.setVisibility(View.GONE);
				imageView_Traffic_direction.setVisibility(View.GONE);
			} else {
				txfx.setVisibility(View.VISIBLE);
				xgtxfx.setVisibility(View.VISIBLE);
				imageView_Traffic_direction.setVisibility(View.VISIBLE);
			}
			lv_ry.setVisibility(View.VISIBLE);
			txsj.setVisibility(View.VISIBLE);
			view_bottom.setVisibility(View.VISIBLE);
			break;
		}
		txfx.setText(Html.fromHtml(txfx_title + "<font color=\"#acacac\">" + fx + "</font>"));

		/** 验证结果 */
		yzjg.setText(Html.fromHtml(yzjg_title + "<font color=\"#acacac\">" + cardInfo.getTsxx() + "</font>"));

		/** 车牌号码 */
		cphm.setText(Html.fromHtml(cphm_title + "<font color=\"#acacac\">" + clzjxx.getCphm() + "</font>"));

		/** 车牌号码 */
		clsbdh.setText(Html.fromHtml(clsbdh_title + "<font color=\"#acacac\">" + clzjxx.getClsbdh() + "</font>"));

		/** 发动机号 */
		fdjh.setText(Html.fromHtml(fdjh_title + "<font color=\"#acacac\">" + clzjxx.getFdjh() + "</font>"));

		/** 车辆类型 */
		cllx.setText(Html.fromHtml(cllx_title + "<font color=\"#acacac\">"
				+ DataDictionary.getDataDictionaryName(clzjxx.getCllx(), DataDictionary.DATADICTIONARY_TYPE_CHELLX_TYPE) + "</font>"));

		/** 国家地区 */
		gjdq.setText(Html.fromHtml(gjdq_title + "<font color=\"#acacac\">" + DataDictionary.getCountryName(clzjxx.getGjdq()) + "</font>"));

		/** 车辆品牌 */
		clpp.setText(Html.fromHtml(clpp_title + "<font color=\"#acacac\">" + clzjxx.getClpp() + "</font>"));

		/** 车辆颜色 */
		clys.setText(Html.fromHtml(clys_title + "<font color=\"#acacac\">" + clzjxx.getClys() + "</font>"));

		/** 公司/拥有者 */
		gs_yyz.setText(Html.fromHtml(gs_yyz_title + "<font color=\"#acacac\">" + clzjxx.getGs_yyz() + "</font>"));

		/** 使用范围 */
		syfw.setText(Html.fromHtml(syfw_title + "<font color=\"#acacac\">" + clzjxx.getSyfw() + "</font>"));

		/** 有效期 */
		yxq.setText(Html.fromHtml(yxq_title + "<font color=\"#acacac\">" + clzjxx.getYxq() + "</font>"));

		/** 通行时间 */
		txsj.setText(Html.fromHtml(txsj_title + "<font color=\"#acacac\">" + clzjxx.getTxsj() + "</font>"));

		/** 驾驶员姓名 */
		jsyxm.setText(Html.fromHtml(jsyxm_title + "<font color=\"#acacac\">" + clzjxx.getJsyxm() + "</font>"));

		/** 性别 */
		xb.setText(Html.fromHtml(xb_title + "<font color=\"#acacac\">"
				+ DataDictionary.getDataDictionaryName(clzjxx.getXb(), DataDictionary.DATADICTIONARY_TYPE_SEX_TYPE) + "</font>"));

		/** 证件类型 */
		zjzl.setText(Html.fromHtml(zjzl_title + "<font color=\"#acacac\">"
				+ DataDictionary.getDataDictionaryName(clzjxx.getZjzl(), DataDictionary.DATADICTIONARY_TYPE_CERTIFICATES_TYPE) + "</font>"));

		/** 证件号码 */
		zjhm.setText(Html.fromHtml(zjhm_title + "<font color=\"#acacac\">" + clzjxx.getZjhm() + "</font>"));

		/** 驾驶证号 */
		jszh.setText(Html.fromHtml(jszh_title + "<font color=\"#acacac\">" + clzjxx.getJszh() + "</font>"));

		/** 国籍 */
		gj.setText(Html.fromHtml(gj_title + "<font color=\"#acacac\">" + DataDictionary.getCountryName(clzjxx.getGj()) + "</font>"));

		/** 出生日期 */
		csrq.setText(Html.fromHtml(csrq_title + "<font color=\"#acacac\">" + clzjxx.getCsrq() + "</font>"));

		/** 联系方式 */
		lxfs.setText(Html.fromHtml(lxfs_title + "<font color=\"#acacac\">" + clzjxx.getLxfs() + "</font>"));

		/** 所属单位 */
		ssdw.setText(Html.fromHtml(ssdw_title + "<font color=\"#acacac\">" + clzjxx.getSsdw() + "</font>"));
		xgtxfx.setEnabled(!isXgtxfx);

		// 设置照片
		Bitmap netWorkImage = null;

		if (isFromOffline) {
			netWorkImage = ImageFactory.loadImage(clzjxx.getCphm());
		} else {
			netWorkImage = clzjxx.getBitmap();
		}

		if (netWorkImage != null) {
			ImageFactory.setImage(icpic, netWorkImage);
		}
	}

	public void setInfo(Activity activity, CardInfo cardInfo, boolean xgtxfx, int arg1) {
		find(activity);
		init();
		switch (arg1) {
		case XunjianRequest.REQUEST_TYPE_SENDCLSWIPERECORD:
			isFromOffline = false;
			break;
		case XunjianRequest.REQUEST_TYPE_SENDCLSWIPERECORD_OFFLINE:
			isFromOffline = true;
			break;
		case 1000:
			break;

		default:
			isFromOffline = false;
			break;
		}
		if (cardInfo == null) {
			return;
		}
		this.cardInfo = cardInfo;
		updateInfo(xgtxfx);

	}

	private void find(Activity activity) {
		view_bottom = (View) activity.findViewById(R.id.view_bottom);
		/** 验证结果 */
		yzjg = (TextView) activity.findViewById(R.id.yzjg);
		/** 车辆识别代号 */
		clsbdh = (TextView) activity.findViewById(R.id.clsbdh);

		/** 通行方向 */
		txfx = (TextView) activity.findViewById(R.id.txfx);

		/** 车牌号码 */
		cphm = (TextView) activity.findViewById(R.id.cphm);

		lv_ry = (View) activity.findViewById(R.id.lv_ry);

		/** 发动机号 */
		fdjh = (TextView) activity.findViewById(R.id.fdjh);

		/** 车辆类型 */
		cllx = (TextView) activity.findViewById(R.id.cllx);

		/** 国家地区 */
		gjdq = (TextView) activity.findViewById(R.id.gjdq);

		/** 车辆品牌 */
		clpp = (TextView) activity.findViewById(R.id.clpp);

		/** 车辆颜色 */
		clys = (TextView) activity.findViewById(R.id.clys);

		/** 公司/拥有者 */
		gs_yyz = (TextView) activity.findViewById(R.id.gs_yyz);

		/** 使用范围 */
		syfw = (TextView) activity.findViewById(R.id.syfw);

		/** 有效期 */
		yxq = (TextView) activity.findViewById(R.id.yxq);

		/** 通行时间 */
		txsj = (TextView) activity.findViewById(R.id.txsj);

		/** 驾驶员姓名 */
		jsyxm = (TextView) activity.findViewById(R.id.jsyxm);

		/** 性别 */
		xb = (TextView) activity.findViewById(R.id.xb);

		/** 证件类型 */
		zjzl = (TextView) activity.findViewById(R.id.zjzl);

		/** 证件号码 */
		zjhm = (TextView) activity.findViewById(R.id.zjhm);

		/** 驾驶证号 */
		jszh = (TextView) activity.findViewById(R.id.jszh);

		/** 国籍 */
		gj = (TextView) activity.findViewById(R.id.gj);

		/** 出生日期 */
		csrq = (TextView) activity.findViewById(R.id.csrq);

		/** 联系方式 */
		lxfs = (TextView) activity.findViewById(R.id.lxfs);

		/** 所属单位 */
		ssdw = (TextView) activity.findViewById(R.id.ssdw);
		icpic = (ImageView) activity.findViewById(R.id.icpic);

		/** 验证结果 */
		yzjg_title = activity.getString(R.string.Valid_results);

		txfx_title = activity.getString(R.string.Traffic_direction);

		cphm_title = "车牌号码：";

		clsbdh_title = "识别代号：";

		fdjh_title = "发动机号：";

		cllx_title = "车辆类型：";

		gjdq_title = "国家地区：";

		clpp_title = "车辆品牌：";

		clys_title = "车辆颜色：";

		gs_yyz_title = "公司/拥有者：";

		syfw_title = "使用范围：";

		yxq_title = "有效期：";

		txsj_title = "通行时间：";

		jsyxm_title = "驾驶员姓名：";

		xb_title = activity.getString(R.string.sel_person_sex);

		zjzl_title = activity.getString(R.string.sel_person_cardtype);

		zjhm_title = activity.getString(R.string.idcardnum);

		jszh_title = "驾驶证编号：";

		gj_title = "国　　籍：";

		csrq_title = "出生日期：";

		lxfs_title = "联系方式：";

		ssdw_title = activity.getString(R.string.goods_check_unit);

		imageView_Traffic_direction = (ImageView) activity.findViewById(R.id.imageView_Traffic_direction);

		jlyc = (Button) activity.findViewById(R.id.jlyc);
		back = (Button) activity.findViewById(R.id.back);
		xgtxfx = (Button) activity.findViewById(R.id.xgtxfx);
		wpjc = (Button) activity.findViewById(R.id.wpjc);
		sewm = (Button) activity.findViewById(R.id.sewm);
		scrollview = (ScrollView) activity.findViewById(R.id.scrollview);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.jlyc:
			jlyc();
			break;
		case R.id.back:
			handler.obtainMessage(KakouCljc.BUTTON_EVENT_BACK).sendToTarget();
			break;
		case R.id.xgtxfx:
			xgtxfx();
			break;
		case R.id.wpjc:

			break;
		case R.id.sewm:

			break;

		default:
			break;
		}
	}

	private void xgtxfx() {
		new KakouRequest(context, handler).requestXgtxfx(cardInfo);
	}

	private void jlyc() {
		Clzjxx clzjxx = cardInfo.getClzjxx();
		if (clzjxx == null) {
			HgqwToast.toast("数据异常，请退出重试！");
			return;
		}
		Intent intent = new Intent();
		// objecttype//对象类型（人员01、车辆02、船舶03、设备04、区域05、查岗查哨06）
		intent.putExtra("objecttype", "02");

		// eventType //事件类别（无效证件01、证件过期02、设备损坏03、查岗查哨04、查获违禁物品05、违法违规偷渡人员06）
		// intent.putExtra("eventType","02" );

		// cphm //车牌号码
		intent.putExtra("cphm", clzjxx.getCphm());

		// clpp //车辆品牌
		intent.putExtra("clpp", clzjxx.getClpp());

		// fdjh //发动机号
		intent.putExtra("fdjh", clzjxx.getFdjh());

		// company //所属单位
		intent.putExtra("company", clzjxx.getGs_yyz());

		// glcbmc //关联船舶名称
		// intent.putExtra("glcbmc",clzjxx.getCphm() );

		// jhhc //关联船舶航次
		// intent.putExtra("jhhc",clzjxx.getCphm() );

		// dockCode //码头（码头id）
		// intent.putExtra("cphm",clzjxx.getCphm() );

		// berthCode //泊位（泊位id）
		// intent.putExtra("cphm",clzjxx.getCphm() );

		switch (from) {
		case ManagerFlag.PDA_XCXJ_CLYZ:
			// areaCode //区域（区域ID）
			intent.putExtra("areacode", XcUtil.getXunjianId());
			// areaname //区域（区域名称）
			intent.putExtra("areaname", XcUtil.getXunjianName());
			intent.putExtra("source", "03");// 信息来源（卡口验证01、梯口验证02、现场巡查03）
			intent.putExtra("scene", XcUtil.getXunjianTypeForYcxx());
			intent.putExtra("jcfsStr", "02");// 现场巡查,02,人员检查03)，PDA只用02、03
			break;
		case ManagerFlag.PDA_KKGL_CLYZ:
			// scene//检查地点(在船上01、在码头02、在泊位03、在区域04)
			intent.putExtra("scene", "04");
			intent.putExtra("source", "01");// 信息来源（卡口验证01、梯口验证02、现场巡查03）
			HashMap<String, Object> bindData = SystemSetting.getBindShip(GlobalFlags.LIST_TYPE_FROM_KAKOUMANAGER + "");
			if (bindData != null) {
				// areaCode //区域（区域ID）
				intent.putExtra("areacode", (String) bindData.get("id"));
				// areaname //区域（区域名称）
				intent.putExtra("areaname", (String) bindData.get("kkmc"));
			}
			intent.putExtra("jcfsStr", "03");// 现场巡查,02,人员检查03)，PDA只用02、03
			break;

		default:
			break;
		}

		// czr // 异常信息处理操作人（当前登陆用户id）
		// intent.putExtra("cphm", clzjxx.getCphm());

		// xunjian_id // 巡查巡检ID
		// intent.putExtra("xunjian_id", clzjxx.getCphm());

		// whetherHandle //是否处理（未处理0、已处理1）
		intent.putExtra("whetherHandle", "0");
		intent.putExtra("isFromOffline", isFromOffline);

		intent.putExtra("from", from + "");
		intent.putExtra("isClyz", true);
		intent.setClass(context, Exceptioninfo.class);
		context.startActivity(intent);
		// handler.obtainMessage(KakouRequest.HANDLER_WHAT_JLYC ,
		// intent).sendToTarget();
	}

	public void xgtxfxSuccess(CardInfo cardInfo) {
		this.cardInfo = cardInfo;
		/** 通行方向 */
		String fx = cardInfo.getFx();// 0上，1下
		if (StringUtils.isEmpty(fx)) {
			fx = "";
		} else if ("0".equals(fx)) {
			fx = BaseApplication.instent.getString(R.string.inkakou);
			imageView_Traffic_direction.setVisibility(View.VISIBLE);
			imageView_Traffic_direction.setImageResource(R.drawable.up);
		} else if ("1".equals(fx)) {
			imageView_Traffic_direction.setVisibility(View.VISIBLE);
			imageView_Traffic_direction.setImageResource(R.drawable.down);
			fx = BaseApplication.instent.getString(R.string.outkakou);
		} else {
			imageView_Traffic_direction.setVisibility(View.GONE);
		}
		xgtxfx.setEnabled(false);
		txfx.setText(Html.fromHtml(txfx_title + "<font color=\"#acacac\">" + fx + "</font>"));
	}

	public void setInfo(CardInfo cardInfo) {
		this.cardInfo = cardInfo;
	}

	public void xgtxfxSuccess(Activity activity) {
		if (cardInfo == null) {
			return;
		}
		String fx = cardInfo.getFx();
		cardInfo.setFx(fx != null && fx.equals("0") ? "1" : "0");
		setInfo(activity, cardInfo, true, 1000);
	}

}
