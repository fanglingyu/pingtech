package com.pingtech.hgqw.module.xtgl.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ToggleButton;

import com.pingtech.R;
import com.pingtech.hgqw.activity.MyActivity;
import com.pingtech.hgqw.utils.Log;
import com.pingtech.hgqw.utils.StringUtils;
import com.pingtech.hgqw.utils.SystemSetting;
import com.pingtech.hgqw.widget.HgqwToast;

public class FunctionSetting extends MyActivity {
	private static final String TAG = "FunctionSetting";

	private SharedPreferences prefs;

	/**
	 * 语音对讲
	 */
	public static final String yydj = "yydj";

	/**
	 * 开启离线
	 */
	public static final String kqlx = "kqlx";

	/**
	 * 推送开关
	 */
	public static final String tskg = "tskg";

	/**
	 * 本地通行验证
	 */
	public static final String bdtxyz = "bdtxyz";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.function_setting);
		setMyActiveTitle(getText(R.string.system) + ">"
				+ getText(R.string.function_setting));
		prefs = getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE);
		ToggleButton toggleButton = (ToggleButton) findViewById(R.id.toggleButton_yydj);
		toggleButton.setChecked(prefs.getBoolean(yydj, false));
		toggleButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				setState(yydj, arg1);
				SystemSetting.setYydjOnOrOff(arg1);
				Log.i(TAG, "语音对讲：" + arg1);
			}
		});
		// 推送开关
		ToggleButton tbTSKG = (ToggleButton) findViewById(R.id.toggleButton_tskg);
		tbTSKG.setChecked(prefs.getBoolean(tskg, false));
		tbTSKG.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				setState(tskg, arg1);
				Log.i(TAG, "推送开关：" + arg1);
				HgqwToast.makeText(FunctionSetting.this,
						getString(R.string.next_ok), HgqwToast.LENGTH_LONG)
						.show();
			}
		});
		// 开启离线
		ToggleButton tbKQLX = (ToggleButton) findViewById(R.id.toggleButton_kqlx);
		tbKQLX.setChecked(prefs.getBoolean(kqlx, false));
		tbKQLX.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				Log.i(TAG, "开启离线：" + arg1);
				setState(kqlx, arg1);
				if (arg1) {
					showBondDialog(getString(R.string.tishi_content_open_offline));
				}
			}
		});
		// 本地通行验证
		ToggleButton tbBDTXYZ = (ToggleButton) findViewById(R.id.toggleButton_bdtxyz);
		tbBDTXYZ.setChecked(prefs.getBoolean(bdtxyz, true));
		tbBDTXYZ.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				setState(bdtxyz, arg1);
				Log.i(TAG, "本地通行验证：" + arg1);
				if (arg1) {
//					showBondDialog(getString(R.string.tishi_content_open_bdtxyz));
				}
			}
		});

	}

	/**
	 * 
	 * @description 提醒用户下载离线数据
	 * @date 2014-4-11
	 * @author zhaotf
	 */
	private void showBondDialog(String content) {

		if (StringUtils.isEmpty(content)) {
			content = "";
		}

		final AlertDialog alertDialog = new AlertDialog.Builder(this)
				.setMessage(content)
				.setCancelable(false)
				.setPositiveButton(R.string.queding,
						new android.content.DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface arg0, int arg1) {
								// TODO Auto-generated method stub
								arg0.cancel();
								// 直接跳回二级页面
								Intent intent = new Intent(
										getApplicationContext(),
										OfflineDataActivity.class);
								startActivity(intent);
							}
						}).create();
		alertDialog.show();
	}
}
