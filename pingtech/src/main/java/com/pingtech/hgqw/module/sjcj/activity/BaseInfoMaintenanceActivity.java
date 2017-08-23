package com.pingtech.hgqw.module.sjcj.activity;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.xmlpull.v1.XmlPullParser;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.TextUtils.TruncateAt;
import android.util.Pair;
import android.util.Xml;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.pingtech.R;
import com.pingtech.hgqw.activity.MyActivity;
import com.pingtech.hgqw.base.BaseApplication;
import com.pingtech.hgqw.entity.BaseInfoElement;
import com.pingtech.hgqw.entity.CardInfo;
import com.pingtech.hgqw.entity.MessageEntity;
import com.pingtech.hgqw.interf.OffLineResult;
import com.pingtech.hgqw.interf.OnHttpResult;
import com.pingtech.hgqw.module.offline.base.utils.OffLineManager;
import com.pingtech.hgqw.module.sjcj.action.SjcjAction;
import com.pingtech.hgqw.module.sjcj.entity.BindXxdAndBaseInfo;
import com.pingtech.hgqw.module.sjcj.utils.PullXmlSjcjUtils;
import com.pingtech.hgqw.module.xtgl.activity.FunctionSetting;
import com.pingtech.hgqw.readcard.service.ReadService;
import com.pingtech.hgqw.service.PingtechService;
import com.pingtech.hgqw.utils.BaseInfoData;
import com.pingtech.hgqw.utils.BaseInfoData.OnCallBack;
import com.pingtech.hgqw.utils.BasicNameValuePair;
import com.pingtech.hgqw.utils.Log;
import com.pingtech.hgqw.utils.NVPairTOMap;
import com.pingtech.hgqw.utils.StringUtils;
import com.pingtech.hgqw.web.NetWorkManager;
import com.pingtech.hgqw.widget.HgqwToast;

/**
 * 
 * 数据采集界面的activity类
 */
public class BaseInfoMaintenanceActivity extends MyActivity implements OnHttpResult, OffLineResult, OnCallBack {
	private static final String TAG = "BaseInfoMaintenanceActivity";

	/** 调用getBaseInfoByCard接口时，http请求类型 */
	private static final int HTTPREQUEST_TYPE_GET_BASEINFO_BY_CARD = 2;

	/** 调用orientBaseInfo接口时，http请求类型 */
	private static final int HTTPREQUEST_TYPE_ORIENT_BASEINFO = 3;

	private ProgressDialog progressDialog = null;

	/** 后台返回的提示信息，见接口文档，error时info字段 */
	private String httpReturnXMLInfo = null;

	/** 要采集的对象id */
	private String infoId;

	/** 要采集的对象类型 */
	private String infoType;

	/** 要采集的对象名称 */
	private String infoName;

	/** 按名称采集时，选中的项索引 */
	private int selectIndex = -1;

	/** GPS纬度 */
	private String lon;

	/** GPS经度 */
	private String lat;

	private ListView listView;

	private TreeViewAdapter adapter = null;

	private OnClickListener onClickListener;

	private EditText inputText;

	private TextView selItem;

	private ImageView imageView;

	private String idCardNumber_s = null;

	private EditText baseinfo_edt_xxd = null;

	private Button read = null;

	private Button read1 = null;

	private Handler handler = new Handler();

	private Handler handlerGps = null;

