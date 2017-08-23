package com.pingtech.hgqw.module.offline.bwdm.dao;

import java.sql.SQLException;
import java.util.List;

import android.util.Log;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;
import com.pingtech.hgqw.module.offline.DBFlag;
import com.pingtech.hgqw.module.offline.base.dao.BaseHgqwDao;
import com.pingtech.hgqw.module.offline.base.utils.DaoManager;
import com.pingtech.hgqw.module.offline.bwdm.entity.Bwdm;

public class BwdmDao extends BaseHgqwDao<Bwdm> {

	private static final String TAG = "BwdmDao";

	public BwdmDao() {
		super();
		initDao();
	}

	private void initDao() {
		try {
			dao = DaoManager.getDaoManager().getBwdmDao();
			if (dao == null) {
				Log.i(TAG, "dao == null");
				DaoManager.getDaoManager().setBwdmDao((Dao<Bwdm, Integer>) getDao(Bwdm.class));
			}
			dao = DaoManager.getDaoManager().getBwdmDao();
			if (!dao.getConnectionSource().isOpen()) {
				Log.i(TAG, "isOpen == false");
				DaoManager.getDaoManager().setBwdmDao((Dao<Bwdm, Integer>) getDao(Bwdm.class));
			}
			dao = DaoManager.getDaoManager().getBwdmDao();
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
	 * @param bwdm
	 * @return
	 * @throws SQLException
	 */
	public int insert(Bwdm bwdm) throws SQLException {
		DBFlag.isDBBusy();
		
		dao.createOrUpdate(bwdm);
		
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
	public int insertList(List<Bwdm> list) throws SQLException {
		if (list == null) {
			return -1;
		}
		beginTransaction();
		int result = 1;
		for (Bwdm bwdm : list) {
			insert(bwdm);
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
	 * @param bwdm
	 * @return
	 * @throws SQLException
	 */
	public int delete(Bwdm bwdm) throws SQLException {
		return dao.delete(bwdm);
	}
	/**
	 * 
	 * @方法名：deleteAll
	 * @功能说明：删除数据
	 * @author 娄高伟
	 * @date  2013-10-12 下午6:49:38
	 * @return
	 * @throws SQLException
	 */
	public int deleteAll() throws SQLException {
		return dao.deleteBuilder().delete();
	}

	/**
	 * 
	 * @方法名：update
	 * @功能说明：更新制定数据
	 * @author liums
	 * @date 2013-9-25 上午9:49:12
	 * @param bwdm
	 * @return
	 * @throws SQLException
	 */
	public int update(Bwdm bwdm) throws SQLException {
		return dao.update(bwdm);
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
	public List<Bwdm> findAll() throws SQLException {
		return dao.queryForAll();
	}

	/**
	 * 
	 * @方法名：getBwmcByMtdmAndBwdm
	 * @功能说明：根据码头代码和泊位代码获取码头
	 * @author liums
	 * @date  2013-11-29 下午2:00:42
	 * @param tkmtDm
	 * @param tkbwDm
	 * @return
	 * @throws SQLException
	 */
	public Bwdm getBwmcByMtdmAndBwdm(String tkmtDm, String tkbwDm) throws SQLException {
		QueryBuilder<Bwdm, Integer> builder = dao.queryBuilder();
		Where<Bwdm, Integer> where = builder.where();
		where.and(where.eq("mtdm", tkmtDm) , where.eq("bwdm", tkbwDm));
		return builder.queryForFirst();
	}

	/**
	 * 
	 * @方法名：getBwdmByBwid
	 * @功能说明：根据泊位ID获取泊位代码
	 * @author liums
	 * @date  2013-12-6 下午2:31:23
	 * @param id
	 * @return
	 * @throws SQLException 
	 */
	public Bwdm getBwdmByBwid(String id) throws SQLException {
		QueryBuilder<Bwdm, Integer> builder = dao.queryBuilder();
		Where<Bwdm, Integer> where = builder.where();
		where.eq("id", id);
		return builder.queryForFirst();
	}
	

}
