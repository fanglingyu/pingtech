package com.pingtech.hgqw.utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;

import com.pingtech.hgqw.entity.AudioFileUtils;
import com.pingtech.hgqw.entity.FlagUrls;
import com.pingtech.hgqw.entity.LoginUser;
import com.pingtech.hgqw.interf.OnHttpResult;
import com.pingtech.hgqw.web.NetWorkManager;

public class AudioService {
	private MediaRecorder recorder = null;

	private String audioName;

	private String audioNameDetail;

	private String filePath;

	private Context context;

	private OnHttpResult onHttpResult;

	private String path = AudioFileUtils.path;

	public AudioService(Context context, OnHttpResult onHttpResult) {
		this.context = context;
		this.onHttpResult = onHttpResult;
	}

	/**
	 * @方法名：startTalk
	 * @功能说明：开始录音
	 * @author liums
	 * @date 2013-4-16 下午6:17:41
	 * @return
	 * @throws IllegalStateException
	 * @throws IOException
	 */
	public boolean startTalk(String audioNameMic) throws IllegalStateException, IOException {
		if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			return false;
		}

		recorder = new MediaRecorder();
		recorder.setAudioSource(MediaRecorder.AudioSource.MIC);// 从麦克风采集声音
		recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);// 内容输出格式
		recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);// 音频编码方式
		filePath = audioNameMic;
		// System.out.println(filePath);
		recorder.setOutputFile(filePath);
		recorder.prepare();
		recorder.start(); // 开始刻录
		return true;
	}

	/**
	 * @方法名：getUrl
	 * @功能说明：得到语音文件保存路径
	 * @author liums
	 * @date 2013-4-16 下午6:16:43
	 * @return
	 * @throws IOException
	 */
	private String getUrl(Set<String> checkedUser) throws IOException {
		// sdcard路径
		File dir = new File(path);
		if (!dir.exists()) {
			dir.mkdirs();
			dir.setWritable(true);
		}
		if (checkedUser != null && checkedUser.size() == 1) {
			for (String s : checkedUser) {
				String temp = "from" + LoginUser.getCurrentLoginUser().getUserID() + "-to" + s + "_" + new Date().getTime();
				audioName = temp + ".amr";
				audioNameDetail = temp + ".txt";
			}
		} else if (checkedUser != null && checkedUser.size() > 1) {
			// 发给多人
			String temp = "from" + LoginUser.getCurrentLoginUser().getUserID() + "-tomany_" + new Date().getTime();
			audioName = temp + ".amr";
			audioNameDetail = temp + ".txt";
		}
		File dirName = new File(path + audioName);
		if (!dirName.exists()) {
			dirName.createNewFile();
			dirName.setWritable(true);
		}
		return dirName.getPath();
	}

	/**
	 * 
	 * @方法名：stopAudio
	 * @功能说明：结束录音
	 * @author liums
	 * @date 2013-4-12 下午2:38:49
	 */
	public void stopAudio() {
		if (recorder != null) {
//			recorder.setOnErrorListener(null);
//			recorder.setOnInfoListener(null);
			recorder.stop();// 停止刻录
			recorder.reset(); // 重设
			recorder.release(); // 刻录完成一定要释放资源
			recorder = null;
		}
	}

	/**
	 * 
	 * @方法名：upAudio
	 * @功能说明：上传音频
	 * @author liums
	 * @param audioNameMic
	 * @date 2013-4-11 下午5:26:56
	 */
	public void upAudio(String audioNameMic) {
		String path = "talk";
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("uFrom", LoginUser.getCurrentLoginUser().getUserID()));
		params.add(new BasicNameValuePair("audioName", audioNameMic));
		String str = AudioFileUtils.getAudioFileByBase64(AudioFileUtils.path + audioNameMic);
		params.add(new BasicNameValuePair("content", str));
		NetWorkManager.request(onHttpResult, path, params, FlagUrls.TALK);

	}

	/**
	 * 
	 * @方法名：upAudioTxt
	 * @功能说明：上传音频详情文件名
	 * @author liums
	 * @date 2013-4-19 下午1:07:52
	 * @param audioNameDetail
	 * @param userToId 
	 */
	public void upAudioTxt(String audioNameDetail, String userToId) {
		String path = "talk";
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("uFrom", LoginUser.getCurrentLoginUser().getUserID()));
		params.add(new BasicNameValuePair("uTo", userToId));
		params.add(new BasicNameValuePair("audioName", audioNameDetail));
		String str = AudioFileUtils.getAudioFileByBase64(AudioFileUtils.path + audioNameDetail);
		params.add(new BasicNameValuePair("content", str));
		NetWorkManager.request(onHttpResult, path, params, FlagUrls.TALK);
	}

	/**
	 * 
	 * @方法名：playAudio
	 * @功能说明：播放音频
	 * @author liums
	 * @date 2013-4-12 下午2:38:03
	 */
	public void playAudio(String fileName) {
		final MediaPlayer mediaPlayer = new MediaPlayer();
		if (mediaPlayer.isPlaying()) {
			mediaPlayer.reset();// 重置为初始状态
		}
		try {
			mediaPlayer.setDataSource(AudioFileUtils.path + fileName);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			mediaPlayer.prepare();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}// 缓冲
		mediaPlayer.start();// 开始或恢复播放
		// mediaPlayer.pause();// 暂停播放
		// mediaPlayer.start();// 恢复播放
		// mediaPlayer.stop();// 停止播放
		// mediaPlayer.release();// 释放资源
		mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {// 播出完毕事件
					@Override
					public void onCompletion(MediaPlayer arg0) {
						mediaPlayer.release();
					}
				});
		mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {// 错误处理事件
					@Override
					public boolean onError(MediaPlayer player, int arg1, int arg2) {
						mediaPlayer.release();
						return false;
					}
				});
	}

}
