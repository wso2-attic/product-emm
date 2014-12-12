/*
 * Copyright (c) 2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package cdm.api.windows.impl;

import cdm.api.windows.EnrolmentService;
import org.apache.log4j.Logger;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.bouncycastle.pkcs.jcajce.JcaPKCS10CertificationRequest;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;
import javax.swing.text.Document;
import javax.ws.rs.core.Response;
import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Iterator;

import cdm.api.windows.util.CertificateSigningService;

public class EnrolmentServiceImpl implements EnrolmentService {

	private Logger LOGGER = Logger.getLogger(EnrolmentServiceImpl.class);

	static {
		Security.addProvider(new BouncyCastleProvider());
	}

	private String enrollmentResponseFile;

	private String wapProvisioningXmlFile;

	private String privatePemKeyFilePath;

	private String caCertificateFilePath;

	PrivateKey privateKey;

	X509Certificate rooCACertificate;

	public void init() {

		try {
			FileInputStream in = new FileInputStream(privatePemKeyFilePath);
			byte[] keyBytes = new byte[in.available()];
			in.read(keyBytes);
			in.close();

			String key = new String(keyBytes, "UTF-8");
			key = key.replaceAll(
					"(-+BEGIN RSA PRIVATE KEY-+\\r?\\n|-+END RSA PRIVATE KEY-+\\r?\\n?)", "");

			// don't use this for real projects!
			BASE64Decoder decoder = new BASE64Decoder();
			keyBytes = decoder.decodeBuffer(key);

			// generate private key

			PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");

			privateKey = keyFactory.generatePrivate(spec);

			LOGGER.info("Private Key Algorithm : " + privateKey.getAlgorithm());
		} catch (Exception e) {
			LOGGER.error("An unexpected Error has occurred while reading CA Private Key, ", e);
		}

		try {
			FileInputStream fr = new FileInputStream(caCertificateFilePath);
			CertificateFactory cf = CertificateFactory.getInstance("X509");
			rooCACertificate = (X509Certificate) cf.generateCertificate(fr);

			rooCACertificate.verify(rooCACertificate.getPublicKey());

			LOGGER.info("CA Certificate Expiration Date : " + rooCACertificate.getNotAfter());

		} catch (Exception e) {
			LOGGER.error("An unexpected Error has occurred while reading CA Root Certificate, ", e);
		}

        /*try {
            KeyPairGenerator gen = KeyPairGenerator.getInstance("RSA");
            gen.initialize(2048);
            key = gen.generateKeyPair();
            PrivateKey privateKey = key.getPrivate();
            PublicKey publicKey = key.getPublic();


            *//**
		 * Following details need to be provided
		 *
		 * Serial number
		 * Signature algorithm
		 * Issuer Name.
		 * Subject Name -- or a Subject Alternative Name (SAN).
		 * Date range (not before, not after).
		 * Subject Public Key.
		 *//*

            X509V3CertificateGenerator v3CertGen = new X509V3CertificateGenerator();
            v3CertGen.setSerialNumber(BigInteger.valueOf(new SecureRandom().nextInt(Integer.MAX_VALUE)));
            v3CertGen.setIssuerDN(new X509Principal("CN=wso2.com"));
            //v3CertGen.setIssuerDN(new X509Principal("CN=wso2.com, OU=Mobile, O=wso2 L=Colombo, C=LK"));
            v3CertGen.setNotBefore(new Date(System.currentTimeMillis() - 1000L * 60 * 60 * 24 * 30));
            v3CertGen.setNotAfter(new Date(System.currentTimeMillis() + (1000L * 60 * 60 * 24 * 365*10)));
            v3CertGen.setSubjectDN(new X509Principal("CN=wso2.com"));
            v3CertGen.setPublicKey(publicKey);
            v3CertGen.setSignatureAlgorithm("SHA1withRSA");

            rooCACertificate = v3CertGen.generateX509Certificate(privateKey);

        } catch (Exception e) {
            e.printStackTrace();
        }*/
	}

	public Response getPolicies(Document request) {
		LOGGER.info("Received Get Policies Request");

		String response = null;
		File file = null;
		FileInputStream fis = null;
		byte[] data = null;

		try {

			file = new File("./conf/policy-service.xml");
			fis = new FileInputStream(file);
			data = new byte[(int) file.length()];
			fis.read(data);
			fis.close();
			response = new String(data, "UTF-8");

		} catch (IOException e) {
			LOGGER.error("An Unexpected Error has occurred while processing the request ", e);
		}

		LOGGER.info("Sending Get Policy Response");
		return Response.ok().entity(response).build();
	}

	public Response enrollUser(Document request) {
		LOGGER.info("Received User Enrollment Request");

		XPath xPath = XPathFactory.newInstance().newXPath();
		xPath.setNamespaceContext(new MyNamespaceContext());
		String response = null;

		DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();

		try {
			NodeList nl = (NodeList) xPath.evaluate(
					"/s:Envelope/s:Body/wst:RequestSecurityToken/wsse:BinarySecurityToken", request,
					XPathConstants.NODESET);
			Node node = nl.item(0);
			String certificateDataString = node.getTextContent();
			byte[] derByteArray =
					javax.xml.bind.DatatypeConverter.parseBase64Binary(certificateDataString);

			PKCS10CertificationRequest certificationRequest =
					new PKCS10CertificationRequest(derByteArray);
			JcaPKCS10CertificationRequest csrReq =
					new JcaPKCS10CertificationRequest(certificationRequest);

			LOGGER.info("Public Key of CSR : " + csrReq.getPublicKey());

			X509Certificate signedCert =
					CertificateSigningService.signCSR(csrReq, privateKey, rooCACertificate);

			LOGGER.info("Verifying Signed Certificate with CSR's public key : " +
			            signedCert.getPublicKey());

			BASE64Encoder base64Encoder = new BASE64Encoder();
			String rootCertEncodedString = base64Encoder.encode(rooCACertificate.getEncoded());
			String signedCertEncoded = base64Encoder.encode(signedCert.getEncoded());

			DocumentBuilder builder = domFactory.newDocumentBuilder();
			org.w3c.dom.Document dDoc = builder.parse(wapProvisioningXmlFile);

			NodeList wapParm = dDoc.getElementsByTagName("parm");

			NamedNodeMap rootCertAttributes = wapParm.item(0).getAttributes();
			Node b64Encoded = rootCertAttributes.getNamedItem("value");
			b64Encoded.setTextContent(rootCertEncodedString);

			NamedNodeMap clientCertAttributes = wapParm.item(1).getAttributes();
			Node b64CliendEncoded = clientCertAttributes.getNamedItem("value");
			b64CliendEncoded.setTextContent(signedCertEncoded);

			String wapProvisioning = convertDocumentToString(dDoc);
			String encodedWap = base64Encoder.encode(wapProvisioning.getBytes());

			org.w3c.dom.Document responseXml = builder.parse(enrollmentResponseFile);
			NodeList token = responseXml.getElementsByTagName("BinarySecurityToken");

			Node firstToken = token.item(0);
			firstToken.setTextContent(encodedWap);

			response = convertDocumentToString(responseXml);
		} catch (Exception e) {
			LOGGER.error("An Unexpected Error has occurred while processing the request ", e);
		}

		LOGGER.info("Sending User Enrollment Response");
		return Response.ok().entity(response).build();
	}

	private String convertDocumentToString(org.w3c.dom.Document document) throws Exception {
		DOMSource domSource = new DOMSource(document);
		StringWriter writer = new StringWriter();
		StreamResult result = new StreamResult(writer);
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer transformer = tf.newTransformer();
		transformer.transform(domSource, result);
		String wapProvisioning = writer.toString();

		return wapProvisioning;

	}

	public void setEnrollmentResponseFile(String enrollmentResponseFile) {
		this.enrollmentResponseFile = enrollmentResponseFile;
	}

	public void setWapProvisioningXmlFile(String wapProvisioningXmlFile) {
		this.wapProvisioningXmlFile = wapProvisioningXmlFile;
	}

	public void setPrivatePemKeyFilePath(String privatePemKeyFilePath) {
		this.privatePemKeyFilePath = privatePemKeyFilePath;
	}

	public void setCaCertificateFilePath(String caCertificateFilePath) {
		this.caCertificateFilePath = caCertificateFilePath;
	}

	public void setPrivateKey(PrivateKey privateKey) {
		this.privateKey = privateKey;
	}

	public void setRooCACertificate(X509Certificate rooCACertificate) {
		this.rooCACertificate = rooCACertificate;
	}

	private static class MyNamespaceContext implements NamespaceContext {

		public String getNamespaceURI(String prefix) {

			if ("s".equals(prefix)) {
				return "http://www.w3.org/2003/05/soap-envelope";
			} else if ("wst".equals(prefix)) {
				return "http://docs.oasis-open.org/ws-sx/ws-trust/200512";
			} else if ("wsse".equals(prefix)) {
				return "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd";
			}
			return null;

		}

		public String getPrefix(String namespaceURI) {
			return null;
		}

		public Iterator getPrefixes(String namespaceURI) {
			return null;
		}
	}
}
