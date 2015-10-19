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
 * <p>Java class for Attributes complex type.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;complexType name="Attributes">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://schemas.microsoft
 *         .com/windows/pki/2009/01/enrollmentpolicy}commonName"/>
 *         &lt;element name="policySchema" type="{http://www.w3.org/2001/XMLSchema}unsignedInt"/>
 *         &lt;element name="certificateValidity" type="{http://schemas.microsoft
 *         .com/windows/pki/2009/01/enrollmentpolicy}CertificateValidity"/>
 *         &lt;element name="permission" type="{http://schemas.microsoft
 *         .com/windows/pki/2009/01/enrollmentpolicy}EnrollmentPermission"/>
 *         &lt;element name="privateKeyAttributes" type="{http://schemas.microsoft
 *         .com/windows/pki/2009/01/enrollmentpolicy}PrivateKeyAttributes"/>
 *         &lt;element name="revision" type="{http://schemas.microsoft
 *         .com/windows/pki/2009/01/enrollmentpolicy}Revision"/>
 *         &lt;element name="supersededPolicies" type="{http://schemas.microsoft
 *         .com/windows/pki/2009/01/enrollmentpolicy}SupersededPolicies"/>
 *         &lt;element name="privateKeyFlags" type="{http://www.w3.org/2001/XMLSchema}unsignedInt"/>
 *         &lt;element name="subjectNameFlags" type="{http://www.w3
 *         .org/2001/XMLSchema}unsignedInt"/>
 *         &lt;element name="enrollmentFlags" type="{http://www.w3.org/2001/XMLSchema}unsignedInt"/>
 *         &lt;element name="generalFlags" type="{http://www.w3.org/2001/XMLSchema}unsignedInt"/>
 *         &lt;element name="hashAlgorithmOIDReference" type="{http://www.w3
 *         .org/2001/XMLSchema}int"/>
 *         &lt;element name="rARequirements" type="{http://schemas.microsoft
 *         .com/windows/pki/2009/01/enrollmentpolicy}RARequirements"/>
 *         &lt;element name="keyArchivalAttributes" type="{http://schemas.microsoft
 *         .com/windows/pki/2009/01/enrollmentpolicy}KeyArchivalAttributes"/>
 *         &lt;element name="extensions" type="{http://schemas.microsoft
 *         .com/windows/pki/2009/01/enrollmentpolicy}ExtensionCollection"/>
 *         &lt;any processContents='lax' maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Attributes", propOrder = {
		"commonName",
		"policySchema",
		"certificateValidity",
		"permission",
		"privateKeyAttributes",
		"revision",
		"supersededPolicies",
		"privateKeyFlags",
		"subjectNameFlags",
		"enrollmentFlags",
		"generalFlags",
		"hashAlgorithmOIDReference",
		"raRequirements",
		"keyArchivalAttributes",
		"extensions",
		"any"
})
@SuppressWarnings("unused")
public class Attributes {

	@XmlElement(required = true)
	protected String commonName;
	@XmlSchemaType(name = "unsignedInt")
	protected long policySchema;
	@XmlElement(required = true)
	protected CertificateValidity certificateValidity;
	@XmlElement(required = true)
	protected EnrollmentPermission permission;
	@XmlElement(required = true)
	protected PrivateKeyAttributes privateKeyAttributes;
	@XmlElement(required = true)
	protected Revision revision;
	@XmlElement(required = true, nillable = true)
	protected SupersededPolicies supersededPolicies;
	@XmlElement(required = true, type = Long.class, nillable = true)
	@XmlSchemaType(name = "unsignedInt")
	protected Long privateKeyFlags;
	@XmlElement(required = true, type = Long.class, nillable = true)
	@XmlSchemaType(name = "unsignedInt")
	protected Long subjectNameFlags;
	@XmlElement(required = true, type = Long.class, nillable = true)
	@XmlSchemaType(name = "unsignedInt")
	protected Long enrollmentFlags;
	@XmlElement(required = true, type = Long.class, nillable = true)
	@XmlSchemaType(name = "unsignedInt")
	protected Long generalFlags;
	@XmlElement(required = true, type = Integer.class, nillable = true)
	protected Integer hashAlgorithmOIDReference;
	@XmlElement(name = "rARequirements", required = true, nillable = true)
	protected RARequirements raRequirements;
	@XmlElement(required = true, nillable = true)
	protected KeyArchivalAttributes keyArchivalAttributes;
	@XmlElement(required = true, nillable = true)
	protected ExtensionCollection extensions;
	@XmlAnyElement(lax = true)
	protected List<Object> any;

	/**
	 * Gets the value of the commonName property.
	 *
	 * @return possible object is
	 * {@link String }
	 */
	public String getCommonName() {
		return commonName;
	}

	/**
	 * Sets the value of the commonName property.
	 *
	 * @param value allowed object is
	 *              {@link String }
	 */
	public void setCommonName(String value) {
		this.commonName = value;
	}

	/**
	 * Gets the value of the policySchema property.
	 */
	public long getPolicySchema() {
		return policySchema;
	}

	/**
	 * Sets the value of the policySchema property.
	 */
	public void setPolicySchema(long value) {
		this.policySchema = value;
	}

	/**
	 * Gets the value of the certificateValidity property.
	 *
	 * @return possible object is
	 * {@link CertificateValidity }
	 */
	public CertificateValidity getCertificateValidity() {
		return certificateValidity;
	}

	/**
	 * Sets the value of the certificateValidity property.
	 *
	 * @param value allowed object is
	 *              {@link CertificateValidity }
	 */
	public void setCertificateValidity(CertificateValidity value) {
		this.certificateValidity = value;
	}

