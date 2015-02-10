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

package cdm.api.windows.xcep.impl;

import cdm.api.windows.xcep.IPolicy;
import cdm.api.windows.xcep.beans.*;
import javax.jws.WebService;
import javax.xml.ws.BindingType;
import javax.xml.ws.Holder;
import javax.xml.ws.soap.Addressing;
import javax.xml.ws.soap.SOAPBinding;

@WebService(endpointInterface = "cdm.api.windows.xcep.IPolicy", targetNamespace = "http://schemas.microsoft.com/windows/pki/2009/01/enrollmentpolicy")
@Addressing(enabled = true, required = true)
@BindingType(value = SOAPBinding.SOAP12HTTP_BINDING)
public class xcepimpl implements IPolicy {

	@Override
	public void getPolicies(Client client, RequestFilter requestFilter,
	                                  Holder<Response> response, Holder<CACollection> cAs,
	                                  Holder<OIDCollection> oIDs) {

		System.out.println("CHECKED_SERVICE");


		Response rs = new Response();
		OIDCollection oidc = new OIDCollection();
		CACollection cac = new CACollection();

		PolicyCollection pc = new PolicyCollection();

		CertificateEnrollmentPolicy cnp = new CertificateEnrollmentPolicy();
		Attributes at = new Attributes();
		PrivateKeyAttributes pkat = new PrivateKeyAttributes();

		pkat.setMinimalKeyLength(2048);

		at.setPolicySchema(3);
		at.setPrivateKeyAttributes(pkat);
		at.setHashAlgorithmOIDReference(0);

		cnp.setPolicyOIDReference(0);
		cnp.setAttributes(at);

		pc.getPolicy().add(cnp);

		rs.setPolicies(pc);

		response.value = rs;

		OID oid = new OID();
		oid.setValue("1.3.14.3.2.29");
		oid.setGroup(1);
		oid.setOIDReferenceID(0);
		oid.setDefaultName("szOID_OIWSEC_sha1RSASign");

		oidc.getOID().add(oid);
		cAs.value = cac;
		oIDs.value = oidc;

	}

}
