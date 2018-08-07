package com.lunabox.bean;

import com.google.gson.Gson;

public abstract class C2SBaseBean {
	public String toJson() {
		Gson g = new Gson();
		return g.toJson(this);
	}
	
	public String toString() {
		return toJson();
	}
}
