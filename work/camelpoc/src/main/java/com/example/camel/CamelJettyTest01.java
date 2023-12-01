package com.example.camel;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CamelJettyTest01 {

    @SuppressWarnings("resource")
    public static void main(String[] args) {
      log.info("===== CamelJettyTest01 Start...=====");
      System.setProperty("spring.devtools.restart.enabled", "false");
      try {
          CamelContext context = new DefaultCamelContext();    
          context.addRoutes(
            new RouteBuilder() {
              @Override
              public void configure() throws Exception {
                from("jetty:http://localhost:8084/testjetty01")
                  .process(
                    new Processor() {
                      public void process(Exchange exchange) throws Exception {
                        Message out = exchange.getMessage();
                        out.setBody("Camel Jetty Test....");
                      }
                    }
                  );
              }
            }
          );
          context.start();
      } catch (Exception e) {
          e.printStackTrace();
      }
    }
}