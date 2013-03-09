package com.gpvision.api.request;

import java.io.IOException;
import java.io.InputStream;

import android.os.AsyncTask;
import android.os.Handler;
import com.gpvision.utils.LogUtil;
import org.apache.http.util.EncodingUtils;
import org.json.JSONException;

import android.content.Context;

import com.gpvision.api.CallAPI;
import com.gpvision.api.response.GetIndexResponse;
import com.gpvision.http.HttpResponse;

public class GetIndexRequest extends CallAPI<GetIndexResponse> {

	private Context mContext;

	public GetIndexRequest(Context mContext) {
		super();
		this.mContext = mContext;
	}

	@Override
	protected String serviceComponent() {
		return null;
	}

	@Override
	protected HttpResponse doInBackground(Void... params) {
		InputStream inputStream;
		String res = null;
		try {
			inputStream = mContext.getAssets().open("mockdata/index.json");
			int length = inputStream.available();
			byte[] buffer = new byte[length];

			inputStream.read(buffer);
			res = EncodingUtils.getString(buffer, "utf-8");
			inputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new HttpResponse(res, 200);
	}

	@Override
	protected void onPostExecute(HttpResponse result) {
		super.onPostExecute(result);
	}

	@Override
	protected void onResponseReceived(final String respString)
			throws JSONException {
		GetIndexResponse response = new GetIndexResponse(respString);
		responseHandler.handleResponse(response);
	}

}
