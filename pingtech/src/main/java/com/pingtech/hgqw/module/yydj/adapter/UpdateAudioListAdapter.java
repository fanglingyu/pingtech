package com.pingtech.hgqw.module.yydj.adapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.pingtech.R;
import com.pingtech.hgqw.entity.Flags;
import com.pingtech.hgqw.module.yydj.utils.AudioUtil;

class ViewHolder {
	/**
	 * 音频按钮,显示时间,点击播放声音
	 */
	Button audioButton_right;

	/**
	 * 删除按钮
	 */
	Button delButton_right;

	/**
	 * 用户名，显示在音频按钮侧面
	 */
	TextView userFromName_right;

	/**
	 * 显示文件修改时间
	 */
	TextView audio_list_class_date_left;

	/**
	 * 显示文件修改时间
	 */
	TextView audio_list_class_date_right;

	/**
	 * 音频按钮,显示时间
	 */
	Button audioButton_left;

	/**
	 * 删除按钮
	 */
	Button delButton_left;

	/**
	 * 用户名，显示在音频按钮侧面
	 */
	TextView userFromName_left;

	/**
	 * 左侧显示
	 */
	View talk_back_audio_list_left;

	/**
	 * 右侧显示
	 */
	View talk_back_audio_list_right;
}

public class UpdateAudioListAdapter extends BaseAdapter {

	private LayoutInflater mInflater;

	List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();;

	private Handler handler;

	public UpdateAudioListAdapter(Context context, List<Map<String, Object>> data, Handler handler) {
		this.mInflater = LayoutInflater.from(context);
		this.data = data;
		this.handler = handler;
	}

	@Override
	public int getCount() {
		int count = data == null ? 0 : data.size();
		return count;
	}

	@Override
	public Object getItem(int arg0) {
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		holder = new ViewHolder();
		convertView = mInflater.inflate(R.layout.talk_back_audio_list_class, null);

		holder.userFromName_left = (TextView) convertView.findViewById(R.id.audio_list_class_name_left);
		holder.audioButton_left = (Button) convertView.findViewById(R.id.audio_list_class_audio_btn_left);
		holder.delButton_left = (Button) convertView.findViewById(R.id.audio_list_class_btn_del_left);

		// 修改时间
		holder.audio_list_class_date_left = (TextView) convertView.findViewById(R.id.audio_list_class_date_left);
		holder.audio_list_class_date_right = (TextView) convertView.findViewById(R.id.audio_list_class_date_right);

		holder.userFromName_right = (TextView) convertView.findViewById(R.id.audio_list_class_name_right);
		holder.audioButton_right = (Button) convertView.findViewById(R.id.audio_list_class_audio_btn_right);
		holder.delButton_right = (Button) convertView.findViewById(R.id.audio_list_class_btn_del_right);

		holder.talk_back_audio_list_left = (View) convertView.findViewById(R.id.talk_back_audio_list_left);
		holder.talk_back_audio_list_right = (View) convertView.findViewById(R.id.talk_back_audio_list_right);

		if (data != null) {
			if ("0".equals(data.get(position).get("type"))) {// 0 本人 右侧，1 他人 左侧
				holder.talk_back_audio_list_left.setVisibility(View.GONE);
				holder.talk_back_audio_list_right.setVisibility(View.VISIBLE);
			} else {
				holder.talk_back_audio_list_left.setVisibility(View.VISIBLE);
				holder.talk_back_audio_list_right.setVisibility(View.GONE);
			}

			if ("0".equals(data.get(position).get("delFlag"))) {
				holder.delButton_left.setVisibility(View.VISIBLE);
				holder.delButton_right.setVisibility(View.VISIBLE);
			} else {
				holder.delButton_left.setVisibility(View.GONE);
				holder.delButton_right.setVisibility(View.GONE);
			}

		} else {
			holder.talk_back_audio_list_left.setVisibility(View.VISIBLE);
			holder.talk_back_audio_list_right.setVisibility(View.GONE);
			holder.delButton_left.setVisibility(View.GONE);
			holder.delButton_right.setVisibility(View.GONE);
		}

		// 设置数据
		if (data != null && data.size() > 0) {
			holder.userFromName_left.setText((String) data.get(position).get("userFromName"));
			holder.audioButton_left.setText("  " + data.get(position).get("time"));

			holder.userFromName_right.setText((String) data.get(position).get("userFromName"));
			holder.audioButton_right.setText("  " + data.get(position).get("time"));

			// 修改时间
			holder.audio_list_class_date_left.setText("  " + data.get(position).get("lastModifiedTime"));
			holder.audio_list_class_date_right.setText("  " + data.get(position).get("lastModifiedTime"));

			final int i = position;

			// 点击播放
			holder.audioButton_right.setOnClickListener(new AudioPlayLis((String) data.get(i).get("fileName"), holder.audioButton_right));

			// 删除按钮事件
			holder.delButton_right.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (i < data.size()) {
						Map map = data.get(i);
						if (map != null) {
							String fileName = (String) map.get("fileName");
							Message msg = new Message();
							msg.obj = fileName;
							msg.what = Flags.DELETE_AUDIO;
							handler.sendMessage(msg);
						}
					}
				}
			});

			// 点击播放
			holder.audioButton_left.setOnClickListener(new AudioPlayLis((String) data.get(i).get("fileName"), holder.audioButton_left));

			// 删除按钮事件
			holder.delButton_left.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (i < data.size()) {
						Map<String, Object> tempData = data.get(i);
						if (tempData != null) {
							String fileName = (String) tempData.get("fileName");
							Message msg = new Message();
							msg.obj = fileName;
							msg.what = Flags.DELETE_AUDIO;
							handler.sendMessage(msg);

						}
					}

				}
			});
			convertView.setTag(holder);
		}

		return convertView;
	}

	class AudioPlayLis implements OnClickListener {
		private String fileName = null;

		private Button audioButtonTemp = null;

		public AudioPlayLis(String fileName, Button audioButtonTemp) {
			this.fileName = fileName;
			this.audioButtonTemp = audioButtonTemp;
		}

		@Override
		public void onClick(View v) {
			AudioUtil.audioBtnClicked(fileName, audioButtonTemp);
		}

	}

}