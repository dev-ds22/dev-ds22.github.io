package com.example.camel;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.camel.Exchange;
import org.apache.camel.ExchangePattern;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.http.common.HttpMessage;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.model.ModelCamelContext;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CamelJettyTest02 extends RouteBuilder {

    @SuppressWarnings("resource")
  	public static void main(String[] args) throws Exception {
		log.info("===== CamelJettyTest02 Start...=====");
		
		ModelCamelContext camelContext = new DefaultCamelContext();
		camelContext.start();
		camelContext.addRoutes(new CamelJettyTest02());

		synchronized (CamelJettyTest02.class) {
			CamelJettyTest02.class.wait();
		}
	}

    @Override
    public void configure() throws Exception {
      from("jetty:http://127.0.0.1:8086/testjetty02")
      // .setBody(simple("${date:now:yyyy-MM-dd'T'HH:mm:ssZ}"))
      .process(new HttpProcessor())
      // .log("Received a request")  
      .to("log:CamelJettyTest02?showExchangeId=true");
    }

public class HttpProcessor implements Processor {

		public void process(Exchange exchange) throws Exception {
			HttpMessage message = (HttpMessage) exchange.getIn();
			InputStream bodyStream = (InputStream) message.getBody();
			String inputContext = this.analysisMessage(bodyStream);
			bodyStream.close();

			if (exchange.getPattern() == ExchangePattern.InOut) {
				Message outMessage = exchange.getMessage();
				outMessage.setBody(inputContext + " - Response by Process.");
			}
		}

		private String analysisMessage(InputStream bodyStream) throws IOException {
			ByteArrayOutputStream outStream = new ByteArrayOutputStream();
			byte[] contextBytes = new byte[4096];
			int realLen;
			while ((realLen = bodyStream.read(contextBytes, 0, 4096)) != -1) {
				outStream.write(contextBytes, 0, realLen);
			}
			try {
				return new String(outStream.toByteArray(), "UTF-8");
			} finally {
				outStream.close();
			}
		}
	}

}