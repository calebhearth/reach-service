package com.tapjoy.reach.paramslist;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;

import com.tapjoy.reach.params.KeyEnum;
import com.tapjoy.reach.service.ResponseModel;

public class ParamsController {
	
	public static Pattern pattern = Pattern.compile("/api/v1/params/([^/]+)/*");

	public ResponseModel getResults(HttpRequest request) {
		String reqStr = request.getUri();
		Matcher matcher = pattern.matcher(reqStr);
		if(!matcher.find()){
			ResponseModel model = new ResponseModel("Invalid URL", HttpResponseStatus.BAD_REQUEST, "application/json");
			return model;
		}
		String param = matcher.group(1);
		KeyEnum key = KeyEnum.getEnum(param);
		if(key == null){
			ResponseModel model = new ResponseModel("Invalid parameter:"+param, HttpResponseStatus.BAD_REQUEST, "application/json");
			return model;
		}
		ParamsHelper paramsHelper = new ParamsHelper(reqStr, key);
		ResponseModel results = paramsHelper.getResult();
		return results;
	}

}
