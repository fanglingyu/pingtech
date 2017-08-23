package com.pingtech.hgqw.readcard.utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import android.os.Environment;

import com.fri.idcread.UnicodeToUTF;

public class GetIDInfo {
	public static String cardNum = "";

	public static String name = "";

	public static String sex = "";

	public static String csrq = "";

	public static boolean getIDCardNum() {
		String content = getContent();
		cardNum = "";
		name = "";
		sex = "";
		csrq = "";
		if ((content != null) && content.length() > 18) {
			String s1[] = content.split(" ");
			int count = s1.length;
			int numindex = -1;
			String idnum = null;
			for (int i = 0; i < count; i++) {
				int len = s1[i].length();
				if (len > 0) {
					if (name.length() == 0) {
						name = s1[i];
					} else if (sex.length() == 0) {
						sex = "1".equals(s1[i].substring(0, 1)) ? "男" : "女";
						csrq = s1[i].substring(3, 7) + "-" + s1[i].substring(7, 9) + "-" + s1[i].substring(9, 11);
					} else {
						if (len >= 18) {
							int j;
							for (j = 0; j < 18; j++) {
								if (((j < 17) && ((s1[i].charAt(j) < 0x30) || (s1[i].charAt(j) > 0x39)))
										|| ((j == 17) && (s1[i].charAt(j) != 'X') && (s1[i].charAt(j) != 'x'))) {
									break;
								}
							}
							if (j == 18) {
								numindex = i;
								break;
							}
						}
					}
				}

			}
			if (numindex >= 0) {
				idnum = s1[numindex].substring(0, 18);
			} else {
				int len = content.length();
				int i;
				int numbercount = 0;
				int start = 0;
				for (i = 0; i < len; i++) {
					if (content.charAt(i) >= 0x30 && content.charAt(i) <= 0x39) {
						numbercount++;
						if (numbercount == 1) {
							start = i;
						}
						if (numbercount == 18) {
							idnum = content.substring(start, start + 18);
							break;
						}
					} else {
						numbercount = 0;
					}
				}
			}
			cardNum = idnum;
			return true;
		}
		return false;
	}

	// 内容转码
	private static String getContent() {
		String idfilepath = "/data/parsebmp/wzuni.txt";

		String zpfilepath = "/data/parsebmp/zp.bmp";
		if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			return null;
		}
		StringBuilder sb = new StringBuilder();
		InputStream instream = null;
		try {
			// instream = new FileInputStream("/sdcard/wzuni.txt");
			instream = new FileInputStream(idfilepath);
			if (instream.available() > 0) {
				byte[] buffer = new byte[instream.available()];
				instream.read(buffer);
				sb.append(new String(UnicodeToUTF.UNICODE_TO_UTF8(buffer), "utf-8"));
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				instream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return sb.toString();
	}
}
