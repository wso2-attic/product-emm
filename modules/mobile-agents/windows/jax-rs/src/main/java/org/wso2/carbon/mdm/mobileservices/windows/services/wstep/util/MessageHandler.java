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

import org.wso2.carbon.mdm.mobileservices.windows.common.PluginConstants;
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
import java.util.*;

/**
 * Class responsible for adding Timestamp security header in SOAP message and adding Content-length
 * in the HTTP header for avoiding HTTP chunking.
 */
public class MessageHandler implements SOAPHandler<SOAPMessageContext> {

	public static final String TIME_ZONE = "Z";
	public static final int VALIDITY_TIME = 5;
	public static final int TIMESTAMP_END_INDEX = 6;
	public static final int TIMESTAMP_BEGIN_INDEX = 0;
	private static Log log = LogFactory.getLog(MessageHandler.class);

	/**
	 * This method resolves the security header coming in the SOAP message.
	 * @return - Security Header
	 */
	@Override
	public Set<QName> getHeaders() {
		QName securityHeader = new QName(PluginConstants.WS_SECURITY_TARGET_NAMESPACE, PluginConstants.SECURITY);
		HashSet<QName> headers = new HashSet<QName>();
		headers.add(securityHeader);
		return headers;
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
				Response.serverError().entity("SOAP message content cannot be read.").build();
			}
			try {
				if ((header == null) && (envelope != null)) {
					header = envelope.addHeader();
				}
			} catch (SOAPException e) {
				Response.serverError().entity("SOAP header cannot be added.").build();
			}

			SOAPFactory soapFactory = null;
			try {
				soapFactory = SOAPFactory.newInstance();
			} catch (SOAPException e) {
				Response.serverError().entity("Cannot get an instance of SOAP factory.").build();
			}

			QName qNamesSecurity = new QName(PluginConstants.WS_SECURITY_TARGET_NAMESPACE,
			                                 PluginConstants.CertificateEnrolment.SECURITY);
			SOAPHeaderElement Security = null;
			Name attributeName = null;
			try {
				if (header != null) {
					Security = header.addHeaderElement(qNamesSecurity);
				}
				if (soapFactory != null) {
					attributeName =
							soapFactory.createName(PluginConstants.CertificateEnrolment.TIMESTAMP_ID,
							                       PluginConstants.CertificateEnrolment.TIMESTAMP_U,
							                       PluginConstants.CertificateEnrolment
									                       .WSS_SECURITY_UTILITY);
				}
			} catch (SOAPException e) {
				Response.serverError().entity("Security header cannot be added.").build();
			}

			QName qNameTimestamp = new QName(PluginConstants.CertificateEnrolment.WSS_SECURITY_UTILITY,
			                                 PluginConstants.CertificateEnrolment.TIMESTAMP);
			SOAPHeaderElement timestamp = null;
			try {
				if (header != null) {
					timestamp = header.addHeaderElement(qNameTimestamp);
					timestamp.addAttribute(attributeName,
					                       PluginConstants.CertificateEnrolment.TIMESTAMP_0);
				}
			} catch (SOAPException e) {
				Response.serverError().entity("Exception while adding timestamp header.").build();
			}
			DateTime dateTime = new DateTime();
			DateTime expiredDateTime = dateTime.plusMinutes(VALIDITY_TIME);
			String createdISOTime = dateTime.toString(ISODateTimeFormat.dateTime());
			String expiredISOTime = expiredDateTime.toString(ISODateTimeFormat.dateTime());
			createdISOTime = createdISOTime.substring(TIMESTAMP_BEGIN_INDEX,
			                                          createdISOTime.length() -
			                                          TIMESTAMP_END_INDEX);
			createdISOTime = createdISOTime + TIME_ZONE;
			expiredISOTime = expiredISOTime.substring(TIMESTAMP_BEGIN_INDEX,
			                                          expiredISOTime.length() -
			                                          TIMESTAMP_END_INDEX);
			expiredISOTime = expiredISOTime + TIME_ZONE;
			QName qNameCreated = new QName(PluginConstants.CertificateEnrolment.WSS_SECURITY_UTILITY,
			                               PluginConstants.CertificateEnrolment.CREATED);
			SOAPHeaderElement SOAPHeaderCreated = null;

			try {
				if (header != null) {
					SOAPHeaderCreated = header.addHeaderElement(qNameCreated);
					SOAPHeaderCreated.addTextNode(createdISOTime);
				}
			} catch (SOAPException e) {
				Response.serverError().entity("Exception while creating SOAP header.").build();
			}
			QName qNameExpires = new QName(PluginConstants.CertificateEnrolment.WSS_SECURITY_UTILITY,
			                               PluginConstants.CertificateEnrolment.EXPIRES);
			SOAPHeaderElement SOAPHeaderExpires = null;
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			String messageString = null;
			try {
				if (header != null) {
					SOAPHeaderExpires = header.addHeaderElement(qNameExpires);
					SOAPHeaderExpires.addTextNode(expiredISOTime);
				}
				if ((timestamp != null) && (Security != null)) {
					timestamp.addChildElement(SOAPHeaderCreated);
					timestamp.addChildElement(SOAPHeaderExpires);
					Security.addChildElement(timestamp);
				}
				message.saveChanges();
				message.writeTo(outputStream);
				messageString = new String(outputStream.toByteArray(),
				                           PluginConstants.CertificateEnrolment.UTF_8);
			} catch (SOAPException e) {
				Response.serverError().entity("Exception while creating timestamp SOAP header.")
				        .build();
			} catch (IOException e) {
				Response.serverError().entity("Exception while writing message to output stream.")
				        .build();
			}

			Map<String, List<String>> headers =
					(Map<String, List<String>>) context.get(MessageContext.HTTP_REQUEST_HEADERS);
			headers = new HashMap<String, List<String>>();
			if (messageString != null) {
				headers.put(PluginConstants.CONTENT_LENGTH, Arrays.asList(String.valueOf(
						messageString.length())));
			}
			context.put(MessageContext.HTTP_REQUEST_HEADERS, headers);
		}
		return true;
	}

	@Override
	public boolean handleFault(SOAPMessageContext context) {
		return true;
	}

	@Override
	public void close(MessageContext context) {
	}
}