	private boolean flagGps = true;

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.baseinfo_list);

		Log.i(TAG, "onCreate()");
		setMyActiveTitle(R.string.baseinfomaintenance);
		selItem = (TextView) findViewById(R.id.btn_send_by_card);
		imageView = (ImageView) findViewById(R.id.image_btn_send_by_card);
		// 离线版无网络进行提示
		if (!BaseApplication.instent.getWebState()) {
			offLineToast(getString(R.string.sjcj_no_web_cannot_use_card));
		}
		find();
		adapter = new TreeViewAdapter(this, R.layout.baseinfo_listview_class, BaseInfoData.mBaseInfoDataDisplay);
		listView = (ListView) findViewById(R.id.listview);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
				if (!BaseInfoData.mBaseInfoDataDisplay.get(position).isMhasChild()) {
					/** 如果没有孩子，也就是已经是最后一级，选中该条记录 */
					BaseInfoElement BaseinfoElement;
					if (selectIndex != -1) {
						BaseinfoElement = BaseInfoData.mBaseInfoDataDisplay.get(selectIndex);
						BaseinfoElement.setChecked(false);
					}
					BaseinfoElement = BaseInfoData.mBaseInfoDataDisplay.get(position);
					BaseinfoElement.setChecked(true);
					selectIndex = position;
					adapter.notifyDataSetChanged();
					return;
				}
				/** 如果有孩子，也就是非最后一级，需要打开或合上操作 */
				if (BaseInfoData.mBaseInfoDataDisplay.get(position).isExpanded()) {
					if (selectIndex != -1) {
						BaseInfoElement Element = BaseInfoData.mBaseInfoDataDisplay.get(selectIndex);
						Element.setChecked(false);
					}
					selectIndex = -1;
					closeTreeView(position);
				} else {
					if (selectIndex != -1) {
						BaseInfoElement Element = BaseInfoData.mBaseInfoDataDisplay.get(selectIndex);
						Element.setChecked(false);
					}
					selectIndex = -1;
					BaseInfoData.mBaseInfoDataDisplay.get(position).setExpanded(true);
					int clickitem_level = BaseInfoData.mBaseInfoDataDisplay.get(position).getLevel();
					String clickitem_id = BaseInfoData.mBaseInfoDataDisplay.get(position).getId();
					String clickitem_parent_id = BaseInfoData.mBaseInfoDataDisplay.get(position).getParent();
					int j = 1;
					/** 打开节点 */
					for (BaseInfoElement BaseinfoElement : BaseInfoData.mBaseInfoDataAll) {
						if ((BaseinfoElement.getParent() != null) && BaseinfoElement.getParent().equals(clickitem_id)) {
							BaseinfoElement.setExpanded(false);
							if (BaseinfoElement.getOutlineTitle() == null) {
								String str = null;
								String item_id = BaseinfoElement.getId();
								item_id = item_id.substring(BaseinfoElement.getParent().length() + BaseInfoData.TREEVIEW_NODE_SERARATE.length());
								for (BaseInfoElement tempElement : BaseInfoData.mBaseInfoDataAll) {
									if ((tempElement.getParent() != null)
											&& (!tempElement.isMhasChild())
											&& (item_id.equals(tempElement.getId().substring(
													tempElement.getParent().length() + BaseInfoData.TREEVIEW_NODE_SERARATE.length())))) {
										str = tempElement.getOutlineTitle();
										break;
									}
								}
								BaseinfoElement.setOutlineTitle(str);
							}
							BaseinfoElement.setChecked(false);
							BaseInfoData.mBaseInfoDataDisplay.add(position + j, BaseinfoElement);
							j++;
						}
					}
					/** 关闭其他打开的节点 */
					for (int index = 0; index < BaseInfoData.mBaseInfoDataDisplay.size(); index++) {
						BaseInfoElement BaseinfoElement = BaseInfoData.mBaseInfoDataDisplay.get(index);
						int itemlevel = BaseinfoElement.getLevel();
						String itemid = BaseinfoElement.getId();
						if (BaseinfoElement.isExpanded() && !clickitem_id.equals(itemid)) {
							switch (clickitem_level - itemlevel) {
							case 1:
								/** 上一级 */
								if (!itemid.equals(clickitem_parent_id)) {
									closeTreeView(index);
								}
								break;
							case 2:
								/** 上二级 */
								String[] parent = clickitem_parent_id.split(BaseInfoData.TREEVIEW_NODE_SERARATE);
								if (!itemid.equals(parent[parent.length - 2])) {
									closeTreeView(index);
								}
								break;
							default:
								closeTreeView(index);
								break;
							}
						}
					}
				}
				adapter.notifyDataSetChanged();
			}
		});

		onClickListener = new OnClickListener() {
			@Override
			public void onClick(View v) {

				Resources resource = (Resources) getBaseContext().getResources();
				ColorStateList csl = (ColorStateList) resource.getColorStateList(R.color.textcolor);
				ColorStateList csl_s = (ColorStateList) resource.getColorStateList(R.color.selectTextColor);
				switch (v.getId()) {
				case R.id.btn_send_by_card:
					/** 选择刷电子标签采集 */
					findViewById(R.id.baseinfo_btn_xxd).setVisibility(View.GONE);
					findViewById(R.id.baseinfo_layout_xxd).setVisibility(View.GONE);
					inputText.setText("");
					baseinfo_edt_xxd.setText("");
					if (selItem == null || selItem != findViewById(R.id.btn_send_by_card)) {
						selItem.setTextColor(csl);
						imageView.setImageResource(R.drawable.exception_line_n);
						selItem = (TextView) findViewById(R.id.btn_send_by_card);
						imageView = (ImageView) findViewById(R.id.image_btn_send_by_card);
						selItem.setTextColor(csl_s);
						imageView.setImageResource(R.drawable.exception_line_s);
						findViewById(R.id.send_by_card_layout).setVisibility(View.VISIBLE);
						findViewById(R.id.send_by_name_layout).setVisibility(View.GONE);

						inputText.requestFocus();
					}
					// 判断有无网络
					if (!BaseApplication.instent.getWebState()) {
						offLineToast(getString(R.string.sjcj_no_web_cannot_use_card));
					}
					break;

				case R.id.btn_send_by_name:
					/** 选择按名称采集 */
					inputText.setText("");
					baseinfo_edt_xxd.setText("");
					if (selItem == null || selItem != findViewById(R.id.btn_send_by_name)) {
						selItem.setTextColor(csl);
						imageView.setImageResource(R.drawable.exception_line_n);
						selItem = (TextView) findViewById(R.id.btn_send_by_name);
						imageView = (ImageView) findViewById(R.id.image_btn_send_by_name);
						selItem.setTextColor(csl_s);
						imageView.setImageResource(R.drawable.exception_line_s);
						findViewById(R.id.send_by_card_layout).setVisibility(View.GONE);
						findViewById(R.id.send_by_name_layout).setVisibility(View.VISIBLE);
						findViewById(R.id.baseinfo_btn_xxd).setVisibility(View.VISIBLE);
						findViewById(R.id.baseinfo_layout_xxd).setVisibility(View.VISIBLE);
						if (BaseInfoData.mBaseInfoDataAll == null || BaseInfoData.mBaseInfoDataAll.size() == 0) {
							progressDialog = new ProgressDialog(BaseInfoMaintenanceActivity.this);
							progressDialog.setTitle(getString(R.string.waiting));
							progressDialog.setMessage(getString(R.string.waiting));
							progressDialog.setCancelable(false);
							progressDialog.setIndeterminate(false);
							progressDialog.show();
							// 判断有无网络
							if (!BaseApplication.instent.getWebState()) {
								offLineGetInfo();
							} else {
								BaseInfoData.onRequestBaseInfoData(BaseInfoMaintenanceActivity.this);
							}

						} else {
							adapter.notifyDataSetChanged();
						}
					}
					break;
				case R.id.baseinfo_select:
					/** 输入电子标签后，点击确定时执行该操作 */
					// 判断有无网络
					if (!BaseApplication.instent.getWebState()) {
						offLineToast(getString(R.string.sjcj_no_web_cannot_use_card));
						break;
					}
					String num = inputText.getText().toString();
					if (StringUtils.isEmpty(num)) {
						offLineToast(getString(R.string.cardnum_empty));
						return;
					}

					idCardNumber_s = num;

					onRequestBaseInfoByCard();

					break;
				case R.id.baseinfo_submit:
					/** 点击采集时执行该操作 */
					if (selItem == (TextView) findViewById(R.id.btn_send_by_name)) {
						if (selectIndex == -1) {
							offLineToast(getString(R.string.baseinfo_no_select));
							break;
						}
					} else {
						if (infoId == null) {
							offLineToast(getString(R.string.baseinfo_no_record));
							break;
						}
					}

					if (PingtechService.mLocation == null) {
						offLineToast(getString(R.string.gps_not_location));
						break;
					}

					if (StringUtils.isEmpty(lon) || StringUtils.isEmpty(lat)) {
						Log.i(TAG, "lon ,lat has empty");
						break;
					}

					onShowSendGPSDataQuestDialog();
					break;
				case R.id.btnRefresh:
					/** 点击按名称采集下的刷新按钮时执行该操作 */

					progressDialog = new ProgressDialog(BaseInfoMaintenanceActivity.this);
					progressDialog.setTitle(getString(R.string.waiting));
					progressDialog.setMessage(getString(R.string.waiting));
					progressDialog.setCancelable(false);
					progressDialog.setIndeterminate(false);
					progressDialog.show();
					// 判断有无网络
					if (!BaseApplication.instent.getWebState()) {
						offLineGetInfo();
					} else {
						BaseInfoData.onRequestBaseInfoData(BaseInfoMaintenanceActivity.this);
					}
					break;
				}
			}
		};
		TextView tvcard = (TextView) findViewById(R.id.btn_send_by_card);
		TextView tvname = (TextView) findViewById(R.id.btn_send_by_name);
		tvcard.setOnClickListener(onClickListener);
		tvname.setOnClickListener(onClickListener);
		((Button) findViewById(R.id.baseinfo_select)).setOnClickListener(onClickListener);
		((Button) findViewById(R.id.baseinfo_submit)).setOnClickListener(onClickListener);
		((Button) findViewById(R.id.baseinfo_select)).setOnKeyListener(btnkeylistener);
		inputText = (EditText) findViewById(R.id.iccardinput);
		inputText.setOnKeyListener(keylistener);
		((Button) findViewById(R.id.btnRefresh)).setOnClickListener(onClickListener);
		// initReadMehtod();
		find();
		onRefreshGPSInfo();
	}

	private void find() {
		baseinfo_edt_xxd = (EditText) findViewById(R.id.baseinfo_edt_xxd);
		read = (Button) findViewById(R.id.read);
		read1 = (Button) findViewById(R.id.read1);
	}

	/**
	 * 
	 * 合上treeview某一节点
	 * 
	 * @param position
	 *            需要合上的节点索引
	 * 
	 */
	private void closeTreeView(int position) {
		BaseInfoData.mBaseInfoDataDisplay.get(position).setExpanded(false);
		BaseInfoElement BaseinfoElement = BaseInfoData.mBaseInfoDataDisplay.get(position);
		ArrayList<BaseInfoElement> temp = new ArrayList<BaseInfoElement>();

		for (int i = position + 1; i < BaseInfoData.mBaseInfoDataDisplay.size(); i++) {
			if (BaseinfoElement.getLevel() >= BaseInfoData.mBaseInfoDataDisplay.get(i).getLevel()) {
				break;
			}
			temp.add(BaseInfoData.mBaseInfoDataDisplay.get(i));
		}
		BaseInfoData.mBaseInfoDataDisplay.removeAll(temp);
	}

	private void onRefreshGPSInfo() {
		runT = new RunT();
		handlerGps = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				
				String curLon = BaseApplication.instent.getLongitude();
				String curLat = BaseApplication.instent.getLatitude();
				lon = curLon;
				lat = curLat;

				if (StringUtils.isNullOrEmpty(curLat) || StringUtils.isNullOrEmpty(curLon)) {
					((TextView) findViewById(R.id.current_lon)).setText(Html.fromHtml("经度：" + "<font color=\"#acacac\">" + "未定位" + "</font>"));
					((TextView) findViewById(R.id.current_lat)).setText(Html.fromHtml("纬度：" + "<font color=\"#acacac\">" + "未定位" + "</font>"));
					this.sendEmptyMessageDelayed(0, 2*1000);
					return;
				}

				int lenInt = curLon.length();
				int index = curLon.indexOf(".");
				if (index != -1) {// 小数点后六位
					if (lenInt - index - 1 > 6) {
						curLon = curLon.substring(0, index + 6 + 1);
					}
				}
				lenInt = curLat.length();
				index = curLat.indexOf(".");
				if (index != -1) {
					if (lenInt - index - 1 > 6) {
						curLat = curLat.substring(0, index + 6 + 1);
					}
				}

				((TextView) findViewById(R.id.current_lon)).setText(Html.fromHtml("经度：" + "<font color=\"#acacac\">" + curLon + "</font>"));
				((TextView) findViewById(R.id.current_lat)).setText(Html.fromHtml("纬度：" + "<font color=\"#acacac\">" + curLat + "</font>"));
				
				this.sendEmptyMessageDelayed(0, 2*1000);
			}

		};
		handlerGps.sendEmptyMessageDelayed(0, 2*1000);
	}
	private RunT runT = null;
	private class RunT implements Runnable {

		@Override
		public void run() {
			if (handlerGps != null) {
				handlerGps.obtainMessage().sendToTarget();
			}
		}
	}

