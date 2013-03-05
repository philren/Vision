package com.gpvision.api.response;

import org.json.JSONException;
import org.json.JSONObject;

import com.gpvision.api.APIResponse;

public class GetAppTokenResponse extends APIResponse {

	private String appToken;

	public GetAppTokenResponse(String reaponse) throws JSONException {
		JSONObject json = new JSONObject(reaponse);
		appToken = json.getString("appToken");
	}

	public String getAppToken() {
		return appToken;
	}
}
