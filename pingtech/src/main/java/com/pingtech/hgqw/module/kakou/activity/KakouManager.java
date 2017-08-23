package com.pingtech.hgqw.module.kakou.activity;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.xmlpull.v1.XmlPullParserException;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Environment;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.SimpleAdapter;

import com.pingtech.R;
import com.pingtech.hgqw.activity.MyActivity;
import com.pingtech.hgqw.activity.PersonBalanceActivity;
import com.pingtech.hgqw.activity.SelectShipActivity;
import com.pingtech.hgqw.activity.ShipDetailActivity;
import com.pingtech.hgqw.activity.ShipListActivity;
import com.pingtech.hgqw.entity.Flags;
import com.pingtech.hgqw.entity.GlobalFlags;
import com.pingtech.hgqw.entity.LoginUser;
import com.pingtech.hgqw.interf.OnHttpResult;
import com.pingtech.hgqw.module.bindship.activity.ShipBind;
import com.pingtech.hgqw.module.exception.activity.Exceptionlist;
import com.pingtech.hgqw.module.offline.base.utils.DbUtil;
import com.pingtech.hgqw.pullxml.PullXmlGetShipByKK;
import com.pingtech.hgqw.utils.BasicNameValuePair;
import com.pingtech.hgqw.utils.DeviceUtils;
import com.pingtech.hgqw.utils.Log;
import com.pingtech.hgqw.utils.StringUtils;
import com.pingtech.hgqw.utils.SystemSetting;
import com.pingtech.hgqw.web.NetWorkManager;
import com.pingtech.hgqw.widget.HgqwToast;

/**
 * 卡口管理界面的activity类
 * */
public class KakouManager extends MyActivity implements OnHttpResult {
	private static final String TAG = "KakouManagerActivity";

	/** 卡口绑定 */
	private static final int STARTACTIVITY_FOR_KAKOU_BIND = 1;

	/** 卡口-》刷卡登记 */
	private static final int STARTACTIVITY_FOR_KAKOU_PAYCARD = 2;

	/** 异常信息 */
	private static final int STARTACTIVITY_FOR_KAKOU_EXCEPTION = 3;

	/** 人员平衡 */
	private static final int STARTACTIVITY_FOR_PERSONNEL_BALANCE = 6;

	/** 解除绑定 */
	private static final int STARTACTIVITY_FOR_KAKOU_UNBIND = 4;

	/** 车辆检查 */
	private static final int STARTACTIVITY_FOR_KAKOU_CLJC = 7;
	
	/** 快速验放 */
	private static final int STARTACTIVITY_FOR_KKQUICKCHECK = 8;

	/** 绑定卡后时，启动刷电子标签 */
	private static final int STARTACTIVITY_FOR_READICCARD = 5;

	/**
	 * 获得卡口绑定地点船舶
	 */
	private static final int GET_SHIP_BY_KK = 6;

	private static final int BUILDKKRELATION = 7;

	private final int gridViewMenuItemId[] = { STARTACTIVITY_FOR_KAKOU_BIND, STARTACTIVITY_FOR_KAKOU_PAYCARD, STARTACTIVITY_FOR_KAKOU_CLJC,
			STARTACTIVITY_FOR_PERSONNEL_BALANCE, STARTACTIVITY_FOR_KAKOU_UNBIND,STARTACTIVITY_FOR_KKQUICKCHECK };

	private final int gridViewMenuItemString[] = { R.string.kakou_band, R.string.paycard, R.string.clyf, R.string.Personnel_balance,
			R.string.kakou_unband,R.string.quickCheck};

	private final int gridViewMenuItemImage[] = { R.drawable.bindkakou, R.drawable.paycard, R.drawable.cljc, R.drawable.personbalance,
			R.drawable.unbindkakou ,R.drawable.quickcheck};

	private GridView mGridView;

	private int mType;

	private ProgressDialog progressDialog = null;

