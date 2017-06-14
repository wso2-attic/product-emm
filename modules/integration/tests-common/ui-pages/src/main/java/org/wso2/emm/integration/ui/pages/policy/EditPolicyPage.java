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
import org.openqa.selenium.interactions.Actions;
import org.wso2.emm.integration.ui.pages.CommonUtil;
import org.wso2.emm.integration.ui.pages.UIElementMapper;

import java.io.IOException;

public class EditPolicyPage {
    private WebDriver driver;
    private Actions actions;
    private UIElementMapper uiElementMapper;

    public EditPolicyPage(WebDriver driver) throws IOException {
        this.driver = driver;
        this.actions = new Actions(driver);
        this.uiElementMapper = UIElementMapper.getInstance();
        // Check that we're on the right page.
        if (!(driver.getCurrentUrl().contains("/policy/edit"))) {
            throw new IllegalStateException("This is not the edit policy");
        }
    }

    /**
     * Imitates edit policy action.
     *
     * @param policyName new policy name to be appended.
     * @throws IOException
     * @throws InterruptedException
     */
    public void editPolicy(String policyName) throws IOException, InterruptedException {
        driver.findElement(By.xpath(uiElementMapper.getElement("emm.policy.add.profile.encryption"))).click();
        WebElement enableElement = driver
                .findElement(By.xpath(uiElementMapper.getElement("emm.policy.add.profile.encryption.enable")));
        actions.moveToElement(enableElement).click().perform();
        CommonUtil.waitAndClick(driver, By.xpath(uiElementMapper.getElement("emm.policy.add.profile.edit.continue")));
        CommonUtil.waitAndClick(driver,
                By.xpath(uiElementMapper.getElement("emm.policy.add.groups.continue.button.xpath")));
        WebElement policyNameField = driver
                .findElement(By.xpath(uiElementMapper.getElement("emm.policy.add.name.input.xpath")));
        policyNameField.sendKeys(policyName);
        driver.findElement(By.xpath(uiElementMapper.getElement("emm.policy.add.publish.button.xpath"))).click();
        String resultText = driver.findElement(By.id(uiElementMapper.getElement("emm.policy.add.publish.created.msg")))
                .getText();
        if (!resultText.contains("POLICY IS SUCCESSFULLY RE-CONFIGURED.")) {
            throw new IllegalStateException("Policy was not updated");
        }
    }
}
