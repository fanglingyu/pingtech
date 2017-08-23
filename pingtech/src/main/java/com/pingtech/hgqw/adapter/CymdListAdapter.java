package com.pingtech.hgqw.adapter;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.pingtech.R;
import com.pingtech.hgqw.utils.ColorConstant;
import com.pingtech.hgqw.utils.DataDictionary;

/**
 * 
 * 
 * 类描述：获取船员名单信息适配器
 * 
 * <p>
 * Title: 系统名称-CymdListAdapter.java
 * </p>
 * <p>
 * Copyright: Copyright (c) 2012
 * </p>
 * <p>
 * Company: 品恩科技
 * </p>
 * 
 * @author jiajw
 * @version 1.0
 * @date 2014-1-17 上午10:44:50
 */
public class CymdListAdapter extends BaseAdapter {

	protected Context context;

	private ArrayList<HashMap<String, String>> personInfoList;

	public CymdListAdapter(Context context, ArrayList<HashMap<String, String>> personInfoList) {
		super();
		this.context = context;
		this.personInfoList = personInfoList;
	}

	@Override
	public int getCount() {
		return personInfoList != null ? personInfoList.size() : 0;
	}

	@Override
	public Object getItem(int position) {
		return personInfoList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		// HashMap<String, String> entityMap=personInfoList.get(position);
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.kacbqk_sailor_list_item, parent, false);
			holder = new ViewHolder();
			holder.index = (TextView) convertView.findViewById(R.id.col1);
			holder.name = (TextView) convertView.findViewById(R.id.xm_tv);
			holder.sex = (TextView) convertView.findViewById(R.id.xb_tv);
			holder.office = (TextView) convertView.findViewById(R.id.zw_tv);
			holder.cardType = (TextView) convertView.findViewById(R.id.zjzl_tv);
			holder.cardNum = (TextView) convertView.findViewById(R.id.zjhm_tv);
			holder.csrq = (TextView) convertView.findViewById(R.id.csrq_tv);
			holder.lcbz = (TextView) convertView.findViewById(R.id.lcbz_tv);
			holder.colorTag = (TextView) convertView.findViewById(R.id.status_tag);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		if (holder.index != null) {
			holder.index.setText((position + 1) + "");
		}
		if (holder.name != null) {
			holder.name.setText(personInfoList.get(position).get("xm") == null ? "" : personInfoList.get(position).get("xm"));
		}
		if (holder.sex != null) {
			holder.sex.setText(DataDictionary.getDataDictionaryName(personInfoList.get(position).get("xb"),
					DataDictionary.DATADICTIONARY_TYPE_SEX_TYPE) == null ? "" : DataDictionary.getDataDictionaryName(personInfoList.get(position)
					.get("xb"), DataDictionary.DATADICTIONARY_TYPE_SEX_TYPE));
		}
		if (holder.office != null) {
			String office_s = personInfoList.get(position).get("zw");
			String hgzl_s = personInfoList.get(position).get("hgzl");
			String str = "";
			if (office_s != null) {
				if (hgzl_s != null && hgzl_s.equals("50")) {
					str = DataDictionary.getDataDictionaryOfficeName(office_s, DataDictionary.DATADICTIONARY_TYPE_CBYGZW);
				} else {
					str = DataDictionary.getDataDictionaryOfficeName(office_s, DataDictionary.DATADICTIONARY_TYPE_DLRYZW);
				}
			}
			holder.office.setText(str == null ? "" : str);
		}
		if (holder.cardType != null) {
			holder.cardType.setText(DataDictionary.getDataDictionaryName(personInfoList.get(position).get("zjlx"),
					DataDictionary.DATADICTIONARY_TYPE_CERTIFICATES_TYPE) == null ? "" : DataDictionary.getDataDictionaryName(
					personInfoList.get(position).get("zjlx"), DataDictionary.DATADICTIONARY_TYPE_CERTIFICATES_TYPE));
		}
		if (holder.cardNum != null) {
			holder.cardNum.setText(personInfoList.get(position).get("zjhm") == null ? "" : personInfoList.get(position).get("zjhm"));
		}
		if (holder.csrq != null) {
			holder.csrq.setText(personInfoList.get(position).get("csrq") == null ? "" : personInfoList.get(position).get("csrq"));
		}

		if (holder.lcbz != null) {

			String lcbz = personInfoList.get(position).get("lcbz");
			// 0：在船 , 1：离船、2：登船
			if ("1".equals(lcbz)) {
				holder.lcbz.setText(context.getString(R.string.cydt_leave));
				holder.lcbz.setTextColor(ColorConstant.BLUE);
				holder.colorTag.setTextColor(ColorConstant.BLUE);

			} else if ("2".equals(lcbz)) {
				holder.lcbz.setText(context.getString(R.string.cydt_dengc));
				holder.lcbz.setTextColor(ColorConstant.RED);
				holder.colorTag.setTextColor(ColorConstant.RED);

			} else if ("0".equals(lcbz)) {
				holder.lcbz.setText(context.getString(R.string.cydt_login));
				holder.lcbz.setTextColor(ColorConstant.GRAY);
				holder.colorTag.setTextColor(ColorConstant.GRAY);
			} else {
				holder.lcbz.setText("");
				holder.lcbz.setTextColor(ColorConstant.GRAY);
				holder.colorTag.setTextColor(ColorConstant.GRAY);
			}
		}
		return convertView;
	}

	public static class ViewHolder {
		private TextView index;

		private TextView name;

		private TextView sex;

		private TextView office;

		private TextView cardType;

		private TextView cardNum;

		private TextView csrq;

		private TextView lcbz;

		private TextView colorTag;
	}
}
