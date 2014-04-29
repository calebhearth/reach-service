package com.tapjoy.reach.counts;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.netty.handler.codec.http.QueryStringDecoder;
import org.jboss.netty.util.CharsetUtil;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.tapjoy.reach.config.OverallConfig;
import com.tapjoy.reach.helper.Helper;
import com.tapjoy.reach.params.AppleProductLine;
import com.tapjoy.reach.params.Continents;
import com.tapjoy.reach.params.Countries;
import com.tapjoy.reach.params.DeviceManufacturer;
import com.tapjoy.reach.params.DeviceModel;
import com.tapjoy.reach.params.DeviceSize;
import com.tapjoy.reach.params.KeyEnum;
import com.tapjoy.reach.params.Language;
import com.tapjoy.reach.params.Personas;
import com.tapjoy.reach.params.Platform;
import com.tapjoy.reach.params.Source;
import com.tapjoy.reach.params.States;
import com.tapjoy.reach.service.ErrorModel;
import com.tapjoy.reach.service.ResponseModel;

public class ReachCountsController {
	
	public static Logger logger = Logger.getLogger(ReachCountsController.class);
	private static Gson gson = new GsonBuilder().create();

	public ResponseModel getResults(HttpRequest request){
		String reqStr = request.getUri();
		Map<String, List<String>> params = null;
		if(request.getMethod() == HttpMethod.GET){			
			params = parseGetRequestParams(reqStr);
		}
		else if(request.getMethod() == HttpMethod.POST){
			params = parsePostRequestParams(request);
		}
		
		if (params == null) {
			logger.error("Error while parsing params");
			ErrorModel errorModel = new ErrorModel(HttpResponseStatus.BAD_REQUEST.getCode(), "Invalid params");
			String error = gson.toJson(errorModel);
			ResponseModel responseModel = new ResponseModel(
					error, HttpResponseStatus.BAD_REQUEST,
					"application/json");
			return responseModel;
		}

		for (String key : params.keySet()) {
			KeyEnum keyEnum = KeyEnum.getEnum(key);
			if (keyEnum == null) {
				logger.error("Invalid parameter:" + key);
				ErrorModel errorModel = new ErrorModel(HttpResponseStatus.BAD_REQUEST.getCode(), "Invalid parameter:" + key);
				String error = gson.toJson(errorModel);
				ResponseModel responseModel = new ResponseModel(
						error,
						HttpResponseStatus.BAD_REQUEST, "application/json");
				return responseModel;
			}

		}

		for (Entry<String, List<String>> entry : params.entrySet()) {
			String key = entry.getKey();
			KeyEnum keyEnum = KeyEnum.getEnum(key);
			List<String> values = entry.getValue();
			boolean check = false;
			switch (keyEnum) {
			case apple_product_line:
				for (String v : values) {
					check = verifyAppleProductLine(v);
					if (!check) {
						ResponseModel responseModel = invalidParamResponse(keyEnum, v);
						return responseModel;
					}
				}

				break;
			case device_os_version:
				for (String v : values) {
					check = verifyDeviceOsVersion(v);
					if (!check) {
						ResponseModel responseModel = invalidParamResponse(keyEnum, v);
						return responseModel; 
					}
				}
				break;
			case device_manufacturer:
				for (String v : values) {
					check = verifyDeviceManufacturer(v);
					if (!check) {
						ResponseModel responseModel = invalidParamResponse(keyEnum, v);
						return responseModel;
					}
				}
				break;
			case device_model:
				for (String v : values) {
					check = verifyDeviceModel(v);
					if (!check) {
						ResponseModel responseModel = invalidParamResponse(keyEnum, v);
						return responseModel;
					}
				}
				break;
			case device_size:
				for (String v : values) {
					check = verifyDeviceSize(v);
					if (!check) {
						ResponseModel responseModel = invalidParamResponse(keyEnum, v);
						return responseModel;
					}
				}
				break;
			case geoip_continent:
				for (String v : values) {
					check = verifyContinent(v);
					if (!check) {
						ResponseModel responseModel = invalidParamResponse(keyEnum, v);
						return responseModel;
					}
				}
				break;
			case geoip_country:
				for (String v : values) {
					check = verifyCountry(v);
					if (!check) {
						ResponseModel responseModel = invalidParamResponse(keyEnum, v);
						return responseModel;
					}
				}
				break;
			case geoip_region:
				for (String v : values) {
					check = verifyRegion(v);
					if (!check) {
						ResponseModel responseModel = invalidParamResponse(keyEnum, v);
						return responseModel;
					}
				}
				break;
			case language:
				for (String v : values) {
					check = verifyLanguage(v);
					if (!check) {
						ResponseModel responseModel = invalidParamResponse(keyEnum, v);
						return responseModel;
					}
				}
				break;
			case persona_name:
				for (String v : values) {
					check = verifyPersonaName(v);
					if (!check) {
						ResponseModel responseModel = invalidParamResponse(keyEnum, v);
						return responseModel;
					}
				}
				break;
			case platform:
				for (String v : values) {
					check = verifyPlatform(v);
					if (!check) {
						ResponseModel responseModel = invalidParamResponse(keyEnum, v);
						return responseModel;
					}
				}
				break;
			case source:
				for (String v : values) {
					check = verifySource(v);
					if (!check) {
						ResponseModel responseModel = invalidParamResponse(keyEnum, v);
						return responseModel;
					}
				}
				break;
			default:
				ResponseModel responseModel = invalidParamResponse(keyEnum, "");
				return responseModel;

			}
		}

		Helper countsHelper = new CountsHelper();
		ResponseModel results = countsHelper.getResult(params);
		return results;
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
	
	private boolean verifyDeviceModel(String v) {
		DeviceModel e = DeviceModel.getEnum(v);
		if(e == null){
			return false;
		}
		return true;
	}

	private boolean verifyDeviceManufacturer(String v) {
		DeviceManufacturer e = DeviceManufacturer.getEnum(v);
		if(e == null){
			return false;
		}
		return true;
	}

	private boolean verifyDeviceSize(String v) {
		DeviceSize e = DeviceSize.getEnum(v);
		if(e == null){
			return false;
		}
		return true;
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
		if (Continents.getEnum(v) == null) {
			return false;
		}
		return true;
	}

	private boolean verifyCountry(String v) {
		if (Countries.getEnum(v) == null) {
			return false;
		}
		return true;
	}

	private boolean verifyRegion(String v) {
		if (States.getEnum(v) == null) {
			return false;
		}
		return true;
	}

	private boolean verifyLanguage(String v) {
		Language enumName = Language.getEnum(v);
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
		Platform enumName = Platform.getEnum(v);
		if (enumName == null) {
			return false;
		}
		return true;
	}

	private boolean verifySource(String v) {
		Source enumName = Source.getEnum(v);
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
		AppleProductLine enumName = AppleProductLine.getEnum(v);
		if (enumName == null) {
			return false;
		}

		return true;
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
			List<String> finalValue = new ArrayList<String>();
			for(String v : value){
				finalValue.add(v.toUpperCase());
			}
			String newKey = key.replace("[]", "");
			cleanParams.put(newKey, finalValue);
		}
		return cleanParams;
	}
}
