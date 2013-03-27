package com.gpvision.api.request;

import java.util.HashMap;

import org.json.JSONException;

import com.gpvision.api.CallAPI;
import com.gpvision.api.response.GetUploadedSizeResponse;
import com.gpvision.utils.LocalDataBuffer;

public class GetUploadedSizeRequest extends CallAPI<GetUploadedSizeResponse> {

	private String fileName;
	private String uuid;
	private long size;
	private long time;

	public GetUploadedSizeRequest(String fileName, String uuid, long size,
			long time) {
		super();
		this.fileName = fileName;
		this.uuid = uuid;
		this.size = size;
		this.time = time;
	}

	@Override
	protected String serviceComponent() {
		return "/api/getuploadedsize";
	}

	@Override
	protected HashMap<String, String> getParameters() {
		HashMap<String, String> parameters = super.getParameters();
		parameters.put("filename", fileName);
		parameters.put("uuid", uuid);
		parameters.put("size", String.valueOf(size));
		parameters.put("timestamp", String.valueOf(time));
		return parameters;
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
		GetUploadedSizeResponse response = new GetUploadedSizeResponse(
				respString);
		responseHandler.handleResponse(response);
	}

}
