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
 * <p>Java class for CA complex type.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;complexType name="CA">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="uris" type="{http://schemas.microsoft
 *         .com/windows/pki/2009/01/enrollmentpolicy}CAURICollection"/>
 *         &lt;element name="certificate" type="{http://www.w3.org/2001/XMLSchema}base64Binary"/>
 *         &lt;element name="enrollPermission" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="cAReferenceID" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;any processContents='lax' maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CA", propOrder = {
		"uris",
		"certificate",
		"enrollPermission",
		"caReferenceID",
		"any"
})
@SuppressWarnings("unused")
public class CA {

	@XmlElement(required = true)
	protected CAURICollection uris;
	@XmlElement(required = true)
	protected byte[] certificate;
	protected boolean enrollPermission;
	@XmlElement(name = "cAReferenceID")
	protected int caReferenceID;
	@XmlAnyElement(lax = true)
	protected List<Object> any;

	/**
	 * Gets the value of the uris property.
	 *
	 * @return possible object is
	 * {@link CAURICollection }
	 */
	public CAURICollection getUris() {
		return uris;
	}

	/**
	 * Sets the value of the uris property.
	 *
	 * @param value allowed object is
	 *              {@link CAURICollection }
	 */
	public void setUris(CAURICollection value) {
		this.uris = value;
	}

	/**
	 * Gets the value of the certificate property.
	 *
	 * @return possible object is
	 * byte[]
	 */
	public byte[] getCertificate() {
		return certificate;
	}

	/**
	 * Sets the value of the certificate property.
	 *
	 * @param value allowed object is
	 *              byte[]
	 */
	public void setCertificate(byte[] value) {
		this.certificate = value;
	}

	/**
	 * Gets the value of the enrollPermission property.
	 */
	public boolean isEnrollPermission() {
		return enrollPermission;
	}

	/**
	 * Sets the value of the enrollPermission property.
	 */
	public void setEnrollPermission(boolean value) {
		this.enrollPermission = value;
	}

	/**
	 * Gets the value of the caReferenceID property.
	 */
	public int getCAReferenceID() {
		return caReferenceID;
	}

	/**
	 * Sets the value of the caReferenceID property.
	 */
	public void setCAReferenceID(int value) {
		this.caReferenceID = value;
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
