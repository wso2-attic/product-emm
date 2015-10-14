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
 * <p>Java class for CertificateEnrollmentPolicy complex type.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;complexType name="CertificateEnrollmentPolicy">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="policyOIDReference" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="cAs" type="{http://schemas.microsoft
 *         .com/windows/pki/2009/01/enrollmentpolicy}CAReferenceCollection"/>
 *         &lt;element name="attributes" type="{http://schemas.microsoft
 *         .com/windows/pki/2009/01/enrollmentpolicy}Attributes"/>
 *         &lt;any processContents='lax' maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CertificateEnrollmentPolicy", propOrder = {
		"policyOIDReference",
		"cAs",
		"attributes",
		"any"
})
@SuppressWarnings("unused")
public class CertificateEnrollmentPolicy {

	protected int policyOIDReference;
	@XmlElement(required = true, nillable = true)
	protected CAReferenceCollection cAs;
	@XmlElement(required = true)
	protected Attributes attributes;
	@XmlAnyElement(lax = true)
	protected List<Object> any;

	/**
	 * Gets the value of the policyOIDReference property.
	 */
	public int getPolicyOIDReference() {
		return policyOIDReference;
	}

	/**
	 * Sets the value of the policyOIDReference property.
	 */
	public void setPolicyOIDReference(int value) {
		this.policyOIDReference = value;
	}

	/**
	 * Gets the value of the cAs property.
	 *
	 * @return possible object is
	 * {@link CAReferenceCollection }
	 */
	public CAReferenceCollection getCAs() {
		return cAs;
	}

	/**
	 * Sets the value of the cAs property.
	 *
	 * @param value allowed object is
	 *              {@link CAReferenceCollection }
	 */
	public void setCAs(CAReferenceCollection value) {
		this.cAs = value;
	}

	/**
	 * Gets the value of the attributes property.
	 *
	 * @return possible object is
	 * {@link Attributes }
	 */
	public Attributes getAttributes() {
		return attributes;
	}

	/**
	 * Sets the value of the attributes property.
	 *
	 * @param value allowed object is
	 *              {@link Attributes }
	 */
	public void setAttributes(Attributes value) {
		this.attributes = value;
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
