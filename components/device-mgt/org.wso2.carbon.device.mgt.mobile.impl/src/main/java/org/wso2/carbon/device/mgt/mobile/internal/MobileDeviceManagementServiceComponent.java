/**
 *  Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.wso2.carbon.device.mgt.mobile.internal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.ComponentContext;
import org.wso2.carbon.apimgt.api.APIManagementException;
import org.wso2.carbon.apimgt.api.APIProvider;
import org.wso2.carbon.apimgt.impl.APIManagerConfigurationService;
import org.wso2.carbon.apimgt.impl.APIManagerFactory;
import org.wso2.carbon.device.mgt.common.DeviceManagementException;
import org.wso2.carbon.device.mgt.common.spi.DeviceManagerService;
import org.wso2.carbon.device.mgt.mobile.config.APIConfig;
import org.wso2.carbon.device.mgt.mobile.config.MobileDeviceConfigurationManager;
import org.wso2.carbon.device.mgt.mobile.config.MobileDeviceManagementConfig;
import org.wso2.carbon.device.mgt.mobile.config.datasource.MobileDataSourceConfig;
import org.wso2.carbon.device.mgt.mobile.dao.MobileDeviceManagementDAOFactory;
import org.wso2.carbon.device.mgt.mobile.dao.util.MobileDeviceManagementDAOUtil;
import org.wso2.carbon.device.mgt.mobile.impl.android.AndroidDeviceManagerService;
import org.wso2.carbon.device.mgt.mobile.impl.ios.IOSDeviceManagerService;
import org.wso2.carbon.device.mgt.mobile.impl.windows.WindowsDeviceManagerService;
import org.wso2.carbon.device.mgt.mobile.util.DeviceManagementAPIPublisherUtil;

import java.util.List;

/**
 * @scr.component name="org.wso2.carbon.device.mgt.mobile.impl.internal.MobileDeviceManagementServiceComponent"
 * immediate="true"
 * @scr.reference name="api.manager.config.service"
 * interface="org.wso2.carbon.apimgt.impl.APIManagerConfigurationService"
 * cardinality="1..1"
 * policy="dynamic"
 * bind="setAPIManagerConfigurationService"
 * unbind="unsetAPIManagerConfigurationService"
 * <p/>
 * Adding reference to API Manager Configuration service is an unavoidable hack to get rid of NPEs thrown while
 * initializing APIMgtDAOs attempting to register APIs programmatically. APIMgtDAO needs to be proper cleaned up
 * to avoid as an ideal fix
 */
public class MobileDeviceManagementServiceComponent {

	private ServiceRegistration androidServiceRegRef;
	private ServiceRegistration iOSServiceRegRef;
	private ServiceRegistration windowsServiceRegRef;

	private static final Log log = LogFactory.getLog(MobileDeviceManagementServiceComponent.class);

	protected void activate(ComponentContext ctx) {
		if (log.isDebugEnabled()) {
			log.debug("Activating Mobile Device Management Service Component");
		}
		try {
			BundleContext bundleContext = ctx.getBundleContext();

            /* Initialize the datasource configuration */
			MobileDeviceConfigurationManager.getInstance().initConfig();
			MobileDeviceManagementConfig config = MobileDeviceConfigurationManager.getInstance()
			                                                                      .getMobileDeviceManagementConfig();
			MobileDataSourceConfig dsConfig =
					config.getMobileDeviceMgtRepository().getMobileDataSourceConfig();

			MobileDeviceManagementDAOFactory.setMobileDataSourceConfig(dsConfig);
			MobileDeviceManagementDAOFactory.init();
			String setupOption = System.getProperty("setup");
			if (setupOption != null) {
				if (log.isDebugEnabled()) {
					log.debug(
							"-Dsetup is enabled. Mobile Device management repository schema initialization is about " +
							"to begin");
				}
				try {
					MobileDeviceManagementDAOUtil.setupMobileDeviceManagementSchema(
							MobileDeviceManagementDAOFactory.getDataSource());
				} catch (DeviceManagementException e) {
					log.error(
							"Exception occurred while initializing mobile device management database schema",
							e);
				}
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

            /* Initialize all API configurations with corresponding API Providers */
			this.initAPIConfigs();
	        /* Publish all mobile device management related JAX-RS services as APIs */
			this.publishAPIs();

			if (log.isDebugEnabled()) {
				log.debug(
						"Mobile Device Management Service Component has been successfully activated");
			}
		} catch (Throwable e) {
			log.error("Error occurred while activating Mobile Device Management Service Component",
			          e);
		}
	}

	protected void deactivate(ComponentContext ctx) {
		if (log.isDebugEnabled()) {
			log.debug("De-activating Mobile Device Management Service Component");
		}
		try {
			BundleContext bundleContext = ctx.getBundleContext();

			androidServiceRegRef.unregister();
			iOSServiceRegRef.unregister();
			windowsServiceRegRef.unregister();

            /* Removing all APIs published upon start-up for mobile device management related JAX-RS
               services */
			this.removeAPIs();
			if (log.isDebugEnabled()) {
				log.debug(
						"Mobile Device Management Service Component has been successfully de-activated");
			}
		} catch (Throwable e) {
			log.error("Error occurred while de-activating Mobile Device Management bundle", e);
		}
	}

	private void initAPIConfigs() throws DeviceManagementException {
		List<APIConfig> apiConfigs =
				MobileDeviceConfigurationManager.getInstance().getMobileDeviceManagementConfig().
						getApiPublisherConfig().getAPIs();
		for (APIConfig apiConfig : apiConfigs) {
			try {
				APIProvider provider =
						APIManagerFactory.getInstance().getAPIProvider(apiConfig.getOwner());
				apiConfig.init(provider);
			} catch (APIManagementException e) {
				throw new DeviceManagementException(
						"Error occurred while initializing API Config '" +
						apiConfig.getName() + "'", e);
			}
		}
	}

	private void publishAPIs() throws DeviceManagementException {
		List<APIConfig> apiConfigs =
				MobileDeviceConfigurationManager.getInstance().getMobileDeviceManagementConfig().
						getApiPublisherConfig().getAPIs();
		for (APIConfig apiConfig : apiConfigs) {
			DeviceManagementAPIPublisherUtil.publishAPI(apiConfig);
		}
	}

	private void removeAPIs() throws DeviceManagementException {
		List<APIConfig> apiConfigs =
				MobileDeviceConfigurationManager.getInstance().getMobileDeviceManagementConfig().
						getApiPublisherConfig().getAPIs();
		for (APIConfig apiConfig : apiConfigs) {
			DeviceManagementAPIPublisherUtil.removeAPI(apiConfig);
		}
	}

	protected void setAPIManagerConfigurationService(APIManagerConfigurationService service) {
		//do nothing
	}

	protected void unsetAPIManagerConfigurationService(APIManagerConfigurationService service) {
		//do nothing
	}

}
