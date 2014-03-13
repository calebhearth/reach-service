package com.tapjoy.reach.service;

import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;

public class ResponseModel {

	private String response;
	private HttpResponseStatus responseStatus;
	private String contentType;

	public ResponseModel(String response, HttpResponseStatus status,
			String contentType) {
		this.response = response;
		this.responseStatus = status;
		this.contentType = contentType;
	}

	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
	}

	public HttpResponseStatus getResponseStatus() {
		return responseStatus;
	}

	public void setResponseStatus(HttpResponseStatus responseStatus) {
		this.responseStatus = responseStatus;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

}
