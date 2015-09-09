/*
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.wso2.carbon.mdm.api;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.mdm.api.common.MDMAPIException;
import org.wso2.carbon.mdm.api.util.MDMAPIUtils;
import org.wso2.carbon.mdm.api.util.ResponsePayload;
import org.wso2.carbon.policy.mgt.common.PolicyAdministratorPoint;
import org.wso2.carbon.policy.mgt.common.PolicyManagementException;
import org.wso2.carbon.policy.mgt.core.PolicyManagerService;

import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

public class Profile {
	private static Log log = LogFactory.getLog(Profile.class);

	@POST
	public org.wso2.carbon.policy.mgt.common.Profile addProfile(org.wso2.carbon.policy.mgt.common.Profile profile) throws MDMAPIException {
		PolicyManagerService policyManagementService = MDMAPIUtils.getPolicyManagementService();
		try {
			PolicyAdministratorPoint pap = policyManagementService.getPAP();
			profile = pap.addProfile(profile);
			Response.status(HttpStatus.SC_CREATED);
			return profile;
		} catch (PolicyManagementException e) {
			String error = "Policy Management related exception";
			log.error(error, e);
			throw new MDMAPIException(error, e);
		}
	}
	@POST
	@Path("{id}")
	public ResponsePayload updateProfile(org.wso2.carbon.policy.mgt.common.Profile profile,  @PathParam("id") String profileId)
			throws MDMAPIException {
		PolicyManagerService policyManagementService = MDMAPIUtils.getPolicyManagementService();
		ResponsePayload responseMsg = new ResponsePayload();
		try {
			PolicyAdministratorPoint pap = policyManagementService.getPAP();
			pap.updateProfile(profile);
			Response.status(HttpStatus.SC_OK);
			responseMsg.setMessageFromServer("Profile has been updated successfully.");
			return responseMsg;
		} catch (PolicyManagementException e) {
			String error = "Policy Management related exception";
			log.error(error, e);
			throw new MDMAPIException(error, e);
		}
	}
	@DELETE
	@Path("{id}")
	public ResponsePayload deleteProfile(@PathParam("id") int profileId) throws MDMAPIException {
		PolicyManagerService policyManagementService = MDMAPIUtils.getPolicyManagementService();
		ResponsePayload responseMsg = new ResponsePayload();
		try {
			PolicyAdministratorPoint pap = policyManagementService.getPAP();
			org.wso2.carbon.policy.mgt.common.Profile profile = pap.getProfile(profileId);
			pap.deleteProfile(profile);
			Response.status(HttpStatus.SC_OK);
			responseMsg.setMessageFromServer("Profile has been deleted successfully.");
			return responseMsg;
		} catch (PolicyManagementException e) {
			String error = "Policy Management related exception";
			log.error(error, e);
			throw new MDMAPIException(error, e);
		}
	}
}
