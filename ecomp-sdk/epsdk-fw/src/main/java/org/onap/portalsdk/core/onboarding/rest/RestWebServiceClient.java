/*
 * ============LICENSE_START==========================================
 * ONAP Portal SDK
 * ===================================================================
 * Copyright © 2017 AT&T Intellectual Property. All rights reserved.
 * ===================================================================
 *
 * Unless otherwise specified, all software contained herein is licensed
 * under the Apache License, Version 2.0 (the "License");
 * you may not use this software except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *             http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Unless otherwise specified, all documentation contained herein is licensed
 * under the Creative Commons License, Attribution 4.0 Intl. (the "License");
 * you may not use this documentation except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *             https://creativecommons.org/licenses/by/4.0/
 *
 * Unless required by applicable law or agreed to in writing, documentation
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ============LICENSE_END============================================
 *
 * ECOMP is a trademark and service mark of AT&T Intellectual Property.
 */
package org.onap.portalsdk.core.onboarding.rest;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;
import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.onap.portalsdk.core.onboarding.util.PortalApiConstants;
import org.onap.portalsdk.core.onboarding.util.PortalApiProperties;
import org.owasp.esapi.ESAPI;

/**
 * Simple REST client for GET, POST and DELETE operations against the Portal
 * application.
 */
public class RestWebServiceClient {

	private final Log logger = LogFactory.getLog(RestWebServiceClient.class);

	/**
	 * Singleton instance
	 */
	private static RestWebServiceClient instance = null;

	/**
	 * Constructor is private. Clients should obtain an instance via getInstance().
	 */
	private RestWebServiceClient() {
	}

	/**
	 * Gets the static instance of RestWebServiceClient; creates it if necessary.
	 * Synchronized to be thread safe.
	 * 
	 * @return Static instance of RestWebServiceClient.
	 */
	public static synchronized RestWebServiceClient getInstance() {
		if (instance == null)
			instance = new RestWebServiceClient();
		return instance;
	}

	/**
	 * Convenience method that fetches the URL for the Portal REST API endpoint and
	 * the application UEB key, then calls
	 * {@link #get(String, String, String, String, String, String, String)} to
	 * access the Portal's REST endpoint.
	 * 
	 * @param restPath
	 *            Partial path of the endpoint; e.g., "/specialRestService"
	 * @param userId
	 *            userId for the user originating the request
	 * @param appName
	 *            Application Name for logging.
	 * @param requestId
	 *            128-bit UUID value to uniquely identify the transaction.
	 * @param appUserName
	 *            REST API user name for Portal to authenticate the request
	 * @param appPassword
	 *            REST API password (in the clear, not encrypted) for Portal to
	 *            authenticate the request
	 * @param isBasicAuth
	 *            If true, send credentials as HTTP Basic Authentication
	 * @return Content from REST endpoint
	 * @throws IOException
	 *             on any failure
	 */
	public String getPortalContent(String restPath, String userId, String appName, String requestId, String appUserName,
			String appPassword, boolean isBasicAuth) throws IOException {
		String restURL = PortalApiProperties.getProperty(PortalApiConstants.ECOMP_REST_URL);
		if (restURL == null) {
			String msg = "getPortalContent: failed to get property " + PortalApiConstants.ECOMP_REST_URL;
			logger.error(msg);
			throw new IOException(msg);
		}
		String appUebKey = PortalApiProperties.getProperty(PortalApiConstants.UEB_APP_KEY);
		if (appUebKey == null) {
			String msg = "getPortalContent: failed to get property " + PortalApiConstants.UEB_APP_KEY;
			logger.error(msg);
			throw new IOException(msg);
		}
		final String restEndpointUrl = restURL + restPath;
		if (isBasicAuth) {
			return get(restEndpointUrl, userId, appName, requestId, appUebKey, appUserName, appPassword, isBasicAuth);
		}
		return get(restEndpointUrl, userId, appName, requestId, appUebKey, appUserName, appPassword);
	}

