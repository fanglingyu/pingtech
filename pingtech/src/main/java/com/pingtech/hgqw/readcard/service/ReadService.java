package com.pingtech.hgqw.readcard.service;

import java.util.Arrays;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;
import android.os.RemoteException;
import android.os.SystemClock;
import android_serialport_api.M1CardAPI.Result;
import android_serialport_api.ParseSFZAPI;
import android_serialport_api.ParseSFZAPI.People;
import android_serialport_api.SerialPortManager;

import com.authentication.asynctask.HgqwAsyncM1Card;
import com.authentication.asynctask.HgqwAsyncParseSFZ;
import com.pingtech.R;
import com.pingtech.hgqw.base.BaseApplication;
import com.pingtech.hgqw.entity.CardInfo;
import com.pingtech.hgqw.entity.MessageEntity;
import com.pingtech.hgqw.readcard.utils.Pa8Utils;
import com.pingtech.hgqw.readcard.utils.ReadCardTools;
import com.pingtech.hgqw.utils.DeviceUtils;
import com.pingtech.hgqw.utils.Log;
import com.pingtech.hgqw.utils.StringUtils;
import com.softsz.deviceInterface.DeviceIDCardInterface;
import com.softsz.deviceInterface.ListenerDisplay;
import com.softsz.deviceInterface.ListenerMHFDData;
import com.softsz.deviceInterface.PersonData;

public class ReadService {
	private static final String TAG = "ReadService";

	/** 设备型号： 1 一代警务通mima_PE43 2 二代警务通 M802 */
	private int model = DeviceUtils.getDeviceModel();

	/** 只读取身份证 */
	public static final int READ_TYPE_ID = 1;

	/** 读取身份证和RFID */
	public static final int READ_TYPE_ID_IC = 2;

	/** 只读取RFID加密区数据 */
	public static final int READ_TYPE_ICKEY = 3;

	/** 只读取RFID序列号 */
	public static final int READ_TYPE_DEFAULT_ICKEY = 4;

	/** 读取RFID序列号和加密区数据 */
	public static final int READ_TYPE_DEFAULT_AND_ICKEY = 5;

	public static final int READ_TYPE_SDSR = 6;

	public static final int READ_TYPE_EWM = 7;

	public static final int INIT_FAILE = 0;

	public static final int INIT_SUCCESS = 1;

	private Context context;

	private Handler handler;

	private int type;

	private HgqwAsyncM1Card reader;

	private HgqwAsyncParseSFZ asyncParseSFZ;

	/** 读取IC卡的密码 */
	private static final byte[] PASSWORD_A = { 0x01, 0x00, 0x02, 0x03, 0x00,
			0x09 };
	/** 读取IC卡的位置 */
	private static final int position = 24;

