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

package org.wso2.emm.integration.ui.pages.user;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.wso2.emm.integration.ui.pages.UIElementMapper;

import java.io.IOException;

public class UserEditPage {
    private WebDriver driver;

    private UIElementMapper uiElementMapper;
    private static final Log log = LogFactory.getLog(UserListPage.class);

    public UserEditPage(WebDriver driver) throws IOException {
        this.driver = driver;
        this.uiElementMapper = UIElementMapper.getInstance();
        //Check that we're on the right page.
        if (driver.findElement(By.id(uiElementMapper.getElement("emm.user.add.button"))).getText() == null) {
            throw new IllegalStateException("This is not the add edit page");
        }
    }

    public void editUser() throws IOException {
        WebElement deleteButton = driver.findElement(By.xpath(uiElementMapper.getElement("emm.user.delete.button")));
        deleteButton.click();
        WebElement deleteConfirmButton = driver.findElement(By.id(uiElementMapper.getElement("emm.user.delete.button" +
                                                                                             ".confirm")));
        deleteConfirmButton.click();
    }
}