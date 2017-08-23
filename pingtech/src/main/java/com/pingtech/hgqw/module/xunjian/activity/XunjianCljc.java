package com.pingtech.hgqw.module.xunjian.activity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;

import com.pingtech.R;
import com.pingtech.hgqw.base.BaseApplication;
import com.pingtech.hgqw.base.activity.BaseActivity;
import com.pingtech.hgqw.entity.CardInfo;
import com.pingtech.hgqw.entity.ManagerFlag;
import com.pingtech.hgqw.entity.MessageEntity;
import com.pingtech.hgqw.module.exception.activity.Exceptioninfo;
import com.pingtech.hgqw.module.publices.fragment.ClzjxxDetileFragment;
import com.pingtech.hgqw.module.publices.fragment.ReadCardFragment;
import com.pingtech.hgqw.module.xunjian.request.XunjianRequest;
import com.pingtech.hgqw.readcard.service.ReadService;
import com.pingtech.hgqw.readcard.utils.ReadCardTools;
import com.pingtech.hgqw.utils.Log;
import com.pingtech.hgqw.widget.HgqwToast;

public class XunjianCljc extends BaseActivity {
	private final String TAG = "XunjianCljc";

	/** 读卡结果返回后是否处理 */
	private boolean ifDealReadCardInfo = true;

	private CardInfo cardInfo = null;

	private ReadCardFragment readCardfragment = null;

	private ClzjxxDetileFragment clzjxxDetileFragment = null;

	private FragmentManager fragmentManager = null;

	private static final String READCARD_FRAGMENT_TAG = "readCardfragment";

	private static final String INFODETILE_FRAGMENT_TAG = "clzjxxDetileFragment";

	public static final int BUTTON_EVENT_BACK = -1000;

	private Button btnok = null;

	private Button cxcb = null;

	private Button zxing = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "onCreate()");
		String title = getString(R.string.xunchaxunjian) + ">" + getString(R.string.clxj);
		super.onCreate(savedInstanceState, R.layout.kakou_clgl_activity, title);
		find();
		init();
	}

	@Override
	protected void find() {
		fragmentManager = getFragmentManager();
		readCardfragment = new ReadCardFragment(handler);
		clzjxxDetileFragment = new ClzjxxDetileFragment(this, handler, ManagerFlag.PDA_XCXJ_CLYZ);

	}

	@Override
	protected void init() {
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		fragmentTransaction.add(R.id.fragment, readCardfragment, READCARD_FRAGMENT_TAG);
//		fragmentTransaction.add(R.id.fragment, clzjxxDetileFragment, INFODETILE_FRAGMENT_TAG);
//		fragmentTransaction.hide(clzjxxDetileFragment);
		fragmentTransaction.commit();
	}

	@Override
	public void click(View v) {
	}

	private void initRfid() {
		ReadService readService = ReadService.getInstent(this, handler , ReadService.READ_TYPE_DEFAULT_AND_ICKEY , true);
		readService.init();
	}

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case MessageEntity.TOAST:
				HgqwToast.toast((String) msg.obj);
				break;
			case ReadService.READ_TYPE_DEFAULT_AND_ICKEY:
			case ReadService.READ_TYPE_ID:
			case ReadService.READ_TYPE_ICKEY:
			case ReadService.READ_TYPE_SDSR:
				dealCard(msg);
				break;
			case XunjianRequest.HANDLER_WHAT_DEALREADCARDINFO_FALSE:
			case XunjianRequest.HANDLER_WHAT_DEALREADCARDINFO_TRUE:
				updateDealFlag(msg.what);
				break;
			case XunjianRequest.HANDLER_WHAT_SDBC:
				sdbc(msg);
				break;
			case XunjianRequest.HANDLER_WHAT_DETAIL:
				
				detail2((CardInfo)msg.obj , false , msg.arg1);
				break;
			case XunjianRequest.HANDLER_WHAT_JLYC:
				detailJlyc(msg);
				break;
			case BUTTON_EVENT_BACK:
				back();
				break;
			case XunjianRequest.HANDLER_WHAT_XGTXFX_SUCCESS:
				xgtxfxSuccess();
				break;
			default:
				break;
			}
		}
	};

	protected void dealCard(Message msg) {
		cardInfo = null;
		if (!ifDealReadCardInfo) {
			Log.i(TAG, "read success deal is false,return!");
			return;
		}
		cardInfo = ReadCardTools.rebuildCardInfo(msg, cardInfo);
		if (cardInfo == null) {
			return;
		}
		if(cardInfo.getCardType()!=CardInfo.CARD_TYPE_SDSR){
			BaseApplication.soundManager.onPlaySoundNoVb(4, 0);
		}
		new XunjianRequest(this, handler).requestSendClSwipeRecord(cardInfo);
		if (readCardfragment != null) {
			readCardfragment.updateCardNum(cardInfo);
		}
	}

	protected void xgtxfxSuccess() {
		if (cardInfo == null) {
			return;
		}
		String fx = cardInfo.getFx();
		cardInfo.setFx(fx != null && fx.equals("0") ? "1" : "0");
		detail2(cardInfo , true , 0);
//		clzjxxDetileFragment.xgtxfxSuccess(cardInfo);

	}

	protected void back() {
		onBackPressed();
		// FragmentTransaction fragmentTransaction =
		// fragmentManager.beginTransaction();
		// fragmentTransaction.show(readCardfragment);
		// fragmentTransaction.hide(clzjxxDetileFragment);
		// fragmentTransaction.commit();
	}

	protected void detailJlyc(Message msg) {
		Intent intent = (Intent) msg.obj;
		intent.setClass(this, Exceptioninfo.class);
		startActivity(intent);
	}

	protected void detail2(CardInfo cardInfo, boolean xgtxfx, int arg1) {
		this.cardInfo = cardInfo;
		setEachContentView(R.layout.clzjxx_detail_01);
		clzjxxDetileFragment.setInfo(this , cardInfo , xgtxfx , arg1);

	}
