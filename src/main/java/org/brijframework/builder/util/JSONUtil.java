package org.brijframework.builder.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JSONUtil {

	@SuppressWarnings("unchecked")
	public static Map<String, Object> toMap(String json) {
		JSONObject object = null;
		try {
			object = new JSONObject(json);
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		Map<String, Object> source = new HashMap<>();
		Iterator<Object> keys = object.keys();
		while (keys.hasNext()) {
			Object key = keys.next();
			try {
				Object value = object.get(key.toString());
				if (value instanceof JSONArray) {
					source.put(key.toString(), JSONUtil.toList((JSONArray) value));
				} else if (value instanceof JSONObject) {
					source.put(key.toString(), JSONUtil.toMap((JSONObject) value));
				} else {
					source.put(key.toString(), value);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return source;
	}

	public static List<Object> toList(JSONArray array) {
		List<Object> list = new ArrayList<>();
		for (int i = 0; i < array.length(); i++) {
			try {
				Object value = array.get(i);
				if (value instanceof JSONArray) {
					list.add(JSONUtil.toList((JSONArray) value));
				} else if (value instanceof JSONObject) {
					list.add(JSONUtil.toMap((JSONObject) value));
				} else {
				   list.add(value);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return list;
	}

	@SuppressWarnings("unchecked")
	public static Map<?, ?> toMap(JSONObject object) {
		Map<String, Object> map = new HashMap<>();
		object.keys().forEachRemaining(key -> {
			Object value = null;
			try {
				value = object.get(key.toString());
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (value instanceof JSONArray) {
				map.put(key.toString(), JSONUtil.toList((JSONArray) value));
			} else if (value instanceof JSONObject) {
				map.put(key.toString(), JSONUtil.toMap((JSONObject) value));
			} else {
				map.put(key.toString(), value);
			}
		});
		return map;
	}
}
