package com.gpvision.api.request;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import android.os.AsyncTask;

import com.gpvision.api.APIError;
import com.gpvision.api.APIResponse;
import com.gpvision.api.APIResponseHandler;
import com.gpvision.ui.LocalDataBuffer;

public class UploadFileRequest<RESPONSE extends APIResponse> extends
		AsyncTask<Void, Void, Integer> {

	private APIResponseHandler<RESPONSE> responseHandler;

	private String mUrl;
	private ArrayList<String[]> mFiles;

	public UploadFileRequest(String url) {
		mUrl = url;
		mFiles = new ArrayList<String[]>();
	}

	public void addFile(String name, String type, String path) {
		mFiles.add(new String[] { name, type, path });
	}

	public void start(APIResponseHandler<RESPONSE> handler) {
		this.responseHandler = handler;
		execute();
	}

	@Override
	protected Integer doInBackground(Void... params) {

		String BOUNDARY = java.util.UUID.randomUUID().toString();
		String PREFIX = "--", LINEND = "\r\n";
		String MULTIPART_FROM_DATA = "multipart/form-data";

		int res = 0;

		try {
			URL uri = new URL(mUrl);
			HttpURLConnection conn = (HttpURLConnection) uri.openConnection();

			conn.setReadTimeout(5 * 1000);
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setUseCaches(false);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("connection", "keep-alive");
			conn.setRequestProperty("Charsert", "UTF-8");

			conn.setRequestProperty("Content-Type", MULTIPART_FROM_DATA
					+ ";boundary=" + BOUNDARY);

			conn.setRequestProperty("accept",
					"application/json, text/javascript, */*; q=0.01");

			conn.setRequestProperty("endUserToken", LocalDataBuffer
					.getInstance().getAccount().getUserToken());

			DataOutputStream outStream = new DataOutputStream(
					conn.getOutputStream());

			if (mFiles != null && mFiles.size() > 0)
				for (int i = 0, num = mFiles.size(); i < num; i++) {
					String[] param = mFiles.get(i);
					String fileName = param[0];
					String fileType = param[1];
					String filePath = param[2];

					StringBuilder sb1 = new StringBuilder();
					sb1.append(PREFIX);
					sb1.append(BOUNDARY);
					sb1.append(LINEND);
					sb1.append("Content-Disposition: form-data; name=\"file[]\"; filename=\""
							+ fileName + "\"" + LINEND);
					sb1.append("Content-Type: " + fileType + LINEND);
					sb1.append(LINEND);
					outStream.write(sb1.toString().getBytes());

					InputStream is = new FileInputStream(new File(filePath));
					byte[] buffer = new byte[1024];
					int len = 0;
					while ((len = is.read(buffer)) != -1) {
						outStream.write(buffer, 0, len);
					}

					is.close();
					outStream.write(LINEND.getBytes());
				}

			byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINEND).getBytes();
			outStream.write(end_data);
			outStream.flush();

			res = conn.getResponseCode();
			InputStream in = conn.getInputStream();
			// InputStreamReader isReader = new InputStreamReader(in);
			//
			// BufferedReader bufReader = new BufferedReader(isReader);
			//
			// String line = null;
			// String data = "OK";
			// while ((line = bufReader.readLine()) == null)
			// data += line;

			if (res == 200) {
				int ch;
				StringBuilder sb2 = new StringBuilder();
				while ((ch = in.read()) != -1) {
					sb2.append((char) ch);
				}
			}

			outStream.close();
			conn.disconnect();

		} catch (IOException e) {
		}

		return res;
	}

	@Override
	protected void onPostExecute(Integer result) {
		if (responseHandler == null)
			return;

		if (result == 200) {
			responseHandler.handleResponse(null);
		} else {
			responseHandler.handleError(APIError.NETWORK_ERROR.first,
					APIError.NETWORK_ERROR.second);
		}
	}
}
