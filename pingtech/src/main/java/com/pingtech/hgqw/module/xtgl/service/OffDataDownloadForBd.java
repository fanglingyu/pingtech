package com.pingtech.hgqw.module.xtgl.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.http.NameValuePair;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;

import com.pingtech.R;
import com.pingtech.hgqw.base.BaseApplication;
import com.pingtech.hgqw.interf.OnHttpResult;
import com.pingtech.hgqw.module.offline.base.utils.DbUtil;
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
import com.pingtech.hgqw.utils.BasicNameValuePair;
import com.pingtech.hgqw.utils.Log;
import com.pingtech.hgqw.utils.StringUtils;
import com.pingtech.hgqw.utils.SystemSetting;
import com.pingtech.hgqw.utils.xml.PullXmlUtils;
import com.pingtech.hgqw.web.NetWorkManager;
import com.pingtech.hgqw.web.ThreadPool;

public class OffDataDownloadForBd implements OnHttpResult {
	public String TAG = "OffDataDownloadForBd";

	private Handler handler = null;

	private String voyageNumber = "";

	private String kkid = "";

	private String id = "";

	private String type = "";

	/** flag=1船舶，2区域 */
	private int flag = -1;

	private int retryCount = 0;

	private Set<Integer> retrySet = new HashSet<Integer>();

	private Handler offDataHandler = null;

	private HashMap<String, Object> bindMap = null;

	public Map<Integer, Integer> map = new HashMap<Integer, Integer>();

	public Map<Integer, String> mapUrl = new HashMap<Integer, String>();

	public Map<Integer, String> mapString = new HashMap<Integer, String>();

	/** 所有项都下载成功 */
	public static final int WHAT_DOWNLOAD_ALL_SUCCESS = 1000;

	/** 下载成功一项 */
	public static final int WHAT_DOWNLOAD_SUCCESS_ONE = 999;

	/***/
	public static final int WHAT_INSERT_DATA_FAILED_ONE = 998;

	/** 有一项下载不成功 */
	public static final int WHAT_DOWNLOAD_ONE_RESULT_NULL = 997;

	/** 重新下载不成功项 */
	public static final int WHAT_DOWNLOAD_ONE_RESULT_SUCCESS = 0x1234;

	/** 下载数据失败，所有或个别数据请求失败，重新下载或稍后重试 */
	public static final int WHAT_DOWNLOAD_ALL_HAS_FAILED = 996;

	/** 开始逐条下载数据 */
	public static final int FLAG_DOWNLOAD_ONE_RESULT_NOT_BEGIN = 1;

	public static final int FLAG_DOWNLOAD_ONE_RESULT_SUCCESS = 2;

	public static final int FLAG_INSERT_ONE_FAILE = 3;

	public static final int FLAG_DOWNLOAD_ONE_RESULT_NULL = 4;

	public static final int FLAG_DOWNLOAD_ONE_RESULT = 5;

	/**
	 * 下载完一条数据后，不管成功与否，是否继续下载下一条数据
	 */
	public static boolean loading = true;

	private int isC = 0;

