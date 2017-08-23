package com.pingtech.hgqw.module.cfzg;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Xml;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.pingtech.R;
import com.pingtech.hgqw.base.BaseApplication;
import com.pingtech.hgqw.entity.FlagManagers;
import com.pingtech.hgqw.entity.FlagUrls;
import com.pingtech.hgqw.entity.LoginUser;
import com.pingtech.hgqw.interf.OnHttpResult;
import com.pingtech.hgqw.module.cfzg.utils.PullXmlCfzgShip;
import com.pingtech.hgqw.pullxml.PullXmlGetCountOfTktxjl;
import com.pingtech.hgqw.pullxml.PullXmlUrgencyWarningInfo;
import com.pingtech.hgqw.utils.BasicNameValuePair;
import com.pingtech.hgqw.utils.DataDictionary;
import com.pingtech.hgqw.utils.Log;
import com.pingtech.hgqw.utils.SystemSetting;
import com.pingtech.hgqw.web.NetWorkManager;
import com.pingtech.hgqw.web.vpn.VpnManager;
import com.pingtech.hgqw.widget.HgqwToast;

public class CfzgIndex extends CfzgSuperActivity implements OnHttpResult {
	private static final String TAG = "CfzgIndex";

	private static final String TAG2 = "CfzgIndex";

	/** 发起获取船舶详情的http请求的type */
	private static final int HTTPREQUEST_TYPE_FOR_GETDETAIL = 1;

	/**
	 * 中英文标识：cn=true中文，cn=false英文。
	 */
	private boolean cn = true;

	/** 发起数据字典同步的http请求type */
	private static final int HTTPREQUEST_TYPE_GET_DD = 70;

	private ProgressDialog progressDialog = null;

	private VpnManager vpnManager = null;

