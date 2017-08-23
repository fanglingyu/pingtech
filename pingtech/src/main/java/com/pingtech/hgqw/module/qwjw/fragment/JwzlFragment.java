package com.pingtech.hgqw.module.qwjw.fragment;

import java.util.Map;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.pingtech.R;
import com.pingtech.hgqw.module.police.activity.MyPoliceDetail;
import com.pingtech.hgqw.module.police.adapter.MyPoliceAdapter;
import com.pingtech.hgqw.module.police.entity.MyPolice;
import com.pingtech.hgqw.module.police.request.RequestPolice;
import com.pingtech.hgqw.utils.SystemSetting;

public class JwzlFragment extends Fragment {

	private static final String TAG = "JwzlFragment";

	private MyPoliceAdapter adapter = null;

	private TextView mypolice_empty;

	private ListView listView;

	private View mView = null;

	private Handler handler = null;

	/** 进入签收（详情）界面 */
	private static final int STARTACTIVITY_FOR_POLICE_DICTATE_QIANSHOU = 1;

	/** 进入反馈界面（暂时不需要该功能） */
	private static final int STARTACTIVITY_FOR_POLICE_DICTATE_FEEDBACK = 2;

	private Context context = null;

	private Activity acitvity = null;

	private Map<String, Object> map;

	public JwzlFragment(Context context, Activity acitvity) {
		this.context = context;
		this.acitvity = acitvity;
	}

	@Override
	public View getView() {
		Log.i(TAG, "getView");
		return mView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		Log.i(TAG, "onActivityCreated");
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "onCreate");
		handler = new PoliceHandler();
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.i(TAG, "onCreateView");
		View root = inflater.inflate(R.layout.police_list, null, true);
		find(root);
		mypolice_empty.setVisibility(View.GONE);
		listView.setVisibility(View.VISIBLE);
		adapter = new MyPoliceAdapter(context, handler , 0);
		listView.setAdapter(adapter);
		mView = root;
		return mView;
	}

	private void find(View root) {
		listView = (ListView) root.findViewById(R.id.listview);
		mypolice_empty = (TextView) root.findViewById(R.id.mypolice_empty);
	}

	@Override
	public void onDestroy() {
		Log.i(TAG, "onDestroy");
		super.onDestroy();
	}

	@Override
	public void onDestroyView() {
		Log.i(TAG, "onDestroyView");
		super.onDestroyView();
	}

	@Override
	public void onPause() {
		Log.i(TAG, "onPause");
		super.onPause();
	}

	@Override
	public void onResume() {
		Log.i(TAG, "onResume");
		super.onResume();
	}

	@Override
	public void onStart() {
		Log.i(TAG, "onStart");
		super.onStart();
	}

	private class PoliceHandler extends Handler {
		/** 进入签收（详情）界面 */
		private static final int STARTACTIVITY_FOR_POLICE_DICTATE_QIANSHOU = 1;

		/** 进入反馈界面（暂时不需要该功能） */
		private static final int STARTACTIVITY_FOR_POLICE_DICTATE_FEEDBACK = 2;

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MyPoliceAdapter.POSITIONEXCEPT:
				map = SystemSetting.taskList.get(msg.arg1 / 2);
				String qszt = (String) map.get("qszt");
				if (qszt != null && qszt.equals("0")) {
					// 签收
					String taskid = (String) map.get("taskid");
					String  dwid = (String) map.get("dwid");
					String jwzldwid = (String) map.get("jwzldwid");
					String cjfzr = (String) map.get("cjfzr");
					String cjlx = (String) map.get("cjlx");
					new RequestPolice(context, this).requestSignMyTask(taskid,  dwid, "JWZL" , jwzldwid , cjfzr , cjlx);
				}

				break;
			case RequestPolice.SIGN_MY_TASK_SUCCESS:// 签收成功
				map.put("qszt", "1");
				adapter.notifyDataSetChanged();
				break;
			case MyPoliceAdapter.POSITIONNOEXCEPT:
				map = SystemSetting.taskList.get((msg.arg1 - 1) / 2);
				Intent intent = new Intent();
				MyPolice myPolice = new MyPolice();
				Bundle bundle = new Bundle();
				intent.putExtra("taskid", (String) map.get("taskid"));
				String qszt1 = (String) map.get("qszt");
				String zlzt = (String) map.get("zlzt");
				if ((qszt1 != null && qszt1.equals("1")) || (zlzt != null && zlzt.equals("1"))) {
					intent.putExtra("new", false);
				} else {
					intent.putExtra("new", true);
				}
				myPolice.setCjfzr((String) map.get("cjfzr"));
				myPolice.setFbr((String) map.get("fbr"));
				myPolice.setFbsj((String) map.get("fbsj"));
				myPolice.setJqlb((String) map.get("jqlb"));
				myPolice.setPzr((String) map.get("pzr"));
				myPolice.setZlnr((String) map.get("zlnr"));
				myPolice.setZlzt((String) map.get("zlzt"));
				myPolice.setQszt((String) map.get("qszt"));
				myPolice.setJwzldwid((String) map.get("jwzldwid"));
				bundle.putSerializable("myPolice", myPolice);
				intent.putExtras(bundle);
				String unReadStr = (String) map.get("unread");
				if (unReadStr == null || unReadStr.length() == 0) {
					intent.putExtra("unread", true);
					map.put("unread", "false");
				} else {
					intent.putExtra("unread", false);
				}
				intent.setClass(context, MyPoliceDetail.class);
				if (qszt1 != null && qszt1.equals("0")) {
					startActivityForResult(intent, STARTACTIVITY_FOR_POLICE_DICTATE_QIANSHOU);
				} else {
					startActivityForResult(intent, STARTACTIVITY_FOR_POLICE_DICTATE_FEEDBACK);
				}

				break;
			default:
				break;
			}
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case STARTACTIVITY_FOR_POLICE_DICTATE_QIANSHOU:
			if (resultCode == Activity.RESULT_OK) {
				map.put("qszt", "1");
				adapter.notifyDataSetChanged();
			}
			break;
		case STARTACTIVITY_FOR_POLICE_DICTATE_FEEDBACK:
			if (resultCode == Activity.RESULT_OK) {
				map.put("fkzt", "1");
				adapter.notifyDataSetChanged();
			}
			break;
		}
	}

}
