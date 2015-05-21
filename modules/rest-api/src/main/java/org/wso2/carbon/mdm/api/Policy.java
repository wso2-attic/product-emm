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
import org.wso2.carbon.mdm.api.util.Message;
import org.wso2.carbon.mdm.beans.PolicyWrapper;
import org.wso2.carbon.policy.mgt.common.PolicyAdministratorPoint;
import org.wso2.carbon.policy.mgt.common.PolicyManagementException;
import org.wso2.carbon.policy.mgt.core.PolicyManagerService;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

public class Policy {
	private static Log log = LogFactory.getLog(Policy.class);

	@POST
	public Message addPolicy(PolicyWrapper policyWrapper) throws MDMAPIException {

        PolicyManagerService policyManagementService = MDMAPIUtils.getPolicyManagementService();
		Message responseMsg = new Message();
        org.wso2.carbon.policy.mgt.common.Policy policy = new org.wso2.carbon.policy.mgt.common.Policy();
        policy.setPolicyName(policyWrapper.getPolicyName());
        policy.setProfileId(policyWrapper.getProfileId());
        policy.setProfile(policyWrapper.getProfile());
        policy.setOwnershipType(policyWrapper.getOwnershipType());
        policy.setRoles(policyWrapper.getRoles());
        policy.setUsers(policyWrapper.getUsers());
        policy.setTenantId(policyWrapper.getTenantId());


		try {
			PolicyAdministratorPoint pap = policyManagementService.getPAP();
			pap.addPolicy(policy);
			Response.status(HttpStatus.SC_CREATED);
			responseMsg.setResponseMessage("Policy has been added successfully.");
			return responseMsg;
		} catch (PolicyManagementException e) {
			String error = "Policy Management related exception";
			log.error(error, e);
			throw new MDMAPIException(error, e);
		}
	}
	@POST
	@Path("{id}")
	public Message updatePolicy(org.wso2.carbon.policy.mgt.common.Policy policy,  @PathParam("id") int policyId)
			throws MDMAPIException {
		PolicyManagerService policyManagementService = MDMAPIUtils.getPolicyManagementService();
		Message responseMsg = new Message();
		try {
			PolicyAdministratorPoint pap = policyManagementService.getPAP();
			policy.setProfile(pap.getProfile(policy.getProfileId()));
			org.wso2.carbon.policy.mgt.common.Policy previousPolicy = pap.getPolicy(policyId);
			policy.setPolicyName(previousPolicy.getPolicyName());
			pap.updatePolicy(policy);
			Response.status(HttpStatus.SC_OK);
			responseMsg.setResponseMessage("Policy has been updated successfully.");
			return responseMsg;
		} catch (PolicyManagementException e) {
			String error = "Policy Management related exception";
			log.error(error, e);
			throw new MDMAPIException(error, e);
		}
	}

	@DELETE
	@Path("{id}")
	public void deletePolicy(@PathParam("id") int policyId) throws MDMAPIException {
		PolicyManagerService policyManagementService = MDMAPIUtils.getPolicyManagementService();
		Message responseMsg = new Message();
		try {
			PolicyAdministratorPoint pap = policyManagementService.getPAP();
			org.wso2.carbon.policy.mgt.common.Policy policy = pap.getPolicy(policyId);
			pap.deletePolicy(policy);
		} catch (PolicyManagementException e) {
			String error = "Policy Management related exception";
			log.error(error, e);
			throw new MDMAPIException(error, e);
		}
	}

	@GET
	@Path("count")
	public int getPolicyCount() throws MDMAPIException {
		int policyCount = 0;
		PolicyManagerService policyManagementService = MDMAPIUtils.getPolicyManagementService();
		try {
			PolicyAdministratorPoint pap = policyManagementService.getPAP();
			policyCount = pap.getPolicyCount();
			return policyCount;
		} catch (PolicyManagementException e) {
			String error = "Policy Management related exception";
			log.error(error, e);
			throw new MDMAPIException(error, e);
		}
	}
}
