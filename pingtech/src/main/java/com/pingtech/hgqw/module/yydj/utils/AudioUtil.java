package com.pingtech.hgqw.module.yydj.utils;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.widget.Button;

import com.pingtech.R;
import com.pingtech.hgqw.base.BaseApplication;
import com.pingtech.hgqw.entity.AudioFileUtils;
import com.pingtech.hgqw.utils.Log;
import com.pingtech.hgqw.utils.StringUtils;

public class AudioUtil {
	private static final String TAG = "AudioUtil";

	private static String fileNameHis = null;

	private static Timer mTimer = null;

	private static TimerTask mTimerTask = null;

	private static Button audioButton = null;;

	private static MediaPlayer mediaPlayer = null;

	private static Handler audioHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			Log.i(TAG, "handleMessage");
			if (audioButton == null) {
				return;
			}
			switch (msg.what) {
			case 1:
				if (audioButton.getId() == R.id.audio_list_class_audio_btn_left) {
					audioButton.setBackgroundResource(R.drawable.tb_btn_audio_bg_left1);
				} else {
					audioButton.setBackgroundResource(R.drawable.tb_btn_audio_bg_right1);
				}
				break;
			case 2:
				if (audioButton.getId() == R.id.audio_list_class_audio_btn_left) {
					audioButton.setBackgroundResource(R.drawable.tb_btn_audio_bg_left2);
				} else {
					audioButton.setBackgroundResource(R.drawable.tb_btn_audio_bg_right2);
				}
				break;
			case 3:
				if (audioButton.getId() == R.id.audio_list_class_audio_btn_left) {
					audioButton.setBackgroundResource(R.drawable.tb_btn_audio_bg_left3);
				} else {
					audioButton.setBackgroundResource(R.drawable.tb_btn_audio_bg_right3);
				}
				break;
			default:
				break;
			}
		}

	};

	public static void audioBtnClicked(String fileName, Button audioButtonTemp) {
		stopTimer();
		if (audioButton != null) {
			if (audioButton.getId() == R.id.audio_list_class_audio_btn_left) {
				audioButton.setBackgroundResource(R.drawable.tb_btn_audio_bg_left3);
			} else {
				audioButton.setBackgroundResource(R.drawable.tb_btn_audio_bg_right3);
			}
		}
		// 同一个文件点击第二次则停止播放
		if (StringUtils.isNotEmpty(fileNameHis) && StringUtils.isNotEmpty(fileName) && fileNameHis.equals(fileName)) {
			if (mediaPlayer != null) {
				mediaPlayer.stop();// 停止播放
				mediaPlayer.release();// 释放资源
				mediaPlayer = null;
			}
			fileNameHis = null;
			return;
		}

		// 如果第二次点击其他文件，则停止前一个播放，再播放新文件
		if (mediaPlayer != null) {
			mediaPlayer.stop();// 停止播放
			mediaPlayer.release();// 释放资源
		}

		mediaPlayer = new MediaPlayer();
		if (mediaPlayer.isPlaying()) {
			Log.i(TAG, "mediaPlayer.isPlaying()");
			mediaPlayer.reset();// 重置为初始状态
			if (audioButton != null) {
				if (audioButton.getId() == R.id.audio_list_class_audio_btn_left) {
					audioButton.setBackgroundResource(R.drawable.tb_btn_audio_bg_left3);
				} else {
					audioButton.setBackgroundResource(R.drawable.tb_btn_audio_bg_right3);
				}
			}
		}

		audioButton = audioButtonTemp;
		try {
			mediaPlayer.setDataSource(AudioFileUtils.path + fileName);
		} catch (IllegalArgumentException e) {
			Log.log2File(TAG, "error:" + e.getMessage());
		} catch (IllegalStateException e) {
			Log.log2File(TAG, "error:" + e.getMessage());
		} catch (IOException e) {
			Log.log2File(TAG, "error:" + e.getMessage());
		}

		try {
			mediaPlayer.prepare();
		} catch (IllegalStateException e) {
			Log.log2File(TAG, "error:" + e.getMessage());
		} catch (IOException e) {
			Log.log2File(TAG, "error:" + e.getMessage());
		}

		AudioManager am = (AudioManager) BaseApplication.instent.getSystemService(Context.AUDIO_SERVICE);
		float currentSound = am.getStreamVolume(AudioManager.STREAM_MUSIC);
		float maxSound = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		float volume = currentSound / maxSound;

		mediaPlayer.setVolume(volume, volume);
		Log.i(TAG, "mediaPlayer.start()");

		fileNameHis = fileName;
		mediaPlayer.start();// 开始或恢复播放
		looperBtmImg();
		// mediaPlayer.pause();// 暂停播放
		// mediaPlayer.start();// 恢复播放
		// mediaPlayer.stop();// 停止播放
		// mediaPlayer.release();// 释放资源
		mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {// 播出完毕事件
					@Override
					public void onCompletion(MediaPlayer arg0) {
						mediaPlayer.release();
						mediaPlayer = null;
						fileNameHis = null;
						if (audioButton != null) {
							if (audioButton.getId() == R.id.audio_list_class_audio_btn_left) {
								audioButton.setBackgroundResource(R.drawable.tb_btn_audio_bg_left3);
							} else {
								audioButton.setBackgroundResource(R.drawable.tb_btn_audio_bg_right3);
							}
						}
						stopTimer();
					}
				});
		mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {// 错误处理事件
					@Override
					public boolean onError(MediaPlayer player, int arg1, int arg2) {
						mediaPlayer.release();
						mediaPlayer = null;
						fileNameHis = null;
						if (audioButton != null) {
							if (audioButton.getId() == R.id.audio_list_class_audio_btn_left) {
								audioButton.setBackgroundResource(R.drawable.tb_btn_audio_bg_left3);
							} else {
								audioButton.setBackgroundResource(R.drawable.tb_btn_audio_bg_right3);
							}
						}
						stopTimer();
						return false;
					}
				});
	}

	/**
	 * 
	 * @方法名：looperBtmImg
	 * @功能说明：添加播放状态
	 * @author liums
	 * @date 2013-12-9 下午4:17:30
	 */
	private static void looperBtmImg() {
		stopTimer();
		mTimer = new Timer();
		mTimerTask = new TimerTask() {
			private int i = 1;

			@Override
			public void run() {
				audioHandler.obtainMessage(i).sendToTarget();
				i++;
				if (i > 3) {
					i = 1;
				}
			}
		};
		// 调用频率为300毫秒一次
		mTimer.schedule(mTimerTask, 0, 300);
	}

	private static void stopTimer() {
		if (mTimer != null) {
			mTimer.cancel();
			mTimer = null;
		}

		if (mTimerTask != null) {
			mTimerTask.cancel();
			mTimerTask = null;
		}
	}

	public static void stopPlay() {
		stopTimer();
		if (mediaPlayer != null) {
			mediaPlayer.stop();// 停止播放
			mediaPlayer.release();// 释放资源
			mediaPlayer = null;
		}
		fileNameHis = null;
	}
}
