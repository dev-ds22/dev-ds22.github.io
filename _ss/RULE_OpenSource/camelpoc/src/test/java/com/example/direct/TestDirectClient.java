package com.example.direct;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLDecoder;

import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.json.JSONObject;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TestDirectClient {

	public static void main(String[] args) throws Exception {

		// CloseableHttpClient httpClient = HttpClients.createDefault();

		// HttpPost httpPost = new HttpPost("http://127.0.0.1:8090/directMain");

		// JSONObject routeDataJson = new JSONObject();
		// routeDataJson.put("routeName", "direct:directRouteB,direct:directRouteC");
		// // authorityJson.put("routeName", "direct:directRouteB");

		// JSONObject requestJson = new JSONObject();
		// requestJson.put("data", routeDataJson);
		// requestJson.put("token", "d9c33c8f-ae59-4edf-b37f-290ff208de2e");
		// requestJson.put("desc", "oasdjosjdsidjfisodjf");

		// StringBuffer sbb = new StringBuffer();
		// sbb.append(requestJson.toString());

		// httpPost.setHeader("ContentType", "UTF-8");
		// StringEntity urlEntity = new StringEntity(sbb.toString(), "UTF-8");
		// httpPost.setEntity(urlEntity);

		// HttpResponse response = httpClient.execute(httpPost);
		// HttpEntity entity = response.getEntity();

		// InputStream inputStream = entity.getContent();
		// BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));

		// String buffer = null;
		// StringBuffer sb = new StringBuffer();
		// while ((buffer = br.readLine()) != null) {
		// 	sb.append(buffer + "\n");
		// }

		// log.info("Reponse Entity : {}\n", URLDecoder.decode(sb.toString().trim(), "UTF-8"));
		// log.info(" response : {}, content : {}", response.getStatusLine(), entity.getContent());

	}

}
