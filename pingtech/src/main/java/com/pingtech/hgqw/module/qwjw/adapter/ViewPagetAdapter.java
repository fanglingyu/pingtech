package com.pingtech.hgqw.module.qwjw.adapter;

import java.util.ArrayList;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.View;

import com.pingtech.hgqw.module.qwjw.utils.FragmentTag;

public class ViewPagetAdapter extends PagerAdapter {
	private static final String TAG = "ViewPagetAdapter";

	private FragmentManager fragmentManager;

	private FragmentTransaction mCurTransaction = null;

	private Fragment mCurrentPrimaryItem;

	private ArrayList<Fragment> data = new ArrayList<Fragment>();

	public ViewPagetAdapter() {
	}

	public ViewPagetAdapter(FragmentManager fragmentManager, ArrayList<Fragment> data ) {
		this.fragmentManager = fragmentManager;
		this.data = data;
	}

	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public int getItemPosition(Object object) {
		Log.i(TAG, "getItemPosition");
		return POSITION_NONE;
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return "";
	}

	@Override
	public void startUpdate(View container) {
		Log.i(TAG, "startUpdate");
	}

	private Fragment getFragment(int position) {
		return data.get(position);
	}

	

	@Override
	public Object instantiateItem(View container, int position) {
		if (mCurTransaction == null) {
			mCurTransaction = fragmentManager.beginTransaction();
		}
		Fragment f = fragmentManager.findFragmentByTag(FragmentTag.TAG_1);
		// Fragment f = getFragment(position);
		/*Fragment f = null;
		switch (position) {
		case 0:
			f = fragmentManager.findFragmentByTag(FragmentTag.TAG_0);
			break;
		case 1:
			f = fragmentManager.findFragmentByTag(FragmentTag.TAG_1);
			break;
		default:
			break;
		}*/
//		mCurTransaction.show(f);
		Log.i(TAG, "instantiateItem,position=" + position);
		return f;
	}

	@Override
	public void destroyItem(View container, int position, Object object) {
		if (mCurTransaction == null) {
			mCurTransaction = fragmentManager.beginTransaction();
		}
		mCurTransaction.hide((Fragment) object);
	}

	@Override
	public void finishUpdate(View container) {
		Log.i(TAG, "finishUpdate");
		if (mCurTransaction != null) {
			mCurTransaction.commitAllowingStateLoss();
			mCurTransaction = null;
			fragmentManager.executePendingTransactions();
		}
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return ((Fragment) object).getView() == view;
	}

	@Override
	public void setPrimaryItem(View container, int position, Object object) {
		Log.i(TAG, "setPrimaryItem");
		Fragment fragment = (Fragment) object;
		if (mCurrentPrimaryItem != fragment) {
			if (mCurrentPrimaryItem != null) {
			}
			if (fragment != null) {
			}
			mCurrentPrimaryItem = fragment;
		}
	}

	@Override
	public Parcelable saveState() {
		Log.i(TAG, "saveState");
		return null;
	}

	@Override
	public void restoreState(Parcelable state, ClassLoader loader) {
	}

}