	/**
	 * Makes a call to a Portal REST API using the specified URL and parameters.
	 * 
	 * @param url
	 *            Complete URL of the REST endpoint.
	 * @param loginId
	 *            User that it should be fetching the data
	 * @param appName
	 *            Application name for logging; if null or empty, defaulted to
	 *            Unknown.
	 * @param requestId
	 *            128-bit UUID value to uniquely identify the transaction; if null
	 *            or empty, one is generated.
	 * @param appUebKey
	 *            Unique key for the application, used by Portal to authenticate the
	 *            request
	 * @param appUserName
	 *            REST API user name, used by Portal to authenticate the request
	 * @param appPassword
	 *            REST API password, used by Portal to authenticate the request
	 * @return Content from REST endpoint
	 * @throws IOException
	 *             On any failure; e.g., unknown host.
	 */
	public String get(String url, String loginId, String appName, String requestId, String appUebKey,
			String appUserName, String appPassword) throws IOException {
		return get(url, loginId, appName, requestId, appUebKey, appUserName, appPassword, false);
	}

	/**
	 * Makes a call to a Portal REST API using the specified URL and parameters.
	 * 
	 * @param url
	 *            Complete URL of the REST endpoint.
	 * @param loginId
	 *            User that it should be fetching the data
	 * @param appName
	 *            Application name for logging; if null or empty, defaulted to
	 *            Unknown.
	 * @param requestId
	 *            128-bit UUID value to uniquely identify the transaction; if null
	 *            or empty, one is generated.
	 * @param appUebKey
	 *            Unique key for the application, used by Portal to authenticate the
	 *            request
	 * @param appUserName
	 *            REST API user name, used by Portal to authenticate the request
	 * @param appPassword
	 *            REST API password, used by Portal to authenticate the request
	 * @param useBasicAuth
	 *            If true, send credentials as HTTP Basic Authentication
	 * @return Content from REST endpoint
	 * @throws IOException
	 *             On any failure; e.g., unknown host.
	 */
	public String get(String url, String loginId, String appName, String requestId, String appUebKey,
			String appUserName, String appPassword, Boolean useBasicAuth) throws IOException {

		logger.debug("RestWebServiceClient.get (" + url + ") operation is started.");

		if (appName == null || appName.trim().length() == 0)
			appName = "Unknown";
		if (requestId == null || requestId.trim().length() == 0)
			requestId = UUID.randomUUID().toString();

		URL obj = new URL(url);
		// Create the connection object
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		con.setRequestMethod("GET");
		// if the portal property is set then gets the timeout value from portal
		// properties
		con.setConnectTimeout(3000);
		if (PortalApiProperties.getProperty(PortalApiConstants.EXT_REQUEST_CONNECTION_TIMEOUT) != null)
			con.setConnectTimeout(Integer
					.parseInt(PortalApiProperties.getProperty(PortalApiConstants.EXT_REQUEST_CONNECTION_TIMEOUT)));
		con.setReadTimeout(8000);
		if (PortalApiProperties.getProperty(PortalApiConstants.EXT_REQUEST_READ_TIMEOUT) != null)
			con.setReadTimeout(
					Integer.parseInt(PortalApiProperties.getProperty(PortalApiConstants.EXT_REQUEST_READ_TIMEOUT)));

		// add request header
		con.setRequestProperty("uebkey", appUebKey);
		if (loginId != null) {
			con.setRequestProperty("LoginId", ESAPI.encoder().canonicalize(loginId));
		}
		con.setRequestProperty("user-agent", appName);
		con.setRequestProperty("X-ECOMP-RequestID", requestId);
		con.setRequestProperty("username", appUserName);
		con.setRequestProperty("password", appPassword);
		if (useBasicAuth) {
			String encoding = Base64.getEncoder().encodeToString((appUserName + ":" + appPassword).getBytes());
			con.setRequestProperty("Authorization", "Basic " + encoding);
		}

		int responseCode = con.getResponseCode();
		logger.debug("get: received response code '" + responseCode + "' while getting the '" + url + "' for user: "
				+ loginId);

		final String response = readAndCloseStream(con.getInputStream());
		if (logger.isDebugEnabled())
			logger.debug("get: url " + url + " yielded " + response);
		return response;
	}

