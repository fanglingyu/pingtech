package com.pingtech.hgqw.utils;

import java.io.ByteArrayInputStream;

import org.kobjects.base64.Base64;
import org.xmlpull.v1.XmlPullParser;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Xml;

import com.pingtech.R;
import com.pingtech.hgqw.entity.Flags;
import com.pingtech.hgqw.entity.UpdataInfo;
import com.pingtech.hgqw.widget.HgqwToast;

/**
 * 软件更新类
 * 
 * @author lougw
 * 
 */
public class UpdataVersionManager {
	private static final String TAG = "UpdataVersionManager";

	/** 解析版本信息文件 ：获取版本号、获取apk文件路径、获取版本详情 */
	private UpdataInfo getUpdateInfo(byte[] buf, int offset, int len) throws Exception {
		XmlPullParser parser = Xml.newPullParser();
		parser.setInput(new ByteArrayInputStream(buf, offset, len), "utf-8");// 设置解析的数据源
		int type = parser.getEventType();
		UpdataInfo info = new UpdataInfo();// 实体
		while (type != XmlPullParser.END_DOCUMENT) {
			switch (type) {
			case XmlPullParser.START_TAG:
				if ("versionCode".equals(parser.getName())) {
					info.setVersionCode(Integer.parseInt(parser.nextText()));
				} else if ("version".equals(parser.getName())) {
					info.setVersion(parser.nextText());
				} else if ("url".equals(parser.getName())) {
					info.setUrl(parser.nextText());
				} else if ("size".equals(parser.getName())) {
					info.setSize(Integer.parseInt(parser.nextText()));
				} else if ("description".equals(parser.getName())) {
					info.setDescription(parser.nextText());
				} else if ("force".equals(parser.getName())) {
					info.setForce(parser.nextText());
				} else if ("forceInfo".equals(parser.getName())) {
					info.setForceInfo(parser.nextText());
				}
				break;
			}
			type = parser.next();
		}
		return info;
	}

	/**
	 * 版本更新回调更新
	 * 
	 * @param str
	 * @param progressDialog
	 * @param context
	 * @param intent
	 * @return
	 */
	public UpdataInfo updateVersion(String str, Context context, Intent intent, int upfateTag) {
		UpdataInfo info = null;
		try {
			int offset = 0;
			byte[] buf = Base64.decode(str);
			if (buf.length >= 3) {
				if (buf[0] == (byte) 0xEF && buf[1] == (byte) 0xBB && buf[2] == (byte) 0xBF) {
					offset = 3;
				}
			}
			info = getUpdateInfo(buf, offset, buf.length - offset);
			Log.i(TAG, "NetWork SW Version:" + info.getVersion() + " versionCode: " + info.getVersionCode());
			if (info.getVersionCode() > 0) {
				PackageManager packageManager = context.getPackageManager();
				int versionCode = 1;
				PackageInfo packInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
				versionCode = packInfo.versionCode;
				Log.i(TAG, "packInfo:" + versionCode);
				if (versionCode < info.getVersionCode()) {
					intent.putExtra("Description", info.getDescription());
					intent.putExtra("Size", info.getSize());
					intent.putExtra("Url", info.getUrl());
					// 版本：0默认版本，1哨兵版，2船方自管
					intent.putExtra("version", Flags.PDA_VERSION + "");

					// 添加强制升级标识
					intent.putExtra("force", info.getForce());
					intent.putExtra("forceInfo", info.getForceInfo());
					info.setUpdate(true);
					return info;
				} else {
					if (Flags.PDA_VERSION_BYHAND == upfateTag) {
						HgqwToast.makeText(context, context.getString(R.string.current_is_latest_version) + "(" + packInfo.versionName + ")",
								HgqwToast.LENGTH_LONG).show();
					}
				}
			} else {
				if (Flags.PDA_VERSION_BYHAND == upfateTag) {
					HgqwToast.makeText(context, context.getString(R.string.data_download_failure_info), HgqwToast.LENGTH_LONG).show();
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			if (Flags.PDA_VERSION_BYHAND == upfateTag) {
				HgqwToast.makeText(context, context.getString(R.string.data_download_failure_info), HgqwToast.LENGTH_LONG).show();
			}
		}
		if (info != null) {
			info.setUpdate(false);
		}
		return info;
	}

}