	/**
	 * Gets the value of the permission property.
	 *
	 * @return possible object is
	 * {@link EnrollmentPermission }
	 */
	public EnrollmentPermission getPermission() {
		return permission;
	}

	/**
	 * Sets the value of the permission property.
	 *
	 * @param value allowed object is
	 *              {@link EnrollmentPermission }
	 */
	public void setPermission(EnrollmentPermission value) {
		this.permission = value;
	}

	/**
	 * Gets the value of the privateKeyAttributes property.
	 *
	 * @return possible object is
	 * {@link PrivateKeyAttributes }
	 */
	public PrivateKeyAttributes getPrivateKeyAttributes() {
		return privateKeyAttributes;
	}

	/**
	 * Sets the value of the privateKeyAttributes property.
	 *
	 * @param value allowed object is
	 *              {@link PrivateKeyAttributes }
	 */
	public void setPrivateKeyAttributes(PrivateKeyAttributes value) {
		this.privateKeyAttributes = value;
	}

	/**
	 * Gets the value of the revision property.
	 *
	 * @return possible object is
	 * {@link Revision }
	 */
	public Revision getRevision() {
		return revision;
	}

	/**
	 * Sets the value of the revision property.
	 *
	 * @param value allowed object is
	 *              {@link Revision }
	 */
	public void setRevision(Revision value) {
		this.revision = value;
	}

	/**
	 * Gets the value of the supersededPolicies property.
	 *
	 * @return possible object is
	 * {@link SupersededPolicies }
	 */
	public SupersededPolicies getSupersededPolicies() {
		return supersededPolicies;
	}

	/**
	 * Sets the value of the supersededPolicies property.
	 *
	 * @param value allowed object is
	 *              {@link SupersededPolicies }
	 */
	public void setSupersededPolicies(SupersededPolicies value) {
		this.supersededPolicies = value;
	}

	/**
	 * Gets the value of the privateKeyFlags property.
	 *
	 * @return possible object is
	 * {@link Long }
	 */
	public Long getPrivateKeyFlags() {
		return privateKeyFlags;
	}

	/**
	 * Sets the value of the privateKeyFlags property.
	 *
	 * @param value allowed object is
	 *              {@link Long }
	 */
	public void setPrivateKeyFlags(Long value) {
		this.privateKeyFlags = value;
	}

	/**
	 * Gets the value of the subjectNameFlags property.
	 *
	 * @return possible object is
	 * {@link Long }
	 */
	public Long getSubjectNameFlags() {
		return subjectNameFlags;
	}

	/**
	 * Sets the value of the subjectNameFlags property.
	 *
	 * @param value allowed object is
	 *              {@link Long }
	 */
	public void setSubjectNameFlags(Long value) {
		this.subjectNameFlags = value;
	}

	/**
	 * Gets the value of the enrollmentFlags property.
	 *
	 * @return possible object is
	 * {@link Long }
	 */
	public Long getEnrollmentFlags() {
		return enrollmentFlags;
	}

	/**
	 * Sets the value of the enrollmentFlags property.
	 *
	 * @param value allowed object is
	 *              {@link Long }
	 */
	public void setEnrollmentFlags(Long value) {
		this.enrollmentFlags = value;
	}

	/**
	 * Gets the value of the generalFlags property.
	 *
	 * @return possible object is
	 * {@link Long }
	 */
	public Long getGeneralFlags() {
		return generalFlags;
	}

	/**
	 * Sets the value of the generalFlags property.
	 *
	 * @param value allowed object is
	 *              {@link Long }
	 */
	public void setGeneralFlags(Long value) {
		this.generalFlags = value;
	}

	/**
	 * Gets the value of the hashAlgorithmOIDReference property.
	 *
	 * @return possible object is
	 * {@link Integer }
	 */
	public Integer getHashAlgorithmOIDReference() {
		return hashAlgorithmOIDReference;
	}

	/**
	 * Sets the value of the hashAlgorithmOIDReference property.
	 *
	 * @param value allowed object is
	 *              {@link Integer }
	 */
	public void setHashAlgorithmOIDReference(Integer value) {
		this.hashAlgorithmOIDReference = value;
	}

	/**
	 * Gets the value of the raRequirements property.
	 *
	 * @return possible object is
	 * {@link RARequirements }
	 */
	public RARequirements getRARequirements() {
		return raRequirements;
	}

	/**
	 * Sets the value of the raRequirements property.
	 *
	 * @param value allowed object is
	 *              {@link RARequirements }
	 */
	public void setRARequirements(RARequirements value) {
		this.raRequirements = value;
	}

	/**
	 * Gets the value of the keyArchivalAttributes property.
	 *
	 * @return possible object is
	 * {@link KeyArchivalAttributes }
	 */
	public KeyArchivalAttributes getKeyArchivalAttributes() {
		return keyArchivalAttributes;
	}

	/**
	 * Sets the value of the keyArchivalAttributes property.
	 *
	 * @param value allowed object is
	 *              {@link KeyArchivalAttributes }
	 */
	public void setKeyArchivalAttributes(KeyArchivalAttributes value) {
		this.keyArchivalAttributes = value;
	}

	/**
	 * Gets the value of the extensions property.
	 *
	 * @return possible object is
	 * {@link ExtensionCollection }
	 */
	public ExtensionCollection getExtensions() {
		return extensions;
	}

	/**
	 * Sets the value of the extensions property.
	 *
	 * @param value allowed object is
	 *              {@link ExtensionCollection }
	 */
	public void setExtensions(ExtensionCollection value) {
		this.extensions = value;
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
