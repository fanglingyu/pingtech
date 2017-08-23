package com.pingtech.hgqw.mqtt.service;

import java.io.File;
import java.util.Locale;

import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttDefaultFilePersistence;
import org.eclipse.paho.client.mqttv3.MqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.eclipse.paho.client.mqttv3.MqttTopic;
import org.eclipse.paho.client.mqttv3.internal.MemoryPersistence;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.provider.Settings.Secure;
import android.util.Log;

import com.pingtech.R;
import com.pingtech.hgqw.base.BaseApplication;
import com.pingtech.hgqw.utils.StringUtils;

/**
 * 
 * 
 * 类描述：推送服务
 * 
 * <p>
 * Title: 系统名称-MqttService.java
 * </p>
 * <p>
 * Copyright: Copyright (c) 2014
 * </p>
 * <p>
 * Company: 品恩科技
 * </p>
 * 
 * @author zhaotf
 * @version 1.0
 * @date 2014-3-24 下午2:58:48
 */
public class MqttService extends Service implements MqttCallback {
	public static final String DEBUG_TAG = "MqttService"; // Debug TAG

	public static final String MQTT_MSG_RECEIVED_INTENT = "com.qonect.services.mqtt.MSGRECVD";

	public static final String MQTT_MSG_RECEIVED_TOPIC = "com.qonect.services.mqtt.MSGRECVD_TOPIC";

	public static final String MQTT_MSG_RECEIVED_MSG = "com.qonect.services.mqtt.MSGRECVD_MSG";

	public static final String MQTT_PUBLISH_MSG_TOPIC = "com.qonect.services.mqtt.SENDMSG_TOPIC"; // topic

	public static final String MQTT_PUBLISH_MSG_QOS = "com.qonect.services.mqtt.SENDMSG_QOS"; // qos

	public static final String MQTT_PUBLISH_MSG = "com.qonect.services.mqtt.SENDMSG_MSG"; // send
																							// msg

	public static final String MQTT_SUBSCRIBE_MSG_TOPIC = "com.qonect.services.mqtt.SUBSCRIBE_TOPIC"; // 订阅topic

	public static final String MQTT_SUBSCRIBE_MSG_QOS = "com.qonect.services.mqtt.SUBSCRIBE_QOS"; // 订阅qos

	public static final String MQTT_UNSUBSCRIBE_MSG_TOPIC = "com.qonect.services.mqtt.UNSUBSCRIBE_TOPIC"; // 取消订阅topic

	private static final String MQTT_THREAD_NAME = "MqttService[" + DEBUG_TAG
			+ "]"; // Handler
					// Thread
					// ID

	private static String MQTT_BROKER = "";// "10.10.2.205"; // Broker URL or IP
											// Address

	private static final int MQTT_PORT = 1883; // Broker Port

	public static final int MQTT_QOS_0 = 0; // QOS Level 0 ( Delivery Once no
											// confirmation )

	public static final int MQTT_QOS_1 = 1; // QOS Level 1 ( Delevery at least
											// Once with confirmation )

	public static final int MQTT_QOS_2 = 2; // QOS Level 2 ( Delivery only once
											// with confirmation with handshake
											// )

	private static final int MQTT_KEEP_ALIVE = 240000; // KeepAlive Interval in
														// MS

	private static final String MQTT_KEEP_ALIVE_TOPIC_FORAMT = "/users/%s/keepalive"; // Topic
																						// format
																						// for
																						// KeepAlives

	private static final byte[] MQTT_KEEP_ALIVE_MESSAGE = { 0 }; // Keep Alive
																	// message
																	// to send

	private static final int MQTT_KEEP_ALIVE_QOS = MQTT_QOS_0; // Default
																// Keepalive QOS

	private static final boolean MQTT_CLEAN_SESSION = true; // Start a clean
															// session?

	private static final String MQTT_URL_FORMAT = "tcp://%s:%d"; // URL Format
																	// normally
																	// don't
																	// change

	private static final String ACTION_START = DEBUG_TAG + ".START"; // Action
																		// to
																		// start

	private static final String ACTION_STOP = DEBUG_TAG + ".STOP"; // Action to
																	// stop

	private static final String ACTION_KEEPALIVE = DEBUG_TAG + ".KEEPALIVE"; // Action
																				// to
																				// keep
																				// alive
																				// used
																				// by
																				// alarm
																				// manager

	private static final String ACTION_RECONNECT = DEBUG_TAG + ".RECONNECT"; // Action
																				// to
																				// reconnect

