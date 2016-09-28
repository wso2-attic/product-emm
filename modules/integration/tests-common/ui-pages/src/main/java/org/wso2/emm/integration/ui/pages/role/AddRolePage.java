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

package org.wso2.emm.integration.ui.pages.role;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.wso2.emm.integration.ui.pages.UIElementMapper;

import java.io.IOException;

public class AddRolePage {
    private WebDriver driver;
    private UIElementMapper uiElementMapper;
    private static final Log log = LogFactory.getLog(RoleListPage.class);

    public AddRolePage(WebDriver driver) throws IOException {
        this.driver = driver;
        this.uiElementMapper = UIElementMapper.getInstance();
        // Check that we're on the right page.
        if (!(driver.getCurrentUrl().contains("role/add"))) {
            throw new IllegalStateException("This is not the add role page");
        }
    }

    public void addRole(String role) {
        // web driver wait for 10 sec
        WebDriverWait waitLoad = new WebDriverWait(driver, 10);
        WebElement roleName = driver.findElement(By.id(uiElementMapper.getElement("emm.roles.add.rolename.input")));
        roleName.sendKeys(role);
        WebElement addRoleButton = driver.findElement(By.id(uiElementMapper.getElement("emm.roles.add.role.button")));
        addRoleButton.click();
        try {
            By permissionList  = By.id(uiElementMapper.getElement(("emm.roles.update.permissionlist")));
            waitLoad.until(ExpectedConditions.visibilityOfElementLocated(permissionList));
        } catch (TimeoutException e) {
            throw new IllegalStateException("Role was not added");
        }
    }
}
