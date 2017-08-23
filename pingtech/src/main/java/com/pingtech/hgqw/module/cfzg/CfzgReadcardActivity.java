package com.pingtech.hgqw.module.cfzg;

import java.io.ByteArrayInputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.xmlpull.v1.XmlPullParser;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.util.Xml;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android_serialport_api.ParseSFZAPI.People;

import com.pingtech.R;
import com.pingtech.hgqw.entity.CardInfo;
import com.pingtech.hgqw.entity.FlagManagers;
import com.pingtech.hgqw.entity.FlagUrls;
import com.pingtech.hgqw.entity.LoginUser;
import com.pingtech.hgqw.entity.MessageEntity;
import com.pingtech.hgqw.interf.OnHttpResult;
import com.pingtech.hgqw.readcard.service.ReadService;
import com.pingtech.hgqw.readcard.utils.GetIDInfo;
import com.pingtech.hgqw.utils.BasicNameValuePair;
import com.pingtech.hgqw.utils.DeviceUtils;
import com.pingtech.hgqw.utils.Log;
import com.pingtech.hgqw.utils.StringUtils;
import com.pingtech.hgqw.utils.SystemSetting;
import com.pingtech.hgqw.web.NetWorkManager;
import com.pingtech.hgqw.widget.HgqwToast;
import com.pingtech.hgqw.zxing.CaptureActivity;
import com.pingtech.hgqw.zxing.Constant;
import com.pingtech.hgqw.zxing.entity.MsTdc;
import com.pingtech.hgqw.zxing.utils.ScanUtils;

public class CfzgReadcardActivity extends CfzgSuperActivity implements OnHttpResult {
	private static final String TAG = "CfzgReadcardActivity";

	/**
	 * 设备型号： 1 一代警务通mima_PE43 2 二代警务通 M802
	 */
	private int model = DeviceUtils.getDeviceModel();

	/**
	 * 正在读卡标志
	 */
	private boolean readingFlag = true;

	/**
	 * 海港证类：48登轮证,50登陆证，52搭靠外轮许可证。
	 */
	private String hgzl = "";

	/**
	 * 通行方向：0上船、 1下船
	 */
	private String sxcfx = "";

	/**
	 * 通行方向：0上船、 1下船
	 */
	private String sxcfxEn = "";

	/**
	 * 通行方向：true上船、false下船
	 */
	private boolean sxcfxBoolean = true;

	/**
	 * 播放声音：0不播放，1播放验证成功提示音，2播放验证失败提示音
	 */
	private int sound = 0;

	/**
	 * 请求成功后，证件号码是否存在
	 */
	private boolean hasCardInfo = false;


	/**
	 * 对话框
	 */
	private ProgressDialog progressDialog = null;

	private int hander = 0;

	/**
	 * 发起验证通行结果的http请求的type
	 */
	private static final int HTTPREQUEST_TYPE_FOR_TRAFFIC_VALID = 7;

	/**
	 * IC读卡区域识别：0默认区域，1加密区域
	 */
	private int readMode = 1;

	private boolean cn = true;

	private boolean ic = true;

	// ***************二维码扫描***********************
	/**
	 * 二维码扫描结果
	 */
	private MsTdc msTdc = null;

	/**
	 * 二维码扫描出来的zjhm
	 */
	private String zxingZJHM = "";