	private static final String DEVICE_ID_FORMAT = "andr_%s"; // Device ID
																// Format, add
																// any prefix
																// you'd like
																// Note: There
																// is a 23
																// character
																// limit you
																// will get
																// An NPE if you
																// go over that
																// limit

	private boolean mStarted = true; // Is the Client started?

	private boolean isOpen = false;

	private String mDeviceId; // Device ID, Secure.ANDROID_ID

	private Handler mConnHandler; // Seperate Handler thread for networking

	private MqttDefaultFilePersistence mDataStore; // Defaults to FileStore

	private MemoryPersistence mMemStore; // On Fail reverts to MemoryStore

	private MqttConnectOptions mOpts; // Connection Options

	private MqttTopic mKeepAliveTopic; // Instance Variable for Keepalive topic

	private MqttClient mClient; // Mqtt Client

	private AlarmManager mAlarmManager; // Alarm manager to perform repeating
										// tasks

	private ConnectivityManager mConnectivityManager; // To check for
														// connectivity changes

	/**
	 * Start MQTT Client
	 * 
	 * @param Context
	 *            context to start the service with
	 * @return void
	 */
	public static void actionStart(Context ctx) {
		Intent i = new Intent(ctx, MqttService.class);
		i.setAction(ACTION_START);
		ctx.startService(i);
	}

	/**
	 * Stop MQTT Client
	 * 
	 * @param Context
	 *            context to start the service with
	 * @return void
	 */
	public static void actionStop(Context ctx) {
		Intent i = new Intent(ctx, MqttService.class);
		i.setAction(ACTION_STOP);
		ctx.startService(i);
	}

	/**
	 * Send a KeepAlive Message
	 * 
	 * @param Context
	 *            context to start the service with
	 * @return void
	 */
	public static void actionKeepalive(Context ctx) {
		Intent i = new Intent(ctx, MqttService.class);
		i.setAction(ACTION_KEEPALIVE);
		ctx.startService(i);
	}

	/**
	 * Initalizes the DeviceId and most instance variables Including the
	 * Connection Handler, Datastore, Alarm Manager and ConnectivityManager.
	 */
	@Override
	public void onCreate() {
		super.onCreate();

		mDeviceId = String.format(DEVICE_ID_FORMAT,
				Secure.getString(getContentResolver(), Secure.ANDROID_ID));

		HandlerThread thread = new HandlerThread(MQTT_THREAD_NAME);
		thread.start();

		mConnHandler = new Handler(thread.getLooper());

		final String path = Environment.getExternalStorageDirectory().getPath()
				+ File.separator + "pingtech" + File.separator + "mqtt";
		File file = new File(path);
		if (!(file.exists() && file.isDirectory())) {
			file.mkdirs();
		}
		try {
			mDataStore = new MqttDefaultFilePersistence(path);
		} catch (MqttPersistenceException e) {
			e.printStackTrace();
			mDataStore = null;
			mMemStore = new MemoryPersistence();
		}

		mOpts = new MqttConnectOptions();
		mOpts.setCleanSession(MQTT_CLEAN_SESSION);
		mOpts.setConnectionTimeout(1000 * 30);
		// mOpts.setKeepAliveInterval(13);
		// Do not set keep alive interval on mOpts we keep track of it with
		// alarm's

		mAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
		mConnectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
	}

