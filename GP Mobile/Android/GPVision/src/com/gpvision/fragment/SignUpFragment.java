package com.gpvision.fragment;

import com.gpvision.R;
import com.gpvision.activity.MainActivity;
import com.gpvision.api.APIResponseHandler;
import com.gpvision.api.request.SignUpRequest;
import com.gpvision.api.response.SignUpResponse;
import com.gpvision.datamodel.Account;
import com.gpvision.utils.AppUtils;
import com.gpvision.utils.LocalDataBuffer;
import com.gpvision.utils.LogUtil;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

public class SignUpFragment extends BaseFragment {

	private EditText mUserName, mEmail, mPass, mConfirmPass;

	// private Button mSignUp, mCancel;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater
				.inflate(R.layout.fragment_signup, container, false);
		mUserName = (EditText) view
				.findViewById(R.id.signup_fragment_user_name);
		mEmail = (EditText) view.findViewById(R.id.signup_fragment_email);
		mPass = (EditText) view.findViewById(R.id.signup_fragment_pass);
		mConfirmPass = (EditText) view
				.findViewById(R.id.signup_fragment_confirm_pass);

		view.findViewById(R.id.signup_fragment_sign_up_btn).setOnClickListener(
				this);
		view.findViewById(R.id.signup_fragment_cancel_btn).setOnClickListener(
				this);
		return view;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.signup_fragment_sign_up_btn:
			doSignUp();
			break;
		case R.id.signup_fragment_cancel_btn:
			getFragmentManager().popBackStack();
			break;

		default:
			break;
		}
	}

	private void doSignUp() {
		final String userName = mUserName.getText().toString().trim();
		String email = mEmail.getText().toString().trim();
		String pass = mPass.getText().toString().trim();
		String confirmPass = mConfirmPass.getText().toString().trim();
		// check
		if (AppUtils.isEmpty(userName)) {
			AppUtils.toastLong(getActivity(),
					R.string.signup_fragment_toast_message_user_name_null);
			return;
		}
		if (AppUtils.isEmpty(email)) {
			AppUtils.toastLong(getActivity(),
					R.string.signup_fragment_toast_message_email_null);
			return;
		}
		if (AppUtils.isEmpty(pass) || AppUtils.isEmpty(confirmPass)) {
			AppUtils.toastLong(getActivity(),
					R.string.signup_fragment_toast_message_password_null);
			return;
		}
		if (!pass.equals(confirmPass)) {
			AppUtils.toastLong(getActivity(),
					R.string.signup_fragment_toast_message_pass_not_same);
			return;
		}

		// call api
		new SignUpRequest(userName, email, pass)
				.start(new APIResponseHandler<SignUpResponse>() {

					@Override
					public void handleResponse(SignUpResponse response) {
						Account account = new Account();
						account.setAccount(userName);
						account.setUserToken(response.getUserToken());
						LocalDataBuffer.getInstance().setAccount(account);
						logined();
					}

					@Override
					public void handleError(Long errorCode, String errorMessage) {
						LogUtil.logE(errorMessage);
					}
				});
	}

	private void logined() {
		Intent intent = new Intent();
		intent.setClass(getActivity(), MainActivity.class);
		startActivity(intent);
	}
}
