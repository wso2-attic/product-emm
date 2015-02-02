/*
 * Copyright (c) 2015, WSO2 Inc. (http:www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http:www.apache.orglicensesLICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.device.mgt.mobile.config.datasource;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Class for holding data source configuration in mobile-config.xml at parsing with JAXB.
 */
@XmlRootElement(name = "DataSourceConfiguration") public class MobileDataSourceConfig {

	private JNDILookupDefinition jndiLookupDefinition;

	@XmlElement(name = "JndiLookupDefinition", nillable = true)
	public JNDILookupDefinition getJndiLookupDefinition() {
		return jndiLookupDefinition;
	}

	public void setJndiLookupDefinition(JNDILookupDefinition jndiLookupDefinition) {
		this.jndiLookupDefinition = jndiLookupDefinition;
	}

}
