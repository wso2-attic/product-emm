package org.wso2.cdm.agent.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.security.KeyStore;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
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
import org.wso2.cdm.agent.proxy.APIController;
import org.wso2.cdm.agent.proxy.APIResultCallBack;
import org.wso2.cdm.agent.proxy.APIUtilities;
import org.wso2.cdm.agent.services.WSO2DeviceAdminReceiver;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ParseException;
import android.util.Log;

public class ServerUtils {
	
	public static String TAG = ServerUtils.class.getSimpleName();
	
	private static final int MAX_ATTEMPTS = 2;
	private static final int BACKOFF_MILLI_SECONDS = 2000;
	private static final Random random = new Random();

	/**
	 * calls the secured API
	 * 
	 * @param context
	 *            the Activity which calls an API
	 * @param serverUrl
	 *            the server url
	 * @param endpoint
	 *            the API endpoint
	 * @param apiVersion
	 *            the API version
	 * @param methodType
	 *            the method type
	 * @param apiResultCallBack
	 *            the API result call back object
	 * @param requestCode
	 *            the request code
	 */
	public static void callSecuredAPI(Context context, String endpoint,
			String methodType, Map<String, String> requestParams,
			APIResultCallBack apiResultCallBack, int requestCode) {
		String serverIP = CommonUtilities.getPref(context, context
				.getResources().getString(R.string.shared_pref_ip));
		String serverURL = CommonUtilities.SERVER_PROTOCOL + serverIP + ":"
				+ CommonUtilities.SERVER_PORT
				+ CommonUtilities.SERVER_APP_ENDPOINT;

		APIUtilities apiUtilities = new APIUtilities();
		apiUtilities.setEndPoint(serverURL + endpoint
				+ CommonUtilities.API_VERSION);
		apiUtilities.setHttpMethod(methodType);
		if (requestParams != null) {
			apiUtilities.setRequestParams(requestParams);
		}
		APIController apiController = new APIController();
		String clientKey=CommonUtilities.getPref(context, context.getResources().getString(R.string.shared_pref_client_id));
		String clientSecret=CommonUtilities.getPref(context, context.getResources().getString(R.string.shared_pref_client_secret));
		if(!clientKey.equals("") && !clientSecret.equals("")){
			CommonUtilities.CLIENT_ID=clientKey;
			CommonUtilities.CLIENT_SECRET=clientSecret;
			apiController.setClientDetails(clientKey, clientSecret);
		}
		apiController.invokeAPI(apiUtilities, apiResultCallBack, requestCode, context.getApplicationContext());
	}

	public static void clearAppData(Context context) {
		DevicePolicyManager devicePolicyManager;
		ComponentName demoDeviceAdmin;
		try {
			devicePolicyManager = (DevicePolicyManager) context
					.getSystemService(Context.DEVICE_POLICY_SERVICE);
			demoDeviceAdmin = new ComponentName(context,
					WSO2DeviceAdminReceiver.class);
			SharedPreferences mainPref = context.getSharedPreferences(context
					.getResources().getString(R.string.shared_pref_package),
					Context.MODE_PRIVATE);
			Editor editor = mainPref.edit();
			editor.putString(
					context.getResources().getString(
							R.string.shared_pref_policy), "");
			editor.putString(
					context.getResources().getString(
							R.string.shared_pref_isagreed), "0");
			editor.putString(
					context.getResources()
							.getString(R.string.shared_pref_regId), "");
			editor.putString(
					context.getResources().getString(
							R.string.shared_pref_registered), "0");
			editor.putString(
					context.getResources().getString(R.string.shared_pref_ip),
					"");
			editor.putString(
					context.getResources().getString(
							R.string.shared_pref_sender_id), "");
			editor.putString(
					context.getResources().getString(R.string.shared_pref_eula),
					"");
			editor.commit();
			devicePolicyManager.removeActiveAdmin(demoDeviceAdmin);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}


}
