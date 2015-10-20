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

package org.wso2.carbon.mdm.mobileservices.windows.services.xcep.impl;

import org.wso2.carbon.mdm.mobileservices.windows.common.PluginConstants;
import org.wso2.carbon.mdm.mobileservices.windows.services.xcep.CertificateEnrollmentPolicyService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.mdm.mobileservices.windows.services.xcep.beans.Attributes;
import org.wso2.carbon.mdm.mobileservices.windows.services.xcep.beans.CACollection;
import org.wso2.carbon.mdm.mobileservices.windows.services.xcep.beans.CertificateEnrollmentPolicy;
import org.wso2.carbon.mdm.mobileservices.windows.services.xcep.beans.Client;
import org.wso2.carbon.mdm.mobileservices.windows.services.xcep.beans.OID;
import org.wso2.carbon.mdm.mobileservices.windows.services.xcep.beans.OIDCollection;
import org.wso2.carbon.mdm.mobileservices.windows.services.xcep.beans.PolicyCollection;
import org.wso2.carbon.mdm.mobileservices.windows.services.xcep.beans.PrivateKeyAttributes;
import org.wso2.carbon.mdm.mobileservices.windows.services.xcep.beans.RequestFilter;
import org.wso2.carbon.mdm.mobileservices.windows.services.xcep.beans.Response;

import javax.jws.WebService;
import javax.xml.ws.BindingType;
import javax.xml.ws.Holder;
import javax.xml.ws.soap.Addressing;
import javax.xml.ws.soap.SOAPBinding;

/**
 * Implementation class for CertificateEnrollmentPolicyService.
 */
@WebService(endpointInterface = PluginConstants.CERTIFICATE_ENROLLMENT_POLICY_SERVICE_ENDPOINT,
		    targetNamespace = PluginConstants.CERTIFICATE_ENROLLMENT_POLICY_SERVICE_TARGET_NAMESPACE)
@Addressing(enabled = true, required = true)
@BindingType(value = SOAPBinding.SOAP12HTTP_BINDING)
public class CertificateEnrollmentPolicyServiceImpl implements CertificateEnrollmentPolicyService {

	private static Log log = LogFactory.getLog(CertificateEnrollmentPolicyServiceImpl.class);

	/**
	 * This method implements the MS-XCEP protocol for certificate enrollment policy service.
	 * @param client        - Included lastUpdate and preferredLanguage tags
	 * @param requestFilter - Policy constrain tag
	 * @param response      - Response which includes minimal key length, hash algorithm, policy
	 *                        schema, policy OID reference
	 * @param caCollection  - Contains the issuers for the certificate enrollment policies
	 * @param oidCollection - Contains the collection of OIDs for the response
	 */
	@Override
	public void getPolicies(Client client, RequestFilter requestFilter,
	                        Holder<Response> response, Holder<CACollection> caCollection,
	                        Holder<OIDCollection> oidCollection) {

		if (log.isDebugEnabled()) {
			log.debug("Enrolment certificate policy end point was triggered by device.");
		}

		Response responseElement = new Response();
		OIDCollection oidCollectionElement = new OIDCollection();
		CACollection caCollectionElement = new CACollection();

		PolicyCollection policyCollectionElement = new PolicyCollection();

		CertificateEnrollmentPolicy certEnrollmentPolicyElement = new CertificateEnrollmentPolicy();
		Attributes attributeElement = new Attributes();
		PrivateKeyAttributes privateKeyAttributeElement = new PrivateKeyAttributes();

		privateKeyAttributeElement.
				setMinimalKeyLength(PluginConstants.CertificateEnrolmentPolicy.MINIMAL_KEY_LENGTH);

		attributeElement.setPolicySchema(PluginConstants.CertificateEnrolmentPolicy.POLICY_SCHEMA);
		attributeElement.setPrivateKeyAttributes(privateKeyAttributeElement);
		attributeElement.setHashAlgorithmOIDReference(PluginConstants.CertificateEnrolmentPolicy.
				                                              HASH_ALGORITHM_OID_REFERENCE);
		certEnrollmentPolicyElement.setPolicyOIDReference(PluginConstants.CertificateEnrolmentPolicy.
				                                              OID_REFERENCE);
		certEnrollmentPolicyElement.setAttributes(attributeElement);
		policyCollectionElement.getPolicy().add(certEnrollmentPolicyElement);
		responseElement.setPolicies(policyCollectionElement);
		response.value = responseElement;

		OID oidElement = new OID();
		oidElement.setValue(PluginConstants.CertificateEnrolmentPolicy.OID);
		oidElement.setGroup(PluginConstants.CertificateEnrolmentPolicy.OID_GROUP);
		oidElement.setOIDReferenceID(PluginConstants.CertificateEnrolmentPolicy.OID_REFERENCE_ID);
		oidElement.setDefaultName(PluginConstants.CertificateEnrolmentPolicy.OID_DEFAULT_NAME);

		oidCollectionElement.getOID().add(oidElement);
		caCollection.value = caCollectionElement;
		oidCollection.value = oidCollectionElement;
	}
}
