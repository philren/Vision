package com.gpvision.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory.Options;
import android.graphics.BitmapFactory;
import android.os.Environment;

public class ImageCacheUtil {
	public static final String CACHE_DIR = Environment
			.getExternalStorageDirectory()
			+ File.separator
			+ "GPVision"
			+ File.separator + "Cache";
	public static final String SAVE_DIR = Environment
			.getExternalStorageDirectory()
			+ File.separator
			+ "GPVision"
			+ File.separator + "Saved";

	public static boolean getStorangeState() {
		return Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED);
	}

	public static boolean isFileExists(String fileName) {
		File file = new File(CACHE_DIR + File.separator + fileName);
		return file.exists();
	}

	public static void write2SDCard(String fileName, InputStream inputStream) {
		File file = new File(CACHE_DIR + File.separator + fileName);
		if (!file.getParentFile().exists()) {
			file.getParentFile().mkdirs();
		}
		Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
		if (bitmap == null)
			return;
		FileOutputStream outputStream = null;
		try {
			outputStream = new FileOutputStream(file);
			bitmap.compress(CompressFormat.JPEG, 100, outputStream);
			outputStream.flush();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			bitmap.recycle();
			try {
				inputStream.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			if (outputStream != null)
				try {
					outputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}

	public static Bitmap getBitmapFromFile(String fileName, int maxWidth,
			int maxHeight) {
		Bitmap bitmap = null;
		String imageFilePath = CACHE_DIR + File.separator + fileName;
		int width, height;
		int simpleSize = 1;
		Options options = new Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(imageFilePath, options);
		width = options.outWidth;
		height = options.outHeight;
		simpleSize = width / maxWidth > height / maxHeight ? width / maxWidth
				+ 1 : height / maxHeight + 1;
		options.inJustDecodeBounds = false;
		options.inSampleSize = simpleSize;
		bitmap = BitmapFactory.decodeFile(imageFilePath, options);

		return bitmap;
	}

	public static String getFileNameFromUrl(String url) {
		return url.substring(url.lastIndexOf("=") + 1);
	}

	public static void saveTo(String fileName, String toPath) {
		File originalFile = new File(CACHE_DIR + File.separator + fileName);
		File toFile = new File(toPath);
		if (originalFile.exists()) {
			if (!toFile.getParentFile().exists()) {
				toFile.getParentFile().mkdirs();
			}
			FileInputStream inputStream = null;
			FileOutputStream outputStream = null;
			try {
				inputStream = new FileInputStream(originalFile);
				outputStream = new FileOutputStream(toFile);
				byte[] bytes = new byte[4 * 1024];
				int len = 0;
				while ((len = inputStream.read(bytes)) != -1) {
					outputStream.write(bytes, 0, len);
				}
				outputStream.flush();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (inputStream != null) {
					try {
						inputStream.close();
					} catch (IOException e) {
					}
				}
				if (outputStream != null) {
					try {
						outputStream.close();
					} catch (IOException e) {
					}
				}
			}

		}
	}

	public static class ImageSize {
		public static final ImageSize GALLERY = new ImageSize(50, 50);
		public static final ImageSize PIC = new ImageSize(200, 200);
		int width;
		int height;

		private ImageSize(int width, int height) {
			super();
			this.width = width;
			this.height = height;
		}

	}
}
