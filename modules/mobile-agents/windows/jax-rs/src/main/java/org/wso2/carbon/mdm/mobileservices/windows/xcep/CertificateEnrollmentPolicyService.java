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

package org.wso2.carbon.mdm.mobileservices.windows.xcep;

import org.wso2.carbon.mdm.mobileservices.windows.common.Constants;
import org.wso2.carbon.mdm.mobileservices.windows.xcep.beans.*;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.BindingType;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;
import javax.xml.ws.soap.SOAPBinding;

/**
 * Interface for MS-XCEP implementation.
 */
@WebService(targetNamespace = Constants.CERTIFICATE_ENROLLMENT_POLICY_SERVICE_TARGET_NAMESPACE, name = "IPolicy")
@BindingType(value = SOAPBinding.SOAP12HTTP_BINDING)
@XmlSeeAlso({ ObjectFactory.class })
public interface CertificateEnrollmentPolicyService {

	@RequestWrapper(localName = "GetPolicies", targetNamespace = Constants.ENROLLMENT_POLICY_TARGET_NAMESPACE, className = Constants.REQUEST_WRAPPER_CLASS_NAME)
	@WebMethod(operationName = "GetPolicies")
	@ResponseWrapper(localName = "GetPoliciesResponse", targetNamespace = Constants.ENROLLMENT_POLICY_TARGET_NAMESPACE, className = Constants.RESPONSE_WRAPPER_CLASS_NAME)
	public void getPolicies(
			@WebParam(name = "client", targetNamespace = Constants.ENROLLMENT_POLICY_TARGET_NAMESPACE)
			Client client,
			@WebParam(name = "requestFilter", targetNamespace = Constants.ENROLLMENT_POLICY_TARGET_NAMESPACE)
			RequestFilter requestFilter,
			@WebParam(mode = WebParam.Mode.OUT, name = "response", targetNamespace = Constants.ENROLLMENT_POLICY_TARGET_NAMESPACE)
			javax.xml.ws.Holder<Response> response,
			@WebParam(mode = WebParam.Mode.OUT, name = "cAs", targetNamespace = Constants.ENROLLMENT_POLICY_TARGET_NAMESPACE)
			javax.xml.ws.Holder<CACollection> CACollection,
			@WebParam(mode = WebParam.Mode.OUT, name = "oIDs", targetNamespace = Constants.ENROLLMENT_POLICY_TARGET_NAMESPACE)
			javax.xml.ws.Holder<OIDCollection> OIDCollection
	);

}
