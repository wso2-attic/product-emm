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

package org.wso2.emm.integration.ui.pages.policy;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.wso2.emm.integration.ui.pages.UIElementMapper;

import java.io.IOException;

public class PolicyPriorityPage {
    private WebDriver driver;
    private Actions actions;
    private UIElementMapper uiElementMapper;

    public PolicyPriorityPage(WebDriver driver) throws IOException {
        this.driver = driver;
        this.actions = new Actions(driver);
        this.uiElementMapper = UIElementMapper.getInstance();
        // Check that we're on the right page.
        if (!(driver.getCurrentUrl().contains("/policy/priority"))) {
            throw new IllegalStateException("This is not the policy priority page.");
        }
    }

    /**
     * Imitates policy priority changing action.
     */
    public void changePolicyPriority() {
        WebElement source = driver
                .findElement(By.xpath(uiElementMapper.getElement("emm.policy.priority.list.element.first.span")));
        WebElement target = driver
                .findElement(By.xpath(uiElementMapper.getElement("emm.policy.priority.list.element.second.span")));

        actions.clickAndHold(source).moveToElement(target).build().perform();
        actions.moveByOffset(0, 5).build().perform();
        actions.release().build().perform();
        driver.findElement(By.id(uiElementMapper.getElement("emm.policy.priority.save.button.id"))).click();

        WebDriverWait wait = new WebDriverWait(driver, 10);
        By successMessageDiv = By.xpath(uiElementMapper.getElement("emm.policy.priority.updated.message.div"));
        wait.until(ExpectedConditions.visibilityOfElementLocated(successMessageDiv));
        String resultText = driver.findElement(successMessageDiv).getText();
        if (!resultText.contains("Done. New Policy priorities were successfully updated.")) {
            throw new IllegalStateException("Policy priority list was not updated.");
        }
    }
}
