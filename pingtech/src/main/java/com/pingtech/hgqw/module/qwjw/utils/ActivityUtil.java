package com.pingtech.hgqw.module.qwjw.utils;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.ActionBar.LayoutParams;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.pingtech.R;
import com.pingtech.hgqw.module.qwjw.listener.ActionBarTabListener;
import com.pingtech.hgqw.utils.StringUtils;

public class ActivityUtil {
	/** 处理每个界面标题上个级别之间的>分隔符 */
	public static void setMyActiveTitle(View view, String title) {
		if (StringUtils.isEmpty(title)) {
			return;
		}
		String subtitle[] = title.split(">");
		if (subtitle.length > 0 && subtitle[0] != null) {
			((TextView) view.findViewById(R.id.current_dialog_title)).setText(subtitle[0]);
		}
		if (subtitle.length > 1 && subtitle[1] != null) {
			view.findViewById(R.id.title_sep1).setVisibility(View.VISIBLE);
			view.findViewById(R.id.current_dialog_title2).setVisibility(View.VISIBLE);
			((TextView) view.findViewById(R.id.current_dialog_title2)).setText(subtitle[1]);
		} else {
			if (view.findViewById(R.id.title_sep1) != null) {
				view.findViewById(R.id.title_sep1).setVisibility(View.GONE);
				view.findViewById(R.id.current_dialog_title2).setVisibility(View.GONE);
			}
		}
		if (subtitle.length > 2 && subtitle[2] != null) {
			view.findViewById(R.id.title_sep2).setVisibility(View.VISIBLE);
			view.findViewById(R.id.current_dialog_title3).setVisibility(View.VISIBLE);
			((TextView) view.findViewById(R.id.current_dialog_title3)).setText(subtitle[2]);
		} else {
			if (view.findViewById(R.id.title_sep2) != null) {
				view.findViewById(R.id.title_sep2).setVisibility(View.GONE);
				view.findViewById(R.id.current_dialog_title3).setVisibility(View.GONE);
			}
		}
	}

	public static void initCustomActionBar(Activity activity, String title) {
		ActionBar actionBar = activity.getActionBar();

		actionBar.setDisplayShowCustomEnabled(true);

		// 设置默认标题栏是否可见
		actionBar.setDisplayShowHomeEnabled(false);

		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);

		View view = LayoutInflater.from(activity).inflate(R.layout.custom_actionbar, null);
		ActionBar.LayoutParams layoutParams = new ActionBar.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

		ActivityUtil.setMyActiveTitle(view, title);
		actionBar.setCustomView(view, layoutParams);
	}

	@SuppressLint("NewApi")
	public static void openTab(Activity activity) {
		ActionBar actionBar = activity.getActionBar();
		// 设置导航模式为Tab选项标签导航模式
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// 设置ActionBar标题不显示
		actionBar.setDisplayShowTitleEnabled(false);

		// 设置ActionBar左边默认的图标是否可用
		actionBar.setDisplayUseLogoEnabled(false);

		// 设置默认标题栏是否可见
		actionBar.setDisplayShowHomeEnabled(true);

		actionBar.setHomeButtonEnabled(false);

		actionBar.setDisplayHomeAsUpEnabled(false);
		actionBar.setBackgroundDrawable(activity.getResources().getDrawable(android.R.color.transparent));
		initTab(actionBar, activity);
	}

	private static void initTab(ActionBar actionBar, Activity activity) {
		for (int i = 0; i < 3; i++) {
			Tab tab = actionBar.newTab();
			tab.setText("TAB" + i);
			tab.setTabListener(new ActionBarTabListener());
			actionBar.addTab(tab);
		}
	}

	public static void openTabBak(Activity context) {
		ActionBar actionBar = context.getActionBar();

		// 设置ActionBar标题不显示
		actionBar.setDisplayShowTitleEnabled(true);

		// 设置ActionBar的背景
		actionBar.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.main_header_bg_v));

		// 设置ActionBar左边默认的图标是否可用
		actionBar.setDisplayUseLogoEnabled(false);

		// 设置默认标题栏是否可见
		actionBar.setDisplayShowHomeEnabled(false);

		// 设置导航模式为Tab选项标签导航模式
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);

		// 设置ActinBar添加Tab选项标签
		actionBar.addTab(actionBar.newTab().setText("TAB1").setTabListener(new ActionBarTabListener()));
		actionBar.addTab(actionBar.newTab().setText("TAB2").setTabListener(new ActionBarTabListener()));
		actionBar.addTab(actionBar.newTab().setText("TAB3").setTabListener(new ActionBarTabListener()));

	}

}
