package com.samsung.airsol.cnx.integration.poc.choice;

import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.model.ModelCamelContext;

/**
 * 파일명에 따른 분기 테스트
 */
public class FileChoice01Main {

	@SuppressWarnings("resource")
	public static void main(String[] args) throws Exception {

		ModelCamelContext camelContext = new DefaultCamelContext();
		camelContext.start();
		//camelContext.addRoutes(new ChoiceCamelRouteBuilder());
		camelContext.addRoutes(new RouteFileChoice01());
		
		synchronized (FileChoice01Main.class) {
			FileChoice01Main.class.wait();
		}
	}

}
