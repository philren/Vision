package com.gpvision.utils;

import com.gpvision.R;
import com.gpvision.api.APIError;
import com.gpvision.ui.dialog.ErrorDialog;

import android.content.Context;

public class ApiErrorHandler {

	public static void handler(Context context, long errorCode) {

		if (errorCode == APIError.NETWORK_ERROR) {
			new ErrorDialog(context, R.string.base_error_title,
					R.string.api_error_network_error);
			return;
		}

		if (errorCode == APIError.ERROR_ARGUMENT_MISTAKE) {
			new ErrorDialog(context, R.string.base_error_title,
					R.string.api_error_argument_has_mistake);
			return;
		}

		if (errorCode == APIError.ERROR_CRYPTO_ERROR) {
			new ErrorDialog(context, R.string.base_error_title,
					R.string.api_error_crypto_error);
			return;
		}

		if (errorCode == APIError.ERROR_INTERNAL_ERROR) {
			new ErrorDialog(context, R.string.base_error_title,
					R.string.api_error_internal_error_occur);
			return;
		}

		if (errorCode == APIError.ERROR_MEDIA_NOT_EXIST) {
			new ErrorDialog(context, R.string.base_error_title,
					R.string.api_error_media_file_does_not_exist);
			return;
		}

		if (errorCode == APIError.ERROR_MONGO_DB_ERROR) {
			new ErrorDialog(context, R.string.base_error_title,
					R.string.api_error_mongo_db_error);
			return;
		}

		if (errorCode == APIError.ERROR_PASS_NOT_CORRENT) {
			new ErrorDialog(context, R.string.base_error_title,
					R.string.api_error_pass_not_corrent);
			return;
		}

		if (errorCode == APIError.ERROR_PERMISSION_ERROR) {
			new ErrorDialog(context, R.string.base_error_title,
					R.string.api_error_permission_error);
			return;
		}

		if (errorCode == APIError.ERROR_USER_EXISTED) {
			new ErrorDialog(context, R.string.base_error_title,
					R.string.api_error_user_existed);
			return;
		}

		if (errorCode == APIError.ERROR_USER_NOT_EXIST) {
			new ErrorDialog(context, R.string.base_error_title,
					R.string.api_error_user_not_exist);
			return;
		}
	}
}
