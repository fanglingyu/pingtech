package com.pingtech.hgqw.module.licence;

import android.os.Handler;

import com.pingtech.hgqw.module.licence.service.LicenceService;
import com.pingtech.hgqw.module.licence.utils.LicenceUtils;

/**
 * 
 * 
 * 类描述：设备及软件的授权认证
 * 
 * <p>
 * Title: 海江港边检勤务综合管理系统-Licence.java
 * </p>
 * <p>
 * Copyright: Copyright (c) 2012
 * </p>
 * <p>
 * Company: 品恩科技
 * </p>
 * 
 * @author liums
 * @version 1.0
 * @date 2013-9-25 下午12:12:43
 */
public class Licence {
	private Handler handler;

	public Licence(Handler handler) {
		this.handler = handler;
	}

	/**
	 * 
	 * @方法名：provePdaCode
	 * @功能说明：验证警务通是否授权
	 * @author liums
	 * @date 2013-11-5 下午5:35:11
	 * @param pdaCode
	 */
	public void provePdaCode(String pdaCode) {
		// 本地有licence文件，直接验证
		if (!LicenceUtils.hasLicenceFile()) {
			//下载文件
			LicenceService licenceService = new LicenceService(handler);
			return;
		}
		//文件存在验证版本号
		String versionCode = LicenceUtils.getVersionCode();
		
	}

	
}
