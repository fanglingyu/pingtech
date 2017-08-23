package com.pingtech.hgqw.module.offline.userinfo.service;

import java.sql.SQLException;
import java.util.List;

import com.pingtech.hgqw.module.offline.base.service.BaseHgqwService;
import com.pingtech.hgqw.module.offline.userinfo.dao.TBUserinfoDao;
import com.pingtech.hgqw.module.offline.userinfo.entity.TBUserinfo;

public class TBUserinfoService extends BaseHgqwService {
	private static final String TAG = "TBUserinfoService";
	private TBUserinfoDao tbuserinfoDao = null;

	public TBUserinfoService() {
		this.tbuserinfoDao = new TBUserinfoDao();
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
	public int insert(TBUserinfo tbUser) throws SQLException {
		return tbuserinfoDao.insert(tbUser);
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
	public int insertList(List<TBUserinfo> list) throws SQLException {
		return tbuserinfoDao.insertList(list);
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
	public int delete(TBUserinfo tbUser) throws SQLException {
		return tbuserinfoDao.delete(tbUser);
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
	public int update(TBUserinfo tbUser) throws SQLException {
		return tbuserinfoDao.update(tbUser);
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
	public List<TBUserinfo> findAll() throws SQLException {
		return tbuserinfoDao.findAll();
	}
	/**
	 * 
	 * @方法名：deleteAll
	 * @功能说明：删除所有人员数据
	 * @author 娄高伟
	 * @date  2013-10-17 下午7:52:51
	 * @return
	 * @throws SQLException
	 */
	public int deleteAll() throws SQLException {
		return  tbuserinfoDao.deleteAll();
	}

	/**
	 * 
	 * @方法名：gainUserByZjhm
	 * @功能说明：根据士兵卡号查询人员
	 * @author 娄高伟
	 * @date  2013-10-17 下午7:48:44
	 * @param zjhm
	 * @return
	 * @throws SQLException
	 */
	public TBUserinfo gainUserByZjhm(String zjhm) throws SQLException {
		return tbuserinfoDao.gainUserByZjhm(zjhm);
	}

}
