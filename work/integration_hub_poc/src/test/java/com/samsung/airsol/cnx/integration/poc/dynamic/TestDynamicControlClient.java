package com.samsung.airsol.cnx.integration.poc.dynamic;

import java.net.HttpURLConnection;
import java.net.URL;

import com.samsung.airsol.cnx.integration.poc.dynamic.vo.TestParamVO;
import com.samsung.airsol.cnx.integration.poc.utils.HttpClient;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TestDynamicControlClient {
    public static void main(String[] args) {
        
        // String targetRouteId = "test-route-control-01";
        // String targetRouteId = "test-route-control-02";
        String targetRouteId = "test-route-control-03";
        try {

            startRoute(targetRouteId);

            // stopRoute(targetRouteId);

            // suspendRoute(targetRouteId);
			
            // testOtherRoutes(targetRouteId);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void startRoute(String routeId) throws Exception {
        log.info("http post start !!!");
        Long startTime = System.currentTimeMillis();

        URL url = new URL("http://127.0.0.1:8092/dynamicControl");
        HttpURLConnection http = (HttpURLConnection) url.openConnection();

        TestParamVO paramVO = new TestParamVO();
        paramVO.setName("CYX");
        paramVO.setId("320265551212584512");
        paramVO.setType("AA");
        paramVO.setActionFlag("START");
        paramVO.setTargetRouteId(routeId);

        ObjectMapper objectMapper = new ObjectMapper();
        String param = objectMapper.writeValueAsString(paramVO);
        log.info("Send Message : {}", param);

        String result = HttpClient.doPost(param, 30000000, http);
        log.info("Estimated Time : {}", (System.currentTimeMillis() - startTime) + "ms");
        log.info("Response : {}", result);
    }

    @SuppressWarnings("unused")
    private static void stopRoute(String routeId) throws Exception {
        log.info("http post start !!!");
        Long startTime = System.currentTimeMillis();

        URL url = new URL("http://127.0.0.1:8092/dynamicControl");
        HttpURLConnection http = (HttpURLConnection) url.openConnection();

        TestParamVO paramVO = new TestParamVO();
        paramVO.setName("CYX");
        paramVO.setId("320265551212584512");
        paramVO.setType("BB");
        paramVO.setActionFlag("STOP");
        paramVO.setTargetRouteId(routeId);

        ObjectMapper objectMapper = new ObjectMapper();
        String param = objectMapper.writeValueAsString(paramVO);
        log.info("Send Message : {}", param);

        String result = HttpClient.doPost(param, 30000000, http);
        log.info("Estimated Time : {}", (System.currentTimeMillis() - startTime) + "ms");
        log.info("Response : {}", result);
    }

    @SuppressWarnings("unused")
    private static void suspendRoute(String routeId) throws Exception {
        log.info("http post start !!!");
        Long startTime = System.currentTimeMillis();

        URL url = new URL("http://127.0.0.1:8092/dynamicControl");
        HttpURLConnection http = (HttpURLConnection) url.openConnection();

        TestParamVO paramVO = new TestParamVO();
        paramVO.setName("CYX");
        paramVO.setId("320265551212584512");
        paramVO.setType("CC");
        paramVO.setActionFlag("SUSPEND");
        paramVO.setTargetRouteId(routeId);

		ObjectMapper objectMapper = new ObjectMapper();
        String param = objectMapper.writeValueAsString(paramVO);
        log.info("Send Message : {}", param);

        String result = HttpClient.doPost(param, 30000000, http);
        log.info("Estimated Time : {}", (System.currentTimeMillis() - startTime) + "ms");
        log.info("Response : {}", result);
    }

    @SuppressWarnings("unused")
    private static void testOtherRoutes(String routeId) throws Exception {
        log.info("http post start !!!");
        Long startTime = System.currentTimeMillis();

        //URL url = new URL("http://127.0.0.1:8092/test-route-control1");
        //URL url = new URL("http://127.0.0.1:8092/test-route-control2");
        URL url = new URL("http://127.0.0.1:8092/test-route-control3");
        HttpURLConnection http = (HttpURLConnection) url.openConnection();

        TestParamVO paramVO = new TestParamVO();
        paramVO.setName("CYX");
        paramVO.setId("320265551212584512");
        paramVO.setType("DD");
        paramVO.setActionFlag("TEST");
        paramVO.setTargetRouteId(routeId);

		ObjectMapper objectMapper = new ObjectMapper();
        String param = objectMapper.writeValueAsString(paramVO);
        log.info("Send Message : {}", param);

        String result = HttpClient.doPost(param, 30000000, http);
        log.info("Estimated Time : {}", (System.currentTimeMillis() - startTime) + "ms");
        log.info("Response : {}", result);
    }

}
