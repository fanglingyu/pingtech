package com.pingtech.hgqw.module.offline.txjl.service;

import java.sql.SQLException;
import java.util.List;

import com.pingtech.hgqw.module.offline.base.service.BaseHgqwService;
import com.pingtech.hgqw.module.offline.hgzjxx.entity.Hgzjxx;
import com.pingtech.hgqw.module.offline.txjl.dao.DkqkDao;
import com.pingtech.hgqw.module.offline.txjl.entity.Dkqk;

public class DkqkService extends BaseHgqwService {
	private static final String TAG = "DkqkService";

	private DkqkDao dkqkDao = null;

	public DkqkService() {
		this.dkqkDao = new DkqkDao();
	}

	/**
	 * 
	 * @方法名：insert
	 * @功能说明：插入一条数据
	 * @author liums
	 * @date 2013-9-23 下午5:32:43
	 * @param dcqk
	 * @return
	 * @throws SQLException
	 */
	public int insert(Dkqk dcqk) throws SQLException {
		dkqkDao.insert(dcqk);
		return 0;
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
	public int insertList(List<Dkqk> list) throws SQLException {
		return dkqkDao.insertList(list);
	}

	/**
	 * 
	 * @方法名：delete
	 * @功能说明：删除一条数据
	 * @author liums
	 * @date 2013-9-23 下午5:32:58
	 * @param dcqk
	 * @return
	 * @throws SQLException
	 */
	public int delete(Dkqk dcqk) throws SQLException {
		return dkqkDao.delete(dcqk);
	}

	/**
	 * 
	 * @方法名：update
	 * @功能说明：更新指定对象
	 * @author liums
	 * @date 2013-9-23 下午5:33:07
	 * @param mtdm
	 * @return
	 * @throws SQLException
	 */
	public int update(Dkqk dcqk) throws SQLException {
		return dkqkDao.update(dcqk);
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
	public List<Dkqk> findAll() throws SQLException {
		return dkqkDao.findAll();
	}

	public int deleteAll() throws SQLException {
		return dkqkDao.deleteAll();
	}

	public Dkqk findDkqkByHgzjxx(Hgzjxx hgzjxx, String kacbqkid) throws SQLException {
		return dkqkDao.findDkqkByHgzjxx(hgzjxx, kacbqkid);
	}

	public void saveOrUpdateDkfx(Hgzjxx hgzjxx, String kacbqkid, String dkfx, String dkqkid) throws SQLException {
		dkqkDao.saveOrUpdateDkfx(hgzjxx, kacbqkid, dkfx, dkqkid);
	}

	public void changDkfxByDkjlid(String dkjlid) throws SQLException {
		dkqkDao.changDkfxByDkjlid(dkjlid);
	}

	public void deleteByKacbqkidAndZjxxid(String cbzjffxxxid, String kacbqkid) {
		try {
			dkqkDao.deleteByKacbqkidAndZjxxid(cbzjffxxxid, kacbqkid);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
