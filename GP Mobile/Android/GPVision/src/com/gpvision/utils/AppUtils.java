package com.gpvision.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.content.Context;
import android.widget.Toast;

public class AppUtils {
	private static char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7',
			'8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

	public static boolean isEmpty(String string) {
		return string == null || string.length() == 0;
	}

	public static void toastLong(Context context, String message) {
		Toast.makeText(context, message, Toast.LENGTH_LONG).show();
	}

	public static void toastShort(Context context, String message) {
		Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
	}

	public static void toastLong(Context context, int id) {
		Toast.makeText(context, id, Toast.LENGTH_LONG).show();
	}

	public static void toastShort(Context context, int id) {
		Toast.makeText(context, id, Toast.LENGTH_SHORT).show();
	}

	public static String precentFormat(float x) {
		return String.format("%.02f", x) + "%";
	}

	public static String getMd5(String absolutePath) {
		File file = new File(absolutePath);
		if (!file.exists())
			return null;
		FileInputStream accessFile = null;
		String md5 = null;
		try {
			accessFile = new FileInputStream(file);
			MessageDigest digest = MessageDigest.getInstance("MD5");
			byte[] bytes = new byte[1024];
			int count = 0;
			int len = 0;
			int length = 1024 * 20;
			while ((len = accessFile.read(bytes)) != -1) {
				count = +len;
				if (count < length) {
					digest.update(bytes, 0, len);
				} else {
					digest.update(bytes, 0, 1024 - (count - length));
					break;
				}
			}
			byte[] b = digest.digest();
			md5 = bufferToHex(b, 0, b.length);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} finally {
			if (accessFile != null)
				try {
					accessFile.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		return md5;
	}

	private static String bufferToHex(byte bytes[], int m, int n) {
		StringBuffer stringbuffer = new StringBuffer(2 * n);
		int k = m + n;
		for (int l = m; l < k; l++) {
			appendHexPair(bytes[l], stringbuffer);
		}
		return stringbuffer.toString();
	}

	private static void appendHexPair(byte bt, StringBuffer stringbuffer) {
		char c0 = hexDigits[(bt & 0xf0) >> 4];
		char c1 = hexDigits[bt & 0xf];
		stringbuffer.append(c0);
		stringbuffer.append(c1);
	}
}
