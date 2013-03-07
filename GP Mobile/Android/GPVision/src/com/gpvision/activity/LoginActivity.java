package com.gpvision.activity;

import com.gpvision.R;
import com.gpvision.api.APIResponseHandler;
import com.gpvision.api.request.GetAppTokenRequest;
import com.gpvision.api.request.GetUserTokenResquest;
import com.gpvision.api.response.GetAppTokenResponse;
import com.gpvision.api.response.GetUserTokenResponse;
import com.gpvision.datamodel.Account;
import com.gpvision.ui.LoadingDialog;
import com.gpvision.ui.LocalDataBuffer;
import com.gpvision.utils.AppUtils;
import com.gpvision.utils.LogUtil;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class LoginActivity extends BaseActivity {

	private EditText mEmail;
	private EditText mPassword;

	private Button mLogIn;
	private LoadingDialog dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_login);

		mEmail = (EditText) findViewById(R.id.login_activity_email);
		mPassword = (EditText) findViewById(R.id.login_activity_password);
		mLogIn = (Button) findViewById(R.id.login_activity_log_in);

		mLogIn.setOnClickListener(this);

		// test only
		mEmail.setText("mobile001");
		mPassword.setText("123456");
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.login_activity_log_in:
			doLogIn();
			break;

		default:
			break;
		}
	}

	private void doLogIn() {
		String email = mEmail.getText().toString().trim();
		String password = mPassword.getText().toString().trim();
		// check email
		if (AppUtils.isEmpty(email)) {
			AppUtils.toastLong(LoginActivity.this,
					R.string.login_activity_email_null_toast);
			return;
		}
		// check password
		if (AppUtils.isEmpty(password)) {
			AppUtils.toastLong(LoginActivity.this,
					R.string.login_activity_password_null_toast);
			return;
		}

		dialog = new LoadingDialog(LoginActivity.this);
		dialog.show();
		Account account = LocalDataBuffer.getInstance().getAccount();
		if (account == null) {
			account = new Account();
			LocalDataBuffer.getInstance().setAccount(account);
		}
		if (account.getAppToken() == null) {
			getAppToken();
		} else {
			if (account.getUserToken() == null) {
				getUserToken();
			} else {
				logined();
			}
		}

	}

	private void getAppToken() {
		new GetAppTokenRequest()
				.start(new APIResponseHandler<GetAppTokenResponse>() {

					@Override
					public void handleResponse(GetAppTokenResponse response) {
						Account account = LocalDataBuffer.getInstance()
								.getAccount();
						account.setAppToken(response.getAppToken());
						getUserToken();
					}

					@Override
					public void handleError(Long errorCode, String errorMessage) {
						LogUtil.logE(errorMessage);
					}
				});
	}

	private void getUserToken() {
		final String userName = mEmail.getText().toString().trim();
		String password = mPassword.getText().toString().trim();
		new GetUserTokenResquest(userName, password)
				.start(new APIResponseHandler<GetUserTokenResponse>() {

					@Override
					public void handleResponse(GetUserTokenResponse response) {
						Account account = LocalDataBuffer.getInstance()
								.getAccount();
						account.setAccount(userName);
						account.setUserToken(response.getUserToken());
						logined();
						dialog.dismiss();
					}

					@Override
					public void handleError(Long errorCode, String errorMessage) {
						LogUtil.logE(errorMessage);
						dialog.dismiss();
					}
				});
	}

	private void logined() {
		Intent intent = new Intent();
		intent.setClass(LoginActivity.this, MainActivity.class);
		startActivity(intent);
	}
}
