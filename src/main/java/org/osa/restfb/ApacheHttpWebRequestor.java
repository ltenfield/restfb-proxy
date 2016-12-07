package org.osa.restfb;

import static com.restfb.logging.RestFBLogger.HTTP_LOGGER;
import static java.lang.String.format;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.util.EntityUtils;

import com.restfb.BinaryAttachment;
import com.restfb.DebugHeaderInfo;
import com.restfb.WebRequestor;

public class ApacheHttpWebRequestor implements WebRequestor {

	// Default charset to use for encoding/decoding strings.
	public static final String ENCODING_CHARSET = "UTF-8";

	HttpClient hc;
	
	public ApacheHttpWebRequestor(HttpClient hc) {
		this.hc = hc;
	}

	@Override
	public Response executeGet(String url) throws IOException {
		HttpGet httpGet = new HttpGet(url);
		return execute(httpGet);
	}

	@Override
	public Response executePost(String url, String parameters) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Response executePost(String url, String parameters, BinaryAttachment... binaryAttachments)
			throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Response executeDelete(String url) throws IOException {
		HttpDelete httpDelete = new HttpDelete(url);
		return execute(httpDelete);
	}

	@Override
	public DebugHeaderInfo getDebugHeaderInfo() {
		// TODO Auto-generated method stub
		return null;
	}

	private Response execute(HttpRequestBase httpRequest) throws IOException, ClientProtocolException {
		if (hc == null) throw new IllegalStateException("HttpClient not initialized");
	    if (HTTP_LOGGER.isDebugEnabled()) {
	        HTTP_LOGGER.debug(format("Making a %s request to %s", httpRequest.getMethod(), httpRequest.getMethod()));
	      }
		HttpResponse httpResponse = hc.execute(httpRequest);
		StatusLine statusLine = httpResponse.getStatusLine();
		if (statusLine == null) throw new IllegalStateException("request statusline returned by HttpClient cannot be null");
		HttpEntity entity = httpResponse.getEntity();
		Header header = entity.getContentType();
		HeaderElement[] elements = header.getElements();
		if (entity == null) throw new IllegalStateException("request entity returned by HttpClient cannot be null");
	      if (HTTP_LOGGER.isTraceEnabled()) {
	          HTTP_LOGGER.trace(format("Response headers: %s", elements == null ? null : Arrays.toString(elements)));
	        }

		InputStream content = entity.getContent();
		if (content == null) return null;		
//		StringBuilder sb = new StringBuilder();
//		try (BufferedReader br = new BufferedReader(new InputStreamReader(content))) {
//			for (int c;(c = br.read()) != -1;)	{
//				sb.append((char)c);
//			}
//		}
		String contentString = EntityUtils.toString(entity, ENCODING_CHARSET);
		Response final_response = new Response(statusLine.getStatusCode(), contentString);
	      if (HTTP_LOGGER.isDebugEnabled()) {
	          HTTP_LOGGER.debug(format("Facebook responded with %s", final_response));
	        }

		return final_response;
	}
}
