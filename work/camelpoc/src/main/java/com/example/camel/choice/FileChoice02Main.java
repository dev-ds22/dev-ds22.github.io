package com.example.camel.choice;

import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.model.ModelCamelContext;

/**
 * 파일 내용에 따른 분기 테스트
 */
public class FileChoice02Main {

	@SuppressWarnings("resource")
	public static void main(String[] args) throws Exception {

		ModelCamelContext camelContext = new DefaultCamelContext();
		camelContext.start();
		camelContext.addRoutes(new RouteFileChoice02());
		
		synchronized (FileChoice02Main.class) {
			FileChoice02Main.class.wait();
		}
	}

}
