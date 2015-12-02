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

package org.wso2.carbon.mdm.mobileservices.windows.services.discovery;

import org.wso2.carbon.mdm.mobileservices.windows.common.PluginConstants;
import org.wso2.carbon.mdm.mobileservices.windows.common.exceptions.WindowsDeviceEnrolmentException;
import org.wso2.carbon.mdm.mobileservices.windows.services.discovery.beans.DiscoveryRequest;
import org.wso2.carbon.mdm.mobileservices.windows.services.discovery.beans.DiscoveryResponse;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.core.Response;
import javax.xml.ws.BindingType;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;
import javax.xml.ws.soap.SOAPBinding;

/**
 * Interface for Discovery service related operations.
 */
@WebService(targetNamespace = PluginConstants.DISCOVERY_SERVICE_TARGET_NAMESPACE,
        name = "IDiscoveryService")
@BindingType(value = SOAPBinding.SOAP12HTTP_BINDING)
public interface DiscoveryService {

    @POST
    @RequestWrapper(localName = "Discover", targetNamespace = PluginConstants.DISCOVERY_SERVICE_TARGET_NAMESPACE)
    @WebMethod(operationName = "Discover")
    @ResponseWrapper(localName = "DiscoverResponse", targetNamespace = PluginConstants.DISCOVERY_SERVICE_TARGET_NAMESPACE)
    void discover(
            @WebParam(name = "request", targetNamespace = PluginConstants.DISCOVERY_SERVICE_TARGET_NAMESPACE)
            DiscoveryRequest request,
            @WebParam(mode = WebParam.Mode.OUT, name = "DiscoverResult",
                    targetNamespace = PluginConstants.DISCOVERY_SERVICE_TARGET_NAMESPACE)
            javax.xml.ws.Holder<DiscoveryResponse> response
    ) throws WindowsDeviceEnrolmentException;

    @GET
    @WebMethod
    @WebResult()
    Response discoverGet();

}