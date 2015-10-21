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
 * <p>Java class for CAURI complex type.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;complexType name="CAURI">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="clientAuthentication" type="{http://www.w3
 *         .org/2001/XMLSchema}unsignedInt"/>
 *         &lt;element name="uri" type="{http://www.w3.org/2001/XMLSchema}anyURI"/>
 *         &lt;element name="priority" type="{http://www.w3.org/2001/XMLSchema}unsignedInt"/>
 *         &lt;element name="renewalOnly" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;any processContents='lax' maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CAURI", propOrder = {
		"clientAuthentication",
		"uri",
		"priority",
		"renewalOnly",
		"any"
})
@SuppressWarnings("unused")
public class CAURI {

	@XmlSchemaType(name = "unsignedInt")
	protected long clientAuthentication;
	@XmlElement(required = true)
	@XmlSchemaType(name = "anyURI")
	protected String uri;
	@XmlElement(required = true, type = Long.class, nillable = true)
	@XmlSchemaType(name = "unsignedInt")
	protected Long priority;
	protected boolean renewalOnly;
	@XmlAnyElement(lax = true)
	protected List<Object> any;

	/**
	 * Gets the value of the clientAuthentication property.
	 */
	public long getClientAuthentication() {
		return clientAuthentication;
	}

	/**
	 * Sets the value of the clientAuthentication property.
	 */
	public void setClientAuthentication(long value) {
		this.clientAuthentication = value;
	}

	/**
	 * Gets the value of the uri property.
	 *
	 * @return possible object is
	 * {@link String }
	 */
	public String getUri() {
		return uri;
	}

	/**
	 * Sets the value of the uri property.
	 *
	 * @param value allowed object is
	 *              {@link String }
	 */
	public void setUri(String value) {
		this.uri = value;
	}

	/**
	 * Gets the value of the priority property.
	 *
	 * @return possible object is
	 * {@link Long }
	 */
	public Long getPriority() {
		return priority;
	}

	/**
	 * Sets the value of the priority property.
	 *
	 * @param value allowed object is
	 *              {@link Long }
	 */
	public void setPriority(Long value) {
		this.priority = value;
	}

	/**
	 * Gets the value of the renewalOnly property.
	 */
	public boolean isRenewalOnly() {
		return renewalOnly;
	}

	/**
	 * Sets the value of the renewalOnly property.
	 */
	public void setRenewalOnly(boolean value) {
		this.renewalOnly = value;
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
