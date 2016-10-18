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
package org.wso2.emm.integration.ui.pages.certificateConfiguration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.wso2.emm.integration.ui.pages.UIElementMapper;

import java.awt.*;
import java.io.IOException;
import java.net.MalformedURLException;

public class AddCertificatePage {
    private WebDriver driver;
    private UIElementMapper uiElementMapper;

    public AddCertificatePage(WebDriver driver) throws IOException {
        this.driver = driver;
        this.uiElementMapper = UIElementMapper.getInstance();
        if (!(driver.getCurrentUrl().contains("certificates/add"))) {
            throw new IllegalStateException("This is not the add certificate page");
        }
    }

    public void addCertificate(String serialNum) throws AWTException, MalformedURLException {
        WebElement serialNumField = driver
                .findElement(By.id(uiElementMapper.getElement("emm.certificate.add.serialnum")));
        serialNumField.sendKeys(serialNum);
        WebElement certFileField = driver
                .findElement(By.id(uiElementMapper.getElement("emm.certificate.add.filename")));
        certFileField.sendKeys(
                AddCertificatePage.class.getResource("/certificates/cert.pem").getPath().replace("file:", ""));

        WebElement addCertificateButton = driver
                .findElement(By.id(uiElementMapper.getElement("emm.certificate.add.button")));
        addCertificateButton.click();
        String resultText = driver.findElement(By.id(uiElementMapper.getElement("emm.certificate.publish.msg.div")))
                .getText();
        if (!resultText.contains("CERTIFICATE WAS ADDED SUCCESSFULLY.")) {
            throw new IllegalStateException("Certificate was not added");
        }
    }
}
