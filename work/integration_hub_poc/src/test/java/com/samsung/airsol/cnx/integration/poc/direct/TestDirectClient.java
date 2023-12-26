package com.samsung.airsol.cnx.integration.poc.direct;

import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONObject;

import com.samsung.airsol.cnx.integration.poc.utils.HttpClient;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TestDirectClient {

	public static void main(String[] args) throws Exception {

		URL url = null;
		HttpURLConnection http = null;

		try {
			url = new URL("http://127.0.0.1:8096/directMain");
			for (int i = 0; i < 1; i++) {
				log.info("Http post start !!!");
				Long startTime = System.currentTimeMillis();
				http = (HttpURLConnection) url.openConnection();

				// ************************************************************
				JSONObject routeDataJson = new JSONObject();
				routeDataJson.put("routeName", "direct:directRouteB,direct:directRouteC");

				JSONObject requestJson = new JSONObject();
				requestJson.put("data", routeDataJson);
				requestJson.put("token", "d9c33c8f-ae59-4edf-b37f-290ff208de2e");
				requestJson.put("desc", "oasdjosjdsidjfisodjf");

				// ************************************************************

				StringBuffer sb = new StringBuffer();
				sb.append(requestJson.toString());
				log.info("Request Json : {}", sb);

				String result = HttpClient.doPost(sb.toString(), 30000000, http);

				log.info("Http post end cost : {}", (System.currentTimeMillis() - startTime) + "ms");
				log.info("Result : {}", result);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
