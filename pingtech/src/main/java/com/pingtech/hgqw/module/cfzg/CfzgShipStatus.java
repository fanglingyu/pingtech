package com.pingtech.hgqw.module.cfzg;

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
import com.pingtech.hgqw.entity.GlobalFlags;
import com.pingtech.hgqw.entity.LoginUser;
import com.pingtech.hgqw.interf.OnHttpResult;
import com.pingtech.hgqw.utils.BasicNameValuePair;
import com.pingtech.hgqw.utils.Log;
import com.pingtech.hgqw.utils.SystemSetting;
import com.pingtech.hgqw.web.NetWorkManager;
import com.pingtech.hgqw.widget.HgqwToast;

/** 船舶动态界面的activity类 */
public class CfzgShipStatus extends CfzgSuperActivity implements OnHttpResult {
	private static final String TAG = "ShipStatusActivity";

	/** 船舶绑定 */
	private static final int STARTACTIVITY_FOR_BIND = 1;

	/** 船舶离港 */
	private static final int STARTACTIVITY_FOR_LEAVE = 2;

	/** 人员平衡 */
	private static final int STARTACTIVITY_FOR_PERSONNEL_BALANCE = 3;

	/** 船舶抵港 */
	private static final int STARTACTIVITY_FOR_ARRIVE = 4;

	/** 船舶解除绑定 */
	private static final int STARTACTIVITY_FOR_UNBIND = 5;

	/** 进入刷电子标签 */
	private static final int STARTACTIVITY_FOR_READICCARD = 6;

	/** 进入人员平衡 */
	private static final int STARTACTIVITY_FOR_PERSON_BALANCE = 7;

	private final int gridViewMenuItemId[] = { STARTACTIVITY_FOR_BIND, STARTACTIVITY_FOR_ARRIVE, STARTACTIVITY_FOR_PERSONNEL_BALANCE,
			STARTACTIVITY_FOR_LEAVE, STARTACTIVITY_FOR_UNBIND };

	private final int gridViewMenuItemString[] = { R.string.bindShip, R.string.arrive, R.string.Personnel_balance, R.string.leave,
			R.string.unbindShip };

	private final int gridViewMenuItemImage[] = { R.drawable.bindship, R.drawable.arrive, R.drawable.personbalance, R.drawable.leave,
			R.drawable.unbindship };

	private GridView mGridView;

	private int mType;

