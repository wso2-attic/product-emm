/*
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
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

package org.wso2.carbon.mdm.mobileservices.windows.common.beans;

/**
 * Bean class for storing Windows plugin properties after reading the property file.
 */
public class WindowsPluginProperties {

    private String keyStorePassword;
    private String privateKeyPassword;
    private String commonName;
    private String authPolicy;
    private String domain;
    private int notBeforeDays;
    private int notAfterDays;

    public String getKeyStorePassword() {
        return keyStorePassword;
    }

    public String getPrivateKeyPassword() {
        return privateKeyPassword;
    }

    public String getCommonName() {
        return commonName;
    }

    public int getNotBeforeDays() {
        return notBeforeDays;
    }

    public int getNotAfterDays() {
        return notAfterDays;
    }

    public void setKeyStorePassword(String keyStorePassword) {
        this.keyStorePassword = keyStorePassword;
    }

    public void setPrivateKeyPassword(String privateKeyPassword) {
        this.privateKeyPassword = privateKeyPassword;
    }

    public void setCommonName(String commonName) {
        this.commonName = commonName;
    }

    public void setNotBeforeDays(int notBeforeDays) {
        this.notBeforeDays = notBeforeDays;
    }

    public void setNotAfterDays(int notAfterDays) {
        this.notAfterDays = notAfterDays;
    }

    public String getAuthPolicy() {
        return authPolicy;
    }

    public void setAuthPolicy(String authPolicy) {
        this.authPolicy = authPolicy;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }


}

