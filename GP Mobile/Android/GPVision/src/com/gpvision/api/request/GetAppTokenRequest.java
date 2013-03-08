package com.gpvision.api.request;

import org.json.JSONException;

import com.gpvision.api.CallAPI;
import com.gpvision.api.response.GetAppTokenResponse;

public class GetAppTokenRequest extends CallAPI<GetAppTokenResponse> {

	@Override
	protected String serviceComponent() {
		return "/api/getapptoken";
	}

	@Override
	protected void onResponseReceived(String respString) throws JSONException {
		GetAppTokenResponse response = new GetAppTokenResponse(respString);
		responseHandler.handleResponse(response);
	}

}
