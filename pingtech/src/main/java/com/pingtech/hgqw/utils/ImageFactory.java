package com.pingtech.hgqw.utils;

import java.io.File;
import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.widget.ImageView;

import com.pingtech.hgqw.base.BaseApplication;

import edu.mit.mobile.android.imagecache.ImageCache;

public class ImageFactory {
	private static final String TAG = "ImageFactory";

	private static ImageCache mCache;

	public static Bitmap getBitmap(byte[] image) {
		if (image == null) {
			return null;
		}
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inJustDecodeBounds = true;
		Bitmap netWorkImage = BitmapFactory.decodeByteArray(image, 0, image.length, opts);
		int height_be = opts.outHeight / 130;
		int width_be = opts.outWidth / 105;
		opts.inSampleSize = height_be > width_be ? height_be : width_be;
		if (opts.inSampleSize <= 0) {
			opts.inSampleSize = 1;
		}

		// Log.i(TAG, "decodeByteArray:" + opts.outHeight + "," + opts.outWidth
		// + "," + opts.inSampleSize);
		opts.inJustDecodeBounds = false;
		netWorkImage = BitmapFactory.decodeByteArray(image, 0, image.length, opts);

		// Log.i(TAG, "decodeByteArray:" + opts.outHeight + "," +
		// opts.outWidth);

		return netWorkImage;
	}

	public static Bitmap loadImage(String zjhm) {

		String imagePath = BaseApplication.instent.getImagePath() + zjhm + ".png";
		File file = new File(imagePath);
		if (!file.exists() || !file.isFile()) {
			return null;
		}

		Bitmap netWorkImage = BitmapFactory.decodeFile(imagePath);

		return netWorkImage;
	}
	public static Bitmap loadImageByPath(String imagePath) {
		
		File file = new File(imagePath);
		if (!file.exists() || !file.isFile()) {
			return null;
		}
		
		Bitmap netWorkImage = BitmapFactory.decodeFile(imagePath);
		
		return netWorkImage;
	}

	public static Drawable loadDrawable(String zjhm) {
		mCache = ImageCache.getInstance(BaseApplication.instent);
		String imagePath = BaseApplication.instent.getImagePath() + zjhm + ".png";
		Drawable drawable = null;
		try {
			drawable = mCache.loadImage(0, Uri.parse("file:" + imagePath), 105 * 2, 130 * 2);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

		return drawable;
	}
	public static Drawable loadDrawableByPath(String imagePath) {
		mCache = ImageCache.getInstance(BaseApplication.instent);
		Drawable drawable = null;
		try {
			drawable = mCache.loadImage(0, Uri.parse("file:" + imagePath), 105 * 2, 130 * 2);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		
		return drawable;
	}

	public static void setImage(ImageView imageView, Bitmap netWorkImage) {
		if (netWorkImage != null) {
			setBitmapBySize(imageView, netWorkImage);
			/*
			 * LayoutParams para; para = imageView.getLayoutParams(); int
			 * height_be = netWorkImage.getHeight() / 130; int width_be =
			 * netWorkImage.getWidth() / 105; if (height_be > width_be) {
			 * para.height = 130 * 2; para.width = 130 * 2 *
			 * netWorkImage.getWidth() / netWorkImage.getHeight(); } else {
			 * para.width = 105 * 2; para.height = 105 * 2 *
			 * netWorkImage.getHeight() / netWorkImage.getWidth(); }
			 * imageView.setLayoutParams(para);
			 * imageView.setImageBitmap(netWorkImage);
			 */
		}
	}
	public static void setDrawable(ImageView imageView, Drawable netWorkImage) {
		if (netWorkImage != null) {
			imageView.setImageDrawable(netWorkImage);
		}
	}

	public static void setBitmapBySize(ImageView imageView, Bitmap netWorkImage) {
		int w = netWorkImage.getWidth();
		int h = netWorkImage.getHeight();
		if (w <= 0 || h <= 0) {
			Log.i(TAG, "w <= 0 || h <= 0");
			return;
		}
		float scalW = (float) 105 * 2 / (float) w;
		float scalH = (float) 130 * 2 / (float) h;
		if (scalW <= 0 || scalH <= 0) {
			Log.i(TAG, "scalW <= 0 || scalH <= 0");
			return;
		}
		Matrix matrix = new Matrix();
		matrix.postScale(scalW, scalH);
		Bitmap temp = Bitmap.createBitmap(netWorkImage, 0, 0, w, h, matrix, true);
		imageView.setImageBitmap(temp);
	}
}
