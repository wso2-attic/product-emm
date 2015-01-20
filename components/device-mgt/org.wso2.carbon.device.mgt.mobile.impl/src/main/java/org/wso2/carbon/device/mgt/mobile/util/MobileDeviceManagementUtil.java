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
import org.wso2.carbon.device.mgt.mobile.dto.MobileDeviceOperation;
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

	private static String getPropertyValue(Device device, String property) {
		for (Device.Property prop : device.getProperties()) {
			if (property.equals(prop.getName())) {
				return prop.getValue();
			}
		}
		return null;
	}

	private static Device.Property getProperty(String property, String value) {
		Device.Property prop = null;
		if (property != null) {
			prop = new Device.Property();
			prop.setName(property);
			prop.setValue(value);
			return prop;
		}
		return prop;
	}

	public static MobileDevice convertToMobileDevice(Device device) {
		MobileDevice mobileDevice = null;
		if (device != null) {
			mobileDevice = new MobileDevice();
			mobileDevice.setMobileDeviceId(device.getDeviceIdentifier());
			mobileDevice.setImei(getPropertyValue(device, MOBILE_DEVICE_IMEI));
			mobileDevice.setImsi(getPropertyValue(device, MOBILE_DEVICE_IMSI));
			mobileDevice.setRegId(getPropertyValue(device, MOBILE_DEVICE_REG_ID));
			mobileDevice.setModel(getPropertyValue(device, MOBILE_DEVICE_MODEL));
			mobileDevice.setOsVersion(getPropertyValue(device, MOBILE_DEVICE_OS_VERSION));
			mobileDevice.setVendor(getPropertyValue(device, MOBILE_DEVICE_VENDOR));
			mobileDevice.setLatitude(getPropertyValue(device, MOBILE_DEVICE_LATITUDE));
			mobileDevice.setLongitude(getPropertyValue(device, MOBILE_DEVICE_LONGITUDE));
		}
		return mobileDevice;
	}

	public static Device convertToDevice(MobileDevice mobileDevice) {
		Device device = null;
		if (mobileDevice != null) {
			device = new Device();
			List<Device.Property> propertyList = new ArrayList<Device.Property>();
			propertyList.add(getProperty(MOBILE_DEVICE_IMEI, mobileDevice.getImei()));
			propertyList.add(getProperty(MOBILE_DEVICE_IMSI, mobileDevice.getImsi()));
			propertyList.add(getProperty(MOBILE_DEVICE_REG_ID, mobileDevice.getRegId()));
			propertyList.add(getProperty(MOBILE_DEVICE_MODEL, mobileDevice.getModel()));
			propertyList.add(getProperty(MOBILE_DEVICE_OS_VERSION, mobileDevice.getOsVersion()));
			propertyList.add(getProperty(MOBILE_DEVICE_VENDOR, mobileDevice.getVendor()));
			propertyList.add(getProperty(MOBILE_DEVICE_LATITUDE, mobileDevice.getLatitude()));
			propertyList.add(getProperty(MOBILE_DEVICE_LONGITUDE, mobileDevice.getLongitude()));
			device.setProperties(propertyList);
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
			List<MobileDeviceOperation> mobileDeviceOperations) {
		List<Integer> mobileOperationIds = new ArrayList<Integer>();
		for(MobileDeviceOperation mobileDeviceOperation:mobileDeviceOperations){
			mobileOperationIds.add(mobileDeviceOperation.getOperationId());
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
