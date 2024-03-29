package com.samsung.airsol.cnx.integration.poc.choice;

import org.apache.camel.builder.RouteBuilder;

import com.samsung.airsol.cnx.integration.poc.choice.processor.Process01;
import com.samsung.airsol.cnx.integration.poc.choice.processor.Process02;
import com.samsung.airsol.cnx.integration.poc.choice.processor.Process03;
import com.samsung.airsol.cnx.integration.poc.choice.processor.Process04;

public class RouteJettyChoice02 extends RouteBuilder {

    /**
     * 호출되는 request body 내용에 따른 분기 테스트
     * 
     * http://127.0.0.1:8084/choiceRoute 
     * 
     * 호출 시 body 안에 test1 이 포함되었을 경우 Process02 처리, 
     * body 안에 test2 가 포함되었을 경우 Process03 처리, 그외는 Process04 가 처리
     */
    @Override
    public void configure() throws Exception {
        from("jetty:http://127.0.0.1:8084/choiceRoute")
        .process(new Process01())
        .choice()
        .when(body().contains("test1")).process(new Process02()).to("log:RouteJettyChoice02?showExchangeId=true&level=INFO")
        .when(body().contains("test2")).process(new Process03()).to("log:RouteJettyChoice02?showExchangeId=true&level=INFO")
        .otherwise().process(new Process04()).to("log:RouteJettyChoice02?showExchangeId=true&level=INFO")
        .endChoice();
    }

}
