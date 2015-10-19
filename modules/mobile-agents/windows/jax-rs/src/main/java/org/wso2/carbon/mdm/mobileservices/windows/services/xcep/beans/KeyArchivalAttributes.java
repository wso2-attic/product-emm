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
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>Java class for KeyArchivalAttributes complex type.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;complexType name="KeyArchivalAttributes">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="symmetricAlgorithmOIDReference" type="{http://www.w3
 *         .org/2001/XMLSchema}int"/>
 *         &lt;element name="symmetricAlgorithmKeyLength" type="{http://www.w3
 *         .org/2001/XMLSchema}unsignedInt"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "KeyArchivalAttributes", propOrder = {
		"symmetricAlgorithmOIDReference",
		"symmetricAlgorithmKeyLength"
})
@SuppressWarnings("unused")
public class KeyArchivalAttributes {

	protected int symmetricAlgorithmOIDReference;
	@XmlSchemaType(name = "unsignedInt")
	protected long symmetricAlgorithmKeyLength;

	/**
	 * Gets the value of the symmetricAlgorithmOIDReference property.
	 */
	public int getSymmetricAlgorithmOIDReference() {
		return symmetricAlgorithmOIDReference;
	}

	/**
	 * Sets the value of the symmetricAlgorithmOIDReference property.
	 */
	public void setSymmetricAlgorithmOIDReference(int value) {
		this.symmetricAlgorithmOIDReference = value;
	}

	/**
	 * Gets the value of the symmetricAlgorithmKeyLength property.
	 */
	public long getSymmetricAlgorithmKeyLength() {
		return symmetricAlgorithmKeyLength;
	}

	/**
	 * Sets the value of the symmetricAlgorithmKeyLength property.
	 */
	public void setSymmetricAlgorithmKeyLength(long value) {
		this.symmetricAlgorithmKeyLength = value;
	}

}
