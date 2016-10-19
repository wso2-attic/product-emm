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

package org.wso2.emm.integration.ui.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class CommonUtil {
    /**
     * Waits until the given element is clickable and perform the click event.
     *
     * @param driver  selenium web driver.
     * @param locator locator of the element.
     * @throws InterruptedException If error occurs with thread execution.
     */
    public static void waitAndClick(WebDriver driver, By locator) throws InterruptedException {
        Actions actions = new Actions(driver);
        WebDriverWait wait = new WebDriverWait(driver, 10);
        wait.until(ExpectedConditions.elementToBeClickable(locator));
        WebElement webElement = driver.findElement(locator);
        /*
        There is a issue in Selenium Chrome Driver where it performs click event in wrong places (#633). Mostly occur
        when the element to be clicked is placed outside the visible area. To overcome this issue scrolling the page to
        the element locator through a JavascriptExecutor
         */
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", webElement);
        Thread.sleep(500);
        actions.moveToElement(webElement).click().build().perform();
    }

    /**
     * Set value to a hidden input element identified by a given id.
     *
     * @param driver selenium web driver.
     * @param id     id of the input element.
     * @param value  value to set.
     */
    public static void setValueOfHiddenInput(WebDriver driver, String id, String value) {
        JavascriptExecutor jse = (JavascriptExecutor) driver;
        jse.executeScript("document.getElementById('" + id + "').setAttribute('value', '" + value + "');");
    }
}
