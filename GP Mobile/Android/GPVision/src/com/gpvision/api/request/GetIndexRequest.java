package com.gpvision.api.request;

import java.util.HashMap;

import org.json.JSONException;

import com.gpvision.api.CallAPI;
import com.gpvision.api.response.GetIndexResponse;
import com.gpvision.utils.LocalDataBuffer;

public class GetIndexRequest extends CallAPI<GetIndexResponse> {

	private String fileName;

	public GetIndexRequest(String fileName) {
		super();
		this.fileName = fileName;
	}

	@Override
	protected String serviceComponent() {
		return "/api/video_index";
	}

	@Override
	protected HashMap<String, String> getParameters() {
		HashMap<String, String> parameters = super.getParameters();
		parameters.put("filename", fileName);
		return parameters;
	}

	@Override
	protected HashMap<String, String> getHeaders() {
		HashMap<String, String> headers = super.getHeaders();
		headers.put("endUserToken", LocalDataBuffer.getInstance().getAccount()
				.getUserToken());
		return headers;
	}

	@Override
	protected void onResponseReceived(final String respString)
			throws JSONException {
		GetIndexResponse response = new GetIndexResponse(respString);
		responseHandler.handleResponse(response);
	}

}
