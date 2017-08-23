package com.pingtech.hgqw.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.kobjects.base64.Base64;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.pingtech.R;
import com.pingtech.hgqw.entity.FlagManagers;
import com.pingtech.hgqw.entity.GetPersonInfo;
import com.pingtech.hgqw.module.exception.activity.Exceptioninfo;
import com.pingtech.hgqw.module.offline.zjyf.util.YfZjxxConstant;
import com.pingtech.hgqw.module.wpjc.activity.GoodsPersonDetail;
import com.pingtech.hgqw.module.wpjc.entity.ReadCardPersonInfo;
import com.pingtech.hgqw.module.xunjian.activity.ReadcardActivity;
import com.pingtech.hgqw.utils.ColorConstant;
import com.pingtech.hgqw.utils.DataDictionary;
import com.pingtech.hgqw.utils.Log;
import com.pingtech.hgqw.utils.SystemSetting;

/**
 * 查询人员或手动选择人员界面的activity类
 * */
public class SelectPersonResultActivity extends MyActivity {
	private static final String TAG = "SelectPersonResultActivity";

	/**
	 * 巡查巡检-查询人员：公安库数据
	 */
	private GetPersonInfo getPersonInfo = null;

	/** 用于区分返回类型，表示来自巡查巡检 */
	public static int FROM_XUNJIAN = 1;

	/** 用于区分返回类型，表示来自异常信息 */
	public static int FROM_EXCEPTION = 2;

	/** 用于区分返回类型，表示来自其他模块 */
	public static int FROM_OTHER = 3;

	private MyAdapter adapter;

	private ListView listView;

	private boolean fromXunCha = false;

	private boolean fromException = false;

	private boolean fromGoodsCheck = false;

	private String from = null;

	private TextView person_result_list_count;

	private String voyageNumber = "";

	/**
	 * 标签切换：公安库人员信息
	 */
	private TextView xcxj_s_p_d_gakryxx;

	/**
	 * 标签切换：人员信息
	 */
	private TextView xcxj_s_p_d_ryxx;

	private View xcxj_s_p_d_label;

	private View select_person_gakryxx;

	private View select_person_ryxx;

	private ImageView xcxj_s_p_d_image_ryxx;

	private ImageView xcxj_s_p_d_image_ga;

	// /////公安库数据////
	private TextView xcxj_s_p_d_detail_name;

	private TextView xcxj_s_p_d_detail_sex;

	private TextView xcxj_s_p_d_detail_country;

	private TextView xcxj_s_p_d_detail_birthday;

	private TextView xcxj_s_p_d_detail_cardtype;

	private TextView xcxj_s_p_d_detail_cardnum;

	private TextView xcxj_s_p_d_detail_unit;

	private TextView xcxj_s_p_d_detail_office;

	private TextView xcxj_s_p_d_detail_minzu;

	private TextView xcxj_s_p_d_ryxx_empty;

	/**
	 * 公安库数据为空提示信息
	 */
	private TextView xcxj_s_p_d_gainfo;

	/**
	 * 碰撞信息
	 */
	private TextView xcxj_s_p_d_detail_pzxx;

	/**
	 * 照片
	 */
	private ImageView xcxj_s_p_d_imageView_photo;

	/** 口岸船舶情况ID */
	private String kacbqkid;

	/** 船舶名称 */
	private String voyagemc = "";

	private boolean flag = true;

	/**
	 * 历史人员(船员)信息
	 */
	private ArrayList<Map<String, String>> histtoryPList = null;

	/**
	 * 非历史人员(船员)信息
	 */
	private ArrayList<Map<String, String>> PersonList = null;

	private ArrayList<Map<String, String>> allData = null;

	private ProgressDialog progressDialog = null;

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	@Override
	public void onDestroy() {
		Log.i(TAG, "onDestroy()");
		super.onDestroy();
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.selectperson_result_list);

		// 结果列表上面添加个结果总数。
		/*
		 * person_result_list_count = (TextView)
		 * findViewById(R.id.person_result_list_count);
		 * Toast.makeText(getApplicationContext(),
		 * person_result_list_count.toString(), 1).show(); if
		 * (SelectPersonActivity.personInfoList != null&&
		 * person_result_list_count!=null) {
		 * person_result_list_count.setTextColor(R.color.blue);
		 * person_result_list_count
		 * .setText(SelectPersonActivity.personInfoList.size()+""); }
		 */

