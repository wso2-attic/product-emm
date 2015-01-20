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

package org.wso2.carbon.policy.mgt.core.dao;

public class PolicyManagerDAOException extends Exception{

    private String policyDAOErrorMessage;

    public String getPolicyDAOErrorMessage() {
        return policyDAOErrorMessage;
    }

    public void setPolicyDAOErrorMessage(String policyDAOErrorMessage) {
        this.policyDAOErrorMessage = policyDAOErrorMessage;
    }

    public PolicyManagerDAOException(String message) {
        super(message);
        setPolicyDAOErrorMessage(message);
    }

    public PolicyManagerDAOException(String message, Exception ex) {
        super(message, ex);
        setPolicyDAOErrorMessage(message);
    }

    public PolicyManagerDAOException(String message, Throwable cause) {
        super(message, cause);
        setPolicyDAOErrorMessage(message);
    }

    public PolicyManagerDAOException() {
        super();
    }

    public PolicyManagerDAOException(Throwable cause) {
        super(cause);
    }
}
