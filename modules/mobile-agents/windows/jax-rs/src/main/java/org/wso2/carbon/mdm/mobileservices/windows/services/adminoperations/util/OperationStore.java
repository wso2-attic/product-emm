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

package org.wso2.carbon.mdm.mobileservices.windows.services.adminoperations.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.device.mgt.common.DeviceIdentifier;
import org.wso2.carbon.device.mgt.common.operation.mgt.OperationManagementException;
import org.wso2.carbon.device.mgt.core.operation.mgt.CommandOperation;
import org.wso2.carbon.device.mgt.core.operation.mgt.ConfigOperation;
import org.wso2.carbon.device.mgt.core.operation.mgt.OperationManagerImpl;
import org.wso2.carbon.mdm.mobileservices.windows.services.adminoperations.beans.OperationRequest;
import org.wso2.carbon.utils.multitenancy.MultitenantConstants;

import java.util.List;

public class OperationStore {

    private static Log log = LogFactory.getLog(OperationStore.class);

    public static boolean storeOperations(OperationRequest operationRequest){

        List<DeviceIdentifier> devices = operationRequest.getDeviceList();

        OperationManagerImpl operationManager = getDeviceManagementService();

        if (operationRequest.getCommandOperations() != null) {
            List<CommandOperation> commandOperations = operationRequest.getCommandOperations();

            for(int i=0 ; i<commandOperations.size() ; i++){
                try {
                    operationManager.addOperation(commandOperations.get(i), devices);
                } catch (OperationManagementException e) {
                    String msg = "Failure occurred while storing command operation.";
                    log.error(msg);
                    return false;
                }
            }
        }
        if (operationRequest.getConfigOperations() != null) {
            List<ConfigOperation> configOperations = operationRequest.getConfigOperations();

            for(int i=0 ; i<configOperations.size() ; i++){
                try {
                    operationManager.addOperation(configOperations.get(i), devices);
                } catch (OperationManagementException e) {
                    String msg = "Failure occurred while storing configuration operation.";
                    log.error(msg);
                    return false;
                }
            }
        }
        return true;
    }

    private static OperationManagerImpl getDeviceManagementService() {

        OperationManagerImpl operationManager;
        PrivilegedCarbonContext.startTenantFlow();
        PrivilegedCarbonContext ctx = PrivilegedCarbonContext.getThreadLocalCarbonContext();
        ctx.setTenantDomain(MultitenantConstants.SUPER_TENANT_DOMAIN_NAME);
        ctx.setTenantId(MultitenantConstants.SUPER_TENANT_ID);
        operationManager = (OperationManagerImpl) ctx.getOSGiService(OperationManagerImpl.class, null);

        if (operationManager == null) {
            String msg = "Operation management service is not initialized";
            log.error(msg);
        }
        PrivilegedCarbonContext.endTenantFlow();
        return operationManager;
    }
}
