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

package org.wso2.carbon.mdm.mobileservices.windows.xcep.impl;

import org.wso2.carbon.mdm.mobileservices.windows.common.Constants;
import org.wso2.carbon.mdm.mobileservices.windows.xcep.CertificateEnrollmentPolicyService;
import org.apache.log4j.Logger;
import org.wso2.carbon.mdm.mobileservices.windows.xcep.beans.*;

import javax.jws.WebService;
import javax.xml.ws.BindingType;
import javax.xml.ws.Holder;
import javax.xml.ws.soap.Addressing;
import javax.xml.ws.soap.SOAPBinding;

/**
 * Implementation class for CertificateEnrollmentPolicyService.
 */
@WebService(endpointInterface = Constants.CERTIFICATE_ENROLLMENT_POLICY_SERVICE_ENDPOINT, targetNamespace = Constants.CERTIFICATE_ENROLLMENT_POLICY_SERVICE_TARGET_NAMESPACE)
@Addressing(enabled = true, required = true)
@BindingType(value = SOAPBinding.SOAP12HTTP_BINDING)
public class CertificateEnrollmentPolicyServiceImpl implements CertificateEnrollmentPolicyService {


	private static Logger logger = Logger.getLogger(CertificateEnrollmentPolicyServiceImpl.class);

	/**
	 *
	 * @param client
	 * @param requestFilter
	 * @param response - response which includes minimal key length, hash algorithm, policy schema, policy OID reference
	 * @param cAs
	 * @param oIDs
	 */
	@Override
	public void getPolicies(Client client, RequestFilter requestFilter,
	                        Holder<Response> response, Holder<CACollection> cAs,
	                        Holder<OIDCollection> oIDs) {

		if (logger.isDebugEnabled()) {
			logger.debug("Enrolment certificate policy end point was triggered by device");
		}

		Response rs = new Response();
		OIDCollection oidc = new OIDCollection();
		CACollection cac = new CACollection();

		PolicyCollection pc = new PolicyCollection();

		CertificateEnrollmentPolicy cnp = new CertificateEnrollmentPolicy();
		Attributes at = new Attributes();
		PrivateKeyAttributes pkat = new PrivateKeyAttributes();

		pkat.setMinimalKeyLength(Constants.MINIMAL_KEY_LENGTH);

		at.setPolicySchema(Constants.POLICY_SCHEMA);
		at.setPrivateKeyAttributes(pkat);
		at.setHashAlgorithmOIDReference(Constants.HASH_ALGORITHM_OID_REFERENCE);

		cnp.setPolicyOIDReference(Constants.OID_REFERENCE);
		cnp.setAttributes(at);

		pc.getPolicy().add(cnp);

		rs.setPolicies(pc);

		response.value = rs;

		OID oid = new OID();
		oid.setValue(Constants.OID);
		oid.setGroup(1);
		oid.setOIDReferenceID(0);
		oid.setDefaultName(Constants.OID_DEFAULT_NAME);

		oidc.getOID().add(oid);
		cAs.value = cac;
		oIDs.value = oidc;

	}

}
