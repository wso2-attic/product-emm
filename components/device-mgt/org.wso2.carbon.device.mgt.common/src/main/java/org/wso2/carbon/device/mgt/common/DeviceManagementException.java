/**
 *  Copyright (c) 2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.wso2.carbon.device.mgt.common;

public class DeviceManagementException extends Exception {

    private static final long serialVersionUID = -3151279311929070297L;

    private String errorMessage;

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public DeviceManagementException(String msg, Exception nestedEx) {
        super(msg, nestedEx);
        setErrorMessage(msg);
    }

    public DeviceManagementException(String message, Throwable cause) {
        super(message, cause);
        setErrorMessage(message);
    }

    public DeviceManagementException(String msg) {
        super(msg);
        setErrorMessage(msg);
    }

    public DeviceManagementException() {
        super();
    }

    public DeviceManagementException(Throwable cause) {
        super(cause);
    }

}
