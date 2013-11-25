/*
 * Copyright 2013 Torben Werner
 *
 * Torben Werner licenses this file to you under the Apache License, version 2.0
 * (the "License"); you may not use this file except in compliance with the
 * License.  You may obtain a copy of the License at:
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.github.torbinsky.billing.recurly;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReference;

import javax.annotation.Nullable;
import javax.xml.bind.DatatypeConverter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.introspect.AnnotationIntrospectorPair;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationIntrospector;
import com.github.torbinsky.billing.recurly.exception.RecurlyAPIException;
import com.github.torbinsky.billing.recurly.exception.RecurlyException;
import com.github.torbinsky.billing.recurly.exception.RecurlySerializationException;
import com.github.torbinsky.billing.recurly.model.RecurlyObject;
import com.github.torbinsky.billing.recurly.serialize.XmlPayloadMap;
import com.ning.http.client.AsyncCompletionHandler;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.AsyncHttpClient.BoundRequestBuilder;
import com.ning.http.client.AsyncHttpClientConfig;
import com.ning.http.client.Response;

/**
 * Basic/common client features such as managing the AsyncHttpClient etc...
 * 
 * @author twerner
 * 
 */
public abstract class RecurlyClientBase {

	private static final String RECURLY_PAGINATION_HEADER = "Link";

	private static final Logger log = LoggerFactory.getLogger(RecurlyClientBase.class);

	public static final String RECURLY_DEBUG_KEY = "recurly.debug";
	public static final String RECURLY_PAGE_SIZE_KEY = "recurly.page.size";

	protected static final Integer DEFAULT_PAGE_SIZE = new Integer(200);
	protected static final String PER_PAGE = "per_page=";

	public static final String FETCH_RESOURCE = "/recurly_js/result";

	/**
	 * Checks a system property to see if debugging output is required. Used
	 * internally by the client to decide whether to generate debug output
	 */
	protected static boolean debug() {
		return Boolean.getBoolean(RECURLY_DEBUG_KEY);
	}

	/**
	 * Returns the page Size to use when querying. The page size is set as
	 * System.property: recurly.page.size
	 */
	public static Integer getPageSize() {
		Integer pageSize;
		try {
			pageSize = new Integer(System.getProperty(RECURLY_PAGE_SIZE_KEY));
		} catch (NumberFormatException nfex) {
			pageSize = DEFAULT_PAGE_SIZE;
		}
		return pageSize;
	}

	public static String getPageSizeGetParam() {
		return PER_PAGE + getPageSize().toString();
	}

	protected final XmlMapper xmlMapper = new XmlMapper();

	private String apiKey;
	private ThreadLocal<String> threadApiKey = new ThreadLocal<>();
	private final String baseUrl;
	private AsyncHttpClient client;

	public RecurlyClientBase(final String apiKey) {
		this(apiKey, "api.recurly.com", 443, "v2");
	}

	public RecurlyClientBase(final String apiKey, final String host, final int port, final String version) {
		if (apiKey != null) {
			this.apiKey = DatatypeConverter.printBase64Binary(apiKey.getBytes());
		}
		this.baseUrl = String.format("https://%s:%d/%s", host, port, version);

		final AnnotationIntrospector primary = new JacksonAnnotationIntrospector();
		final AnnotationIntrospector secondary = new JaxbAnnotationIntrospector(TypeFactory.defaultInstance());
		final AnnotationIntrospector pair = new AnnotationIntrospectorPair(primary, secondary);
		xmlMapper.setAnnotationIntrospector(pair);
		xmlMapper.registerModule(new JodaModule());
		xmlMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
	}

	protected void setThreadLocalApiKey(String apiKey) {
		this.threadApiKey.set(DatatypeConverter.printBase64Binary(apiKey.getBytes()));
	}

	protected void unsetThreadLocalApiKey() {
		this.threadApiKey.remove();
	}

	private String getApiKey() {
		String threadKey = threadApiKey.get();
		if (threadKey != null) {
			return threadKey;
		}

		// Fall back to the non-thread local api key
		return apiKey;
	}

	/**
	 * Open the underlying http client
	 */
	public synchronized void open() {
		client = createHttpClient();
	}

	/**
	 * Close the underlying http client
	 */
	public synchronized void close() {
		if (client != null) {
			client.close();
		}
	}

	// /////////////////////////////////////////////////////////////////////////

	protected <T> List<T> fetches(final String recurlyToken, final Class<T> clazz) {
		return doGETs(FETCH_RESOURCE + "/" + recurlyToken, clazz);
	}

