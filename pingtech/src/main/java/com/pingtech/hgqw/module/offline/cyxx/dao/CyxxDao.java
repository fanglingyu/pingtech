package com.pingtech.hgqw.module.offline.cyxx.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.util.Log;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;
import com.pingtech.hgqw.module.offline.DBFlag;
import com.pingtech.hgqw.module.offline.base.dao.BaseHgqwDao;
import com.pingtech.hgqw.module.offline.base.utils.DaoManager;
import com.pingtech.hgqw.module.offline.cyxx.entity.TBCyxx;
import com.pingtech.hgqw.utils.DateUtils;
import com.pingtech.hgqw.utils.StringUtils;

public class CyxxDao extends BaseHgqwDao<TBCyxx> {

	private static final String TAG = "CyxxDao";

	public CyxxDao() {
		super();
		initDao();
	}

	private void initDao() {
		try {
			dao = DaoManager.getDaoManager().getCyxxDao();
			if (dao == null) {
				Log.i(TAG, "dao == null");
				DaoManager.getDaoManager().setCyxxDao((Dao<TBCyxx, Integer>) getDao(TBCyxx.class));
			}
			dao = DaoManager.getDaoManager().getCyxxDao();
			if (!dao.getConnectionSource().isOpen()) {
				Log.i(TAG, "isOpen == false");
				DaoManager.getDaoManager().setCyxxDao((Dao<TBCyxx, Integer>) getDao(TBCyxx.class));
			}
			dao = DaoManager.getDaoManager().getCyxxDao();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public int insert(TBCyxx cyxx) throws SQLException {
		DBFlag.isDBBusy();
		dao.createOrUpdate(cyxx);

		DBFlag.setDBOnlyNotBusy();
		return 0;
	}

	public int insertList(List<TBCyxx> list) throws SQLException {
		beginTransaction();
		int i = 0;
		for (TBCyxx cyxx : list) {
			insert(cyxx);
		}
		endTransaction();
		return 0;

	}

	public int delete(TBCyxx cyxx) throws SQLException {
		DBFlag.isDBBusy();
		dao.delete(cyxx);
		DBFlag.setDBOnlyNotBusy();
		return 0;
	}

	public TBCyxx getTBCyxxByZjhm(String zjhm) throws SQLException {
		List<TBCyxx> list = dao.queryBuilder().where().eq("zjhm", zjhm.trim()).and().eq("cbzt", "0").query();
		if (list != null && list.size() > 0) {
			return list.get(0);
		}
		return null;
	}

	public int deleteAll() throws SQLException {
		DBFlag.isDBBusy();
		dao.deleteBuilder().delete();
		DBFlag.setDBOnlyNotBusy();

		return 0;
	}

	public int update(TBCyxx cyxx) throws SQLException {
		return dao.update(cyxx);
	}

	public List<TBCyxx> findAll() throws SQLException {
		return dao.queryForAll();
	}

	public List<Map<String, String>> findAll(Map<String, Object> params) throws SQLException {
		StringBuffer sb = new StringBuffer();
		sb.append("select cy.hyid,cy.xm,cy.xb,cy.gj,cy.zw,cy.zjlx,cy.zjhm,ka.cbzwm,cy.zjlx,cy.csrq,cy.kacbqkid,ka.hc,cy.lcbz from TBCyxx cy ,Kacbqk ka where cy.kacbqkid=ka.kacbqkid");
		if (StringUtils.isNotEmpty(params.get("xm"))) {
			sb.append(" and cy.xm like '%" + params.get("xm").toString().trim() + "%'");
		}
		if (StringUtils.isNotEmpty(params.get("xb"))) {
			sb.append(" and cy.xb = '" + params.get("xb").toString().trim() + "'");
		}
		if (StringUtils.isNotEmpty(params.get("zw"))) {
			sb.append(" and cy.zw = '" + params.get("zw").toString().trim() + "'");
		}
		if (StringUtils.isNotEmpty(params.get("zjlx"))) {
			sb.append(" and cy.zjlx = '" + params.get("zjlx").toString().trim() + "'");
		}
		if (StringUtils.isNotEmpty(params.get("zjhm"))) {
			sb.append(" and cy.zjhm = '" + params.get("zjhm").toString().trim() + "'");
		}

		if (StringUtils.isNotEmpty(params.get("voyageNumber"))) {
			sb.append(" and ka.hc = '" + params.get("voyageNumber").toString().trim() + "'");
		}

		if (StringUtils.isNotEmpty(params.get("kacbqkid"))) {
			sb.append(" and cy.kacbqkid = '" + params.get("kacbqkid").toString().trim() + "'");
			sb.append(" and cy.jcfl = ka.jcfl");
		}
		sb.append(" order by cy.ryxh ");

		DBFlag.isDBBusy();
		GenericRawResults<String[]> rawResults = dao.queryRaw(sb.toString());
		DBFlag.setDBOnlyNotBusy();

		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
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
			map.put("ssdw", str[7]);
			map.put("cbzwm", str[7]);
			map.put("hgzl", str[8]);
			map.put("csrq", DateUtils.gainBirthday(str[9]));
			map.put("kacbqkid", str[10]);
			map.put("hc", str[11]);
			map.put("lcbz", str[12]);
			list.add(map);
		}
		return list;
	}

	public List<Map<String, String>> getCyxxForException(Map<String, Object> params) throws SQLException {
		StringBuffer sb = new StringBuffer();
		sb.append("select cy.hyid,cy.xm,cy.xb,cy.gj,cy.zw,cy.zjlx,cy.zjhm,cy.csrq ,ka.cbzwm ,cy.kacbqkid,ka.hc ,cy.lcbz from TBCyxx cy, Kacbqk ka where cy.kacbqkid=ka.kacbqkid and ka.cbkazt in ('1' ,'2') ");

		if (params.get("xm") != null && !"".equals(params.get("xm"))) {
			sb.append(" and cy.xm like '%" + params.get("xm").toString().trim() + "%'");
		}
		if (params.get("xb") != null && !"".equals(params.get("xb"))) {
			sb.append(" and cy.xb = '" + params.get("xb").toString().trim() + "'");
		}
		if (params.get("zw") != null && !"".equals(params.get("zw"))) {
			sb.append(" and cy.zw = '" + params.get("zw").toString().trim() + "'");
			sb.append(" and cy.zw is not null");
		}
		if (params.get("zjlx") != null && !"".equals(params.get("zjlx"))) {
			sb.append(" and cy.zjlx = '" + params.get("zjlx").toString().trim() + "'");
		}
		if (params.get("zjhm") != null && !"".equals(params.get("zjhm"))) {
			sb.append(" and cy.zjhm = '" + params.get("zjhm").toString().trim() + "'");
		}
		sb.append(" order by cy.ryxh ");

		DBFlag.isDBBusy();
		GenericRawResults<String[]> rawResults = dao.queryRaw(sb.toString());
		DBFlag.setDBOnlyNotBusy();

		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
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
			map.put("csrq", DateUtils.gainBirthday(str[7]));
			map.put("ssdw", str[8]);
			map.put("cbzwm", str[8]);
			map.put("kacbqkid", str[9]);
			map.put("hc", str[10]);
			map.put("lcbz", str[11]);
			list.add(map);
		}
		return list;
	}

