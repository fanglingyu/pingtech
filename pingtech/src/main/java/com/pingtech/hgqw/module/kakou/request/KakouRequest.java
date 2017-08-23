package com.pingtech.hgqw.module.kakou.request;

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
import com.pingtech.hgqw.module.kakou.action.KakouAction;
import com.pingtech.hgqw.module.kakou.utils.PullXmlUtil;
import com.pingtech.hgqw.module.offline.base.utils.OffLineManager;
import com.pingtech.hgqw.module.offline.zjyf.util.YfZjxxConstant;
import com.pingtech.hgqw.module.publices.utils.Utils;
import com.pingtech.hgqw.module.xtgl.activity.FunctionSetting;
import com.pingtech.hgqw.module.xunjian.request.XunjianRequest;
import com.pingtech.hgqw.utils.BasicNameValuePair;
import com.pingtech.hgqw.utils.NVPairTOMap;
import com.pingtech.hgqw.utils.StringUtils;
import com.pingtech.hgqw.utils.SystemSetting;
import com.pingtech.hgqw.web.NetWorkManager;
import com.pingtech.hgqw.widget.HgqwToast;

public class KakouRequest implements OnHttpResult, OffLineResult {
	/** 调用卡口车辆验放 */
	private static final int REQUEST_TYPE_INSPECTCLFORKK = 0;

	/** 调用卡口车辆验放 */
	private static final int REQUEST_TYPE_INSPECTCLFORKK_OFFLINE = 2;

	private static final int HTTPREQUEST_TYPE_FOR_MODIFY_CL_PASSDIRECTION = 1;

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

