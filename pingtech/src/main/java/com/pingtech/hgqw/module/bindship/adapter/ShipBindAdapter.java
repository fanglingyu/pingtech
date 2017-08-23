package com.pingtech.hgqw.module.bindship.adapter;

import java.util.ArrayList;
import java.util.HashMap;

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
import com.pingtech.hgqw.entity.GlobalFlags;
import com.pingtech.hgqw.utils.DataDictionary;
import com.pingtech.hgqw.utils.SystemSetting;

public class ShipBindAdapter extends BaseAdapter{
	public static final int POSITIONZERO=0;
	public static final int POSITIONONE=1;
	public static final int POSITIONSECOND=2;
	private int fromType;
	private LayoutInflater mInflater;
	private ArrayList<HashMap<String, Object>> shipInfoList=null;
	private Handler handler;
	public ShipBindAdapter(Context context,ArrayList<HashMap<String, Object>> shipInfoList,Handler handler,int fromType) {
		this.mInflater = LayoutInflater.from(context);
		this.shipInfoList=shipInfoList;
		this.handler=handler;
		this.fromType=fromType;
	}

	@Override
	public int getCount() {
		int count = shipInfoList == null ? 0 : shipInfoList.size();
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

	private OnClickListener clickListener = new OnClickListener() {
		/** 执行相关操作 */
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Message msg = new Message();
			int position = Integer.parseInt(v.getTag().toString());
			if (position % 3 == 2) {
				// 执勤人员
				msg.what = 2;
				msg.arg1 = (position - 2) / 3;
				handler.sendMessage(msg);

			} else if (position % 3 == 1) {
				// 详情
				msg.what = 1;
				msg.arg1 = (position - 1) / 3;
				handler.sendMessage(msg);

			} else if (position % 3 == 0) {
				msg.what = 0;
				msg.arg1 = position / 3;
				handler.sendMessage(msg);

			}
		}
	};

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			if (fromType ==  GlobalFlags.LIST_TYPE_FROM_KAKOUMANAGER) {
				convertView = mInflater.inflate(R.layout.kk_listview_class,
						null);
				holder.index = (TextView) convertView
						.findViewById(R.id.index);
				holder.name = (TextView) convertView
						.findViewById(R.id.name);
				holder.en_name = (TextView) convertView
						.findViewById(R.id.rang);
				holder.country = (TextView) convertView
						.findViewById(R.id.addr);
			} else {
				convertView = mInflater.inflate(
						R.layout.ship_listview_class, null);
				holder.index = (TextView) convertView
						.findViewById(R.id.index);
				holder.name = (TextView) convertView
						.findViewById(R.id.name);
				holder.pos = (TextView) convertView.findViewById(R.id.pos);
				holder.kacbzt = (TextView) convertView
						.findViewById(R.id.kacbzt);
				holder.en_name = (TextView) convertView
						.findViewById(R.id.en_name);
				holder.country = (TextView) convertView
						.findViewById(R.id.country);
				holder.protry = (TextView) convertView
						.findViewById(R.id.protry);
			}
			holder.operate = (Button) convertView
					.findViewById(R.id.operate_btn);
			holder.detail = (Button) convertView
					.findViewById(R.id.detail_btn);
			holder.duty = (Button) convertView.findViewById(R.id.duty_btn);
			convertView.setTag(holder);
			holder.operate.setOnClickListener(clickListener);
			if (holder.detail != null) {
				holder.detail.setOnClickListener(clickListener);
			}
			if (holder.duty != null) {
				holder.duty.setOnClickListener(clickListener);
			}
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.operate.setTag(position * 3);
		if (holder.detail != null) {
			holder.detail.setTag(position * 3 + 1);
		}
		if (holder.duty != null) {
			holder.duty.setTag(position * 3 + 2);
		}
		if (shipInfoList == null || shipInfoList.size() == 0) {
			if (holder.index != null) {
				holder.index.setText("无");
			}
			if (holder.name != null) {
				holder.name.setText("无");
			}
			if (holder.en_name != null) {
				holder.en_name.setText("无");
			}
			if (holder.country != null) {
				holder.country.setText("无");
			}
			if (holder.protry != null) {
				holder.protry.setText("无");
			}
			if (holder.pos != null) {
				holder.pos.setText("无");
			}
			if (holder.kacbzt != null) {
				holder.kacbzt.setText("无");
			}
			holder.operate.setText("绑定");
			holder.operate.setEnabled(false);
			if (holder.detail != null) {
				holder.detail.setText(R.string.detail);
				holder.detail.setEnabled(false);
			}
		} else {
			if (holder.index != null) {
				holder.index.setText((position + 1) + "");
			}
			if (holder.name != null) {
				if (fromType == GlobalFlags.LIST_TYPE_FROM_KAKOUMANAGER) {
					holder.name.setText((String) shipInfoList.get(position)
							.get("kkmc"));
				} else {
					holder.name.setText((String) shipInfoList.get(position)
							.get("cbzwm"));
				}
			}
			if (holder.en_name != null) {
				if (fromType == GlobalFlags.LIST_TYPE_FROM_KAKOUMANAGER) {
					holder.en_name.setText((String) shipInfoList.get(
							position).get("kkfw"));
				} else {
					holder.en_name.setText((String) shipInfoList.get(
							position).get("cbywm"));
				}
			}
			if (holder.country != null) {
				if (fromType == GlobalFlags.LIST_TYPE_FROM_KAKOUMANAGER) {
					holder.country.setText((String) shipInfoList.get(
							position).get("kkxx"));
				} else {
					String gj_str = (String) (shipInfoList.get(position)
							.get("gj"));
					if (gj_str == null || gj_str.length() == 0) {
						holder.country.setText("");
					} else {
						holder.country.setText(DataDictionary
								.getCountryName(gj_str));
					}
				}
			}
			if (holder.protry != null) {
				String cbxz_str = (String) shipInfoList.get(position).get(
						"cbxz");
				if (cbxz_str == null || cbxz_str.length() == 0) {
					holder.protry.setText("");
				} else {
					holder.protry
							.setText(DataDictionary
									.getDataDictionaryName(
											cbxz_str,
											DataDictionary.DATADICTIONARY_TYPE_SHIP_TYPE));
				}
			}
			if (holder.pos != null) {
				if (fromType == GlobalFlags.LIST_TYPE_FROM_KAKOUMANAGER) {
					holder.pos.setVisibility(View.GONE);
				} else {
					holder.pos.setVisibility(View.VISIBLE);
					holder.pos.setText((String) shipInfoList.get(position)
							.get("tkwz"));
				}
			}
			if (holder.kacbzt != null) {
				holder.kacbzt.setText((String) shipInfoList.get(position)
						.get("kacbzt"));
			}
			String bdzt = ((String) shipInfoList.get(position).get("bdzt"));
			if (bdzt == null || bdzt.equals("未绑定")) {
				holder.operate.setText("绑定");
				holder.operate.setEnabled(true);
			} else {
				holder.operate.setText("已绑定");
				holder.operate.setEnabled(false);
				SystemSetting.setBindShip(shipInfoList.get(position),
						fromType + "");
			}
			if (holder.detail != null) {
				holder.detail.setText(R.string.detail);
			}
			if (fromType == GlobalFlags.LIST_TYPE_FROM_XUNCHAXUNJIAN) {
				if (holder.duty != null) {
					holder.duty.setVisibility(View.VISIBLE);
				}
			}
		}

		return convertView;
	}

	static class ViewHolder {
		private TextView index;
		private TextView name;
		private TextView en_name;
		private TextView country;
		private TextView protry;
		private TextView pos;
		private TextView kacbzt;
		private Button operate;
		private Button detail;
		private Button duty;
	}

}
