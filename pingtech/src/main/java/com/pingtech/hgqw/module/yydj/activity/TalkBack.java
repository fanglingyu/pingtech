package com.pingtech.hgqw.module.yydj.activity;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.xmlpull.v1.XmlPullParserException;

import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.pingtech.R;
import com.pingtech.hgqw.activity.MyActivity;
import com.pingtech.hgqw.entity.AudioFileUtils;
import com.pingtech.hgqw.entity.AudioInfo;
import com.pingtech.hgqw.entity.FlagUrls;
import com.pingtech.hgqw.entity.Flags;
import com.pingtech.hgqw.entity.GetUserList;
import com.pingtech.hgqw.entity.LoginUser;
import com.pingtech.hgqw.entity.UserInfo;
import com.pingtech.hgqw.interf.OnHttpResult;
import com.pingtech.hgqw.module.yydj.adapter.UpdateAudioListAdapter;
import com.pingtech.hgqw.module.yydj.utils.AudioUtil;
import com.pingtech.hgqw.mqtt.MqttContent;
import com.pingtech.hgqw.mqtt.interfaces.MessageHandler;
import com.pingtech.hgqw.mqtt.receiver.MessageReceiver;
import com.pingtech.hgqw.mqtt.service.MqttService;
import com.pingtech.hgqw.pullxml.PullXmlGetUserList;
import com.pingtech.hgqw.utils.AudioService;
import com.pingtech.hgqw.utils.Log;
import com.pingtech.hgqw.utils.StringUtils;
import com.pingtech.hgqw.web.NetWorkManager;
import com.pingtech.hgqw.widget.HgqwToast;

/**
 * 
 * 类描述：语音对讲
 * 
 * <p>
 * Title: 江海港边检勤务综合管理系统-TalkBack.java
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
 * @date 2013-4-16 下午1:58:11
 */
public class TalkBack extends MyActivity implements OnHttpResult, MessageHandler {
	private static final String TAG = "TalkBack";

	/** 控制语音录制时间一分钟 */
	protected static final int LOOPER_CHECK_ONE_MINUTE = 100;

	/** 控制语音录制时间一分钟 */
	protected static final long ONE_MINUTE = 1 * 60 * 1000;

	private boolean isPlaying = false;

	/** 控制语音录制时间定时器 */
	private Timer mTimer = null;

	/** 控制语音录制时间定时器 */
	private TimerTask mTimerTask = null;

	private AudioService audioService = new AudioService(TalkBack.this, TalkBack.this);

	/**
	 * 当前选择的用户ID
	 */
	private Set<String> checkedUser = new HashSet<String>();

	private Set<String> checkedUserName = new HashSet<String>();

	private List<UserInfo> userInfos;

	/**
	 * 用户全选按钮标志位
	 */
	private boolean flagAll = true;

	/**
	 * 用户添加按钮标志位
	 */
	private boolean userAddBtnFlag = true;

	/**
	 * 删除语音列表标志位
	 */
	private boolean delAudioBtnFlag = true;

	/**
	 * 视图切换标识
	 */
	private boolean audioViewShow = true;

	private ListView talk_back_audio_listview;

	/**
	 * 语音列表
	 */
	private View talk_back_list_audio_layout;

	/**
	 * 用户列表
	 */
	private View talk_back_user_layout;

	private View talk_back_user_layout_sv;

	/**
	 * 用户列表,底部按钮
	 */
	private View tb_layout_add_user_bottom;

	/**
	 * 发送语音
	 */
	private Button talk_back_list_button;

	/**
	 * 选择用户确认
	 */
	private Button talk_back_btn_user_enter;

	/**
	 * 选择用户取消
	 */
	private Button talk_back_btn_user_exit;

	/**
	 * 编辑用户列表按钮
	 */
	private Button talk_back_btn_edit;

	/**
	 * 全选用户按钮
	 */
	private Button talk_back_btn_sel_all;

	/**
	 * 选择用户
	 */
	private Button talk_back_list_btn_add_user;

	private GetUserList getUserList;

	private ProgressDialog progressDialog = null;

	private UpdateAudioListAdapter adapter;

	/**
	 * 当前登录对象ID
	 */
	private String userid;

	private List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();

	private String type = "";

	private String audioName;

	private String nameList;

	/**
	 * 开始录音时间
	 */
	private long timeBegin;

