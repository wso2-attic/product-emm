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

package org.wso2.carbon.mdm.mobileservices.windows.wstep.util;

import org.wso2.carbon.mdm.mobileservices.windows.common.Constants;
import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;
import javax.ws.rs.core.Response;
import javax.xml.namespace.QName;
import javax.xml.soap.*;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;

/**
 * Class responsible for adding Timestamp security header in SOAP message and adding Content-length
 * in the HTTP header for avoiding HTTP chunking.
 */
public class MessageHandler implements SOAPHandler<SOAPMessageContext> {



	@Override
	public Set<QName> getHeaders() {
		return null;
	}

	/**
	 *
	 * @param context
	 * This method adds Timestamp for SOAP header, and adds Content-length for HTTP header for
	 * avoiding HTTP chunking.
	 */
	@Override
	public boolean handleMessage(SOAPMessageContext context){

		Boolean outboundProperty = (Boolean)
				context.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);

		if (outboundProperty.booleanValue()) {

			SOAPMessage message = context.getMessage();
			SOAPHeader header = null;
			SOAPEnvelope envelope=null;

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
			SOAPFactory sf = null;

			try {
				sf = SOAPFactory.newInstance();
			} catch (SOAPException e) {
				Response.serverError().build();
			}

			QName qNamesSecurity = new QName(
					Constants.WS_SECURITY_TARGET_NAMESPACE,
					Constants.SECURITY);

			SOAPHeaderElement Security = null;

			try {
				Security = header.addHeaderElement(qNamesSecurity);
			} catch (SOAPException e) {
				Response.serverError().build();
			}

			Name attributeName = null;
			try {
				attributeName = sf.createName(Constants.TIMESTAMP_ID, Constants.TIMESTAMP_U,
				                              Constants.WSS_SECURITY_UTILITY);
			} catch (SOAPException e) {
				Response.serverError().build();
			}


			QName qNameTimestamp = new QName(
						Constants.WSS_SECURITY_UTILITY,
						Constants.TIMESTAMP);
			SOAPHeaderElement Timestamp = null;

			try {
				Timestamp = header.addHeaderElement(qNameTimestamp);
				Timestamp.addAttribute(attributeName, Constants.TIMESTAMP_0);
			} catch (SOAPException e) {
				Response.serverError().build();
			}

			DateTime dateTime = new DateTime();
				DateTime dateTimeEx = dateTime.plusMinutes(5);
				String CreatedTime = dateTime.toString(ISODateTimeFormat.dateTime());
				String ExpiredTime = dateTimeEx.toString(ISODateTimeFormat.dateTime());
				CreatedTime = CreatedTime.substring(0, CreatedTime.length() - 6);
				CreatedTime = CreatedTime + "Z";
				ExpiredTime = ExpiredTime.substring(0, ExpiredTime.length() - 6);
				ExpiredTime = ExpiredTime + "Z";

				QName qNameCreated = new QName(
						Constants.WSS_SECURITY_UTILITY,
						Constants.CREATED);
			SOAPHeaderElement Created = null;

			try {
				Created = header.addHeaderElement(qNameCreated);
				Created.addTextNode(CreatedTime);
			} catch (SOAPException e) {
				Response.serverError().build();
			}


			QName qNameExpires = new QName(
						Constants.WSS_SECURITY_UTILITY,
						Constants.EXPIRES);
			SOAPHeaderElement Expires = null;



			try {
				Expires = header.addHeaderElement(qNameExpires);
				Expires.addTextNode(ExpiredTime);
			} catch (SOAPException e) {
				Response.serverError().build();
			}


			try {
				Timestamp.addChildElement(Created);
				Timestamp.addChildElement(Expires);
				Security.addChildElement(Timestamp);
			} catch (SOAPException e) {
				Response.serverError().build();
			}

             try {
	           message.saveChanges();
            }

            catch(SOAPException e){

            }
				ByteArrayOutputStream stream = new ByteArrayOutputStream();

			try {
				message.writeTo(stream);
			} catch (IOException e){
				Response.serverError().build();
			}catch (SOAPException e){
				Response.serverError().build();
			}


			String messagestring=null;
			try {
				messagestring = new String(stream.toByteArray(), Constants.UTF_8);
			}
			catch(UnsupportedEncodingException e){
				Response.serverError().build();
			}


				Map<String, List<String>> headers = (Map<String, List<String>>) context
						.get(MessageContext.HTTP_REQUEST_HEADERS);
				headers = new HashMap<String, List<String>>();
				headers.put(Constants.CONTENT_LENGTH,
				            Arrays.asList(String.valueOf(messagestring.length())));
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
