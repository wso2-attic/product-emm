/*
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.carbon.mdm.mobileservices.windows.services.adminoperations.impl;

import org.wso2.carbon.device.mgt.common.operation.mgt.Operation;
import org.wso2.carbon.mdm.mobileservices.windows.common.exceptions.WindowsDeviceEnrolmentException;
import org.wso2.carbon.mdm.mobileservices.windows.common.SyncmlCommandType;
import org.wso2.carbon.mdm.mobileservices.windows.services.adminoperations.Operations;
import org.wso2.carbon.mdm.mobileservices.windows.services.adminoperations.beans.OperationRequest;
import org.wso2.carbon.mdm.mobileservices.windows.services.adminoperations.beans.OperationResponse;
import org.wso2.carbon.mdm.mobileservices.windows.services.adminoperations.util.OperationStore;

/**
 * Implementation class of operations interface. Each method in this class receives the operations comes via UI
 * and persists those in the correct format.
 */
public class OperationsImpl implements Operations {

    @Override
    public OperationResponse lock(OperationRequest lock) throws WindowsDeviceEnrolmentException {

        OperationResponse operationResponse = new OperationResponse();

        if(OperationStore.storeOperation(lock, Operation.Type.COMMAND, "LOCK")){
            operationResponse.setStatusCode("Lock operation added successfully.");
            return operationResponse;
        }
        else{
            operationResponse.setErrorCode("Error while storing Lock operation.");
            return operationResponse;
        }
    }

    @Override
    public OperationResponse ring(OperationRequest ring) throws WindowsDeviceEnrolmentException {
        OperationResponse operationResponse = new OperationResponse();

        if(OperationStore.storeOperation(ring, Operation.Type.COMMAND, "RING")){
            operationResponse.setStatusCode("Ring operation added successfully.");
            return operationResponse;
        }
        else{
            operationResponse.setErrorCode("Error while storing Ring operation.");
            return operationResponse;
        }
    }

    @Override
    public OperationResponse wipe(OperationRequest wipe) throws WindowsDeviceEnrolmentException {
        OperationResponse operationResponse = new OperationResponse();

        if(OperationStore.storeOperation(wipe, Operation.Type.COMMAND, "WIPE")){
            operationResponse.setStatusCode("Wipe operation added successfully.");
            return operationResponse;
        }
        else{
            operationResponse.setErrorCode("Error while storing Wipe operation.");
            return operationResponse;
        }
    }

    @Override
    public OperationResponse wifi(OperationRequest wifi) throws WindowsDeviceEnrolmentException {

        OperationResponse operationResponse = new OperationResponse();

        if(OperationStore.storeOperation(wifi, Operation.Type.CONFIG, SyncmlCommandType.WIFI.getValue())){
            operationResponse.setStatusCode("Wifi operation added successfully.");
            return operationResponse;
        }
        else{
            operationResponse.setErrorCode("Error while storing Lock operation.");
            return operationResponse;
        }
    }
}