		Log.i(TAG, "onCreate()");
		Intent intent = getIntent();
		fromXunCha = intent.getBooleanExtra("fromxuncha", false);
		fromException = intent.getBooleanExtra("fromexception", false);
		fromGoodsCheck = intent.getBooleanExtra("fromGoodsCheck", false);
		voyageNumber = intent.getStringExtra("hc");
		kacbqkid = intent.getStringExtra("kacbqkid");
		voyagemc = intent.getStringExtra("voyagemc");
		from = intent.getStringExtra("from");
		getPersonInfo = (GetPersonInfo) intent.getSerializableExtra("gaInfo");
		histtoryPList = new ArrayList<Map<String, String>>();
		PersonList = new ArrayList<Map<String, String>>();
		allData = new ArrayList<Map<String, String>>();
		if (fromXunCha) {
			// View赋值
			this.customFindViewById();
			// 标签切换监听
			this.customSetOnClick();
			xcxj_s_p_d_label.setVisibility(View.VISIBLE);// 显示标签
			// 公安库数据赋值
			this.customSetContent();
			// 如果本地人员信息为空，则显示提示信息。
			if (SelectPersonActivity.personInfoList.size() > 0) {
				String str = SelectPersonActivity.personInfoList.get(0).get("hyid");
				if (str == null || "".equals(str)) {
					flag = false;
				}
			}
			if (!flag) {
				xcxj_s_p_d_ryxx_empty.setVisibility(View.VISIBLE);
				select_person_ryxx.setVisibility(View.GONE);
			}
			setMyActiveTitle(getText(R.string.xunchaxunjian) + ">" + getText(R.string.select_person) + getText(R.string.result));
		} else if (fromException) {
			setMyActiveTitle(getText(R.string.exception_info) + ">" + getText(R.string.select_person) + getText(R.string.result));
		} else if (fromGoodsCheck) {
			setMyActiveTitle(getText(R.string.goods_check) + ">" + getText(R.string.select_person) + getText(R.string.result));
		} else {
			if (from.equals("01")) {
				setMyActiveTitle(getText(R.string.kakoumanager) + ">" + getText(R.string.select_person) + getText(R.string.result));
			} else if (from.equals("02")) {
				setMyActiveTitle(getText(R.string.tikoumanager) + ">" + getText(R.string.select_person) + getText(R.string.result));
			}
		}
		sortList(SelectPersonActivity.personInfoList);
		adapter = new MyAdapter(this);
		listView = (ListView) findViewById(R.id.listview);
		listView.setAdapter(adapter);
		if (findViewById(R.id.listview_topline) != null) {
			findViewById(R.id.listview_topline).setVisibility(View.VISIBLE);
		}
		if (findViewById(R.id.listview_bottomline) != null) {
			findViewById(R.id.listview_bottomline).setVisibility(View.VISIBLE);
		}
		listView.setVisibility(View.VISIBLE);
		// 显示等待提示对话框
		progressDialog = new ProgressDialog(SelectPersonResultActivity.this);
		progressDialog.setTitle(getString(R.string.waiting));
		progressDialog.setMessage(getString(R.string.waiting));
		progressDialog.setCancelable(false);
		progressDialog.setIndeterminate(false);
		progressDialog.show();
		// adapter.notifyDataSetChanged();
		dealData();
	}

	/**
	 * 
	 * @方法名：customSetContent
	 * @功能说明：公安库返回数据赋值
	 * @author liums
	 * @date 2013-4-9 上午11:20:23
	 */
	private void customSetContent() {
		if (getPersonInfo != null && !"error".equals(getPersonInfo.getGaResult())) {
			xcxj_s_p_d_detail_name.setText(getPersonInfo.getXm());
			xcxj_s_p_d_detail_sex.setText(DataDictionary.getDataDictionaryName(getPersonInfo.getXb(), DataDictionary.DATADICTIONARY_TYPE_SEX_TYPE));
			xcxj_s_p_d_detail_birthday.setText(getPersonInfo.getCsrq());
			xcxj_s_p_d_detail_country.setText(getPersonInfo.getGj());
			xcxj_s_p_d_detail_minzu.setText(DataDictionary.getDataDictionaryName(getPersonInfo.getMz(), DataDictionary.DATADICTIONARY_TYPE_SEX_TYPE));
			xcxj_s_p_d_detail_office.setText(getPersonInfo.getZy());
			// 证件种类，取数据字典
			xcxj_s_p_d_detail_cardtype.setText(DataDictionary.getDataDictionaryName(getPersonInfo.getZjzl(),
					DataDictionary.DATADICTIONARY_TYPE_CERTIFICATES_TYPE));
			xcxj_s_p_d_detail_cardnum.setText(getPersonInfo.getSfzh());
			// 碰撞信息
			xcxj_s_p_d_detail_pzxx.setText(getPersonInfo.getPzxx());
			// 照片处理
			if (getPersonInfo.isHasPhoto()) {
				byte[] image = Base64.decode(getPersonInfo.getZp());
				BitmapFactory.Options opts = new BitmapFactory.Options();
				opts.inJustDecodeBounds = true;
				Bitmap netWorkImage = BitmapFactory.decodeByteArray(image, 0, image.length, opts);
				int height_be = opts.outHeight / 130;
				int width_be = opts.outWidth / 105;
				opts.inSampleSize = height_be > width_be ? height_be : width_be;
				if (opts.inSampleSize <= 0) {
					opts.inSampleSize = 1;
				}
				Log.i(TAG, "decodeByteArray:" + opts.outHeight + "," + opts.outWidth + "," + opts.inSampleSize);
				opts.inJustDecodeBounds = false;
				netWorkImage = BitmapFactory.decodeByteArray(image, 0, image.length, opts);
				Log.i(TAG, "decodeByteArray:" + opts.outHeight + "," + opts.outWidth);
				xcxj_s_p_d_imageView_photo.setImageBitmap(netWorkImage);
			}
		} else {

		}
	}

	/**
	 * 
	 * @方法名：customSetOnClick
	 * @功能说明：标签切换按钮监听
	 * @author liums
	 * @date 2013-4-8 下午5:56:34
	 */
	private void customSetOnClick() {
		xcxj_s_p_d_ryxx.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 文字颜色
				xcxj_s_p_d_ryxx.setTextColor(getResources().getColorStateList(R.color.selectTextColor));
				xcxj_s_p_d_gakryxx.setTextColor(getResources().getColorStateList(R.color.textcolor));

				// 标签下的图片切换
				xcxj_s_p_d_image_ryxx.setImageResource(R.drawable.exception_line_s);
				xcxj_s_p_d_image_ga.setImageResource(R.drawable.exception_line_n);

				// 切换视图
				if (!flag) {
					xcxj_s_p_d_ryxx_empty.setVisibility(View.VISIBLE);
					select_person_ryxx.setVisibility(View.GONE);
				} else {
					xcxj_s_p_d_ryxx_empty.setVisibility(View.GONE);
					select_person_ryxx.setVisibility(View.VISIBLE);
				}
				select_person_gakryxx.setVisibility(View.GONE);
				xcxj_s_p_d_gainfo.setVisibility(View.GONE);
			}
		});

		xcxj_s_p_d_gakryxx.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 文字颜色
				xcxj_s_p_d_ryxx.setTextColor(getResources().getColorStateList(R.color.textcolor));
				xcxj_s_p_d_gakryxx.setTextColor(getResources().getColorStateList(R.color.selectTextColor));

				// 标签下的图片切换
				xcxj_s_p_d_image_ga.setImageResource(R.drawable.exception_line_s);
				xcxj_s_p_d_image_ryxx.setImageResource(R.drawable.exception_line_n);
				if (getPersonInfo != null && "success".equals(getPersonInfo.getGaResult())) {
					// 切换视图
					select_person_gakryxx.setVisibility(View.VISIBLE);
					select_person_ryxx.setVisibility(View.GONE);
					xcxj_s_p_d_ryxx_empty.setVisibility(View.GONE);
				} else {
					select_person_ryxx.setVisibility(View.GONE);
					xcxj_s_p_d_gainfo.setVisibility(View.VISIBLE);
					xcxj_s_p_d_gainfo.setText(getPersonInfo.getGaResult());
				}
			}
		});
	}

	/**
	 * 
	 * @方法名：customFindViewById
	 * @功能说明：FindView
	 * @author liums
	 * @date 2013-4-8 下午5:56:18
	 */
	private void customFindViewById() {
		xcxj_s_p_d_ryxx = (TextView) this.findViewById(R.id.xcxj_s_p_d_ryxx);
		xcxj_s_p_d_gakryxx = (TextView) this.findViewById(R.id.xcxj_s_p_d_gakryxx);
		xcxj_s_p_d_gainfo = (TextView) this.findViewById(R.id.xcxj_s_p_d_gainfo);
		xcxj_s_p_d_ryxx_empty = (TextView) this.findViewById(R.id.ryxx_empty);

		xcxj_s_p_d_label = (View) this.findViewById(R.id.xcxj_s_p_d_label);

		xcxj_s_p_d_image_ryxx = (ImageView) this.findViewById(R.id.xcxj_s_p_d_image_ryxx);
		xcxj_s_p_d_image_ga = (ImageView) this.findViewById(R.id.xcxj_s_p_d_image_ga);

		select_person_gakryxx = (View) this.findViewById(R.id.select_person_gakryxx);
		select_person_ryxx = (View) this.findViewById(R.id.select_person_ryxx);

		// 公安库人员详细信息
		xcxj_s_p_d_detail_name = (TextView) this.findViewById(R.id.xcxj_s_p_d_detail_name);
		xcxj_s_p_d_detail_sex = (TextView) this.findViewById(R.id.xcxj_s_p_d_detail_sex);
		xcxj_s_p_d_detail_country = (TextView) this.findViewById(R.id.xcxj_s_p_d_detail_country);
		xcxj_s_p_d_detail_birthday = (TextView) this.findViewById(R.id.xcxj_s_p_d_detail_birthday);

		xcxj_s_p_d_detail_cardtype = (TextView) this.findViewById(R.id.cardtype);
		xcxj_s_p_d_detail_cardnum = (TextView) this.findViewById(R.id.xcxj_s_p_d_detail_cardnum);
		xcxj_s_p_d_detail_unit = (TextView) this.findViewById(R.id.xcxj_s_p_d_detail_unit);
		xcxj_s_p_d_detail_office = (TextView) this.findViewById(R.id.xcxj_s_p_d_detail_office);
		xcxj_s_p_d_detail_name = (TextView) this.findViewById(R.id.xcxj_s_p_d_detail_name);
		xcxj_s_p_d_detail_name = (TextView) this.findViewById(R.id.xcxj_s_p_d_detail_name);
		xcxj_s_p_d_detail_minzu = (TextView) this.findViewById(R.id.xcxj_s_p_d_detail_minzu);
		xcxj_s_p_d_detail_pzxx = (TextView) this.findViewById(R.id.xcxj_s_p_d_detail_pzxx);
		xcxj_s_p_d_imageView_photo = (ImageView) this.findViewById(R.id.xcxj_s_p_d_imageView_photo);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

	}

	static class ViewHolder {
		private TextView index;

		private TextView name;

		private TextView sex;

		private TextView office;

		private TextView cardType;

		private TextView cardNum;

		private TextView lcbz;

		private TextView colorTag;

		private TextView csrq;

		private Button operate;

		/**
		 * 状态LinearLayout
		 */
		private LinearLayout state;

		/**
		 * 有效期LinearLayout
		 */
		private LinearLayout layout_yxq;

		/**
		 * 有效期
		 */
		private TextView yxq;
	}

	/** 自定义列表显示适配器 */
	private class MyAdapter extends BaseAdapter {
		private LayoutInflater mInflater;

		private View view;

		public MyAdapter(Context context) {
			this.mInflater = LayoutInflater.from(context);
			this.view = mInflater.inflate(R.layout.selectperson_listview_item_history, null);
			this.view.setClickable(false);
		}

		@Override
		public int getCount() {
			int count = histtoryPList == null ? 0 : histtoryPList.size();
			int num = PersonList == null ? 0 : PersonList.size();
			if (count > 0) {
				return count + 1 + num;
			} else {
				return num;
			}
		}

		@Override
		public Object getItem(int arg0) {
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		private OnClickListener clickListener = new OnClickListener() {
			/** 执行列表右边按钮操作 */
			public void onClick(View v) {
				// TODO Auto-generated method stub
				int position = Integer.parseInt(v.getTag().toString());
				if (fromXunCha) {//
					Intent intent = null;
					intent = new Intent();
					intent.putExtra("title", getString(R.string.xunchaxunjian) + ">" + getString(R.string.xunjian_person_detail));
					intent.putExtra("from", "04");
					intent.putExtra("id", allData.get(position).get("hyid"));
					intent.putExtra("xm", allData.get(position).get("xm"));
					intent.putExtra("xb", allData.get(position).get("xb"));
					intent.putExtra("gj", allData.get(position).get("gj"));
					intent.putExtra("zw", allData.get(position).get("zw"));
					intent.putExtra("zjzl", allData.get(position).get("zjlx"));
					intent.putExtra("zjhm", allData.get(position).get("zjhm"));
					intent.putExtra("ssdw", allData.get(position).get("ssdw"));
					intent.putExtra("pzxx", allData.get(position).get("pzxx"));
					intent.putExtra("csrq", allData.get(position).get("csrq"));
					intent.putExtra("hgzl", allData.get(position).get("hgzl"));
					intent.putExtra("photo", allData.get(position).get("photo"));
					intent.putExtra("hc", allData.get(position).get("hc"));
					intent.putExtra("lcbz", allData.get(position).get("lcbz"));
					intent.putExtra("kacbqkid", allData.get(position).get("kacbqkid"));
					intent.putExtra("cbzwm", allData.get(position).get("cbzwm"));
					intent.putExtra("yxq", allData.get(position).get("yxq"));
					intent.setClass(getApplicationContext(), ReadcardActivity.class);
					startActivity(intent);
				} else if (fromGoodsCheck) {
					Intent intent = null;
					intent = new Intent();
					intent.putExtra("from", "1");
					intent.putExtra("kacbqkid", kacbqkid);
					intent.putExtra("voyageNumber", voyageNumber);
					intent.putExtra("voyagemc", voyagemc);
					ReadCardPersonInfo personInfo = new ReadCardPersonInfo();
					personInfo.setBirthday(allData.get(position).get("csrq"));
					personInfo.setCardnumber(allData.get(position).get("zjhm"));
					personInfo.setCardtype(allData.get(position).get("zjlx"));
					personInfo.setCountry(allData.get(position).get("gj"));
					personInfo.setName(allData.get(position).get("xm"));
					personInfo.setOffice(allData.get(position).get("zw"));
					String photo = allData.get(position).get("photo");
					if (photo != null && !"".equals(photo)) {
						personInfo.setPhoto(Base64.decode(photo));
					}
					personInfo.setRyid(allData.get(position).get("id"));
					personInfo.setSex(allData.get(position).get("xb"));
					personInfo.setUnit(allData.get(position).get("ssdw"));
					Bundle bundle = new Bundle();
					bundle.putSerializable("personInfo", personInfo);
					intent.putExtras(bundle);
					intent.setClass(getApplicationContext(), GoodsPersonDetail.class);
					startActivity(intent);
					finish();
				} else if (fromException) {
					Intent data = null;
					data = new Intent();
					data.putExtra("type", FROM_EXCEPTION);
					data.putExtra("xm", allData.get(position).get("xm"));
					data.putExtra("xb", allData.get(position).get("xb"));
					data.putExtra("gj", allData.get(position).get("gj"));
					data.putExtra("zw", allData.get(position).get("zw"));
					data.putExtra("zjzl", allData.get(position).get("zjlx"));
					data.putExtra("zjhm", allData.get(position).get("zjhm"));
					data.putExtra("ssdw", allData.get(position).get("ssdw"));
					data.putExtra("csrq", allData.get(position).get("csrq"));
					data.putExtra("hgzl", allData.get(position).get("hgzl"));
					setResult(RESULT_OK, data);
					finish();
				} else {
					Intent data = null;
					data = new Intent();
					data.putExtra("type", FROM_OTHER);
					data.putExtra("id", allData.get(position).get("hyid"));
					data.putExtra("cardnum", allData.get(position).get("zjhm"));
					data.putExtra("xm", allData.get(position).get("xm"));
					data.putExtra("xb", allData.get(position).get("xb"));
					data.putExtra("gj", allData.get(position).get("gj"));
					data.putExtra("zw", allData.get(position).get("zw"));
					data.putExtra("zjzl", allData.get(position).get("zjlx"));
					data.putExtra("zjhm", allData.get(position).get("zjhm"));
					data.putExtra("lcbz", allData.get(position).get("lcbz"));
					data.putExtra("ssdw", allData.get(position).get("ssdw"));
					data.putExtra("csrq", allData.get(position).get("csrq"));
					data.putExtra("hgzl", allData.get(position).get("hgzl"));
					setResult(RESULT_OK, data);
					finish();
				}
			}
		};

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (position == PersonList.size() && histtoryPList.size() > 0) {
				return view;
			}
			if (convertView == null || convertView.getTag(R.layout.selectperson_listview_class) == null) {
				holder = new ViewHolder();
				convertView = mInflater.inflate(R.layout.selectperson_listview_class, null);
				holder.index = (TextView) convertView.findViewById(R.id.col1);
				holder.name = (TextView) convertView.findViewById(R.id.col2);
				holder.sex = (TextView) convertView.findViewById(R.id.col3);
				holder.office = (TextView) convertView.findViewById(R.id.col4);
				holder.cardType = (TextView) convertView.findViewById(R.id.col5);
				holder.cardNum = (TextView) convertView.findViewById(R.id.col6);
				holder.csrq = (TextView) convertView.findViewById(R.id.col7);
				holder.colorTag = (TextView) convertView.findViewById(R.id.tag_tv);
				holder.lcbz = (TextView) convertView.findViewById(R.id.tk_lcbs);
				holder.operate = (Button) convertView.findViewById(R.id.operate_btn);
				holder.operate.setOnClickListener(clickListener);
				holder.state = (LinearLayout) convertView.findViewById(R.id.layout_zt);
				holder.yxq = (TextView) convertView.findViewById(R.id.yxq);
				holder.layout_yxq = (LinearLayout) convertView.findViewById(R.id.layout_yxq);
				convertView.setTag(R.layout.selectperson_listview_class, holder);
			} else {
				// holder = (ViewHolder) convertView.getTag();
				holder = (ViewHolder) convertView.getTag(R.layout.selectperson_listview_class);
			}
			if (holder.operate != null) {
				holder.operate.setTag(position);
			}
			if (holder.index != null) {
				holder.index.setText((position + 1) + "");
			}
			if (holder.name != null) {
				holder.name.setText(allData.get(position).get("xm") == null ? "" : allData.get(position).get("xm"));
			}
			if (holder.sex != null) {
				holder.sex
						.setText(DataDictionary.getDataDictionaryName(allData.get(position).get("xb"), DataDictionary.DATADICTIONARY_TYPE_SEX_TYPE) == null ? ""
								: DataDictionary.getDataDictionaryName(allData.get(position).get("xb"), DataDictionary.DATADICTIONARY_TYPE_SEX_TYPE));
			}
			String hgzl_s = allData.get(position).get("hgzl");
			if (holder.office != null) {
				String office_s = allData.get(position).get("zw");

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
			// 证件类型 48:登轮证, 50登陆证,17海员证 , 52搭靠外轮许可证,sbk士兵卡。
			// 状态字段在登陆证显示，在其他情况下，隐藏
			if (hgzl_s != null
					&& (YfZjxxConstant.ZJLX_XDQY.equals(hgzl_s) || YfZjxxConstant.ZJLX_DK.equals(hgzl_s) || YfZjxxConstant.ZJLX_DLUN.equals(hgzl_s))
					&& holder.state != null) {
				// 隐藏“状态”
				holder.state.setVisibility(View.GONE);

				// 显示有效期
				if (holder.layout_yxq != null) {
					holder.layout_yxq.setVisibility(View.VISIBLE);
				}

				if (holder.yxq != null) {
					holder.yxq.setText(allData.get(position).get("yxq") == null ? "" : allData.get(position).get("yxq"));
				}
			} else {
				// 显示“状态”
				holder.state.setVisibility(View.VISIBLE);
				// 隐藏有效期
				holder.layout_yxq.setVisibility(View.GONE);
			}
			if (holder.cardType != null) {
				holder.cardType.setText(DataDictionary.getDataDictionaryName(allData.get(position).get("zjlx"),
						DataDictionary.DATADICTIONARY_TYPE_CERTIFICATES_TYPE) == null ? "" : DataDictionary.getDataDictionaryName(
						allData.get(position).get("zjlx"), DataDictionary.DATADICTIONARY_TYPE_CERTIFICATES_TYPE));
			}
			if (holder.cardNum != null) {
				holder.cardNum.setText(allData.get(position).get("zjhm") == null ? "" : allData.get(position).get("zjhm"));
			}
			if (holder.csrq != null) {
				holder.csrq.setText(allData.get(position).get("csrq") == null ? "" : allData.get(position).get("csrq"));
			}
			if (holder.lcbz != null) {
				String lcbs = allData.get(position).get("lcbz");
				// 0：在船 , 1：离船、2：登船
				if ("1".equals(lcbs)) {
					holder.lcbz.setText(getString(R.string.cydt_leave));
					holder.lcbz.setTextColor(ColorConstant.BLUE);
					holder.colorTag.setTextColor(ColorConstant.BLUE);
				} else if ("0".equals(lcbs)) {
					holder.lcbz.setText(getString(R.string.cydt_login));
					holder.lcbz.setTextColor(ColorConstant.GRAY);
					holder.colorTag.setTextColor(ColorConstant.GRAY);
				} else if ("2".equals(lcbs)) {
					holder.lcbz.setText(getString(R.string.cydt_dengc));
					holder.lcbz.setTextColor(ColorConstant.RED);
					holder.colorTag.setTextColor(ColorConstant.RED);
				} else {
					holder.lcbz.setText("");
					holder.lcbz.setTextColor(ColorConstant.GRAY);
					holder.colorTag.setTextColor(ColorConstant.GRAY);
				}

			}

			if (holder.operate != null) {
				if (fromXunCha) {
					holder.operate.setText(R.string.detail);
				} else {
					holder.operate.setText(R.string.choose);
				}
			}

			return convertView;
		}
	}

	/**
	 * 
	 * @方法名：gaButtonOnClick
	 * @功能说明：公安库详情页面按钮点击事件
	 * @author liums
	 * @date 2013-4-9 下午5:23:35
	 * @param v
	 */
	public void gaButtonOnClick(View v) {
		switch (v.getId()) {
		case R.id.xcxj_s_p_d_btnExceptionRegist:
			// 记录异常
			Intent intent = new Intent();
			intent.putExtra("name", getPersonInfo.getXm());
			intent.putExtra("nationality", getPersonInfo.getGj());
			intent.putExtra("sex", getPersonInfo.getXb());
			intent.putExtra("cardtype", getPersonInfo.getZjzl());
			intent.putExtra("birthday", getPersonInfo.getCsrq());
			intent.putExtra("cardnumber", getPersonInfo.getSfzh());
			intent.putExtra("from", FlagManagers.CXRY);
			// 异常信息公共参数设置
			intent.putExtra("source", "03");
			intent.putExtra("objecttype", "01");// 对象类型：人员01、车辆02、船舶03、设备04、区域05、查岗查哨06
			intent.putExtra("jcfs", "02");// 检查方式: (视频巡视01,现场巡查,02,人员检查03)
			// 检查地点
			HashMap<String, Object> ship = SystemSetting.getBindShip(2 + "");
			if (ship != null) {
				String tkwz = (String) ship.get("tkwz");
				String[] temp_str = null;
				if (tkwz != null) {
					temp_str = tkwz.split(",");
				}
				intent.putExtra("shipname", (String) ship.get("cbzwm"));
				intent.putExtra("jhhc", (String) ship.get("hc"));
				intent.putExtra("dockcode", (String) ship.get("tkmt"));
				if (temp_str != null && temp_str.length > 0) {
					intent.putExtra("dockname", temp_str[0]);
				} else {
					intent.putExtra("dockname", "");
				}
				intent.putExtra("berthcode", (String) ship.get("tkbw"));
				if (temp_str != null && temp_str.length > 1) {
					intent.putExtra("berthname", temp_str[1]);
				} else {
					intent.putExtra("berthname", "");
				}
			} else if (SystemSetting.xunJianId != null && SystemSetting.xunJianId.length() > 0) {
				if (SystemSetting.xunJianType != null && SystemSetting.xunJianType.equals("bw")) {
					intent.putExtra("berthcode", SystemSetting.xunJianId);
					intent.putExtra("berthname", SystemSetting.xunJianName);
					intent.putExtra("dockcode", SystemSetting.xunJianMTid);
					intent.putExtra("dockname", SystemSetting.xunJianMTname);
					intent.putExtra("scene", "02");
				} else if (SystemSetting.xunJianType != null && SystemSetting.xunJianType.equals("mt")) {
					intent.putExtra("dockcode", SystemSetting.xunJianId);
					intent.putExtra("dockname", SystemSetting.xunJianName);
					intent.putExtra("scene", "03");
				} else if (SystemSetting.xunJianType != null && SystemSetting.xunJianType.equals("qy")) {
					intent.putExtra("areacode", SystemSetting.xunJianId);
					intent.putExtra("areaname", SystemSetting.xunJianName);
					intent.putExtra("scene", "04");
				}
			}
			intent.setClass(this, Exceptioninfo.class);
			startActivity(intent);
			break;
		case R.id.xcxj_s_p_d_btnBack:
			// 返回
			this.finish();
			break;

		default:
			break;
		}
	}

	/**
	 * 
	 * @方法名：sortList
	 * @功能说明：列表重新排序，离船的放到最后, 0：在船 , 1：离船、2：登船
	 * @author liums
	 * @date 2014-1-21 下午3:26:35
	 * @param list
	 */
	private void sortList(ArrayList<Map<String, String>> list) {
		if (list == null || list.size() < 1) {
			return;
		}
		String lcbz = "";
		ArrayList<Map<String, String>> lcList = new ArrayList<Map<String, String>>();
		ArrayList<Map<String, String>> notLcList = new ArrayList<Map<String, String>>();
		for (Map<String, String> map : list) {
			lcbz = map.get("lcbz");
			if ("1".equals(lcbz)) {// 如果是离船
				lcList.add(map);
			} else {
				notLcList.add(map);
			}
		}
		SelectPersonActivity.personInfoList.clear();
		SelectPersonActivity.personInfoList.addAll(notLcList);
		SelectPersonActivity.personInfoList.addAll(lcList);
	}

	/**
	 * 
	 * @方法名：dealData
	 * @功能说明：处理personInfoList数据，将历史记录数据找出来
	 * @author zhaotf
	 * @date 2014-3-10 下午5:19:56
	 */
	private void dealData() {
		histtoryPList.clear();
		PersonList.clear();
		allData.clear();
		if (SelectPersonActivity.personInfoList != null && SelectPersonActivity.personInfoList.size() > 0) {
			int size = SelectPersonActivity.personInfoList.size();
			for (int i = 0; i < size; i++) {
				Map<String, String> map = SelectPersonActivity.personInfoList.get(i);
				// 如果cbkazt是离港(3)的话，将数据保存在histtoryPList里面
				if (map != null && map.containsKey("cbkazt") && ("3".equals(map.get("cbkazt")))) {
					histtoryPList.add(map);
				} else {
					PersonList.add(map);
				}
			}
		}
		allData.addAll(PersonList);
		if (histtoryPList.size() > 0) {
			Map<String, String> map = new HashMap<String, String>();
			allData.add(map);
			allData.addAll(histtoryPList);
		}
		adapter.notifyDataSetChanged();
		progressDialog.cancel();
		progressDialog = null;
	}

}
