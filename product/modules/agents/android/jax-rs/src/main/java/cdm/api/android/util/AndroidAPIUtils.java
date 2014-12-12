/*
 * Copyright (c) 2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package cdm.api.android.util;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.device.mgt.common.*;
import org.wso2.carbon.device.mgt.core.service.DeviceManagementService;
import org.wso2.carbon.utils.multitenancy.MultitenantConstants;

import java.util.*;

/**
 * AndroidAPIUtil class provides utility function used by Android REST-API classes.
 */
public class AndroidAPIUtils {

	public static Device convertToDeviceObject(String jsonString) {
		JsonObject obj = new Gson().fromJson(jsonString, JsonObject.class);
		Device device = new Device();
		device.setType(DeviceManagementConstants.MobileDeviceTypes.MOBILE_DEVICE_TYPE_ANDROID);
		if (obj.get(AndroidConstants.DeviceConstants.DEVICE_MAC_KEY) != null) {
			device.setDeviceIdentifier(
					obj.get(AndroidConstants.DeviceConstants.DEVICE_MAC_KEY).getAsString());
		}
		if (obj.get(AndroidConstants.DeviceConstants.DEVICE_DESCRIPTION_KEY) != null) {
			device.setDescription(
					obj.get(AndroidConstants.DeviceConstants.DEVICE_DESCRIPTION_KEY).getAsString());
		}
		if (obj.get(AndroidConstants.DeviceConstants.DEVICE_OWNERSHIP_KEY) != null) {
			device.setOwnership(
					obj.get(AndroidConstants.DeviceConstants.DEVICE_OWNERSHIP_KEY).getAsString());
		}

		if (obj.get(AndroidConstants.DeviceConstants.DEVICE_PROPERTIES_KEY) != null) {
			JsonObject properties =
					new Gson().fromJson(
							obj.get(AndroidConstants.DeviceConstants.DEVICE_PROPERTIES_KEY)
							, JsonObject.class);
			if (properties.get(AndroidConstants.DeviceProperties.PROPERTY_USER_KEY) != null) {
				device.setOwner(properties.get(AndroidConstants.DeviceProperties.PROPERTY_USER_KEY)
				                          .getAsString());
			}
			if (properties.get(AndroidConstants.DeviceProperties.PROPERTY_DEVICE_KEY) != null) {
				device.setName(properties.get(AndroidConstants.DeviceProperties.PROPERTY_DEVICE_KEY)
				                         .getAsString());
			}
			device.setProperties(parseProperties(properties));
		}else{
			device.setProperties(new ArrayList<Property>(0));
		}

		if (obj.get(AndroidConstants.DeviceConstants.DEVICE_FEATURES_KEY) != null) {
			JsonObject features =
					new Gson().fromJson(
							obj.get(AndroidConstants.DeviceConstants.DEVICE_FEATURES_KEY),
							JsonObject.class);
			device.setFeatures(parseFeatures(features));
		}else{
			device.setFeatures(new ArrayList<Feature>(0));
		}
		return device;
	}

	private static List<Property> parseProperties(JsonObject properties) {
		List<Property> propertyList = new ArrayList<Property>(0);
		for (Map.Entry<String, JsonElement> entry : properties.entrySet()) {
			propertyList.add(parseProperty(entry.getKey(), entry.getValue()));
		}
		//		propertyList.add(parseProperty("regid", properties.get("regid").getAsString()));
		//		propertyList.add(parseProperty("osversion", properties.get("osversion").getAsString()));
		//		propertyList.add(parseProperty("vendor", properties.get("vendor").getAsString()));
		//		propertyList.add(parseProperty("imei", properties.get("imei").getAsString()));
		//		propertyList.add(parseProperty("imsi", properties.get("imsi").getAsString()));
		//		propertyList.add(parseProperty("model", properties.get("model").getAsString()));
		return propertyList;
	}

	private static List<Feature> parseFeatures(JsonObject features) {
		List<Feature> featureList = new ArrayList<Feature>(0);
		return featureList;
	}

	private static Property parseProperty(String property, JsonElement value) {
		Property prop = new Property();
		prop.setName(property);
		prop.setValue(value.getAsString());
		return prop;
	}

	private static Feature parseFeature(JsonElement featureElement) {
		Feature feature = new Feature();
		return feature;
	}

	public static DeviceIdentifier convertToDeviceIdentifierObject(String deviceId) {
		DeviceIdentifier identifier = new DeviceIdentifier();
		identifier.setId(deviceId);
		identifier.setType(DeviceManagementConstants.MobileDeviceTypes.MOBILE_DEVICE_TYPE_ANDROID);
		return identifier;
	}


	public static DeviceManagementService getDeviceManagementService() {
		DeviceManagementService dmService;
		PrivilegedCarbonContext.startTenantFlow();
		PrivilegedCarbonContext ctx = PrivilegedCarbonContext.getThreadLocalCarbonContext();
		ctx.setTenantDomain(MultitenantConstants.SUPER_TENANT_DOMAIN_NAME);
		ctx.setTenantId(MultitenantConstants.SUPER_TENANT_ID);
		dmService = (DeviceManagementService) ctx
				.getOSGiService(DeviceManagementService.class, null);
		return dmService;
	}
}
