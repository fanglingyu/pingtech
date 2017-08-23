package com.pingtech.hgqw.module.publices.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.pingtech.R;
import com.pingtech.hgqw.entity.CardInfo;
import com.pingtech.hgqw.readcard.service.ReadService;
import com.pingtech.hgqw.utils.Log;
import com.pingtech.hgqw.utils.StringUtils;
import com.pingtech.hgqw.widget.HgqwToast;

public class ReadCardFragment extends Fragment implements OnClickListener {

	private static final String TAG = "ReadCardFragment";

	private EditText edt_cphm = null;
	private EditText edt_jszbh_sfzh = null;

	private Button btnok = null;

	private Button cxcb = null;

	private Button zxing = null;

	private Handler handler = null;


	public ReadCardFragment(Handler handler) {
		this.handler = handler;
	}

	@Override
	public void onAttach(Activity activity) {
		Log.i(TAG, "onAttach()");
		super.onAttach(activity);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "onCreate()");
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.i(TAG, "onCreateView()");
		View view = inflater.inflate(R.layout.readcard_kakou_cljc, null);
		find(view);
		init();
		return view;
	}

	private void init() {
		btnok.setOnClickListener(this);
		cxcb.setOnClickListener(this);
		zxing.setOnClickListener(this);
	}

	private void find(View view) {
		edt_jszbh_sfzh = (EditText) view.findViewById(R.id.edt_jszbh_sfzh);
		edt_cphm = (EditText) view.findViewById(R.id.edt_cphm);
		cxcb = (Button) view.findViewById(R.id.cxcb);
		zxing = (Button) view.findViewById(R.id.zxing);
		btnok = (Button) view.findViewById(R.id.btnok);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		Log.i(TAG, "onActivityCreated()");
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onStart() {
		Log.i(TAG, "onStart()");
		super.onStart();
	}

	@Override
	public void onResume() {
		Log.i(TAG, "onResume()");
		super.onResume();
	}

	@Override
	public void onPause() {
		Log.i(TAG, "onPause()");
		super.onPause();
	}

	@Override
	public void onStop() {
		Log.i(TAG, "onStop()");
		super.onStop();
	}

	@Override
	public void onDestroyView() {
		Log.i(TAG, "onDestroyView()");
		super.onDestroyView();
	}

	@Override
	public void onDestroy() {
		Log.i(TAG, "onDestroy()");
		super.onDestroy();
	}

	public void updateCardNum(CardInfo cardInfo) {
		if (cardInfo == null) {
			return;
		}
		int cardType = cardInfo.getCardType();
		String num = "";
		switch (cardType) {
		case CardInfo.CARD_TYPE_IC:
			num = cardInfo.getIckey();
			break;
		case CardInfo.CARD_TYPE_ID:
			num = cardInfo.getPeople().getPeopleIDCode();
			break;
		case CardInfo.CARD_TYPE_SDSR:

			break;
		case CardInfo.CARD_TYPE_ZXING:

			break;

		default:
			break;
		}
		/*if (edt_jszbh_sfzh != null) {
			edt_jszbh_sfzh.setText(num);
		}
		if (edt_cphm != null) {
			edt_cphm.setText(num);
		}*/
	}

	@Override
	public void onClick(View v) {
		Log.i(TAG, "onClick");
		switch (v.getId()) {
		case R.id.btnok:
			sdsr();
			break;
		case R.id.cxcb:

			break;
		case R.id.zxing:

			break;

		default:
			break;
		}
	
	}

	/**
	 * 手动输入
	 */
	private void sdsr() {
		String cphm = edt_cphm.getText().toString().trim();
		String jszbh_sfzh = edt_jszbh_sfzh.getText().toString().trim();
		if(StringUtils.isEmpty(cphm)){
			HgqwToast.toast(getString(R.string.cphm_cannot_empty));
			return;
		}
		CardInfo cardInfo = new CardInfo();
		cardInfo.setCardType(CardInfo.CARD_TYPE_SDSR);
		cardInfo.setCphm(cphm);
		cardInfo.setJszbh_sfzh(jszbh_sfzh);
		handler.obtainMessage(ReadService.READ_TYPE_SDSR , cardInfo).sendToTarget();
	}

}