	/**
	 * Convenience method that fetches the URL for the Portal REST API endpoint and
	 * the application UEB key, then calls
	 * {@link #post(String, String, String, String, String, String, String, String, String, boolean)}
	 * to access the Portal's REST endpoint.
	 * 
	 * @param restPath
	 *            Partial path of the endpoint; e.g., "/specialRestService"
	 * @param userId
	 *            ID for the user originating the request
	 * @param appName
	 *            Application Name for logging.
	 * @param requestId
	 *            128-bit UUID value to uniquely identify the transaction.
	 * @param appUserName
	 *            REST API user name for Portal to authenticate the request; ignored
	 *            if null
	 * @param appPassword
	 *            REST API password (in the clear, not encrypted) for Portal to
	 *            authenticate the request; ignored if null
	 * @param contentType
	 *            content type for header
	 * @param content
	 *            String to post
	 * @param isBasicAuth
	 *            If true, use HTTP Basic Authentication
	 * @return Content from REST endpoint
	 * @throws IOException
	 *             on any failure
	 */
	public String postPortalContent(String restPath, String userId, String appName, String requestId,
			String appUserName, String appPassword, String contentType, String content, boolean isBasicAuth)
			throws IOException {
		String restURL = PortalApiProperties.getProperty(PortalApiConstants.ECOMP_REST_URL);

		if (restURL == null) {
			String msg = "getPortalContent: failed to get property " + PortalApiConstants.ECOMP_REST_URL;
			logger.error(msg);
			throw new IOException(msg);
		}
		String appUebKey = PortalApiProperties.getProperty(PortalApiConstants.UEB_APP_KEY);
		if (appUebKey == null) {
			String msg = "getPortalContent: failed to get property " + PortalApiConstants.UEB_APP_KEY;
			logger.error(msg);
			throw new IOException(msg);
		}
		final String separator = restURL.endsWith("/") || restPath.startsWith("/") ? "" : "/";
		final String restEndpointUrl = restURL + separator + restPath;
		return post(restEndpointUrl, userId, appName, requestId, appUebKey, appUserName, appPassword, contentType,
				content, isBasicAuth);
	}

	/**
	 * Makes a POST call to a Portal REST API using the specified URL and
	 * parameters.
	 * 
	 * @param url
	 *            Complete URL of the REST endpoint.
	 * @param loginId
	 *            User who is fetching the data
	 * @param appName
	 *            Application name for logging; if null or empty, defaulted to
	 *            Unknown.
	 * @param requestId
	 *            128-bit UUID value to uniquely identify the transaction; if null
	 *            or empty, one is generated.
	 * @param appUebKey
	 *            Unique key for the application, used by Portal to authenticate the
	 *            request
	 * @param appUserName
	 *            REST API user name used by Portal to authenticate the request;
	 *            ignored if null
	 * @param appPassword
	 *            REST API password used by Portal to authenticate the request;
	 *            ignored if null
	 * @param contentType
	 *            MIME header
	 * @param content
	 *            Content to POST
	 * @param isBasicAuth
	 *            If true, use HTTP Basic Authentication
	 * @return Any content read from the endpoint
	 * @throws IOException
	 *             On any error
	 */
	public String post(String url, String loginId, String appName, String requestId, String appUebKey,
			String appUserName, String appPassword, String contentType, String content, boolean isBasicAuth)
			throws IOException {

		if (logger.isDebugEnabled())
			logger.debug("RestWebServiceClient.post to URL " + url);
		if (appName == null || appName.trim().length() == 0)
			appName = "Unknown";
		if (requestId == null || requestId.trim().length() == 0)
			requestId = UUID.randomUUID().toString();

		URL obj = new URL(url);
		// Create the connection object
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		con.setRequestMethod("POST");

		con.setConnectTimeout(3000);
		// if the portal property is set then gets the timeout value from portal
		// properties
		if (PortalApiProperties.getProperty(PortalApiConstants.EXT_REQUEST_CONNECTION_TIMEOUT) != null)
			con.setConnectTimeout(Integer
					.parseInt(PortalApiProperties.getProperty(PortalApiConstants.EXT_REQUEST_CONNECTION_TIMEOUT)));
		con.setReadTimeout(15000);
		if (PortalApiProperties.getProperty(PortalApiConstants.EXT_REQUEST_READ_TIMEOUT) != null)
			con.setReadTimeout(
					Integer.parseInt(PortalApiProperties.getProperty(PortalApiConstants.EXT_REQUEST_READ_TIMEOUT)));

		// add request header
		con.setRequestProperty("uebkey", appUebKey);
		if (appUserName != null)
			con.setRequestProperty("username", appUserName);
		if (appPassword != null)
			con.setRequestProperty("password", appPassword);
		con.setRequestProperty("LoginId", loginId);
		con.setRequestProperty("user-agent", appName);
		con.setRequestProperty("X-ECOMP-RequestID", requestId);
		con.setRequestProperty("Content-Type", contentType);
		if (isBasicAuth) {
			String encoding = Base64.getEncoder().encodeToString((appUserName + ":" + appPassword).getBytes());
			con.setRequestProperty("Authorization", "Basic " + encoding);
		}

		con.setDoInput(true);
		con.setDoOutput(true);
		if (content != null) {
			con.getOutputStream().write(content.getBytes());
		}
		con.getOutputStream().flush();
		con.getOutputStream().close();

		int responseCode = con.getResponseCode();
		logger.debug("RestWebServiceClient.post: response code " + responseCode);
		final String response = readAndCloseStream(con.getInputStream());
		return response;
	}

