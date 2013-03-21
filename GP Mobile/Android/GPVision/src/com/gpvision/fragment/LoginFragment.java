package com.gpvision.fragment;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.gpvision.R;
import com.gpvision.activity.MainActivity;
import com.gpvision.api.APIError;
import com.gpvision.api.APIResponseHandler;
import com.gpvision.api.request.GetAppTokenRequest;
import com.gpvision.api.request.GetUserTokenResquest;
import com.gpvision.api.response.GetAppTokenResponse;
import com.gpvision.api.response.GetUserTokenResponse;
import com.gpvision.datamodel.Account;
import com.gpvision.ui.dialog.ErrorDialog;
import com.gpvision.ui.dialog.LoginDialog;
import com.gpvision.utils.AppUtils;
import com.gpvision.utils.LocalDataBuffer;
import com.gpvision.utils.LogUtil;

public class LoginFragment extends BaseFragment {

	public static final String TAG = LoginFragment.class.getName();
	public static final String REG_TEXT = "^[A-Za-z0-9]+$";
	private EditText mUserName;
	private EditText mPassword;

	private Button mLogIn, mSignUp;
	private LoginDialog dialog;

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

			FragmentManager manager = getFragmentManager();
			SignUpFragment fragment = (SignUpFragment) manager
					.findFragmentByTag(SignUpFragment.TAG);
			if (fragment == null)
				fragment = new SignUpFragment();
			FragmentTransaction transaction = manager.beginTransaction();
			transaction.replace(R.id.login_activity_content, fragment,
					SignUpFragment.TAG);
			transaction.addToBackStack(null).commit();
			break;
		default:
			break;
		}
	}

	private void doLogIn() {
		String useerName = mUserName.getText().toString().trim();
		String password = mPassword.getText().toString().trim();

		Pattern pattern = Pattern.compile(REG_TEXT);
		// check user name
		if (AppUtils.isEmpty(useerName)) {
			new ErrorDialog(getActivity(), R.string.base_error_title,
					R.string.login_fragment_error_message_user_name_empty);
			return;
		}
		Matcher matcher = pattern.matcher(useerName);
		if (!matcher.matches()) {
			new ErrorDialog(
					getActivity(),
					R.string.base_error_title,
					R.string.login_fragment_error_message_user_name_sprcial_chars);
			return;
		}
		// check password
		if (AppUtils.isEmpty(password)) {
			new ErrorDialog(getActivity(), R.string.base_error_title,
					R.string.login_fragment_error_message_password_empty);
			return;
		}
		matcher = pattern.matcher(password);
		if (!matcher.matches()) {
			new ErrorDialog(
					getActivity(),
					R.string.base_error_title,
					R.string.login_fragment_error_message_password_sprcial_chars);
			return;
		}

		dialog = new LoginDialog(getActivity());
		dialog.show();
		Account account = LocalDataBuffer.getInstance().getAccount();
		if (account == null) {
			account = new Account();
			LocalDataBuffer.getInstance().setAccount(account);
		}
		getAppToken();
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
						if (errorCode == APIError.NETWORK_ERROR) {
							new ErrorDialog(getActivity(),
									R.string.base_error_title,
									R.string.api_error_network_error);
							return;
						}
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
						dialog.dismiss();
						if (errorCode == APIError.LOGIN_ERROR_PASS_NOT_CORRENT) {
							new ErrorDialog(
									getActivity(),
									R.string.base_error_title,
									R.string.api_error_login_error_pass_not_corrent);
							return;
						}
						if (errorCode == APIError.LOGIN_ERROR_USER_NOT_EXIST) {
							new ErrorDialog(
									getActivity(),
									R.string.base_error_title,
									R.string.api_error_login_error_user_not_exist);
							return;
						}
						if (errorCode == APIError.NETWORK_ERROR) {
							new ErrorDialog(getActivity(),
									R.string.base_error_title,
									R.string.api_error_network_error);
							return;
						}
					}
				});
	}

	private void logined() {
		Intent intent = new Intent();
		intent.setClass(getActivity(), MainActivity.class);
		startActivity(intent);
	}
}
