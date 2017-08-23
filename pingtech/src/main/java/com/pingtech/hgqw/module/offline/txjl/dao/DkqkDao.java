package com.pingtech.hgqw.module.offline.txjl.dao;

import java.sql.SQLException;
import java.util.List;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.j256.ormlite.stmt.Where;
import com.pingtech.hgqw.module.offline.DBFlag;
import com.pingtech.hgqw.module.offline.base.dao.BaseHgqwDao;
import com.pingtech.hgqw.module.offline.base.utils.DaoManager;
import com.pingtech.hgqw.module.offline.hgzjxx.entity.Hgzjxx;
import com.pingtech.hgqw.module.offline.txjl.entity.Dkqk;
import com.pingtech.hgqw.module.offline.zjyf.util.YfZjxxConstant;
import com.pingtech.hgqw.utils.Log;

public class DkqkDao extends BaseHgqwDao<Dkqk> {

	private static final String TAG = "DkqkDao";

	public DkqkDao() {
		super();
		initDao();
	}

	private void initDao() {
		try {
			dao = DaoManager.getDaoManager().getDkqkDao();
			if (dao == null) {
				Log.i(TAG, "dao == null");
				DaoManager.getDaoManager().setDkqkDao((Dao<Dkqk, Integer>) getDao(Dkqk.class));
			}
			dao = DaoManager.getDaoManager().getDkqkDao();
			if (!dao.getConnectionSource().isOpen()) {
				Log.i(TAG, "isOpen == false");
				DaoManager.getDaoManager().setDkqkDao((Dao<Dkqk, Integer>) getDao(Dkqk.class));
			}
			dao = DaoManager.getDaoManager().getDkqkDao();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @方法名：insert
	 * @功能说明：插入一条数据
	 * @author liums
	 * @date 2013-9-25 上午9:48:33
	 * @param dkqk
	 * @return
	 * @throws SQLException
	 */
	public int insert(Dkqk dkqk) throws SQLException {
		DBFlag.isDBBusy();

		dao.createOrUpdate(dkqk);

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
	public int insertList(List<Dkqk> list) throws SQLException {
		if (list == null) {
			return -1;
		}
		beginTransaction();
		int result = 1;
		for (Dkqk dkqk : list) {
			insert(dkqk);
		}
		endTransaction();
		return result;

	}

	/**
	 * 
	 * @方法名：delete
	 * @功能说明：删除一条数据
	 * @author liums
	 * @date 2013-9-25 上午9:49:02
	 * @param dkqk
	 * @return
	 * @throws SQLException
	 */
	public int delete(Dkqk dkqk) throws SQLException {
		DBFlag.isDBBusy();
		dao.delete(dkqk);
		DBFlag.setDBOnlyNotBusy();

		return 0;

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

	/**
	 * 
	 * @方法名：update
	 * @功能说明：更新制定数据
	 * @author liums
	 * @date 2013-9-25 上午9:49:12
	 * @param dkqk
	 * @return
	 * @throws SQLException
	 */
	public int update(Dkqk dkqk) throws SQLException {
		return dao.update(dkqk);
	}

	/**
	 * 
	 * @方法名：findAll
	 * @功能说明：查询所有数据
	 * @author liums
	 * @date 2013-9-25 上午9:49:24
	 * @return
	 * @throws SQLException
	 */
	public List<Dkqk> findAll() throws SQLException {
		return dao.queryForAll();
	}

	public Dkqk findDkqkByHgzjxx(Hgzjxx hgzjxx, String kacbqkid) throws SQLException {
		QueryBuilder<Dkqk, Integer> queryBuilder = dao.queryBuilder();
		Where<Dkqk, Integer> where = queryBuilder.where();
		where.and(where.eq("kacbqkid", kacbqkid), where.eq("zjffxxId", hgzjxx.getCbzjffxxxid()));
		Dkqk dkqk = where.queryForFirst();
		return dkqk;
	}

	public void saveOrUpdateDkfx(Hgzjxx hgzjxx, String kacbqkid, String dkfx, String dkqkid) throws SQLException {
		// 先查看，如果有则更新，没有则新增
		Dkqk dkqk = null;
		dkqk = findDkqkByHgzjxx(hgzjxx, kacbqkid);
		if (dkqk != null) {
			DBFlag.isDBBusy(); 

			UpdateBuilder<Dkqk, Integer> updateBuilder = dao.updateBuilder();
			Where<Dkqk, Integer> where = updateBuilder.where();
			where.and(where.eq("kacbqkid", kacbqkid), where.eq("zjffxxId", hgzjxx.getCbzjffxxxid()));
			updateBuilder.updateColumnValue("dkfx", dkfx);
			updateBuilder.update();

			DBFlag.setDBOnlyNotBusy();

		} else {
			dkqk = new Dkqk();
			dkqk.setKacbqkid(kacbqkid);
			dkqk.setDkqkid(dkqkid);
			dkqk.setDkfx(dkfx);
			dkqk.setZjffxxId(hgzjxx.getCbzjffxxxid());
			insert(dkqk);
		}

	}

	/**
	 * 
	 * @方法名：changDkfxByDkjlid
	 * @功能说明：根据搭靠id修改搭靠方向
	 * @author liums
	 * @date 2013-10-24 下午8:00:28
	 * @param dkjlid
	 * @throws SQLException
	 */
	public void changDkfxByDkjlid(String dkjlid) throws SQLException {
		DBFlag.isDBBusy();

		Dkqk dkqk = null;
		dkqk = findDkqkByDkjlid(dkjlid);
		if (dkqk != null) {
			String dkfx = dkqk.getDkfx();
			dkqk.setDkfx(changeDkfx(dkfx));
			dao.update(dkqk);
		}

		DBFlag.setDBOnlyNotBusy();

	}

	/**
	 * 
	 * @方法名：changeDkfx
	 * @功能说明：转换方向
	 * @author liums
	 * @date 2013-10-24 下午7:58:24
	 * @param dkfx
	 * @return
	 */
	private String changeDkfx(String dkfx) {
		if (YfZjxxConstant.TXFX_SC.equals(dkfx)) {
			return YfZjxxConstant.TXFX_XC;
		}
		return YfZjxxConstant.TXFX_SC;
	}

	/**
	 * 
	 * @方法名：findDkqkByDkjlid
	 * @功能说明：根据id查询对象
	 * @author liums
	 * @date 2013-10-24 下午7:44:59
	 * @param dkjlid
	 * @return
	 */
	private Dkqk findDkqkByDkjlid(String dkjlid) throws SQLException {
		QueryBuilder<Dkqk, Integer> queryBuilder = dao.queryBuilder();
		Where<Dkqk, Integer> where = queryBuilder.where();
		where.eq("dkqkid", dkjlid);
		return where.queryForFirst();
	}

	public void deleteByKacbqkidAndZjxxid(String cbzjffxxxid, String kacbqkid) throws SQLException {
		DBFlag.isDBBusy();

		DeleteBuilder<Dkqk, Integer> deleteBuilder = dao.deleteBuilder();
		Where<Dkqk, Integer> where = deleteBuilder.where();
		where.and(where.eq("kacbqkid", kacbqkid), where.eq("zjffxxId", cbzjffxxxid));
		deleteBuilder.delete();

		DBFlag.setDBOnlyNotBusy();

	}

}
