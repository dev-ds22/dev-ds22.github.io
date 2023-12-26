package com.samsung.airsol.cnx.integration.poc.choice;

import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.model.ModelCamelContext;

import lombok.extern.slf4j.Slf4j;

/**
 * http://127.0.0.1:8088/choiceRoute 를 호출 시 body 의 $.data.routeType 값에 따른 분기 확인
 * 
 * $.data.routeType = 'test1' 일 경우 --> Response Test Processor-#1 Contents
 * $.data.routeType = 'test2' 일 경우 --> Response Test Processor-#2 Contents
 * 그 외의 경우 --> Response Test Processor-#3 Contents
 */
@Slf4j
public class ProcessorChoice02Main {

	@SuppressWarnings("resource")
	public static void main(String[] args) throws Exception {
		log.info("===== ProcessorChoice02Main Start...=====");		
		ModelCamelContext camelContext = new DefaultCamelContext();
		camelContext.start();
		camelContext.addRoutes(new RouteProcessorChoice02());

		synchronized (ProcessorChoice02Main.class) {
			ProcessorChoice02Main.class.wait();
		}
	}

}
