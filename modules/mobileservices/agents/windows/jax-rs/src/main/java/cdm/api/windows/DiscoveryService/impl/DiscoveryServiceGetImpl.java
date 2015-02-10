/*
 * Copyright (c) 2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied. See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */

package cdm.api.windows.DiscoveryService.impl;

import cdm.api.windows.DiscoveryService.beans.in.DiscoveryRequest;
import cdm.api.windows.DiscoveryService.DiscoveryServiceGet;
import cdm.api.windows.DiscoveryService.beans.out.Message;
import org.apache.log4j.Logger;
import javax.jws.WebService;
import javax.ws.rs.core.Response;
import javax.xml.ws.BindingType;
import javax.xml.ws.soap.Addressing;
import javax.xml.ws.soap.SOAPBinding;

@WebService(endpointInterface = "cdm.api.windows.DiscoveryService.DiscoveryServiceGet", targetNamespace = "http://schemas.microsoft.com/windows/management/2012/01/enrollment")
@Addressing(enabled = true, required = true)
@BindingType(value = SOAPBinding.SOAP12HTTP_BINDING)
public class DiscoveryServiceGetImpl implements DiscoveryServiceGet {

	private Logger LOGGER = Logger.getLogger(DiscoveryServiceGetImpl.class);

	@Override
	public Message Discover(DiscoveryRequest disRequest) {

		System.out.println("CHECK_SECOND_POST");

		Message message = new Message();
		message.setAuthPolicy("OnPremise");

		message.setEnrollmentPolicyServiceUrl(
				"https://EnterpriseEnrollment.wso2.com/ENROLLMENTSERVER/PolicyEnrollmentWebservice.svc");
		message.setEnrollmentServiceUrl(
				"https://EnterpriseEnrollment.wso2.com/ENROLLMENTSERVER/DeviceEnrollmentWebservice.svc");

		LOGGER.info("Received Discovery Service Request");

		return message;
	}

	@Override
	public Response DiscoverGet() {

		System.out.println("CHECK_FIRST_GET");

		return Response.ok().build();
	}

}
