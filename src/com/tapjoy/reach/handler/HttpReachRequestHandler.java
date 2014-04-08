package com.tapjoy.reach.handler;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.netty.handler.codec.http.HttpVersion;
import org.jboss.netty.handler.codec.http.QueryStringDecoder;
import org.jboss.netty.util.CharsetUtil;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.tapjoy.reach.config.OverallConfig;
import com.tapjoy.reach.counts.CountsHelper;
import com.tapjoy.reach.helper.Helper;
import com.tapjoy.reach.params.AppleProductLine;
import com.tapjoy.reach.params.Continents;
import com.tapjoy.reach.params.Countries;
import com.tapjoy.reach.params.KeyEnum;
import com.tapjoy.reach.params.Language;
import com.tapjoy.reach.params.Personas;
import com.tapjoy.reach.params.Platform;
import com.tapjoy.reach.params.Source;
import com.tapjoy.reach.params.States;
import com.tapjoy.reach.service.ErrorModel;
import com.tapjoy.reach.service.ResponseModel;

public class HttpReachRequestHandler extends SimpleChannelUpstreamHandler {

	private Logger logger = Logger.getLogger(HttpReachRequestHandler.class);
	
	private static Gson gson = new GsonBuilder().create();

	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) {

		HttpRequest request = (HttpRequest) e.getMessage();
		Map<String, List<String>> params = null;
		String reqStr = request.getUri();
		
		if(request.getMethod() == HttpMethod.GET){			
			params = parseGetRequestParams(reqStr);
		}
		else if(request.getMethod() == HttpMethod.POST){
			params = parsePostRequestParams(request);
		}
		
		if (!StringUtils.startsWithIgnoreCase(reqStr, "/health")) {
			logger.info(reqStr);
		}
		if (StringUtils.startsWithIgnoreCase(reqStr, "/health")) {
			reqStr = OverallConfig.healthCheck;
		}
		

		
		if (params == null) {
			logger.error("Error while parsing params");
			ErrorModel errorModel = new ErrorModel(HttpResponseStatus.BAD_REQUEST.getCode(), "Invalid params");
			String error = gson.toJson(errorModel);
			ResponseModel responseModel = new ResponseModel(
					error, HttpResponseStatus.BAD_REQUEST,
					"application/json");
			writeResponse(responseModel, e);
			return;
		}

		for (String key : params.keySet()) {
			KeyEnum keyEnum = KeyEnum.fromString(key);
			if (keyEnum == null) {
				logger.error("Invalid parameter:" + key);
				ErrorModel errorModel = new ErrorModel(HttpResponseStatus.BAD_REQUEST.getCode(), "Invalid parameter:" + key);
				String error = gson.toJson(errorModel);
				ResponseModel responseModel = new ResponseModel(
						error,
						HttpResponseStatus.BAD_REQUEST, "application/json");
				writeResponse(responseModel, e);
				return;
			}

		}

		for (Entry<String, List<String>> entry : params.entrySet()) {
			String key = entry.getKey();
			KeyEnum keyEnum = KeyEnum.fromString(key);
			List<String> values = entry.getValue();
			boolean check = false;
			switch (keyEnum) {
			case apple_product_line:
				for (String v : values) {
					check = verifyAppleProductLine(v);
					if (!check) {
						writeResponse(invalidParamResponse(keyEnum, v), e);
						return;
					}
				}

				break;
			case device_os_version:
				for (String v : values) {
					check = verifyDeviceOsVersion(v);
					if (!check) {
						writeResponse(invalidParamResponse(keyEnum, v), e);
						return;
					}
				}
				break;
			case geoip_continent:
				for (String v : values) {
					check = verifyContinent(v);
					if (!check) {
						writeResponse(invalidParamResponse(keyEnum, v), e);
						return;
					}
				}
				break;
			case geoip_country:
				for (String v : values) {
					check = verifyCountry(v);
					if (!check) {
						writeResponse(invalidParamResponse(keyEnum, v), e);
						return;
					}
				}
				break;
			case geoip_region:
				for (String v : values) {
					check = verifyRegion(v);
					if (!check) {
						writeResponse(invalidParamResponse(keyEnum, v), e);
						return;
					}
				}
				break;
			case language:
				for (String v : values) {
					check = verifyLanguage(v);
					if (!check) {
						writeResponse(invalidParamResponse(keyEnum, v), e);
						return;
					}
				}
				break;
			case persona_name:
				for (String v : values) {
					check = verifyPersonaName(v);
					if (!check) {
						writeResponse(invalidParamResponse(keyEnum, v), e);
						return;
					}
				}
				break;
			case platform:
				for (String v : values) {
					check = verifyPlatform(v);
					if (!check) {
						writeResponse(invalidParamResponse(keyEnum, v), e);
						return;
					}
				}
				break;
			case source:
				for (String v : values) {
					check = verifySource(v);
					if (!check) {
						writeResponse(invalidParamResponse(keyEnum, v), e);
						return;
					}
				}
				break;
			default:
				writeResponse(invalidParamResponse(keyEnum, ""), e);
				return;

			}
		}

		// modifyLanguage(params);
		// modifyPlatform(params);

		Helper countsHelper = new CountsHelper();
		ResponseModel results = countsHelper.getResult(params);
		writeResponse(results, e);
	}

	
	private Map<String, List<String>> parsePostRequestParams(HttpRequest request) {
		ChannelBuffer content = request.getContent();
		if (!content.readable()) {
			return null;
		}
		System.out.println(content.toString(CharsetUtil.UTF_8));
		String json = content.toString(CharsetUtil.UTF_8);
		Type mapType = new TypeToken<Map<String, List<String>>>() {
		}.getType();
		try {
			Map<String, List<String>> specs = new Gson()
					.fromJson(json, mapType);
			return specs;
		} catch (JsonSyntaxException ex) {
			logger.error(ex);
			return null;
		}
	}

	private void modifyLanguage(Map<String, List<String>> params) {
		List<String> vals = params.get(KeyEnum.language.toString());
		if (vals == null) {
			return;
		}
		for (int i = 0; i < vals.size(); i++) {
			Language enumName = Language.fromString(vals.get(i));
			vals.set(i, enumName.name().toUpperCase());
		}

	}

	private void modifyPlatform(Map<String, List<String>> params) {
		List<String> vals = params.get(KeyEnum.platform.toString());
		if (vals == null) {
			return;
		}
		for (int i = 0; i < vals.size(); i++) {
			Platform enumName = Platform.fromString(vals.get(i));
			if (enumName.equals(Platform.IOS)) {
				vals.set(i, "IPHONE");
			}

		}

	}

	private boolean verifyDeviceOsVersion(String v) {
		try{
			Double d = Double.parseDouble(v);
		}catch(NumberFormatException ex){
			return false;
		}
		return true;
	}

	private boolean verifyContinent(String v) {
		if (!Continents.hasValue(v)) {
			return false;
		}
		return true;
	}

	private boolean verifyCountry(String v) {
		if (!Countries.hasValue(v)) {
			return false;
		}
		return true;
	}

	private boolean verifyRegion(String v) {
		if (!States.hasValue(v)) {
			return false;
		}
		return true;
	}

	private boolean verifyLanguage(String v) {
		Language enumName = Language.fromString(v);
		if (enumName == null) {
			return false;
		}
		return true;
	}

	private boolean verifyPersonaName(String v) {
		int personaId = Personas.getInstance().getPersonaId(v);
		if (personaId == 0) {
			return false;
		}
		return true;
	}

	private boolean verifyPlatform(String v) {
		Platform enumName = Platform.fromString(v);
		if (enumName == null) {
			return false;
		}
		return true;
	}

	private boolean verifySource(String v) {
		Source enumName = Source.fromString(v);
		if (enumName == null) {
			return false;
		}
		return true;
	}

	private ResponseModel invalidParamResponse(KeyEnum keyEnum, String value) {
		logger.error("Invalid value for parameter:" + KeyEnum.getValue(keyEnum));
		ErrorModel errorModel = new ErrorModel(
				HttpResponseStatus.BAD_REQUEST.getCode(),
				"Invalid value for " + KeyEnum.getValue(keyEnum)
						+ " :" + value);
		String error = gson.toJson(errorModel);
		ResponseModel responseModel = new ResponseModel(error,
				HttpResponseStatus.BAD_REQUEST, "application/json");
		return responseModel;
	}

	private boolean verifyAppleProductLine(String v) {
		AppleProductLine enumName = AppleProductLine.fromString(v);
		if (enumName == null) {
			return false;
		}

		return true;
	}

	private void writeResponse(ResponseModel responseModel, MessageEvent e) {

		// Build the response object.
		HttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_1,
				responseModel.getResponseStatus());
		response.setContent(ChannelBuffers.copiedBuffer(
				responseModel.getResponse(), CharsetUtil.UTF_8));
		response.setHeader(HttpHeaders.Names.CONTENT_TYPE,
				responseModel.getContentType());
		response.setHeader(HttpHeaders.Names.ACCESS_CONTROL_ALLOW_ORIGIN, "*");
		response.setHeader(HttpHeaders.Names.CONTENT_LENGTH, response
				.getContent().readableBytes());
		response.setHeader(HttpHeaders.Names.CONNECTION,
				HttpHeaders.Values.KEEP_ALIVE);
		logger.info("Response:" + responseModel.getResponse() + " Status:"
				+ responseModel.getResponseStatus().toString()
				+ " Content Type:"
				+ response.getHeader(HttpHeaders.Names.CONTENT_TYPE));

		// Write the response.
		ChannelFuture future = e.getChannel().write(response);

		future.addListener(ChannelFutureListener.CLOSE);

	}

	private Map<String, List<String>> parseGetRequestParams(String reqStr) {
		// Parse the request parameters
		QueryStringDecoder queryStringDecoder = new QueryStringDecoder(reqStr);
		Map<String, List<String>> params = queryStringDecoder.getParameters();
		Map<String, List<String>> cleanParams = new HashMap<String, List<String>>();
		Iterator<Map.Entry<String, List<String>>> itr = params.entrySet()
				.iterator();
		while (itr.hasNext()) {
			Map.Entry<String, List<String>> entry = itr.next();
			String key = entry.getKey();
			List<String> value = entry.getValue();
			String newKey = key.replace("[]", "");
			cleanParams.put(newKey, value);
		}
		return cleanParams;
	}

}
