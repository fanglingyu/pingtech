package com.pingtech.hgqw.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pingtech.R;
import com.pingtech.hgqw.utils.StringUtils;

public class CustomTextView extends LinearLayout {
	private static final String HORIZONTAL = "horizontal";

	private static final String VERTICAL = "vertical";

	private LinearLayout linearLayout = null;

	private String orientation = HORIZONTAL;

	private TextView vertical = null;

	private TextView title = null;

	private TextView text = null;

	private String titleStr = null;

	private int titleColor = Color.BLACK;

	private float titleSize = 16;

	private String textStr = null;

	private int textColor = Color.BLACK;

	private float textSize = 16;

	public CustomTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		View view = LayoutInflater.from(context).inflate(R.layout.custom_textview_title_text, this);
		linearLayout = (LinearLayout) view.findViewById(R.id.layout);
		title = (TextView) view.findViewById(R.id.title);
		text = (TextView) view.findViewById(R.id.text);

		TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.custom_textview_title_text);
		orientation = typedArray.getString(R.styleable.custom_textview_title_text_orientation);
		if (StringUtils.isNotEmpty(orientation) || VERTICAL.equals(orientation)) {
			linearLayout.setOrientation(LinearLayout.VERTICAL);
		}

		titleStr = typedArray.getString(R.styleable.custom_textview_title_text_title);
		titleColor = typedArray.getColor(R.styleable.custom_textview_title_text_titleColor, Color.BLACK);
		titleSize = typedArray.getInteger(R.styleable.custom_textview_title_text_titleSize, 16);


		title.setText(titleStr);
		title.setTextColor(titleColor);
		title.setTextSize(titleSize);

		textStr = typedArray.getString(R.styleable.custom_textview_title_text_text);
		textColor = typedArray.getColor(R.styleable.custom_textview_title_text_textColor, Color.BLACK);
		textSize = typedArray.getInteger(R.styleable.custom_textview_title_text_textSize, 16);

		text.setText(textStr);
		text.setTextColor(textColor);
		text.setTextSize(textSize);
		typedArray.recycle();
	}

	public void setTitle(String title) {
		this.title.setText(title);
	}

	public void setTitleSize(int size) {
		this.title.setTextSize(size);
	}

	public void setTitleColor(int color) {
		this.title.setTextSize(color);
	}

	public void setText(String title) {
		this.text.setText(title);
	}

	public void setTextSize(int size) {
		this.text.setTextSize(size);
	}

	public void setTextColor(int color) {
		this.text.setTextSize(color);
	}
}
