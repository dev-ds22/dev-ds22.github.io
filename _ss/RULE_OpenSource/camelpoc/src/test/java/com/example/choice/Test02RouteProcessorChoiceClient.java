package com.example.choice;

import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONObject;

import com.example.camel.utils.HttpClient;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Test02RouteProcessorChoiceClient {

	public static void main(String[] args) {
		URL url = null;
		HttpURLConnection http = null;

		try {
			url = new URL("http://127.0.0.1:8082/choiceRoute");
			for (int i = 0; i < 3; i++) {
				log.info("Http post start !!!");
				Long startTime = System.currentTimeMillis();
				http = (HttpURLConnection) url.openConnection();

				// ************************************************************
				JSONObject authorityJson = new JSONObject();
				if ( i > 1) {
					authorityJson.put("routeType", "testX");
				} else {
					authorityJson.put("routeType", "test" + String.valueOf(i+1));
				}
				JSONObject requestJson = new JSONObject();
				requestJson.put("data", authorityJson);
				requestJson.put("token", "asdaopsd89as0d8as7dasdas-=8a90sd7as6dasd");
				requestJson.put("desc", "");

				// ************************************************************

				StringBuffer sb = new StringBuffer();
				sb.append(requestJson.toString());
				log.info("Request Json : {}", sb);

				String result = HttpClient.doPost(sb.toString(), 30000000, http);

				log.info("Http post end cost : {}", (System.currentTimeMillis() - startTime) + "ms");
				log.info("Result : {}", result);

				Thread.sleep(500);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