	/**
	 * 电池电量监听
	 */
	BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			int level = intent.getIntExtra("level", 0);
			int status = intent.getIntExtra("status", 0);
			if ((level == 20 || level == 15 || level == 10) && status != BatteryManager.BATTERY_STATUS_CHARGING) {
				onNotifyBatteries(level);
			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.i(TAG2, this.getClass().getName() + " onCreate");
		BaseApplication.instent.settingInit();// 启动vpn
		SystemSetting.restoreBindShipInfo(); // 将船舶绑定的信息加入内存。
		getDD();// 后台自动更新数据字典

		super.onCreateForCfzg(savedInstanceState, R.layout.cfzg);
		setContentView(R.layout.cfzg, 2);

		stopService(new Intent("com.pingtech.PINGTECH_SERVICE"));
		startService(new Intent("com.pingtech.PINGTECH_SERVICE"));
		updateCountInShip();
		((TextView) findViewById(R.id.current_dialog_title)).setTextSize(22);
		((TextView) findViewById(R.id.current_dialog_title)).setText("主页面");

		KeyguardManager keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
		KeyguardLock lock = keyguardManager.newKeyguardLock(KEYGUARD_SERVICE);// 屏保
		lock.disableKeyguard();
		// 电池电量监听
		this.registerReceiver(broadcastReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
		Intent intent = getIntent();
		if (intent != null && intent.getBooleanExtra("cnEnFlag", false)) {
			cn = intent.getBooleanExtra("cn", true);
			changeCNEN(cn);
		}
		updateShipInfo();// 更新船舶信息
	}

	private void changeCNEN(boolean cn) {
		HashMap<String, Object> _BindShip = SystemSetting.getBindShip(CfzgShipListActivity.LIST_TYPE_FROM_SHIPSTATUS + "");

		if (!cn) {// 切换到英文页面
			((TextView) findViewById(R.id.current_dialog_title)).setText("Main Page");// 标题

			findViewById(R.id.cfzg_layout_en).setVisibility(View.VISIBLE);
			findViewById(R.id.cfzg_layout_cn).setVisibility(View.GONE);
			// 船方自管首页显示船舶名称
			if (_BindShip != null) {
				((TextView) findViewById(R.id.current_user)).setText("  Current user: " + (String) _BindShip.get("cbywm"));
			} else {
				((TextView) findViewById(R.id.current_user)).setText("  Current user: " + "No bind ship");

			}
			// ((TextView) findViewById(R.id.current_user)).setTextSize(18);
			findViewById(R.id.base_layout_bottom_user).setBackgroundDrawable(null);
			findViewById(R.id.layout).setBackgroundResource(R.drawable.mainmenu_bg_v);
		} else {// 切换到中文页面
			((TextView) findViewById(R.id.current_dialog_title)).setText("主页面");// 标题

			findViewById(R.id.cfzg_layout_en).setVisibility(View.GONE);
			findViewById(R.id.cfzg_layout_cn).setVisibility(View.VISIBLE);
			// 船方自管首页显示船舶名称
			if (_BindShip != null) {
				((TextView) findViewById(R.id.current_user)).setText("  " + getString(R.string.currentuser) + (String) _BindShip.get("cbzwm"));
			} else {
				((TextView) findViewById(R.id.current_user)).setText("  " + getString(R.string.currentuser) + "未绑定船舶");

			}
			// ((TextView) findViewById(R.id.current_user)).setTextSize(18);
			findViewById(R.id.base_layout_bottom_user).setBackgroundDrawable(null);
			findViewById(R.id.layout).setBackgroundResource(R.drawable.mainmenu_bg_v);
		}
	}

	/**
	 * @方法名：getDD
	 * @功能说明：更新数据字典
	 * @author liums
	 * @date 2013-4-24 下午3:20:42
	 */
	private void getDD() {
		if (progressDialog != null) {
			return;
		}
		String str = "getDD";
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		/*
		 * progressDialog = new ProgressDialog(MainmenuActivity.this);
		 * progressDialog.setTitle(getString(R.string.syncdata));
		 * progressDialog.setMessage(getString(R.string.waiting));
		 * progressDialog.setCancelable(false);
		 * progressDialog.setIndeterminate(false); progressDialog.show();
		 */
		NetWorkManager.request(CfzgIndex.this, str, params, HTTPREQUEST_TYPE_GET_DD);
	}

	/**
	 * @方法名：cfzgButtonClick
	 * @功能说明：按钮点击事件
	 * @author liums
	 * @date 2013-4-27 下午3:18:37
	 * @param v
	 */
	public void cfzgButtonClick(View v) {
		HashMap<String, Object> _BindShipShipInfo = SystemSetting.getBindShip(CfzgShipListActivity.LIST_TYPE_FROM_SHIPSTATUS + "");
		switch (v.getId()) {
		case R.id.cfzg_btn_cn_rigth:// 中英文切换
			switchCNEN();
			break;
		case R.id.cfzg_btn_en_left:// 中英文切换
			switchCNEN();
			break;
		case R.id.cfzg_btn_passcard:// 刷登轮证

			if (_BindShipShipInfo != null) {
				Intent intentPassCard = new Intent();
				intentPassCard.putExtra("ic", true);
				intentPassCard.putExtra("cn", cn);
				intentPassCard.setClass(getApplicationContext(), CfzgReadcardActivity.class);
				startActivity(intentPassCard);
			} else {
				if (cn) {
					HgqwToast.makeText(getApplicationContext(), getString(R.string.no_bindship), HgqwToast.LENGTH_LONG).show();
				} else {
					HgqwToast.makeText(getApplicationContext(), getString(R.string.cbzg_no_bindship_en), HgqwToast.LENGTH_LONG).show();
				}
			}
			break;
		case R.id.cfzg_btn_idcard:// 刷身份证

			if (_BindShipShipInfo != null) {
				Intent intentIdCard = new Intent();
				intentIdCard.putExtra("ic", false);
				intentIdCard.putExtra("cn", cn);
				intentIdCard.setClass(getApplicationContext(), CfzgReadcardActivity.class);
				startActivity(intentIdCard);
			} else {
				if (cn) {
					HgqwToast.makeText(getApplicationContext(), getString(R.string.no_bindship), HgqwToast.LENGTH_LONG).show();
				} else {
					HgqwToast.makeText(getApplicationContext(), getString(R.string.cbzg_no_bindship_en), HgqwToast.LENGTH_LONG).show();
				}
			}
			break;
		case R.id.cfzg_btn_110:// 紧急报警
			if (_BindShipShipInfo == null) {
				if (cn) {
					HgqwToast.makeText(getApplicationContext(), getString(R.string.no_bindship), HgqwToast.LENGTH_LONG).show();
				} else {
					HgqwToast.makeText(getApplicationContext(), getString(R.string.cbzg_no_bindship_en), HgqwToast.LENGTH_LONG).show();
				}
				return;
			}
			// 先判断本地用户是否已经登录
			String userid = LoginUser.getCurrentLoginUser().getUserID();
			if (userid != null && !"".equals(userid)) {
				dialogActivity();// 调用自定义对话框
			} else {
				HgqwToast.getToastView(getApplicationContext(), "用户信息获取失败，请先登录！").show();
			}

			break;
		case R.id.cfzg_btn_shipinfo:// 船舶信息
			if (_BindShipShipInfo != null) {
				showShipInfo();// 进入船舶详情页面
			} else {
				if (cn) {
					HgqwToast.makeText(getApplicationContext(), getString(R.string.no_bindship), HgqwToast.LENGTH_LONG).show();
				} else {
					HgqwToast.makeText(getApplicationContext(), getString(R.string.cbzg_no_bindship_en), HgqwToast.LENGTH_LONG).show();
				}
			}
			break;
		case R.id.cfzg_btn_useonly:// 边检专用键
			// 跳转到登陆页面
			Intent intent = new Intent();
			intent.setClass(getApplicationContext(), CfzgLoginByUserName.class);
			intent.putExtra("cn", cn);
			startActivity(intent);
			this.overridePendingTransition(R.anim.enter_righttoleft, R.anim.disappear_alpha);
			finish();
			break;
		case R.id.cfzg_btn_sailorcard:// 船员名单
			if (_BindShipShipInfo != null) {
				// 跳转到船员列表页面
				Intent intentSailorcard = new Intent();
				intentSailorcard.putExtra("hc", (String) _BindShipShipInfo.get("hc"));
				intentSailorcard.putExtra("cn", cn);
				intentSailorcard.setClass(getApplicationContext(), CfzgSailorList.class);
				startActivity(intentSailorcard);
			} else {
				if (cn) {
					HgqwToast.makeText(getApplicationContext(), getString(R.string.no_bindship), HgqwToast.LENGTH_LONG).show();
				} else {
					HgqwToast.makeText(getApplicationContext(), getString(R.string.cbzg_no_bindship_en), HgqwToast.LENGTH_LONG).show();
				}
			}
			break;

		default:
			break;
		}

	}

	/**
	 * @方法名：sendAlarmInfo
	 * @功能说明：发送紧急报警信息
	 * @author liums
	 * @param hc
	 * @date 2013-5-3 下午5:37:19
	 */
	private void sendAlarmInfo() {

		if (progressDialog != null) {
			return;
		}
		HashMap<String, Object> _BindShipShipInfo = SystemSetting.getBindShip(CfzgShipListActivity.LIST_TYPE_FROM_SHIPSTATUS + "");
		if (_BindShipShipInfo == null) {
			if (cn) {
				HgqwToast.makeText(getApplicationContext(), getString(R.string.no_bindship), HgqwToast.LENGTH_LONG).show();
			} else {
				HgqwToast.makeText(getApplicationContext(), getString(R.string.cbzg_no_bindship_en), HgqwToast.LENGTH_LONG).show();
			}
			return;
		}
		String url = "urgencyWarningInfo";
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("hc", (String) _BindShipShipInfo.get("hc")));
		params.add(new BasicNameValuePair("userid", LoginUser.getCurrentLoginUser().getUserID()));
		progressDialog = new ProgressDialog(CfzgIndex.this);
		progressDialog.setTitle(getString(R.string.waiting));
		progressDialog.setMessage(getString(R.string.waiting));
		progressDialog.setCancelable(false);
		progressDialog.setIndeterminate(false);
		progressDialog.show();
		NetWorkManager.request(CfzgIndex.this, url, params, FlagUrls.URGENCY_WARNING_INFO);
	}

