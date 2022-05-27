package com.otsi.retail.inventory.controller;

import org.json.JSONObject;

/**
 * 
 * @author Saikiran Kola
 *
 */
public class CommonUtilities {

	public static String buildSuccessResponse(Object data, String key) {
		JSONObject json = new JSONObject();
		if (data != null)
			json.put(key, data);
		return json.toString();
	}

}
