package com.samsung.airsol.cnx.integration.poc.direct;

import org.apache.camel.builder.RouteBuilder;

public class RouteDirectB extends RouteBuilder {

	/**
     * 테스트용 Logging Route - direct:directRouteB
     */
	@Override
	public void configure() throws Exception {
		from("direct:directRouteB")
		.log("[Integration Hub Lib.] - Process#DirectRoute-B process Start...")  
		// .setBody(simple("${date:now:yyyy-MM-dd'T'HH:mm:ssZ}"))
		.to("log:RouteDirectB?showExchangeId=true&level=INFO&showBody=true");

	}

}
