package com.gpvision.fragment;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.gpvision.R;
import com.gpvision.api.APIError;
import com.gpvision.api.APIResponseHandler;
import com.gpvision.api.request.ChangePassRequest;
import com.gpvision.api.response.ChangePassResponse;
import com.gpvision.ui.dialog.ErrorDialog;
import com.gpvision.ui.dialog.LoadingDialog;
import com.gpvision.utils.AppUtils;
import com.gpvision.utils.LocalDataBuffer;
import com.gpvision.utils.LogUtil;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class SettingFragment extends BaseFragment {

	public static final String TAG = SettingFragment.class.getName();
	public static final String REG_TEXT = "^[A-Za-z0-9]+$";
	private TextView mOldPass, mNewPass, mConfirmPass;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_setting, container,
				false);
		mOldPass = (TextView) view
				.findViewById(R.id.setting_fragment_old_password);
		mNewPass = (TextView) view
				.findViewById(R.id.setting_fragment_new_password);
		mConfirmPass = (TextView) view
				.findViewById(R.id.setting_fragment_confirm_password);

		view.findViewById(R.id.setting_fragment_ok_btn)
				.setOnClickListener(this);
		view.findViewById(R.id.setting_fragment_cancel_btn).setOnClickListener(
				this);
		return view;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.setting_fragment_ok_btn:
			doChangePass();
			break;
		case R.id.setting_fragment_cancel_btn:
			getFragmentManager().popBackStack();
			break;

		default:
			break;
		}
	}

	private void doChangePass() {
		final LoadingDialog dialog = new LoadingDialog(getActivity());
		dialog.show();
		String oldPass = mOldPass.getText().toString().trim();
		String newPass = mNewPass.getText().toString().trim();
		String confirmPass = mConfirmPass.getText().toString().trim();

		Pattern pattern = Pattern.compile(REG_TEXT);
		// check old pass
		if (AppUtils.isEmpty(oldPass)) {
			new ErrorDialog(getActivity(), R.string.base_error_title,
					R.string.setting_fragment_error_message_old_pass_empty);
			return;
		}
		// check new pass
		if (AppUtils.isEmpty(newPass) || AppUtils.isEmpty(confirmPass)) {
			new ErrorDialog(getActivity(), R.string.base_error_title,
					R.string.setting_fragment_error_message_new_pass_empty);
			return;
		}
		if (!newPass.equals(confirmPass)) {
			new ErrorDialog(getActivity(), R.string.base_error_title,
					R.string.setting_fragment_error_message_pass_not_same);
			return;
		}
		Matcher matcher = pattern.matcher(newPass);
		if (!matcher.matches()) {
			new ErrorDialog(
					getActivity(),
					R.string.base_error_title,
					R.string.setting_fragment_error_message_pass_can_not_input_special_chars);
			return;
		}

		String userName = LocalDataBuffer.getInstance().getAccount()
				.getAccount();
		new ChangePassRequest(userName, oldPass, newPass)
				.start(new APIResponseHandler<ChangePassResponse>() {

					@Override
					public void handleResponse(ChangePassResponse response) {
						AppUtils.toastLong(getActivity(), response.getResult());
						dialog.dismiss();
						getFragmentManager().popBackStack();
					}

					@Override
					public void handleError(Long errorCode, String errorMessage) {
						LogUtil.logE(errorMessage);
						dialog.dismiss();
						if (errorCode == APIError.SETTING_ERROR_PASS_NOT_CORRENT) {
							new ErrorDialog(
									getActivity(),
									R.string.base_error_title,
									R.string.api_error_setting_error_pass_not_corrent);
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
}
