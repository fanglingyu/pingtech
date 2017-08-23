package com.pingtech.hgqw.utils;

import java.util.HashMap;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Vibrator;

import com.pingtech.R;
import com.pingtech.hgqw.base.BaseApplication;

public class SoundManager {
	private SoundPool sp;

	private HashMap<Integer, Integer> hm;

	private float volume;

	private Vibrator vibrator = null;

	private static final String TAG = "SoundManager";

	public static final int MESSAGE_SOUND = 5;

	public SoundManager() {
	}

	public void init() {
		onInitVibrator();
		onInitSoundPool();
	}

	/** 初始震动源 */
	private void onInitVibrator() {
		vibrator = (Vibrator) BaseApplication.instent.getSystemService(Context.VIBRATOR_SERVICE);
	}

	private void onInitSoundPool() {
		sp = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
		hm = new HashMap<Integer, Integer>();
		hm.put(1, sp.load(BaseApplication.instent, R.raw.msgx4, 1));
		hm.put(2, sp.load(BaseApplication.instent, R.raw.kkyz_successful, 1));
		hm.put(3, sp.load(BaseApplication.instent, R.raw.kkyz_fail, 1));
		hm.put(4, sp.load(BaseApplication.instent, R.raw.ic_mag, 1));
		hm.put(5, sp.load(BaseApplication.instent, R.raw.notification, 1));
		hm.put(MESSAGE_SOUND, sp.load(BaseApplication.instent, R.raw.message_sound, 1));
	}

	public void onPlayVibrator(boolean isContral) {
		long[] pattern = { 100, 400 };
		AudioManager am = (AudioManager) BaseApplication.instent.getSystemService(Context.AUDIO_SERVICE);
		float currentSound = am.getStreamVolume(AudioManager.STREAM_MUSIC);
		float maxSound = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		if (!isContral) {
			vibrator.vibrate(pattern, -1);
			return;
		}
		volume = currentSound / maxSound;
		if (vibrator != null && volume > 0) {
			vibrator.vibrate(pattern, -1);
		}
	}

	/** 播放声音 */
	public void onPlaySound(int num, int loop) {
		Log.i(TAG, "playSound:" + num + "," + loop);
		AudioManager am = (AudioManager) BaseApplication.instent.getSystemService(Context.AUDIO_SERVICE);
		float currentSound = am.getStreamVolume(AudioManager.STREAM_MUSIC);
		float maxSound = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		volume = currentSound / maxSound;
		Log.i("currentVolume", "currentSound=" + currentSound + ", maxSound=" + maxSound + ", volume=" + volume);
		if (sp != null && hm != null) {
			sp.play(hm.get(num), volume, volume, 1, loop, 1.0f);
		}
		onPlayVibrator(true);
	}

	/** 播放声音 */
	public void onPlaySoundNoVb(int num, int loop) {
		Log.i(TAG, "playSound:" + num + "," + loop);
		AudioManager am = (AudioManager) BaseApplication.instent.getSystemService(Context.AUDIO_SERVICE);
		float currentSound = am.getStreamVolume(AudioManager.STREAM_MUSIC);
		float maxSound = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		volume = currentSound / maxSound;
		sp.play(hm.get(num), volume, volume, 1, loop, 1.0f);
	}

	public void close() {
		onUnInitSoundPool();
		onCloseVibrator();
	}

	private void onUnInitSoundPool() {
		sp = null;
		hm = null;
	}

	private void onCloseVibrator() {
		vibrator.cancel();
	}

}
