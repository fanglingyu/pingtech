package com.pingtech.hgqw.module.offline.pzxx.service;

import java.sql.SQLException;
import java.util.ArrayList;

import com.pingtech.hgqw.module.offline.base.service.BaseHgqwService;
import com.pingtech.hgqw.module.offline.pzxx.dao.PzxxDao;
import com.pingtech.hgqw.module.offline.pzxx.entity.Pzxx;

public class PzxxService extends BaseHgqwService {
	PzxxDao pzxxDao = null;
	public PzxxService(){
		this.pzxxDao = new PzxxDao();
	}
	
	
	public void insertList(ArrayList<Pzxx> pzxxs) throws SQLException {
		pzxxDao.insertList(pzxxs);
	}

	public Pzxx getPzxxByZjhm(String zjhm) throws SQLException {
		return pzxxDao.getPzxxByZjhm(zjhm);
	}


	public int deleteAll() throws SQLException {
		return pzxxDao.deleteAll();
	}
	
}
