package com.pingtech.hgqw.activity;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ImageView;
import android.widget.ListView;

import com.pingtech.R;
import com.pingtech.hgqw.adapter.InspectPlaceExListAdapter;
import com.pingtech.hgqw.adapter.InspectPlaceListAdapter;
import com.pingtech.hgqw.entity.BaseInfoElement;
import com.pingtech.hgqw.interf.OnHttpResult;
import com.pingtech.hgqw.utils.BaseInfoData;
import com.pingtech.hgqw.utils.BaseInfoData.OnCallBack;
import com.pingtech.hgqw.utils.BasicNameValuePair;

/**
 * 类描述：巡检地点选择
 * 
 * <p>
 * Title: 江海港边检勤务综合管理系统-InspectPlace.java
 * </p>
 * <p>
 * Copyright: Copyright (c) 2012
 * </p>
 * <p>
 * Company: 品恩科技
 * </p>
 * 
 * @author liums
 * @version 1.0
 * @date 2013-5-8 上午11:08:53
 */
public class InspectPlace extends MyActivity implements OnHttpResult, OnCallBack {
	private ExpandableListView expandableListView;

	private ListView listView;

	private List<BaseInfoElement> group; // 组列表

	private List<List<BaseInfoElement>> child; // 子列表

	private InspectPlaceExListAdapter adapter;

	private InspectPlaceListAdapter listViewAdapter;

	private ArrayList<BaseInfoElement> mDockList = BaseInfoData.mDockList;// 码头列表

	private ArrayList<BaseInfoElement> mBerthList = BaseInfoData.mBerthList;// 泊位列表

	private ArrayList<BaseInfoElement> mKkAreaList = BaseInfoData.mKkAreaList;// 卡口区域列表

	private int groupPosition = -1;

	private int groupPositionForBw = -1;

	private int childPosition = -1;

	private int qyPosition = -1;

	/** 标识选中的类型：0码头、1泊位、2卡口区域 */
	private int selectedFlag = -1;

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				resetPositions();
				groupPosition = msg.arg1;
				group.get(groupPosition).setChecked(true);

				selectedFlag = 0;// 0码头、1泊位、2卡口区域

				adapter.notifyDataSetChanged();// 刷新列表
				listViewAdapter.notifyDataSetChanged();// 刷新列表
				break;
			case 1:// 泊位
				resetPositions();
				selectedFlag = 1;// 0码头、1泊位、2卡口区域
				groupPositionForBw = msg.arg1;
				childPosition = msg.arg2;

				child.get(groupPositionForBw).get(childPosition).setChecked(true);

