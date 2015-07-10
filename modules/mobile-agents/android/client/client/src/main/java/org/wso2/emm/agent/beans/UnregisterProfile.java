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

import android.util.Log;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.wso2.emm.agent.AndroidAgentException;

import java.io.IOException;

/**
 * This class represents the data that are required to unregister
 * the oauth application.
 */
public class UnregisterProfile {

    private String userId;
    private String consumerKey;
    private String applicationName;

    private final String TAG = UnregisterProfile.class.getSimpleName();

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getConsumerKey() {
        return consumerKey;
    }

    public void setConsumerKey(String consumerKey) {
        this.consumerKey = consumerKey;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public String toJSON() throws AndroidAgentException {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(this);
        } catch (JsonMappingException e) {
            String errorMessage = "Error occurred while mapping class to json.";
            Log.e(TAG, errorMessage);
            throw new AndroidAgentException(errorMessage, e);
        } catch (JsonGenerationException e) {
            String errorMessage = "Error occurred while generating json.";
            Log.e(TAG, errorMessage);
            throw new AndroidAgentException(errorMessage, e);
        } catch (IOException e) {
            String errorMessage = "Error occurred while reading the stream.";
            Log.e(TAG, errorMessage);
            throw new AndroidAgentException(errorMessage, e);
        }
    }
}
