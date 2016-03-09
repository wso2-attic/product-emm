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
import javax.xml.bind.annotation.XmlType;

/**
 * <p>Java class for Extension complex type.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;complexType name="Extension">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="oIDReference" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="critical" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="value" type="{http://www.w3.org/2001/XMLSchema}base64Binary"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Extension", propOrder = {
		"oidReference",
		"critical",
		"value"
})
@SuppressWarnings("unused")
public class Extension {

	@XmlElement(name = "oIDReference")
	protected int oidReference;
	protected boolean critical;
	@XmlElement(required = true, nillable = true)
	protected byte[] value;

	/**
	 * Gets the value of the oidReference property.
	 */
	public int getOIDReference() {
		return oidReference;
	}

	/**
	 * Sets the value of the oidReference property.
	 */
	public void setOIDReference(int value) {
		this.oidReference = value;
	}

	/**
	 * Gets the value of the critical property.
	 */
	public boolean isCritical() {
		return critical;
	}

	/**
	 * Sets the value of the critical property.
	 */
	public void setCritical(boolean value) {
		this.critical = value;
	}

	/**
	 * Gets the value of the value property.
	 *
	 * @return possible object is
	 * byte[]
	 */
	public byte[] getValue() {
		return value;
	}

	/**
	 * Sets the value of the value property.
	 *
	 * @param value allowed object is
	 *              byte[]
	 */
	public void setValue(byte[] value) {
		this.value = value;
	}

}
