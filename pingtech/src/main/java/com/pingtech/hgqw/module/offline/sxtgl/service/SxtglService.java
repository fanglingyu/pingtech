package com.pingtech.hgqw.module.offline.sxtgl.service;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.pingtech.hgqw.module.offline.base.service.BaseHgqwService;
import com.pingtech.hgqw.module.offline.sxtgl.dao.SxtglDao;
import com.pingtech.hgqw.module.offline.sxtgl.entity.Sxtgl;

/**
 * @title SxtglService.java
 * @description 获取摄像头概要信息Service
 * @author zhaotf
 * @company PingTech
 * @date 2013-10-18 下午5:41:36
 * @version V1.0
 * @Copyright(c)2013
 */
public class SxtglService extends BaseHgqwService {
	private  SxtglDao sxtglDao = null;
	public SxtglService() {
		this.sxtglDao = new SxtglDao();
	}


	/**
	 * @方法名：insert
	 * @功能说明：插入一条数据
	 * @author zhaotf
	 * @date 2013-10-18 下午3:35:23 
	 * @param sxtgl
	 * @return
	 * @throws SQLException
	 */
	public int insert(Sxtgl sxtgl) throws SQLException {
		return sxtglDao.insert(sxtgl);
	}

	/**
	 * @方法名：insertList
	 * @功能说明：插入多条数据
	 * @author zhaotf
	 * @date 2013-10-18 下午3:35:43 
	 * @param sxtglList
	 * @return
	 * @throws SQLException
	 */
	public int insertList(List<Sxtgl> sxtglList) throws SQLException {
			return sxtglDao.insertList(sxtglList);
	}


	/**
	 * @方法名：delete
	 * @功能说明：删除指定对象
	 * @author zhaotf
	 * @date 2013-10-18 下午3:36:00 
	 * @param sxtgl
	 * @return
	 * @throws SQLException
	 */
	public int delete(Sxtgl sxtgl) throws SQLException {
		return sxtglDao.delete(sxtgl);
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
		return  sxtglDao.deleteAll();
	}

	/**
	 * @方法名：update
	 * @功能说明：更新指定数据
	 * @author zhaotf
	 * @date 2013-10-18 下午3:36:32 
	 * @param sxtgl
	 * @return
	 * @throws SQLException
	 */
	public int update(Sxtgl sxtgl) throws SQLException {
		return sxtglDao.update(sxtgl);
	}

	/**
	 * @方法名：fiadAll
	 * @功能说明：查询所有数据
	 * @author zhaotf
	 * @date 2013-10-18 下午3:36:44 
	 * @return
	 * @throws SQLException
	 */
	public List<Sxtgl> fiadAll() throws SQLException {
		return sxtglDao.findAll();
	}

	/**
	 * @方法名：findAllForException
	 * @功能说明：查询符合条件数据(模糊才查询)
	 * @author zhaotf
	 * @date 2013-10-18 下午3:37:19 
	 * @param params
	 * @return
	 * @throws SQLException 
	 */
	public List<Map<String, String>> findAllForException(Map<String, Object> params) throws SQLException {
		return sxtglDao.findAllForException(params);
	}
}
