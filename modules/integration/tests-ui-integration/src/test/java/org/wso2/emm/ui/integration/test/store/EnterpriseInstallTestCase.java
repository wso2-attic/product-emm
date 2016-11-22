/*
 * Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.emm.ui.integration.test.store;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.*;
import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.automation.engine.context.beans.User;
import org.wso2.carbon.automation.engine.frameworkutils.FrameworkPathUtil;
import org.wso2.carbon.automation.extensions.selenium.BrowserManager;
import org.wso2.carbon.integration.common.utils.exceptions.AutomationUtilException;
import org.wso2.carbon.integration.common.utils.mgt.ServerConfigurationManager;
import org.wso2.carbon.user.mgt.stub.UserAdminUserAdminException;
import org.wso2.emm.integration.ui.pages.EMMIntegrationUiBaseTestCase;
import org.wso2.emm.ui.integration.test.Constants;
import org.wso2.emm.ui.integration.test.LoginUtils;

import javax.xml.xpath.XPathExpressionException;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.RemoteException;
import java.util.List;

/**
 * This class is used to test whether the enterprise install is displayed for the relevant user and whether the correct
 * users and roles are displayed
 */
public class EnterpriseInstallTestCase extends EMMIntegrationUiBaseTestCase {
    private WebDriver driver;
    private String webAppUrl;

    /**
     * Initializes the class.
     *
     * @param userMode       user mode
     * @throws XPathExpressionException
     * @throws RemoteException
     * @throws UserAdminUserAdminException
     * @throws MalformedURLException
     */
    @Factory(dataProvider = "userMode")
    public EnterpriseInstallTestCase(TestUserMode userMode)
            throws XPathExpressionException, RemoteException, UserAdminUserAdminException, MalformedURLException {
        // Read the editor and viewer users from the automation.xml file.
        this.automationContext = new AutomationContext(MDM_PRODUCT_GROUP_NAME, userMode);
    }

    /**
     * Provides user modes.
     *
     * @return the user modes that need to be tested
     */
    @DataProvider(name = "userMode")
    private static Object[][] userModeProvider() {
        return new Object[][] { { TestUserMode.SUPER_TENANT_ADMIN }, { TestUserMode.TENANT_ADMIN }};
    }

    @BeforeClass(alwaysRun = true)
    public void setUp() throws IOException, XPathExpressionException, AutomationUtilException, InterruptedException {
        // Replace the app-manager.xml after replacing the roles for enterprise install, so that admins can do
        // enterprise install
        String appManagerConf =
                FrameworkPathUtil.getSystemResourceLocation() + File.separator + "files" + File.separator
                        + "app-manager.xml";
        String CONFIG_LOCATION =
                FrameworkPathUtil.getCarbonHome() + File.separator + "repository" + File.separator + "conf"
                        + File.separator + "app-manager.xml";
        FileUtils.copyFile(new File(appManagerConf), new File(CONFIG_LOCATION));
        // Restart the server after app-manager.xml is modified
        AutomationContext automationContext = new AutomationContext(MDM_PRODUCT_GROUP_NAME,
                TestUserMode.SUPER_TENANT_ADMIN);
        ServerConfigurationManager serverConfigurationManager = new ServerConfigurationManager(automationContext);
        this.webAppUrl = automationContext.getContextUrls().getWebAppURL();
        serverConfigurationManager.restartGracefully();
        driver = BrowserManager.getWebDriver();
        LoginUtils.loginToPublisherAndStore(Constants.PUBLISHER_LOGIN_PATH, driver, this.automationContext, webAppUrl);
    }

