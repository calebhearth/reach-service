package com.tapjoy.reach.handler;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
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

import com.tapjoy.reach.config.OverallConfig;
import com.tapjoy.reach.counts.CountsHelper;
import com.tapjoy.reach.helper.Helper;
import com.tapjoy.reach.service.ResponseModel;

public class HttpReachRequestHandler extends SimpleChannelUpstreamHandler {

	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) {

		HttpRequest request = (HttpRequest) e.getMessage();
		String reqStr = request.getUri();
		if(StringUtils.startsWithIgnoreCase(reqStr, "/health")){
			reqStr = OverallConfig.healthCheck;
		}
		
		Map<String, List<String>> params = parseReq(reqStr);
		if (params == null || params.size() == 0) {
			ResponseModel responseModel = new ResponseModel("Bad parameters",
					HttpResponseStatus.BAD_REQUEST, "application/json");
			writeResponse(responseModel,e);
			return;
		}
		Helper countsHelper = new CountsHelper();
		ResponseModel results = countsHelper.getResult(params);
		writeResponse(results,e);
	}

	private void writeResponse(ResponseModel responseModel, MessageEvent e) {

		// Build the response object.
		HttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_1,
				responseModel.getResponseStatus());
		response.setContent(ChannelBuffers.copiedBuffer(
				responseModel.getResponse(), CharsetUtil.UTF_8));
		response.setHeader(HttpHeaders.Names.CONTENT_TYPE,
				responseModel.getContentType());

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
		System.out.println(cleanParams);
		return cleanParams;
	}

}
