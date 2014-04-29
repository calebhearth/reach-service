package com.tapjoy.reach.paramslist;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tapjoy.reach.params.AppleProductLine;
import com.tapjoy.reach.params.Continents;
import com.tapjoy.reach.params.Countries;
import com.tapjoy.reach.utils.CountryContinentMap;
import com.tapjoy.reach.utils.DeviceOsVersionMap;
import com.tapjoy.reach.utils.ManufacturerSizeMap;
import com.tapjoy.reach.utils.ModelManufacturerMap;
import com.tapjoy.reach.utils.RegionCountryMap;
import com.tapjoy.reach.params.DeviceManufacturer;
import com.tapjoy.reach.params.DeviceModel;
import com.tapjoy.reach.params.DeviceOsVersion;
import com.tapjoy.reach.params.DeviceSize;
import com.tapjoy.reach.params.KeyEnum;
import com.tapjoy.reach.params.Language;
import com.tapjoy.reach.params.Personas;
import com.tapjoy.reach.params.Platform;
import com.tapjoy.reach.params.Source;
import com.tapjoy.reach.params.States;
import com.tapjoy.reach.service.ResponseModel;

public class ParamsHelper {

	private String path;
	private KeyEnum param;
	private static Gson gson = new GsonBuilder().create();

	public ParamsHelper(String path, KeyEnum param) {
		this.path = path;
		this.param = param;
	}

	public ResponseModel getResult() {
		switch (param) {
		case apple_product_line:
			return getAppleProductLines();
		case geoip_continent:
			return getContinents();
		case geoip_country:
			return getCountries();
		case language:
			return getLanguages();
		case device_os_version:
			return getOsVersions();
		case platform:
			return getPlatforms();
		case geoip_region:
			return getRegions();
		case source:
			return getSources();
		case persona_name:
			return getPersonas();
		case device_manufacturer:
			return getDeviceManufacturers();
		case device_model:
			return getDeviceModels();
		case device_size:
			return getDeviceSizes();
		default:
			break;

		}
		return null;
	}

	private ResponseModel getAppleProductLines() {
		List<ParamModel> l = new ArrayList<ParamModel>();
		AppleProductLine[] enums = AppleProductLine.values();
		for (int i = 0; i < enums.length; i++) {
			ParamModel p = new ParamModel(i + 1, enums[i].name(), null, null);
			l.add(p);
		}
		Collections.sort(l);
		String json = gson.toJson(l);
		ResponseModel responseModel = new ResponseModel(json,
				HttpResponseStatus.OK, "application/json");
		return responseModel;
	}

	private ResponseModel getContinents() {

		String json = "";

		Pattern pattern = Pattern
				.compile("/api/v1/params/geoip_continent/(\\d+)/geoip_country/*");
		Matcher matcher = pattern.matcher(path);

		if (matcher.find()) {

			int id = Integer.parseInt(matcher.group(1));

			if (id < 1 || id > Continents.values().length) {
				ResponseModel responseModel = new ResponseModel("Invalid id",
						HttpResponseStatus.BAD_REQUEST, "application/json");
				return responseModel;
			}

			String continent = Continents.values()[id - 1].name();
			List<ParamModel> l = new ArrayList<ParamModel>();
			Set<Object> countries = CountryContinentMap.getInstance().getKeys();
			for (Object o : countries) {
				String country = (String) o;
				String c = CountryContinentMap.getInstance().getContinent(
						country);
				if (c.equalsIgnoreCase(continent)) {
					Countries e = Countries.valueOf(country.toUpperCase());
					DependentModel dp = new DependentModel(
							KeyEnum.geoip_continent.name(), id);
					List<DependentModel> dl = new ArrayList<DependentModel>();
					dl.add(dp);
					ParamModel p = new ParamModel(e.ordinal() + 1, e.name(),
							e.getKeyword(), dl);
					l.add(p);
				}
			}

			Collections.sort(l);
			json = gson.toJson(l);

		}

		else {
			List<ParamModel> l = new ArrayList<ParamModel>();
			Continents[] enums = Continents.values();
			for (int i = 0; i < enums.length; i++) {
				ParamModel p = new ParamModel(i + 1, enums[i].name(),
						enums[i].getKeyword(), null);
				l.add(p);
			}
			Collections.sort(l);
			json = gson.toJson(l);

		}

		ResponseModel responseModel = new ResponseModel(json,
				HttpResponseStatus.OK, "application/json");
		return responseModel;
	}

