/*
*  Copyright (c) 2005-2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*  WSO2 Inc. licenses this file to you under the Apache License,
*  Version 2.0 (the "License"); you may not use this file except
*  in compliance with the License.
*  You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
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

    PolicyCreator creator = new PolicyCreator();
    Policy policy = creator.getPolicy();

    private PolicyManagement policyManagement = new PolicyManagement();

    @Test(groups = "policy.mgt.test", description = "Testing the first test case with testng.")
    public void testPolicy() {
        Assert.assertEquals("A", "A");
    }


    @Test(groups = "policy.mgt.test", description = "Testing the adding policy to a device")
    public void testAddPolicy() {
        try {
            Assert.assertEquals(policyManagement.addPolicyToDevice("1212-ESDD-12ER-7890", "MD", policy), 0);
        } catch (FeatureManagementException e) {
            log.error("Feature management exception happened.", e);
            Assert.fail();
        } catch (PolicyManagementException e) {
            log.error("Policy management exception happened.", e);
            Assert.fail();
        }
    }

}