//	protected void detail(Message msg) {
//		cardInfo = (CardInfo) msg.obj;
//		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//		fragmentTransaction.addToBackStack("THIS");
//		fragmentTransaction.show(clzjxxDetileFragment);
//		fragmentTransaction.hide(readCardfragment);
//		fragmentTransaction.commit();
////		clzjxxDetileFragment.setInfo(this , cardInfo);
//		
//	}

	protected void sdbc(Message msg) {
		Intent intent = new Intent(this, XunjianClglSdbc.class);
		intent.putExtra("from", ManagerFlag.PDA_XCXJ_CLYZ);
		intent.putExtra("cardInfo", (CardInfo) msg.obj);
		startActivity(intent);
	}

	protected void updateDealFlag(int what) {
		switch (what) {
		case XunjianRequest.HANDLER_WHAT_DEALREADCARDINFO_FALSE:
			ifDealReadCardInfo = false;
			break;
		case XunjianRequest.HANDLER_WHAT_DEALREADCARDINFO_TRUE:
			ifDealReadCardInfo = true;
			break;
		default:
			break;
		}
	}

	@Override
	protected void onStart() {
		Log.i(TAG, "onStart()");
		super.onStart();
	}

	@Override
	protected void onRestart() {
		Log.i(TAG, "onRestart()");
		super.onRestart();
	}

	@Override
	protected void onResume() {
		Log.i(TAG, "onResume()");
		initRfid();
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		Log.i(TAG, "onDestroy()");
		super.onDestroy();
	}

	@Override
	protected void onNewIntent(Intent intent) {
		Log.i(TAG, "onNewIntent()");
		super.onNewIntent(intent);
	}

	@Override
	protected void onPause() {
		Log.i(TAG, "onPause()");
		ReadService.getInstent().close();
		super.onPause();
	}

	@Override
	protected void onStop() {
		Log.i(TAG, "onStop()");
		super.onStop();
	}

	@Override
	public void onBackPressed() {
		Log.i(TAG, "onBackPressed()");
		super.onBackPressed();
	}
}
