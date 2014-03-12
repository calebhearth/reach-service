package com.tapjoy.reach.helper;

import java.util.List;
import java.util.Map;

public interface Helper<T> {
	
	public T getResult(Map<String, List<String>> params);

}
