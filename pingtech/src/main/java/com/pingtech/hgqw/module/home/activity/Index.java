package com.pingtech.hgqw.module.home.activity;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.NameValuePair;
import org.xmlpull.v1.XmlPullParser;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.location.LocationManager;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.util.Xml;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.pingtech.R;
import com.pingtech.hgqw.activity.MyActivity;
import com.pingtech.hgqw.activity.PopupActivity;
import com.pingtech.hgqw.activity.SelectShipActivity;
import com.pingtech.hgqw.activity.ShipDetailActivity;
import com.pingtech.hgqw.activity.ShipListActivity;
import com.pingtech.hgqw.activity.SystemActivity;
import com.pingtech.hgqw.activity.UpdateActivity;
import com.pingtech.hgqw.base.BaseApplication;
import com.pingtech.hgqw.entity.FlagUrls;
import com.pingtech.hgqw.entity.Flags;
import com.pingtech.hgqw.entity.GlobalFlags;
import com.pingtech.hgqw.entity.LoginUser;
import com.pingtech.hgqw.entity.UpdataInfo;
import com.pingtech.hgqw.interf.OnHttpResult;
import com.pingtech.hgqw.module.bindship.activity.ShipBind;
import com.pingtech.hgqw.module.cbdt.activity.ShipStatus;
import com.pingtech.hgqw.module.exception.activity.Exceptionlist;
import com.pingtech.hgqw.module.home.service.UpdateSysTime;
import com.pingtech.hgqw.module.home.utils.SkipUtil;
import com.pingtech.hgqw.module.kacbqk.activity.KacbqkShipSearch;
import com.pingtech.hgqw.module.kakou.activity.KakouManager;
import com.pingtech.hgqw.module.login.activity.Login;
import com.pingtech.hgqw.module.login.utils.ActivityJump;
import com.pingtech.hgqw.module.offline.base.utils.DbUtil;
import com.pingtech.hgqw.module.offline.hgzjxx.service.HgzjxxService;
import com.pingtech.hgqw.module.offline.util.OffLineUtil;
import com.pingtech.hgqw.module.police.activity.MyPoliceList;
import com.pingtech.hgqw.module.police.request.RequestPolice;
import com.pingtech.hgqw.module.police.utils.PullXmlMyPolice;
import com.pingtech.hgqw.module.qwjw.fragment.QwzlFragment;
import com.pingtech.hgqw.module.sjcj.activity.BaseInfoMaintenanceActivity;
import com.pingtech.hgqw.module.tikou.activity.TikouManager;
import com.pingtech.hgqw.module.wpjc.activity.GoodsCheckList;
import com.pingtech.hgqw.module.xtgl.activity.FunctionSetting;
import com.pingtech.hgqw.module.xtgl.service.OffDataDownload;
import com.pingtech.hgqw.module.xtgl.service.OffDataDownloadForBd;
import com.pingtech.hgqw.module.xunjian.activity.XunChaXunJian;
import com.pingtech.hgqw.module.xunjian.utils.XcUtil;
import com.pingtech.hgqw.module.yydj.activity.TalkBack;
import com.pingtech.hgqw.mqtt.receiver.MessageReceiver;
import com.pingtech.hgqw.service.AndSerOffLineData;
import com.pingtech.hgqw.service.ListenerService;
import com.pingtech.hgqw.service.SynchDataService;
import com.pingtech.hgqw.utils.BaseInfoData;
import com.pingtech.hgqw.utils.BasicNameValuePair;
import com.pingtech.hgqw.utils.DataDictionary;
import com.pingtech.hgqw.utils.Log;
import com.pingtech.hgqw.utils.RestoreBindShipInfo;
import com.pingtech.hgqw.utils.SoundManager;
import com.pingtech.hgqw.utils.StringUtils;
import com.pingtech.hgqw.utils.SystemSetting;
import com.pingtech.hgqw.utils.UpdataVersionManager;
import com.pingtech.hgqw.web.NetWorkManager;
import com.pingtech.hgqw.web.vpn.VpnManager;
import com.pingtech.hgqw.widget.HgqwToast;

public class Index extends MyActivity implements OnHttpResult {

	private static final String TAG = "Index";

	private Intent listenerServiceIntent;

	/** 船舶动态 */
	private static final int STARTACTIVITY_FOR_SHIPSTATUS = 1;

	/** 梯口管理 */
	private static final int STARTACTIVITY_FOR_TIKOUMANAGER = 2;

	/** 巡查巡检 */
	private static final int STARTACTIVITY_FOR_XUNCHAXUNJIAN = 3;

	/** 快速验放 */
	private static final int STARTACTIVITY_FOR_XCXJKSYF = 29;

	/** 数据字典同步 */
	private static final int STARTACTIVITY_FOR_DICTSYNC = 30;

	/** 发起数据字典同步的http请求type */
	private static final int HTTPREQUEST_TYPE_GET_DD = 70;

	/** 卡口管理 */
	private static final int STARTACTIVITY_FOR_KAKOUMANAGER = 4;

	/** 警务管理 */
	private static final int STARTACTIVITY_FOR_MYPOLICE = 5;

	/** 数据采集 */
	private static final int STARTACTIVITY_FOR_MAINTENANCE = 6;

	/** 语音对讲 */
	private static final int STARTACTIVITY_FOR_TALK_BACK = 100;

	/** 软件升级 */
	private static final int STARTACTIVITY_FOR_SWUPDATE = 600;

	/** 发起软件更新的http请求type */
	private static final int HTTPREQUEST_TYPE_GET_SW_VERSION = 800;

	/** 发起软件更新的http请求type */
	private static final int HTTPREQUEST_TYPE_GET_SW_VERSIONBYHAND = 900;

	/** 系统管理 */
	private static final int STARTACTIVITY_FOR_SYSTEM = 7;

	/** 注销 */
	private static final int STARTACTIVITY_FOR_LOGOUT = 8;

	/** 异常信息 */
	private static final int STARTACTIVITY_FOR_EXCEPTION_INFO = 9;

	/** VPN设置 */
	private static final int CHECK_VPN = 109;

	private static final int QWJW = 110;

	/** 货物检查 */
	private static final int STARTACTIVITY_FOR_GOODS_CHECK = 15;

	/** 口岸船舶情况 */
	private static final int STARTACTIVITY_FOR_KACBQK = 16;

	/** 发起注销的http请求的type */
	private static final int HTTPREQUEST_TYPE_FOR_LOGOUT = 10;

	/** 发起继承绑定的http请求的type */
	private static final int HTTPREQUEST_TYPE_FOR_EXTENDS_BIND = 12;

	/** 发起解除继承绑定的http请求的type */
	private static final int HTTPREQUEST_TYPE_FOR_EXTENDS_UNBIND = 13;

	/** 发起退出应用的http请求的type */
	private static final int HTTPREQUEST_TYPE_FOR_EXIT = 14;

	private SimpleAdapter adpter;

	private int[] gridViewMenuItemId;

	private int[] gridViewMenuItemString;

	private int[] gridViewMenuItemImage;

	private GridView mGridView;

	/** 是否处于发起我的警务流程未返回状态 */
	private static boolean mReceiving = false;

	/** 接收我的警务时间间隔计数器，单位10秒 */
	private static int receiveTaskTimerCount = 0;

	private ProgressDialog progressDialog = null;

	private ProgressDialog progressDialogFoD = null;

	private DialogInterface quesrDialog;

	/** 继承绑定界面的checkbox（船舶动态） */
	private CheckBox box1;

	/** 继承绑定界面的checkbox（梯口） */
	private CheckBox box2;

	/** 继承绑定界面的checkbox（卡口） */
	private CheckBox box3;

	/** 继承绑定界面的checkbox（巡查） */
	private CheckBox box4;

	private String httpReturnXMLInfo = null;

	private Timer timer = null;

	private TimerTask timerTaskGetDD = null;

	private TimerTask timerTaskBaseInfo = null;

	private UpdataInfo info = null;

