package com.pingtech.hgqw.module.offline.txjl.dao;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.j256.ormlite.stmt.Where;
import com.pingtech.hgqw.module.offline.DBFlag;
import com.pingtech.hgqw.module.offline.base.dao.BaseHgqwDao;
import com.pingtech.hgqw.module.offline.base.utils.DaoManager;
import com.pingtech.hgqw.module.offline.txjl.entity.TxjlKk;
import com.pingtech.hgqw.utils.Log;

public class TxjlKkDao extends BaseHgqwDao<TxjlKk> {

	private static final String TAG = "TxjlKkDao";

	public TxjlKkDao() {
		super();
		initDao();
	}

	private void initDao() {
		try {
			dao = DaoManager.getDaoManager().getTxjlKkDao();
			if (dao == null) {
				Log.i(TAG, "dao == null");
				DaoManager.getDaoManager().setTxjlKkDao((Dao<TxjlKk, Integer>) getDao(TxjlKk.class));
			}
			dao = DaoManager.getDaoManager().getTxjlKkDao();
			if (!dao.getConnectionSource().isOpen()) {
				Log.i(TAG, "isOpen == false");
				DaoManager.getDaoManager().setTxjlKkDao((Dao<TxjlKk, Integer>) getDao(TxjlKk.class));
			}
			dao = DaoManager.getDaoManager().getTxjlKkDao();
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
	 * @param mtdm
	 * @return
	 * @throws SQLException
	 */
	public int insert(TxjlKk mtdm) throws SQLException {
		DBFlag.isDBBusy();

		dao.createOrUpdate(mtdm);

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
	public int insertList(List<TxjlKk> list) throws SQLException {
		if (list == null) {
			return -1;
		}
		beginTransaction();
		int result = 1;
		for (TxjlKk mtdm : list) {
			insert(mtdm);
		}
		endTransaction();
		return result;

	}

	/**
	 * 
	 * @方法名：getTxjlTkByZjhmAndKkId
	 * @功能说明：根据卡口验证通行方向
	 * @author 娄高伟
	 * @date 2013-10-23 上午10:56:06
	 * @param zjhm
	 * @param txkkid
	 * @return
	 * @throws SQLException
	 */
	public TxjlKk getTxjlKkByZjhmAndKkId(String zjhm, String txkkid) throws SQLException {
		QueryBuilder<TxjlKk, Integer> queryBuilder = dao.queryBuilder();
		Where<TxjlKk, Integer> where = queryBuilder.where();
		where.eq("zjhm", zjhm);
		where.and().eq("txkkid", txkkid);
		queryBuilder.orderBy("txsj", false);
		TxjlKk txjlKk = where.queryForFirst();
		return txjlKk;
	}

	/**
	 * 
	 * @方法名：delete
	 * @功能说明：删除一条数据
	 * @author liums
	 * @date 2013-9-25 上午9:49:02
	 * @param mtdm
	 * @return
	 * @throws SQLException
	 */
	public int delete(TxjlKk mtdm) throws SQLException {
		DBFlag.isDBBusy();
		dao.delete(mtdm);
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
	 * @param mtdm
	 * @return
	 * @throws SQLException
	 */
	public int update(TxjlKk mtdm) throws SQLException {
		DBFlag.isDBBusy();
		dao.update(mtdm);
		DBFlag.setDBOnlyNotBusy();

		return 0;
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
	public List<TxjlKk> findAll() throws SQLException {
		return dao.queryForAll();
	}

	public void updateTxjlKkTxfx(String zjhm, String txkkid, String txfx) throws SQLException {
		// 先查看，如果有则更新，没有则新增
		TxjlKk txjlkk = getTxjlKkByZjhmAndKkId(zjhm, txkkid);
		if (txjlkk != null) {
			DBFlag.isDBBusy();

			UpdateBuilder<TxjlKk, Integer> updateBuilder = dao.updateBuilder();
			Where<TxjlKk, Integer> where = updateBuilder.where();
			where.and(where.eq("zjhm", zjhm), where.eq("txkkid", txkkid));
			updateBuilder.updateColumnValue("txfx", txfx);
			updateBuilder.update();

			DBFlag.setDBOnlyNotBusy();

		} else {
			txjlkk = new TxjlKk();
			txjlkk.setZjhm(zjhm);
			txjlkk.setTxfx(txfx);
			txjlkk.setTxkkid(txkkid);
			txjlkk.setTxsj(new Date());
			insert(txjlkk);
		}

	}

	public void deleteByZjhmAndKkid(String zjhm, String kkID) throws SQLException {
		DBFlag.isDBBusy();

		DeleteBuilder<TxjlKk, Integer> deleteBuilder = dao.deleteBuilder();
		Where<TxjlKk, Integer> where = deleteBuilder.where();
		where.and(where.eq("zjhm", zjhm), where.eq("txkkid", kkID));
		deleteBuilder.delete();

		DBFlag.setDBOnlyNotBusy();

	}
}
