package com.pingtech.hgqw.module.xtgl.adapter;

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
import android.widget.ImageView;
import android.widget.TextView;

import com.pingtech.R;
import com.pingtech.hgqw.module.xtgl.service.OffDataDownload;

public class OfflineDataAdapter extends BaseAdapter {

	private List<Map<String, Object>> data = null;
	private Context context;
	private Handler handler;
	/**false：表示数据正在下载中按钮不可点，true：表示数据下载完成后，如果下载失败则按钮可点*/
	private boolean bool = true;

	public void setClikable(boolean bool) {

		this.bool = bool;
	}

	public OfflineDataAdapter(Context context, List<Map<String, Object>> data,
			Handler handler) {
		super();
		this.data = data;
		this.context = context;
		this.handler = handler;

	}

	@Override
	public int getCount() {
		int count = data == null ? 0 : data.size();
		return count;
	}

	@Override
	public Object getItem(int position) {
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {

		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final Map<String, Object> map = data.get(position);
		final ViewHolder holder;
		// View view = convertView;
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(
					R.layout.offlinedata_item, null);
			holder = new ViewHolder();
			holder.textViewItem = (TextView) convertView
					.findViewById(R.id.offlinedata_item_textview);
			holder.imgState = (ImageView) convertView
					.findViewById(R.id.offlinedata_item_img);
			holder.btnRedown = (Button) convertView
					.findViewById(R.id.offlinedata_item_btn_redownload);
			convertView.setTag(holder);

		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		if (holder.textViewItem != null) {

			if (data.get(position).get("offlinedata_item_textview") != null) {
				String tvItem = (String) data.get(position).get(
						"offlinedata_item_textview");
				holder.textViewItem.setText(tvItem);
			}
		}
		int drawId = (Integer) map.get("offlinedata_item_img");
		if (holder.imgState != null) {

			if (map.get("offlinedata_item_img") != null) {
				holder.imgState.setBackgroundResource(drawId);

			}
		}
		
			if (drawId == R.drawable.ic_delete) {
				holder.btnRedown.setVisibility(View.VISIBLE);
//				holder.btnRedown.setVisibility(View.GONE);
				if (bool) {
					holder.btnRedown.setEnabled(true);
				} else {
					holder.btnRedown.setEnabled(false);
				}
			} else {
				holder.btnRedown.setVisibility(View.GONE);
			}

		
		final int i = position;
		holder.btnRedown.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Message msg = new Message();
				msg.what = OffDataDownload.WHAT_DOWNLOAD_ONE_RESULT_SUCCESS;
				msg.obj = data.get(i);// 下载失败项的信息
				msg.arg1 = i;// 下载失败项的序号
				handler.sendMessage(msg);
				//holder.btnRedown.setEnabled(false);
				
			}
		});

		return convertView;
	}

	public static class ViewHolder {
		private TextView textViewItem;
		private ImageView imgState;
		private Button btnRedown;
	}
}
