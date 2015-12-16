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
package org.wso2.emm.integration.ui.pages.policy;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.wso2.emm.integration.ui.pages.UIElementMapper;
import java.io.IOException;

public class AddPolicyPage {
    private WebDriver driver;
    private UIElementMapper uiElementMapper;

    public AddPolicyPage(WebDriver driver) throws IOException{
        this.driver = driver;
        this.uiElementMapper = UIElementMapper.getInstance();
        // Check that we're on the right page.
        if (!(driver.getCurrentUrl().contains("policies/add-policy"))) {
            throw new IllegalStateException("This is not the add policy page");
        }
    }

    public void addPolicy(String policyName) throws IOException {

        driver.findElement(By.xpath(uiElementMapper.getElement("emm.add.policy.platform.android"))).click();
        driver.findElement(By.xpath(uiElementMapper.getElement("emm.add.policy.profile.camera"))).click();
        driver.findElement(By.xpath(uiElementMapper.getElement("emm.add.policy.profile.checkbox.camera"))).click();
        driver.findElement(By.xpath(uiElementMapper.getElement("emm.add.policy.profile.continue"))).click();
        driver.findElement(By.xpath(uiElementMapper.getElement("emm.add.policy.groups.continue"))).click();
        WebElement policyNameField = driver.findElement(By.xpath(uiElementMapper.getElement("emm.add.policy.name")));
        policyNameField.sendKeys(policyName);
        driver.findElement(By.xpath(uiElementMapper.getElement("emm.add.policy.publish"))).click();
        String resultText = driver.findElement(By.id(uiElementMapper.getElement("emm.add.policy.publish.created.msg.div")
        )).getText();
        if(!resultText.contains("POLICY CREATION IS SUCCESSFUL.")){
            throw new IllegalStateException("Policy was not added");
        }
    }
}
