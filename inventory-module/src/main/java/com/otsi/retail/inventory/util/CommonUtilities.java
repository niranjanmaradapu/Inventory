package com.otsi.retail.inventory.util;

import org.json.JSONObject;

public class CommonUtilities {
	
	public static String buildSuccessResponse(Object data, String key) {
		JSONObject json = new JSONObject();
		if (data != null)
			json.put(key, data);
		return json.toString();
	}

}
