package com.example.camel.choice;

import java.io.InputStream;

import org.apache.camel.Exchange;
import org.apache.camel.ExchangePattern;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.http.common.HttpMessage;
import org.apache.commons.io.IOUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RouteProcessorChoice01 extends RouteBuilder {

	/**
	 * 멀티URL 처리 - 호출 URL에 따른 처리분기 태스트
	 * 
	 * http://127.0.0.1:8083/testRouteA -> Response Process-A Contents.
	 * http://127.0.0.1:8083/testRouteB -> Response Process-B Contents.
	 * 
	 */
	@Override
	public void configure() throws Exception {
		from("jetty:http://127.0.0.1:8083/testRouteA").process(new Processor() {
			@Override
			public void process(Exchange exchange) throws Exception {
				HttpMessage message = (HttpMessage) exchange.getIn();
				InputStream bodyStream = (InputStream) message.getBody();
				String inputContext = IOUtils.toString(bodyStream, "UTF-8");

				log.info("Process-A executed.", inputContext);

				bodyStream.close();

				if (exchange.getPattern() == ExchangePattern.InOut) {
					Message outMessage = exchange.getMessage();
					outMessage.setBody(inputContext + " - Response by Process-A.");
				}

			}
		}).to("log:RouteProcessorChoice01?showExchangeId=true&level=INFO");				
		
		from("jetty:http://127.0.0.1:8083/testRouteB").process(new Processor() {
			@Override
			public void process(Exchange exchange) throws Exception {
				HttpMessage message = (HttpMessage) exchange.getIn();
				InputStream bodyStream = (InputStream) message.getBody();
				String inputContext = IOUtils.toString(bodyStream, "UTF-8");

				log.info("Process-B executed.", inputContext);

				bodyStream.close();

				if (exchange.getPattern() == ExchangePattern.InOut) {
					Message outMessage = exchange.getMessage();
					outMessage.setBody(inputContext + " - Response by Process-B.");
				}

			}
		}).to("log:RouteProcessorChoice01?showExchangeId=true&level=INFO");
	}

}
