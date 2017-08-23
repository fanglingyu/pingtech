package com.pingtech.hgqw.module.wpjc.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.pingtech.R;
import com.pingtech.hgqw.activity.MyActivity;
import com.pingtech.hgqw.base.BaseApplication;
import com.pingtech.hgqw.entity.Flags;
import com.pingtech.hgqw.entity.GlobalFlags;
import com.pingtech.hgqw.entity.LoginUser;
import com.pingtech.hgqw.interf.OffLineResult;
import com.pingtech.hgqw.interf.OnHttpResult;
import com.pingtech.hgqw.module.offline.base.utils.OffLineManager;
import com.pingtech.hgqw.module.wpjc.action.GoodsCheckAction;
import com.pingtech.hgqw.module.wpjc.adapter.GoodsCheckAdapter;
import com.pingtech.hgqw.module.wpjc.utils.PullXmlGoodsCheckList;
import com.pingtech.hgqw.utils.BasicNameValuePair;
import com.pingtech.hgqw.utils.Log;
import com.pingtech.hgqw.utils.NVPairTOMap;
import com.pingtech.hgqw.utils.SystemSetting;
import com.pingtech.hgqw.web.NetWorkManager;
import com.pingtech.hgqw.widget.HgqwToast;

/**
 * 
 * 梯口管理下货物检查界面的activity类
 */
