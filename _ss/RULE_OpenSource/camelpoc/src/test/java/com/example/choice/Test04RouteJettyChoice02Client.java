package com.example.choice;

import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONObject;

import com.example.camel.utils.HttpClient;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Test04RouteJettyChoice02Client {

	public static void main(String[] args) {
		URL url = null;
		HttpURLConnection http = null;

		try {
			Long startTime = System.currentTimeMillis();
			url = new URL("http://127.0.0.1:8084/choiceRoute?type=test1");
			http = (HttpURLConnection) url.openConnection();

			// ************************************************************
			StringBuffer sb = getRequestInfo("test1");
			String result = HttpClient.doPost(sb.toString(), 30000000, http);
			log.info("Http post end cost : {}", (System.currentTimeMillis() - startTime) + "ms");
			log.info("Result : {}", result);

			Thread.sleep(500);

			startTime = System.currentTimeMillis();
			url = new URL("http://127.0.0.1:8084/choiceRoute?type=test2");
			http = (HttpURLConnection) url.openConnection();

			// ************************************************************
			sb = getRequestInfo("test2");
			result = HttpClient.doPost(sb.toString(), 30000000, http);
			log.info("Http post end cost : {}", (System.currentTimeMillis() - startTime) + "ms");
			log.info("Result : {}", result);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static StringBuffer getRequestInfo(String type) {
		JSONObject authorityJson = new JSONObject();
		authorityJson.put("type", type);

		JSONObject requestJson = new JSONObject();
		requestJson.put("data", authorityJson);
		requestJson.put("token", "asdaopsd89as0d8as7dasdas-=8a90sd7as6dasd");
		requestJson.put("desc", "");
		requestJson.put("type", type);

		StringBuffer sb = new StringBuffer();
		sb.append(requestJson.toString());
		log.info("Request Json : {}", sb);
		return sb;
	}
}
