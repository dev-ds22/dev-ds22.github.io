package com.example.camel.choice.processor;

import java.io.InputStream;

import org.apache.camel.Exchange;
import org.apache.camel.ExchangePattern;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.commons.io.IOUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Process04 implements Processor {

    @Override
    public void process(Exchange exchange) throws Exception {

        InputStream body = exchange.getIn().getBody(InputStream.class);
        String inputContext = IOUtils.toString(body, "UTF-8");

        log.info("Process04 inputContext : {}", inputContext);

        // 存入到exchange的out区域
        if (exchange.getPattern() == ExchangePattern.InOut) {
            Message outMessage = exchange.getMessage();
            outMessage.setBody(inputContext + " [Integration Hub Lib.] - From Process#4 : Response by Process-04.");
        }
    }
}
