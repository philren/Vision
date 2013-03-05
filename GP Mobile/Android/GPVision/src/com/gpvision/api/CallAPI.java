package com.gpvision.api;

import java.util.HashMap;
import java.util.Map.Entry;

import org.json.JSONException;
import org.json.JSONObject;

import com.gpvision.http.HttpRequest;
import com.gpvision.http.HttpResponse;
import com.gpvision.utils.AppUtils;
import com.gpvision.utils.Environment;
import com.gpvision.utils.LogUtil;

import android.net.Uri;
import android.net.Uri.Builder;
import android.os.AsyncTask;

public abstract class CallAPI<RESPONSE extends APIResponse> extends
		AsyncTask<Void, Void, HttpResponse> {

	private static final String SCHEME = "http";
	private static final String HTTP_HEADER_CONTENT_TYPE = "Content-Type";
	private static final String CONTENT_TYPE_APPLICATION_JSON = "application/json";

	protected APIResponseHandler<RESPONSE> responseHandler;

	public void start(APIResponseHandler<RESPONSE> handler) {
		this.responseHandler = handler;
		execute();
	}

	protected HashMap<String, String> getHeaders() {
		HashMap<String, String> headers = new HashMap<String, String>();
		headers.put(HTTP_HEADER_CONTENT_TYPE, CONTENT_TYPE_APPLICATION_JSON);
		return headers;
	}

	protected abstract void getComponent(Builder builder);

	protected void addGetParams(Builder builder) {

	}

	protected String getPostBody() throws JSONException {
		return null;
	}

	private String getUrl() {
		Uri.Builder builder = new Uri.Builder();
		builder.encodedPath(String.format("%s://%s", SCHEME,
				Environment.E9.getHost()));
		if (!AppUtils.isEmpty(Environment.E9.getBasePath())) {
			builder.appendPath(Environment.E9.getBasePath());
		}
		getComponent(builder);
		addGetParams(builder);
		return builder.toString();
	}

	@Override
	protected HttpResponse doInBackground(Void... params) {
		String url = getUrl();
		HttpRequest request = new HttpRequest(url);

		try {
			String content = getPostBody();
			if (content != null) {
				request.setPostBody(content);
				LogUtil.logI(content);
			}
		} catch (JSONException e1) {
			e1.printStackTrace();
		}

		// set request header
		HashMap<String, String> headeres = getHeaders();
		if (headeres != null)
			for (Entry<String, String> header : headeres.entrySet())
				request.addHeader(header.getKey(), header.getValue());

		String response = null;
		int responseCode = 0;

		try {
			response = request.getString();
			responseCode = request.getStatusCode();
			LogUtil.logI(response);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return new HttpResponse(response, responseCode);
	}

	@Override
	protected void onPostExecute(HttpResponse result) {
		super.onPostExecute(result);
		if (responseHandler == null)
			return;

		if (result.isSuccess()) {
			try {
				onResponseReceived(result.getData());
			} catch (JSONException e) {
				e.printStackTrace();
				handleError(result.getData());
			}
		} else {
			responseHandler.handleError(APIError.NETWORK_ERROR.first,
					APIError.NETWORK_ERROR.second);
		}
	}

	private void handleError(String response) {
		JSONObject json;
		try {
			json = new JSONObject(response);
			long errorCode = json.getLong("errorCode");
			String errorMessage = json.getString("error");
			responseHandler.handleError(errorCode, errorMessage);
		} catch (JSONException e) {
			e.printStackTrace();
			responseHandler.handleError(APIError.UNKOOW_ERROR.first,
					APIError.UNKOOW_ERROR.second);
		}
	}

	protected abstract void onResponseReceived(String respString)
			throws JSONException;
}
