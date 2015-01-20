/*
 * Copyright (c) 2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.wso2.carbon.policy.mgt.common;

public class FeatureManagementException extends Exception{

    private String errorMessage;

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public FeatureManagementException(String message) {
        super(message);
        setErrorMessage(message);
    }

    public FeatureManagementException(String message, Exception ex) {
        super(message, ex);
        setErrorMessage(message);
    }

    public FeatureManagementException(String message, Throwable cause) {
        super(message, cause);
        setErrorMessage(message);
    }

    public FeatureManagementException() {
        super();
    }

    public FeatureManagementException(Throwable cause) {
        super(cause);
    }
}
