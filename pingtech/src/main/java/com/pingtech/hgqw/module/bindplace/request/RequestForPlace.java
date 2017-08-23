package com.pingtech.hgqw.module.bindplace.request;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;

import android.content.Context;
import android.os.Handler;
import android.util.Pair;

import com.pingtech.R;
import com.pingtech.hgqw.base.BaseApplication;
import com.pingtech.hgqw.base.dialog.BaseDialogUtils;
import com.pingtech.hgqw.interf.OffLineResult;
import com.pingtech.hgqw.interf.OnHttpResult;
import com.pingtech.hgqw.module.bindplace.utils.SharedPreferencesUtil;
import com.pingtech.hgqw.utils.BasicNameValuePair;
import com.pingtech.hgqw.utils.DeviceUtils;
import com.pingtech.hgqw.utils.HgqwToastUtil;
import com.pingtech.hgqw.utils.StringUtils;
import com.pingtech.hgqw.utils.SystemSetting;
import com.pingtech.hgqw.web.NetWorkManager;
import com.pingtech.hgqw.widget.HgqwToast;

public class RequestForPlace implements OnHttpResult, OffLineResult {
	private Handler handler = null;

	public static final int BIND_SUCCESS = 10000;

	public static final int BIND_FAILE = 10001;

	public static final int UN_BIND_SUCCESS = 10002;

	public static final int UN_BIND_FAILE = 10003;

	public static final int BUILD_XCXJ_RELATION_BIND = 1;
	public static final int BUILD_XCXJ_RELATION_UNBIND = 0;
	
	private Context context = null;
	private int bindState = -1;

	public RequestForPlace(Handler handler) {
		this.handler = handler;
	}

	public RequestForPlace(Handler handler, Context context) {
		this.handler = handler;
		this.context = context;
	}

	@Override
	public void offLineResult(Pair<Boolean, Object> res, int offLineRequestType) {

	}

	@Override
	public void onHttpResult(String str, int httpRequestType) {
		switch (httpRequestType) {
		case BUILD_XCXJ_RELATION_BIND:
			responseBuildXcxjRelationBind(str);
			break;
		case BUILD_XCXJ_RELATION_UNBIND:
			responseBuildXcxjRelationUnBind(str);
			break;
		default:
			break;
		}
	}

	private void responseBuildXcxjRelationBind(String result) {
		if (StringUtils.isEmpty(result)) {
			HgqwToastUtil.requestNullToast();
			BaseDialogUtils.dismissRequestDialog();
			return;
		}
		if ("-1".equals(result)) {
			HgqwToast.toast(R.string.bindship_failure);
			BaseDialogUtils.dismissRequestDialog();
			return;
		}
		// 1成功，-1失败
		if ("1".equals(result)) {
			HgqwToast.toast(R.string.bindship_success);
			// handler通知Activity下载离线数据
			if (handler != null) {
				// 先将地点绑定关系保存到本地
				SharedPreferencesUtil.saveDdbdToSharedPre();
				handler.obtainMessage(BIND_SUCCESS).sendToTarget();
			}
			return;
		}

	}
	private void responseBuildXcxjRelationUnBind(String result) {
		if (StringUtils.isEmpty(result)) {
			HgqwToastUtil.requestNullToast();
			BaseDialogUtils.dismissRequestDialog();
			return;
		}
		if ("-1".equals(result)) {
			HgqwToast.toast(R.string.unbindship_failure);
			BaseDialogUtils.dismissRequestDialog();
			return;
		}
		// 1成功，-1失败
		if ("1".equals(result)) {
			HgqwToast.toast(R.string.unbindship_success);
			// handler通知Activity下载离线数据
			if (handler != null) {
				handler.obtainMessage(UN_BIND_SUCCESS).sendToTarget();
			}
			return;
		}
		
	}

	/**
	 * @param zqdxlx
	 *            执勤对象类型 :船舶0 ,卡口(区域),1 码头2 ,泊位3
	 * @param zqdxId
	 *            执勤对象id
	 * @param bindState
	 *            绑定状态（1绑定，0解除绑定）
	 */
	public void requestBuildXcxjRelation(String zqdxlx, String zqdxId, int bindState) {
		this.bindState = bindState;
		// String userID = (String) request.getParameter("userID"); // 当前登录用户ID
		// String pdacode = (String) request.getParameter("PDACode"); // PDA编号
		// String bindState = (String) request.getParameter("bindState"); //
		// 绑定状态（1绑定，0解除绑定）
		// String zqlx = (String) request.getParameter("bindType"); //
		// 绑定类型（船舶动态0、梯口管理1、巡查巡检2）
		// String zqdxlx = (String) request.getParameter("zqdxlx"); //执勤对象类型:船舶0
		// 卡口(区域)1 码头2 泊位3
		// String zqdxId = (String) request.getParameter("zqdxId");//执勤对象id
		String url = "buildXcxjRelation";
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("userID", BaseApplication.instent.gainUserID()));
		params.add(new BasicNameValuePair("PDACode", DeviceUtils.getIMEI()));
		params.add(new BasicNameValuePair("bindState", bindState+""));
		params.add(new BasicNameValuePair("bindType", "2"));
		params.add(new BasicNameValuePair("zqdxlx", zqdxlx));
		params.add(new BasicNameValuePair("zqdxId", SystemSetting.xunJianId));

		// handler.obtainMessage(-1).sendToTarget();
		BaseDialogUtils.showRequestDialog(context, false);
		NetWorkManager.request(this, url, params, bindState);
	}


	/*protected void showRequestDialog() {
		BaseApplication.instent.progressDialog = new ProgressDialog(context);
		BaseApplication.instent.progressDialog.setTitle(BaseApplication.instent.getString(R.string.Validing));
		BaseApplication.instent.progressDialog.setMessage(BaseApplication.instent.getString(R.string.waiting));
		BaseApplication.instent.progressDialog.setCancelable(false);
		BaseApplication.instent.progressDialog.setIndeterminate(false);
		BaseApplication.instent.progressDialog.show();
	}

	protected void dismissRequestDialog() {
		if (BaseApplication.instent.progressDialog != null) {
			BaseApplication.instent.progressDialog.dismiss();
			BaseApplication.instent.progressDialog = null;
		}
	}*/
}
