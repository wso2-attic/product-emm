/**
 *  Copyright (c) 2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.wso2.carbon.device.mgt.mobile.config;

import org.wso2.carbon.apimgt.api.APIProvider;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@XmlRootElement(name = "API")
public class APIConfig {

    private String name;
    private String owner;
    private String context;
    private String endpoint;
    private String version;
    private String transports;
    private APIProvider provider;

    public void init(APIProvider provider) {
        this.provider = provider;
    }

    @XmlTransient
    public APIProvider getProvider() {
        return provider;
    }

    @XmlElement(name = "Name", nillable = false)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @XmlElement(name = "Owner", nillable = false)
    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    @XmlElement(name = "Context", nillable = false)
    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    @XmlElement(name = "Endpoint", nillable = false)
    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    @XmlElement(name = "Version", nillable = false)
    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @XmlElement(name = "Transports", nillable = false)
    public String getTransports() {
        return transports;
    }

    public void setTransports(String transports) {
        this.transports = transports;
    }

}
