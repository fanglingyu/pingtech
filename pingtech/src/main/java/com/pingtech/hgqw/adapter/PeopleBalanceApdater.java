package com.pingtech.hgqw.adapter;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.pingtech.R;
import com.pingtech.hgqw.interf.OnChangeTab;
import com.pingtech.hgqw.utils.DataDictionary;
import com.pingtech.hgqw.utils.StringUtils;

public class PeopleBalanceApdater extends BaseAdapter implements OnChangeTab {

	private LayoutInflater mInflater;

	List<Map<String, String>> dataList;

	private String title_down = "";

	private String title_up = "";

	private boolean iskk = false;// 是否卡口人员平衡

	private Context context;

	/**
	 * 当前选中的是哪个Tab: 1,登轮人员；2：登陆船员
	 */
	private int selectNum = 1;

	public PeopleBalanceApdater(Context context, List<Map<String, String>> dataList, String kkid) {
		this.dataList = dataList;
		this.context = context;
		this.mInflater = LayoutInflater.from(context);
		if (StringUtils.isNotEmpty(kkid)) {
			iskk = true;
		}
		init();
	}

	private void init() {
		if (iskk) {
			title_down = context.getString(R.string.person_balance_downtime_kk);
			title_up = context.getString(R.string.person_balance_uptime_kk);
		} else {
			title_down = context.getString(R.string.person_balance_downtime_tag);
			title_up = context.getString(R.string.person_balance_uptime_tag);

		}
	}

	public PeopleBalanceApdater(Context context, List<Map<String, String>> dataList) {
		this.dataList = dataList;
		this.context = context;
		this.mInflater = LayoutInflater.from(context);
		init();
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
		// TODO Auto-generated method stub
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.personbalance_listview_class, null);
			holder.title = (TextView) convertView.findViewById(R.id.title);
			holder.name = (TextView) convertView.findViewById(R.id.name);
			holder.sex = (TextView) convertView.findViewById(R.id.sex);
			holder.country = (TextView) convertView.findViewById(R.id.country);
			holder.cardType = (TextView) convertView.findViewById(R.id.cardtype);
			holder.cardNum = (TextView) convertView.findViewById(R.id.cardnum);
			holder.time = (TextView) convertView.findViewById(R.id.time);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.title.setVisibility(View.GONE);
		convertView.findViewById(R.id.left_line).setVisibility(View.VISIBLE);
		convertView.findViewById(R.id.right_line).setVisibility(View.VISIBLE);
		holder.name.setVisibility(View.VISIBLE);
		holder.sex.setVisibility(View.VISIBLE);
		holder.country.setVisibility(View.VISIBLE);
		holder.cardType.setVisibility(View.VISIBLE);
		holder.cardNum.setVisibility(View.VISIBLE);
		holder.time.setVisibility(View.VISIBLE);
		Map<String, String> map = dataList.get(position);
		if (holder.name != null) {
			holder.name.setText(map.get("xm") == null ? "" : map.get("xm"));
		}
		if (holder.sex != null) {
			holder.sex.setText(DataDictionary.getDataDictionaryName(map.get("xb"), DataDictionary.DATADICTIONARY_TYPE_SEX_TYPE));
		}
		if (holder.country != null) {
			holder.country.setText(context.getString(R.string.person_balance_country) + DataDictionary.getCountryName(map.get("gj")));
		}
		if (holder.cardType != null) {
			holder.cardType.setText(context.getString(R.string.sel_person_cardtype)
					+ DataDictionary.getDataDictionaryName(map.get("zjzl"), DataDictionary.DATADICTIONARY_TYPE_CERTIFICATES_TYPE));
		}
		if (holder.cardNum != null) {
			holder.cardNum.setText(context.getString(R.string.idcardnum) + (map.get("zjhm") == null ? "" : map.get("zjhm")));
		}
		if (holder.time != null) {
			if (selectNum == 1) {
				holder.time.setText(title_up + (map.get("time") == null ? "" : map.get("time")));
			} else if (selectNum == 2) {
				holder.time.setText(title_down + (map.get("time") == null ? "" : map.get("time")));
			}
		}
		return convertView;
	}

	static class ViewHolder {
		private TextView title;

		private TextView name;

		private TextView sex;

		private TextView country;

		private TextView cardType;

		private TextView cardNum;

		private TextView time;
	}

	@Override
	public void setCheckTab(int selectTab) {
		// TODO Auto-generated method stub
		this.selectNum = selectTab;
	}
}