	protected <T> List<T> doGETs(final String resource, final Class<T> clazz) {
		return doGETs(resource, null, clazz);
	}
	
	protected <T> List<T> doGETsByUrl(final String url, final Class<T> clazz){
		if(debug()){
			log.info("Msg to Recurly API [GET] :: URL : {}", url); 
		}
		return callRecurlySafe(client.prepareGet(url.toString()), clazz, true); 
	}

	protected <T> List<T> doGETs(final String resource, String paramString, final Class<T> clazz) {
		String url = buildRecurlyUrl(resource, paramString);
		return doGETsByUrl(url, clazz); 
	}

	protected List<String> doGET(final String resource, String paramString) {
		String url = buildRecurlyUrl(resource, paramString);
		return callRecurlySafe(client.prepareGet(url));
	}

	protected String buildRecurlyUrl(String resource, String paramString) {
		StringBuffer url = new StringBuffer(baseUrl);
		url.append(resource);
		if (resource != null && !resource.contains("?")) {
			url.append("?");
		} else {
			url.append("&");
		}
		url.append(getPageSizeGetParam());

		if (paramString != null) {
			url.append(paramString);
		}

		return url.toString();
	}

	protected <T> List<T> doPOSTs(final String resource, final RecurlyObject payload, final Class<T> clazz) {
		final String xmlPayload;
		try {
			xmlPayload = xmlMapper.writeValueAsString(payload);
			if (debug()) {
				log.info("Msg to Recurly API [POST]:: URL : {}", baseUrl + resource);
				log.info("Payload for [POST]:: {}", xmlPayload);
			}
		} catch (IOException e) {
			log.warn("Unable to serialize {} object as XML: {}", clazz.getName(), payload.toString());
			throw new RecurlySerializationException("Unable to serialize {} object as XML: {}", e);
		}

		return callRecurlySafe(client.preparePost(baseUrl + resource).setBody(xmlPayload), clazz, true);
	}

	protected <T> List<T> doPUTs(final String resource, final RecurlyObject payload, final Class<T> clazz) {
		final String xmlPayload;
		try {
			xmlPayload = xmlMapper.writeValueAsString(payload);
			if (debug()) {
				log.info("Msg to Recurly API [PUT]:: URL : {}", baseUrl + resource);
				log.info("Payload for [PUT]:: {}", xmlPayload);
			}
		} catch (IOException e) {
			log.warn("Unable to serialize {} object as XML: {}", clazz.getName(), payload.toString());
			throw new RecurlySerializationException("Unable to serialize {} object as XML: {}", e);
		}

		return callRecurlySafe(client.preparePut(baseUrl + resource).setBody(xmlPayload), clazz, true);
	}

	protected <T> List<T> doPOSTs(final String resource, final XmlPayloadMap<?, ?> payload, final Class<T> clazz) {
		final String xmlPayload;
		try {
			xmlPayload = convertPayloadMapToXmlString(payload);
			if (debug()) {
				log.info("Msg to Recurly API [POST]:: URL : {}", baseUrl + resource);
				log.info("Payload for [POST]:: {}", xmlPayload);
			}
		} catch (IOException e) {
			log.warn("Unable to serialize {} object as XML: {}", clazz.getName(), payload.toString());
			throw new RecurlySerializationException("Unable to serialize {} object as XML: {}", e);
		}

		return callRecurlySafe(client.preparePost(baseUrl + resource).setBody(xmlPayload), clazz, true);
	}

	protected <T> List<T> doPUTs(final String resource, final XmlPayloadMap<?, ?> payload, final Class<T> clazz) {
		final String xmlPayload;
		try {
			xmlPayload = convertPayloadMapToXmlString(payload);
			if (debug()) {
				log.info("Msg to Recurly API [PUT]:: URL : {}", baseUrl + resource);
				log.info("Payload for [PUT]:: {}", xmlPayload);
			}
		} catch (IOException e) {
			log.warn("Unable to serialize {} object as XML: {}", clazz.getName(), payload.toString());
			throw new RecurlySerializationException("Unable to serialize {} object as XML: {}", e);
		}

		return callRecurlySafe(client.preparePut(baseUrl + resource).setBody(xmlPayload), clazz, true);
	}

	protected <T> T fetch(final String recurlyToken, final Class<T> clazz) {
		return doGET(FETCH_RESOURCE + "/" + recurlyToken, clazz);
	}

	protected <T> T doGET(final String resource, final Class<T> clazz) {
		return doGET(resource, null, clazz);
	}
	
