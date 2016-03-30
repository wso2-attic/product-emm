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
package org.wso2.emm.agent.proxy.utils;

import android.util.Log;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;
import org.wso2.emm.agent.proxy.IDPTokenManagerException;
import org.wso2.emm.agent.proxy.beans.EndPointInfo;
import org.wso2.emm.agent.proxy.clients.CommunicationClient;
import org.wso2.emm.agent.proxy.clients.CommunicationClientFactory;
import org.wso2.emm.agent.proxy.utils.Constants.HTTP_METHODS;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

/**
 * This class represents all the utilities used for network communication between SDK 
 * and authorization server.
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
		return isExpired || isEqual;

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

	public static HttpRequestBase buildHeaders(HttpRequestBase httpRequestBase, Map<String, String> headers) {
		for (Entry<String, String> header : headers.entrySet()) {
			httpRequestBase.setHeader(header.getKey(), header.getValue());
		}
		return httpRequestBase;
	}

	public static Map<String, String> postData(EndPointInfo endPointInfo, Map<String, String> headers)
			throws IDPTokenManagerException {

		HTTP_METHODS httpMethod = endPointInfo.getHttpMethod();
		String url = endPointInfo.getEndPoint();

        String params = null;

        if(endPointInfo != null && endPointInfo.getRequestParams() != null){
            params = endPointInfo.getRequestParams();
        }

        Map<String, String> responseParams = new HashMap<String, String>();
		addHeaders(headers);

		switch (httpMethod) {
			case GET:
				responseParams = sendGetRequest(url, headers);
				break;
			case POST:
				responseParams = sendPostRequest(url, params, headers);
				break;
            case DELETE:
                responseParams = sendDeleteRequest(url, headers);
                break;
            case PUT:
                responseParams = sendPutRequest(url, params, headers);
				break;
		}
		return responseParams;
	}

	public static Map<String, String> sendGetRequest(String url, Map<String, String> headers)
			throws IDPTokenManagerException {
		HttpGet httpGet = new HttpGet(url);
		HttpGet httpGetWithHeaders = (HttpGet) buildHeaders(httpGet, headers);
		HttpClient httpClient = getCertifiedHttpClient();
		Map<String, String> responseParams = new HashMap<String, String>();

		try {
			HttpResponse response = httpClient.execute(httpGetWithHeaders);
			responseParams.put(Constants.SERVER_RESPONSE_BODY, getResponseBody(response));
			responseParams.put(Constants.SERVER_RESPONSE_STATUS,
					String.valueOf(response.getStatusLine().getStatusCode()));
		} catch (ClientProtocolException e) {
			String errorMsg = "Error occurred while sending 'Get' request due to an invalid client protocol being used";
			responseParams.put(Constants.SERVER_RESPONSE_BODY, "Internal Server Error");
			responseParams.put(Constants.SERVER_RESPONSE_STATUS, Constants.INTERNAL_SERVER_ERROR);
			Log.e(TAG, errorMsg);
			throw new IDPTokenManagerException(errorMsg, e);
		} catch (IOException e) {
			String errorMsg = "Error occurred while sending 'Get' request due to failure of server connection";
			responseParams.put(Constants.SERVER_RESPONSE_BODY, "Internal Server Error");
			responseParams.put(Constants.SERVER_RESPONSE_STATUS, Constants.INTERNAL_SERVER_ERROR);
			Log.e(TAG, errorMsg);
			throw new IDPTokenManagerException(errorMsg, e);
		} catch (IllegalArgumentException e) {
			String errorMsg = "Error occurred while sending 'Get' request due to empty host name";
			responseParams.put(Constants.SERVER_RESPONSE_BODY, "Internal Server Error");
			responseParams.put(Constants.SERVER_RESPONSE_STATUS, Constants.INTERNAL_SERVER_ERROR);
			Log.e(TAG, errorMsg);
			throw new IDPTokenManagerException(errorMsg, e);
		}
		return responseParams;
	}

	private static void addHeaders(Map<String, String> headers) {
		CommunicationClientFactory communicationClientFactory = new CommunicationClientFactory();
		CommunicationClient communicationClient = communicationClientFactory.
				getClient(Constants.HttpClient.HTTP_CLIENT_IN_USE);
		if(communicationClient != null) {
			communicationClient.addAdditionalHeader(headers);
		}
	}

	public static HttpClient getCertifiedHttpClient() throws IDPTokenManagerException {
		CommunicationClientFactory communicationClientFactory = new CommunicationClientFactory();
		CommunicationClient communicationClient = communicationClientFactory.
				getClient(Constants.HttpClient.HTTP_CLIENT_IN_USE);
		return communicationClient.getHttpClient();
	}

    public static Map<String, String> sendDeleteRequest(String url, Map<String, String> headers)
            throws IDPTokenManagerException {

        Map<String, String> responseParams = new HashMap<String, String>();
        HttpDelete httpDelete = new HttpDelete(url);
        HttpDelete httpDeleteWithHeaders = (HttpDelete) buildHeaders(httpDelete, headers);

        try {

            HttpClient httpClient = getCertifiedHttpClient();

            HttpResponse response = httpClient.execute(httpDeleteWithHeaders);
            responseParams.put(Constants.SERVER_RESPONSE_BODY, getResponseBody(response));
            responseParams.put(Constants.SERVER_RESPONSE_STATUS,
                    String.valueOf(response.getStatusLine().getStatusCode()));

        } catch (ClientProtocolException e) {
			String errorMsg = "Error occurred while sending 'Delete' request due to an invalid client protocol being used";
			responseParams.put(Constants.SERVER_RESPONSE_BODY, "Internal Server Error");
			responseParams.put(Constants.SERVER_RESPONSE_STATUS, Constants.INTERNAL_SERVER_ERROR);
			Log.e(TAG, errorMsg);
			throw new IDPTokenManagerException(errorMsg, e);
        } catch (IOException e) {
			String errorMsg =
					"Error occurred while sending 'Delete' request due to failure of server connection";
			responseParams.put(Constants.SERVER_RESPONSE_BODY, "Internal Server Error");
			responseParams.put(Constants.SERVER_RESPONSE_STATUS, Constants.INTERNAL_SERVER_ERROR);
			Log.e(TAG, errorMsg);
			throw new IDPTokenManagerException(errorMsg, e);
        } catch (IllegalArgumentException e) {
	        String errorMsg = "Error occurred while sending 'Get' request due to empty host name";
	        responseParams.put(Constants.SERVER_RESPONSE_BODY, "Internal Server Error");
	        responseParams.put(Constants.SERVER_RESPONSE_STATUS, Constants.INTERNAL_SERVER_ERROR);
	        Log.e(TAG, errorMsg);
	        throw new IDPTokenManagerException(errorMsg, e);
        }

        return responseParams;
    }

    public static Map<String, String> sendPostRequest(String url, String params, Map<String, String> headers)
            throws IDPTokenManagerException {
        HttpPost httpPost = new HttpPost(url);
        HttpClient httpClient = getCertifiedHttpClient();
        Map<String, String> responseParams = new HashMap<String, String>();

        if (params != null) {
            try {
                httpPost.setEntity(new StringEntity(params));
            } catch (UnsupportedEncodingException e) {
                throw new IDPTokenManagerException("Invalid encoding type.", e);
            }
        }

		httpPost = (HttpPost) buildHeaders(httpPost, headers);
        try {
            HttpResponse response = httpClient.execute(httpPost);
            String status = String.valueOf(response.getStatusLine().getStatusCode());

            responseParams.put(Constants.SERVER_RESPONSE_BODY, getResponseBody(response));
            responseParams.put(Constants.SERVER_RESPONSE_STATUS, status);

        } catch (ClientProtocolException e) {
			String errorMsg = "Error occurred while sending 'Post' request due to an invalid client protocol being used";
			responseParams.put(Constants.SERVER_RESPONSE_BODY, "Internal Server Error");
			responseParams.put(Constants.SERVER_RESPONSE_STATUS, Constants.INTERNAL_SERVER_ERROR);
			Log.e(TAG, errorMsg);
			throw new IDPTokenManagerException(errorMsg, e);
        } catch (IOException e) {
			String errorMsg = "Error occurred while sending 'Post' request due to failure of server connection";
			responseParams.put(Constants.SERVER_RESPONSE_BODY, "Internal Server Error");
			responseParams.put(Constants.SERVER_RESPONSE_STATUS, Constants.INTERNAL_SERVER_ERROR);
			Log.e(TAG, errorMsg);
			throw new IDPTokenManagerException(errorMsg, e);
        } catch (IllegalArgumentException e) {
	        String errorMsg = "Error occurred while sending 'Get' request due to empty host name";
	        responseParams.put(Constants.SERVER_RESPONSE_BODY, "Internal Server Error");
	        responseParams.put(Constants.SERVER_RESPONSE_STATUS, Constants.INTERNAL_SERVER_ERROR);
	        Log.e(TAG, errorMsg);
	        throw new IDPTokenManagerException(errorMsg, e);
        }

        return responseParams;
    }

    public static Map<String, String> sendPutRequest(String url, String params, Map<String, String> headers)
		    throws IDPTokenManagerException {

        HttpPut httpPut = new HttpPut(url);
        HttpClient httpClient = getCertifiedHttpClient();
		httpPut = (HttpPut) buildHeaders(httpPut, headers);
        Map<String, String> responseParams = new HashMap<String, String>();

        if (params != null) {
            try {
	            httpPut.setEntity(new StringEntity(params));
            } catch (UnsupportedEncodingException e) {
                throw new IDPTokenManagerException("Invalid encoding type.", e);
            }
        }
        try {
            HttpResponse response = httpClient.execute(httpPut);
            String status = String.valueOf(response.getStatusLine().getStatusCode());
            responseParams.put(Constants.SERVER_RESPONSE_STATUS, status);
            responseParams.put(Constants.SERVER_RESPONSE_BODY, getResponseBody(response));

        } catch (ClientProtocolException e) {
			String errorMsg = "Error occurred while sending 'Put' request due to an invalid client protocol being used";
			responseParams.put(Constants.SERVER_RESPONSE_BODY, "Internal Server Error");
			responseParams.put(Constants.SERVER_RESPONSE_STATUS, Constants.INTERNAL_SERVER_ERROR);
			Log.e(TAG, errorMsg);
			throw new IDPTokenManagerException(errorMsg, e);
        } catch (IOException e) {
			String errorMsg = "Error occurred while sending 'Put' request due to failure of server connection";
			responseParams.put(Constants.SERVER_RESPONSE_BODY, "Internal Server Error");
			responseParams.put(Constants.SERVER_RESPONSE_STATUS, Constants.INTERNAL_SERVER_ERROR);
			Log.e(TAG, errorMsg);
			throw new IDPTokenManagerException(errorMsg, e);
        } catch (IllegalArgumentException e) {
	        String errorMsg = "Error occurred while sending 'Get' request due to empty host name";
	        responseParams.put(Constants.SERVER_RESPONSE_BODY, "Internal Server Error");
	        responseParams.put(Constants.SERVER_RESPONSE_STATUS, Constants.INTERNAL_SERVER_ERROR);
	        Log.e(TAG, errorMsg);
	        throw new IDPTokenManagerException(errorMsg, e);
        }
        return responseParams;
    }

	public static Map<String, String> postDataAPI(EndPointInfo endPointInfo, Map<String, String> headers)
			throws IDPTokenManagerException {
		HTTP_METHODS httpMethod = endPointInfo.getHttpMethod();
		String url = endPointInfo.getEndPoint();
		Map<String, String> params = endPointInfo.getRequestParamsMap();

		Map<String, String> responseParams = new HashMap<String, String>();
		HttpClient httpclient = getCertifiedHttpClient();
		String payload = buildPayload(params);

		if (httpMethod.equals(HTTP_METHODS.POST)) {		
			HttpPost httpPost = new HttpPost(url);
			httpPost = (HttpPost) buildHeaders(httpPost, headers);
			byte[] postData = payload.getBytes();
			try {
				httpPost.setEntity(new ByteArrayEntity(postData));
				HttpResponse response = httpclient.execute(httpPost);
				String status = String.valueOf(response.getStatusLine().getStatusCode());

				responseParams.put(Constants.SERVER_RESPONSE_BODY, getResponseBody(response));
				responseParams.put(Constants.SERVER_RESPONSE_STATUS, status);
				return responseParams;
			} catch (ClientProtocolException e) {
				String errorMsg = "Error occurred while sending 'Post' request due to an invalid client protocol being used";
				responseParams.put(Constants.SERVER_RESPONSE_BODY, "Internal Server Error");
				responseParams.put(Constants.SERVER_RESPONSE_STATUS, Constants.INTERNAL_SERVER_ERROR);
				Log.e(TAG, errorMsg);
				throw new IDPTokenManagerException(errorMsg, e);
			} catch (IOException e) {
				String errorMsg = "Error occurred while sending 'Post' request due to failure of server connection";
				responseParams.put(Constants.SERVER_RESPONSE_BODY, "Internal Server Error");
				responseParams.put(Constants.SERVER_RESPONSE_STATUS, Constants.INTERNAL_SERVER_ERROR);
				Log.e(TAG, errorMsg);
				throw new IDPTokenManagerException(errorMsg, e);
			} catch (IllegalArgumentException e) {
				String errorMsg = "Error occurred while sending 'Get' request due to empty host name";
				responseParams.put(Constants.SERVER_RESPONSE_BODY, "Internal Server Error");
				responseParams.put(Constants.SERVER_RESPONSE_STATUS, Constants.INTERNAL_SERVER_ERROR);
				Log.e(TAG, errorMsg);
				throw new IDPTokenManagerException(errorMsg, e);
			}
		}
		return responseParams;
	}

	public static String getResponseBody(HttpResponse response) throws IDPTokenManagerException {

		String responseBody = null;
		HttpEntity entity = null;

		try {
			entity = response.getEntity();
			responseBody = getResponseBodyContent(entity);
		} catch (ParseException e) {
			String errorMsg = "Error occurred while parsing response body.";
			Log.e(TAG, errorMsg);
			throw new IDPTokenManagerException(errorMsg, e);
		} catch (IOException e) {
			if (entity != null) {
				try {
					entity.consumeContent();
				} catch (IOException ex) {
					String errorMsg = "Error occurred due to failure of HTTP response.";
					Log.e(TAG, errorMsg);
					throw new IDPTokenManagerException(errorMsg, e);
				}
			}
		}
		return responseBody;
	}

	public static String getResponseBodyContent(final HttpEntity entity) throws IOException, ParseException {

		InputStream instream = entity.getContent();

		if (entity.getContentLength() > Integer.MAX_VALUE) {
			String errorMsg = "HTTP entity too large to be buffered in memory.";
			Log.e(TAG, errorMsg);
			throw new IllegalArgumentException(errorMsg);
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
