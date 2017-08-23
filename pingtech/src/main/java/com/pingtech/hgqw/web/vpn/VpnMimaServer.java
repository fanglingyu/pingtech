package com.pingtech.hgqw.web.vpn;

import com.pingtech.hgqw.utils.Log;


/** VPN服务类，详细参考安全客户端说明文档 */
public class VpnMimaServer {

	public static int start() {
		byte[] VPNState = new byte[100];
		int VPNStateLen = 0;
		// 连接安全客户端
		// VPNSocket类是一个简单的socket类
		Log.i("VpnServer", "VpnServer start");
		VpnMimaSocket vpnCS = new VpnMimaSocket("127.0.0.1", 3001);
		int nres = vpnCS.connect();
		Log.i("VpnServer", "vpnCS connect nres:" + nres);
		Long ctime = System.currentTimeMillis();
		while (nres != 0) {
			if (System.currentTimeMillis() - ctime > 20000) {
				Log.i("VpnServer", "vpnCS connect timerout");
				return VpnManager.START_VPNSERVICE_RESULT_CONNECT_TIMEOUT;
			}
			nres = vpnCS.connect();
		}
		Log.i("VpnServer", "vpnCS connect success");
		// 启动安全客户端
		vpnCS.sendData("VPNSTART"); // 发送启动命令
		VPNStateLen = vpnCS.recvData(VPNState);
		Log.i("VpnServer", "vpnCS recvData success:" + VPNStateLen);
		// 通过判断返回的状态判断是否启动安全客户端成功
		String state = null;
		state = new String(VPNState, 0, VPNStateLen);
		Log.i("VpnServer", "vpnCS recvData state:" + state);
		if (!state.startsWith("OK")) {
			return VpnManager.START_VPNSERVICE_RESULT_START_FAILED;
		}

		// 循环发送getstatus命令检测安全通道是否已经建立，如果出错或者超//时将返回对应错误
		ctime = System.currentTimeMillis();
		while (!state.startsWith("OK 100")) {
			if (state.startsWith("FAILED")) {
				Log.i("VpnServer", "vpnCS GETSTATUS failed:" + state);
				return VpnManager.START_VPNSERVICE_RESULT_CONNECT_FAILED;
			}

			if (System.currentTimeMillis() - ctime > 20000) {
				Log.i("VpnServer", "vpnCS GETSTATUS timerout");
				return VpnManager.START_VPNSERVICE_RESULT_START_TIMEOUT;
			}
			vpnCS.sendData("GETSTATUS");
			VPNStateLen = vpnCS.recvData(VPNState);
			Log.i("VpnServer", "vpnCS recvData(GETSTATUS) success:" + VPNStateLen);
			state = new String(VPNState, 0, VPNStateLen);
			Log.i("VpnServer", "vpnCS recvData(GETSTATUS) state:" + state);
		}
		Log.i("VpnServer", "vpnCS success!!!!");
		return VpnManager.START_VPNSERVICE_RESULT_SUCCESS;
	}
}
