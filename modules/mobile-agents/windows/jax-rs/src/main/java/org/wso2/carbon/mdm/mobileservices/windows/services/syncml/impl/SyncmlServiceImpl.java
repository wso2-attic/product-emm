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

package org.wso2.carbon.mdm.mobileservices.windows.services.syncml.impl;

import org.apache.commons.io.FileUtils;
import org.wso2.carbon.mdm.mobileservices.windows.common.exceptions.FileOperationException;
import org.wso2.carbon.mdm.mobileservices.windows.common.exceptions.WindowsDeviceEnrolmentException;
import org.wso2.carbon.mdm.mobileservices.windows.services.syncml.SyncmlService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.wso2.carbon.device.mgt.common.Device;
import org.wso2.carbon.device.mgt.common.DeviceManagementConstants;
import org.wso2.carbon.device.mgt.common.DeviceManagementException;
import org.wso2.carbon.mdm.mobileservices.windows.services.syncml.util.SyncmlUtils;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.wso2.carbon.mdm.mobileservices.windows.common.Constants;

/**
 * Implementing class of SyncmlImpl interface.
 */
public class SyncmlServiceImpl implements SyncmlService {

	private static final String SYNCML_FIRST_MESSAGE = "1";
	private static final String SYNCML_SECOND_MESSAGE = "2";
	private static final int SYNCML_MESSAGE_POSITION = 0;
	private static final int SYNCML_ITEM_DATA_POSITION = 1;
	private static final String OS_VERSION = "osVersion";
	private static final String IMSI = "imsi";
	private static final String IMEI = "imei";
	private static final String VENDOR = "vendor";
	private static final String MODEL = "model";
	private enum DevicePropertyIndex {
		OS_VERSION(3),
		IMSI(4),
		IMEI(5),
		DEVICE_ID(6),
		DEVICE_MANUFACTURER(7),
		DEVICE_MODEL(8),
		DEVICE_LANGUAGE(9);
		private final int itemPosition;
		private DevicePropertyIndex(final int itemPosition) {
			this.itemPosition = itemPosition;
		}
		public int getValue() {
			return this.itemPosition;
		}
	}
	private static Log log = LogFactory.getLog(SyncmlServiceImpl.class);

	/**
	 * This method resolves the Syncml messages received through device and send the
	 * response accordingly.
	 * @param request - Syncml request comes through the device
	 * @return - Syncml response generated for the request
	 */
	@Override
	public Response getInitialResponse(Document request) throws WindowsDeviceEnrolmentException {

		Node headerNode = request.getElementsByTagName(Constants.SyncML.SYNC_ML).item(SYNCML_MESSAGE_POSITION).
				          getFirstChild();
		Node bodyNode = request.getElementsByTagName(Constants.SyncML.SYNC_ML).item(SYNCML_MESSAGE_POSITION).
				        getChildNodes().item(SYNCML_ITEM_DATA_POSITION);
		NodeList nodeListHeader = headerNode.getChildNodes();
		NodeList nodeListBody = bodyNode.getChildNodes();

		String targetURI = null;
		String sourceURI = null;
		String msgID = null;
		String osVersion;
		String imsi;
		String imei;
		String devID;
		String devMan;
		String devMod;
		String devLang;

		for (int i = 0; i < nodeListHeader.getLength(); i++) {
			Node node = nodeListHeader.item(i);

			if (node.getNodeType() == Node.ELEMENT_NODE) {

				String nodeName = node.getNodeName();

				if (Constants.SyncML.SYNCML_MSG_ID.equals(nodeName)) {
					msgID = node.getTextContent().trim();
					if (log.isDebugEnabled()) {
						log.debug("Request SyncML message ID: " + msgID);
					}
				}
				if (Constants.SyncML.SYNCML_MESSAGE_ONE.equals(msgID)) {
					if (Constants.SyncML.SYNCML_TARGET.equals(nodeName)) {
						targetURI = node.getFirstChild().getTextContent().trim();
					} else if (Constants.SyncML.SYNCML_SOURCE.equals(nodeName)) {
						sourceURI = node.getFirstChild().getTextContent().trim();
					}
				}
			}
		}

		for (int i = 0; i < nodeListBody.getLength(); i++) {
			Node node = nodeListBody.item(i);

			if (node.getNodeType() == Node.ELEMENT_NODE) {

				String nodeName = node.getNodeName();

				if ((Constants.SyncML.SYNCML_MESSAGE_TWO.equals(msgID))&&
				    (Constants.SyncML.SYNCML_RESULTS.equals(nodeName))) {

					NodeList childNodes = node.getChildNodes();
					osVersion = childNodes.item(DevicePropertyIndex.OS_VERSION.getValue()).
							getChildNodes().item(SYNCML_ITEM_DATA_POSITION).getTextContent();
					imsi = childNodes.item(DevicePropertyIndex.IMSI.getValue()).
							getChildNodes().item(SYNCML_ITEM_DATA_POSITION).getTextContent();
					imei = childNodes.item(DevicePropertyIndex.IMEI.getValue()).
							getChildNodes().item(SYNCML_ITEM_DATA_POSITION).getTextContent();
					devID = childNodes.item(DevicePropertyIndex.DEVICE_ID.getValue()).
							getChildNodes().item(SYNCML_ITEM_DATA_POSITION).getTextContent();
					devMan = childNodes.item(DevicePropertyIndex.DEVICE_MANUFACTURER.getValue()).
							getChildNodes().item(SYNCML_ITEM_DATA_POSITION).getTextContent();
					devMod = childNodes.item(DevicePropertyIndex.DEVICE_MODEL.getValue()).
							getChildNodes().item(SYNCML_ITEM_DATA_POSITION).getTextContent();
					devLang = childNodes.item(DevicePropertyIndex.DEVICE_LANGUAGE.getValue()).
							getChildNodes().item(SYNCML_ITEM_DATA_POSITION).getTextContent();

					if (log.isDebugEnabled()) {
						log.debug(
								"OS Version:" + osVersion + ", IMSI: " + imsi + ", IMEI: " +
								imei + ", DevID: " + devID + ", DevMan: " + devMan +
								", DevMod: " + devMod + ", DevLang: " + devLang);
					}

					Device generatedDevice =
						generateDevice(DeviceManagementConstants.MobileDeviceTypes.
						MOBILE_DEVICE_TYPE_WINDOWS, devID, osVersion, imsi, imei, devMan, devMod);
					try {
						SyncmlUtils.getDeviceManagementService()
						                     .enrollDevice(generatedDevice);
					} catch (DeviceManagementException e) {
						String msg = "Exception while getting Device Management Service.";
						log.error(msg, e);
						throw new WindowsDeviceEnrolmentException(msg, e);
					}
				}
			}
		}
		String response = prepareResponse(msgID, targetURI, sourceURI);
		return Response.ok().entity(response).build();
	}

