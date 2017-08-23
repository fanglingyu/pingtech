package com.pingtech.hgqw.module.offline.fwxcb.service;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.pingtech.hgqw.module.offline.base.service.BaseHgqwService;
import com.pingtech.hgqw.module.offline.fwxcb.dao.FwxcbDao;
import com.pingtech.hgqw.module.offline.fwxcb.entity.Fwxcb;

public class FwxcbService extends BaseHgqwService {
	private static final String TAG = "FwxcbService";
	private FwxcbDao fwxcbDao = null;

	public FwxcbService() {
		this.fwxcbDao = new FwxcbDao();
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
	public int insert(Fwxcb fwxcb) throws SQLException {
		return fwxcbDao.insert(fwxcb);
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
	public int insertList(List<Fwxcb> list) throws SQLException {
		return fwxcbDao.insertList(list);
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
	public int delete(Fwxcb fwxcb) throws SQLException {
		return fwxcbDao.delete(fwxcb);
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
	public int update(Fwxcb fwxcb) throws SQLException {
		return fwxcbDao.update(fwxcb);
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
	public List<Fwxcb> findAll() throws SQLException {
		return fwxcbDao.findAll();
	}

	public int deleteAll() throws SQLException {
		return  fwxcbDao.deleteAll();
	}

	/**
	 * 
	 * @方法名：findFwShipsbyPrams
	 * @功能说明：根据条件查询涉外船舶
	 * @author 娄高伟
	 * @date  2013-10-21 上午11:21:21
	 * @param mapParms
	 * @return
	 * @throws SQLException
	 */
	public List<Map<String, String>> findFwShipsbyPrams(Map<String, Object> mapParms) throws SQLException {
		return fwxcbDao.findFwShipsbyPrams(mapParms);
	}

}
