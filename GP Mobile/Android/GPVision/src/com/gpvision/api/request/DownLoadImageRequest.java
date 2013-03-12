package com.gpvision.api.request;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.apache.http.client.ClientProtocolException;

import android.os.AsyncTask;

import com.gpvision.api.APIResponse;
import com.gpvision.api.APIResponseHandler;
import com.gpvision.http.HttpRequest;
import com.gpvision.utils.ImageCacheUtil;
import com.gpvision.utils.LocalDataBuffer;

public class DownLoadImageRequest<RESPONSE extends APIResponse> extends
		AsyncTask<Void, Void, Void> {

	private APIResponseHandler<RESPONSE> responseHandler;

	private ArrayList<String> imageUrls;

	public DownLoadImageRequest(ArrayList<String> imageUrls) {
		super();
		this.imageUrls = imageUrls;
	}

	public void start(APIResponseHandler<RESPONSE> handler) {
		this.responseHandler = handler;
		execute();
	}

	@Override
	protected Void doInBackground(Void... params) {
		String userToken = LocalDataBuffer.getInstance().getAccount()
				.getUserToken();
		for (String url : imageUrls) {
			HttpRequest request = new HttpRequest(url);
			request.addHeader("endUserToken", userToken);
			String fileName = ImageCacheUtil.getFileNameFromUrl(url);
			InputStream inputStream;
			try {
				inputStream = request.getStream();
				ImageCacheUtil.write2SDCard(fileName, inputStream);
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		super.onPostExecute(result);
		responseHandler.handleResponse(null);
	}

}
