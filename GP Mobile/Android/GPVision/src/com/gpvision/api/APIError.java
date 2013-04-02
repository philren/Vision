package com.gpvision.api;

public class APIError {
	// exports.PASSWORD_NOT_CORRECT =
	// "{ error: 'password is not correct' , errorCode:'001'}";
	// exports.USER_NOT_EXIST =
	// "{ error: 'user does not exist' , errorCode:'002'}";
	// exports.INTERNAL_ERROR =
	// "{ error: 'internal error occur!' , errorCode:'003'}";
	// exports.PERMISSION_ERROR =
	// "{ error: 'permission error' , errorCode:'004'}";
	// exports.ARGUMENT_MISTAKE =
	// "{ error: 'argument has mistake' , errorCode:'005'}";
	// exports.MEDIA_NOT_EXIST =
	// "{ error: 'media file does not exist' , errorCode:'006}";
	// exports.CRYPTO_ERROR = "{ error: 'crypto error' , errorCode:'007}";
	// exports.MONGO_DB_ERROR = "{ error: 'mongo db error' , errorCode:'008'}";
	// exports.USER_ALREADY_EXIST =
	// "{ error: 'user already existed' , errorCode:'009'}";

	public static final long UNKOOW_ERROR = 999;
	public static final long NETWORK_ERROR = 400;

	public static final long ERROR_PASS_NOT_CORRENT = 1l;
	public static final long ERROR_USER_NOT_EXIST = 2l;
	public static final long ERROR_INTERNAL_ERROR = 3l;
	public static final long ERROR_PERMISSION_ERROR = 4l;
	public static final long ERROR_ARGUMENT_MISTAKE = 5l;
	public static final long ERROR_MEDIA_NOT_EXIST = 6l;
	public static final long ERROR_CRYPTO_ERROR = 7l;
	public static final long ERROR_MONGO_DB_ERROR = 8l;
	public static final long ERROR_USER_EXISTED = 9l;

}
