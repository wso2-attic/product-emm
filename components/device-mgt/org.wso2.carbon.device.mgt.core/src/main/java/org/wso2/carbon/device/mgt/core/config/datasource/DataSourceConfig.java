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
package org.wso2.carbon.device.mgt.core.config.datasource;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Class for holding data source configuration in malformed-cdm-config-no-mgt-repo.xml at parsing with JAXB
 */
@XmlRootElement(name = "DataSourceConfiguration")
public class DataSourceConfig {

	private JNDILookupDefinition jndiLookupDefintion;

	@XmlElement(name = "JndiLookupDefinition", required = true)
	public JNDILookupDefinition getJndiLookupDefintion() {
		return jndiLookupDefintion;
	}

	public void setJndiLookupDefintion(JNDILookupDefinition jndiLookupDefintion) {
		this.jndiLookupDefintion = jndiLookupDefintion;
	}

}
