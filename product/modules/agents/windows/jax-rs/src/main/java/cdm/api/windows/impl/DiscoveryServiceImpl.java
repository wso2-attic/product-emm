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
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package cdm.api.windows.impl;

import cdm.api.windows.DiscoveryService;

import javax.ws.rs.core.Response;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;


import org.apache.log4j.Logger;

public class DiscoveryServiceImpl implements DiscoveryService {

	private Logger LOGGER = Logger.getLogger(DiscoveryServiceImpl.class);

	public Response getDiscoveryResponse(InputStream discoveryRequest) {
		LOGGER.info("Received Discovery Service POST Request [{}]");

		String response = null;
		File file = null;
		FileInputStream fis = null;
		byte[] data = null;

		try {

			file = new File("./conf/discover-service.xml");
			fis = new FileInputStream(file);
			data = new byte[(int) file.length()];
			fis.read(data);
			fis.close();
			response = new String(data, "UTF-8");

		} catch (IOException e) {
			LOGGER.error("An Unexpected Error has occurred while processing the request ", e);
		}
		LOGGER.info("Sending Discovery Response");

		return Response.ok().entity(response).build();
	}

	public Response getDiscoveryOKRequest() {
		LOGGER.info("Received a GET Request without body");
		return Response.ok().build();
	}

	public Response getDiscoveryOKRequestWithBody(InputStream discoveryRequest) {
		LOGGER.info("Received a GET Request with body [{}]");
		return Response.ok().build();
	}


}
