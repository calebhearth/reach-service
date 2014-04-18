package com.tapjoy.reach.params;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class GeoCombinationGenerator {

	public Set<String> getGeoCombinations(Map<String, List<String>> params) {
		Set<String> keys = new TreeSet<String>();

		Set<String> continents = new TreeSet<String>();
		if (params.get(KeyEnum.getValue(KeyEnum.geoip_continent)) != null) {
			continents.addAll(params.get(KeyEnum
					.getValue(KeyEnum.geoip_continent)));
		}

		Set<String> countries = new TreeSet<String>();
		if (params.get(KeyEnum.getValue(KeyEnum.geoip_country)) != null) {
			countries
					.addAll(params.get(KeyEnum.getValue(KeyEnum.geoip_country)));
		}

		Set<String> regions = new TreeSet<String>();
		if (params.get(KeyEnum.getValue(KeyEnum.geoip_region)) != null) {
			regions.addAll(params.get(KeyEnum.getValue(KeyEnum.geoip_region)));
		}

		for (String region : regions) {
			String country = RegionCountryMap.getInstance().getCountry(region);
			String continent = CountryContinentMap.getInstance().getContinent(
					country);
			String key = continent + "-" + country + "-" + region;
			keys.add(key);
			continents.remove(continent);
			countries.remove(country);
		}

		for (String country : countries) {
			String continent = CountryContinentMap.getInstance().getContinent(
					country);
			String key = continent + "-" + country + "-$";
			keys.add(key);
			continents.remove(continent);
		}

		for (String continent : continents) {
			String key = continent + "-$-$";
			keys.add(key);
		}

		if (keys.size() == 0) {
			keys.add("$-$-$");
		}

		return keys;
	}

}
