package com.gpvision.fragment;

import com.gpvision.R;
import com.gpvision.api.APIResponseHandler;
import com.gpvision.api.request.ChangePassRequest;
import com.gpvision.api.response.ChangePassResponse;
import com.gpvision.utils.AppUtils;
import com.gpvision.utils.LocalDataBuffer;
import com.gpvision.utils.LogUtil;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class SettingFragment extends BaseFragment {

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
		String oldPass = mOldPass.getText().toString().trim();
		String newPass = mNewPass.getText().toString().trim();
		String confirmPass = mConfirmPass.getText().toString().trim();
		// check
		if (AppUtils.isEmpty(oldPass)) {
			AppUtils.toastLong(getActivity(),
					R.string.setting_fragment_toast_message_new_pass_null);
			return;
		}
		if (AppUtils.isEmpty(newPass) || AppUtils.isEmpty(confirmPass)) {
			AppUtils.toastLong(getActivity(),
					R.string.setting_fragment_toast_message_new_pass_null);
			return;
		}
		if (!newPass.equals(confirmPass)) {
			AppUtils.toastLong(getActivity(),
					R.string.setting_fragment_toast_message_pass_not_same);
			return;
		}
		String userName = LocalDataBuffer.getInstance().getAccount()
				.getAccount();
		new ChangePassRequest(userName, oldPass, newPass)
				.start(new APIResponseHandler<ChangePassResponse>() {

					@Override
					public void handleResponse(ChangePassResponse response) {
						AppUtils.toastLong(getActivity(), response.getResult());
						getFragmentManager().popBackStack();
					}

					@Override
					public void handleError(Long errorCode, String errorMessage) {
						LogUtil.logE(errorMessage);
					}
				});
	}
}
