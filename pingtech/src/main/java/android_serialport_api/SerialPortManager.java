package android_serialport_api;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.security.InvalidParameterException;

import android.os.SystemClock;

import com.pingtech.hgqw.utils.DeviceUtils;

public class SerialPortManager {
	/**
	 * 串口设备路径（老版肯麦思）
	 */
	private static final String PATH = "/dev/ttyHS1";
	/**
	 * 串口设备路径（新版肯麦思）
	 */
	private static final String PATH_CFON640 = "/dev/ttyHSL0";
	/**
	 * 串口波特率
	 */

	// private static final int BAUDRATE = 115200;
	// private static final int BAUDRATE = 230400;
	// private static final int BAUDRATE = 345600;
	private static final int BAUDRATE = 460800;

	public static boolean switchRFID = false;
	/**
	 * gpio路径（老版肯麦思）
	 */
	final String GPIO_DEV = "/sys/GPIO/GPIO13/value";
	/**
	 * gpio路径（新版肯麦思）
	 */
	final String GPIO_DEV_CFON640 = "/sys/class/pwv_gpios/as602-en/enable";
	final byte[] UP = { '1' };
	final byte[] DOWN = { '0' };

	private static SerialPortManager mSerialPortManager = new SerialPortManager();

	private SerialPort mSerialPort = null;

	private boolean isOpen;

	private boolean firstOpen = false;

	private OutputStream mOutputStream;

	private InputStream mInputStream;

	private byte[] mBuffer = new byte[50 * 1024];

	private int mCurrentSize = 0;

	private ReadThread mReadThread;

	private String path;
	private String gpio_dev;

	private SerialPortManager() {
		switch (DeviceUtils.getDeviceModel()) {
		case DeviceUtils.DEVICE_MODEL_M:
			path = PATH;
			gpio_dev = GPIO_DEV;
			break;
		case DeviceUtils.DEVICE_MODEL_CFON640:
			path = PATH_CFON640;
			gpio_dev = GPIO_DEV_CFON640;
			break;
		}
	}

	/**
	 * 获取该类的实例对象，为单例
	 * 
	 * @return
	 */
	public static SerialPortManager getInstance() {
		return mSerialPortManager;
	}

	/**
	 * 判断串口是否打开
	 * 
	 * @return true：打开 false：未打开
	 */
	public boolean isOpen() {
		return isOpen;
	}

	/**
	 * 打开串口，如果需要读取身份证和指纹信息，必须先打开串口，调用此方法
	 * 
	 * @throws SecurityException
	 * @throws IOException
	 * @throws InvalidParameterException
	 */
	public boolean openSerialPort() throws SecurityException, IOException,
			InvalidParameterException {
		if (mSerialPort == null) {
			// 上电
			setUpGpio();
			// Open the serial port
			mSerialPort = new SerialPort(new File(path), BAUDRATE, 0);
			mOutputStream = mSerialPort.getOutputStream();
			mInputStream = mSerialPort.getInputStream();
			mReadThread = new ReadThread();
			mReadThread.start();
			isOpen = true;
			firstOpen = true;
			return true;
		}
		return false;
	}

	/**
	 * 关闭串口，如果不需要读取指纹或身份证信息时，就关闭串口(可以节约电池电量)，建议程序退出时关闭
	 */
	public void closeSerialPort() {
		if (mReadThread != null)
			mReadThread.interrupt();
		mReadThread = null;
		try {
			// 断电
			setDownGpio();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		if (mSerialPort != null) {
			try {
				mOutputStream.close();
				mInputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			mSerialPort.close();
			mSerialPort = null;
		}
		isOpen = false;
		firstOpen = false;
		mCurrentSize = 0;
		switchRFID = false;
	}

	protected synchronized int read(byte buffer[], int waittime,
			int endWaitTimeout) {
		if (!isOpen) {
			return 0;
		}
		int sleepTime = 5;
		int length = waittime / sleepTime;
		boolean shutDown = false;
		int[] readDataLength = new int[3];
		for (int i = 0; i < length; i++) {
			if (mCurrentSize == 0) {
				SystemClock.sleep(sleepTime);
				continue;
			} else {
				break;
			}
		}

		if (mCurrentSize > 0) {
			while (!shutDown) {
				SystemClock.sleep(endWaitTimeout / 3);
				readDataLength[0] = readDataLength[1];
				readDataLength[1] = readDataLength[2];
				readDataLength[2] = mCurrentSize;
				if (readDataLength[0] == readDataLength[1]
						&& readDataLength[1] == readDataLength[2]) {
					shutDown = true;
				}
			}
			if (mCurrentSize <= buffer.length) {
				System.arraycopy(mBuffer, 0, buffer, 0, mCurrentSize);
			}
		}
		return mCurrentSize;
	}

	protected synchronized void write(byte[] data) {
		if (!isOpen) {
			return;
		}
		if (firstOpen) {
			SystemClock.sleep(2000);
			firstOpen = false;
		}
		mCurrentSize = 0;
		try {
			mOutputStream.write(data);
		} catch (IOException e) {
		}
	}

	private void setUpGpio() throws IOException {
		FileOutputStream fw = new FileOutputStream(gpio_dev);
		fw.write(UP);
		fw.close();
	}

	private void setDownGpio() throws IOException {
		FileOutputStream fw = new FileOutputStream(gpio_dev);
		fw.write(DOWN);
		fw.close();
	}

	private String getGpioStatus() throws IOException {
		String value;
		BufferedReader br = null;

		FileInputStream inStream = new FileInputStream(gpio_dev);
		br = new BufferedReader(new InputStreamReader(inStream));

		value = br.readLine();
		inStream.close();
		System.out.println(value);
		return value;

	}

	private class ReadThread extends Thread {

		@Override
		public void run() {
			while (!isInterrupted()) {
				int length = 0;
				try {
					byte[] buffer = new byte[100];
					if (mInputStream == null)
						return;
					length = mInputStream.read(buffer);
					if (length > 0) {
						System.arraycopy(buffer, 0, mBuffer, mCurrentSize,
								length);
						mCurrentSize += length;
					}
				} catch (IOException e) {
					e.printStackTrace();
					return;
				}
			}
		}
	}

}
