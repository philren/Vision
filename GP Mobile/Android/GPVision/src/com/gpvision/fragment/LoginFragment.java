package com.gpvision.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.gpvision.R;
import com.gpvision.activity.MainActivity;
import com.gpvision.api.APIResponseHandler;
import com.gpvision.api.request.GetAppTokenRequest;
import com.gpvision.api.request.GetUserTokenResquest;
import com.gpvision.api.response.GetAppTokenResponse;
import com.gpvision.api.response.GetUserTokenResponse;
import com.gpvision.datamodel.Account;
import com.gpvision.ui.LoadingDialog;
import com.gpvision.utils.AppUtils;
import com.gpvision.utils.LocalDataBuffer;
import com.gpvision.utils.LogUtil;

public class LoginFragment extends BaseFragment {

	private EditText mUserName;
	private EditText mPassword;

	private Button mLogIn, mSignUp;
	private LoadingDialog dialog;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_login, container, false);
		mUserName = (EditText) view.findViewById(R.id.login_fragment_user_name);
		mPassword = (EditText) view.findViewById(R.id.login_fragment_password);
		mLogIn = (Button) view.findViewById(R.id.login_fragment_log_in);
		mSignUp = (Button) view.findViewById(R.id.login_fragment_sign_up);
		mLogIn.setOnClickListener(this);
		mSignUp.setOnClickListener(this);

		// test only
		mUserName.setText("mobile001");
		mPassword.setText("123456");
		return view;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.login_fragment_log_in:
			doLogIn();
			break;
		case R.id.login_fragment_sign_up:
			FragmentTransaction transaction = getFragmentManager()
					.beginTransaction();
			SignUpFragment fragment = new SignUpFragment();
			transaction.replace(R.id.login_activity_content, fragment);
			transaction.addToBackStack(null).commit();
			break;
		default:
			break;
		}
	}

	private void doLogIn() {
		String email = mUserName.getText().toString().trim();
		String password = mPassword.getText().toString().trim();
		// check email
		if (AppUtils.isEmpty(email)) {
			AppUtils.toastLong(getActivity(),
					R.string.login_fragment_user_name_null_toast);
			return;
		}
		// check password
		if (AppUtils.isEmpty(password)) {
			AppUtils.toastLong(getActivity(),
					R.string.login_fragment_password_null_toast);
			return;
		}

		dialog = new LoadingDialog(getActivity());
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
						dialog.dismiss();
					}
				});
	}

	private void getUserToken() {
		final String userName = mUserName.getText().toString().trim();
		String password = mPassword.getText().toString().trim();
		new GetUserTokenResquest(userName, password)
				.start(new APIResponseHandler<GetUserTokenResponse>() {

					@Override
					public void handleResponse(GetUserTokenResponse response) {
						Account account = LocalDataBuffer.getInstance()
								.getAccount();
						account.setAccount(userName);
						account.setUserToken(response.getUserToken());
						dialog.dismiss();
						logined();
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
		intent.setClass(getActivity(), MainActivity.class);
		startActivity(intent);
	}
}
