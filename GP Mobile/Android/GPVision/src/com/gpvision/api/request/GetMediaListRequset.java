package com.gpvision.api.request;

import java.util.HashMap;

import org.json.JSONException;

import android.net.Uri.Builder;

import com.gpvision.api.CallAPI;
import com.gpvision.api.response.GetMediaListResponse;
import com.gpvision.ui.LocalDataBuffer;

public class GetMediaListRequset extends CallAPI<GetMediaListResponse> {

	@Override
	protected void getComponent(Builder builder) {
		builder.appendEncodedPath("api");
		builder.appendEncodedPath("getmedialist");
	}

	@Override
	protected void addGetParams(Builder builder) {
		builder.appendQueryParameter("endUserToken", LocalDataBuffer
				.getInstance().getAccount().getUserToken());
	}

	@Override
	protected HashMap<String, String> getHeaders() {
		HashMap<String, String> headers = super.getHeaders();
		headers.put("endUserToken", LocalDataBuffer.getInstance().getAccount()
				.getUserToken());
		return headers;
	}

	@Override
	protected void onResponseReceived(String respString) throws JSONException {
		GetMediaListResponse response = new GetMediaListResponse(respString);
		responseHandler.handleResponse(response);
	}

}
