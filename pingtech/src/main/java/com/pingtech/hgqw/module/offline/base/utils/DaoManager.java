package com.pingtech.hgqw.module.offline.base.utils;

import com.j256.ormlite.dao.Dao;
import com.pingtech.hgqw.module.offline.bwdm.entity.Bwdm;
import com.pingtech.hgqw.module.offline.cyxx.entity.TBCyxx;
import com.pingtech.hgqw.module.offline.fwxcb.entity.Fwxcb;
import com.pingtech.hgqw.module.offline.hgzjxx.entity.Hgzjxx;
import com.pingtech.hgqw.module.offline.kacbqk.entity.Kacbqk;
import com.pingtech.hgqw.module.offline.mtdm.entity.Mtdm;
import com.pingtech.hgqw.module.offline.offdata.entity.OffData;
import com.pingtech.hgqw.module.offline.pzxx.entity.Pzxx;
import com.pingtech.hgqw.module.offline.qyxx.entity.Qyxx;
import com.pingtech.hgqw.module.offline.sbxx.entity.Sbxx;
import com.pingtech.hgqw.module.offline.scsb.entity.Scsb;
import com.pingtech.hgqw.module.offline.sxtgl.entity.Sxtgl;
import com.pingtech.hgqw.module.offline.txjl.entity.Dkqk;
import com.pingtech.hgqw.module.offline.txjl.entity.TxjlKk;
import com.pingtech.hgqw.module.offline.txjl.entity.TxjlTk;
import com.pingtech.hgqw.module.offline.userinfo.entity.TBUserinfo;

public class DaoManager {
	public static DaoManager daoManager = null;

	private Dao<Kacbqk, Integer> kacaqkDao = null;

	private Dao<Hgzjxx, Integer> hgzjxxDao = null;

	private Dao<Bwdm, Integer> bwdmDao = null;
	private Dao<TBCyxx, Integer> cyxxDao = null;
	private Dao<Pzxx, Integer> pzxxDao = null;
	private Dao<Mtdm, Integer> mtdmDao = null;
	private Dao<Qyxx, Integer> qyxxDao = null;
	private Dao<Fwxcb, Integer> fwxcbDao = null;
	private Dao<OffData, Integer> offDataDao = null;
	private Dao<Sbxx, Integer> sbxxDao = null;
	private Dao<Scsb, Integer> scsbDao = null;
	private Dao<Sxtgl, Integer> sxtglDao = null;
	private Dao<Dkqk, Integer> dkqkDao = null;
	private Dao<TxjlKk, Integer> txjlKkDao = null;
	private Dao<TxjlTk, Integer> txjlTkDao = null;
	private Dao<TBUserinfo, Integer> userinfoDao = null;

	private DaoManager() {

	}

	public static DaoManager getDaoManager() {
		if (daoManager == null) {
			daoManager = new DaoManager();
		}
		return daoManager;
	}

	public Dao<Kacbqk, Integer> getKacaqkDao() {
		return kacaqkDao;
	}

	public void setKacaqkDao(Dao<Kacbqk, Integer> kacaqkDao) {
		this.kacaqkDao = kacaqkDao;
	}

	public Dao<Hgzjxx, Integer> getHgzjxxDao() {
		return hgzjxxDao;
	}

	public void setHgzjxxDao(Dao<Hgzjxx, Integer> hgzjxxDao) {
		this.hgzjxxDao = hgzjxxDao;
	}

	public Dao<Bwdm, Integer> getBwdmDao() {
		return bwdmDao;
	}

	public void setBwdmDao(Dao<Bwdm, Integer> bwdmDao) {
		this.bwdmDao = bwdmDao;
	}

	public Dao<TBCyxx, Integer> getCyxxDao() {
		return cyxxDao;
	}

	public void setCyxxDao(Dao<TBCyxx, Integer> cyxxDao) {
		this.cyxxDao = cyxxDao;
	}

	public Dao<Mtdm, Integer> getMtdmDao() {
		return mtdmDao;
	}

	public void setMtdmDao(Dao<Mtdm, Integer> mtdmDao) {
		this.mtdmDao = mtdmDao;
	}

	public Dao<Qyxx, Integer> getQyxxDao() {
		return qyxxDao;
	}

	public void setQyxxDao(Dao<Qyxx, Integer> qyxxDao) {
		this.qyxxDao = qyxxDao;
	}

	public Dao<Fwxcb, Integer> getFwxcbDao() {
		return fwxcbDao;
	}

	public void setFwxcbDao(Dao<Fwxcb, Integer> fwxcbDao) {
		this.fwxcbDao = fwxcbDao;
	}

	public Dao<OffData, Integer> getOffDataDao() {
		return offDataDao;
	}

	public void setOffDataDao(Dao<OffData, Integer> offDataDao) {
		this.offDataDao = offDataDao;
	}

	public Dao<Sbxx, Integer> getSbxxDao() {
		return sbxxDao;
	}

	public void setSbxxDao(Dao<Sbxx, Integer> sbxxDao) {
		this.sbxxDao = sbxxDao;
	}

	public Dao<Scsb, Integer> getScsbDao() {
		return scsbDao;
	}

	public void setScsbDao(Dao<Scsb, Integer> scsbDao) {
		this.scsbDao = scsbDao;
	}

	public Dao<Sxtgl, Integer> getSxtglDao() {
		return sxtglDao;
	}

	public void setSxtglDao(Dao<Sxtgl, Integer> sxtglDao) {
		this.sxtglDao = sxtglDao;
	}

	public Dao<Dkqk, Integer> getDkqkDao() {
		return dkqkDao;
	}

	public void setDkqkDao(Dao<Dkqk, Integer> dkqkDao) {
		this.dkqkDao = dkqkDao;
	}

	public Dao<TxjlKk, Integer> getTxjlKkDao() {
		return txjlKkDao;
	}

	public void setTxjlKkDao(Dao<TxjlKk, Integer> txjlKkDao) {
		this.txjlKkDao = txjlKkDao;
	}

	public Dao<TxjlTk, Integer> getTxjlTkDao() {
		return txjlTkDao;
	}

	public void setTxjlTkDao(Dao<TxjlTk, Integer> txjlTkDao) {
		this.txjlTkDao = txjlTkDao;
	}

	public Dao<TBUserinfo, Integer> getUserinfoDao() {
		return userinfoDao;
	}

	public void setUserinfoDao(Dao<TBUserinfo, Integer> userinfoDao) {
		this.userinfoDao = userinfoDao;
	}

	public Dao<Pzxx, Integer> getPzxxDao() {
		return pzxxDao;
	}

	public void setPzxxDao(Dao<Pzxx, Integer> pzxxDao) {
		this.pzxxDao = pzxxDao;
	}

}
