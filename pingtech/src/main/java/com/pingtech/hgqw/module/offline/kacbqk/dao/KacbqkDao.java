package com.pingtech.hgqw.module.offline.kacbqk.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import android.util.Log;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.j256.ormlite.stmt.Where;
import com.pingtech.hgqw.module.offline.DBFlag;
import com.pingtech.hgqw.module.offline.base.dao.BaseHgqwDao;
import com.pingtech.hgqw.module.offline.base.utils.DaoManager;
import com.pingtech.hgqw.module.offline.bwdm.entity.Bwdm;
import com.pingtech.hgqw.module.offline.bwdm.service.BwdmService;
import com.pingtech.hgqw.module.offline.kacbqk.entity.Kacbqk;
import com.pingtech.hgqw.module.offline.kacbqk.service.KacbqkService;
import com.pingtech.hgqw.module.offline.mtdm.entity.Mtdm;
import com.pingtech.hgqw.module.offline.mtdm.service.MtdmService;
import com.pingtech.hgqw.utils.StringUtils;
import com.pingtech.hgqw.utils.xml.XmlUtils;

public class KacbqkDao extends BaseHgqwDao<Kacbqk> {

	private static final String TAG = "KacbqkDao";

	public KacbqkDao() {
		super();
		initDao();

	}

