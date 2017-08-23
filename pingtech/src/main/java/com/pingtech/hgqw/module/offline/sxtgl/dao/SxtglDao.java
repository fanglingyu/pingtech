package com.pingtech.hgqw.module.offline.sxtgl.dao;

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
import com.pingtech.hgqw.module.offline.sxtgl.entity.Sxtgl;
import com.pingtech.hgqw.utils.Log;
import com.pingtech.hgqw.utils.StringUtils;
/**
 * @title SxtglDao.java
 * @description 获取摄像头概要信息Dao
 * @author zhaotf
 * @company PingTech
 * @date 2013-10-18 下午5:38:47
 * @version V1.0
 * @Copyright(c)2013
 */
public class SxtglDao extends BaseHgqwDao<Sxtgl> {

	private static final String TAG = "SxtglDao";

	public SxtglDao() {
		super();
		initDao();
	}

	private void initDao() {
		try {
			dao = DaoManager.getDaoManager().getSxtglDao();
			if (dao == null) {
				Log.i(TAG, "dao == null");
				DaoManager.getDaoManager().setSxtglDao((Dao<Sxtgl, Integer>) getDao(Sxtgl.class));
			}
			dao = DaoManager.getDaoManager().getSxtglDao();
			if (!dao.getConnectionSource().isOpen()) {
				Log.i(TAG, "isOpen == false");
				DaoManager.getDaoManager().setSxtglDao((Dao<Sxtgl, Integer>) getDao(Sxtgl.class));
			}
			dao = DaoManager.getDaoManager().getSxtglDao();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @方法名：insert
	 * @功能说明：插入一条信息
	 * @author zhaotf
	 * @date 2013-10-18 下午5:39:12 
	 * @param sxtgl
	 * @return
	 * @throws SQLException
	 */
	public int insert(Sxtgl sxtgl) throws SQLException {
		dao.createOrUpdate(sxtgl);
		return 0;
	}

	/**
	 * @方法名：insertList
	 * @功能说明：插入多条信息
	 * @author zhaotf
	 * @date 2013-10-18 下午5:39:24 
	 * @param sxtglList
	 * @return
	 * @throws SQLException
	 */
	public int insertList(List<Sxtgl> sxtglList) throws SQLException {
		beginTransaction();
		int i = 0;
		for (Sxtgl sxtgl : sxtglList) {
			insert(sxtgl);
		}
		endTransaction();
		return 0;

	}

	/**
	 * @方法名：delete
	 * @功能说明：删除一条信息
	 * @author zhaotf
	 * @date 2013-10-18 下午5:39:39 
	 * @param sxtgl
	 * @return
	 * @throws SQLException
	 */
	public int delete(Sxtgl sxtgl) throws SQLException {
		return dao.delete(sxtgl);
	}

	/**
	 * @方法名：deleteAll
	 * @功能说明：删除全部信息
	 * @author zhaotf
	 * @date 2013-10-18 下午5:39:54 
	 * @return
	 * @throws SQLException
	 */
	public int deleteAll() throws SQLException {
		return dao.deleteBuilder().delete();
	}

	/**
	 * @方法名：update
	 * @功能说明：更新一条信息
	 * @author zhaotf
	 * @date 2013-10-18 下午5:40:09 
	 * @param sxtgl
	 * @return
	 * @throws SQLException
	 */
	public int update(Sxtgl sxtgl) throws SQLException {
		return dao.update(sxtgl);
	}
	/**
	 * @方法名：findAll
	 * @功能说明：查找全部信息
	 * @author zhaotf
	 * @date 2013-10-18 下午5:40:22 
	 * @return
	 * @throws SQLException
	 */
	public List<Sxtgl> findAll() throws SQLException {
		return dao.queryForAll();
	}
	
	/**
	 * @方法名：findAllForException
	 * @功能说明：异常信息模块获取获取摄像头概要信息
	 * @author zhaotf
	 * @date 2013-10-18 下午5:40:37 
	 * @param params
	 * @return
	 * @throws SQLException
	 */
	public List<Map<String, String>> findAllForException(Map<String, Object> params) throws SQLException {
		QueryBuilder<Sxtgl, Integer> queryBuilder = dao.queryBuilder();
		Where<Sxtgl, Integer> where = queryBuilder.where();
		where.isNotNull("id");
		if (StringUtils.isNotEmpty(params.get("sbbh"))) {
			where.and().like("sxjbh", "%" + params.get("sbbh").toString().trim() + "%");
		}
		if (StringUtils.isNotEmpty(params.get("name"))) {
			where.and().like("sxjmc", "%" + params.get("name").toString().trim() + "%");
		}
		if (StringUtils.isNotEmpty(params.get("type"))) {
			where.and().eq("sxtlx", params.get("type").toString().trim());
		}
		List<Map<String, String>> mapList = new ArrayList<Map<String, String>>();
		List<Sxtgl> sxtglLists = where.query();
		for (int i = 0; i < sxtglLists.size(); i++) {
			Sxtgl sxtgl = sxtglLists.get(i);
			if (sxtgl != null) {
				Map<String, String> info = new HashMap<String, String>();
				info.put("id", sxtgl.getId());
				info.put("sbbh", sxtgl.getSxjbh());
				info.put("name", sxtgl.getSxjmc());
				info.put("type", sxtgl.getSxtlx());
				info.put("ip", sxtgl.getIp());
				info.put("ssdw", sxtgl.getSydwmc());
				mapList.add(info);
			}
		}
		return mapList;
	}
}
