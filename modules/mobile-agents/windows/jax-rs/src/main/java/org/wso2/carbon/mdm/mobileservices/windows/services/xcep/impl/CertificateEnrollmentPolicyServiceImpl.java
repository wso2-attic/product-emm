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

package org.wso2.carbon.mdm.mobileservices.windows.services.xcep.impl;

import org.wso2.carbon.mdm.mobileservices.windows.common.Constants;
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
@WebService(endpointInterface = Constants.CERTIFICATE_ENROLLMENT_POLICY_SERVICE_ENDPOINT,
		targetNamespace = Constants.CERTIFICATE_ENROLLMENT_POLICY_SERVICE_TARGET_NAMESPACE)
@Addressing(enabled = true, required = true)
@BindingType(value = SOAPBinding.SOAP12HTTP_BINDING)
public class CertificateEnrollmentPolicyServiceImpl implements CertificateEnrollmentPolicyService {

	private static Log log = LogFactory.getLog(CertificateEnrollmentPolicyServiceImpl.class);

	/**
	 * This method implements the MS-XCEP protocol for certificate enrollment policy service.
	 *
	 * @param client        - Included lastUpdate and preferredLanguage tags
	 * @param requestFilter - Policy constrain tag
	 * @param response      - Response which includes minimal key length, hash algorithm, policy
	 *                        schema, policy OID reference
	 * @param CACollection  - Contains the issuers for the certificate enrollment policies
	 * @param OIDCollection - Contains the collection of OIDs for the response
	 */
	@Override
	public void getPolicies(Client client, RequestFilter requestFilter,
	                        Holder<Response> response, Holder<CACollection> CACollection,
	                        Holder<OIDCollection> OIDCollection) {

		if (log.isDebugEnabled()) {
			log.debug("Enrolment certificate policy end point was triggered by device.");
		}

		Response responseElement = new Response();
		OIDCollection OIDCollectionElement = new OIDCollection();
		CACollection CACollectionElement = new CACollection();

		PolicyCollection policyCollectionElement = new PolicyCollection();

		CertificateEnrollmentPolicy certEnrollmentPolicyElement = new CertificateEnrollmentPolicy();
		Attributes attributeElement = new Attributes();
		PrivateKeyAttributes privateKeyAttributeElement = new PrivateKeyAttributes();

		privateKeyAttributeElement.
				setMinimalKeyLength(Constants.CertificateEnrolmentPolicy.MINIMAL_KEY_LENGTH);

		attributeElement.setPolicySchema(Constants.CertificateEnrolmentPolicy.POLICY_SCHEMA);
		attributeElement.setPrivateKeyAttributes(privateKeyAttributeElement);
		attributeElement.setHashAlgorithmOIDReference(Constants.CertificateEnrolmentPolicy.
				                                              HASH_ALGORITHM_OID_REFERENCE);
		certEnrollmentPolicyElement.setPolicyOIDReference(Constants.CertificateEnrolmentPolicy.
				                                              OID_REFERENCE);
		certEnrollmentPolicyElement.setAttributes(attributeElement);
		policyCollectionElement.getPolicy().add(certEnrollmentPolicyElement);
		responseElement.setPolicies(policyCollectionElement);
		response.value = responseElement;

		OID OIDElement = new OID();
		OIDElement.setValue(Constants.CertificateEnrolmentPolicy.OID);
		OIDElement.setGroup(Constants.CertificateEnrolmentPolicy.OID_GROUP);
		OIDElement.setOIDReferenceID(Constants.CertificateEnrolmentPolicy.OID_REFERENCE_ID);
		OIDElement.setDefaultName(Constants.CertificateEnrolmentPolicy.OID_DEFAULT_NAME);

		OIDCollectionElement.getOID().add(OIDElement);
		CACollection.value = CACollectionElement;
		OIDCollection.value = OIDCollectionElement;
	}
}
