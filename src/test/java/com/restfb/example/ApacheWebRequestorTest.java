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

import com.restfb.WebRequestor.Response;

public class ApacheWebRequestorTest {
	
	static CloseableHttpClient client, proxyClient;
	static org.slf4j.Logger log;
	
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
		log = LoggerFactory.getLogger(ApacheWebRequestorTest.class);
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
    public ExpectedException thrown= ExpectedException.none();
	
	@Test
	public void testGetGoogleSearchBar() throws Exception {
		thrown.expect(java.net.UnknownHostException.class);
		ApacheHttpWebRequestor awr = new ApacheHttpWebRequestor(client);
		String baseUri = "http://www.google.com";
		Response response = awr.executeGet(baseUri);
		parseGoogleBar(baseUri, response);
	}

	@Test
	public void testHttpsGetGoogleSearchBar() throws Exception {
		thrown.expect(java.net.UnknownHostException.class);
		ApacheHttpWebRequestor awr = new ApacheHttpWebRequestor(client);
		String baseUri = "https://www.google.com";
		Response response = awr.executeGet(baseUri);
		parseGoogleBar(baseUri, response);
	}

	@Test
	public void testProxyHttpsGetGoogleSearchBar() throws Exception {
		ApacheHttpWebRequestor awr = new ApacheHttpWebRequestor(proxyClient);
		String baseUri = "https://www.google.com";
		Response response = awr.executeGet(baseUri);
		parseGoogleBar(baseUri, response);
	}

	@Test
	public void testProxyGetGoogleSearchBar() throws Exception {
		ApacheHttpWebRequestor awr = new ApacheHttpWebRequestor(proxyClient);
		String baseUri = "http://www.google.com";
		Response response = awr.executeGet(baseUri);
		parseGoogleBar(baseUri, response);
	}

	void parseGoogleBar(String baseUri, Response response) {
		String html = response.toString();
		Document document = Jsoup.parse(html, baseUri);
		Element body = document.body();
		Element gbarElement = document.getElementById("gbar");
		log.info("Google bar markup:{}",gbarElement.toString());
	}

	@Test
	public void testHttpsGetGoogleSearch() throws Exception {
		thrown.expect(java.net.UnknownHostException.class);
		ApacheHttpWebRequestor awr = new ApacheHttpWebRequestor(client);
		String baseUri = "https://www.google.com";
		Response response = awr.executeGet(baseUri);
		String html = response.toString();
		parseTitle(baseUri, html);
			
	}

	@Test
	public void testProxyHttpsGetGoogleSearch() throws Exception {
		ApacheHttpWebRequestor awr = new ApacheHttpWebRequestor(proxyClient);
		String baseUri = "https://www.google.com";
		Response response = awr.executeGet(baseUri);
		String html = response.toString();
		parseTitle(baseUri, html);
			
	}

	void parseTitle(String baseUri, String html) {
		Document document = Jsoup.parse(html, baseUri);
		Element body = document.body();
		Elements headElements = document.select("title");
		if (!headElements.isEmpty()) {
			Element first = headElements.first();
			String text = first.text();
			log.info("title tag [{}]",text);
		}
	}
}
