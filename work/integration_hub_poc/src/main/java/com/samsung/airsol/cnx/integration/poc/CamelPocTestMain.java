package com.samsung.airsol.cnx.integration.poc;

import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.model.ModelCamelContext;

import com.samsung.airsol.cnx.integration.poc.choice.RouteJettyChoice01;
import com.samsung.airsol.cnx.integration.poc.choice.RouteJettyChoice02;
import com.samsung.airsol.cnx.integration.poc.choice.RouteProcessorChoice01;
import com.samsung.airsol.cnx.integration.poc.choice.RouteProcessorChoice02;
import com.samsung.airsol.cnx.integration.poc.direct.RouteDirectA;
import com.samsung.airsol.cnx.integration.poc.direct.RouteDirectB;
import com.samsung.airsol.cnx.integration.poc.direct.RouteDirectC;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CamelPocTestMain {

    @SuppressWarnings("resource")
    public static void main(String[] args) throws Exception {
        log.info("===== CamelPocTestMain Start...=====");	
        ModelCamelContext camelContext = new DefaultCamelContext();
        camelContext.start();

	     /**
	      * 멀티URL 처리 - 호출 URL에 따른 처리분기 태스트
	      * 
	      * http://127.0.0.1:8081/testRouteA -> Response Process-A Contents.
	      * http://127.0.0.1:8081/testRouteB -> Response Process-B Contents.
	      * 
	      */        
        camelContext.addRoutes(new RouteProcessorChoice01());

       /**
	      * http://127.0.0.1:8082/choiceRoute 를 호출 시 body 의 $.data.routeType 값에 따른 분기 확인
	      * 
	      * $.data.routeType = 'test1' 일 경우 --> Response Test Processor-#1 Contents
	      * $.data.routeType = 'test2' 일 경우 --> Response Test Processor-#2 Contents
	      * 그 외의 경우 --> Response Test Processor-#3 Contents
	      */
        camelContext.addRoutes(new RouteProcessorChoice02());

       /**
        * 호출되는 request body 내용에 따른 분기 테스트 #2
        * 
        * http://127.0.0.1:8083/choiceRoute 
        * 
        * 호출 시 body 안의 data.routeType 의 값이 test1 일 경우 Process02 처리, 
        * data.routeType 의 값이 test2 일 경우 Process03 처리, 그외는 Process04 가 처리
        */        
        camelContext.addRoutes(new RouteJettyChoice01());

       /**
        * 호출되는 request body 내용에 따른 분기 테스트
        * 
        * http://127.0.0.1:8084/choiceRoute 
        * 
        * 호출 시 body 안에 test1 이 포함되었을 경우 Process02 처리, 
        * body 안에 test2 가 포함되었을 경우 Process03 처리, 그외는 Process04 가 처리
        */        
        camelContext.addRoutes(new RouteJettyChoice02());

       /**
        * 파일명에 따른 분기 테스트
        * 
        * ./data 폴더안의 파일을 읽어들여 
        * 파일명이 sample-json-data-1.json 일 경우 Process05 처리, 
        * 파일명이 sample-json-data-2.json 일 경우 Process06 처리, 그외는 Process07 처리
        */
        // camelContext.addRoutes(new RouteFileChoice01());

       /**
        * 파일 내용에 따른 분기 테스트
        * 
        * ./data 폴더안의 파일을 읽어들여 
        * ${body.type} == 'testJson#1' 일 경우 Process05 처리, 
        * ${body.type} == 'testJson#2' 일 경우 Process06 처리, 그외는 Process07 가 처리
        */
        //camelContext.addRoutes(new RouteFileChoice02());

       /**
        * http 호출 시 request 내용(json)에 설정된 Direct Route 실행 테스트
        * 
        * http://127.0.0.1:8096/directMain
        * 
        * request json 의 $.data.routeName 에 설정된 Route 호출
        * direct:directRouteB,direct:directRouteC 와 같이 ',' 구분자로 순서대로 해당 Route 처리 
        * 
        */
        camelContext.addRoutes(new RouteDirectA());
        camelContext.addRoutes(new RouteDirectB());
        camelContext.addRoutes(new RouteDirectC());
        
        synchronized (CamelPocTestMain.class) {
            CamelPocTestMain.class.wait();
        }
    }
}