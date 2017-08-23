package com.pingtech.hgqw.module.xunjian.activity;

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
import com.pingtech.hgqw.activity.SelectPersonActivity;
import com.pingtech.hgqw.activity.SelectShipActivity;
import com.pingtech.hgqw.activity.ShipDetailActivity;
import com.pingtech.hgqw.activity.ShipListActivity;
import com.pingtech.hgqw.base.BaseApplication;
import com.pingtech.hgqw.entity.GlobalFlags;
import com.pingtech.hgqw.entity.LoginUser;
import com.pingtech.hgqw.interf.OnHttpResult;
import com.pingtech.hgqw.module.bindplace.activity.BindPlaceDetail;
import com.pingtech.hgqw.module.bindplace.activity.BindPlaceReadcard;
import com.pingtech.hgqw.module.bindship.activity.ShipBind;
import com.pingtech.hgqw.module.cgcs.activity.CgcsReadcard;
import com.pingtech.hgqw.module.offline.base.utils.DbUtil;
import com.pingtech.hgqw.utils.BasicNameValuePair;
import com.pingtech.hgqw.utils.Log;
import com.pingtech.hgqw.utils.StringUtils;
import com.pingtech.hgqw.utils.SystemSetting;
import com.pingtech.hgqw.web.NetWorkManager;
import com.pingtech.hgqw.widget.HgqwToast;

/** 巡查巡检主界面的activity类 */
public class XunChaXunJian extends MyActivity implements OnHttpResult {
	private static final String TAG = "XunChaXunJian";

	/** 日常巡检 */
	private static final int STARTACTIVITY_FOR_NORMALXUNJIAN = 1;

	/** 船舶绑定 */
	private static final int STARTACTIVITY_FOR_BINDSHIP = 2;

	/** 查岗查哨 */
	private static final int STARTACTIVITY_FOR_CHAGANGCHASHAO = 3;

	/** 解除船舶绑定 */
	private static final int STARTACTIVITY_FOR_UNBINDSHIP = 4;

	/** 进入刷卡界面 */
	private static final int STARTACTIVITY_FOR_READICCARD = 5;

	/** 查询人员 */
	private static final int STARTACTIVITY_FOR_SELECT_PERSON = 6;

	/** 查询船舶 */
	private static final int STARTACTIVITY_FOR_SELECT_SHIP = 7;

	/** 地点绑定 */
	private static final int STARTACTIVITY_FOR_BINDPLACE = 8;

	/** 车辆检查 */
	private static final int STARTACTIVITY_FOR_KAKOU_CLJC = 10;

	/** 解除地点绑定 */
	private static final int STARTACTIVITY_FOR_UNBINDPLACE = 9;

	private final int gridViewMenuItemId[] = { STARTACTIVITY_FOR_NORMALXUNJIAN, STARTACTIVITY_FOR_KAKOU_CLJC, STARTACTIVITY_FOR_BINDSHIP,
			STARTACTIVITY_FOR_UNBINDSHIP, STARTACTIVITY_FOR_BINDPLACE, STARTACTIVITY_FOR_UNBINDPLACE, STARTACTIVITY_FOR_CHAGANGCHASHAO,
			STARTACTIVITY_FOR_SELECT_PERSON, STARTACTIVITY_FOR_SELECT_SHIP };

	private final int gridViewMenuItemString[] = { R.string.normalxunjian, R.string.clxj, R.string.bindShip, R.string.unbindShip, R.string.bindPlace,
			R.string.unbindPlace, R.string.chagangchashao, R.string.select_person, R.string.select_ship };

	private final int gridViewMenuItemImage[] = { R.drawable.normalxunjian, R.drawable.cljc, R.drawable.bindship, R.drawable.unbindship,
			R.drawable.bindplace, R.drawable.unbindplace, R.drawable.chagangchashao, R.drawable.peoplesel, R.drawable.shipsel };

	private GridView mGridView;

	private int mType;

	private ProgressDialog progressDialog = null;

