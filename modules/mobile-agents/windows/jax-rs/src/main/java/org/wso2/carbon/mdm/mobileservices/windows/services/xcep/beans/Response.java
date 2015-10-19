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

import org.w3c.dom.Element;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>Java class for Response complex type.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;complexType name="Response">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="policyID" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="policyFriendlyName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="nextUpdateHours" type="{http://www.w3.org/2001/XMLSchema}unsignedInt"/>
 *         &lt;element name="policiesNotChanged" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="policies" type="{http://schemas.microsoft
 *         .com/windows/pki/2009/01/enrollmentpolicy}PolicyCollection"/>
 *         &lt;any processContents='lax' maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Response", propOrder = {
		"policyID",
		"policyFriendlyName",
		"nextUpdateHours",
		"policiesNotChanged",
		"policies",
		"any"
})
@SuppressWarnings("unused")
public class Response {

	@XmlElement(required = true)
	protected String policyID;
	@XmlElement(required = true, nillable = true)
	protected String policyFriendlyName;
	@XmlElement(required = true, type = Long.class, nillable = true)
	@XmlSchemaType(name = "unsignedInt")
	protected Long nextUpdateHours;
	@XmlElement(required = true, type = Boolean.class, nillable = true)
	protected Boolean policiesNotChanged;
	@XmlElement(required = true, nillable = true)
	protected PolicyCollection policies;
	@XmlAnyElement(lax = true)
	protected List<Object> any;

	/**
	 * Gets the value of the policyID property.
	 *
	 * @return possible object is
	 * {@link String }
	 */
	public String getPolicyID() {
		return policyID;
	}

	/**
	 * Sets the value of the policyID property.
	 *
	 * @param value allowed object is
	 *              {@link String }
	 */
	public void setPolicyID(String value) {
		this.policyID = value;
	}

	/**
	 * Gets the value of the policyFriendlyName property.
	 *
	 * @return possible object is
	 * {@link String }
	 */
	public String getPolicyFriendlyName() {
		return policyFriendlyName;
	}

	/**
	 * Sets the value of the policyFriendlyName property.
	 *
	 * @param value allowed object is
	 *              {@link String }
	 */
	public void setPolicyFriendlyName(String value) {
		this.policyFriendlyName = value;
	}

	/**
	 * Gets the value of the nextUpdateHours property.
	 *
	 * @return possible object is
	 * {@link Long }
	 */
	public Long getNextUpdateHours() {
		return nextUpdateHours;
	}

	/**
	 * Sets the value of the nextUpdateHours property.
	 *
	 * @param value allowed object is
	 *              {@link Long }
	 */
	public void setNextUpdateHours(Long value) {
		this.nextUpdateHours = value;
	}

	/**
	 * Gets the value of the policiesNotChanged property.
	 *
	 * @return possible object is
	 * {@link Boolean }
	 */
	public Boolean isPoliciesNotChanged() {
		return policiesNotChanged;
	}

	/**
	 * Sets the value of the policiesNotChanged property.
	 *
	 * @param value allowed object is
	 *              {@link Boolean }
	 */
	public void setPoliciesNotChanged(Boolean value) {
		this.policiesNotChanged = value;
	}

	/**
	 * Gets the value of the policies property.
	 *
	 * @return possible object is
	 * {@link PolicyCollection }
	 */
	public PolicyCollection getPolicies() {
		return policies;
	}

	/**
	 * Sets the value of the policies property.
	 *
	 * @param value allowed object is
	 *              {@link PolicyCollection }
	 */
	public void setPolicies(PolicyCollection value) {
		this.policies = value;
	}

	/**
	 * Gets the value of the any property.
	 * <p/>
	 * <p/>
	 * This accessor method returns a reference to the live list,
	 * not a snapshot. Therefore any modification you make to the
	 * returned list will be present inside the JAXB object.
	 * This is why there is not a <CODE>set</CODE> method for the any property.
	 * <p/>
	 * <p/>
	 * For example, to add a new item, do as follows:
	 * <pre>
	 *    getAny().add(newItem);
	 * </pre>
	 * <p/>
	 * <p/>
	 * <p/>
	 * Objects of the following type(s) are allowed in the list
	 * {@link Object }
	 * {@link Element }
	 */
	public List<Object> getAny() {
		if (any == null) {
			any = new ArrayList<Object>();
		}
		return this.any;
	}

}
