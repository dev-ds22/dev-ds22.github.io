package com.example.camel.choice;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;

import com.example.camel.choice.processor.Process05;
import com.example.camel.choice.processor.Process06;
import com.example.camel.choice.processor.Process07;

public class RouteFileChoice01 extends RouteBuilder {

    /**
     * 파일명에 따른 분기 테스트
     * 
     * ./data 폴더안의 파일을 읽어들여 
     * 파일명이 sample-json-data-1.json 일 경우 Process05 처리, 
     * 파일명이 sample-json-data-2.json 일 경우 Process06 처리, 그외는 Process07 처리
     */
    @Override
    public void configure() throws Exception {
        from("file:./data?noop=true")
        .unmarshal().json(JsonLibrary.Jackson, com.example.camel.choice.processor.Order.class)
        .choice()
        .when(header("CamelFileName").isEqualTo("sample-json-data-1.json")).process(new Process05()).to("log:RouteFileChoice01?showExchangeId=true&level=INFO")
        .when(header("CamelFileName").isEqualTo("sample-json-data-2.json")).process(new Process06()).to("log:RouteFileChoice01?showExchangeId=true&level=INFO")
        .otherwise().process(new Process07()).to("log:RouteFileChoice01?showExchangeId=true&level=INFO")
        .endChoice();

    }

}
