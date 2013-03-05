package com.gpvision.api;

public interface APIResponseHandler<RESPONSE extends APIResponse> {

	void handleError(Long errorCode, String errorMessage);

	void handleResponse(RESPONSE response);
}
