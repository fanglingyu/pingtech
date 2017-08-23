package com.pingtech.hgqw.module.offline.hgzjxx.service;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.pingtech.hgqw.module.offline.base.service.BaseHgqwService;
import com.pingtech.hgqw.module.offline.hgzjxx.dao.HgzjxxDao;
import com.pingtech.hgqw.module.offline.hgzjxx.entity.Hgzjxx;

public class HgzjxxService extends BaseHgqwService {
	private HgzjxxDao hgzjxxDao = null;

	public HgzjxxService() {
		this.hgzjxxDao = new HgzjxxDao();
	}

	/**
	 * 
	 * @方法名：insert
	 * @功能说明：插入一条数据
	 * @author liums
	 * @date 2013-9-23 下午5:32:43
	 * @param hgzjxx
	 * @return
	 * @throws SQLException
	 */
	public int insert(Hgzjxx hgzjxx) throws SQLException {
		return hgzjxxDao.insert(hgzjxx);
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
	public int insertList(List<Hgzjxx> list) throws SQLException {
		return hgzjxxDao.insertList(list);
	}

	/**
	 * 
	 * @方法名：delete
	 * @功能说明：删除一条数据
	 * @author liums
	 * @date 2013-9-23 下午5:32:58
	 * @param hgzjxx
	 * @return
	 * @throws SQLException
	 */
	public int delete(Hgzjxx hgzjxx) throws SQLException {
		return hgzjxxDao.delete(hgzjxx);
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
		return hgzjxxDao.delete(hgzjxxs);
	}

	public int deleteAll() throws SQLException {
		return hgzjxxDao.deleteAll();
	}

	/**
	 * 
	 * @方法名：update
	 * @功能说明：更新指定对象
	 * @author liums
	 * @date 2013-9-23 下午5:33:07
	 * @param hgzjxx
	 * @return
	 * @throws SQLException
	 */
	public int update(Hgzjxx hgzjxx) throws SQLException {
		return hgzjxxDao.update(hgzjxx);
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
	public List<Hgzjxx> fiadAll() throws SQLException {
		return hgzjxxDao.findAll();
	}

	/**
	 * 
	 * @方法名：getYfZjxx
	 * @功能说明：验放信息查询
	 * @author 娄高伟
	 * @date 2013-10-10 下午4:25:43
	 * @param ickey
	 * @param zjhm
	 * @param mrickey
	 * @param sfsk
	 * @param zjType
	 * @param icffzt
	 * @param cphm TODO
	 * @param isClyz TODO
	 * @return
	 * @throws SQLException
	 */
	public List<Hgzjxx> getYfZjxx(String ickey, String zjhm, String mrickey, String sfsk, String zjType, String icffzt, String cphm, boolean isClyz) throws SQLException {

		return hgzjxxDao.getYfZjxx(ickey, zjhm, mrickey, sfsk, zjType, icffzt, cphm, isClyz);
	}
	public List<Hgzjxx> getYfZjxx(String ickey, String zjhm, String mrickey, String sfsk,boolean isSearchDk, String icffzt, String cphm, boolean isClyz) throws SQLException {
		
		return hgzjxxDao.getYfZjxx(ickey, zjhm, mrickey, sfsk, isSearchDk, icffzt, cphm, isClyz);
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
		return hgzjxxDao.getZjxx(params);
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
		return hgzjxxDao.gainXJHgzjxx(cardNumber, sfsk, hc);

	}

	/**
	 * 
	 * @方法名：findById
	 * @功能说明：通过主键ID查找对象
	 * @author liums
	 * @return
	 * @throws SQLException
	 * @date 2013-10-23 下午5:13:29
	 */
	public Hgzjxx findById(String cbzjffxxxid) throws SQLException {
		return hgzjxxDao.findById(cbzjffxxxid);
	}

	public long countOf() {
		try {
			return hgzjxxDao.countOf();
		} catch (SQLException e) {
			e.printStackTrace();
			return 0;
		}
	}

	public void deleteByHc(String hc) throws SQLException {
		hgzjxxDao.deleteByHc(hc);
	}

	public void deleteHgzjxxWhichHasHc() {
		try {
			hgzjxxDao.deleteHgzjxxWhichHasHc();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public List<Hgzjxx> findByCountAndOffset(int startRow, int maxRows) {
		try {
			return hgzjxxDao.findByCountAndOffset(startRow, maxRows);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	public void updateList(List<Hgzjxx> hgzjxxs) {
		try {
			hgzjxxDao.updateList(hgzjxxs);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public List<Hgzjxx> findListByCphm(Hgzjxx hgzjxx) {
		try {
			return hgzjxxDao.findListByCphm(hgzjxx);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	public List<Hgzjxx> findListByZjhm(Hgzjxx hgzjxx) {
		try {
			return hgzjxxDao.findListByZjhm(hgzjxx);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
}
