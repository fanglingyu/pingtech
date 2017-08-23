package com.pingtech.hgqw.module.offline.txjl.service;

import java.sql.SQLException;
import java.util.List;

import com.pingtech.hgqw.module.offline.base.service.BaseHgqwService;
import com.pingtech.hgqw.module.offline.txjl.dao.TxjlKkDao;
import com.pingtech.hgqw.module.offline.txjl.entity.TxjlKk;

public class TxjlKkService extends BaseHgqwService {
	private static final String TAG = "TxjlKkService";

	private TxjlKkDao txjlKkDao = null;

	public TxjlKkService() {
		this.txjlKkDao = new TxjlKkDao();
	}

	/**
	 * 
	 * @方法名：insert
	 * @功能说明：插入一条数据
	 * @author liums
	 * @date 2013-9-23 下午5:32:43
	 * @param mtdm
	 * @return
	 * @throws SQLException
	 */
	public int insert(TxjlKk mtdm) throws SQLException {
		txjlKkDao.insert(mtdm);
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
	public int insertList(List<TxjlKk> list) throws SQLException {
		return txjlKkDao.insertList(list);
	}

	/**
	 * 
	 * @方法名：getTxjlTkByZjhmAndKkId
	 * @功能说明：根据卡口验证通行方向
	 * @author 娄高伟
	 * @date 2013-10-23 上午10:56:06
	 * @param zjhm
	 * @param txkkid
	 * @return
	 * @throws SQLException
	 */
	public TxjlKk getTxjlKkByZjhmAndKkId(String zjhm, String txkkid) throws SQLException {
		return txjlKkDao.getTxjlKkByZjhmAndKkId(zjhm, txkkid);
	}

	/**
	 * 
	 * @方法名：delete
	 * @功能说明：删除一条数据
	 * @author liums
	 * @date 2013-9-23 下午5:32:58
	 * @param mtdm
	 * @return
	 * @throws SQLException
	 */
	public int delete(TxjlKk mtdm) throws SQLException {
		return txjlKkDao.delete(mtdm);
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
	public int update(TxjlKk mtdm) throws SQLException {
		return txjlKkDao.update(mtdm);
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
	public List<TxjlKk> findAll() throws SQLException {
		return txjlKkDao.findAll();
	}

	public int deleteAll() throws SQLException {
		return txjlKkDao.deleteAll();
	}

	public void updateTxjlKkTxfx(String zjhm, String txkkid, String txfx) throws SQLException {
		txjlKkDao.updateTxjlKkTxfx(zjhm, txkkid, txfx);
	}

	public void deleteByZjhmAndKkid(String zjhm, String kkID) {
		try {
			txjlKkDao.deleteByZjhmAndKkid(zjhm, kkID);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
