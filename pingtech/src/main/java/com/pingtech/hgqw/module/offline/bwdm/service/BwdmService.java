package com.pingtech.hgqw.module.offline.bwdm.service;

import java.sql.SQLException;
import java.util.List;

import com.pingtech.hgqw.module.offline.base.service.BaseHgqwService;
import com.pingtech.hgqw.module.offline.bwdm.dao.BwdmDao;
import com.pingtech.hgqw.module.offline.bwdm.entity.Bwdm;

public class BwdmService extends BaseHgqwService {
	private static final String TAG = "BwdmService";
	private BwdmDao bwdmDao = null;

	public BwdmService() {
		this.bwdmDao = new BwdmDao();
	}

	/**
	 * 
	 * @方法名：insert
	 * @功能说明：插入一条数据
	 * @author liums
	 * @date 2013-9-23 下午5:32:43
	 * @param bwdm
	 * @return
	 * @throws SQLException
	 */
	public int insert(Bwdm bwdm) throws SQLException {
		return bwdmDao.insert(bwdm);
	}

	/**
	 * 
	 * @方法名：insertList
	 * @功能说明：批量插入数据
	 * @author liums
	 * @date 2013-9-25 上午9:49:52
	 * @param list
	 * @return
	 * @throws SQLException
	 */
	public int insertList(List<Bwdm> list) throws SQLException {
		return bwdmDao.insertList(list);
	}

	/**
	 * 
	 * @方法名：delete
	 * @功能说明：删除一条数据
	 * @author liums
	 * @date 2013-9-23 下午5:32:58
	 * @param bwdm
	 * @return
	 * @throws SQLException
	 */
	public int delete(Bwdm bwdm) throws SQLException {
		return bwdmDao.delete(bwdm);
	}

	/**
	 * 
	 * @方法名：update
	 * @功能说明：更新指定对象
	 * @author liums
	 * @date 2013-9-23 下午5:33:07
	 * @param bwdm
	 * @return
	 * @throws SQLException
	 */
	public int update(Bwdm bwdm) throws SQLException {
		return bwdmDao.update(bwdm);
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
	public List<Bwdm> findAll() throws SQLException {
		return bwdmDao.findAll();
	}

	public int deleteAll() throws SQLException {
		return  bwdmDao.deleteAll();
	}

	/**
	 * 
	 * @方法名：getBwmcByMtdmAndBwdm
	 * @功能说明：根据码头代码和泊位代码获取码头
	 * @author liums
	 * @date  2013-11-29 下午2:00:42
	 * @param tkmtDm
	 * @param tkbwDm
	 * @return
	 * @throws SQLException
	 */
	public Bwdm getBwmcByMtdmAndBwdm(String tkmtDm, String tkbwDm) throws SQLException {
		return  bwdmDao.getBwmcByMtdmAndBwdm(tkmtDm , tkbwDm);
		}

	/**
	 * 
	 * @方法名：getBwdmByBwid
	 * @功能说明：根据泊位ID获取泊位代码
	 * @author liums
	 * @date  2013-12-6 下午2:30:57
	 * @param id
	 * @return
	 */
	public Bwdm getBwdmByBwid(String id) {
		try {
			return bwdmDao.getBwdmByBwid(id);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}



}
