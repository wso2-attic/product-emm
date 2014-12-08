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
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.wso2.carbon.device.mgt.common.spi.DeviceManagerService;
import org.wso2.carbon.device.mgt.mobile.impl.android.AndroidDeviceManagerService;
import org.wso2.carbon.device.mgt.mobile.impl.ios.IOSDeviceManagerService;
import org.wso2.carbon.device.mgt.mobile.impl.windows.WindowsDeviceManagerService;

public class MobileDeviceManagementServiceComponent implements BundleActivator {

	private static final Log log = LogFactory.getLog(MobileDeviceManagementServiceComponent.class);
	private ServiceRegistration androidServiceRegRef;
	private ServiceRegistration iOSServiceRegRef;
	private ServiceRegistration windowsServiceRegRef;

    @Override
    public void start(BundleContext bundleContext) throws Exception {
        try {
            if (log.isDebugEnabled()) {
                log.debug("Activating Mobile Device Management Service bundle");
            }
            androidServiceRegRef =
                    bundleContext.registerService(DeviceManagerService.class.getName(),
                            new AndroidDeviceManagerService(), null);
            iOSServiceRegRef =
                    bundleContext.registerService(DeviceManagerService.class.getName(),
                            new IOSDeviceManagerService(), null);
            windowsServiceRegRef =
                    bundleContext.registerService(DeviceManagerService.class.getName(),
                            new WindowsDeviceManagerService(), null);
            if (log.isDebugEnabled()) {
                log.debug("Mobile Device Management Service bundle is activated");
            }
        } catch (Throwable e) {
            log.error("Error occurred while activating Mobile Device Management Service Component", e);
        }
    }

    @Override
    public void stop(BundleContext bundleContext) throws Exception {
        if (log.isDebugEnabled()) {
            log.debug("Deactivating Mobile Device Management Service");
        }
        androidServiceRegRef.unregister();
        iOSServiceRegRef.unregister();
        windowsServiceRegRef.unregister();
    }

}
