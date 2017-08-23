package com.pingtech.hgqw.module.cbdt.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.SimpleAdapter;

import com.pingtech.R;
import com.pingtech.hgqw.activity.MyActivity;
import com.pingtech.hgqw.activity.PersonBalanceActivity;
import com.pingtech.hgqw.activity.ShipListActivity;
import com.pingtech.hgqw.base.BaseApplication;
import com.pingtech.hgqw.entity.Flags;
import com.pingtech.hgqw.entity.GlobalFlags;
import com.pingtech.hgqw.entity.LoginUser;
import com.pingtech.hgqw.entity.ManagerFlag;
import com.pingtech.hgqw.interf.OnHttpResult;
import com.pingtech.hgqw.module.bindship.activity.ShipBind;
import com.pingtech.hgqw.utils.BasicNameValuePair;
import com.pingtech.hgqw.utils.DeviceUtils;
import com.pingtech.hgqw.utils.Log;
import com.pingtech.hgqw.utils.SystemSetting;
import com.pingtech.hgqw.web.NetWorkManager;
import com.pingtech.hgqw.widget.HgqwToast;

/** 船舶动态界面的activity类 */
public class ShipStatus extends MyActivity implements OnHttpResult {
	private static final String TAG = "ShipStatus";

	/** 船舶绑定 */
	private static final int STARTACTIVITY_FOR_BIND = 1;

	/** 船舶离港 */
	private static final int STARTACTIVITY_FOR_LEAVE = 2;

	/** 船舶移泊 */
	private static final int STARTACTIVITY_FOR_SHIP_OUT = 20;

	/** 人员平衡 */
	private static final int STARTACTIVITY_FOR_PERSONNEL_BALANCE = 3;

	/** 船舶抵港 */
	private static final int STARTACTIVITY_FOR_ARRIVE = 4;

	/** 船舶靠泊 */
	private static final int STARTACTIVITY_FOR_SHIP_IN = 40;

	/** 船舶解除绑定 */
	private static final int STARTACTIVITY_FOR_UNBIND = 5;

	/** 进入刷电子标签 */
	private static final int STARTACTIVITY_FOR_READICCARD = 6;

	/** 进入人员平衡 */
	private static final int STARTACTIVITY_FOR_PERSON_BALANCE = 7;

	private final int gridViewMenuItemId[] = { STARTACTIVITY_FOR_BIND, STARTACTIVITY_FOR_ARRIVE, STARTACTIVITY_FOR_SHIP_IN,
			STARTACTIVITY_FOR_PERSONNEL_BALANCE, STARTACTIVITY_FOR_LEAVE, STARTACTIVITY_FOR_SHIP_OUT, STARTACTIVITY_FOR_UNBIND };

	private final int gridViewMenuItemString[] = { R.string.bindShip, R.string.arrive, R.string.ship_in, R.string.Personnel_balance, R.string.leave,
			R.string.ship_out, R.string.unbindShip };

	private final int gridViewMenuItemImage[] = { R.drawable.bindship, R.drawable.arrive, R.drawable.ship_in, R.drawable.personbalance,
			R.drawable.leave, R.drawable.ship_out, R.drawable.unbindship };

	private GridView mGridView;

	private int mType;

	private ProgressDialog progressDialog = null;

	private SimpleAdapter adpter;

