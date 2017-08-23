package com.pingtech.hgqw.module.offline.scsb.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;
import com.pingtech.hgqw.module.offline.base.dao.BaseHgqwDao;
import com.pingtech.hgqw.module.offline.base.utils.DaoManager;
import com.pingtech.hgqw.module.offline.scsb.entity.Scsb;
import com.pingtech.hgqw.utils.Log;
import com.pingtech.hgqw.utils.StringUtils;

/**
 * @title ScsbDao.java
 * @description 获取手持设备概要信息Dao
 * @author zhaotf
 * @company PingTech
 * @date 2013-10-18 下午5:31:26
 * @version V1.0
 * @Copyright(c)2013
 */
public class ScsbDao extends BaseHgqwDao<Scsb> {

	private static final String TAG = "ScsbDao";
	public ScsbDao() {
		super();
		initDao();
	}

	private void initDao() {
		try {
			dao = DaoManager.getDaoManager().getScsbDao();
			if (dao == null) {
				Log.i(TAG, "dao == null");
				DaoManager.getDaoManager().setScsbDao((Dao<Scsb, Integer>) getDao(Scsb.class));
			}
			dao = DaoManager.getDaoManager().getScsbDao();
			if (!dao.getConnectionSource().isOpen()) {
				Log.i(TAG, "isOpen == false");
				DaoManager.getDaoManager().setScsbDao((Dao<Scsb, Integer>) getDao(Scsb.class));
			}
			dao = DaoManager.getDaoManager().getScsbDao();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @方法名：insert
	 * @功能说明：插入一条Scsb对象
	 * @author zhaotf
	 * @date 2013-10-18 下午5:31:45 
	 * @param scsb
	 * @return
	 * @throws SQLException
	 */
	public int insert(Scsb scsb) throws SQLException {
		dao.createOrUpdate(scsb);
		return 0;
	}
	/**
	 * @方法名：insertList
	 * @功能说明：插入多条信息
	 * @author zhaotf
	 * @date 2013-10-18 下午5:32:00 
	 * @param scsbList
	 * @return
	 * @throws SQLException
	 */
	public int insertList(List<Scsb> scsbList) throws SQLException {
		beginTransaction();
		int i = 0;
		for (Scsb scsb : scsbList) {
			insert(scsb);
		}
		endTransaction();
		return 0;

	}

	/**
	 * @方法名：delete
	 * @功能说明：删除一条记录
	 * @author zhaotf
	 * @date 2013-10-18 下午5:32:21 
	 * @param scsb
	 * @return
	 * @throws SQLException
	 */
	public int delete(Scsb scsb) throws SQLException {
		return dao.delete(scsb);
	}
	/**
	 * @方法名：deleteAll
	 * @功能说明：删除所有信息
	 * @author zhaotf
	 * @date 2013-10-18 下午5:32:34 
	 * @return
	 * @throws SQLException
	 */
	public int deleteAll() throws SQLException {
		return dao.deleteBuilder().delete();
	}
	/**
	 * @方法名：update
	 * @功能说明：修改一条信息
	 * @author zhaotf
	 * @date 2013-10-18 下午5:32:47 
	 * @param scsb
	 * @return
	 * @throws SQLException
	 */
	public int update(Scsb scsb) throws SQLException {
		return dao.update(scsb);
	}
	/**
	 * @方法名：findAll
	 * @功能说明：查询所有数据
	 * @author zhaotf
	 * @date 2013-10-18 下午5:33:12 
	 * @return
	 * @throws SQLException
	 */
	public List<Scsb> findAll() throws SQLException {
		return dao.queryForAll();
	}
	/**
	 * @方法名：findAllForException
	 * @功能说明：异常信息模块 手持设备概要信息查询
	 * @author zhaotf
	 * @date 2013-10-18 下午5:33:25 
	 * @param params
	 * @return
	 * @throws SQLException
	 */
	public List<Map<String, String>> findAllForException(Map<String, Object> params) throws SQLException {
		QueryBuilder<Scsb, Integer> queryBuilder = dao.queryBuilder();
		Where<Scsb, Integer> where = queryBuilder.where();
		where.isNotNull("id");
		if (StringUtils.isNotEmpty(params.get("sbbh"))) {
			where.and().like("sbbh", "%" + params.get("sbbh").toString().trim() + "%");
		}
		if (StringUtils.isNotEmpty(params.get("sbxh"))) {
			where.and().like("sbxh", "%" + params.get("sbxh").toString().trim() + "%");
		}
		if (StringUtils.isNotEmpty(params.get("type"))) {
			where.and().eq("sylx", params.get("type").toString().trim());
		}
		if (StringUtils.isNotEmpty(params.get("ip"))) {
			where.and().eq("ip", params.get("ip").toString().trim());
		}
		if (StringUtils.isNotEmpty(params.get("zt"))) {
			where.and().eq("zt", params.get("zt").toString().trim());
		}
	
		List<Map<String, String>> list=new ArrayList<Map<String,String>>();
		List<Scsb> lists=where.query();
		for (int i = 0; i < lists.size(); i++) {
			Scsb scsb = lists.get(i);
			if (scsb != null) {
				Map<String, String> info = new HashMap<String, String>();
				info.put("id", scsb.getId());
				info.put("sbbh", scsb.getSbbh());
				info.put("sbxh", scsb.getSbxh());
				info.put("ip", scsb.getIp());
				info.put("type", scsb.getSylx());
				info.put("zt", scsb.getZt());
				info.put("ssdw", scsb.getSsdw());
				list.add(info);
			}
		}
		return list;
	}
}
