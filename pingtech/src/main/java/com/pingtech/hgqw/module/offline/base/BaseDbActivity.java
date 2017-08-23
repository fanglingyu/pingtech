package com.pingtech.hgqw.module.offline.base;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.pingtech.R;
import com.pingtech.hgqw.module.offline.bwdm.dao.BwdmDao;
import com.pingtech.hgqw.module.offline.bwdm.entity.Bwdm;
import com.pingtech.hgqw.module.offline.kacbqk.dao.KacbqkDao;
import com.pingtech.hgqw.module.offline.kacbqk.entity.Kacbqk;
import com.pingtech.hgqw.module.offline.mtdm.dao.MtdmDao;
import com.pingtech.hgqw.module.offline.mtdm.entity.Mtdm;

public class BaseDbActivity extends Activity {
	private Button btn01;

	private Button btn02;

	private Button btn03;

	private List<Bwdm> bwdmList = new ArrayList<Bwdm>();

	private List<Mtdm> mtdmList = new ArrayList<Mtdm>();

	private List<Kacbqk> kacbqkList = new ArrayList<Kacbqk>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.base_db_activity);
		find();
		init();
	}

	private void init() {
		Bwdm bwdm = null;
		Mtdm mtdm = null;
		Kacbqk kacbqk = null;
		for (int i = 0; i < 1000; i++) {
			bwdm = new Bwdm();
			bwdm.setId(i+"");
			bwdm.setBwdm(i+"");
			bwdm.setBwmc("泊位名称"+i);
			
			mtdm = new Mtdm();
			mtdm.setId(""+i);
			mtdm.setMtdm(""+i);
			mtdm.setMtmc("码头名称"+i);
			
			kacbqk = new Kacbqk();
			kacbqk.setKacbqkid(""+i);
			kacbqk.setCbzwm("船舶名称"+i);
			
			bwdmList.add(bwdm);
			mtdmList.add(mtdm);
			kacbqkList.add(kacbqk);
		}
	}

	public void click(View v) {
		switch (v.getId()) {
		case R.id.btn_01:
			startThread01("01");
			break;
		case R.id.btn_02:
			startThread02("02");
			break;
		case R.id.btn_03:
			startThread03("03");
			break;

		default:
			break;
		}
	}

	private void startThread01(String threadName) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					new BwdmDao().insertList(bwdmList);
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}, threadName).start();
	}

	private void startThread02(String threadName) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					new MtdmDao().insertList(mtdmList);
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}, threadName).start();
	}

	private void startThread03(String threadName) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					new KacbqkDao().insertList(kacbqkList);
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}, threadName).start();
	}

	private void find() {
		btn01 = (Button) findViewById(R.id.btn_01);
		btn02 = (Button) findViewById(R.id.btn_02);
		btn03 = (Button) findViewById(R.id.btn_03);
	}

}
