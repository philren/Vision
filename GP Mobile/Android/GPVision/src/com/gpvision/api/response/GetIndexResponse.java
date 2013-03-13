package com.gpvision.api.response;

import java.util.ArrayList;
import java.util.HashMap;

import com.gpvision.datamodel.Index;
import com.gpvision.datamodel.Location;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.gpvision.api.APIResponse;
import com.gpvision.utils.LogUtil;

public class GetIndexResponse extends APIResponse {

	private HashMap<Integer, Index> indexMap;

	public GetIndexResponse(String response) throws JSONException {
		LogUtil.logI(response);
		indexMap = new HashMap<Integer, Index>();
		JSONObject jsonObject = new JSONObject(response);
		int size = jsonObject.length();
		LogUtil.logI("size:" + size);
		int n = 0, position = 0;
		while (n < size) {
			String temp = String.valueOf(position);
			if (jsonObject.has(temp)) {
				JSONArray locationArray = jsonObject.getJSONArray(temp);
				int locationSize = locationArray.length();
				Index index = new Index();
				ArrayList<Location> locations = new ArrayList<Location>();
				ArrayList<String> imageUris = new ArrayList<String>();
				for (int i = 0; i < locationSize; i++) {
					Location location = new Location();
					JSONObject object = locationArray.getJSONObject(i);
					JSONObject locationObject = object
							.getJSONObject("location");
					location.setHeight(locationObject.getInt("Height"));
					location.setTop(locationObject.getInt("Top"));
					location.setLeft(locationObject.getInt("left"));
					location.setWidth(locationObject.getInt("Width"));
					locations.add(location);
					imageUris.add(object.getString("name"));
				}
				index.setLocations(locations);
				index.setImageUrls(imageUris);
				indexMap.put(position, index);
				position++;
				n++;
			} else {
				position++;
			}
		}

	}

	public HashMap<Integer, Index> getIndexMap() {
		return indexMap;
	}
}
