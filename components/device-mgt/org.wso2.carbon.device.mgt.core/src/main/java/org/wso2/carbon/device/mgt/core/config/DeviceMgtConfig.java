/*
 * Copyright (c) 2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.wso2.carbon.device.mgt.core.config;

import org.wso2.carbon.rssmanager.core.environment.Environment;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Represents RSS configuration.
 */
@XmlRootElement(name = "RSSConfiguration")
public final class DeviceMgtConfig {

	private String rssProvider;
	private Environment[] environments;
	private RSSManagementRepository rssMgtRepository;

	@XmlElement(name = "ManagementRepository", nillable = false)
	public RSSManagementRepository getRSSManagementRepository() {
		return rssMgtRepository;
	}

	public void setRSSManagementRepository(RSSManagementRepository rssMgtRepository) {
		this.rssMgtRepository = rssMgtRepository;
	}

	@XmlElementWrapper(name = "Environments", nillable = false)
	@XmlElement(name = "Environment", nillable = false)
	public Environment[] getRSSEnvironments() {
		return environments;
	}

	public void setRSSEnvironments(Environment[] environments) {
		this.environments = environments;
	}

	@XmlElement(name = "Provider", nillable = false)
	public String getRSSProvider() {
		return rssProvider;
	}

	public void setRSSProvider(String rssProvider) {
		this.rssProvider = rssProvider;
	}

}
