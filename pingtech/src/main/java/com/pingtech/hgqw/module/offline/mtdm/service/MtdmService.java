package com.pingtech.hgqw.module.offline.mtdm.service;

import java.sql.SQLException;
import java.util.List;

import com.pingtech.hgqw.module.offline.base.service.BaseHgqwService;
import com.pingtech.hgqw.module.offline.mtdm.dao.MtdmDao;
import com.pingtech.hgqw.module.offline.mtdm.entity.Mtdm;

public class MtdmService extends BaseHgqwService {
	private static final String TAG = "MtdmService";

	private MtdmDao mtdmDao = null;

	public MtdmService() {
		this.mtdmDao = new MtdmDao();
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
	public int insert(Mtdm mtdm) throws SQLException {
		return mtdmDao.insert(mtdm);
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
	public int insertList(List<Mtdm> list) throws SQLException {
		return mtdmDao.insertList(list);
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
	public int delete(Mtdm mtdm) throws SQLException {
		return mtdmDao.delete(mtdm);
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
	public int update(Mtdm mtdm) throws SQLException {
		return mtdmDao.update(mtdm);
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
	public List<Mtdm> findAll() throws SQLException {
		return mtdmDao.findAll();
	}

	public int deleteAll() throws SQLException {
		return mtdmDao.deleteAll();
	}

	/**
	 * 
	 * @方法名：getMtmcByMtdm
	 * @功能说明：根据码头代码获取码头名称
	 * @author liums
	 * @date 2013-11-29 下午1:41:04
	 * @param tkmtDm
	 * @return
	 * @throws SQLException
	 */
	public Mtdm getMtmcByMtdm(String tkmtDm) throws SQLException {
		return mtdmDao.getMtmcByMtdm(tkmtDm);
	}

	public String getMtmcStrByMtdm(String tkmtDm) throws SQLException {
		Mtdm mtdm = mtdmDao.getMtmcByMtdm(tkmtDm);
		if (mtdm == null) {
			return "";
		}
		return mtdm.getMtmc();
	}

	/**
	 * 
	 * @方法名：getMtdmByMtid
	 * @功能说明：根据码头ID获取码头
	 * @author liums
	 * @date 2013-12-6 下午2:26:12
	 * @param id
	 */
	public Mtdm getMtdmByMtid(String id) {
		try {
			return mtdmDao.getMtdmByMtid(id);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

}
