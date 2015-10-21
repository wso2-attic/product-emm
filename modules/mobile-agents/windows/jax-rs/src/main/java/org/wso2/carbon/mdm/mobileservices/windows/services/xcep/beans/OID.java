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
 * <p>Java class for OID complex type.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;complexType name="OID">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="value" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="group" type="{http://www.w3.org/2001/XMLSchema}unsignedInt"/>
 *         &lt;element name="oIDReferenceID" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="defaultName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;any processContents='lax' maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "OID", propOrder = {
		"value",
		"group",
		"oidReferenceID",
		"defaultName",
		"any"
})
@SuppressWarnings("unused")
public class OID {

	@XmlElement(required = true)
	protected String value;
	@XmlSchemaType(name = "unsignedInt")
	protected long group;
	@XmlElement(name = "oIDReferenceID")
	protected int oidReferenceID;
	@XmlElement(required = true, nillable = true)
	protected String defaultName;
	@XmlAnyElement(lax = true)
	protected List<Object> any;

	/**
	 * Gets the value of the value property.
	 *
	 * @return possible object is
	 * {@link String }
	 */
	public String getValue() {
		return value;
	}

	/**
	 * Sets the value of the value property.
	 *
	 * @param value allowed object is
	 *              {@link String }
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * Gets the value of the group property.
	 */
	public long getGroup() {
		return group;
	}

	/**
	 * Sets the value of the group property.
	 */
	public void setGroup(long value) {
		this.group = value;
	}

	/**
	 * Gets the value of the oidReferenceID property.
	 */
	public int getOIDReferenceID() {
		return oidReferenceID;
	}

	/**
	 * Sets the value of the oidReferenceID property.
	 */
	public void setOIDReferenceID(int value) {
		this.oidReferenceID = value;
	}

	/**
	 * Gets the value of the defaultName property.
	 *
	 * @return possible object is
	 * {@link String }
	 */
	public String getDefaultName() {
		return defaultName;
	}

	/**
	 * Sets the value of the defaultName property.
	 *
	 * @param value allowed object is
	 *              {@link String }
	 */
	public void setDefaultName(String value) {
		this.defaultName = value;
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
