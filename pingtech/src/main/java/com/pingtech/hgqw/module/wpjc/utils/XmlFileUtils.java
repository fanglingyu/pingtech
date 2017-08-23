package com.pingtech.hgqw.module.wpjc.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class XmlFileUtils {

	/**
	 * 读取Xml文件里面的信息
	 * @param File
	 * @return
	 */
	public static String getStringFromFile(File file) {
		if(!file.exists()){
			return null;
		}
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(file.getAbsoluteFile()
					.toString()));
			String line;
			StringBuffer buffer = new StringBuffer();
			while ((line = br.readLine()) != null) {
				buffer.append(line);
			}
			br.close();
			return buffer.toString();
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		}
		return null;

	}
}
