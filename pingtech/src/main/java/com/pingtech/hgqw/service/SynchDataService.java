package com.pingtech.hgqw.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.NameValuePair;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;

import com.pingtech.R;
import com.pingtech.hgqw.base.BaseApplication;
import com.pingtech.hgqw.entity.Flags;
import com.pingtech.hgqw.entity.GlobalFlags;
import com.pingtech.hgqw.interf.OnHttpResult;
import com.pingtech.hgqw.module.offline.bwdm.entity.Bwdm;
import com.pingtech.hgqw.module.offline.bwdm.service.BwdmService;
import com.pingtech.hgqw.module.offline.cyxx.entity.TBCyxx;
import com.pingtech.hgqw.module.offline.cyxx.service.CyxxService;
import com.pingtech.hgqw.module.offline.fwxcb.entity.Fwxcb;
import com.pingtech.hgqw.module.offline.fwxcb.service.FwxcbService;
import com.pingtech.hgqw.module.offline.hgzjxx.entity.Hgzjxx;
import com.pingtech.hgqw.module.offline.hgzjxx.service.HgzjxxService;
import com.pingtech.hgqw.module.offline.kacbqk.entity.Kacbqk;
import com.pingtech.hgqw.module.offline.kacbqk.service.KacbqkService;
import com.pingtech.hgqw.module.offline.mtdm.entity.Mtdm;
import com.pingtech.hgqw.module.offline.mtdm.service.MtdmService;
import com.pingtech.hgqw.module.offline.pzxx.request.RequestPzxx;
import com.pingtech.hgqw.module.offline.qyxx.entity.Qyxx;
import com.pingtech.hgqw.module.offline.qyxx.service.QyxxService;
import com.pingtech.hgqw.module.offline.sbxx.entity.Sbxx;
import com.pingtech.hgqw.module.offline.sbxx.service.SbxxService;
import com.pingtech.hgqw.module.offline.scsb.entity.Scsb;
import com.pingtech.hgqw.module.offline.scsb.service.ScsbService;
import com.pingtech.hgqw.module.offline.sxtgl.entity.Sxtgl;
import com.pingtech.hgqw.module.offline.sxtgl.service.SxtglService;
import com.pingtech.hgqw.module.offline.userinfo.entity.TBUserinfo;
import com.pingtech.hgqw.module.offline.userinfo.service.TBUserinfoService;
import com.pingtech.hgqw.module.offline.util.OffLineUtil;
import com.pingtech.hgqw.module.xtgl.service.OffDataDownloadForBd;
import com.pingtech.hgqw.utils.BasicNameValuePair;
import com.pingtech.hgqw.utils.Log;
import com.pingtech.hgqw.utils.StringUtils;
import com.pingtech.hgqw.utils.SystemSetting;
import com.pingtech.hgqw.utils.xml.PullXmlUtils;
import com.pingtech.hgqw.web.NetWorkManager;

public class SynchDataService extends Service implements OnHttpResult {
	private static final String TAG = "SynchDataService";

	private static boolean flag = true;

	public static Handler handler = null;

	private ImageDownload imageDownload = null;

	@Override
	public void onCreate() {
		super.onCreate();
		Log.i(TAG, "onCreate");
		flag = true;
		handler = new MyHandler();
		lopperThread();
	}

	@Override
	public IBinder onBind(Intent arg0) {
		Log.i(TAG, "onBind");
		flag = true;
		// 启动服务
		// startStartServices();
		handler = new MyHandler();

		return null;
	}

	private Timer timer;

	private TimerTask timerTaskForHgzjxx;

	private TimerTask timerTaskForKakou;

	private TimerTask timerTaskForTkglOrXcxj;

	private TimerTask timerTaskForXcDdbd;

	private TimerTask timerTaskForZp;

	private TimerTask timerTaskForPzjl;