    @Test(description = "Testing whether enterprise install is visible for the authorized users and correct set of "
            + "users and roles are displayed, when clicking on enterprise install button")
    public void enterpriseInstalltest() throws XPathExpressionException, InterruptedException {
        driver.get(webAppUrl + "/publisher");
        driver.get(webAppUrl + "/publisher/asset/mobileapp");
        Thread.sleep(2000);
        String apkFileLocation =
                FrameworkPathUtil.getSystemResourceLocation() + File.separator + "apk_files" + File.separator
                        + "test.apk";
        String imageFileLocation =
                FrameworkPathUtil.getSystemResourceLocation() + File.separator + "images" + File.separator
                        + "image.jpg";
        driver.findElement(By.id("txtAppUpload")).sendKeys(apkFileLocation);
        driver.findElement(By.id("btn-app-upload")).click();
        driver.findElement(By.id("txtName")).clear();
        driver.findElement(By.id("txtName")).sendKeys("test");
        driver.findElement(By.id("txtDisplayName")).clear();
        driver.findElement(By.id("txtDisplayName")).sendKeys("test");
        driver.findElement(By.id("txtDescription")).clear();
        driver.findElement(By.id("txtDescription")).sendKeys("test");
        driver.findElement(By.id("txtBanner")).sendKeys(imageFileLocation);
        driver.findElement(By.id("txtScreenShot1")).sendKeys(imageFileLocation);
        driver.findElement(By.name("iconFile")).sendKeys(imageFileLocation);
        driver.findElement(By.id("submitButton")).click();
        Thread.sleep(30000); // Add enough time, so that the mobile get deployed
        driver.get(webAppUrl + "/publisher");
        driver.findElement(By.xpath("(//button[@type='button'])[3]")).click();
        Thread.sleep(2000);
        driver.findElement(By.xpath("(//button[@type='button'])[3]")).click();
        Thread.sleep(2000);
        driver.findElement(By.xpath("(//button[@type='button'])[3]")).click();
        Thread.sleep(2000);
        WebElement baseTable = driver.findElement(By.id("apps-listing-table"));
        List<WebElement> tableRows = baseTable.findElements(By.tagName("tr"));
        String id = tableRows.get(1).getAttribute("data-row");
        driver.get(webAppUrl + "/store");
        Thread.sleep(2000);
        LoginUtils.loginToPublisherAndStore("/store/t/" + automationContext.getContextTenant().getDomain() + "/login",
                driver, automationContext, webAppUrl);
        Thread.sleep(2000);
        driver.get(webAppUrl + "/store/t/" + automationContext.getContextTenant().getDomain() + "/assets/mobileapp");
        Thread.sleep(2000);
        driver.get(
                webAppUrl + "/store/t/" + automationContext.getContextTenant().getDomain() + "/assets/mobileapp/" + id);
        Thread.sleep(2000);
        Assert.assertTrue(driver.findElement(By.id("btn-ent-install")).isDisplayed(),
                "Enterprise install is not " + "visible");
        driver.findElement(By.id("btn-ent-install")).click();
        driver.findElement(By.cssSelector("[href='#ent-users']")).click();
        WebElement tableElement = driver.findElement(By.id("users-table"));
        List<WebElement> tr_collection = tableElement.findElements(By.xpath("id('users-table')/tbody/tr"));
        List<User> usersInCurrentTenant = automationContext.getContextTenant().getTenantUserList();
        Assert.assertEquals(usersInCurrentTenant.size() + 1, tr_collection.size(),
                "All the users in the tenant are " + "not displayed");

        // Logout as a tenant admin and login as a normal tenant user, who does not have the permission for
        // enterprise install and check whether the enterprise install is visible
        driver.get(webAppUrl + "/store/t/" + automationContext.getContextTenant().getDomain() + "/assets/mobileapp");
        Thread.sleep(2000);
        driver.get(webAppUrl + "/store/logout");
        Thread.sleep(2000);
        driver.get(webAppUrl + "/store");
        Thread.sleep(2000);
        driver.get(webAppUrl + "/store/t/" + automationContext.getContextTenant().getDomain() + "/login");
        Thread.sleep(2000);
        driver.findElement(By.id("username")).clear();
        driver.findElement(By.id("username")).sendKeys(usersInCurrentTenant.get(0).getUserName());
        driver.findElement(By.id("password")).clear();
        driver.findElement(By.id("password")).sendKeys(usersInCurrentTenant.get(0).getPassword());
        driver.findElement(By.xpath("//button[@type='submit']")).click();
        Thread.sleep(3000);
        driver.get(webAppUrl + "/store/t/" + automationContext.getContextTenant().getDomain() + "/assets/mobileapp");
        Thread.sleep(2000);
        driver.get(
                webAppUrl + "/store/t/" + automationContext.getContextTenant().getDomain() + "/assets/mobileapp/" + id);
        Thread.sleep(2000);
        Assert.assertFalse(isElementPresent(driver, By.id("btn-ent-install")),
                "Enterprise install is visible" + " for the users who are not authorized");
    }

    @AfterClass(alwaysRun = true)
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