	private class OffDataHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			int what = msg.what;
			switch (what) {
			case WHAT_DOWNLOAD_SUCCESS_ONE:
				Log.i(TAG, "WHAT_DOWNLOAD_SUCCESS_ONE");
				changeDownLoadState((Integer) msg.obj, FLAG_DOWNLOAD_ONE_RESULT_SUCCESS);
				handler.obtainMessage(what, (Integer) msg.obj, 0).sendToTarget();// 通知Activity
				requestAgain();

				break;
			case WHAT_DOWNLOAD_ALL_SUCCESS:
				handler.sendEmptyMessage(WHAT_DOWNLOAD_ALL_SUCCESS);// 通知Activity
				BaseApplication.instent.setDownloadFlag(false);
				break;
			case WHAT_INSERT_DATA_FAILED_ONE:
				Log.i(TAG, "WHAT_INSERT_DATA_FAILED_ONE");
				changeDownLoadState((Integer) msg.obj, FLAG_INSERT_ONE_FAILE);
				handler.obtainMessage(what, (Integer) msg.obj, 0).sendToTarget();// 通知Activity
				requestAgain();
				break;
			case WHAT_DOWNLOAD_ONE_RESULT_NULL:
				Log.i(TAG, "WHAT_DOWNLOAD_ONE_RESULT_NULL");
				changeDownLoadState((Integer) msg.obj, FLAG_DOWNLOAD_ONE_RESULT_NULL);
				handler.obtainMessage(what, (Integer) msg.obj, 0).sendToTarget();// 通知Activity
				requestAgain();

				break;
			case WHAT_DOWNLOAD_ALL_HAS_FAILED:
				handler.sendEmptyMessage(WHAT_DOWNLOAD_ALL_HAS_FAILED);// 通知Activity
				break;

			default:
				break;
			}
		}

	}

	public OffDataDownloadForBd(Handler handler, HashMap<String, Object> bindMap, int flag) {
		Log.i(TAG, "OffDataDownload(Handler handler)");
		this.handler = handler;
		this.bindMap = bindMap;
		this.flag = flag;
		offDataHandler = new OffDataHandler();
		init();// 初始化需要下载的数据
	}

	/**
	 * 子线程调用此构造函数 Constructor for OffDataDownloadForBd
	 * 
	 * @param handler
	 * @param bindMap
	 * @param flag
	 * @param retryCount
	 *            下载数据失败后重试次数，0 不重试
	 */
	public OffDataDownloadForBd(Handler handler, HashMap<String, Object> bindMap, int flag, int retryCount) {
		Log.i(TAG, "OffDataDownload(Handler handler)");
		this.handler = handler;
		this.bindMap = bindMap;
		this.flag = flag;
		this.retryCount = retryCount;
		TAG += " " + retryCount;
		offDataHandler = new OffDataHandler();
		init();// 初始化需要下载的数据

	}

	/**
	 * 
	 * @方法名：setSuccess
	 * @功能说明：请求成功修改标识
	 * @author liums
	 * @date 2013-10-31 下午6:49:40
	 * @param key
	 */
	private void changeDownLoadState(int key, int state) {
		map.put(key, state);
	}

	/**
	 * 
	 * @方法名：requestSuccessOne
	 * @功能说明：判断下一个请求
	 * @author liums
	 * @date 2013-10-31 下午6:49:51
	 */
	public void requestAgain() {
		Log.i(TAG, "Set 视图：" + map.entrySet());

		for (Entry<Integer, Integer> e : map.entrySet()) {

			// 循环逐条下载数据
			if (FLAG_DOWNLOAD_ONE_RESULT_NOT_BEGIN == e.getValue()) {
				requestNext(e.getKey());
				return;
			}

		}
		for (Entry<Integer, Integer> e : map.entrySet()) {
			// FLAG_INSERT_ONE_FAILE
			// FLAG_DOWNLOAD_ONE_RESULT_NULL
			// 循环完所有未开始的数据，检测是否有同步失败的数据，再同步一次
			if ((FLAG_DOWNLOAD_ONE_RESULT_NULL == e.getValue() || FLAG_INSERT_ONE_FAILE == e.getValue()) && retryCount > 0) {
				Log.i(TAG, "has faile request again,retryCount = " + retryCount);
				// 记录已经重试过的对象，如果已经重试过，retryCount不进行递减
				if (!retrySet.contains(e.getKey())) {
					retrySet.add(e.getKey());
				}
				retryCount--;
				requestNext(e.getKey());
				return;
			}

		}
		// 检测是否有请求失败的数据，如果有提示用户重新下载

		// for (Entry<Integer, Integer> e : map.entrySet()) {
		// if (FLAG_DOWNLOAD_ONE_RESULT_NULL == e.getValue()) {
		// offDataHandler.sendEmptyMessage(WHAT_DOWNLOAD_ALL_HAS_FAILED);
		// return;
		// }
		// }

		// 全部下载完成
		offDataHandler.sendEmptyMessage(WHAT_DOWNLOAD_ALL_SUCCESS);
		BaseApplication.instent.setDownloadFlag(false);
	}

	/**
	 * 
	 * @方法名：requestNext
	 * @功能说明：请求下一个
	 * @author liums
	 * @date 2013-10-31 下午6:43:30
	 * @param key
	 */
	public void requestNext(Integer key) {

		Log.i(TAG, "requestNext：" + key + ":" + mapUrl.get(key));

		if (key == OffLineUtil.OFFLINE_HGZJXX_DELETE_RESULT_CODE) {
			requestDeleteHgzjxx();
			return;
		}
		if (key == OffLineUtil.OFFLINE_HGZJXX_ADD_RESULT_CODE_FOR_CQZ) {
			requestHgzjxx();
			return;
		}

		/**
		 * 根据传入的键key 获取map中键对应的值，即接口的名称，请求完后，得到返回的结果
		 */
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("userid", BaseApplication.instent.gainUserID()));
		params.add(new BasicNameValuePair("kkid", kkid));
		params.add(new BasicNameValuePair("id", id));
		params.add(new BasicNameValuePair("type", type));
		params.add(new BasicNameValuePair("voyageNumber", voyageNumber));

		NetWorkManager.request(this, mapUrl.get(key), params, key);
	}

	/**
	 * 
	 * @方法名：init
	 * @功能说明：初始化数据，
	 * @author liums
	 * @date 2013-10-31 下午6:42:34
	 */
	private void init() {
		Log.i(TAG, " init()");
		BaseApplication.instent.setDownloadFlag(true);
		initHcOrKkid();
		map = new HashMap<Integer, Integer>();
		mapUrl = new HashMap<Integer, String>();
		mapString = new HashMap<Integer, String>();

		if (flag == OffLineUtil.DOWNLOAD_CQZ_MT_BW_QY) {// 长期证
			/** 区域数据 */
			map.put(OffLineUtil.OFFLINE_QYXX_ADD_RESULT_CODE, FLAG_DOWNLOAD_ONE_RESULT_NOT_BEGIN);
			mapUrl.put(OffLineUtil.OFFLINE_QYXX_ADD_RESULT_CODE, OffLineUtil.OFFLINE_QYXX_ADD_URL);
			mapString.put(OffLineUtil.OFFLINE_QYXX_ADD_RESULT_CODE, "区域数据");

			/** 码头数据 */
			map.put(OffLineUtil.OFFLINE_MTDM_ADD_RESULT_CODE, FLAG_DOWNLOAD_ONE_RESULT_NOT_BEGIN);
			mapUrl.put(OffLineUtil.OFFLINE_MTDM_ADD_RESULT_CODE, OffLineUtil.OFFLINE_MTDM_ADD_URL);
			mapString.put(OffLineUtil.OFFLINE_MTDM_ADD_RESULT_CODE, "码头数据");

			/** 泊位数据 */
			map.put(OffLineUtil.OFFLINE_BWDM_ADD_RESULT_CODE, FLAG_DOWNLOAD_ONE_RESULT_NOT_BEGIN);
			mapUrl.put(OffLineUtil.OFFLINE_BWDM_ADD_RESULT_CODE, OffLineUtil.OFFLINE_BWDM_ADD_URL);
			mapString.put(OffLineUtil.OFFLINE_BWDM_ADD_RESULT_CODE, "泊位数据");

			/** 证件数据 */
			map.put(OffLineUtil.OFFLINE_HGZJXX_ADD_RESULT_CODE_FOR_CQZ, FLAG_DOWNLOAD_ONE_RESULT_NOT_BEGIN);
			mapUrl.put(OffLineUtil.OFFLINE_HGZJXX_ADD_RESULT_CODE_FOR_CQZ, OffLineUtil.OFFLINE_HGZJXX_ADD_URL_FOR_CQZ);
			mapString.put(OffLineUtil.OFFLINE_HGZJXX_ADD_RESULT_CODE_FOR_CQZ, "长期证数据");

			map.put(OffLineUtil.OFFLINE_HGZJXX_DELETE_RESULT_CODE, FLAG_DOWNLOAD_ONE_RESULT_NOT_BEGIN);
			mapUrl.put(OffLineUtil.OFFLINE_HGZJXX_DELETE_RESULT_CODE, OffLineUtil.OFFLINE_HGZJXX_DELETE_URL);
			mapString.put(OffLineUtil.OFFLINE_HGZJXX_DELETE_RESULT_CODE, "证件数据更新完成");
		}else if (flag == OffLineUtil.DOWNLOAD_CQZ_MT_BW_QY_NO_DELETE) {// 长期证
			/** 区域数据 */
			map.put(OffLineUtil.OFFLINE_QYXX_ADD_RESULT_CODE, FLAG_DOWNLOAD_ONE_RESULT_NOT_BEGIN);
			mapUrl.put(OffLineUtil.OFFLINE_QYXX_ADD_RESULT_CODE, OffLineUtil.OFFLINE_QYXX_ADD_URL);
			mapString.put(OffLineUtil.OFFLINE_QYXX_ADD_RESULT_CODE, "区域数据");

			/** 码头数据 */
			map.put(OffLineUtil.OFFLINE_MTDM_ADD_RESULT_CODE, FLAG_DOWNLOAD_ONE_RESULT_NOT_BEGIN);
			mapUrl.put(OffLineUtil.OFFLINE_MTDM_ADD_RESULT_CODE, OffLineUtil.OFFLINE_MTDM_ADD_URL);
			mapString.put(OffLineUtil.OFFLINE_MTDM_ADD_RESULT_CODE, "码头数据");

			/** 泊位数据 */
			map.put(OffLineUtil.OFFLINE_BWDM_ADD_RESULT_CODE, FLAG_DOWNLOAD_ONE_RESULT_NOT_BEGIN);
			mapUrl.put(OffLineUtil.OFFLINE_BWDM_ADD_RESULT_CODE, OffLineUtil.OFFLINE_BWDM_ADD_URL);
			mapString.put(OffLineUtil.OFFLINE_BWDM_ADD_RESULT_CODE, "泊位数据");

			/** 证件数据 */
			map.put(OffLineUtil.OFFLINE_HGZJXX_ADD_RESULT_CODE_FOR_CQZ, FLAG_DOWNLOAD_ONE_RESULT_NOT_BEGIN);
			mapUrl.put(OffLineUtil.OFFLINE_HGZJXX_ADD_RESULT_CODE_FOR_CQZ, OffLineUtil.OFFLINE_HGZJXX_ADD_URL_FOR_CQZ);
			mapString.put(OffLineUtil.OFFLINE_HGZJXX_ADD_RESULT_CODE_FOR_CQZ, "长期证数据");

//			map.put(OffLineUtil.OFFLINE_HGZJXX_DELETE_RESULT_CODE, FLAG_DOWNLOAD_ONE_RESULT_NOT_BEGIN);
//			mapUrl.put(OffLineUtil.OFFLINE_HGZJXX_DELETE_RESULT_CODE, OffLineUtil.OFFLINE_HGZJXX_DELETE_URL);
//			mapString.put(OffLineUtil.OFFLINE_HGZJXX_DELETE_RESULT_CODE, "证件数据更新完成");
		}  else if (flag == OffLineUtil.DOWNLOAD_FOR_KACBQK) {// 船舶
			/** 口岸船舶数据 */
			map.put(OffLineUtil.OFFLINE_KACBQK_ADD_RESULT_CODE_FOR_KACBQK, FLAG_DOWNLOAD_ONE_RESULT_NOT_BEGIN);
			mapUrl.put(OffLineUtil.OFFLINE_KACBQK_ADD_RESULT_CODE_FOR_KACBQK, OffLineUtil.OFFLINE_KACBQK_ADD_URL_FOR_KACBQK);
			mapString.put(OffLineUtil.OFFLINE_KACBQK_ADD_RESULT_CODE_FOR_KACBQK, "口岸船舶数据");

			/** 船员数据 */
			map.put(OffLineUtil.OFFLINE_CYXX_ADD_RESULT_CODE_FOR_KACBQK, FLAG_DOWNLOAD_ONE_RESULT_NOT_BEGIN);
			mapUrl.put(OffLineUtil.OFFLINE_CYXX_ADD_RESULT_CODE_FOR_KACBQK, OffLineUtil.OFFLINE_CYXX_ADD_URL_FOR_KACBQK);
			mapString.put(OffLineUtil.OFFLINE_CYXX_ADD_RESULT_CODE_FOR_KACBQK, "船员数据");

			/** 证件数据 */
			map.put(OffLineUtil.OFFLINE_HGZJXX_ADD_RESULT_CODE_FOR_KACBQK, FLAG_DOWNLOAD_ONE_RESULT_NOT_BEGIN);
			mapUrl.put(OffLineUtil.OFFLINE_HGZJXX_ADD_RESULT_CODE_FOR_KACBQK, OffLineUtil.OFFLINE_HGZJXX_ADD_URL_FOR_KACBQK);
			mapString.put(OffLineUtil.OFFLINE_HGZJXX_ADD_RESULT_CODE_FOR_KACBQK, "证件数据");
		} else if (flag == OffLineUtil.UPDATE_CQZ) {// 更新长期证
			/** 证件数据 */
			map.put(OffLineUtil.OFFLINE_HGZJXX_ADD_RESULT_CODE_FOR_CQZ, FLAG_DOWNLOAD_ONE_RESULT_NOT_BEGIN);
			mapUrl.put(OffLineUtil.OFFLINE_HGZJXX_ADD_RESULT_CODE_FOR_CQZ, OffLineUtil.OFFLINE_HGZJXX_ADD_URL_FOR_CQZ);
			mapString.put(OffLineUtil.OFFLINE_HGZJXX_ADD_RESULT_CODE_FOR_CQZ, "长期证数据");

			map.put(OffLineUtil.OFFLINE_HGZJXX_DELETE_RESULT_CODE, FLAG_DOWNLOAD_ONE_RESULT_NOT_BEGIN);
			mapUrl.put(OffLineUtil.OFFLINE_HGZJXX_DELETE_RESULT_CODE, OffLineUtil.OFFLINE_HGZJXX_DELETE_URL);
			mapString.put(OffLineUtil.OFFLINE_HGZJXX_DELETE_RESULT_CODE, "证件数据更新完成");
		} else if (flag == OffLineUtil.DOWNLOAD_ALL_OFFLINE_DATA_FOR_HC) {// 绑定船舶后，下载全部相关离线信息

			map.put(OffLineUtil.OFFLINE_ALL_OFFLINE_DATA_CODE_FOR_HC, FLAG_DOWNLOAD_ONE_RESULT_NOT_BEGIN);
			mapUrl.put(OffLineUtil.OFFLINE_ALL_OFFLINE_DATA_CODE_FOR_HC, OffLineUtil.OFFLINE_ALL_OFFLINE_DATA_URL_FOR_HC);
			mapString.put(OffLineUtil.OFFLINE_ALL_OFFLINE_DATA_CODE_FOR_HC, "绑定船舶，相关离线数据更新完成");
		} else if (flag == OffLineUtil.DOWNLOAD_ALL_OFFLINE_DATA_FOR_KKID) {// 绑定卡口后，下载全部相关离线信息

			map.put(OffLineUtil.OFFLINE_ALL_OFFLINE_DATA_CODE_FOR_KKID, FLAG_DOWNLOAD_ONE_RESULT_NOT_BEGIN);
			mapUrl.put(OffLineUtil.OFFLINE_ALL_OFFLINE_DATA_CODE_FOR_KKID, OffLineUtil.OFFLINE_ALL_OFFLINE_DATA_URL_FOR_KKID);
			mapString.put(OffLineUtil.OFFLINE_ALL_OFFLINE_DATA_CODE_FOR_KKID, "绑定卡口，相关离线数据更新完成");
		} else if (flag == OffLineUtil.DOWNLOAD_FOR_QYXX) {// 区域
			/** 口岸船舶数据 */
			map.put(OffLineUtil.OFFLINE_KACBQK_ADD_RESULT_CODE_FOR_QYXX, FLAG_DOWNLOAD_ONE_RESULT_NOT_BEGIN);
			mapUrl.put(OffLineUtil.OFFLINE_KACBQK_ADD_RESULT_CODE_FOR_QYXX, OffLineUtil.OFFLINE_KACBQK_ADD_URL_FOR_QYXX);
			mapString.put(OffLineUtil.OFFLINE_KACBQK_ADD_RESULT_CODE_FOR_QYXX, "口岸船舶数据");

			/** 船员数据 */
			map.put(OffLineUtil.OFFLINE_CYXX_ADD_RESULT_CODE_FOR_QYXX, FLAG_DOWNLOAD_ONE_RESULT_NOT_BEGIN);
			mapUrl.put(OffLineUtil.OFFLINE_CYXX_ADD_RESULT_CODE_FOR_QYXX, OffLineUtil.OFFLINE_CYXX_ADD_URL_FOR_QYXX);
			mapString.put(OffLineUtil.OFFLINE_CYXX_ADD_RESULT_CODE_FOR_QYXX, "船员数据");

			/** 证件数据 */
			map.put(OffLineUtil.OFFLINE_HGZJXX_ADD_RESULT_CODE_FOR_QYXX, FLAG_DOWNLOAD_ONE_RESULT_NOT_BEGIN);
			mapUrl.put(OffLineUtil.OFFLINE_HGZJXX_ADD_RESULT_CODE_FOR_QYXX, OffLineUtil.OFFLINE_HGZJXX_ADD_URL_FOR_QYXX);
			mapString.put(OffLineUtil.OFFLINE_HGZJXX_ADD_RESULT_CODE_FOR_QYXX, "证件数据");
		} else if (flag == OffLineUtil.DOWNLOAD_FOR_MT_OR_BW_QY) {// 巡查巡检绑定地点下载
			Log.i(TAG, SystemSetting.xunJianType);
			Log.i(TAG, "id=" + SystemSetting.xunJianId + ",mtid=" + SystemSetting.xunJianMTid);
			// mt,id=20140416100813887129929243548942,mtid=null
			// bw：id=2014041610093390227619913886896,mtid=20140416100813887129929243548942
			// qy：id=0001439f2c1b1b474b,mtid=null
			// SystemSetting.xunJianId;
			// SystemSetting.xunJianType;
			// SystemSetting.xunJianMTid;
			id = SystemSetting.xunJianId;
			type = SystemSetting.xunJianType;
			kkid = SystemSetting.xunJianId;
			if ("mt".equals(SystemSetting.xunJianType)) {
				/** 船舶数据 */
				map.put(OffLineUtil.OFFLINE_KACBQK_ADD_RESULT_CODE_FOR_MTID, FLAG_DOWNLOAD_ONE_RESULT_NOT_BEGIN);
				mapUrl.put(OffLineUtil.OFFLINE_KACBQK_ADD_RESULT_CODE_FOR_MTID, OffLineUtil.OFFLINE_KACBQK_ADD_URL_FOR_MTID);
				mapString.put(OffLineUtil.OFFLINE_KACBQK_ADD_RESULT_CODE_FOR_MTID, "船舶数据");

				/** 证件数据 */
				map.put(OffLineUtil.OFFLINE_HGZJXX_ADD_RESULT_CODE_FOR_MTID, FLAG_DOWNLOAD_ONE_RESULT_NOT_BEGIN);
				mapUrl.put(OffLineUtil.OFFLINE_HGZJXX_ADD_RESULT_CODE_FOR_MTID, OffLineUtil.OFFLINE_HGZJXX_ADD_URL_FOR_MTID);
				mapString.put(OffLineUtil.OFFLINE_HGZJXX_ADD_RESULT_CODE_FOR_MTID, "证件数据");

				/** 船员数据 */
				map.put(OffLineUtil.OFFLINE_CYXX_ADD_RESULT_CODE_FOR_MTID, FLAG_DOWNLOAD_ONE_RESULT_NOT_BEGIN);
				mapUrl.put(OffLineUtil.OFFLINE_CYXX_ADD_RESULT_CODE_FOR_MTID, OffLineUtil.OFFLINE_CYXX_ADD_URL_FOR_MTID);
				mapString.put(OffLineUtil.OFFLINE_CYXX_ADD_RESULT_CODE_FOR_MTID, "船员数据");
			} else if ("bw".equals(SystemSetting.xunJianType)) {
				/** 船舶数据 */
				map.put(OffLineUtil.OFFLINE_KACBQK_ADD_RESULT_CODE_FOR_BWID, FLAG_DOWNLOAD_ONE_RESULT_NOT_BEGIN);
				mapUrl.put(OffLineUtil.OFFLINE_KACBQK_ADD_RESULT_CODE_FOR_BWID, OffLineUtil.OFFLINE_KACBQK_ADD_URL_FOR_BWID);
				mapString.put(OffLineUtil.OFFLINE_KACBQK_ADD_RESULT_CODE_FOR_BWID, "船舶数据");

				/** 证件数据 */
				map.put(OffLineUtil.OFFLINE_HGZJXX_ADD_RESULT_CODE_FOR_BWID, FLAG_DOWNLOAD_ONE_RESULT_NOT_BEGIN);
				mapUrl.put(OffLineUtil.OFFLINE_HGZJXX_ADD_RESULT_CODE_FOR_BWID, OffLineUtil.OFFLINE_HGZJXX_ADD_URL_FOR_BWID);
				mapString.put(OffLineUtil.OFFLINE_HGZJXX_ADD_RESULT_CODE_FOR_BWID, "证件数据");

				/** 船员数据 */
				map.put(OffLineUtil.OFFLINE_CYXX_ADD_RESULT_CODE_FOR_BWID, FLAG_DOWNLOAD_ONE_RESULT_NOT_BEGIN);
				mapUrl.put(OffLineUtil.OFFLINE_CYXX_ADD_RESULT_CODE_FOR_BWID, OffLineUtil.OFFLINE_CYXX_ADD_URL_FOR_BWID);
				mapString.put(OffLineUtil.OFFLINE_CYXX_ADD_RESULT_CODE_FOR_BWID, "船员数据");

			} else if ("qy".equals(SystemSetting.xunJianType)) {

				/** 口岸船舶数据 */
				map.put(OffLineUtil.OFFLINE_KACBQK_ADD_RESULT_CODE_FOR_QYXX, FLAG_DOWNLOAD_ONE_RESULT_NOT_BEGIN);
				mapUrl.put(OffLineUtil.OFFLINE_KACBQK_ADD_RESULT_CODE_FOR_QYXX, OffLineUtil.OFFLINE_KACBQK_ADD_URL_FOR_QYXX);
				mapString.put(OffLineUtil.OFFLINE_KACBQK_ADD_RESULT_CODE_FOR_QYXX, "口岸船舶数据");

				/** 船员数据 */
				map.put(OffLineUtil.OFFLINE_CYXX_ADD_RESULT_CODE_FOR_QYXX, FLAG_DOWNLOAD_ONE_RESULT_NOT_BEGIN);
				mapUrl.put(OffLineUtil.OFFLINE_CYXX_ADD_RESULT_CODE_FOR_QYXX, OffLineUtil.OFFLINE_CYXX_ADD_URL_FOR_QYXX);
				mapString.put(OffLineUtil.OFFLINE_CYXX_ADD_RESULT_CODE_FOR_QYXX, "船员数据");

				/** 证件数据 */
				map.put(OffLineUtil.OFFLINE_HGZJXX_ADD_RESULT_CODE_FOR_QYXX, FLAG_DOWNLOAD_ONE_RESULT_NOT_BEGIN);
				mapUrl.put(OffLineUtil.OFFLINE_HGZJXX_ADD_RESULT_CODE_FOR_QYXX, OffLineUtil.OFFLINE_HGZJXX_ADD_URL_FOR_QYXX);
				mapString.put(OffLineUtil.OFFLINE_HGZJXX_ADD_RESULT_CODE_FOR_QYXX, "证件数据");
			}

		}

	}

	private void initHcOrKkid() {
		if (bindMap != null) {
			kkid = (String) bindMap.get("id");
			voyageNumber = (String) bindMap.get("hc");
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
		// 不判断网络是否可用，直接请求，否则可能进入死循环
		// if (BaseApplication.instent.getWebState()) {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		SharedPreferences prefs = BaseApplication.instent.getSharedPreferences(BaseApplication.instent.getString(R.string.app_name),
				Context.MODE_PRIVATE);
		String userid = prefs.getString("userid", "");
		params.add(new BasicNameValuePair("userid", userid));
		String sjid = prefs.getString("Hgzjxx_addSjid", "0");
		params.add(new BasicNameValuePair("sjid", sjid));
		params.add(new BasicNameValuePair("operateType", "ADD"));

		Log.i(TAG, "证件数据的请求参数" + params);
		NetWorkManager.request(this, OffLineUtil.OFFLINE_HGZJXX_ADD_URL_FOR_CQZ, params, OffLineUtil.OFFLINE_HGZJXX_ADD_RESULT_CODE_FOR_CQZ);
		// }
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
		// 不判断网络是否可用，直接请求，否则可能进入死循环
		// if (BaseApplication.instent.getWebState()) {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		SharedPreferences prefs = BaseApplication.instent.getSharedPreferences(BaseApplication.instent.getString(R.string.app_name),
				Context.MODE_PRIVATE);

		params.add(new BasicNameValuePair("userid", BaseApplication.instent.gainUserID()));

		String sjid = prefs.getString("Hgzjxx_delSjid", "0");
		params.add(new BasicNameValuePair("sjid", sjid));
		Log.i(TAG, "请求离线证件删除数据开始");
		params.add(new BasicNameValuePair("operateType", "DELETE"));
		NetWorkManager.request(this, OffLineUtil.OFFLINE_HGZJXX_DELETE_URL, params, OffLineUtil.OFFLINE_HGZJXX_DELETE_RESULT_CODE);
		// }
	}

	/**
	 * 处理平台返回的数据，调用不同的方法，解析返回的数据
	 */
	@Override
	public void onHttpResult(final String str, final int httpRequestType) {
		ThreadPool.addToSingleThreadExecutor(new Runnable() {
			@Override
			public void run() {
				switch (httpRequestType) {
				case OffLineUtil.OFFLINE_MTDM_ADD_RESULT_CODE:// 码头数据
					mtdmBusiness(str);
					break;
				case OffLineUtil.OFFLINE_BWDM_ADD_RESULT_CODE:// 泊位数据
					bwdmBusiness(str);
					break;
				case OffLineUtil.OFFLINE_QYXX_ADD_RESULT_CODE:// 区域信息数据
					qyxxBusiness(str);
					break;
				case OffLineUtil.OFFLINE_HGZJXX_DELETE_RESULT_CODE:// 删除的证件信息情况
					hgzjxxDelBusiness(str);
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
				case OffLineUtil.OFFLINE_HGZJXX_ADD_RESULT_CODE_FOR_CQZ:
					hgzjxxBusinessForCqz(str);
					break;

				case OffLineUtil.OFFLINE_ALL_OFFLINE_DATA_CODE_FOR_HC:
				case OffLineUtil.OFFLINE_ALL_OFFLINE_DATA_CODE_FOR_KKID:
					allOffLineDataForBindShipBusiness(str, httpRequestType);
					break;

				// ////
				case OffLineUtil.OFFLINE_HGZJXX_ADD_RESULT_CODE:// 证件数据
				case OffLineUtil.OFFLINE_HGZJXX_ADD_RESULT_CODE_FOR_KACBQK:
				case OffLineUtil.OFFLINE_HGZJXX_ADD_RESULT_CODE_FOR_QYXX:
				case OffLineUtil.OFFLINE_HGZJXX_ADD_RESULT_CODE_FOR_MTID:
				case OffLineUtil.OFFLINE_HGZJXX_ADD_RESULT_CODE_FOR_BWID:
					hgzjxxBusiness(str, httpRequestType);
					break;

				// ////
				case OffLineUtil.OFFLINE_KACBQK_ADD_RESULT_CODE:// 口岸船舶数据
				case OffLineUtil.OFFLINE_KACBQK_ADD_RESULT_CODE_FOR_KACBQK:
				case OffLineUtil.OFFLINE_KACBQK_ADD_RESULT_CODE_FOR_QYXX:
				case OffLineUtil.OFFLINE_KACBQK_ADD_RESULT_CODE_FOR_MTID:
				case OffLineUtil.OFFLINE_KACBQK_ADD_RESULT_CODE_FOR_BWID:
					kacbqkBusiness(str, httpRequestType);
					break;

				// ////
				case OffLineUtil.OFFLINE_CYXX_ADD_RESULT_CODE: // 船员
				case OffLineUtil.OFFLINE_CYXX_ADD_RESULT_CODE_FOR_KACBQK:
				case OffLineUtil.OFFLINE_CYXX_ADD_RESULT_CODE_FOR_QYXX:
				case OffLineUtil.OFFLINE_CYXX_ADD_RESULT_CODE_FOR_MTID:
				case OffLineUtil.OFFLINE_CYXX_ADD_RESULT_CODE_FOR_BWID:
					cyxxBusiness(str, httpRequestType);
					break;
				default:
					break;
				}

			}
		});
	}

	private void mtdmBusiness(String str) {
		Log.i(TAG, "=======Mtdm======Mtdm处理完成，返回的数据***********");
		if (StringUtils.isNotEmpty(str)) {
			try {
				List<Mtdm> mtdms = PullXmlUtils.parseXMLData(Mtdm.class, str);
				MtdmService service = new MtdmService();
				service.deleteAll();
				if (mtdms.size() > 0) {
					service.insertList(mtdms);
					Log.i(TAG, "====Mtdm处理完成======" + mtdms.size());
					offDataHandler.obtainMessage(WHAT_DOWNLOAD_SUCCESS_ONE, OffLineUtil.OFFLINE_MTDM_ADD_RESULT_CODE).sendToTarget();
				}
			} catch (Exception e) {
				e.printStackTrace();
				BaseApplication.instent.isDbBusy = false;
				offDataHandler.obtainMessage(WHAT_INSERT_DATA_FAILED_ONE, OffLineUtil.OFFLINE_MTDM_ADD_RESULT_CODE).sendToTarget();
			}
		} else {
			offDataHandler.obtainMessage(WHAT_DOWNLOAD_ONE_RESULT_NULL, OffLineUtil.OFFLINE_MTDM_ADD_RESULT_CODE).sendToTarget();
		}
	}

	private void bwdmBusiness(String str) {
		Log.i(TAG, "=======Bwdm====== 返回的数据 开始处理***********");
		if (StringUtils.isNotEmpty(str)) {
			Log.i(TAG, "=======Bwdm======StringUtils.isNotEmpty(str)***********");
			try {
				BwdmService service = new BwdmService();
				service.deleteAll();

				List<Bwdm> bwdms = PullXmlUtils.parseXMLData(Bwdm.class, str);
				if (bwdms.size() > 0) {
					service.insertList(bwdms);
					Log.i(TAG, "====Bwdm处理完成======" + bwdms.size());
					offDataHandler.obtainMessage(WHAT_DOWNLOAD_SUCCESS_ONE, OffLineUtil.OFFLINE_BWDM_ADD_RESULT_CODE).sendToTarget();
				}
			} catch (Exception e) {
				Log.i(TAG, "=======Bwdm======catch (Exception e)***********");

				e.printStackTrace();
				BaseApplication.instent.isDbBusy = false;
				offDataHandler.obtainMessage(WHAT_INSERT_DATA_FAILED_ONE, OffLineUtil.OFFLINE_BWDM_ADD_RESULT_CODE).sendToTarget();
			}
		} else {
			Log.i(TAG, "=======Bwdm====== !StringUtils.isNotEmpty(str)***********");
			offDataHandler.obtainMessage(WHAT_DOWNLOAD_ONE_RESULT_NULL, OffLineUtil.OFFLINE_BWDM_ADD_RESULT_CODE).sendToTarget();
		}
	}

	private void hgzjxxBusiness(String str, int flag) {

		Log.i(TAG, "=======Hgzjxx======证件信息返回的数据***********");
		if (StringUtils.isEmpty(str)) {
			offDataHandler.obtainMessage(WHAT_DOWNLOAD_ONE_RESULT_NULL, flag).sendToTarget();
		} else {
			try {
				// 先删除各类型的历史数据
				delHgzjxxHisByFlag();
				List<Hgzjxx> hgzjxxs = PullXmlUtils.parseXMLData(Hgzjxx.class, str);
				HgzjxxService service = null;
				if (hgzjxxs.size() > 0) {
					service = new HgzjxxService();
					service.insertList(hgzjxxs);
					Log.i(TAG, "====add===Hgzjxx======" + hgzjxxs.size());
				} else {
					Log.i(TAG, "====add===Hgzjxx======" + hgzjxxs.size());

				}
				offDataHandler.obtainMessage(WHAT_DOWNLOAD_SUCCESS_ONE, flag).sendToTarget();
			} catch (Exception e) {
				e.printStackTrace();
				BaseApplication.instent.isDbBusy = false;
				offDataHandler.obtainMessage(WHAT_INSERT_DATA_FAILED_ONE, flag).sendToTarget();
			}
		}
	}

	private void hgzjxxBusinessForCqz(String str) {

		Log.i(TAG, "=======Hgzjxx======证件信息返回的数据***********");
		if (StringUtils.isEmpty(str)) {
			offDataHandler.obtainMessage(WHAT_DOWNLOAD_ONE_RESULT_NULL, OffLineUtil.OFFLINE_HGZJXX_ADD_RESULT_CODE_FOR_CQZ).sendToTarget();
		} else if ("OK".equals(str)) {
			Log.i(TAG, "=======Hgzjxx======OK");
			offDataHandler.obtainMessage(WHAT_DOWNLOAD_SUCCESS_ONE, OffLineUtil.OFFLINE_HGZJXX_ADD_RESULT_CODE_FOR_CQZ).sendToTarget();
		} else {
			try {
				List<Hgzjxx> hgzjxxs = PullXmlUtils.parseXMLData(Hgzjxx.class, str);
				HgzjxxService service = null;
				if (hgzjxxs.size() > 0) {
					service = new HgzjxxService();
					service.insertList(hgzjxxs);
					Log.i(TAG, "====add===Hgzjxx======" + hgzjxxs.size());
				}
				this.requestHgzjxx();

			} catch (Exception e) {
				e.printStackTrace();
				BaseApplication.instent.isDbBusy = false;
				offDataHandler.obtainMessage(WHAT_INSERT_DATA_FAILED_ONE, OffLineUtil.OFFLINE_HGZJXX_ADD_RESULT_CODE_FOR_CQZ).sendToTarget();
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
		Log.i(TAG, "=======hgzjxxDel======hgzjxxDel返回的数据***********");
		if (StringUtils.isEmpty(str)) {
			Log.i(TAG, "======hgzjxxDelBusiness======request is empty");
			offDataHandler.obtainMessage(WHAT_DOWNLOAD_ONE_RESULT_NULL, OffLineUtil.OFFLINE_HGZJXX_DELETE_RESULT_CODE).sendToTarget();
		} else if ("OK".equals(str)) {
			offDataHandler.obtainMessage(WHAT_DOWNLOAD_SUCCESS_ONE, OffLineUtil.OFFLINE_HGZJXX_DELETE_RESULT_CODE).sendToTarget();
		} else {
			try {
				List<Hgzjxx> delHgzjxxs = PullXmlUtils.parseXMLData(Hgzjxx.class, str);
				HgzjxxService service = null;
				if (delHgzjxxs.size() > 0) {
					service = new HgzjxxService();
					service.delete(delHgzjxxs);
					Log.i(TAG, "======del Hgzjxx======" + delHgzjxxs.size());
				} else {
					Log.i(TAG, "======del Hgzjxx======" + delHgzjxxs.size());
				}
				this.requestDeleteHgzjxx();// 继续执行
			} catch (Exception e) {
				e.printStackTrace();
				BaseApplication.instent.isDbBusy = false;
				offDataHandler.obtainMessage(WHAT_INSERT_DATA_FAILED_ONE, OffLineUtil.OFFLINE_HGZJXX_DELETE_RESULT_CODE).sendToTarget();
			}
		}
	}

	private void qyxxBusiness(String str) {
		Log.i(TAG, "=======qyxxBusiness======qyxxBusiness返回的数据***********");
		if (StringUtils.isNotEmpty(str)) {
			try {
				QyxxService service = new QyxxService();
				service.deleteAll();
				List<Qyxx> list = PullXmlUtils.parseXMLData(Qyxx.class, str);
				if (list.size() > 0) {
					
					service.insertList(list);
					Log.i(TAG, "======Qyxx处理完成======" + list.size());
				} else {
					Log.i(TAG, "======Qyxx处理完成======" + list.size());
				}
				offDataHandler.obtainMessage(WHAT_DOWNLOAD_SUCCESS_ONE, OffLineUtil.OFFLINE_QYXX_ADD_RESULT_CODE).sendToTarget();
			} catch (Exception e) {
				e.printStackTrace();
				BaseApplication.instent.isDbBusy = false;
				offDataHandler.obtainMessage(WHAT_INSERT_DATA_FAILED_ONE, OffLineUtil.OFFLINE_QYXX_ADD_RESULT_CODE).sendToTarget();
			}
		} else {
			offDataHandler.obtainMessage(WHAT_DOWNLOAD_ONE_RESULT_NULL, OffLineUtil.OFFLINE_QYXX_ADD_RESULT_CODE).sendToTarget();
		}
	}

	/**
	 * 
	 * @方法名：cyxxBusiness
	 * @功能说明：船员数据处理
	 * @author 赵琳
	 * @date 2013-10-15 下午2:23:36
	 * @param str
	 */
	private void cyxxBusiness(String str, int flag) {
		Log.i(TAG, "=======cyxxBusiness======cyxxBusiness返回的数据***********");
		if (StringUtils.isNotEmpty(str)) {
			try {
				delCyxxHis();
				CyxxService service = new CyxxService();
				List<TBCyxx> list = PullXmlUtils.parseXMLData(TBCyxx.class, str);
				if (list.size() > 0) {
					// service.deleteAll();
					service.insertList(list);
					Log.i(TAG, "======TBCyxx处理完成======" + list.size());
				} else {
					Log.i(TAG, "======TBCyxx处理完成======" + list.size());
				}
				offDataHandler.obtainMessage(WHAT_DOWNLOAD_SUCCESS_ONE, flag).sendToTarget();
			} catch (Exception e) {
				e.printStackTrace();
				BaseApplication.instent.isDbBusy = false;
				offDataHandler.obtainMessage(WHAT_INSERT_DATA_FAILED_ONE, flag).sendToTarget();
			}
		} else {
			offDataHandler.obtainMessage(WHAT_DOWNLOAD_ONE_RESULT_NULL, flag).sendToTarget();
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
		Log.i(TAG, "=======fwxcbBusiness======fwxcbBusiness返回的数据***********");
		if (StringUtils.isNotEmpty(str)) {
			try {
				FwxcbService service = new FwxcbService();
				List<Fwxcb> list = PullXmlUtils.parseXMLData(Fwxcb.class, str);
				if (list.size() > 0) {
					service.deleteAll();
					service.insertList(list);
					Log.i(TAG, "======Fwxcb处理完成======" + list.size());
				} else {
					Log.i(TAG, "======Fwxcb处理完成======" + list.size());
				}
				offDataHandler.obtainMessage(WHAT_DOWNLOAD_SUCCESS_ONE, OffLineUtil.OFFLINE_FWXCB_ADD_RESULT_CODE).sendToTarget();
			} catch (Exception e) {
				e.printStackTrace();
				offDataHandler.obtainMessage(WHAT_INSERT_DATA_FAILED_ONE, OffLineUtil.OFFLINE_FWXCB_ADD_RESULT_CODE).sendToTarget();
			}
		} else {
			offDataHandler.obtainMessage(WHAT_DOWNLOAD_ONE_RESULT_NULL, OffLineUtil.OFFLINE_FWXCB_ADD_RESULT_CODE).sendToTarget();
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
		Log.i(TAG, "=======tbUserinfoBusiness======tbUserinfoBusiness返回的数据***********");
		if (StringUtils.isNotEmpty(str)) {
			try {
				TBUserinfoService service = new TBUserinfoService();
				List<TBUserinfo> list = PullXmlUtils.parseXMLData(TBUserinfo.class, str);
				if (list.size() > 0) {
					service.deleteAll();
					service.insertList(list);
					Log.i(TAG, "======TBUserinfo处理完成======" + list.size());
				} else {
					Log.i(TAG, "======TBUserinfo处理完成======" + list.size());
				}
				offDataHandler.obtainMessage(WHAT_DOWNLOAD_SUCCESS_ONE, OffLineUtil.OFFLINE_USERINFO_ADD_RESULT_CODE).sendToTarget();
			} catch (Exception e) {
				e.printStackTrace();
				offDataHandler.obtainMessage(WHAT_INSERT_DATA_FAILED_ONE, OffLineUtil.OFFLINE_USERINFO_ADD_RESULT_CODE).sendToTarget();
			}
		} else {
			offDataHandler.obtainMessage(WHAT_DOWNLOAD_ONE_RESULT_NULL, OffLineUtil.OFFLINE_USERINFO_ADD_RESULT_CODE).sendToTarget();
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
	private void kacbqkBusiness(String str, int flag) {
		Log.i(TAG, "=======kacbqkBusiness======kacbqkBusiness返回的数据***********");
		if (StringUtils.isNotEmpty(str)) {
			try {
				// 更新卡口、巡查绑定地点时需要删除原有船舶
				delKacbqkHis();
				List<Kacbqk> kacbqks = PullXmlUtils.parseXMLData(Kacbqk.class, str);
				KacbqkService service = null;
				if (kacbqks.size() > 0) {
					service = new KacbqkService();
					// service.deleteAll();
					service.insertList(kacbqks);
					Log.i(TAG, "======Kacbqk处理完成======" + kacbqks.size());
				} else {
					Log.i(TAG, "======Kacbqk处理完成======" + kacbqks.size());
				}
				offDataHandler.obtainMessage(WHAT_DOWNLOAD_SUCCESS_ONE, flag).sendToTarget();
			} catch (Exception e) {
				e.printStackTrace();
				BaseApplication.instent.isDbBusy = false;
				offDataHandler.obtainMessage(WHAT_INSERT_DATA_FAILED_ONE, flag).sendToTarget();
			}
		} else {
			offDataHandler.obtainMessage(WHAT_DOWNLOAD_ONE_RESULT_NULL, flag).sendToTarget();
		}
	}

	private void scsbBusiness(String str) {
		Log.i(TAG, "=======scsbBusiness======scsbBusiness返回的数据***********");
		if (StringUtils.isNotEmpty(str)) {
			try {
				List<Scsb> scsbs = PullXmlUtils.parseXMLData(Scsb.class, str);
				ScsbService service = null;
				if (scsbs.size() > 0) {
					service = new ScsbService();
					service.deleteAll();
					service.insertList(scsbs);
					Log.i(TAG, "======Scsb处理完成======" + scsbs.size());
				} else {
					Log.i(TAG, "======Scsb处理完成======" + scsbs.size());
				}
				offDataHandler.obtainMessage(WHAT_DOWNLOAD_SUCCESS_ONE, OffLineUtil.OFFLINE_SCSB_ADD_RESULT_CODE).sendToTarget();
			} catch (Exception e) {
				e.printStackTrace();
				offDataHandler.obtainMessage(WHAT_INSERT_DATA_FAILED_ONE, OffLineUtil.OFFLINE_SCSB_ADD_RESULT_CODE).sendToTarget();
			}
		} else {
			offDataHandler.obtainMessage(WHAT_DOWNLOAD_ONE_RESULT_NULL, OffLineUtil.OFFLINE_SCSB_ADD_RESULT_CODE).sendToTarget();
		}

	}

	private void sbxxBusiness(String str) {
		Log.i(TAG, "=======sbxxBusiness======sbxxBusiness返回的数据***********");
		if (StringUtils.isNotEmpty(str)) {
			try {
				List<Sbxx> sbxxs = PullXmlUtils.parseXMLData(Sbxx.class, str);
				SbxxService service = null;
				if (sbxxs.size() > 0) {
					service = new SbxxService();
					service.deleteAll();
					service.insertList(sbxxs);
					Log.i(TAG, "======Sbxx处理完成======" + sbxxs.size());
				} else {
					Log.i(TAG, "======Sbxx处理完成======" + sbxxs.size());
				}
				offDataHandler.obtainMessage(WHAT_DOWNLOAD_SUCCESS_ONE, OffLineUtil.OFFLINE_SBXX_ADD_RESULT_CODE).sendToTarget();
			} catch (Exception e) {
				e.printStackTrace();
				offDataHandler.obtainMessage(WHAT_INSERT_DATA_FAILED_ONE, OffLineUtil.OFFLINE_SBXX_ADD_RESULT_CODE).sendToTarget();
			}
		} else {
			offDataHandler.obtainMessage(WHAT_DOWNLOAD_ONE_RESULT_NULL, OffLineUtil.OFFLINE_SBXX_ADD_RESULT_CODE).sendToTarget();
		}
	}

	private void sxtglBusiness(String str) {
		Log.i(TAG, "=======sxtglBusiness======sxtglBusiness返回的数据***********");
		if (StringUtils.isNotEmpty(str)) {
			try {
				List<Sxtgl> sxtgls = PullXmlUtils.parseXMLData(Sxtgl.class, str);
				SxtglService service = null;
				if (sxtgls.size() > 0) {
					service = new SxtglService();
					service.deleteAll();
					service.insertList(sxtgls);
					Log.i(TAG, "======Sxtgl处理完成======" + sxtgls.size());
				} else {
					Log.i(TAG, "======Sxtgl处理完成======" + sxtgls.size());
				}
				offDataHandler.obtainMessage(WHAT_DOWNLOAD_SUCCESS_ONE, OffLineUtil.OFFLINE_SXTGL_ADD_RESULT_CODE).sendToTarget();
			} catch (Exception e) {
				e.printStackTrace();
				offDataHandler.obtainMessage(WHAT_INSERT_DATA_FAILED_ONE, OffLineUtil.OFFLINE_SXTGL_ADD_RESULT_CODE).sendToTarget();
			}
		} else {
			offDataHandler.obtainMessage(WHAT_DOWNLOAD_ONE_RESULT_NULL, OffLineUtil.OFFLINE_SXTGL_ADD_RESULT_CODE).sendToTarget();
		}
	}

	/**
	 * 
	 * @description 绑定船舶或卡口后，下载全部的相关离线数据
	 * @param str
	 * @param dataType
	 *            用来区分是什么类型的数据。<br>
	 *            绑定船舶后，下载离线数据的话，传值：OffLineUtil.
	 *            OFFLINE_ALL_OFFLINE_DATA_CODE_FOR_HC;<br>
	 *            绑定卡口后，下载离线数据的话，传值:OffLineUtil.
	 *            OFFLINE_ALL_OFFLINE_DATA_CODE_FOR_KKID
	 * @date 2014-5-8
	 * @author zhaotf
	 */
	private void allOffLineDataForBindShipBusiness(String str, int dataType) {
		Log.i(TAG, "=======allOffLineDataForBindShipBusiness======绑定船舶或卡口后，下载全部的相关离线数据   之数据处理***********");
		Log.i(TAG, "=======数据类型(OffLineUtil类里面的值)：" + dataType);
		if (StringUtils.isNotEmpty(str)) {

			try {
				PullXmlUtils.parseXMLDataForAllOfflineData(str);
				@SuppressWarnings("unchecked")
				List<Hgzjxx> hgzjxxs = PullXmlUtils.getHgzjxxs();

				if (hgzjxxs != null && hgzjxxs.size() > 0) {

					HgzjxxService service = null;

					service = new HgzjxxService();
					service.insertList(hgzjxxs);
					Log.i(TAG, "======处理完成hgzjxxs======" + hgzjxxs.size());
				} else {
					Log.i(TAG, "======处理完成hgzjxxs======" + hgzjxxs == null ? "null" : hgzjxxs.size() + "");
				}

				@SuppressWarnings("unchecked")
				List<TBCyxx> cyxxs = PullXmlUtils.getCyxxs();
				if (cyxxs != null && cyxxs.size() > 0) {
					CyxxService cyxxService = null;
					cyxxService = new CyxxService();
					// cyxxService.deleteAll();
					cyxxService.insertList(cyxxs);
					Log.i(TAG, "======处理完成cyxxs======" + cyxxs.size());
				} else {
					Log.i(TAG, "======处理完成cyxxs======" + cyxxs == null ? "null" : cyxxs.size() + "");
				}

				@SuppressWarnings("unchecked")
				List<Kacbqk> kacbqks = PullXmlUtils.getKacbqks();
				if (kacbqks != null && kacbqks.size() > 0) {
					KacbqkService kacbqkService = null;
					kacbqkService = new KacbqkService();
					// kacbqkService.deleteAll();
					kacbqkService.insertList(kacbqks);
					Log.i(TAG, "======处理完成kacbqks======" + kacbqks.size());
				} else {
					Log.i(TAG, "======处理完成kacbqks======" + kacbqks == null ? "null" : kacbqks.size() + "");
				}

			} catch (Exception e) {
				e.printStackTrace();
				BaseApplication.instent.isDbBusy = false;
				offDataHandler.obtainMessage(WHAT_INSERT_DATA_FAILED_ONE, dataType).sendToTarget();
			}
			offDataHandler.obtainMessage(WHAT_DOWNLOAD_ALL_SUCCESS, dataType).sendToTarget();
		} else {
			offDataHandler.obtainMessage(WHAT_DOWNLOAD_ONE_RESULT_NULL, dataType).sendToTarget();
		}
	}

	private void delHgzjxxHisByFlag() {
		switch (flag) {
		case OffLineUtil.DOWNLOAD_FOR_KACBQK:// 梯口船舶、巡检船舶
			DbUtil.deleteHgzjxxByHc(voyageNumber);
			break;
		case OffLineUtil.DOWNLOAD_FOR_QYXX:// 绑定卡口
			DbUtil.deletdHgzjxxForKakou();
			break;
		case OffLineUtil.DOWNLOAD_FOR_MT_OR_BW_QY:// 巡检绑定地点
			DbUtil.deleteHgzjxxForXjdd();
			break;
		default:
			break;
		}
	}

	private void delKacbqkHis() {
		switch (flag) {
		case OffLineUtil.DOWNLOAD_FOR_KACBQK:// 梯口船舶、巡检船舶
			// DbUtil.deleteHgzjxxByHc(voyageNumber);
			break;
		case OffLineUtil.DOWNLOAD_FOR_QYXX:// 绑定卡口
			DbUtil.deletdKacbqkForKakou();
			break;
		case OffLineUtil.DOWNLOAD_FOR_MT_OR_BW_QY:// 巡检绑定地点
			DbUtil.deleteKacbqkForXjdd();
			break;
		default:
			break;
		}
	}

	private void delCyxxHis() {
		switch (flag) {
		case OffLineUtil.DOWNLOAD_FOR_KACBQK:// 梯口船舶、巡检船舶
			DbUtil.deleteCyxxByHc();
			break;
		case OffLineUtil.DOWNLOAD_FOR_QYXX:// 绑定卡口
			DbUtil.deletdCyxxForKakou();
			break;
		case OffLineUtil.DOWNLOAD_FOR_MT_OR_BW_QY:// 巡检绑定地点
			DbUtil.deleteCyxxForXjdd();
			break;
		default:
			break;
		}
	}
}
