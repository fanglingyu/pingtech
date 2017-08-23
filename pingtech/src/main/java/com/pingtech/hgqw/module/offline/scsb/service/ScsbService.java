package com.pingtech.hgqw.module.offline.scsb.service;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.pingtech.hgqw.module.offline.base.service.BaseHgqwService;
import com.pingtech.hgqw.module.offline.scsb.dao.ScsbDao;
import com.pingtech.hgqw.module.offline.scsb.entity.Scsb;

/**
 * @title ScsbService.java
 * @description 获取手持设备概要信息接口Service
 * @author zhaotf
 * @company PingTech
 * @date 2013-10-18 下午5:37:08
 * @version V1.0
 * @Copyright(c)2013
 */
public class ScsbService extends BaseHgqwService {
	private  ScsbDao scsbDao = null;
	public ScsbService() {
		this.scsbDao = new ScsbDao();
	}


	/**
	 * @方法名：insert
	 * @功能说明：插入一条数据
	 * @author zhaotf
	 * @date 2013-10-18 下午3:35:23 
	 * @param Sbxx
	 * @return
	 * @throws SQLException
	 */
	public int insert(Scsb scsb) throws SQLException {
		return scsbDao.insert(scsb);
	}

	/**
	 * @方法名：insertList
	 * @功能说明：插入多条数据
	 * @author zhaotf
	 * @date 2013-10-18 下午3:35:43 
	 * @param scsbList
	 * @return
	 * @throws SQLException
	 */
	public int insertList(List<Scsb> scsbList) throws SQLException {
			return scsbDao.insertList(scsbList);
	}


	/**
	 * @方法名：delete
	 * @功能说明：删除指定对象
	 * @author zhaotf
	 * @date 2013-10-18 下午3:36:00 
	 * @param scsb
	 * @return
	 * @throws SQLException
	 */
	public int delete(Scsb scsb) throws SQLException {
		return scsbDao.delete(scsb);
	}

	/**
	 * @方法名：deleteAll
	 * @功能说明：删除全部数据
	 * @author zhaotf
	 * @date 2013-10-18 下午3:36:20 
	 * @return
	 * @throws SQLException
	 */
	public int deleteAll() throws SQLException {
		return  scsbDao.deleteAll();
	}

	/**
	 * @方法名：update
	 * @功能说明：更新指定数据
	 * @author zhaotf
	 * @date 2013-10-18 下午3:36:32 
	 * @param scsb
	 * @return
	 * @throws SQLException
	 */
	public int update(Scsb scsb) throws SQLException {
		return scsbDao.update(scsb);
	}

	/**
	 * @方法名：fiadAll
	 * @功能说明：查询所有数据
	 * @author zhaotf
	 * @date 2013-10-18 下午3:36:44 
	 * @return
	 * @throws SQLException
	 */
	public List<Scsb> fiadAll() throws SQLException {
		return scsbDao.findAll();
	}

	/**
	 * @方法名：findListSbxxLike
	 * @功能说明：查询符合条件数据(模糊才查询)
	 * @author zhaotf
	 * @date 2013-10-18 下午3:37:19 
	 * @param params
	 * @return
	 * @throws SQLException 
	 */
	public List<Map<String, String>> findAllForException(Map<String, Object> params) throws SQLException {
		return scsbDao.findAllForException(params);
	}
}
