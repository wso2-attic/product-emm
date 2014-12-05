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

package org.wso2.carbon.device.mgt.mobile.impl.internal;

import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.ComponentContext;
import org.wso2.carbon.device.mgt.common.spi.DeviceManagerService;
import org.wso2.carbon.device.mgt.mobile.impl.android.AndroidDeviceManagerService;
import org.wso2.carbon.device.mgt.mobile.impl.ios.IOSDeviceManagerService;
import org.wso2.carbon.device.mgt.mobile.impl.windows.WindowsDeviceManagerService;

/**
 * @scr.component name="org.wso2.carbon.device.manager.mobile" immediate="true"
 */
public class MobileDeviceMgtServiceComponent {

	private static final Log log = LogFactory.getLog(MobileDeviceMgtServiceComponent.class);
	ServiceRegistration serviceRegistration;

	protected void activate(ComponentContext ctx) {
		try {
			if (log.isDebugEnabled()) {
				log.debug("Activating Mobile Device Management Service");
			}
			AndroidDeviceManagerService androidDeviceMgrService = new AndroidDeviceManagerService();
			IOSDeviceManagerService iOSDeviceMgrService = new IOSDeviceManagerService();
			WindowsDeviceManagerService windowsDeviceMgrService = new WindowsDeviceManagerService();
			serviceRegistration =
					ctx.getBundleContext().registerService(DeviceManagerService.class.getName(),
					                                       androidDeviceMgrService, null);
			serviceRegistration =
					ctx.getBundleContext().registerService(DeviceManagerService.class.getName(),
					                                       iOSDeviceMgrService, null);
			serviceRegistration =
					ctx.getBundleContext().registerService(DeviceManagerService.class.getName(),
					                                       windowsDeviceMgrService, null);
		} catch (Throwable e) {
			log.error("Unable to activate Mobile Device Management Service Component", e);
		}
	}

	protected void deactivate(ComponentContext ctx) {
		if (log.isDebugEnabled()) {
			log.debug("Deactivating Mobile Device Management Service");
		}
		serviceRegistration.unregister();
	}
}
