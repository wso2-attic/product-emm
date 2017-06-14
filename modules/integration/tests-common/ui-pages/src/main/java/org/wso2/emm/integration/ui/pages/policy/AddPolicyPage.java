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
import java.util.List;

public class AddPolicyPage {
    private WebDriver driver;
    private Actions actions;
    private UIElementMapper uiElementMapper;

    public AddPolicyPage(WebDriver driver) throws IOException {
        this.driver = driver;
        this.actions = new Actions(driver);
        this.uiElementMapper = UIElementMapper.getInstance();
        // Check that we're on the right page.
        if (!(driver.getCurrentUrl().contains("policy/add"))) {
            throw new IllegalStateException("This is not the add policy page");
        }
    }

    /**
     * Imitate the create policy function.
     *
     * @param policyName name of the policy to be added.
     * @throws IOException
     * @throws InterruptedException
     */
    public void addPolicy(String policyName) throws IOException, InterruptedException {
        driver.findElement(By.xpath(uiElementMapper.getElement("emm.policy.add.platform.android"))).click();
        //configure policy
        WebElement enableElement = driver
                .findElement(By.xpath(uiElementMapper.getElement("emm.policy.add.profile.passcode.enable")));
        actions.moveToElement(enableElement).click().build().perform();
        CommonUtil.waitAndClick(driver,
                By.xpath(uiElementMapper.getElement("emm.policy.add.profile.continue.button.xpath")));
        //Group Tab
        CommonUtil.waitAndClick(driver,
                By.xpath(uiElementMapper.getElement("emm.policy.add.groups.continue.button.xpath")));
        //Publish Policy Tab
        WebElement policyNameField = driver
                .findElement(By.xpath(uiElementMapper.getElement("emm.policy.add.name.input.xpath")));
        policyNameField.sendKeys(policyName);
        driver.findElement(By.xpath(uiElementMapper.getElement("emm.policy.add.publish.button.xpath"))).click();
        String resultText = driver.findElement(By.id(uiElementMapper.getElement("emm.policy.add.publish.created.msg")))
                .getText();
        if (!resultText.contains("POLICY CREATION IS SUCCESSFUL.")) {
            throw new IllegalStateException("Policy was not added");
        }
    }

    /**
     * Imitates adding multiple policies action
     *
     * @param policyNameList policy name list.
     * @throws IOException
     * @throws InterruptedException
     */
    public void addMultiplePolicy(List<String> policyNameList) throws IOException, InterruptedException {
        addPolicy(policyNameList.get(0));
        policyNameList.remove(0);
        for (String policyName : policyNameList) {
            driver.findElement(By.xpath(uiElementMapper.getElement("emm.policy.add.add.another.link"))).click();
            addPolicy(policyName);
        }
    }
}
