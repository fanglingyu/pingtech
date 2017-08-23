package com.pingtech.hgqw.module.offline.hgzjxx.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.os.SystemClock;
import android.util.Log;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.j256.ormlite.stmt.Where;
import com.pingtech.R;
import com.pingtech.hgqw.base.BaseApplication;
import com.pingtech.hgqw.module.offline.DBFlag;
import com.pingtech.hgqw.module.offline.base.dao.BaseHgqwDao;
import com.pingtech.hgqw.module.offline.base.utils.DaoManager;
import com.pingtech.hgqw.module.offline.hgzjxx.entity.Hgzjxx;
import com.pingtech.hgqw.module.offline.zjyf.util.YfZjxxConstant;
import com.pingtech.hgqw.utils.DateUtils;
import com.pingtech.hgqw.utils.StringUtils;

public class HgzjxxDao extends BaseHgqwDao<Hgzjxx> {

	private static final String TAG = "HgzjxxDao";

	private List<Map<String, String>> maps;

	public HgzjxxDao() {
		super();
		initDao();
	}

	private void initDao() {
		try {
			// DaoManager.getDaoManager().setHgzjxxDao((Dao<Hgzjxx, Integer>)
			// getDao(Hgzjxx.class));
			dao = DaoManager.getDaoManager().getHgzjxxDao();
			if (dao == null) {
				Log.i(TAG, "dao == null");
				DaoManager.getDaoManager().setHgzjxxDao((Dao<Hgzjxx, Integer>) getDao(Hgzjxx.class));
			}
			dao = DaoManager.getDaoManager().getHgzjxxDao();
			if (!dao.getConnectionSource().isOpen()) {
				Log.i(TAG, "isOpen == false");
				DaoManager.getDaoManager().setHgzjxxDao((Dao<Hgzjxx, Integer>) getDao(Hgzjxx.class));
			}
			dao = DaoManager.getDaoManager().getHgzjxxDao();
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
	 * @param hgzjxx
	 * @return
	 * @throws SQLException
	 */
	public int insert(Hgzjxx hgzjxx) throws SQLException {
		DBFlag.isDBBusy();
		dao.createOrUpdate(hgzjxx);

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
	public int insertList(List<Hgzjxx> list) throws SQLException {
		if (list == null) {
			return -1;
		}
		beginTransaction();
		int result = 1;
		for (int i = 0; i < list.size(); i++) {
			Hgzjxx hgzjxx = list.get(i);
			if (hgzjxx.getCbzjffxxxid() != null) {
				insert(hgzjxx);
				// Log.i(TAG, i + "");
			}
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
	 * @param hgzjxx
	 * @return
	 * @throws SQLException
	 */
	public int delete(Hgzjxx hgzjxx) throws SQLException {
		DBFlag.isDBBusy();
		dao.delete(hgzjxx);
		DBFlag.setDBOnlyNotBusy();

		return 0;
	}

	/**
	 * @方法名：delete
	 * @功能说明：删除多条数据
	 * @author zhaotf
	 * @date 2013-10-14 下午7:22:11
	 * @param hgzjxxs
	 * @return
	 * @throws SQLException
	 */
	public int delete(List<Hgzjxx> hgzjxxs) throws SQLException {
		if (hgzjxxs == null || hgzjxxs.size() < 1) {
			return -1;
		}
		beginTransaction();
		for (Hgzjxx h : hgzjxxs) {
			dao.delete(h);
		}
		endTransaction();
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
	 * @param hgzjxx
	 * @return
	 * @throws SQLException
	 */
	public int update(Hgzjxx hgzjxx) throws SQLException {
		return dao.update(hgzjxx);
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
	public List<Hgzjxx> findAll() throws SQLException {
		return dao.queryForAll();
	}

	/**
	 * 
	 * @方法名：getYfZjxx
	 * @功能说明：验放信息查询
	 * @author 娄高伟
	 * @date 2013-10-10 下午4:24:02
	 * @param ickey
	 * @param zjhm
	 * @param mrickey
	 * @param sfsk
	 * @param zjType
	 * @param icffzt
	 * @param cphm
	 *            TODO
	 * @param isClyz
	 *            TODO
	 * @return
	 * @throws SQLException
	 */
	public List<Hgzjxx> getYfZjxx(String ickey, String zjhm, String mrickey, String sfsk, String zjType, String icffzt, String cphm, boolean isClyz)
			throws SQLException {
		QueryBuilder<Hgzjxx, Integer> queryBuilder = dao.queryBuilder();
		Where<Hgzjxx, Integer> where = queryBuilder.where();
		if (YfZjxxConstant.ZJCX_SFSK_SK.equals(sfsk) && ickey != null && !"".equals(ickey)) {
			if (mrickey != null && !"".equals(mrickey)) {
				where.or(where.and(where.eq("ickey", ickey), where.or(where.isNull("mrickey"), where.eq("mrickey", ""))),
						where.and(where.eq("ickey", ickey), where.eq("mrickey", mrickey)));
			} else {
				where.eq("ickey", ickey);
			}

		} else if ((YfZjxxConstant.ZJCX_SFSK_SDSR.equals(sfsk) && !isClyz && zjhm != null && !"".equals(zjhm))) {
			where.eq("zjhm", zjhm.trim());
		} else if ((YfZjxxConstant.ZJCX_SFSK_SDSR.equals(sfsk) && isClyz && cphm != null && !"".equals(cphm))) {
			if (StringUtils.isNotEmpty(zjhm)) {
				// where.or(where.and(where.eq("cphm", zjhm.trim()),
				// where.eq("jsyhm", jszbh_sfzh.trim())), where.eq("zjhm",
				// zjhm.trim()));
				where.and(where.eq("cphm", cphm.trim()), where.eq("jsyhm", zjhm.trim()));
			} else {
				where.eq("cphm", cphm.trim());
			}
		} else if (YfZjxxConstant.ZJCX_SFSK_ZXING.equals(sfsk) && zjhm != null && !"".equals(zjhm)) {
			where.eq("zjbh", zjhm.trim());
		} else {
			return null;
		}

		if (zjType != null && !"".equals(zjType)) {
			where.and().eq("zjlb", zjType);
		}
		if (icffzt != null && !"".equals(icffzt)) {
			where.and().eq("icffzt", icffzt);
		}
		DBFlag.isDBBusy();
		List<Hgzjxx> hgzjxxs = null;
		try {
			hgzjxxs = where.query();
		} catch (Exception e) {
			try {
				SystemClock.sleep(100);
				hgzjxxs = where.query();
				rebuildList(hgzjxxs);
				return hgzjxxs;
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			DBFlag.setDBOnlyNotBusy();
			e.printStackTrace();
			return null;
		}
		DBFlag.setDBOnlyNotBusy();

		rebuildList(hgzjxxs);
		return hgzjxxs;
	}

	public List<Hgzjxx> getYfZjxx(String ickey, String zjhm, String mrickey, String sfsk, boolean isSearchDk, String icffzt, String cphm,
			boolean isClyz) throws SQLException {
		QueryBuilder<Hgzjxx, Integer> queryBuilder = dao.queryBuilder();
		Where<Hgzjxx, Integer> where = queryBuilder.where();
		if (YfZjxxConstant.ZJCX_SFSK_SK.equals(sfsk) && ickey != null && !"".equals(ickey)) {
			if (mrickey != null && !"".equals(mrickey)) {
				where.or(where.and(where.eq("ickey", ickey), where.or(where.isNull("mrickey"), where.eq("mrickey", ""))),
						where.and(where.eq("ickey", ickey), where.eq("mrickey", mrickey)));
			} else {
				where.eq("ickey", ickey);
			}

		} else if ((YfZjxxConstant.ZJCX_SFSK_SDSR.equals(sfsk) && !isClyz && zjhm != null && !"".equals(zjhm))) {
			where.eq("zjhm", zjhm.trim());
		} else if ((YfZjxxConstant.ZJCX_SFSK_SDSR.equals(sfsk) && isClyz && cphm != null && !"".equals(cphm))) {
			if (StringUtils.isNotEmpty(zjhm)) {
				// where.or(where.and(where.eq("cphm", zjhm.trim()),
				// where.eq("jsyhm", jszbh_sfzh.trim())), where.eq("zjhm",
				// zjhm.trim()));
				where.and(where.eq("cphm", cphm.trim()), where.eq("jsyhm", zjhm.trim()));
			} else {
				where.eq("cphm", cphm.trim());
			}
		} else if (YfZjxxConstant.ZJCX_SFSK_ZXING.equals(sfsk) && zjhm != null && !"".equals(zjhm)) {
			where.eq("zjbh", zjhm.trim());
		} else {
			return null;
		}

		if (icffzt != null && !"".equals(icffzt)) {
			where.and().eq("icffzt", icffzt);
		}
		where.and().ne("zjlb", YfZjxxConstant.ZJLX_DK);// 不查搭靠证

		DBFlag.isDBBusy();
		List<Hgzjxx> hgzjxxs = null;
		try {
			hgzjxxs = where.query();
		} catch (Exception e) {
			DBFlag.setDBOnlyNotBusy();
			e.printStackTrace();
			return null;
		}
		DBFlag.setDBOnlyNotBusy();

		rebuildList(hgzjxxs);
		return hgzjxxs;
	}

	private void rebuildList(List<Hgzjxx> hgzjxxs) {
		Comparator<Hgzjxx> comparator = new Comparator<Hgzjxx>() {

			@Override
			public int compare(Hgzjxx lhs, Hgzjxx rhs) {
				if (lhs.getYxqq().before(rhs.getYxqq())) {
					return 1;
				} else {
					return -1;
				}

			}
		};
		Collections.sort(hgzjxxs, comparator);
	}

	/**
	 * 
	 * @方法名：getZjxx
	 * @功能说明：人员查询证件查询
	 * @author 娄高伟
	 * @date 2013-10-12 下午3:20:09
	 * @param params
	 * @return
	 * @throws SQLException
	 */
	public List<Map<String, String>> getZjxx(Map<String, Object> params) throws SQLException {
		QueryBuilder<Hgzjxx, Integer> queryBuilder = dao.queryBuilder();
		Where<Hgzjxx, Integer> where = queryBuilder.where();
		where.ne("icffzt", "2");
		if (params.get("xm") != null && !"".equals(params.get("xm"))) {
			where.and().like("xm", "%" + params.get("xm").toString().trim() + "%");
		}
		if (params.get("xb") != null && !"".equals(params.get("xb"))) {
			where.and().eq("xbdm", params.get("xb").toString().trim());
		}
		if (params.get("zw") != null && !"".equals(params.get("zw"))) {
			where.and().eq("zwdm", params.get("zw").toString().trim());
		}
		if (params.get("zjlx") != null && !"".equals(params.get("zjlx"))) {
			where.and().eq("zjlbdm", params.get("zjlx").toString().trim());
		}
		if (params.get("zjzl") != null && !"".equals(params.get("zjzl"))) {
			where.and().eq("zjlbdm", params.get("zjzl").toString().trim());
		}
		if (params.get("zjhm") != null && !"".equals(params.get("zjhm"))) {
			where.and().eq("zjhm", params.get("zjhm").toString().trim());
		}
		if (params.get("cywz") != null && !"".equals(params.get("cywz"))) {
			where.and().eq("cywz", params.get("cywz").toString().trim());
		}
		where.and().ne("zjlb", YfZjxxConstant.ZJLX_DK);// 不查搭靠证
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();

		DBFlag.isDBBusy();
		List<Hgzjxx> lists = null;
		try {
			lists = where.query();
		} catch (Exception e) {
			DBFlag.setDBOnlyNotBusy();
			e.printStackTrace();
			return null;
		}
		DBFlag.setDBOnlyNotBusy();

		rebuildList(lists);
		for (int i = 0; i < lists.size(); i++) {
			Hgzjxx hgzjxx = lists.get(i);
			if (hgzjxx != null) {
				Map<String, String> map = new HashMap<String, String>();
				map.put("zw", hgzjxx.getZwdm());
				map.put("xm", hgzjxx.getXm());
				map.put("xb", hgzjxx.getXbdm());
				map.put("gj", hgzjxx.getGjdqdm());
				map.put("zjhm", hgzjxx.getZjhm());
				map.put("ssdw", hgzjxx.getSsdw());
				map.put("csrq", DateUtils.gainBirthday(hgzjxx.getCsrq()));
				map.put("hyid", hgzjxx.getCbzjffxxxid());
				map.put("zjlx", hgzjxx.getZjlbdm());
				map.put("hgzl", hgzjxx.getZjlb());
				if ("1".equals(hgzjxx.getYcyxbz() == null ? "" : hgzjxx.getYcyxbz())) {
					// 本航次有效
					map.put("yxq", BaseApplication.instent.getString(R.string.bhcyx));
				} else {
					map.put("yxq",
							new StringBuffer().append(DateUtils.DateToString(hgzjxx.getYxqq()))
									.append(BaseApplication.instent.getString(R.string.zhi)).append(DateUtils.DateToString(hgzjxx.getYxqz()))
									.toString());
				}
				list.add(map);
			}
		}
		return list;

	}

	/**
	 * 
	 * @方法名：gainXJHgzjxx
	 * @功能说明：获得证件信息
	 * @author 娄高伟
	 * @date 2013-10-17 下午8:16:20
	 * @param cardNumber
	 * @param sfsk
	 * @param hc
	 * @return
	 * @throws SQLException
	 */
	public Hgzjxx gainXJHgzjxx(String cardNumber, String sfsk, String hc) throws SQLException {
		QueryBuilder<Hgzjxx, Integer> queryBuilder = dao.queryBuilder();
		Where<Hgzjxx, Integer> where = queryBuilder.where();
		where.eq("icffzt", "1");
		if ("0".equals(sfsk)) {
			where.and().eq("ickey", cardNumber);
		} else if (YfZjxxConstant.ZJCX_SFSK_ZXING.equals(sfsk) && cardNumber != null && !"".equals(cardNumber)) {
			where.and().eq("zjbh", cardNumber);// 证件编号
		} else {
			where.and().eq("zjhm", cardNumber);
		}
		if (StringUtils.isNotEmpty(hc)) {
			where.and().eq("hc", hc);
		}

		DBFlag.isDBBusy();
		Hgzjxx hgzjxx = null;
		try {
			hgzjxx = where.queryForFirst();
		} catch (Exception e) {
			DBFlag.setDBOnlyNotBusy();
			e.printStackTrace();
			return null;
		}
		DBFlag.setDBOnlyNotBusy();

		return hgzjxx;

	}

	/**
	 * 
	 * @方法名：findById
	 * @功能说明：通过ID查找对象
	 * @author liums
	 * @date 2013-10-23 下午5:17:53
	 * @param cbzjffxxxid
	 * @return
	 * @throws SQLException
	 */
	public Hgzjxx findById(String cbzjffxxxid) throws SQLException {
		QueryBuilder<Hgzjxx, Integer> queryBuilder = dao.queryBuilder();
		Where<Hgzjxx, Integer> where = queryBuilder.where();
		where.eq("cbzjffxxxid", cbzjffxxxid);

		DBFlag.isDBBusy();
		Hgzjxx h = where.queryForFirst();
		DBFlag.setDBOnlyNotBusy();

		return h;
	}

	public long countOf() throws SQLException {
		return dao.countOf();
	}

	public void deleteByHc(String hc) throws SQLException {
		DeleteBuilder<Hgzjxx, Integer> deleteBuilder = dao.deleteBuilder();
		Where<Hgzjxx, Integer> where = deleteBuilder.where();
		where.eq("hc", hc);

		DBFlag.isDBBusy();
		deleteBuilder.delete();
		DBFlag.setDBOnlyNotBusy();

	}

	public void deleteHgzjxxWhichHasHc() throws SQLException {
		DeleteBuilder<Hgzjxx, Integer> deleteBuilder = dao.deleteBuilder();
		Where<Hgzjxx, Integer> where = deleteBuilder.where();
		where.isNotNull("hc");

		DBFlag.isDBBusy();
		deleteBuilder.delete();
		DBFlag.setDBOnlyNotBusy();
	}

	public List<Hgzjxx> findByCountAndOffset(long startRow, long maxRows) throws SQLException {
		QueryBuilder<Hgzjxx, Integer> queryBuilder = dao.queryBuilder();
		Where<Hgzjxx, Integer> where = queryBuilder.where();
		where.isNotNull("zjhm");
		queryBuilder.offset(startRow);
		queryBuilder.limit(maxRows);
		queryBuilder.groupBy("zjhm");

		DBFlag.isDBBusy();
		List<Hgzjxx> datas = queryBuilder.query();
		DBFlag.setDBOnlyNotBusy();

		if (datas != null && !datas.isEmpty()) {
			Log.i(TAG, Integer.toString(datas.size()));
		}
		return datas;
	}

	public void updateList(List<Hgzjxx> hgzjxxs) throws SQLException {
		if (hgzjxxs == null) {
			return;
		}
		beginTransaction();
		int result = 1;
		for (int i = 0; i < hgzjxxs.size(); i++) {
			Hgzjxx hgzjxx = hgzjxxs.get(i);
			if (hgzjxx.getCbzjffxxxid() != null) {
				updateByZjhm(hgzjxx);
				Log.i(TAG, i + "");
			}
		}
		endTransaction();
	}

	public void updateByZjhm(Hgzjxx hgzjxx) throws SQLException {
		UpdateBuilder<Hgzjxx, Integer> updateBuilder = dao.updateBuilder();
		Where<Hgzjxx, Integer> where = updateBuilder.where();
		where.eq("zjhm", hgzjxx.getZjhm());
		updateBuilder.updateColumnValue("idPic", hgzjxx.getIdPic());
		updateBuilder.update();
	}

	public List<Hgzjxx> findListByCphm(Hgzjxx hgzjxx) throws SQLException {
		String zjlb = hgzjxx.getZjlb();
		String cphm = hgzjxx.getCphm();
		String fffw = hgzjxx.getFffw();
		String zjbh = hgzjxx.getZjbh();
		// f.addSearch(SearchBuilder.eq("hgzjxx.zjlb", zjlb)); //证件类型
		// f.addSearch(SearchBuilder.eq("hgzjxx.zjhm", zjhm)); //证件号码
		// f.addSearch(SearchBuilder.eq("hgzjxx.fffw", fffw)); //服务范围
		// f.addSearch(SearchBuilder.eq("hgzjxx.zjbh", zjbh)); //证件编号
		QueryBuilder<Hgzjxx, Integer> queryBuilder = dao.queryBuilder();
		Where<Hgzjxx, Integer> where = queryBuilder.where();
		where.and(where.eq("zjlb", zjlb), where.eq("cphm", cphm), where.eq("fffw", fffw), where.eq("zjbh", zjbh));

		DBFlag.isDBBusy();
		List<Hgzjxx> hgzjxxs = null;
		try {
			hgzjxxs = where.query();
		} catch (Exception e) {
			DBFlag.setDBOnlyNotBusy();
			e.printStackTrace();
			return null;
		}
		DBFlag.setDBOnlyNotBusy();

		rebuildList(hgzjxxs);
		return hgzjxxs;
	}

	public List<Hgzjxx> findListByZjhm(Hgzjxx hgzjxx) throws SQLException {
		String zjlb = hgzjxx.getZjlb();
		String zjhm = hgzjxx.getZjhm();
		String fffw = hgzjxx.getFffw();
		String zjbh = hgzjxx.getZjbh();
		QueryBuilder<Hgzjxx, Integer> queryBuilder = dao.queryBuilder();
		Where<Hgzjxx, Integer> where = queryBuilder.where();
		where.and(where.eq("zjlb", zjlb), where.eq("zjhm", zjhm), where.eq("fffw", fffw), where.eq("zjbh", zjbh));

		DBFlag.isDBBusy();
		List<Hgzjxx> hgzjxxs = null;
		try {
			hgzjxxs = where.query();
		} catch (Exception e) {
			DBFlag.setDBOnlyNotBusy();
			e.printStackTrace();
			return null;
		}
		DBFlag.setDBOnlyNotBusy();

		rebuildList(hgzjxxs);
		return hgzjxxs;
	}
}
