package com.gpvision.api.response;

import com.gpvision.api.APIResponse;

public class ChangePassResponse extends APIResponse {

	private String result;

	public ChangePassResponse(String response) {
		result = response;
	}

	public String getResult() {
		return result;
	}
}
