package com.pingtech.hgqw.module.offline.sbxx.dao;

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
import com.pingtech.hgqw.module.offline.sbxx.entity.Sbxx;
import com.pingtech.hgqw.utils.Log;
import com.pingtech.hgqw.utils.StringUtils;

/**
 * @title SbxxDao.java
 * @description 智能设备概要信息Dao
 * @author zhaotf
 * @company PingTech
 * @date 2013-10-18 下午5:28:44
 * @version V1.0
 * @Copyright(c)2013
 */

public class SbxxDao extends BaseHgqwDao<Sbxx> {

	private static final String TAG = "SbxxDao";

	public SbxxDao() {
		super();
		initDao();
	}

	private void initDao() {
		try {
			dao = DaoManager.getDaoManager().getSbxxDao();
			if (dao == null) {
				Log.i(TAG, "dao == null");
				DaoManager.getDaoManager().setSbxxDao((Dao<Sbxx, Integer>) getDao(Sbxx.class));
			}
			dao = DaoManager.getDaoManager().getSbxxDao();
			if (!dao.getConnectionSource().isOpen()) {
				Log.i(TAG, "isOpen == false");
				DaoManager.getDaoManager().setSbxxDao((Dao<Sbxx, Integer>) getDao(Sbxx.class));
			}
			dao = DaoManager.getDaoManager().getSbxxDao();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @方法名：insert
	 * @功能说明：插入一条信息
	 * @author zhaotf
	 * @date 2013-10-18 下午5:20:08 
	 * @param sbxx
	 * @return
	 * @throws SQLException
	 */
	public int insert(Sbxx sbxx) throws SQLException {
		dao.createOrUpdate(sbxx);
		return 0;
	}

	/**
	 * @方法名：insertList
	 * @功能说明：插入多条信息
	 * @author zhaotf
	 * @date 2013-10-18 下午5:20:25 
	 * @param list
	 * @return
	 * @throws SQLException
	 */
	public int insertList(List<Sbxx> sbxxList) throws SQLException {
		beginTransaction();
		int i = 0;
		for (Sbxx sbxx : sbxxList) {
			Log.i("insert Sbxx", i++ + "");
			insert(sbxx);
		}
		endTransaction();
		return 0;

	}

	/**
	 * @方法名：delete
	 * @功能说明：删除一条信息
	 * @author zhaotf
	 * @date 2013-10-18 下午5:22:39 
	 * @param sbxx
	 * @return
	 * @throws SQLException
	 */
	public int delete(Sbxx sbxx) throws SQLException {
		return dao.delete(sbxx);
	}

	/**
	 * @方法名：deleteAll
	 * @功能说明：删除所有信息
	 * @author zhaotf
	 * @date 2013-10-18 下午5:26:28 
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
	 * @date 2013-10-18 下午5:26:39 
	 * @param sbxx
	 * @return
	 * @throws SQLException
	 */
	public int update(Sbxx sbxx) throws SQLException {
		return dao.update(sbxx);
	}

	/**
	 * @方法名：findAll
	 * @功能说明：查询所有信息
	 * @author zhaotf
	 * @date 2013-10-18 下午5:27:00 
	 * @return
	 * @throws SQLException
	 */
	public List<Sbxx> findAll() throws SQLException {
		return dao.queryForAll();
	}

	/**
	 * @方法名：findAllForException
	 * @功能说明：异常信息模块数据查询
	 * @author zhaotf
	 * @date 2013-10-18 下午5:27:42 
	 * @param params
	 * @return
	 * @throws SQLException
	 */
	public List<Map<String, String>> findAllForException(Map<String, Object> params) throws SQLException {
		QueryBuilder<Sbxx, Integer> queryBuilder = dao.queryBuilder();
		Where<Sbxx, Integer> where = queryBuilder.where();
		where.isNotNull("id");
		if (StringUtils.isNotEmpty(params.get("sbbh"))) {
			where.and().like("sbbh", "%" + params.get("sbbh").toString().trim() + "%");
		}
		if (StringUtils.isNotEmpty(params.get("type"))) {
			where.and().like("lx", "%" + params.get("type").toString().trim() + "%");
		}
		if (StringUtils.isNotEmpty(params.get("name"))) {
			where.and().like("sbmc", "%" + params.get("name").toString().trim() + "%");
		}

		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		List<Sbxx> lists = where.query();
		for (int i = 0; i < lists.size(); i++) {
			Sbxx sbxx = lists.get(i);
			if (sbxx != null) {
				Map<String, String> info = new HashMap<String, String>();
				info.put("id", sbxx.getId());
				info.put("sbbh", sbxx.getSbbh());
				info.put("name", sbxx.getSbmc());
				info.put("ip", sbxx.getIp());
				info.put("type", sbxx.getLx());
				list.add(info);
			}
		}
		return list;
	}
}
