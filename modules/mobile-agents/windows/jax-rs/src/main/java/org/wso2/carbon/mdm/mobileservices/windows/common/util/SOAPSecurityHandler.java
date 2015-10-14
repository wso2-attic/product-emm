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

package org.wso2.carbon.mdm.mobileservices.windows.common.util;


import org.wso2.carbon.mdm.mobileservices.windows.common.PluginConstants;

import javax.xml.namespace.QName;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import java.util.HashSet;
import java.util.Set;

/**
 * Class for handle SOAP message security.
 */
public class SOAPSecurityHandler implements SOAPHandler<SOAPMessageContext> {

    /**
     * This method resolves the security header coming in the SOAP message.
     *
     * @return - Security Header
     */
    @Override
    public Set<QName> getHeaders() {
        QName securityHeader = new QName(PluginConstants.WS_SECURITY_TARGET_NAMESPACE, PluginConstants.SECURITY);
        HashSet<QName> headers = new HashSet<QName>();
        headers.add(securityHeader);
        return headers;
    }

    @Override
    public boolean handleMessage(SOAPMessageContext context) {
        return true;
    }

    @Override
    public boolean handleFault(SOAPMessageContext context) {
        return true;
    }

    @Override
    public void close(MessageContext context) {

    }
}
