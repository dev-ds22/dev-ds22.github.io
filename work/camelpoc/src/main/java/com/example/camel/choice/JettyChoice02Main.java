package com.example.camel.choice;

import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.model.ModelCamelContext;

import lombok.extern.slf4j.Slf4j;

/**
 * 호출되는 request body 내용에 따른 분기 테스트
 * 
 * http://127.0.0.1:8088/choiceRoute 
 * 
 * 호출 시 body 안에 test1 이 포함되었을 경우 Process02 처리, 
 * body 안에 test2 가 포함되었을 경우 Process03 처리, 그외는 Process04 가 처리
 */
@Slf4j
public class JettyChoice02Main {

	@SuppressWarnings("resource")
	public static void main(String[] args) throws Exception {
		log.info("===== JettyChoice02Main Start...=====");	

		ModelCamelContext camelContext = new DefaultCamelContext();
		camelContext.start();
		camelContext.addRoutes(new RouteJettyChoice02());
		
		synchronized (JettyChoice02Main.class) {
			JettyChoice02Main.class.wait();
		}
	}

}