	/**
	 * Convenience method that fetches the URL for the Portal REST API endpoint and
	 * the application UEB key, then calls
	 * {@link #delete(String, String, String, String, String, String, String, String, String, boolean)}
	 * to access the Portal's REST endpoint.
	 * 
	 * @param restPath
	 *            Complete URL of the REST endpoint.
	 * @param userId
	 *            User who is fetching the data
	 * @param appName
	 *            Application name for logging; if null or empty, defaulted to
	 *            Unknown.
	 * @param requestId
	 *            128-bit UUID value to uniquely identify the transaction; if null
	 *            or empty, one is generated.
	 * @param appUserName
	 *            REST API user name used by Portal to authenticate the request;
	 *            ignored if null
	 * @param appPassword
	 *            REST API password used by Portal to authenticate the request;
	 *            ignored if null
	 * @param contentType
	 *            MIME header
	 * @param content
	 *            Content
	 * @param isBasicAuth
	 *            If true, use HTTP Basic Authentication
	 * @return Any content read from the endpoint
	 * @throws IOException
	 *             On any error
	 */
	public String deletePortalContent(String restPath, String userId, String appName, String requestId,
			String appUserName, String appPassword, String contentType, String content, boolean isBasicAuth)
			throws IOException {
		String restURL = PortalApiProperties.getProperty(PortalApiConstants.ECOMP_REST_URL);
		if (restURL == null) {
			String msg = "deletePortalContent: failed to get property " + PortalApiConstants.ECOMP_REST_URL;
			logger.error(msg);
			throw new IOException(msg);
		}
		String appUebKey = PortalApiProperties.getProperty(PortalApiConstants.UEB_APP_KEY);
		if (appUebKey == null) {
			String msg = "deletePortalContent: failed to get property " + PortalApiConstants.UEB_APP_KEY;
			logger.error(msg);
			throw new IOException(msg);
		}
		final String separator = restURL.endsWith("/") || restPath.startsWith("/") ? "" : "/";
		final String restEndpointUrl = restURL + separator + restPath;
		return delete(restEndpointUrl, userId, appName, requestId, appUebKey, appUserName, appPassword, contentType,
				content, isBasicAuth);
	}

