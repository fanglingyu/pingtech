package com.pingtech.hgqw.module.offline.fwxcb.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;
import com.pingtech.hgqw.module.offline.base.dao.BaseHgqwDao;
import com.pingtech.hgqw.module.offline.fwxcb.entity.Fwxcb;
import com.pingtech.hgqw.utils.StringUtils;

/**
 * 
 * 
 * 类描述：服务性船舶DAO
 * 
 * <p>
 * Title: 江海港边检勤务综合管理系统-FwxcbDao.java
 * </p>
 * <p>
 * Copyright: Copyright (c) 2012
 * </p>
 * <p>
 * Company: 品恩科技
 * </p>
 * 
 * @author 赵琳
 * @version 1.0
 * @date 2013-10-15 下午3:34:15
 */
public class FwxcbDao extends BaseHgqwDao<Fwxcb> {

	public FwxcbDao() {
		super();
		initDao();
	}

	private void initDao() {
		try {
			dao = getDao(Fwxcb.class);
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
	public int insert(Fwxcb fwxcb) throws SQLException {
		dao.createOrUpdate(fwxcb);
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
	public int insertList(List<Fwxcb> list) throws SQLException {
		if (list == null) {
			return -1;
		}
		beginTransaction();
		int result = 1;
		for (Fwxcb fwxcb : list) {
			insert(fwxcb);
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
	public int delete(Fwxcb fwxcb) throws SQLException {
		return dao.delete(fwxcb);
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
	public int update(Fwxcb fwxcb) throws SQLException {
		return dao.update(fwxcb);
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
	public List<Fwxcb> findAll() throws SQLException {
		return dao.queryForAll();
	}

	/**
	 * 
	 * @方法名：findFwShipsbyPrams
	 * @功能说明：根据条件查询涉外船舶
	 * @author 娄高伟
	 * @date 2013-10-21 上午11:21:21
	 * @param mapParms
	 * @return
	 * @throws SQLException
	 */
	public List<Map<String, String>> findFwShipsbyPrams(Map<String, Object> mapParms) throws SQLException {
		QueryBuilder<Fwxcb, Integer> queryBuilder = dao.queryBuilder();
		Where<Fwxcb, Integer> where = queryBuilder.where();
		where.eq("sybj", "1");
		if (StringUtils.isNotEmpty(mapParms.get("shipName"))) {
			where.and().like("cbmc", "%" + mapParms.get("shipName").toString().trim() + "%");
		}
		if (StringUtils.isNotEmpty(mapParms.get("shipPurpose"))) {
			where.and().eq("cbzyyt", mapParms.get("shipPurpose").toString().trim());
		}
		if (StringUtils.isNotEmpty(mapParms.get("shipMaster"))) {
			where.and().eq("cz", "%" + mapParms.get("shipMaster").toString().trim() + "%");
		}
		List<Fwxcb> list = where.query();
		List<Map<String, String>> lists = new ArrayList<Map<String, String>>();
		for (int i = 0; i < list.size(); i++) {
			Fwxcb fwxcb = list.get(i);
			Map<String, String> map = new HashMap<String, String>();
			map.put("id", fwxcb.getId());
			// map.put("cbmc", fwxcb.getCbmc());
			map.put("cbzwm", fwxcb.getCbmc());// 更改标签cbmc--->cbzwm
			map.put("cjsj", fwxcb.getCjsj());
			map.put("ss", fwxcb.getSwfwdwmc());
			map.put("ssdw", fwxcb.getSwfwdwmc());
			map.put("cbzyyt", fwxcb.getCbzyyt());
			map.put("czmc", fwxcb.getCz());
			map.put("cblx", "service");
			lists.add(map);

		}
		return lists;

	}

}
