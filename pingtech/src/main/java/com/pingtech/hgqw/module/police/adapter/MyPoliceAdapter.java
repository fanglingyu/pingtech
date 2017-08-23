package com.pingtech.hgqw.module.police.adapter;

import java.util.Map;

import android.content.Context;
import android.graphics.Color;
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
import com.pingtech.hgqw.base.BaseApplication;
import com.pingtech.hgqw.module.police.entity.Qwzlqwjs;
import com.pingtech.hgqw.module.qwjw.utils.QwzlConstant;
import com.pingtech.hgqw.utils.StringUtils;
import com.pingtech.hgqw.utils.SystemSetting;

public class MyPoliceAdapter extends BaseAdapter {

	private LayoutInflater mInflater;

	protected Context context;

	private Handler handler;

	private int from = 0;

	private ViewHolder holder = null;

	public static final int POSITIONEXCEPT = 0;

	public static final int POSITIONNOEXCEPT = 1;

	/**
	 * 
	 * @param context
	 * @param handler
	 * @param from
	 *            0警务指令，1勤务指令
	 */
	public MyPoliceAdapter(Context context, Handler handler, int from) {
		this.context = context;
		this.mInflater = LayoutInflater.from(context);
		this.handler = handler;
		this.from = from;
	}

	public MyPoliceAdapter() {
		this.mInflater = LayoutInflater.from(BaseApplication.instent);
	}

	@Override
	public int getCount() {
		int size = 0;
		switch (from) {
		case 0:
			size = SystemSetting.taskList == null ? 0 : SystemSetting.taskList.size();
			break;
		case 1:
			size = SystemSetting.qwzlList == null ? 0 : SystemSetting.qwzlList.size();
			break;
		default:
			break;
		}
		if (size == 0) {
			size = 1;
		}
		return size;

	}

