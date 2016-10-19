/*
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * you may obtain a copy of the License at
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

package org.wso2.emm.ui.integration.test;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.extensions.selenium.BrowserManager;
import org.wso2.emm.integration.ui.pages.EMMIntegrationUiBaseTestCase;
import org.wso2.emm.integration.ui.pages.policy.AddPolicyPage;
import org.wso2.emm.integration.ui.pages.policy.EditPolicyPage;
import org.wso2.emm.integration.ui.pages.policy.PolicyPriorityPage;
import org.wso2.emm.integration.ui.pages.policy.RemovePolicyPage;

import java.util.ArrayList;
import java.util.List;

public class PolicyTestCase extends EMMIntegrationUiBaseTestCase {

    private WebDriver driver;
    private static final Log log = LogFactory.getLog(UserTestCase.class);

    @BeforeClass(alwaysRun = true)
    public void setUp() throws Exception {
        super.init();
        driver = BrowserManager.getWebDriver();
        LoginUtils.login(driver, automationContext, getWebAppURL());
    }

    @Test(description = "verify add policy to emm")
    public void testAddPolicy() throws Exception {
        driver.get(getWebAppURL() + Constants.MDM_POLICY_ADD_URL);
        AddPolicyPage addPolicyPage = new AddPolicyPage(driver);
        addPolicyPage.addPolicy("PassCode");
    }

    @Test(description = "verify edit policy in emm", dependsOnMethods = { "testAddPolicy" })
    public void testEditPolicy() throws Exception {
        driver.get(getWebAppURL() + GetURL(Constants.MDM_POLICY_EDIT_URL));
        EditPolicyPage editPolicyPage = new EditPolicyPage(driver);
        editPolicyPage.editPolicy("Encryption");
    }

    @Test(description = "Verify editing policy priority")
    public void testPolicyPriority() throws Exception {
        driver.get(getWebAppURL() + GetURL(Constants.MDM_POLICY_ADD_URL));
        AddPolicyPage addPolicyPage = new AddPolicyPage(driver);
        List<String> policyNameList = new ArrayList<>();
        policyNameList.add("policy1");
        policyNameList.add("policy2");
        policyNameList.add("policy3");
        addPolicyPage.addMultiplePolicy(policyNameList);
        driver.get(getWebAppURL() + GetURL(Constants.MDM_POLICY_PRIORITY_URL));
        PolicyPriorityPage policyPriorityPage = new PolicyPriorityPage(driver);
        policyPriorityPage.changePolicyPriority();
    }

    @Test(description = "verify remove role to emm", dependsOnMethods = { "testEditPolicy" })
    public void testRemovePolicy() throws Exception {
        driver.get(getWebAppURL() + GetURL(Constants.MDM_POLICY_URL));
        RemovePolicyPage removePolicyPage = new RemovePolicyPage(driver);
        removePolicyPage.removePolicy();
    }

    @AfterClass(alwaysRun = true)
    public void tearDown() throws Exception {
        driver.quit();
    }

    private String GetURL(String endPoint){
        return endPoint+"?id="+Constants.POLICY_ID;
    }
}
