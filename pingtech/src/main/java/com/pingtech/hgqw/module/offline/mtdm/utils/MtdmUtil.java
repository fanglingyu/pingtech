package com.pingtech.hgqw.module.offline.mtdm.utils;

import com.pingtech.hgqw.module.offline.mtdm.entity.Mtdm;
import com.pingtech.hgqw.module.offline.mtdm.service.MtdmService;

public class MtdmUtil {

	public static String getMtdmByMtid(String mtid) {
		Mtdm mtdm = new MtdmService().getMtdmByMtid(mtid);
		if (mtdm != null) {
			return mtdm.getMtdm();
		}
		return null;
	}

}