	/**
	 * Makes a DELETE call to a Portal REST API using the specified URL and
	 * parameters.
	 * 
	 * @param url
	 *            Complete URL of the REST endpoint.
	 * @param loginId
	 *            User who is fetching the data
	 * @param appName
	 *            Application name for logging; if null or empty, defaulted to
	 *            Unknown.
	 * @param requestId
	 *            128-bit UUID value to uniquely identify the transaction; if null
	 *            or empty, one is generated.
	 * @param appUebKey
	 *            Unique key for the application, used by Portal to authenticate the
	 *            request
	 * @param appUserName
	 *            REST API user name used by Portal to authenticate the request;
	 *            ignored if null
	 * @param appPassword
	 *            REST API password used by Portal to authenticate the request;
	 *            ignored if null
	 * @param contentType
	 *            MIME header
	 * @param content
	 *            Content
	 * @param isBasicAuth
	 *            If true, use HTTP Basic Authentication
	 * @return Any content read from the endpoint
	 * @throws IOException
	 *             On any error
	 */
	public String delete(String url, String loginId, String appName, String requestId, String appUebKey,
			String appUserName, String appPassword, String contentType, String content, boolean isBasicAuth)
			throws IOException {

		if (logger.isDebugEnabled())
			logger.debug("RestWebServiceClient.delete to URL " + url);
		if (appName == null || appName.trim().length() == 0)
			appName = "Unknown";
		if (requestId == null || requestId.trim().length() == 0)
			requestId = UUID.randomUUID().toString();

		URL obj = new URL(url);
		// Create the connection object
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		con.setRequestMethod("DELETE");
		con.setConnectTimeout(3000);
		// if the portal property is set then gets the timeout value from portal
		// properties
		if (PortalApiProperties.getProperty(PortalApiConstants.EXT_REQUEST_CONNECTION_TIMEOUT) != null)
			con.setConnectTimeout(Integer
					.parseInt(PortalApiProperties.getProperty(PortalApiConstants.EXT_REQUEST_CONNECTION_TIMEOUT)));
		con.setReadTimeout(15000);
		if (PortalApiProperties.getProperty(PortalApiConstants.EXT_REQUEST_READ_TIMEOUT) != null)
			con.setReadTimeout(
					Integer.parseInt(PortalApiProperties.getProperty(PortalApiConstants.EXT_REQUEST_READ_TIMEOUT)));

		// add request header
		con.setRequestProperty("uebkey", appUebKey);
		if (appUserName != null)
			con.setRequestProperty("username", appUserName);
		if (appPassword != null)
			con.setRequestProperty("password", appPassword);
		con.setRequestProperty("LoginId", loginId);
		con.setRequestProperty("user-agent", appName);
		con.setRequestProperty("X-ECOMP-RequestID", requestId);
		con.setRequestProperty("Content-Type", contentType);
		if (isBasicAuth) {
			String encoding = Base64.getEncoder().encodeToString((appUserName + ":" + appPassword).getBytes());
			con.setRequestProperty("Authorization", "Basic " + encoding);
		}

		con.setDoInput(true);
		con.setDoOutput(true);
		if (content != null)
			con.getOutputStream().write(content.getBytes());
		con.getOutputStream().flush();
		con.getOutputStream().close();

		int responseCode = con.getResponseCode();
		logger.debug("RestWebServiceClient.delete: response code " + responseCode);
		final String response = readAndCloseStream(con.getInputStream());
		return response;
	}

	/**
	 * Reads content of stream, decodes as UTF-8, and returns as string.
	 * 
	 * @param inputStream
	 *            Stream to read
	 * @return String read
	 * @throws IOException
	 */
	private String readAndCloseStream(InputStream inputStream) throws IOException {
		StringBuilder sb = new StringBuilder();
		try (InputStreamReader in = new InputStreamReader(inputStream, "UTF-8")) {
			char[] buf = new char[8196];
			int bytes;
			while ((bytes = in.read(buf)) > 0)
				sb.append(new String(buf, 0, bytes));
		} catch (Exception ex) {
			logger.error("readAndCloseStream", ex);
		}
		return sb.toString();
	}

}
