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

import javax.xml.bind.annotation.*;
import java.math.BigInteger;

/**
 * <p>Java class for CertificateValidity complex type.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;complexType name="CertificateValidity">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="validityPeriodSeconds" type="{http://www.w3
 *         .org/2001/XMLSchema}unsignedLong"/>
 *         &lt;element name="renewalPeriodSeconds" type="{http://www.w3
 *         .org/2001/XMLSchema}unsignedLong"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CertificateValidity", propOrder = {
		"validityPeriodSeconds",
		"renewalPeriodSeconds"
})
@SuppressWarnings("unused")
public class CertificateValidity {

	@XmlElement(required = true)
	@XmlSchemaType(name = "unsignedLong")
	protected BigInteger validityPeriodSeconds;
	@XmlElement(required = true)
	@XmlSchemaType(name = "unsignedLong")
	protected BigInteger renewalPeriodSeconds;

	/**
	 * Gets the value of the validityPeriodSeconds property.
	 *
	 * @return possible object is
	 * {@link BigInteger }
	 */
	public BigInteger getValidityPeriodSeconds() {
		return validityPeriodSeconds;
	}

	/**
	 * Sets the value of the validityPeriodSeconds property.
	 *
	 * @param value allowed object is
	 *              {@link BigInteger }
	 */
	public void setValidityPeriodSeconds(BigInteger value) {
		this.validityPeriodSeconds = value;
	}

	/**
	 * Gets the value of the renewalPeriodSeconds property.
	 *
	 * @return possible object is
	 * {@link BigInteger }
	 */
	public BigInteger getRenewalPeriodSeconds() {
		return renewalPeriodSeconds;
	}

	/**
	 * Sets the value of the renewalPeriodSeconds property.
	 *
	 * @param value allowed object is
	 *              {@link BigInteger }
	 */
	public void setRenewalPeriodSeconds(BigInteger value) {
		this.renewalPeriodSeconds = value;
	}

}
