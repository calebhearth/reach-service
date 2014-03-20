package com.tapjoy.reach.service;

public class ErrorModel {
	
	public ErrorModel(int code, String msg){
		error_code = code;
		error_message = msg;
	}
	
	private int error_code;
	private String error_message;
	public int getError_code() {
		return error_code;
	}
	public void setError_code(int error_code) {
		this.error_code = error_code;
	}
	public String getError_message() {
		return error_message;
	}
	public void setError_message(String error_message) {
		this.error_message = error_message;
	}
	
	

}
