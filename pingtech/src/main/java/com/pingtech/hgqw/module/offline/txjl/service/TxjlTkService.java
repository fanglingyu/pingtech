package com.pingtech.hgqw.module.offline.txjl.service;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.pingtech.hgqw.module.offline.base.service.BaseHgqwService;
import com.pingtech.hgqw.module.offline.txjl.dao.TxjlTkDao;
import com.pingtech.hgqw.module.offline.txjl.entity.TxjlTk;

public class TxjlTkService extends BaseHgqwService {
	private static final String TAG = "TxjlTkService";

	private TxjlTkDao txjlTkDao = null;

	public TxjlTkService() {
		this.txjlTkDao = new TxjlTkDao();
	}

	/**
	 * 
	 * @方法名：insert
	 * @功能说明：插入一条数据
	 * @author liums
	 * @date 2013-9-23 下午5:32:43
	 * @param txjlTk
	 * @return
	 * @throws SQLException
	 */
	public int insert(TxjlTk txjlTk) throws SQLException {
		txjlTkDao.insert(txjlTk);
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
	public int insertList(List<TxjlTk> list) throws SQLException {
		return txjlTkDao.insertList(list);
	}

	/**
	 * 
	 * @方法名：delete
	 * @功能说明：删除一条数据
	 * @author liums
	 * @date 2013-9-23 下午5:32:58
	 * @param txjlTk
	 * @return
	 * @throws SQLException
	 */
	public int delete(TxjlTk txjlTk) throws SQLException {
		return txjlTkDao.delete(txjlTk);
	}

	/**
	 * 
	 * @方法名：update
	 * @功能说明：更新指定对象
	 * @author liums
	 * @date 2013-9-23 下午5:33:07
	 * @param txjlTk
	 * @return
	 * @throws SQLException
	 */
	public int update(TxjlTk txjlTk) throws SQLException {
		return txjlTkDao.update(txjlTk);
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
	public List<TxjlTk> findAll() throws SQLException {
		return txjlTkDao.findAll();
	}

	public int deleteAll() throws SQLException {
		return txjlTkDao.deleteAll();
	}

	public TxjlTk getTxjlTkByZjhmAndHc(String zjhm, String hc) {
		try {
			return txjlTkDao.getTxjlTkByZjhmAndHc(zjhm, hc);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 根据相关条件获取船员信息
	 */
	// public List<Cyxx> getCurrentCyxx(Cyxx cyxx){
	//
	// if (cyxx == null) {
	// return null;
	// }
	// // 获取在船下的船员信息
	// List<Object[]> objList = this.txjlTkDao.getTxjltkNowList(in_cyxx.getHc(),
	// "lu", "1");
	// // 查询所有船员数据
	// Finder cyfinder = Finder.getInstance(Cyxx.class, "cyxx");
	// cyfinder.addEntity(SearchBuilder.entity(Kacbqk.class, "kacbqk"));
	// cyfinder.addEntity(SearchBuilder.entity(Cyxx.class, "cyxx"));
	// cyfinder.addSearch(SearchBuilder.eqProperty("kacbqk.kacbqkid",
	// "cyxx.kacbqkid"));
	// cyfinder.addSearch(SearchBuilder.eqProperty("kacbqk.jcfl", "cyxx.jcfl"));
	// // 船员的检查分类要与口岸船舶情况的保持一致
	// cyfinder.addSearch(SearchBuilder.eq("kacbqk.hc", in_cyxx.getHc()));
	// cyfinder.addOrder(Order.asc("cyxx.ryxh")); // 按照人员序号排序
	// cyfinder.addFields(SearchBuilder
	// .fields("cyxx.hyid as hyid, cyxx.kacbqkid as kacbqkid, kacbqk.hc as hc, cyxx.zjhm, cyxx.xm, cyxx.xb,cyxx.zw, cyxx.lcbz, cyxx.ryxh"));
	// List<Cyxx> cyxxList = cyfinder.execute(); // 执行查询语句，获取所有的船员数据
	// // 整理船员信息，lcbz用于标识该船员在船上，还是在船下，
	// if (objList != null && objList.size() > 0 && cyxxList != null &&
	// cyxxList.size() > 0) {
	// for (Cyxx cyxx : cyxxList) {
	// cyxx.setCywz("0"); // 标识在船上
	// for (Object[] dldl : objList) {
	// if (dldl[1] != null && dldl[1].equals(cyxx.getZjhm())) {
	// cyxx.setCywz("1"); // 标识在船下
	// continue;
	// }
	// }
	// }
	// } else if ((objList == null || objList.size() < 1) && cyxxList != null &&
	// cyxxList.size() > 0) { // 如果所有都在船上
	// for (Cyxx cyxx : cyxxList) {
	// cyxx.setCywz("0"); // 标识在船上
	// }
	// }
	//
	// return cyxxList;
	//
	// }
	/**
	 * 
	 * @方法名：getTxjlTkByZjhmAndHc
	 * @功能说明：根据航次查询数据
	 * @author jiajw
	 * @date 2014-2-14 上午10:13:34
	 * @param hc
	 * @return
	 */
	public List<Map<String, String>> findCyxxByHc(Map<String, Object> params) throws SQLException {
		return txjlTkDao.findCyxxByHc(params);
	}

	public void updateTxjlTkTxfx(String zjhm, String hc, String txfx) throws SQLException {
		txjlTkDao.updateTxjlTkTxfx(zjhm, hc, txfx);
	}

	public void deleteByZjhmAndHc(String zjhm, String hc) {
		try {
			txjlTkDao.deleteByZjhmAndHc(zjhm, hc);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
