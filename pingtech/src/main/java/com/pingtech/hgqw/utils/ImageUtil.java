package com.pingtech.hgqw.utils;

import java.io.File;
import java.io.FileOutputStream;

import org.kobjects.base64.Base64;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import com.pingtech.hgqw.base.BaseApplication;

public class ImageUtil {
	private static final String TAG = "ImageUtil";

	public static Intent photograph(String path, Context context) {

		Intent intent = new Intent();
		intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
		Uri fileUri = getOutputMediaFileUri(path); // create a file to save the
													// image
		intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
		return intent;

	}

	private static Uri getOutputMediaFileUri(String string) {

		return null;
	}

	private static File fileaa;
	private static byte[] byteImage;
	private static Bitmap bitmap;

	public static boolean saveImage(String zjhm, String image) {
		String path = BaseApplication.instent.getImagePath();
		fileaa = new File(path);
		if (!fileaa.exists()) {
			fileaa.mkdir();
		}
		byteImage = Base64.decode(image);
//		saveImageByByteArray(path + zjhm + ".png", false, byteImage);
		bitmap = ImageFactory.getBitmap(byteImage);
		saveBitmap(path + zjhm + ".png", bitmap, true);
		Log.i(TAG, "saveBitmap success ,zjhm:" + zjhm);
		return true;
	}

	private static void saveImageByByteArray(String path, boolean delHis,
			byte[] byteImage) {
		File file = new File(path);
		if (file.exists() && !delHis) {
			return;
		} else if (file.exists() && delHis) {
			file.delete();
		}
		try {
			FileOutputStream fileOutputStream = new FileOutputStream(file);
			int len = 0;
			while (len <= byteImage.length) {
				fileOutputStream.write(byteImage, len, 1024);
				len += 1024;
			}
			fileOutputStream.write(byteImage);
			fileOutputStream.flush();
			fileOutputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static FileOutputStream fileOutputStream;
	private static File file;

	public static void saveBitmap(String path, Bitmap bitmap, boolean delHis) {
		file = new File(path);
		if (file.exists() && !delHis) {
			return;
		} else if (file.exists() && delHis) {
			file.delete();
		}
		try {
			fileOutputStream = new FileOutputStream(file);
			bitmap.compress(Bitmap.CompressFormat.PNG, 90, fileOutputStream);
			fileOutputStream.flush();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (fileOutputStream != null) {
				try {
					fileOutputStream.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	private static Thread t = null;
	private static boolean runNext = false;

	public static void testSaveImageLooper() {
		if (t != null && t.isInterrupted()) {
			runNext = false;
			t.interrupt();
			t = null;
		}
		runNext = true;
		t = new Thread() {
			@Override
			public void run() {
				String path1 = null;
				if (Environment.getExternalStorageState().equals(
						Environment.MEDIA_MOUNTED)) {
					path1 = Environment.getExternalStorageDirectory().getPath();
				}
				if (path1 == null || "".equals(path1)) {
					return;
				}
				Bitmap bitmap = BitmapFactory.decodeFile(path1 + File.separator
						+ "1.png");
				for (int i = 5; i < 2000; i++) {
					if (!runNext) {
						return;
					}
					String path = null;
					if (Environment.getExternalStorageState().equals(
							Environment.MEDIA_MOUNTED)) {
						path = Environment.getExternalStorageDirectory()
								.getPath();
					}
					if (path == null || "".equals(path)) {
						return;
					}
					if (path.endsWith(File.separator)) {
						path = path + i + ".png";
					} else {
						path = path + File.separator + i + ".png";
					}
					Log.i(TAG, "path=" + path);
					saveBitmap(path, bitmap, true);
				}
			}
		};
		t.start();
	}
}
