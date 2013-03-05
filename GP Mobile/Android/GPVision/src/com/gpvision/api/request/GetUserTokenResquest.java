package com.gpvision.api.request;

import org.json.JSONException;
import org.json.JSONObject;

import android.net.Uri.Builder;

import com.gpvision.api.CallAPI;
import com.gpvision.api.response.GetUserTokenResponse;
import com.gpvision.ui.LocalDataBuffer;

public class GetUserTokenResquest extends CallAPI<GetUserTokenResponse> {

	private String account, password;

	public GetUserTokenResquest(String account, String password) {
		super();
		this.account = account;
		this.password = password;
	}

	@Override
	protected void getComponent(Builder builder) {
		builder.appendEncodedPath("api");
		builder.appendEncodedPath("getendusertoken");
	}

	@Override
	protected String getPostBody() throws JSONException {
		JSONObject json = new JSONObject();
		json.put("userName", account);
		json.put("password", password);
		json.put("appToken", LocalDataBuffer.getInstance().getAccount()
				.getAppToken());
		return json.toString();
	}

	@Override
	protected void onResponseReceived(String respString) throws JSONException {
		GetUserTokenResponse response = new GetUserTokenResponse(respString);
		responseHandler.handleResponse(response);
	}

}
