package com.pingtech.hgqw.module.offline.txjl.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.j256.ormlite.stmt.Where;
import com.pingtech.hgqw.module.offline.DBFlag;
import com.pingtech.hgqw.module.offline.base.dao.BaseHgqwDao;
import com.pingtech.hgqw.module.offline.base.utils.DaoManager;
import com.pingtech.hgqw.module.offline.txjl.entity.TxjlTk;
import com.pingtech.hgqw.utils.Log;
import com.pingtech.hgqw.utils.StringUtils;

public class TxjlTkDao extends BaseHgqwDao<TxjlTk> {

	private static final String TAG = "TxjlTkDao";

	public TxjlTkDao() {
		super();
		initDao();
	}

	private void initDao() {
		try {
			dao = DaoManager.getDaoManager().getTxjlTkDao();
			if (dao == null) {
				Log.i(TAG, "dao == null");
				DaoManager.getDaoManager().setTxjlTkDao((Dao<TxjlTk, Integer>) getDao(TxjlTk.class));
			}
			dao = DaoManager.getDaoManager().getTxjlTkDao();
			if (!dao.getConnectionSource().isOpen()) {
				Log.i(TAG, "isOpen == false");
				DaoManager.getDaoManager().setTxjlTkDao((Dao<TxjlTk, Integer>) getDao(TxjlTk.class));
			}
			dao = DaoManager.getDaoManager().getTxjlTkDao();
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
	 * @param txjlTk
	 * @return
	 * @throws SQLException
	 */
	public int insert(TxjlTk txjlTk) throws SQLException {
		DBFlag.isDBBusy();
		dao.createOrUpdate(txjlTk);
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
	public int insertList(List<TxjlTk> list) throws SQLException {
		if (list == null) {
			return -1;
		}
		beginTransaction();
		int result = 1;
		for (TxjlTk txjlTk : list) {
			insert(txjlTk);
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
	 * @param txjlTk
	 * @return
	 * @throws SQLException
	 */
	public int delete(TxjlTk txjlTk) throws SQLException {
		DBFlag.isDBBusy();

		dao.delete(txjlTk);
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
	 * @param txjlTk
	 * @return
	 * @throws SQLException
	 */
	public int update(TxjlTk txjlTk) throws SQLException {
		DBFlag.isDBBusy();
		dao.update(txjlTk);
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
	public List<TxjlTk> findAll() throws SQLException {
		return dao.queryForAll();
	}

	public TxjlTk getTxjlTkByZjhmAndHc(String zjhm, String hc) throws SQLException {
		QueryBuilder<TxjlTk, Integer> queryBuilder = dao.queryBuilder();
		Where<TxjlTk, Integer> where = queryBuilder.where();
		where.and(where.eq("zjhm", zjhm), where.eq("hc", hc));
		TxjlTk txjlTk = where.queryForFirst();
		return txjlTk;
	}

	public TxjlTk getTxjlTkByHc(String hc) throws SQLException {
		QueryBuilder<TxjlTk, Integer> queryBuilder = dao.queryBuilder();
		Where<TxjlTk, Integer> where = queryBuilder.where();
		where.eq("hc", hc);
		TxjlTk txjlTk = where.queryForFirst();
		return txjlTk;
	}

	/**
	 * 
	 * @方法名：getTxjltkNowList
	 * @功能说明：获取某航次下所有在船下的船员或者人员
	 * @author jiajw
	 * @date 2014-2-14 上午10:32:43
	 * @param hc
	 * @param dldllx
	 * @param txfx
	 * @return
	 */
	// public List<Object[]> getTxjltkNowList(String hc, String dldllx, String
	// txfx){
	//
	// StringBuffer sql = new StringBuffer();
	// sql.append(SELECT tt.tktxjlid,tt.zjhm FROM TB_HGQW_DLDL_TKTXJL tt ,(
	// SELECT tktxjlid FROM V_HGQW_DLDL_TXJLTK t, (SELECT max(txsj) AS sj,
	// hc,zjhm FROM V_HGQW_DLDL_TXJLTK GROUP BY hc,zjhm ) a WHERE t.TXSJ=a.sj )
	// aa WHERE tt.tktxjlid =aa.tktxjlid ")
	//
	// StringBuffer sql = new
	// StringBuffer("SELECT tt.tktxjlid,tt.zjhm FROM TB_HGQW_DLDL_TKTXJL tt ,( SELECT tktxjlid FROM V_HGQW_DLDL_TXJLTK  t, (SELECT max(txsj) AS sj, hc,zjhm FROM V_HGQW_DLDL_TXJLTK  GROUP BY hc,zjhm  ) a WHERE t.TXSJ=a.sj )  aa WHERE tt.tktxjlid =aa.tktxjlid ");
	// if(hc != null && !"".equals(hc)){ // 如果航次不为空，把航次添加到条件中
	// sql.append(" and tt.hc='"+hc);
	// sql.append("'");
	// }
	// if(dldllx != null && !"".equals(dldllx)){ // 登轮登陆类型如果不为空，把航次添加到条件中
	// sql.append(" and tt.dldllx='"+dldllx);
	// sql.append("'");
	// }
	// if(hc != null && !"".equals(hc)){ // 如果通行方向不为空，把航次添加到条件中
	// sql.append(" and tt.txfx='"+txfx);
	// sql.append("'");
	// }
	// Query query=this.getSession().createSQLQuery(sql.toString());
	// return query.list();
	// }
	//
	// }

	/***
	 * 
	 * @方法名：findAll
	 * @功能说明：关联查询
	 * @author jiajw
	 * @date 2014-2-14 上午9:21:38
	 * @param params
	 * @return
	 * @throws SQLException
	 */

	public List<Map<String, String>> findCyxxByHc(Map<String, Object> params) throws SQLException {

		StringBuffer sb = new StringBuffer();
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();

		if (StringUtils.isNotEmpty(params.get("hc"))) {
			String hc = params.get("hc").toString().trim();
			sb.append("select cy.hyid,cy.xm,cy.xb,cy.gj,cy.zw,cy.zjlx,cy.zjhm,cy.hc,(select tx.txfx from TxjlTkNow tx where tx.zjhm = cy.zjhm) as txfx from TBCyxx cy where cy.kacbqkid = (select kacbqkid from Kacbqk where hc = '"
					+ hc + "')");
			sb.append(" order by cy.ryxh ");
		} else {
			return list;
		}

		GenericRawResults<String[]> rawResults = dao.queryRaw(sb.toString());

		for (String[] str : rawResults) {
			Map<String, String> map = null;
			map = new HashMap<String, String>();
			map.put("hyid", str[0]);
			map.put("xm", str[1]);
			map.put("xb", str[2]);
			map.put("gj", str[3]);
			map.put("zw", str[4]);
			map.put("zjlx", str[5]);
			map.put("zjhm", str[6]);
			map.put("hc", str[7]);
			map.put("txfx", str[8]);
			list.add(map);
		}
		return list;
	}

	/**
	 * 
	 * @方法名：updateTxjltkTxfx
	 * @功能说明：通行验证通过，修改本地通行方向
	 * @author liums
	 * @date 2013-10-22 下午8:02:12
	 * @param zjhm
	 * @param hc
	 * @param txfx
	 * @throws SQLException
	 */
	public void updateTxjlTkTxfx(String zjhm, String hc, String txfx) throws SQLException {
		// 先查看，如果有则更新，没有则新增
		TxjlTk txjltk = null;
		txjltk = getTxjlTkByZjhmAndHc(zjhm, hc);
		if (txjltk != null) {
			DBFlag.isDBBusy();

			UpdateBuilder<TxjlTk, Integer> updateBuilder = dao.updateBuilder();
			Where<TxjlTk, Integer> where = updateBuilder.where();
			where.and(where.eq("zjhm", zjhm), where.eq("hc", hc));
			updateBuilder.updateColumnValue("txfx", txfx);
			updateBuilder.update();

			DBFlag.setDBOnlyNotBusy();

		} else {
			txjltk = new TxjlTk();
			txjltk.setHc(hc);
			txjltk.setZjhm(zjhm);
			txjltk.setTxfx(txfx);
			insert(txjltk);
		}

	}

	public void deleteByZjhmAndHc(String zjhm, String hc) throws SQLException {
		DBFlag.isDBBusy();

		DeleteBuilder<TxjlTk, Integer> deleteBuilder = dao.deleteBuilder();
		Where<TxjlTk, Integer> where = deleteBuilder.where();
		where.and(where.eq("zjhm", zjhm), where.eq("hc", hc));
		deleteBuilder.delete();

		DBFlag.setDBOnlyNotBusy();

	}

}
