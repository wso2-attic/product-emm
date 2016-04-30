/*
 * Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.emm.agent.proxy.interfaces;

/**
 * Callback interface Handles call backs to authentication requests.
 * This is to provide a call back to a UI/caller with the out put of the
 */
public interface AuthenticationCallback {

    /**
     * When the authentication is completed, this can be called to notify the caller.
     * @param authenticated Status of the authentication.
     * @param requestCode Request code to identify a request uniquely.
     */
    void onAuthenticated(boolean authenticated, int requestCode);
}
