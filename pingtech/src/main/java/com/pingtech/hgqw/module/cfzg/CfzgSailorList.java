package com.pingtech.hgqw.module.cfzg;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.xmlpull.v1.XmlPullParserException;

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
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.pingtech.R;
import com.pingtech.hgqw.adapter.GetCyxxListAdapter;
import com.pingtech.hgqw.entity.Cyxx;
import com.pingtech.hgqw.entity.FlagManagers;
import com.pingtech.hgqw.entity.FlagUrls;
import com.pingtech.hgqw.entity.LoginUser;
import com.pingtech.hgqw.entity.SaveOrUpdateTktxjl;
import com.pingtech.hgqw.interf.OnHttpResult;
import com.pingtech.hgqw.module.offline.zjyf.util.YfZjxxConstant;
import com.pingtech.hgqw.pullxml.PullXmlSaveOrUpdateTktxjl;
import com.pingtech.hgqw.pullxml.PullXmlShipList;
import com.pingtech.hgqw.utils.BasicNameValuePair;
import com.pingtech.hgqw.utils.DataDictionary;
import com.pingtech.hgqw.utils.Log;
import com.pingtech.hgqw.utils.StringUtils;
import com.pingtech.hgqw.utils.SystemSetting;
import com.pingtech.hgqw.web.NetWorkManager;
import com.pingtech.hgqw.widget.HgqwToast;

/**
 * 类描述：船方自管，船员名单
 * 
 * <p>
 * Title: 江海港边检勤务综合管理系统-CfzgSailorList.java
 * </p>
 * <p>
 * Copyright: Copyright (c) 2012
 * </p>
 * <p>
 * Company: 品恩科技
 * </p>
 * 
 * @author liums
 * @version 1.0
 * @date 2013-5-2 上午9:23:12
 */
public class CfzgSailorList extends CfzgSuperActivity implements OnHttpResult {
	private static final String TAG = "CfzgSailorList";

	/**
	 * 分页开始序号
	 */
	int start = 0;

	/**
	 * 每页显示数量
	 */
	int count = 5;

	/** 获取船员列表的请求 */
	private static final int HTTPREQUEST_TYPE_FOR_GETLIST = 1;

	/**
	 * 通行方向：上船
	 */
	private static final int TXFX_UP = 0;

	/**
	 * 通行方向：true上船、false下船
	 */
	private boolean sxcfxBoolean = true;

	/**
	 * 通行方向：下船
	 */
	private static final int TXFX_DOWN = 1;

	private ListView listView;

	/**
	 * 自定义适配器
	 */
	private GetCyxxListAdapter adapter;

	private ProgressDialog progressDialog = null;

	/**
	 * 当前绑定船舶的航次号
	 */
	private String hc = null;

	/**
	 * 列表数据详情List
	 */
	List<Map<String, String>> data = new ArrayList<Map<String, String>>();

	List<Map<String, String>> dataPage = new ArrayList<Map<String, String>>();

	private Handler handler = new MyHandler();

