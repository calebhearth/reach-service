package com.tapjoy.reach.paramslist;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jboss.netty.handler.codec.http.HttpResponseStatus;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tapjoy.reach.params.AppleProductLine;
import com.tapjoy.reach.params.Continents;
import com.tapjoy.reach.params.Countries;
import com.tapjoy.reach.params.CountryContinentMap;
import com.tapjoy.reach.params.KeyEnum;
import com.tapjoy.reach.params.Language;
import com.tapjoy.reach.params.Platform;
import com.tapjoy.reach.params.RegionCountryMap;
import com.tapjoy.reach.params.Source;
import com.tapjoy.reach.params.States;
import com.tapjoy.reach.service.ResponseModel;

public class ParamsHelper {

	private String path;
	private ParamsApiEnum param;
	private static Gson gson = new GsonBuilder().create();

	public ParamsHelper(String path, ParamsApiEnum param) {
		this.path = path;
		this.param = param;
	}

	public ResponseModel getResult() {
		switch (param) {
		case apple_product_lines:
			return getAppleProductLines();
		case geoip_continents:
			return getContinents();
		case geoip_countries:
			return getCountries();
		case languages:
			return getLanguages();
		case os_versions:
			return getOsVersions();
		case platforms:
			return getPlatforms();
		case regions:
			return getRegions();
		case sources:
			return getSources();
		case personas:
			return getPersonas();
		default:
			break;

		}
		return null;
	}

	private ResponseModel getAppleProductLines() {
		List<ParamModel> l = new ArrayList<ParamModel>();
		AppleProductLine[] enums = AppleProductLine.values();
		for (int i = 0; i < enums.length; i++) {
			ParamModel p = new ParamModel(i + 1, enums[i].name());
			l.add(p);
		}
		String json = gson.toJson(l);
		ResponseModel responseModel = new ResponseModel(json,
				HttpResponseStatus.OK, "application/json");
		return responseModel;
	}

	private ResponseModel getContinents() {

		String json = "";

		Pattern pattern = Pattern
				.compile("/api/v1/params/geoip_continents/(\\d+)/geoip_countries/*");
		Matcher matcher = pattern.matcher(path);

		if (matcher.find()) {

			int id = Integer.parseInt(matcher.group(1));

			if (id < 1 || id > Continents.values().length) {
				ResponseModel responseModel = new ResponseModel("Invalid id",
						HttpResponseStatus.BAD_REQUEST, "application/json");
				return responseModel;
			}

			String continent = Continents.values()[id - 1].name();
			List<DependentGeoModel> l = new ArrayList<ParamsHelper.DependentGeoModel>();
			Set<Object> countries = CountryContinentMap.getInstance().getKeys();
			for (Object o : countries) {
				String country = (String) o;
				String c = CountryContinentMap.getInstance().getContinent(
						country);
				if (c.equalsIgnoreCase(continent)) {
					Countries e = Countries.valueOf(country.toUpperCase());
					DependentGeoModel p = new DependentGeoModel(
							e.ordinal() + 1, e.name(), e.getKeyword(),
							KeyEnum.geoip_continent.name(), id);
					l.add(p);
					json = gson.toJson(l);
				}
			}

		}

		else {
			List<GeoModel> l = new ArrayList<GeoModel>();
			Continents[] enums = Continents.values();
			for (int i = 0; i < enums.length; i++) {
				GeoModel p = new GeoModel(i + 1, enums[i].name(),
						enums[i].getKeyword());
				l.add(p);
			}
			json = gson.toJson(l);

		}

		ResponseModel responseModel = new ResponseModel(json,
				HttpResponseStatus.OK, "application/json");
		return responseModel;
	}

