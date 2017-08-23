package com.pingtech.hgqw.adapter;

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

public class CyxxListAdapter extends BaseAdapter {
	private LayoutInflater mInflater;

	List<Map<String, String>> data = new ArrayList<Map<String, String>>();;

	private Handler handler;

	public CyxxListAdapter(Context context, List<Map<String, String>> data,
			Handler handler) {
		this.mInflater = LayoutInflater.from(context);
		this.data = data;
		this.handler = handler;
	}

	@Override
	public int getCount() {
		// int count = data == null ? 0 : data.size();
		return data.size();
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
		CyxxListAdapterViewHolder holder = null;
		if (convertView == null) {
			holder = new CyxxListAdapterViewHolder();
			convertView = mInflater.inflate(R.layout.cfzg_cyxx_listview_class,
					null);

			holder.xuhao = (TextView) convertView
					.findViewById(R.id.cfzg_cyxx_listview_class_xh);
			holder.xm = (TextView) convertView
					.findViewById(R.id.cfzg_cyxx_listview_class_xm);
			holder.zw = (TextView) convertView
					.findViewById(R.id.cfzg_cyxx_listview_class_zw);
			// holder.zjhm = (TextView)
			// convertView.findViewById(R.id.cfzg_cyxx_listview_class_zjhm);

			holder.up = (Button) convertView
					.findViewById(R.id.cfzg_cyxx_listview_class_btn_up);
			holder.down = (Button) convertView
					.findViewById(R.id.cfzg_cyxx_listview_class_btn_down);
			convertView.setTag(holder);

		} else {
			holder = (CyxxListAdapterViewHolder) convertView.getTag();
		}

		// 设置数据
		if (data != null && data.size() > 0) {
			holder.xuhao.setText(data.get(position).get("xuhao"));
			// holder.xuhao.setText("序号");
			holder.xm.setText(data.get(position).get("xm"));
			holder.zw.setText(data.get(position).get("zw"));
			holder.zjhm = data.get(position).get("zjhm");
			// 0在船上,上船按钮置灰；1在船下，下船按钮置灰
			if ("0".equals(data.get(position).get("cywz"))) {
				holder.up.setEnabled(false);
				holder.down.setEnabled(true);
			} else if ("1".equals(data.get(position).get("cywz"))) {
				holder.up.setEnabled(true);
				holder.down.setEnabled(false);
			} else {
				holder.up.setEnabled(false);
				holder.down.setEnabled(true);
			}

			// 按钮监听
			final int i = position;
			final Map<String, String> map = data.get(i);
			holder.up.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Message msg = new Message();
					msg.arg1 = 0;
					msg.obj = map;
					handler.sendMessage(msg);
				}
			});

			holder.down.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Message msg = new Message();
					msg.arg1 = 1;
					msg.obj = map;
					handler.sendMessage(msg);
				}
			});
			convertView.setTag(holder);
		}

		return convertView;
	}

	class CyxxListAdapterViewHolder {
		TextView xuhao;

		TextView xm;

		TextView zw;

		String zjhm;

		Button up;

		Button down;
	}
}