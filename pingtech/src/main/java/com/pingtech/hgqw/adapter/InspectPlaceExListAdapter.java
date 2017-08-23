package com.pingtech.hgqw.adapter;

import java.util.List;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.pingtech.R;
import com.pingtech.hgqw.entity.BaseInfoElement;

public class InspectPlaceExListAdapter extends BaseExpandableListAdapter {
	static  class ViewHolderPlace {
		/**
		 * 删除按钮
		 */
		Button btn;

		/**
		 * 用户名，显示在音频按钮侧面
		 */
		TextView text;
	}

	private List<BaseInfoElement> group; // 组列表

	private List<List<BaseInfoElement>> child; // 子列表

	private Context context;

	private LayoutInflater inflater;

	private View view;

	private Handler handler;

	public InspectPlaceExListAdapter(Context context, List<BaseInfoElement> group, List<List<BaseInfoElement>> child, Handler handler) {
		this.context = context;
		this.group = group;
		this.child = child;
		this.handler = handler;
		inflater = LayoutInflater.from(context);
	}

	@Override
	public int getGroupCount() {
		return group.size();
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return child.get(groupPosition).size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		return group.get(groupPosition);
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return child.get(groupPosition).get(childPosition);
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
		return getGenericViewMt(group.get(groupPosition) , groupPosition);

	}

	@Override
	public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
		// String str = child.get(groupPosition).get(childPosition);
		// String[] strArr = str.split(",");
		// String[] strArr01 = strArr[1].split("@");
		// strArr[1] = strArr01[0];
		// return getGenericView(strArr, 1, strArr01[1] , groupPosition , childPosition);

		return getGenericViewBw(child.get(groupPosition).get(childPosition),  groupPosition , childPosition);
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {

		return false;
	}

	private View getGenericViewMt(BaseInfoElement baseInfoElement, final int groupPosition) {
		view = inflater.inflate(R.layout.inspect_place_list_class, null);
		ViewHolderPlace holder = new ViewHolderPlace();
		holder.text = (TextView) view.findViewById(R.id.text);
		holder.btn = (Button) view.findViewById(R.id.icon);
		// holder.text.setTag(groupPosition);
		// holder.btn.setTag(childPosition);
		holder.text.setText(baseInfoElement.getOutlineTitle());
		if (baseInfoElement.getChecked()) {
			holder.btn.setBackgroundResource(R.drawable.radiobutton_check_on_n);
		} else {
			holder.btn.setBackgroundResource(R.drawable.radiobutton_check_off_n);
		}
		holder.btn.setFocusable(false);
		holder.btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Message msg = new Message();
				msg.what = 0;
				msg.arg1 = groupPosition;
				handler.sendMessage(msg);
			}
		});
		view.setTag(holder);
		return view;
	}

	private View getGenericViewBw(BaseInfoElement baseInfoElement, final int groupPosition, final int childPosition) {
		view = inflater.inflate(R.layout.inspect_place_list_class, null);
		ViewHolderPlace holder = new ViewHolderPlace();
		holder.text = (TextView) view.findViewById(R.id.text);
		holder.btn = (Button) view.findViewById(R.id.icon);
		// holder.text.setTag(groupPosition);
		// holder.btn.setTag(childPosition);
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
				msg.what = 1;
				msg.arg1 = groupPosition;
				msg.arg2 = childPosition;
				handler.sendMessage(msg);
			}
		});
		view.setTag(holder);
		return view;
	}

	// 创建组/子视图
	public View getGenericView(final String[] strArr, int flag, String checkFlag, final int groupPosition, final int childPosition) {
		view = inflater.inflate(R.layout.inspect_place_list_class, null);
		ViewHolderPlace holder = new ViewHolderPlace();
		holder.text = (TextView) view.findViewById(R.id.text);
		holder.btn = (Button) view.findViewById(R.id.icon);
		holder.text.setTag(groupPosition);
		holder.btn.setTag(childPosition);
		holder.text.setText(strArr[1]);
		if (flag == 0) {
			holder.btn.setFocusable(false);
			holder.btn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Message msg = new Message();
					msg.what = 0;
					msg.arg1 = groupPosition;
					msg.arg2 = childPosition;
					msg.obj = strArr[0];
					handler.sendMessage(msg);
				}
			});
		} else {
			holder.btn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Message msg = new Message();
					msg.what = 1;
					msg.obj = strArr[0];
					msg.arg1 = groupPosition;
					msg.arg2 = childPosition;
					handler.sendMessage(msg);
				}
			});
		}
		if ("1".equals(checkFlag)) {
			holder.btn.setBackgroundResource(R.drawable.radiobutton_check_on_n);
		} else {
			holder.btn.setBackgroundResource(R.drawable.radiobutton_check_off_n);
		}

		view.setTag(holder);
		return view;
	}
}