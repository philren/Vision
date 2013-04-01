package com.gpvision.api.request;

import java.util.HashMap;

import org.json.JSONException;

import com.gpvision.api.CallAPI;
import com.gpvision.api.response.GetMediaListResponse;
import com.gpvision.utils.LocalDataBuffer;

public class GetMediaListRequset extends CallAPI<GetMediaListResponse> {

	@Override
	protected String serviceComponent() {
		return "/api/getmedialist";
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
	protected void onResponseReceived(String respString) throws JSONException {
		GetMediaListResponse response = new GetMediaListResponse(respString);
		responseHandler.handleResponse(response);
	}

}
