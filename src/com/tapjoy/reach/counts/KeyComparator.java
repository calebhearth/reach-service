package com.tapjoy.reach.counts;

import java.util.Comparator;
import java.util.List;
import java.util.Map.Entry;

public class KeyComparator implements Comparator<Entry<String, List<String>>> {

	@Override
	public int compare(Entry<String, List<String>> o1,
			Entry<String, List<String>> o2) {
		KeyEnum key1 = KeyEnum.fromString(o1.getKey());
		KeyEnum key2 = KeyEnum.fromString(o2.getKey());
		return key1.ordinal() - key2.ordinal();
	}

}