	private void initDao() {
		try {
			dao = DaoManager.getDaoManager().getKacaqkDao();
			if (dao == null) {
				Log.i(TAG, "dao == null");
				DaoManager.getDaoManager().setKacaqkDao((Dao<Kacbqk, Integer>) getDao(Kacbqk.class));
			}
			dao = DaoManager.getDaoManager().getKacaqkDao();
			if (!dao.getConnectionSource().isOpen()) {
				Log.i(TAG, "isOpen == false");
				DaoManager.getDaoManager().setKacaqkDao((Dao<Kacbqk, Integer>) getDao(Kacbqk.class));
			}
			dao = DaoManager.getDaoManager().getKacaqkDao();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @方法名：insert
	 * @功能说明：插入一条数据
	 * @author liums
	 * @date 2013-9-23 下午5:32:43
	 * @param Kacbqk
	 * @return
	 * @throws SQLException
	 */
	public int insert(Kacbqk kacbqk) throws SQLException {
		Log.i(TAG, "insert(Kacbqk kacbqk)");
		DBFlag.isDBBusy();

		dao.createOrUpdate(kacbqk);

		DBFlag.setDBOnlyNotBusy();

		return 0;
	}

	/**
	 * 
	 * @方法名：delete
	 * @功能说明：删除一条数据
	 * @author liums
	 * @date 2013-9-23 下午5:32:58
	 * @param Kacbqk
	 * @return
	 * @throws SQLException
	 */
	public int delete(Kacbqk kacbqk) throws SQLException {
		DBFlag.isDBBusy();
		dao.delete(kacbqk);
		DBFlag.setDBOnlyNotBusy();

		return 0;
	}

	/**
	 * 
	 * @方法名：update
	 * @功能说明：更新指定对象
	 * @author liums
	 * @date 2013-9-23 下午5:33:07
	 * @param Kacbqk
	 * @return
	 * @throws SQLException
	 */
	public int update(Kacbqk kacbqk) throws SQLException {
		return dao.update(kacbqk);
	}

	/**
	 * 
	 * @方法名：select
	 * @功能说明：查询所有数据
	 * @author liums
	 * @date 2013-9-23 下午5:33:25
	 * @return
	 * @throws SQLException
	 */
	public List<Kacbqk> select() throws SQLException {
		return dao.queryForAll();
	}

	/**
	 * 
	 * @方法名：query
	 * @功能说明：查询数据
	 * @author 娄高伟
	 * @date 2013-10-12 下午12:06:06
	 * @param hc
	 * @return
	 * @throws SQLException
	 */
	public Kacbqk query(String hc) throws SQLException {
		QueryBuilder<Kacbqk, Integer> queryBuilder = dao.queryBuilder();
		queryBuilder.where().eq("hc", hc);
		PreparedQuery<Kacbqk> prepare = queryBuilder.prepare();
		Kacbqk ka = dao.queryForFirst(prepare);
		return ka;
	}

	/**
	 * 
	 * @方法名：hasDg
	 * @功能说明：根据航次判断船舶是否有抵港时间
	 * @author liums
	 * @date 2013-10-10 下午3:57:59
	 * @param voyageNumber
	 * @return
	 * @throws SQLException
	 */
	public List<Kacbqk> hasDg(String voyageNumber) throws SQLException {
		QueryBuilder<Kacbqk, Integer> queryBuilder = dao.queryBuilder();
		Where<Kacbqk, Integer> where = queryBuilder.where();
		where.and(where.eq("hc", voyageNumber), where.isNotNull("dgsj"));

		return where.query();
	}

	/**
	 * 
	 * @方法名：hasDg
	 * @功能说明：根据航次判断船舶是否有离港时间
	 * @author liums
	 * @date 2013-10-10 下午3:57:59
	 * @param voyageNumber
	 * @return
	 * @throws SQLException
	 */
	public List<Kacbqk> hasLg(String voyageNumber) throws SQLException {
		QueryBuilder<Kacbqk, Integer> queryBuilder = dao.queryBuilder();
		Where<Kacbqk, Integer> where = queryBuilder.where();
		where.and(where.eq("hc", voyageNumber), where.isNotNull("lgsj"));
		return where.query();
	}

	/**
	 * 
	 * @方法名：setDgsj
	 * @功能说明：更新指定航次的抵港时间
	 * @author liums
	 * @date 2013-10-10 下午4:52:01
	 * @param voyageNumber
	 * @param date
	 * @throws SQLException
	 */
	public void setDgsj(String voyageNumber, Date date) throws SQLException {
		UpdateBuilder<Kacbqk, Integer> updateBuilder = dao.updateBuilder();
		updateBuilder.updateColumnValue("dgsj", date);
		// //口岸船舶状态，0预到港、1在港、2预离港、3离港
		updateBuilder.updateColumnValue("cbkazt", "1");
		updateBuilder.where().eq("hc", voyageNumber);
		updateBuilder.update();
	}

	/**
	 * 
	 * @方法名：setDgsj
	 * @功能说明：更新指定航次的离港时间
	 * @author liums
	 * @date 2013-10-10 下午4:52:01
	 * @param voyageNumber
	 * @param date
	 * @throws SQLException
	 */
	public void setLgsj(String voyageNumber, Date date) throws SQLException {
		UpdateBuilder<Kacbqk, Integer> updateBuilder = dao.updateBuilder();
		updateBuilder.updateColumnValue("lgsj", date);
		// 口岸船舶状态，0预到港、1在港、2预离港、3离港
		updateBuilder.updateColumnValue("cbkazt", "3");
		updateBuilder.where().eq("hc", voyageNumber);
		updateBuilder.update();
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

	public int insertList(List<Kacbqk> kacbqks) throws SQLException {
		beginTransaction();
		int i = 0;
		for (Kacbqk cyxx : kacbqks) {
			insert(cyxx);
		}
		endTransaction();
		return 0;
	}

	/**
	 * 
	 * @方法名：byConditionGetKacbqk
	 * @功能说明：通过查询条件得到口岸船舶情况
	 * @author 赵琳
	 * @date 2013-10-11 上午11:32:17
	 * @param kacbqkid
	 * @param hc
	 * @param cbdh
	 * @return
	 * @throws SQLException
	 */
	public Kacbqk byConditionGetKacbqk(String kacbqkid, String hc, String cbdh) {

		QueryBuilder<Kacbqk, Integer> queryBuilder = dao.queryBuilder();
		Where<Kacbqk, Integer> where = queryBuilder.where();
		try {
			
			where.isNotNull("kacbqkid");
			if (StringUtils.isNotEmpty(hc)) {
				where.and().eq("hc", hc);
			} else if (StringUtils.isNotEmpty(kacbqkid)) {
				where.and().eq("kacbqkid", kacbqkid);
			} else if (StringUtils.isNotEmpty(cbdh)) {
				where.and().eq("cbdh", cbdh);
			}
			DBFlag.isDBBusy();
			Kacbqk kacbqk = null;
			try {
				kacbqk = where.queryForFirst();
			} catch (Exception e) {
				DBFlag.setDBOnlyNotBusy();
				e.printStackTrace();
			}
			DBFlag.setDBOnlyNotBusy();
			return kacbqk;

		} catch (SQLException e) {
			e.printStackTrace();
		}
		DBFlag.setDBOnlyNotBusy();
		return null;
	}

	/**
	 * 
	 * @方法名：findShipsbyPrams
	 * @功能说明：选择船舶根据条件查询船舶
	 * @author 娄高伟
	 * @date 2013-10-21 上午11:09:18
	 * @param mapParms
	 * @return
	 * @throws SQLException
	 */
	public List<Map<String, String>> findShipsbyPrams(Map<String, Object> mapParms) throws SQLException {
		QueryBuilder<Kacbqk, Integer> queryBuilder = dao.queryBuilder();
		Where<Kacbqk, Integer> where = queryBuilder.where();
		where.isNotNull("kacbqkid");

		String cbkazt = (String) mapParms.get("cbkazt");
		if (StringUtils.isEmpty(cbkazt)) {
			where.and().in("cbkazt", new Object[] { "0", "1", "2" });
		} else if ("1".equals(cbkazt)) {
			where.and().in("cbkazt", new Object[] { "1", "2" });
		} else {
			where.and().in("cbkazt", new Object[] { cbkazt });
		}

		if (StringUtils.isNotEmpty(mapParms.get("shipName"))) {
			where.and().like("cbzwm", "%" + mapParms.get("shipName").toString() + "%");
		}
		if (StringUtils.isNotEmpty(mapParms.get("shipQuality"))) {
			where.and().eq("cbxz", mapParms.get("shipQuality").toString());
		}
		if (StringUtils.isNotEmpty(mapParms.get("dock"))) {
			// 根据码头ID获取代码
			Mtdm mtdm = new MtdmService().getMtdmByMtid(mapParms.get("dock").toString());
			if (mtdm != null) {
				where.and().eq("tkmt", mtdm.getMtdm());
			}
		}
		if (StringUtils.isNotEmpty(mapParms.get("berth"))) {
			// 根据泊位ID获取泊位代码
			Bwdm bwdm = new BwdmService().getBwdmByBwid(mapParms.get("berth").toString());
			if (bwdm != null) {
				where.and().eq("tkbw", bwdm.getBwdm());
			}
		}
		List<Kacbqk> list = null;
		try {
			list = where.query();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		List<Map<String, String>> lists = new ArrayList<Map<String, String>>();
		KacbqkService kacbqkService = new KacbqkService();
		for (int i = 0; i < list.size(); i++) {
			Kacbqk kacbqk = list.get(i);
			Map<String, String> map = XmlUtils.buildCursor(kacbqk);
			map.put("cblx", "ward");

			// 处理停靠位置
			// String tkmtDm = kacbqk.getTkmt();
			// String tkbwDm = kacbqk.getTkbw();
			// String tkwzStr = kacbqkService.getTkwzStr(tkmtDm, tkbwDm);
			// map.put("tkwz", tkwzStr);

			// 处理口岸船舶状态
			String kacbztFlag = kacbqk.getCbkazt();
			if (kacbztFlag.equals("0")) {
				map.put("kacbzt", "预到港");
			} else if (kacbztFlag.equals("1")) {
				map.put("kacbzt", "在港");
			} else if (kacbztFlag.equals("2")) {
				map.put("kacbzt", "预离港");
			} else if (kacbztFlag.equals("3")) {
				map.put("kacbzt", "离港");
			} else {
				map.put("kacbzt", "");
			}
			lists.add(map);

		}

		return lists;

	}

	/**
	 * @方法名：getKacbqkByKacbqkId
	 * @功能说明：通过kacbqkId取得对象
	 * @author zhaotf
	 * @date 2013-10-23 下午4:02:15
	 * @param kacbqkId
	 * @return
	 * @throws SQLException
	 */
	public Kacbqk getKacbqkByKacbqkId(String kacbqkId) throws SQLException {
		return dao.queryForEq("kacbqkid", kacbqkId).get(0);
	}

	public void deleteKacbqkByHc(String hc) throws SQLException {
		DeleteBuilder<Kacbqk, Integer> deleteBuilder = dao.deleteBuilder();
		Where<Kacbqk, Integer> where = deleteBuilder.where();
		where.eq("hc", hc);
		deleteBuilder.delete();
	}
}
