package com.pingtech.hgqw.module.tikou.activity;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.xmlpull.v1.XmlPullParserException;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.pingtech.R;
import com.pingtech.hgqw.activity.MyActivity;
import com.pingtech.hgqw.adapter.CyxxListAdapter;
import com.pingtech.hgqw.base.BaseApplication;
import com.pingtech.hgqw.entity.Cyxx;
import com.pingtech.hgqw.entity.LoginUser;
import com.pingtech.hgqw.entity.SaveOrUpdateTktxjl;
import com.pingtech.hgqw.interf.OffLineResult;
import com.pingtech.hgqw.interf.OnHttpResult;
import com.pingtech.hgqw.module.offline.base.utils.OffLineManager;
import com.pingtech.hgqw.module.offline.txjl.action.TxjlTkAction;
import com.pingtech.hgqw.module.tikou.action.TkglAction;
import com.pingtech.hgqw.module.tikou.entity.PersonInfo;
import com.pingtech.hgqw.module.tikou.utils.PullXmlTiKou;
import com.pingtech.hgqw.module.xtgl.activity.FunctionSetting;
import com.pingtech.hgqw.pullxml.PullXmlSaveOrUpdateTktxjl;
import com.pingtech.hgqw.pullxml.PullXmlShipList;
import com.pingtech.hgqw.utils.BasicNameValuePair;
import com.pingtech.hgqw.utils.DataDictionary;
import com.pingtech.hgqw.utils.Log;
import com.pingtech.hgqw.utils.NVPairTOMap;
import com.pingtech.hgqw.utils.StringUtils;
import com.pingtech.hgqw.utils.SystemSetting;
import com.pingtech.hgqw.web.NetWorkManager;
import com.pingtech.hgqw.widget.HgqwToast;

/**
 * 
 * 
 * 类描述：梯口管理-刷卡登记-船员名单
 * 
 * <p>
 * Title: 系统名称-TikoucymdActivity.java
 * </p>
 * <p>
 * Copyright: Copyright (c) 2012
 * </p>
 * <p>
 * Company: 品恩科技
 * </p>
 * 
 * @author jiajw
 * @version 1.0
 * @date 2014-2-12 下午2:15:45
 */
public class TikoucymdActivity extends MyActivity implements OnHttpResult, OffLineResult {

	private static final String TAG = "TikoucymdActivity";

	/* 分页 */
	/**
	 * 分页开始序号
	 */
	int start = 0;

	/**
	 * 每页显示数量
	 */
	int count = 5;

	/* 通用程序：验证提示：音频提示、振动提示 */

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

	/* 请求服务器参数 */

	/**
	 * 获取船员列表的请求
	 */
	private static final int HTTPREQUEST_TYPE_FOR_GETLIST = 1;

	/**
	 * 船员上下船验证请求
	 */
	private static final int HTTPREQUEST_TYPE_FOR_UPDOWM = 2;

	/**
	 * 通行方向：0:上船
	 */
	private static final int TXFX_UP = 0;

	/**
	 * 通行方向：1:下船
	 */
	private static final int TXFX_DOWN = 1;

	private String defaultickey = "";

	/**
	 * 通行方向：true上船、false下船
	 */
	private boolean sxcfxBoolean = true;

	/* 界面组件件 */
	private ListView listView;

	private Button upBtn, downBtn;

	/**
	 * 自定义适配器
	 */
	private CyxxListAdapter adapter;

	private ProgressDialog progressDialog = null;

	private boolean needShowDialog = true;

	/**
	 * 当前绑定船舶的航次号
	 */
	private String hc = null;

	/**
	 * 保存查询结果
	 */
	List<Map<String, String>> data = new ArrayList<Map<String, String>>();

	List<Map<String, String>> dataPage = new ArrayList<Map<String, String>>();

