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
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.wso2.cdmserver.mobileservices.android.common;

public class AndroidAgentException extends Exception{

    private static final long serialVersionUID = 7950151650447893900L;
    private String errorMessage;

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public AndroidAgentException(String msg, Exception nestedEx) {
        super(msg, nestedEx);
        setErrorMessage(msg);
    }

    public AndroidAgentException(String message, Throwable cause) {
        super(message, cause);
        setErrorMessage(message);
    }

    public AndroidAgentException(String msg) {
        super(msg);
        setErrorMessage(msg);
    }

    public AndroidAgentException() {
        super();
    }

    public AndroidAgentException(Throwable cause) {
        super(cause);
    }



}
