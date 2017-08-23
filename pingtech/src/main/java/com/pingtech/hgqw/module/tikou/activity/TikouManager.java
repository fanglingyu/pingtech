package com.pingtech.hgqw.module.tikou.activity;

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
import com.pingtech.hgqw.activity.SelectShipActivity;
import com.pingtech.hgqw.activity.ShipDetailActivity;
import com.pingtech.hgqw.activity.ShipListActivity;
import com.pingtech.hgqw.base.BaseApplication;
import com.pingtech.hgqw.entity.Flags;
import com.pingtech.hgqw.entity.GlobalFlags;
import com.pingtech.hgqw.entity.LoginUser;
import com.pingtech.hgqw.interf.OnHttpResult;
import com.pingtech.hgqw.module.bindship.activity.ShipBind;
import com.pingtech.hgqw.module.exception.activity.Exceptionlist;
import com.pingtech.hgqw.module.offline.base.utils.DbUtil;
import com.pingtech.hgqw.module.wpjc.activity.GoodsCheckList;
import com.pingtech.hgqw.utils.BasicNameValuePair;
import com.pingtech.hgqw.utils.DeviceUtils;
import com.pingtech.hgqw.utils.Log;
import com.pingtech.hgqw.utils.SystemSetting;
import com.pingtech.hgqw.web.NetWorkManager;
import com.pingtech.hgqw.widget.HgqwToast;

/** 梯口管理界面的activity类 */
public class TikouManager extends MyActivity implements OnHttpResult {
	private static final String TAG = "TikouManagerActivity";

	/** 船舶绑定 */
	private static final int STARTACTIVITY_FOR_BINDSHIP = 1;

	/** 刷卡登记 */
	private static final int STARTACTIVITY_FOR_PAYCARD = 2;

	/** 异常信息 */
	private static final int STARTACTIVITY_FOR_EXCEPTION_INFO = 3;

	/** 货物检查 */
	private static final int STARTACTIVITY_FOR_GOODS_CHECK = 4;

	/** 解除绑定 */
	private static final int STARTACTIVITY_FOR_UNBINDSHIP = 5;

	/** 进入刷卡界面 */
	private static final int STARTACTIVITY_FOR_READICCARD = 6;
	
	/** 快速验放 */
	private static final int STARTACTIVITY_FOR_QUICKCHECK = 7;

	private final int gridViewMenuItemId[] = { STARTACTIVITY_FOR_BINDSHIP, STARTACTIVITY_FOR_PAYCARD, STARTACTIVITY_FOR_UNBINDSHIP, STARTACTIVITY_FOR_QUICKCHECK};

	private final int gridViewMenuItemString[] = { R.string.bindShip, R.string.paycard, R.string.unbindShip,R.string.quickCheck};

