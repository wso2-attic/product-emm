/*
 * Copyright (c) 2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied. See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */

package cdm.api.windows.DiscoveryService.beans.in;


import javax.xml.bind.annotation.*;
import java.io.Serializable;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class DiscoveryRequest implements Serializable {

	@XmlElement(name = "EmailAddress")
	private String emailId;

	@XmlElement(name = "RequestVersion")
	private String version;

	@XmlElement(name = "DeviceType")
	private String deviceType;

	public String getEmailId() {
		return emailId;
	}

	public String getVersion() {
		return version;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}
}