	private class MyHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case OffLineUtil.DOWNLOAD_CQZ_MT_BW_QY:
				updateCqz();
				break;
			case OffLineUtil.DOWNLOAD_FOR_KACBQK:
				updateForTikouOrXcXj();
				break;
			case OffLineUtil.DOWNLOAD_FOR_QYXX:
				updateForKakou();
				break;
			case OffLineUtil.DOWNLOAD_FOR_MT_OR_BW_QY:
				updateForXcDdbd();
				break;
			case OffLineUtil.RESULT_CODE_SENT_OFFLINE_HGZJXX_ZP:
				updateZp();
				break;
			case -100:// StringUtils.isEmpty(res)
				imageDownload.stop();
				break;
			case 100:// "OK".equals(res)
				imageDownload.stop();
				break;
			case 100000:// 下载图片完成，结束服务
				Log.i(TAG, "handleMessage, what = 1000");
				stopService(new Intent(SynchDataService.this, ImageDownloadService.class));
				break;
			case OffLineUtil.RESULT_CODE_SENT_OFFLINE_PZXX:
				updatePzxx();
				break;
			default:
				break;
			}
		}
	}

	private void updateZp() {
		Log.i(TAG, "again updateZp");
		// imageDownload = new ImageDownload(handler);
		// imageDownload.startDownload();
		stopService(new Intent(this, ImageDownloadService.class));
		startService(new Intent(this, ImageDownloadService.class));
	}

	/**
	 * 下载更新报警提示信息
	 */
	public void updatePzxx() {
		Log.i(TAG, "updatePzxx");
		new RequestPzxx().request();
	}

	private void lopperThread() {
		stopTimer();
		timer = new Timer();
		timerTaskForHgzjxx = new TimerTask() {

			@Override
			public void run() {
				// updateCqz();
				handler.obtainMessage(OffLineUtil.DOWNLOAD_CQZ_MT_BW_QY).sendToTarget();
			}
		};

		timerTaskForTkglOrXcxj = new TimerTask() {
			@Override
			public void run() {
				// updateForTikou();
				handler.obtainMessage(OffLineUtil.DOWNLOAD_FOR_KACBQK).sendToTarget();
			}
		};

		timer.schedule(timerTaskForHgzjxx, 0, 10 * 60 * 1000);
		timer.schedule(timerTaskForTkglOrXcxj, 30 * 1000, 5 * 60 * 1000);

		// 离线碰撞记录定时下载
		timerTaskForPzjl = new TimerTask() {
			@Override
			public void run() {
				handler.obtainMessage(OffLineUtil.RESULT_CODE_SENT_OFFLINE_PZXX).sendToTarget();
			}
		};
		timer.schedule(timerTaskForPzjl, 35 * 1000, 30 * 60 * 1000);

		switch (Flags.PDA_VERSION) {
		case Flags.PDA_VERSION_DEFAULT:
			timerTaskForXcDdbd = new TimerTask() {
				@Override
				public void run() {
					// updateForTikou();
					handler.obtainMessage(OffLineUtil.DOWNLOAD_FOR_MT_OR_BW_QY).sendToTarget();
				}
			};
			timer.schedule(timerTaskForXcDdbd, 45 * 1000, 5 * 60 * 1000);
			break;

		case Flags.PDA_VERSION_SENTINEL:
			timerTaskForKakou = new TimerTask() {
				@Override
				public void run() {
					handler.obtainMessage(OffLineUtil.DOWNLOAD_FOR_QYXX).sendToTarget();
				}
			};
			timer.schedule(timerTaskForKakou, 45 * 1000, 5 * 60 * 1000);
			break;

		default:
			break;
		}

		// 下载离线照片数据
		timerTaskForZp = new TimerTask() {
			@Override
			public void run() {
				Log.i(TAG, "BaseApplication.instent.imageDownlond = " + BaseApplication.instent.imageDownlond);
				handler.obtainMessage(OffLineUtil.RESULT_CODE_SENT_OFFLINE_HGZJXX_ZP).sendToTarget();
			}
		};
		timer.schedule(timerTaskForZp, 1 * 1000, 30 * 60 * 1000);

	}

	public void updateForXcDdbd() {
		Log.i(TAG, "again updateForXcDdbd");
		if (StringUtils.isNotEmpty(SystemSetting.xunJianId)) {
			Log.i(TAG, "againg xuncha isNotEmpty(SystemSetting.xunJianId)");
			OffDataDownloadForBd dataDownload = new OffDataDownloadForBd(handler, null, OffLineUtil.DOWNLOAD_FOR_MT_OR_BW_QY);
			dataDownload.requestAgain();
		} else {
			Log.i(TAG, "againg xuncha isEmpty(SystemSetting.xunJianId)");
		}
	}

	private void stopTimer() {
		if (timer != null) {
			timer.cancel();
			timer = null;
		}

		if (timerTaskForHgzjxx != null) {
			timerTaskForHgzjxx.cancel();
			timerTaskForHgzjxx = null;
		}
		if (timerTaskForTkglOrXcxj != null) {
			timerTaskForTkglOrXcxj.cancel();
			timerTaskForTkglOrXcxj = null;
		}
		if (timerTaskForKakou != null) {
			timerTaskForKakou.cancel();
			timerTaskForKakou = null;
		}
		if (timerTaskForXcDdbd != null) {
			timerTaskForXcDdbd.cancel();
			timerTaskForXcDdbd = null;
		}
		if (timerTaskForZp != null) {
			timerTaskForZp.cancel();
			timerTaskForZp = null;
		}

		if (timerTaskForPzjl != null) {
			timerTaskForPzjl.cancel();
			timerTaskForPzjl = null;
		}

	}

	/**
	 * 
	 * @方法名：updateCqz
	 * @功能说明：更新长期证
	 * @author liums
	 * @date 2014-5-5 上午10:13:05
	 */
	private void updateCqz() {
		Log.i(TAG, "again updateCqz");
		// OffDataDownloadForBd dataDownload = new OffDataDownloadForBd(handler,
		// new HashMap<String, Object>(), OffLineUtil.UPDATE_CQZ);
		OffDataDownloadForBd dataDownload = new OffDataDownloadForBd(handler, new HashMap<String, Object>(), OffLineUtil.DOWNLOAD_CQZ_MT_BW_QY);
		// OffDataDownloadForBd dataDownload = new OffDataDownloadForBd(handler,
		// new HashMap<String, Object>(), OffLineUtil.DOWNLOAD_CQZ_MT_BW_QY ,
		// true);
		dataDownload.requestAgain();

	}

	/**
	 * 
	 * @方法名：updateForTikou
	 * @功能说明：更新梯口数据
	 * @author liums
	 * @date 2014-5-5 上午10:13:05
	 */
	private void updateForTikouOrXcXj() {
		Log.i(TAG, "again updateForTikou");
		HashMap<String, Object> bindData = null;
		switch (Flags.PDA_VERSION) {
		case Flags.PDA_VERSION_DEFAULT:
			bindData = SystemSetting.getBindShip(GlobalFlags.LIST_TYPE_FROM_XUNCHAXUNJIAN + "");
			break;
		case Flags.PDA_VERSION_SENTINEL:
			bindData = SystemSetting.getBindShip(GlobalFlags.LIST_TYPE_FROM_TIKOUMANAGER + "");
			break;
		default:
			break;
		}

		if (bindData != null) {
			Log.i(TAG, "againg tikou bindData!=null");
			OffDataDownloadForBd dataDownload = new OffDataDownloadForBd(handler, bindData, OffLineUtil.DOWNLOAD_FOR_KACBQK);
			dataDownload.requestAgain();
		} else {
			Log.i(TAG, "tikou bindData==null");
		}

	}

	/**
	 * 
	 * @方法名：updateForKakou
	 * @功能说明：更新卡口数据
	 * @author liums
	 * @date 2014-5-5 上午10:13:05
	 */
	private void updateForKakou() {
		Log.i(TAG, "again updateForKakou");
		HashMap<String, Object> kaKouBinddata = SystemSetting.getBindShip(GlobalFlags.LIST_TYPE_FROM_KAKOUMANAGER + "");
		if (kaKouBinddata != null) {
			Log.i(TAG, "againg kakou bindData!=null");
			OffDataDownloadForBd dataDownload = new OffDataDownloadForBd(handler, kaKouBinddata, OffLineUtil.DOWNLOAD_FOR_QYXX);
			dataDownload.requestAgain();
		} else {
			Log.i(TAG, "kakou bindData==null");
		}

	}

	/**
	 * 
	 * @方法名：startStartServices
	 * @功能说明：启动服务
	 * @author liums
	 * @date 2013-10-28 上午10:30:59
	 */
	private void startStartServices() {
		Log.i(TAG, "startStartServices，flag=" + flag);
		new Thread(cyxxRunnable).start();// 船员信息
		new Thread(kacbqkRunnable).start();// 船舶信息
		new Thread(hgzjxxRunnable).start(); // 证件信息
		new Thread(mtdmRunnable).start();// 码头
		new Thread(bwdmRunnable).start();// 泊位
		new Thread(qyxxRunnable).start();// 区域
		new Thread(fwxcbRunnable).start();// 服务性船舶
		new Thread(tbUserinfoRunnable).start();// 一线用户
		new Thread(delHgzjxxRunnable).start();// 删除证件
		new Thread(scsbRunnable).start();// 手持设备
		new Thread(sxtglRunnable).start();// 摄像头
		new Thread(sbxxRunnable).start();// 设备
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.i(TAG, "onStartCommand");
		return startId;
	}

	private Runnable scsbRunnable = new Runnable() {
		public void run() {
			while (flag) {
				SystemClock.sleep(1 * 100 * 1000);
				requestScsb();
				SystemClock.sleep(OffLineUtil.OFFLINE_SCSB_ADD_WAITTIME);
			}
		}

	};

	private Runnable sxtglRunnable = new Runnable() {
		public void run() {
			while (flag) {
				SystemClock.sleep(1 * 110 * 1000);
				requestSxtgl();
				SystemClock.sleep(OffLineUtil.OFFLINE_SXTGL_ADD_WAITTIME);
			}
		}

	};

	private Runnable sbxxRunnable = new Runnable() {
		public void run() {
			while (flag) {
				SystemClock.sleep(1 * 120 * 1000);
				requestSbxx();
				SystemClock.sleep(OffLineUtil.OFFLINE_SBXX_ADD_WAITTIME);
			}
		}

	};

	private Runnable hgzjxxRunnable = new Runnable() {
		public void run() {
			while (flag) {
				SystemClock.sleep(1 * 30 * 1000);
				requestHgzjxx();
				SystemClock.sleep(OffLineUtil.OFFLINE_HGZJXX_ADD_WAITTIME);
			}
		}

	};

	private Runnable delHgzjxxRunnable = new Runnable() {
		public void run() {
			while (flag) {
				SystemClock.sleep(1 * 90 * 1000);
				requestDeleteHgzjxx();
				SystemClock.sleep(OffLineUtil.OFFLINE_HGZJXX_DELETE_WAITTIME);
			}
		}

	};

	private Runnable kacbqkRunnable = new Runnable() {
		public void run() {
			while (flag) {
				SystemClock.sleep(1 * 20 * 1000);
				requestKacbqk();
				SystemClock.sleep(OffLineUtil.OFFLINE_KACBQK_ADD_WAITTIME);
			}
		}

	};

	private Runnable cyxxRunnable = new Runnable() {
		public void run() {
			while (flag) {
				SystemClock.sleep(1 * 10 * 1000);
				requestCyxx();
				SystemClock.sleep(OffLineUtil.OFFLINE_CYXX_ADD_WAITTIME);
			}
		}
	};

	private Runnable mtdmRunnable = new Runnable() {
		public void run() {
			while (flag) {
				SystemClock.sleep(1 * 40 * 1000);
				requestMtdm();
				SystemClock.sleep(OffLineUtil.OFFLINE_MTDM_ADD_WAITTIME);
			}
		}

	};

	private Runnable bwdmRunnable = new Runnable() {
		public void run() {
			while (flag) {
				SystemClock.sleep(1 * 50 * 1000);
				requestBwdm();
				SystemClock.sleep(OffLineUtil.OFFLINE_BWDM_ADD_WAITTIME);
			}
		}

	};

	private Runnable qyxxRunnable = new Runnable() {
		public void run() {
			while (flag) {
				SystemClock.sleep(1 * 60 * 1000);
				requestQyxx();
				SystemClock.sleep(OffLineUtil.OFFLINE_QYXX_ADD_WAITTIME);
			}
		}
	};

	private Runnable fwxcbRunnable = new Runnable() {
		public void run() {
			while (flag) {
				SystemClock.sleep(1 * 70 * 1000);
				requestFwxcb();
				SystemClock.sleep(OffLineUtil.OFFLINE_FWXCB_ADD_WAITTIME);
			}
		}
	};

	private Runnable tbUserinfoRunnable = new Runnable() {
		public void run() {
			while (flag) {
				SystemClock.sleep(1 * 80 * 1000);
				requestUserInfo();
				SystemClock.sleep(OffLineUtil.OFFLINE_USERINFO_ADD_WAITTIME);
			}
		}
	};

	/**
	 * 
	 * @方法名：requestMtdm
	 * @功能说明：请求离线码头代码数据
	 * @author liums
	 * @date 2013-10-10 下午8:02:26
	 */
	private void requestMtdm() {
		Log.i(TAG, "requestMtdm");
		// 先判断网络是否可用，通过Handler
		if (BaseApplication.instent.getWebState()) {
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("userid", BaseApplication.instent.gainUserID()));
			NetWorkManager.request(this, OffLineUtil.OFFLINE_MTDM_ADD_URL, params, OffLineUtil.OFFLINE_MTDM_ADD_RESULT_CODE);
		}
	}

	/**
	 * 
	 * @方法名：requestBwdm
	 * @功能说明：请求离线泊位数据
	 * @author liums
	 * @date 2013-10-10 下午8:02:26
	 */
	private void requestBwdm() {
		Log.i(TAG, "requestBwdm");
		// 先判断网络是否可用，通过Handler
		if (BaseApplication.instent.getWebState()) {
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("userid", BaseApplication.instent.gainUserID()));
			NetWorkManager.request(this, OffLineUtil.OFFLINE_BWDM_ADD_URL, params, OffLineUtil.OFFLINE_BWDM_ADD_RESULT_CODE);
		}
	}

	/**
	 * 
	 * @方法名：requestQyxx
	 * @功能说明：请求离线区域数据
	 * @author liums
	 * @date 2013-10-10 下午8:02:26
	 */
	private void requestQyxx() {
		Log.i(TAG, "requestQyxx");
		// 先判断网络是否可用，通过Handler
		if (BaseApplication.instent.getWebState()) {
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("userid", BaseApplication.instent.gainUserID()));
			NetWorkManager.request(this, OffLineUtil.OFFLINE_QYXX_ADD_URL, params, OffLineUtil.OFFLINE_QYXX_ADD_RESULT_CODE);
		}
	}

	/**
	 * 
	 * @方法名：requestFwxcb
	 * @功能说明：请求服务性船舶数据
	 * @author 赵琳
	 * @date 2013-10-15 下午3:08:44
	 */
	private void requestFwxcb() {
		Log.i(TAG, "requestFwxcb");
		// 先判断网络是否可用，通过Handler
		if (BaseApplication.instent.getWebState()) {
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("userid", BaseApplication.instent.gainUserID()));
			NetWorkManager.request(this, OffLineUtil.OFFLINE_FWXCB_ADD_URL, params, OffLineUtil.OFFLINE_FWXCB_ADD_RESULT_CODE);
		}
	}

	private void requestUserInfo() {
		Log.i(TAG, "requestUserInfo");
		// 先判断网络是否可用，通过Handler
		if (BaseApplication.instent.getWebState()) {
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("userid", BaseApplication.instent.gainUserID()));
			NetWorkManager.request(this, OffLineUtil.OFFLINE_USERINFO_ADD_URL, params, OffLineUtil.OFFLINE_USERINFO_ADD_RESULT_CODE);
		}
	}

	/**
	 * 
	 * @方法名：requestKacbqk
	 * @功能说明：请求离线船舶数据
	 * @author liums
	 * @date 2013-10-10 下午8:02:26
	 */
	private void requestKacbqk() {
		Log.i(TAG, "requestKacbqk");
		// 先判断网络是否可用，通过Handler
		if (BaseApplication.instent.getWebState()) {
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("userid", BaseApplication.instent.gainUserID()));
			NetWorkManager.request(this, OffLineUtil.OFFLINE_KACBQK_ADD_URL, params, OffLineUtil.OFFLINE_KACBQK_ADD_RESULT_CODE);
		}
	}

	/**
	 * 
	 * @方法名：requestKacbqk
	 * @功能说明：请求离线证件数据
	 * @author liums
	 * @date 2013-10-10 下午8:02:26
	 */
	private void requestHgzjxx() {
		Log.i(TAG, "requestHgzjxx");
		// 先判断网络是否可用，通过Handler
		if (BaseApplication.instent.getWebState()) {
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			SharedPreferences prefs = BaseApplication.instent.getSharedPreferences(BaseApplication.instent.getString(R.string.app_name),
					Context.MODE_PRIVATE);
			String userid = prefs.getString("userid", "");
			params.add(new BasicNameValuePair("userid", userid));
			String sjid = prefs.getString("Hgzjxx_addSjid", "0");
			params.add(new BasicNameValuePair("sjid", sjid));
			params.add(new BasicNameValuePair("operateType", "ADD"));
			NetWorkManager.request(this, OffLineUtil.OFFLINE_HGZJXX_ADD_URL, params, OffLineUtil.OFFLINE_HGZJXX_ADD_RESULT_CODE);
		}
	}

	/**
	 * 
	 * @方法名：requestKacbqk
	 * @功能说明：请求离线证件数据
	 * @author liums
	 * @date 2013-10-10 下午8:02:26
	 */
	private void requestHgzjxxForCqz() {
		Log.i(TAG, "requestHgzjxx");
		// 先判断网络是否可用，通过Handler
		if (BaseApplication.instent.getWebState()) {
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			SharedPreferences prefs = BaseApplication.instent.getSharedPreferences(BaseApplication.instent.getString(R.string.app_name),
					Context.MODE_PRIVATE);
			String userid = prefs.getString("userid", "");
			params.add(new BasicNameValuePair("userid", userid));
			String sjid = prefs.getString("Hgzjxx_addSjid", "0");
			params.add(new BasicNameValuePair("sjid", sjid));
			params.add(new BasicNameValuePair("operateType", "ADD"));
			NetWorkManager.request(this, OffLineUtil.OFFLINE_HGZJXX_ADD_URL_FOR_CQZ, params, OffLineUtil.OFFLINE_HGZJXX_ADD_RESULT_CODE_FOR_CQZ);
		}
	}

	/**
	 * 
	 * @方法名：requestHgzjxx_delete
	 * @功能说明：请求本地要删除那些数据
	 * @author liums
	 * @date 2013-10-10 下午8:02:26
	 */
	private void requestDeleteHgzjxx() {
		Log.i(TAG, "requestDeleteHgzjxx");
		// 先判断网络是否可用，通过Handler
		if (BaseApplication.instent.getWebState()) {
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			SharedPreferences prefs = BaseApplication.instent.getSharedPreferences(BaseApplication.instent.getString(R.string.app_name),
					Context.MODE_PRIVATE);

			params.add(new BasicNameValuePair("userid", BaseApplication.instent.gainUserID()));

			String sjid = prefs.getString("Hgzjxx_delSjid", "0");
			params.add(new BasicNameValuePair("sjid", sjid));
			params.add(new BasicNameValuePair("operateType", "DELETE"));
			NetWorkManager.request(this, OffLineUtil.OFFLINE_HGZJXX_DELETE_URL, params, OffLineUtil.OFFLINE_HGZJXX_DELETE_RESULT_CODE);
		}
	}

	private void requestCyxx() {
		Log.i(TAG, "requestCyxx");
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("userid", BaseApplication.instent.gainUserID()));
		NetWorkManager.request(SynchDataService.this, OffLineUtil.OFFLINE_CYXX_ADD_URL, params, OffLineUtil.OFFLINE_CYXX_ADD_RESULT_CODE);
	}

	/**
	 * 
	 * @方法名：requestKacbqk
	 * @功能说明：请求离线船舶数据
	 * @author liums
	 * @date 2013-10-10 下午8:02:26
	 */
	private void requestScsb() {
		Log.i(TAG, "requestScsb");
		// 先判断网络是否可用，通过Handler
		if (BaseApplication.instent.getWebState()) {
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("userid", BaseApplication.instent.gainUserID()));
			NetWorkManager.request(this, OffLineUtil.OFFLINE_SCSB_ADD_URL, params, OffLineUtil.OFFLINE_SCSB_ADD_RESULT_CODE);
		}
	}

	/**
	 * 
	 * @方法名：requestKacbqk
	 * @功能说明：请求离线船舶数据
	 * @author liums
	 * @date 2013-10-10 下午8:02:26
	 */
	private void requestSbxx() {
		Log.i(TAG, "requestSbxx");
		// 先判断网络是否可用，通过Handler
		if (BaseApplication.instent.getWebState()) {
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("userid", BaseApplication.instent.gainUserID()));
			NetWorkManager.request(this, OffLineUtil.OFFLINE_SBXX_ADD_URL, params, OffLineUtil.OFFLINE_SBXX_ADD_RESULT_CODE);
		}
	}

	/**
	 * 
	 * @方法名：requestKacbqk
	 * @功能说明：请求离线船舶数据
	 * @author liums
	 * @date 2013-10-10 下午8:02:26
	 */
	private void requestSxtgl() {
		Log.i(TAG, "requestSxtgl");
		// 先判断网络是否可用，通过Handler
		if (BaseApplication.instent.getWebState()) {
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("userid", BaseApplication.instent.gainUserID()));
			NetWorkManager.request(this, OffLineUtil.OFFLINE_SXTGL_ADD_URL, params, OffLineUtil.OFFLINE_SXTGL_ADD_RESULT_CODE);
		}
	}

	@Override
	public void onHttpResult(final String str, final int httpRequestType) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				switch (httpRequestType) {
				case OffLineUtil.OFFLINE_KACBQK_ADD_RESULT_CODE:
					kacbqkBusiness(str);
					break;
				case OffLineUtil.OFFLINE_MTDM_ADD_RESULT_CODE:
					mtdmBusiness(str);
					break;
				case OffLineUtil.OFFLINE_BWDM_ADD_RESULT_CODE:
					bwdmBusiness(str);
					break;
				case OffLineUtil.OFFLINE_QYXX_ADD_RESULT_CODE:
					qyxxBusiness(str);
					break;
				case OffLineUtil.OFFLINE_HGZJXX_ADD_RESULT_CODE:
					hgzjxxBusiness(str);
					break;
				case OffLineUtil.OFFLINE_HGZJXX_ADD_RESULT_CODE_FOR_CQZ:
					hgzjxxBusinessForCqz(str);
					break;
				case OffLineUtil.OFFLINE_HGZJXX_DELETE_RESULT_CODE:
					hgzjxxDelBusiness(str);
					break;
				case OffLineUtil.OFFLINE_CYXX_ADD_RESULT_CODE: // 船员
					cyxxBusiness(str);
					break;
				case OffLineUtil.OFFLINE_FWXCB_ADD_RESULT_CODE: // 服务性船舶
					fwxcbBusiness(str);
					break;
				case OffLineUtil.OFFLINE_USERINFO_ADD_RESULT_CODE: // 一线用户
					tbUserinfoBusiness(str);
					break;
				case OffLineUtil.OFFLINE_SBXX_ADD_RESULT_CODE: // 一线用户
					sbxxBusiness(str);
					break;
				case OffLineUtil.OFFLINE_SCSB_ADD_RESULT_CODE: // 一线用户
					scsbBusiness(str);
					break;
				case OffLineUtil.OFFLINE_SXTGL_ADD_RESULT_CODE: // 一线用户
					sxtglBusiness(str);
					break;
				default:
					break;
				}

			}
		}).start();

	}

	private void mtdmBusiness(String str) {
		if (str == null) {
			return;
		} else {
			try {
				List<Mtdm> mtdms = PullXmlUtils.parseXMLData(Mtdm.class, str);
				if (mtdms.size() > 0) {
					MtdmService service = new MtdmService();
					service.deleteAll();
					service.insertList(mtdms);
					Log.i(TAG, "====Mtdm处理完成======" + mtdms.size());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void bwdmBusiness(String str) {
		if (str == null) {
			return;
		} else {
			try {
				List<Bwdm> bwdms = PullXmlUtils.parseXMLData(Bwdm.class, str);
				if (bwdms.size() > 0) {
					BwdmService service = new BwdmService();
					service.deleteAll();
					service.insertList(bwdms);
					Log.i(TAG, "====Bwdm处理完成======" + bwdms.size());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void hgzjxxBusinessForCqz(String str) {
		if (str == null || "OK".equals(str)) {
			Log.i(TAG, "=======Hgzjxx======OK");
			return;
		} else {
			try {
				List<Hgzjxx> hgzjxxs = PullXmlUtils.parseXMLData(Hgzjxx.class, str);
				if (hgzjxxs.size() > 0) {
					HgzjxxService service = new HgzjxxService();
					service.insertList(hgzjxxs);
					Log.i(TAG, "====add===Hgzjxx======" + hgzjxxs.size());
				}
				this.requestHgzjxxForCqz();

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void hgzjxxBusiness(String str) {
		if (str == null || "OK".equals(str)) {
			Log.i(TAG, "=======Hgzjxx======OK");
			return;
		} else {
			try {
				List<Hgzjxx> hgzjxxs = PullXmlUtils.parseXMLData(Hgzjxx.class, str);
				if (hgzjxxs.size() > 0) {
					HgzjxxService service = new HgzjxxService();
					service.insertList(hgzjxxs);
					Log.i(TAG, "====add===Hgzjxx======" + hgzjxxs.size());
				}
				this.requestHgzjxx();

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * @方法名：hgzjxxDelBusiness
	 * @功能说明：请求离线证件删除数据
	 * @author zhaotf
	 * @date 2013-10-14 下午7:51:46
	 * @param str
	 */
	private void hgzjxxDelBusiness(String str) {
		if (str == null || "OK".equals(str)) {
			return;
		} else {
			try {
				List<Hgzjxx> delHgzjxxs = PullXmlUtils.parseXMLData(Hgzjxx.class, str);
				if (delHgzjxxs.size() > 0) {
					HgzjxxService service = new HgzjxxService();
					service.delete(delHgzjxxs);
					Log.i(TAG, "======del Hgzjxx======" + delHgzjxxs.size());
				}
				this.requestDeleteHgzjxx();// 继续执行
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void qyxxBusiness(String str) {
		if (str != null && !"".equals(str)) {
			try {
				List<Qyxx> list = PullXmlUtils.parseXMLData(Qyxx.class, str);
				if (list.size() > 0) {
					QyxxService service = new QyxxService();
					service.deleteAll();
					service.insertList(list);
					Log.i(TAG, "======Qyxx处理完成======" + list.size());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 
	 * @方法名：cyxxBusiness
	 * @功能说明：船员离开数据处理
	 * @author 赵琳
	 * @date 2013-10-15 下午2:23:36
	 * @param str
	 */
	private void cyxxBusiness(String str) {
		if (str != null && !"".equals(str)) {
			try {
				List<TBCyxx> list = PullXmlUtils.parseXMLData(TBCyxx.class, str);
				if (list.size() > 0) {
					CyxxService service = new CyxxService();
					service.deleteAll();
					service.insertList(list);
					Log.i(TAG, "======TBCyxx处理完成======" + list.size());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 
	 * @方法名：fwxcbBusiness
	 * @功能说明：服务性船舶处理
	 * @author 赵琳
	 * @date 2013-10-15 下午3:15:09
	 * @param str
	 */
	private void fwxcbBusiness(String str) {
		if (str != null && !"".equals(str)) {
			try {
				List<Fwxcb> list = PullXmlUtils.parseXMLData(Fwxcb.class, str);
				if (list.size() > 0) {
					FwxcbService service = new FwxcbService();
					service.deleteAll();
					service.insertList(list);
					Log.i(TAG, "======Fwxcb处理完成======" + list.size());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 
	 * @方法名：userInfoBusiness
	 * @功能说明：一线用户处理
	 * @author 赵琳
	 * @date 2013-10-17 下午1:44:23
	 * @param str
	 */
	private void tbUserinfoBusiness(String str) {
		if (str != null && !"".equals(str)) {
			try {
				List<TBUserinfo> list = PullXmlUtils.parseXMLData(TBUserinfo.class, str);
				if (list.size() > 0) {
					TBUserinfoService service = new TBUserinfoService();
					service.deleteAll();
					service.insertList(list);
					Log.i(TAG, "======TBUserinfo处理完成======" + list.size());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 
	 * @方法名：kacbqkBusiness
	 * @功能说明：离线船舶数据业务处理
	 * @author liums
	 * @date 2013-10-10 下午8:15:02
	 * @param str
	 */
	private void kacbqkBusiness(String str) {
		if (str == null) {
			return;
		} else {
			try {
				List<Kacbqk> kacbqks = PullXmlUtils.parseXMLData(Kacbqk.class, str);
				if (kacbqks.size() > 0) {
					KacbqkService service = new KacbqkService();
					service.deleteAll();
					service.insertList(kacbqks);
					Log.i(TAG, "======Kacbqk处理完成======" + kacbqks.size());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void scsbBusiness(String str) {
		if (str == null) {
			return;
		} else {
			try {
				List<Scsb> scsbs = PullXmlUtils.parseXMLData(Scsb.class, str);
				if (scsbs.size() > 0) {
					ScsbService service = new ScsbService();
					service.deleteAll();
					service.insertList(scsbs);
					Log.i(TAG, "======Scsb处理完成======" + scsbs.size());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	private void sbxxBusiness(String str) {

		if (str == null) {
			return;
		} else {
			try {
				List<Sbxx> sbxxs = PullXmlUtils.parseXMLData(Sbxx.class, str);
				if (sbxxs.size() > 0) {
					SbxxService service = new SbxxService();
					service.deleteAll();
					service.insertList(sbxxs);
					Log.i(TAG, "======Sbxx处理完成======" + sbxxs.size());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void sxtglBusiness(String str) {

		if (str == null) {
			return;
		} else {
			try {
				List<Sxtgl> sxtgls = PullXmlUtils.parseXMLData(Sxtgl.class, str);
				if (sxtgls.size() > 0) {
					SxtglService service = new SxtglService();
					service.deleteAll();
					service.insertList(sxtgls);
					Log.i(TAG, "======Sxtgl处理完成======" + sxtgls.size());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onDestroy() {
		flag = false;
		BaseApplication.instent.setDownloadFlag(false);
		stopTimer();
		stopService(new Intent(SynchDataService.this, ImageDownloadService.class));
		super.onDestroy();
	}
}
