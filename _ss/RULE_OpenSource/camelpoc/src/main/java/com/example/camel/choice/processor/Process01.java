package com.example.camel.choice.processor;

import java.io.InputStream;

import org.apache.camel.Exchange;
import org.apache.camel.ExchangePattern;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.commons.io.IOUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Process01 implements Processor {

    @Override
    public void process(Exchange exchange) throws Exception {
        InputStream bodyStream = exchange.getIn().getBody(InputStream.class);
        String inputContext = IOUtils.toString(bodyStream, "UTF-8");

        log.info("Process01 inputContext : {}", inputContext);
        bodyStream.close();

        if (exchange.getPattern() == ExchangePattern.InOut) {
            Message outMessage = exchange.getMessage();
            outMessage.setBody(inputContext + " [Integration Hub Lib.] - From Process#1 : Response by Process-01.");
        }
    }
}
