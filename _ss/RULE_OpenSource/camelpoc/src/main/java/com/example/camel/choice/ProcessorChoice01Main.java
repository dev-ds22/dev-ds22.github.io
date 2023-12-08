package com.example.camel.choice;

import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.model.ModelCamelContext;

import lombok.extern.slf4j.Slf4j;

/**
 * 멀티URL 처리 - 호출 URL에 따른 처리분기 태스트
 * 
 * http://127.0.0.1:8083/testRouteA -> Response Process-A Contents.
 * http://127.0.0.1:8083/testRouteB -> Response Process-B Contents.
 * 
 */
@Slf4j
public class ProcessorChoice01Main {

	@SuppressWarnings("resource")
	public static void main(String[] args) throws Exception {
		log.info("===== ProcessorChoice01Main Start...=====");
		ModelCamelContext camelContext = new DefaultCamelContext();
		camelContext.start();
		camelContext.addRoutes(new RouteProcessorChoice01());

		synchronized (ProcessorChoice01Main.class) {
			ProcessorChoice01Main.class.wait();
		}
	}

}
