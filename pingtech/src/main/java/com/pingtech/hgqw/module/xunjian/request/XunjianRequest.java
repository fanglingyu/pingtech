package com.pingtech.hgqw.module.xunjian.request;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;

import android.content.Context;
import android.os.Handler;
import android.util.Pair;

import com.pingtech.R;
import com.pingtech.hgqw.base.BaseApplication;
import com.pingtech.hgqw.base.dialog.BaseDialogUtils;
import com.pingtech.hgqw.entity.CardInfo;
import com.pingtech.hgqw.entity.GlobalFlags;
import com.pingtech.hgqw.entity.LoginUser;
import com.pingtech.hgqw.interf.OffLineResult;
import com.pingtech.hgqw.interf.OnHttpResult;
import com.pingtech.hgqw.module.kakou.utils.PullXmlUtil;
import com.pingtech.hgqw.module.offline.base.utils.OffLineManager;
import com.pingtech.hgqw.module.publices.utils.Utils;
import com.pingtech.hgqw.module.xtgl.activity.FunctionSetting;
import com.pingtech.hgqw.module.xunjian.action.XunJianAction;
import com.pingtech.hgqw.module.xunjian.utils.XcUtil;
import com.pingtech.hgqw.utils.BasicNameValuePair;
import com.pingtech.hgqw.utils.NVPairTOMap;
import com.pingtech.hgqw.utils.StringUtils;
import com.pingtech.hgqw.utils.SystemSetting;
import com.pingtech.hgqw.web.NetWorkManager;
import com.pingtech.hgqw.widget.HgqwToast;

public class XunjianRequest implements OnHttpResult, OffLineResult {
	/** 调用卡口车辆验放 */
	public static final int REQUEST_TYPE_SENDCLSWIPERECORD = 0;
	/** 调用卡口车辆验放_离线 */
	public static final int REQUEST_TYPE_SENDCLSWIPERECORD_OFFLINE = 2;

	private static final int HTTPREQUEST_TYPE_FOR_MODIFY_PASSDIRECTION = 1;

	/** 更新读卡接口处理标识，处理 */
	public static final int HANDLER_WHAT_DEALREADCARDINFO_TRUE = -10;

	/** 更新读卡接口处理标识，不处理 */
	public static final int HANDLER_WHAT_DEALREADCARDINFO_FALSE = -20;

	/** 手动保存 */
	public static final int HANDLER_WHAT_SDBC = -30;

	/** 详情页面 */
	public static final int HANDLER_WHAT_DETAIL = -40;

	/** 记录异常 */
	public static final int HANDLER_WHAT_JLYC = -50;

	/** 修改通行方向成功 */
	public static final int HANDLER_WHAT_XGTXFX_SUCCESS = -60;

	private Handler handler;

	private Context context;

	private CardInfo cardInfo;

	public XunjianRequest(Context context, Handler handler) {
		this.handler = handler;
		this.context = context;
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
		switch (httpRequestType) {
		case REQUEST_TYPE_SENDCLSWIPERECORD:
		case REQUEST_TYPE_SENDCLSWIPERECORD_OFFLINE:
			responseSendClSwipeRecord(str , httpRequestType);
			break;
		case HTTPREQUEST_TYPE_FOR_MODIFY_PASSDIRECTION:
			responseXgtxfx(str);
			break;

		default:
			break;
		}
	}

	private void responseXgtxfx(String str) {
		boolean success = false;
		BaseDialogUtils.dismissRequestDialog();
		if (str != null && "1".equals(str)) {
			success = true;
		}
		if (success) {
			HgqwToast.toast(R.string.modify_success);
			handler.obtainMessage(HANDLER_WHAT_XGTXFX_SUCCESS).sendToTarget();
		} else {
			HgqwToast.toast(R.string.modify_failure);
		}

	}

	/**
	 * 处理卡口车辆验放返回结果
	 * 
	 * @param str
	 * @param httpRequestType 
	 */
	private void responseSendClSwipeRecord(String str, int httpRequestType) {
		if (StringUtils.isEmpty(str)) {
			BaseDialogUtils.dismissRequestDialog();
			handler.obtainMessage(HANDLER_WHAT_DEALREADCARDINFO_TRUE).sendToTarget();
			HgqwToast.toast(R.string.data_download_failure_info);
			return;
		}

		CardInfo cardInfo = PullXmlUtil.pullXmlInspectForKkCl(str);
		// 解析报错
		if (cardInfo == null) {
			BaseDialogUtils.dismissRequestDialog();
			handler.obtainMessage(HANDLER_WHAT_DEALREADCARDINFO_TRUE).sendToTarget();
			BaseApplication.soundManager.onPlaySound(3, 0);// 验证失败提示音
			HgqwToast.toast(R.string.data_download_failure_info);
			return;
		}

		// 提示音
		if (cardInfo.isPass()) {
			BaseApplication.soundManager.onPlaySound(2, 0);// 成功提示音
		} else {
			BaseApplication.soundManager.onPlaySound(3, 0);// 验证失败提示音
		}

		// 返回错误提示
		if (!cardInfo.isResult()) {
			BaseDialogUtils.dismissRequestDialog();
			handler.obtainMessage(HANDLER_WHAT_DEALREADCARDINFO_TRUE).sendToTarget();
			HgqwToast.toast(cardInfo.getInfo());
			return;
		}

		if (!cardInfo.isHasCardInfo()) {
			// 手动保存页面
			BaseDialogUtils.dismissRequestDialog();
			handler.obtainMessage(HANDLER_WHAT_DEALREADCARDINFO_TRUE).sendToTarget();
			handler.obtainMessage(HANDLER_WHAT_SDBC, this.cardInfo).sendToTarget();
			/*if(!XcUtil.getXunjianType().equals("1")){
				HgqwToast.toast("验证失败，不是边防证件");
			}else{
			}*/
			return;
		}
		// 显示详情
		BaseDialogUtils.dismissRequestDialog();
		handler.obtainMessage(HANDLER_WHAT_DEALREADCARDINFO_TRUE).sendToTarget();
		handler.obtainMessage(HANDLER_WHAT_DETAIL ,httpRequestType , 0 , cardInfo).sendToTarget();
	}

