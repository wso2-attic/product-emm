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

package org.wso2.carbon.mdm.mobileservices.windows.services.wstep.beans;

import org.wso2.carbon.mdm.mobileservices.windows.common.PluginConstants;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ContextItem", namespace = PluginConstants.SOAP_AUTHORIZATION_TARGET_NAMESPACE,
        propOrder = {"Value"})
public class ContextItem {

    @XmlElement(required = true, namespace = PluginConstants.SOAP_AUTHORIZATION_TARGET_NAMESPACE)
    protected String Value;

    public String getValue() {
        return Value;
    }

    public void setValue(String value) {
        Value = value;
    }

}
