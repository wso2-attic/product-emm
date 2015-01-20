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

package cdm.api.windows;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.io.InputStream;

@Path("/EnrollmentServer")
public interface DiscoveryService {

	@Path("/Discovery.svc")
	@POST
	@Consumes({ "application/soap+xml;charset=utf-8", "application/xml" })
	@Produces("application/soap+xml;charset=utf-8")
	Response getDiscoveryResponse(
			InputStream discoveryRequest);

	@Path("/Discovery.svc")
	@GET
	@Consumes("text/html")
	@Produces("text/html")
	Response getDiscoveryOKRequest();

	@Path("/Discovery.svc")
	@GET
	@Consumes({ "application/soap+xml;charset=utf-8", "application/xml" })
	@Produces("text/html")
	Response getDiscoveryOKRequestWithBody(InputStream discoveryRequest);

}
