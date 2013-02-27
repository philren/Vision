package com.gpvision.utils;

import android.util.Log;

public class LogUtil {
	private static final boolean LOG = true;
	private static final String TAG = "GPVision";

	public static void logI(String message) {
		if (LOG)
			Log.i(TAG, message);
	}

	public static void logE(String message) {
		if (LOG)
			Log.e(TAG, message);
	}
}
