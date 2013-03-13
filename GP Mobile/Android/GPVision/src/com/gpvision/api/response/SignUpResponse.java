package com.gpvision.api.response;

import org.json.JSONException;
import org.json.JSONObject;

import com.gpvision.api.APIResponse;

public class SignUpResponse extends APIResponse {

	private String userToken;

	public SignUpResponse(String response) throws JSONException {
		JSONObject jsonObject = new JSONObject(response);
		userToken = jsonObject.getString("endUserToken");
	}

	public String getUserToken() {
		return userToken;
	}

}
