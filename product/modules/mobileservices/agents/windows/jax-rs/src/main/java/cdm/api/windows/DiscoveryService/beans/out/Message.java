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

package cdm.api.windows.DiscoveryService.beans.out;

import javax.xml.bind.annotation.*;
import java.io.Serializable;


@XmlRootElement
public class Message implements Serializable {

	private String authPolicy;
	private String enrollmentPolicyServiceUrl;
	private String enrollmentServiceUrl;

	@XmlElement(name = "AuthPolicy", namespace = "http://schemas.microsoft.com/windows/management/2012/01/enrollment")
	public String getAuthPolicy() {
		return authPolicy;
	}

	@XmlElement(name = "EnrollmentPolicyServiceUrl", namespace = "http://schemas.microsoft.com/windows/management/2012/01/enrollment")
	public String getEnrollmentPolicyServiceUrl() {
		return enrollmentPolicyServiceUrl;
	}

	@XmlElement(name = "EnrollmentServiceUrl", namespace = "http://schemas.microsoft.com/windows/management/2012/01/enrollment")
	public String getEnrollmentServiceUrl() {
		return enrollmentServiceUrl;
	}

	public void setAuthPolicy(String authPolicy) {
		this.authPolicy = authPolicy;
	}

	public void setEnrollmentPolicyServiceUrl(String enrollmentPolicyServiceUrl) {
		this.enrollmentPolicyServiceUrl = enrollmentPolicyServiceUrl;
	}

	public void setEnrollmentServiceUrl(String enrollmentServiceUrl) {
		this.enrollmentServiceUrl = enrollmentServiceUrl;
	}
}