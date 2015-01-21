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

package cdm.api.windows.wstep.impl;

import cdm.api.windows.wstep.beans.AdditionalContext;
import cdm.api.windows.wstep.CertificateEnrollmentService;
import cdm.api.windows.wstep.beans.BinarySecurityToken;
import javax.jws.WebService;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.ws.BindingType;
import javax.xml.ws.Holder;
import javax.xml.ws.soap.Addressing;
import javax.xml.ws.soap.SOAPBinding;
import org.apache.commons.codec.digest.DigestUtils;
import cdm.api.windows.wstep.beans.RequestSecurityTokenResponse;
import cdm.api.windows.wstep.beans.RequestedSecurityToken;
import cdm.api.windows.wstep.util.CertificateSigningService;
import cdm.api.windows.wstep.util.KeyStoreGenerator;
import org.apache.log4j.Logger;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.bouncycastle.pkcs.jcajce.JcaPKCS10CertificationRequest;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import sun.misc.BASE64Encoder;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.StringWriter;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

@WebService(endpointInterface = "cdm.api.windows.wstep.CertificateEnrollmentService", targetNamespace = "http://schemas.microsoft.com/windows/pki/2009/01/enrollment/RSTRC")
@Addressing(enabled = true, required = true)
@BindingType(value = SOAPBinding.SOAP12HTTP_BINDING)
public class CertificateEnrollmentServiceImpl implements CertificateEnrollmentService {

	private Logger LOGGER = Logger.getLogger(CertificateEnrollmentServiceImpl.class);

	PrivateKey privateKey;
	X509Certificate rooCACertificate;
	JcaPKCS10CertificationRequest csrReq;
	PKCS10CertificationRequest certificationRequest;

	String wapProvisioningXmlFile;
	DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();

	@Override public void RequestSecurityToken(String TokenType, String RequestType,
	                                           String BinarySecurityToken,
	                                           AdditionalContext AdditionalContext,
	                                           Holder<RequestSecurityTokenResponse> response) {

		certificateSign();
		//////////
		System.out.println("\n\n\n"+"REQUEST_CSR:"+BinarySecurityToken+"\n\n\n");
		//////////

		File file = new File(getClass().getClassLoader().getResource("wap-provisioning.xml").getFile());
		wapProvisioningXmlFile = file.getPath();

		String encodedWap="Initial_test";

		RequestSecurityTokenResponse rs = new RequestSecurityTokenResponse();
		rs.setTokenType(
				"http://schemas.microsoft.com/5.0.0.0/ConfigurationManager/Enrollment/DeviceEnrollmentToken");

		try {
			byte[] derByteArray = javax.xml.bind.DatatypeConverter.parseBase64Binary(BinarySecurityToken);
			certificationRequest = new PKCS10CertificationRequest(derByteArray);
			csrReq = new JcaPKCS10CertificationRequest(certificationRequest);

			X509Certificate signedCert = CertificateSigningService.signCSR(csrReq, privateKey, rooCACertificate);

			System.out.println("PUBLIC KEY OF SIGNED CERT :"+signedCert.getPublicKey()+"\n\n\n");
			System.out.println("PUBLIC KEY OF CSR :"+csrReq.getPublicKey()+"\n\n\n");



			BASE64Encoder base64Encoder = new BASE64Encoder();
			String rootCertEncodedString = base64Encoder.encode(rooCACertificate.getEncoded());
			String signedCertEncoded = base64Encoder.encode(signedCert.getEncoded());

			DocumentBuilder builder = domFactory.newDocumentBuilder();
			Document dDoc = builder.parse(wapProvisioningXmlFile);

			NodeList wapParm = dDoc.getElementsByTagName("parm");
			/////////
			wapParm.item(0).getParentNode().getAttributes().getNamedItem("type").setTextContent(String.valueOf(
					DigestUtils.sha1Hex(rooCACertificate.getEncoded())));
			/////////
			NamedNodeMap rootCertAttributes = wapParm.item(0).getAttributes();
			Node b64Encoded = rootCertAttributes.getNamedItem("value");
			rootCertEncodedString=rootCertEncodedString.replaceAll("\n","");
			b64Encoded.setTextContent(rootCertEncodedString);
			System.out.println("COPY_ROOT_CERT:"+rootCertEncodedString);

			/////////
			wapParm.item(1).getParentNode().getAttributes().getNamedItem("type").setTextContent(String.valueOf(DigestUtils.sha1Hex(signedCert.getEncoded())));
			/////////



			NamedNodeMap clientCertAttributes = wapParm.item(1).getAttributes();
			Node b64CliendEncoded = clientCertAttributes.getNamedItem("value");
			signedCertEncoded=signedCertEncoded.replaceAll("\n","");
			b64CliendEncoded.setTextContent(signedCertEncoded);
			System.out.println("COPY_SIGNED_CERT:"+signedCertEncoded);


			String wapProvisioning = convertDocumentToString(dDoc);

			///////
			System.out.println("WAP_XML:"+wapProvisioning+"\n\n\n");
			///////

			encodedWap = base64Encoder.encode(wapProvisioning.getBytes());

		} catch (Exception e) {
			//throw
		}

		RequestedSecurityToken rst = new RequestedSecurityToken();
		BinarySecurityToken BinarySecToken=new BinarySecurityToken();
		BinarySecToken.setValueType("http://schemas.microsoft.com/5.0.0.0/ConfigurationManager/Enrollment/DeviceEnrollmentProvisionDoc");
		BinarySecToken.setEncodingType(
				"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd#base64binary");
		BinarySecToken.setToken(encodedWap);
		rst.setBinarySecurityToken(BinarySecToken);

		rs.setRequestedSecurityToken(rst);
		rs.setRequestID(0);
		response.value = rs;

	}

	private String convertDocumentToString(Document document) throws Exception {
		DOMSource domSource = new DOMSource(document);
		StringWriter writer = new StringWriter();
		StreamResult result = new StreamResult(writer);
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer transformer = tf.newTransformer();
		transformer.transform(domSource, result);
		String wapProvisioning = writer.toString();

		return wapProvisioning;

	}

	public void certificateSign() {

		try {
			KeyStore securityJks = KeyStoreGenerator.getKeyStore();
			String pass = "wso2carbon";
			KeyStoreGenerator.loadToStore(securityJks, pass.toCharArray(), "/Users/asok/Downloads/wso2as-5.2.1/repository/resources/security/wso2carbon.jks");
			PrivateKey privateKeyCA = (PrivateKey) securityJks.getKey("wso2carbon", pass.toCharArray());

			privateKey=privateKeyCA;

			Certificate cartificateCA = securityJks.getCertificate(pass);
			CertificateFactory cf = CertificateFactory.getInstance("X.509");
			ByteArrayInputStream bais = new ByteArrayInputStream(cartificateCA.getEncoded());
			X509Certificate cartificateCAX509 = (X509Certificate) cf.generateCertificate(bais);

			rooCACertificate=cartificateCAX509;

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
