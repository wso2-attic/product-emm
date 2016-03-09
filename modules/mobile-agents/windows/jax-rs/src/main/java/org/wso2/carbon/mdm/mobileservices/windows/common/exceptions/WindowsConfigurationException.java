/*
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.mdm.mobileservices.windows.common.exceptions;

/**
 * Custom class for windows device configurations.
 */
public class WindowsConfigurationException extends Exception {

    private String errorMessage;

    private static final long serialVersionUID = 7950151650447893900L;

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public WindowsConfigurationException(Throwable cause) {
        super(cause);
    }

    public WindowsConfigurationException() {
        super();
    }

    public WindowsConfigurationException(String msg) {
        super(msg);
        setErrorMessage(msg);
    }

    public WindowsConfigurationException(String msg, Throwable cause) {
        super(msg, cause);
        setErrorMessage(msg);
    }

    public WindowsConfigurationException(String msg, Exception exception) {
        super(msg, exception);
        setErrorMessage(msg);
    }

}