	/** 进入船舶绑定时，如果存在快速绑定条件，提示是否选择快速绑定 */
	private void onShowQuestFromBindListDialog() {
		// TODO Auto-generated method stub
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(R.string.query_from_bindlist);
		builder.setTitle(R.string.info);
		builder.setPositiveButton(R.string.yes, new AlertDialog.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				Intent intent = new Intent();
				intent.putExtra("title", getString(R.string.bindShip));
				intent.putExtra("cardNumber", ShipListActivity.FROM_BINDLIST);
				intent.putExtra("from", GlobalFlags.LIST_TYPE_FROM_SHIPSTATUS);
				intent.setClass(getApplicationContext(), ShipListActivity.class);
				startActivity(intent);
			}
		});
		builder.setNegativeButton(R.string.no, new AlertDialog.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				Intent intent = new Intent();
				intent.putExtra("from", GlobalFlags.BINDSHIP_FROM_SHIPSTATUS);
				intent.setClass(getApplicationContext(), ShipBind.class);
				startActivity(intent);
			}
		});
		builder.create().show();
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		int viewID = DeviceUtils.switchVersion(R.layout.main, R.layout.main_sentinel);
		super.onCreate(savedInstanceState, viewID);

		Log.i(TAG, "onCreate()");
		setMyActiveTitle(R.string.ShipStatus);
		mGridView = (GridView) findViewById(R.id.gridView1);
		mGridView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				// TODO Auto-generated method stub
				mType = gridViewMenuItemId[arg2];
				HashMap<String, Object> _BindShip = SystemSetting.getBindShip(GlobalFlags.LIST_TYPE_FROM_SHIPSTATUS + "");
				Intent intent = new Intent();
				switch (gridViewMenuItemId[arg2]) {
				case STARTACTIVITY_FOR_BIND:
					if (_BindShip != null) {
						intent.putExtra("hc", (String) _BindShip.get("hc"));
						intent.putExtra("cbzwm", (String) _BindShip.get("cbzwm"));
						intent.putExtra("jcfl", (String) _BindShip.get("jcfl"));
						intent.putExtra("cbywm", (String) _BindShip.get("cbywm"));
						intent.putExtra("gj", (String) _BindShip.get("gj"));
						intent.putExtra("cbxz", (String) _BindShip.get("cbxz"));
						intent.putExtra("tkwz", (String) _BindShip.get("tkwz"));
						intent.putExtra("cdgs", (String) _BindShip.get("cdgs"));
						intent.putExtra("cys", (String) _BindShip.get("cys"));
						intent.putExtra("dlcys", (String) _BindShip.get("dlcys"));
						intent.putExtra("dlrys", (String) _BindShip.get("dlrys"));
						intent.putExtra("bdzt", (String) _BindShip.get("bdzt"));
						intent.putExtra("kacbzt", (String) _BindShip.get("kacbzt"));

						intent.putExtra("dqjczt", (String) _BindShip.get("dqjczt") == null ? "" : (String) _BindShip.get("dqjczt"));
						intent.putExtra("cys", (String) _BindShip.get("cys") == null ? "" : (String) _BindShip.get("cys"));
						intent.putExtra("cdgs", (String) _BindShip.get("cdgs") == null ? "" : (String) _BindShip.get("cdgs"));
						intent.putExtra("jcfl", (String) _BindShip.get("jcfl") == null ? "" : (String) _BindShip.get("jcfl"));

						intent.putExtra("from", GlobalFlags.LIST_TYPE_FROM_SHIPSTATUS);
						intent.setClass(getApplicationContext(), CbdtShipDetail.class);
						startActivity(intent);
						return;
					} else {
						if (!BaseApplication.instent.getWebState()) {
							HgqwToast.toast(getString(R.string.no_web_cannot_bind), HgqwToast.LENGTH_LONG);
							return;
						}

						if (SystemSetting.getBindShipAllSize(GlobalFlags.LIST_TYPE_FROM_SHIPSTATUS) > 0) {
							onShowQuestFromBindListDialog();
						} else {
							intent.putExtra("from", GlobalFlags.BINDSHIP_FROM_SHIPSTATUS);
							intent.setClass(getApplicationContext(), ShipBind.class);
							startActivity(intent);
						}
						return;
					}
				case STARTACTIVITY_FOR_LEAVE:
					if (_BindShip == null) {
						HgqwToast.toast(R.string.no_bindship, HgqwToast.LENGTH_LONG);
						return;
					}
					if ((!Flags.peClickFlag) && BaseApplication.instent.getWebState()) {
						HgqwToast.toast(R.string.no_execut_personbalance, HgqwToast.LENGTH_LONG);
						return;
					}
					intent.putExtra("title", getString(R.string.leave));
					intent.putExtra("czgn", ManagerFlag.PDA_CBDT_CBLG);
					break;
				case STARTACTIVITY_FOR_PERSONNEL_BALANCE:
					if (_BindShip == null) {
						HgqwToast.toast(R.string.no_bindship, HgqwToast.LENGTH_LONG);
						return;
					}
					intent.putExtra("title", getString(R.string.Personnel_balance));
					intent.putExtra("hc", (String) _BindShip.get("hc"));
					intent.setClass(getApplicationContext(), PersonBalanceActivity.class);
					startActivityForResult(intent, STARTACTIVITY_FOR_PERSON_BALANCE);
					Flags.peClickFlag = true;
					return;
				case STARTACTIVITY_FOR_ARRIVE:
					if (_BindShip == null) {
						HgqwToast.toast(R.string.no_bindship, HgqwToast.LENGTH_LONG);
						return;
					}
					intent.putExtra("title", getString(R.string.arrive));
					intent.putExtra("czgn", ManagerFlag.PDA_CBDT_CBDG);
					break;
				case STARTACTIVITY_FOR_SHIP_IN:
					if (_BindShip == null) {
						HgqwToast.toast(R.string.no_bindship, HgqwToast.LENGTH_LONG);
						return;
					}
					intent.putExtra("title", getString(R.string.ship_in));
					intent.putExtra("czgn", ManagerFlag.PDA_CBDT_CBKB);
					break;
				case STARTACTIVITY_FOR_SHIP_OUT:
					if (_BindShip == null) {
						HgqwToast.toast(R.string.no_bindship, HgqwToast.LENGTH_LONG);
						return;
					}
					intent.putExtra("title", getString(R.string.ship_out));
					intent.putExtra("czgn", ManagerFlag.PDA_CBDT_CBYB);
					break;
				case STARTACTIVITY_FOR_UNBIND:
					if (_BindShip == null) {
						HgqwToast.toast(R.string.no_bindship, HgqwToast.LENGTH_LONG);
						return;
					}
					intent.putExtra("title", getString(R.string.unbindShip));
					break;
				}
				intent.putExtra("hc", (String) _BindShip.get("hc"));
				intent.putExtra("cbzwm", (String) _BindShip.get("cbzwm"));
				intent.putExtra("jcfl", (String) _BindShip.get("jcfl"));
				intent.putExtra("cbywm", (String) _BindShip.get("cbywm"));
				intent.putExtra("gj", (String) _BindShip.get("gj"));
				intent.putExtra("cbxz", (String) _BindShip.get("cbxz"));
				intent.putExtra("tkwz", (String) _BindShip.get("tkwz"));
				intent.putExtra("cdgs", (String) _BindShip.get("cdgs"));
				intent.putExtra("cys", (String) _BindShip.get("cys"));
				intent.putExtra("dlcys", (String) _BindShip.get("dlcys"));
				intent.putExtra("dlrys", (String) _BindShip.get("dlrys"));
				intent.putExtra("bdzt", (String) _BindShip.get("bdzt"));
				intent.putExtra("dqjczt", (String) _BindShip.get("dqjczt"));
				intent.putExtra("kacbzt", (String) _BindShip.get("kacbzt"));
				intent.putExtra("from", GlobalFlags.LIST_TYPE_FROM_SHIPSTATUS);
				intent.setClass(getApplicationContext(), CbdtShipDetail.class);
				startActivity(intent);
			}

		});
		onUpdateGridViewmenu();
	}

	public void onUpdateGridViewmenu() {
		ArrayList<HashMap<String, Object>> lst = new ArrayList<HashMap<String, Object>>();
		for (int i = 0; i < gridViewMenuItemString.length; i++) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("itemImage", gridViewMenuItemImage[i]);
			map.put("itemText", this.getResources().getString(gridViewMenuItemString[i]));
			lst.add(map);
		}
		switch (Flags.PDA_VERSION) {
		case Flags.PDA_VERSION_DEFAULT:
			// 全版本
			adpter = new SimpleAdapter(this, lst, R.layout.mainmenu_item, new String[] { "itemImage", "itemText" }, new int[] {
					R.id.imageView_ItemImage, R.id.textView_ItemText });

			break;
		case Flags.PDA_VERSION_SENTINEL:
			adpter = new SimpleAdapter(this, lst, R.layout.main_sentinel_menu_item, new String[] { "itemImage", "itemText" }, new int[] {
					R.id.imageView_ItemImage, R.id.textView_ItemText });
			break;
		default:
			// 全版本
			adpter = new SimpleAdapter(this, lst, R.layout.mainmenu_item, new String[] { "itemImage", "itemText" }, new int[] {
					R.id.imageView_ItemImage, R.id.textView_ItemText });
			break;
		}

		mGridView.setAdapter(adpter);
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
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		Log.i(TAG, "EditText.OnKeyListener onKey(),keycode=" + keyCode + ", action=" + event.getAction());
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			if (SystemSetting.getBindShip(GlobalFlags.LIST_TYPE_FROM_SHIPSTATUS + "") != null && BaseApplication.instent.getWebState()) {// 离线状态不提示
				onShowUnBindQuestDialog();
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	/** 退出该模块时，如果存在为解除绑定，提示是否解除绑定 */
	private void onShowUnBindQuestDialog() {
		// TODO Auto-generated method stub
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(R.string.unbind_quit);
		builder.setTitle(R.string.info);
		builder.setPositiveButton(R.string.yes, new AlertDialog.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				// 执行船舶解除绑定
				String url = "buildRelation";
				if (progressDialog != null) {
					return;
				}
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("userID", LoginUser.getCurrentLoginUser().getUserID()));
				params.add(new BasicNameValuePair("PDACode", SystemSetting.getPDACode()));
				params.add(new BasicNameValuePair("bindState", "0"));
				params.add(new BasicNameValuePair("voyageNumber", SystemSetting.getVoyageNumber(GlobalFlags.LIST_TYPE_FROM_SHIPSTATUS + "")));
				params.add(new BasicNameValuePair("bindType", "0"));
				//执勤对象类型:船舶0 卡口(区域)1  码头2 泊位3
				params.add(new BasicNameValuePair("zqdxlx", GlobalFlags.ZQDXLX_CB + ""));
				
				progressDialog = new ProgressDialog(ShipStatus.this);
				progressDialog.setTitle(getString(R.string.waiting));
				progressDialog.setMessage(getString(R.string.waiting));
				progressDialog.setCancelable(false);
				progressDialog.setIndeterminate(false);
				progressDialog.show();
				NetWorkManager.request(ShipStatus.this, url, params, 0);
			}
		});
		builder.setNegativeButton(R.string.no, new AlertDialog.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				ShipStatus.this.finish();
			}
		});
		builder.create().show();
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		int keyCode = event.getKeyCode();
		Log.i(TAG, "dispatchKeyEvent,keycode=" + keyCode);
		if (keyCode == KeyEvent.KEYCODE_ENTER) {
			return true;
		}
		if ((keyCode >= KeyEvent.KEYCODE_0 && keyCode <= KeyEvent.KEYCODE_9) || (keyCode == KeyEvent.KEYCODE_ENTER)) {
			return true;
		}
		return super.dispatchKeyEvent(event);
	}

	/**
	 * 刷电子标签完毕后，立即进入根据电子标签号查询船舶列表界面
	 * 
	 * @see ShipListActivity
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case STARTACTIVITY_FOR_READICCARD:
			if (resultCode == RESULT_OK) {
				Intent intent = new Intent();
				switch (mType) {
				case STARTACTIVITY_FOR_BIND:
					intent.putExtra("title", getString(R.string.bindShip));
					break;
				}
				if (data.getStringExtra("cardNumber") != null) {
					intent.putExtra("cardNumber", data.getStringExtra("cardNumber"));
					intent.putExtra("from", GlobalFlags.LIST_TYPE_FROM_SHIPSTATUS);
					intent.setClass(getApplicationContext(), ShipListActivity.class);
					startActivity(intent);
				}
			}
			break;
		}
	}

	/** 处理解除绑定结果 */
	@Override
	public void onHttpResult(String str, int httpRequestType) {
		// TODO Auto-generated method stub
		Log.i(TAG, "onHttpResult() str:" + (str != null));
		if (progressDialog != null) {
			progressDialog.dismiss();
			progressDialog = null;
		}
		if (str != null && ("1".equals(str) || "2".equals(str))) {
			SystemSetting.setBindShip(null, GlobalFlags.LIST_TYPE_FROM_SHIPSTATUS + "");
			finish();
		} else {
			HgqwToast.makeText(ShipStatus.this, R.string.unbindship_failure, HgqwToast.LENGTH_LONG).show();
		}
	}
}
