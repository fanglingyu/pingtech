package com.pingtech.hgqw.module.offline.base.utils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import com.pingtech.hgqw.entity.Flags;
import com.pingtech.hgqw.entity.GlobalFlags;
import com.pingtech.hgqw.module.offline.cyxx.service.CyxxService;
import com.pingtech.hgqw.module.offline.hgzjxx.service.HgzjxxService;
import com.pingtech.hgqw.module.offline.kacbqk.service.KacbqkService;
import com.pingtech.hgqw.module.offline.txjl.service.DkqkService;
import com.pingtech.hgqw.module.offline.txjl.service.TxjlKkService;
import com.pingtech.hgqw.module.offline.txjl.service.TxjlTkService;
import com.pingtech.hgqw.module.xunjian.utils.XcUtil;
import com.pingtech.hgqw.utils.SystemSetting;

public class DbUtil {

	public static void deleteKacbqk() {
		try {
			new KacbqkService().deleteAll();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void deleteHgzjxx() {
		try {
			new HgzjxxService().deleteAll();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void deleteHgzjxxByHc(String hc) {
		try {
			new HgzjxxService().deleteByHc(hc);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void deleteCyxx() {
		try {
			new CyxxService().deleteAll();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public static void deleteKacbqkByHc(String hc) {
		try {
			new KacbqkService().deleteKacbqkByHc(hc);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void deleteCyxxByCbid(String kacbqkid) {
		try {
			new CyxxService().deleteCyxxByCbid(kacbqkid);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void deleteCyxxByHc() {
		HashMap<String, Object> bindData = SystemSetting.getBindShip(GlobalFlags.LIST_TYPE_FROM_TIKOUMANAGER + "");
		HashMap<String, Object> bindDataXj = SystemSetting.getBindShip(GlobalFlags.LIST_TYPE_FROM_XUNCHAXUNJIAN + "");
		if (bindData != null) {
			String kacbqkid = (String) bindData.get("kacbqkid");
			DbUtil.deleteCyxxByCbid(kacbqkid);
		}
		if (bindDataXj != null) {
			String kacbqkid = (String) bindDataXj.get("kacbqkid");
			DbUtil.deleteCyxxByCbid(kacbqkid);
		}
	}

	public static void deleteTxjlForTiKou() {
		try {
			DkqkService dkqkService = new DkqkService();
			dkqkService.deleteAll();

			TxjlTkService txjlTkService = new TxjlTkService();
			txjlTkService.deleteAll();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void deleteForTikou() {
		HashMap<String, Object> bindData = SystemSetting.getBindShip(GlobalFlags.LIST_TYPE_FROM_TIKOUMANAGER + "");
		if (bindData != null) {
			String hc = (String) bindData.get("hc");
			String kacbqkid = (String) bindData.get("kacbqkid");
			DbUtil.deleteKacbqkByHc(hc);
			DbUtil.deleteHgzjxxByHc(hc);
			DbUtil.deleteCyxxByCbid(kacbqkid);
			// 删除梯口通行记录
			DbUtil.deleteTxjlForTiKou();
		}
	}

	public static void deleteForTikouAndKakou() {
		/*HashMap<String, Object> bindData = SystemSetting.getBindShip(GlobalFlags.LIST_TYPE_FROM_TIKOUMANAGER + "");
		String hc = "";
		if (bindData != null) {
			hc = (String) bindData.get("hc");
			DbUtil.deleteHgzjxxByHc(hc);// 删除梯口内船舶关联的证件
		}*/
		DbUtil.deleteHgzjxxHasHc();
		DbUtil.deleteKacbqk();// 删除所有船舶
		DbUtil.deleteCyxx();// 删除所有船员
		// 删除通行记录表
		// DbUtil.deleteTxjlForTiKou();
		// DbUtil.deleteTxjlForKakou();
	}

	private static void deleteHgzjxxHasHc() {
		new HgzjxxService().deleteHgzjxxWhichHasHc();
	}

	public static void deleteTxjlForKakou() {
		TxjlKkService txjlKkService = new TxjlKkService();
		try {
			txjlKkService.deleteAll();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @方法名：deleteForXjdd
	 * @功能说明：巡查版地点解绑删除数据库文件
	 * @author liums
	 * @date 2014-5-16 下午4:36:50
	 */
	public static void deleteForXjdd() {
		DbUtil.deleteKacbqk();// 删除所有船舶
		DbUtil.deleteCyxx();// 删除所有船员
		DbUtil.deleteHgzjxxWhichHasHc();
	}

	public static void deleteHgzjxxForXjdd() {
		// DbUtil.deleteKacbqk();// 删除所有船舶
		// DbUtil.deleteCyxx();// 删除所有船员
		DbUtil.deleteHgzjxxWhichHasHc();
	}

	public static void deleteKacbqkForXjdd() {
		DbUtil.deleteKacbqk();// 删除所有船舶
		// DbUtil.deleteCyxx();// 删除所有船员
		// DbUtil.deleteHgzjxxWhichHasHc();
	}

	public static void deleteCyxxForXjdd() {
		// DbUtil.deleteKacbqk();// 删除所有船舶
		DbUtil.deleteCyxx();// 删除所有船员
		// DbUtil.deleteHgzjxxWhichHasHc();
	}

	/**
	 * 
	 * @方法名：deleteHgzjxxWhichHasHc
	 * @功能说明：删除证件表中所有的临时证
	 * @author liums
	 * @date 2014-5-16 下午4:38:50
	 */
	private static void deleteHgzjxxWhichHasHc() {
		new HgzjxxService().deleteHgzjxxWhichHasHc();
	}

	/**
	 * 
	 * @方法名：deleteOfflineData
	 * @功能说明：哨兵版继承绑定会调用
	 * @author liums
	 * @date 2014-5-15 下午4:07:48
	 * @param httprequestTypeForExtendsBind
	 * @param kaKouBinddata
	 * @param bindDataForTiKou
	 * @param mapsForKk
	 */
	public static void deleteOfflineData(int httprequestTypeForExtendsBind, HashMap<String, Object> kaKouBinddata,
			HashMap<String, Object> bindDataForTiKou, ArrayList<HashMap<String, Object>> mapsForKk) {
		switch (Flags.PDA_VERSION) {
		case Flags.PDA_VERSION_SENTINEL:
			shaobingBusiness(httprequestTypeForExtendsBind, bindDataForTiKou, mapsForKk);
			break;
		case Flags.PDA_VERSION_DEFAULT:
			xunchaBusiness(httprequestTypeForExtendsBind);
			break;

		default:
			break;
		}
	}

	private static void xunchaBusiness(int httprequestTypeForExtendsBind) {
		switch (httprequestTypeForExtendsBind) {
		case 12://继承
			//如果未选择，已经删除相关数据，不用处理。选择的话则本地数据不变。
//			XcUtil.deleteBindInfo();
			break;
		case 13://取消继承
			XcUtil.deleteBindInfo();
			break;

		default:
			break;
		}
	}

	private static void shaobingBusiness(int httprequestTypeForExtendsBind, HashMap<String, Object> bindDataForTiKou,
			ArrayList<HashMap<String, Object>> mapsForKk) {
		if (13 == httprequestTypeForExtendsBind) {
			deleteAllCyxxAndKacb();
		} else if (12 == httprequestTypeForExtendsBind) {
			HashMap<String, Object> kaKouBindNow = SystemSetting.getBindShip(GlobalFlags.LIST_TYPE_FROM_KAKOUMANAGER + "");
			HashMap<String, Object> tiKoubindNow = SystemSetting.getBindShip(GlobalFlags.LIST_TYPE_FROM_TIKOUMANAGER + "");
			// 梯口、卡口都已经解绑
			if (kaKouBindNow == null && tiKoubindNow == null) {
				deleteAllCyxxAndKacb();
			} else if (kaKouBindNow == null && tiKoubindNow != null) {// 卡口解绑，梯口未解绑
				String kacbqkid = "";
				if (bindDataForTiKou != null) {
					kacbqkid = (String) bindDataForTiKou.get("kacbqkid");
				}
				String hcForKk = "";
				String kacbqkidForKk = "";
				if (mapsForKk != null && mapsForKk.size() > 0) {
					for (HashMap<String, Object> hashMap : mapsForKk) {
						hcForKk = (String) hashMap.get("hc");
						kacbqkidForKk = (String) hashMap.get("kacbqkid");
						if (kacbqkidForKk != null && !kacbqkidForKk.equals(kacbqkid)) {
							DbUtil.deleteKacbqkByHc(hcForKk);
							DbUtil.deleteHgzjxxByHc(hcForKk);
							DbUtil.deleteCyxxByCbid(kacbqkidForKk);
						}
					}
				}

				// DbUtil.deleteTxjlForKakou();// 删除卡口通行记录

			} else if (kaKouBindNow != null && tiKoubindNow == null) {
				String hc = "";
				String kacbqkid = "";
				if (bindDataForTiKou != null) {
					hc = (String) bindDataForTiKou.get("hc");
					kacbqkid = (String) bindDataForTiKou.get("kacbqkid");
				}
				boolean isInKk = false;
				if (mapsForKk != null && mapsForKk.size() > 0) {// 卡口没有解绑,并且卡口内有船舶
					for (HashMap<String, Object> hashMap : mapsForKk) {
						String hcTemp = (String) hashMap.get("hc");
						if (hcTemp != null && hcTemp.equals(hc)) {
							isInKk = true;
							break;
						}
					}
				}

				if (!isInKk) {
					DbUtil.deleteKacbqkByHc(hc);
					DbUtil.deleteHgzjxxByHc(hc);
					DbUtil.deleteCyxxByCbid(kacbqkid);
					// 删除梯口通行记录
					// DbUtil.deleteTxjlForTiKou();
				}
			}
		}
	}

	private static void deleteAllCyxxAndKacb() {
		DbUtil.deleteForTikouAndKakou();
	}

	public static void deletdHgzjxxForKakou() {
		ArrayList<HashMap<String, Object>> mapsForKk = SystemSetting.getShipOfKK();
		HashMap<String, Object> kaKouBinddata = SystemSetting.getBindShip(GlobalFlags.LIST_TYPE_FROM_KAKOUMANAGER + "");
		HashMap<String, Object> bindDataForTiKou = SystemSetting.getBindShip(GlobalFlags.LIST_TYPE_FROM_TIKOUMANAGER + "");
		String hcForKk = "";
		String kacbqkidForKk = "";
		String kacbqkidForTiKou = "";
		String hcForTiKou = "";
		if (bindDataForTiKou != null) {// 如果梯口没有解绑
			hcForTiKou = (String) bindDataForTiKou.get("hc");
			kacbqkidForTiKou = (String) bindDataForTiKou.get("kacbqkid");
		}

		if (kaKouBinddata != null) {// 卡口已经解绑
			if (mapsForKk != null && mapsForKk.size() > 0) {
				for (HashMap<String, Object> hashMap : mapsForKk) {
					hcForKk = (String) hashMap.get("hc");
					kacbqkidForKk = (String) hashMap.get("kacbqkid");
					if (kacbqkidForKk != null && !kacbqkidForKk.equals(kacbqkidForTiKou)) {
						// DbUtil.deleteKacbqkByHc(hcForKk);
						DbUtil.deleteHgzjxxByHc(hcForKk);
						// DbUtil.deleteCyxxByCbid(kacbqkidForKk);
					}
				}
			}
		}
	}

	public static void deletdKacbqkForKakou() {
		ArrayList<HashMap<String, Object>> mapsForKk = SystemSetting.getShipOfKK();
		HashMap<String, Object> kaKouBinddata = SystemSetting.getBindShip(GlobalFlags.LIST_TYPE_FROM_KAKOUMANAGER + "");
		HashMap<String, Object> bindDataForTiKou = SystemSetting.getBindShip(GlobalFlags.LIST_TYPE_FROM_TIKOUMANAGER + "");
		String hcForKk = "";
		String kacbqkidForKk = "";
		String kacbqkidForTiKou = "";
		String hcForTiKou = "";
		if (bindDataForTiKou != null) {// 如果梯口没有解绑
			hcForTiKou = (String) bindDataForTiKou.get("hc");
			kacbqkidForTiKou = (String) bindDataForTiKou.get("kacbqkid");
		}

		if (kaKouBinddata != null) {// 卡口已经解绑
			if (mapsForKk != null && mapsForKk.size() > 0) {
				for (HashMap<String, Object> hashMap : mapsForKk) {
					hcForKk = (String) hashMap.get("hc");
					kacbqkidForKk = (String) hashMap.get("kacbqkid");
					if (kacbqkidForKk != null && !kacbqkidForKk.equals(kacbqkidForTiKou)) {
						DbUtil.deleteKacbqkByHc(hcForKk);
						// DbUtil.deleteHgzjxxByHc(hcForKk);
						// DbUtil.deleteCyxxByCbid(kacbqkidForKk);
					}
				}
			}
		}
	}

	public static void deletdCyxxForKakou() {
		ArrayList<HashMap<String, Object>> mapsForKk = SystemSetting.getShipOfKK();
		HashMap<String, Object> kaKouBinddata = SystemSetting.getBindShip(GlobalFlags.LIST_TYPE_FROM_KAKOUMANAGER + "");
		HashMap<String, Object> bindDataForTiKou = SystemSetting.getBindShip(GlobalFlags.LIST_TYPE_FROM_TIKOUMANAGER + "");
		String hcForKk = "";
		String kacbqkidForKk = "";
		String kacbqkidForTiKou = "";
		String hcForTiKou = "";
		if (bindDataForTiKou != null) {// 如果梯口没有解绑
			hcForTiKou = (String) bindDataForTiKou.get("hc");
			kacbqkidForTiKou = (String) bindDataForTiKou.get("kacbqkid");
		}

		if (kaKouBinddata != null) {// 卡口已经解绑
			if (mapsForKk != null && mapsForKk.size() > 0) {
				for (HashMap<String, Object> hashMap : mapsForKk) {
					hcForKk = (String) hashMap.get("hc");
					kacbqkidForKk = (String) hashMap.get("kacbqkid");
					if (kacbqkidForKk != null && !kacbqkidForKk.equals(kacbqkidForTiKou)) {
						// DbUtil.deleteKacbqkByHc(hcForKk);
						// DbUtil.deleteHgzjxxByHc(hcForKk);
						DbUtil.deleteCyxxByCbid(kacbqkidForKk);
					}
				}
			}
		}
	}
}
