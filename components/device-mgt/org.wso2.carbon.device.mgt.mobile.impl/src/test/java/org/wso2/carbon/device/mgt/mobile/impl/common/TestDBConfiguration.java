/*
 * Copyright (c) 2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.device.mgt.mobile.impl.common;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Type")
public class TestDBConfiguration {

    private String connectionURL;
    private String driverClassName;
    private String username;
    private String password;

    @Override public String toString() {
        return "TestDataSourceConfiguration{" +
                "ConnectionURL='" + connectionURL + '\'' +
                ", DriverClassName='" + driverClassName + '\'' +
                ", Username='" + username + '\'' +
                ", Password='" + password + '\'' +
                ", Type='" + dbType + '\'' +
                '}';
    }

    private String dbType;

    @XmlElement(name = "ConnectionURL", nillable = false)
    public String getConnectionURL() {
        return connectionURL;
    }

    public void setConnectionURL(String connectionURL) {
        this.connectionURL = connectionURL;
    }

    @XmlElement(name = "DriverClassName", nillable = false)
    public String getDriverClassName() {
        return driverClassName;
    }

    public void setDriverClassName(String driverClassName) {
        this.driverClassName = driverClassName;
    }

    @XmlElement(name = "Username", nillable = false)
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @XmlElement(name = "Password", nillable = false)
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @XmlAttribute(name = "name")
    public String getType() {
        return dbType;
    }

    public void setType(String type) {
        this.dbType = type;
    }

}
