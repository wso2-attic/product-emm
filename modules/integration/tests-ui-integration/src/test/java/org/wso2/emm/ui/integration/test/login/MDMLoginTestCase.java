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

package org.wso2.emm.ui.integration.test.login;

import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.extensions.selenium.BrowserManager;
import org.wso2.emm.integration.ui.pages.EMMIntegrationUiBaseTestCase;
import org.wso2.emm.integration.ui.pages.home.MDMHomePage;
import org.wso2.emm.integration.ui.pages.login.MDMLoginPage;
import org.wso2.emm.ui.integration.test.Constants;

public class MDMLoginTestCase extends EMMIntegrationUiBaseTestCase {

    private WebDriver driver;

    @BeforeClass(alwaysRun = true, groups = { Constants.LOGIN_GROUP })
    public void setUp() throws Exception {
        super.init();
        driver = BrowserManager.getWebDriver();
        driver.get(getWebAppURL() + Constants.MDM_LOGIN_PATH);
    }

    @Test(groups = { Constants.LOGIN_GROUP }, description = "verify login to emm console")
    public void testLogin() throws Exception {
        MDMLoginPage test = new MDMLoginPage(driver);
        MDMHomePage home = test.loginAs(automationContext.getSuperTenant().getTenantAdmin().getUserName(),
                                        automationContext.getSuperTenant().getTenantAdmin().getPassword());
        home.logout();
        driver.close();
    }

    @Test(groups = { Constants.LOGIN_GROUP }, description = "verify change password and login.", dependsOnMethods = { "testLogin"})
    public void testPasswordChange() throws Exception {
        String tempPwd = "admin";
        String userName = automationContext.getSuperTenant().getTenantAdmin().getUserName();
        String password = automationContext.getSuperTenant().getTenantAdmin().getPassword();
        MDMLoginPage login = new MDMLoginPage(driver);
        MDMHomePage home = login.loginAs(userName, password);
        home.changePassword(userName, password, tempPwd);
        login = home.logout();
        home = login.loginAs(userName, tempPwd);
        home.changePassword(userName, tempPwd, password);
        home.logout();
    }

    @AfterClass(alwaysRun = true)
    public void tearDown() throws Exception {
        driver.quit();
    }
}
