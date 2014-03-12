package com.tapjoy.reach.service;

import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;

public class ResponseModel {

	private String response;
	private HttpResponseStatus responseStatus;
	private MessageEvent messageEvent;
	private String contentType;

	public ResponseModel(String response, HttpResponseStatus status,
			MessageEvent messageEvent, String contentType) {
		this.response = response;
		this.responseStatus = status;
		this.messageEvent = messageEvent;
		this.contentType = contentType;
	}

	public ResponseModel(MessageEvent messageEvent) {
		this.messageEvent = messageEvent;
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

	public MessageEvent getMessageEvent() {
		return messageEvent;
	}

	public void setMessageEvent(MessageEvent messageEvent) {
		this.messageEvent = messageEvent;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

}
