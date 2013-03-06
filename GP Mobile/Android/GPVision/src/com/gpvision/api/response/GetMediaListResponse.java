package com.gpvision.api.response;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.gpvision.api.APIResponse;
import com.gpvision.datamodel.Video;

public class GetMediaListResponse extends APIResponse {
	private ArrayList<Video> videos;

	public GetMediaListResponse(String Response) throws JSONException {
		JSONArray jsonArray = new JSONArray(Response);
		int size = jsonArray.length();
		videos = new ArrayList<Video>();
		for (int i = 0; i < size; i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);
			Video video = new Video();
			video.setUuid(jsonObject.getString("videoUUID"));
			video.setStoreName(jsonObject.getString("videoStoreName"));
			video.setOriginalName(jsonObject.getString("videoOriginalName"));
			videos.add(video);
		}
	}

	public ArrayList<Video> getVideos() {
		return videos;
	}
}
