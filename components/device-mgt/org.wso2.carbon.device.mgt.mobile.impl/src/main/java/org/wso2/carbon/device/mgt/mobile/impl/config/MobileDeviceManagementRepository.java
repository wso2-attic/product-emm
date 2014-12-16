/*
 * Copyright (c) 2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.wso2.carbon.device.mgt.mobile.impl.config;

import org.wso2.carbon.device.mgt.mobile.impl.config.datasource.MobileDataSourceConfig;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Class for holding management repository data
 */
@XmlRootElement(name = "ManagementRepository")
public class MobileDeviceManagementRepository {

	private MobileDataSourceConfig mobileDataSourceConfig;

	@XmlElement(name = "DataSourceConfiguration", nillable = false)
	public MobileDataSourceConfig getMobileDataSourceConfig() {
		return mobileDataSourceConfig;
	}

	public void setMobileDataSourceConfig(MobileDataSourceConfig mobileDataSourceConfig) {
		this.mobileDataSourceConfig = mobileDataSourceConfig;
	}

}
