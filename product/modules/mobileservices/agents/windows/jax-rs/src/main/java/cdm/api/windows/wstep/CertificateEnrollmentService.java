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
package cdm.api.windows.wstep;

import cdm.api.windows.wstep.beans.RequestSecurityTokenResponse;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.ws.rs.*;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.Action;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;
import javax.xml.ws.soap.SOAPBinding;
import javax.xml.ws.BindingType;

@WebService(targetNamespace = "http://schemas.microsoft.com/windows/pki/2009/01/enrollment/RSTRC", name = "wstep")
@BindingType(value = SOAPBinding.SOAP12HTTP_BINDING)
public interface CertificateEnrollmentService {

	@RequestWrapper(localName = "RequestSecurityToken", targetNamespace = "http://docs.oasis-open.org/ws-sx/ws-trust/200512")
	@WebMethod(operationName = "RequestSecurityToken")
	@ResponseWrapper(localName = "RequestSecurityTokenResponseCollection", targetNamespace = "http://docs.oasis-open.org/ws-sx/ws-trust/200512")
	public void RequestSecurityToken(
			@WebParam(name = "TokenType", targetNamespace = "http://docs.oasis-open.org/ws-sx/ws-trust/200512")
			String TokenType,
			@WebParam(name = "RequestType", targetNamespace = "http://docs.oasis-open.org/ws-sx/ws-trust/200512")
			String RequestType,
			@WebParam(name = "BinarySecurityToken", targetNamespace = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd")
			String BinarySecurityToken,
			@WebParam(name = "AdditionalContext", targetNamespace = "http://schemas.xmlsoap.org/ws/2006/12/authorization")
			cdm.api.windows.wstep.beans.AdditionalContext AdditionalContext,
			@WebParam(mode = WebParam.Mode.OUT, name = "RequestSecurityTokenResponse", targetNamespace = "http://docs.oasis-open.org/ws-sx/ws-trust/200512")
			javax.xml.ws.Holder<RequestSecurityTokenResponse> response
	);

}