public class GoodsCheckList extends MyActivity implements OnHttpResult ,OffLineResult {
	private static final String TAG = "GoodsCheckActivity";
private GoodsCheckAdapter adapter;
	/** 保存货物列表信息 */
	private ArrayList<Map<String, String>> goodsList = null;
	private ListView listView;
	private String httpReturnXMLInfo = null;
	private ProgressDialog progressDialog = null;
	/** 绑定的船舶的航次号 */
	private String voyageNumber;
	private Spinner spinner;
	private ArrayAdapter<String> shipAdapter;
	private HashMap<String, Object> xunchaBindShip = null;
	private HashMap<String, Object> tikoumBindShip  = null;
	private List<String> cbzwmList = null;
	private List<String> hcList  = null;
	private String voyagemc="";
	private List<String> kacbqkidList = null;
	/** 口岸船舶情况ID */
	private String kacbqkid;
	
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
		super.onCreate(savedInstanceState, R.layout.goodscheck_list);
		Log.i(TAG, "onCreate()");
		setMyActiveTitle(getText(R.string.goods_check) + ">"
				+ getText(R.string.Goods_check));
		Intent intent = getIntent();
		voyageNumber = intent.getStringExtra("hc");
		voyagemc = intent.getStringExtra("voyagemc");
		kacbqkid = intent.getStringExtra("kacbqkid");
		goodsList=new ArrayList<Map<String,String>>();
		adapter = new GoodsCheckAdapter(getApplicationContext(),goodsList);
		listView = (ListView) findViewById(R.id.listview);
		listView.setAdapter(adapter);
		ImageView imageview = (ImageView) findViewById(R.id.goodscheck_ship_imageview);
		imageview.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent readcardintent = new Intent();
				readcardintent.putExtra("voyageNumber", voyageNumber);
				readcardintent.putExtra("voyagemc", voyagemc);
				readcardintent.putExtra("kacbqkid", kacbqkid);
				readcardintent.putStringArrayListExtra("cbzwmList", (ArrayList<String>) cbzwmList);
				readcardintent.putStringArrayListExtra("hcList", (ArrayList<String>) hcList);
				readcardintent.putStringArrayListExtra("kacbqkidList", (ArrayList<String>) kacbqkidList);
				readcardintent.setClass(getApplicationContext(), GoodsReadCard.class);
				startActivity(readcardintent);
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

	}


	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		switch (Flags.PDA_VERSION) {
		case Flags.PDA_VERSION_DEFAULT:
			// 全版本
			xunchaBindShip = SystemSetting.getBindShip(GlobalFlags.LIST_TYPE_FROM_XUNCHAXUNJIAN + "");
			xcGoodsCheck(xunchaBindShip);
			break;
		case Flags.PDA_VERSION_SENTINEL:
			List<HashMap<String, Object>> list=SystemSetting.shipOfKK;
			tikoumBindShip = SystemSetting.getBindShip(GlobalFlags.LIST_TYPE_FROM_TIKOUMANAGER + "");
			List<HashMap<String, Object>> listShip=new ArrayList<HashMap<String,Object>>();
			boolean contain = false;
			if (tikoumBindShip != null && list != null && list.size() > 0) {
				for (int i = 0; i < list.size(); i++) {
					if (tikoumBindShip.get("hc").equals(list.get(i).get("hc"))) {
						contain = true;
					}
				}
				if (!contain) {
					listShip.add(tikoumBindShip);
				}
			} else if (tikoumBindShip != null
					&& list == null ) {
				listShip.add(tikoumBindShip);
			}

			if (list != null && list.size() > 0) {
				listShip.addAll(list);
			}
			sbGoodsCheck(listShip);
		default:
			// 全版本
			xcGoodsCheck(xunchaBindShip);
			break;
		}
	}

	public void xcGoodsCheck(HashMap<String, Object> BindShip) {
		String url = "getGoodsList";
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("userID", LoginUser.getCurrentLoginUser().getUserID()));
			if (BindShip!=null&&BindShip.size() > 0) {
			String cbzwm = (String) BindShip.get("cbzwm");
			String hc = (String) BindShip.get("hc");
			String mykacbqkid = (String)BindShip.get("kacbqkid");
			cbzwmList=new ArrayList<String>();
			hcList=new ArrayList<String>();
			cbzwmList.add(cbzwm);
			hcList.add(hc);
			kacbqkidList = new ArrayList<String>();
			kacbqkidList.add(mykacbqkid);
			params.add(new BasicNameValuePair("voyageNumber", hc));
			SystemSetting.readcardhc=hc;
			
			spinner = (Spinner) findViewById(R.id.goodscheck_ship_spinner);
			shipAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, cbzwmList);
			shipAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spinner.setAdapter(shipAdapter);
			spinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				
				String hc = hcList.get(position);
				voyageNumber=hc;
				SystemSetting.readcardhc=hc;
				
				kacbqkid = kacbqkidList.get(position);
				
				String url = "getGoodsList";
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("userID", LoginUser
						.getCurrentLoginUser().getUserID()));
				params.add(new BasicNameValuePair("voyageNumber", hc));
				if(!BaseApplication.instent.getWebState()){
					OffLineManager.request(GoodsCheckList.this, new GoodsCheckAction(), url, NVPairTOMap.nameValuePairTOMap(params), 0);
				}else{
					NetWorkManager.request(GoodsCheckList.this, url, params, 0);
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub

			}

			});
			if(!BaseApplication.instent.getWebState()){
				OffLineManager.request(GoodsCheckList.this, new GoodsCheckAction(), url, NVPairTOMap.nameValuePairTOMap(params), 0);
			}else{
				NetWorkManager.request(GoodsCheckList.this, url, params, 0);
			}
			
		}
	}
	
	private void sbGoodsCheck(List<HashMap<String, Object>> BindShip) {
		cbzwmList = new ArrayList<String>();
		hcList = new ArrayList<String>();
		kacbqkidList = new ArrayList<String>();
		String url = "getGoodsList";
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		for (int i = 0; i < BindShip.size(); i++) {
			HashMap<String, Object> ship = BindShip.get(i);
			if (ship != null) {
				cbzwmList.add((String) ship.get("cbzwm"));
				hcList.add((String) ship.get("hc"));
				kacbqkidList.add((String)ship.get("kacbqkid"));
			}
		}

		spinner = (Spinner) findViewById(R.id.goodscheck_ship_spinner);
		shipAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, cbzwmList);
		shipAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(shipAdapter);
		if (hcList!=null&&hcList.size()>0) {
			if (SystemSetting.readcardhc != null) {
				if (hcList.contains(SystemSetting.readcardhc)) {
					params.add(new BasicNameValuePair("voyageNumber", SystemSetting.readcardhc));
					spinner.setSelection(hcList.indexOf(SystemSetting.readcardhc));
				} else {
					voyagemc =cbzwmList.get(0);
					voyageNumber = hcList.get(0);
					kacbqkid = kacbqkidList.get(0);
					SystemSetting.readcardhc=hcList.get(0);
					params.add(new BasicNameValuePair("voyageNumber", hcList.get(0)));

				}

			}
			
			spinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
				@Override
				public void onItemSelected(AdapterView<?> parent, View view,
						int position, long id) {
					String hc = hcList.get(position);
					voyageNumber=hc;
					SystemSetting.readcardhc=hc;
					kacbqkid = kacbqkidList.get(position);
					voyagemc =cbzwmList.get(position);
					String url = "getGoodsList";
	
					List<NameValuePair> params = new ArrayList<NameValuePair>();
					params.add(new BasicNameValuePair("userID", LoginUser.getCurrentLoginUser().getUserID()));
					params.add(new BasicNameValuePair("voyageNumber", hc));
					if(!BaseApplication.instent.getWebState()){
						OffLineManager.request(GoodsCheckList.this, new GoodsCheckAction(), url, NVPairTOMap.nameValuePairTOMap(params), 0);
					}else{
						NetWorkManager.request(GoodsCheckList.this, url, params, 0);
					}

				}

				@Override
				public void onNothingSelected(AdapterView<?> parent) {
					// TODO Auto-generated method stub

				}

			});
			if(!BaseApplication.instent.getWebState()){
				OffLineManager.request(GoodsCheckList.this, new GoodsCheckAction(), url, NVPairTOMap.nameValuePairTOMap(params), 0);
			}else{
				NetWorkManager.request(GoodsCheckList.this, url, params, 0);
			}
		}

	}
	
	
	

	@Override
	public void onHttpResult(String str, int httpRequestType) {
		// TODO Auto-generated method stub
		Log.i(TAG, "onHttpResult()httpRequestType:" + httpRequestType + ",result" + (str != null));
		if (progressDialog != null) {
			progressDialog.dismiss();
			progressDialog = null;
		}
		if (str != null) {
			httpReturnXMLInfo =PullXmlGoodsCheckList.onParseXMLData(str,goodsList);
			if (adapter.getCount() > 0) {
				if (findViewById(R.id.listview_topline) != null) {
					findViewById(R.id.listview_topline).setVisibility(View.VISIBLE);
				}
				if (findViewById(R.id.select_result_empty) != null) {
					findViewById(R.id.select_result_empty).setVisibility(View.GONE);
				}
				listView.setVisibility(View.VISIBLE);
				adapter.notifyDataSetChanged();
				
			} else {
				if (findViewById(R.id.listview_topline) != null) {
					findViewById(R.id.listview_topline).setVisibility(View.GONE);
				}
				listView.setVisibility(View.GONE);
				if (httpReturnXMLInfo != null) {
					if (findViewById(R.id.select_result_empty) != null) {
						findViewById(R.id.select_result_empty).setVisibility(View.GONE);
						((TextView) findViewById(R.id.select_result_empty)).setText(httpReturnXMLInfo);
					}
					HgqwToast.makeText(GoodsCheckList.this, httpReturnXMLInfo, HgqwToast.LENGTH_LONG).show();
				} else {
					if (findViewById(R.id.select_result_empty) != null) {
						findViewById(R.id.select_result_empty).setVisibility(View.GONE);
						((TextView) findViewById(R.id.select_result_empty)).setText(R.string.no_data);
					}
					HgqwToast.makeText(GoodsCheckList.this, R.string.no_data, HgqwToast.LENGTH_LONG).show();
				}
			}
		} else {
			if (findViewById(R.id.listview_topline) != null) {
				findViewById(R.id.listview_topline).setVisibility(View.GONE);
			}
			listView.setVisibility(View.GONE);
			if (findViewById(R.id.select_result_empty) != null) {
				findViewById(R.id.select_result_empty).setVisibility(View.GONE);
				((TextView) findViewById(R.id.select_result_empty)).setText(R.string.data_download_failure_info);
			}
		}
	}

	/**
	 * 离线版Toast
	 */
	private void offLineToast(String show){
		if (progressDialog != null) {
			progressDialog.dismiss();
			progressDialog = null;
		}
		HgqwToast.makeText(getApplicationContext(), show, HgqwToast.LENGTH_LONG).show();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void offLineResult(Pair<Boolean, Object> res, int offLineRequestType) {
		// TODO Auto-generated method stub
		if(res.first){
			if (goodsList == null) {
				goodsList = new ArrayList<Map<String, String>>();
			}else{
				goodsList.clear();
			}
			goodsList =  (ArrayList<Map<String, String>>)(res.second);
			if (goodsList!=null && goodsList.size()>0) {
				if (findViewById(R.id.listview_topline) != null) {
					findViewById(R.id.listview_topline).setVisibility(View.VISIBLE);
				}
				if (findViewById(R.id.select_result_empty) != null) {
					findViewById(R.id.select_result_empty).setVisibility(View.GONE);
				}
				listView.setVisibility(View.VISIBLE);
				adapter = new GoodsCheckAdapter(getApplicationContext(),goodsList);
				listView.setAdapter(adapter);
				
			} else {
				if (findViewById(R.id.listview_topline) != null) {
					findViewById(R.id.listview_topline).setVisibility(View.GONE);
				}
				listView.setVisibility(View.GONE);
				if (httpReturnXMLInfo != null) {
					if (findViewById(R.id.select_result_empty) != null) {
						findViewById(R.id.select_result_empty).setVisibility(View.GONE);
						((TextView) findViewById(R.id.select_result_empty)).setText(httpReturnXMLInfo);
					}
					offLineToast(httpReturnXMLInfo);
				} else {
					if (findViewById(R.id.select_result_empty) != null) {
						findViewById(R.id.select_result_empty).setVisibility(View.GONE);
						((TextView) findViewById(R.id.select_result_empty)).setText(R.string.no_data);
					}
					offLineToast(getString(R.string.no_data));
				}
			}
		}else{
			if (findViewById(R.id.listview_topline) != null) {
				findViewById(R.id.listview_topline).setVisibility(View.GONE);
			}
			listView.setVisibility(View.GONE);
			if (findViewById(R.id.select_result_empty) != null) {
				findViewById(R.id.select_result_empty).setVisibility(View.GONE);
				((TextView) findViewById(R.id.select_result_empty)).setText(R.string.data_download_failure_info);
			}
		}
	}
}