	private static final byte[] PASSWORD_PX_A = { (byte) 0xFF, (byte) 0xFF,
			(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF };

	private static final int POSITION_PX = 17;

	private CardInfo cardInfo;

	private long readWaitTimeM802 = 1000;

	private volatile boolean read_flag = false;

	long startTime = 0;

	long endTime = 0;

	int icCount = 0;

	int icSuccessCount = 0;

	int icInfoCount = 0;

	int icInfoSuccessCount = 0;

	int idCount = 0;

	int idSuccessCount = 0;

	private static ReadService instent = null;

	public static final int KEY_A = 1;

	public static final int KEY_B = 2;

	private Timer timer = null;

	private boolean isPx = false;

	private TimerTask timerTask = null;

	private ReadService() {
	}

	public static ReadService getInstent(Context context, Handler handler,
			int type, boolean isPx) {
		if (instent == null) {
			instent = new ReadService();
		}
		instent.initData(context, handler, type);
		// instent.isPx = isPx;
		return instent;
	}

	public static ReadService getInstent(Context context, Handler handler,
			int type) {
		if (instent == null) {
			instent = new ReadService();
		}
		instent.isPx = false;
		instent.initData(context, handler, type);
		return instent;
	}

	public static ReadService getInstent(Context context, Handler handler) {
		if (instent == null) {
			instent = new ReadService();
		}
		instent.isPx = false;
		int type = getDefaultType();
		instent.initData(context, handler, type);
		return instent;
	}

	public static ReadService getInstent() {
		if (instent == null) {
			instent = new ReadService();
		}
		instent.isPx = false;
		return instent;
	}

	private static int getDefaultType() {
		switch (DeviceUtils.getDeviceModel()) {
		case DeviceUtils.DEVICE_MODEL_MIMA:
			return ReadService.READ_TYPE_DEFAULT_AND_ICKEY;
		case DeviceUtils.DEVICE_MODEL_M:
		case DeviceUtils.DEVICE_MODEL_CFON640:
		case DeviceUtils.DEVICE_MODEL_PA8:
		case DeviceUtils.DEVICE_MODEL_PA9:
			return ReadService.READ_TYPE_ID_IC;
		case DeviceUtils.DEVICE_MODEL_SDK:
			return ReadService.READ_TYPE_ID_IC;
		default:
			break;
		}
		return ReadService.READ_TYPE_ID_IC;
	}

	/**
	 * 
	 * @方法名：initData
	 * @功能说明：重新赋值
	 * @author liums
	 * @date 2013-12-18 下午2:25:12
	 * @param context
	 * @param handler
	 * @param type
	 */
	private void initData(Context context, Handler handler, int type) {
		this.context = context;
		this.handler = handler;
		this.type = type;

	}

	/**
	 * 初始化读卡器
	 */
	public void init() {
		keepScreen();
		switch (model) {
		case DeviceUtils.DEVICE_MODEL_M:
		case DeviceUtils.DEVICE_MODEL_CFON640:
			read_flag = true;
			Log.i(TAG, "initM802");
			if (!initM802()) {
				return;
			}
			// 开始循环读卡
			readLooperM802();
			break;
		case DeviceUtils.DEVICE_MODEL_MIMA:
		case DeviceUtils.DEVICE_MODEL_PA8:
		case DeviceUtils.DEVICE_MODEL_PA9:
			Log.i(TAG, "readLooperPA8");
			// 开始循环读卡
			// readLooperPA8();
			initPa8New();
			break;
		default:
			break;
		}
	}

	private PowerManager.WakeLock mWakeLock = null;

	private void keepScreen() {
		PowerManager pm = (PowerManager) context
				.getSystemService(Context.POWER_SERVICE);
		mWakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK
				| PowerManager.ON_AFTER_RELEASE, "My Tag");
		// in onResume() call

		mWakeLock.acquire();
		// in onPause() call
		// mWakeLock.release();
	}

	private void releaseKeepScreen() {
		if (mWakeLock != null) {
			mWakeLock.release();
			mWakeLock = null;
		}
	}

	public void close() {
		releaseKeepScreen();
		switch (model) {
		case DeviceUtils.DEVICE_MODEL_M:
		case DeviceUtils.DEVICE_MODEL_CFON640:
			read_flag = false;
			closeM802();
			break;
		case DeviceUtils.DEVICE_MODEL_MIMA:
		case DeviceUtils.DEVICE_MODEL_PA8:
		case DeviceUtils.DEVICE_MODEL_PA9:
			closePa8New();
			break;
		default:
			break;
		}
	}

	private void clear() {
		stopTimer();
		this.context = null;
		this.handler = null;
		this.reader = null;
		this.asyncParseSFZ = null;
	}

	private void sendMessage(int what, String text, CardInfo cardInfo) {
		Message msg = null;
		if (handler != null) {
			msg = handler.obtainMessage();
		}
		msg.what = what;
		switch (what) {
		case MessageEntity.TOAST:
			msg.obj = text;
			break;
		default:
			msg.obj = cardInfo;
			break;
		}
		if (handler != null) {
			handler.sendMessage(msg);
		}
	}

