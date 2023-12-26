package com.samsung.airsol.cnx.integration.poc.choice;

import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.model.ModelCamelContext;

import lombok.extern.slf4j.Slf4j;

/**
 * 호출되는 request body 내용에 따른 분기 테스트 #2
 * 
 * http://127.0.0.1:8088/choiceRoute 
 * 
 * 호출 시 body 안의 data.routeType 의 값이 test1 일 경우 Process02 처리, 
 * data.routeType 의 값이 test2 일 경우 Process03 처리, 그외는 Process04 가 처리
 */
@Slf4j
public class JettyChoice01Main {

	@SuppressWarnings("resource")
	public static void main(String[] args) throws Exception {
		log.info("===== JettyChoice01Main Start...=====");	

		ModelCamelContext camelContext = new DefaultCamelContext();
		camelContext.start();
		camelContext.addRoutes(new RouteJettyChoice01());
		
		synchronized (JettyChoice01Main.class) {
			JettyChoice01Main.class.wait();
		}
	}

}
