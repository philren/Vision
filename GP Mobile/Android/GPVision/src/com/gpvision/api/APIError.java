package com.gpvision.api;

import android.util.Pair;

public class APIError {

	public static final Pair<Long, String> UNKOOW_ERROR = new Pair<Long, String>(
			999l, "unknow error");
	public static final Pair<Long, String> NETWORK_ERROR = new Pair<Long, String>(
			400l, "net work error");

	// login errors
	public static final Pair<Long, String> LOGIN_ERROR_PASS_NOT_CORRENT = new Pair<Long, String>(
			001l, "password id not corrent");
	public static final Pair<Long, String> LOGIN_ERROR_USER_NOT_EXIST = new Pair<Long, String>(
			002l, "user does not exist");
}
