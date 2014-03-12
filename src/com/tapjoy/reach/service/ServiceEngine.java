package com.tapjoy.reach.service;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.handler.codec.http.HttpContentCompressor;
import org.jboss.netty.handler.codec.http.HttpRequestDecoder;
import org.jboss.netty.handler.codec.http.HttpResponseEncoder;
import org.jboss.netty.handler.execution.ExecutionHandler;
import org.jboss.netty.handler.execution.OrderedMemoryAwareThreadPoolExecutor;

import com.tapjoy.reach.config.OverallConfig;
import com.tapjoy.reach.handler.HttpReachRequestHandler;




public class ServiceEngine {
	
	private static ServiceEngine servEng;
	private static Logger logger = Logger.getLogger(ServiceEngine.class);
	
	private final ExecutionHandler executionHandler = new ExecutionHandler(
            new OrderedMemoryAwareThreadPoolExecutor(OverallConfig.NETTY_EXEC_HANDLER_THREADPOOL_SIZE, 1048576, 1048576));   //change max thread num to 40 from 16, others using acknowledged empirical values
	
	private ServiceEngine() {
		
	}
	
	
	public static ServiceEngine getInstance(){
		if (servEng == null) {
			synchronized(ServiceEngine.class){
				if (servEng == null){
					servEng = new ServiceEngine();
					servEng.initialize();
				}
			}
		}
		
		return servEng;
	}

	 
	private void initialize(){
		
		ChannelFactory factory = new NioServerSocketChannelFactory(
				Executors.newCachedThreadPool(),
				Executors.newCachedThreadPool());
		
		
		// Use this when expecting less than 1K concurrent connections
		//ChannelFactory factory = new OioServerSocketChannelFactory();

		ServerBootstrap bootstrap = new ServerBootstrap(factory);
		
		//bootstrap.setPipeline(pipeline);

		bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
			public ChannelPipeline getPipeline() {
				ChannelPipeline pipeline = Channels.pipeline();
				/* http version */
				pipeline.addLast("decoder", new HttpRequestDecoder());
				// Uncomment the following line if you don't want to handle HttpChunks.
				//pipeline.addLast("aggregator", new HttpChunkAggregator(1048576));
				pipeline.addLast("encoder", new HttpResponseEncoder());
				// Remove the following line if you don't want automatic content compression.
				pipeline.addLast("execution", executionHandler);
				pipeline.addLast("deflater", new HttpContentCompressor());
				pipeline.addLast("handler", new HttpReachRequestHandler());
				
				return pipeline;
			}
		});
		

		bootstrap.setOption("child.tcpNoDelay", true);
		bootstrap.setOption("child.keepAlive", true);

		bootstrap.bind(new InetSocketAddress(OverallConfig.REACH_PORT));
		logger.info("Server is up, ready to serve requests");
		System.out.println("Server is up, ready to serve requests");
	}
	

	// TODO - Shutdown hook for house keeping
	public void shutDown(){
		logger.info("Shutting down main server");
	}
	
}
