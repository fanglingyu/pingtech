package com.pingtech.hgqw.module.offline.pzxx.dao;

import java.sql.SQLException;
import java.util.ArrayList;

import android.util.Log;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;
import com.pingtech.hgqw.module.offline.DBFlag;
import com.pingtech.hgqw.module.offline.base.dao.BaseHgqwDao;
import com.pingtech.hgqw.module.offline.base.utils.DaoManager;
import com.pingtech.hgqw.module.offline.pzxx.entity.Pzxx;

public class PzxxDao extends BaseHgqwDao<Pzxx> {
	private static final String TAG = "PzxxDao";

	public PzxxDao() {
		super();
		initDao();
	}

	private void initDao() {
		try {
			dao = DaoManager.getDaoManager().getPzxxDao();
			if (dao == null) {
				Log.i(TAG, "dao == null");
				DaoManager.getDaoManager().setPzxxDao((Dao<Pzxx, Integer>) getDao(Pzxx.class));
			}
			dao = DaoManager.getDaoManager().getPzxxDao();
			if (!dao.getConnectionSource().isOpen()) {
				Log.i(TAG, "isOpen == false");
				DaoManager.getDaoManager().setPzxxDao((Dao<Pzxx, Integer>) getDao(Pzxx.class));
			}
			dao = DaoManager.getDaoManager().getPzxxDao();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void insert(Pzxx pzxx) throws SQLException {

		DBFlag.isDBBusy();

		dao.createOrUpdate(pzxx);

		DBFlag.setDBOnlyNotBusy();

	}

	public int insertList(ArrayList<Pzxx> pzxxs) throws SQLException {
		Log.i(TAG, "insertList");
		beginTransaction();
		int i = 0;
		for (Pzxx pzxx : pzxxs) {
			insert(pzxx);
			// Log.i("insert Pzxx", i++ + "");
		}
		endTransaction();
		return i;
	}

	public Pzxx getPzxxByZjhm(String zjhm) throws SQLException {
		QueryBuilder<Pzxx, Integer> queryBuilder = dao.queryBuilder();
		queryBuilder.where().eq("zjhm", zjhm);
		return queryBuilder.queryForFirst();
	}

	public int deleteAll() throws SQLException {
		DBFlag.isDBBusy();

		DeleteBuilder<Pzxx, Integer> deleteBuilder = dao.deleteBuilder();
		deleteBuilder.delete();

		DBFlag.setDBOnlyNotBusy();

		return 0;
	}

}