	private final int gridViewMenuItemImage[] = { R.drawable.bindship, R.drawable.paycard, R.drawable.unbindship,R.drawable.quickcheck };

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
				intent.putExtra("from", GlobalFlags.LIST_TYPE_FROM_TIKOUMANAGER);
				intent.setClass(getApplicationContext(), ShipListActivity.class);
				startActivity(intent);
			}
		});
		builder.setNegativeButton(R.string.no, new AlertDialog.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				Intent intent = new Intent();
				// 01卡口、02梯口、03巡查巡检、04查询人员模块、05船舶动态、0501船舶动态>>>船舶绑定、0201梯口管理>>>船舶绑定、0101卡口管理>>>船舶绑定、0301巡查巡检>>>船舶绑定
				intent.putExtra("from", GlobalFlags.BINDSHIP_FROM_TIKOUMANAGER);
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
		setMyActiveTitle(R.string.tikoumanager);

		mGridView = (GridView) findViewById(R.id.gridView1);
		mGridView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				HashMap<String, Object> bindData = SystemSetting.getBindShip(GlobalFlags.LIST_TYPE_FROM_TIKOUMANAGER + "");
				HashMap<String, Object> KaKouBinddata = SystemSetting.getBindShip(GlobalFlags.LIST_TYPE_FROM_KAKOUMANAGER + "");

				mType = gridViewMenuItemId[arg2];
				switch (mType) {
				case STARTACTIVITY_FOR_BINDSHIP:
					if (bindData != null) {
						intent.putExtra("hc", (String) bindData.get("hc"));
						intent.putExtra("kacbqkid", (String) bindData.get("kacbqkid"));
						intent.putExtra("cbzwm", (String) bindData.get("cbzwm"));
						intent.putExtra("jcfl", (String) bindData.get("jcfl"));
						intent.putExtra("cbywm", (String) bindData.get("cbywm"));
						intent.putExtra("gj", (String) bindData.get("gj"));
						intent.putExtra("cbxz", (String) bindData.get("cbxz"));
						intent.putExtra("tkwz", (String) bindData.get("tkwz"));
						intent.putExtra("cdgs", (String) bindData.get("cdgs"));
						intent.putExtra("cys", (String) bindData.get("cys"));
						intent.putExtra("dlcys", (String) bindData.get("dlcys"));
						intent.putExtra("dlrys", (String) bindData.get("dlrys"));
						intent.putExtra("bdzt", (String) bindData.get("bdzt"));
						intent.putExtra("kacbzt", (String) bindData.get("kacbzt"));
						intent.putExtra("from", GlobalFlags.LIST_TYPE_FROM_TIKOUMANAGER);
						intent.setClass(getApplicationContext(), ShipDetailActivity.class);
						startActivity(intent);
						return;
					} else {
						if (!BaseApplication.instent.getWebState()) {
							HgqwToast.toast(getString(R.string.no_web_cannot_bind), HgqwToast.LENGTH_LONG);
							return;
						}
						if (SystemSetting.getBindShipAllSize(GlobalFlags.LIST_TYPE_FROM_TIKOUMANAGER) > 0) {
							onShowQuestFromBindListDialog();
						} else {
							// 01卡口、02梯口、03巡查巡检、04查询人员模块、05船舶动态、0501船舶动态>>>船舶绑定、0201梯口管理>>>船舶绑定、0101卡口管理>>>船舶绑定、0301巡查巡检>>>船舶绑定
							intent.putExtra("from", GlobalFlags.BINDSHIP_FROM_TIKOUMANAGER);
							intent.setClass(getApplicationContext(), ShipBind.class);
							startActivity(intent);
						}
						return;
					}
				case STARTACTIVITY_FOR_PAYCARD:
					if (bindData == null && SystemSetting.shipOfKK == null) {
						HgqwToast.makeText(TikouManager.this, R.string.no_bindship, HgqwToast.LENGTH_LONG).show();
						return;
					}
					intent.putExtra("title", getString(R.string.tikoumanager) + ">" + getString(R.string.paycard));
					intent.putExtra("from", "02");
					intent.setClass(getApplicationContext(), TiKouReadCard.class);
					startActivityForResult(intent, STARTACTIVITY_FOR_READICCARD);
					return;
				case STARTACTIVITY_FOR_EXCEPTION_INFO:
					intent.putExtra("from", "02");
					intent.setClass(getApplicationContext(), Exceptionlist.class);
					startActivity(intent);
					return;
				case STARTACTIVITY_FOR_GOODS_CHECK:
					if (bindData == null) {
						HgqwToast.makeText(TikouManager.this, R.string.no_bindship, HgqwToast.LENGTH_LONG).show();
						return;
					}
					intent.putExtra("hc", (String) bindData.get("hc"));
					intent.putExtra("kacbqkid", (String) bindData.get("kacbqkid"));
					intent.setClass(getApplicationContext(), GoodsCheckList.class);
					startActivity(intent);
					return;
				case STARTACTIVITY_FOR_UNBINDSHIP:
					if (bindData == null) {
						HgqwToast.makeText(TikouManager.this, R.string.no_bindship, HgqwToast.LENGTH_LONG).show();
						return;
					}
					intent.putExtra("hc", (String) bindData.get("hc"));
					intent.putExtra("kacbqkid", (String) bindData.get("kacbqkid"));
					intent.putExtra("cbzwm", (String) bindData.get("cbzwm"));
					intent.putExtra("jcfl", (String) bindData.get("jcfl"));
					intent.putExtra("cbywm", (String) bindData.get("cbywm"));
					intent.putExtra("gj", (String) bindData.get("gj"));
					intent.putExtra("cbxz", (String) bindData.get("cbxz"));
					intent.putExtra("tkwz", (String) bindData.get("tkwz"));
					intent.putExtra("cdgs", (String) bindData.get("cdgs"));
					intent.putExtra("cys", (String) bindData.get("cys"));
					intent.putExtra("dlcys", (String) bindData.get("dlcys"));
					intent.putExtra("dlrys", (String) bindData.get("dlrys"));
					intent.putExtra("bdzt", (String) bindData.get("bdzt"));
					intent.putExtra("kacbzt", (String) bindData.get("kacbzt"));
					intent.putExtra("from", GlobalFlags.LIST_TYPE_FROM_TIKOUMANAGER);
					intent.putExtra("title", getString(R.string.unbindShip));
					intent.setClass(getApplicationContext(), ShipDetailActivity.class);
					startActivity(intent);
					return;
				case STARTACTIVITY_FOR_QUICKCHECK:
					if(bindData != null){
						
						intent.putExtra("hc", (String) bindData.get("hc"));
						intent.putExtra("kacbqkid", (String) bindData.get("kacbqkid"));
						intent.putExtra("cbzwm", (String) bindData.get("cbzwm"));
						intent.putExtra("jcfl", (String) bindData.get("jcfl"));
						intent.putExtra("cbywm", (String) bindData.get("cbywm"));
						intent.putExtra("gj", (String) bindData.get("gj"));
						intent.putExtra("cbxz", (String) bindData.get("cbxz"));
						intent.putExtra("tkwz", (String) bindData.get("tkwz"));
						intent.putExtra("cdgs", (String) bindData.get("cdgs"));
						intent.putExtra("cys", (String) bindData.get("cys"));
						intent.putExtra("dlcys", (String) bindData.get("dlcys"));
						intent.putExtra("dlrys", (String) bindData.get("dlrys"));
						intent.putExtra("bdzt", (String) bindData.get("bdzt"));
						intent.putExtra("kacbzt", (String) bindData.get("kacbzt"));
						intent.putExtra("from", GlobalFlags.LIST_TYPE_FROM_TIKOUMANAGER);
						intent.putExtra("fromtkkshc", true);
						intent.setClass(getApplicationContext(), ShipDetailActivity.class);
						startActivity(intent);
						return;
						
					}else{
						intent.putExtra("frombindship", true);
						intent.putExtra("bindtype", 1);
						intent.putExtra("fromtkkshc", true);
						intent.setClass(getApplicationContext(), SelectShipActivity.class);
						startActivity(intent);
						return;
					}
				default:
					break;
				}
			}

		});
		onUpdateGridViewmenu();
	}

	private void onUpdateGridViewmenu() {
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

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		Log.i(TAG, "EditText.OnKeyListener onKey(),keycode=" + keyCode + ", action=" + event.getAction());
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			if (SystemSetting.getBindShip(GlobalFlags.LIST_TYPE_FROM_TIKOUMANAGER + "") != null) {
				onShowUnBindQuestDialog();
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	/** 退出该模块时，如果存在为解除绑定，提示是否解除绑定 */
	private void onShowUnBindQuestDialog() {
		// 离线不提示解除绑定
		if (!BaseApplication.instent.getWebState()) {
			return;
		}

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(R.string.unbind_quit);
		builder.setTitle(R.string.info);
		builder.setPositiveButton(R.string.yes, new AlertDialog.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				// 执行船舶解除绑定
				String url = "buildRelation";
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("userID", LoginUser.getCurrentLoginUser().getUserID()));
				params.add(new BasicNameValuePair("PDACode", SystemSetting.getPDACode()));
				params.add(new BasicNameValuePair("bindState", "0"));
				params.add(new BasicNameValuePair("voyageNumber", SystemSetting.getVoyageNumber(GlobalFlags.LIST_TYPE_FROM_TIKOUMANAGER + "")));
				params.add(new BasicNameValuePair("bindType", "1"));
				//执勤对象类型:船舶0 卡口(区域)1  码头2 泊位3
				params.add(new BasicNameValuePair("zqdxlx", GlobalFlags.ZQDXLX_CB + ""));
				
				if (progressDialog != null) {
					return;
				}
				progressDialog = new ProgressDialog(TikouManager.this);
				progressDialog.setTitle(getString(R.string.waiting));
				progressDialog.setMessage(getString(R.string.waiting));
				progressDialog.setCancelable(false);
				progressDialog.setIndeterminate(false);
				progressDialog.show();
				NetWorkManager.request(TikouManager.this, url, params, 0);
			}
		});
		builder.setNegativeButton(R.string.no, new AlertDialog.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				TikouManager.this.finish();
			}
		});
		builder.create().show();
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
				case STARTACTIVITY_FOR_BINDSHIP:
					intent.putExtra("title", getString(R.string.bindShip));
					if (data.getStringExtra("cardNumber") != null) {
						intent.putExtra("cardNumber", data.getStringExtra("cardNumber"));
						intent.putExtra("from", GlobalFlags.LIST_TYPE_FROM_TIKOUMANAGER);
						intent.setClass(getApplicationContext(), ShipListActivity.class);
						startActivity(intent);
					}
					break;
				case STARTACTIVITY_FOR_PAYCARD:
					intent.putExtra("title", getString(R.string.paycard));
					break;
				}
			}
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
			// 解绑成功，删除历史数据
			deleteHistory();
			SystemSetting.setBindShip(null, GlobalFlags.LIST_TYPE_FROM_TIKOUMANAGER + "");
			finish();
		} else {
			HgqwToast.makeText(TikouManager.this, R.string.unbindship_failure, HgqwToast.LENGTH_LONG).show();
		}
	}

	private void deleteHistory() {
		HashMap<String, Object> kaKouBinddata = SystemSetting.getBindShip(GlobalFlags.LIST_TYPE_FROM_KAKOUMANAGER + "");
		HashMap<String, Object> bindDataForTiKou = SystemSetting.getBindShip(GlobalFlags.LIST_TYPE_FROM_TIKOUMANAGER + "");
		ArrayList<HashMap<String, Object>> mapsForKk = SystemSetting.getShipOfKK();
		String hc = "";
		String kacbqkid = "";
		if(bindDataForTiKou!=null){
			hc = (String) bindDataForTiKou.get("hc");
			kacbqkid = (String) bindDataForTiKou.get("kacbqkid");
		}
		boolean isInKk = false;
		if (kaKouBinddata != null && mapsForKk != null && mapsForKk.size() > 0) {// 卡口没有解绑,并且卡口内有船舶
			for (HashMap<String, Object> hashMap : mapsForKk) {
				String hcTemp = (String) hashMap.get("hc");
				if (hcTemp != null && hcTemp.equals(hc)) {
					isInKk = true;
					break;
				}
			}
		}

		if (!isInKk) {
			DbUtil.deleteKacbqkByHc(hc);
			DbUtil.deleteHgzjxxByHc(hc);
			DbUtil.deleteCyxxByCbid(kacbqkid);
		}
		// 删除梯口通行记录
//		DbUtil.deleteTxjlForTiKou();
	}

}
