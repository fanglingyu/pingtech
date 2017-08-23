package com.pingtech.hgqw.module.offline.test;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.pingtech.R;
import com.pingtech.hgqw.activity.NetworkSettingActivity;
import com.pingtech.hgqw.interf.OnHttpResult;
import com.pingtech.hgqw.module.offline.cyxx.entity.TBCyxx;
import com.pingtech.hgqw.module.offline.cyxx.service.CyxxService;
import com.pingtech.hgqw.module.offline.hgzjxx.entity.Hgzjxx;
import com.pingtech.hgqw.module.offline.hgzjxx.service.HgzjxxService;
import com.pingtech.hgqw.module.offline.offdata.entity.OffData;
import com.pingtech.hgqw.module.offline.offdata.service.OffDataService;
import com.pingtech.hgqw.service.AndSerOffLineData;
import com.pingtech.hgqw.utils.BasicNameValuePair;
import com.pingtech.hgqw.utils.Log;
import com.pingtech.hgqw.utils.xml.PullXmlUtils;
import com.pingtech.hgqw.utils.zip.ZipUtils;
import com.pingtech.hgqw.web.NetWorkManager;
import com.pingtech.hgqw.widget.HgqwToast;

public class TestActivity extends Activity implements OnHttpResult {
	private static final String TAG = "TestActivity";

