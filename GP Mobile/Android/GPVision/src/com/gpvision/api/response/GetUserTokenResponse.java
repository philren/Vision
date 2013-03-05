package com.gpvision.api.response;

import org.json.JSONException;
import org.json.JSONObject;

import com.gpvision.api.APIResponse;

public class GetUserTokenResponse extends APIResponse {

	private String userToken;

	public GetUserTokenResponse(String response) throws JSONException {
		JSONObject json = new JSONObject(response);
		userToken = json.getString("endUserToken");
	}

	public String getUserToken() {
		return userToken;
	}
}
