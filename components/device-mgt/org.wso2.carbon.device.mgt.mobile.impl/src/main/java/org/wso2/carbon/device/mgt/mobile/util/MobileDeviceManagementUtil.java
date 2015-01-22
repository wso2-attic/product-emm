/*
 * Copyright (c) 2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.wso2.carbon.device.mgt.mobile.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.wso2.carbon.device.mgt.common.Device;
import org.wso2.carbon.device.mgt.common.DeviceManagementException;
import org.wso2.carbon.device.mgt.common.Operation;
import org.wso2.carbon.device.mgt.mobile.dto.MobileDevice;
import org.wso2.carbon.device.mgt.mobile.dto.MobileDeviceOperationMapping;
import org.wso2.carbon.device.mgt.mobile.dto.MobileOperation;
import org.wso2.carbon.device.mgt.mobile.dto.MobileOperationProperty;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.*;

/**
 * Provides utility methods required by the mobile device management bundle.
 */
public class MobileDeviceManagementUtil {

	private static final Log log = LogFactory.getLog(MobileDeviceManagementUtil.class);
	private static final String MOBILE_DEVICE_IMEI = "imei";
	private static final String MOBILE_DEVICE_IMSI = "imsi";
	private static final String MOBILE_DEVICE_REG_ID = "regId";
	private static final String MOBILE_DEVICE_VENDOR = "vendor";
	private static final String MOBILE_DEVICE_OS_VERSION = "osVersion";
	private static final String MOBILE_DEVICE_MODEL = "model";
	private static final String MOBILE_DEVICE_LATITUDE = "latitude";
	private static final String MOBILE_DEVICE_LONGITUDE = "longitude";

	public static Document convertToDocument(File file) throws DeviceManagementException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		try {
			DocumentBuilder docBuilder = factory.newDocumentBuilder();
			return docBuilder.parse(file);
		} catch (Exception e) {
			throw new DeviceManagementException(
					"Error occurred while parsing file, while converting " +
					"to a org.w3c.dom.Document : " + e.getMessage(), e);
		}
	}

	public static MobileDevice convertToMobileDevice(Device device) {
		MobileDevice mobileDevice = null;
		if (device != null) {
			mobileDevice = new MobileDevice();
			mobileDevice.setMobileDeviceId(device.getDeviceIdentifier());
			mobileDevice.setImei(device.getProperties().get(MOBILE_DEVICE_IMEI));
			mobileDevice.setImsi(device.getProperties().get(MOBILE_DEVICE_IMSI));
			mobileDevice.setRegId(device.getProperties().get(MOBILE_DEVICE_REG_ID));
			mobileDevice.setModel(device.getProperties().get(MOBILE_DEVICE_MODEL));
			mobileDevice.setOsVersion(device.getProperties().get(MOBILE_DEVICE_OS_VERSION));
			mobileDevice.setVendor(device.getProperties().get(MOBILE_DEVICE_VENDOR));
			mobileDevice.setLatitude(device.getProperties().get(MOBILE_DEVICE_LATITUDE));
			mobileDevice.setLongitude(device.getProperties().get(MOBILE_DEVICE_LONGITUDE));
		}
		return mobileDevice;
	}

	public static Device convertToDevice(MobileDevice mobileDevice) {
		Device device = null;
		if (mobileDevice != null) {
			device = new Device();
			Map<String, String> propertyMap = new HashMap<String, String>();
			propertyMap.put(MOBILE_DEVICE_IMEI, mobileDevice.getImei());
			propertyMap.put(MOBILE_DEVICE_IMSI, mobileDevice.getImsi());
			propertyMap.put(MOBILE_DEVICE_REG_ID, mobileDevice.getRegId());
			propertyMap.put(MOBILE_DEVICE_MODEL, mobileDevice.getModel());
			propertyMap.put(MOBILE_DEVICE_OS_VERSION, mobileDevice.getOsVersion());
			propertyMap.put(MOBILE_DEVICE_VENDOR, mobileDevice.getVendor());
			propertyMap.put(MOBILE_DEVICE_LATITUDE, mobileDevice.getLatitude());
			propertyMap.put(MOBILE_DEVICE_LONGITUDE, mobileDevice.getLongitude());
			device.setProperties(propertyMap);
			device.setDeviceIdentifier(mobileDevice.getMobileDeviceId());
		}
		return device;
	}

	public static MobileOperation convertToMobileOperation(
			org.wso2.carbon.device.mgt.common.Operation operation) {
		MobileOperation mobileOperation = new MobileOperation();
		MobileOperationProperty operationProperty = null;
		List<MobileOperationProperty> properties = new LinkedList<MobileOperationProperty>();
		mobileOperation.setFeatureCode(operation.getCode());
		mobileOperation.setCreatedDate(new Date().getTime());
		Properties operationProperties = operation.getProperties();
		for (String key : operationProperties.stringPropertyNames()) {
			operationProperty = new MobileOperationProperty();
			operationProperty.setProperty(key);
			operationProperty.setValue(operationProperties.getProperty(key));
			properties.add(operationProperty);
		}
		mobileOperation.setProperties(properties);
		return mobileOperation;
	}

	public static List<Integer> getMobileOperationIdsFromMobileDeviceOperations(
			List<MobileDeviceOperationMapping> mobileDeviceOperationMappings) {
		List<Integer> mobileOperationIds = new ArrayList<Integer>();
		for(MobileDeviceOperationMapping mobileDeviceOperationMapping : mobileDeviceOperationMappings){
			mobileOperationIds.add(mobileDeviceOperationMapping.getOperationId());
		}
		return mobileOperationIds;
	}

	public static Operation convertMobileOperationToOperation(MobileOperation mobileOperation){
		Operation operation = new Operation();
		Properties properties = new Properties();
		operation.setCode(mobileOperation.getFeatureCode());
		for(MobileOperationProperty mobileOperationProperty:mobileOperation.getProperties()){
			properties.put(mobileOperationProperty.getProperty(),mobileOperationProperty.getValue());
		}
		operation.setProperties(properties);
		return operation;
	}
}