	@SuppressLint("HandlerLeak")
	private Handler zxingHandler = new Handler() {
		public void handleMessage(Message msg) {
			// 手动输入类型
			// onReadComplete(zxingZJHM, null, true);
			onReadComplete(zxingZJHM, null, "2");
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreateForCfzg(savedInstanceState, R.layout.cfzg_readcard);

		setContentView(R.layout.cfzg_readcard, false);
		// 初始化声音、震动设备
		onInitSoundPool();
		onInitVibrator();

		Intent intent = getIntent();
		ic = intent.getBooleanExtra("ic", true);
		cn = intent.getBooleanExtra("cn", true);
		// 中英界面设置
		if (cn) {
			((TextView) findViewById(R.id.current_dialog_title)).setTextSize(22);
			if (ic) {
				setMyActiveTitle("登轮证刷卡页面");
			} else {
				setMyActiveTitle("身份证刷卡页面");
			}
			findViewById(R.id.cfzg_readcard_layout_cn).setVisibility(View.VISIBLE);
			findViewById(R.id.cfzg_readcard_layout_en).setVisibility(View.GONE);
		} else {
			if (ic) {
				setMyActiveTitle("Interface of swiping boarding pass card");
				((TextView) findViewById(R.id.current_dialog_title)).setTextSize(15);
			} else {
				setMyActiveTitle("Interface of swiping ID card");
			}
			findViewById(R.id.cfzg_readcard_layout_cn).setVisibility(View.GONE);
			findViewById(R.id.cfzg_readcard_layout_en).setVisibility(View.VISIBLE);
		}
	}

	/**
	 * @方法名：buttonOnClick
	 * @功能说明：按钮点击事件
	 * @author liums
	 * @date 2013-4-27 下午9:06:15
	 * @param v
	 */
	public void buttonOnClick(View v) {
		switch (v.getId()) {
		case R.id.cfzg_readcard_btn_back:// 返回按钮
			startActivity(new Intent(this, CfzgIndex.class));
			finish();
			break;
		case R.id.cfzg_btn_zxing:
			if (DeviceUtils.getDeviceModel() == DeviceUtils.DEVICE_MODEL_CFON640) {
				Intent startIntent = new Intent(
						"android.intent.action.SCANNER_BUTTON_DOWN", null);
				sendOrderedBroadcast(startIntent, null);
			} else if (DeviceUtils.getDeviceModel() == DeviceUtils.DEVICE_MODEL_PA8) {
				ScanUtils.pa8Ewm(readCardHander);
			}else if(DeviceUtils.getDeviceModel() == DeviceUtils.DEVICE_MODEL_PA9){
				ScanUtils.readByPA9(readCardHander);
			}else {
				// 扫描二维码
				Intent intent = new Intent(CfzgReadcardActivity.this, CaptureActivity.class);
				startActivityForResult(intent, FlagManagers.CUSTOM_SCAN_ZXING);
			}
			break;
		default:
			break;
		}

	}

	/**
	 * 
	 * @方法名：onReadComplete
	 * @功能说明：
	 * @author liums
	 * @date 2013-12-19 上午10:38:21
	 * @param cardNumber
	 * @param defaultickey
	 * @param inputType
	 *            输入方式(默认为"0")<br>
	 *            是否刷卡：0刷卡true、1手动输入、2二维码扫描
	 */
	private void onReadComplete(String cardNumber, String defaultickey, String inputType) {
		String url = "inspectForAcross";

		// 从内存中取船舶动态的绑定数据
		HashMap<String, Object> _BindShip = SystemSetting.getBindShip(CfzgShipListActivity.LIST_TYPE_FROM_SHIPSTATUS + "");
		String voyageNumber = (String) _BindShip.get("hc");

		List<NameValuePair> params = new ArrayList<NameValuePair>();

		params.add(new BasicNameValuePair("voyageNumber", voyageNumber));
		params.add(new BasicNameValuePair("cardNumber", cardNumber));
		params.add(new BasicNameValuePair("defaultickey", defaultickey));
		// Old data
		// // 是否刷卡：0刷卡、1手动输入
		// if (idcard) {
		// params.add(new BasicNameValuePair("sfsk", "1"));
		// } else {
		// params.add(new BasicNameValuePair("sfsk", "0"));
		// }
		if (StringUtils.isNotEmpty(inputType)) {
			if ("0".equals(inputType.trim()) || "1".equals(inputType.trim()) || "2".equals(inputType.trim())) {
				params.add(new BasicNameValuePair("sfsk", inputType.trim()));
			} else {
				params.add(new BasicNameValuePair("sfsk", "0"));
			}
		} else {
			params.add(new BasicNameValuePair("sfsk", "0"));
		}
		params.add(new BasicNameValuePair("PDACode", SystemSetting.getPDACode()));
		params.add(new BasicNameValuePair("acrossTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(System.currentTimeMillis()))));
		params.add(new BasicNameValuePair("userID", LoginUser.getCurrentLoginUser().getUserID()));
		if (progressDialog != null) {
			return;
		}
		progressDialog = new ProgressDialog(this);
		progressDialog.setTitle(getString(R.string.Validing));
		progressDialog.setMessage(getString(R.string.waiting));
		progressDialog.setCancelable(false);
		progressDialog.setIndeterminate(false);
		progressDialog.show();
		NetWorkManager.request(this, url, params, HTTPREQUEST_TYPE_FOR_TRAFFIC_VALID);
	}

	private boolean tsxxFlag = false;

	private String tsxx = "请刷登轮证！";

	@Override
	public void onHttpResult(String str, int httpRequestType) {
		if (progressDialog != null) {
			progressDialog.dismiss();
			progressDialog = null;
		}
		switch (httpRequestType) {
		case HTTPREQUEST_TYPE_FOR_TRAFFIC_VALID:
			boolean success = false;
			httpRequestType = 0;
			if (str == null) {// 请求失败
				HgqwToast.getToastView(this, getString(R.string.data_download_failure_info)).show();
				return;
			}
			success = onParseXMLData(str);

			if (!success || tsxxFlag) {// 提示，验证失败
				if (cn) {
					HgqwToast.getToastView(this, "验证失败！").show();
					onPlaySound(3, 0);
				} else {
					HgqwToast.getToastView(this, "Verification failed!").show();
					onPlaySound(33, 0);
				}
				return;
			}
			if (hasCardInfo == false) {
				if (cn) {
					HgqwToast.getToastView(this, "验证失败！").show();
					onPlaySound(3, 0);
				} else {
					HgqwToast.getToastView(this, "Verification failed!").show();
					onPlaySound(33, 0);
				}
				return;
			}

			// 修改通行方向按钮和声音控制。
			if (sound == 1) {
				if (cn) {
					HgqwToast.getToastView(this, sxcfx + "，验证通过！").show();
					if (sxcfxBoolean) {
						onPlaySound(1, 0);// 成功提示音
					} else {
						onPlaySound(2, 0);// 成功提示音
					}
				} else {
					HgqwToast.getToastView(this, sxcfxEn + ", Verification Passed!").show();
					if (sxcfxBoolean) {
						onPlaySound(11, 0);// 成功提示音
					} else {
						onPlaySound(22, 0);// 成功提示音
					}
				}

			} else if (sound == 2) {
				if (cn) {
					HgqwToast.getToastView(this, "验证失败！！").show();
					onPlaySound(3, 0);
				} else {
					HgqwToast.getToastView(this, "Verification failed!").show();
					onPlaySound(33, 0);
				}
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
		default:
			break;
		}
	}

	/** 解析刷卡登记平台返回的数据 */
	private boolean onParseXMLData(String str) {
		try {
			XmlPullParser parser = Xml.newPullParser();
			parser.setInput(new ByteArrayInputStream(str.getBytes()), "utf-8");// 设置解析的数据源
			int type = parser.getEventType();
			String text = "";
			boolean zjxx = false;
			boolean dkxx = false;
			hasCardInfo = false;
			tsxxFlag = false;
			sound = 0;
			hgzl = "";
			// HashMap<String, Object> map = null;
			boolean success = false;
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
						if (success) {
						} else {
							// tempHttpReturnXMLInfo = parser.nextText();
						}
					} else if ("tsxx".equals(parser.getName())) {
						// 提示信息
						// tempHttpReturnXMLInfo = parser.nextText();
					} else if ("zjxx".equals(parser.getName())) {
						// 证件信息
						zjxx = true;
						hasCardInfo = true;
					} else if ("dkxx".equals(parser.getName())) {
						// 搭靠信息
						dkxx = true;
						hasCardInfo = true;
					} else if ("txjlid".equals(parser.getName())) {
						// 通行记录id
						// txjlid_s = parser.nextText();
					} else if ("isPass".equals(parser.getName())) {
						// 是否验证通过
						String strIsPass = parser.nextText();
						if ("pass".equals(strIsPass)) {
							sound = 1;
						} else {
							sound = 2;
						}

					} else if ("dkjlid".equals(parser.getName())) {
						// 搭靠记录id
						// dkjlid_s = parser.nextText();
					} else if ("sfdk".equals(parser.getName())) {
						// 是否搭靠
						// sfdk_s = parser.nextText();
					} else if ("sxcfx".equals(parser.getName())) {
						// 上下船方向
						// sxcfx_s = parser.nextText();
						String sxcfxStr = parser.nextText();
						if ("0".equals(sxcfxStr)) {
							sxcfx = "上船";
							sxcfxEn = "Embark";
							sxcfxBoolean = true;
						} else {
							sxcfx = "下船";
							sxcfxEn = "Disembark";
							sxcfxBoolean = false;
						}
					} else if ("fx".equals(parser.getName())) {
						// 上下船方向
						// sxcfx_s = parser.nextText();
					} else if ("xm".equals(parser.getName())) {
						// 姓名
						if (zjxx) {
							// name_s = parser.nextText();
						}
					} else if ("xb".equals(parser.getName())) {
						// 性别
						if (zjxx) {
							// sex_s = parser.nextText();
						}
					} else if ("zw".equals(parser.getName())) {
						// 职务
						if (zjxx) {
							// office_s = parser.nextText();
						}
					} else if ("gj".equals(parser.getName())) {
						// 国籍
						if (zjxx) {
							// country_s = parser.nextText();
						}
					} else if ("hgzl".equals(parser.getName())) {
						// 海港证类
						// hgzl_s = parser.nextText();
						hgzl = parser.nextText();
					} else if ("csrq".equals(parser.getName())) {
						// 出生日期
						if (zjxx) {
							// birthday_s = parser.nextText();
						}
					} else if ("ryid".equals(parser.getName())) {
						// 人员id
						if (zjxx) {
							// ryid_s = parser.nextText();
						}
					} else if ("zjhm".equals(parser.getName())) {
						// 证件号码
						if (zjxx) {
							// zjhm = parser.nextText();
						} else if (dkxx) {
							// dkzjhm_s = parser.nextText();
						}

					} else if ("zjlx".equals(parser.getName())) {
						// 证件类型
						String zjlx = parser.nextText();
						if ("52".equals(zjlx) || "50".equals(zjlx)) {// 52搭靠证件
							success = false;
							tsxxFlag = true;
						}
					} else if ("ssdw".equals(parser.getName())) {
						// 所属单位
						if (zjxx) {
							// unit_s = parser.nextText();
						} else if (dkxx) {
							// dkssdw_s = parser.nextText();
						}
					} else if ("icpic".equals(parser.getName())) {
						// 照片信息
						// if (zjxx) {
						// String icpic_s = parser.nextText();
						// if (icpic_s != null && icpic_s.length() > 0) {
						// hasImage = true;
						// byte[] image = Base64.decode(icpic_s);
						// image_cgcs = Base64.decode(icpic_s);
						// BitmapFactory.Options opts = new
						// BitmapFactory.Options();
						// opts.inJustDecodeBounds = true;
						// netWorkImage = BitmapFactory.decodeByteArray(image,
						// 0, image.length, opts);
						// int height_be = opts.outHeight / 130;
						// int width_be = opts.outWidth / 105;
						// opts.inSampleSize = height_be > width_be ? height_be
						// : width_be;
						// if (opts.inSampleSize <= 0) {
						// opts.inSampleSize = 1;
						// }
						// Log.i(TAG, "decodeByteArray:" + opts.outHeight + ","
						// + opts.outWidth + "," +
						// opts.inSampleSize);
						// opts.inJustDecodeBounds = false;
						// netWorkImage = BitmapFactory.decodeByteArray(image,
						// 0, image.length, opts);
						// Log.i(TAG, "decodeByteArray:" + opts.outHeight + ","
						// + opts.outWidth);
						// }
						// }
					} else if ("yxq".equals(parser.getName())) {
						// 有效期限
					} else if ("cbmc".equals(parser.getName())) {
						// 船舶名称
						if (dkxx) {
							// dkcbmc_s = parser.nextText();
						}
					} else if ("cgj".equals(parser.getName())) {
						// 船港籍
						if (dkxx) {
							// dkcgj_s = parser.nextText();
						}
					} else if ("zzdw".equals(parser.getName())) {
						// 载重吨位
						if (dkxx) {
							// dkzzdw_s = parser.nextText();
						}
					} else if ("ml".equals(parser.getName())) {
						// 马力
						if (dkxx) {
							// dkml_s = parser.nextText();
						}
					} else if ("yt".equals(parser.getName())) {
						// 用途
						if (dkxx) {
							// dkyt_s = parser.nextText();
						}
					} else if ("dkfw".equals(parser.getName())) {
						// 搭靠船舶（范围）
						if (dkxx) {
							// dkdkfw_s = parser.nextText();
						}

					} else if ("txjl".equals(parser.getName())) {
						// 通行记录
						// if (txjl == null) {
						// txjl = new ArrayList<Map<String, Object>>();
						// } else {
						// txjl.clear();
						// }
					} else if ("jl".equals(parser.getName())) {
						// map = new HashMap<String, Object>();
					} else if ("txsj".equals(parser.getName())) {
						// 通行时间
						// map.put("txsj", parser.nextText());
					} else if ("txfx".equals(parser.getName())) {
						// 通行方向（0上船、 1下船）
						// map.put("txfx", parser.nextText());

					} else if ("xgcb".equals(parser.getName())) {
						// 船舶名称
						// map.put("xgcb", parser.nextText());
					} else if ("txdd".equals(parser.getName())) {
						// 通行地点
						// map.put("txdd", parser.nextText());
					}
					break;
				case XmlPullParser.END_TAG:
					if ("jl".equals(parser.getName())) {
						// if (txjl == null) {
						// txjl = new ArrayList<Map<String, Object>>();
						// }
						// txjl.add(map);
					} else if ("zjxx".equals(parser.getName())) {
						// 证件信息
						zjxx = false;
					} else if ("dkxx".equals(parser.getName())) {
						// 搭靠信息
						dkxx = false;
					}

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

	/* IC读卡器程序开始 */

	/**
	 * 读卡循环控制标识
	 */
	boolean iCReadFlag = true;

	/**
	 * 处理IC读卡器结果的Handler
	 */
	Handler iCReaderHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.arg1) {
			case 1:// 卡号读取成功
				onPlaySoundNoVb(4, 0);// 播放声音,无振动
				String cardNumber = (String) msg.obj;
				// 请求平台
				// onReadComplete(cardNumber, null, false);
				onReadComplete(cardNumber, null, "0");
				break;
			case 110:
				break;
			case 102:
				HgqwToast.getToastView(getApplicationContext(), "IC读卡器启动成功").show();
				break;
			case 103:// RFID读卡器启动失败
				HgqwToast.getToastView(getApplicationContext(), "IC读卡器启动失败").show();
				break;
			case 101:// 身份证读卡器启动成功
				HgqwToast.getToastView(getApplicationContext(), "身份证读卡器启动成功").show();
				break;
			case 1011:// 身份证读卡器启动失败
				HgqwToast.getToastView(getApplicationContext(), "身份证读卡器启动失败").show();
				break;
			case 100:// 身份证刷卡完成
				if (GetIDInfo.getIDCardNum()) {
					String sfzh = GetIDInfo.cardNum;
					// 请求平台
					// onReadComplete(sfzh, null, true);
					onReadComplete(sfzh, null, "1");
				}
				break;

			default:
				break;
			}
		}

	};


	/* 音频、震动程序开始 */

	/**
	 * 音频池
	 */
	private SoundPool sp;

	/**
	 * 音频源
	 */
	private HashMap<Integer, Integer> hm;

	/**
	 * android震动设备
	 */
	private Vibrator vibrator = null;

	/**
	 * 音量
	 */
	private float volume;

	/** 初始震动源，用于提示刷卡未通过验证时 */
	private void onInitVibrator() {
		vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
	}

	/** 开启震动 */
	private void onPlayVibrator() {
		long[] pattern = { 100, 900 };
		vibrator.vibrate(pattern, -1);
	}

	/** 关闭震动源 */
	private void onCloseVibrator() {
		vibrator.cancel();
	}

	/**
	 * @方法名：onInitSoundPool
	 * @功能说明：初始化音源
	 * @author liums
	 * @date 2013-4-28 下午12:58:55
	 */
	private void onInitSoundPool() {
		sp = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
		hm = new HashMap<Integer, Integer>();
		// hm.put(1, sp.load(getApplicationContext(), R.raw.msgx4, 1));
		// hm.put(2, sp.load(getApplicationContext(), R.raw.kkyz_successful,
		// 1));
		// hm.put(3, sp.load(getApplicationContext(), R.raw.kkyz_fail, 1));
		hm.put(1, sp.load(getApplicationContext(), R.raw.ship_up_passed_cn, 1));
		hm.put(2, sp.load(getApplicationContext(), R.raw.ship_down_passed_cn, 1));
		hm.put(3, sp.load(getApplicationContext(), R.raw.verification_failed_cn, 1));
		hm.put(4, sp.load(getApplicationContext(), R.raw.ic_mag, 1));
		hm.put(11, sp.load(getApplicationContext(), R.raw.ship_up_passed_en, 1));
		hm.put(22, sp.load(getApplicationContext(), R.raw.ship_down_passed_en, 1));
		hm.put(33, sp.load(getApplicationContext(), R.raw.verification_failed_en, 1));
	}

	/**
	 * @方法名：onUnInitSoundPool
	 * @功能说明：是否音频资源
	 * @author liums
	 * @date 2013-4-28 下午12:59:19
	 */
	private void onUnInitSoundPool() {
		sp = null;
		hm = null;
	}

	/**
	 * @方法名：onPlaySound
	 * @功能说明：播放声音，附带震动
	 * @author liums
	 * @date 2013-4-28 下午12:59:36
	 * @param num
	 * @param loop
	 */
	private void onPlaySound(int num, int loop) {
		Log.i(TAG, "playSound:" + num + "," + loop);
		AudioManager am = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
		float currentSound = am.getStreamVolume(AudioManager.STREAM_RING);
		float maxSound = am.getStreamMaxVolume(AudioManager.STREAM_RING);
		volume = currentSound / maxSound;
		// sp.play(hm.get(num), volume, volume, 1, loop, 1.0f);
		sp.play(hm.get(num), volume, volume, 1, loop, 1.0f);
		// 震动
		onPlayVibrator();
	}

	/**
	 * @方法名：onPlaySoundNoVb
	 * @功能说明：播放声音，不加震动
	 * @author liums
	 * @date 2013-4-28 下午12:59:49
	 * @param num
	 * @param loop
	 */
	private void onPlaySoundNoVb(int num, int loop) {
		Log.i(TAG, "playSound:" + num + "," + loop);
		AudioManager am = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
		float currentSound = am.getStreamVolume(AudioManager.STREAM_RING);
		float maxSound = am.getStreamMaxVolume(AudioManager.STREAM_RING);
		volume = currentSound / maxSound;
		// sp.play(hm.get(num), volume, volume, 1, loop, 1.0f);
		sp.play(hm.get(num), volume, volume, 1, loop, 1.0f);
	}

	/* 音频、震动程序结束 */

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// 从对话框返回
		if (requestCode == FlagManagers.CUSTOM_DIALOG && resultCode == RESULT_OK) {
			// this.sendAlarmInfo();
		} else if (requestCode == FlagManagers.CUSTOM_DIALOG_FOR_EXIT && resultCode == RESULT_OK) {
			String password = data.getStringExtra("password");
			this.validatePassword(password);
		} else if (requestCode == FlagManagers.CUSTOM_SCAN_ZXING && resultCode == RESULT_OK) {
			// 接收扫描二维码返回来的数据
			boolean isback = data.getBooleanExtra(Constant.ZXING_ISBACK, false);
			if (!isback) {// 判断是不是从按返回按钮进入该界面
				msTdc = (MsTdc) data.getSerializableExtra(Constant.ZXING_DATA);
				if (msTdc != null) {
					zxingZJHM = msTdc.getZjhm();
					if (com.pingtech.hgqw.utils.StringUtils.isEmpty(zxingZJHM)) {
						zxingZJHM = "";
					}
				} else {
					zxingZJHM = "";
				}
				zxingHandler.sendEmptyMessage(0);
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
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
		// progressDialog.setTitle(getString(R.string.waiting));
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

	/* 监听物理按键 */
	@Override
	public void onAttachedToWindow() {
		getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD);
		super.onAttachedToWindow();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		Boolean isVolumnKey = false;
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:// 返回键
			return super.onKeyDown(keyCode, event);
		case KeyEvent.KEYCODE_HOME:
			break;
		case KeyEvent.KEYCODE_VOLUME_DOWN:
		case KeyEvent.KEYCODE_VOLUME_UP:
			if (DeviceUtils.getDeviceModel() == DeviceUtils.DEVICE_MODEL_CFON640) {
				Intent startIntent = new Intent(
						"android.intent.action.SCANNER_BUTTON_DOWN", null);
				sendOrderedBroadcast(startIntent, null);
				isVolumnKey = true;
			} else if (DeviceUtils.getDeviceModel() == DeviceUtils.DEVICE_MODEL_PA8) {
				ScanUtils.pa8Ewm(readCardHander);
				isVolumnKey = true;
			}else if(DeviceUtils.getDeviceModel() == DeviceUtils.DEVICE_MODEL_PA9){
				ScanUtils.readByPA9(readCardHander);
				isVolumnKey = true;
			}
			break;
		default:
			break;
		}
		if (isVolumnKey) {
			return true;
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
		Intent intent = new Intent();

		intent.setClass(getApplicationContext(), CfzgCustomDialogForExit.class);
		intent.putExtra("cn", cn);
		startActivityForResult(intent, FlagManagers.CUSTOM_DIALOG_FOR_EXIT);
	}

	@Override
	protected void onStart() {
		Log.i(TAG, this.getClass().getName() + " onStart");
		super.onStart();
	}

	@Override
	protected void onRestart() {
		Log.i(TAG, this.getClass().getName() + " onRestart");
		super.onRestart();
	}

	/* 读卡程序开始 */
	private ReadService readService;

	private ReadCardHandler readCardHander;

	private CardInfo cardInfo;

	@Override
	protected void onResume() {
		Log.i(TAG, "onResume");
		readCardHander = new ReadCardHandler();
		ScanUtils.initScanBarCode(getApplicationContext(), readCardHander);
		readInit();
		super.onResume();
	}

	@Override
	public void onDestroy() {
		// 释放声音、震动设备
		ScanUtils.closeScanBarCode(getApplicationContext());
		onCloseVibrator();
		onUnInitSoundPool();
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		Log.i(TAG, "onPause");
		ReadService.getInstent().close();
		super.onPause();
	}

//	private CopyOfReadService r = null;
	private void readInit() {
		if (ic) { 
			 ReadService.getInstent(this, readCardHander, ReadService.READ_TYPE_DEFAULT_AND_ICKEY).init();
		} else {
			 ReadService.getInstent(this, readCardHander, ReadService.READ_TYPE_ID).init();
		}
	}

	public void btnClick(View v) {
		switch (v.getId()) {
		case R.id.btn_ic_id_change:
//			readService.readChange();
			break;
		default:
			break;
		}
	}

	// 读卡Handler类
	class ReadCardHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			// cardInfo = null;
			switch (msg.what) {
			case ReadService.READ_TYPE_EWM:
				cardInfo = (CardInfo) msg.obj;
				if(cardInfo==null){
					return;
				}
				msTdc = cardInfo.getMsTdc();
				if (msTdc != null) {
					zxingZJHM = msTdc.getZjhm();
					if (StringUtils.isNotEmpty(zxingZJHM)) {
						zxingHandler.sendEmptyMessage(0);
					}
				}
				break;
			case MessageEntity.TOAST:
				HgqwToast.getToastView(getApplicationContext(), (String) msg.obj).show();
				break;
			case ReadService.READ_TYPE_DEFAULT_ICKEY:
				break;
			case ReadService.READ_TYPE_DEFAULT_AND_ICKEY:
				cardInfo = (CardInfo) msg.obj;
				String cardnumber = cardInfo.getIckey();
				String defaultickey = cardInfo.getDefaultIckey();
				onPlaySoundNoVb(4, 0);// 播放声音
				// onReadComplete(cardnumber, defaultickey, false);
				onReadComplete(cardnumber, defaultickey, "0");
				break;
			case ReadService.READ_TYPE_ID:
				cardInfo = (CardInfo) msg.obj;
				onPlaySoundNoVb(4, 0);// 播放声音
				People people = cardInfo.getPeople();
				// onReadComplete(people.getPeopleIDCode(), null, true);
				onReadComplete(people.getPeopleIDCode(), null, "1");
				Log.i("身份证读卡结果", people.getPeopleIDCode());
				break;
			case ReadService.READ_TYPE_ICKEY:
				break;
			default:
				break;
			}
		}
	}
	/* 读卡程序结束 */
}
