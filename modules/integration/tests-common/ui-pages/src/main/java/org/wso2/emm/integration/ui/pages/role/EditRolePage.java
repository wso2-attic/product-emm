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

public class EditRolePage {
    private WebDriver driver;
    private UIElementMapper uiElementMapper;
    private static final Log log = LogFactory.getLog(RoleListPage.class);

    public EditRolePage(WebDriver driver) throws IOException {
        this.driver = driver;
        this.uiElementMapper = UIElementMapper.getInstance();
        // Check that we're on the right page.
        if (!(driver.getCurrentUrl().contains("role/edit-permission"))) {
            throw new IllegalStateException("This is not the edit role page");
        }
    }

    public void editRole(String role){
        WebDriverWait waitLoad = new WebDriverWait(driver, 10);
        WebElement permissionItem = driver.findElement(By.xpath(uiElementMapper.getElement("emm.roles.update.permissionItemLogin")));
        permissionItem.click();
        WebElement editRoleButton = driver.findElement(By.id(uiElementMapper.getElement("emm.roles.update.role.button")));
        editRoleButton.click();
        try {
            By roleCreatedMsg  = By.xpath(uiElementMapper.getElement(("emm.roles.update.role.created.msg.div")));
            waitLoad.until(ExpectedConditions.visibilityOfElementLocated(roleCreatedMsg));
            String resultText = driver.findElement(roleCreatedMsg
            ).getText();

            if(!resultText.contains("PERMISSIONS WERE ASSIGNED TO THE ROLE SUCCESSFULLY")){
                throw new IllegalStateException("Role was not edited");
            }
        } catch (TimeoutException e) {
            throw new IllegalStateException("Role was not edited");
        }

    }
}