	@Override
	public Object getItem(int arg0) {
		return 0;
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	private OnClickListener clicklistenerQwzl = new OnClickListener() {
		/** 处理点击列表右边的按钮 */
		public void onClick(View v) {
			int position = Integer.parseInt(v.getTag().toString());
			switch (v.getId()) {
			case R.id.detail_btn:
				handler.obtainMessage(1, position, -1).sendToTarget();
				break;
			case R.id.operate_btn:
				handler.obtainMessage(0, position, -1).sendToTarget();
				break;

			default:
				break;
			}

		}
	};

	private OnClickListener clicklistener = new OnClickListener() {
		/** 处理点击列表右边的按钮 */
		public void onClick(View v) {
			Message msg = new Message();
			int position = Integer.parseInt(v.getTag().toString());
			if (position % 2 == 1) {
				// 详情
				msg.what = 1;
				msg.arg1 = position;
				handler.sendMessage(msg);

			} else if (position % 2 == 0) {
				msg.what = 0;
				msg.arg1 = position;
				handler.sendMessage(msg);
			}
		}
	};

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		switch (from) {
		case 0:// 警务指令
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.police_listview_class, null);
				holder = new ViewHolder();
			}
			jwzl(position, convertView);
			break;
		case 1:// 勤务指令
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.police_listview_qwzl_class, null);
				holder = new ViewHolder();
			}
			qwzl(position, convertView);
			break;

		default:
			break;
		}
		return convertView;

	}

	private void qwzl(int position, View convertView) {
		boolean empty = false;
		if ((SystemSetting.qwzlList == null) || (SystemSetting.qwzlList.size() == 0)) {
			empty = true;
		}
		holder.index = (TextView) convertView.findViewById(R.id.index);
		holder.zllx = (TextView) convertView.findViewById(R.id.zllx);
		holder.cbmc = (TextView) convertView.findViewById(R.id.cbmc);
		holder.fbdw = (TextView) convertView.findViewById(R.id.fbdw);
		holder.fbsj = (TextView) convertView.findViewById(R.id.fbsj);
		holder.qsr = (TextView) convertView.findViewById(R.id.qsr);
		holder.qssj = (TextView) convertView.findViewById(R.id.qssj);
		holder.qsrinfo = (TextView) convertView.findViewById(R.id.qsrinfo);
		holder.qssjinfo = (TextView) convertView.findViewById(R.id.qssjinfo);
		holder.operateBtn = (Button) convertView.findViewById(R.id.operate_btn);
		holder.detailBtn = (Button) convertView.findViewById(R.id.detail_btn);
		holder.operateBtn.setTag(position);
		holder.operateBtn.setOnClickListener(clicklistenerQwzl);
		holder.detailBtn.setTag(position);
		holder.detailBtn.setOnClickListener(clicklistenerQwzl);
		convertView.setTag(holder);

		if (empty) {
			(convertView.findViewById(R.id.police_lv_item_layout_all)).setVisibility(View.GONE);
			(convertView.findViewById(R.id.police_lv_item_tv_empty)).setVisibility(View.VISIBLE);
		} else {
			(convertView.findViewById(R.id.police_lv_item_layout_all)).setVisibility(View.VISIBLE);
			(convertView.findViewById(R.id.police_lv_item_tv_empty)).setVisibility(View.GONE);
			Qwzlqwjs qwzlqwjs = SystemSetting.qwzlList.get(position);
			String qszt = qwzlqwjs.getQszt();
			String zlzt = qwzlqwjs.getZlzt();

			if (holder.index != null) {
				holder.index.setText((position + 1) + "");
			}
			if (holder.zllx != null) {
				holder.zllx.setText(QwzlConstant.getZllxName(qwzlqwjs.getZllx()));
			}
			if (holder.cbmc != null) {
				holder.cbmc.setText(qwzlqwjs.getCbzwm());
			}
			if (holder.fbdw != null) {
				holder.fbdw.setText(qwzlqwjs.getFbdw());
			}
			if (holder.fbsj != null) {
				holder.fbsj.setText(qwzlqwjs.getFbsj());
			}
			if (holder.qsr != null) {
				holder.qsr.setText(qwzlqwjs.getQsr());
			}
			if (holder.qssj != null) {
				holder.qssj.setText(qwzlqwjs.getQssj());
			}

			if (zlzt != null && zlzt.equals("1")) {
				// 已取消显示
				holder.operateBtn.setText(R.string.canceled);
				holder.operateBtn.setBackgroundColor(0x00000000);
				holder.operateBtn.setTextColor(Color.RED);
				holder.operateBtn.setEnabled(false);
			}

			if (qszt != null && !qszt.equals(QwzlConstant.QSZT_WQS)) {
				holder.operateBtn.setText(R.string.yiqianshou);
				holder.operateBtn.setEnabled(false);
				// holder.qsr.setVisibility(View.VISIBLE);
				// holder.qssj.setVisibility(View.VISIBLE);
				// holder.qsrinfo.setVisibility(View.VISIBLE);
				// holder.qssjinfo.setVisibility(View.VISIBLE);
				if (StringUtils.isEmpty(qwzlqwjs.getQsr())) {
					holder.qsr.setVisibility(View.GONE);
					holder.qsrinfo.setVisibility(View.GONE);
				} else {
					holder.qsr.setVisibility(View.VISIBLE);
					holder.qsrinfo.setVisibility(View.VISIBLE);
				}
				if (StringUtils.isEmpty(qwzlqwjs.getQssj())) {
					holder.qssj.setVisibility(View.GONE);
					holder.qssjinfo.setVisibility(View.GONE);
				} else {
					holder.qssj.setVisibility(View.VISIBLE);
					holder.qssjinfo.setVisibility(View.VISIBLE);
				}
			} else {
				holder.operateBtn.setText(R.string.qianshou);
				holder.operateBtn.setEnabled(true);
				holder.qsr.setVisibility(View.GONE);
				holder.qssj.setVisibility(View.GONE);
				holder.qsrinfo.setVisibility(View.GONE);
				holder.qssjinfo.setVisibility(View.GONE);
			}

			holder.detailBtn.setText(R.string.detail);
			holder.detailBtn.setEnabled(true);
		}
	}

	private void jwzl(int position, View convertView) {
		boolean empty = false;
		if ((SystemSetting.taskList == null) || (SystemSetting.taskList.size() == 0)) {
			empty = true;
		}

		holder.index = (TextView) convertView.findViewById(R.id.index);
		holder.jqlb = (TextView) convertView.findViewById(R.id.jqlb);
		holder.cjfzr = (TextView) convertView.findViewById(R.id.cjfzr);
		holder.pzr = (TextView) convertView.findViewById(R.id.pzr);
		holder.fbr = (TextView) convertView.findViewById(R.id.fbr);
		holder.fbsj = (TextView) convertView.findViewById(R.id.fbsj);
		holder.operateBtn = (Button) convertView.findViewById(R.id.operate_btn);
		holder.detailBtn = (Button) convertView.findViewById(R.id.detail_btn);
		holder.operateBtn.setTag(position * 2);
		holder.operateBtn.setOnClickListener(clicklistener);
		holder.detailBtn.setTag(position * 2 + 1);
		holder.detailBtn.setOnClickListener(clicklistener);
		// 勤务
		holder.zllx = (TextView) convertView.findViewById(R.id.zllx);
		convertView.setTag(holder);

		if (empty) {
			(convertView.findViewById(R.id.police_lv_item_layout_all)).setVisibility(View.GONE);
			(convertView.findViewById(R.id.police_lv_item_tv_empty)).setVisibility(View.VISIBLE);
		} else {
			Map<String, Object> jwzl = SystemSetting.taskList.get(position);
			(convertView.findViewById(R.id.police_lv_item_layout_all)).setVisibility(View.VISIBLE);
			(convertView.findViewById(R.id.police_lv_item_tv_empty)).setVisibility(View.GONE);

			if (holder.index != null) {
				holder.index.setText((position + 1) + "");
			}
			if (holder.jqlb != null) {
				holder.jqlb.setText((String) jwzl.get("jqlb"));
			}
			if (holder.cjfzr != null) {
				holder.cjfzr.setText((String) jwzl.get("cjfzr"));
			}
			if (holder.pzr != null) {
				holder.pzr.setText((String) jwzl.get("pzr"));
			}
			if (holder.fbr != null) {
				holder.fbr.setText((String) jwzl.get("fbr"));
			}
			if (holder.fbsj != null) {
				holder.fbsj.setText((String) jwzl.get("fbsj"));
			}
			String qszt = (String) jwzl.get("qszt");
			String zlzt = (String) jwzl.get("zlzt");
			if (zlzt != null && zlzt.equals("1")) {
				// 已取消显示
				holder.operateBtn.setText(R.string.canceled);
				holder.operateBtn.setBackgroundColor(0x00000000);
				holder.operateBtn.setTextColor(Color.RED);
				holder.operateBtn.setEnabled(false);
			} else {
				if (qszt != null && !qszt.equals("0")) {
					holder.operateBtn.setText(R.string.yiqianshou);
					holder.operateBtn.setEnabled(false);
				} else {
					holder.operateBtn.setText(R.string.qianshou);
					holder.operateBtn.setEnabled(true);
				}
			}
			holder.detailBtn.setText(R.string.detail);
			holder.detailBtn.setEnabled(true);
		}
	}

	static class ViewHolder {
		private TextView index;

		private TextView jqlb;

		private TextView cjfzr;

		private TextView pzr;

		private TextView fbr;

		private TextView fbsj;

		private Button operateBtn;

		private Button detailBtn;

		// 勤务指令
		/** 指令类型;列表 */
		private TextView zllx;

		/** 船舶名称;列表 */
		private TextView cbmc;

		/** 现监护方式 */
		private TextView xjhfs;

		/** 变更后监护方式 */
		private TextView bghjhfs;

		/** 当前停靠位置 */
		private TextView dqtkwz;

		/** 移往位置 */
		private TextView ywwz;

		/** 预移泊时间 */
		private TextView yybsj;

		/** 预靠泊时间 */
		private TextView ykbsj;

		/** 工作要求 */
		private TextView gzyq;

		/** 发布单位;列表 */
		private TextView fbdw;

		// private TextView fbsj;//发布时间;列表
		// private TextView fbr;//发布人;
		/** 签收人;列表 */
		private TextView qsrinfo;

		/** 签收时间;列表 */
		private TextView qssjinfo;

		/** 签收人;列表 */
		private TextView qsr;

		/** 签收时间;列表 */
		private TextView qssj;

		/** 带队人 */
		private TextView ddr;

		/** 出勤人员 */
		private TextView cqry;
	}

}