	protected <T> T doGETByUrl(final String url, final Class<T> clazz){
		return returnSingleResult(doGETsByUrl(url, clazz)); 
	}

	protected <T> T doGET(final String resource, String paramString, final Class<T> clazz) {
		return returnSingleResult(doGETs(resource, paramString, clazz));
	}

	protected <T> T doPOST(final String resource, final RecurlyObject payload, final Class<T> clazz) {
		return returnSingleResult(doPOSTs(resource, payload, clazz));
	}

	protected <T> T doPUT(final String resource, final RecurlyObject payload, final Class<T> clazz) {
		return returnSingleResult(doPUTs(resource, payload, clazz));
	}

	protected <T> T doPOST(final String resource, final XmlPayloadMap<?, ?> payload, final Class<T> clazz) {
		return returnSingleResult(doPOSTs(resource, payload, clazz));
	}

	protected <T> T doPUT(final String resource, final XmlPayloadMap<?, ?> payload, final Class<T> clazz) {
		return returnSingleResult(doPUTs(resource, payload, clazz));
	}

	protected String convertPayloadMapToXmlString(final XmlPayloadMap<?, ?> xmlPayloadMap) throws JsonProcessingException {
		String xmlPayload = xmlMapper.writeValueAsString(xmlPayloadMap);
		return xmlPayload.replaceAll("XmlPayloadMap", xmlPayloadMap.getRootElementName());
	}

	protected void doDELETE(final String resource) {
		callRecurlySafe(client.prepareDelete(baseUrl + resource), null, false);
	}
	
	protected void doDELETE(final String resource, Map<String,String> queryParameters){
		BoundRequestBuilder prepareDelete = client.prepareDelete(baseUrl + resource); 
		for(String key : queryParameters.keySet()){
			prepareDelete = prepareDelete.addQueryParameter(key, queryParameters.get(key)); 
		}
		callRecurlySafe(prepareDelete, null, false); 
	}

	protected <T> T returnSingleResult(List<T> results) {
		if (!results.isEmpty()) {
			// Warn if we received more than one result
			if (results.size() > 1) {
				log.warn("Received multiple results from Recurly when only one was expected.");
			}
			return results.get(0);
		}

		return null;
	}
	
	protected <T> List<T> callRecurlySafe(final AsyncHttpClient.BoundRequestBuilder builder, @Nullable final Class<T> clazz, final boolean parseResult) {
		List<String> results = callRecurlySafe(builder);
		if(parseResult){
			try {
				return deserialize(results, clazz);
			} catch (IOException e) {
				log.warn("Error while calling Recurly", e);
				throw new RecurlySerializationException("Error while calling Recurly", e);
			}
		}
		
		return null;
	}

	protected List<String> callRecurlySafe(final AsyncHttpClient.BoundRequestBuilder builder) {
		final String requestKey = getApiKey();
		final RecurlyAPICallResults<String> results = doSinglePageRecurlySafeCall(builder, new RecurlyAPICallResults<String>(), requestKey);
		while(results.hasNextPage()){
			doSinglePageRecurlySafeCall(client.prepareGet(results.getNextPageUrl()), results, requestKey);
		}
		
		return results.getResults();
	}
	
	protected RecurlyAPICallResults<String> doSinglePageRecurlySafeCall(final AsyncHttpClient.BoundRequestBuilder builder, final RecurlyAPICallResults<String> pageResults, final String requestKey){
		try {
			final AtomicReference<Throwable> tRef = new AtomicReference<>();
			RecurlyAPICallResults<String> result = builder.addHeader("Authorization", "Basic " + requestKey).addHeader("Accept", "application/xml")
					.addHeader("Content-Type", "application/xml; charset=utf-8").execute(new AsyncCompletionHandler<RecurlyAPICallResults<String>>() {
						
						@Override
						public void onThrowable(Throwable t) {
							tRef.set(t); // We do this because it seems that Ning sporadically smothers the exceptions from onCompleted
						}

						@Override
						public RecurlyAPICallResults<String> onCompleted(final Response response) throws Exception {						
							if (response.getStatusCode() >= 300) {
								log.debug("Recurly error whilst calling: status[{}] body{}", response.getStatusCode(), response.getUri());
								log.debug("Recurly error: {}", response.getResponseBody());
								throw new RecurlyAPIException("Recurly error status:[" + response.getStatusCode() + "] error body: " + response.getResponseBody(), response.getStatusCode());
							}

							final InputStream in = response.getResponseBodyAsStream();
							try {
								String payload = convertStreamToString(in);
								if (debug()) {
									log.info("Msg from Recurly API :: {}", payload);
								}
								pageResults.getResults().add(payload);
								pageResults.setNextPageUrl(getPageUrlFromResponseHeader(response));
								
								return pageResults;
							} finally {
								closeStream(in);
							}
						}
					}).get();
			// TODO: Investigate why sometimes when we encounter exceptions we don't bubble it up but rather Ning somehow smothers it and returns null
			// ---> Begin hacky workaround <---
			if(result == null){
				Throwable encounteredException = tRef.get();
				// See if we encountered a Recurly API Exception
				RecurlyAPIException apiE = unwrapRecurlyAPIException(encounteredException);
				if(apiE != null){
					throw apiE;
				}
				throw new RecurlyException("Execution error", encounteredException);
			}
			// ---> End hacky workaround <---
			
			return result;
		} catch (IOException e) {
			log.warn("Error while calling Recurly", e);
			throw new RecurlyAPIException("Error while calling Recurly", e);
		} catch (ExecutionException e) {
			Throwable t = e;
			// Unwrap any of the API exceptions
			RecurlyAPIException apiE = unwrapRecurlyAPIException(t);
			if(apiE != null){
				throw apiE;
			}
			throw new RecurlyException("Execution error", e);
		} catch (InterruptedException e) {
			log.error("Interrupted while calling Recurly", e);
			throw new RecurlyException("Interrupted while calling Recurly", e);
		}
	}
	
