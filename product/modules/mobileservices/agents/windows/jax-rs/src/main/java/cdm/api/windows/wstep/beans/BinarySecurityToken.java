/*
 * Copyright (c) 2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied. See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */

package cdm.api.windows.wstep.beans;

import javax.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BinarySecurityToken", namespace = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd")
public class BinarySecurityToken {

	@XmlAttribute(name = "ValueType")
	protected String ValueType;
	@XmlAttribute(name = "EncodingType")
	protected String EncodingType;

	@XmlValue
	protected String Token;

	public void setValueType(String valuetype) {
		this.ValueType = valuetype;
	}

	public String getValueType() {
		return this.ValueType;
	}

	public void setEncodingType(String encodingtype) {
		this.EncodingType = encodingtype;
	}

	public String getEncodingType() {
		return this.EncodingType;
	}

	public void setToken(String token) {
		this.Token = token;
	}

	public String getToken() {
		return this.Token;
	}

}

