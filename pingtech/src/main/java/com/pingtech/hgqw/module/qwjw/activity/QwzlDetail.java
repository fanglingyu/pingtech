package com.pingtech.hgqw.module.qwjw.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.pingtech.R;
import com.pingtech.hgqw.activity.MyActivity;
import com.pingtech.hgqw.interf.OnHttpResult;
import com.pingtech.hgqw.module.police.adapter.MyPoliceAdapter;
import com.pingtech.hgqw.module.police.entity.Qwzlqwjs;
import com.pingtech.hgqw.module.police.request.RequestPolice;
import com.pingtech.hgqw.module.qwjw.utils.QwzlConstant;
import com.pingtech.hgqw.utils.Log;
import com.pingtech.hgqw.utils.StringUtils;

public class QwzlDetail extends MyActivity implements OnHttpResult {
	private static final String TAG = "MyPoliceDetail";

	private Button btn_qs;

	private Qwzlqwjs qwzlqwjs = null;

	private Handler handler = new PoliceHandler();

	private class PoliceHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MyPoliceAdapter.POSITIONEXCEPT:// 签收

				break;
			case RequestPolice.SIGN_MY_TASK_SUCCESS:// 签收成功
				qwzlqwjs.setQszt("1");
				Intent data = new Intent();
				setResult(RESULT_OK, data);
				finish();
				break;
			case MyPoliceAdapter.POSITIONNOEXCEPT:// 详情
				break;
			default:
				break;
			}
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.qwzldictate);
		Log.i(TAG, "onCreate()");
		setMyActiveTitle(getString(R.string.qwjw) + ">" + getString(R.string.qwzlxxxx));
		qwzlqwjs = (Qwzlqwjs) getIntent().getSerializableExtra("qwzlqwjs");
		find();
		init();

	}

	public void click(View v) {
		switch (v.getId()) {
		case R.id.btn_qs:
			if (qwzlqwjs != null) {
				new RequestPolice(this, handler).requestSignMyTask(qwzlqwjs.getQwzljbid(), qwzlqwjs.getQwzldwid(), "QWZL", "", "", "");
			}
			break;

		default:
			break;
		}
	}

	private void find() {
		btn_qs = (Button) findViewById(R.id.btn_qs);
	}

	private void init() {
		if (qwzlqwjs != null) {
			onSendViewTaskMsg();
			otherBusiness();
			((TextView) findViewById(R.id.zllx)).setText(Html.fromHtml(getString(R.string.title_zllx) + "<font color=\"#acacac\">"
					+ QwzlConstant.getZllxName(qwzlqwjs.getZllx()) + "</font>"));

			((TextView) findViewById(R.id.cbmc)).setText(Html.fromHtml(getString(R.string.title_cbmc) + "<font color=\"#acacac\">"
					+ qwzlqwjs.getCbzwm() + "</font>"));

			((TextView) findViewById(R.id.jhfs)).setText(Html.fromHtml(getString(R.string.title_jhfs) + "<font color=\"#acacac\">"
					+ qwzlqwjs.getJhfs() + "</font>"));

			((TextView) findViewById(R.id.xjhfs)).setText(Html.fromHtml(getString(R.string.title_xjhfs) + "<font color=\"#acacac\">"
					+ qwzlqwjs.getYjhfs() + "</font>"));

			((TextView) findViewById(R.id.bghjhfs)).setText(Html.fromHtml(getString(R.string.title_bghjhfs) + "<font color=\"#acacac\">"
					+ qwzlqwjs.getXjhfs() + "</font>"));

			((TextView) findViewById(R.id.bgyy)).setText(Html.fromHtml(getString(R.string.title_bgyy) + "<font color=\"#acacac\">"
					+ qwzlqwjs.getBgyy() + "</font>"));

			((TextView) findViewById(R.id.dqtkwz)).setText(Html.fromHtml(getString(R.string.title_dqtkwz) + "<font color=\"#acacac\">"
					+ qwzlqwjs.getDqtkwz() + "</font>"));

			((TextView) findViewById(R.id.ywwz)).setText(Html.fromHtml(getString(R.string.title_ywwz) + "<font color=\"#acacac\">"
					+ qwzlqwjs.getYwwz() + "</font>"));

			((TextView) findViewById(R.id.gzyq)).setText(Html.fromHtml(getString(R.string.title_gzyq) + "<font color=\"#acacac\">"
					+ qwzlqwjs.getGzyq() + "</font>"));

			((TextView) findViewById(R.id.fbdw)).setText(Html.fromHtml(getString(R.string.title_fbdw) + "<font color=\"#acacac\">"
					+ qwzlqwjs.getFbdw() + "</font>"));

			((TextView) findViewById(R.id.fbsj)).setText(Html.fromHtml(getString(R.string.title_fbsj) + "<font color=\"#acacac\" size=18>"
					+ qwzlqwjs.getFbsj() + "</font>"));

			((TextView) findViewById(R.id.fbr)).setText(Html.fromHtml(getString(R.string.title_fbr) + "<font color=\"#acacac\">" + qwzlqwjs.getFbr()
					+ "</font>"));

			((TextView) findViewById(R.id.qsr)).setText(Html.fromHtml(getString(R.string.title_qsr) + "<font color=\"#acacac\">" + qwzlqwjs.getQsr()
					+ "</font>"));

			((TextView) findViewById(R.id.qssj)).setText(Html.fromHtml(getString(R.string.title_qssj) + "<font color=\"#acacac\" size=18 >"
					+ qwzlqwjs.getQssj() + "</font>"));

			((TextView) findViewById(R.id.ddr)).setText(Html.fromHtml(getString(R.string.title_ddr) + "<font color=\"#acacac\">" + qwzlqwjs.getDdr()
					+ "</font>"));

			((TextView) findViewById(R.id.cqry)).setText(Html.fromHtml(getString(R.string.title_cqry) + "<font color=\"#acacac\">"
					+ qwzlqwjs.getCqry() + "</font>"));

			((TextView) findViewById(R.id.yybsj)).setText(Html.fromHtml(getString(R.string.title_yybsj) + "<font color=\"#acacac\" size=18 >"
					+ qwzlqwjs.getYybsj() + "</font>"));

			((TextView) findViewById(R.id.ykbsj)).setText(Html.fromHtml(getString(R.string.title_ykbsj) + "<font color=\"#acacac\" size=18 >"
					+ qwzlqwjs.getYkbsj() + "</font>"));
		}
	}

	private void otherBusiness() {
		String zllx = qwzlqwjs.getZllx();

		/** 签收状态 0：未签收 1：已签收 */
		String qszt = qwzlqwjs.getQszt();
		if (!QwzlConstant.JBXX_ZLLX_CBJH.equals(zllx)) {
			((TextView) findViewById(R.id.jhfs)).setVisibility(View.GONE);
		}
		if (!QwzlConstant.JBXX_ZLLX_ZQBG.equals(zllx)) {
			((TextView) findViewById(R.id.xjhfs)).setVisibility(View.GONE);
			((TextView) findViewById(R.id.bghjhfs)).setVisibility(View.GONE);
			((TextView) findViewById(R.id.bgyy)).setVisibility(View.GONE);
		}

		if (!QwzlConstant.JBXX_ZLLX_CBYB.equals(zllx)) {
			((TextView) findViewById(R.id.dqtkwz)).setVisibility(View.GONE);
			((TextView) findViewById(R.id.ywwz)).setVisibility(View.GONE);
			((TextView) findViewById(R.id.yybsj)).setVisibility(View.GONE);
			((TextView) findViewById(R.id.ykbsj)).setVisibility(View.GONE);
		}

		if (QwzlConstant.QSZT_WQS.equals(qszt)) {
			((TextView) findViewById(R.id.qsr)).setVisibility(View.GONE);
			((TextView) findViewById(R.id.qssj)).setVisibility(View.GONE);
			((TextView) findViewById(R.id.ddr)).setVisibility(View.GONE);
			((TextView) findViewById(R.id.cqry)).setVisibility(View.GONE);
		} else {
			btn_qs.setText(R.string.yiqianshou);
			btn_qs.setEnabled(false);
		}

		// 签收人、签收时间、带队人、出勤人员为空则不显示
		String qsr = qwzlqwjs.getQsr();
		String qssj = qwzlqwjs.getQssj();
		String ddr = qwzlqwjs.getDdr();
		String cqry = qwzlqwjs.getCqry();
		if (StringUtils.isEmpty(qsr)) {
			((TextView) findViewById(R.id.qsr)).setVisibility(View.GONE);
		}
		if (StringUtils.isEmpty(qssj)) {
			((TextView) findViewById(R.id.qssj)).setVisibility(View.GONE);
		}
		if (StringUtils.isEmpty(ddr)) {
			((TextView) findViewById(R.id.ddr)).setVisibility(View.GONE);
		}
		if (StringUtils.isEmpty(cqry)) {
			((TextView) findViewById(R.id.cqry)).setVisibility(View.GONE);
		}
	}

	private void onSendViewTaskMsg() {
		if (qwzlqwjs != null) {
			new RequestPolice(this, handler).requestSendViewTaskMsg(qwzlqwjs.getQwzljbid(), qwzlqwjs.getQwzldwid(), "QWZL", "", "", "");
		}
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	@Override
	public void onDestroy() {
		Log.i(TAG, "onDestroy()");
		super.onDestroy();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

	}

	@Override
	public void onHttpResult(String str, int httpRequestType) {
	}

}
