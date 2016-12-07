package org.osa.restfb;

import static com.restfb.logging.RestFBLogger.HTTP_LOGGER;
import static java.lang.String.format;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.util.EntityUtils;

import com.restfb.BinaryAttachment;
import com.restfb.DebugHeaderInfo;
import com.restfb.Version;
import com.restfb.WebRequestor;
import com.restfb.util.StringUtils;

public class ApacheHttpWebRequestor implements WebRequestor {

	// Default charset to use for encoding/decoding strings.
	public static final String ENCODING_CHARSET = "UTF-8";

	HttpClient hc;
	
	DebugHeaderInfo debugHeaderInfo;

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
		HttpPost httpPost = new HttpPost(url + parameters == null ? "" : parameters);
		return execute(httpPost);
	}

	@Override
	public Response executePost(String url, String parameters, BinaryAttachment... binaryAttachments)
			throws IOException {
		HttpPost httpPost = new HttpPost(url + parameters == null ? "" : parameters);
		if (binaryAttachments.length > 0) {
			MultipartEntityBuilder mpeb = MultipartEntityBuilder.create();
			for (BinaryAttachment binaryAttachment : binaryAttachments) {
				InputStreamBody isb = new InputStreamBody(binaryAttachment.getData(), binaryAttachment.getContentType());
				mpeb.addPart(binaryAttachment.getFilename(), isb);
			}
			httpPost.setEntity(mpeb.build());
		}
		return execute(httpPost);
	}

	@Override
	public Response executeDelete(String url) throws IOException {
		HttpDelete httpDelete = new HttpDelete(url);
		return execute(httpDelete);
	}

	@Override
	public DebugHeaderInfo getDebugHeaderInfo() {
		return debugHeaderInfo;
	}

	protected void fillHeaderAndDebugInfo(Map<String, Header> headerMap) {
		//currentHeaders = Collections.unmodifiableMap(httpUrlConnection.getHeaderFields());

		Header header = headerMap.get("facebook-api-version");
		String usedApiVersion = StringUtils.trimToEmpty(header == null ? "" : header.getValue());
		if (HTTP_LOGGER.isDebugEnabled()) {
			HTTP_LOGGER.debug(format("Facebook used the API %s to answer your request", usedApiVersion));
		}
		header = headerMap.get("facebook-api-x-fb-trace-id");
		String fbTraceId = StringUtils.trimToEmpty(header == null ? "" : header.getValue());
		header = headerMap.get("x-fb-rev");
		String fbRev = StringUtils.trimToEmpty(header == null ? "" : header.getValue());
		header = headerMap.get("x-fb-debug");
		String fbDebug = StringUtils.trimToEmpty(header == null ? "" : header.getValue());
		header = headerMap.get("x-app-usage");
		String fbAppUsage = StringUtils.trimToEmpty(header == null ? "" : header.getValue());
		header = headerMap.get("x-page-usage");
		String fbPageUsage = StringUtils.trimToEmpty(header == null ? "" : header.getValue());
		Version usedVersion = Version.getVersionFromString(usedApiVersion);
		debugHeaderInfo = new DebugHeaderInfo(fbDebug, fbRev, fbTraceId, usedVersion, fbAppUsage, fbPageUsage);
	}

	private Response execute(HttpRequestBase httpRequest) throws IOException, ClientProtocolException {
		if (hc == null) throw new IllegalStateException("HttpClient not initialized");
	    if (HTTP_LOGGER.isDebugEnabled()) {
	        HTTP_LOGGER.debug(format("Making a %s request to %s", httpRequest.getMethod(), httpRequest.getMethod()));
	      }
		HttpResponse httpResponse = hc.execute(httpRequest);
		StatusLine statusLine = httpResponse.getStatusLine();
		if (statusLine == null) throw new IllegalStateException("request statusline returned by HttpClient cannot be null");
		Header[] headers = httpResponse.getAllHeaders();
		HttpEntity entity = httpResponse.getEntity();
		if (entity == null) throw new IllegalStateException("request entity returned by HttpClient cannot be null");
		if (HTTP_LOGGER.isTraceEnabled()) {
		      HTTP_LOGGER.trace(format("Response headers: %s", headers == null ? null : Arrays.toString(headers)));
		}
		Map<String, Header> headerMap  = (headers == null || (headers.length < 1)) ? Collections.EMPTY_MAP : new HashMap<String, Header>(headers.length);
		for (int i = 0;i < headers.length;i++) {
			Header header = headers[i];
			headerMap.put(header.getName(), header);
		}
		fillHeaderAndDebugInfo(headerMap);
//		InputStream content = entity.getContent();
//		if (content == null) return null;		
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
	    httpRequest.releaseConnection();
		return final_response;
	}
}
