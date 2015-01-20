/*
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.wos2.carbon.policy.mgt.common;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.wos2.carbon.policy.mgt.common.utils.PolicyCreator;
import org.wso2.carbon.policy.mgt.common.FeatureManagementException;
import org.wso2.carbon.policy.mgt.common.Policy;
import org.wso2.carbon.policy.mgt.common.PolicyManagementException;
import org.wso2.carbon.policy.mgt.common.impl.PolicyManagement;

public class PolicyManagementTestCase {

    private static final Log log = LogFactory.getLog(PolicyManagementTestCase.class);

    Policy policy = PolicyCreator.createPolicy();

    private PolicyManagement policyManagement = new PolicyManagement();

    @Test(groups = "policy.mgt.test", description = "Testing the adding policy to a device")
    public void testAddPolicy() throws FeatureManagementException, PolicyManagementException {
        Assert.assertEquals(policyManagement.addPolicyToDevice("1212-ESDD-12ER-7890", "MD", policy), 0);
    }

    @Test(groups = "policy.mgt.test", description = "Testing the adding policy to a device type")
    public void testAddPolicyToDeviceType() throws FeatureManagementException, PolicyManagementException {
        Assert.assertEquals(policyManagement.addPolicyToDeviceType("MD", policy), 0);
    }

    @Test(groups = "policy.mgt.test", description = "Testing the adding policy to a user Role")
    public void testAddPolicyToRole() throws FeatureManagementException, PolicyManagementException {
        Assert.assertEquals(policyManagement.addPolicyToRole("Admin", policy), 0);
    }

}
