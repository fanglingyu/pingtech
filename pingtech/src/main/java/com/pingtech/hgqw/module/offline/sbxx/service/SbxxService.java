package com.pingtech.hgqw.module.offline.sbxx.service;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.pingtech.hgqw.module.offline.base.service.BaseHgqwService;
import com.pingtech.hgqw.module.offline.sbxx.dao.SbxxDao;
import com.pingtech.hgqw.module.offline.sbxx.entity.Sbxx;

/**
 * @title SbxxService.java
 * @description 智能设备概要信息Service
 * @author zhaotf
 * @company PingTech
 * @date 2013-10-18 下午5:29:18
 * @version V1.0
 * @Copyright(c)2013
 */
public class SbxxService extends BaseHgqwService {
	private  SbxxDao sbxxDao = null;
	public SbxxService() {
		this.sbxxDao = new SbxxDao();
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
	public int insert(Sbxx Sbxx) throws SQLException {
		return sbxxDao.insert(Sbxx);
	}

	/**
	 * @方法名：insertList
	 * @功能说明：插入多条数据
	 * @author zhaotf
	 * @date 2013-10-18 下午3:35:43 
	 * @param sbxxList
	 * @return
	 * @throws SQLException
	 */
	public int insertList(List<Sbxx> sbxxList) throws SQLException {
			return sbxxDao.insertList(sbxxList);
	}


	/**
	 * @方法名：delete
	 * @功能说明：删除指定对象
	 * @author zhaotf
	 * @date 2013-10-18 下午3:36:00 
	 * @param sbxx
	 * @return
	 * @throws SQLException
	 */
	public int delete(Sbxx sbxx) throws SQLException {
		return sbxxDao.delete(sbxx);
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
		return  sbxxDao.deleteAll();
	}

	/**
	 * @方法名：update
	 * @功能说明：更新指定数据
	 * @author zhaotf
	 * @date 2013-10-18 下午3:36:32 
	 * @param sbxx
	 * @return
	 * @throws SQLException
	 */
	public int update(Sbxx sbxx) throws SQLException {
		return sbxxDao.update(sbxx);
	}

	/**
	 * @方法名：fiadAll
	 * @功能说明：查询所有数据
	 * @author zhaotf
	 * @date 2013-10-18 下午3:36:44 
	 * @return
	 * @throws SQLException
	 */
	public List<Sbxx> fiadAll() throws SQLException {
		return sbxxDao.findAll();
	}

	/**
	 * @方法名：findAllForException
	 * @功能说明：异常信息模块只能设备查询
	 * @author zhaotf
	 * @date 2013-10-18 下午3:37:19 
	 * @param params
	 * @return
	 * @throws SQLException 
	 */
	public List<Map<String, String>> findAllForException(Map<String, Object> params) throws SQLException {
		return sbxxDao.findAllForException(params);

	}
}
