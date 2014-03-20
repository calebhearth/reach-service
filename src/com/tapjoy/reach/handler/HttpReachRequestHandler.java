package com.tapjoy.reach.handler;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.netty.handler.codec.http.HttpVersion;
import org.jboss.netty.handler.codec.http.QueryStringDecoder;
import org.jboss.netty.util.CharsetUtil;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tapjoy.reach.config.OverallConfig;
import com.tapjoy.reach.counts.CountsHelper;
import com.tapjoy.reach.helper.Helper;
import com.tapjoy.reach.params.AppleProductLine;
import com.tapjoy.reach.params.Continents;
import com.tapjoy.reach.params.Countries;
import com.tapjoy.reach.params.KeyEnum;
import com.tapjoy.reach.params.Language;
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
		/*ChannelBuffer content = request.getContent();
		System.out.println(content);
		if(content.readable()){
			System.out.println(content.toString(CharsetUtil.UTF_8));
		}
		HttpPostRequestDecoder decoder = null;
		try {
			decoder = new HttpPostRequestDecoder(new DefaultHttpDataFactory(false), request);
		} catch (ErrorDataDecoderException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		} catch (IncompatibleDataDecoderException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		try {
			List<InterfaceHttpData> datas = decoder.getBodyHttpDatas("person");
			System.out.println(datas);
			for(InterfaceHttpData d:datas){
				if(d instanceof MemoryAttribute){
					MemoryAttribute m = (MemoryAttribute) d;
					System.out.println(m.getValue());
				}
				System.out.println(d.getName());
			}
		} catch (NotEnoughDataDecoderException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}

		  InterfaceHttpData data = null;
		try {
			data = decoder.getBodyHttpData("platform");
		} catch (NotEnoughDataDecoderException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		  if (data.getHttpDataType() == HttpDataType.Attribute) {
		     Attribute attribute = (Attribute) data;
		     String value = null;
			try {
				value = attribute.getValue();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		     System.out.println("platform :" + value);
		  }
		  
		  try {
				data = decoder.getBodyHttpData("platform");
			} catch (NotEnoughDataDecoderException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			  if (data.getHttpDataType() == HttpDataType.Attribute) {
			     Attribute attribute = (Attribute) data;
			     String value = null;
				try {
					value = attribute.getValue();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			     System.out.println("platform :" + value);
			  }*/
		String reqStr = request.getUri();
		if (!StringUtils.startsWithIgnoreCase(reqStr, "/health")) {
			logger.info(reqStr);
		}
		if (StringUtils.startsWithIgnoreCase(reqStr, "/health")) {
			reqStr = OverallConfig.healthCheck;
		}

		Map<String, List<String>> params = parseReq(reqStr);
		if (params == null || params.size() == 0) {
			logger.error("No params");
			ErrorModel errorModel = new ErrorModel(HttpResponseStatus.BAD_REQUEST.getCode(), "No params");
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

		List<String> sources = params.get(KeyEnum.getValue(KeyEnum.source));
		/*if (sources == null) {
			logger.error("Source not specified");
			ErrorModel errorModel = new ErrorModel(HttpResponseStatus.BAD_REQUEST.getCode(), "Missing parameter:sources");
			String error = gson.toJson(errorModel);
			ResponseModel responseModel = new ResponseModel(
					error, HttpResponseStatus.BAD_REQUEST,
					"application/json");
			writeResponse(responseModel, e);
			return;
		}*/

		for (Entry<String, List<String>> entry : params.entrySet()) {
			String key = entry.getKey();
			KeyEnum keyEnum = KeyEnum.fromString(key);
			List<String> values = entry.getValue();
			boolean check = false;
			switch (keyEnum) {
			case apple_product_line:
				check = verifyAppleProductLine(values);
				if (!check) {
					writeResponse(invalidParamResponse(keyEnum), e);
					return;
				}
				break;
			case device_os_version:
				check = verifyDeviceOsVersion(values);
				if (!check) {
					writeResponse(invalidParamResponse(keyEnum), e);
					return;
				}
				break;
			case geoip_continent:
				check = verifyContinent(values);
				if (!check) {
					writeResponse(invalidParamResponse(keyEnum), e);
					return;
				}
				break;
			case geoip_country:
				check = verifyCountry(values);
				if (!check) {
					writeResponse(invalidParamResponse(keyEnum), e);
					return;
				}
				break;
			case geoip_region:
				check = verifyRegion(values);
				if (!check) {
					writeResponse(invalidParamResponse(keyEnum), e);
					return;
				}
				break;
			case language:
				check = verifyLanguage(values);
				if (!check) {
					writeResponse(invalidParamResponse(keyEnum), e);
					return;
				}
				break;
			case persona_name:
				check = verifyPersonaName(values);
				if (!check) {
					writeResponse(invalidParamResponse(keyEnum), e);
					return;
				}
				break;
			case platform:
				check = verifyPlatform(values);
				if (!check) {
					writeResponse(invalidParamResponse(keyEnum), e);
					return;
				}
				break;
			case source:
				check = verifySource(values);
				if (!check) {
					writeResponse(invalidParamResponse(keyEnum), e);
					return;
				}
				break;
			default:
				writeResponse(invalidParamResponse(keyEnum), e);
				return;

			}
		}

		//modifyLanguage(params);
		//modifyPlatform(params);

		Helper countsHelper = new CountsHelper();
		ResponseModel results = countsHelper.getResult(params);
		writeResponse(results, e);
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

	private boolean verifyDeviceOsVersion(List<String> values) {
		return true;
	}

	private boolean verifyContinent(List<String> values) {
		for (String v : values) {
			if (!Continents.hasValue(v)) {
				return false;
			}
		}
		return true;
	}

	private boolean verifyCountry(List<String> values) {
		for (String v : values) {
			if (!Countries.hasValue(v)) {
				return false;
			}
		}
		return true;
	}

	private boolean verifyRegion(List<String> values) {
		for (String v : values) {
			if (!States.hasValue(v)) {
				return false;
			}
		}
		return true;
	}

	private boolean verifyLanguage(List<String> values) {
		for (String v : values) {
			Language enumName = Language.fromString(v);
			if (enumName == null) {
				return false;
			}
		}
		return true;
	}

	private boolean verifyPersonaName(List<String> values) {
		// TODO Auto-generated method stub
		return true;
	}

	private boolean verifyPlatform(List<String> values) {
		for (String v : values) {
			Platform enumName = Platform.fromString(v);
			if (enumName == null) {
				return false;
			}
		}
		return true;
	}

	private boolean verifySource(List<String> values) {
		for (String v : values) {
			Source enumName = Source.fromString(v);
			if (enumName == null) {
				return false;
			}
		}
		return true;
	}

	private ResponseModel invalidParamResponse(KeyEnum keyEnum) {
		logger.error("Invalid value for parameter:" + KeyEnum.getValue(keyEnum));
		ErrorModel errorModel = new ErrorModel(HttpResponseStatus.BAD_REQUEST.getCode(), "Invalid value for parameter:" + KeyEnum.getValue(keyEnum));
		String error = gson.toJson(errorModel);
		ResponseModel responseModel = new ResponseModel(
				 error,
				HttpResponseStatus.BAD_REQUEST, "application/json");
		return responseModel;
	}

	private boolean verifyAppleProductLine(List<String> values) {
		for (String v : values) {
			AppleProductLine enumName = AppleProductLine.fromString(v);
			if (enumName == null) {
				return false;
			}
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

	private Map<String, List<String>> parseReq(String reqStr) {
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
