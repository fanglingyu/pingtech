package com.pingtech.hgqw.module.kacbqk.adapter;

import java.util.HashMap;
import java.util.List;

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
import com.pingtech.hgqw.interf.OnChangeTab;
import com.pingtech.hgqw.utils.DataDictionary;

public class KacbApdater extends BaseAdapter implements OnChangeTab {

	private LayoutInflater mInflater;

	List<HashMap<String, Object>> dataList;

	private Context context;

	/**
	 * 当前选中的是哪个Tab: 1,预到港；2：在港；3：预离港
	 */
	private int selectNum = 1;

	private Handler handler;

	public KacbApdater(Context context, List<HashMap<String, Object>> dataList, Handler handler) {
		this.dataList = dataList;
		this.context = context;
		this.mInflater = LayoutInflater.from(context);
		this.handler = handler;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return dataList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.kacbqk_ship_list_item_class, null);
			holder.index = (TextView) convertView.findViewById(R.id.index);
			holder.name = (TextView) convertView.findViewById(R.id.name);
			holder.en_name = (TextView) convertView.findViewById(R.id.en_name);
			holder.port_en_name = (TextView) convertView.findViewById(R.id.port_en_name);
			holder.pos = (TextView) convertView.findViewById(R.id.pos);
			holder.kacbzt = (TextView) convertView.findViewById(R.id.kacbzt);
			holder.country = (TextView) convertView.findViewById(R.id.country);
			holder.port_country = (TextView) convertView.findViewById(R.id.port_country);
			holder.protry = (TextView) convertView.findViewById(R.id.protry);
			holder.port_protry = (TextView) convertView.findViewById(R.id.port_protry);
			holder.btnCbxq = (Button) convertView.findViewById(R.id.kacbqk_ship_list_item_cbxq);
			holder.btnCymd = (Button) convertView.findViewById(R.id.kacbqk_ship_list_item_cymd);
			holder.btnDldl = (Button) convertView.findViewById(R.id.kacbqk_ship_list_item_dldl);
			holder.btnCymd.setOnClickListener(clickListener);
			holder.btnCbxq.setOnClickListener(clickListener);
			holder.btnDldl.setOnClickListener(clickListener);
			convertView.setTag(holder);

		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.btnCbxq.setTag(position);
		holder.btnCymd.setTag(position);
		holder.btnDldl.setTag(position);

		if (holder.index != null) {
			holder.index.setText((position + 1) + "");
		}
		if (holder.name != null) {
			holder.name.setText((String) dataList.get(position).get("cbzwm"));
		}
		String cblx = (String) (dataList.get(position).get("cblx"));
		if (cblx.equals("ward")) {
			if (holder.en_name != null) {
				holder.en_name.setText((String) (dataList.get(position).get("cbywm")));
			}
			if (holder.port_en_name != null) {
				holder.port_en_name.setText((String) (dataList.get(position).get("cbywm")));
			}
			if (holder.pos != null) {
				holder.pos.setText((String) (dataList.get(position).get("tkwz")));
			}
			if (holder.kacbzt != null) {
				holder.kacbzt.setText((String) (dataList.get(position).get("kacbzt")));
			}
			if (holder.country != null) {
				String gj_str = (String) (dataList.get(position).get("gj"));
				if (gj_str == null || gj_str.length() == 0) {
					holder.country.setText("");
				} else {
					holder.country.setText(DataDictionary.getCountryName(gj_str));
				}
			}
			if (holder.port_country != null) {
				String gj_str = (String) (dataList.get(position).get("gj"));
				if (gj_str == null || gj_str.length() == 0) {
					holder.port_country.setText("");
				} else {
					holder.port_country.setText(DataDictionary.getCountryName(gj_str));
				}
			}
			if (holder.protry != null) {
				String cbxz_str = (String) dataList.get(position).get("cbxz");
				if (cbxz_str == null || cbxz_str.length() == 0) {
					holder.protry.setText("");
				} else {
					holder.protry.setText(DataDictionary.getDataDictionaryName(cbxz_str, DataDictionary.DATADICTIONARY_TYPE_SHIP_TYPE));
				}
			}
			if (holder.port_protry != null) {
				String cbxz_str = (String) dataList.get(position).get("cbxz");
				if (cbxz_str == null || cbxz_str.length() == 0) {
					holder.port_protry.setText("");
				} else {
					holder.port_protry.setText(DataDictionary.getDataDictionaryName(cbxz_str, DataDictionary.DATADICTIONARY_TYPE_SHIP_TYPE));
				}
			}

		} else {
			if (holder.en_name != null) {
				holder.en_name.setText((String) (dataList.get(position).get("cjsj")));
			}
			if (holder.port_en_name != null) {
				holder.port_en_name.setText((String) (dataList.get(position).get("czmc")));
			}
			if (holder.pos != null) {
				holder.pos.setText((String) (dataList.get(position).get("ssdw")));
			}
			if (holder.kacbzt != null) {
				holder.kacbzt.setVisibility(View.GONE);
			}
			if (holder.country != null) {
				holder.country.setText((String) (dataList.get(position).get("cbzyyt")));
			}
			if (holder.port_country != null) {
				holder.port_country.setText((String) (dataList.get(position).get("cjsj")));
			}
			if (holder.protry != null) {
				holder.protry.setText((String) (dataList.get(position).get("czmc")));
			}
			if (holder.port_protry != null) {
				holder.port_protry.setText((String) (dataList.get(position).get("cbzyyt")));
			}

		}
		return convertView;
	}

	static class ViewHolder {
		private TextView index;

		private TextView name;

		private TextView en_name;

		private TextView port_en_name;

		private TextView pos;

		private TextView kacbzt;

		private TextView country;

		private TextView port_country;

		private TextView protry;

		private TextView port_protry;

		private Button btnCbxq;// 船舶详情

		private Button btnCymd;// 船员名单

		private Button btnDldl;// 登轮登陆
	}

	private OnClickListener clickListener = new OnClickListener() {
		/** 处理点击右边按钮操作 */
		public void onClick(View v) {
			int position = Integer.parseInt(v.getTag().toString());
			switch (v.getId()) {
			case R.id.kacbqk_ship_list_item_cbxq:
				// 跳转到船舶详情页面
				Message msg = new Message();
				msg.what = 100;
				msg.obj = dataList.get(position);
				handler.sendMessage(msg);
				break;
			case R.id.kacbqk_ship_list_item_cymd:
				// 获取船员名单
				msg = new Message();
				msg.what = 101;
				msg.obj = dataList.get(position);
				handler.sendMessage(msg);
				break;
			case R.id.kacbqk_ship_list_item_dldl:
				// 获取登轮登陆情况
				msg = new Message();
				msg.what = 102;
				msg.obj = dataList.get(position);
				handler.sendMessage(msg);
				break;

			default:
				break;
			}
		}
	};

	@Override
	public void setCheckTab(int selectTab) {
		// TODO Auto-generated method stub
		this.selectNum = selectTab;
	}
}