	/**
	 * 结束录音时间
	 */
	private long timeAfter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.talk_back_list);
		setContentView(R.layout.talk_back_list, 1);
		Log.i("日志", getClass().getName() + ":onCreate");
		Intent intent = getIntent();
		type = intent.getStringExtra("type");
		audioName = intent.getStringExtra("audioName");
		nameList = intent.getStringExtra("nameList");
		userid = LoginUser.getCurrentLoginUser().getUserID();
		this.customfindViewById();
		this.customSetAction();

		setMyActiveTitle(getString(R.string.talkbackstr));
		bindMessageReceiver();

		NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		manager.cancel(MessageReceiver.NOTIFICATION_ID);
	}

	@Override
	protected void onResume() {
		if (msgReceiver != null) {
			msgReceiver.registerHandler(this);
		}
		// 加载用户列表
		getUserListFromWeb();
		// 更新语音列表
		updateAudioList(false);
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		Log.i(TAG, "onDestroy()");
		msgReceiver.clearHandlers();
		try {
			unbindMessageReceiver();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		super.onDestroy();
		try {
			unbindMessageReceiver();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	@Override
	protected void onNewIntent(Intent intent) {
		type = intent.getStringExtra("type");
		bindMessageReceiver();
		// 来自通知接口
		if ("true".equals(type)) {
			// 更新语音列表
			updateAudioList(false);
		}
	}

	/**
	 * @方法名：customSetAction
	 * @功能说明：按钮动作监听
	 * @author liums
	 * @date 2013-4-24 下午4:18:27
	 */
	private void customSetAction() {
		talk_back_btn_sel_all.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// CheckBox全选
				if (flagAll) {
					talk_back_btn_sel_all.setText("取消");
					updateUserList(true, true);
					flagAll = false;

				} else {
					talk_back_btn_sel_all.setText("全选");
					updateUserList(false, true);
					flagAll = true;
				}

				// 如果取消，录音按钮置灰
				// 语音对讲功能按钮设置
				if (checkedUser != null && checkedUser.size() > 0) {
					talk_back_btn_user_enter.setText("确定(" + checkedUser.size() + ")");
					talk_back_list_button.setEnabled(true);
				} else {
					talk_back_btn_user_enter.setText("确定");
					talk_back_list_button.setEnabled(false);
				}
			}
		});
		/**
		 * 点击编辑，重新加载listView，显示删除按钮
		 */
		talk_back_btn_edit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (delAudioBtnFlag) {
					delAudioBtnFlag = false;
					// 更新语音列表
					updateAudioList(true);
				} else {
					delAudioBtnFlag = true;
					// 更新语音列表
					updateAudioList(false);
				}
			}
		});
	}

	/**
	 * @方法名：btnOnClick
	 * @功能说明：按钮点击触发事件，定义在layout中
	 * @author liums
	 * @date 2013-4-24 下午7:59:22
	 * @param v
	 */
	public void btnOnClick(View v) {
		switch (v.getId()) {
		case R.id.talk_back_btn_user_enter:
			// 返回语音列表页面,全选按钮隐藏、用户列表layout隐藏、确定、取消按钮Layout隐藏
			findViewById(R.id.talk_back_user_layout).setVisibility(View.GONE);
			findViewById(R.id.talk_back_user_layout_sv).setVisibility(View.GONE);
			findViewById(R.id.talk_back_btn_sel_all).setVisibility(View.GONE);
			findViewById(R.id.tb_layout_add_user_bottom).setVisibility(View.GONE);

			// 语音列表layout显示
			findViewById(R.id.talk_back_list_audio_layout).setVisibility(View.VISIBLE);

			// 语音列表与用户列表标志位重置。
			audioViewShow = true;
			break;
		case R.id.talk_back_btn_user_exit:
			// 返回语音列表页面,全选按钮隐藏、用户列表layout隐藏、确定、取消按钮Layout隐藏
			findViewById(R.id.talk_back_user_layout).setVisibility(View.GONE);
			findViewById(R.id.talk_back_user_layout_sv).setVisibility(View.GONE);
			findViewById(R.id.talk_back_btn_sel_all).setVisibility(View.GONE);
			findViewById(R.id.tb_layout_add_user_bottom).setVisibility(View.GONE);

			// 语音列表layout显示
			findViewById(R.id.talk_back_list_audio_layout).setVisibility(View.VISIBLE);

			// 语音列表与用户列表标志位重置。
			audioViewShow = true;
			break;

		default:
			break;
		}
	}

	/**
	 * 
	 * @方法名：getUserList
	 * @功能说明：请求在线用户列表
	 * @author liums
	 * @date 2013-4-16 上午10:52:18
	 * @return
	 */
	private void getUserListFromWeb() {
		String url = "getUserList";
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("userID", LoginUser.getCurrentLoginUser().getUserID()));

		NetWorkManager.request(TalkBack.this, url, params, FlagUrls.GET_USER_LIST);
	}

	/**
	 * 
	 * @方法名：customfindViewById
	 * @功能说明：控件赋值，监听
	 * @author liums
	 * @date 2013-4-18 上午10:28:24
	 */
	private boolean startFlag = false;

	private MediaRecorder recorder = null;

	private void customfindViewById() {
		talk_back_list_button = (Button) findViewById(R.id.talk_back_list_button);// 录音
		talk_back_btn_user_enter = (Button) findViewById(R.id.talk_back_btn_user_enter);// 选择用户确认
		talk_back_btn_user_exit = (Button) findViewById(R.id.talk_back_btn_user_exit);// 选择用户取消
		talk_back_btn_edit = (Button) findViewById(R.id.talk_back_btn_edit);// 编辑按钮
		talk_back_btn_sel_all = (Button) findViewById(R.id.talk_back_btn_sel_all);// 全选用户按钮
		talk_back_list_btn_add_user = (Button) findViewById(R.id.talk_back_list_btn_add_user);// 选择用户
		talk_back_audio_listview = (ListView) findViewById(R.id.talk_back_audio_listview);
		talk_back_audio_listview.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
		talk_back_list_audio_layout = (View) findViewById(R.id.talk_back_list_audio_layout);
		talk_back_user_layout = (View) findViewById(R.id.talk_back_user_layout);
		talk_back_user_layout_sv = (View) findViewById(R.id.talk_back_user_layout_sv);
		tb_layout_add_user_bottom = (View) findViewById(R.id.tb_layout_add_user_bottom);

		// 语音列表ListView设置
		adapter = new UpdateAudioListAdapter(this, data, handler01);
		talk_back_audio_listview.setAdapter(adapter);
		/*---------  ListView选择事件 ---------*/
		talk_back_audio_listview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

			}
		});

		/*--------- 发送语音按钮事件---------*/
		// 语音对讲功能按钮设置
		if (checkedUser != null && checkedUser.size() > 0) {
			talk_back_btn_user_enter.setText("确定(" + checkedUser.size() + ")");
			talk_back_list_button.setEnabled(true);
		} else {
			talk_back_btn_user_enter.setText("确定");
			talk_back_list_button.setEnabled(false);
		}
		talk_back_list_button.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				try {
					startFlag = true;
					if (recorder != null) {
						recorder.stop();// 停止刻录
						recorder.reset(); // 重设
						recorder.release(); // 刻录完成一定要释放资源
						recorder = null;
					}

					recorder = new MediaRecorder();
					recorder.setAudioSource(MediaRecorder.AudioSource.MIC);// 从麦克风采集声音
					recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);// 内容输出格式
					recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);// 音频编码方式
					recorder.setOutputFile(getUrl(checkedUser));
					recorder.prepare();
					recorder.start(); // 开始刻录
					timeBegin = new Date().getTime();
					looperCheckOneMinute();// 监听一分钟事件
				} catch (IllegalStateException e) {
					Log.log2File(TAG, "error:" + e.getMessage());
				} catch (IOException e) {
					Log.log2File(TAG, "error:" + e.getMessage());
				}
				return false;
			}
		});
		talk_back_list_button.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				int action = event.getAction();
				switch (action) {
				case MotionEvent.ACTION_DOWN:
					break;
				case MotionEvent.ACTION_UP:
					recordEnd();// 录音结束
					break;
				default:
					break;
				}
				return false;
			}

		});
		/*--------- 选择用户按钮事件---------*/
		talk_back_list_btn_add_user.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 加载用户列表
				getUserListFromWeb();
				if (audioViewShow) {
					talk_back_btn_sel_all.setVisibility(View.VISIBLE);
					talk_back_user_layout.setVisibility(View.VISIBLE);
					talk_back_user_layout_sv.setVisibility(View.VISIBLE);
					tb_layout_add_user_bottom.setVisibility(View.VISIBLE);
					talk_back_list_audio_layout.setVisibility(View.GONE);
					audioViewShow = false;
					// 语音对讲功能按钮设置
					if (checkedUser != null && checkedUser.size() > 0) {
						talk_back_btn_user_enter.setText("确定(" + checkedUser.size() + ")");
						talk_back_list_button.setEnabled(true);
					} else {
						talk_back_btn_user_enter.setText("确定");
						talk_back_list_button.setEnabled(false);
					}
				} else {

					talk_back_list_audio_layout.setVisibility(View.VISIBLE);

					talk_back_btn_sel_all.setVisibility(View.GONE);
					talk_back_user_layout.setVisibility(View.GONE);
					talk_back_user_layout_sv.setVisibility(View.GONE);
					tb_layout_add_user_bottom.setVisibility(View.GONE);
					audioViewShow = true;

					// 更新语音列表，暂不操作
				}

			}
		});
	}

	/**
	 * 
	 * @方法名：recordEnd
	 * @功能说明：录音结束
	 * @author liums
	 * @date 2013-12-10 下午3:53:55
	 */
	private void recordEnd() {
		try {
			if (audioService != null && startFlag) {
				startFlag = false;
				if (recorder != null) {
					recorder.stop();// 停止刻录
					recorder.reset(); // 重设
					recorder.release(); // 刻录完成一定要释放资源
					recorder = null;
				}
				timeAfter = new Date().getTime();
				// 取消一分钟控制定时器
				stopTimer();

				long time = (timeAfter - timeBegin) / 1000;
				if (time == 0) {
					time = 1;
				}
				audioService.upAudio(audioNameMic);

				// 拼装音频对象
				AudioInfo audioInfo = new AudioInfo();
				audioInfo.setFileName(audioNameMic);
				audioInfo.setFileNameDetail(audioNameDetail);
				audioInfo.setUserFromId(userid);
				audioInfo.setUserFromName(LoginUser.getCurrentLoginUser().getName());
				audioInfo.setUserToId(getCheckedUsers());
				audioInfo.setTime(time + "''");
				// FileUtils.saveTxtFiles(audioInfo);
				AudioFileUtils.saveTxtFilesToXml(audioInfo);
				audioService.upAudioTxt(audioNameDetail, audioInfo.getUserToId());
				// 创建音频附属txt
				// 更新语音列表
				updateAudioList(false);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @方法名：stopTimer
	 * @功能说明：取消一分钟控制定时器
	 * @author liums
	 * @date 2013-12-10 下午4:01:39
	 */
	private void stopTimer() {
		if (mTimer != null) {
			mTimer.cancel();
			mTimer = null;
		}

		if (mTimerTask != null) {
			mTimerTask.cancel();
			mTimerTask = null;
		}
	}

	/**
	 * @方法名：getCheckedUsers
	 * @功能说明：拼装用户id
	 * @author liums
	 * @date 2013-4-19 下午12:26:53
	 * @return
	 */
	private String getCheckedUsers() {
		StringBuilder stringBuilder = new StringBuilder();
		for (String s : checkedUser) {
			stringBuilder.append(s + " ");
		}
		return stringBuilder.toString();
	}

	/**
	 * @方法名：getUrl
	 * @功能说明：得到语音文件保存路径
	 * @author liums
	 * @date 2013-4-16 下午6:16:43
	 * @return
	 * @throws IOException
	 */
	private String audioNameMic;

	private String audioNameDetail;

	private String getUrl(Set<String> checkedUser) throws IOException {
		// sdcard路径
		File dir = new File(AudioFileUtils.path);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		if (checkedUser != null && checkedUser.size() == 1) {
			for (String s : checkedUser) {
				String temp = "a" + new Date().getTime();
				audioNameMic = temp + ".amr";
				audioNameDetail = temp + ".xml";
			}
		} else if (checkedUser != null && checkedUser.size() > 1) {
			// 发给多人
			String temp = "a" + new Date().getTime();
			audioNameMic = temp + ".amr";
			audioNameDetail = temp + ".xml";
		}
		File dirName = new File(AudioFileUtils.path + audioNameMic);
		if (!dirName.exists()) {
			dirName.createNewFile();
		}
		return dirName.getPath();
	}

	/**
	 * 
	 * @方法名：updateAudioList
	 * @功能说明：更新语音列表
	 * @author liums
	 * @date 2013-4-19 上午10:14:16
	 * @param delFlag
	 *            删除按钮显示标识：true显示，false不显示
	 */
	private void updateAudioList(boolean delFlag) {
		// 读取本地列表
		List<AudioInfo> audioInfos = AudioFileUtils.getLocalFileInfoList();
		// 本地列表根据最后修改时间排序。
		if (audioInfos != null && audioInfos.size() > 0) {
			data.clear();
			for (AudioInfo a : audioInfos) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("fileName", a.getFileName());
				map.put("time", a.getTime());
				map.put("userFromId", a.getUserFromId());
				map.put("userFromName", a.getUserFromName());
				map.put("userToId", a.getUserToId());
				map.put("userToName", a.getUserToName());
				map.put("type", a.getType());
				// 如果最后修改时间不是当天，只显示日期，否则只显示时间
				long lastModifiedTime = a.getLastModifiedTime();
				String lastModifiedTimeStr = this.setLastModifiedTime(lastModifiedTime);
				map.put("lastModifiedTime", lastModifiedTimeStr);
				if (delFlag) {
					map.put("delFlag", "0");// 删除按钮显示：0显示，1不显示
				} else {
					map.put("delFlag", "1");
				}
				data.add(map);
			}
		} else {
			// 本地没有文件，或手动全部删除：重置data，将编辑按钮复位
			data.clear();
			delAudioBtnFlag = true;
		}
		this.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				adapter.notifyDataSetChanged();
				talk_back_audio_listview.setSelection(adapter.getCount());
			}
		});

	}

	/**
	 * @方法名：setLastModifiedTime
	 * @功能说明：如果最后修改时间不是当天，只显示日期，否则只显示时间
	 * @author liums
	 * @date 2013-4-24 上午10:39:03
	 * @param lastModifiedTime
	 * @return
	 */
	private String setLastModifiedTime(long lastModifiedTime) {
		Date nowDate = new Date();
		Date fileDate = new Date(lastModifiedTime);
		SimpleDateFormat formatTime = new SimpleDateFormat("HH:mm:ss");
		SimpleDateFormat formatDay = new SimpleDateFormat("yyyy-MM-dd");
		if (nowDate.getYear() == fileDate.getYear() && nowDate.getMonth() == fileDate.getMonth() && nowDate.getDay() == fileDate.getDay()) {
			return formatTime.format(fileDate);
		} else {
			return formatDay.format(fileDate);
		}
	}

	@Override
	public void onHttpResult(String strTalkBack, int httpRequestType) {
		switch (httpRequestType) {
		case FlagUrls.GET_USER_LIST:
			Log.i(TAG, "onHttpResult() str：" + (strTalkBack != null));
			if (progressDialog != null) {
				progressDialog.dismiss();
				progressDialog = null;
			}
			if (strTalkBack == null || "".equals(strTalkBack)) {
				HgqwToast.makeText(this, R.string.data_download_failure_info, HgqwToast.LENGTH_SHORT).show();
				return;
			}
			try {
				userInfos = null;
				getUserList = PullXmlGetUserList.pullXml(strTalkBack);
				if (getUserList == null) {
					return;
				}
				if ("error".equals(getUserList.getResult())) {
					return;
				}
				userInfos = getUserList.getUserinfoList();
				// 更新用户列表页面
				updateUserList(false, false);

			} catch (XmlPullParserException e) {
				Log.log2File(TAG, "解析GetUserList报错：" + e.getMessage());
			} catch (IOException e) {
				Log.log2File(TAG, "解析GetUserList报错：" + e.getMessage());
			}
			break;
		case FlagUrls.TALK:
			break;
		default:
			break;
		}
	}

	/**
	 * @方法名：updateUserList
	 * @功能说明：更新用户列表
	 * @author liums
	 * @date 2013-4-19 上午9:14:07
	 * @param flag
	 *            是否全选标识
	 * @param fromFlag
	 *            全选按钮调用标志，只有为true时处理全选或取消全选操作。
	 */
	private void updateUserList(boolean flag, boolean fromFlag) {

		// CheckBox显示
		LinearLayout linearLayout = (LinearLayout) talk_back_user_layout;
		linearLayout.setOrientation(LinearLayout.VERTICAL);
		linearLayout.removeAllViews();
		if (userInfos != null && userInfos.size() > 0) {
			for (UserInfo u : userInfos) {
				View checkBoxView = (View) getLayoutInflater().inflate(R.layout.tb_layout_checkbox, null);
				final CheckBox checkBox = (CheckBox) checkBoxView.findViewById(R.id.tb_user_checkbox);
				TextView nameTextView = (TextView) checkBoxView.findViewById(R.id.tb_user_checkbox_name);
				checkBox.setTag(u.getId());
				if (checkedUser != null) {
					if (checkedUser.contains(u.getId())) {
						checkBox.setChecked(true);
					}
				}
				nameTextView.setText(u.getXm());
				// 如果异常离线用户，红色标识,PDA状态（1:PDA自己正常注销 0:平台判断异常注销）
				if (StringUtils.isNotEmpty(u.getPdazt()) && "0".equals(u.getPdazt())) {
					nameTextView.setTextColor(Color.RED);
				}
				checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
						// 设置选择对象
						if (isChecked) {
							// checkedUserName.add(checkBox.getText().toString());
							checkedUser.add(checkBox.getTag().toString());
							// 如果当前用户全部手动选完了，则将全选按钮置为 取消状态
							if (checkedUser.size() == userInfos.size()) {
								talk_back_btn_sel_all.setText("取消");
								flagAll = false;
							}
						} else {
							checkedUser.remove(checkBox.getTag());
							// 如果全选按钮选中过则复位，更改flag，更改text
							if (!flagAll) {// 点击过
								talk_back_btn_sel_all.setText("全选");
								flagAll = true;
							}
						}
						// 语音对讲功能按钮设置
						if (checkedUser != null && checkedUser.size() > 0) {
							talk_back_btn_user_enter.setText("确定(" + checkedUser.size() + ")");
							talk_back_list_button.setEnabled(true);
						} else {
							talk_back_btn_user_enter.setText("确定");
							talk_back_list_button.setEnabled(false);
						}
					}
				});
				if (fromFlag) {
					if (flag) {
						checkBox.setChecked(true);
					} else {
						checkBox.setChecked(false);
						// 这里取消时，不会调用onCheckedChanged，所以手动清空
						checkedUser.remove(checkBox.getTag());
					}
				}
				linearLayout.addView(checkBoxView);
				checkBoxView = null;
			}
		} else {
		}
	}

	private Handler handler01 = new Handler() {
		@Override
		public void handleMessage(Message msg) {

			switch (msg.what) {
			case Flags.PLAY_AUDIO:
				/*
				 * if (!isPlaying) { String fileName = (String) msg.obj;
				 * playAudio(fileName); isPlaying = true; } else { if
				 * (mediaPlayer != null) { mediaPlayer.stop();// 停止播放
				 * mediaPlayer.release();// 释放资源 isPlaying = false; } }
				 */
				break;
			case Flags.DELETE_AUDIO:
				String fileName = (String) msg.obj;
				if (AudioFileUtils.delAudioFile(fileName)) {
					// 删除成功，更新列表
					updateAudioList(true);
				}
				break;
			case LOOPER_CHECK_ONE_MINUTE:// 已经录制一分钟。
				recordEnd();
				break;
			default:
				break;
			}
		}

	};

	/**
	 * 
	 * @方法名：looperCheckOneMinute
	 * @功能说明：一分钟定时器
	 * @author liums
	 * @date 2013-12-10 下午3:59:21
	 */
	protected void looperCheckOneMinute() {
		mTimer = new Timer();
		mTimerTask = new TimerTask() {
			@Override
			public void run() {
				if (handler01 != null) {
					Log.i(TAG, "Already one minute!");
					handler01.obtainMessage(LOOPER_CHECK_ONE_MINUTE).sendToTarget();
				} else {
					Log.i(TAG, "handler01 == null");
				}
			}
		};
		// 调用频率
		mTimer.schedule(mTimerTask, ONE_MINUTE);
	}

	@Override
	protected void onPause() {
		// 停止语音播放
		AudioUtil.stopPlay();
		msgReceiver.clearHandlers();
		super.onPause();
	}

	private MessageReceiver msgReceiver;

	private void bindMessageReceiver() {
		msgReceiver = new MessageReceiver();
		msgReceiver.registerHandler(this);
		registerReceiver(msgReceiver, new IntentFilter(MqttService.MQTT_MSG_RECEIVED_INTENT));
	}

	private void unbindMessageReceiver() {
		if (msgReceiver != null) {
			msgReceiver.unregisterHandler(this);
			unregisterReceiver(msgReceiver);
			msgReceiver = null;
		}
	}

	@Override
	public void handleMessage(String topic, byte[] payload) {
		// TODO Auto-generated method stub
		Log.i(TAG, "收到信息 ");
		if (payload != null && null != new String(payload) && MqttContent.MQTT_MESSAGE_YYXX.equals(new String(payload))) {
			// 更新语音列表
			// delAudioBtnFlag = false;
			updateAudioList(false);
		}
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		msgReceiver.clearHandlers();
		super.onStop();
	}

}
