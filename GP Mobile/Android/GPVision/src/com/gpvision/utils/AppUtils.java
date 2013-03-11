package com.gpvision.utils;

import android.content.Context;
import android.widget.Toast;

public class AppUtils {
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
}