	public static RecurlyAPIException unwrapRecurlyAPIException(Throwable t){
		do {
			if (t instanceof RecurlyAPIException) {
				return (RecurlyAPIException) t;
			}
			t = t.getCause();
		}while(t != null);
		
		return null;
	}

	private String getPageUrlFromResponseHeader(Response response) {
		// TODO: There is probably a less hacky way to parse the pagination
		// header...

		String header = response.getHeader(RECURLY_PAGINATION_HEADER);
		if (header != null) {
			/*
			 * EXAMPLE:
			 * 
			 * Status: 200 OK X-Records: 204 Link:
			 * <https://api.recurly.com/v2/transactions>; rel="start",
			 * <https://api.recurly.com/v2/transactions?cursor=-1241412>;
			 * rel="prev"
			 * <https://api.recurly.com/v2/transactions?cursor=124142>;
			 * rel="next" ETag: "c7431fcfc386fd59ee6c3c2e9ac2a30c"
			 */
			for (String paginationInfo : header.split(",")) {
				String[] p = paginationInfo.split(";");
				if (p.length >= 2) {
					String pageType = p[1].trim();
					if (pageType.equals("rel=\"next\"")) {
						try {
							return new URL(p[0].trim().replaceAll("<|>", "")).toString();
						} catch (IOException e) {
							log.warn("Unable to understand pagination url[" + p[0] + "]");
						}
					}
				}
			}
		}

		return null;
	}

	protected <T> List<T> deserialize(List<String> data, @Nullable final Class<T> clazz) throws JsonParseException, JsonMappingException, IOException {
		List<T> results = new ArrayList<>();
		for (String dataItem : data) {
			T obj = xmlMapper.readValue(dataItem, clazz);
			results.add(obj);
		}

		return results;
	}

	protected String convertStreamToString(java.io.InputStream is) {
		try {
			return new java.util.Scanner(is).useDelimiter("\\A").next();
		} catch (java.util.NoSuchElementException e) {
			return "";
		}
	}

	protected void closeStream(final InputStream in) {
		if (in != null) {
			try {
				in.close();
			} catch (IOException e) {
				log.warn("Failed to close http-client - provided InputStream: {}", e.getLocalizedMessage());
			}
		}
	}

	protected AsyncHttpClient createHttpClient() {
		// Don't limit the number of connections per host
		// See https://github.com/ning/async-http-client/issues/issue/28
		final AsyncHttpClientConfig.Builder builder = new AsyncHttpClientConfig.Builder();
		builder.setMaximumConnectionsPerHost(-1);
		builder.setUserAgent("");
		return new AsyncHttpClient(builder.build());
	}

	protected class RecurlyAPICallResults<T> {
		private String nextPageUrl = null;
		private List<T> results = new ArrayList<>();

		private RecurlyAPICallResults() {
			super();
		}

		public String getNextPageUrl() {
			return nextPageUrl;
		}

		public void setNextPageUrl(String nextPageUrl) {
			this.nextPageUrl = nextPageUrl;
		}

		public List<T> getResults() {
			return results;
		}

		public boolean hasNextPage(){
			return nextPageUrl != null;
		}
	}
}
