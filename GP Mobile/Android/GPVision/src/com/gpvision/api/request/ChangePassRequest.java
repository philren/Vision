package com.gpvision.api.request;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import com.gpvision.api.CallAPI;
import com.gpvision.api.response.ChangePassResponse;
import com.gpvision.utils.LocalDataBuffer;

public class ChangePassRequest extends CallAPI<ChangePassResponse> {

	private String userName, oldPass, newPass;

	public ChangePassRequest(String userName, String oldPass, String newPass) {
		super();
		this.userName = userName;
		this.oldPass = oldPass;
		this.newPass = newPass;
	}

	@Override
	protected String serviceComponent() {
		return "/api/changepassword";
	}

	@Override
	protected HashMap<String, String> getHeaders() {
		HashMap<String, String> headers = super.getHeaders();
		if (LocalDataBuffer.getInstance().getAccount() != null) {
			headers.put("endUserToken", LocalDataBuffer.getInstance()
					.getAccount().getUserToken());
		}
		return headers;
	}

	@Override
	protected String getContent() throws JSONException {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("userName", userName);
		jsonObject.put("oldPassword", oldPass);
		jsonObject.put("newPassword", newPass);
		return jsonObject.toString();
	}

	@Override
	protected void onResponseReceived(String respString) throws JSONException {
		ChangePassResponse response = new ChangePassResponse(respString);
		responseHandler.handleResponse(response);
	}

}
