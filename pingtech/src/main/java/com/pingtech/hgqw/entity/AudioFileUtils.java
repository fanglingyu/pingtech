package com.pingtech.hgqw.entity;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import org.kobjects.base64.Base64;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

import android.os.Environment;
import android.util.Xml;

import com.pingtech.hgqw.pullxml.PullXmlAudioInfoDetail;
import com.pingtech.hgqw.pullxml.PullXmlUpLoadAudio;
import com.pingtech.hgqw.utils.Log;

public class AudioFileUtils {
	public final static String path = Environment.getExternalStorageDirectory().getPath() + File.separator + "pingtech" + File.separator + "audio"
			+ File.separator;;

	/**
	 * 
	 * @方法名：getAudioByte
	 * @功能说明：获取指定音频的字节码，并base64编码。ByteArray实现方式
	 * @author liums
	 * @date 2013-4-11 下午5:27:11
	 * @param url2
	 * @return
	 */
	public static String getAudioFileByBase64(String url) {
		try {
			File file = new File(url);
			FileInputStream fileInputStream = new FileInputStream(file);
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream(1024);
			fileInputStream.available();
			byte[] buffer = new byte[1024];
			int len = 0;
			while ((len = fileInputStream.read(buffer)) > 0) {
				outputStream.write(buffer, 0, len);
			}
			byte[] temp = outputStream.toByteArray();
			String str = Base64.encode(temp);
			if (outputStream != null) {
				outputStream.close();
			}
			if (fileInputStream != null) {
				fileInputStream.close();
			}
			return str;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			// Log.log2File("FileUtils", "error:" + e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
			// Log.log2File("FileUtils", "error:" + e.getMessage());
		}

		return null;
	}

	/**
	 * 
	 * @方法名：getAudioByte
	 * @功能说明：获取指定音频的字节码，并base64编码。byte[]实现方式
	 * @author liums
	 * @date 2013-4-11 下午5:27:11
	 * @param url2
	 * @return
	 */
	public static String getAudioFileByBase64_02(String url) {
		try {
			File file = new File(url);
			FileInputStream fileInputStream = new FileInputStream(file);
			int length = (int) file.length();
			byte[] buffer = new byte[length];
			int len = 0;
			int offset = 0;
			while ((len = fileInputStream.read(buffer, offset, length - offset)) > 0) {
				offset += len;
			}
			String str = org.kobjects.base64.Base64.encode(buffer);
			if (fileInputStream != null) {
				fileInputStream.close();
			}
			return str;
		} catch (FileNotFoundException e) {
			// e.printStackTrace();
			Log.log2File("FileUtils", "error:" + e.getMessage());
		} catch (IOException e) {
			// e.printStackTrace();
			Log.log2File("FileUtils", "error:" + e.getMessage());
		}

		return null;
	}

	/**
	 * 
	 * @方法名：saveFiles
	 * @功能说明：保存从服务器下载的文件
	 * @author liums
	 * @date 2013-4-12 下午3:33:42
	 * @param str
	 * @return
	 */
	public static ArrayList<String> saveFiles(String str) {
		ArrayList<String> list = new ArrayList<String>();
		StringBuilder stringBuilder = new StringBuilder();
		// 解析 xml
		AudioInfoList audioInfoList = PullXmlUpLoadAudio.pullXml(str);
		if (audioInfoList == null) {
			return null;
		}
		List<AudioInfo> infos = audioInfoList.getAudioList();
		if (infos == null || "error".equals(audioInfoList.getResult())) {
			return null;
		}
		String path = AudioFileUtils.path;
		FileOutputStream fileOutputStream = null;
		FileOutputStream fileOutputStreamXml = null;
		File filePath = new File(path);
		if (!filePath.exists()) {
			filePath.mkdirs();
		}
		for (AudioInfo a : infos) {
			// 保存音频
			byte[] buffer = org.kobjects.base64.Base64.decode(a.getContent());

			byte[] bufferXml = org.kobjects.base64.Base64.decode(a.getContentXml());
			String nameXml = a.getFileNameDetail();
			File fileXml = new File(path + nameXml);
			if (fileXml.exists()) {
				continue;
			}

			String name = a.getFileName();
			File file = new File(path + name);
			if (file.exists()) {
				continue;
			}
			try {
				fileOutputStream = new FileOutputStream(file);
				fileOutputStream.write(buffer);
				fileOutputStreamXml = new FileOutputStream(fileXml);
				fileOutputStreamXml.write(bufferXml);
				stringBuilder.append(name + ",");
				list.add(name);
			} catch (FileNotFoundException e) {
				// e.printStackTrace();
				Log.log2File("FileUtils", "error:" + e.getMessage());
			} catch (IOException e) {
				// e.printStackTrace();
				Log.log2File("FileUtils", "error:" + e.getMessage());
			}
		}
		try {
			if (fileOutputStream != null) {
				fileOutputStream.close();
			}
			if (fileOutputStreamXml != null) {
				fileOutputStreamXml.close();
			}
		} catch (IOException e) {
			// e.printStackTrace();
			Log.log2File("FileUtils", "error:" + e.getMessage());
		}
		// return stringBuilder.toString();
		return list;
	}

