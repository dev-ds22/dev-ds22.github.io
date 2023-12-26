package com.samsung.airsol.cnx.integration.poc.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.nio.charset.Charset;

public class HttpClient {

	public static final String CODEFORMAT = "UTF-8";

	public static String doPost(String requestBody, int timeout, HttpURLConnection http) throws Exception {
		String retResult = "";
		try {
			http.setDoInput(true);
			http.setDoOutput(true);
			http.setUseCaches(false);
			http.setConnectTimeout(timeout);
			http.setReadTimeout(timeout);
			http.setRequestMethod("POST");
			http.setRequestProperty("accept", "*/*");
			http.setRequestProperty("connection", "Keep-Alive");
			http.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			http.setRequestProperty("Content-Type", "text/plain;charset=UTF-8");
			http.connect();
			
			OutputStreamWriter osw = new OutputStreamWriter(http.getOutputStream(), Charset.forName("UTF-8"));
			osw.write(requestBody);
			osw.flush();
			osw.close();
			if (http.getResponseCode() == 200) {
				BufferedReader in = new BufferedReader(new InputStreamReader(http.getInputStream(), Charset.forName("UTF-8")));
				String inputLine;
				while ((inputLine = in.readLine()) != null) {
					retResult += inputLine;
				}
				in.close();
			} else {
				throw new Exception("the http.getResponseCode() is " + http.getResponseCode());
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (http != null) {
				http.disconnect();
				http = null;
			}
		}
		return retResult;
	}
}
