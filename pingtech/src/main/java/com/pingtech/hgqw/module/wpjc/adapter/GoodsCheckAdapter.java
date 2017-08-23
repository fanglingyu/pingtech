package com.pingtech.hgqw.module.wpjc.adapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.pingtech.R;
import com.pingtech.hgqw.utils.DataDictionary;
import com.pingtech.hgqw.utils.StringUtils;

public class GoodsCheckAdapter extends BaseAdapter {
	private LayoutInflater mInflater;
	ArrayList<Map<String, String>> goodsList = null;

	public GoodsCheckAdapter(Context context,
			ArrayList<Map<String, String>> list) {
		this.mInflater = LayoutInflater.from(context);
		this.goodsList = list;
	}

	@Override
	public int getCount() {
		return goodsList == null ? 0 : goodsList.size();
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
		GoodsViewHolder holder = null;
		if (convertView == null) {
			holder = new GoodsViewHolder();
			convertView = mInflater.inflate(R.layout.goodscheck_listview_class,
					null);
			holder.index = (TextView) convertView.findViewById(R.id.col1);
			holder.unit = (TextView) convertView.findViewById(R.id.col2);
			holder.name = (TextView) convertView.findViewById(R.id.col3);
			holder.type = (TextView) convertView.findViewById(R.id.col4);
			holder.time = (TextView) convertView.findViewById(R.id.col5);
			holder.dict = (TextView) convertView.findViewById(R.id.col6);
			convertView.setTag(holder);

		} else {
			holder = (GoodsViewHolder) convertView.getTag();
		}
		if (holder.index != null) {
			holder.index.setText((position + 1) + "");
		}
		if (holder.unit != null) {
			holder.unit.setText(goodsList.get(position).get("ssdw"));
		}
		if (holder.name != null) {
			holder.name.setText(goodsList.get(position).get("xm"));
		}
		if (holder.type != null) {
			String type = goodsList.get(position).get("type");
			List<String> types = getStringArray(type);
			StringBuffer buffer = new StringBuffer();
			if (types != null && types.size() > 0) {
				for (int i = 0; i < types.size(); i++) {
					if (buffer != null && buffer.length() > 0) {
						buffer.append(";");
					}
					buffer.append(DataDictionary.getDataDictionaryName(
							types.get(i),
							DataDictionary.DATADICTIONARY_TYPE_GOODS_TYPE));
				}
			}
			holder.type.setText(buffer.toString());
		}
		if (holder.time != null) {
			holder.time.setText(goodsList.get(position).get("time"));
		}
		if (holder.dict != null) {
			String str = goodsList.get(position).get("fx");
			if (str != null) {
				holder.dict.setText(str.equals("1") ? "下船" : "上船");
			}
		}

		return convertView;
	}

	static class GoodsViewHolder {
		private TextView index;
		private TextView unit;
		private TextView name;
		private TextView type;
		private TextView time;
		private TextView dict;
	}

	private List<String> getStringArray(String type) {
		List<String> listGoods = new ArrayList<String>();
		final String bz = "|";
		if (StringUtils.isNotEmpty(type)) {
			if (type.contains(bz)) {
				String mType = type;
				mType = bz + mType;
				while (mType.contains(bz)) {
					mType = mType.substring(1);
					int num = mType.indexOf(bz);
					if (num == -1 && StringUtils.isNotEmpty(mType)) {
						listGoods.add(mType);
						break;
					}
					String good = mType.substring(0, num);
					mType = mType.substring(num);
					listGoods.add(good);
				}

			} else {
				listGoods.add(type);
			}
		}
		return listGoods;
	}

}
