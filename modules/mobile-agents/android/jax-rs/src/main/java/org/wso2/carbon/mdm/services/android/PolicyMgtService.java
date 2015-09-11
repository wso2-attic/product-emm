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


package org.wso2.carbon.mdm.services.android;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.device.mgt.common.DeviceIdentifier;
import org.wso2.carbon.mdm.services.android.exception.AndroidAgentException;
import org.wso2.carbon.mdm.services.android.util.AndroidAPIUtils;
import org.wso2.carbon.mdm.services.android.util.Message;
import org.wso2.carbon.policy.mgt.common.FeatureManagementException;
import org.wso2.carbon.policy.mgt.common.Policy;
import org.wso2.carbon.policy.mgt.common.PolicyManagementException;
import org.wso2.carbon.policy.mgt.common.ProfileFeature;
import org.wso2.carbon.policy.mgt.core.PolicyManagerService;

import javax.jws.WebService;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@WebService
@Produces({"application/json", "application/xml"})
@Consumes({"application/json", "application/xml"})
public class PolicyMgtService {

	private static Log log = LogFactory.getLog(PolicyMgtService.class);

    @GET
    @Path("{id}")
    public Message getEffectivePolicy(@HeaderParam("Accept") String acceptHeader,
									   @PathParam("id") String id) throws AndroidAgentException {

		DeviceIdentifier deviceIdentifier = AndroidAPIUtils.convertToDeviceIdentifierObject(id);
		MediaType responseMediaType = AndroidAPIUtils.getResponseMediaType(acceptHeader);
		Message responseMessage = new Message();
        Policy policy;
        try {
            PolicyManagerService policyManagerService = AndroidAPIUtils.getPolicyManagerService();
            policy = policyManagerService.getEffectivePolicy(deviceIdentifier);
            if (policy == null) {
				responseMessage.setResponseCode(Response.Status.NO_CONTENT.toString());
				responseMessage.setResponseMessage("No effective policy found");
            } else {
				responseMessage.setResponseCode(Response.Status.OK.toString());
				responseMessage.setResponseMessage("Effective policy added to operation");
			}

        } catch (PolicyManagementException e) {
            String msg = "Error occurred while getting the policy.";
            log.error(msg, e);
            throw new AndroidAgentException(msg, e);
        } finally {
            AndroidAPIUtils.endTenantFlow();
        }
        return responseMessage;
    }

    @GET
    @Path("/features/{id}")
    public List<ProfileFeature> getEffectiveFeatures(@HeaderParam("Accept") String acceptHeader,
													 @PathParam("id") String id) throws AndroidAgentException {
        List<ProfileFeature> profileFeatures;
		DeviceIdentifier deviceIdentifier = AndroidAPIUtils.convertToDeviceIdentifierObject(id);
        try {
            PolicyManagerService policyManagerService = AndroidAPIUtils.getPolicyManagerService();
            profileFeatures =  policyManagerService.getEffectiveFeatures(deviceIdentifier);
            if (profileFeatures == null) {
                Response.status(Response.Status.NOT_FOUND);
            }
        } catch (FeatureManagementException e) {
            String msg = "Error occurred while getting the features.";
            log.error(msg, e);
            throw new AndroidAgentException(msg, e);
        } finally {
            AndroidAPIUtils.endTenantFlow();
        }
        return profileFeatures;
    }
}
