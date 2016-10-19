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

package org.wso2.emm.integration.ui.pages.home;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.wso2.emm.integration.ui.pages.CommonUtil;
import org.wso2.emm.integration.ui.pages.UIElementMapper;
import org.wso2.emm.integration.ui.pages.login.MDMLoginPage;

import java.io.IOException;

/**
 * This class represents the state and behavior of the MDM Home User interface.
 */
public class MDMHomePage {
    private static final Log log = LogFactory.getLog(MDMHomePage.class);
    private WebDriver driver;
    private WebDriverWait wait;
    private UIElementMapper uiElementMapper;

    public MDMHomePage(WebDriver driver) throws IOException {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, 10);
        this.uiElementMapper = UIElementMapper.getInstance();
        // Check that we're on the right page.
        if (!driver.findElement(By.xpath(uiElementMapper.getElement("emm.home.navbar.header.li.xpath"))).getText()
                .toUpperCase().contains("RESOURCE DASHBOARD")) {
            throw new IllegalStateException("This is not the home page");
        }
    }
    /**
     * Imitates logging out action.
     *
     * @return  the login page where log out action redirects to. {@see MDMLoginPage}
     * @throws IOException
     * @throws InterruptedException
     */
    public MDMLoginPage logout() throws IOException, InterruptedException {
        CommonUtil.waitAndClick(driver, By.xpath(uiElementMapper.getElement("emm.home.navbar.logged.in.user.span")));
        CommonUtil.waitAndClick(driver, By.xpath(uiElementMapper.getElement("emm.home.sign.out.button.xpath")));
        return new MDMLoginPage(driver);
    }

    /**
     * Imitate change password action.
     *
     * @param user            current logged in user's username.
     * @param currentPassword current password.
     * @param newPassword     changed password.
     * @throws InterruptedException
     */
    public void changePassword(String user, String currentPassword, String newPassword) throws InterruptedException {
        driver.findElement(By.xpath(uiElementMapper.getElement("emm.home.navbar.logged.in.user.span"))).click();
        driver.findElement(By.id(uiElementMapper.getElement("emm.home.change.password.button.id"))).click();

        CommonUtil.setValueOfHiddenInput(driver, uiElementMapper.getElement("emm.home.user.input.id"), user);
        By currentPasswordInput = By.id(uiElementMapper.getElement("emm.home.change.password.current.pwd.input"));
        wait.until(ExpectedConditions.visibilityOfElementLocated(currentPasswordInput));
        WebElement inputElement = driver.findElement(currentPasswordInput);
        inputElement.sendKeys(currentPassword);
        inputElement = driver.findElement(By.id(uiElementMapper.getElement("emm.home.change.password.new.pwd.input")));
        inputElement.sendKeys(newPassword);
        inputElement = driver
                .findElement(By.id(uiElementMapper.getElement("emm.home.change.password.retype.pwd.input")));
        inputElement.sendKeys(newPassword);
        driver.findElement(By.id(uiElementMapper.getElement("emm.home.change.password.yes.link.id"))).click();
        CommonUtil.waitAndClick(driver, By.id(uiElementMapper.getElement("emm.home.change.password.success.link")));
    }
}

