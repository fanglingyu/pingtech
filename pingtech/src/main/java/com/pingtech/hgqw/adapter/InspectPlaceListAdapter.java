package com.pingtech.hgqw.adapter;

import java.util.ArrayList;

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
import com.pingtech.hgqw.entity.BaseInfoElement;

class ViewHolderForIp {
	/**
	 * 删除按钮
	 */
	Button btn;

	/**
	 * 用户名，显示在音频按钮侧面
	 */
	TextView text;
}

public class InspectPlaceListAdapter extends BaseAdapter {
	private LayoutInflater mInflater;

	ArrayList<BaseInfoElement> data = new ArrayList<BaseInfoElement>();

	private Handler handler;

	public InspectPlaceListAdapter(Context context, ArrayList<BaseInfoElement> mKkAreaList, Handler handler) {
		this.mInflater = LayoutInflater.from(context);
		this.data = mKkAreaList;
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
		final int flag = position;
		ViewHolderForIp holder = null;
		if (convertView == null) {
			holder = new ViewHolderForIp();
			convertView = mInflater.inflate(R.layout.inspect_place_list_class_qy, null);

			holder.text = (TextView) convertView.findViewById(R.id.text);
			holder.btn = (Button) convertView.findViewById(R.id.icon);

		} else {
			holder = (ViewHolderForIp) convertView.getTag();
		}

		// 设置数据
		if (data != null && data.size() > 0) {
			BaseInfoElement baseInfoElement = data.get(position);
			holder.text.setText(baseInfoElement.getOutlineTitle());
			if (baseInfoElement.getChecked()) {
				holder.btn.setBackgroundResource(R.drawable.radiobutton_check_on_n);
			} else {
				holder.btn.setBackgroundResource(R.drawable.radiobutton_check_off_n);
			}
			holder.btn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Message msg = new Message();
					msg.what = 2;
					msg.arg1 = flag;
					handler.sendMessage(msg);
				}
			});

			convertView.setTag(holder);
		}

		return convertView;
	}
}