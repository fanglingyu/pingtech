package com.pingtech.hgqw.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.pingtech.R;
import com.pingtech.hgqw.utils.DataDictionary;
import com.pingtech.hgqw.utils.Log;

/** 显示查询设备结果列表界面activity类 */
public class SelectDeviceResultActivity extends MyActivity {
	private static final String TAG = "SelectDeviceResultActivity";
	private ListView listView;
	private MyAdapter adapter;
	private int selType;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.selectdevice_result_list);

		Log.i(TAG, "onCreate()");

		Intent intent = getIntent();
		selType = intent.getIntExtra("selhand", SelectDeviceActivity.SELECT_DEVICE_TYPE_SMART_DEVICE);
		setMyActiveTitle(getString(R.string.exception_info) + ">" + getString(R.string.selectdevice)
				+ getString(R.string.result));
		adapter = new MyAdapter(this);

		listView = (ListView) findViewById(R.id.listview);
		if (listView != null) {
			listView.setAdapter(adapter);
		}
		if (findViewById(R.id.listview_topline) != null) {
			findViewById(R.id.listview_topline).setVisibility(View.VISIBLE);
		}
		if (findViewById(R.id.select_result_empty) != null) {
			findViewById(R.id.select_result_empty).setVisibility(View.GONE);
		}
		adapter.notifyDataSetChanged();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	static class ViewHolder {
		private TextView sbbh;
		private TextView sbxh;
		private TextView ip;
		private TextView type;
		private TextView ssdw;
		private TextView zt;
		private Button operate;
	}

	/** 自定义列表显示适配器 */
	private class MyAdapter extends BaseAdapter {
		private LayoutInflater mInflater;

		public MyAdapter(Context context) {
			this.mInflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			return SelectDeviceActivity.deviceInfoList == null ? 0 : SelectDeviceActivity.deviceInfoList.size();
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
			@Override
			/**点击选择后，需要把设备名称、设备id等带回*/
			public void onClick(View v) {
				// TODO Auto-generated method stub
				int position = Integer.parseInt(v.getTag().toString());
				Intent data = null;
				data = new Intent();
				if (selType == SelectDeviceActivity.SELECT_DEVICE_TYPE_HAND_DEVICE) {
					data.putExtra("sbmc", (String) (SelectDeviceActivity.deviceInfoList.get(position).get("sbbh")));
				} else {
					data.putExtra("sbmc", (String) (SelectDeviceActivity.deviceInfoList.get(position).get("name")));
				}
				data.putExtra("sbid", (String) (SelectDeviceActivity.deviceInfoList.get(position).get("id")));
				setResult(RESULT_OK, data);
				finish();
			}
		};

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = mInflater.inflate(R.layout.selectdevice_result_listview_class, null);
				holder.sbbh = (TextView) convertView.findViewById(R.id.sbbh);
				holder.sbxh = (TextView) convertView.findViewById(R.id.sbxh);
				holder.ip = (TextView) convertView.findViewById(R.id.ip);
				holder.type = (TextView) convertView.findViewById(R.id.type);
				holder.ssdw = (TextView) convertView.findViewById(R.id.ssdw);
				holder.zt = (TextView) convertView.findViewById(R.id.zt);
				holder.operate = (Button) convertView.findViewById(R.id.operate_btn);
				holder.operate.setOnClickListener(clickListener);
				convertView.setTag(holder);

			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.operate.setTag(position);
			if (selType == SelectDeviceActivity.SELECT_DEVICE_TYPE_SMART_DEVICE) {
				if (holder.sbbh != null) {
					holder.sbbh
							.setText((String) SelectDeviceActivity.deviceInfoList.get(position).get("sbbh") == null ? ""
									: (String) SelectDeviceActivity.deviceInfoList.get(position).get("sbbh"));
				}
				if (holder.sbxh != null) {
					holder.sbxh.setText(getString(R.string.smartdevice_mc)
							+ ((String) SelectDeviceActivity.deviceInfoList.get(position).get("name") == null ? ""
									: (String) SelectDeviceActivity.deviceInfoList.get(position).get("name")));
				}
				if (holder.ip != null) {
					holder.ip.setText(getString(R.string.device_ip)
							+ ((String) SelectDeviceActivity.deviceInfoList.get(position).get("ip") == null ? ""
									: (String) SelectDeviceActivity.deviceInfoList.get(position).get("ip")));
				}
				if (holder.type != null) {
					String str = (String) SelectDeviceActivity.deviceInfoList.get(position).get("type");
					if (str != null && str.equals("1")) {
						holder.type.setText(getString(R.string.smartdevice_type) + getString(R.string.smartdevice_zq));
					} else if (str != null && str.equals("2")) {
						holder.type.setText(getString(R.string.smartdevice_type) + getString(R.string.smartdevice_jy));
					} else {
						holder.type.setText(getString(R.string.smartdevice_type));
					}
				}

				if (holder.ssdw != null) {
					holder.ssdw.setVisibility(View.GONE);
				}
				if (holder.zt != null) {
					holder.zt.setVisibility(View.GONE);
				}
			} else if (selType == SelectDeviceActivity.SELECT_DEVICE_TYPE_HAND_DEVICE) {
				if (holder.sbbh != null) {
					holder.sbbh
							.setText((String) SelectDeviceActivity.deviceInfoList.get(position).get("sbbh") == null ? ""
									: (String) SelectDeviceActivity.deviceInfoList.get(position).get("sbbh"));
				}
				if (holder.sbxh != null) {
					holder.sbxh.setText(getString(R.string.device_xh)
							+ ((String) SelectDeviceActivity.deviceInfoList.get(position).get("sbxh") == null ? ""
									: (String) SelectDeviceActivity.deviceInfoList.get(position).get("sbxh")));
				}
				if (holder.ip != null) {
					holder.ip.setText(getString(R.string.device_ip)
							+ ((String) SelectDeviceActivity.deviceInfoList.get(position).get("ip") == null ? ""
									: (String) SelectDeviceActivity.deviceInfoList.get(position).get("ip")));
				}
				if (holder.type != null) {
					String str = (String) SelectDeviceActivity.deviceInfoList.get(position).get("type");
					if (str != null && str.equals("0")) {
						holder.type.setText(getString(R.string.device_type) + getString(R.string.in));
					} else if (str != null && str.equals("1")) {
						holder.type.setText(getString(R.string.device_type) + getString(R.string.out));
					} else {
						holder.type.setText(getString(R.string.device_type));
					}
				}
				if (holder.ssdw != null) {
					holder.ssdw.setText(getString(R.string.goods_check_unit)
							+ ((String) SelectDeviceActivity.deviceInfoList.get(position).get("ssdw") == null ? ""
									: (String) SelectDeviceActivity.deviceInfoList.get(position).get("ssdw")));
				}
				if (holder.zt != null) {
					String str = (String) SelectDeviceActivity.deviceInfoList.get(position).get("zt");
					if (str != null && str.equals("1")) {
						holder.zt.setText(getString(R.string.device_status) + getString(R.string.normal));
					} else if (str != null && str.equals("2")) {
						holder.zt.setText(getString(R.string.device_status) + getString(R.string.sunhuai));
					} else if (str != null && str.equals("3")) {
						holder.zt.setText(getString(R.string.device_status) + getString(R.string.diushi));
					} else if (str != null && str.equals("4")) {
						holder.zt.setText(getString(R.string.device_status) + getString(R.string.weixiu));
					} else {
						holder.zt.setText(getString(R.string.device_status));
					}
				}
			} else if (selType == SelectDeviceActivity.SELECT_DEVICE_TYPE_CAMERA) {
				if (holder.sbbh != null) {
					holder.sbbh
							.setText((String) SelectDeviceActivity.deviceInfoList.get(position).get("sbbh") == null ? ""
									: (String) SelectDeviceActivity.deviceInfoList.get(position).get("sbbh"));
				}
				if (holder.sbxh != null) {
					holder.sbxh.setText(getString(R.string.camera_mc)
							+ ((String) SelectDeviceActivity.deviceInfoList.get(position).get("name") == null ? ""
									: (String) SelectDeviceActivity.deviceInfoList.get(position).get("name")));
				}
				if (holder.ip != null) {
					holder.ip.setText(getString(R.string.camera_ip)
							+ ((String) SelectDeviceActivity.deviceInfoList.get(position).get("ip") == null ? ""
									: (String) SelectDeviceActivity.deviceInfoList.get(position).get("ip")));
				}
				if (holder.type != null) {
					holder.type.setText(getString(R.string.camera_type)
							+ (DataDictionary.getDataDictionaryName(
									(String) SelectDeviceActivity.deviceInfoList.get(position).get("type"),
									DataDictionary.DATADICTIONARY_TYPE_CAMERA_TYPE) == null ? "" : DataDictionary
									.getDataDictionaryName((String) SelectDeviceActivity.deviceInfoList.get(position)
											.get("type"), DataDictionary.DATADICTIONARY_TYPE_CAMERA_TYPE)));
				}
				if (holder.ssdw != null) {
					holder.ssdw.setText(getString(R.string.goods_check_unit)
							+ ((String) SelectDeviceActivity.deviceInfoList.get(position).get("ssdw") == null ? ""
									: (String) SelectDeviceActivity.deviceInfoList.get(position).get("ssdw")));
				}
				if (holder.zt != null) {
					holder.zt.setVisibility(View.GONE);
				}
			}
			holder.operate.setText("选择");
			return convertView;
		}
	}

	@Override
	public void onDestroy() {
		Log.i(TAG, "onDestroy()");
		super.onDestroy();
	}
}
