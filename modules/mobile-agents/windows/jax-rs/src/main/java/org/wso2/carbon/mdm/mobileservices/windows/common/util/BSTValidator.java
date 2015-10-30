/*
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * you may obtain a copy of the License at
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

package org.wso2.carbon.mdm.mobileservices.windows.common.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ws.security.WSSecurityException;
import org.apache.ws.security.handler.RequestData;
import org.apache.ws.security.message.token.BinarySecurity;
import org.apache.ws.security.validate.Credential;
import org.apache.ws.security.validate.Validator;
import org.wso2.carbon.mdm.mobileservices.windows.common.beans.CacheEntry;
import org.wso2.carbon.mdm.mobileservices.windows.common.exceptions.AuthenticationException;
import org.wso2.carbon.mdm.mobileservices.windows.common.exceptions.WindowsDeviceEnrolmentException;

/**
 * Validator class for user authentication checking the default carbon user store.
 */
public class BSTValidator implements Validator {

    private static Log log = LogFactory.getLog(BSTValidator.class);

    /**
     * This method validates the binary security token in SOAP message coming from the device.
     *
     * @param credential  - binary security token credential object
     * @param requestData - Request data associated with the request
     * @return - Credential object if authentication is success, or null if not success
     * @throws WSSecurityException
     */
    @Override
    public Credential validate(Credential credential, RequestData requestData) throws WSSecurityException {

        BinarySecurity binarySecurityTokenObject = credential.getBinarySecurityToken();
        String binarySecurityToken = new String(binarySecurityTokenObject.getElement().getFirstChild().getTextContent());
        Credential returnCredentials;
        try {
            if (authenticate(binarySecurityToken)) {
                returnCredentials = credential;
            } else {
                String msg = "Authentication failure due to invalid binary security token.";
                log.error(msg);
                throw new WindowsDeviceEnrolmentException(msg);
            }
            //Generic exception is caught here as there is no need of taking different actions for
            //different exceptions.
        } catch (Exception e) {
            String msg = "Failure occurred in the BST validator.";
            log.error(msg, e);
            throw new WSSecurityException(msg, e);
        }
        return returnCredentials;
    }

    /**
     * This method authenticates the user checking the binary security token in the user store.
     *
     * @param binarySecurityToken - Binary security token received in the SOAP message header
     * @return - Authentication status
     * @throws AuthenticationException
     */
    public boolean authenticate(String binarySecurityToken) throws
            AuthenticationException {

        CacheEntry cacheentry = (CacheEntry) DeviceUtil.getCacheEntry(binarySecurityToken);
        String username = cacheentry.getUsername();
        if (username != null) {
            return true;
        } else {
            return false;
        }
    }
}
