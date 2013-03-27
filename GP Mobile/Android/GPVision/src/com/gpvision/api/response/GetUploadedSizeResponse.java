package com.gpvision.api.response;

import org.json.JSONException;
import org.json.JSONObject;

import com.gpvision.api.APIResponse;

public class GetUploadedSizeResponse extends APIResponse {

	private long uploadedSize;

	public GetUploadedSizeResponse(String response) throws JSONException {

		JSONObject jsonObject = new JSONObject(response).getJSONObject("file");
		uploadedSize = jsonObject.getLong("size");
	}

	public long getUploadedSize() {
		return uploadedSize;
	}

}
