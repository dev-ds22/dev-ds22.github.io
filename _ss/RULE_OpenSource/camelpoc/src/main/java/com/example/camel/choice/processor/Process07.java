package com.example.camel.choice.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Process07 implements Processor {

    @Override
    public void process(Exchange exchange) throws Exception {
        log.info("===== Process07 =====");
        log.info("[Integration Hub Lib.] - From Process#7 : Process07 executed... : {}", exchange);
        log.info("===== Process07 =====");
    }

}