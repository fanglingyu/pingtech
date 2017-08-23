package com.pingtech.hgqw.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.TextView;

public class HgqwTextView extends TextView implements Runnable {
	private int currentScrollX;// 当前滚动的位置

	private boolean isStop = false;

	private int textWidth;

	private boolean isMeasure = false;

	public HgqwTextView(Context context) {
		super(context);
	}

	public HgqwTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public HgqwTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	private void getTextWidth() {
		Paint paint = this.getPaint();
		String str = this.getText().toString();
		textWidth = (int) paint.measureText(str);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (!isMeasure) {// 文字宽度只需获取一次就可以了
			getTextWidth();
			isMeasure = true;
		}
	}

	// 重写setText 在setText的时候重新计算text的宽度
	@Override
	public void setText(CharSequence text, BufferType type) {
		// TODO Auto-generated method stub
		super.setText(text, type);
		this.isMeasure = false;
	}

	@Override
	public void run() {
		currentScrollX -= 2;// 滚动速度
		scrollTo(currentScrollX, 0);
		if (isStop) {
			return;
		}
		if (getScrollX() <= -(this.getWidth())) {
			scrollTo(textWidth, 0);
			currentScrollX = textWidth;
			// return;
		}
		postDelayed(this, 5);
	}

	// 开始滚动
	public void startScroll() {
		isStop = false;
		this.removeCallbacks(this);
		post(this);
	}

	// 停止滚动
	public void stopScroll() {
		isStop = true;
	}

	// 从头开始滚动
	public void startFor0() {
		currentScrollX = 0;
		startScroll();
	}

	
	public void setClick(boolean isClick){
		// 如果正在滚动则停止
		if (isClick) {
			this.stopScroll();
		} else {
			this.startScroll();
		}
		
	}
}
