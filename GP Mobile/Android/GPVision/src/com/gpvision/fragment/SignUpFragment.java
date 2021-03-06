package com.gpvision.fragment;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.gpvision.R;
import com.gpvision.api.APIResponseHandler;
import com.gpvision.api.request.SignUpRequest;
import com.gpvision.api.response.SignUpResponse;
import com.gpvision.ui.dialog.ErrorDialog;
import com.gpvision.ui.dialog.SignUpDialog;
import com.gpvision.utils.ApiErrorHandler;
import com.gpvision.utils.AppUtils;
import com.gpvision.utils.LogUtil;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

public class SignUpFragment extends BaseFragment {

	public static final String TAG = SignUpFragment.class.getName();
	public static final String REG_TEXT = "^[A-Za-z0-9]+$";
	public static final String REG_EMAIL = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
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

		Pattern pattern = Pattern.compile(REG_TEXT);
		// check user name
		if (AppUtils.isEmpty(userName)) {
			new ErrorDialog(getActivity(), R.string.base_error_title,
					R.string.signup_fragment_error_message_user_name_empty);
			return;
		}
		Matcher matcher = pattern.matcher(userName);
		if (!matcher.matches()) {
			new ErrorDialog(
					getActivity(),
					R.string.base_error_title,
					R.string.signup_fragment_error_message_user_name_sprcial_chars);
			return;
		}
		// check pass
		if (AppUtils.isEmpty(pass) || AppUtils.isEmpty(confirmPass)) {
			new ErrorDialog(getActivity(), R.string.base_error_title,
					R.string.signup_fragment_error_message_password_empty);
			return;
		}
		if (!pass.equals(confirmPass)) {
			new ErrorDialog(getActivity(), R.string.base_error_title,
					R.string.signup_fragment_error_message_pass_not_same);
			return;
		}
		matcher = pattern.matcher(pass);
		if (!matcher.matches()) {
			new ErrorDialog(
					getActivity(),
					R.string.base_error_title,
					R.string.signup_fragment_error_message_password_sprcial_chars);
			return;
		}
		// check email
		if (AppUtils.isEmpty(email)) {
			new ErrorDialog(getActivity(), R.string.base_error_title,
					R.string.signup_fragment_error_message_email_empty);
			return;
		}
		pattern = Pattern.compile(REG_EMAIL);
		matcher = pattern.matcher(email);
		if (!matcher.matches()) {
			new ErrorDialog(getActivity(), R.string.base_error_title,
					R.string.signup_fragment_error_message_email_format_error);
			return;
		}

		final SignUpDialog dialog = new SignUpDialog(getActivity());
		dialog.show();
		// call api
		new SignUpRequest(userName, email, pass)
				.start(new APIResponseHandler<SignUpResponse>() {

					@Override
					public void handleResponse(SignUpResponse response) {
						dialog.dismiss();
						AppUtils.toastLong(getActivity(),
								R.string.signup_fragment_success);
						getFragmentManager().popBackStack();
					}

					@Override
					public void handleError(Long errorCode, String errorMessage) {
						LogUtil.logE(errorMessage);
						dialog.dismiss();
						ApiErrorHandler.handler(getActivity(), errorCode);
					}
				});
	}

}
