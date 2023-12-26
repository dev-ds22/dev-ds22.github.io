package com.samsung.airsol.cnx.integration.poc.dynamic;


import java.io.InputStream;

import org.apache.camel.Exchange;
import org.apache.camel.ExchangePattern;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.commons.io.IOUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RouteControlTarget extends RouteBuilder {

    /**
     * 동작 Control 대상 Slave Route 설정
     * 
     * http://127.0.0.1:8092/test-route-control1 ("test-route-control-01")
     * http://127.0.0.1:8092/test-route-control2 ("test-route-control-02")
     * http://127.0.0.1:8092/test-route-control3 ("test-route-control-03")
     */
    @Override
    public void configure() throws Exception {
        from("jetty:http://127.0.0.1:8092/test-route-control1")
        .routeId("test-route-control-01")
        .process(new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {

                InputStream inputStream = exchange.getIn().getBody(InputStream.class);
                String bodyStr = IOUtils.toString(inputStream, "UTF-8");

                log.info("bodyStr : {}", bodyStr);
                inputStream.close();

                if (exchange.getPattern() == ExchangePattern.InOut) {
                    Message outMessage = exchange.getMessage();
                    outMessage.setBody(" - Response by Control Target-01. (test-route-control-01)");
                }
            }
        })
        .to("log:RouteControlTarget?showExchangeId=true&level=INFO&showBody=true");;

        from("jetty:http://127.0.0.1:8092/test-route-control2")
        .routeId("test-route-control-02")
        .process(new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {

                InputStream inputStream = exchange.getIn().getBody(InputStream.class);
                String bodyStr = IOUtils.toString(inputStream, "UTF-8");

                log.info("bodyStr : {}", bodyStr);
                inputStream.close();

                if (exchange.getPattern() == ExchangePattern.InOut) {
                    Message outMessage = exchange.getMessage();
                    outMessage.setBody(" - Response by Control Target-02. (test-route-control-02)");
                }
            }
        })
        .to("log:RouteControlTarget?showExchangeId=true&level=INFO&showBody=true");;

        from("jetty:http://127.0.0.1:8092/test-route-control3")
        .routeId("test-route-control-03")
        .autoStartup(false)
        .process(new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {

                InputStream inputStream = exchange.getIn().getBody(InputStream.class);
                String bodyStr = IOUtils.toString(inputStream, "UTF-8");

                log.info("bodyStr : {}", bodyStr);
                inputStream.close();

                if (exchange.getPattern() == ExchangePattern.InOut) {
                    Message outMessage = exchange.getMessage();
                    outMessage.setBody(" - Response by Control Target-03. (test-route-control-03)");
                }
            }
        })
        .to("log:RouteControlTarget?showExchangeId=true&level=INFO&showBody=true");;
    }

}