package com.pingtech.hgqw.module.offline.kacbqk.service;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.pingtech.hgqw.module.offline.bwdm.entity.Bwdm;
import com.pingtech.hgqw.module.offline.bwdm.service.BwdmService;
import com.pingtech.hgqw.module.offline.kacbqk.dao.KacbqkDao;
import com.pingtech.hgqw.module.offline.kacbqk.entity.Kacbqk;
import com.pingtech.hgqw.module.offline.mtdm.entity.Mtdm;
import com.pingtech.hgqw.module.offline.mtdm.service.MtdmService;
import com.pingtech.hgqw.utils.Log;

public class KacbqkService {
	private static final String TAG = "KacbqkService";

	/** 船舶口岸状态 0预到港 */
	public static final String CBKAZT_YDG = "0";

	/** 船舶口岸状态 1在港 */
	public static final String CBKAZT_ZG = "1";

	/** 船舶口岸状态 2 预离港 */
	public static final String CBKAZT_YLG = "2";

	/** 船舶口岸状态 3离港 */
	public static final String CBKAZT_LG = "3";

	private KacbqkDao kacbqkDao = null;

	public KacbqkService() {
		this.kacbqkDao = new KacbqkDao();
	}

	/**
	 * 
	 * @方法名：insert
	 * @功能说明：插入一条数据
	 * @author liums
	 * @date 2013-9-23 下午5:32:43
	 * @param Kacbqk
	 * @return
	 * @throws SQLException
	 */
	public int insert(Kacbqk kacbqk) {
		try {
			kacbqkDao.insert(kacbqk);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	/**
	 * 
	 * @方法名：delete
	 * @功能说明：删除一条数据
	 * @author liums
	 * @date 2013-9-23 下午5:32:58
	 * @param Kacbqk
	 * @return
	 * @throws SQLException
	 */
	public int delete(Kacbqk kacbqk) throws SQLException {
		return kacbqkDao.delete(kacbqk);
	}

	/**
	 * 
	 * @方法名：update
	 * @功能说明：更新指定对象
	 * @author liums
	 * @date 2013-9-23 下午5:33:07
	 * @param Kacbqk
	 * @return
	 * @throws SQLException
	 */
	public int update(Kacbqk kacbqk) throws SQLException {
		return kacbqkDao.update(kacbqk);
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
	public List<Kacbqk> select() throws SQLException {
		return kacbqkDao.select();
	}

	/**
	 * 
	 * @方法名：hasDg
	 * @功能说明：根据航次判断船舶是否有抵港时间
	 * @author liums
	 * @date 2013-10-10 下午3:57:59
	 * @param voyageNumber
	 * @throws SQLException
	 */
	public boolean hasDg(String voyageNumber) {
		List<Kacbqk> kacbqks = null;
		try {
			kacbqks = kacbqkDao.hasDg(voyageNumber);
		} catch (SQLException e) {
			Log.e(TAG, e.getMessage(), e);
		}
		if (kacbqks == null || kacbqks.size() < 1) {
			return false;
		}
		return true;
	}

	/**
	 * 
	 * @方法名：hasDg
	 * @功能说明：根据航次判断船舶是否有离港时间
	 * @author liums
	 * @date 2013-10-10 下午3:57:59
	 * @param voyageNumber
	 * @throws SQLException
	 */
	public boolean hasLg(String voyageNumber) {
		List<Kacbqk> kacbqks = null;
		try {
			kacbqks = kacbqkDao.hasLg(voyageNumber);
		} catch (SQLException e) {
			Log.e(TAG, e.getMessage(), e);
		}
		if (kacbqks == null || kacbqks.size() < 1) {
			return false;
		}
		return true;
	}

	/**
	 * 
	 * @方法名：setDgsj
	 * @功能说明：更新指定航次的抵港时间
	 * @author liums
	 * @date 2013-10-10 下午4:52:01
	 * @param voyageNumber
	 * @param date
	 * @throws SQLException
	 */
	public void setDgsj(String voyageNumber, Date date) {
		try {
			kacbqkDao.setDgsj(voyageNumber, date);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @方法名：setDgsj
	 * @功能说明：更新指定航次的离港时间
	 * @author liums
	 * @date 2013-10-10 下午4:52:01
	 * @param voyageNumber
	 * @param date
	 * @throws SQLException
	 */
	public void setLgsj(String voyageNumber, Date date) {
		try {
			kacbqkDao.setLgsj(voyageNumber, date);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public int deleteAll() throws SQLException {
		return kacbqkDao.deleteAll();
	}

	public int insertList(List<Kacbqk> kacbqks) throws SQLException {
		return kacbqkDao.insertList(kacbqks);
	}

	/**
	 * 
	 * @方法名：byConditionGetKacbqk
	 * @功能说明：通过查询条件得到口岸船舶情况
	 * @author 赵琳
	 * @date 2013-10-11 上午11:32:17
	 * @param kacbqkId
	 * @param hc
	 * @param cbdh
	 * @return
	 */
	public Kacbqk byConditionGetKacbqk(String kacbqkid, String hc, String cbdh) {
		return kacbqkDao.byConditionGetKacbqk(kacbqkid, hc, cbdh);
	}

	/**
	 * 
	 * @方法名：findShipsbyPrams
	 * @功能说明：选择船舶根据条件查询船舶
	 * @author 娄高伟
	 * @date 2013-10-21 上午11:09:18
	 * @param mapParms
	 * @return
	 * @throws SQLException
	 */
	public List<Map<String, String>> findShipsbyPrams(Map<String, Object> mapParms) throws SQLException {
		return kacbqkDao.findShipsbyPrams(mapParms);
	}

	/**
	 * @方法名：getkacbqkByHC
	 * @功能说明：通过航次查找对象
	 * @author zhaotf
	 * @date 2013-10-21 下午2:02:15
	 * @param hc
	 *            航次
	 * @return
	 * @throws SQLException
	 */
	public Kacbqk getKacbqkByHC(String hc) {
		try {
			return kacbqkDao.query(hc);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * @方法名：getKacbqkByKacbqkId
	 * @功能说明：通过kacbqkId获取对象
	 * @author zhaotf
	 * @date 2013-10-23 下午3:58:41
	 * @param kacbqkId
	 * @return
	 * @throws SQLException
	 */
	public Kacbqk getKacbqkByKacbqkId(String kacbqkId) throws SQLException {
		return kacbqkDao.getKacbqkByKacbqkId(kacbqkId);
	}

	/**
	 * 
	 * @方法名：getTkwzStr
	 * @功能说明：获取停靠位置名称
	 * @author liums
	 * @date 2013-11-29 下午1:39:50
	 * @param tkmtDm
	 * @param tkbwDm
	 * @return
	 * @throws SQLException
	 */
	public String getTkwzStr(String tkmtDm, String tkbwDm) {
		try {
			Mtdm mtdm = new MtdmService().getMtmcByMtdm(tkmtDm);
			Bwdm bwdm = new BwdmService().getBwmcByMtdmAndBwdm(tkmtDm, tkbwDm);
			if (mtdm != null && bwdm != null) {
				return mtdm.getMtmc() + "," + bwdm.getBwmc();
			} else if (mtdm != null) {
				return mtdm.getMtmc();
			} else if (bwdm != null) {
				return bwdm.getBwmc();
			}
		} catch (Exception e) {
			Log.i(TAG, "getTkwzStr，查询失败：" + e.getMessage());
		}
		return "";
	}

	public void deleteKacbqkByHc(String hc) throws SQLException {
		kacbqkDao.deleteKacbqkByHc(hc);
	}
}