	private StringBuilder stringBuilderDialog = new StringBuilder();

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case ListenerService.HAVE_NEW_AUDIO:
				onNotifyTalkBack((ArrayList<String>) msg.obj);
				break;
			case VpnManager.START_VPNSERVICE_RESULT_SUCCESS:
				updateVpnDialogMsg("安全通道已连接");
				// login_vpn_status.append("\n" +
				// getString(R.string.login_vpn_status) + "安全通道已连接");
				break;
			case VpnManager.START_VPNSERVICE_RESULT_CONNECT_TIMEOUT:
				updateVpnDialogMsg("安全通道连接超时");
				// login_vpn_status.append("\n" +
				// getString(R.string.login_vpn_status) + "安全通道连接超时");
				break;
			case VpnManager.START_VPNSERVICE_RESULT_START_FAILED:
				updateVpnDialogMsg("安全客户端启动失败");
				// login_vpn_status.append("\n" +
				// getString(R.string.login_vpn_status) + "安全客户端启动失败");
				break;
			case VpnManager.START_VPNSERVICE_RESULT_CONNECT_FAILED:
				updateVpnDialogMsg("安全通道连接失败");
				// login_vpn_status.append("\n" +
				// getString(R.string.login_vpn_status) + "安全通道连接失败");
				break;
			case VpnManager.START_VPNSERVICE_RESULT_START_TIMEOUT:
				updateVpnDialogMsg("安全通道连接超时");
				// login_vpn_status.append("\n" +
				// getString(R.string.login_vpn_status) + "安全通道连接超时");
				break;
			case VpnManager.START_VPNSERVICE_RESULT_NOT_FOUNT:
				updateVpnDialogMsg(getString(R.string.start_vpnservice_result_not_fount));
				// login_vpn_status.append("\n" +
				// getString(R.string.login_vpn_status) +
				// getString(R.string.start_vpnservice_result_not_fount));
				break;
			case VpnManager.START_VPNSERVICE_RESULT_START_SUCCESS:
				updateVpnDialogMsg("安全客户端启动成功");
				updateVpnDialogMsg("正在建立安全通道");
				// login_vpn_status.append("\n" +
				// getString(R.string.login_vpn_status) + "安全客户端启动成功");
				// login_vpn_status.append("\n" +
				// getString(R.string.login_vpn_status) + "正在建立安全通道");
				break;
			case VpnManager.START_VPNSERVICE_RESULT_CONNECT_SUCCESS:
				updateVpnDialogMsg("安全通道已连接");
				// login_vpn_status.append("\n" +
				// getString(R.string.login_vpn_status) + "安全通道已连接");
				break;
			case OffDataDownload.WHAT_DOWNLOAD_SUCCESS_ONE:// 下载完成一个
				// 码头、泊位、区域、船舶、证件、船员
				String str = dataDownload.mapString.get(msg.arg1);
				stringBuilderDialog.append(str + "，下载完成");
				stringBuilderDialog.append("\n");
				// if(progressDialog!=null){
				// progressDialog.setMessage(stringBuilderDialog.toString());
				// }
				break;
			case OffDataDownload.WHAT_DOWNLOAD_ONE_RESULT_NULL:// 下载失败一个
			case OffDataDownload.WHAT_INSERT_DATA_FAILED_ONE:// 下载失败一个
				// 码头、泊位、区域、船舶、证件、船员
				String str1 = dataDownload.mapString.get(msg.arg1);
				stringBuilderDialog.append(str1 + "，下载失败");
				stringBuilderDialog.append("\n");
				if (progressDialogFoD != null) {
					// progressDialogFoD.setMessage(stringBuilderDialog.toString());
				}
				break;
			case OffDataDownload.WHAT_DOWNLOAD_ALL_SUCCESS:// 下载完成
				HgqwToast.toast("下载完成");
				if (progressDialogFoD != null) {
					progressDialogFoD.dismiss();
					progressDialogFoD = null;
				}
				// 启动后台定时更新服务
				startSynchDataService();
				break;
			case RequestPolice.HTTPREQUEST_TYPE_FOR_RECEIVE_MY_TASK:
			case RequestPolice.HTTPREQUEST_TYPE_FOR_RECEIVE_MY_TASK_QWZL:
				receive_my_task((String) msg.obj, msg.what);
				break;
			default:
				break;
			}

		}

	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		switch (Flags.PDA_VERSION) {
		case Flags.PDA_VERSION_DEFAULT:
			gridViewMenuItemId = new int[] { STARTACTIVITY_FOR_SHIPSTATUS,
					STARTACTIVITY_FOR_XUNCHAXUNJIAN,
					STARTACTIVITY_FOR_XCXJKSYF, STARTACTIVITY_FOR_KACBQK,
					STARTACTIVITY_FOR_MYPOLICE, QWJW,
					STARTACTIVITY_FOR_EXCEPTION_INFO,
					STARTACTIVITY_FOR_TALK_BACK, STARTACTIVITY_FOR_GOODS_CHECK,
					CHECK_VPN };

			gridViewMenuItemString = new int[] { R.string.ShipStatus,
					R.string.xunchaxunjian, R.string.quickCheck,
					R.string.kacbqk, R.string.mypolice, R.string.qwjw,
					R.string.exception_info, R.string.talkbackstr,
					R.string.Goods_check, R.string.check_vpn };

			gridViewMenuItemImage = new int[] { R.drawable.shipstatus,
					R.drawable.xunchaxunjian, R.drawable.quickcheck,
					R.drawable.kacbqk, R.drawable.mypolice, R.drawable.wdqw,
					R.drawable.exceptioninfo, R.drawable.talkback,
					R.drawable.goodsckeck, R.drawable.check_network };

			super.onCreate(savedInstanceState, R.layout.main);
			findViewById(R.id.image_datacollect).setVisibility(View.VISIBLE);
			findViewById(R.id.textview_datacollect).setVisibility(View.VISIBLE);
			findViewById(R.id.image_systemmanage).setVisibility(View.VISIBLE);
			findViewById(R.id.textview_systemmanage)
					.setVisibility(View.VISIBLE);
			break;
		case Flags.PDA_VERSION_SENTINEL:
			gridViewMenuItemId = new int[] { STARTACTIVITY_FOR_SHIPSTATUS,
					STARTACTIVITY_FOR_TIKOUMANAGER,
					STARTACTIVITY_FOR_KAKOUMANAGER, STARTACTIVITY_FOR_KACBQK,
					STARTACTIVITY_FOR_MYPOLICE, QWJW,
					STARTACTIVITY_FOR_EXCEPTION_INFO,
					STARTACTIVITY_FOR_GOODS_CHECK, STARTACTIVITY_FOR_TALK_BACK,
					CHECK_VPN };

			gridViewMenuItemString = new int[] { R.string.ShipStatus,
					R.string.tikoumanager, R.string.kakoumanager,
					R.string.kacbqk, R.string.mypolice, R.string.qwjw,
					R.string.exception_info, R.string.Goods_check,
					R.string.talkbackstr, R.string.check_vpn };

			gridViewMenuItemImage = new int[] { R.drawable.shipstatus,
					R.drawable.tikoumanager, R.drawable.kakoumanager,
					R.drawable.kacbqk, R.drawable.mypolice, R.drawable.wdqw,
					R.drawable.exceptioninfo, R.drawable.goodsckeck,
					R.drawable.talkback, R.drawable.check_network };
			super.onCreate(savedInstanceState, R.layout.main_sentinel);
			findViewById(R.id.image_systemmanage).setVisibility(View.VISIBLE);
			findViewById(R.id.textview_systemmanage)
					.setVisibility(View.VISIBLE);
			break;
		case Flags.PDA_VERSION_BATE:
			gridViewMenuItemId = new int[] { STARTACTIVITY_FOR_SHIPSTATUS,
					STARTACTIVITY_FOR_TIKOUMANAGER,
					STARTACTIVITY_FOR_KAKOUMANAGER, STARTACTIVITY_FOR_KACBQK,
					STARTACTIVITY_FOR_XUNCHAXUNJIAN,
					STARTACTIVITY_FOR_EXCEPTION_INFO,
					STARTACTIVITY_FOR_MYPOLICE, STARTACTIVITY_FOR_GOODS_CHECK,
					STARTACTIVITY_FOR_TALK_BACK };

			gridViewMenuItemString = new int[] { R.string.ShipStatus,
					R.string.tikoumanager, R.string.kakoumanager,
					R.string.xunchaxunjian, R.string.exception_info,
					R.string.mypolice, R.string.Goods_check,
					R.string.talkbackstr, R.string.kacbqk };

			gridViewMenuItemImage = new int[] { R.drawable.shipstatus,
					R.drawable.tikoumanager, R.drawable.kakoumanager,
					R.drawable.xunchaxunjian, R.drawable.exceptioninfo,
					R.drawable.mypolice, R.drawable.goodsckeck,
					R.drawable.talkback, R.drawable.kacbqk };
			super.onCreate(savedInstanceState, R.layout.main_sentinel);
			findViewById(R.id.image_datacollect).setVisibility(View.VISIBLE);
			findViewById(R.id.textview_datacollect).setVisibility(View.VISIBLE);
			findViewById(R.id.image_systemmanage).setVisibility(View.VISIBLE);
			findViewById(R.id.textview_systemmanage)
					.setVisibility(View.VISIBLE);
			break;
		default:
			gridViewMenuItemId = new int[] { STARTACTIVITY_FOR_SHIPSTATUS,
					STARTACTIVITY_FOR_XUNCHAXUNJIAN,
					STARTACTIVITY_FOR_MYPOLICE,
					STARTACTIVITY_FOR_EXCEPTION_INFO,
					STARTACTIVITY_FOR_TALK_BACK, STARTACTIVITY_FOR_KACBQK };

			gridViewMenuItemString = new int[] { R.string.ShipStatus,
					R.string.xunchaxunjian, R.string.mypolice,
					R.string.exception_info, R.string.talkbackstr,
					R.string.Goods_check };

			gridViewMenuItemImage = new int[] { R.drawable.shipstatus,
					R.drawable.xunchaxunjian, R.drawable.mypolice,
					R.drawable.exceptioninfo, R.drawable.talkback,
					R.drawable.kacbqk };
			super.onCreate(savedInstanceState, R.layout.main);
			findViewById(R.id.image_datacollect).setVisibility(View.VISIBLE);
			findViewById(R.id.textview_datacollect).setVisibility(View.VISIBLE);
			findViewById(R.id.image_systemmanage).setVisibility(View.VISIBLE);
			findViewById(R.id.textview_systemmanage)
					.setVisibility(View.VISIBLE);
			break;
		}
		findViewById(R.id.textview_logout).setVisibility(View.VISIBLE);
		findViewById(R.id.image_logout).setVisibility(View.VISIBLE);

		Log.i(TAG, "onCreate()");

		setMyActiveTitle(R.string.app_name_all);
		mGridView = (GridView) findViewById(R.id.gridView1);
		mGridView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Intent intent = new Intent();
				HashMap<String, Object> Binddata = SystemSetting
						.getBindShip(GlobalFlags.LIST_TYPE_FROM_XUNCHAXUNJIAN
								+ "");
				HashMap<String, Object> tikoumBinddata = SystemSetting
						.getBindShip(GlobalFlags.LIST_TYPE_FROM_TIKOUMANAGER
								+ "");
				int permission = LoginUser.getCurrentLoginUser()
						.getPermission();
				switch (gridViewMenuItemId[arg2]) {
				case QWJW:
					SkipUtil.skipToQwjw(Index.this);
					return;
				case CHECK_VPN:
					reCheckVpn();
					return;
				case STARTACTIVITY_FOR_SHIPSTATUS:
					if (permission == -1) {
						HgqwToast.makeText(Index.this,
								getString(R.string.no_permission),
								HgqwToast.LENGTH_LONG).show();
						return;
					}
					intent.setClass(getApplicationContext(), ShipStatus.class);
					startActivity(intent);
					return;
				case STARTACTIVITY_FOR_TIKOUMANAGER:
					if (permission == -1) {
						HgqwToast.makeText(Index.this,
								getString(R.string.no_permission),
								HgqwToast.LENGTH_LONG).show();
						return;
					}
					intent.setClass(getApplicationContext(), TikouManager.class);
					startActivity(intent);
					return;
				case STARTACTIVITY_FOR_XUNCHAXUNJIAN:
					if (permission == -1) {
						HgqwToast.makeText(Index.this,
								getString(R.string.no_permission),
								HgqwToast.LENGTH_LONG).show();
						return;
					}
					intent.setClass(getApplicationContext(),
							XunChaXunJian.class);
					startActivity(intent);
					return;
				case STARTACTIVITY_FOR_MYPOLICE:
					if (permission == -1) {
						HgqwToast.makeText(Index.this,
								getString(R.string.no_permission),
								HgqwToast.LENGTH_LONG).show();
						return;
					}
					autoReceiveMyTask(true);
					intent.setClass(getApplicationContext(), MyPoliceList.class);
					startActivity(intent);
					return;
				case STARTACTIVITY_FOR_KAKOUMANAGER:
					if (permission == -1) {
						HgqwToast.makeText(Index.this,
								getString(R.string.no_permission),
								HgqwToast.LENGTH_LONG).show();
						return;
					}
					intent.setClass(getApplicationContext(), KakouManager.class);
					startActivity(intent);
					return;
				case STARTACTIVITY_FOR_SYSTEM:
					intent.setClass(getApplicationContext(),
							SystemActivity.class);
					startActivityForResult(intent, STARTACTIVITY_FOR_SYSTEM);
					return;
				case STARTACTIVITY_FOR_LOGOUT:
					onLogOutRequest(false);
					return;
				case STARTACTIVITY_FOR_EXCEPTION_INFO:
					if (permission == -1) {
						HgqwToast.makeText(Index.this,
								getString(R.string.no_permission),
								HgqwToast.LENGTH_LONG).show();
						return;
					}
					switch (Flags.PDA_VERSION) {
					case Flags.PDA_VERSION_DEFAULT:
						intent.putExtra("from", "03");
						break;
					case Flags.PDA_VERSION_SENTINEL:
						intent.putExtra("from", "02");
						break;
					default:
						intent.putExtra("from", "03");
						break;
					}
					intent.putExtra("fromMain", "01");
					intent.setClass(getApplicationContext(),
							Exceptionlist.class);
					startActivity(intent);
					return;
				case STARTACTIVITY_FOR_MAINTENANCE:
					if (permission == -1) {
						HgqwToast.makeText(Index.this,
								getString(R.string.no_permission),
								HgqwToast.LENGTH_LONG).show();
						return;
					}
					intent.setClass(getApplicationContext(),
							BaseInfoMaintenanceActivity.class);
					startActivity(intent);
					return;
				case STARTACTIVITY_FOR_TALK_BACK:// 语音对讲
					if (permission == -1) {
						HgqwToast.makeText(Index.this,
								getString(R.string.no_permission),
								HgqwToast.LENGTH_LONG).show();
						return;
					}
					intent.setClass(getApplicationContext(), TalkBack.class);
					startActivity(intent);
					return;
				case STARTACTIVITY_FOR_SWUPDATE:
					if (permission == -1) {
						HgqwToast.makeText(Index.this,
								getString(R.string.no_permission),
								HgqwToast.LENGTH_LONG).show();
						return;
					}
					progressDialog = new ProgressDialog(Index.this);
					progressDialog.setTitle(getString(R.string.swupdate));
					progressDialog.setMessage(getString(R.string.waiting));
					progressDialog.setCancelable(false);
					progressDialog.setIndeterminate(false);
					progressDialog.show();
					String strCheckUpdate = "checkUpdate";
					List<NameValuePair> paramsCheckUpdate = new ArrayList<NameValuePair>();
					paramsCheckUpdate.add(new BasicNameValuePair("type", "0"));
					// 版本：0默认版本，1哨兵版，2船方自管
					paramsCheckUpdate.add(new BasicNameValuePair("version",
							Flags.PDA_VERSION + ""));
					NetWorkManager.request(Index.this, strCheckUpdate,
							paramsCheckUpdate,
							HTTPREQUEST_TYPE_GET_SW_VERSIONBYHAND);
					return;

				case STARTACTIVITY_FOR_DICTSYNC:// 数据字典同步
					if (permission == -1) {
						HgqwToast.makeText(Index.this,
								getString(R.string.no_permission),
								HgqwToast.LENGTH_LONG).show();
						return;
					}
					if (progressDialog != null) {
						return;
					}
					String str = "getDD";
					List<NameValuePair> params = new ArrayList<NameValuePair>();
					progressDialog = new ProgressDialog(Index.this);
					progressDialog.setTitle(getString(R.string.syncdata));
					progressDialog.setMessage(getString(R.string.waiting));
					progressDialog.setCancelable(false);
					progressDialog.setIndeterminate(false);
					progressDialog.show();
					NetWorkManager.request(Index.this, str, params,
							HTTPREQUEST_TYPE_GET_DD);
					return;
				case STARTACTIVITY_FOR_KACBQK:// 口岸船舶
					Intent intentKacbqk = new Intent();
					intentKacbqk.setClass(getApplicationContext(),
							KacbqkShipSearch.class);
					startActivity(intentKacbqk);
					return;
				case STARTACTIVITY_FOR_XCXJKSYF:
					
					// 已经绑定地点，提示先解绑
					if (StringUtils.isNotEmpty(SystemSetting.xunJianId)) {
						HgqwToast.makeText(Index.this,
								R.string.already_bindplace,
								HgqwToast.LENGTH_LONG).show();
						return;
					}
					if (Binddata != null) {
						intent.putExtra("hc", (String) Binddata.get("hc"));
						intent.putExtra("kacbqkid",
								(String) Binddata.get("kacbqkid"));
						intent.putExtra("cbzwm", (String) Binddata.get("cbzwm"));
						intent.putExtra("jcfl", (String) Binddata.get("jcfl"));
						intent.putExtra("cbywm", (String) Binddata.get("cbywm"));
						intent.putExtra("gj", (String) Binddata.get("gj"));
						intent.putExtra("cbxz", (String) Binddata.get("cbxz"));
						intent.putExtra("tkwz", (String) Binddata.get("tkwz"));
						intent.putExtra("cdgs", (String) Binddata.get("cdgs"));
						// intent.putExtra("cys", (String) Binddata.get("cys"));
						intent.putExtra("dlcys", (String) Binddata.get("dlcys"));
						intent.putExtra("dlrys", (String) Binddata.get("dlrys"));
						intent.putExtra("bdzt", (String) Binddata.get("bdzt"));
						intent.putExtra("kacbzt",
								(String) Binddata.get("kacbzt"));
						intent.putExtra("from",
								GlobalFlags.LIST_TYPE_FROM_XUNCHAXUNJIAN);
						intent.putExtra("fromxunchaxunjian", true);
						intent.putExtra("frombindship", true);
						intent.putExtra("saveXjBtnFlag", true);
						intent.putExtra("fromXCXJkshc", true);
						intent.setClass(getApplicationContext(),
								ShipDetailActivity.class);
						startActivity(intent);
						return;
					} else {
						if (SystemSetting
								.getBindShipAllSize(GlobalFlags.LIST_TYPE_FROM_XUNCHAXUNJIAN) > 0) {
							onShowQuestFromBindListDialog();
						} else {
							if (BaseApplication.instent.getWebState()) {
								// 01卡口、02梯口、03巡查巡检、04查询人员模块、05船舶动态、0501船舶动态>>>船舶绑定、0201梯口管理>>>船舶绑定、0101卡口管理>>>船舶绑定、0301巡查巡检>>>船舶绑定

								intent.putExtra("frombindship", true);
								intent.putExtra("bindtype",GlobalFlags.LIST_TYPE_FROM_XUNCHAXUNJIAN);
								intent.putExtra("fromxuncha", true);
								intent.putExtra("fromXCXJkshc", true);
								intent.setClass(getApplicationContext(),SelectShipActivity.class);
								startActivity(intent);
							} else {
								HgqwToast.makeText(getApplicationContext(),
										R.string.no_web_cannot_bind_place,
										HgqwToast.LENGTH_LONG).show();
							}
						}
						return;
					}
				case STARTACTIVITY_FOR_GOODS_CHECK:
					switch (Flags.PDA_VERSION) {
					case Flags.PDA_VERSION_DEFAULT:
						if (Binddata == null) {
							HgqwToast.makeText(Index.this,
									R.string.xc_no_bindplace,
									HgqwToast.LENGTH_LONG).show();
							return;
						}
						intent.putExtra("hc", (String) Binddata.get("hc"));
						intent.putExtra("voyagemc",
								(String) Binddata.get("cbzwm"));
						intent.putExtra("kacbqkid",
								(String) Binddata.get("kacbqkid"));
						break;
					case Flags.PDA_VERSION_SENTINEL:
						if (tikoumBinddata == null
								&& SystemSetting.shipOfKK == null) {
							HgqwToast.makeText(Index.this,
									R.string.sb_no_ship_place,
									HgqwToast.LENGTH_LONG).show();
							return;
						}
						break;
					case Flags.PDA_VERSION_BATE:
						break;
					default:
						if (Binddata == null) {
							HgqwToast
									.makeText(Index.this, R.string.no_bindship,
											HgqwToast.LENGTH_LONG).show();
							return;
						}
						intent.putExtra("hc", (String) Binddata.get("hc"));
						intent.putExtra("voyagemc",
								(String) Binddata.get("cbzwm"));
						intent.putExtra("kacbqkid",
								(String) Binddata.get("kacbqkid"));
						break;
					}

					intent.setClass(getApplicationContext(),
							GoodsCheckList.class);
					startActivity(intent);
					return;
				}
			}

		});
		onUpdateGridViewMenu();
		/*
		 * File data = new
		 * File(Environment.getExternalStorageDirectory().getPath() +
		 * File.separator + "pingtech" + File.separator + "datadict.xml"); if
		 * (!data.exists()) { HgqwToast.makeText(Index.this,
		 * getString(R.string.no_datadict), HgqwToast.LENGTH_LONG).show(); }
		 */
		if (LoginUser.getCurrentLoginUser() == null) {
			return;
		}
		if (Login.ADMINISTRATOR.equals(LoginUser.getCurrentLoginUser()
				.getUserName())) {
			return;
		}
		SharedPreferences prefs = getSharedPreferences(
				getString(R.string.app_name), MODE_PRIVATE);
		SystemSetting.setYydjOnOrOff(prefs.getBoolean("yydj", false));

		otherBusiness();// 继承绑定
		initData();// 后台程序
		initAndSer();// 初始化后台监听
		// openMqttService();

		// 开启离线设置为false(取消'开启离线功能')
		setState(FunctionSetting.kqlx, false);
	}

	/** 进入船舶绑定时，如果存在快速绑定条件，提示是否选择快速绑定 */
	private void onShowQuestFromBindListDialog() {
		// TODO Auto-generated method stub
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(R.string.query_from_bindlist);
		builder.setTitle(R.string.info);
		builder.setPositiveButton(R.string.yes,
				new AlertDialog.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						Intent intent = new Intent();
						intent.putExtra("title", getString(R.string.bindShip));
						intent.putExtra("cardNumber",
								ShipListActivity.FROM_BINDLIST);
						intent.putExtra("from",
								GlobalFlags.LIST_TYPE_FROM_XUNCHAXUNJIAN);
						intent.setClass(getApplicationContext(),
								ShipListActivity.class);
						startActivity(intent);
					}
				});
		builder.setNegativeButton(R.string.no,
				new AlertDialog.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						Intent intent = new Intent();
						// 01卡口、02梯口、03巡查巡检、04查询人员模块、05船舶动态、0501船舶动态>>>船舶绑定、0201梯口管理>>>船舶绑定、0101卡口管理>>>船舶绑定、0301巡查巡检>>>船舶绑定
						intent.putExtra("from",
								GlobalFlags.BINDSHIP_FROM_XUNCHAXUNJIAN);
						intent.setClass(getApplicationContext(), ShipBind.class);
						startActivity(intent);
					}
				});
		builder.create().show();
	}

	ServiceConnection connectionForSync = null;

	protected void startSynchDataService() {
		Intent intent = new Intent(Index.this, SynchDataService.class);
		connectionForSync = new ServiceConnection() {

			@Override
			public void onServiceDisconnected(ComponentName name) {
			}

			@Override
			public void onServiceConnected(ComponentName name, IBinder service) {

			}
		};
		bindService(intent, connectionForSync, Context.BIND_AUTO_CREATE);
	}

	/**
	 * 
	 * @方法名：reCheckVpn
	 * @功能说明：检测网络，重新连接vpn
	 * @author liums
	 * @date 2014-3-3 上午11:40:06
	 */
	private VpnManager vpnManager = null;

	protected void reCheckVpn() {
		vpnManager = new VpnManager(handler, this);
		Log.i(TAG, "onResume,status=" + vpnManager.getStatus());
		vpnManager.init();
		buildVpnDialog("开始检测VPN状态");
	}

	ProgressDialog progressDialogForVpn = null;

	private StringBuilder stringBuilder = null;

	private View dialog = null;

	private TextView msg = null;

	@SuppressWarnings("deprecation")
	private void buildVpnDialog(String info) {
		Log.i(TAG, "buildVpnDialog");
		stringBuilder = new StringBuilder();
		stringBuilder.append(info);
		stringBuilder.append("\n");
		progressDialogForVpn = new ProgressDialog(this);
		progressDialogForVpn.setTitle("正在检测网络");
		progressDialogForVpn.setMessage(stringBuilder.toString());
		progressDialogForVpn.setButton("确定", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		progressDialogForVpn.setButton2("取消", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		progressDialogForVpn.setCanceledOnTouchOutside(false);
		progressDialogForVpn.setCancelable(false);
		progressDialogForVpn.show();
		// dialog = LayoutInflater.from(this).inflate(R.layout.vpn_check_view,
		// null);
		// msg = (TextView) dialog.findViewById(R.id.message);
		// msg.setText(stringBuilder.toString());
		// progressDialogForVpn.setView(dialog);
	}

	private void updateVpnDialogMsg(String info) {
		Log.i(TAG, "updateVpnDialogMsg");
		if (stringBuilder != null && progressDialogForVpn != null) {
			stringBuilder.append(info);
			stringBuilder.append("\n");
			Log.i(TAG, "***" + stringBuilder.toString());
			progressDialogForVpn.setMessage(stringBuilder.toString());
		}
	}

	private void initData() {
		stopTimer();
		timer = new Timer();
		timerTaskGetDD = new TimerTask() {
			@Override
			public void run() {
				Log.i(TAG, "timerTaskGetDD run");
				getDD();// 后台自动更新数据字典
			}
		};
		// 延迟十秒加载数据字典
		timer.schedule(timerTaskGetDD, 1 * 12 * 1000);

		timerTaskBaseInfo = new TimerTask() {
			@Override
			public void run() {
				Log.i(TAG, "timerTaskBaseInfo run");
				BaseInfoData.onRequestBaseInfoData(null);
			}
		};
		// 延迟十秒加载数据字典
		timer.schedule(timerTaskBaseInfo, 1 * 2 * 1000);
	}

	/**
	 * 
	 * @方法名：initAndSer
	 * @功能说明：绑定后台服务
	 * @author liums
	 * @date 2013-9-27 下午2:38:13
	 */
	private void initAndSer() {
		this.registerReceiver(broadcastReceiver, new IntentFilter(
				Intent.ACTION_BATTERY_CHANGED));
		// 如果推送开关不打开，则开启轮询
		if (!getState(FunctionSetting.tskg, false)) {
			autoReceiveMyTask(true);// 定时接收我的警务
			// 语音对讲定时接收服务
			switch (Flags.PDA_VERSION) {
			case Flags.PDA_VERSION_DEFAULT:
			case Flags.PDA_VERSION_SENTINEL:
				startListen();
				break;
			}
		}

		// GPS定位，离线数据上传
		stopService(new Intent("com.pingtech.PINGTECH_SERVICE"));
		startService(new Intent("com.pingtech.PINGTECH_SERVICE"));// 启动后台GPS定位服务
		if (Flags.IF_OPEN_OFFLINE_MODULE) {
			Intent intent = new Intent(Index.this, AndSerOffLineData.class);
			stopService(intent);
			SystemClock.sleep(200);
			startService(intent);
		}

	}

	private void startListen() {
		listenerServiceIntent = new Intent("com.pingtech.LISTENER_SERVICE");
		bindService(listenerServiceIntent, conn, Context.BIND_AUTO_CREATE);
		new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					if (ls != null) {
						ls.setOnHttpResult(Index.this);
						ls.startListen(handler);
						break;
					}
				}
			}

		}).start();
	}

	/** 显示主菜单 */
	private void onUpdateGridViewMenu() {
		ArrayList<HashMap<String, Object>> lst = new ArrayList<HashMap<String, Object>>();
		for (int i = 0; i < gridViewMenuItemString.length; i++) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("itemImage", gridViewMenuItemImage[i]);
			map.put("itemText",
					this.getResources().getString(gridViewMenuItemString[i]));
			lst.add(map);
		}

		switch (Flags.PDA_VERSION) {
		case Flags.PDA_VERSION_DEFAULT:
			// 全版本
			adpter = new SimpleAdapter(this, lst, R.layout.mainmenu_item,
					new String[] { "itemImage", "itemText" }, new int[] {
							R.id.imageView_ItemImage, R.id.textView_ItemText });

			break;
		case Flags.PDA_VERSION_SENTINEL:
			adpter = new SimpleAdapter(this, lst,
					R.layout.main_sentinel_menu_item, new String[] {
							"itemImage", "itemText" }, new int[] {
							R.id.imageView_ItemImage, R.id.textView_ItemText });
			break;
		default:
			// 全版本
			adpter = new SimpleAdapter(this, lst, R.layout.mainmenu_item,
					new String[] { "itemImage", "itemText" }, new int[] {
							R.id.imageView_ItemImage, R.id.textView_ItemText });
			break;
		}
		mGridView.setAdapter(adpter);
	}

	/**
	 * 验证pda版本升级
	 */
	private void checkVersionUpdate() {
		// 如果离线，则不验证版本
		if (!BaseApplication.instent.getWebState()) {
			return;
		}

		// 系统版本升级
		String strCheckUpdate = "checkUpdate";
		List<NameValuePair> paramsCheckUpdate = new ArrayList<NameValuePair>();
		paramsCheckUpdate.add(new BasicNameValuePair("type", "0"));
		// 版本：0默认版本，1哨兵版，2船方自管
		paramsCheckUpdate.add(new BasicNameValuePair("version",
				Flags.PDA_VERSION + ""));
		NetWorkManager.request(Index.this, strCheckUpdate, paramsCheckUpdate,
				HTTPREQUEST_TYPE_GET_SW_VERSION);

	}

	/**
	 * 注销请求
	 * 
	 * @param exit
	 *            是否退出
	 * */
	private void onLogOutRequest(boolean exit) {
		if (Login.ADMINISTRATOR.equals(LoginUser.getCurrentLoginUser()
				.getUserName())) {
			LoginUser.SetCurrentLoginUser(null);
			if (!exit) {
				Intent intent = new Intent();
				intent.setClass(getApplicationContext(), Login.class);
				startActivity(intent);
			}
			this.finish();
		} else {
			// if (!BaseApplication.instent.getWebState()) {
			// HgqwToast.toast(getApplicationContext(),
			// getString(R.string.no_web_cannot_logout), HgqwToast.LENGTH_LONG);
			// return;
			// }

			String str = "doLoginOut";
			if (progressDialog != null) {
				return;
			}
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			SharedPreferences prefs = BaseApplication.instent
					.getSharedPreferences(BaseApplication.instent
							.getString(R.string.app_name), Context.MODE_PRIVATE);
			String userName = prefs.getString("userName", LoginUser
					.getCurrentLoginUser().getUserName());
			params.add(new BasicNameValuePair("userName", userName));
			params.add(new BasicNameValuePair("dutyid", LoginUser
					.getCurrentLoginUser().getzqjlid()));
			params.add(new BasicNameValuePair("PDACode", SystemSetting
					.getPDACode()));
			// progressDialog = new ProgressDialog(this);
			// progressDialog.setTitle(getString(R.string.logouting));
			// progressDialog.setMessage(getString(R.string.waiting));
			// progressDialog.setCancelable(false);
			// progressDialog.setIndeterminate(false);
			// progressDialog.show();
			if (exit) {
				NetWorkManager.request(this, str, params,
						HTTPREQUEST_TYPE_FOR_EXIT);
			} else {
				NetWorkManager.request(this, str, params,
						HTTPREQUEST_TYPE_FOR_LOGOUT);
			}
			Intent intent = new Intent();
			intent.setClass(getApplicationContext(), Login.class);
			startActivity(intent);
			finish();
		}
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
		if ((keyCode >= KeyEvent.KEYCODE_0 && keyCode <= KeyEvent.KEYCODE_9)
				|| (keyCode == KeyEvent.KEYCODE_ENTER)) {
			return true;
		}
		return super.dispatchKeyEvent(event);
	}

	/** 显示是否退出的dialog */
	private void onShowExitQuestDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(R.string.quest_quit);
		builder.setTitle(R.string.info);
		builder.setPositiveButton(R.string.ok,
				new AlertDialog.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						onLogOutRequest(true);
					}
				});
		builder.setNegativeButton(R.string.cancel,
				new AlertDialog.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		builder.create().show();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.i(TAG, "onActivityResult");
		switch (requestCode) {
		case STARTACTIVITY_FOR_SYSTEM:
			Log.i(TAG, "onActivityResult , STARTACTIVITY_FOR_SYSTEM");
			if (resultCode == RESULT_OK) {
				LoginUser.SetCurrentLoginUser(null);
				Intent intent = new Intent();
				intent.setClass(getApplicationContext(), Login.class);
				startActivity(intent);
				this.finish();
			}
			break;
		case STARTACTIVITY_FOR_SWUPDATE:
			Log.i(TAG, "onActivityResult , STARTACTIVITY_FOR_SWUPDATE");
			if (resultCode == RESULT_OK) {
				Log.i(TAG, "onActivityResult , RESULT_OK");
				break;
			}

			if (data != null) {
				Log.i(TAG, "onActivityResult , data != null");
				boolean isOk = data.getBooleanExtra("isOk", true);
				boolean force = data.getBooleanExtra("force", false);// 强制升级标识
				if (!isOk && force) {// 强制升级版本，下载未成功
					updateAgain();
				}
			} else {
				Log.i(TAG, "onActivityResult , data == null");
				updateAgain();
			}
			break;
		default:
			break;
		}

	}

	/**
	 * 
	 * @方法名：updateError
	 * @功能说明：强制升级版本，下载未成功
	 * @author liums
	 * @param data
	 * @date 2014-1-20 下午3:12:43
	 */
	private void updateAgain() {
		Intent intent = new Intent();
		intent.setClass(Index.this, UpdateActivity.class);
		intent.putExtra("Description", info.getDescription());
		intent.putExtra("Size", info.getSize());
		intent.putExtra("Url", info.getUrl());
		intent.putExtra("version", Flags.PDA_VERSION + "");
		intent.putExtra("force", info.getForce());
		intent.putExtra("forceInfo", getString(R.string.update_error_restart));
		startActivityForResult(intent, STARTACTIVITY_FOR_SWUPDATE);
	}

	/**
	 * 接收我的警务，暂定30秒收一次，如果需要修改，修改int receiveTasktimer = 30;即可
	 * 
	 * @param force
	 *            是否（时间未到）强制接收，比如进入我的警务界面就需要强制接收
	 * 
	 * */
	private void autoReceiveMyTask(boolean force) {
		if (LoginUser.getCurrentLoginUser() == null) {
			return;
		}
		if (Login.ADMINISTRATOR.equals(LoginUser.getCurrentLoginUser()
				.getUserName())) {
			return;
		}

		receiveTaskTimerCount++;
		if (mReceiving) {
			return;
		}
		// int receiveTasktimer = 10;
		int receiveTasktimer = 30;
		if (UpdateActivity.downloading
				|| (!force && (receiveTasktimer > receiveTaskTimerCount * 10))) {
			if (handler != null) {
				handler.postDelayed(autoReceiveTaskRunnable, 10000);
			}
			return;
		}
		if (force) {
			if (handler != null) {
				handler.removeCallbacks(autoReceiveTaskRunnable);
			}
		}
		mReceiving = true;
		new RequestPolice(this, handler)
				.requestReceiveMyTask(RequestPolice.HTTPREQUEST_TYPE_FOR_RECEIVE_MY_TASK);
		receiveTaskTimerCount = 0;
	}

	/** 解析注销请求返回的数据 */
	private boolean onParseXMLDataLogOut(String str) {
		boolean success = false;
		try {
			XmlPullParser parser = Xml.newPullParser();
			parser.setInput(new ByteArrayInputStream(str.getBytes()), "utf-8");// 设置解析的数据源
			int type = parser.getEventType();
			String text = null;
			httpReturnXMLInfo = null;
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
						httpReturnXMLInfo = parser.nextText();
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
	public void onHttpResult(String str, int httpRequestType) {

		boolean ret = (str != null);
		Log.i(TAG, "onHttpResult()httpRequestType:" + httpRequestType
				+ ",result" + ret);
		if (httpRequestType == HTTPREQUEST_TYPE_FOR_LOGOUT
				|| httpRequestType == HTTPREQUEST_TYPE_FOR_EXIT) {
			// if (progressDialog != null) {
			// progressDialog.dismiss();
			// progressDialog = null;
			// }
			// if (str != null) {
			// if (onParseXMLDataLogOut(str)) {
			// LoginUser.SetCurrentLoginUser(null);
			// if (httpRequestType == HTTPREQUEST_TYPE_FOR_LOGOUT) {// 注销启动登录页面
			// Intent intent = new Intent();
			// intent.setClass(getApplicationContext(), Login.class);
			// startActivity(intent);
			// } else if (httpRequestType == HTTPREQUEST_TYPE_FOR_EXIT) {
			// VpnManager.close();
			// }
			// closeMqttService();
			// SystemSetting.destroy();
			// this.finish();
			// } else {
			// if (httpReturnXMLInfo != null) {
			// HgqwToast.makeText(Index.this, httpReturnXMLInfo,
			// HgqwToast.LENGTH_LONG).show();
			// } else {
			// HgqwToast.makeText(Index.this, R.string.logout_failure,
			// HgqwToast.LENGTH_LONG).show();
			// }
			// }
			// } else {
			// HgqwToast.makeText(Index.this, R.string.logout_failure,
			// HgqwToast.LENGTH_LONG).show();
			// }
		} else if (HTTPREQUEST_TYPE_FOR_EXTENDS_BIND == httpRequestType) {
			if (str != null) {
				HashMap<String, Object> kaKouBinddata = SystemSetting
						.getBindShip(GlobalFlags.LIST_TYPE_FROM_KAKOUMANAGER
								+ "");
				HashMap<String, Object> bindDataForTiKou = SystemSetting
						.getBindShip(GlobalFlags.LIST_TYPE_FROM_TIKOUMANAGER
								+ "");
				ArrayList<HashMap<String, Object>> mapsForKk = SystemSetting
						.getShipOfKK();
				if (onParseXMLDataLogOut(str)) {
					if (!box1.isChecked()) {
						SystemSetting.setBindShip(null,
								GlobalFlags.LIST_TYPE_FROM_SHIPSTATUS + "");
					}

					if (!box2.isChecked()) {
						SystemSetting.setBindShip(null,
								GlobalFlags.LIST_TYPE_FROM_TIKOUMANAGER + "");
					}
					if (!box3.isChecked()) {
						SystemSetting.setBindShip(null,
								GlobalFlags.LIST_TYPE_FROM_KAKOUMANAGER + "");
					}
					if (!box4.isChecked()) {
						XcUtil.deleteBindInfo();
					}

					dismess();
					if (httpReturnXMLInfo != null) {
						HgqwToast.makeText(Index.this, httpReturnXMLInfo,
								HgqwToast.LENGTH_LONG).show();
					} else {
						HgqwToast.makeText(Index.this,
								R.string.extends_bind_success,
								HgqwToast.LENGTH_LONG).show();
					}
					// 更新卡口下的船舶，巡检模块不刷新
					switch (Flags.PDA_VERSION) {
					case Flags.PDA_VERSION_SENTINEL:
						RestoreBindShipInfo.restoreBindShipInfo();
						break;
					default:
						break;
					}
				} else {
					if (httpReturnXMLInfo != null) {
						HgqwToast.makeText(Index.this, httpReturnXMLInfo,
								HgqwToast.LENGTH_LONG).show();
					} else {
						HgqwToast.makeText(Index.this,
								R.string.extends_bind_failed,
								HgqwToast.LENGTH_LONG).show();
					}
				}
				DbUtil.deleteOfflineData(HTTPREQUEST_TYPE_FOR_EXTENDS_BIND,
						kaKouBinddata, bindDataForTiKou, mapsForKk);
			} else {
				HgqwToast.makeText(Index.this,
						R.string.data_download_failure_info,
						HgqwToast.LENGTH_LONG).show();
			}
			if (progressDialog != null) {
				progressDialog.dismiss();
				progressDialog = null;
			}
			dismess();
			checkVersionUpdate();
		} else if (HTTPREQUEST_TYPE_FOR_EXTENDS_UNBIND == httpRequestType) {
			if (str != null) {
				dismess();
				if (onParseXMLDataLogOut(str)) {
					// 取消继承绑定成功，清空梯口数据
					DbUtil.deleteOfflineData(
							HTTPREQUEST_TYPE_FOR_EXTENDS_UNBIND, null, null,
							null);

					SystemSetting.setBindShip(null,
							GlobalFlags.LIST_TYPE_FROM_SHIPSTATUS + "");
					SystemSetting.setBindShip(null,
							GlobalFlags.LIST_TYPE_FROM_TIKOUMANAGER + "");
					SystemSetting.setBindShip(null,
							GlobalFlags.LIST_TYPE_FROM_KAKOUMANAGER + "");
					SystemSetting.setBindShip(null,
							GlobalFlags.LIST_TYPE_FROM_XUNCHAXUNJIAN + "");

					if (httpReturnXMLInfo != null) {
						HgqwToast.makeText(Index.this, httpReturnXMLInfo,
								HgqwToast.LENGTH_LONG).show();
					} else {
						HgqwToast.makeText(Index.this,
								R.string.extends_bind_success,
								HgqwToast.LENGTH_LONG).show();
					}
				} else {
					if (httpReturnXMLInfo != null) {
						HgqwToast.makeText(Index.this, httpReturnXMLInfo,
								HgqwToast.LENGTH_LONG).show();
					} else {
						HgqwToast.makeText(Index.this,
								R.string.extends_bind_failed,
								HgqwToast.LENGTH_LONG).show();
					}
				}
			} else {
				HgqwToast.makeText(Index.this,
						R.string.data_download_failure_info,
						HgqwToast.LENGTH_LONG).show();
			}
			if (progressDialog != null) {
				progressDialog.dismiss();
				progressDialog = null;
			}
			dismess();
			checkVersionUpdate();
		} else if (RequestPolice.HTTPREQUEST_TYPE_FOR_RECEIVE_MY_TASK == httpRequestType
				|| RequestPolice.HTTPREQUEST_TYPE_FOR_RECEIVE_MY_TASK_QWZL == httpRequestType) {
			receive_my_task(str, httpRequestType);
		} else if (FlagUrls.LISTEN == httpRequestType) {
			if (progressDialog != null) {
				progressDialog.dismiss();
				progressDialog = null;
			}
			/*
			 * if (str != null && !"".equals(str)) { final String audioStr =
			 * str; if (audioStr != null && !"".equals(audioStr)) { // 保存文件
			 * ArrayList<String> nameList = AudioFileUtils.saveFiles(audioStr);
			 * if (nameList != null && nameList.size() > 0) {
			 * onNotifyTalkBack(nameList); } else {
			 * 
			 * } } }
			 */
		} else if (httpRequestType == HTTPREQUEST_TYPE_GET_SW_VERSION) {
			Intent intent = new Intent(Index.this, UpdateActivity.class);
			UpdataVersionManager manager = new UpdataVersionManager();
			info = manager.updateVersion(str, this.getApplicationContext(),
					intent, Flags.PDA_VERSION_SYSTEMAUTO);
			if (info != null && info.isUpdate()) {
				if (progressDialog != null) {
					progressDialog.dismiss();
					progressDialog = null;
				}
				startActivityForResult(intent, STARTACTIVITY_FOR_SWUPDATE);
			} else {
				xzJcsj();
			}
		} else if (httpRequestType == HTTPREQUEST_TYPE_GET_SW_VERSIONBYHAND) {
			if (progressDialog != null) {
				progressDialog.dismiss();
				progressDialog = null;
			}
			if (str == null) {
				HgqwToast.makeText(Index.this,
						getString(R.string.data_download_failure_info),
						HgqwToast.LENGTH_LONG).show();
				return;
			}
			Intent intent = new Intent(Index.this, UpdateActivity.class);
			UpdataVersionManager manager = new UpdataVersionManager();
			info = manager.updateVersion(str, this.getApplicationContext(),
					intent, Flags.PDA_VERSION_BYHAND);
			boolean update = info.isUpdate();
			if (progressDialog != null) {
				progressDialog.dismiss();
				progressDialog = null;
			}
			if (update) {
				startActivityForResult(intent, STARTACTIVITY_FOR_SWUPDATE);
			}
		} else if (HTTPREQUEST_TYPE_GET_DD == httpRequestType) {
			if (progressDialog != null) {
				progressDialog.dismiss();
				progressDialog = null;
			}
			if (str != null) {
				if (onParseDataDictionaryXMLData(str)) {
					try {
						if (!Environment.getExternalStorageState().equals(
								Environment.MEDIA_MOUNTED)) {
							return;
						}
						String projectDir = Environment
								.getExternalStorageDirectory().getPath()
								+ File.separator + "pingtech";
						File dir = new File(projectDir);
						if (!dir.exists()) {
							dir.mkdir();
						}
						FileWriter writer = new FileWriter(projectDir
								+ File.separator + "datadict.xml");
						writer.write(str);
						writer.close();
						DataDictionary.restoreDataDictionary();
					} catch (IOException e) {
						e.printStackTrace();
						// HgqwToast.makeText(Index.this,
						// R.string.data_download_failure_info,
						// HgqwToast.LENGTH_LONG).show();
					}
				} else {
				}
			} else {
			}
		}

		httpRequestType = 0;
	}

	private void receive_my_task(String str, int what) {
		if (progressDialog != null) {
			progressDialog.dismiss();
			progressDialog = null;
		}
		mReceiving = false;
		if (LoginUser.getCurrentLoginUser() == null) {
			return;
		}

		// 网络请求不通，设置离线标识为false
		if (StringUtils.isEmpty(str)) {
			Log.i(TAG,
					"set web state StringUtils.isEmpty(time) BaseApplication.instent.setWebState(false)");
			BaseApplication.instent.setWebState(false);
		} else if (str.contains("error")
				&& (str.contains("边检通调用服务地址为空") || str.contains("边检通服务调用出错"))) {// "边检通"为代理程序调用平台不通时返回的错误提示
			Log.i(TAG,
					"set web state str.contains(\"error\") && str.contains(\"边检通\") BaseApplication.instent.setWebState(false)");
			BaseApplication.instent.setWebState(false);
		} else {
			BaseApplication.instent.setWebState(true);
		}

		if (StringUtils.isNotEmpty(str)) {
			boolean newTask = PullXmlMyPolice.onParseXMLDataMyTask(str, what);
			if (MyPoliceList.adapter != null) {
				((BaseAdapter) (MyPoliceList.adapter)).notifyDataSetChanged();
			}
			if (QwzlFragment.adapter != null) {
				((BaseAdapter) (QwzlFragment.adapter)).notifyDataSetChanged();
			}
			if (newTask) {
				onNotifyMyTask(what);
			}
		}
		if (handler != null
				&& RequestPolice.HTTPREQUEST_TYPE_FOR_RECEIVE_MY_TASK == what) {
			handler.postDelayed(autoReceiveTaskRunnable, 10000);
		}
	}

	private void dismess() {
		try {
			Field field = quesrDialog.getClass().getSuperclass()
					.getDeclaredField("mShowing");
			field.setAccessible(true);
			// 设置mShowing值，欺骗Android系统
			field.set(quesrDialog, true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (quesrDialog != null) {
			quesrDialog.dismiss();
			quesrDialog = null;
		}
	}

	/**
	 * 
	 * @方法名：xzJcsj
	 * @功能说明：基础数据下载，长期证、码头、泊位、区域
	 * @author liums
	 * @date 2014-5-5 上午10:13:05
	 */
	private void xzJcsj() {
		HgzjxxService hgzjxxService = new HgzjxxService();
		long countOf = hgzjxxService.countOf();
		if (countOf == 0) {
			Log.i(TAG, "Hgzjxx count==0");
			if (progressDialogFoD == null) {
				progressDialogFoD = new ProgressDialog(Index.this);
				progressDialogFoD.setTitle("提示");
				progressDialogFoD.setIcon(android.R.drawable.ic_dialog_info);
				progressDialogFoD
						.setMessage(getString(R.string.first_use_download_offline_data));
				progressDialogFoD.show();
			} else {
				progressDialogFoD.setTitle("提示");
				progressDialogFoD.setIcon(android.R.drawable.ic_dialog_info);
				progressDialogFoD
						.setMessage(getString(R.string.first_use_download_offline_data));
			}
			progressDialogFoD.setCancelable(false);
			dataDownload = new OffDataDownloadForBd(handler,
					new HashMap<String, Object>(),
					OffLineUtil.DOWNLOAD_CQZ_MT_BW_QY_NO_DELETE);

			dataDownload.requestAgain();
		} else {
			// 启动后台更新服务
			Log.i(TAG, "Hgzjxx count!=0");
			startSynchDataService();
		}

	}

	private OffDataDownloadForBd dataDownload = null;

	/**
	 * 有新语音消息时，弹出通知并播放声音
	 * 
	 * @param nameList
	 */
	private Boolean onNotifyTalkBack(ArrayList<String> nameList) {
		Log.i(TAG, "onNotifyTalkBack()");
		Intent intent = new Intent();
		intent.putExtra("type", "TalkBack");
		intent.putStringArrayListExtra("nameList", nameList);
		intent.setClass(getApplicationContext(), PopupActivity.class);
		startActivity(intent);
		BaseApplication.soundManager.onPlaySound(SoundManager.MESSAGE_SOUND, 0);
		return true;
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
		intent.setClass(getApplicationContext(), PopupActivity.class);
		startActivity(intent);
		BaseApplication.soundManager.onPlaySound(1, 0);
		return true;
	}

	/**
	 * 有新警务时，弹出通知并播放声音
	 * 
	 * @param what
	 */
	private Boolean onNotifyMyTask(int what) {
		Log.i(TAG, "notifyMyTask()");
		Intent intent = new Intent();
		intent.putExtra("type", "mypolice");
		intent.putExtra("what", what);
		intent.setClass(getApplicationContext(), PopupActivity.class);
		startActivity(intent);
		BaseApplication.soundManager.onPlaySound(SoundManager.MESSAGE_SOUND, 0);
		return true;
	}

	/**
	 * @方法名：getDD
	 * @功能说明：更新数据字典
	 * @author liums
	 * @date 2013-4-24 下午3:20:42
	 */
	private void getDD() {
		if (!BaseApplication.instent.getWebState()) {
			return;
		}
		String str = "getDD";
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		NetWorkManager
				.request(Index.this, str, params, HTTPREQUEST_TYPE_GET_DD);

	}

	/** 解析数据字典同步返回结果 */
	private boolean onParseDataDictionaryXMLData(String str) {
		boolean success = false;
		try {
			XmlPullParser parser = Xml.newPullParser();
			parser.setInput(new ByteArrayInputStream(str.getBytes()), "utf-8");// 设置解析的数据源
			int type = parser.getEventType();
			String text = null;
			httpReturnXMLInfo = null;
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
							httpReturnXMLInfo = parser.nextText();
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

	public void bottomBarClick(View v) {
		Intent intent = new Intent();
		int permission = LoginUser.getCurrentLoginUser().getPermission();
		switch (v.getId()) {
		case R.id.image_datacollect:
			if (permission == -1) {
				HgqwToast.makeText(Index.this,
						getString(R.string.no_permission),
						HgqwToast.LENGTH_LONG).show();
				return;
			}
			intent.setClass(getApplicationContext(),
					BaseInfoMaintenanceActivity.class);
			startActivity(intent);
			break;
		case R.id.textview_datacollect:
			if (permission == -1) {
				HgqwToast.makeText(Index.this,
						getString(R.string.no_permission),
						HgqwToast.LENGTH_LONG).show();
				return;
			}
			intent.setClass(getApplicationContext(),
					BaseInfoMaintenanceActivity.class);
			startActivity(intent);
			break;

		case R.id.image_systemmanage:
			if (BaseApplication.instent.getWebState()) {
				intent.setClass(getApplicationContext(), SystemActivity.class);
				startActivityForResult(intent, STARTACTIVITY_FOR_SYSTEM);
			} else {
				HgqwToast.makeText(Index.this,
						getString(R.string.no_web_cannot_systemset),
						HgqwToast.LENGTH_LONG).show();
			}
			break;

		case R.id.textview_systemmanage:
			// if (BaseApplication.instent.getWebState()) {
			intent.setClass(getApplicationContext(), SystemActivity.class);
			startActivityForResult(intent, STARTACTIVITY_FOR_SYSTEM);
			// } else {
			// HgqwToast.makeText(Index.this,
			// getString(R.string.no_web_cannot_systemset),
			// HgqwToast.LENGTH_LONG).show();
			//
			// }
			break;

		case R.id.image_logout:
			onLogOutRequest(false);
			break;
		case R.id.textview_logout:
			onLogOutRequest(false);
			break;
		default:
			break;
		}
	}

	@Override
	protected void onResume() {
		Log.i(TAG, "onResume");
		// 更新卡口下的船舶，巡检模块不刷新
		switch (Flags.PDA_VERSION) {
		case Flags.PDA_VERSION_SENTINEL:
			RestoreBindShipInfo.restoreBindShipInfo();
			break;
		default:
			break;
		}
		if (BaseApplication.instent.getWebState()) {
			// 后台更新系统时间
			Log.i(TAG, "UpdateSysTime");
			new UpdateSysTime()
					.execute(new String[] { UpdateSysTime.GET_CURRENT_TIME,
							SystemSetting.getPDACode() });
		}
		// checkGpsState();
		// openMqttService();
		// checkBgService();
		super.onResume();
	}

	private void checkGpsState() {
		// 判断GPS状态，未开启提示升级
		LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			showGpsDialog();
		}
	}

	private void showGpsDialog() {
		Builder builder = new Builder(this);
		builder.setTitle("提示");
		builder.setMessage(getString(R.string.gps_not_open));

		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				ActivityJump.toSysSetting(Index.this);
			}
		});

		// builder.setNegativeButton("取消", new DialogInterface.OnClickListener()
		// {
		// @Override
		// public void onClick(DialogInterface dialog, int which) {
		// dialog.dismiss();
		// dialog.cancel();
		// }
		// });
		builder.setCancelable(true);
		builder.create();
		builder.show();
	}

	private void checkBgService() {
		Log.i("classServiceName", "***strat***");
		ActivityManager mActivityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningServiceInfo> mServiceList = mActivityManager
				.getRunningServices(60);
		for (RunningServiceInfo runningServiceInfo : mServiceList) {
			String classServiceName = runningServiceInfo.service.getClassName();
			if (classServiceName != null
					&& classServiceName.contains("com.pingtech")) {
				Log.i("classServiceName", classServiceName);
			}
		}
		Log.i("classServiceName", "***end***");

	}

	private void otherBusiness() {
		boolean offLineLogin = getIntent().getBooleanExtra("offLineLogin",
				false);
		// 离线模式不执行继承绑定
		if (!offLineLogin) {
			// SystemSetting.setBindShip(null,
			// GlobalFlags.LIST_TYPE_FROM_XUNCHAXUNJIAN + "");
			final HashMap<String, Object> ship_Binddata = SystemSetting
					.getBindShip(GlobalFlags.LIST_TYPE_FROM_SHIPSTATUS + "");
			final HashMap<String, Object> tikou_Binddata = SystemSetting
					.getBindShip(GlobalFlags.LIST_TYPE_FROM_TIKOUMANAGER + "");
			final HashMap<String, Object> kakou_Binddata = SystemSetting
					.getBindShip(GlobalFlags.LIST_TYPE_FROM_KAKOUMANAGER + "");
			final String xunjianName = XcUtil.getXunchaBindName();
			final String xunjianId = XcUtil.getXunjianId();
			final String zqdxlx = XcUtil.getXunjianType();
			if (ship_Binddata != null || tikou_Binddata != null
					|| kakou_Binddata != null
					|| StringUtils.isNotEmpty(xunjianName)) {
				LayoutInflater inflater = getLayoutInflater();
				final View layout = inflater.inflate(R.layout.extendsbind,
						(ViewGroup) findViewById(R.id.dialog));
				box1 = (CheckBox) layout.findViewById(R.id.shipstatus_cb);
				box2 = (CheckBox) layout.findViewById(R.id.tikou_cb);
				box3 = (CheckBox) layout.findViewById(R.id.kakou_cb);
				box4 = (CheckBox) layout.findViewById(R.id.xuncha);
				if (ship_Binddata != null) {
					box1.setText(ship_Binddata.get("cbzwm") + "（船舶动态）");
					box3.setChecked(true);
				} else {
					box1.setChecked(false);
					box1.setVisibility(View.GONE);
				}
				if (tikou_Binddata != null) {
					box2.setText(tikou_Binddata.get("cbzwm") + "（梯口管理）");
					box3.setChecked(true);
				} else {
					box2.setChecked(false);
					box2.setVisibility(View.GONE);
				}
				if (kakou_Binddata != null) {
					box3.setText(kakou_Binddata.get("kkmc") + "（卡口管理）");
					box3.setChecked(true);
				} else {
					box3.setChecked(false);
					box3.setVisibility(View.GONE);
				}
				if (StringUtils.isNotEmpty(xunjianName)) {
					box4.setText(xunjianName + "（巡查巡检）");
					box4.setChecked(true);
				} else {
					box4.setChecked(false);
					box4.setVisibility(View.GONE);
				}
				quesrDialog = new AlertDialog.Builder(this)
						.setTitle(R.string.info)
						.setIcon(android.R.drawable.ic_dialog_info)
						.setView(layout)
						.setPositiveButton(R.string.ok,
								new AlertDialog.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										String str = "extendsBindObj";
										if (progressDialog != null) {
											return;
										}
										String idstr = "";
										try {
											Field field = dialog
													.getClass()
													.getSuperclass()
													.getDeclaredField(
															"mShowing");
											field.setAccessible(true);
											// 设置mShowing值，欺骗Android系统
											field.set(dialog, false);
										} catch (Exception e) {
											e.printStackTrace();
										}

										if (box1.isChecked()) {
											if (idstr.length() != 0) {
												idstr = idstr
														+ "|"
														+ ship_Binddata
																.get("hc")
														+ "_"
														+ GlobalFlags.LIST_TYPE_FROM_SHIPSTATUS;
											} else {
												idstr = ship_Binddata.get("hc")
														+ "_"
														+ GlobalFlags.LIST_TYPE_FROM_SHIPSTATUS;
											}
										}
										if (box2.isChecked()) {
											if (idstr.length() != 0) {
												idstr = idstr
														+ "|"
														+ tikou_Binddata
																.get("hc")
														+ "_"
														+ GlobalFlags.LIST_TYPE_FROM_TIKOUMANAGER;
											} else {
												idstr = tikou_Binddata
														.get("hc")
														+ "_"
														+ GlobalFlags.LIST_TYPE_FROM_TIKOUMANAGER;
											}
										}
										if (box3.isChecked()) {
											if (idstr.length() != 0) {
												idstr = idstr
														+ "|"
														+ kakou_Binddata
																.get("id")
														+ "_"
														+ GlobalFlags.LIST_TYPE_FROM_KAKOUMANAGER;
											} else {
												idstr = kakou_Binddata
														.get("id")
														+ "_"
														+ GlobalFlags.LIST_TYPE_FROM_KAKOUMANAGER;
											}
										}
										if (box4.isChecked()
												&& StringUtils
														.isNotEmpty(zqdxlx)
												&& "0".equals(zqdxlx)) {
											if (idstr.length() != 0) {
												idstr = idstr
														+ "|"
														+ xunjianId
														+ "_"
														+ GlobalFlags.LIST_TYPE_FROM_XUNCHAXUNJIAN;
											} else {
												idstr = xunjianId
														+ "_"
														+ GlobalFlags.LIST_TYPE_FROM_XUNCHAXUNJIAN;
											}
										}
										if (!box1.isChecked()
												&& !box2.isChecked()
												&& !box3.isChecked()
												&& !box4.isChecked()) {
											HgqwToast.makeText(Index.this,
													R.string.no_select_item,
													HgqwToast.LENGTH_LONG)
													.show();
											return;
										}
										List<NameValuePair> params = new ArrayList<NameValuePair>();
										params.add(new BasicNameValuePair(
												"bindObjID", idstr));
										params.add(new BasicNameValuePair(
												"PDACode", SystemSetting
														.getPDACode()));
										params.add(new BasicNameValuePair(
												"userID", LoginUser
														.getCurrentLoginUser()
														.getUserID()));
										params.add(new BasicNameValuePair(
												"requestType", "0"));// 继承0，取消继承1
										params.add(new BasicNameValuePair(
												"version", Flags.PDA_VERSION
														+ ""));// 巡查0，哨兵1，船方自管2
										if (box4.isChecked()
												&& StringUtils
														.isNotEmpty(zqdxlx)
												&& !"0".equals(zqdxlx)) {
											params.add(new BasicNameValuePair(
													"bindType",
													GlobalFlags.LIST_TYPE_FROM_XUNCHAXUNJIAN
															+ ""));
											params.add(new BasicNameValuePair(
													"zqdxlx", zqdxlx));
											params.add(new BasicNameValuePair(
													"zqdxId", xunjianId));
										}
										progressDialog = new ProgressDialog(
												Index.this);
										progressDialog
												.setTitle(getString(R.string.waiting));
										progressDialog
												.setMessage(getString(R.string.waiting));
										progressDialog.setCancelable(false);
										progressDialog.setIndeterminate(false);
										progressDialog.show();
										NetWorkManager
												.request(Index.this, str,
														params,
														HTTPREQUEST_TYPE_FOR_EXTENDS_BIND);
									}
								})
						.setNegativeButton(R.string.cancel,
								new AlertDialog.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										if (progressDialog != null) {
											return;
										}
										try {
											Field field = dialog
													.getClass()
													.getSuperclass()
													.getDeclaredField(
															"mShowing");
											field.setAccessible(true);
											// 设置mShowing值，欺骗Android系统
											field.set(dialog, false);
										} catch (Exception e) {
											e.printStackTrace();
										}
										String str = "extendsBindObj";
										List<NameValuePair> params = new ArrayList<NameValuePair>();
										params.add(new BasicNameValuePair(
												"bindObjID", ""));
										params.add(new BasicNameValuePair(
												"requestType", "1"));// 继承0，取消继承1
										params.add(new BasicNameValuePair(
												"version", Flags.PDA_VERSION
														+ ""));// 巡查0，哨兵1，船方自管2
										params.add(new BasicNameValuePair(
												"PDACode", SystemSetting
														.getPDACode()));
										params.add(new BasicNameValuePair(
												"userID", LoginUser
														.getCurrentLoginUser()
														.getUserID()));
										progressDialog = new ProgressDialog(
												Index.this);
										progressDialog
												.setTitle(getString(R.string.waiting));
										progressDialog
												.setMessage(getString(R.string.waiting));
										progressDialog.setCancelable(false);
										progressDialog.setIndeterminate(false);
										progressDialog.show();
										NetWorkManager
												.request(Index.this, str,
														params,
														HTTPREQUEST_TYPE_FOR_EXTENDS_UNBIND);
									}
								}).setCancelable(false).show();
			} else {
				// 系统版本升级
				checkVersionUpdate();
			}
		}

	}

	/****************** 注销相关业务 ******************/
	@Override
	public void onDestroy() {
		Log.i(TAG, "onDestroy()");
		unBindHgqwService();// 页面销毁解绑相关服务及监听
		handler = null;
		stopTimer();
		// closeMqttService();
		NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		manager.cancel(MessageReceiver.NOTIFICATION_ID);
		super.onDestroy();
	}

	/**
	 * 
	 * @方法名：stopTimer
	 * @功能说明：停止
	 * @author liums
	 * @date 2013-12-13 上午10:59:48
	 */
	private void stopTimer() {
		Log.i(TAG, "stopTimer");
		if (timer != null) {
			timer.cancel();
			timer = null;
		}
		if (timerTaskGetDD != null) {
			timerTaskGetDD.cancel();
			timerTaskGetDD = null;
		}
		if (timerTaskBaseInfo != null) {
			timerTaskBaseInfo.cancel();
			timerTaskBaseInfo = null;
		}

	}

	/**
	 * 
	 * @方法名：unBindHgqwService
	 * @功能说明：页面销毁解绑相关服务及监听
	 * @author liums
	 * @date 2013-10-26 下午5:38:58
	 */
	private void unBindHgqwService() {
		if (conn != null) {// 解绑语音对讲服务
			unbindService(conn);
		}
		if (connectionForSync != null) {
			BaseApplication.instent.setDownloadFlag(false);
			unbindService(connectionForSync);
		}

		if (broadcastReceiver != null) {// 取消电池电量监听
			unregisterReceiver(broadcastReceiver);
		}

		// GPS定位服务，离线数据上传服务
		stopService(new Intent("com.pingtech.PINGTECH_SERVICE"));
		stopService(new Intent(Index.this, AndSerOffLineData.class));

	}

	/****************** 平台登陆后绑定的服务 ******************/
	/** 语音对讲服务 */
	private ListenerService ls;

	private ServiceConnection conn = new ServiceConnection() {
		@Override
		public void onServiceDisconnected(ComponentName name) {
			ls = null;
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			ls = ((ListenerService.LocalBinder) service).getService();
		}
	};

	/** 电池电量监听 */
	BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			int level = intent.getIntExtra("level", 0);
			int status = intent.getIntExtra("status", 0);
			if ((level == 20 || level == 15 || level == 10)
					&& status != BatteryManager.BATTERY_STATUS_CHARGING) {
				onNotifyBatteries(level);
			}
		}
	};

	/** 接收警务指令 */
	private Runnable autoReceiveTaskRunnable = new Runnable() {
		@Override
		public void run() {
			if (handler == null) {
				return;
			}
			autoReceiveMyTask(false);
		}
	};

	@Override
	protected void onNewIntent(Intent intent) {
		Log.i(TAG, "onNewIntent");
		super.onNewIntent(intent);
		boolean activityForHome = intent.getBooleanExtra("activityForHome",
				false);
		boolean fromLogin = intent.getBooleanExtra("fromLogin", false);
		if (activityForHome) {
			Log.i(TAG, "activityForHome==true");
			// activityForExit();
			return;
		}

		if (fromLogin) {
			// 继承绑定业务
			Log.i(TAG, "fromLogin==true");
			if (quesrDialog != null) {
				quesrDialog.dismiss();
				quesrDialog = null;
			}
			otherBusiness();
		}
	}

	/* 监听物理按键 */
	/*
	 * @Override public void onAttachedToWindow() {
	 * getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD);
	 * super.onAttachedToWindow(); }
	 */

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		android.util.Log.i(TAG, "onKeyDown:" + keyCode);

		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:// 返回键
			onShowExitQuestDialog();
			break;
		case KeyEvent.KEYCODE_HOME:
			// activityForExit();// 拦截HOME键
			break;
		default:
			break;
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * 
	 * @方法名：activityForExit
	 * @功能说明：模拟HOME键
	 * @author liums
	 * @date 2013-11-29 下午3:35:31
	 */
	private void activityForExit() {
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_HOME);
		startActivity(intent);
		finish();
	}

}