//	@Override
//	public boolean dispatchKeyEvent(KeyEvent event) {
//		int keyCode = event.getKeyCode();
//		Log.i(TAG, "dispatchKeyEvent,keycode=" + keyCode);
//		if (selItem == (TextView) findViewById(R.id.btn_send_by_name)) {
//			if (keyCode == KeyEvent.KEYCODE_ENTER) {
//				return true;
//			}
//			if ((keyCode >= KeyEvent.KEYCODE_0 && keyCode <= KeyEvent.KEYCODE_9) || (keyCode == KeyEvent.KEYCODE_ENTER)) {
//				return true;
//			}
//		}
//		return super.dispatchKeyEvent(event);
//	}

	/** 根据用户刷的电子标签号，向平台发起查询基础信息的请求 */
	private void onRequestBaseInfoByCard() {
		if (selItem == (TextView) findViewById(R.id.btn_send_by_name)) {
			return;
		}
		if (progressDialog != null) {
			return;
		}
		inputText.setText(idCardNumber_s);

		String url = "getBaseInfoByCard";
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("cardNumber", idCardNumber_s));
		params.add(new BasicNameValuePair("type", "-1"));
		progressDialog = new ProgressDialog(this);
		progressDialog.setTitle(getString(R.string.waiting));
		progressDialog.setMessage(getString(R.string.waiting));
		progressDialog.setCancelable(false);
		progressDialog.setIndeterminate(false);
		progressDialog.show();
		NetWorkManager.request(this, url, params, HTTPREQUEST_TYPE_GET_BASEINFO_BY_CARD);
	}

	/** 处理ic卡读卡器 */
	private EditText.OnKeyListener keylistener = new EditText.OnKeyListener() {
		@Override
		public boolean onKey(View v, int keyCode, KeyEvent event) {
			Log.i(TAG, "EditText.OnKeyListener onKey(),keycode=" + keyCode + ", action=" + event.getAction());
			if (selItem == (TextView) findViewById(R.id.btn_send_by_name)) {
				return true;
			}
			if ((keyCode == KeyEvent.KEYCODE_ENTER) && (event.getAction() == KeyEvent.ACTION_UP)) {
				String num = inputText.getText().toString();
				if (num.length() != 0) {
					if (progressDialog != null && progressDialog.isShowing()) {
						return true;
					}
					idCardNumber_s = num;
					onRequestBaseInfoByCard();
				}
				return true;
			}
			return false;
		}
	};

	/** button不响应ic卡读卡器 */
	private Button.OnKeyListener btnkeylistener = new Button.OnKeyListener() {
		@Override
		public boolean onKey(View v, int keyCode, KeyEvent event) {
			Log.i(TAG, "Button.OnKeyListener onKey()" + keyCode);
			return false;
		}
	};

	/** 显示采集前向用户提示确认的dialog */
	private void onShowSendGPSDataQuestDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		int len = lon.length();
		int index = lon.indexOf(".");
		if (index != -1) {
			if (len - index - 1 > 6) {
				lon = lon.substring(0, index + 6 + 1);
			}
		}
		len = lat.length();
		index = lat.indexOf(".");
		if (index != -1) {
			if (len - index - 1 > 6) {
				lat = lat.substring(0, index + 6 + 1);
			}
		}

		if (selItem == (TextView) findViewById(R.id.btn_send_by_card)) {
			builder.setMessage("是否发送\"" + infoName + "\"的采集结果？\n经度：" + lon + "\n纬度：" + lat);
		} else {
			if (BaseInfoData.mBaseInfoDataDisplay.get(selectIndex).isSended()) {
				builder.setMessage("是否重新发送\"" + BaseInfoData.mBaseInfoDataDisplay.get(selectIndex).getOutlineTitle() + "\"的采集结果？（已经采集过）\n经度：" + lon
						+ "\n纬度：" + lat);
			} else {
				builder.setMessage("是否发送\"" + BaseInfoData.mBaseInfoDataDisplay.get(selectIndex).getOutlineTitle() + "\"的采集结果？\n经度：" + lon + "\n纬度："
						+ lat);
			}
		}
		builder.setTitle(R.string.info);
		builder.setPositiveButton(R.string.yes, new AlertDialog.OnClickListener() {
			/** 按下"是"的操作 */
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				// 执行经纬度信息
				if (progressDialog != null) {
					return;
				}
				String url = "orientBaseInfo";
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				if (selItem == (TextView) findViewById(R.id.btn_send_by_name)) {
					params.add(new BasicNameValuePair("type", BaseInfoData.mBaseInfoDataDisplay.get(selectIndex).getType()));
					String id = BaseInfoData.mBaseInfoDataDisplay.get(selectIndex).getId();
					params.add(new BasicNameValuePair("id", id.substring(BaseInfoData.mBaseInfoDataDisplay.get(selectIndex).getParent().length()
							+ BaseInfoData.TREEVIEW_NODE_SERARATE.length())));
				} else {
					params.add(new BasicNameValuePair("type", infoType));
					params.add(new BasicNameValuePair("id", infoId));
				}
				params.add(new BasicNameValuePair("longitude", lon));
				params.add(new BasicNameValuePair("latitude", lat));
				BaseInfoData.mBaseInfoDataDisplay.get(selectIndex).setGpsData("(" + lon + "," + lat + ")");
				progressDialog = new ProgressDialog(BaseInfoMaintenanceActivity.this);
				progressDialog.setTitle(getString(R.string.sending));
				progressDialog.setMessage(getString(R.string.waiting));
				progressDialog.setCancelable(false);
				progressDialog.setIndeterminate(false);
				progressDialog.show();
				if (getState(FunctionSetting.kqlx, false)) {
					OffLineManager.request(BaseInfoMaintenanceActivity.this, new SjcjAction(), url, NVPairTOMap.nameValuePairTOMap(params), 0);
				} else {
					NetWorkManager.request(BaseInfoMaintenanceActivity.this, url, params, HTTPREQUEST_TYPE_ORIENT_BASEINFO);
				}

			}
		});
		builder.setNegativeButton(R.string.no, new AlertDialog.OnClickListener() {
			/** 按下"否"的操作 */
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		builder.create().show();
	}

	/** 自定义一个以树形来显示按名称采集的基础信息一览表的适配器 */
	private class TreeViewAdapter extends ArrayAdapter<BaseInfoElement> {

		public TreeViewAdapter(Context context, int textViewResourceId, List<BaseInfoElement> objects) {
			super(context, textViewResourceId, objects);
			mInflater = LayoutInflater.from(context);
			mFileList = objects;
			mIconCollapse = BitmapFactory.decodeResource(context.getResources(), R.drawable.outline_list_collapse);
			mIconExpand = BitmapFactory.decodeResource(context.getResources(), R.drawable.outline_list_expand);
			mIconCheckOn = BitmapFactory.decodeResource(context.getResources(), R.drawable.radiobutton_check_on_n);
			mIconCheckOff = BitmapFactory.decodeResource(context.getResources(), R.drawable.radiobutton_check_off_n);
			Resources resource = (Resources) getBaseContext().getResources();
			SendTextColor = (ColorStateList) resource.getColorStateList(R.color.selectTextColor);
		}

		private LayoutInflater mInflater;

		private List<BaseInfoElement> mFileList;

		private Bitmap mIconCollapse;

		private Bitmap mIconExpand;

		private Bitmap mIconCheckOn;

		private Bitmap mIconCheckOff;

		private ColorStateList SendTextColor;

		private void setObject(List<BaseInfoElement> objects) {
			mFileList = objects;
		}

		@Override
		public int getCount() {
			return mFileList.size();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			ViewHolder holder;
			convertView = mInflater.inflate(R.layout.baseinfo_listview_class, null);
			holder = new ViewHolder();
			holder.text = (TextView) convertView.findViewById(R.id.text);
			holder.icon = (ImageView) convertView.findViewById(R.id.icon);
			convertView.setTag(holder);

			int level = mFileList.get(position).getLevel();
			holder.icon.setPadding(25 * (level + 1), holder.icon.getPaddingTop(), 0, holder.icon.getPaddingBottom());
			holder.text.setText(mFileList.get(position).getOutlineTitle());
			if (mFileList.get(position).isSended()) {
				holder.text.setTextColor(SendTextColor);
				holder.text.setText(mFileList.get(position).getOutlineTitle() + "    " + mFileList.get(position).getGpsData());
			}
			if ((mFileList.get(position).isMhasChild()) && (mFileList.get(position).isExpanded() == false)) {
				holder.icon.setImageBitmap(mIconCollapse);
			} else if (mFileList.get(position).isMhasChild() && (mFileList.get(position).isExpanded() == true)) {
				holder.icon.setImageBitmap(mIconExpand);
			} else if (!mFileList.get(position).isMhasChild()) {
				if (mFileList.get(position).getChecked()) {
					holder.icon.setImageBitmap(mIconCheckOn);
					holder.icon.setVisibility(View.VISIBLE);
					holder.text.setSelected(true);
					holder.text.setEllipsize(TruncateAt.MARQUEE);
					holder.text.setMarqueeRepeatLimit(-1);
				} else {
					holder.icon.setImageBitmap(mIconCheckOff);
					holder.icon.setVisibility(View.VISIBLE);
					holder.text.setSelected(false);
					holder.text.setMarqueeRepeatLimit(-1);
					holder.text.setEllipsize(TruncateAt.END);
				}
			}
			return convertView;
		}

		@Override
		public void notifyDataSetChanged() {
			if (BaseInfoData.mBaseInfoDataAll != null && BaseInfoData.mBaseInfoDataAll.size() > 0 && BaseInfoData.mBaseInfoDataDisplay != null
					&& BaseInfoData.mBaseInfoDataDisplay.size() == 0) {
				int j = 0;
				for (BaseInfoElement BaseinfoElement : BaseInfoData.mBaseInfoDataAll) {
					if (BaseinfoElement.getLevel() == 0) {
						BaseinfoElement.setExpanded(false);
						BaseinfoElement.setChecked(false);
						BaseInfoData.mBaseInfoDataDisplay.add(j, BaseinfoElement);
						j++;
					}
				}
			}
			super.notifyDataSetChanged();
		}

		private class ViewHolder {
			private TextView text;

			private ImageView icon;
		}
	}

	/**
	 * 解析刷电子标签后，平台返回的内容，详细参见接口文档
	 * */
	private boolean onParseXMLDataByCard(String str) {
		// TODO Auto-generated method stub
		boolean success = false;
		try {
			XmlPullParser parser = Xml.newPullParser();
			parser.setInput(new ByteArrayInputStream(str.getBytes()), "utf-8");// 设置解析的数据源
			int type = parser.getEventType();
			String text = null;
			httpReturnXMLInfo = null;
			while (type != XmlPullParser.END_DOCUMENT) {
				switch (type) {
				case XmlPullParser.START_TAG:
					if ("result".equals(parser.getName())) {
						text = parser.nextText();
						if ("error".equals(text)) {
							success = false;
						} else if ("success".equals(text)) {
							success = true;
						}
					} else if ("info".equals(parser.getName())) {
						// 信息
						if (!success) {
							httpReturnXMLInfo = parser.nextText();
						}
					} else if ("id".equals(parser.getName())) {
						// id
						infoId = parser.nextText();
					} else if ("type".equals(parser.getName())) {
						// type
						infoType = parser.nextText();
					} else if ("name".equals(parser.getName())) {
						// name
						infoName = parser.nextText();
					} else if ("cardNumber".equals(parser.getName())) {
						// ic card
					}
					break;
				case XmlPullParser.END_TAG:
					break;
				}
				type = parser.next();
			}
			return success;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public void onHttpResult(String str, int httpRequestType) {

		Log.i(TAG, "onHttpResult()httpRequestType:" + httpRequestType + ",result" + (str != null));
		if (progressDialog != null) {
			progressDialog.dismiss();
			progressDialog = null;
		}

		if (HTTPREQUEST_TYPE_ORIENT_BASEINFO == httpRequestType) {
			if (str != null && "1".equals(str)) {
				if (selItem == (TextView) findViewById(R.id.btn_send_by_name)) {
					BaseInfoData.mBaseInfoDataDisplay.get(selectIndex).setSended(true);
					adapter.notifyDataSetChanged();
				}
				offLineToast(getString(R.string.send_success));
			} else {
				offLineToast(getString(R.string.send_failure));
			}
		} else if (HTTPREQUEST_TYPE_GET_BASEINFO_BY_CARD == httpRequestType) {
			infoName = null;
			infoType = null;
			infoId = null;
			if (str != null) {
				if (onParseXMLDataByCard(str)) {
					offLineToast(getString(R.string.select_success));
				} else {
					if (httpReturnXMLInfo != null) {
						offLineToast(httpReturnXMLInfo);
					} else {
						offLineToast(getString(R.string.data_download_failure_info));
					}
				}
			} else {
				offLineToast(getString(R.string.data_download_failure_info));
			}
			if (infoName != null) {
				((TextView) findViewById(R.id.baseinfodetail_name)).setText(infoName);
			} else {
				((TextView) findViewById(R.id.baseinfodetail_name)).setText("");
			}
			if (infoType != null) {
				if (infoType.equals("cheliang")) {
					((TextView) findViewById(R.id.baseinfodetail_type)).setText("车辆");
				} else if (infoType.equals("chuanbo")) {
					((TextView) findViewById(R.id.baseinfodetail_type)).setText("船舶");
				} else if (infoType.equals("tk")) {
					((TextView) findViewById(R.id.baseinfodetail_type)).setText("梯口");
				} else if (infoType.equals("kk")) {
					((TextView) findViewById(R.id.baseinfodetail_type)).setText("卡口");
				} else if (infoType.equals("sxt")) {
					((TextView) findViewById(R.id.baseinfodetail_type)).setText("摄像头");
				} else if (infoType.equals("ka")) {
					((TextView) findViewById(R.id.baseinfodetail_type)).setText("口岸");
				} else if (infoType.equals("mt")) {
					((TextView) findViewById(R.id.baseinfodetail_type)).setText("码头");
				} else if (infoType.equals("bw")) {
					((TextView) findViewById(R.id.baseinfodetail_type)).setText("泊位");
				} else if (infoType.equals("qy")) {
					((TextView) findViewById(R.id.baseinfodetail_type)).setText("区域");
				} else if (infoType.equals("ft")) {
					((TextView) findViewById(R.id.baseinfodetail_type)).setText("浮筒");
				} else if (infoType.equals("md")) {
					((TextView) findViewById(R.id.baseinfodetail_type)).setText("锚地");
				} else if (infoType.equals("swfwdw")) {
					((TextView) findViewById(R.id.baseinfodetail_type)).setText("涉外服务单位");
				} else if (infoType.equals("swfwcb")) {
					((TextView) findViewById(R.id.baseinfodetail_type)).setText("涉外服务船舶");
				} else if (infoType.equals("dlbzxx")) {
					((TextView) findViewById(R.id.baseinfodetail_type)).setText("地理标注信息");
				} else {
					((TextView) findViewById(R.id.baseinfodetail_type)).setText(infoType);
				}
			} else {
				((TextView) findViewById(R.id.baseinfodetail_type)).setText("");
			}
		} else if (PullXmlSjcjUtils.HTTPREQUEST_TYPE_BIND_XXD_AND_BASEINFO == httpRequestType) {
			// 解析xml
			BindXxdAndBaseInfo bindXxdAndBaseInfo = (BindXxdAndBaseInfo) PullXmlSjcjUtils.parseXMLData(str,
					PullXmlSjcjUtils.HTTPREQUEST_TYPE_BIND_XXD_AND_BASEINFO);
			if (bindXxdAndBaseInfo == null) {
				HgqwToast.toast(R.string.send_failure);
				return;
			}
			HgqwToast.toast(bindXxdAndBaseInfo.getInfo());
			// 绑定成功业务
			if ("success".equals(bindXxdAndBaseInfo.getResult())) {

				// BaseInfoElement baseInfoElement =
				// BaseInfoData.mBaseInfoDataDisplay.get(selectIndex);
				// String outlineTitle = baseInfoElement.getOutlineTitle();
				// baseInfoElement.setOutlineTitle(outlineTitle + "，信息钉：" +
				// baseinfo_edt_xxd.getText().toString());
				inputText.setText("");
				baseinfo_edt_xxd.setText("");
			} else {

			}
		}

		httpRequestType = 0;
	}

	/**
	 * @see OnCallBack
	 * */
	@Override
	public void onCallBack(boolean ret) {
		if (ret) {
			selectIndex = -1;
			adapter.setObject(BaseInfoData.mBaseInfoDataDisplay);
			adapter.notifyDataSetChanged();
			offLineToast(getString(R.string.baseInfo_get_success));
		} else {
			offLineToast(getString(R.string.data_download_failure_info));
		}
		if (progressDialog != null) {
			progressDialog.dismiss();
			progressDialog = null;
		}
	}

	boolean flag = false;

	@Override
	protected void onPause() {
		// ReadService.getInstent().close();
		closeRead();
		flagGps = false;
		handlerGps = null;
		super.onPause();
	}

	@Override
	protected void onResume() {
		flagGps = true;
		super.onResume();
	}

	@Override
	public void onDestroy() {
		Log.i(TAG, "onDestroy()");
		selectIndex = -1;
		BaseInfoData.mBaseInfoDataDisplay.clear();

		handlerGps = null;
		super.onDestroy();
	}

	/* 读卡程序开始 */
	private ReadService readService;

	private ReadCardHandler_Base readCardHander_base;

	private void readInit() {
		readCardHander_base = new ReadCardHandler_Base();
		ReadService.getInstent(this, readCardHander_base, ReadService.READ_TYPE_DEFAULT_ICKEY).init();
	}

	/**
	 * 离线版Toast
	 */
	private void offLineToast(String show) {
		if (progressDialog != null) {
			progressDialog.dismiss();
		}
		progressDialog = null;
		HgqwToast.makeText(getApplicationContext(), show, HgqwToast.LENGTH_LONG).show();

	}

	/**
	 * 离线版：(按名称采集模块)取得本地“名称”数据
	 */
	private void offLineGetInfo() {
		if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			offLineToast(getString(R.string.sdcardunmounted));
			return;
		}

		StringBuffer projectDir = new StringBuffer();

		projectDir.append(Environment.getExternalStorageDirectory().getPath()).append(File.separator).append("pingtech").append(File.separator)
				.append("baseinfo.xml");

		File xmlFile = new File(projectDir.toString());
		if (!xmlFile.exists()) {
			offLineToast(getString(R.string.sjcj_no_data_qljwl));
			projectDir = null;
			return;
		}

		BufferedReader br;
		try {

			br = new BufferedReader(new FileReader(projectDir.toString()));
			projectDir = null;
			String line = "";
			StringBuffer buffer = new StringBuffer();
			while ((line = br.readLine()) != null) {
				buffer.append(line);
			}
			br.close();
			String fileContent = buffer.toString();
			BaseInfoData.destroy();
			BaseInfoData.init();
			if (BaseInfoData.onParseXMLBaseInfoData(fileContent, false)) {
				if (progressDialog != null) {
					progressDialog.dismiss();
				}
				progressDialog = null;
				selectIndex = -1;
				adapter.setObject(BaseInfoData.mBaseInfoDataDisplay);
				adapter.notifyDataSetChanged();
			} else {
				if (progressDialog != null) {
					progressDialog.dismiss();
				}
				progressDialog = null;
				offLineToast(getString(R.string.sjcj_no_data_qljwl));
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			offLineToast(getString(R.string.sjcj_no_data_qljwl));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			offLineToast(getString(R.string.sjcj_no_data_qljwl));
		}

	}

	/**
	 * 读卡Handler类
	 * 
	 * @author Administrator
	 * 
	 */
	class ReadCardHandler_Base extends Handler {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MessageEntity.TOAST:
				HgqwToast.getToastView(getApplicationContext(), (String) msg.obj).show();
				break;
			case ReadService.READ_TYPE_DEFAULT_ICKEY:
				closeRead();
				CardInfo cardInfo = (CardInfo) msg.obj;
				String cardNum = cardInfo.getDefaultIckey();
				idCardNumber_s = cardNum;
				inputText.setText(idCardNumber_s);
				baseinfo_edt_xxd.setText(idCardNumber_s);
				BaseApplication.soundManager.onPlaySoundNoVb(4, 0);
				onRequestBaseInfoByCard();
				break;
			default:
				break;
			}
		}

	}

	private void closeRead() {
		ReadService.getInstent().close();
		if (read != null) {
			read.setText("点击刷卡");
			read.setEnabled(true);
			read1.setText("点击刷卡");
			read1.setEnabled(true);
			read = null;
		}
	}

	/* 读卡程序结束 */

	@Override
	public void offLineResult(Pair<Boolean, Object> res, int offLineRequestType) {
		// TODO Auto-generated method stub
		if (res.first) {
			offLineToast(getString(R.string.sjcj_save_success));
		} else {
			offLineToast(getString(R.string.sjcj_save_failure));
		}
	}

	/**
	 * 
	 * @方法名：click
	 * @功能说明：按钮点击事件
	 * @author liums
	 * @date 2013-11-13 下午3:44:47
	 * @param v
	 */
	public void click(View v) {
		switch (v.getId()) {
		case R.id.baseinfo_btn_xxd:
			// 信息钉与基础数据绑定
			bindXxdForBaseInfo();
			break;
		case R.id.read:
		case R.id.read1:
			read();
			break;
		default:
			break;
		}
	}

	private void read() {
		if (!BaseApplication.instent.getWebState()) {
			// offLineToast(getString(R.string.sjcj_no_web));
		} else {
			readInit();
			if (read == null) {
				read = (Button) findViewById(R.id.read);
			}
			if (read != null) {
				baseinfo_edt_xxd.setText("");
				inputText.setText("");
				read.setText("请刷卡");
				read.setEnabled(false);
				read1.setText("请刷卡");
				read1.setEnabled(false);
			}
		}
	}

	/**
	 * 
	 * @方法名：bindXxdForBaseInfo
	 * @功能说明：信息钉采集业务
	 * @author liums
	 * @date 2013-11-14 下午2:41:02
	 */
	private void bindXxdForBaseInfo() {
		if (selItem == (TextView) findViewById(R.id.btn_send_by_name)) {
			if (selectIndex == -1) {
				HgqwToast.toast(R.string.baseinfo_no_select);
				return;
			}

			String xxdStr = baseinfo_edt_xxd.getText().toString();
			if (StringUtils.isEmpty(xxdStr)) {
				HgqwToast.toast(R.string.baseinfo_edt_hint_text);
				return;
			}

			// 选定了对象，并且已刷电子标签，请求接口
			onShowSendXxdDialog(xxdStr, selectIndex);
		}
	}

	private void onShowSendXxdDialog(final String xxdStr, final int selectIndex) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		BaseInfoElement baseInfoElement = BaseInfoData.mBaseInfoDataDisplay.get(selectIndex);
		String outlineTitle = baseInfoElement.getOutlineTitle();
		builder.setMessage("确定\"" + outlineTitle + "\"与电子标签\"" + xxdStr + "\"的绑定关系？");
		builder.setTitle(R.string.info);
		builder.setPositiveButton(R.string.yes, new AlertDialog.OnClickListener() {
			/** 按下"是"的操作 */
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				sendXxdAndBaseInfo(xxdStr, selectIndex);
			}
		});
		builder.setNegativeButton(R.string.no, new AlertDialog.OnClickListener() {
			/** 按下"否"的操作 */
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		builder.create().show();
	}

	/**
	 * 
	 * @方法名：sendXxdAndBaseInfo
	 * @功能说明：请求服务器，发送接口。
	 * @author liums
	 * @date 2013-11-14 下午5:56:11
	 * @param xxdStr
	 * @param selectIndex2
	 */
	private void sendXxdAndBaseInfo(String xxdStr, int selectIndex) {
		String url = "bindXxdAndBaseInfo";
		List<NameValuePair> params = new ArrayList<NameValuePair>();

		params.add(new BasicNameValuePair("type", BaseInfoData.mBaseInfoDataDisplay.get(selectIndex).getType()));
		String id = BaseInfoData.mBaseInfoDataDisplay.get(selectIndex).getId();
		params.add(new BasicNameValuePair("id", id.substring(BaseInfoData.mBaseInfoDataDisplay.get(selectIndex).getParent().length()
				+ BaseInfoData.TREEVIEW_NODE_SERARATE.length())));
		params.add(new BasicNameValuePair("xxd", xxdStr));
		params.add(new BasicNameValuePair("userid", BaseApplication.instent.getUserInfo().getUserID()));

		progressDialog = new ProgressDialog(BaseInfoMaintenanceActivity.this);
		progressDialog.setTitle(getString(R.string.sending));
		progressDialog.setMessage(getString(R.string.waiting));
		progressDialog.setCancelable(false);
		progressDialog.setIndeterminate(false);
		progressDialog.show();
		NetWorkManager.request(BaseInfoMaintenanceActivity.this, url, params, PullXmlSjcjUtils.HTTPREQUEST_TYPE_BIND_XXD_AND_BASEINFO);

	}

}
