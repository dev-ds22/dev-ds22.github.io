package com.example.camel.choice;

import org.apache.camel.Exchange;
import org.apache.camel.ExchangePattern;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.http.common.HttpMessage;
import org.apache.camel.model.language.JsonPathExpression;
import org.apache.commons.io.IOUtils;

import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;

@Slf4j
public class RouteProcessorChoice02 extends RouteBuilder {
	
	/**
	 * http://127.0.0.1:8082/choiceRoute 를 호출 시 body 의 $.data.routeType 값에 따른 분기 확인
	 * 
	 * $.data.routeType = 'test1' 일 경우 --> Response Test Processor-#1 Contents
	 * $.data.routeType = 'test2' 일 경우 --> Response Test Processor-#2 Contents
	 * 그 외의 경우 --> Response Test Processor-#3 Contents
	 */
	@Override
	public void configure() throws Exception {
		JsonPathExpression jsonPathExpression = new JsonPathExpression("$.data.routeType");
		jsonPathExpression.setResultType(String.class);
		// log.info("jsonPathExpression : {}", jsonPathExpression.toString());

		from("jetty:http://127.0.0.1:8082/choiceRoute")
		.process(new Processor() {
			@Override
			public void process(Exchange exchange) throws Exception {
				HttpMessage message = (HttpMessage) exchange.getIn();
				InputStream bodyStream = (InputStream) message.getBody();
				String inputContext = IOUtils.toString(bodyStream, "UTF-8");
				log.info("inputContext : {}", inputContext);
				bodyStream.close();

				if (exchange.getPattern() == ExchangePattern.InOut) {
					Message outMessage = exchange.getMessage();
					outMessage.setBody(inputContext + " [Integration Hub Lib.] Response by Process-PRE");
				}
			}

		})
		.setHeader("routeType", jsonPathExpression).choice()
		.when(header("routeType").isEqualTo("test1")).process(new Processor() {
			@Override
			public void process(Exchange exchange) throws Exception {
				HttpMessage message = (HttpMessage) exchange.getIn();
				String body = message.getBody().toString();

				log.info("TestProcessor #1 body: {}", body);

				if (exchange.getPattern() == ExchangePattern.InOut) {
					Message outMessage = exchange.getMessage();
					outMessage.setBody(body + " [Integration Hub Lib.] Test REST Response by Process-#1.");
				}

			}
		}).when(header("routeType").isEqualTo("test2")).process(new Processor() {

			@Override
			public void process(Exchange exchange) throws Exception {
				HttpMessage message = (HttpMessage) exchange.getIn();
				String body = message.getBody().toString();

				log.info("TestProcessor #2 body: {}", body);

				if (exchange.getPattern() == ExchangePattern.InOut) {
					Message outMessage = exchange.getMessage();
					outMessage.setBody(body + " [Integration Hub Lib.] Test REST Response by Process-#2.");
				}

			}
		}).otherwise().process(new Processor() {

			@Override
			public void process(Exchange exchange) throws Exception {
				HttpMessage message = (HttpMessage) exchange.getIn();
				String body = message.getBody().toString();

				log.info("TestProcessor #3 body: {}", body);

				if (exchange.getPattern() == ExchangePattern.InOut) {
					Message outMessage = exchange.getMessage();
					outMessage.setBody(body + " [Integration Hub Lib.] Test REST Response by Process-#3.");
				}

			}
		})
		.endChoice();
}

}
