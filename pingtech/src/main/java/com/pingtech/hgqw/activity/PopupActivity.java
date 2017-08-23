package com.pingtech.hgqw.activity;

import java.io.IOException;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.pingtech.R;
import com.pingtech.hgqw.entity.AudioFileUtils;
import com.pingtech.hgqw.module.police.activity.MyPoliceList;
import com.pingtech.hgqw.module.police.request.RequestPolice;
import com.pingtech.hgqw.module.qwjw.QwzlBroadCaseReceiver;
import com.pingtech.hgqw.module.qwjw.activity.QwjwActivity;
import com.pingtech.hgqw.module.yydj.activity.TalkBack;
import com.pingtech.hgqw.utils.Log;

/**
 * 有新警务时，弹出该popup activity提示用户，也可以扩展为所有需要框activity提示的popup
 * 由于这是一个半屏dialog，因此需要自定义
 * */
public class PopupActivity extends Activity {
	private static final String TAG = "PopupActivity";

	private String type;

	private int what;

	private TextView message;

	private Class clazz = null;

	private String audioName;

	private Intent broadcastIntent = null;

	// private String nameList;
	private ArrayList<String> nameList;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Intent intent = getIntent();
		type = intent.getStringExtra("type");
		what = intent.getIntExtra("what", RequestPolice.HTTPREQUEST_TYPE_FOR_RECEIVE_MY_TASK);
		audioName = intent.getStringExtra("audioName");
		nameList = intent.getStringArrayListExtra("nameList");
		super.onCreate(savedInstanceState);
		Log.i(TAG, "onCreate()");
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		int level = intent.getIntExtra("level", -1);
		setContentView(R.layout.popup);
		find();
		if ("mypolice".equals(type)) {
			init();
			Button btn = (Button) findViewById(R.id.read);
			btn.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					myPolice();
				}
			});
		} else if ("TalkBack".equals(type)) {
			((TextView) findViewById(R.id.message)).setText("有新的语音消息");
			// 播放语音
			// playAudio();
			Button btn = (Button) findViewById(R.id.read);
			btn.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					finish();
					Intent intent = new Intent();
					intent.putExtra("audioName", audioName);
					intent.putExtra("nameList", nameList);
					intent.putExtra("type", "listen");
					intent.setClass(getApplicationContext(), TalkBack.class);
					startActivity(intent);
				}
			});
		} else if ("batteries".equals(type) && level != -1) {
			((Button) findViewById(R.id.read)).setVisibility(View.GONE);
			((TextView) findViewById(R.id.message)).setVisibility(View.GONE);
			((TextView) findViewById(R.id.message_02)).setVisibility(View.VISIBLE);
			((TextView) findViewById(R.id.message_02)).setText("电池电量剩余" + level + "%   请充电！");
		}
	}

	private void find() {
		message = (TextView) findViewById(R.id.message);
	}

	private void init() {
		switch (what) {
		case RequestPolice.HTTPREQUEST_TYPE_FOR_RECEIVE_MY_TASK:
			clazz = MyPoliceList.class;
			message.setText(R.string.new_jw);
			broadcastIntent = null;
			break;
		case RequestPolice.HTTPREQUEST_TYPE_FOR_RECEIVE_MY_TASK_QWZL:
			clazz = QwjwActivity.class;
			message.setText(R.string.new_qw);
			broadcastIntent = new Intent();
			broadcastIntent.setAction("com.pingtech.hgqw.module.qwjw.QwzlBroadCaseReceiver");
			broadcastIntent.setClass(this, QwzlBroadCaseReceiver.class);

			break;

		default:
			break;
		}
	}

	protected void myPolice() {
		if (clazz != null) {
			if (broadcastIntent != null) {
				sendBroadcast(broadcastIntent);// 发送广播
			}
			Intent intent = new Intent();
			intent.setClass(getApplicationContext(), clazz);
			startActivity(intent);
			finish();
		}
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		int keyCode = event.getKeyCode();
		Log.i(TAG, "dispatchKeyEvent,keycode=" + keyCode);
		if (keyCode == KeyEvent.KEYCODE_ENTER) {
			return true;
		}
		if ((keyCode >= KeyEvent.KEYCODE_0 && keyCode <= KeyEvent.KEYCODE_9) || (keyCode == KeyEvent.KEYCODE_ENTER)) {
			return true;
		}
		return super.dispatchKeyEvent(event);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	@Override
	public void onDestroy() {
		Log.i(TAG, "onDestroy()");
		super.onDestroy();
	}

	/**
	 * 
	 * @方法名：playAudio
	 * @功能说明：播放音频
	 * @author liums
	 * @date 2013-4-12 下午2:38:03
	 */
	MediaPlayer mediaPlayer = null;

	private boolean isPlaying = false;

	public void playAudio() {
		String name = null;
		// 循环播放
		if (nameList != null && nameList.size() > 0) {
			name = nameList.get(0);
		}
		mediaPlayer = new MediaPlayer();
		if (mediaPlayer.isPlaying()) {
			mediaPlayer.reset();// 重置为初始状态
		}
		try {
			mediaPlayer.setDataSource(AudioFileUtils.path + name);
		} catch (IllegalArgumentException e) {
			// e.printStackTrace();
			Log.log2File(TAG, "error:" + e.getMessage());
		} catch (IllegalStateException e) {
			// e.printStackTrace();
			Log.log2File(TAG, "error:" + e.getMessage());
		} catch (IOException e) {
			// e.printStackTrace();
			Log.log2File(TAG, "error:" + e.getMessage());
		}

		try {
			mediaPlayer.prepare();
		} catch (IllegalStateException e) {
			// e.printStackTrace();
			Log.log2File(TAG, "error:" + e.getMessage());
		} catch (IOException e) {
			// e.printStackTrace();
			Log.log2File(TAG, "error:" + e.getMessage());
		}// 缓冲
		mediaPlayer.start();// 开始或恢复播放
		// mediaPlayer.pause();// 暂停播放
		// mediaPlayer.start();// 恢复播放
		// mediaPlayer.stop();// 停止播放
		// mediaPlayer.release();// 释放资源
		mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {// 播出完毕事件
					@Override
					public void onCompletion(MediaPlayer arg0) {
						isPlaying = false;
						mediaPlayer.release();
						/*
						 * if (nameList != null && nameList.size() > 0) {
						 * playAudio(); }else{ mediaPlayer.stop(); }
						 */
					}
				});
		mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {// 错误处理事件
					@Override
					public boolean onError(MediaPlayer player, int arg1, int arg2) {
						isPlaying = false;
						mediaPlayer.release();
						return false;
					}
				});
	}
}