	private Handler handler = new mHandler();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.tkgl_cyxx_listview);
		Log.i(TAG, "onCreate()");
		Intent intent = getIntent();
		hc = intent.getStringExtra("hc");
		initView();
		adapter = new CyxxListAdapter(this, dataPage, handler);
		listView.setAdapter(adapter);
		bindData();

	}

	/**
	 * 
	 * @方法名：bindData
	 * @功能说明：初始化数据
	 * @author jiajw
	 * @date 2014-2-12 下午2:17:23
	 */
	private void bindData() {
		data.clear();
		dataPage.clear();
		if (hc == null || "".equals(hc.trim())) {
			// 船员信息获取失败
			HgqwToast.makeText(getApplicationContext(), getString(R.string.cyxx_failture), HgqwToast.LENGTH_LONG).show();
		} else {
			// 从服务器获取船员信息

			if (!getState(FunctionSetting.bdtxyz, true)) {
				if (progressDialog != null) {
					return;
				}
				String url = "getCyxxList";
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("userid", LoginUser.getCurrentLoginUser().getUserID()));
				params.add(new BasicNameValuePair("hc", hc));
				if (progressDialog != null) {
					return;
				}
				if (needShowDialog) {
					showDialog().show();
				}

				NetWorkManager.request(TikoucymdActivity.this, url, params, HTTPREQUEST_TYPE_FOR_GETLIST);

			} else {
				String url = "getLxCyxxList";
				if (progressDialog != null) {
					return;
				}
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("hc", hc));
				OffLineManager.request(TikoucymdActivity.this, new TxjlTkAction(), url, NVPairTOMap.nameValuePairTOMap(params),
						HTTPREQUEST_TYPE_FOR_GETLIST);

			}
		}
	}

	/**
	 * 
	 * @方法名：showDialog
	 * @功能说明：请求服务时弹出dialog,提示正在请求，请稍后
	 * @author jiajw
	 * @date 2014-2-12 下午4:26:27
	 * @return
	 */
	private ProgressDialog showDialog() {
		progressDialog = new ProgressDialog(this);
		progressDialog.setTitle(getString(R.string.waiting));
		progressDialog.setMessage(getString(R.string.waiting));
		progressDialog.setCancelable(false);
		progressDialog.setIndeterminate(false);
		return progressDialog;

	}

	/**
	 * 
	 * @方法名：initView
	 * @功能说明：初始化界面组件
	 * @author jiajw
	 * @date 2014-2-12 下午2:18:20
	 */
	private void initView() {
		setMyActiveTitle(getString(R.string.tikoumanager) + ">" + getString(R.string.paycard) + ">" + getString(R.string.tkgl_sailor_list));
		listView = (ListView) findViewById(R.id.tkgl_list_listview);
		upBtn = (Button) findViewById(R.id.tkgl_listview_page_up);
		downBtn = (Button) findViewById(R.id.tkgl_listview_page_down);

		onInitSoundPool();
		onInitVibrator();
	}

	/**
	 * 处理平台返回的数据
	 */
	@Override
	public void onHttpResult(String str, int httpRequestType) {
		Log.i(TAG, "onHttpResult()httpRequestType:" + httpRequestType + ",result" + (str != null));

		if (progressDialog != null) {
			progressDialog.dismiss();
			progressDialog = null;
		}
		needShowDialog = true;
		switch (httpRequestType) {
		case HTTPREQUEST_TYPE_FOR_GETLIST:
			setShipListContent(str);
			break;
		case HTTPREQUEST_TYPE_FOR_UPDOWM:
			setSailorTxjl(str);
			break;
		}

	}

	/**
	 * 
	 * @方法名：setShipListContent
	 * @功能说明：处理平台返回的船员数据
	 * @author jiajw
	 * @date 2014-2-12 下午4:57:00
	 * @param str
	 */
	private void setShipListContent(String str) {

		if (str != null) {
			try {
				// 解析xml
				Map<String, Object> map = PullXmlShipList.pullXml(str);
				if ("success".equals(map.get("result"))) {
					@SuppressWarnings("unchecked")
					List<Cyxx> cyxxList = (List<Cyxx>) map.get("cyxxList");

					if (cyxxList != null && cyxxList.size() > 0) {
						data.clear();
						dataPage.clear();
						int i = 1;
						for (Cyxx c : cyxxList) {
							Map<String, String> dataMap = new HashMap<String, String>();

							dataMap.put("xuhao", i + ".");// 序号
							dataMap.put("xm", c.getXm());// 姓名
							dataMap.put("zw", DataDictionary.getDataDictionaryName(c.getZw(), DataDictionary.DATADICTIONARY_TYPE_CBYGZW));// 职务
							dataMap.put("zjhm", c.getZjhm());// 证件号码
							dataMap.put("cywz", c.getCywz());// 船员位置
							dataMap.put("hyid", c.getHyid());// 海员id
							i++;
							data.add(dataMap);
							dataMap = null;
						}
						setCyxxListContent();
					} else {
						// 船员信息为空
						HgqwToast.getToastView(TikoucymdActivity.this, getString(R.string.cyxx_null)).show();
					}
				} else if ("error".equals(map.get("result"))) {
					HgqwToast.getToastView(TikoucymdActivity.this, getString(R.string.cyxx_null)).show();
				}

			} catch (XmlPullParserException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			// 网络连接失败，请稍后再试
			HgqwToast.getToastView(TikoucymdActivity.this, getString(R.string.data_download_failure_info)).show();
		}

	}

	/**
	 * 
	 * @方法名：setSailorTxjl
	 * @功能说明：处理平台返回的船员上下船记录
	 * @author jiajw
	 * @date 2014-2-12 下午2:43:30
	 * @param str
	 */
	private void setSailorTxjl(String str) {
		if (str == null) {
			// 请求失败
			HgqwToast.getToastView(this, getString(R.string.data_download_failure_info)).show();
			return;
		}

		if (StringUtils.isNotEmpty(str)) {
			if (!getState(FunctionSetting.bdtxyz, true)) {
				onLineRepDeal(str);
				return;
			}
			PersonInfo personInfo = PullXmlTiKou.parseXMLData(str);
			if (personInfo != null) {
				// 1：通过；2：不通过
				if (personInfo.getSuccessFlag() == 1) {
					String sxcfx = "";
					if (sxcfxBoolean) {
						// 上船
						sxcfx = getString(R.string.cyxx_upship);
					} else {
						// 下船
						sxcfx = getString(R.string.cyxx_downship);
					}
					// 验证通过
					HgqwToast.getToastView(this, sxcfx + "，" + getString(R.string.cyxx_updown_ship_succ)).show();
					if (sxcfxBoolean) {
						onPlaySound(1, 0);// 成功提示音
					} else {
						onPlaySound(2, 0);// 成功提示音
					}
					needShowDialog = false;
					// 刷新船员列表
					bindData();
					return;
				}
			}
		}
		// 验证失败提示音
		HgqwToast.getToastView(this, getString(R.string.cyxx_updown_ship_fail)).show();
		onPlaySound(3, 0);

		// String url = "getCyxxList";
		// List<NameValuePair> params = new ArrayList<NameValuePair>();
		// params.add(new BasicNameValuePair("userid", LoginUser
		// .getCurrentLoginUser().getUserID()));
		// params.add(new BasicNameValuePair("hc", hc));
		//
		// NetWorkManager.request(TikoucymdActivity.this, url, params,
		// HTTPREQUEST_TYPE_FOR_GETLIST);
	}

	/**
	 * 
	 * @方法名：onLineRepDeal
	 * @功能说明：在线情况下，修改通行记录后，处理 “返回的数据”的方法
	 * @author zhaotf
	 * @date 2014-2-17 下午5:06:16
	 * @param str
	 */
	private void onLineRepDeal(String str) {
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
			HgqwToast.getToastView(this, getString(R.string.cyxx_updown_ship_fail)).show();
			onPlaySound(3, 0);

		} else if ("success".equals(saveOrUpdateTktxjl.getResult())) {
			String sxcfx = "";
			if (sxcfxBoolean) {
				// 上船
				sxcfx = getString(R.string.cyxx_upship);
			} else {
				// 下船
				sxcfx = getString(R.string.cyxx_downship);
			}
			// 验证通过
			HgqwToast.getToastView(this, sxcfx + "," + getString(R.string.cyxx_updown_ship_succ)).show();
			if (sxcfxBoolean) {
				onPlaySound(1, 0);// 成功提示音
			} else {
				onPlaySound(2, 0);// 成功提示音
			}

		} else {
			// 验证失败提示音
			HgqwToast.getToastView(this, getString(R.string.cyxx_updown_ship_fail)).show();
			onPlaySound(3, 0);

		}
		needShowDialog = false;
		// 刷新类别
		// adapter.notifyDataSetChanged();
		bindData();
	}

	/**
	 * 
	 * @方法名：setCyxxListContent
	 * @功能说明：设置船员列表，分页显示
	 * @author jiajw
	 * @date 2014-2-12 下午5:06:58
	 */
	private void setCyxxListContent() {
		dataPage.clear();
		int size = data.size();

		// 第一页，上一页按钮置灰
		if (start == 0) {
			upBtn.setEnabled(false);
		} else {
			upBtn.setEnabled(true);
		}

		// 最后一页，下一页按钮置灰
		if (size - start <= count) {
			downBtn.setEnabled(false);
			count = size - start;// 请求个数赋值
		} else {
			downBtn.setEnabled(true);
		}
		for (int j = start; j < count + start; j++) {
			dataPage.add(data.get(j));
		}
		count = 5;// 每页显示数量复位
		adapter.notifyDataSetChanged();

	}

	/**
	 * 处理船舶上下船验证请求线程
	 */
	@SuppressLint("HandlerLeak")
	public class mHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			@SuppressWarnings("unchecked")
			Map<String, String> dataItem = (Map<String, String>) msg.obj;
			String zjhm = "";
			String hyid = "";
			if (null != dataItem) {
				zjhm = dataItem.get("zjhm");
				hyid = dataItem.get("hyid");
			}

			super.handleMessage(msg);
			switch (msg.arg1) {
			// 上船按钮单击事件
			case TXFX_UP:
				sxcfxBoolean = true;// 上船
				// 通行验证
				// if (!getState(FunctionSetting.bdtxyz, true)) {
				// saveOrUpdateTktxjl(TXFX_UP, StringUtils.isEmpty(zjhm) ? "" :
				// zjhm);
				// } else {
				saveOrUpdateTktxjl(TXFX_UP, StringUtils.isEmpty(hyid) ? "" : hyid);
				// }

				break;
			// 下船按钮点击事件
			case TXFX_DOWN:
				sxcfxBoolean = false;
				// 通行验证
				// if (!getState(FunctionSetting.bdtxyz, true)) {
				// saveOrUpdateTktxjl(TXFX_DOWN, StringUtils.isEmpty(zjhm) ? ""
				// : zjhm);
				// } else {
				saveOrUpdateTktxjl(TXFX_DOWN, StringUtils.isEmpty(hyid) ? "" : hyid);
				// }

				break;
			default:
				break;
			}
		}

	};

	/**
	 * 
	 * @方法名：saveOrUpdateTktxjl
	 * @功能说明：通行验证服务请求
	 * @author jiajw
	 * @date 2014-2-12 下午5:00:35
	 * @param txfx
	 * @param zjhm
	 */
	private void saveOrUpdateTktxjl(int txfx, String zjhm) {

		if (progressDialog != null) {
			return;
		}
		String url = "inspectForAcross";
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("voyageNumber", hc));
		// cardNumber及为hyid
		params.add(new BasicNameValuePair("cardNumber", zjhm));
		params.add(new BasicNameValuePair("defaultickey", defaultickey));
		params.add(new BasicNameValuePair("sfsk", "1"));
		params.add(new BasicNameValuePair("PDACode", SystemSetting.getPDACode()));
		params.add(new BasicNameValuePair("sfcy", "1"));
		params.add(new BasicNameValuePair("acrossTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(System.currentTimeMillis()))));
		params.add(new BasicNameValuePair("userID", LoginUser.getCurrentLoginUser().getUserID()));
		showDialog().show();
		if (!getState(FunctionSetting.bdtxyz, true)) {
			NetWorkManager.request(this, url, params, HTTPREQUEST_TYPE_FOR_UPDOWM);
		} else {
			OffLineManager.request(this, new TkglAction(), url, NVPairTOMap.nameValuePairTOMap(params), HTTPREQUEST_TYPE_FOR_UPDOWM);

		}
	}

	@Override
	public void offLineResult(Pair<Boolean, Object> res, int offLineRequestType) {

		if (progressDialog != null) {
			progressDialog.dismiss();
			progressDialog = null;
		}
		needShowDialog = true;
		if (res != null) {

			switch (offLineRequestType) {

			case HTTPREQUEST_TYPE_FOR_GETLIST:
				@SuppressWarnings("unchecked")
				List<Map<String, String>> cyxxList = (List<Map<String, String>>) res.second;
				// cyxxList.get(i).get("xm")
				if (cyxxList != null && cyxxList.size() > 0) {
					int i = 1;
					for (Map<String, String> c : cyxxList) {
						Map<String, String> dataMap = new HashMap<String, String>();
						dataMap.put("xuhao", i + ".");// 序号
						dataMap.put("xm", c.get("xm"));// 姓名
						dataMap.put("zw", DataDictionary.getDataDictionaryName(c.get("zw"), DataDictionary.DATADICTIONARY_TYPE_CBYGZW));// 职务
						dataMap.put("zjhm", c.get("zjhm"));// 证件号码
						dataMap.put("hyid", c.get("hyid"));
						String txfx = c.get("txfx");// 船员位置
						if (txfx != null) {
							if (txfx.equals("0")) {
								dataMap.put("cywz", "0");
							} else if (txfx.equals("1")) {
								dataMap.put("cywz", "1");
							} else {
								dataMap.put("cywz", "0");
							}
						} else {
							// 为空的话表示0，在船上,上船按钮置灰
							dataMap.put("cywz", "0");
						}
						i++;
						data.add(dataMap);
						dataMap = null;
					}
					setCyxxListContent();

					break;
				}
				break;

			case HTTPREQUEST_TYPE_FOR_UPDOWM:

				String str = res.second.toString();
				setSailorTxjl(str);

				break;
			}

		} else {
			HgqwToast.makeText(TikoucymdActivity.this, R.string.no_data, HgqwToast.LENGTH_LONG).show();

		}
	}

	/**
	 * 按钮单击事件
	 */
	/**
	 * 
	 * @方法名：tkglButtonClick
	 * @功能说明：上一页，下一页，返回按钮的单击事件
	 * @author jiajw
	 * @date 2014-2-12 下午5:13:52
	 * @param v
	 */
	public void tkglButtonClick(View v) {
		switch (v.getId()) {
		case R.id.tkgl_listview_page_up:// 上一页
			if (start > count) {
				start = start - count;
			} else {
				start = 0;
			}
			setCyxxListContent();
			break;

		case R.id.tkgl_listview_page_down:// 下一页
			start = start + count;
			setCyxxListContent();
			break;

		case R.id.tkgl_listview_page_back:// 返回上层页面
			if (progressDialog != null) {
				return;
			}
			TikoucymdActivity.this.finish();
			break;
		default:
			break;
		}
	}

	/**
	 * 初始化音频池
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

	/** 初始震动源，用于提示刷卡未通过验证时 */
	private void onInitVibrator() {
		vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
	}

	@Override
	protected void onDestroy() {
		// 释放声音、震动设备
		onCloseVibrator();
		onUnInitSoundPool();
		data.clear();
		dataPage.clear();
		super.onDestroy();
	}

	/**
	 * 
	 * @方法名：onCloseVibrator
	 * @功能说明：关闭振动源
	 * @author jiajw
	 * @date 2014-2-12 下午3:01:21
	 */
	private void onCloseVibrator() {
		vibrator.cancel();

	}

	/**
	 * 
	 * @方法名：onUnInitSoundPool
	 * @功能说明：是否音频资源
	 * @author jiajw
	 * @date 2014-2-12 下午3:02:10
	 */
	private void onUnInitSoundPool() {
		sp = null;
		hm = null;

	}

	/**
	 * 
	 * @方法名：onPlaySound
	 * @功能说明：播放提示声音，附带振动
	 * @author jiajw
	 * @date 2014-2-12 下午2:46:40
	 * @param i
	 * @param j
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
	 * 
	 * @方法名：onPlayVibrator
	 * @功能说明：开启振动
	 * @author jiajw
	 * @date 2014-2-12 下午2:48:13
	 */
	private void onPlayVibrator() {
		long[] pattern = { 100, 900 };
		AudioManager am = (AudioManager) BaseApplication.instent.getSystemService(Context.AUDIO_SERVICE);
		float currentSound = am.getStreamVolume(AudioManager.STREAM_MUSIC);
		float maxSound = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		volume = currentSound / maxSound;
		if (vibrator != null && volume > 0) {
			vibrator.vibrate(pattern, -1);
		}
	}

}
