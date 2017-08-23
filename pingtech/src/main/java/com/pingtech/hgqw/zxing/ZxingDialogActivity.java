package com.pingtech.hgqw.zxing;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.pingtech.R;

public class ZxingDialogActivity extends Activity {
	
	private Intent intent;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		final long start = System.currentTimeMillis();
		setContentView(R.layout.dialog_zxing);
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				intent = new Intent(ZxingDialogActivity.this,CaptureActivity.class);
				long end = System.currentTimeMillis();
				
				if(end-start<650){
					try {
						Thread.sleep(650-(end-start));
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				startActivity(intent);
				finish();
			}
		}).start();
		
	}
}
