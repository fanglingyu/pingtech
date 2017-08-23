package com.pingtech.hgqw.module.offline.test;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import android.test.AndroidTestCase;

import com.pingtech.hgqw.module.offline.offdata.entity.OffData;
import com.pingtech.hgqw.module.offline.offdata.service.OffDataService;
import com.pingtech.hgqw.utils.Log;

public class TestService extends AndroidTestCase {
	private static final String TAG = "TestService";
	public void getOffData(){
		OffDataService dataService =new OffDataService();
		try {
		List<OffData> list=	dataService.findAllByGN(1, 2,"",null);
		List<Integer> ll=new ArrayList<Integer>();
		for (int i = 0; i < 10; i++) {
			ll.add(i);
		}
		dataService.deleteByIds(ll);
		for (int i = 0; i < list.size(); i++) {
			Log.i(TAG, list.get(i).getXmldata());
		}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		
		
	}
}
