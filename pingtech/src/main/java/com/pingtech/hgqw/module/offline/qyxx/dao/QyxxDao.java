package com.pingtech.hgqw.module.offline.qyxx.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.util.Log;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;
import com.pingtech.hgqw.module.offline.DBFlag;
import com.pingtech.hgqw.module.offline.base.dao.BaseHgqwDao;
import com.pingtech.hgqw.module.offline.base.utils.DaoManager;
import com.pingtech.hgqw.module.offline.qyxx.entity.Qyxx;

public class QyxxDao extends BaseHgqwDao<Qyxx> {

	private static final String TAG = "QyxxDao";

	public QyxxDao() {
		super();
		initDao();

	}

	private void initDao() {
		try {
			dao = DaoManager.getDaoManager().getQyxxDao();
			if (dao == null) {
				Log.i(TAG, "dao == null");
				DaoManager.getDaoManager().setQyxxDao((Dao<Qyxx, Integer>) getDao(Qyxx.class));
			}
			dao = DaoManager.getDaoManager().getQyxxDao();
			if (!dao.getConnectionSource().isOpen()) {
				Log.i(TAG, "isOpen == false");
				DaoManager.getDaoManager().setQyxxDao((Dao<Qyxx, Integer>) getDao(Qyxx.class));
			}
			dao = DaoManager.getDaoManager().getQyxxDao();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public int insert(Qyxx qyxx) throws SQLException {

		DBFlag.isDBBusy();

		dao.createOrUpdate(qyxx);
		DBFlag.setDBOnlyNotBusy();

		return 0;
	}

	/**
	 * 
	 * @方法名：insertList
	 * @功能说明：批量插入数据
	 * @author liums
	 * @date 2013-9-25 上午9:48:46
	 * @param list
	 * @return
	 * @throws SQLException
	 */
	public int insertList(List<Qyxx> list) throws SQLException {
		if (list == null) {
			return -1;
		}
		beginTransaction();
		int result = 1;
		for (Qyxx qyxx : list) {
			insert(qyxx);
		}
		endTransaction();
		return result;

	}

	/**
	 * 
	 * @方法名：bbyIdGetQyxx
	 * @功能说明：通过ID得到区域信息
	 * @author 赵琳
	 * @date 2013-10-11 上午11:32:17
	 * @param kacbqkid
	 * @param hc
	 * @param cbdh
	 * @return
	 * @throws SQLException
	 */
	public Qyxx byIdGetQyxx(String qyid) throws SQLException {
		QueryBuilder<Qyxx, Integer> queryBuilder = dao.queryBuilder();
		queryBuilder.where().eq("id", qyid);
		PreparedQuery<Qyxx> prepare = queryBuilder.prepare();
		Qyxx qyxx = dao.queryForFirst(prepare);
		return qyxx;
	}

	/**
	 * 
	 * @方法名：deleteAll
	 * @功能说明：删除数据
	 * @author 娄高伟
	 * @date 2013-10-12 下午6:49:38
	 * @return
	 * @throws SQLException
	 */
	public int deleteAll() throws SQLException {
		DBFlag.isDBBusy();
		dao.deleteBuilder().delete();
		DBFlag.setDBOnlyNotBusy();

		return 0;
	}

	public List<Map<String, String>> findQyxxByPrams(Map<String, Object> mapParms) throws SQLException {
		QueryBuilder<Qyxx, Integer> queryBuilder = dao.queryBuilder();
		Where<Qyxx, Integer> where = queryBuilder.where();
		where.eq("qylx", "kkqy");
		if (mapParms.get("kkmc") != null) {
			where.and().like("qymc", "%" + mapParms.get("kkmc") + "%");
		}
		if (mapParms.get("cardNumber") != null && !"".equals(mapParms.get("cardNumber"))) {
			where.and().eq("dzbqh", mapParms.get("cardNumber"));
		}
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		List<Qyxx> lists = where.query();
		for (int i = 0; i < lists.size(); i++) {
			Qyxx qyxx = lists.get(i);
			Map<String, String> map = new HashMap<String, String>();
			if (qyxx != null) {
				map.put("cardNumber", qyxx.getDzbqh());
				map.put("id", qyxx.getId());
				map.put("kkmc", qyxx.getQymc());
				map.put("kkfw", qyxx.getQyfw());
				map.put("kkxx", qyxx.getXxdz());
				map.put("bdzt", "未绑定");

				list.add(map);
			}
		}
		return list;
	}

	public Qyxx getQyxxByQyid(String id) throws SQLException {
		List<Qyxx> list = dao.queryBuilder().where().eq("id", id).query();
		if (list != null && list.size() > 0) {
			return list.get(0);
		}
		return null;
	}
}
