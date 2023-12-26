package com.samsung.airsol.cnx.integration.poc.choice.processor;

import java.io.InputStream;

import org.apache.camel.Exchange;
import org.apache.camel.ExchangePattern;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.commons.io.IOUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Process03 implements Processor {

    @Override
    public void process(Exchange exchange) throws Exception {

        InputStream body = exchange.getIn().getBody(InputStream.class);
        String inputContext = IOUtils.toString(body, "UTF-8");

        log.info("Process03 inputContext : {}", inputContext);

        if (exchange.getPattern() == ExchangePattern.InOut) {
            Message outMessage = exchange.getMessage();
            outMessage.setBody(inputContext + " [Integration Hub Lib.] - From Process#3 : Response by Process-03.");
        }
    }
}
