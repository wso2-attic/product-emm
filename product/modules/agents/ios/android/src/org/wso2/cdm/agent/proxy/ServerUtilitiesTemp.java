package org.wso2.cdm.agent.proxy;

import android.content.Context;
import android.util.Log;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.*;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.wso2.cdm.agent.R;
import org.wso2.cdm.agent.utils.CommonUtilities;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.security.KeyStore;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Handle network communication between SDK and authorization server
 */
public class ServerUtilitiesTemp {
    private final static String TAG = "ServerUtilities";
    private static boolean isSSLEnable = false;
    private static InputStream inputStream;
    private static String trustStorePassword;

    /**
     * Enable SSL communication between client application and authorization server (if you have selfish sign certificate)
     *
     * @param in
     * @param myTrustStorePassword
     */
    public static void enableSSL(InputStream in, String myTrustStorePassword) {
        inputStream = in;
        isSSLEnable = true;
        trustStorePassword = myTrustStorePassword;
    }

    public static String buildPayload(Map<String, String> params){
    	if(params==null){
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
    public static HttpRequestBase buildHeaders(HttpRequestBase httpRequestBase, Map<String, String> headers,String httpMethod){
    	Iterator<Entry<String, String>> iterator = headers.entrySet().iterator();
        while (iterator.hasNext()) {
                Entry<String, String> header = iterator.next();
                httpRequestBase.setHeader(header.getKey(), header.getValue());
        }
        return httpRequestBase;	  	  	
    }
    public static Map<String, String> postData(APIUtilities apiUtilities, Map<String, String> headers) {
    	String httpMethod = apiUtilities.getHttpMethod();
    	String url = apiUtilities.getEndPoint();
       	Map<String, String> params = apiUtilities.getRequestParams();
    	 	
    	Map<String, String> response_params = new HashMap<String, String>();
    	HttpClient httpclient = getCertifiedHttpClient();
    	String payload = buildPayload(params);

    	if(httpMethod.equals("POST")){
    		HttpPost httpPost = new HttpPost(url);
    		Log.e("url",""+url);
    		HttpPost httpPostWithHeaders = (HttpPost)buildHeaders(httpPost,headers,httpMethod);
    		byte[] postData = payload.getBytes();             
    	    try {
    	    	httpPostWithHeaders.setEntity(new ByteArrayEntity(postData));
    	        HttpResponse response = httpclient.execute(httpPostWithHeaders);
    	        String status = String.valueOf(response.getStatusLine().getStatusCode());
    	        Log.d(TAG,status);
    	        response_params.put("response", getResponseBody(response));
    	        response_params.put("status", status);
    	        return response_params;
    	    } catch (ClientProtocolException e) {
    	    	Log.d(TAG, "ClientProtocolException :"+e.toString());
    	        return null;
    	    } catch (IOException e) {
    	        Log.d(TAG, e.toString());
    	        response_params.put("response", "Internal Server Error");
    	        response_params.put("status", "500");
    	        return response_params;
    	    }  
    	}else if(httpMethod.equals("PUT")){
    		HttpPut httpPut = new HttpPut(url);
    		HttpPut httpPutWithHeaders = (HttpPut)buildHeaders(httpPut,headers,httpMethod);
    		byte[] putData = payload.getBytes();             
    	    try {
    	    	httpPutWithHeaders.setEntity(new ByteArrayEntity(putData));
    	        HttpResponse response = httpclient.execute(httpPutWithHeaders);
    	        response_params.put("response", getResponseBody(response));
    	        response_params.put("status", String.valueOf(response.getStatusLine().getStatusCode()));
    	        return response_params;
    	    } catch (ClientProtocolException e) {
    	    	Log.d(TAG, "ClientProtocolException :"+e.toString());
    	        return null;
    	    } catch (IOException e) {
    	    	Log.d(TAG, e.toString());
    	        response_params.put("response", "Internal Server Error");
    	        response_params.put("status", "500");
    	        return response_params;
    	    }  
    	}else if(httpMethod.equals("GET")){ 
    		if(payload!=null){
    			url = url+"?"+payload;
    		}
    		HttpGet httpGet = new HttpGet(url);
    		HttpGet httpGetWithHeaders = (HttpGet) buildHeaders(httpGet,headers,httpMethod);
    		Log.d(TAG,httpGetWithHeaders.toString());
    	    try {
    	        HttpResponse response = httpclient.execute(httpGetWithHeaders);
    	        response_params.put("response", getResponseBody(response));
    	        response_params.put("status", String.valueOf(response.getStatusLine().getStatusCode()));
    	        return response_params;
    	    } catch (ClientProtocolException e) {
    	    	Log.d(TAG, "ClientProtocolException :"+e.toString());
    	        return null;
    	    } catch (IOException e) {
    	    	Log.d(TAG, e.toString());
    	        response_params.put("response", "Internal Server Error");
    	        response_params.put("status", "500");
    	        return response_params;
    	    }  
    	}else if(httpMethod.equals("Delete")){ 
    		if(payload!=null){
    			url = url+"?"+payload;
    		}
    		HttpDelete httpDelete = new HttpDelete(url);
    		HttpDelete httpDeleteWithHeaders = (HttpDelete) buildHeaders(httpDelete,headers,httpMethod);
    		Log.d(TAG,httpDeleteWithHeaders.toString());
    	    try {
    	        HttpResponse response = httpclient.execute(httpDeleteWithHeaders);
    	        response_params.put("response", getResponseBody(response));
    	        response_params.put("status", String.valueOf(response.getStatusLine().getStatusCode()));
    	        return response_params;
    	    } catch (ClientProtocolException e) {
    	    	Log.d(TAG, "ClientProtocolException :"+e.toString());
    	        return null;
    	    } catch (IOException e) {
    	    	Log.d(TAG, e.toString());
    	        response_params.put("response", "Internal Server Error");
    	        response_params.put("status", "500");
    	        return response_params;
    	    }  
    	}
    	return null;   
    }

    public static HttpClient getCertifiedHttpClient() {
        try {
            HttpClient client = null;
            if(CommonUtilities.SERVER_PROTOCOL.equalsIgnoreCase("https://")){
                KeyStore localTrustStore = KeyStore.getInstance("BKS");
                InputStream in = IdentityProxy.getInstance().getContext()
                		.getResources().openRawResource(R.raw.emm_truststore);
   	    	 	localTrustStore.load(in, CommonUtilities.TRUSTSTORE_PASSWORD.toCharArray());
   	    	 

                SchemeRegistry schemeRegistry = new SchemeRegistry();
                schemeRegistry.register(new Scheme("http", PlainSocketFactory
                        .getSocketFactory(), 80));
                SSLSocketFactory sslSocketFactory = new SSLSocketFactory(localTrustStore);
                sslSocketFactory.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
                schemeRegistry.register(new Scheme("https", sslSocketFactory, 443));
                HttpParams params = new BasicHttpParams();
                ClientConnectionManager cm =
                        new ThreadSafeClientConnManager(params, schemeRegistry);

                client = new DefaultHttpClient(cm, params);
                
            } else {
                client = new DefaultHttpClient();
            }
            return client;
        } catch (Exception e) {
            Log.d(TAG, e.toString());
            return null;
        }
    }

    public static String getResponseBody(HttpResponse response) {

        String response_text = null;
        HttpEntity entity = null;
        try {
            entity = response.getEntity();
            response_text = _getResponseBody(entity);
        } catch (ParseException e) {
            Log.d(TAG, e.toString());
        } catch (IOException e) {
            if (entity != null) {
                try {
                    entity.consumeContent();
                } catch (IOException e1) {
                    Log.d(TAG, e1.toString());
                }
            }
        }
        return response_text;
    }

    public static String _getResponseBody(final HttpEntity entity) throws IOException, ParseException {

        if (entity == null) {
            throw new IllegalArgumentException("HTTP entity may not be null");
        }

        InputStream instream = entity.getContent();

        if (instream == null) {
            return "";
        }

        if (entity.getContentLength() > Integer.MAX_VALUE) {
            throw new IllegalArgumentException(

                    "HTTP entity too large to be buffered in memory");
        }

        String charset = getContentCharSet(entity);

        if (charset == null) {

            charset = HTTP.DEFAULT_CONTENT_CHARSET;

        }

        Reader reader = new InputStreamReader(instream, charset);

        StringBuilder buffer = new StringBuilder();

        try {

            char[] tmp = new char[1024];

            int l;

            while ((l = reader.read(tmp)) != -1) {

                buffer.append(tmp, 0, l);

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

        String charset = null;

        if (entity.getContentType() != null) {

            HeaderElement values[] = entity.getContentType().getElements();

            if (values.length > 0) {

                NameValuePair param = values[0].getParameterByName("charset");

                if (param != null) {

                    charset = param.getValue();

                }

            }

        }

        return charset;

    }

}
