package com.pingtech.hgqw.module.offline.bwdm.utils;

import com.pingtech.hgqw.module.offline.bwdm.entity.Bwdm;
import com.pingtech.hgqw.module.offline.bwdm.service.BwdmService;

public class BwdmUtil {

	public static String getBwdmByBwid(String bwid) {
		Bwdm bwdm = new BwdmService().getBwdmByBwid(bwid);
		if(bwdm!=null){
			return bwdm.getBwdm();
		}
		return null;
	}

}
