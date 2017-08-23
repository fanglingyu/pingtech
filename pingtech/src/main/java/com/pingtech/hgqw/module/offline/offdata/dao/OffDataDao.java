package com.pingtech.hgqw.module.offline.offdata.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.util.Log;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;
import com.pingtech.hgqw.module.offline.DBFlag;
import com.pingtech.hgqw.module.offline.base.dao.BaseHgqwDao;
import com.pingtech.hgqw.module.offline.base.utils.DaoManager;
import com.pingtech.hgqw.module.offline.offdata.entity.OffData;
import com.pingtech.hgqw.utils.StringUtils;
import com.pingtech.hgqw.utils.xml.PullXmlUtils;

public class OffDataDao extends BaseHgqwDao<OffData> {

	private static final String TAG = "OffDataDao";

	public OffDataDao() {
		super();
		initDao();
	}

	private void initDao() {
		try {
			dao = DaoManager.getDaoManager().getOffDataDao();
			if (dao == null) {
				Log.i(TAG, "dao == null");
				DaoManager.getDaoManager().setOffDataDao((Dao<OffData, Integer>) getDao(OffData.class));
			}
			dao = DaoManager.getDaoManager().getOffDataDao();
			if (!dao.getConnectionSource().isOpen()) {
				Log.i(TAG, "isOpen == false");
				DaoManager.getDaoManager().setOffDataDao((Dao<OffData, Integer>) getDao(OffData.class));
			}
			dao = DaoManager.getDaoManager().getOffDataDao();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @方法名：insert
	 * @功能说明：插入或修改一条数据
	 * @author liums
	 * @date 2013-9-25 上午9:48:33
	 * @param offData
	 * @return
	 * @throws SQLException
	 */
	public int insert(OffData offData) throws SQLException {
		DBFlag.isDBBusy();

		dao.createOrUpdate(offData);
		DBFlag.setDBOnlyNotBusy();

		return 0;
	}

	/**
	 * 
	 * @方法名：create
	 * @功能说明：插入一条数据(如果存在，在保留原来的基础上添加)
	 * @author zhaotf
	 * @date 2013-9-25 上午9:48:33
	 * @param offData
	 * @return
	 * @throws SQLException
	 */
	public int create(OffData offData) throws SQLException {

		DBFlag.isDBBusy();

		dao.create(offData);

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
	public int insertList(List<OffData> list) throws SQLException {
		if (list == null) {
			return -1;
		}
		beginTransaction();
		int i = 0;
		int result = 1;
		for (OffData offData : list) {
			// Log.i("insert", i++ + "");
			insert(offData);
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
	 * @param offData
	 * @return
	 * @throws SQLException
	 */
	public int delete(OffData offData) throws SQLException {
		DBFlag.isDBBusy();
		dao.delete(offData);
		DBFlag.setDBOnlyNotBusy();

		return 0;
	}

	/**
	 * 
	 * @方法名：deleteByIds
	 * @功能说明：删除多条数据
	 * @author 娄高伟
	 * @date 2013-9-27 上午11:53:49
	 * @param ids
	 * @return
	 * @throws SQLException
	 */

	public int deleteByIds(Collection<Integer> ids) throws SQLException {
		DBFlag.isDBBusy();
		dao.deleteIds(ids);
		DBFlag.setDBOnlyNotBusy();

		return 0;
	}

	/**
	 * 
	 * @方法名：deleteByCzid
	 * @功能说明：删除根据操作ID
	 * @author 娄高伟
	 * @date 2013-10-20 上午11:22:40
	 * @param id
	 * @return
	 * @throws SQLException
	 */
	public int deleteByCzid(String id) throws SQLException {
		DBFlag.isDBBusy();

		DeleteBuilder<OffData, Integer> builder = dao.deleteBuilder();
		Where<OffData, Integer> where = builder.where();
		where.eq("czid", id);
		builder.delete();
		
		DBFlag.setDBOnlyNotBusy();
		return 0;

	}

	/**
	 * 
	 * @方法名：findByCzid
	 * @功能说明：查询数据
	 * @author 娄高伟
	 * @date 2013-11-12 下午8:40:15
	 * @param czid
	 * @return
	 * @throws SQLException
	 */
	public OffData findByCzid(String czid) throws SQLException {
		QueryBuilder<OffData, Integer> builder = dao.queryBuilder();
		Where<OffData, Integer> where = builder.where();
		where.eq("czid", czid);
		return builder.queryForFirst();
	}

	/**
	 * 
	 * @方法名：update
	 * @功能说明：更新制定数据
	 * @author liums
	 * @date 2013-9-25 上午9:49:12
	 * @param offData
	 * @return
	 * @throws SQLException
	 */
	public int update(OffData offData) throws SQLException {
		return dao.update(offData);
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
	public List<OffData> findAll() throws SQLException {
		return dao.queryForAll();
	}

	/**
	 * 
	 * @方法名：findAllByGN
	 * @功能说明：以功能点查询数据
	 * @author 娄高伟
	 * @date 2013-10-14 下午1:01:54
	 * @param czmk
	 * @param czgn
	 * @param clstatus
	 * @return
	 * @throws SQLException
	 */
	public List<OffData> findAllByGN(int czmk, int czgn, String clstatus, String czid) throws SQLException {
		List<OffData> list = null;
		if (StringUtils.isNotEmpty(clstatus)) {
			list = dao.queryBuilder().where().eq("czmk", czmk).and().eq("czgn", czgn).and().eq("clstatus", clstatus).query();
		} else {
			if (StringUtils.isNotEmpty(czid)) {
				list = dao.queryBuilder().where().eq("czmk", czmk).and().eq("czgn", czgn).and().eq("czid", czid).query();
			} else {

				list = dao.queryBuilder().where().eq("czmk", czmk).and().eq("czgn", czgn).query();
			}
		}
		return list;
	}

	/**
	 * 
	 * @方法名：findAllByGN
	 * @功能说明：查询离线数据
	 * @author 娄高伟
	 * @date 2013-10-30 下午6:19:00
	 * @param czmk
	 * @param czgn
	 * @param clstatus
	 * @param czid
	 * @param userid
	 * @return
	 * @throws SQLException
	 */
	public List<OffData> findAllByGN(int czmk, int czgn, String clstatus, String czid, String userid) throws SQLException {
		List<OffData> list = null;
		if (StringUtils.isNotEmpty(clstatus)) {
			list = dao.queryBuilder().where().eq("czmk", czmk).and().eq("czgn", czgn).and().eq("clstatus", clstatus).and().eq("userid", userid)
					.query();
		} else {
			if (StringUtils.isNotEmpty(czid)) {
				list = dao.queryBuilder().where().eq("czmk", czmk).and().eq("czgn", czgn).and().eq("czid", czid).and().eq("userid", userid).query();
			} else {

				list = dao.queryBuilder().where().eq("czmk", czmk).and().eq("czgn", czgn).and().eq("userid", userid).query();
			}
		}
		return list;
	}

	/**
	 * 
	 * @方法名：findOneByGN
	 * @功能说明：根据功能模块查询离线数据
	 * @author 娄高伟
	 * @date 2013-11-12 下午2:38:43
	 * @param czmk
	 * @param czgn
	 * @return
	 * @throws SQLException
	 */
	public OffData findOneByGN(int czmk, int czgn) throws SQLException {
		QueryBuilder<OffData, Integer> queryBuilder = dao.queryBuilder();
		Where<OffData, Integer> where = queryBuilder.where();
		queryBuilder.orderBy("cjsj", false);
		where.eq("czmk", czmk).and().eq("czgn", czgn).query();
		return where.queryForFirst();
	}

	/**
	 * 
	 * @方法名：findModuleByGN
	 * @功能说明：根据功能模块获得集合数据
	 * @author 娄高伟
	 * @date 2013-10-14 下午12:58:00
	 * @param czmk
	 * @param czgn
	 * @param clstatus
	 * @return
	 * @throws SQLException
	 */
	public List<Map<String, String>> findModuleByGN(int czmk, int czgn, String clstatus, String czid) throws SQLException {
		List<Map<String, String>> lists = new ArrayList<Map<String, String>>();
		List<OffData> list = this.findAllByGN(czmk, czgn, clstatus, czid);
		for (int i = 0; i < list.size(); i++) {
			String xml = list.get(i).getXmldata();
			if (xml != null && !"".equals(xml)) {
				Map<String, String> map = PullXmlUtils.parseXMLData(xml);
				lists.add(map);
			}
		}

		return lists;
	}

	/**
	 * 
	 * @方法名：findAll
	 * @功能说明：查询所有数据按功能分开
	 * @author liums
	 * @date 2013-9-25 上午9:49:24
	 * @return
	 * @throws SQLException
	 */
	public Map<String, List<OffData>> findAllByCount(long count) throws SQLException {
		Map<String, List<OffData>> map = new HashMap<String, List<OffData>>();
		// List<OffData> list = dao.queryForAll();
		List<OffData> list = findByCount(count);
		for (int i = 0; i < list.size(); i++) {
			OffData offData = list.get(i);
			if (map != null) {
				if (offData != null) {
					if (map.containsKey(offData.getCzmk() + "_" + offData.getCzgn())) {
						List<OffData> offdatas = map.get(offData.getCzmk() + "_" + offData.getCzgn());
						offdatas.add(offData);
						map.put(offData.getCzmk() + "_" + offData.getCzgn(), offdatas);
					} else {
						List<OffData> offdatas = new ArrayList<OffData>();
						offdatas.add(offData);
						map.put(offData.getCzmk() + "_" + offData.getCzgn(), offdatas);
					}
				}

			} else {
				if (offData != null) {
					List<OffData> offdatas = new ArrayList<OffData>();
					offdatas.add(offData);
					map.put(offData.getCzmk() + "_" + offData.getCzgn(), offdatas);
				}
			}

		}

		return map;
	}

	public OffData findAllByGN(int czmk, int czgn, String czid) throws SQLException {
		return dao.queryBuilder().where().eq("czmk", czmk).and().eq("czgn", czgn).and().eq("czid", czid).queryForFirst();
	}

	public List<OffData> findByCount(long count) throws SQLException {
		QueryBuilder<OffData, Integer> queryBuilder = dao.queryBuilder();
		queryBuilder.limit(count);
		List<OffData> datas = new ArrayList<OffData>();
		datas = queryBuilder.query();
		return datas;
	}
}