	private ResponseModel getCountries() {

		List<DependentGeoModel> l = new ArrayList<ParamsHelper.DependentGeoModel>();

		Pattern pattern = Pattern
				.compile("/api/v1/params/geoip_countries/(\\d+)/regions/*");
		Matcher matcher = pattern.matcher(path);

		if (matcher.find()) {
			int id = Integer.parseInt(matcher.group(1));

			if (id < 1 || id > Countries.values().length) {
				ResponseModel responseModel = new ResponseModel("Invalid id",
						HttpResponseStatus.BAD_REQUEST, "application/json");
				return responseModel;
			}

			String country = Countries.values()[id - 1].name();

			Set<Object> regions = RegionCountryMap.getInstance().getKeys();
			for (Object o : regions) {
				String region = (String) o;
				String c = RegionCountryMap.getInstance().getCountry(region);
				if (c.equalsIgnoreCase(country)) {
					States e = States.valueOf(region.toUpperCase());
					DependentGeoModel p = new DependentGeoModel(
							e.ordinal() + 1, e.name(), e.getKeyword(),
							KeyEnum.geoip_country.name(), id);
					l.add(p);
				}
			}

		}

		else {
			Countries[] enums = Countries.values();
			for (int i = 0; i < enums.length; i++) {
				String continent = CountryContinentMap.getInstance()
						.getContinent(enums[i].name());
				int continentId = Continents.getEnum(continent).ordinal() + 1;
				DependentGeoModel p = new DependentGeoModel(i + 1,
						enums[i].name(), enums[i].getKeyword(),
						KeyEnum.geoip_continent.name(), continentId);
				l.add(p);
			}

		}

		String json = gson.toJson(l);
		ResponseModel responseModel = new ResponseModel(json,
				HttpResponseStatus.OK, "application/json");
		return responseModel;
	}

	private ResponseModel getLanguages() {
		List<ParamModel> l = new ArrayList<ParamModel>();
		Language[] enums = Language.values();
		for (int i = 0; i < enums.length; i++) {
			ParamModel p = new ParamModel(i + 1, enums[i].getKeyword());
			l.add(p);
		}
		String json = gson.toJson(l);
		ResponseModel responseModel = new ResponseModel(json,
				HttpResponseStatus.OK, "application/json");
		return responseModel;
	}

	private ResponseModel getPlatforms() {
		List<ParamModel> l = new ArrayList<ParamModel>();
		Platform[] enums = Platform.values();
		for (int i = 0; i < enums.length; i++) {
			ParamModel p = new ParamModel(i + 1, enums[i].name());
			l.add(p);
		}
		String json = gson.toJson(l);
		ResponseModel responseModel = new ResponseModel(json,
				HttpResponseStatus.OK, "application/json");
		return responseModel;
	}

	private ResponseModel getOsVersions() {
		// TODO Auto-generated method stub
		return null;
	}

	private ResponseModel getRegions() {
		List<DependentGeoModel> l = new ArrayList<DependentGeoModel>();
		States[] enums = States.values();
		for (int i = 0; i < enums.length; i++) {
			String country = RegionCountryMap.getInstance().getCountry(
					enums[i].name());
			try{
			int id = Countries.getEnum(country).ordinal() + 1;
			DependentGeoModel p = new DependentGeoModel(i + 1, enums[i].name(),
					enums[i].getKeyword(), KeyEnum.geoip_country.name(), id);
			l.add(p);
			}catch(Exception ex){
				ex.printStackTrace();
			}
		}
		String json = gson.toJson(l);
		ResponseModel responseModel = new ResponseModel(json,
				HttpResponseStatus.OK, "application/json");
		return responseModel;
	}

	private ResponseModel getSources() {
		List<ParamModel> l = new ArrayList<ParamModel>();
		Source[] enums = Source.values();
		for (int i = 0; i < enums.length; i++) {
			ParamModel p = new ParamModel(i + 1, enums[i].name());
			l.add(p);
		}
		String json = gson.toJson(l);
		ResponseModel responseModel = new ResponseModel(json,
				HttpResponseStatus.OK, "application/json");
		return responseModel;
	}

	private ResponseModel getPersonas() {
		// TODO Auto-generated method stub
		return null;
	}

	private class DependentGeoModel {
		public int id;
		public String name;
		public String properName;
		public String dependentLocationType;
		public int dependentLocationId;

		public DependentGeoModel(int id, String name, String properName,
				String dependentLocationType, int dependentLocationId) {
			this.id = id;
			this.name = name;
			this.properName = properName;
			this.dependentLocationType = dependentLocationType;
			this.dependentLocationId = dependentLocationId;
		}

	}

	private class GeoModel {

		public int id;
		public String name;
		public String properName;

		public GeoModel(int id, String name, String properName) {
			this.id = id;
			this.name = name;
			this.properName = properName;
		}

	}

}
