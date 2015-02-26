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

package org.wso2.carbon.mdm.mobileservices.windows.services.wstep.util;

import org.wso2.carbon.mdm.mobileservices.windows.common.Constants;
import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;

import javax.ws.rs.core.Response;
import javax.xml.namespace.QName;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.xml.soap.Name;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFactory;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPHeaderElement;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Class responsible for adding Timestamp security header in SOAP message and adding Content-length
 * in the HTTP header for avoiding HTTP chunking.
 */
public class MessageHandler implements SOAPHandler<SOAPMessageContext> {

	private static Log logger = LogFactory.getLog(MessageHandler.class);

	@Override
	public Set<QName> getHeaders() {
		return null;
	}

	/**
	 * This method adds Timestamp for SOAP header, and adds Content-length for HTTP header for
	 * avoiding HTTP chunking.
	 *
	 * @param context - Context of the SOAP Message
	 */
	@Override
	public boolean handleMessage(SOAPMessageContext context) {

		Boolean outBoundProperty = (Boolean) context.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);

		if (outBoundProperty) {
			SOAPMessage message = context.getMessage();
			SOAPHeader header = null;
			SOAPEnvelope envelope = null;
			try {
				header = message.getSOAPHeader();
				envelope = message.getSOAPPart().getEnvelope();
			} catch (SOAPException e) {
				Response.serverError().build();
			}
			if (header == null) {
				try {
					header = envelope.addHeader();
				} catch (SOAPException e) {
					Response.serverError().build();
				}
			}

			SOAPFactory soapFactory = null;
			try {
				soapFactory = SOAPFactory.newInstance();
			} catch (SOAPException e) {
				Response.serverError().build();
			}
			QName qNamesSecurity = new QName(
					Constants.WS_SECURITY_TARGET_NAMESPACE,
					Constants.CertificateEnrolment.SECURITY);

			SOAPHeaderElement Security = null;
			try {
				Security = header.addHeaderElement(qNamesSecurity);
			} catch (SOAPException e) {
				Response.serverError().build();
			}

			Name attributeName = null;
			try {
				attributeName = soapFactory.createName(Constants.CertificateEnrolment.TIMESTAMP_ID,
				                                       Constants.CertificateEnrolment.TIMESTAMP_U,
				                                       Constants.CertificateEnrolment.
						                                         WSS_SECURITY_UTILITY);
			} catch (SOAPException e) {
				Response.serverError().build();
			}

			QName qNameTimestamp = new QName(
					Constants.CertificateEnrolment.WSS_SECURITY_UTILITY,
					Constants.CertificateEnrolment.TIMESTAMP);
			SOAPHeaderElement timestamp = null;

			try {
				timestamp = header.addHeaderElement(qNameTimestamp);
				timestamp.addAttribute(attributeName, Constants.CertificateEnrolment.TIMESTAMP_0);
			} catch (SOAPException e) {
				Response.serverError().build();
			}

			DateTime dateTime = new DateTime();
			DateTime expiredDateTime = dateTime.plusMinutes(5);
			String createdISOTime = dateTime.toString(ISODateTimeFormat.dateTime());
			String expiredISOTime = expiredDateTime.toString(ISODateTimeFormat.dateTime());
			createdISOTime = createdISOTime.substring(0, createdISOTime.length() - 6);
			createdISOTime = createdISOTime + "Z";
			expiredISOTime = expiredISOTime.substring(0, expiredISOTime.length() - 6);
			expiredISOTime = expiredISOTime + "Z";

			QName qNameCreated = new QName(Constants.CertificateEnrolment.WSS_SECURITY_UTILITY,
			                               Constants.CertificateEnrolment.CREATED);
			SOAPHeaderElement SOAPHeaderCreated = null;

			try {
				SOAPHeaderCreated = header.addHeaderElement(qNameCreated);
				SOAPHeaderCreated.addTextNode(createdISOTime);
			} catch (SOAPException e) {
				Response.serverError().build();
			}

			QName qNameExpires = new QName(
					Constants.CertificateEnrolment.WSS_SECURITY_UTILITY,
					Constants.CertificateEnrolment.EXPIRES);
			SOAPHeaderElement SOAPHeaderExpires = null;

			try {
				SOAPHeaderExpires = header.addHeaderElement(qNameExpires);
				SOAPHeaderExpires.addTextNode(expiredISOTime);
			} catch (SOAPException e) {
				Response.serverError().build();
			}

			try {
				timestamp.addChildElement(SOAPHeaderCreated);
				timestamp.addChildElement(SOAPHeaderExpires);
				Security.addChildElement(timestamp);
			} catch (SOAPException e) {
				Response.serverError().build();
			}

			try {
				message.saveChanges();
			} catch (SOAPException e) {
				Response.serverError().build();
			}

			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

			try {
				message.writeTo(outputStream);
			} catch (IOException e) {
				Response.serverError().build();
			} catch (SOAPException e) {
				Response.serverError().build();
			}

			String messageString = null;
			try {
				messageString = new String(outputStream.toByteArray(),
				                           Constants.CertificateEnrolment.UTF_8);
			} catch (UnsupportedEncodingException e) {
				Response.serverError().build();
			}

			Map<String, List<String>> headers =
					(Map<String, List<String>>) context.get(MessageContext.HTTP_REQUEST_HEADERS);
			headers = new HashMap<String, List<String>>();
			headers.put(Constants.CertificateEnrolment.CONTENT_LENGTH,
			            Arrays.asList(String.valueOf(messageString.length())));
			context.put(MessageContext.HTTP_REQUEST_HEADERS, headers);
		}
		return true;
	}

	@Override public boolean handleFault(SOAPMessageContext context) {
		return true;
	}

	@Override public void close(MessageContext context) {
	}
}
