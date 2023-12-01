package com.example.camel.dynamic;

import org.apache.camel.CamelContext;
import org.apache.camel.impl.DefaultCamelContext;

import lombok.extern.slf4j.Slf4j;

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
@Slf4j
public class DynamicControlMain {

    public static CamelContext camelContext;

    public static void main(String[] args) {

        log.info("===== DynamicControlMain Start...=====");
        try {
            camelContext = new DefaultCamelContext();
            camelContext.start();
            camelContext.addRoutes(new RouteControl());
            camelContext.addRoutes(new RouteControlTarget());

            synchronized (DynamicControlMain.class) {
                DynamicControlMain.class.wait();
            }

        } catch (Exception e) {
            log.error("error : {} , errorMessage : {}", new Object[]{e, e.getMessage()});
        }
    }
}
