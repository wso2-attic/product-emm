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

import org.wso2.carbon.mdm.mobileservices.windows.services.adminoperations.Operations;
import org.wso2.carbon.mdm.mobileservices.windows.services.adminoperations.beans.OperationRequest;
import org.wso2.carbon.mdm.mobileservices.windows.services.adminoperations.beans.OperationResponse;

/**
 * Implementation class of operations interface. Each method in this class receives the operations comes via UI
 * and persists those in the correct format.
 */
public class OperationsImpl implements Operations {

    @Override
    public OperationResponse ring(OperationRequest ring) {
        return null;
    }

    @Override
    public OperationResponse wipe(OperationRequest wipe) {
        return null;
    }

    @Override
    public OperationResponse lock(OperationRequest lock) {
        return null;
    }

    @Override
    public OperationResponse wifi(OperationRequest wifi) {
        return null;
    }
}
