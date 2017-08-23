package com.pingtech.hgqw.module.offline.qyxx.service;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.pingtech.hgqw.module.offline.base.dao.BaseHgqwDao;
import com.pingtech.hgqw.module.offline.qyxx.dao.QyxxDao;
import com.pingtech.hgqw.module.offline.qyxx.entity.Qyxx;

public class QyxxService extends BaseHgqwDao<Qyxx> {
	private static final String TAG = "QyxxService";

	private QyxxDao qyxxDao = null;

	public QyxxService() {
		this.qyxxDao = new QyxxDao();
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
	public int insert(Qyxx qyxx) throws SQLException {
		return qyxxDao.insert(qyxx);
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
	public int insertList(List<Qyxx> list) throws SQLException {
		return qyxxDao.insertList(list);
	}

	/**
	 * 
	 * @方法名：byIdGetQyxx
	 * @功能说明：通过 ID得到区域信息
	 * @author 赵琳
	 * @date 2013-10-11 下午2:31:31
	 * @param qykd
	 * @return
	 */
	public Qyxx byIdGetQyxx(String qykd) throws SQLException {
		return qyxxDao.byIdGetQyxx(qykd);

	}

	public int deleteAll() throws SQLException {
		return qyxxDao.deleteAll();
	}

	public List<Map<String, String>> findQyxxByPrams(Map<String, Object> mapParms) throws SQLException {
		return qyxxDao.findQyxxByPrams(mapParms);
	}

	public Qyxx getQyxxByQyid(String id) {
		try {
			return qyxxDao.getQyxxByQyid(id);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public String getQymcByQyid(String id) {
		String qymc = "";
		try {
			Qyxx qyxx = qyxxDao.getQyxxByQyid(id);
			if (qyxx == null) {
				return "";
			}
			qymc = qyxx.getQymc();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return qymc;
	}

}
