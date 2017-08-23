package android_serialport_api;

import android.os.SystemClock;
import android.util.Log;

public class BeidouAPI {
	
	private static final byte[] SWITCH_COMMAND = "D&C00040106".getBytes();
	
	/*启动控制命令*/
	
	/**
	 * 模块恢复出厂模式
	 */
	public static final byte[] RESTORE_FACTORY_MODEL_COMMAND = "$PMTK104*29\r\n"
			.getBytes();

	/**
	 * 模块冷启动命令
	 */
	public static final byte[] COLD_START_COMMAND = "$PMTK103*30\r\n"
			.getBytes();

	/**
	 * 模块温启动命令
	 */
	public static final byte[] WARM_START_COMMAND = "$PMTK102*31\r\n"
			.getBytes();

	/**
	 * 模块热启动命令
	 */
	public static final byte[] HOT_START_COMMAND = "$PMTK102*32\r\n"
			.getBytes();
	
	
	/*模式切换命令*/
	
	/**
	 * 使用纯GPS 模式操作
	 */
	public static final byte[] GPS_MODE_OPERATION_COMMAND = "$PMTK353,1,0,0,0,0*2A\r\n"
			.getBytes();
	
	/**
	 * 使用BDS+GPS 混星模式操作
	 */
	public static final byte[] GPS_BDS_MODE_OPERATION_COMMAND = "$PMTK353,1,0,0,0,1*2B\r\n"
			.getBytes();
	
	
	/**
	 * 使用纯BDS 模式操作
	 */
	public static final byte[] BDS_MODE_OPERATION_COMMAND = "$PMTK353,0,0,0,0,1*2A\r\n"
			.getBytes();
	
	
	/*语句及其频度控制命令*/
	
	/**
	 * 模块恢复出厂语句模式
	 */
	public static final byte[] RESTORE_FACTORY_STATEMENT_MODEL_COMMAND = "$PMTK314,-1*04\r\n"
			.getBytes();
	
	/**
	 * 模块只输出GLL 语句，频度为1HZ
	 */
	public static final byte[] GLL_STATEMENT_MODEL_COMMAND = "$PMTK314,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0*29\r\n"
			.getBytes();
	
	/**
	 * 模块只输出RMC 语句，频度为1HZ
	 */
	public static final byte[] RMC_STATEMENT_MODEL_COMMAND = "$PMTK314,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0*29\r\n"
			.getBytes();
	
	/**
	 * 模块只输出VTG 语句，频度为1HZ
	 */
	public static final byte[] VTG_STATEMENT_MODEL_COMMAND = "$PMTK314,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0*29\r\n"
			.getBytes();
	
	/**
	 * 模块只输出GGA 语句，频度为1HZ
	 */
	public static final byte[] GGA_STATEMENT_MODEL_COMMAND = "$PMTK314,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0*29\r\n"
			.getBytes();
	
	/**
	 * 模块只输出GSA 语句，频度为1HZ
	 */
	public static final byte[] GSA_STATEMENT_MODEL_COMMAND = "$PMTK314,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0*29\r\n"
			.getBytes();
	
	/**
	 * 模块只输出GSV 语句，频度为1HZ
	 */
	public static final byte[] GSV_STATEMENT_MODEL_COMMAND = "$PMTK314,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0*29\r\n"
			.getBytes();
	
	/**
	 * 模块只输出ZDA 语句，频度为1HZ
	 */
	public static final byte[] ZDA_STATEMENT_MODEL_COMMAND = "$PMTK314,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0*29\r\n"
			.getBytes();
	
	/**
	 * 输出RMC, GGA, GSA语句，频度为1HZ；输出GSV 语句，频度为5HZ
	 */
	public static final byte[] RMC_GGA_GSA_GSV_STATEMENT_MODEL_COMMAND = "$PMTK314,0,1,0,1,1,5,0,0,0,0,0,0,0,0,0,0,0,0,0*2C\r\n"
			.getBytes();
	
	/**
	 * 模块定位输出频度为100ms（10HZ）
	 */
	public static final byte[] OUTPUT_FREQUENCY_100MS_COMMAND = "$PMTK220,100*2F\r\n"
			.getBytes();
	
	/**
	 * 模块定位输出频度为200ms（5HZ）
	 */
	public static final byte[] OUTPUT_FREQUENCY_200MS_COMMAND = "$PMTK220,200*2C\r\n"
			.getBytes();
	
	/**
	 * 模块定位输出频度为500ms（2HZ）
	 */
	public static final byte[] OUTPUT_FREQUENCY_500MS_COMMAND = "$PMTK220,500*2B\r\n"
			.getBytes();
	
	/**
	 * 模块定位输出频度为1000ms（1HZ）
	 */
	public static final byte[] OUTPUT_FREQUENCY_1000MS_COMMAND = "$PMTK220,1000*1F\r\n"
			.getBytes();
	
	/**
	 * 模块定位输出频度为2000ms（0.5HZ）
	 */
	public static final byte[] OUTPUT_FREQUENCY_2000MS_COMMAND = "$PMTK220,2000*1C\r\n"
			.getBytes();
	
	
	

	private byte[] buffer = new byte[1024];

	public void get() {

		
		SerialPortManager.getInstance().write(WARM_START_COMMAND);
		int length = SerialPortManager.getInstance().read(buffer, 3000, 100);
		Log.i("whw", "length=" + length);
		Log.i("whw", "str=" + new String(buffer));
		
//		SerialPortManager.getInstance().write(ZDA_STATEMENT_MODEL_COMMAND);
//		int length2 = SerialPortManager.getInstance().read(buffer, 3000, 100);
//		Log.i("whw", "length2=" + length2);
	}
	
	public void receive(byte[] command){
		SerialPortManager.getInstance().write(WARM_START_COMMAND);
		int length = SerialPortManager.getInstance().read(buffer, 3000, 100);
		Log.i("whw", "length=" + length);
		Log.i("whw", "str=" + new String(buffer));
	}
	
	private boolean isSwitch = false;
	
	public boolean isSwitch() {
		return isSwitch;
	}

	public void switchStatus(){
		SerialPortManager.getInstance().write(SWITCH_COMMAND);
		SystemClock.sleep(100);
	}
}
