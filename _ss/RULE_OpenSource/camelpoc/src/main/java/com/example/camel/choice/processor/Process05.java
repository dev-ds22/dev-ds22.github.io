package com.example.camel.choice.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Process05 implements Processor {

    @Override
    public void process(Exchange exchange) throws Exception {
        log.info("===== Process05 =====");
        log.info("Process05 executed... : {}", exchange);
        log.info("===== Process05 =====");
    }
}