	/* M802读卡程序开始 */
	private boolean initM802() {
		Log.i("read", "***start***" + new Date().toLocaleString());
		if (!SerialPortManager.getInstance().isOpen()) {
			try {
				SerialPortManager.getInstance().openSerialPort();
			} catch (Exception e) {
				e.printStackTrace();
				sendMessage(MessageEntity.TOAST,
						context.getString(R.string.init_faile), null);
				return false;
			}
		}
		if (!SerialPortManager.getInstance().isOpen()) {
			sendMessage(MessageEntity.TOAST,
					context.getString(R.string.init_faile), null);
			return false;
		}

		switch (type) {
		case READ_TYPE_ICKEY:
		case READ_TYPE_DEFAULT_AND_ICKEY:
		case READ_TYPE_DEFAULT_ICKEY:
			reader = new HgqwAsyncM1Card(BaseApplication.instent
					.getHandlerThread().getLooper());
			break;
		case READ_TYPE_ID:
			asyncParseSFZ = new HgqwAsyncParseSFZ(BaseApplication.instent
					.getHandlerThread().getLooper(),
					DeviceUtils.getRootPath(context));
			break;
		case READ_TYPE_ID_IC:
			readWaitTimeM802 = 300;
			asyncParseSFZ = new HgqwAsyncParseSFZ(BaseApplication.instent
					.getHandlerThread().getLooper(),
					DeviceUtils.getRootPath(context));
			reader = new HgqwAsyncM1Card(BaseApplication.instent
					.getHandlerThread().getLooper());
			break;
		default:
			break;
		}
		Log.i("read", "***end***" + new Date().toLocaleString());
		return true;

	}

	private void readLooperM802() {
		read_flag = true;
		/*
		 * stopTimer(); timer = new Timer(); timerTask = new TimerTask() {
		 * 
		 * @Override public void run() { runMethod(); } };
		 * timer.schedule(timerTask, 0, readWaitTimeM802);
		 */
		new Thread(new Runnable() {
			@Override
			public void run() {
				while (read_flag) {
					runMethod();
					SystemClock.sleep(readWaitTimeM802);
				}
			}
		}).start();
	}

	private void runMethod() {

		if (!read_flag) {
			return;
		}
		cardInfo = new CardInfo();
		switch (type) {
		case READ_TYPE_ICKEY:
		case READ_TYPE_DEFAULT_AND_ICKEY:
			looperIC();
			break;
		case READ_TYPE_DEFAULT_ICKEY:
			looperDefaultNum();
			break;
		case READ_TYPE_ID:
			looperID();
			break;
		case READ_TYPE_ID_IC:
			looperID();
			if (!read_flag) {
				return;
			}
			SystemClock.sleep(100);
			looperIC();
			break;
		default:
			break;
		}
	}

	/**
	 * 
	 * @方法名：looperDefaultNum
	 * @功能说明：单独读取默认卡号
	 * @author liums
	 * @date 2013-12-18 下午4:40:39
	 */
	private void looperDefaultNum() {
		if (reader == null) {
			return;
		}
		String defaultIckey = reader.readCardNum();
		if (defaultIckey != null) {
			cardInfo.setDefaultIckey(defaultIckey);
			sendMessage(type, null, cardInfo);
		}
	}

	private void looperIC() {
		// Log.i("read", "***start2***" + new Date().toLocaleString());
		String ickey = null;
		String defaultIckey = null;
		if (reader == null) {
			return;
		}
		Result result = reader.read(position, KEY_A, 1, PASSWORD_A);
		if (result != null) {
			cardInfo = new CardInfo();
			byte[][] bs = (byte[][]) result.resultInfo;
			if (bs == null) {
				return;
			}
			ickey = ReadCardTools.byteArrayToString(bs[0]);
			defaultIckey = ReadCardTools.reverseStrToHex_M802(result.num);
			cardInfo.setIckey(ickey);
			cardInfo.setDefaultIckey(defaultIckey);
			cardInfo.setCardType(CardInfo.CARD_TYPE_IC);
		}
		// Log.i("read", "***end2***" + new Date().toLocaleString());
		if (StringUtils.isEmpty(ickey)) {
			return;
		}
		sendMessage(READ_TYPE_DEFAULT_AND_ICKEY, null, cardInfo);
	}

