package com.samsung.airsol.cnx.integration.poc.choice.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Process06 implements Processor {

    @Override
    public void process(Exchange exchange) throws Exception {

        log.info("===== Process06 =====");
        log.info("[Integration Hub Lib.] - From Process#6 : Process06 executed... : {}", exchange);
        log.info("===== Process06 =====");

    }
}
