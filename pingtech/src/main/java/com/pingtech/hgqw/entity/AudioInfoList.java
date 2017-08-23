package com.pingtech.hgqw.entity;

import java.util.ArrayList;
import java.util.List;

public class AudioInfoList {
	/**
	 * 请求结果
	 */
	private String result = "";

	/**
	 * 结果提示
	 */
	private String info = "";

	private List<AudioInfo> audioList = new ArrayList<AudioInfo>();

	public void add(AudioInfo audioInfo) {
		audioList.add(audioInfo);
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public List<AudioInfo> getAudioList() {
		return audioList;
	}
}