				adapter.notifyDataSetChanged();// 刷新列表
				listViewAdapter.notifyDataSetChanged();// 刷新列表
				break;
			case 2:// 区域
				resetPositions();
				selectedFlag = 2;// 0码头、1泊位、2卡口区域
				qyPosition = msg.arg1;
				mKkAreaList.get(qyPosition).setChecked(true);
				adapter.notifyDataSetChanged();// 刷新列表
				listViewAdapter.notifyDataSetChanged();// 刷新列表
				break;
			default:
				break;
			}
			super.handleMessage(msg);
		}

	};

	/**
	 * @方法名：resetPositions
	 * @功能说明：重置选中标志位
	 * @author liums
	 * @date 2013-5-9 下午1:07:09
	 */
	private void resetPositions() {
		if (groupPosition != -1) {
			group.get(groupPosition).setChecked(false);
		}
		if (groupPositionForBw != -1 && childPosition != -1) {
			child.get(groupPositionForBw).get(childPosition).setChecked(false);
		}
		if (qyPosition != -1) {
			mKkAreaList.get(qyPosition).setChecked(false);
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.inspect_place_list);
//		setContentView(R.layout.inspect_place_list);
		setMyActiveTitle(getText(R.string.xunchaxunjian) + ">" + getText(R.string.normalxunjian) + ">" + getText(R.string.inspectPlace));
		expandableListView = (ExpandableListView) findViewById(R.id.inspect_place_list_exlistview);
		initializeMtBwData();
		initializeQyData();
		adapter = new InspectPlaceExListAdapter(getApplicationContext(), group, child, handler);
		expandableListView.setAdapter(adapter);

		expandableListView.setOnGroupClickListener(new OnGroupClickListener() {
			@Override
			public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
				return false;
			}
		});
		expandableListView.setOnChildClickListener(new OnChildClickListener() {

			@Override
			public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
				return false;
			}
		});

		expandableListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

			}
		});
	}

	/**
	 * @方法名：initializeQyData
	 * @功能说明：初始化区域列表
	 * @author liums
	 * @date 2013-5-9 下午12:44:02
	 */
	private void initializeQyData() {
		if (mKkAreaList == null || mKkAreaList.size() < 1 || mKkAreaList == null || mKkAreaList.size() < 1) {
			BaseInfoData.onParseXMLBaseInfoDataOnOffline();
		}
		listView = (ListView) findViewById(R.id.inspect_place_list_listview);
		listViewAdapter = new InspectPlaceListAdapter(getApplicationContext(), mKkAreaList, handler);
		listView.setAdapter(listViewAdapter);
	}

	/**
	 * 初始化码头泊位组、子列表数据
	 */
	private void initializeMtBwData() {
		group = new ArrayList<BaseInfoElement>();
		child = new ArrayList<List<BaseInfoElement>>();
		int length = BaseInfoData.TREEVIEW_NODE_SERARATE.length();
		if (mDockList == null || mDockList.size() < 1 || mBerthList == null || mBerthList.size() < 1) {
			BaseInfoData.onParseXMLBaseInfoDataOnOffline();
		}
		for (BaseInfoElement m : mDockList) {
			String mtId = m.getId();
			int i = mtId.lastIndexOf(BaseInfoData.TREEVIEW_NODE_SERARATE);
			mtId = mtId.substring(i + BaseInfoData.TREEVIEW_NODE_SERARATE.length());

			List<BaseInfoElement> c = new ArrayList<BaseInfoElement>();
			for (BaseInfoElement b : mBerthList) {
				String bwIdTemp = b.getId();
				String temp = bwIdTemp.substring(bwIdTemp.indexOf(BaseInfoData.TREEVIEW_NODE_SERARATE) + length);
				String mtIdTemp = temp.subSequence(temp.indexOf(BaseInfoData.TREEVIEW_NODE_SERARATE) + length,
						temp.lastIndexOf(BaseInfoData.TREEVIEW_NODE_SERARATE)).toString();
				if (mtId.equals(mtIdTemp)) {
					c.add(b);
				}
			}
			addInfo(m, c);
		}
	}

	/**
	 * 模拟给组、子列表添加数据
	 * 
	 * @param g
	 *            -group
	 * @param objects
	 *            -child
	 */
	private void addInfo(BaseInfoElement g, List<BaseInfoElement> c) {
		group.add(g);
		child.add(c);
	}

	/**
	 * @方法名：btnClick
	 * @功能说明：按钮控件监听
	 * @author liums
	 * @date 2013-5-8 下午8:45:35
	 * @param v
	 */
	public void btnClick(View v) {
		Intent intent = new Intent();
		Resources resource = (Resources) getBaseContext().getResources();
		ColorStateList csl = (ColorStateList) resource.getColorStateList(R.color.textcolor);
		ColorStateList csl_s = (ColorStateList) resource.getColorStateList(R.color.selectTextColor);
		switch (v.getId()) {
		case R.id.inspect_place_qy:
			((ImageView) findViewById(R.id.image_btn_send_by_card)).setImageResource(R.drawable.exception_line_n);
			((ImageView) findViewById(R.id.image_btn_send_by_name)).setImageResource(R.drawable.exception_line_s);
			((Button) findViewById(R.id.inspect_place_mtbw)).setTextColor(csl);
			((Button) findViewById(R.id.inspect_place_qy)).setTextColor(csl_s);
			findViewById(R.id.inspect_place_listview_layout_mtbw).setVisibility(View.GONE);
			findViewById(R.id.inspect_place_listview_layout_qy).setVisibility(View.VISIBLE);

			break;
		case R.id.inspect_place_mtbw:
			((ImageView) findViewById(R.id.image_btn_send_by_card)).setImageResource(R.drawable.exception_line_s);
			((ImageView) findViewById(R.id.image_btn_send_by_name)).setImageResource(R.drawable.exception_line_n);
			((Button) findViewById(R.id.inspect_place_mtbw)).setTextColor(csl_s);
			((Button) findViewById(R.id.inspect_place_qy)).setTextColor(csl);
			findViewById(R.id.inspect_place_listview_layout_mtbw).setVisibility(View.VISIBLE);
			findViewById(R.id.inspect_place_listview_layout_qy).setVisibility(View.GONE);
			break;

		case R.id.inspect_place_select_submit:
			switch (selectedFlag) {
			case 0:
				String idMt = group.get(groupPosition).getIds();
				intent = new Intent();
				intent.putExtra("type", 0);
				intent.putExtra("id", idMt);
				setResult(RESULT_OK, intent);
				finish();
				break;
			case 1:
				String idBw = child.get(groupPositionForBw).get(childPosition).getIds();
				intent.putExtra("type", 1);
				intent.putExtra("id", idBw);
				setResult(RESULT_OK, intent);
				finish();
				break;
			case 2:
				String idQy = mKkAreaList.get(qyPosition).getIds();
				intent.putExtra("type", 2);
				intent.putExtra("id", idQy);
				setResult(RESULT_OK, intent);
				finish();
				break;

			default:
				break;
			}
			break;

		default:
			break;
		}
	}

	/**
	 * @方法名：getInfoFromService
	 * @功能说明：从服务器请求数据
	 * @author liums
	 * @date 2013-5-8 下午9:19:07
	 * @param type
	 * @param idMt
	 */
	private ProgressDialog progressDialog = null;

	private void getInfoFromService(String type, String idMt) {
		String url = "getBaseInfoByCard";
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("id", idMt));
		params.add(new BasicNameValuePair("type", type));
		progressDialog = new ProgressDialog(this);
		progressDialog.setTitle(getString(R.string.waiting));
		progressDialog.setMessage(getString(R.string.waiting));
		progressDialog.setCancelable(false);
		progressDialog.setIndeterminate(false);
		progressDialog.show();
		// NetWorkManager.request(this, url, params,
		// HTTPREQUEST_TYPE_GET_BASEINFO_BY_CARD);
	}

	@Override
	public void onCallBack(boolean ret) {

	}

	@Override
	public void onHttpResult(String str, int httpRequestType) {

	}

	@Override
	protected void onPause() {
		resetPositions();
		selectedFlag = -1;
		adapter.notifyDataSetChanged();// 刷新列表
		listViewAdapter.notifyDataSetChanged();// 刷新列表
		super.onPause();
	}

}
