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

package org.wso2.emm.agent.beans;

import org.wso2.emm.agent.AndroidAgentException;
import org.wso2.emm.agent.utils.CommonUtils;
import java.util.HashMap;
import java.util.Map;

/**
 * This class represents the data that are required to register
 * the oauth application.
 */
public class RegistrationProfile {

    private String callbackUrl;
    private String clientName;
    private String tokenScope;
    private String owner;
    private String grantType;
    private String applicationType;

    private static final String TAG = RegistrationProfile.class.getSimpleName();

    public String getCallbackUrl() {
        return callbackUrl;
    }

    public void setCallbackUrl(String callBackUrl) {
        this.callbackUrl = callBackUrl;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getTokenScope() {
        return tokenScope;
    }

    public void setTokenScope(String tokenScope) {
        this.tokenScope = tokenScope;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getGrantType() {
        return grantType;
    }

    public void setGrantType(String grantType) {
        this.grantType = grantType;
    }

    public String getApplicationType() {
        return applicationType;
    }

    public void setApplicationType(String applicationType) {
        this.applicationType = applicationType;
    }

    public String toJSON() throws AndroidAgentException {
        return CommonUtils.toJSON(this);
    }

    public Map<String, String> toMap() throws AndroidAgentException {
        Map<String, String> resultMap = new HashMap<>();
        resultMap.put("callbackUrl", getCallbackUrl());
        resultMap.put("clientName", getClientName());
        resultMap.put("tokenScope", getTokenScope());
        resultMap.put("owner", getOwner());
        resultMap.put("grantType", getGrantType());
        resultMap.put("applicationType", getApplicationType());
        return resultMap;
    }
}