	/**
	 * @方法名：updateCountInShip
	 * @功能说明：更新本船船下船员，非本船船上人员
	 * @author liums
	 * @date 2013-5-3 下午5:46:33
	 */
	private void updateCountInShip() {
		Log.i(TAG, "updateCountInShip");
		if (progressDialog != null) {
			return;
		}
		HashMap<String, Object> _BindShipShipInfo = SystemSetting.getBindShip(CfzgShipListActivity.LIST_TYPE_FROM_SHIPSTATUS + "");
		if (_BindShipShipInfo == null) {
			return;
		}
		String url = "getCountOfTktxjl";

		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("hc", (String) _BindShipShipInfo.get("hc")));
		params.add(new BasicNameValuePair("userid", LoginUser.getCurrentLoginUser().getUserID()));
		// progressDialog = new ProgressDialog(CfzgIndex.this);
		// progressDialog.setTitle(getString(R.string.waiting));
		// progressDialog.setMessage(getString(R.string.waiting));
		// progressDialog.setCancelable(false);
		// progressDialog.setIndeterminate(false);
		// progressDialog.show();
		NetWorkManager.request(CfzgIndex.this, url, params, FlagUrls.GET_COUNT_OF_TKTXJL);
	}

	/**
	 * @方法名：switchCNEN
	 * @功能说明：中英文切换按钮
	 * @author liums
	 * @date 2013-4-27 下午7:29:00
	 */
	private void switchCNEN() {
		HashMap<String, Object> _BindShip = SystemSetting.getBindShip(CfzgShipListActivity.LIST_TYPE_FROM_SHIPSTATUS + "");

		if (cn) {// 切换到英文页面
			((TextView) findViewById(R.id.current_dialog_title)).setText("Main Page");// 标题

			findViewById(R.id.cfzg_layout_en).setVisibility(View.VISIBLE);
			findViewById(R.id.cfzg_layout_cn).setVisibility(View.GONE);
			cn = false;
			// 船方自管首页显示船舶名称
			if (_BindShip != null) {
				((TextView) findViewById(R.id.current_user)).setText("  Current user: " + (String) _BindShip.get("cbywm"));
			} else {
				((TextView) findViewById(R.id.current_user)).setText("  Current user: " + "No bind ship");

			}
			// ((TextView) findViewById(R.id.current_user)).setTextSize(18);
			findViewById(R.id.base_layout_bottom_user).setBackgroundDrawable(null);
			findViewById(R.id.layout).setBackgroundResource(R.drawable.mainmenu_bg_v);
		} else {// 切换到中文页面
			((TextView) findViewById(R.id.current_dialog_title)).setText("主页面");// 标题

			findViewById(R.id.cfzg_layout_en).setVisibility(View.GONE);
			findViewById(R.id.cfzg_layout_cn).setVisibility(View.VISIBLE);
			cn = true;
			// 船方自管首页显示船舶名称
			if (_BindShip != null) {
				((TextView) findViewById(R.id.current_user)).setText("  " + getString(R.string.currentuser) + (String) _BindShip.get("cbzwm"));
			} else {
				((TextView) findViewById(R.id.current_user)).setText("  " + getString(R.string.currentuser) + "未绑定船舶");

			}
			// ((TextView) findViewById(R.id.current_user)).setTextSize(18);
			findViewById(R.id.base_layout_bottom_user).setBackgroundDrawable(null);
			findViewById(R.id.layout).setBackgroundResource(R.drawable.mainmenu_bg_v);
		}
	}

	/**
	 * @方法名：showShipInfo
	 * @功能说明：查看船舶概要信息，如果没有绑定船舶给出提示
	 * @author liums
	 * @date 2013-4-27 下午7:48:32
	 */
	private void showShipInfo() {
		HashMap<String, Object> _BindShip = SystemSetting.getBindShip(CfzgShipListActivity.LIST_TYPE_FROM_SHIPSTATUS + "");
		Intent intent = new Intent();
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
			intent.putExtra("bdzt", (String) _BindShip.get("bdzt"));
			intent.putExtra("kacbzt", (String) _BindShip.get("kacbzt"));
			intent.putExtra("from", CfzgShipListActivity.LIST_TYPE_FROM_SHIPSTATUS);
			/**
			 * 船方自管标志位：true来自船方自管，false 默认版本 cfzgFlag
			 */
			intent.putExtra("cfzgFlag", true);
			intent.setClass(getApplicationContext(), CfzgShipDetailActivity.class);
			startActivity(intent);
			return;
		} else {
			if (cn) {
				HgqwToast.makeText(getApplicationContext(), getString(R.string.no_bindship), HgqwToast.LENGTH_LONG).show();
			} else {
				HgqwToast.makeText(getApplicationContext(), getString(R.string.cbzg_no_bindship_en), HgqwToast.LENGTH_LONG).show();
			}
		}
	}

	@Override
	public void onHttpResult(String str, int httpRequestType) {
		if (progressDialog != null) {
			progressDialog.dismiss();
			progressDialog = null;
		}
		switch (httpRequestType) {
		case HTTPREQUEST_TYPE_GET_DD:
			if (str != null) {
				if (onParseDataDictionaryXMLData(str)) {
					try {
						if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
							HgqwToast.makeText(CfzgIndex.this, R.string.sdcardunmounted, HgqwToast.LENGTH_LONG).show();
							return;
						}
						String projectDir = Environment.getExternalStorageDirectory().getPath() + File.separator + "pingtech";
						File dir = new File(projectDir);
						if (!dir.exists()) {
							dir.mkdir();
						}
						FileWriter writer = new FileWriter(projectDir + File.separator + "datadict.xml");
						writer.write(str);
						writer.close();
						// Toast.makeText(MainmenuActivity.this,
						// R.string.syncdatasuccess,
						// BaseToast.LENGTH_LONG).show();
						DataDictionary.restoreDataDictionary();
					} catch (IOException e) {
						e.printStackTrace();
						HgqwToast.makeText(CfzgIndex.this, R.string.data_download_failure_info, HgqwToast.LENGTH_LONG).show();
					}
				} else {
					/*
					 * if (httpReturnXMLInfo != null) {
					 * Toast.makeText(MainmenuActivity.this, httpReturnXMLInfo,
					 * Toast.LENGTH_LONG).show(); } else {
					 * Toast.makeText(MainmenuActivity.this,
					 * R.string.data_download_failure_info,
					 * BaseToast.LENGTH_LONG).show(); }
					 */
				}
			} else {
				// Toast.makeText(MainmenuActivity.this,
				// R.string.data_download_failure_info,
				// Toast.LENGTH_LONG).show();
			}
			break;
		case FlagUrls.URGENCY_WARNING_INFO:
			if (str != null) {
				try {
					if (PullXmlUrgencyWarningInfo.pullXml(str)) {
						if (cn) {
							HgqwToast.getToastView(getApplicationContext(), "发送成功！").show();
						} else {
							HgqwToast.getToastView(getApplicationContext(), "Success!").show();
						}

					} else {
						if (cn) {
							HgqwToast.getToastView(getApplicationContext(), "发送失败，请稍后再试！").show();
						} else {
							HgqwToast.getToastView(getApplicationContext(), "error!").show();
						}
					}
				} catch (XmlPullParserException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				if (cn) {
					HgqwToast.getToastView(getApplicationContext(), getString(R.string.data_download_failure_info)).show();
				} else {
					HgqwToast.getToastView(getApplicationContext(), "error!").show();

				}
			}
			break;
		case FlagUrls.GET_COUNT_OF_TKTXJL:
			if (str != null) {
				try {
					String[] arr = PullXmlGetCountOfTktxjl.pullXml(str);
					TextView textViewLun = (TextView) findViewById(R.id.cfzg_index_lun);
					TextView textViewLu = (TextView) findViewById(R.id.cfzg_index_lu);
					TextView textViewLun_en = (TextView) findViewById(R.id.cfzg_index_lun_en);
					TextView textViewLu_en = (TextView) findViewById(R.id.cfzg_index_lu_en);
					textViewLun.setText("非本船人员登轮人数 " + arr[1]);
					textViewLu.setText("本船船员登陆人数  " + arr[0]);
					textViewLun_en.setText("The number of non-ship personnel on board " + arr[1]);
					textViewLu_en.setText("The number of ship’s crew on shore " + arr[0]);

				} catch (XmlPullParserException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
			}
			break;
		case FlagUrls.VALIDATE_PASSWORD:
			if (str != null) {
				if ("success".equals(str)) {
					// 密码验证成功，模拟调用Home键
					Intent intent = new Intent();
					intent.setAction(Intent.ACTION_MAIN);
					intent.addCategory(Intent.CATEGORY_HOME);
					startActivity(intent);
					finish();
				} else {
					HgqwToast.getToastView(getApplicationContext(), "密码错误！").show();
				}
			} else {
				if (cn) {
					HgqwToast.getToastView(getApplicationContext(), getString(R.string.data_download_failure_info)).show();
				} else {
					HgqwToast.getToastView(getApplicationContext(), "error!").show();

				}
			}
			break;
		case HTTPREQUEST_TYPE_FOR_GETDETAIL:
			updateShipView(str);
			break;
		default:
			break;
		}
	}

	/**
	 * 
	 * @方法名：updateShipView
	 * @功能说明：更新船舶页面
	 * @author liums
	 * @date 2013-10-28 下午3:23:32
	 * @param str
	 */
	private void updateShipView(String str) {
		if (str == null) {
			return;
		}
		HashMap<String, Object> bindShip = PullXmlCfzgShip.onParseXMLData(str);

		SystemSetting.setBindShip(bindShip, CfzgShipListActivity.LIST_TYPE_FROM_SHIPSTATUS + "");

		if (!cn) {// 英文页面
			// 船方自管首页显示船舶名称
			if (bindShip != null) {
				bindShip.put("bdzt", "已绑定");
				((TextView) findViewById(R.id.current_user)).setText("  Current user: " + (String) bindShip.get("cbywm"));
			} else {
				((TextView) findViewById(R.id.current_user)).setText("  Current user: " + "No bind ship");

			}
		} else {// 中文页面
			// 船方自管首页显示船舶名称
			if (bindShip != null) {
				bindShip.put("bdzt", "已绑定");
				((TextView) findViewById(R.id.current_user)).setText("  " + getString(R.string.currentuser) + (String) bindShip.get("cbzwm"));
			} else {
				((TextView) findViewById(R.id.current_user)).setText("  " + getString(R.string.currentuser) + "未绑定船舶");

			}
		}

	}

	/** 解析数据字典同步返回结果 */
	private boolean onParseDataDictionaryXMLData(String str) {
		boolean success = false;
		try {
			XmlPullParser parser = Xml.newPullParser();
			parser.setInput(new ByteArrayInputStream(str.getBytes()), "utf-8");// 设置解析的数据源
			int type = parser.getEventType();
			String text = null;
			// httpReturnXMLInfo = null;
			while (type != XmlPullParser.END_DOCUMENT) {
				switch (type) {
				case XmlPullParser.START_TAG:
					if ("result".equals(parser.getName())) {
						text = parser.nextText();
						if ("error".equals(text)) {
							success = false;
						} else if ("success".equals(text)) {
							success = true;
						}
					} else if ("info".equals(parser.getName())) {
						// 信息
						if (!success) {
							// httpReturnXMLInfo = parser.nextText();
						}
					}
					break;
				case XmlPullParser.END_TAG:
					break;
				}
				type = parser.next();
			}
			return success;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.i(TAG, "onActivityResult");
		super.onActivityResult(requestCode, resultCode, data);
		// 从对话框返回
		if (requestCode == FlagManagers.CUSTOM_DIALOG && resultCode == RESULT_OK) {
			this.sendAlarmInfo();
		} else if (requestCode == FlagManagers.CUSTOM_DIALOG_FOR_EXIT && resultCode == RESULT_OK) {
			String password = data.getStringExtra("password");
			this.validatePassword(password);
		} else {
			this.updateCountInShip();
			HashMap<String, Object> _BindShip = SystemSetting.getBindShip(CfzgShipListActivity.LIST_TYPE_FROM_SHIPSTATUS + "");

			if (cn) {
				// 船方自管首页显示船舶名称
				if (_BindShip != null) {
					((TextView) findViewById(R.id.current_user)).setText("    " + getString(R.string.currentuser) + (String) _BindShip.get("cbzwm"));
				} else {
					((TextView) findViewById(R.id.current_user)).setText("    " + getString(R.string.currentuser) + "未绑定船舶");

				}
			} else {
				// 船方自管首页显示船舶名称
				if (_BindShip != null) {
					((TextView) findViewById(R.id.current_user)).setText("  Current user: " + (String) _BindShip.get("cbywm"));
				} else {
					((TextView) findViewById(R.id.current_user)).setText("  Current user: " + "No bind ship");

				}
			}
		}
	}

	/**
	 * @方法名：validatePassword
	 * @功能说明：验证当前用户密码
	 * @author liums
	 * @date 2013-5-7 下午5:42:16
	 * @param password
	 * @return
	 */
	@SuppressWarnings("deprecation")
	private void validatePassword(String password) {

		String url = "validatePassword";
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("userName", LoginUser.getCurrentLoginUser().getUserName()));
		params.add(new BasicNameValuePair("password", password));
		progressDialog = new ProgressDialog(this);
		progressDialog.setTitle(getString(R.string.waiting));
		progressDialog.setMessage(getString(R.string.waiting));
		progressDialog.setCancelable(false);
		progressDialog.setIndeterminate(false);
		progressDialog.setButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (!((ProgressDialog) dialog).isShowing()) {
					Log.i(TAG, "!((ProgressDialog)dialog).isShowing()");
					progressDialog = null;
					return;
				}
				Log.i(TAG, "progressDialog onClick");
				dialog.dismiss();
				progressDialog = null;
			}
		});
		progressDialog.show();
		NetWorkManager.request(this, url, params, FlagUrls.VALIDATE_PASSWORD);

	}

	@Override
	protected void onNewIntent(Intent intent) {
		// updateCountInShip();

		String flag = intent.getStringExtra("flag");
		if ("back".equals(flag) || "logout".equals(flag)) {
			// 更新底部当前用户
			HashMap<String, Object> _BindShip = SystemSetting.getBindShip(CfzgShipListActivity.LIST_TYPE_FROM_SHIPSTATUS + "");

			if (cn) {
				// 船方自管首页显示船舶名称
				if (_BindShip != null) {
					((TextView) findViewById(R.id.current_user)).setText("    " + getString(R.string.currentuser) + (String) _BindShip.get("cbzwm"));
				} else {
					((TextView) findViewById(R.id.current_user)).setText("    " + getString(R.string.currentuser) + "未绑定船舶");

				}
			} else {
				// 船方自管首页显示船舶名称
				if (_BindShip != null) {
					((TextView) findViewById(R.id.current_user)).setText("  Current user: " + (String) _BindShip.get("cbywm"));
				} else {
					((TextView) findViewById(R.id.current_user)).setText("  Current user: " + "No bind ship");

				}
			}
		}
	}

	/** 接收vpn连接结果 */
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {

		}
	};

	@Override
	protected void onResume() {
		Log.i(TAG, "onResume");
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		updateCountInShip();
		vpnManager = new VpnManager(handler, this);
		Log.i(TAG, "onResume,status=" + vpnManager.getStatus());
		vpnManager.init();// vpn初始化
		super.onResume();
	}



	/** 监听物理按键 */
	@Override
	public void onAttachedToWindow() {
		Log.i(TAG, "onAttachedToWindow");
		getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD);
		super.onAttachedToWindow();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		android.util.Log.i(TAG, "onKeyDown:" + keyCode);
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:// 返回键
			dialogActivityForExit();
			break;
		case KeyEvent.KEYCODE_HOME:
			dialogActivityForExit();
			break;
		default:
			break;
		}
		return false;
	}

	/**
	 * @方法名：dialogActivity
	 * @功能说明：弹出Home键验证对话框
	 * @author liums
	 * @date 2013-5-7 下午5:34:08
	 */
	private void dialogActivityForExit() {
		String userName = LoginUser.getCurrentLoginUser().getUserName();
		if (userName == null || "".equals(userName.trim())) {
			HgqwToast.getToastView(getApplicationContext(), "如需返回主页面，请先登录！").show();
			return;
		}
		Intent intent = new Intent();
		intent.setClass(getApplicationContext(), CfzgCustomDialogForExit.class);
		intent.putExtra("cn", cn);
		startActivityForResult(intent, FlagManagers.CUSTOM_DIALOG_FOR_EXIT);
	}

	/**
	 * @方法名：dialogActivity
	 * @功能说明：弹出紧急报警对话框
	 * @author liums
	 * @date 2013-5-7 下午5:34:08
	 */
	private void dialogActivity() {
		Intent intent = new Intent();

		intent.setClass(getApplicationContext(), CfzgCustomDialog.class);
		intent.putExtra("cn", cn);
		startActivityForResult(intent, FlagManagers.CUSTOM_DIALOG);
	}

	/**
	 * 电池电量低于20%提醒
	 * 
	 * @param nameList
	 */
	private Boolean onNotifyBatteries(int level) {
		Log.i(TAG, "onNotifyTalkBack()");
		Intent intent = new Intent();
		intent.putExtra("type", "batteries");
		intent.putExtra("level", level);
		intent.putExtra("cfzgFlag", true);
		intent.setClass(getApplicationContext(), CfzgPopupActivity.class);
		startActivity(intent);
		BaseApplication.soundManager.onPlaySound(5, 0);
		return true;
	}

	@Override
	protected void onDestroy() {
		Log.i(TAG2, this.getClass().getName() + " onDestroy");
		unregisterReceiver(broadcastReceiver);
		super.onDestroy();
	}

	@Override
	protected void onStart() {
		Log.i(TAG2, this.getClass().getName() + " onStart");
		super.onStart();
	}

	@Override
	protected void onRestart() {
		Log.i(TAG2, this.getClass().getName() + " onRestart");
		super.onRestart();
	}

	@Override
	protected void onPause() {
		Log.i(TAG2, this.getClass().getName() + " onPause");
		flag = false;
		super.onPause();
	}

	@Override
	protected void onStop() {
		Log.i(TAG2, this.getClass().getName() + " onStop");
		super.onStop();
	}

	/**
	 * 
	 * @方法名：updateShipInfo
	 * @功能说明：更新船舶信息
	 * @author liums
	 * @date 2013-10-28 下午3:07:56
	 * @param hc
	 */
	private void updateShipInfo() {
		HashMap<String, Object> bindShip = SystemSetting.getBindShip(CfzgShipListActivity.LIST_TYPE_FROM_SHIPSTATUS + "");
		if (bindShip == null) {
			SystemSetting.restoreBindShipInfo();
			bindShip = SystemSetting.getBindShip(CfzgShipListActivity.LIST_TYPE_FROM_SHIPSTATUS + "");
		}
		if (bindShip == null) {
			return;
		}
		String url = "getShipInfo";
		if (progressDialog != null) {
			return;
		}
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("voyageNumber", (String) bindShip.get("hc")));
		progressDialog = new ProgressDialog(this);
		NetWorkManager.request(this, url, params, HTTPREQUEST_TYPE_FOR_GETDETAIL);
	}
}
