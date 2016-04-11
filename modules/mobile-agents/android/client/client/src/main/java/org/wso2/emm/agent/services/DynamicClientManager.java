/*
 *
 *  Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.wso2.emm.agent.services;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.wso2.emm.agent.AndroidAgentException;
import org.wso2.emm.agent.beans.RegistrationProfile;
import org.wso2.emm.agent.beans.ServerConfig;
import org.wso2.emm.agent.beans.UnregisterProfile;
import org.wso2.emm.agent.proxy.IDPTokenManagerException;
import org.wso2.emm.agent.proxy.IdentityProxy;
import org.wso2.emm.agent.proxy.beans.EndPointInfo;
import org.wso2.emm.agent.proxy.utils.ServerUtilities;
import org.wso2.emm.agent.utils.Constants;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * This class is used to register and unregister oauth application.
 */
public class DynamicClientManager {

    private static final String TAG = DynamicClientManager.class.getSimpleName();
    private static final String USER_ID = "userId";
    private static final String CONSUMER_KEY = "consumerKey";
    private static final String APPLICATION_NAME = "applicationName";

    /**
     * This method is used to register an oauth application in the backend.
     *
     * @param profile Payload of the register request.
     * @param utils Server configurations.
     *
     * @return returns consumer key and consumer secret if success. Else returns null
     *         if it fails to register.
     * @throws AndroidAgentException
     */
    public String getClientCredentials(RegistrationProfile profile, ServerConfig utils, Context context)
            throws AndroidAgentException {
        IdentityProxy.getInstance().setContext(context);
        EndPointInfo endPointInfo = new EndPointInfo();
        String endPoint = utils.getAPIServerURL(context) +
                org.wso2.emm.agent.utils.Constants.DYNAMIC_CLIENT_REGISTER_ENDPOINT;
        endPointInfo.setHttpMethod(org.wso2.emm.agent.proxy.utils.Constants.HTTP_METHODS.POST);
        endPointInfo.setEndPoint(endPoint);
        endPointInfo.setRequestParams(profile.toJSON());
        String response = null;
        try {
            SendRequest sendRequestTask = new SendRequest();
            Map<String, String> responseParams = sendRequestTask.execute(endPointInfo).get();

            if (responseParams != null) {
                String statusCode = responseParams.get(Constants.STATUS);
                if (Constants.Status.CREATED.equalsIgnoreCase(statusCode)) {
                    response = responseParams.get(Constants.RESPONSE);
                }
            }
            return response;
        } catch (InterruptedException e) {
            throw new AndroidAgentException("Error occurred due to thread interruption", e);
        } catch (ExecutionException e) {
            throw new AndroidAgentException("Error occurred while fetching credentials", e);
        }
    }

    /**
     * This method is used to unregister the oauth application that has been
     * registered at the device authentication.
     *
     * @param profile Payload of the unregister request.
     * @param utils Server configurations
     *
     * @return true if unregistration success, else false.
     * @throws AndroidAgentException
     */
    public boolean unregisterClient(UnregisterProfile profile, ServerConfig utils, Context context)
            throws AndroidAgentException {
        StringBuilder endPoint = new StringBuilder();
        endPoint.append(utils.getAPIServerURL(context));
        endPoint.append(Constants.DYNAMIC_CLIENT_REGISTER_ENDPOINT);
        endPoint.append("?" + USER_ID + "=" + profile.getUserId());
        endPoint.append("&" + CONSUMER_KEY + "=" + profile.getConsumerKey());
        endPoint.append("&" + APPLICATION_NAME + "=" + profile.getApplicationName());

        EndPointInfo endPointInfo = new EndPointInfo();
        endPointInfo.setHttpMethod(org.wso2.emm.agent.proxy.utils.Constants.HTTP_METHODS.DELETE);
        endPointInfo.setEndPoint(endPoint.toString());

        try {
            SendRequest sendRequestTask = new SendRequest();
            Map<String, String> responseParams = sendRequestTask.execute(endPointInfo).get();
            String statusCode = null;
            if(responseParams != null) {
                statusCode = responseParams.get(Constants.STATUS);
            }
            return Constants.Status.ACCEPT.equalsIgnoreCase(statusCode);
        } catch (InterruptedException e) {
            throw new AndroidAgentException("Error occurred due to thread interruption", e);
        } catch (ExecutionException e) {
            throw new AndroidAgentException("Error occurred while fetching credentials", e);
        }
    }

    /**
     * This class is used to send requests to backend.
     * The reason to use this private class because the function which is already
     * available for sending requests is secured with token. Therefor this async task can be used 
     * to send requests without tokens.
     */
    private class SendRequest extends AsyncTask<EndPointInfo, Void, Map<String, String>> {
        @Override
        protected Map<String, String> doInBackground(EndPointInfo... params) {
            EndPointInfo endPointInfo = params[0];

            Map<String, String> responseParams = null;
            Map<String, String> headers = new HashMap<String, String>();
            headers.put("Content-Type", "application/json");
            headers.put("Accept", "application/json");
            headers.put("User-Agent", Constants.USER_AGENT);

            try {
                responseParams = ServerUtilities.postData(endPointInfo, headers);
                if (responseParams != null) {
                    if (Constants.DEBUG_MODE_ENABLED) {
                        Iterator<Map.Entry<String, String>> iterator = responseParams.entrySet().iterator();
                        while (iterator.hasNext()) {
                            Map.Entry<String, String> respParams = iterator.next();
                            StringBuilder paras = new StringBuilder();
                            paras.append("response-params: key:");
                            paras.append(respParams.getKey());
                            paras.append(", value:");
                            paras.append(respParams.getValue());
                            Log.d(TAG, paras.toString());
                        }
                    }
                }
            } catch (IDPTokenManagerException e) {
                Log.e(TAG, "Failed to contact server", e);
            }
            return responseParams;
        }
    }
}
