package com.tapjoy.reach.helper;

import java.util.List;
import java.util.Map;

import com.tapjoy.reach.service.ResponseModel;

public interface Helper {
	
	public ResponseModel getResult(Map<String, List<String>> params);

}