	private void looperID() {
		// Log.i("read", "***start1***" + new Date().toLocaleString());
		if (!read_flag) {
			return;
		}

		People people = asyncParseSFZ
				.readSFZ(ParseSFZAPI.SECOND_GENERATION_CARD);
		if (people != null) {
			cardInfo = new CardInfo();
			cardInfo.setPeople(people);
			cardInfo.setCardType(CardInfo.CARD_TYPE_ID);
			sendMessage(READ_TYPE_ID, null, cardInfo);
		}
		// Log.i("read", "***end1***" + new Date().toLocaleString());
	}

	private void closeM802() {
		SerialPortManager.getInstance().closeSerialPort();
		clear();
	}

	/**
	 * 
	 * @方法名：stopTimer
	 * @功能说明：停止
	 * @author liums
	 * @date 2013-12-13 上午10:59:48
	 */
	private void stopTimer() {
		if (timer != null) {
			timer.cancel();
			timer = null;
		}

		if (timerTask != null) {
			timerTask.cancel();
			timerTask = null;
		}

	}

	/* M802读卡程序结束 */

	/* PA8读卡程序开始 */
	private void hasDefaultIckey(String defaultIckey, boolean isNewInterface) {
		cardInfo = new CardInfo();
		cardInfo.setDefaultIckey(defaultIckey);
		if (StringUtils.isEmpty(defaultIckey)) {
			Log.i(TAG, "hasDefaultIckey：isEmpty");
			return;
		}
		// Log.i(TAG, "hasDefaultIckey：" + defaultIckey);
		sendMessage(READ_TYPE_DEFAULT_ICKEY, null, cardInfo);

	}

	/**
	 * 封装身份证数据
	 * 
	 * @param people
	 *            新接口直接使用返回的数据
	 * @param isNewInterface
	 *            是否是新接口
	 */
	private void hasSfzInfo(People people, boolean isNewInterface) {
		CardInfo cardInfo = new CardInfo();
		cardInfo.setPeople(people);
		sendMessage(READ_TYPE_ID, null, cardInfo);
	}

	private void hasRfid(String ickey, String defaultIckey,
			boolean isNewInterface) {
		// ToneGenerator tone = new ToneGenerator(AudioManager.STREAM_MUSIC,
		// ToneGenerator.MAX_VOLUME);
		// tone.startTone(ToneGenerator.TONE_PROP_ACK, 500);
		// tone.release();
		cardInfo = new CardInfo();

		cardInfo.setIckey(ickey);
		cardInfo.setDefaultIckey(defaultIckey);
		if (StringUtils.isEmpty(ickey)) {
			return;
		}
		Log.i("ReadService", "ickey：" + ickey + "，defaultIckey：" + defaultIckey);

		sendMessage(READ_TYPE_DEFAULT_AND_ICKEY, null, cardInfo);
	}

	/* PA8读卡程序结束 */
	private DeviceIDCardInterface idcard = null;

	private void initPa8New() {
		// .remote.readid.DeviceService com.softsz.mimaqudongservice
		// KillPackage.kill(".remote.readid.DeviceService");
		// KillPackage.kill("com.pingtech");
		// context.stopService(new Intent("com.softsz.IDCARDACTION"))
		context.bindService(new Intent("com.softsz.IDCARDACTION"), connectPa8,
				Context.BIND_AUTO_CREATE);
	}

	private void closePa8New() {
		if (idcard != null) {
			try {
				idcard.stopReadIDCard();
			} catch (RemoteException e) {
				e.printStackTrace();
			}
			idcard = null;
			context.unbindService(connectPa8);
		}
	}

