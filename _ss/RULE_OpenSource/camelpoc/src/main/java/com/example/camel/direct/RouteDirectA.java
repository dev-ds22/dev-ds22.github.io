package com.example.camel.direct;

import java.io.InputStream;

import org.apache.camel.Exchange;
import org.apache.camel.ExchangePattern;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.commons.io.IOUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RouteDirectA extends RouteBuilder {

    /**
     * http 호출 시 request 내용(json)에 설정된 Direct Route 실행 테스트
     * 
     * http://127.0.0.1:8090/directMain
     * 
     * request json 의 $.data.routeName 에 설정된 Route 호출
     * direct:directRouteB,direct:directRouteC 와 같이 ',' 구분자로 순서대로 해당 Route 처리 
	 * 
     */
	@Override
	public void configure() throws Exception {

		from("jetty:http://127.0.0.1:8096/directMain")
			.setExchangePattern(ExchangePattern.InOnly)
			.recipientList()
			.jsonpath("$.data.routeName")
			.delimiter(",")
			.end()
			.process(new Processor() {
				@Override
				public void process(Exchange exchange) throws Exception {
					Message message = exchange.getIn();
					log.info("Processor exchange : {}", exchange);

					InputStream body = (InputStream) message.getBody();
					String str = IOUtils.toString(body, "UTF-8");
					// log.info("Processor str : {}", str);

					if (exchange.getPattern() == ExchangePattern.InOut) {
						Message outMessage = exchange.getMessage();
						outMessage.setBody(str + " [Integration Hub Lib.] - From Process#Direct-A Response by DirectRoute-A Processor.");
					}
				}
			});

	}

}
