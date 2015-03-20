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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.wso2.carbon.device.mgt.common.Device;
import org.wso2.carbon.device.mgt.common.DeviceIdentifier;
import org.wso2.carbon.device.mgt.common.DeviceManagementConstants;
import org.wso2.carbon.device.mgt.common.DeviceManagementException;
import org.wso2.carbon.device.mgt.common.operation.mgt.Operation;
import org.wso2.carbon.device.mgt.common.operation.mgt.OperationManagementException;
import org.wso2.carbon.device.mgt.core.operation.mgt.SimpleOperation;
import org.wso2.carbon.mdm.mobileservices.windows.common.Constants;
import org.wso2.carbon.mdm.mobileservices.windows.common.exceptions.FileOperationException;
import org.wso2.carbon.mdm.mobileservices.windows.common.exceptions.WindowsDeviceEnrolmentException;
import org.wso2.carbon.mdm.mobileservices.windows.common.util.SyncmlCommandType;
import org.wso2.carbon.mdm.mobileservices.windows.operations.SyncmlDocument;
import org.wso2.carbon.mdm.mobileservices.windows.operations.WindowsOperationException;
import org.wso2.carbon.mdm.mobileservices.windows.operations.util.OperationReply;
import org.wso2.carbon.mdm.mobileservices.windows.operations.util.SyncmlGenerator;
import org.wso2.carbon.mdm.mobileservices.windows.operations.util.SyncmlParser;
import org.wso2.carbon.mdm.mobileservices.windows.services.syncml.SyncmlService;
import org.wso2.carbon.mdm.mobileservices.windows.services.syncml.beans.BasicOperation;
import org.wso2.carbon.mdm.mobileservices.windows.services.syncml.util.SyncmlUtils;

import javax.ws.rs.core.Response;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

	//Primary method for Syncml engine usage...
	public Response getResponse(Document request) throws WindowsOperationException, WindowsDeviceEnrolmentException {

		SyncmlDocument syncmlDocument = SyncmlParser.parseSyncmlPayload(request);
		int msgID = syncmlDocument.getHeader().getMsgID();
		DeviceIdentifier deviceIdentifier = new DeviceIdentifier();
		deviceIdentifier.setId(syncmlDocument.getHeader().getSource().getLocURI());
		deviceIdentifier.setType("Windows");
 		List<Operation> pendingOperations;

		if(msgID == 1){
			pendingOperations = new ArrayList<Operation>();
            BasicOperation basicOperation = new BasicOperation();
			ObjectWriter objectWriter = new ObjectMapper().writer().withDefaultPrettyPrinter();

//			try {
				Operation osVersion = new SimpleOperation();
				basicOperation.setName("OS_VERSION");
				osVersion.setCode(SyncmlCommandType.BASIC.getValue());
				osVersion.setType(Operation.Type.INFO);
//				osVersion.setPayload(objectWriter.writeValueAsString(basicOperation));
				pendingOperations.add(osVersion);


				Operation imsi = new SimpleOperation();
				basicOperation.setName("IMSI");
				imsi.setCode(SyncmlCommandType.BASIC.getValue());
				imsi.setType(Operation.Type.INFO);
//				imsi.setPayload(objectWriter.writeValueAsString(basicOperation));
				pendingOperations.add(imsi);

				Operation imei = new SimpleOperation();
				basicOperation.setName("IMEI");
				imei.setCode(SyncmlCommandType.BASIC.getValue());
				imei.setType(Operation.Type.INFO);
//				imei.setPayload(objectWriter.writeValueAsString(basicOperation));
				pendingOperations.add(imei);

				Operation deviceID = new SimpleOperation();
				basicOperation.setName("DEVICE_ID");
				deviceID.setCode(SyncmlCommandType.BASIC.getValue());
				deviceID.setType(Operation.Type.INFO);
//				deviceID.setPayload(objectWriter.writeValueAsString(basicOperation));
				pendingOperations.add(deviceID);

				Operation manufacturer = new SimpleOperation();
				basicOperation.setName("MANUFACTURER");
				manufacturer.setCode(SyncmlCommandType.BASIC.getValue());
				manufacturer.setType(Operation.Type.INFO);
//				manufacturer.setPayload(objectWriter.writeValueAsString(basicOperation));
				pendingOperations.add(manufacturer);

				Operation model = new SimpleOperation();
				basicOperation.setName("MODEL");
				model.setCode(SyncmlCommandType.BASIC.getValue());
				model.setType(Operation.Type.INFO);
//				model.setPayload(objectWriter.writeValueAsString(basicOperation));
				pendingOperations.add(model);

				Operation language = new SimpleOperation();
				basicOperation.setName("LANGUAGE");
				language.setCode(SyncmlCommandType.BASIC.getValue());
				language.setType(Operation.Type.INFO);
//				language.setPayload(objectWriter.writeValueAsString(basicOperation));
				pendingOperations.add(language);
//			}
//			catch (IOException e){
//				String msg = "Initial get operations cannot be performed.";
//				log.error(msg);
//				throw new WindowsDeviceEnrolmentException(msg, e);
//			}
		}
		else{
			try {
				pendingOperations = SyncmlUtils.getOperationManagementService().getPendingOperations(deviceIdentifier);
			} catch (OperationManagementException e) {
				throw new WindowsOperationException("Cannot access operation management service." , e);
			}
		}

		OperationReply operationReply = new OperationReply(syncmlDocument, pendingOperations);
		SyncmlDocument syncmlResponse = operationReply.generateReply();
		SyncmlGenerator generator = new SyncmlGenerator();
		String response = generator.generatePayload(syncmlResponse);

		return Response.ok().entity(response).build();
	}
}