	private ServiceConnection connectPa8 = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName name) {
			idcard = null;
			Log.i(TAG, "connectPa8 onServiceDisconnected");
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			idcard = DeviceIDCardInterface.Stub.asInterface(service);
			Log.i(TAG, "connectPa8 onServiceConnected");
			try {
				// 读卡接口如果只启动身份证，关闭的时候无法切换回GPS定位，所以两种读卡全部启动
				idcard.setIntervalTime(1200);
				idcard.setReadIdListener(sfzCall);
				Log.i(TAG, "isPx=" + isPx);
				// if (isPx) {
				// idcard.ReadRFIDCard(true, (byte) 8, (byte) POSITION_PX,
				// PASSWORD_PX_A, rfidCall);
				// } else {
				idcard.ReadRFIDCard(true, (byte) 8, (byte) position,
						PASSWORD_A, rfidCall);
				// }

			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	};

	private ListenerMHFDData.Stub rfidCall = new ListenerMHFDData.Stub() {
		@Override
		public void getMFHLData(com.softsz.deviceInterface.RFIDData data)
				throws RemoteException {
			Log.i(TAG, "ListenerMHFDData ");
			if (!checkType("rfidCall")) {
				Log.i(TAG, "!checkType(\"rfidCall\")");

				return;
			}
			byte[] defaultIckeyResultByte = data.getMSN();
			byte[] defaultIckeyByte = Arrays.copyOfRange(
					defaultIckeyResultByte, 10, 14);
			String defaultIckey = ReadCardTools
					.byteArrayToHex(defaultIckeyByte);

			byte[] ickeyResultByte = data.getContent();
			byte[] ickeyByte = null;
			String ickey = null;
			// if(isPx){
			// if (ickeyResultByte != null && ickeyResultByte.length > 0) {
			// ickeyByte = Arrays.copyOfRange(ickeyResultByte, 16, 18);
			// ickey = ReadCardTools.byteArrayToString(ickeyByte);
			// }
			// }else{
			if (ickeyResultByte != null && ickeyResultByte.length > 0) {
				ickeyByte = Arrays.copyOfRange(ickeyResultByte, 14, 29);
				ickey = ReadCardTools.byteArrayToString(ickeyByte);
			}
			// }

			// playSound();

			if (StringUtils.isNullOrEmpty(defaultIckey)) {
				Log.i(TAG, "type == " + type
						+ " , StringUtils.isNullOrEmpty(msn) return");
				return;
			}
			if (type == READ_TYPE_DEFAULT_ICKEY) {
				hasDefaultIckey(defaultIckey, true);
				return;
			}

			if (StringUtils.isNullOrEmpty(ickey)) {
				Log.i(TAG, "type == " + type
						+ " , StringUtils.isNullOrEmpty(ickey) return");
				return;
			}
			hasRfid(ickey, defaultIckey, true);

		}

	};

	private ListenerDisplay.Stub sfzCall = new ListenerDisplay.Stub() {

		public void display() throws RemoteException {
			if (!checkType("sfzCall")) {
				Log.i(TAG, "!checkType(\"sfzCall\")");
				return;
			}
			PersonData data = null;
			try {
				data = PersonData.parse();
				Log.i(TAG, "PersonData=" + data.toString());
			} catch (Exception e) {
				e.printStackTrace();
				Log.i(TAG, "PersonData=null");
				return;
			}

			People people = Pa8Utils.rebuildPersonData(data);
			// playSound();
			hasSfzInfo(people, true);
		}

	};

	private void playSound() {
		// ToneGenerator tone = new ToneGenerator(AudioManager.STREAM_MUSIC,
		// ToneGenerator.MAX_VOLUME);
		// tone.startTone(ToneGenerator.TONE_PROP_BEEP,300);
		// tone.release();
	}

	/* PA8读卡程序结束 */

	/**
	 * PA8单独启动身份证或RFID读卡时可能会读取另一个证件类型，此处进行过滤
	 * 
	 * @param type
	 *            "rfidCall" "sfzCall"
	 * @return
	 */
	protected boolean checkType(String type) {
		if (StringUtils.isNullOrEmpty(type)) {
			return false;
		}
		if ("rfidCall".equals(type)) {
			switch (this.type) {
			case READ_TYPE_ICKEY:
			case READ_TYPE_DEFAULT_AND_ICKEY:
			case READ_TYPE_DEFAULT_ICKEY:
			case READ_TYPE_ID_IC:
				return true;
			case READ_TYPE_ID:
				return false;
			default:
				break;
			}
		} else if ("sfzCall".equals(type)) {
			switch (this.type) {
			case READ_TYPE_ICKEY:
			case READ_TYPE_DEFAULT_AND_ICKEY:
			case READ_TYPE_DEFAULT_ICKEY:
				return false;
			case READ_TYPE_ID:
			case READ_TYPE_ID_IC:
				return true;
			default:
				break;
			}
		}
		return true;

	}

}
