package com.example.camel.direct;

import org.apache.camel.builder.RouteBuilder;

public class RouteDirectC extends RouteBuilder {

	/**
     * 테스트용 Logging Route - direct:directRouteC
     */
	@Override
	public void configure() throws Exception {
		from("direct:directRouteC")
		.log("DirectRoute-C process Start...")  
		.to("log:RouteDirectC?showExchangeId=true&level=INFO&showBody=true");
	}

}
