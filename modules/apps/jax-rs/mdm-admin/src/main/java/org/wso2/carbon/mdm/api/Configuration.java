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

package org.wso2.carbon.mdm.api;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.device.mgt.common.configuration.mgt.TenantConfiguration;
import org.wso2.carbon.mdm.api.common.MDMAPIException;
import org.wso2.carbon.mdm.api.util.MDMAPIUtils;
import org.wso2.carbon.mdm.api.util.MDMAppConstants;
import org.wso2.carbon.mdm.api.util.Message;
import org.wso2.carbon.registry.api.RegistryException;
import org.wso2.carbon.registry.api.Resource;

import javax.jws.WebService;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.StringWriter;

/**
 * Android Platform Configuration REST-API implementation.
 * All end points supports JSON, XMl with content negotiation.
 */
@WebService
@Produces({ "application/json", "application/xml" })
@Consumes({ "application/json", "application/xml" })
public class Configuration {

	private static Log log = LogFactory.getLog(Configuration.class);

	@POST
	public Message configureSettings(TenantConfiguration configuration)
			throws MDMAPIException {

		Message responseMsg = new Message();
		try {
			StringWriter writer = new StringWriter();
			JAXBContext context = JAXBContext.newInstance(TenantConfiguration.class);
			Marshaller marshaller = context.createMarshaller();
			marshaller.marshal(configuration, writer);

			Resource resource = MDMAPIUtils.getConfigurationRegistry().newResource();
			resource.setContent(writer.toString());
			resource.setMediaType(MDMAppConstants.RegistryConstants.MEDIA_TYPE_XML);
			MDMAPIUtils.putRegistryResource(MDMAppConstants.RegistryConstants.GENERAL_CONFIG_RESOURCE_PATH, resource);
			Response.status(Response.Status.CREATED);
			responseMsg.setResponseMessage("Android platform configuration saved successfully");
			responseMsg.setResponseCode(Response.Status.CREATED.toString());
			return responseMsg;
		} catch (RegistryException e) {
			throw new MDMAPIException(
					"Error occurred while persisting the Registry resource of Android Configuration : " + e.getMessage(), e);
		} catch (JAXBException e) {
			throw new MDMAPIException(
					"Error occurred while parsing the Android configuration : " + e.getMessage(), e);
		}
	}

}
