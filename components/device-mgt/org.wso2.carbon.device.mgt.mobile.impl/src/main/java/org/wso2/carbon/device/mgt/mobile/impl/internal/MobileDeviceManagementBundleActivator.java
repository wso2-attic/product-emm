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
<<<<<<< HEAD
import org.osgi.framework.*;
=======
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.wso2.carbon.device.mgt.common.DeviceManagementException;
>>>>>>> 95f72f77f1a86f8f7e92b8cceafd63b40374dcbb
import org.wso2.carbon.device.mgt.common.spi.DeviceManagerService;
import org.wso2.carbon.device.mgt.mobile.impl.DataSourceListener;
import org.wso2.carbon.device.mgt.mobile.impl.android.AndroidDeviceManagerService;
<<<<<<< HEAD
import org.wso2.carbon.device.mgt.mobile.impl.dao.MobileDeviceDAOFactory;
=======
import org.wso2.carbon.device.mgt.mobile.impl.config.MobileDeviceConfigurationManager;
import org.wso2.carbon.device.mgt.mobile.impl.config.MobileDeviceManagementConfig;
import org.wso2.carbon.device.mgt.mobile.impl.config.datasource.MobileDataSourceConfig;
import org.wso2.carbon.device.mgt.mobile.impl.dao.MobileDeviceManagementDAOFactory;
>>>>>>> 95f72f77f1a86f8f7e92b8cceafd63b40374dcbb
import org.wso2.carbon.device.mgt.mobile.impl.ios.IOSDeviceManagerService;
import org.wso2.carbon.device.mgt.mobile.impl.util.MobileDeviceManagementSchemaInitializer;
import org.wso2.carbon.device.mgt.mobile.impl.windows.WindowsDeviceManagerService;

import java.util.ArrayList;
import java.util.List;

public class MobileDeviceManagementBundleActivator implements BundleActivator, BundleListener {

    private ServiceRegistration androidServiceRegRef;
    private ServiceRegistration iOSServiceRegRef;
    private ServiceRegistration windowsServiceRegRef;
    private static List<DataSourceListener> dataSourceListeners = new ArrayList<DataSourceListener>();

    private static final Log log = LogFactory.getLog(MobileDeviceManagementBundleActivator.class);
    private static final String SYMBOLIC_NAME_DATA_SOURCE_COMPONENT = "org.wso2.carbon.ndatasource.core";

<<<<<<< HEAD
    @Override
    public void start(BundleContext bundleContext) throws Exception {
        try {
            if (log.isDebugEnabled()) {
                log.debug("Activating Mobile Device Management Service bundle");
            }
            bundleContext.addBundleListener(this);

            MobileDeviceDAOFactory daoFactory = new MobileDeviceDAOFactory();
            //TODO Register this dao to an appropriate config file

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

        bundleContext.removeBundleListener(this);
    }

    @Override
    public void bundleChanged(BundleEvent bundleEvent) {
        int eventType = bundleEvent.getType();
        String bundleSymbolicName = bundleEvent.getBundle().getSymbolicName();
        if (SYMBOLIC_NAME_DATA_SOURCE_COMPONENT.equals(bundleSymbolicName) && eventType == BundleEvent.STARTED) {
            for (DataSourceListener listener : this.getDataSourceListeners()) {
                listener.notifyObserver();
            }
        }
    }

    public static void registerDataSourceListener(DataSourceListener listener) {
        dataSourceListeners.add(listener);
    }

    private List<DataSourceListener> getDataSourceListeners() {
        return dataSourceListeners;
    }
=======
	@Override
	public void start(BundleContext bundleContext) throws Exception {
		try {
			//Initialize Mobile Device Management datasource
//			MobileDeviceConfigurationManager.getInstance().initConfig();
//			MobileDeviceManagementConfig config = MobileDeviceConfigurationManager.getInstance()
//			                                                                      .getMobileDeviceManagementConfig();
//			MobileDataSourceConfig
//					dsConfig = config.getMobileDeviceMgtRepository().getMobileDataSourceConfig();
//			MobileDeviceManagementDAOFactory.init(dsConfig);

			/* If -Dsetup option enabled then create device management database schema */
			String setupOption = System.getProperty("setup");
			if (setupOption != null) {
				if (log.isDebugEnabled()) {
					log.debug(
							"-Dsetup is enabled. Mobile Device management repository schema initialization is about " +
							"to begin");
				}
				setupMobileDeviceManagementSchema(null);
			}

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
			log.error("Error occurred while activating Mobile Device Management Service Component",
			          e);
		}
	}

	private void setupMobileDeviceManagementSchema(MobileDataSourceConfig config) throws
	                                                                              DeviceManagementException {
		MobileDeviceManagementSchemaInitializer initializer =
				new MobileDeviceManagementSchemaInitializer(config);
		log.info("Initializing mobile device management repository database schema");
		try {
			initializer.createRegistryDatabase();
		} catch (Exception e) {
			throw new DeviceManagementException(
					"Error occurred while initializing Mobile Device Management " +
					"database schema", e);
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
>>>>>>> 95f72f77f1a86f8f7e92b8cceafd63b40374dcbb

}