	/**
	 * 请求卡口车辆验放
	 * 
	 * @param cardInfo
	 */
	public void requestSendClSwipeRecord(CardInfo cardInfo) {
		this.cardInfo = cardInfo;
		HashMap<String, Object> bindData = SystemSetting.getBindShip(GlobalFlags.LIST_TYPE_FROM_XUNCHAXUNJIAN + "");
		if (cardInfo == null) {
			return;
		}

		if (StringUtils.isEmpty(SystemSetting.xunJianId) && bindData == null) {
			return;
		}
		
		String hc = null;

		if ( bindData != null) {
			hc = (String) bindData.get("hc");
		}
		
		handler.obtainMessage(HANDLER_WHAT_DEALREADCARDINFO_FALSE).sendToTarget();
		String url = "sendClSwipeRecord";
		// sfsk = (String) request.getParameter("sfsk");// 是否刷卡：0刷卡、1手动输入、2二维码扫描
		// type = (String) request.getParameter("type");//
		// 当前巡查地点，返回对应代码,口岸：ka，码头：mt，泊位：bw，区域：qy，浮筒：ft，锚地：md
		// ddID = (String) request.getParameter("ddID");// 巡查地点对象ID
		// cardNumber = (String) request.getParameter("cardNumber");// 卡号或证件号码
		// cphm = (String) request.getParameter("cphm"); //车牌号码。
		// userID = (String) request.getParameter("userID");// 当前登录PDA用户ID
		// pdaCode = (String) request.getParameter("PDACode");// PDA编号
		// time = DateUtils.dateToString(new Date());// 统一使用服务器时间
		// xjlx// 巡检类型：01日常巡检人员、06查岗查哨
		// longitude = request.getParameter("longitude");// 经度
		// latitude = request.getParameter("latitude");// 纬度
		//
		String sfsk = cardInfo.getCardType() + "";
		String ddID = XcUtil.getXunjianId();
		String cardNumber ="";
		if(cardInfo.getCardType()==CardInfo.CARD_TYPE_SDSR){
			  cardNumber = cardInfo.getJszbh_sfzh();
		}else{
			  cardNumber = cardInfo.getIckey();
		}
		
		String type = XcUtil.getXunjianType02();
		String defaultickey = cardInfo.getDefaultIckey();
		String cphm = cardInfo.getCphm();
		String userID = LoginUser.getCurrentLoginUser().getUserID();
		String jszbh_sfzh = cardInfo.getJszbh_sfzh();
		String pdaCode = SystemSetting.getPDACode();
		
		String xjlx = "01";
		String longitude = BaseApplication.instent.getLongitude();
		String latitude = BaseApplication.instent.getLatitude();

		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();

		params.add(new BasicNameValuePair("sfsk", sfsk));
		params.add(new BasicNameValuePair("type", type));
		params.add(new BasicNameValuePair("ddID", ddID));

		params.add(new BasicNameValuePair("cardNumber", cardNumber));
		params.add(new BasicNameValuePair("defaultickey", defaultickey));
		params.add(new BasicNameValuePair("cphm", cphm));
		params.add(new BasicNameValuePair("voyageNumber", hc));
		params.add(new BasicNameValuePair("userID", userID));
		params.add(new BasicNameValuePair("jszbh_sfzh", jszbh_sfzh));
		params.add(new BasicNameValuePair("PDACode", pdaCode));
		params.add(new BasicNameValuePair("xjlx", xjlx));
		params.add(new BasicNameValuePair("longitude", longitude));
		params.add(new BasicNameValuePair("latitude", latitude));

		BaseDialogUtils.showRequestDialog(context, false);
		if (!Utils.getState(FunctionSetting.bdtxyz, true)) {
			NetWorkManager.request(this, url, params, REQUEST_TYPE_SENDCLSWIPERECORD);
		} else {
			OffLineManager.request(this, new XunJianAction(), url, NVPairTOMap.nameValuePairTOMap(params), REQUEST_TYPE_SENDCLSWIPERECORD_OFFLINE);
		}
	}

	public void requestXgtxfx(CardInfo cardInfo) {
		String url = "modifyKkPassDirection";
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("recordid", cardInfo.getTxjlid()));
		params.add(new BasicNameValuePair("userid", LoginUser.getCurrentLoginUser().getUserID()));
		String fx = cardInfo.getFx();
		params.add(new BasicNameValuePair("passDirection", (fx != null && fx.equals("0")) ? "1" : "0"));
		params.add(new BasicNameValuePair("time", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(System.currentTimeMillis()))));
		BaseDialogUtils.showRequestDialog(context, false);
		NetWorkManager.request(this, url, params, HTTPREQUEST_TYPE_FOR_MODIFY_PASSDIRECTION);
	}

}
