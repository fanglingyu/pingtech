package com.pingtech.hgqw.module.offline.mtdm.dao;

import java.sql.SQLException;
import java.util.List;

import android.util.Log;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;
import com.pingtech.hgqw.module.offline.DBFlag;
import com.pingtech.hgqw.module.offline.base.dao.BaseHgqwDao;
import com.pingtech.hgqw.module.offline.base.utils.DaoManager;
import com.pingtech.hgqw.module.offline.mtdm.entity.Mtdm;

public class MtdmDao extends BaseHgqwDao<Mtdm> {

	private static final String TAG = "MtdmDao";

	public MtdmDao() {
		super();
		initDao();
	}

	private void initDao() {
		try {
			dao = DaoManager.getDaoManager().getMtdmDao();
			if (dao == null) {
				Log.i(TAG, "dao == null");
				DaoManager.getDaoManager().setMtdmDao((Dao<Mtdm, Integer>) getDao(Mtdm.class));
			}
			dao = DaoManager.getDaoManager().getMtdmDao();
			if (!dao.getConnectionSource().isOpen()) {
				Log.i(TAG, "isOpen == false");
				DaoManager.getDaoManager().setMtdmDao((Dao<Mtdm, Integer>) getDao(Mtdm.class));
			}
			dao = DaoManager.getDaoManager().getMtdmDao();
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
	public int insert(Mtdm mtdm) throws SQLException {
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
	public int insertList(List<Mtdm> list) throws SQLException {
		if (list == null) {
			return -1;
		}
		beginTransaction();
		int result = 1;
		for (Mtdm mtdm : list) {
			insert(mtdm);
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
	 * @param mtdm
	 * @return
	 * @throws SQLException
	 */
	public int delete(Mtdm mtdm) throws SQLException {
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
	public int update(Mtdm mtdm) throws SQLException {
		return dao.update(mtdm);
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
	public List<Mtdm> findAll() throws SQLException {
		return dao.queryForAll();
	}

	/**
	 * 
	 * @方法名：getMtmcByMtdm
	 * @功能说明：根据码头代码获取码头名称
	 * @author liums
	 * @date 2013-11-29 下午1:41:04
	 * @param tkmtDm
	 * @return
	 * @throws SQLException
	 */
	public Mtdm getMtmcByMtdm(String tkmtDm) throws SQLException {
		QueryBuilder<Mtdm, Integer> builder = dao.queryBuilder();
		Where<Mtdm, Integer> where = builder.where();
		where.eq("mtdm", tkmtDm);
		return builder.queryForFirst();
	}

	/**
	 * 
	 * @方法名：getMtdmByMtid
	 * @功能说明：根据码头ID获取码头
	 * @author liums
	 * @date 2013-12-6 下午2:27:00
	 * @param id
	 * @return
	 * @throws SQLException
	 */
	public Mtdm getMtdmByMtid(String id) throws SQLException {
		QueryBuilder<Mtdm, Integer> builder = dao.queryBuilder();
		Where<Mtdm, Integer> where = builder.where();
		where.eq("id", id);
		return builder.queryForFirst();
	}

}
