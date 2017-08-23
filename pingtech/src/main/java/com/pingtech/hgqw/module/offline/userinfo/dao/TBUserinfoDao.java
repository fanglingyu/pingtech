package com.pingtech.hgqw.module.offline.userinfo.dao;

import java.sql.SQLException;
import java.util.List;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;
import com.pingtech.hgqw.module.offline.base.dao.BaseHgqwDao;
import com.pingtech.hgqw.module.offline.base.utils.DaoManager;
import com.pingtech.hgqw.module.offline.userinfo.entity.TBUserinfo;
import com.pingtech.hgqw.utils.Log;

/**
 * 
 *
 * 类描述：一线用户DAO
 *
 * <p> Title: 江海港边检勤务综合管理系统-TBUserinfoDao.java </p>
 * <p> Copyright: Copyright (c) 2012 </p>
 * <p> Company: 品恩科技 </p>
 * @author  赵琳 
 * @version 1.0
 * @date  2013-10-15 下午3:34:15
 */
public class TBUserinfoDao extends BaseHgqwDao<TBUserinfo> {

	private static final String TAG = "TBUserinfoDao";
	public TBUserinfoDao() {
		super();
		initDao();
	}

	private void initDao() {
		try {
			dao = DaoManager.getDaoManager().getUserinfoDao();
			if (dao == null) {
				Log.i(TAG, "dao == null");
				DaoManager.getDaoManager().setUserinfoDao((Dao<TBUserinfo, Integer>) getDao(TBUserinfo.class));
			}
			dao = DaoManager.getDaoManager().getUserinfoDao();
			if (!dao.getConnectionSource().isOpen()) {
				Log.i(TAG, "isOpen == false");
				DaoManager.getDaoManager().setUserinfoDao((Dao<TBUserinfo, Integer>) getDao(TBUserinfo.class));
			}
			dao = DaoManager.getDaoManager().getUserinfoDao();
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
	 * @param fwxcb
	 * @return
	 * @throws SQLException
	 */
	public int insert(TBUserinfo tbuser) throws SQLException {
		dao.createOrUpdate(tbuser);
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
	public int insertList(List<TBUserinfo> list) throws SQLException {
		if (list == null) {
			return -1;
		}
		beginTransaction();
		int result = 1;
		for (TBUserinfo tbuser : list) {		
			insert(tbuser);
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
	 * @param Fwxcb
	 * @return
	 * @throws SQLException
	 */
	public int delete(TBUserinfo tbuser) throws SQLException {
		return dao.delete(tbuser);
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
	 * @param Fwxcb
	 * @return
	 * @throws SQLException
	 */
	public int update(TBUserinfo tbuser) throws SQLException {
		return dao.update(tbuser);
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
	public List<TBUserinfo> findAll() throws SQLException {
		return dao.queryForAll();
	}
	/**
	 * 
	 * @方法名：gainUserByZjhm
	 * @功能说明：根据士兵卡号查询人员
	 * @author 娄高伟
	 * @date  2013-10-17 下午7:48:44
	 * @param zjhm
	 * @return
	 * @throws SQLException
	 */
	public TBUserinfo gainUserByZjhm(String zjhm) throws SQLException {
		QueryBuilder<TBUserinfo, Integer> queryBuilder = dao.queryBuilder();
		Where<TBUserinfo, Integer> where = queryBuilder.where();
		where.eq("sbk", zjhm);
		TBUserinfo user = where.queryForFirst();
		return user;
	}

}