	/**
	 * @方法名：getLocalFileInfoList
	 * @功能说明：获取本地音频文件信息
	 * @author liums
	 * @date 2013-4-18 下午2:04:17
	 */
	public static List<AudioInfo> getLocalFileInfoList() {
		List<AudioInfo> audioInfos = new ArrayList<AudioInfo>();
		File file = new File(path);
		if (!file.exists()) {
			return null;
		}
		File[] files = file.listFiles();// 得到路径下的所有音频文件
		if (files.length > 0) {
			for (File f : files) {
				String fileName = f.getName();
				// 只保留amr文件
				if (fileName.lastIndexOf(".amr") == -1) {
					continue;
				}
				// 截取xml文件名
				String txtFileName = (fileName.subSequence(0, fileName.indexOf(".amr"))).toString();
				File txtFile = new File(AudioFileUtils.path + txtFileName + ".xml");
				if (!txtFile.exists()) {
					continue;
				}
				// 读取文件数据
				String audioXmlInfo = getAudioXmlInfo(txtFile);
				// 解析xml数据
				AudioInfo audioInfo = null;
				try {
					audioInfo = PullXmlAudioInfoDetail.pullXml(audioXmlInfo);
				} catch (XmlPullParserException e) {
					// e.printStackTrace();
					Log.log2File("FileUtils", "error:" + e.getMessage());
				} catch (IOException e) {
					// e.printStackTrace();
					Log.log2File("FileUtils", "error:" + e.getMessage());
				}
				if (audioInfo == null) {
					continue;
				}

				String type = "0";// 左右标识：0本人右侧，1他人左侧
				String userID = LoginUser.getCurrentLoginUser().getUserID();
				if (audioInfo.getUserFromId().equals(userID)) {

				} else if (audioInfo.getUserToId().contains(userID)) {
					type = "1";
				} else {// 发送人和接收者都不包含本人id则不显示。
					continue;
				}
				audioInfo.setLastModifiedTime(f.lastModified());
				audioInfo.setType(type);
				audioInfos.add(audioInfo);
			}
		} else {
			return null;
		}

		return audioInfos;
	}

	/**
	 * @方法名：getTxtInfo
	 * @功能说明：读取txt文件数据
	 * @author liums
	 * @date 2013-4-19 下午4:37:34
	 * @param txtFile
	 */
	private static String getAudioXmlInfo(File txtFile) {
		String str = null;
		try {
			FileInputStream fileInputStream;
			fileInputStream = new FileInputStream(txtFile);
			int length = (int) txtFile.length();
			byte[] buffer = new byte[length];
			int len = 0;
			int offset = 0;
			while ((len = fileInputStream.read(buffer, offset, length - offset)) > 0) {
				offset += len;
			}
			str = new String(buffer, "utf-8");
			if (fileInputStream != null) {
				fileInputStream.close();
				fileInputStream = null;
			}
		} catch (FileNotFoundException e) {
			// e.printStackTrace();
			Log.log2File("FileUtils", "error:" + e.getMessage());
		} catch (IOException e) {
			// e.printStackTrace();
			Log.log2File("FileUtils", "error:" + e.getMessage());
		}
		return str;
	}

	/**
	 * @方法名：delAudioFile
	 * @功能说明：删除音频
	 * @author liums
	 * @date 2013-4-19 上午10:27:04
	 * @param fileName
	 */
	public static boolean delAudioFile(String fileName) {
		File file = new File(path);
		if (!file.exists()) {
			return false;
		}
		File file01 = new File(path + fileName);
		if (!file01.exists()) {
			return false;
		}

		file01.delete();

		// 删除对应的xml文件
		String xmlFileName = (fileName.subSequence(0, fileName.indexOf(".amr"))).toString();
		File txtFile = new File(path + xmlFileName + ".xml");
		if (txtFile.exists()) {
			txtFile.delete();
		}
		return true;
	}

	/**
	 * @方法名：saveTxtFilesToXml
	 * @功能说明：保存为xml
	 * @author liums
	 * @date 2013-4-19 下午5:02:21
	 * @param audioInfo
	 */
	public static void saveTxtFilesToXml(AudioInfo audioInfo) {
		File xmlFile = new File(AudioFileUtils.path + audioInfo.getFileNameDetail());
		try {
			FileOutputStream outStream;
			outStream = new FileOutputStream(xmlFile);
			OutputStreamWriter outStreamWriter = new OutputStreamWriter(outStream, "UTF-8");
			BufferedWriter writer = new BufferedWriter(outStreamWriter);
			writeXML(audioInfo, writer);
		} catch (FileNotFoundException e) {
			// e.printStackTrace();
			Log.log2File("FileUtils", "error:" + e.getMessage());
		} catch (UnsupportedEncodingException e) {
			// e.printStackTrace();
			Log.log2File("FileUtils", "error:" + e.getMessage());
		}
	}

	public static String writeXML(AudioInfo audioInfo, Writer writer) {
		XmlSerializer serializer = Xml.newSerializer();
		try {
			serializer.setOutput(writer);
			serializer.startDocument("UTF-8", true);
			// 第一个参数为命名空间,如果不使用命名空间,可以设置为null
			serializer.startTag("", "datas");
			serializer.startTag("", "audio");

			serializer.startTag("", "fileName");
			serializer.text(audioInfo.getFileName());
			serializer.endTag("", "fileName");

			serializer.startTag("", "time");
			serializer.text(audioInfo.getTime());
			serializer.endTag("", "time");

			serializer.startTag("", "userFromId");
			serializer.text(audioInfo.getUserFromId());
			serializer.endTag("", "userFromId");

			serializer.startTag("", "userToId");
			serializer.text(audioInfo.getUserToId());
			serializer.endTag("", "userToId");

			serializer.startTag("", "userFromName");
			serializer.text(audioInfo.getUserFromName());
			serializer.endTag("", "userFromName");

			serializer.startTag("", "userToName");
			serializer.text(audioInfo.getUserToName());
			serializer.endTag("", "userToName");

			serializer.endTag("", "audio");
			serializer.endTag("", "datas");
			serializer.endDocument();
			writer.flush();
			writer.close();
		} catch (Exception e) {
			// e.printStackTrace();
			Log.log2File("FileUtils", "error:" + e.getMessage());
		}
		return null;
	}

}
