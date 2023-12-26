package com.samsung.airsol.cnx.integration.poc.direct;

import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.model.ModelCamelContext;

import lombok.extern.slf4j.Slf4j;

/**
 * http 호출 시 request 내용(json)에 설정된 Direct Route 실행 테스트
 * 
 * http://127.0.0.1:8090/directMain
 * 
 * request json 의 $.data.routeName 에 설정된 Route 호출
 * direct:directRouteB,direct:directRouteC 와 같이 ',' 구분자로 순서대로 해당 Route 처리 
 * 
 */
@Slf4j
public class DirectMain {

	@SuppressWarnings("resource")
	public static void main(String[] args) throws Exception {
        log.info("===== DirectMain Start...=====");		
		ModelCamelContext camelContext = new DefaultCamelContext();
		camelContext.start();

		camelContext.addRoutes(new RouteDirectA());
		camelContext.addRoutes(new RouteDirectB());
		camelContext.addRoutes(new RouteDirectC());

		synchronized (DirectMain.class) {
			DirectMain.class.wait();
		}
	}

}
