/*
 * Copyright (c) 2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 * 
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.wso2.mdm.agent.proxy.utils;

import android.util.Log;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;
import org.wso2.mdm.agent.proxy.IDPTokenManagerException;
import org.wso2.mdm.agent.proxy.IdentityProxy;
import org.wso2.mdm.agent.proxy.R;
import org.wso2.mdm.agent.proxy.beans.APIUtilities;

import java.io.*;
import java.security.*;
import java.security.cert.CertificateException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;

/**
 * Handle network communication between SDK and authorization server
 */
public class ServerUtilities {
	private final static String TAG = "ServerUtilities";
	private static final DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss",
	                                                                  Locale.getDefault());

	/**
	 * Validate the token expiration date.
	 *
	 * @param expirationDate - Token expiration date.
	 * @return - Token status.
	 */
	public static boolean isValid(Date expirationDate) {
		Date currentDate = new Date();
		String formattedDate = dateFormat.format(currentDate);
		currentDate = convertDate(formattedDate);

		boolean isExpired = currentDate.after(expirationDate);
		boolean isEqual = currentDate.equals(expirationDate);
		if (isExpired == true || isEqual == true) {
			return true;
		}

		return false;
	}

	/**
	 * Convert the date to the standard format.
	 *
	 * @param date - Date as a string.
	 * @return - Formatted date.
	 */
	public static Date convertDate(String date) {
		Date receivedDate = null;
		try {
			receivedDate = dateFormat.parse(date);
		} catch (ParseException e) {
			Log.e(TAG, "Invalid date format." + e);
		}

		return receivedDate;
	}

	public static String buildPayload(Map<String, String> params) {
		if (params == null) {
			return null;
		}
		StringBuilder bodyBuilder = new StringBuilder();
		Iterator<Entry<String, String>> iterator = params.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<String, String> param = iterator.next();
			bodyBuilder.append(param.getKey()).append('=').append(param.getValue());
			if (iterator.hasNext()) {
				bodyBuilder.append('&');
			}
		}
		return bodyBuilder.toString();
	}

	public static HttpRequestBase buildHeaders(HttpRequestBase httpRequestBase,
	                                           Map<String, String> headers, String httpMethod) {
		Iterator<Entry<String, String>> iterator = headers.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<String, String> header = iterator.next();
			httpRequestBase.setHeader(header.getKey(), header.getValue());
		}
		return httpRequestBase;
	}

	public static Map<String, String> postData(APIUtilities apiUtilities,
	                                           Map<String, String> headers)
			throws IDPTokenManagerException {

		String httpMethod = apiUtilities.getHttpMethod();
		String message;
		String url = apiUtilities.getEndPoint();
		JSONObject params = apiUtilities.getRequestParams();
		Map<String, String> responseParams = new HashMap<String, String>();
		HttpClient httpClient = getCertifiedHttpClient();

		if (httpMethod.equals(Constants.POST_METHOD)) {
			HttpPost httpPost = new HttpPost(url);
			if (params != null) {
				try {
					httpPost.setEntity(new StringEntity(params.toString()));
				} catch (UnsupportedEncodingException e) {
					message = "Invalid encoding type.";
					throw new IDPTokenManagerException(message, e);
				}
			} else {
				httpPost.setEntity(null);
			}

			HttpPost httpPostWithHeaders = (HttpPost) buildHeaders(httpPost, headers, httpMethod);
			try {
				HttpResponse response = httpClient.execute(httpPostWithHeaders);
				String status = String.valueOf(response.getStatusLine().getStatusCode());

				responseParams.put(Constants.SERVER_RESPONSE_BODY, getResponseBody(response));
				responseParams.put(Constants.SERVER_RESPONSE_STATUS, status);

			} catch (ClientProtocolException e) {
				message = "Invalid client protocol.";
				throw new IDPTokenManagerException(message, e);
			} catch (IOException e) {
				message = "Server connectivity failure.";
				responseParams.put(Constants.SERVER_RESPONSE_BODY, "Internal Server Error");
				responseParams.put(Constants.SERVER_RESPONSE_STATUS,
				                   Constants.INTERNAL_SERVER_ERROR);
				throw new IDPTokenManagerException(message, e);
			}
		} else if (httpMethod.equals(Constants.GET_METHOD)) {
			HttpGet httpGet = new HttpGet(url);
			HttpGet httpGetWithHeaders = (HttpGet) buildHeaders(httpGet, headers, httpMethod);

			try {
				HttpResponse response = httpClient.execute(httpGetWithHeaders);
				responseParams.put(Constants.SERVER_RESPONSE_BODY, getResponseBody(response));
				responseParams.put(Constants.SERVER_RESPONSE_STATUS,
				                   String.valueOf(response.getStatusLine().getStatusCode()));
			} catch (ClientProtocolException e) {
				message = "Invalid client protocol.";
				throw new IDPTokenManagerException(message, e);
			} catch (IOException e) {
				responseParams.put(Constants.SERVER_RESPONSE_BODY, "Internal Server Error");
				responseParams.put(Constants.SERVER_RESPONSE_STATUS,
				                   Constants.INTERNAL_SERVER_ERROR);
				message = "Server connectivity failure.";
				throw new IDPTokenManagerException(message, e);
			}
		}

		return responseParams;

	}

	public static Map<String, String> postDataAPI(APIUtilities apiUtilities,
	                                              Map<String, String> headers)
			throws IDPTokenManagerException {
		String httpMethod = apiUtilities.getHttpMethod();
		String message;
		String url = apiUtilities.getEndPoint();
		Map<String, String> params = apiUtilities.getRequestParamsMap();

		Map<String, String> responseParams = new HashMap<String, String>();
		HttpClient httpclient = getCertifiedHttpClient();
		String payload = buildPayload(params);

		if (httpMethod.equals(Constants.POST_METHOD)) {
			HttpPost httpPost = new HttpPost(url);
			HttpPost httpPostWithHeaders = (HttpPost) buildHeaders(httpPost, headers, httpMethod);
			byte[] postData = payload.getBytes();
			try {
				httpPostWithHeaders.setEntity(new ByteArrayEntity(postData));
				HttpResponse response = httpclient.execute(httpPostWithHeaders);
				String status = String.valueOf(response.getStatusLine().getStatusCode());

				responseParams.put(Constants.SERVER_RESPONSE_BODY, getResponseBody(response));
				responseParams.put(Constants.SERVER_RESPONSE_STATUS, status);
				return responseParams;
			} catch (ClientProtocolException e) {
				message = "Invalid client protocol.";
				throw new IDPTokenManagerException(message, e);
			} catch (IOException e) {
				responseParams.put(Constants.SERVER_RESPONSE_BODY, "Internal Server Error");
				responseParams.put(Constants.SERVER_RESPONSE_STATUS,
				                   Constants.INTERNAL_SERVER_ERROR);
				message = "Server connectivity failure.";
				throw new IDPTokenManagerException(message, e);
			}
		}

		return responseParams;
	}

	public static HttpClient getCertifiedHttpClient() throws IDPTokenManagerException {
		HttpClient client = null;
		InputStream inStream = null;
		String message;
		try {
			if (Constants.SERVER_PROTOCOL.equalsIgnoreCase("https://")) {
				KeyStore localTrustStore = KeyStore.getInstance("BKS");
				inStream =
						IdentityProxy.getInstance().getContext().getResources()
						             .openRawResource(R.raw.emm_truststore);
				localTrustStore.load(inStream, Constants.TRUSTSTORE_PASSWORD.toCharArray());

				SchemeRegistry schemeRegistry = new SchemeRegistry();
				schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(),
				                                   80));
				SSLSocketFactory sslSocketFactory = new SSLSocketFactory(localTrustStore);
				sslSocketFactory.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
				schemeRegistry.register(new Scheme("https", sslSocketFactory, 443));
				HttpParams params = new BasicHttpParams();
				ClientConnectionManager connectionManager =
						new ThreadSafeClientConnManager(params,
						                                schemeRegistry);

				client = new DefaultHttpClient(connectionManager, params);

			} else {
				client = new DefaultHttpClient();
			}

		} catch (KeyStoreException e) {
			message = "Invalid keystore.";
			throw new IDPTokenManagerException(message, e);
		} catch (CertificateException e) {
			message = "Invalid certificate.";
			throw new IDPTokenManagerException(message, e);
		} catch (NoSuchAlgorithmException e) {
			message = "Keystore algorithm does not match.";
			throw new IDPTokenManagerException(message, e);
		} catch (UnrecoverableKeyException e) {
			message = "Invalid keystore.";
			throw new IDPTokenManagerException(message, e);
		} catch (KeyManagementException e) {
			message = "Invalid keystore.";
			throw new IDPTokenManagerException(message, e);
		} catch (IOException e) {
			message = "Trust store failed to load.";
			throw new IDPTokenManagerException(message, e);
		} finally {
			StreamHandler.closeInputStream(inStream, TAG);
		}

		return client;
	}

	public static String getResponseBody(HttpResponse response) throws IDPTokenManagerException {

		String responseBody = null;
		HttpEntity entity = null;
		String message;

		try {
			entity = response.getEntity();
			responseBody = getResponseBodyContent(entity);
		} catch (ParseException e) {
			message = "Invalid response.";
			throw new IDPTokenManagerException(message, e);
		} catch (IOException e) {
			if (entity != null) {
				try {
					entity.consumeContent();
				} catch (IOException ex) {
					message = "HTTP Response failure.";
					throw new IDPTokenManagerException(message, e);
				}
			}
		}

		return responseBody;
	}

	public static String getResponseBodyContent(final HttpEntity entity) throws IOException,
	                                                                            ParseException {

		if (entity == null) {
			throw new IllegalArgumentException("HTTP entity may not be null.");
		}

		InputStream instream = entity.getContent();

		if (entity.getContentLength() > Integer.MAX_VALUE) {
			throw new IllegalArgumentException("HTTP entity too large to be buffered in memory.");
		}

		String charset = getContentCharSet(entity);

		if (charset == null) {
			charset = HTTP.DEFAULT_CONTENT_CHARSET;
		}

		Reader reader = new InputStreamReader(instream, charset);
		StringBuilder buffer = new StringBuilder();

		try {
			char[] bufferSize = new char[1024];
			int length;

			while ((length = reader.read(bufferSize)) != -1) {
				buffer.append(bufferSize, 0, length);
			}
		} finally {
			reader.close();
		}

		return buffer.toString();

	}

	public static String getContentCharSet(final HttpEntity entity) throws ParseException {

		if (entity == null) {
			throw new IllegalArgumentException("HTTP entity may not be null");
		}

		String charSet = null;

		if (entity.getContentType() != null) {
			HeaderElement values[] = entity.getContentType().getElements();

			if (values.length > 0) {
				NameValuePair param = values[0].getParameterByName("charset");

				if (param != null) {
					charSet = param.getValue();
				}
			}
		}

		return charSet;

	}

}
