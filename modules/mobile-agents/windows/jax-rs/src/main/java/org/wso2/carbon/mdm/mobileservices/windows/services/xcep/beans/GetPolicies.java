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

package org.wso2.carbon.mdm.mobileservices.windows.services.xcep.beans;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>Java class for anonymous complex type.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="client" type="{http://schemas.microsoft
 *         .com/windows/pki/2009/01/enrollmentpolicy}Client"/>
 *         &lt;element name="requestFilter" type="{http://schemas.microsoft
 *         .com/windows/pki/2009/01/enrollmentpolicy}RequestFilter"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
		"client",
		"requestFilter"
})
@XmlRootElement(name = "GetPolicies")
@SuppressWarnings("unused")
public class GetPolicies {

	@XmlElement(required = true)
	protected Client client;
	@XmlElement(required = true, nillable = true)
	protected RequestFilter requestFilter;

	/**
	 * Gets the value of the client property.
	 *
	 * @return possible object is
	 * {@link Client }
	 */
	public Client getClient() {
		return client;
	}

	/**
	 * Sets the value of the client property.
	 *
	 * @param value allowed object is
	 *              {@link Client }
	 */
	public void setClient(Client value) {
		this.client = value;
	}

	/**
	 * Gets the value of the requestFilter property.
	 *
	 * @return possible object is
	 * {@link RequestFilter }
	 */
	public RequestFilter getRequestFilter() {
		return requestFilter;
	}

	/**
	 * Sets the value of the requestFilter property.
	 *
	 * @param value allowed object is
	 *              {@link RequestFilter }
	 */
	public void setRequestFilter(RequestFilter value) {
		this.requestFilter = value;
	}

}