	private SimpleAdapter adpter;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		int viewID = DeviceUtils.switchVersion(R.layout.main, R.layout.main_sentinel);
		super.onCreate(savedInstanceState, viewID);
		Log.i(TAG, "onCreate()");
		setMyActiveTitle(R.string.kakoumanager);
		mGridView = (GridView) findViewById(R.id.gridView1);
		mGridView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				HashMap<String, Object> bindData = SystemSetting.getBindShip(GlobalFlags.LIST_TYPE_FROM_KAKOUMANAGER + "");
				mType = gridViewMenuItemId[arg2];
				switch (mType) {
				case STARTACTIVITY_FOR_KAKOU_BIND:
					if (bindData != null) {
						intent.putExtra("id", (String) bindData.get("id"));
						intent.putExtra("kkmc", (String) bindData.get("kkmc"));
						intent.putExtra("kkfw", (String) bindData.get("kkfw"));
						intent.putExtra("kkxx", (String) bindData.get("kkxx"));
						intent.putExtra("bdzt", (String) bindData.get("bdzt"));
						intent.putExtra("from", GlobalFlags.LIST_TYPE_FROM_KAKOUMANAGER);
						intent.setClass(getApplicationContext(), ShipDetailActivity.class);
						startActivity(intent);
						return;
					} else {
						// 01卡口、02梯口、03巡查巡检、04查询人员模块、05船舶动态、0501船舶动态>>>船舶绑定、0201梯口管理>>>船舶绑定、0101卡口管理>>>船舶绑定、0301巡查巡检>>>船舶绑定
						intent.putExtra("from", GlobalFlags.BINDSHIP_FROM_KAKOUMANAGER);
						intent.setClass(getApplicationContext(), ShipBind.class);
						startActivity(intent);
						return;
					}
				case STARTACTIVITY_FOR_KAKOU_PAYCARD:
					if (bindData == null) {
						HgqwToast.makeText(KakouManager.this, R.string.no_bindkakou, HgqwToast.LENGTH_LONG).show();
						return;
					}
					intent.putExtra("hc", (String) bindData.get("id"));
					intent.putExtra("kkmc", (String) bindData.get("kkmc"));
					intent.putExtra("title", getString(R.string.kakoumanager) + ">" + getString(R.string.paycard));
					intent.putExtra("cardtype", KaKouReadCard.READCARD_TYPE_ID_CARD);
					intent.putExtra("from", "01");
					intent.setClass(getApplicationContext(), KaKouReadCard.class);
					startActivityForResult(intent, STARTACTIVITY_FOR_READICCARD);
					break;
				case STARTACTIVITY_FOR_KAKOU_EXCEPTION:
					intent.putExtra("from", "01");
					intent.setClass(getApplicationContext(), Exceptionlist.class);
					startActivity(intent);
					break;
				case STARTACTIVITY_FOR_KAKOU_UNBIND:
					if (bindData == null) {
						HgqwToast.makeText(KakouManager.this, R.string.no_bindkakou, HgqwToast.LENGTH_LONG).show();
						return;
					}
					intent.putExtra("id", (String) bindData.get("id"));
					intent.putExtra("kkmc", (String) bindData.get("kkmc"));
					intent.putExtra("kkfw", (String) bindData.get("kkfw"));
					intent.putExtra("kkxx", (String) bindData.get("kkxx"));
					intent.putExtra("bdzt", (String) bindData.get("bdzt"));
					intent.putExtra("from", GlobalFlags.LIST_TYPE_FROM_KAKOUMANAGER);
					intent.putExtra("title", getString(R.string.kakou_unband));
					intent.setClass(getApplicationContext(), ShipDetailActivity.class);
					startActivity(intent);
					return;
				case STARTACTIVITY_FOR_PERSONNEL_BALANCE:
					if (bindData == null) {
						HgqwToast.toast(R.string.no_bindkakou, HgqwToast.LENGTH_LONG);
						return;
					}
					intent.putExtra("title", getString(R.string.Personnel_balance));
					intent.putExtra("kkid", (String) bindData.get("id"));
					intent.setClass(getApplicationContext(), PersonBalanceActivity.class);
					startActivityForResult(intent, STARTACTIVITY_FOR_PERSONNEL_BALANCE);
					Flags.peClickFlag = true;
					return;
				case STARTACTIVITY_FOR_KAKOU_CLJC:
					cljc();
					return;
				case STARTACTIVITY_FOR_KKQUICKCHECK:
					
					if (bindData != null) {
						intent.putExtra("id", (String) bindData.get("id"));
						intent.putExtra("kkmc", (String) bindData.get("kkmc"));
						intent.putExtra("kkfw", (String) bindData.get("kkfw"));
						intent.putExtra("kkxx", (String) bindData.get("kkxx"));
						intent.putExtra("bdzt", (String) bindData.get("bdzt"));
						intent.putExtra("from", GlobalFlags.LIST_TYPE_FROM_KAKOUMANAGER);
						intent.putExtra("fromkkkshc", true);
						intent.setClass(getApplicationContext(), ShipDetailActivity.class);
						startActivity(intent);
						return;
					} else {
						intent.putExtra("frombindship", true);
						intent.putExtra("bindtype", 3);
						intent.putExtra("fromkkkshc", true);
						intent.setClass(getApplicationContext(), SelectShipActivity.class);
						startActivity(intent);
						return;
					}
					
				default:
					break;
				}
			}

		});
		onUpdateGridViewMenu();
	}

	/**
	 * 车辆检查
	 */
	protected void cljc() {
		if (!hasBind()) {
			return;
		}
		Intent intent = new Intent();
		intent.setClass(getApplicationContext(), KakouCljc.class);
		intent.putExtra("from", GlobalFlags.LIST_TYPE_FROM_KAKOUMANAGER);
		startActivity(intent);
	}

	private boolean hasBind() {
		HashMap<String, Object> bindData = SystemSetting.getBindShip(GlobalFlags.LIST_TYPE_FROM_KAKOUMANAGER + "");
		if (bindData == null) {
			HgqwToast.toast(R.string.no_bindkakou, HgqwToast.LENGTH_LONG);
			return false;
		}
		return true;
	}

	@Override
	protected void onResume() {
		HashMap<String, Object> bindData = SystemSetting.getBindShip(GlobalFlags.LIST_TYPE_FROM_KAKOUMANAGER + "");
		if (bindData != null) {
			String url = "getShipByKK";
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("kkID", SystemSetting.getVoyageNumber(GlobalFlags.LIST_TYPE_FROM_KAKOUMANAGER + "")));
			NetWorkManager.request(KakouManager.this, url, params, GET_SHIP_BY_KK);
		} else {
			SystemSetting.setShipOfKK(null);
		}
		super.onResume();
	}

	private void onUpdateGridViewMenu() {
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
			if (SystemSetting.getBindShip(GlobalFlags.LIST_TYPE_FROM_KAKOUMANAGER + "") != null) {
				onShowUnBindQuestDialog();
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	/** 退出卡口管理模块时，如果当前绑定卡口，询问是否解除绑定 */
	private void onShowUnBindQuestDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(R.string.unbindkakou_quit);
		builder.setTitle(R.string.info);
		builder.setPositiveButton(R.string.yes, new AlertDialog.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				if (progressDialog != null) {
					return;
				}
				String url = "buildKkRelation";
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("userID", LoginUser.getCurrentLoginUser().getUserID()));
				params.add(new BasicNameValuePair("PDACode", SystemSetting.getPDACode()));
				params.add(new BasicNameValuePair("bindState", "0"));
				params.add(new BasicNameValuePair("kkID", SystemSetting.getVoyageNumber(GlobalFlags.LIST_TYPE_FROM_KAKOUMANAGER + "")));
				params.add(new BasicNameValuePair("bindType", "3"));
				// 执勤对象类型:船舶0 卡口(区域)1 码头2 泊位3
				params.add(new BasicNameValuePair("zqdxlx", GlobalFlags.ZQDXLX_KK + ""));

				progressDialog = new ProgressDialog(KakouManager.this);
				progressDialog.setTitle(getString(R.string.waiting));
				progressDialog.setMessage(getString(R.string.waiting));
				progressDialog.setCancelable(false);
				progressDialog.setIndeterminate(false);
				progressDialog.show();
				NetWorkManager.request(KakouManager.this, url, params, BUILDKKRELATION);
			}
		});
		builder.setNegativeButton(R.string.no, new AlertDialog.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				KakouManager.this.finish();
			}
		});
		builder.create().show();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case STARTACTIVITY_FOR_READICCARD:
			if (resultCode == RESULT_OK) {
				Intent intent = new Intent();
				switch (mType) {
				case STARTACTIVITY_FOR_KAKOU_BIND:
					if (data.getStringExtra("cardNumber") != null) {
						intent.putExtra("title", getString(R.string.kakou_band));
						intent.putExtra("cardNumber", data.getStringExtra("cardNumber"));
						intent.putExtra("from", GlobalFlags.LIST_TYPE_FROM_KAKOUMANAGER);
						intent.setClass(getApplicationContext(), ShipListActivity.class);
						startActivity(intent);
					}
					break;
				case STARTACTIVITY_FOR_KAKOU_PAYCARD:
					intent.putExtra("title", getString(R.string.paycard));
					break;
				}
			}
		}
	}

	@Override
	public void onHttpResult(String str, int httpRequestType) {
		// TODO Auto-generated method stub
		Log.i(TAG, "onHttpResult() str:" + (str != null));
		if (progressDialog != null) {
			progressDialog.dismiss();
			progressDialog = null;
		}
		if (BUILDKKRELATION == httpRequestType) {
			if (str != null && ("1".equals(str) || "2".equals(str))) {
				SystemSetting.setBindShip(null, GlobalFlags.LIST_TYPE_FROM_KAKOUMANAGER + "");
				deleteHistory();
				finish();
			} else {
				HgqwToast.makeText(KakouManager.this, R.string.unbindship_failure, HgqwToast.LENGTH_LONG).show();
			}

		} else if (GET_SHIP_BY_KK == httpRequestType) {
			try {
				if (StringUtils.isNotEmpty(str)) {
					SystemSetting.setShipOfKK(PullXmlGetShipByKK.pullXml(str));
					try {
						if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
							return;
						}
						String projectDir = Environment.getExternalStorageDirectory().getPath() + File.separator + "pingtech";
						File dir = new File(projectDir);
						if (!dir.exists()) {
							dir.mkdir();
						}
						FileWriter writer = new FileWriter(projectDir + File.separator + "kakoubindshipinfo.xml");
						writer.write(str);
						writer.close();
						return;
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			} catch (XmlPullParserException e) {
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * 删除历史数据。巡查版巡查模块解绑删除船舶、证件、船员
	 * 
	 * @param hc
	 * @param mapsForKk
	 */
	private void deleteHistory() {
		String hcForTiKou = "";
		String kacbqkidForTiKou = "";
		String hcForKk = "";
		String kacbqkidForKk = "";

		ArrayList<HashMap<String, Object>> mapsForKk = SystemSetting.getShipOfKK();
		HashMap<String, Object> kaKouBinddata = SystemSetting.getBindShip(GlobalFlags.LIST_TYPE_FROM_KAKOUMANAGER + "");
		HashMap<String, Object> bindDataForTiKou = SystemSetting.getBindShip(GlobalFlags.LIST_TYPE_FROM_TIKOUMANAGER + "");

		if (bindDataForTiKou != null) {// 如果梯口没有解绑
			hcForTiKou = (String) bindDataForTiKou.get("hc");
			kacbqkidForTiKou = (String) bindDataForTiKou.get("kacbqkid");
		}

		if (kaKouBinddata == null) {// 卡口已经解绑
			if (mapsForKk != null && mapsForKk.size() > 0) {
				for (HashMap<String, Object> hashMap : mapsForKk) {
					hcForKk = (String) hashMap.get("hc");
					kacbqkidForKk = (String) hashMap.get("kacbqkid");
					if (kacbqkidForKk != null && !kacbqkidForKk.equals(kacbqkidForTiKou)) {
						DbUtil.deleteKacbqkByHc(hcForKk);
						DbUtil.deleteHgzjxxByHc(hcForKk);
						DbUtil.deleteCyxxByCbid(kacbqkidForKk);
					}
				}
			}
			// 删除卡口通行记录
			// DbUtil.deleteTxjlForKakou();
		}
	}
}
