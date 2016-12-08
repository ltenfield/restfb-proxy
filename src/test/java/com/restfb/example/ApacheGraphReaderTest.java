package com.restfb.example;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.config.RequestConfig.Builder;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.osa.restfb.ApacheHttpWebRequestor;
import org.slf4j.LoggerFactory;

import com.restfb.WebRequestor;
import com.restfb.WebRequestor.Response;

public class ApacheGraphReaderTest {
	
	static CloseableHttpClient client, proxyClient;
	static org.slf4j.Logger log;
	static String accessToken = "EAARCO4bfxmYBAKMZCjgI5vorPZApPl6wyJhbmMgk9QFhqLOzBZB0iuqZCGot4lNacTccf1MCourFhFHoZBMIEEQLfayuCRxGfZCNzbs3G6s0pEQjrev6Idqx2ULpQCW2pRBAXh1lqXDiZA1z8nrkgk1Ix8MCkTIoJNGheWA0i3GLwZDZD";
	
	@BeforeClass
	public static void setupConnection() {
		InputStream is = ClassLoader.class.getResourceAsStream("/logging-config.prop");
		if (is != null) {
			try {
				LogManager.getLogManager().readConfiguration(is);
			} catch (SecurityException | IOException e) {
				System.out.println("unable to load logging-config.prop from classpath:" + e.getMessage());
			}
		} else
			System.out.println("unable to load logging-config.prop, file not found");
		Logger.getGlobal().info("configuration completed");
		log = LoggerFactory.getLogger(ApacheGraphReaderTest.class);
		client = HttpClientBuilder.create().build();
		HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
		//AuthScope authScope = new AuthScope("localhost", AuthScope.ANY_PORT, AuthScope.ANY_REALM, AuthScope.ANY_SCHEME);
		BasicCredentialsProvider bcp = new BasicCredentialsProvider();
		bcp.setCredentials(
                new AuthScope("localhost", 3128),
                new UsernamePasswordCredentials("squiduser", "squid"));
		httpClientBuilder.setDefaultCredentialsProvider(bcp);
		Builder rcb = RequestConfig.custom();
		rcb.setProxy(new HttpHost("localhost", 3128));
		rcb.setAuthenticationEnabled(true);
		httpClientBuilder.setDefaultRequestConfig(rcb.build());
		proxyClient = httpClientBuilder.build();
	}

	@Rule
    public ExpectedException thrown = ExpectedException.none();
	
	@Test
	public void testGraphfetch() throws Exception {
		//thrown.expect(java.net.UnknownHostException.class);
		ApacheHttpWebRequestor awr = new ApacheHttpWebRequestor(client);
		GraphReaderExample readerExample = new GraphReaderExample(accessToken, awr);
		readerExample.runEverything();
	}

	@Test
	public void testProxyGraphfetch() throws Exception {
		//thrown.expect(java.net.UnknownHostException.class);
		ApacheHttpWebRequestor awr = new ApacheHttpWebRequestor(proxyClient);
		GraphReaderExample readerExample = new GraphReaderExample(accessToken, awr);
		readerExample.runEverything();
	}

}