	/**
	 * This method is used to generate and return Device object from the received information at
	 * the Syncml step.
	 * @param deviceID     - Unique device ID received from the Device
	 * @param osVersion    - Device OS version
	 * @param imsi         - Device IMSI
	 * @param imei         - Device IMEI
	 * @param manufacturer - Device Manufacturer name
	 * @param model        - Device Model
	 * @return - Generated device object
	 */
	private Device generateDevice(String type, String deviceID, String osVersion, String imsi,
	                                    String imei, String manufacturer, String model) {

		Device generatedDevice = new Device();

		Device.Property OSVersionProperty = new Device.Property();
		OSVersionProperty.setName(OS_VERSION);
		OSVersionProperty.setValue(osVersion);

		Device.Property IMSEIProperty = new Device.Property();
		IMSEIProperty.setName(SyncmlServiceImpl.IMSI);
		IMSEIProperty.setValue(imsi);

		Device.Property IMEIProperty = new Device.Property();
		IMEIProperty.setName(SyncmlServiceImpl.IMEI);
		IMEIProperty.setValue(imei);

		Device.Property DevManProperty = new Device.Property();
		DevManProperty.setName(VENDOR);
		DevManProperty.setValue(manufacturer);

		Device.Property DevModProperty = new Device.Property();
		DevModProperty.setName(MODEL);
		DevModProperty.setValue(model);

		List<Device.Property> propertyList = new ArrayList<Device.Property>();
		propertyList.add(OSVersionProperty);
		propertyList.add(IMSEIProperty);
		propertyList.add(IMEIProperty);
		propertyList.add(DevManProperty);
		propertyList.add(DevModProperty);

		generatedDevice.setDeviceIdentifier(deviceID);
		generatedDevice.setProperties(propertyList);
		generatedDevice.setType(type);

		return generatedDevice;
	}

	/**
	 * This method prepares the SyncML response.
	 * @param msgID - Incoming message ID
	 * @param targetURI - Target URI in SyncML message
	 * @param sourceURI - Sourse URI in SyncML message
	 * @return - Returns the SyncML response as a String
	 * @throws FileOperationException
	 */
	private String prepareResponse(String msgID, String targetURI, String sourceURI) throws
								   WindowsDeviceEnrolmentException {

		String response = null;
		File responseFile;
		try {
			if (SYNCML_FIRST_MESSAGE.equals(msgID)) {
				responseFile = new File(getClass().getClassLoader().getResource(Constants.SyncML.
						                          SYNCML_RESPONSE).getFile());
				response = FileUtils.readFileToString(responseFile);
				if ((targetURI != null)&&(sourceURI != null)) {
					response = response.replaceAll(Constants.SyncML.SYNCML_SOURCE_URI, targetURI);
					response = response.replaceAll(Constants.SyncML.SYNCML_TARGET_URI, sourceURI);
				}
			}
			else if(SYNCML_SECOND_MESSAGE.equals(msgID)){
				responseFile = new File(getClass().getClassLoader().getResource(Constants.SyncML.
						                          SYNCML_SECOND_RESPONSE).getFile());
				response = FileUtils.readFileToString(responseFile);
				if ((targetURI != null)&&(sourceURI != null)) {
					response = response.replaceAll(Constants.SyncML.SYNCML_SOURCE_URI, targetURI);
					response = response.replaceAll(Constants.SyncML.SYNCML_TARGET_URI, sourceURI);
				}
			}
		} catch (IOException e) {
			String msg = "Syncml response file cannot be read.";
			log.error(msg, e);
			throw new WindowsDeviceEnrolmentException(msg, e);
		}
		return response;
	}
}