	private boolean cn = true;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreateForCfzg(savedInstanceState, R.layout.cfzg_cyxx_list);
		setContentView(R.layout.cfzg_cyxx_list, false);
		Log.i(TAG, "onCreate()");
		Intent intent = getIntent();
		hc = intent.getStringExtra("hc");
		cn = intent.getBooleanExtra("cn", true);
		if (hc == null || "".equals(hc.trim())) {
			HgqwToast.makeText(getApplicationContext(), "船员信息获取失败", HgqwToast.LENGTH_LONG).show();
		} else {
			onLoadSailorList(true);// 从服务器获取船员
		}
		// 中英识别
		// switchCNEN();
		((TextView) findViewById(R.id.current_dialog_title)).setTextSize(22);
		if (cn) {
			setMyActiveTitle("船员名单");
			listView = (ListView) findViewById(R.id.cfzg_list_listview);
			findViewById(R.id.cfzg_cyxx_list_layout_cn).setVisibility(View.VISIBLE);
			findViewById(R.id.cfzg_cyxx_list_layout_en).setVisibility(View.GONE);
		} else {
			setMyActiveTitle("Crew list");
			findViewById(R.id.cfzg_cyxx_list_layout_cn).setVisibility(View.GONE);
			findViewById(R.id.cfzg_cyxx_list_layout_en).setVisibility(View.VISIBLE);
			listView = (ListView) findViewById(R.id.cfzg_list_listview_en);
		}
		TextView titleTextView = (TextView) findViewById(R.id.cfzg_cyxx_list_title_count);
		titleTextView.setVisibility(View.VISIBLE);
		int size = data.size();
		titleTextView.setVisibility(View.VISIBLE);
		if (cn) {
			titleTextView.setText("船员总数:" + size);
		} else {
			titleTextView.setText("Crew number:" + size);
		}
		// 分页按钮
		adapter = new GetCyxxListAdapter(this, dataPage, handler, cn);
		// LayoutInflater layoutInflater = LayoutInflater.from(this);
		// View v = layoutInflater.inflate(R.layout.cfzg_listview_page_btn,
		// null);
		// v.setLayoutParams(new
		// android.widget.AbsListView.LayoutParams(LayoutParams.FILL_PARENT,
		// 36));
		// listView.addFooterView(v);
		listView.setAdapter(adapter);

