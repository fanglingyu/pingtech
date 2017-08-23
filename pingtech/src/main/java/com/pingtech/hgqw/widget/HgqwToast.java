package com.pingtech.hgqw.widget;

import android.content.Context;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.pingtech.R;
import com.pingtech.hgqw.base.BaseApplication;

/**
 * @title ToastWidget
 * @description Toast控件
 * @author zuolong
 * @date 2013-6-27
 * @version V1.0
 */
public class HgqwToast extends Toast {
	/**
	 * toast实体
	 */
	private static HgqwToast toastWidget;

	/**
	 * 提示文字
	 */
	private static TextView toastText;

	/**
	 * 当前显示位置
	 */
	private static int currentGravity;

	private static Handler handler = new Handler();

	/**
	 * 解决连续点击多次显示很长时间的问题
	 */
	static Runnable r = new Runnable() {

		@Override
		public void run() {
			toastWidget.cancel();
		}
	};

	public HgqwToast(Context context) {
		super(context);
	}

	@Override
	public void show() {
		handler.postDelayed(r, 2000);
		super.show();
	}

	/**
	 * 获取控件实例
	 * 
	 * @param context
	 * @param text
	 *            提示消息
	 * @return
	 */
	public static HgqwToast makeText(Context context, CharSequence text) {
		return makeText(context, text, Toast.LENGTH_SHORT);
	}

	/**
	 * 获取控件实例
	 * 
	 * @param context
	 * @param resId
	 *            提示消息id
	 * @return
	 */
	public static HgqwToast makeText(Context context, int resId) {
		return makeText(context, context.getResources().getText(resId), Toast.LENGTH_SHORT);
	}

	public static HgqwToast getToastView(Context context, String text) {
		return makeText(context, text, Toast.LENGTH_SHORT);
	}

	public static void toast(Context context, String text, int showTime) {
		makeText(context, text, showTime).show();
	}

	public static void toast(String text, int showTime) {
		makeText(BaseApplication.instent, text, showTime).show();
	}

	public static void toast(String text) {
		makeText(BaseApplication.instent, text).show();
	}

	public static void toast(int resId, int showTime) {
		makeText(BaseApplication.instent, BaseApplication.instent.getText(resId), showTime).show();
	}

	public static void toast(int resId) {
		toast(resId, HgqwToast.LENGTH_LONG);
	}

	public static HgqwToast makeText(Context context, int resId, int showTime) {
		return makeText(context, context.getText(resId), showTime);
	}

	/**
	 * 获取控件实例
	 * 
	 * @param gravity
	 *            显示位置
	 * @param context
	 * @param text
	 *            提示消息
	 * @return
	 */
	public static HgqwToast makeText(int gravity, Context context, CharSequence text) {
		return makeText(gravity, context, text, Toast.LENGTH_SHORT);
	}

	/**
	 * 获取控件实例
	 * 
	 * @param gravity
	 *            显示位置
	 * @param context
	 * @param resId
	 *            提示消息id
	 * 
	 * @return
	 */
	public static HgqwToast makeText(int gravity, Context context, int resId) {
		return makeText(gravity, context, context.getResources().getText(resId), Toast.LENGTH_SHORT);
	}

	/**
	 * 获取控件实例
	 * 
	 * @param context
	 * @param text
	 *            提示消息
	 * @param duration
	 *            显示周期
	 * @return
	 */
	public static HgqwToast makeText(Context context, CharSequence text, int duration) {
		handler.removeCallbacks(r);
		if (null != toastWidget && Gravity.TOP == currentGravity) {
			toastText.setText(text);
		} else {
			toastWidget = customMakeText(Gravity.TOP, context, text, duration);
		}
		return toastWidget;
	}

	/**
	 * 获取控件实例
	 * 
	 * @param gravity
	 *            显示位置
	 * @param context
	 * @param text
	 *            提示消息
	 * @param duration
	 *            显示周期
	 * 
	 * @return
	 */
	public static HgqwToast makeText(int gravity, Context context, CharSequence text, int duration) {
		handler.removeCallbacks(r);
		if (null != toastWidget && gravity == currentGravity) {
			toastText.setText(text);
		} else {
			toastWidget = customMakeText(gravity, context, text, duration);
		}
		return toastWidget;
	}

	/**
	 * 获取控件实例
	 * 
	 * @param gravity
	 *            显示位置
	 * @param context
	 * @param text
	 *            提示消息
	 * @param duration
	 *            显示周期
	 * @return
	 */
	private static HgqwToast customMakeText(int gravity, Context context, CharSequence text, int duration) {
		currentGravity = gravity;
		HgqwToast result = new HgqwToast(context);

		LayoutInflater inflate = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		DisplayMetrics dm = context.getResources().getDisplayMetrics();

		View v = inflate.inflate(R.layout.base_toast, null);
		// // 设置控件最小宽度为手机屏幕宽度
		// v.setMinimumWidth(dm.widthPixels);

		toastText = (TextView) v.findViewById(R.id.base_toast_text);
		toastText.setText(text);
		result.setView(v);
		result.setDuration(duration);
		// result.setGravity(gravity, 0, (int) (dm.heightPixels / 6));
		result.setGravity(Gravity.CENTER, 0, 0);
		result.setGravity(Gravity.BOTTOM, 0, 100);
		return result;
	}
}
