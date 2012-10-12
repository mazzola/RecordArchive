package com.recordarchive.android;

public class ApiCall {
	
	public int action;
	//public String resource;
	public String[][] params;
	
	public ApiCall(int action, String[][] params) {
		this.action = action;
		//this.resource = rescource;
		this.params = params;
	}
	
	public String getArtist() {
		return params[1][1];
	}
	
	public String getTitle() {
		return params[2][1];
	}
}