		onInitSoundPool();
		onInitVibrator();
	}

	/**
	 * @方法名：switchCNEN
	 * @功能说明：中英文页面切换
	 * @author liums
	 * @date 2013-5-4 上午11:04:07
	 */
	private void switchCNEN() {
		TextView czsmTextView = (TextView) findViewById(R.id.cfzg_cyxx_list_czsm_textview);
		Button downButton = (Button) findViewById(R.id.cfzg_listview_page_down);
		Button upButton = (Button) findViewById(R.id.cfzg_listview_page_up);
		Button backButton = (Button) findViewById(R.id.cfzg_listview_page_back);
		if (cn) {
			czsmTextView.setText("请认准船员姓名，然后点击后面上船或下船，最后请点击返回");
			upButton.setText("上一页");
			downButton.setText("下一页");
			backButton.setText("返回");
		} else {
			czsmTextView.setTextSize(16);
			czsmTextView
					.setText("Please select the name of the crew and then click the embark or disembark button behind, at last please click the return button.");
			upButton.setText("Last page");
			downButton.setText("Next page");
			backButton.setText("Return");
		}
	}

	/**
	 * 类描述：Handler类，子线程与主界面交互
	 * 
	 * <p>
	 * Title: 江海港边检勤务综合管理系统-CfzgSailorList.java
	 * </p>
	 * <p>
	 * Copyright: Copyright (c) 2012
	 * </p>
	 * <p>
	 * Company: 品恩科技
	 * </p>
	 * 
	 * @author liums
	 * @version 1.0
	 * @date 2013-5-2 下午7:57:32
	 */
	class MyHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.arg1) {
			case TXFX_UP:// 上船按钮点击事件
				sxcfxBoolean = true;
				String hyidUp = (String) msg.obj;
				// 通行验证
				saveOrUpdateTktxjl(TXFX_UP, hyidUp);
				break;
			case TXFX_DOWN:// 下船按钮点击事件
				sxcfxBoolean = false;
				String hyidDown = (String) msg.obj;
				// 通行验证
				saveOrUpdateTktxjl(TXFX_DOWN, hyidDown);
				break;

			default:
				break;
			}
		}

	};

	/**
	 * @方法名：saveOrUpdateTktxjl
	 * @功能说明：
	 * @author liums
	 * @date 2013-5-2 下午7:56:03
	 * @param txfx
	 */
	private void saveOrUpdateTktxjl(int txfx, String hyid) {
		String url = "inspectForAcross";

		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("voyageNumber", hc));
		params.add(new BasicNameValuePair("cardNumber", hyid));
		params.add(new BasicNameValuePair("sfsk", YfZjxxConstant.ZJCX_SFSK_SDSR));
		params.add(new BasicNameValuePair("sfcy", "1")); //是否船员：0否、1是
		params.add(new BasicNameValuePair("PDACode", SystemSetting.getPDACode()));
		params.add(new BasicNameValuePair("acrossTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(System.currentTimeMillis()))));
		params.add(new BasicNameValuePair("userID", LoginUser.getCurrentLoginUser().getUserID()));
		if (progressDialog != null) {
			return;
		}
		progressDialog = new ProgressDialog(CfzgSailorList.this);
		progressDialog.setTitle(getString(R.string.waiting));
		progressDialog.setMessage(getString(R.string.waiting));
		progressDialog.setCancelable(false);
		progressDialog.setIndeterminate(false);
		progressDialog.show();
		NetWorkManager.request(this, url, params, FlagUrls.SAVE_OR_UPDATE_TKTXJL);
	}
	/**
	 * @方法名：saveOrUpdateTktxjl
	 * @功能说明：
	 * @author liums
	 * @date 2013-5-2 下午7:56:03
	 * @param txfx
	 */
	private void saveOrUpdateTktxjlHis(int txfx, String zjhm) {
		String url = "saveOrUpdateTktxjl";
		if (progressDialog != null) {
			return;
		}
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("userid", LoginUser.getCurrentLoginUser().getUserID()));
		params.add(new BasicNameValuePair("hc", hc));
		params.add(new BasicNameValuePair("txfx", txfx + ""));
		params.add(new BasicNameValuePair("PDACode", SystemSetting.getPDACode()));
		params.add(new BasicNameValuePair("zjhm", zjhm));
		progressDialog = new ProgressDialog(CfzgSailorList.this);
		progressDialog.setTitle(getString(R.string.waiting));
		progressDialog.setMessage(getString(R.string.waiting));
		progressDialog.setCancelable(false);
		progressDialog.setIndeterminate(false);
		progressDialog.show();
		NetWorkManager.request(this, url, params, FlagUrls.SAVE_OR_UPDATE_TKTXJL);
	}

	/**
	 * @方法名：onLoadSailorList
	 * @功能说明：获取船员数据
	 * @author liums
	 * @param flag
	 * @date 2013-5-2 上午9:54:31
	 */
	private void onLoadSailorList(boolean flag) {
		String url = "getCyxxList";
		if (progressDialog != null) {
			return;
		}
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("userid", LoginUser.getCurrentLoginUser().getUserID()));
		params.add(new BasicNameValuePair("hc", hc));
		if (flag) {
			progressDialog = new ProgressDialog(CfzgSailorList.this);
			progressDialog.setTitle(getString(R.string.waiting));
			progressDialog.setMessage(getString(R.string.waiting));
			progressDialog.setCancelable(false);
			progressDialog.setIndeterminate(false);
			progressDialog.show();
		}
		NetWorkManager.request(this, url, params, HTTPREQUEST_TYPE_FOR_GETLIST);
	}

	@Override
	public void onHttpResult(String str, int httpRequestType) {
		Log.i(TAG, "onHttpResult()httpRequestType:" + httpRequestType + ",result" + (str != null));

		if (progressDialog != null) {
			progressDialog.dismiss();
			progressDialog = null;
		}

		switch (httpRequestType) {
		case HTTPREQUEST_TYPE_FOR_GETLIST:
			setShipListContent(str);
			break;
		case FlagUrls.SAVE_OR_UPDATE_TKTXJL:
			setSailorTxjl(str);
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

	/**
	 * @方法名：setSailorTxjl
	 * @功能说明：处理船员上下船记录
	 * @author liums
	 * @date 2013-5-3 上午10:03:35
	 * @param str
	 */
	private void setSailorTxjl(String str) {
		if (str == null) {// 请求失败
			HgqwToast.getToastView(this, getString(R.string.data_download_failure_info)).show();
			// Toast.makeText(CfzgSailorList.this,
			// R.string.data_download_failure_info,
			// BaseToast.LENGTH_LONG).show();
			return;
		}

		SaveOrUpdateTktxjl saveOrUpdateTktxjl = null;
		try {
			saveOrUpdateTktxjl = PullXmlSaveOrUpdateTktxjl.pullXml(str);
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (saveOrUpdateTktxjl == null) {
			// 验证失败提示音
			if (cn) {
				HgqwToast.getToastView(this, "验证失败！").show();
				onPlaySound(3, 0);
			} else {
				HgqwToast.getToastView(this, "Verification failed!").show();
				onPlaySound(33, 0);
			}
		} else if ("success".equals(saveOrUpdateTktxjl.getResult())) {
			String sxcfx = "";
			if (sxcfxBoolean) {
				sxcfx = "上船";
			} else {
				sxcfx = "下船";
			}
			if (cn) {
				HgqwToast.getToastView(this, sxcfx + "，验证通过！").show();
				if (sxcfxBoolean) {
					onPlaySound(1, 0);// 成功提示音
				} else {
					onPlaySound(2, 0);// 成功提示音
				}
			} else {
				if (sxcfxBoolean) {
					HgqwToast.getToastView(this, "Embark, Verification Passed!").show();
					onPlaySound(11, 0);// 成功提示音
				} else {
					HgqwToast.getToastView(this, "Disembark, Verification Passed!").show();
					onPlaySound(22, 0);// 成功提示音
				}
			}

		} else {
			// 验证失败提示音
			if (cn) {
				HgqwToast.getToastView(this, "验证失败！").show();
				onPlaySound(3, 0);
			} else {
				HgqwToast.getToastView(this, "Verification failed!").show();
				onPlaySound(33, 0);
			}
		}
		// 刷新船员列表
		onLoadSailorList(false);
	}

	/**
	 * @方法名：setShipListContent
	 * @功能说明：获取船员列表处理
	 * @author liums
	 * @date 2013-5-3 上午10:01:54
	 * @param str
	 */
	private void setShipListContent(String str) {
		if (str != null) {
			// 解析xml
			try {
				Map<String, Object> map = PullXmlShipList.pullXml(str);
				if ("success".equals(map.get("result"))) {
					List<Cyxx> cyxxList = (List<Cyxx>) map.get("cyxxList");
					if (cyxxList != null && cyxxList.size() > 0) {
						data.clear();
						// findViewById(R.id.cfzg_cyxx_list_bottom_layout).setVisibility(View.VISIBLE);
						findViewById(R.id.cfzg_cyxx_list_listview_layout).setVisibility(View.VISIBLE);
						int i = 1;
						for (Cyxx c : cyxxList) {
							Map<String, String> dataMap = new HashMap<String, String>();
							dataMap.put("xuhao", i + ".");
							dataMap.put("xm", c.getXm());
							if (cn) {
								dataMap.put("zw", DataDictionary.getDataDictionaryName(c.getZw(), DataDictionary.DATADICTIONARY_TYPE_CBYGZW));
							} else {
								dataMap.put("zw", DataDictionary.getDataDictionaryName(c.getZw(), DataDictionary.DATADICTIONARY_TYPE_CBYGZW_EN));
							}
							dataMap.put("zjhm", c.getZjhm());
							dataMap.put("hyid", c.getHyid());
							// 设置船员位置，默认为“在船上0”
							dataMap.put("cywz", StringUtils.isEmpty(c.getCywz()) ? "0" : c.getCywz());
							i++;
							data.add(dataMap);
							dataMap = null;
						}
						setCyxxListContent();
					} else {
						// findViewById(R.id.cfzg_cyxx_list_bottom_layout).setVisibility(View.GONE);
						findViewById(R.id.cfzg_cyxx_list_listview_layout).setVisibility(View.GONE);
						HgqwToast.getToastView(CfzgSailorList.this, "船员信息为空").show();
					}
				} else if ("error".equals(map.get("result"))) {
					HgqwToast.getToastView(CfzgSailorList.this, "船员信息为空").show();
				}

			} catch (XmlPullParserException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			HgqwToast.getToastView(CfzgSailorList.this, getString(R.string.data_download_failure_info)).show();
		}

	}

	/**
	 * @方法名：setCyxxListContent
	 * @功能说明：设置船员列表，分页显示
	 * @author liums
	 * @date 2013-5-2 下午7:38:00
	 */
	private void setCyxxListContent() {
		dataPage.clear();
		int size = data.size();
		TextView titleTextView = (TextView) findViewById(R.id.cfzg_cyxx_list_title_count);
		titleTextView.setVisibility(View.VISIBLE);
		if (cn) {
			titleTextView.setText("船员总数:" + size);
		} else {
			titleTextView.setText("Crew number:" + size);
		}
		// 第一页，上一页按钮置灰
		if (start == 0) {
			findViewById(R.id.cfzg_listview_page_up).setEnabled(false);
			findViewById(R.id.cfzg_listview_page_up_en).setEnabled(false);
		} else {
			findViewById(R.id.cfzg_listview_page_up).setEnabled(true);
			findViewById(R.id.cfzg_listview_page_up_en).setEnabled(true);
		}

		// 最后一页，下一页按钮置灰
		if (size - start <= count) {
			findViewById(R.id.cfzg_listview_page_down).setEnabled(false);
			findViewById(R.id.cfzg_listview_page_down_en).setEnabled(false);
			count = size - start;// 请求个数赋值
		} else {
			findViewById(R.id.cfzg_listview_page_down).setEnabled(true);
			findViewById(R.id.cfzg_listview_page_down_en).setEnabled(true);
		}
		for (int j = start; j < count + start; j++) {
			dataPage.add(data.get(j));
		}
		count = 5;// 每页显示数量复位
		adapter.notifyDataSetChanged();
	}

	/**
	 * @方法名：cfzgButtonClick
	 * @功能说明：按钮点击事件
	 * @author liums
	 * @date 2013-4-27 下午3:18:37
	 * @param v
	 */
	public void cfzgButtonClick(View v) {
		switch (v.getId()) {
		case R.id.cfzg_listview_page_up:// 上一页
			if (start > count) {
				start = start - count;
			} else {
				start = 0;
			}
			setCyxxListContent();
			break;
		case R.id.cfzg_listview_page_up_en:// 上一页 en
			if (start > count) {
				start = start - count;
			} else {
				start = 0;
			}
			setCyxxListContent();
			break;
		case R.id.cfzg_listview_page_down:// 下一页
			start = start + count;
			setCyxxListContent();
			break;
		case R.id.cfzg_listview_page_down_en:// 下一页 en
			start = start + count;
			setCyxxListContent();
			break;
		case R.id.cfzg_listview_page_back:// 返回上层页面
			finish();
			break;
		default:
			break;
		}
	}

	/* 通用程序：验证提示音、验证震动 */
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
		// hm.put(4, sp.load(getApplicationContext(), R.raw.ic_mag, 1));
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
		float currentSound = am.getStreamVolume(AudioManager.STREAM_MUSIC);
		float maxSound = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
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
		float currentSound = am.getStreamVolume(AudioManager.STREAM_MUSIC);
		float maxSound = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		volume = currentSound / maxSound;
		// sp.play(hm.get(num), volume, volume, 1, loop, 1.0f);
		sp.play(hm.get(num), maxSound, maxSound, 1, loop, 1.0f);
	}

	/* 音频、震动程序结束 */

	@Override
	protected void onDestroy() {
		// 释放声音、震动设备
		onCloseVibrator();
		onUnInitSoundPool();
		super.onDestroy();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// 从对话框返回
		if (requestCode == FlagManagers.CUSTOM_DIALOG && resultCode == RESULT_OK) {
			// this.sendAlarmInfo();
		} else if (requestCode == FlagManagers.CUSTOM_DIALOG_FOR_EXIT && resultCode == RESULT_OK) {
			String password = data.getStringExtra("password");
			this.validatePassword(password);
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
		// return super.onKeyDown(keyCode, event);
		android.util.Log.i(TAG, "onKeyDown:" + keyCode);
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:// 返回键
			// dialogActivityForExit();
			return super.onKeyDown(keyCode, event);
		case KeyEvent.KEYCODE_HOME:
			// dialogActivityForExit();
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
		Intent intent = new Intent();

		intent.setClass(getApplicationContext(), CfzgCustomDialogForExit.class);
		intent.putExtra("cn", cn);
		startActivityForResult(intent, FlagManagers.CUSTOM_DIALOG_FOR_EXIT);
	}
}