	public KakouRequest(Context context, Handler handler) {
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
		case REQUEST_TYPE_INSPECTCLFORKK:
			responseInspectClForKk(str, httpRequestType);
			break;
		case REQUEST_TYPE_INSPECTCLFORKK_OFFLINE:
			responseInspectClForKkOffline(str, httpRequestType);
			break;
		case HTTPREQUEST_TYPE_FOR_MODIFY_CL_PASSDIRECTION:
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
	private void responseInspectClForKkOffline(String str, int httpRequestType) {
		if (StringUtils.isEmpty(str)) {
			BaseDialogUtils.dismissRequestDialog();
			// handler.obtainMessage(HANDLER_WHAT_DEALREADCARDINFO_TRUE).sendToTarget();
			// HgqwToast.toast(R.string.data_download_failure_info);
			requestInspectClForKk(this.cardInfo, true);
			return;
		}

		CardInfo cardInfo = PullXmlUtil.pullXmlInspectForKkCl(str);
		// 解析报错
		if (cardInfo == null) {
			BaseDialogUtils.dismissRequestDialog();
			// handler.obtainMessage(HANDLER_WHAT_DEALREADCARDINFO_TRUE).sendToTarget();
			// BaseApplication.soundManager.onPlaySound(3, 0);// 验证失败提示音
			// HgqwToast.toast(R.string.data_download_failure_info);
			requestInspectClForKk(this.cardInfo, true);
			return;
		}

		// 返回错误提示
		if (!cardInfo.isResult()) {
			BaseDialogUtils.dismissRequestDialog();
			handler.obtainMessage(HANDLER_WHAT_DEALREADCARDINFO_TRUE)
					.sendToTarget();
			HgqwToast.toast(cardInfo.getInfo());
			// 提示音
			if (cardInfo.isPass()) {
				BaseApplication.soundManager.onPlaySound(2, 0);// 成功提示音
			} else {
				BaseApplication.soundManager.onPlaySound(3, 0);// 验证失败提示音
			}
			return;
		}

		if (!cardInfo.isHasCardInfo()) {
			// 手动保存页面
			BaseDialogUtils.dismissRequestDialog();
			// handler.obtainMessage(HANDLER_WHAT_DEALREADCARDINFO_TRUE).sendToTarget();
			// handler.obtainMessage(HANDLER_WHAT_SDBC,
			// this.cardInfo).sendToTarget();
			requestInspectClForKk(this.cardInfo, true);
			return;
		}
		// 提示音
		if (cardInfo.isPass()) {
			BaseApplication.soundManager.onPlaySound(2, 0);// 成功提示音
		} else {
			BaseApplication.soundManager.onPlaySound(3, 0);// 验证失败提示音
		}
		switch (httpRequestType) {
		case XunjianRequest.REQUEST_TYPE_SENDCLSWIPERECORD:
			cardInfo.setOffline(false);
			break;
		case XunjianRequest.REQUEST_TYPE_SENDCLSWIPERECORD_OFFLINE:
			cardInfo.setOffline(true);
			break;
		default:
			break;
		}
		// 显示详情
		BaseDialogUtils.dismissRequestDialog();
		handler.obtainMessage(HANDLER_WHAT_DEALREADCARDINFO_TRUE)
				.sendToTarget();
		handler.obtainMessage(HANDLER_WHAT_DETAIL, httpRequestType, 0, cardInfo)
				.sendToTarget();
	}

	private void responseInspectClForKk(String str, int httpRequestType) {
		if (StringUtils.isEmpty(str)) {
			BaseDialogUtils.dismissRequestDialog();
			handler.obtainMessage(HANDLER_WHAT_DEALREADCARDINFO_TRUE)
					.sendToTarget();
			HgqwToast.toast(R.string.data_download_failure_info);
			return;
		}

		CardInfo cardInfo = PullXmlUtil.pullXmlInspectForKkCl(str);
		// 解析报错
		if (cardInfo == null) {
			BaseDialogUtils.dismissRequestDialog();
			handler.obtainMessage(HANDLER_WHAT_DEALREADCARDINFO_TRUE)
					.sendToTarget();
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
			handler.obtainMessage(HANDLER_WHAT_DEALREADCARDINFO_TRUE)
					.sendToTarget();
			HgqwToast.toast(cardInfo.getInfo());
			return;
		}

		if (!cardInfo.isHasCardInfo()) {
			// 手动保存页面
			BaseDialogUtils.dismissRequestDialog();
			handler.obtainMessage(HANDLER_WHAT_DEALREADCARDINFO_TRUE)
					.sendToTarget();
			handler.obtainMessage(HANDLER_WHAT_SDBC, this.cardInfo)
					.sendToTarget();
			return;
		}

		switch (httpRequestType) {
		case XunjianRequest.REQUEST_TYPE_SENDCLSWIPERECORD:
			cardInfo.setOffline(false);
			break;
		case XunjianRequest.REQUEST_TYPE_SENDCLSWIPERECORD_OFFLINE:
			cardInfo.setOffline(true);
			break;
		default:
			break;
		}
		// 显示详情
		BaseDialogUtils.dismissRequestDialog();
		handler.obtainMessage(HANDLER_WHAT_DEALREADCARDINFO_TRUE)
				.sendToTarget();
		handler.obtainMessage(HANDLER_WHAT_DETAIL, httpRequestType, 0, cardInfo)
				.sendToTarget();
	}

	/**
	 * 请求卡口车辆验放
	 * 
	 * @param cardInfo
	 * @param isFromOffline
	 *            TODO
	 */
	public void requestInspectClForKk(CardInfo cardInfo, boolean isFromOffline) {
		this.cardInfo = cardInfo;
		HashMap<String, Object> bindData = SystemSetting
				.getBindShip(GlobalFlags.LIST_TYPE_FROM_KAKOUMANAGER + "");
		if (bindData == null || cardInfo == null) {
			return;
		}
		handler.obtainMessage(HANDLER_WHAT_DEALREADCARDINFO_FALSE)
				.sendToTarget();
		String url = "inspectClForKk";

		List<NameValuePair> params = new ArrayList<NameValuePair>();

		params.add(new BasicNameValuePair("kkID", (String) bindData.get("id")));
		params.add(new BasicNameValuePair("kkmc", (String) bindData.get("kkmc")));
		params.add(new BasicNameValuePair("userID", LoginUser
				.getCurrentLoginUser().getUserID()));

		params.add(new BasicNameValuePair("defaultickey", cardInfo
				.getDefaultIckey()));

		String cardNumber = "";
		if (cardInfo.getCardType() == CardInfo.CARD_TYPE_SDSR) {
			cardNumber = cardInfo.getJszbh_sfzh();
		} else {
			cardNumber = cardInfo.getIckey();
		}

		params.add(new BasicNameValuePair("cardNumber", cardNumber));
		params.add(new BasicNameValuePair("cphm", cardInfo.getCphm()));
		params.add(new BasicNameValuePair("jszbh_sfzh", cardInfo
				.getJszbh_sfzh()));
		params.add(new BasicNameValuePair("sfsk", cardInfo.getCardType() + ""));
		params.add(new BasicNameValuePair("PDACode", SystemSetting.getPDACode()));
		BaseDialogUtils.showRequestDialog(context, isFromOffline);
		if (isFromOffline) {
			NetWorkManager.request(this, url, params,
					REQUEST_TYPE_INSPECTCLFORKK);
			return;
		}
		if (!Utils.getState(FunctionSetting.bdtxyz, true)) {
			NetWorkManager.request(this, url, params,
					REQUEST_TYPE_INSPECTCLFORKK);
		} else {
			OffLineManager.request(this, new KakouAction(), url,
					NVPairTOMap.nameValuePairTOMap(params),
					REQUEST_TYPE_INSPECTCLFORKK_OFFLINE);
		}

	}

	public void requestXgtxfx(CardInfo cardInfo) {
		String url = "modifyKkPassDirection";
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("recordid", cardInfo.getTxjlid()));
		params.add(new BasicNameValuePair("userid", LoginUser
				.getCurrentLoginUser().getUserID()));
		String fx = cardInfo.getFx();
		params.add(new BasicNameValuePair("passDirection", (fx != null && fx
				.equals("0")) ? "1" : "0"));
		params.add(new BasicNameValuePair("time", new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss").format(new Date(System
				.currentTimeMillis()))));

		HashMap<String, Object> bindData = SystemSetting
				.getBindShip(GlobalFlags.LIST_TYPE_FROM_KAKOUMANAGER + "");
		if (bindData == null || cardInfo == null) {
			return;
		}
		params.add(new BasicNameValuePair("kkID", (String) bindData.get("id")));
		params.add(new BasicNameValuePair("hgzl", YfZjxxConstant.ZJLX_XDQY));
		params.add(new BasicNameValuePair("ryid", cardInfo.getClzjxx()
				.getRyid()));
		params.add(new BasicNameValuePair("kkmc", (String) bindData.get("kkmc")));

		BaseDialogUtils.showRequestDialog(context, false);
		if (!Utils.getState(FunctionSetting.bdtxyz, true)) {
			NetWorkManager.request(this, url, params,
					HTTPREQUEST_TYPE_FOR_MODIFY_CL_PASSDIRECTION);
		} else {
			url = "modifyKkClPassDirection";
			OffLineManager.request(this, new KakouAction(), url,
					NVPairTOMap.nameValuePairTOMap(params),
					HTTPREQUEST_TYPE_FOR_MODIFY_CL_PASSDIRECTION);
		}
	}

}
