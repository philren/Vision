package com.gpvision.activity;

import com.gpvision.R;
import com.gpvision.api.APIResponseHandler;
import com.gpvision.api.request.GetAppTokenRequest;
import com.gpvision.api.response.GetAppTokenResponse;
import com.gpvision.utils.AppUtils;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class LoginActivity extends BaseActivity {

	private EditText mEmail;
	private EditText mPassword;

	private Button mLogIn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_login);

		mEmail = (EditText) findViewById(R.id.login_activity_email);
		mPassword = (EditText) findViewById(R.id.login_activity_password);
		mLogIn = (Button) findViewById(R.id.login_activity_log_in);

		mLogIn.setOnClickListener(this);
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

		// TODO call API
		new GetAppTokenRequest()
				.start(new APIResponseHandler<GetAppTokenResponse>() {

					@Override
					public void handleResponse(GetAppTokenResponse response) {

					}

					@Override
					public void handleError(Long errorCode, String errorMessage) {

					}
				});
		Intent intent = new Intent();
		intent.setClass(LoginActivity.this, MainActivity.class);
		startActivity(intent);
	}
}
