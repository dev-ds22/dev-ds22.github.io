package com.samsung.airsol.cnx.integration.poc.choice;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.jsonpath.JsonPathExpression;

import com.samsung.airsol.cnx.integration.poc.choice.processor.Process01;
import com.samsung.airsol.cnx.integration.poc.choice.processor.Process02;
import com.samsung.airsol.cnx.integration.poc.choice.processor.Process03;
import com.samsung.airsol.cnx.integration.poc.choice.processor.Process04;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RouteJettyChoice01 extends RouteBuilder {

    /**
     * 호출되는 request body 내용에 따른 분기 테스트 #2
     * 
     * http://127.0.0.1:8083/choiceRoute 
     * 
     * 호출 시 body 안의 data.routeType 의 값이 test1 일 경우 Process02 처리, 
     * data.routeType 의 값이 test2 일 경우 Process03 처리, 그외는 Process04 가 처리
     */
    @Override
    public void configure() throws Exception {
        JsonPathExpression jsonPathExpression = new JsonPathExpression("$.data.routeType");
        jsonPathExpression.setResultType(String.class);
        log.info("jsonPathExpression : {}", jsonPathExpression);
        
        from("jetty:http://127.0.0.1:8083/choiceRoute")
        .process(new Process01())
        .setHeader("routeType", jsonPathExpression)
        .choice()
        .when(header("routeType").isEqualTo("test1")).process(new Process02()).to("log:RouteJettyChoice01?showExchangeId=true&level=INFO")
        .when(header("routeType").isEqualTo("test2")).process(new Process03()).to("log:RouteJettyChoice01?showExchangeId=true&level=INFO")
        .otherwise().process(new Process04()).to("log:RouteJettyChoice01?showExchangeId=true&level=INFO")
        .endChoice();
    }

}
