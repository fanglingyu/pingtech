package com.authentication.asynctask;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android_serialport_api.ParseSFZAPI;
import android_serialport_api.ParseSFZAPI.People;
import android_serialport_api.ParseSFZAPI.Result;

public class HgqwAsyncParseSFZ extends Handler {

	private static final int READ_SFZ = 1000;

	private static final int READ_MODULE = 2000;

	private static final int FIND_CARD_SUCCESS = 1001;

	private static final int FIND_CARD_FAIL = 1002;

	private static final int FIND_MODULE_SUCCESS = 1003;

	private static final int FIND_MODULE_FAIL = 1004;

	public static final int DATA_SIZE = 1295;

	private byte[] buffer = new byte[DATA_SIZE];

	private ParseSFZAPI parseAPI;

	private String rootPath;

	private String path;

	private Handler mWorkerThreadHandler;

	private OnReadSFZListener onReadSFZListener;

	private OnReadModuleListener onReadModuleListener;

	public void setOnReadModuleListener(OnReadModuleListener onReadModuleListener) {
		this.onReadModuleListener = onReadModuleListener;
	}

	public void setOnReadSFZListener(OnReadSFZListener onReadSFZListener) {
		this.onReadSFZListener = onReadSFZListener;
	}

	public interface OnReadModuleListener {
		void onReadSuccess(String module);

		void onReadFail(int confirmationCode);
	}

	public interface OnReadSFZListener {
		void onReadSuccess(People people);

		void onReadFail(int confirmationCode);
	}

	public HgqwAsyncParseSFZ(Looper looper, String rootPath) {
		parseAPI = new ParseSFZAPI(looper, rootPath);
		mWorkerThreadHandler = createHandler(looper);
	}

	protected Handler createHandler(Looper looper) {
		return new WorkerHandler(looper);
	}

	protected class WorkerHandler extends Handler {
		public WorkerHandler(Looper looper) {
			super(looper);
		}

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case READ_SFZ:
				Result resultSFZ = parseAPI.read(msg.arg1);
				if (resultSFZ.confirmationCode == Result.SUCCESS) {
					HgqwAsyncParseSFZ.this.obtainMessage(FIND_CARD_SUCCESS, resultSFZ.resultInfo).sendToTarget();
				} else {
					HgqwAsyncParseSFZ.this.obtainMessage(FIND_CARD_FAIL, resultSFZ.confirmationCode, -1).sendToTarget();
				}
				break;
			case READ_MODULE:
				Result resultModule = parseAPI.readModule();
				Log.i("whw", "module=" + resultModule.resultInfo);
				if (resultModule.confirmationCode == Result.SUCCESS) {
					HgqwAsyncParseSFZ.this.obtainMessage(FIND_MODULE_SUCCESS, resultModule.resultInfo).sendToTarget();
				} else {
					HgqwAsyncParseSFZ.this.obtainMessage(FIND_MODULE_FAIL, resultModule.confirmationCode, -1).sendToTarget();
				}
				break;
			default:
				break;
			}
		}
	}

	public People readSFZ(int cardType) {
		Result resultSFZ = parseAPI.read(cardType);
		if (resultSFZ.confirmationCode == Result.SUCCESS) {
			return (People) resultSFZ.resultInfo;
		}
		return null;
	}

	public void readModuleNum() {
		mWorkerThreadHandler.obtainMessage(READ_MODULE).sendToTarget();
	}

	@Override
	public void handleMessage(Message msg) {
		super.handleMessage(msg);
		switch (msg.what) {
		case FIND_CARD_SUCCESS:
			if (onReadSFZListener != null) {
				onReadSFZListener.onReadSuccess((People) msg.obj);
			}
			break;
		case FIND_CARD_FAIL:
			if (onReadSFZListener != null) {
				onReadSFZListener.onReadFail(msg.arg1);
			}
			break;
		case FIND_MODULE_SUCCESS:
			if (onReadModuleListener != null) {
				onReadModuleListener.onReadSuccess((String) msg.obj);
			}
			break;
		case FIND_MODULE_FAIL:
			if (onReadModuleListener != null) {
				onReadModuleListener.onReadFail(msg.arg1);
			}
			break;
		default:
			break;
		}
	}

}