	private ProgressDialog progressDialog = null;

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
				intent.putExtra("cardNumber", CfzgShipListActivity.FROM_BINDLIST);
				intent.putExtra("from", CfzgShipListActivity.LIST_TYPE_FROM_SHIPSTATUS);
				intent.setClass(getApplicationContext(), CfzgShipListActivity.class);
				startActivity(intent);
			}
		});
		builder.setNegativeButton(R.string.no, new AlertDialog.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				Intent intent = new Intent();
				intent.putExtra("from", "0501");
				intent.setClass(getApplicationContext(), CfzgShipBind.class);
				startActivityForResult(intent, STARTACTIVITY_FOR_READICCARD);
			}
		});
		builder.create().show();
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.main);

		Log.i(TAG, "onCreate()");
		setMyActiveTitle(R.string.ShipStatus);
		mGridView = (GridView) findViewById(R.id.gridView1);
		mGridView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				// TODO Auto-generated method stub
				mType = gridViewMenuItemId[arg2];
				HashMap<String, Object> _BindShip = SystemSetting.getBindShip(CfzgShipListActivity.LIST_TYPE_FROM_SHIPSTATUS + "");
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
						// 注释掉，让详情页面每次都请求接口。（详情页面判断船员数为空则请求接口）
						// intent.putExtra("cys", (String)
						// _BindShip.get("cys"));
						intent.putExtra("dlcys", (String) _BindShip.get("dlcys"));
						intent.putExtra("dlrys", (String) _BindShip.get("dlrys"));
						String bdzt = (String) _BindShip.get("bdzt");
						intent.putExtra("bdzt", bdzt==null?"":bdzt);
						intent.putExtra("kacbzt", (String) _BindShip.get("kacbzt"));
						intent.putExtra("from", CfzgShipListActivity.LIST_TYPE_FROM_SHIPSTATUS);
						intent.setClass(getApplicationContext(), CfzgShipDetailActivity.class);
						startActivity(intent);
						return;
					} else {
						if (SystemSetting.getBindShipAllSize(-1) > 0) {
							onShowQuestFromBindListDialog();
						} else {
							intent.putExtra("from", "0501");
							intent.setClass(getApplicationContext(), CfzgShipBind.class);
							startActivityForResult(intent, STARTACTIVITY_FOR_READICCARD);
						}
						return;
					}
				case STARTACTIVITY_FOR_LEAVE:
					if (_BindShip == null) {
						HgqwToast.makeText(CfzgShipStatus.this, R.string.no_bindship, HgqwToast.LENGTH_LONG).show();
						return;
					}
					intent.putExtra("title", getString(R.string.leave));
					break;
				case STARTACTIVITY_FOR_PERSONNEL_BALANCE:
					/*if (_BindShip == null) {
						BaseToast.makeText(CfzgShipStatus.this, R.string.no_bindship, BaseToast.LENGTH_LONG).show();
						return;
					}
					intent.putExtra("title", getString(R.string.Personnel_balance));
					intent.putExtra("hc", (String) _BindShip.get("hc"));
					intent.setClass(getApplicationContext(), PersonBalanceActivity.class);
					startActivityForResult(intent, STARTACTIVITY_FOR_PERSON_BALANCE);*/
					return;
				case STARTACTIVITY_FOR_ARRIVE:
					if (_BindShip == null) {
						HgqwToast.makeText(CfzgShipStatus.this, R.string.no_bindship, HgqwToast.LENGTH_LONG).show();
						return;
					}
					intent.putExtra("title", getString(R.string.arrive));
					break;
				case STARTACTIVITY_FOR_UNBIND:
					if (_BindShip == null) {
						HgqwToast.makeText(CfzgShipStatus.this, R.string.no_bindship, HgqwToast.LENGTH_LONG).show();
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
				// intent.putExtra("cys", (String) _BindShip.get("cys"));
				intent.putExtra("dlcys", (String) _BindShip.get("dlcys"));
				intent.putExtra("dlrys", (String) _BindShip.get("dlrys"));
				intent.putExtra("bdzt", (String) _BindShip.get("bdzt"));
				intent.putExtra("kacbzt", (String) _BindShip.get("kacbzt"));
				intent.putExtra("from", CfzgShipListActivity.LIST_TYPE_FROM_SHIPSTATUS);
				intent.setClass(getApplicationContext(), CfzgShipDetailActivity.class);
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
		SimpleAdapter adpter = new SimpleAdapter(this, lst, R.layout.mainmenu_item, new String[] { "itemImage", "itemText" }, new int[] {
				R.id.imageView_ItemImage, R.id.textView_ItemText });

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
			if (SystemSetting.getBindShip(CfzgShipListActivity.LIST_TYPE_FROM_SHIPSTATUS + "") != null) {
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
				params.add(new BasicNameValuePair("voyageNumber", SystemSetting.getVoyageNumber(CfzgShipListActivity.LIST_TYPE_FROM_SHIPSTATUS + "")));
				params.add(new BasicNameValuePair("bindType", "0"));
				//执勤对象类型:船舶0 卡口(区域)1  码头2 泊位3
				params.add(new BasicNameValuePair("zqdxlx", GlobalFlags.ZQDXLX_CB + ""));
				

				progressDialog = new ProgressDialog(CfzgShipStatus.this);
				progressDialog.setTitle(getString(R.string.waiting));
				progressDialog.setMessage(getString(R.string.waiting));
				progressDialog.setCancelable(false);
				progressDialog.setIndeterminate(false);
				progressDialog.show();
				NetWorkManager.request(CfzgShipStatus.this, url, params, 0);
			}
		});
		builder.setNegativeButton(R.string.no, new AlertDialog.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				CfzgShipStatus.this.finish();
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
	 * @see CfzgShipListActivity
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
					intent.putExtra("from", CfzgShipListActivity.LIST_TYPE_FROM_SHIPSTATUS);
					intent.setClass(getApplicationContext(), CfzgShipListActivity.class);
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
			SystemSetting.setBindShip(null, CfzgShipListActivity.LIST_TYPE_FROM_SHIPSTATUS + "");
			finish();
		} else {
			HgqwToast.makeText(CfzgShipStatus.this, R.string.unbindship_failure, HgqwToast.LENGTH_LONG).show();
		}
	}
}
