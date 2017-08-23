package com.pingtech.hgqw.module.offline.base.utils;

import com.pingtech.hgqw.module.offline.bwdm.service.BwdmService;
import com.pingtech.hgqw.module.offline.cyxx.service.CyxxService;
import com.pingtech.hgqw.module.offline.fwxcb.service.FwxcbService;
import com.pingtech.hgqw.module.offline.hgzjxx.service.HgzjxxService;
import com.pingtech.hgqw.module.offline.kacbqk.service.KacbqkService;
import com.pingtech.hgqw.module.offline.mtdm.service.MtdmService;
import com.pingtech.hgqw.module.offline.offdata.service.OffDataService;
import com.pingtech.hgqw.module.offline.pzxx.service.PzxxService;
import com.pingtech.hgqw.module.offline.qyxx.service.QyxxService;
import com.pingtech.hgqw.module.offline.sbxx.service.SbxxService;
import com.pingtech.hgqw.module.offline.scsb.service.ScsbService;
import com.pingtech.hgqw.module.offline.sxtgl.service.SxtglService;
import com.pingtech.hgqw.module.offline.txjl.service.DkqkService;
import com.pingtech.hgqw.module.offline.txjl.service.TxjlKkService;
import com.pingtech.hgqw.module.offline.txjl.service.TxjlTkService;
import com.pingtech.hgqw.module.offline.userinfo.service.TBUserinfoService;

public class ServiceManager {
	public static ServiceManager serviceManager = null;

	private KacbqkService kacaqkDao = null;

	private HgzjxxService hgzjxxDao = null;

	private BwdmService bwdmDao = null;
	private CyxxService cyxxDao = null;
	private PzxxService pzxxDao = null;
	private MtdmService mtdmDao = null;
	private QyxxService qyxxDao = null;
	private FwxcbService fwxcbDao = null;
	private OffDataService offDataDao = null;
	private SbxxService sbxxDao = null;
	private ScsbService scsbDao = null;
	private SxtglService sxtglDao = null;
	private DkqkService dkqkDao = null;
	private TxjlKkService txjlKkDao = null;
	private TxjlTkService txjlTkDao = null;
	private TBUserinfoService userinfoDao = null;

	public static ServiceManager getDaoManager() {
		if (serviceManager == null) {
			serviceManager = new ServiceManager();
		}
		return serviceManager;
	}


}
