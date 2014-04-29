package com.tapjoy.reach.handler;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import org.jboss.netty.util.CharsetUtil;

import com.tapjoy.reach.config.OverallConfig;
import com.tapjoy.reach.counts.ReachCountsController;
import com.tapjoy.reach.paramslist.ParamsController;
import com.tapjoy.reach.service.ResponseModel;

public class HttpReachRequestHandler extends SimpleChannelUpstreamHandler {

	private Logger logger = Logger.getLogger(HttpReachRequestHandler.class);
	
	private static Pattern uriPattern = Pattern.compile("/api/v1/([^/]+)/*");

	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) {
		try{
		HttpRequest request = (HttpRequest) e.getMessage();
		String reqStr = request.getUri();
		if (!StringUtils.startsWithIgnoreCase(reqStr, "/health")) {
			logger.info(reqStr);
		}
		if (StringUtils.startsWithIgnoreCase(reqStr, "/health")) {
			reqStr = OverallConfig.healthCheck;
		}
		Matcher matcher = uriPattern.matcher(reqStr);
		ResponseModel results = null;
		if(!matcher.find()){
			/*ResponseModel model = new ResponseModel("Invalid URL", HttpResponseStatus.BAD_REQUEST, "application/json");
			writeResponse(model, e);
			return;*/
			ReachCountsController reachCountsController = new ReachCountsController();
			results = reachCountsController.getResults(request);
		
		}
		else{
		String path = matcher.group(1);
		
		/*if(path.equalsIgnoreCase("reach_count")){
			ReachCountsController reachCountsController = new ReachCountsController();
			results = reachCountsController.getResults(request);
		}*/
		
		if(path.equalsIgnoreCase("params")){
			ParamsController paramsController = new ParamsController();
			results = paramsController.getResults(request);
		}
		else{
			ReachCountsController reachCountsController = new ReachCountsController();
			results = reachCountsController.getResults(request);
		}
		/*else{
			ResponseModel model = new ResponseModel("Invalid PATH", HttpResponseStatus.BAD_REQUEST, "application/json");
			writeResponse(model, e);
			return;
		}*/
		
		}
		writeResponse(results, e);
		}
		catch(Exception ex){
			logger.error("error",ex);
			ResponseModel model = new ResponseModel("Unknown error", HttpResponseStatus.BAD_REQUEST, "application/json");
			writeResponse(model, e);
			
		}
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

}
