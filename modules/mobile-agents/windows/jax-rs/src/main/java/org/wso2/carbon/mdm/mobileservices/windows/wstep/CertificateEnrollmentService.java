/*
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
package org.wso2.carbon.mdm.mobileservices.windows.wstep;

import org.wso2.carbon.mdm.mobileservices.windows.common.Constants;
import org.wso2.carbon.mdm.mobileservices.windows.common.exceptions.CertificateGenerationException;
import org.wso2.carbon.mdm.mobileservices.windows.common.exceptions.KeyStoreGenerationException;
import org.wso2.carbon.mdm.mobileservices.windows.common.exceptions.PropertyFileException;
import org.wso2.carbon.mdm.mobileservices.windows.wstep.beans.AdditionalContext;
import org.wso2.carbon.mdm.mobileservices.windows.wstep.beans.RequestSecurityTokenResponse;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;
import javax.xml.ws.soap.SOAPBinding;
import javax.xml.ws.BindingType;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

/**
 * Interface of WSTEP implementation.
 */
@WebService(targetNamespace = Constants.DEVICE_ENROLLMENT_SERVICE_TARGET_NAMESPACE, name = "wstep")
@BindingType(value = SOAPBinding.SOAP12HTTP_BINDING)
public interface CertificateEnrollmentService {

	@RequestWrapper(localName = "RequestSecurityToken", targetNamespace = Constants.WS_TRUST_TARGET_NAMESPACE)
	@WebMethod(operationName = "RequestSecurityToken")
	@ResponseWrapper(localName = "RequestSecurityTokenResponseCollection", targetNamespace = Constants.WS_TRUST_TARGET_NAMESPACE)
	public void RequestSecurityToken(
			@WebParam(name = "TokenType", targetNamespace = Constants.WS_TRUST_TARGET_NAMESPACE)
			String TokenType,
			@WebParam(name = "RequestType", targetNamespace = Constants.WS_TRUST_TARGET_NAMESPACE)
			String RequestType,
			@WebParam(name = "BinarySecurityToken", targetNamespace = Constants.WS_SECURITY_TARGET_NAMESPACE)
			String BinarySecurityToken,
			@WebParam(name = "AdditionalContext", targetNamespace = Constants.SOAP_AUTHORIZATION_TARGET_NAMESPACE)
			AdditionalContext AdditionalContext,
			@WebParam(mode = WebParam.Mode.OUT, name = "RequestSecurityTokenResponse", targetNamespace = Constants.WS_TRUST_TARGET_NAMESPACE)
			javax.xml.ws.Holder<RequestSecurityTokenResponse> response
	) throws KeyStoreGenerationException, PropertyFileException, CertificateGenerationException;

}