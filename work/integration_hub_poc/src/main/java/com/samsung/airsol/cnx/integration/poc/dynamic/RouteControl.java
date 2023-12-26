package com.samsung.airsol.cnx.integration.poc.dynamic;

import java.io.InputStream;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.ExchangePattern;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.commons.io.IOUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.samsung.airsol.cnx.integration.poc.dynamic.vo.TestParamVO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RouteControl extends RouteBuilder {

    private CamelContext camelContext = DynamicControlMain.camelContext;

    /**
     * 호출 시 request 로 받은 json을 TestParamVO Object 에 파싱.
     * TestParamVO 의 actionFlag의 값에 따라 targetRouteId 의 동작을 제어한다.
     * 
     * http://127.0.0.1:8092/dynamicControl
     * 
     * 이하 동작대상 Target Route
     * http://127.0.0.1:8092/test-route-control1 ("test-route-control-01")
     * http://127.0.0.1:8092/test-route-control2 ("test-route-control-02")
     * http://127.0.0.1:8092/test-route-control3 ("test-route-control-03")     * 
     */
    @Override
    public void configure() throws Exception {

        from("jetty:http://127.0.0.1:8092/dynamicControl")
        // .unmarshal()
        // .json(JsonLibrary.Jackson)
        .routeId("master-control-route")
        .process(new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {

                log.info("exchange : {}", exchange);

                InputStream bodyStream = exchange.getIn().getBody(InputStream.class);
                String bodyStr = IOUtils.toString(bodyStream, "UTF-8");

                log.info("bodyStr : {}", bodyStr);

                ObjectMapper mapper = new ObjectMapper();
			    TestParamVO paramVO = mapper.readValue(bodyStr, TestParamVO.class);
                String targetRouteId = paramVO.getTargetRouteId();
                String actionFlag = paramVO.getActionFlag();

                log.info("===== {} ROUTE routeID:{} =====", actionFlag, targetRouteId);

                if ("STOP".equals(actionFlag)) {
                    // camelContext.getRouteController().stopRoute("test-route-control-03");
                    camelContext.getRouteController().stopRoute(targetRouteId);
                    // log.info("===== STOP ROUTE routeID:{} =====", targetRouteId);

                } else if ("START".equals(actionFlag)) {
                    // camelContext.getRouteController().startRoute("test-route-control-03");
                    camelContext.getRouteController().startRoute(targetRouteId);
                    // log.info("===== START ROUTE routeID:{} =====", targetRouteId);

                } else if ("SUSPEND".equals(actionFlag)) {
                    // camelContext.getRouteController().suspendRoute("test-route-control-03");
                    camelContext.getRouteController().suspendRoute(targetRouteId);
                    // log.info("===== SUSPEND ROUTE routeID:{} =====", targetRouteId);

                }

                if (exchange.getPattern() == ExchangePattern.InOut) {
                    Message outMessage = exchange.getMessage();
                    outMessage.setBody(" - Control process completed. (Response by RouteControl Processor.)");
                }
            }
        });

    }

}