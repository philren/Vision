package com.gpvision.utils;

import java.io.File;
import java.util.HashMap;
import java.util.Map.Entry;

import com.gpvision.http.HttpRequest;

import android.net.Uri;
import android.os.AsyncTask;

public class UploadUtil {
	private static final String SCHEME = "http";

	public void upload() {
		new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {
				String url = getUrl();
				HttpRequest request = new HttpRequest(url);
				// set request header
				HashMap<String, String> headeres = getHeaders();
				if (headeres != null)
					for (Entry<String, String> header : headeres.entrySet())
						request.addHeader(header.getKey(), header.getValue());

				File file = new File(
						android.os.Environment.getExternalStorageDirectory()
								+ "/test/test.mp4");
				request.setPostBody(file);
				

				String response = null;
				int responseCode = 0;
				try {
					request.execute();
					response = request.getString();
					responseCode = request.getStatusCode();
					LogUtil.logI(response);
				} catch (Exception e) {
					e.printStackTrace();
				}
				return null;
			}
		}.execute();
	}

	private String getUrl() {
		Uri.Builder builder = new Uri.Builder();
		builder.encodedPath(String.format("%s://%s", SCHEME,
				Environment.E9.getHost()));
		if (!AppUtils.isEmpty(Environment.E9.getBasePath())) {
			builder.appendPath(Environment.E9.getBasePath());
		}
		builder.appendEncodedPath("api");
		builder.appendEncodedPath("upload");
		return builder.toString();
	}

	private HashMap<String, String> getHeaders() {
		HashMap<String, String> headers = new HashMap<String, String>();
		headers.put("accept", "application/json, text/javascript, */*; q=0.01");
		headers.put("name", "test.mp4");
		headers.put("type", "video/mp4");
		return headers;
	}
}
