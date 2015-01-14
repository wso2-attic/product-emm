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

package org.wso2.carbon.device.mgt.mobile.config;

import org.w3c.dom.Document;
import org.wso2.carbon.device.mgt.common.DeviceManagementException;
import org.wso2.carbon.device.mgt.mobile.util.MobileDeviceManagementUtil;
import org.wso2.carbon.device.mgt.mobile.config.datasource.MobileDataSourceConfig;
import org.wso2.carbon.utils.CarbonUtils;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import java.io.File;

/**
 * Class responsible for the mobile device manager configuration initialization.
 */
public class MobileDeviceConfigurationManager {

	private static final String MOBILE_DEVICE_CONFIG_XML_NAME = "mobile-config.xml";
    private static final String MOBILE_DEVICE_PLUGIN_DIRECTORY = "mobile";
	private MobileDeviceManagementConfig currentMobileDeviceConfig;
	private static MobileDeviceConfigurationManager mobileDeviceConfigManager;

	private final String mobileDeviceMgtConfigXMLPath =
			CarbonUtils.getEtcCarbonConfigDirPath() + File.separator + "device-mgt-plugin-configs" + File.separator +
                    MOBILE_DEVICE_PLUGIN_DIRECTORY + File.separator + MOBILE_DEVICE_CONFIG_XML_NAME;

	public static MobileDeviceConfigurationManager getInstance() {
		if (mobileDeviceConfigManager == null) {
			synchronized (MobileDeviceConfigurationManager.class) {
				if (mobileDeviceConfigManager == null) {
					mobileDeviceConfigManager = new MobileDeviceConfigurationManager();
				}
			}
		}
		return mobileDeviceConfigManager;
	}

	public synchronized void initConfig() throws DeviceManagementException {
		try {
			File mobileDeviceMgtConfig = new File(mobileDeviceMgtConfigXMLPath);
			Document doc = MobileDeviceManagementUtil.convertToDocument(mobileDeviceMgtConfig);
			JAXBContext mobileDeviceMgmtContext =
					JAXBContext.newInstance(MobileDeviceManagementConfig.class);
			Unmarshaller unmarshaller = mobileDeviceMgmtContext.createUnmarshaller();
			this.currentMobileDeviceConfig =
					(MobileDeviceManagementConfig) unmarshaller.unmarshal(doc);
		} catch (Exception e) {
			throw new DeviceManagementException(
					"Error occurred while initializing Mobile Device Management config", e);
		}
	}

	public MobileDeviceManagementConfig getMobileDeviceManagementConfig() {
		return currentMobileDeviceConfig;
	}

	public MobileDataSourceConfig getMobileDataSourceConfig() {
		return currentMobileDeviceConfig.getMobileDeviceMgtRepository().getMobileDataSourceConfig();
	}

}
