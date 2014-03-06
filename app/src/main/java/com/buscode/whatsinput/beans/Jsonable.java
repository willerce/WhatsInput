package com.buscode.whatsinput.beans;

public class Jsonable {

	public String toJson() {
		return Jsoner.getInstance().toJson(this);
	}
}
