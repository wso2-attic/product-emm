/*
 * Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.mdm.qsg;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.wso2.mdm.qsg.dto.EMMConfig;
import org.wso2.mdm.qsg.dto.HTTPResponse;
import org.wso2.mdm.qsg.utils.Constants;
import org.wso2.mdm.qsg.utils.HTTPInvoker;

import java.util.HashMap;

/**
 * Created by harshan on 7/25/16.
 */
public class PolicyOperations {

    public static boolean createPasscodePolicy (String policyName, String deviceType) {
        HashMap<String, String> headers = new HashMap<String, String>();
        String policyEndpoint = EMMConfig.getInstance().getEmmHost() + "/api/device-mgt/v1.0/policies";
        //Set the policy payload
        JSONObject policyData = new JSONObject();
        policyData.put("policyName", policyName);
        policyData.put("description", "Passcode Policy");
        policyData.put("compliance", "enforce");
        policyData.put("ownershipType", "ANY");
        policyData.put("active", false);
        JSONObject profile = new JSONObject();
        profile.put("profileName","passcode");
        profile.put("deviceType", deviceType);
        JSONArray featureList = new JSONArray();
        JSONObject feature = new JSONObject();
        feature.put("featureCode", "PASSCODE_POLICY");
        feature.put("deviceType", "android");
        JSONObject featureContent = new JSONObject();
        featureContent.put("allowSimple", true);
        featureContent.put("requireAlphanumeric", true);
        featureContent.put("minLength", null);
        featureContent.put("minComplexChars", null);
        featureContent.put("maxPINAgeInDays", 7);
        featureContent.put("pinHistory", 7);
        featureContent.put("maxFailedAttempts", null);
        feature.put("content", featureContent);
        featureList.add(feature);
        profile.put("profileFeaturesList", featureList);
        JSONArray roles = new JSONArray();
        roles.add(Constants.EMM_USER_ROLE);
        policyData.put("profile", profile);
        policyData.put("roles", roles);
        //Set the headers
        headers.put(Constants.CONTENT_TYPE_HEADER,Constants.APPLICATION_JSON);
        HTTPResponse
                httpResponse = HTTPInvoker
                .sendHTTPPostWithOAuthSecurity(policyEndpoint, policyData.toJSONString(), headers);
        if (httpResponse.getResponseCode() == 201) {
            return true;
        }
        return false;
    }
}
