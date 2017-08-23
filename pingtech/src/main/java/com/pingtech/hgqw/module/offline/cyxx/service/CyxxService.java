package com.pingtech.hgqw.module.offline.cyxx.service;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.pingtech.hgqw.module.offline.base.service.BaseHgqwService;
import com.pingtech.hgqw.module.offline.cyxx.dao.CyxxDao;
import com.pingtech.hgqw.module.offline.cyxx.entity.TBCyxx;
import com.pingtech.hgqw.module.offline.kacbqk.dao.KacbqkDao;
import com.pingtech.hgqw.module.offline.kacbqk.entity.Kacbqk;

public class CyxxService extends BaseHgqwService {
	private  CyxxDao cyxxDao = null;
	private KacbqkDao kacbqkDao = null;
	public CyxxService() {
		this.cyxxDao = new CyxxDao();
		this.kacbqkDao = new KacbqkDao();
	}

	/**
	 * 
	 * @方法名：insert
	 * @功能说明：插入一条数据
	 * @author liums
	 * @date 2013-9-23 下午5:32:43
	 * @param cyxx
	 * @return
	 * @throws SQLException
	 */
	public int insert(TBCyxx cyxx) throws SQLException {
		return cyxxDao.insert(cyxx);
	}

	public int insertList(List<TBCyxx> list) throws SQLException {
			return cyxxDao.insertList(list);
	}

	/**
	 * 
	 * @方法名：delete
	 * @功能说明：删除一条数据
	 * @author liums
	 * @date 2013-9-23 下午5:32:58
	 * @param cyxx
	 * @return
	 * @throws SQLException
	 */
	public int delete(TBCyxx cyxx) throws SQLException {
		return cyxxDao.delete(cyxx);
	}
	/**
	 * 
	 * @方法名：deleteAll
	 * @功能说明：删除全部数据
	 * @author 娄高伟
	 * @date  2013-10-10 上午11:15:35
	 * @return
	 * @throws SQLException
	 */
	public int deleteAll() throws SQLException {
		return  cyxxDao.deleteAll();
	}
	/**
	 * 
	 * @方法名：update
	 * @功能说明：更新指定对象
	 * @author liums
	 * @date 2013-9-23 下午5:33:07
	 * @param cyxx
	 * @return
	 * @throws SQLException
	 */
	public int update(TBCyxx cyxx) throws SQLException {
		return cyxxDao.update(cyxx);
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
	public List<TBCyxx> fiadAll() throws SQLException {
		return cyxxDao.findAll();
	}
	/**
	 * 
	 * @方法名：getTBCyxxByZjhm
	 * @功能说明：根据证件号码查船员
	 * @author 娄高伟
	 * @date  2013-10-10 下午2:13:16
	 * @param zjhm
	 * @return
	 * @throws SQLException
	 */
	
	public TBCyxx getTBCyxxByZjhm(String zjhm) throws SQLException {
		TBCyxx tbCyxx = cyxxDao.getTBCyxxByZjhm(zjhm);
		if (tbCyxx != null) {
			Kacbqk kacbqk = kacbqkDao.getKacbqkByKacbqkId(tbCyxx.getKacbqkid());
			if (kacbqk != null) {
				tbCyxx.setCbzwm(kacbqk.getCbzwm());
				tbCyxx.setHc(kacbqk.getHc());
			}
		}
		return tbCyxx;
	}

	public List<Map<String, String>> findAll(Map<String, Object> params) throws SQLException {
		return cyxxDao.findAll(params);
	}
	public List<Map<String, String>> getCyxxForException(Map<String, Object> params) throws SQLException {
		return cyxxDao.getCyxxForException(params);
	}

	/**
	 * 
	 * @方法名：gainCyxxNum
	 * @功能说明：获得船员数
	 * @author 娄高伟
	 * @date  2013-11-13 上午11:06:31
	 * @param kacbqkid
	 * @return
	 * @throws SQLException
	 */
	public int gainCyxxNum(String kacbqkid) throws SQLException {
		return cyxxDao.gainCyxxNum(kacbqkid);
	}
	
	/**
	 * 
	 * @方法名：gainCyxxCx
	 * @功能说明：根据证件号码HC查询船员
	 * @author 娄高伟
	 * @date  2013-10-18 上午9:01:41
	 * @param cardNumber
	 * @param hc
	 * @param kacbqkid
	 * @return
	 * @throws SQLException
	 */
	public TBCyxx gainCyxxCx(String cardNumber, String hc, String kacbqkid) throws SQLException {
		return cyxxDao.gainCyxxCx(cardNumber, hc, kacbqkid);
	}

	public TBCyxx findById(String ryid) throws SQLException{
		return cyxxDao.findById(ryid);
	}

	public void deleteCyxxByCbid(String kacbqkid) throws SQLException {
		cyxxDao.deleteCyxxByCbid(kacbqkid);
	}
}