	private String xjddName = "未绑定";

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
				intent.putExtra("from", GlobalFlags.LIST_TYPE_FROM_XUNCHAXUNJIAN);
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
				intent.putExtra("from", GlobalFlags.BINDSHIP_FROM_XUNCHAXUNJIAN);
				intent.setClass(getApplicationContext(), ShipBind.class);
				startActivity(intent);
			}
		});
		builder.create().show();
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.main);

		Log.i(TAG, "onCreate()");
		setMyActiveTitle(R.string.xunchaxunjian);
		mGridView = (GridView) findViewById(R.id.gridView1);
		mGridView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				HashMap<String, Object> bindData = SystemSetting.getBindShip(GlobalFlags.LIST_TYPE_FROM_XUNCHAXUNJIAN + "");
				mType = gridViewMenuItemId[arg2];
				switch (mType) {
				case STARTACTIVITY_FOR_NORMALXUNJIAN:
					if (StringUtils.isEmpty(SystemSetting.xunJianId) && bindData == null) {
						HgqwToast.makeText(XunChaXunJian.this, R.string.no_ship_place, HgqwToast.LENGTH_LONG).show();
						return;
					}
					intent.putExtra("title", getString(R.string.xunchaxunjian) + ">" + getString(R.string.normalxunjian));
					intent.putExtra("cardtype", ReadcardActivity.READCARD_TYPE_ID_CARD);
					intent.putExtra("from", "03");
					if (bindData != null) {
						intent.putExtra("hc", bindData.get("hc") + "");
						intent.putExtra("kacbqkid", bindData.get("kacbqkid") + "");
						intent.putExtra("cbzwm", bindData.get("cbzwm") + "");
					}
					intent.setClass(getApplicationContext(), ReadcardActivity.class);
					startActivityForResult(intent, STARTACTIVITY_FOR_READICCARD);
					return;
				case STARTACTIVITY_FOR_BINDSHIP:
					// 已经绑定地点，提示先解绑
					if (StringUtils.isNotEmpty(SystemSetting.xunJianId)) {
						HgqwToast.makeText(XunChaXunJian.this, R.string.already_bindplace, HgqwToast.LENGTH_LONG).show();
						return;
					}
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
						// intent.putExtra("cys", (String) Binddata.get("cys"));
						intent.putExtra("dlcys", (String) bindData.get("dlcys"));
						intent.putExtra("dlrys", (String) bindData.get("dlrys"));
						intent.putExtra("bdzt", (String) bindData.get("bdzt"));
						intent.putExtra("kacbzt", (String) bindData.get("kacbzt"));
						intent.putExtra("from", GlobalFlags.LIST_TYPE_FROM_XUNCHAXUNJIAN);
						intent.putExtra("fromxunchaxunjian", true);
						intent.putExtra("frombindship", true);
						intent.putExtra("saveXjBtnFlag", true);
						intent.setClass(getApplicationContext(), ShipDetailActivity.class);
						startActivity(intent);
						return;
					} else {
						if (SystemSetting.getBindShipAllSize(GlobalFlags.LIST_TYPE_FROM_XUNCHAXUNJIAN) > 0) {
							onShowQuestFromBindListDialog();
						} else {
							if (BaseApplication.instent.getWebState()) {
								// 01卡口、02梯口、03巡查巡检、04查询人员模块、05船舶动态、0501船舶动态>>>船舶绑定、0201梯口管理>>>船舶绑定、0101卡口管理>>>船舶绑定、0301巡查巡检>>>船舶绑定
								intent.putExtra("from", GlobalFlags.BINDSHIP_FROM_XUNCHAXUNJIAN);
								intent.setClass(getApplicationContext(), ShipBind.class);
								startActivity(intent);
							} else {
								HgqwToast.makeText(getApplicationContext(), R.string.no_web_cannot_bind_place, HgqwToast.LENGTH_LONG).show();
							}
						}
						return;
					}
				case STARTACTIVITY_FOR_CHAGANGCHASHAO:
					if (BaseApplication.instent.getWebState()) {
						intent.putExtra("title", getString(R.string.xunchaxunjian) + ">" + getString(R.string.chagangchashao));
						intent.putExtra("cardtype", ReadcardActivity.READCARD_TYPE_ID_CARD);
						intent.putExtra("from", "03");
						intent.setClass(getApplicationContext(), CgcsReadcard.class);
						startActivityForResult(intent, STARTACTIVITY_FOR_READICCARD);
					} else {
						HgqwToast.makeText(getApplicationContext(), R.string.no_web_cannot_cgcs, HgqwToast.LENGTH_LONG).show();
					}
					return;
				case STARTACTIVITY_FOR_UNBINDSHIP:
					if (bindData == null) {
						HgqwToast.makeText(XunChaXunJian.this, R.string.no_bindship, HgqwToast.LENGTH_LONG).show();
						return;
					}
					intent.putExtra("hc", (String) bindData.get("hc"));
					intent.putExtra("cbzwm", (String) bindData.get("cbzwm"));
					intent.putExtra("jcfl", (String) bindData.get("jcfl"));
					intent.putExtra("cbywm", (String) bindData.get("cbywm"));
					intent.putExtra("gj", (String) bindData.get("gj"));
					intent.putExtra("cbxz", (String) bindData.get("cbxz"));
					intent.putExtra("tkwz", (String) bindData.get("tkwz"));
					intent.putExtra("cdgs", (String) bindData.get("cdgs"));
					// intent.putExtra("cys", (String) Binddata.get("cys"));
					intent.putExtra("dlcys", (String) bindData.get("dlcys"));
					intent.putExtra("dlrys", (String) bindData.get("dlrys"));
					intent.putExtra("bdzt", (String) bindData.get("bdzt"));
					intent.putExtra("kacbzt", (String) bindData.get("kacbzt"));
					intent.putExtra("from", GlobalFlags.LIST_TYPE_FROM_XUNCHAXUNJIAN);
					intent.putExtra("title", getString(R.string.unbindShip));
					intent.setClass(getApplicationContext(), ShipDetailActivity.class);
					startActivity(intent);
					return;
				case STARTACTIVITY_FOR_SELECT_PERSON:
					intent.putExtra("fromxuncha", true);
					intent.setClass(getApplicationContext(), SelectPersonActivity.class);
					startActivity(intent);
					return;
				case STARTACTIVITY_FOR_SELECT_SHIP:
					intent.putExtra("fromxuncha", true);
					intent.putExtra("bindtype", GlobalFlags.LIST_TYPE_FROM_XUNCHAXUNJIAN);
					intent.setClass(getApplicationContext(), SelectShipActivity.class);
					startActivity(intent);
					return;
				case STARTACTIVITY_FOR_BINDPLACE:
					if (bindData != null) {
						HgqwToast.makeText(XunChaXunJian.this, R.string.already_bindship, HgqwToast.LENGTH_LONG).show();
						return;
					} else {
						if (StringUtils.isNotEmpty(SystemSetting.xunJianId)) {
							intent.putExtra("BINDPLACE", "0");
							intent.setClass(getApplicationContext(), BindPlaceDetail.class);
							startActivity(intent);
						} else {
							if (BaseApplication.instent.getWebState()) {
								intent.putExtra("title", getString(R.string.xunchaxunjian) + ">" + getString(R.string.bindPlace));
								intent.putExtra("cardtype", ReadcardActivity.READCARD_TYPE_ID_CARD);
								intent.putExtra("from", "03");
								intent.putExtra("isClear", true);
								intent.setClass(getApplicationContext(), BindPlaceReadcard.class);
								startActivityForResult(intent, STARTACTIVITY_FOR_READICCARD);
							} else {
								HgqwToast.makeText(XunChaXunJian.this, R.string.no_web_cannot_bind_place, HgqwToast.LENGTH_LONG).show();
							}
						}
					}

					return;
				case STARTACTIVITY_FOR_UNBINDPLACE:
					if (bindData != null) {
						HgqwToast.makeText(XunChaXunJian.this, R.string.already_bindship, HgqwToast.LENGTH_LONG).show();
						return;
					} else {
						if (StringUtils.isNotEmpty(SystemSetting.xunJianId)) {
							intent.putExtra("BINDPLACE", "1");
							intent.setClass(getApplicationContext(), BindPlaceDetail.class);
							startActivity(intent);
						} else {
							HgqwToast.makeText(XunChaXunJian.this, R.string.no_bindplace, HgqwToast.LENGTH_LONG).show();
							return;
						}
					}
					return;
				case STARTACTIVITY_FOR_KAKOU_CLJC:
					cljc();
					return;
				default:
					break;
				}
			}

		});
		onUpdateGridViewmenu();
	}

	/**
	 * 车辆检查
	 */
	protected void cljc() {
		if (!hasBind()) {
			return;
		}
		Intent intent = new Intent();
		intent.setClass(getApplicationContext(), XunjianCljc.class);
		intent.putExtra("from", GlobalFlags.LIST_TYPE_FROM_XUNCHAXUNJIAN);
		startActivity(intent);
	}

	private boolean hasBind() {
		HashMap<String, Object> bindData = SystemSetting.getBindShip(GlobalFlags.LIST_TYPE_FROM_XUNCHAXUNJIAN + "");
		if (StringUtils.isEmpty(SystemSetting.xunJianId) && bindData == null) {
			HgqwToast.makeText(XunChaXunJian.this, R.string.no_ship_place, HgqwToast.LENGTH_LONG).show();
			return false;
		}
		return true;
	}

	private boolean hasBindQyxx() {
		HashMap<String, Object> bindData = SystemSetting.getBindShip(GlobalFlags.LIST_TYPE_FROM_XUNCHAXUNJIAN + "");
		if (StringUtils.isEmpty(SystemSetting.xunJianId) && bindData == null) {
			HgqwToast.makeText(XunChaXunJian.this, R.string.no_ship_place, HgqwToast.LENGTH_LONG).show();
			return false;
		}
		return true;
	}

	private void onUpdateGridViewmenu() {
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
	public void onDestroy() {
		Log.i(TAG, "onDestroy()");
		super.onDestroy();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			if (SystemSetting.getBindShip(GlobalFlags.LIST_TYPE_FROM_XUNCHAXUNJIAN + "") != null) {
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
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("userID", LoginUser.getCurrentLoginUser().getUserID()));
				params.add(new BasicNameValuePair("PDACode", SystemSetting.getPDACode()));
				params.add(new BasicNameValuePair("bindState", "0"));
				params.add(new BasicNameValuePair("voyageNumber", SystemSetting.getVoyageNumber(GlobalFlags.LIST_TYPE_FROM_XUNCHAXUNJIAN + "")));
				params.add(new BasicNameValuePair("bindType", "2"));
				// 执勤对象类型:船舶0 卡口(区域)1 码头2 泊位3
				params.add(new BasicNameValuePair("zqdxlx", GlobalFlags.ZQDXLX_CB + ""));

				if (progressDialog != null) {
					return;
				}
				progressDialog = new ProgressDialog(XunChaXunJian.this);
				progressDialog.setTitle(getString(R.string.waiting));
				progressDialog.setMessage(getString(R.string.waiting));
				progressDialog.setCancelable(false);
				progressDialog.setIndeterminate(false);
				progressDialog.show();
				NetWorkManager.request(XunChaXunJian.this, url, params, 0);
			}
		});
		builder.setNegativeButton(R.string.no, new AlertDialog.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				XunChaXunJian.this.finish();
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
					if (data.getStringExtra("cardNumber") != null) {
						intent.putExtra("title", getString(R.string.bindShip));
						intent.putExtra("cardNumber", data.getStringExtra("cardNumber"));
						intent.putExtra("from", GlobalFlags.LIST_TYPE_FROM_XUNCHAXUNJIAN);
						intent.setClass(getApplicationContext(), ShipListActivity.class);
						startActivity(intent);
					}
					break;
				case STARTACTIVITY_FOR_NORMALXUNJIAN:
					break;
				}
			}
		}
	}

	/** 处理解除绑定结果 */
	@Override
	public void onHttpResult(String str, int httpRequestType) {
		Log.i(TAG, "onHttpResult() str：" + (str != null));
		if (progressDialog != null) {
			progressDialog.dismiss();
			progressDialog = null;
		}
		if (str != null && ("1".equals(str) || "2".equals(str))) {
			// 解绑成功，删除历史数据
			deleteHistory();
			SystemSetting.setBindShip(null, GlobalFlags.LIST_TYPE_FROM_XUNCHAXUNJIAN + "");
			finish();
		} else {
			HgqwToast.makeText(XunChaXunJian.this, R.string.unbindship_failure, HgqwToast.LENGTH_LONG).show();
		}
	}

	private void deleteHistory() {
		HashMap<String, Object> xunchaBinddata = SystemSetting.getBindShip(GlobalFlags.LIST_TYPE_FROM_XUNCHAXUNJIAN + "");
		String hc = "";
		String kacbqkid = "";
		if (xunchaBinddata != null) {
			hc = (String) xunchaBinddata.get("hc");
			kacbqkid = (String) xunchaBinddata.get("kacbqkid");
			DbUtil.deleteKacbqkByHc(hc);
			DbUtil.deleteHgzjxxByHc(hc);
			DbUtil.deleteCyxxByCbid(kacbqkid);
		}
	}
}
