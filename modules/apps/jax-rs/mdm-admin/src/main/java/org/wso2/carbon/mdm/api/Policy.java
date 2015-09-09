/*
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.mdm.api;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.device.mgt.common.DeviceIdentifier;
import org.wso2.carbon.mdm.api.common.MDMAPIException;
import org.wso2.carbon.mdm.api.util.MDMAPIUtils;
import org.wso2.carbon.mdm.api.util.ResponsePayload;
import org.wso2.carbon.mdm.beans.PolicyWrapper;
import org.wso2.carbon.mdm.beans.PriorityUpdatedPolicyWrapper;
import org.wso2.carbon.mdm.util.MDMUtil;
import org.wso2.carbon.policy.mgt.common.PolicyAdministratorPoint;
import org.wso2.carbon.policy.mgt.common.PolicyManagementException;
import org.wso2.carbon.policy.mgt.common.PolicyMonitoringTaskException;
import org.wso2.carbon.policy.mgt.common.monitor.ComplianceData;
import org.wso2.carbon.policy.mgt.common.monitor.PolicyComplianceException;
import org.wso2.carbon.policy.mgt.core.PolicyManagerService;
import org.wso2.carbon.policy.mgt.core.task.TaskScheduleService;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

public class Policy {
    private static Log log = LogFactory.getLog(Policy.class);

    @POST
    @Path("inactive-policy")
    public ResponsePayload addPolicy(PolicyWrapper policyWrapper) throws MDMAPIException {

        PolicyManagerService policyManagementService = MDMAPIUtils.getPolicyManagementService();
        ResponsePayload responseMsg = new ResponsePayload();
        org.wso2.carbon.policy.mgt.common.Policy policy = new org.wso2.carbon.policy.mgt.common.Policy();
        policy.setPolicyName(policyWrapper.getPolicyName());
        policy.setProfileId(policyWrapper.getProfileId());

        policy.setProfile(MDMUtil.convertProfile(policyWrapper.getProfile()));
        policy.setOwnershipType(policyWrapper.getOwnershipType());
        policy.setRoles(policyWrapper.getRoles());
        policy.setUsers(policyWrapper.getUsers());
        policy.setTenantId(policyWrapper.getTenantId());
        policy.setCompliance(policyWrapper.getCompliance());

        try {
            PolicyAdministratorPoint pap = policyManagementService.getPAP();
            pap.addPolicy(policy);
            Response.status(HttpStatus.SC_CREATED);
            responseMsg.setMessageFromServer("Policy has been added successfully.");
            return responseMsg;
        } catch (PolicyManagementException e) {
            String error = "Policy Management related exception";
            log.error(error, e);
            throw new MDMAPIException(error, e);
        }
    }

    @POST
    @Path("active-policy")
    public ResponsePayload addActivePolicy(PolicyWrapper policyWrapper) throws MDMAPIException {

        PolicyManagerService policyManagementService = MDMAPIUtils.getPolicyManagementService();
        ResponsePayload responseMsg = new ResponsePayload();
        org.wso2.carbon.policy.mgt.common.Policy policy = new org.wso2.carbon.policy.mgt.common.Policy();
        policy.setPolicyName(policyWrapper.getPolicyName());
        policy.setProfileId(policyWrapper.getProfileId());

        policy.setProfile(MDMUtil.convertProfile(policyWrapper.getProfile()));
        policy.setOwnershipType(policyWrapper.getOwnershipType());
        policy.setRoles(policyWrapper.getRoles());
        policy.setUsers(policyWrapper.getUsers());
        policy.setTenantId(policyWrapper.getTenantId());
        policy.setCompliance(policyWrapper.getCompliance());
        policy.setActive(true);

        try {
            PolicyAdministratorPoint pap = policyManagementService.getPAP();
            pap.addPolicy(policy);
            Response.status(HttpStatus.SC_CREATED);
            responseMsg.setMessageFromServer("Policy has been added successfully.");
            return responseMsg;
        } catch (PolicyManagementException e) {
            String error = "Policy Management related exception";
            log.error(error, e);
            throw new MDMAPIException(error, e);
        }
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public Response getAllPolicies() throws MDMAPIException {
        PolicyManagerService policyManagementService = MDMAPIUtils.getPolicyManagementService();
        List<org.wso2.carbon.policy.mgt.common.Policy> policies;
        try {
            PolicyAdministratorPoint policyAdministratorPoint = policyManagementService.getPAP();
            policies = policyAdministratorPoint.getPolicies();
        } catch (PolicyManagementException e) {
            String error = "Policy Management related exception";
            log.error(error, e);
            throw new MDMAPIException(error, e);
        }
        ResponsePayload responsePayload = new ResponsePayload();
        responsePayload.setStatusCode(HttpStatus.SC_OK);
        responsePayload.setMessageFromServer("Sending all retrieved device policies.");
        responsePayload.setResponseContent(policies);
        return Response.status(HttpStatus.SC_OK).entity(responsePayload).build();
    }

    @GET
    @Path("count")
    public int getPolicyCount() throws MDMAPIException {
        PolicyManagerService policyManagementService = MDMAPIUtils.getPolicyManagementService();
        try {
            PolicyAdministratorPoint policyAdministratorPoint = policyManagementService.getPAP();
            return policyAdministratorPoint.getPolicyCount();
        } catch (PolicyManagementException e) {
            String error = "Policy Management related exception";
            log.error(error, e);
            throw new MDMAPIException(error, e);
        }
    }

    @POST
    @Path("{id}")
    public ResponsePayload updatePolicy(org.wso2.carbon.policy.mgt.common.Policy policy, @PathParam("id") int policyId)
            throws MDMAPIException {
        PolicyManagerService policyManagementService = MDMAPIUtils.getPolicyManagementService();
        ResponsePayload responseMsg = new ResponsePayload();
        try {
            PolicyAdministratorPoint pap = policyManagementService.getPAP();
            policy.setProfile(pap.getProfile(policy.getProfileId()));
            org.wso2.carbon.policy.mgt.common.Policy previousPolicy = pap.getPolicy(policyId);
            policy.setPolicyName(previousPolicy.getPolicyName());
            pap.updatePolicy(policy);
            Response.status(HttpStatus.SC_OK);
            responseMsg.setMessageFromServer("Policy has been updated successfully.");
            return responseMsg;
        } catch (PolicyManagementException e) {
            String error = "Policy Management related exception";
            log.error(error, e);
            throw new MDMAPIException(error, e);
        }
    }

    @PUT
    @Path("priorities")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public Response updatePolicyPriorities(List<PriorityUpdatedPolicyWrapper> priorityUpdatedPolicies)
            throws MDMAPIException {
        PolicyManagerService policyManagementService = MDMAPIUtils.getPolicyManagementService();
        List<org.wso2.carbon.policy.mgt.common.Policy> policiesToUpdate =
                new ArrayList<org.wso2.carbon.policy.mgt.common.Policy>();
        int i;
        for (i = 0; i < priorityUpdatedPolicies.size(); i++) {
            org.wso2.carbon.policy.mgt.common.Policy policyObj = new org.wso2.carbon.policy.mgt.common.Policy();
            policyObj.setId(priorityUpdatedPolicies.get(i).getId());
            policyObj.setPriorityId(priorityUpdatedPolicies.get(i).getPriority());
            policiesToUpdate.add(policyObj);
        }
        boolean policiesUpdated;
        try {
            PolicyAdministratorPoint pap = policyManagementService.getPAP();
            policiesUpdated = pap.updatePolicyPriorities(policiesToUpdate);
        } catch (PolicyManagementException e) {
            String error = "Exception in updating policy priorities.";
            log.error(error, e);
            throw new MDMAPIException(error, e);
        }
        ResponsePayload responsePayload = new ResponsePayload();
        if (policiesUpdated) {
            responsePayload.setStatusCode(HttpStatus.SC_OK);
            responsePayload.setMessageFromServer("Policy Priorities successfully updated.");
            return Response.status(HttpStatus.SC_OK).entity(responsePayload).build();
        } else {
            responsePayload.setStatusCode(HttpStatus.SC_BAD_REQUEST);
            responsePayload.setMessageFromServer("Policy priorities did not update. Bad Request.");
            return Response.status(HttpStatus.SC_BAD_REQUEST).entity(responsePayload).build();
        }
    }

    @DELETE
    @Path("{id}")
    @Produces({MediaType.APPLICATION_JSON})
    public Response deletePolicy(@PathParam("id") int policyId) throws MDMAPIException {
        PolicyManagerService policyManagementService = MDMAPIUtils.getPolicyManagementService();
        boolean policyDeleted;
        try {
            PolicyAdministratorPoint pap = policyManagementService.getPAP();
            org.wso2.carbon.policy.mgt.common.Policy policy = pap.getPolicy(policyId);
            policyDeleted = pap.deletePolicy(policy);
        } catch (PolicyManagementException e) {
            String error = "Exception in deleting policy by id:" + policyId;
            log.error(error, e);
            throw new MDMAPIException(error, e);
        }
        ResponsePayload responsePayload = new ResponsePayload();
        if (policyDeleted) {
            responsePayload.setStatusCode(HttpStatus.SC_OK);
            responsePayload.setMessageFromServer("Policy by id:" + policyId + " has been successfully deleted.");
            return Response.status(HttpStatus.SC_OK).entity(responsePayload).build();
        } else {
            responsePayload.setStatusCode(HttpStatus.SC_BAD_REQUEST);
            responsePayload.setMessageFromServer("Policy by id:" + policyId + " does not exist.");
            return Response.status(HttpStatus.SC_BAD_REQUEST).entity(responsePayload).build();
        }
    }

//    @GET
//    @Path("task/{mf}")
//    public int taskService(@PathParam("mf") int monitoringFrequency) throws MDMAPIException {
//        int policyCount = 0;
//        PolicyManagerService policyManagementService = MDMAPIUtils.getPolicyManagementService();
//        try {
//            TaskScheduleService taskScheduleService = policyManagementService.getTaskScheduleService();
//            taskScheduleService.startTask(monitoringFrequency);
//            return policyCount;
//        } catch (PolicyMonitoringTaskException e) {
//            String error = "Policy Management related exception";
//            log.error(error, e);
//            throw new MDMAPIException(error, e);
//        }
//    }


    @PUT
    @Produces("application/json")
    @Path("activate/{id}")
    public Response activatePolicy(@PathParam("id") int policyId) throws MDMAPIException {
        try {
            PolicyManagerService policyManagementService = MDMAPIUtils.getPolicyManagementService();
            PolicyAdministratorPoint pap = policyManagementService.getPAP();
            pap.activatePolicy(policyId);
        } catch (PolicyManagementException e) {
            String error = "Exception in activating policy by id:" + policyId;
            log.error(error, e);
            throw new MDMAPIException(error, e);
        }

        ResponsePayload responsePayload = new ResponsePayload();
        responsePayload.setStatusCode(HttpStatus.SC_OK);
        responsePayload.setMessageFromServer("Policy by id:" + policyId + " has been successfully activated.");
        return Response.status(HttpStatus.SC_OK).entity(responsePayload).build();

    }


    @PUT
    @Produces("application/json")
    @Path("inactivate/{id}")
    public Response inactivatePolicy(@PathParam("id") int policyId) throws MDMAPIException {

        try {
            PolicyManagerService policyManagementService = MDMAPIUtils.getPolicyManagementService();
            PolicyAdministratorPoint pap = policyManagementService.getPAP();
            pap.inactivatePolicy(policyId);
        } catch (PolicyManagementException e) {
            String error = "Exception in inactivating policy by id:" + policyId;
            log.error(error, e);
            throw new MDMAPIException(error, e);
        }
        ResponsePayload responsePayload = new ResponsePayload();
        responsePayload.setStatusCode(HttpStatus.SC_OK);
        responsePayload.setMessageFromServer("Policy by id:" + policyId + " has been successfully inactivated.");
        return Response.status(HttpStatus.SC_OK).entity(responsePayload).build();
    }


    @PUT
    @Produces("application/json")
    @Path("apply-changes")
    public Response applyChanges() throws MDMAPIException {

        try {
            PolicyManagerService policyManagementService = MDMAPIUtils.getPolicyManagementService();
            PolicyAdministratorPoint pap = policyManagementService.getPAP();
            pap.publishChanges();


        } catch (PolicyManagementException e) {
            String error = "Exception in applying changes.";
            log.error(error, e);
            throw new MDMAPIException(error, e);
        }
        ResponsePayload responsePayload = new ResponsePayload();
        responsePayload.setStatusCode(HttpStatus.SC_OK);
        responsePayload.setMessageFromServer("Changes have been successfully updated.");
        return Response.status(HttpStatus.SC_OK).entity(responsePayload).build();
    }

    @GET
    @Path("start-task/{milliseconds}")
    public Response startTaskService(@PathParam("milliseconds") int monitoringFrequency) throws MDMAPIException {

        PolicyManagerService policyManagementService = MDMAPIUtils.getPolicyManagementService();
        try {
            TaskScheduleService taskScheduleService = policyManagementService.getTaskScheduleService();
            taskScheduleService.startTask(monitoringFrequency);


        } catch (PolicyMonitoringTaskException e) {
            String error = "Policy Management related exception.";
            log.error(error, e);
            throw new MDMAPIException(error, e);
        }
        ResponsePayload responsePayload = new ResponsePayload();
        responsePayload.setStatusCode(HttpStatus.SC_OK);
        responsePayload.setMessageFromServer("Policy monitoring service started successfully.");
        return Response.status(HttpStatus.SC_OK).entity(responsePayload).build();
    }


    @GET
    @Path("update-task/{milliseconds}")
    public Response updateTaskService(@PathParam("milliseconds") int monitoringFrequency) throws MDMAPIException {

        PolicyManagerService policyManagementService = MDMAPIUtils.getPolicyManagementService();
        try {
            TaskScheduleService taskScheduleService = policyManagementService.getTaskScheduleService();
            taskScheduleService.updateTask(monitoringFrequency);

        } catch (PolicyMonitoringTaskException e) {
            String error = "Policy Management related exception.";
            log.error(error, e);
            throw new MDMAPIException(error, e);
        }
        ResponsePayload responsePayload = new ResponsePayload();
        responsePayload.setStatusCode(HttpStatus.SC_OK);
        responsePayload.setMessageFromServer("Policy monitoring service updated successfully.");
        return Response.status(HttpStatus.SC_OK).entity(responsePayload).build();
    }


    @GET
    @Path("stop-task")
    public Response stopTaskService() throws MDMAPIException {

        PolicyManagerService policyManagementService = MDMAPIUtils.getPolicyManagementService();
        try {
            TaskScheduleService taskScheduleService = policyManagementService.getTaskScheduleService();
            taskScheduleService.stopTask();

        } catch (PolicyMonitoringTaskException e) {
            String error = "Policy Management related exception.";
            log.error(error, e);
            throw new MDMAPIException(error, e);
        }
        ResponsePayload responsePayload = new ResponsePayload();
        responsePayload.setStatusCode(HttpStatus.SC_OK);
        responsePayload.setMessageFromServer("Policy monitoring service stopped successfully.");
        return Response.status(HttpStatus.SC_OK).entity(responsePayload).build();
    }


    @GET
    @Path("{type}/{id}")
    public ComplianceData getComplianceDataOfDevice(@PathParam("id") String id, @PathParam("type") String type) throws
            MDMAPIException {
        try {
            DeviceIdentifier deviceIdentifier = MDMAPIUtils.instantiateDeviceIdentifier(id, type);
            PolicyManagerService policyManagementService = MDMAPIUtils.getPolicyManagementService();
            return policyManagementService.getDeviceCompliance(deviceIdentifier);
        } catch (MDMAPIException e) {
            String error = "Error occurred while getting the policy management service.";
            log.error(error, e);
            throw new MDMAPIException(error, e);
        } catch (PolicyComplianceException e) {
            String error = "Error occurred while getting the compliance data.";
            log.error(error, e);
            throw new MDMAPIException(error, e);
        }
    }
}
