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

	private HashMap<Integer, ArrayList<Index>> indexMap;

	public GetIndexResponse(String response) throws JSONException {
		LogUtil.logI(response);
		indexMap = new HashMap<Integer, ArrayList<Index>>();
		JSONObject jsonObject = new JSONObject(response);
		int size = jsonObject.length();
		LogUtil.logI("size:" + size);
		int n = 0, position = 0;
		while (n < size) {
			String temp = String.valueOf(position);
			if (jsonObject.has(temp)) {
				JSONArray indexArray = jsonObject.getJSONArray(temp);
				int indexArraySize = indexArray.length();
				ArrayList<Index> indexs = new ArrayList<Index>();
				for (int i = 0; i < indexArraySize; i++) {
					Index index = new Index();
					Location location = new Location();
					JSONObject object = indexArray.getJSONObject(i);
					JSONObject locationObject = object
							.getJSONObject("location");
					location.setHeight(locationObject.getInt("Height"));
					location.setTop(locationObject.getInt("Top"));
					location.setLeft(locationObject.getInt("left"));
					location.setWidth(locationObject.getInt("Width"));
					index.setLocation(location);
					index.setImageUrl(object.getString("name"));
					indexs.add(index);
				}
				indexMap.put(position, indexs);
				position++;
				n++;
			} else {
				position++;
			}
		}

	}

	public HashMap<Integer, ArrayList<Index>> getIndexMap() {
		return indexMap;
	}
}
