package com.gpvision.api.request;

import java.util.HashMap;

import org.json.JSONException;

import com.gpvision.api.CallAPI;
import com.gpvision.api.response.DeleteVideoResponse;
import com.gpvision.utils.LocalDataBuffer;

public class DeleteVideoRequest extends CallAPI<DeleteVideoResponse> {

	private String videoId;

	public DeleteVideoRequest(String videoId) {
		super();
		this.videoId = videoId;
	}

	@Override
	protected String serviceComponent() {
		return "/api/deletevideo/" + videoId;
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
		if (respString.equals("true")) {
			responseHandler.handleResponse(null);
		} else {
			new JSONException(respString);
		}
	}

}