	private TextView textView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.test);
		find();

	}

	private void find() {
		textView = (TextView) findViewById(R.id.tv_find_offdata);
	}

	public void click(View v) {
		switch (v.getId()) {
		case R.id.btn_add_hgzjxx:
//			insertHgzjxx();
			insertHgzjxx02();
			break;
		case R.id.btn_filter_zjxx:
			selHgzjxxByFilter();
			break;
		case R.id.btn_add_offdata:
			insertOffData();
			break;
		case R.id.btn_find_offdata:
			readOffData();
			break;
		case R.id.button2:
			insertCyxx();
			break;
		case R.id.button3:
			getHgzjxx();
			break;
		case R.id.button4:
			insertOffData();
			break;
		case R.id.button5:
			startService(new Intent(this, AndSerOffLineData.class));
			break;

		default:
			break;
		}
	}

	private void selHgzjxxByFilter() {
		HgzjxxService hgzjxxService = new HgzjxxService();
		List<Hgzjxx> hgzjxxs = hgzjxxService.findByCountAndOffset(99 ,200);
		
	}

	private void readOffData() {
		OffDataService offDataService = new OffDataService();
		try {
			textView.clearComposingText();
			List<OffData> offDatas = offDataService.findByCount(100);
			Toast.makeText(this, "offDatas.size=" + offDatas.size(), 0).show();
			textView.setText(offDatas.size()+"");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void insertOffData() {
		OffDataService cyxxService = new OffDataService();
		List<OffData> list = new ArrayList<OffData>();
		OffData offData;
		for (int i = 0; i < 500; i++) {
			offData = new OffData();
			offData.setCjsj(new Date());
			offData.setCzgn("10101");
			offData.setCzmk("101");
			offData.setGxsj(new Date());
			offData.setPdacode("012345678966666");
			offData.setUserid("0123456");
			offData.setXmldata("<type>0</type><typeName>船舶抵港</typeName><userid>0123456</userid><pdacode>0123456</pdacode><hc>0123456</hc><dgsj>2013-09-25 11:05:20</dgsj>");
			list.add(offData);
		}
		try {
			cyxxService.insertList(list);
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	private void insertCyxx() {
		CyxxService cyxxService = new CyxxService();
		List<TBCyxx> list = new ArrayList<TBCyxx>();
		TBCyxx cyxx;
		for (int i = 0; i < 1000; i++) {
			cyxx = new TBCyxx();
			cyxx.setXm("测试一个船员" + i);
			list.add(cyxx);
		}
		try {
			cyxxService.insertList(list);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void insertHgzjxx() {
		HgzjxxService hgzjxxService = new HgzjxxService();
		List<Hgzjxx> list = new ArrayList<Hgzjxx>();
		Hgzjxx hgzjxx;
		for (int i = 0; i < 1000; i++) {
			hgzjxx = new Hgzjxx();
			hgzjxx.setZwcbm("测试一个证件" + i);
			list.add(hgzjxx);
		}
		try {
			hgzjxxService.insertList(list);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	private void insertHgzjxx02() {
		HgzjxxService hgzjxxService = new HgzjxxService();
		List<Hgzjxx> list = new ArrayList<Hgzjxx>();
		Hgzjxx hgzjxx;
		for (int i = 0; i < 100; i++) {
			hgzjxx = new Hgzjxx();
			hgzjxx.setZwcbm("测试一个证件" + i);
			hgzjxx.setZjhm(Integer.toString(i));
			hgzjxx.setCbzjffxxxid(Integer.toString(i));
			list.add(hgzjxx);
		}
		try {
			hgzjxxService.insertList(list);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		hgzjxxService = new HgzjxxService();
		list.clear();
		for (int i = 10; i < 65; i++) {
			hgzjxx = new Hgzjxx();
			hgzjxx.setZwcbm("测试一个证件" + i);
			hgzjxx.setZjhm(Integer.toString(i));
			hgzjxx.setCbzjffxxxid(Integer.toString(i)+100);
			list.add(hgzjxx);
		}
		try {
			hgzjxxService.insertList(list);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private long beginTime = 0;

	private long endTime = 0;

	private void getHgzjxx() {
		String url = "getOffLineHgzjxx";
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("userid", ""));
		beginTime = Calendar.getInstance().getTimeInMillis();
		NetWorkManager.request(this, url, params, 100);
	}

	@Override
	public void onHttpResult(String str, int httpRequestType) {
		Log.i(TAG, "onHttpResult()httpRequestType:" + httpRequestType + ",result" + (str != null));
		endTime = Calendar.getInstance().getTimeInMillis();
		Log.i(TAG, "接收到数据，网络请求耗时：" + (endTime - beginTime) + "");

		switch (httpRequestType) {
		case 100:
			if (str == null || "".equals(str)) {
				HgqwToast.toast(getApplicationContext(), "请求失败，数据为空", HgqwToast.LENGTH_LONG);
				return;
			}
			datasToDb(str);
			break;

		default:
			break;
		}
	}

	private void datasToDb(String str) {
		try {

			// FileUtils.strToFile(null, str, "/mnt/sdcard/pingtech/offline",
			// "hgzjxx_zip.xml");
			String unZipStr = ZipUtils.uncompress(str);
			// FileUtils.strToFile(null, unZipStr,
			// "/mnt/sdcard/pingtech/offline", "hgzjxx_unzip.xml");
			Map<String, List> map = PullXmlUtils.onParseXMLData(unZipStr);

			endTime = Calendar.getInstance().getTimeInMillis();
			Log.i(TAG, "解析完成，总体耗时：" + (endTime - beginTime) + "");

			List<Hgzjxx> list = map.get("hgzjxx");

			if (list == null || list.size() < 1) {
				HgqwToast.toast(getApplicationContext(), "接收到的数据为空", HgqwToast.LENGTH_LONG);
				return;
			}
			HgzjxxService hgzjxxService = new HgzjxxService();
			try {
				int saveResult = hgzjxxService.insertList(list);
			} catch (SQLException e) {
				e.printStackTrace();
				HgqwToast.toast(getApplicationContext(), "保存失败", HgqwToast.LENGTH_LONG);

			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		endTime = Calendar.getInstance().getTimeInMillis();
		Log.i(TAG, "插入数据结束，总体耗时：" + (endTime - beginTime) + "");
	}

	@Override
	public boolean onMenuOpened(int featureId, Menu menu) {
		return super.onMenuOpened(featureId, menu);
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case R.id.login_menu_settings:
			Intent intent = new Intent();
			intent.setClass(getApplicationContext(), NetworkSettingActivity.class);
			startActivity(intent);
			break;
		case R.id.login_menu_quit:
			break;

		default:
			break;
		}
		return true;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate(R.menu.login_menu, menu);
		return true;

	}
}
