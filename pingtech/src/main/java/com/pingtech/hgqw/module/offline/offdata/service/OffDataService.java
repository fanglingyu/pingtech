package com.pingtech.hgqw.module.offline.offdata.service;

import java.net.URLEncoder;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.http.NameValuePair;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.pingtech.R;
import com.pingtech.hgqw.base.BaseApplication;
import com.pingtech.hgqw.module.offline.base.service.BaseHgqwService;
import com.pingtech.hgqw.module.offline.offdata.dao.OffDataDao;
import com.pingtech.hgqw.module.offline.offdata.entity.OffData;
import com.pingtech.hgqw.utils.BasicNameValuePair;
import com.pingtech.hgqw.utils.DeviceUtils;
import com.pingtech.hgqw.utils.xml.HashBuild;
import com.pingtech.hgqw.utils.xml.XmlUtils;

public class OffDataService extends BaseHgqwService {
	private static final String TAG = "OffDataService";

	private OffDataDao offDataDao = null;

	public OffDataService() {
		this.offDataDao = new OffDataDao();
	}

	/**
	 * 
	 * @方法名：insert
	 * @功能说明：插入一条数据
	 * @author liums
	 * @date 2013-9-23 下午5:32:43
	 * @param offData
	 * @return
	 * @throws SQLException
	 */
	public int insert(OffData offData) throws SQLException {
		try {
			offDataDao.insert(offData);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	/**
	 * 
	 * @方法名：create
	 * @功能说明：插入一条数据(如果存在，在保留原来的基础上添加)
	 * @author zhaotf
	 * @date 2013-9-25 上午9:48:33
	 * @param offData
	 * @return
	 * @throws SQLException
	 */
	public int create(OffData offData) throws SQLException {
		return offDataDao.create(offData);
	}

	/**
	 * 
	 * @方法名：insertList
	 * @功能说明：批量插入数据
	 * @author liums
	 * @date 2013-9-25 上午9:49:52
	 * @param list
	 * @return
	 * @throws SQLException
	 */
	public int insertList(List<OffData> list) throws SQLException {
		return offDataDao.insertList(list);
	}

	/**
	 * 
	 * @方法名：delete
	 * @功能说明：删除一条数据
	 * @author liums
	 * @date 2013-9-23 下午5:32:58
	 * @param offData
	 * @return
	 * @throws SQLException
	 */
	public int delete(OffData offData) throws SQLException {
		return offDataDao.delete(offData);
	}

	/**
	 * 
	 * @方法名：deleteByCzid
	 * @功能说明：删除根据操作ID
	 * @author 娄高伟
	 * @date 2013-10-20 上午11:22:40
	 * @param id
	 * @return
	 * @throws SQLException
	 */
	public int deleteByCzid(String id) throws SQLException {
		return offDataDao.deleteByCzid(id);
	}

	/**
	 * 
	 * @方法名：update
	 * @功能说明：更新指定对象
	 * @author liums
	 * @date 2013-9-23 下午5:33:07
	 * @param offData
	 * @return
	 * @throws SQLException
	 */
	public int update(OffData offData) throws SQLException {
		return offDataDao.update(offData);
	}

	/**
	 * 
	 * @方法名：select
	 * @功能说明：查询所有数据
	 * @author liums
	 * @date 2013-9-23 下午5:33:25
	 * @return
	 * @throws SQLException
	 */
	public List<OffData> findAll() throws SQLException {
		return offDataDao.findAll();
	}

	/**
	 * 
	 * @方法名：findByCzid
	 * @功能说明：查询数据
	 * @author 娄高伟
	 * @date 2013-11-12 下午8:40:15
	 * @param czid
	 * @return
	 * @throws SQLException
	 */
	public OffData findByCzid(String czid) throws SQLException {
		return offDataDao.findByCzid(czid);
	}

	/**
	 * 
	 * @方法名：findAllByGN
	 * @功能说明：以功能点查询数据
	 * @author 娄高伟
	 * @date 2013-9-27 上午11:55:57
	 * @param czmk
	 * @param czgn
	 * @return
	 * @throws SQLException
	 */
	public List<OffData> findAllByGN(int czmk, int czgn, String clstatus, String czid) throws SQLException {
		return offDataDao.findAllByGN(czmk, czgn, clstatus, czid);
	}

	/**
	 * 
	 * @方法名：findAllByGN
	 * @功能说明：查询离线数据
	 * @author 娄高伟
	 * @date 2013-10-30 下午6:19:00
	 * @param czmk
	 * @param czgn
	 * @param clstatus
	 * @param czid
	 * @param userid
	 * @return
	 * @throws SQLException
	 */
	public List<OffData> findAllByGN(int czmk, int czgn, String clstatus, String czid, String userid) throws SQLException {
		return offDataDao.findAllByGN(czmk, czgn, clstatus, czid, userid);
	}

	/**
	 * 
	 * @方法名：findOneByGN
	 * @功能说明：根据功能模块查询离线数据
	 * @author 娄高伟
	 * @date 2013-11-12 下午2:38:43
	 * @param czmk
	 * @param czgn
	 * @return
	 * @throws SQLException
	 */
	public OffData findOneByGN(int czmk, int czgn) throws SQLException {
		return offDataDao.findOneByGN(czmk, czgn);
	}

	/**
	 * 
	 * @方法名：findModuleByGN
	 * @功能说明：根据功能模块获得集合数据
	 * @author 娄高伟
	 * @date 2013-10-14 下午12:58:00
	 * @param czmk
	 * @param czgn
	 * @param clstatus
	 * @return
	 * @throws SQLException
	 */
	public List<Map<String, String>> findModuleByGN(int czmk, int czgn, String clstatus, String czid) throws SQLException {
		return offDataDao.findModuleByGN(czmk, czgn, clstatus, czid);
	}

	/**
	 * 
	 * @方法名：deleteByIds
	 * @功能说明：删除多条数据
	 * @author 娄高伟
	 * @date 2013-9-27 上午11:53:49
	 * @param ids
	 * @return
	 * @throws SQLException
	 */

	public int deleteByIds(Collection<Integer> ids) throws SQLException {
		return offDataDao.deleteByIds(ids);
	}

	/**
	 * 
	 * @方法名：offLineDataPackage
	 * @功能说明：离线业务数据封装
	 * @author liums
	 * @date 2013-9-26 下午4:28:12
	 * @param czmk
	 *            操作模块
	 * @param czgn
	 *            操作功能
	 * @return
	 */

	public void offLineDataPackage(List<NameValuePair> params, List<Integer> ids) {
		SharedPreferences prefs = BaseApplication.instent.getSharedPreferences(BaseApplication.instent.getString(R.string.app_name),
				Context.MODE_PRIVATE);
		String userid = prefs.getString("userid", "");
		params.add(new BasicNameValuePair("userid", userid));
		params.add(new BasicNameValuePair("pdacode", DeviceUtils.getIMEI(BaseApplication.instent)));
		HashBuild datas = null;
		HashBuild data = null;
		try {
			Map<String, List<OffData>> offdatas = offDataDao.findAllByCount(300);
			if (offdatas != null) {
				datas = new HashBuild(offdatas.size());
				Set<Map.Entry<String, List<OffData>>> set = offdatas.entrySet();
				for (Iterator<Map.Entry<String, List<OffData>>> iterator = set.iterator(); iterator.hasNext();) {
					HashBuild head = new HashBuild(4);
					Map.Entry<String, List<OffData>> entity = iterator.next();
					String key = entity.getKey();
					List<OffData> list = entity.getValue();
					data = new HashBuild(list.size() + 1);
					StringBuffer sb = new StringBuffer();
					String[] mkgn = key.split("_");
					head.put("czmk", mkgn[0]);
					head.put("czgn", mkgn[1]);
					head.put("userid", userid);
					head.put("pdacode", DeviceUtils.getIMEI());
					data.put("head", head);
					for (int i = 0; i < list.size(); i++) {
						OffData offData = list.get(i);
						ids.add(offData.getId());
						sb.append(offData.getXmldata());
					}
					try {
						data.put("body", URLEncoder.encode(sb.toString(), "UTF-8"));
					} catch (Exception e) {
						Log.i(TAG, "离线数据压缩出错");
						e.printStackTrace();
					}
					datas.put("data", data);

				}
				params.add(new BasicNameValuePair("xmlData", XmlUtils.buildXml(datas.get())));

			} else {
				params.add(new BasicNameValuePair("xmlData", ""));
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
		}

	}

	public OffData getOffDataByCzid(int czmk, int czgn, String czid) throws SQLException {
		return offDataDao.findAllByGN(czmk, czgn, czid);
	}
	
	public List<OffData> findByCount(long count) throws SQLException {
		return offDataDao.findByCount(count);
	}
}