	private ResponseModel getCountries() {

		List<ParamModel> l = new ArrayList<ParamModel>();

		Pattern pattern = Pattern
				.compile("/api/v1/params/geoip_country/(\\d+)/regions/*");
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
					DependentModel dp = new DependentModel(
							KeyEnum.geoip_country.name(), id);
					List<DependentModel> dl = new ArrayList<DependentModel>();
					dl.add(dp);
					ParamModel p = new ParamModel(e.ordinal() + 1, e.name(),
							e.getKeyword(), dl);
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
				DependentModel dp = new DependentModel(
						KeyEnum.geoip_continent.name(), continentId);
				List<DependentModel> dl = new ArrayList<DependentModel>();
				dl.add(dp);
				ParamModel p = new ParamModel(i + 1, enums[i].name(),
						enums[i].getKeyword(), dl);
				l.add(p);
			}

		}

		Collections.sort(l);
		String json = gson.toJson(l);
		ResponseModel responseModel = new ResponseModel(json,
				HttpResponseStatus.OK, "application/json");
		return responseModel;
	}

	private ResponseModel getLanguages() {
		List<ParamModel> l = new ArrayList<ParamModel>();
		Language[] enums = Language.values();
		for (int i = 0; i < enums.length; i++) {
			ParamModel p = new ParamModel(i + 1, enums[i].getKeyword(), enums[i].toString(),
					null);
			l.add(p);
		}

		Collections.sort(l);
		String json = gson.toJson(l);
		ResponseModel responseModel = new ResponseModel(json,
				HttpResponseStatus.OK, "application/json");
		return responseModel;
	}

	private ResponseModel getPlatforms() {

		Pattern pattern = Pattern
				.compile("/api/v1/params/platform/(\\d+)/device_os_version/*");
		Matcher matcher = pattern.matcher(path);

		String json = "";

		if (matcher.find()) {
			int id = Integer.parseInt(matcher.group(1));

			if (id < 1 || id > Platform.values().length) {
				ResponseModel responseModel = new ResponseModel("Invalid id",
						HttpResponseStatus.BAD_REQUEST, "application/json");
				return responseModel;
			}

			List<ParamModel> l = new ArrayList<ParamModel>();
			DeviceOsVersion[] enums = DeviceOsVersion.values();
			for (int i = 0; i < enums.length; i++) {
				DeviceOsVersion osv = enums[i];
				String platforms = DeviceOsVersionMap.getInstance()
						.getPlatforms(osv.getKeyword());
				String[] parts = platforms.split(",");
				List<DependentModel> dl = new ArrayList<DependentModel>();
				for (String p : parts) {
					Platform platform = Platform.getEnum(p);
					if (platform.ordinal() + 1 == id) {
						DependentModel dp = new DependentModel(
								KeyEnum.platform.name(), platform.ordinal() + 1);
						dl.add(dp);
						ParamModel m = new ParamModel(i + 1, enums[i].getKeyword(),
								null, dl);
						l.add(m);
						break;
					}

				}
				
			}

			Collections.sort(l);
			json = gson.toJson(l);

		}

		else {
			List<ParamModel> l = new ArrayList<ParamModel>();
			Platform[] enums = Platform.values();
			for (int i = 0; i < enums.length; i++) {
				ParamModel p = new ParamModel(i + 1, enums[i].name(), null,
						null);
				l.add(p);
			}

			Collections.sort(l);
			json = gson.toJson(l);
		}

		ResponseModel responseModel = new ResponseModel(json,
				HttpResponseStatus.OK, "application/json");
		return responseModel;
	}

	private ResponseModel getOsVersions() {
		List<ParamModel> l = new ArrayList<ParamModel>();
		DeviceOsVersion[] enums = DeviceOsVersion.values();
		for (int i = 0; i < enums.length; i++) {
			DeviceOsVersion osv = enums[i];
			String platforms = DeviceOsVersionMap.getInstance().getPlatforms(
					osv.getKeyword());
			String[] parts = platforms.split(",");
			List<DependentModel> dl = new ArrayList<DependentModel>();
			for (String p : parts) {
				Platform platform = Platform.getEnum(p);
				DependentModel dp = new DependentModel(KeyEnum.platform.name(),
						platform.ordinal() + 1);
				dl.add(dp);
			}
			Collections.sort(dl);
			ParamModel p = new ParamModel(i + 1, enums[i].getKeyword(), null,
					dl);
			l.add(p);
		}

		Collections.sort(l);
		String json = gson.toJson(l);
		ResponseModel responseModel = new ResponseModel(json,
				HttpResponseStatus.OK, "application/json");
		return responseModel;
	}

	private ResponseModel getRegions() {
		List<ParamModel> l = new ArrayList<ParamModel>();
		States[] enums = States.values();
		for (int i = 0; i < enums.length; i++) {
			String country = RegionCountryMap.getInstance().getCountry(
					enums[i].name());
			try {
				int id = Countries.getEnum(country).ordinal() + 1;
				DependentModel dp = new DependentModel(
						KeyEnum.geoip_country.name(), id);
				List<DependentModel> dl = new ArrayList<DependentModel>();
				dl.add(dp);
				ParamModel p = new ParamModel(i + 1, enums[i].name(),
						enums[i].getKeyword(), dl);
				l.add(p);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		Collections.sort(l);
		String json = gson.toJson(l);
		ResponseModel responseModel = new ResponseModel(json,
				HttpResponseStatus.OK, "application/json");
		return responseModel;
	}

	private ResponseModel getSources() {
		List<ParamModel> l = new ArrayList<ParamModel>();
		Source[] enums = Source.values();
		for (int i = 0; i < enums.length; i++) {
			ParamModel p = new ParamModel(i + 1, enums[i].name(), null, null);
			l.add(p);
		}

		Collections.sort(l);
		String json = gson.toJson(l);
		ResponseModel responseModel = new ResponseModel(json,
				HttpResponseStatus.OK, "application/json");
		return responseModel;
	}

	private ResponseModel getPersonas() {
		List<ParamModel> l = new ArrayList<ParamModel>();
		Map<String, Integer> personaMap = Personas.getInstance()
				.getPersonaMap();
		for (Entry<String, Integer> entry : personaMap.entrySet()) {
			String persona = entry.getKey();
			int id = entry.getValue();
			ParamModel p = new ParamModel(id, persona, null, null);
			l.add(p);
		}

		Collections.sort(l);
		String json = gson.toJson(l);
		ResponseModel responseModel = new ResponseModel(json,
				HttpResponseStatus.OK, "application/json");
		return responseModel;
	}

	private ResponseModel getDeviceManufacturers() {
		String json = "";
		Pattern pattern = Pattern
				.compile("/api/v1/params/device_manufacturer/(\\d+)/device_model/*");
		Matcher matcher = pattern.matcher(path);

		if (matcher.find()) {
			int id = Integer.parseInt(matcher.group(1));

			if (id < 1 || id > DeviceManufacturer.values().length) {
				ResponseModel responseModel = new ResponseModel("Invalid id",
						HttpResponseStatus.BAD_REQUEST, "application/json");
				return responseModel;
			}

			DeviceManufacturer deviceManufacturer = DeviceManufacturer.values()[id - 1];
			List<ParamModel> l = new ArrayList<ParamModel>();
			Set<Object> keys = ModelManufacturerMap.getInstance().getKeys();
			for (Object o : keys) {
				String model = (String) o;
				String manufacturer = ModelManufacturerMap.getInstance()
						.getManufacturer(model);
				String[] parts = manufacturer.split(",");
				if (parts[0].equalsIgnoreCase(deviceManufacturer.name())) {
					DeviceModel deviceModel = DeviceModel.getEnum(model);
					int sId = DeviceSize.getEnum(parts[1]).ordinal() + 1;

					DependentModel dp1 = new DependentModel(
							KeyEnum.getValue(KeyEnum.device_manufacturer), id);
					DependentModel dp2 = new DependentModel(
							KeyEnum.getValue(KeyEnum.device_size), sId);
					List<DependentModel> dl = new ArrayList<DependentModel>();
					dl.add(dp1);
					dl.add(dp2);
					ParamModel p = new ParamModel(deviceModel.ordinal() + 1,
							model, null, dl);

					l.add(p);
				}

			}

			Collections.sort(l);
			json = gson.toJson(l);

		} else {
			List<ParamModel> l = new ArrayList<ParamModel>();
			DeviceManufacturer[] deviceManufacturers = DeviceManufacturer
					.values();
			for (DeviceManufacturer d : deviceManufacturers) {
				String value = ManufacturerSizeMap.getInstance().getSize(
						d.toString().toUpperCase());
				try {
					String[] sizes = value.split(",");
					List<DependentModel> dl = new ArrayList<DependentModel>();
					for (String s : sizes) {
						if (StringUtils.isBlank(s)) {
							continue;
						}
						DeviceSize ds = DeviceSize.getEnum(s);
						DependentModel dp = new DependentModel(
								KeyEnum.getValue(KeyEnum.device_size),
								ds.ordinal() + 1);
						dl.add(dp);
					}

					ParamModel m = new ParamModel(d.ordinal() + 1,
							d.toString(), null, dl);
					l.add(m);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}

			Collections.sort(l);
			json = gson.toJson(l);
		}
		ResponseModel responseModel = new ResponseModel(json,
				HttpResponseStatus.OK, "application/json");
		return responseModel;
	}

	private ResponseModel getDeviceModels() {
		List<ParamModel> l = new ArrayList<ParamModel>();

		DeviceModel[] deviceModels = DeviceModel.values();

		for (DeviceModel d : deviceModels) {
			String name = d.getKeyword().toUpperCase();
			String value = ModelManufacturerMap.getInstance().getManufacturer(
					name);
			String[] splits;
			try {
				splits = value.split(",");
				String manufacturer = splits[0];
				int mId = DeviceManufacturer.getEnum(manufacturer).ordinal() + 1;
				String size = splits[1];
				int sId = DeviceSize.getEnum(size).ordinal() + 1;
				DependentModel dp1 = new DependentModel(
						KeyEnum.getValue(KeyEnum.device_manufacturer), mId);
				DependentModel dp2 = new DependentModel(
						KeyEnum.getValue(KeyEnum.device_size), sId);
				List<DependentModel> dl = new ArrayList<DependentModel>();
				dl.add(dp1);
				dl.add(dp2);
				ParamModel m = new ParamModel(d.ordinal() + 1, name, null, dl);
				l.add(m);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		Collections.sort(l);
		String json = gson.toJson(l);
		ResponseModel responseModel = new ResponseModel(json,
				HttpResponseStatus.OK, "application/json");
		return responseModel;
	}

	private ResponseModel getDeviceSizes() {
		String json = "";
		Pattern pattern = Pattern
				.compile("/api/v1/params/device_size/(\\d+)/device_manufacturer/*");
		Matcher matcher = pattern.matcher(path);

		if (matcher.find()) {
			int id = Integer.parseInt(matcher.group(1));

			if (id < 1 || id > DeviceSize.values().length) {
				ResponseModel responseModel = new ResponseModel("Invalid id",
						HttpResponseStatus.BAD_REQUEST, "application/json");
				return responseModel;
			}

			DeviceSize deviceSize = DeviceSize.values()[id - 1];
			List<ParamModel> l = new ArrayList<ParamModel>();
			Set<Object> keys = ManufacturerSizeMap.getInstance().getKeys();
			for (Object o : keys) {
				String manufacturer = (String) o;
				String size = ManufacturerSizeMap.getInstance().getSize(
						manufacturer);
				String[] parts = size.split(",");
				for (String part : parts) {
					if (part.equalsIgnoreCase(deviceSize.name())) {
						DeviceManufacturer d = DeviceManufacturer
								.getEnum(manufacturer);
						DependentModel dp = new DependentModel(
								KeyEnum.device_size.name(), id);
						List<DependentModel> dl = new ArrayList<DependentModel>();
						dl.add(dp);
						ParamModel p = new ParamModel(d.ordinal() + 1,
								d.toString(), null, dl);
						l.add(p);
						break;
					}
				}

			}

			Collections.sort(l);
			json = gson.toJson(l);

		} else {
			List<ParamModel> l = new ArrayList<ParamModel>();
			DeviceSize[] enums = DeviceSize.values();
			for (int i = 0; i < enums.length; i++) {
				ParamModel p = new ParamModel(i + 1, enums[i].name(), null,
						null);
				l.add(p);
			}

			Collections.sort(l);
			json = gson.toJson(l);
		}

		ResponseModel responseModel = new ResponseModel(json,
				HttpResponseStatus.OK, "application/json");
		return responseModel;
	}

}