	/**
	 * Service onStartCommand Handles the action passed via the Intent
	 * 
	 * @return START_REDELIVER_INTENT
	 */
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);

		String action = intent.getAction();

		Log.i(DEBUG_TAG, "Received action of " + action);

		if (action == null) {
			Log.i(DEBUG_TAG,
					"Starting service with no action\n Probably from a crash");
		} else {
			if (action.equals(ACTION_START)) {
				Log.i(DEBUG_TAG, "Received ACTION_START");
				start();
			} else if (action.equals(ACTION_STOP)) {
				stop();
			} else if (action.equals(ACTION_KEEPALIVE)) {
				keepAlive();
			} else if (action.equals(ACTION_RECONNECT)) {
				if (isNetworkAvailable()) {
					reconnectIfNecessary();
				}
			}
		}

		return START_REDELIVER_INTENT;
	}

	/**
	 * Attempts connect to the Mqtt Broker and listen for Connectivity changes
	 * via ConnectivityManager.CONNECTVITIY_ACTION BroadcastReceiver
	 */
	private synchronized void start() {
		// if (mStarted) {
		// Log.i(DEBUG_TAG, "Attempt to start while already started");
		// return;
		// }

		if (isOpen) {
			Log.i(DEBUG_TAG, "Attempt to start while already started");
			return;
		}

		if (hasScheduledKeepAlives()) {
			stopKeepAlives();
		}

		connect();

		registerReceiver(mConnectivityReceiver, new IntentFilter(
				ConnectivityManager.CONNECTIVITY_ACTION));
	}

	/**
	 * Attempts to stop the Mqtt client as well as halting all keep alive
	 * messages queued in the alarm manager
	 */
	private synchronized void stop() {
		// if (!mStarted) {
		// Log.i(DEBUG_TAG, "Attemtpign to stop connection that isn't running");
		// return;
		// }

		if (mClient != null) {
			mConnHandler.post(new Runnable() {
				@Override
				public void run() {
					try {
						if (mClient != null) {
							mClient.disconnect();
							mClient = null;
						}
						if (mDataStore != null) {
							mDataStore.clear();
							mDataStore.close();
							mDataStore = null;
						}
						if (mMemStore != null) {
							mMemStore.clear();
							mMemStore.close();
							mMemStore = null;
						}

					} catch (MqttException ex) {
						ex.printStackTrace();
					}
					// mStarted = false;

					stopKeepAlives();
				}
			});
		}

		isOpen = false;
		try {
			unregisterReceiver(mConnectivityReceiver);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @方法名：getPic
	 * @功能说明：得到订阅编号
	 * @author zhaotf
	 * @date 2014-3-31 下午5:44:59
	 * @return
	 */
	private String getPic() {
		String mark1 = "://";
		String mark2 = ":";
		String ip = "";
		try {
			ip = BaseApplication.instent.getSharedPreferences(
					getString(R.string.app_name), MODE_PRIVATE).getString(
					getString(R.string.webservice_url), "");
			if (ip.contains(mark1)) {
				ip = ip.substring(ip.indexOf(mark1) + mark1.length());
				if (ip.contains(mark2)) {
					ip = ip.substring(0, ip.indexOf(mark2));
				}
			}
		} catch (Exception e) {
		}
		if (StringUtils.isEmpty(ip)) {
			ip = "192.168.1.1";
		}
		MQTT_BROKER = ip;
		return ip;
	}

	/**
	 * Connects to the broker with the appropriate datastore
	 */
	private synchronized void connect() {
		String url = String.format(Locale.CHINA, MQTT_URL_FORMAT, getPic(),
				MQTT_PORT);
		Log.i(DEBUG_TAG, "Connecting with URL: " + url);
		try {
			if (mDataStore != null) {
				Log.i(DEBUG_TAG, "Connecting with DataStore");
				mClient = new MqttClient(url, mDeviceId, mDataStore);
			} else {
				Log.i(DEBUG_TAG, "Connecting with MemStore");
				mClient = new MqttClient(url, mDeviceId, mMemStore);
			}
		} catch (MqttException e) {
			mClient = null;
			Log.i(DEBUG_TAG, " mClient初始化   错误");
			e.printStackTrace();
		}

		mConnHandler.post(new Runnable() {
			@Override
			public void run() {
				try {
					if (mClient == null || mOpts == null) {
						Log.i(DEBUG_TAG, "mClient == null :"
								+ (mClient == null) + "  mOpts == null :"
								+ (mOpts == null));
						return;
					}
					mClient.connect(mOpts);

					// 获取topic
					String topic = new StringBuffer()
							.append(BaseApplication.instent.getUserInfo()
									.getUserID())
							.append(getString(R.string.xhx))
							.append(BaseApplication.instent
									.getSystemSettingInfo().getServerKadm())
							.toString();
					// topic = new
					// StringBuffer().append(BaseApplication.instent.getUserInfo().getUserID()).append(getString(R.string.xhx))
					// .append("209").toString();

					if (mClient.isConnected()) {
						mClient.subscribe(topic, 1);
					} else {
						Log.i(DEBUG_TAG, "mClient未连接，不能订阅默认信息");
						mClient = null;
						goOnReConnect = true;
						Log.i(DEBUG_TAG, "mConnHandler 连接错误");
						// mStarted = true;
						// 重新连接
						reConnect();
						return;
					}
					// subscribeRoot();
					mClient.setCallback(MqttService.this);
					// mStarted = true; // Service is now connected

					isOpen = true;
					goOnReConnect = false;

					Log.i(DEBUG_TAG,
							"Successfully connected and subscribed starting keep alives");

					startKeepAlives();
				} catch (MqttException e) {
					mClient = null;
					goOnReConnect = true;
					Log.i(DEBUG_TAG, "mConnHandler1 连接错误");
					// 重新连接
					reConnect();
					e.printStackTrace();
				} catch (Exception e) {
					mClient = null;
					goOnReConnect = true;
					Log.i(DEBUG_TAG, "mConnHandler1 连接错误");
					// 重新连接
					reConnect();
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Schedules keep alives via a PendingIntent in the Alarm Manager
	 */
	private void startKeepAlives() {
		Intent i = new Intent();
		i.setClass(this, MqttService.class);
		i.setAction(ACTION_KEEPALIVE);
		PendingIntent pi = PendingIntent.getService(this, 0, i, 0);
		mAlarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
				System.currentTimeMillis() + MQTT_KEEP_ALIVE, MQTT_KEEP_ALIVE,
				pi);
	}

	/**
	 * Cancels the Pending Intent in the alarm manager
	 */
	private void stopKeepAlives() {
		Intent i = new Intent();
		i.setClass(this, MqttService.class);
		i.setAction(ACTION_KEEPALIVE);
		PendingIntent pi = PendingIntent.getService(this, 0, i, 0);
		mAlarmManager.cancel(pi);
	}

	/**
	 * Publishes a KeepALive to the topic in the broker
	 */
	private synchronized void keepAlive() {
		if (isConnected()) {
			try {
				sendKeepAlive();
				return;
			} catch (MqttConnectivityException ex) {
				ex.printStackTrace();
				reconnectIfNecessary();
			} catch (MqttPersistenceException ex) {
				ex.printStackTrace();
				stop();
			} catch (MqttException ex) {
				ex.printStackTrace();
				stop();
			}
		}
	}

	/**
	 * Checkes the current connectivity and reconnects if it is required.
	 */
	private synchronized void reconnectIfNecessary() {
		if (mClient == null) {
			connect();
		}
	}

	/**
	 * Query's the NetworkInfo via ConnectivityManager to return the current
	 * connected state
	 * 
	 * @return boolean true if we are connected false otherwise
	 */
	private boolean isNetworkAvailable() {
		NetworkInfo info = mConnectivityManager.getActiveNetworkInfo();

		return (info == null) ? false : info.isConnected();
	}

	/**
	 * Verifies the client State with our local connected state
	 * 
	 * @return true if its a match we are connected false if we aren't connected
	 */
	private boolean isConnected() {
		if (mClient != null && !mClient.isConnected()) {
			Log.i(DEBUG_TAG,
					"Mismatch between what we think is connected and what is connected");
		}

		if (mClient != null) {
			return (mClient.isConnected()) ? true : false;
		}

		return false;
	}

	/**
	 * Receiver that listens for connectivity chanes via ConnectivityManager
	 */
	private final BroadcastReceiver mConnectivityReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.i(DEBUG_TAG, "Connectivity Changed...");
			if (isNetworkAvailable() && !isConnected()) {
				if (goOnReConnect) {
					// 如果正在进行重新连接操作，不在执行“重连接”操作
					Log.i(DEBUG_TAG,
							"MqttService>mConnectivityReceiver:“冲链接“操作正在执行，不再执行“重连接”操作");
					return;
				}
				// 如果服务开启，并且没有连接服务器，就开始重连接
				if (!isConnected()) {
					reConnect();
				}
			} else {
				// connect();
			}
		}
	};

	/**
	 * Sends a Keep Alive message to the specified topic
	 * 
	 * @see MQTT_KEEP_ALIVE_MESSAGE
	 * @see MQTT_KEEP_ALIVE_TOPIC_FORMAT
	 * @return MqttDeliveryToken specified token you can choose to wait for
	 *         completion
	 */
	private synchronized MqttDeliveryToken sendKeepAlive()
			throws MqttConnectivityException, MqttPersistenceException,
			MqttException {
		if (!isConnected())
			throw new MqttConnectivityException();

		if (mKeepAliveTopic == null) {
			mKeepAliveTopic = mClient.getTopic(String.format(Locale.US,
					MQTT_KEEP_ALIVE_TOPIC_FORAMT, mDeviceId));
		}

		Log.i(DEBUG_TAG, "Sending Keepalive to " + MQTT_BROKER);

		MqttMessage message = new MqttMessage(MQTT_KEEP_ALIVE_MESSAGE);
		message.setQos(MQTT_KEEP_ALIVE_QOS);

		return mKeepAliveTopic.publish(message);
	}

	/**
	 * Query's the AlarmManager to check if there is a keep alive currently
	 * scheduled
	 * 
	 * @return true if there is currently one scheduled false otherwise
	 */
	private synchronized boolean hasScheduledKeepAlives() {
		Intent i = new Intent();
		i.setClass(this, MqttService.class);
		i.setAction(ACTION_KEEPALIVE);
		PendingIntent pi = PendingIntent.getBroadcast(this, 0, i,
				PendingIntent.FLAG_NO_CREATE);

		return (pi != null) ? true : false;
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	/**
	 * Connectivity Lost from broker
	 */
	@Override
	public void connectionLost(Throwable arg0) {

		Log.i(DEBUG_TAG, "MqttService  connectionLost");

		if (goOnReConnect) {
			// 如果正在进行重新连接操作，不在执行“重连接”操作
			Log.i(DEBUG_TAG, "MqttService>connectionLost:“重连接”已经在执行，不用再执行");
			return;
		}
		stopKeepAlives();
		mClient = null;
		isOpen = false;
		// 重新连接
		reConnect();
	}

	/**
	 * Publish Message Completion
	 */
	@Override
	public void deliveryComplete(MqttDeliveryToken arg0) {

	}

	/**
	 * Received Message from broker
	 */
	@Override
	public void messageArrived(MqttTopic topic, MqttMessage message)
			throws Exception {
		// Log.i(DEBUG_TAG, "  Topic messageArrived:\t" + topic.getName() +
		// "  Message:\t" + new String(message.getPayload()) + "  QoS:\t" +
		// message.getQos());
		Log.i(DEBUG_TAG, "  Topic messageArrived:\t" + topic.getName());
		Log.i(DEBUG_TAG, "  Message:\t" + new String(message.getPayload()));
		Log.i(DEBUG_TAG, "  QoS:\t" + message.getQos());
		if (message != null) {
			broadcastReceivedMessage(topic.getName(), message.getPayload());
		}
	}

	/**
	 * MqttConnectivityException Exception class
	 */
	private class MqttConnectivityException extends Exception {
		private static final long serialVersionUID = -7385866796799469420L;
	}

	/**
	 * 和服务器连接失败后，每隔多少分钟重新访问一下
	 */
	private long time = 1000 * 6;

	/**
	 * 是否允许重新连接
	 */
	// private boolean allowReConnect = false;

	/**
	 * 是否继续重新连接
	 */
	private boolean goOnReConnect = false;

	/**
	 * 
	 * @方法名：reConnect
	 * @功能说明：和服务器连接失败后，才执行本方法重新连接
	 * @author zhaotf
	 * @date 2014-2-27 下午5:14:31
	 */
	private void reConnect() {
		if (!isNetworkAvailable()) {
			// 如果没有网络，停止轮询重连接
			goOnReConnect = false;
			return;
		}
		Log.i(DEBUG_TAG, "开始准备循环重链接了 + mStarted = " + mStarted);
		mConnHandler.post(new Runnable() {
			@Override
			public void run() {
				goOnReConnect = true;
				Log.i(DEBUG_TAG, "重链接了 + mStarted = " + mStarted
						+ "  goOnReConnec=" + goOnReConnect);
				if (mClient == null) {
					Log.i(DEBUG_TAG, "mClient  null");
				}
				try {
					Thread.sleep(time);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					goOnReConnect = false;
					e.printStackTrace();
					Log.i(DEBUG_TAG, "重新（循环）连接异常，退出");
					return;
				}
				if (!isNetworkAvailable()) {
					goOnReConnect = false;
					return;
				}
				reconnectIfNecessary();
			}
		});
	}

	private void broadcastReceivedMessage(String topic, byte[] message) {
		// pass a message received from the MQTT server on to the Activity UI
		// (for times when it is running / active) so that it can be displayed
		// in the app GUI
		Log.i("MqttService", "broadcastReceivedMessage");
		Intent broadcastIntent = new Intent();
		broadcastIntent.setAction(MQTT_MSG_RECEIVED_INTENT);
		broadcastIntent.putExtra(MQTT_MSG_RECEIVED_TOPIC, topic);
		broadcastIntent.putExtra(MQTT_MSG_RECEIVED_MSG, message);
		sendBroadcast(broadcastIntent);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		Log.i(DEBUG_TAG, "onDestroy()");
		isOpen = false;
		super.onDestroy();
	}

}
