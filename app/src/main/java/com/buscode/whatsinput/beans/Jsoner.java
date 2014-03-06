package com.buscode.whatsinput.beans;

import com.google.gson.Gson;

public class Jsoner {

	private static Jsoner sInstance;

	public static Jsoner getInstance() {
		if (sInstance == null) {
			sInstance = new Jsoner();
		}
		return sInstance;
	}
	private Gson mGson;
	
	private Jsoner() {
		mGson = new Gson();
	}
	
	public String toJson(Object obj) {
		return mGson.toJson(obj);
	}
	
	public <T> T fromJson(String s, Class<T> cls) {
		return mGson.fromJson(s, cls);
	}
}
