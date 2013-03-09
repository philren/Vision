package com.gpvision.api.response;

import java.util.ArrayList;
import java.util.HashMap;

import com.gpvision.datamodel.Location;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.gpvision.api.APIResponse;
import com.gpvision.utils.LogUtil;

public class GetIndexResponse extends APIResponse {

	private HashMap<Integer,ArrayList<Location>> indexMap;

	public GetIndexResponse(String response) throws JSONException {
		LogUtil.logI(response);
        indexMap = new HashMap<Integer, ArrayList<Location>>();
		JSONObject jsonObject = new JSONObject(response);
		int size = jsonObject.length();
		LogUtil.logI("size:" + size);
		int n = 0, position = 0;
		while (n < size) {
			String temp = String.valueOf(position);
			if (jsonObject.has(temp)) {
				LogUtil.logI("n:" + n + "key:" + position + "text:"
                        + jsonObject.getJSONArray(temp));
                JSONArray jsonArray = jsonObject.getJSONArray(temp);
                int locationSize = jsonArray.length();
                ArrayList<Location> locations = new ArrayList<Location>();
                for(int i=0;i<locationSize;i++){
                    Location location = new Location();
                    JSONObject object = jsonArray.getJSONObject(i);
                    object = object.getJSONObject("location");
                    location.setHeight(object.getInt("Height"));
                    location.setTop(object.getInt("Top"));
                    location.setLeft(object.getInt("left"));
                    location.setWidth(object.getInt("Width"));
                    locations.add(location);
                }
                indexMap.put(position,locations);
				position++;
				n++;
			} else {
				position++;
			}
		}

	}

	public HashMap<Integer,ArrayList<Location>> getIndexMap() {
		return indexMap;
	}
}
