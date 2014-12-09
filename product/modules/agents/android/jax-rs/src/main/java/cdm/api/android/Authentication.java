/*
 * Copyright 2011-2012 WSO2, Inc. (http://wso2.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cdm.api.android;


import com.google.gson.JsonObject;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.FormParam;
import javax.ws.rs.core.Response;

@Path("/authenticate/")
public class Authentication {

	@POST
	@Path("/device/")
	@Produces("application/json")
	public String authenticateDevice(@FormParam("username") String username, @FormParam("password") String password) {
		JsonObject result = new JsonObject();
		result.addProperty("senderId","jwwfowrjwqporqwrpqworpq");
		return result.toString();
	}

	@POST
	@Path("/device/license")
	public String getLicense() {
		return "License Agreement";
	}

	@POST
	@Path("/device/enroll")
	public Response enrollDevice() {
		return Response.status(201).entity("Registration Successful").build();
	}
}
