package com.example.httpclient.sync;

import java.io.InputStream;
import java.util.Iterator;

import org.apache.hc.client5.http.ClientProtocolException;
import org.apache.hc.client5.http.ConnectionKeepAliveStrategy;
import org.apache.hc.client5.http.auth.CredentialsProvider;
import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.cookie.BasicCookieStore;
import org.apache.hc.client5.http.cookie.CookieStore;
import org.apache.hc.client5.http.cookie.StandardCookieSpec;
import org.apache.hc.client5.http.impl.auth.BasicCredentialsProvider;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.protocol.HttpClientContext;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactoryBuilder;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HeaderElement;
import org.apache.hc.core5.http.HeaderElements;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.http.io.SocketConfig;
import org.apache.hc.core5.http.io.entity.HttpEntities;
import org.apache.hc.core5.http.io.support.ClassicRequestBuilder;
import org.apache.hc.core5.http.message.MessageSupport;
import org.apache.hc.core5.http.message.StatusLine;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.apache.hc.core5.http.ssl.TLS;
import org.apache.hc.core5.pool.PoolConcurrencyPolicy;
import org.apache.hc.core5.pool.PoolReusePolicy;
import org.apache.hc.core5.ssl.SSLContexts;
import org.apache.hc.core5.util.Args;
import org.apache.hc.core5.util.TimeValue;
import org.apache.hc.core5.util.Timeout;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HttpClient5ClassicExample {

    public static void main(String... args) throws Exception {
        PoolingHttpClientConnectionManager connectionManager = PoolingHttpClientConnectionManagerBuilder.create()
                .setSSLSocketFactory(SSLConnectionSocketFactoryBuilder.create()
                        .setSslContext(SSLContexts.createSystemDefault())
                        .setTlsVersions(TLS.V_1_3)
                        .build())
                .setDefaultSocketConfig(SocketConfig.custom()
                        .setSoTimeout(Timeout.ofMinutes(1))
                        .build())
                .setPoolConcurrencyPolicy(PoolConcurrencyPolicy.STRICT)
                .setConnPoolPolicy(PoolReusePolicy.LIFO)
                .setDefaultConnectionConfig(ConnectionConfig.custom()
                        .setSocketTimeout(Timeout.ofMinutes(1))
                        .setConnectTimeout(Timeout.ofMinutes(1))
                        .setTimeToLive(TimeValue.ofMinutes(10))
                        .build())
                .build();

        CloseableHttpClient client = HttpClients.custom()
                .setConnectionManager(connectionManager)
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setCookieSpec(StandardCookieSpec.STRICT)
                        .build())
                .setKeepAliveStrategy(new ConnectionKeepAliveStrategy() {

                    @Override	
                    public TimeValue getKeepAliveDuration(HttpResponse response, HttpContext context) {	
                        Args.notNull(response, "HTTP response");	
                        Iterator<HeaderElement> it = MessageSupport.iterate(response, HeaderElements.KEEP_ALIVE);
                        if (it != null && it.hasNext()) {
                            final HeaderElement he = it.next();	
                            final String param = he.getName();	
                            final String value = he.getValue();	
                            if (value != null && param.equalsIgnoreCase("timeout")) {	
                                try {	
                                    return TimeValue.ofSeconds(Long.parseLong(value));

                                } catch (final NumberFormatException ignore) {
                                    
                                }	
                            }	
                        }
                        return TimeValue.ofSeconds(5);	
                    }
                
                })
                .build();

        CookieStore cookieStore = new BasicCookieStore();

        CredentialsProvider credentialsProvider = new BasicCredentialsProvider();

        HttpClientContext clientContext = HttpClientContext.create();
        clientContext.setCookieStore(cookieStore);
        clientContext.setCredentialsProvider(credentialsProvider);
        clientContext.setRequestConfig(RequestConfig.custom()
                .setCookieSpec(StandardCookieSpec.STRICT)
                .build());

        JsonFactory jsonFactory = new JsonFactory();
        ObjectMapper objectMapper = new ObjectMapper(jsonFactory);

        Authority authority = new Authority();
        authority.setRouteType("test2");

        Body body = new Body();
        body.setData(authority);
        body.setToken("asdaopsd89as0d8as7dasdas-=8a90sd7as6dasd");
        body.setDesc("Test Request Json data....");

        // log.info("jsonString : {}", objectMapper.writeValueAsString(body));
        
        ClassicHttpRequest httpPost = ClassicRequestBuilder.post("http://127.0.0.1:8088/choiceRoute")
        .setEntity(HttpEntities.create(outstream -> {
                //     objectMapper.writeValue(outstream, Arrays.asList(
                        //             new BasicNameValuePair("data", "test2"),
                        //             new BasicNameValuePair("name2", "value2")));

                    objectMapper.writeValue(outstream, body);
                    outstream.flush();
                }, ContentType.APPLICATION_JSON))
                .build();

        JsonNode responseData = client.execute(httpPost, response -> {
            if (response.getCode() >= 300) {
                throw new ClientProtocolException(new StatusLine(response).toString());
            }
            final HttpEntity responseEntity = response.getEntity();
            if (responseEntity == null) {
                return null;
            }
            try (InputStream inputStream = responseEntity.getContent()) {
                return objectMapper.readTree(inputStream);
            }
        });
        System.out.println(responseData);

        client.close();
    }

}

@Data
class Authority{
    private String routeType;
}

@Data
class Body{
    private Authority data;
    private String token;
    private String desc;
}