	/**
	 * 
	 * @方法名：gainCyxxNum
	 * @功能说明：获得船员数
	 * @author 娄高伟
	 * @date 2013-11-13 上午11:06:31
	 * @param kacbqkid
	 * @return
	 * @throws SQLException
	 */
	public int gainCyxxNum(String kacbqkid) throws SQLException {
		QueryBuilder<TBCyxx, Integer> queryBuilder = dao.queryBuilder();
		Where<TBCyxx, Integer> where = queryBuilder.where();
		if (StringUtils.isNotEmpty(kacbqkid)) {
			where.eq("kacbqkid", kacbqkid);
		} else {
			return 0;
		}
		List<TBCyxx> list = where.query();
		if (list != null) {
			return list.size();
		}
		return 0;
	}

	/**
	 * 
	 * @方法名：gainCyxxCx
	 * @功能说明：根据证件号码HC查询船员
	 * @author 娄高伟
	 * @date 2013-10-18 上午9:01:41
	 * @param cardNumber
	 * @param hc
	 * @param kacbqkid
	 * @return
	 * @throws SQLException
	 */
	public TBCyxx gainCyxxCx(String cardNumber, String hc, String kacbqkid) throws SQLException {
		QueryBuilder<TBCyxx, Integer> queryBuilder = dao.queryBuilder();
		Where<TBCyxx, Integer> where = queryBuilder.where();
		where.eq("zjhm", cardNumber);
		if (StringUtils.isNotEmpty(kacbqkid)) {
			where.and().eq("kacbqkid", kacbqkid);
		}
		
		DBFlag.isDBBusy();
		TBCyxx t = where.queryForFirst();
		DBFlag.setDBOnlyNotBusy();

		return t;
	}

	public TBCyxx findById(String ryid) throws SQLException {
		QueryBuilder<TBCyxx, Integer> queryBuilder = dao.queryBuilder();
		Where<TBCyxx, Integer> where = queryBuilder.where();
		where.eq("hyid", ryid);
		return where.queryForFirst();
	}

	public void deleteCyxxByCbid(String kacbqkid) throws SQLException {
		DeleteBuilder<TBCyxx, Integer> deleteBuilder = dao.deleteBuilder();
		Where<TBCyxx, Integer> where = deleteBuilder.where();
		where.eq("kacbqkid", kacbqkid);
		deleteBuilder.delete();
	}
}
