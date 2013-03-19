package com.github.torbinsky.billing.recurly;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutionException;

import javax.annotation.Nullable;
import javax.xml.bind.DatatypeConverter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.introspect.AnnotationIntrospectorPair;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationIntrospector;
import com.github.torbinsky.billing.recurly.model.RecurlyObject;
import com.ning.http.client.AsyncCompletionHandler;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.AsyncHttpClientConfig;
import com.ning.http.client.Response;

/**
 * Basic/common client features such as managing the AsyncHttpClient etc...
 * 
 * @author twerner
 *
 */
public abstract class RecurlyClientBase {

	private static final Logger log = LoggerFactory.getLogger(RecurlyClientBase.class);

	public static final String RECURLY_DEBUG_KEY = "recurly.debug";
	public static final String RECURLY_PAGE_SIZE_KEY = "recurly.page.size";

	protected static final Integer DEFAULT_PAGE_SIZE = new Integer(20);
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

	private final XmlMapper xmlMapper = new XmlMapper();

	private String key;
	private final String baseUrl;
	private AsyncHttpClient client;

	public RecurlyClientBase(final String apiKey) {
		this(apiKey, "api.recurly.com", 443, "v2");
	}

	public RecurlyClientBase(final String apiKey, final String host, final int port, final String version) {
		setApiKey(apiKey);
		this.baseUrl = String.format("https://%s:%d/%s", host, port, version);

		final AnnotationIntrospector primary = new JacksonAnnotationIntrospector();
		final AnnotationIntrospector secondary = new JaxbAnnotationIntrospector(TypeFactory.defaultInstance());
		final AnnotationIntrospector pair = new AnnotationIntrospectorPair(primary, secondary);
		xmlMapper.setAnnotationIntrospector(pair);
		xmlMapper.registerModule(new JodaModule());
		xmlMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
	}

	protected void setApiKey(String apiKey) {
		this.key = DatatypeConverter.printBase64Binary(apiKey.getBytes());
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

	protected <T> T fetch(final String recurlyToken, final Class<T> clazz) {
		return doGET(FETCH_RESOURCE + "/" + recurlyToken, clazz);
	}

	// /////////////////////////////////////////////////////////////////////////

	protected <T> T doGET(final String resource, final Class<T> clazz) {
		StringBuffer url = new StringBuffer(baseUrl);
		url.append(resource);
		if (resource != null && !resource.contains("?")) {
			url.append("?");
		} else {
			url.append("&");
			url.append("&");
		}
		url.append(getPageSizeGetParam());

		if (debug()) {
			log.info("Msg to Recurly API [GET] :: URL : {}", url);
		}
		return callRecurlySafe(client.prepareGet(url.toString()), clazz);
	}

	protected <T> T doPOST(final String resource, final RecurlyObject payload, final Class<T> clazz) {
		final String xmlPayload;
		try {
			xmlPayload = xmlMapper.writeValueAsString(payload);
			if (debug()) {
				log.info("Msg to Recurly API [POST]:: URL : {}", baseUrl + resource);
				log.info("Payload for [POST]:: {}", xmlPayload);
			}
		} catch (IOException e) {
			log.warn("Unable to serialize {} object as XML: {}", clazz.getName(), payload.toString());
			return null;
		}

		return callRecurlySafe(client.preparePost(baseUrl + resource).setBody(xmlPayload), clazz);
	}

	protected <T> T doPUT(final String resource, final RecurlyObject payload, final Class<T> clazz) {
		final String xmlPayload;
		try {
			xmlPayload = xmlMapper.writeValueAsString(payload);
			if (debug()) {
				log.info("Msg to Recurly API [PUT]:: URL : {}", baseUrl + resource);
				log.info("Payload for [PUT]:: {}", xmlPayload);
			}
		} catch (IOException e) {
			log.warn("Unable to serialize {} object as XML: {}", clazz.getName(), payload.toString());
			return null;
		}

		return callRecurlySafe(client.preparePut(baseUrl + resource).setBody(xmlPayload), clazz);
	}

	protected void doDELETE(final String resource) {
		callRecurlySafe(client.prepareDelete(baseUrl + resource), null);
	}

	protected <T> T callRecurlySafe(final AsyncHttpClient.BoundRequestBuilder builder, @Nullable final Class<T> clazz) {
		try {
			return callRecurly(builder, clazz);
		} catch (IOException e) {
			log.warn("Error while calling Recurly", e);
			return null;
		} catch (ExecutionException e) {
			log.error("Execution error", e);
			return null;
		} catch (InterruptedException e) {
			log.error("Interrupted while calling Recurly", e);
			return null;
		}
	}

	protected <T> T callRecurly(final AsyncHttpClient.BoundRequestBuilder builder, @Nullable final Class<T> clazz) throws IOException,
			ExecutionException, InterruptedException {
		return builder.addHeader("Authorization", "Basic " + key).addHeader("Accept", "application/xml")
				.addHeader("Content-Type", "application/xml; charset=utf-8").execute(new AsyncCompletionHandler<T>() {
					@Override
					public T onCompleted(final Response response) throws Exception {
						if (response.getStatusCode() >= 300) {
							log.warn("Recurly error whilst calling: {}", response.getUri());
							log.warn("Recurly error: {}", response.getResponseBody());
							return null;
						}

						if (clazz == null) {
							return null;
						}

						final InputStream in = response.getResponseBodyAsStream();
						try {
							String payload = convertStreamToString(in);
							if (debug()) {
								log.info("Msg from Recurly API :: {}", payload);
							}
							T obj = xmlMapper.readValue(payload, clazz);
							return obj;
						} finally {
							closeStream(in);
						}
					}
				}).get();
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
		return new AsyncHttpClient(builder.build());
	}
}
