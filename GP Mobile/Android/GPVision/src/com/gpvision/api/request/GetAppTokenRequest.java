package com.gpvision.api.request;

import org.json.JSONException;

import android.net.Uri.Builder;

import com.gpvision.api.CallAPI;
import com.gpvision.api.response.GetAppTokenResponse;

public class GetAppTokenRequest extends CallAPI<GetAppTokenResponse> {

	@Override
	protected void getComponent(Builder builder) {
		builder.appendEncodedPath("api");
		builder.appendEncodedPath("getapptoken");
	}

	@Override
	protected void onResponseReceived(String respString) throws JSONException {
		GetAppTokenResponse response = new GetAppTokenResponse(respString);
		responseHandler.handleResponse(response);
	}

}
