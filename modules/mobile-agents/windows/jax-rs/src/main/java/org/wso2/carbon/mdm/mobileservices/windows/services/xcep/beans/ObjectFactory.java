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

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;

/**
 * This object contains factory methods for each
 * Java content interface and Java element interface
 * generated in the com.microsoft.schemas.windows.pki._2009._01.enrollmentpolicy package.
 * <p>An ObjectFactory allows you to programatically
 * construct new instances of the Java representation
 * for XML content. The Java representation of XML
 * content can consist of schema derived interfaces
 * and classes representing the binding of schema
 * type definitions, element declarations and model
 * groups.  Factory methods for each of these are
 * provided in this class.
 */
@XmlRegistry
@SuppressWarnings("unused")
public class ObjectFactory {

	private final static QName _CommonName_QNAME =
			new QName("http://schemas.microsoft.com/windows/pki/2009/01/enrollmentpolicy",
			          "commonName");

	/**
	 * Create a new ObjectFactory that can be used to create new instances of schema derived
	 * classes for package: com.microsoft.schemas.windows.pki._2009._01.enrollmentpolicy
	 */
	public ObjectFactory() {
	}

	/**
	 * Create an instance of {@link GetPolicies }
	 */
	public GetPolicies createGetPolicies() {
		return new GetPolicies();
	}

	/**
	 * Create an instance of {@link Client }
	 */
	public Client createClient() {
		return new Client();
	}

	/**
	 * Create an instance of {@link RequestFilter }
	 */
	public RequestFilter createRequestFilter() {
		return new RequestFilter();
	}

	/**
	 * Create an instance of {@link GetPoliciesResponse }
	 */
	public GetPoliciesResponse createGetPoliciesResponse() {
		return new GetPoliciesResponse();
	}

	/**
	 * Create an instance of {@link Response }
	 */
	public Response createResponse() {
		return new Response();
	}

	/**
	 * Create an instance of {@link CACollection }
	 */
	public CACollection createCACollection() {
		return new CACollection();
	}

	/**
	 * Create an instance of {@link OIDCollection }
	 */
	public OIDCollection createOIDCollection() {
		return new OIDCollection();
	}

	/**
	 * Create an instance of {@link SupersededPolicies }
	 */
	public SupersededPolicies createSupersededPolicies() {
		return new SupersededPolicies();
	}

	/**
	 * Create an instance of {@link OID }
	 */
	public OID createOID() {
		return new OID();
	}

	/**
	 * Create an instance of {@link ExtensionCollection }
	 */
	public ExtensionCollection createExtensionCollection() {
		return new ExtensionCollection();
	}

	/**
	 * Create an instance of {@link Attributes }
	 */
	public Attributes createAttributes() {
		return new Attributes();
	}

	/**
	 * Create an instance of {@link EnrollmentPermission }
	 */
	public EnrollmentPermission createEnrollmentPermission() {
		return new EnrollmentPermission();
	}

	/**
	 * Create an instance of {@link CAReferenceCollection }
	 */
	public CAReferenceCollection createCAReferenceCollection() {
		return new CAReferenceCollection();
	}

	/**
	 * Create an instance of {@link CertificateValidity }
	 */
	public CertificateValidity createCertificateValidity() {
		return new CertificateValidity();
	}

	/**
	 * Create an instance of {@link CAURICollection }
	 */
	public CAURICollection createCAURICollection() {
		return new CAURICollection();
	}

	/**
	 * Create an instance of {@link PolicyCollection }
	 */
	public PolicyCollection createPolicyCollection() {
		return new PolicyCollection();
	}

	/**
	 * Create an instance of {@link Revision }
	 */
	public Revision createRevision() {
		return new Revision();
	}

	/**
	 * Create an instance of {@link OIDReferenceCollection }
	 */
	public OIDReferenceCollection createOIDReferenceCollection() {
		return new OIDReferenceCollection();
	}

	/**
	 * Create an instance of {@link CA }
	 */
	public CA createCA() {
		return new CA();
	}

	/**
	 * Create an instance of {@link CertificateEnrollmentPolicy }
	 */
	public CertificateEnrollmentPolicy createCertificateEnrollmentPolicy() {
		return new CertificateEnrollmentPolicy();
	}

	/**
	 * Create an instance of {@link CryptoProviders }
	 */
	public CryptoProviders createCryptoProviders() {
		return new CryptoProviders();
	}

	/**
	 * Create an instance of {@link Extension }
	 */
	public Extension createExtension() {
		return new Extension();
	}

	/**
	 * Create an instance of {@link FilterOIDCollection }
	 */
	public FilterOIDCollection createFilterOIDCollection() {
		return new FilterOIDCollection();
	}

	/**
	 * Create an instance of {@link CAURI }
	 */
	public CAURI createCAURI() {
		return new CAURI();
	}

	/**
	 * Create an instance of {@link RARequirements }
	 */
	public RARequirements createRARequirements() {
		return new RARequirements();
	}

	/**
	 * Create an instance of {@link PrivateKeyAttributes }
	 */
	public PrivateKeyAttributes createPrivateKeyAttributes() {
		return new PrivateKeyAttributes();
	}

	/**
	 * Create an instance of {@link KeyArchivalAttributes }
	 */
	public KeyArchivalAttributes createKeyArchivalAttributes() {
		return new KeyArchivalAttributes();
	}

	/**
	 * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
	 */
	@XmlElementDecl(namespace =
			"http://schemas.microsoft.com/windows/pki/2009/01/enrollmentpolicy" +
			"", name = "commonName")
	public JAXBElement<String> createCommonName(String value) {
		return new JAXBElement<String>(_CommonName_QNAME, String.class, null, value);
	}

}
