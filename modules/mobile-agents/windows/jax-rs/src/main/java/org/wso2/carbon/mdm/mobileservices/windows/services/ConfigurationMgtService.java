/*
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.mdm.mobileservices.windows.services;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.device.mgt.common.DeviceManagementConstants;
import org.wso2.carbon.device.mgt.common.DeviceManagementException;
import org.wso2.carbon.device.mgt.common.configuration.mgt.ConfigurationEntry;
import org.wso2.carbon.device.mgt.common.configuration.mgt.TenantConfiguration;
import org.wso2.carbon.device.mgt.common.license.mgt.License;
import org.wso2.carbon.mdm.mobileservices.windows.common.PluginConstants;
import org.wso2.carbon.mdm.mobileservices.windows.common.exceptions.WindowsConfigurationException;
import org.wso2.carbon.mdm.mobileservices.windows.common.util.Message;
import org.wso2.carbon.mdm.mobileservices.windows.common.util.WindowsAPIUtils;

import javax.jws.WebService;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

/**
 * Windows Platform Configuration REST-API implementation.
 * All end points supports JSON, XMl with content negotiation.
 */
@WebService
@Produces({"application/json", "application/xml"})
@Consumes({"application/json", "application/xml"})
public class ConfigurationMgtService {

    private static Log log = LogFactory.getLog(ConfigurationMgtService.class);

    /**
     * Save Tenant configurations.
     *
     * @param configuration Tenant Configurations to be saved.
     * @return Message type object for the provide save status.
     * @throws WindowsConfigurationException
     */
    @POST
    public Message ConfigureSettings(TenantConfiguration configuration) throws WindowsConfigurationException {
        Message responseMsg = new Message();
        ConfigurationEntry licenseEntry = null;
        String message;

        try {
            configuration.setType(DeviceManagementConstants.MobileDeviceTypes.MOBILE_DEVICE_TYPE_WINDOWS);
            if (!configuration.getConfiguration().isEmpty()) {
                List<ConfigurationEntry> configs = configuration.getConfiguration();
                for (ConfigurationEntry entry : configs) {
                    if (PluginConstants.TenantConfigProperties.LICENSE_KEY.equals(entry.getName())) {
                        License license = new License();
                        license.setName(DeviceManagementConstants.MobileDeviceTypes.MOBILE_DEVICE_TYPE_WINDOWS);
                        license.setLanguage(PluginConstants.TenantConfigProperties.LANGUAGE_US);
                        license.setVersion("1.0.0");
                        license.setText(entry.getValue().toString());
                        WindowsAPIUtils.getDeviceManagementService().addLicense(DeviceManagementConstants.
                                MobileDeviceTypes.MOBILE_DEVICE_TYPE_WINDOWS, license);
                        licenseEntry = entry;
                    }
                }

                if (licenseEntry != null) {
                    configs.remove(licenseEntry);
                }
                configuration.setConfiguration(configs);
                WindowsAPIUtils.getDeviceManagementService().saveConfiguration(configuration);
                Response.status(Response.Status.CREATED);
                responseMsg.setResponseMessage("Windows platform configuration saved successfully.");
                responseMsg.setResponseCode(Response.Status.CREATED.toString());
                return responseMsg;
            } else {
                Response.status(Response.Status.BAD_REQUEST);
                responseMsg.setResponseMessage("Windows platform configuration can not be saved.");
                responseMsg.setResponseCode(Response.Status.CREATED.toString());
            }
        } catch (DeviceManagementException e) {
            message = "Error Occurred while configuring Windows Platform.";
            log.error(message, e);
            throw new WindowsConfigurationException(message, e);
        }
        return responseMsg;
    }

    /**
     * Retrieve Tenant configurations according to the device type.
     *
     * @return Tenant configuration object contains specific tenant configurations.
     * @throws WindowsConfigurationException
     */
    @GET
    public TenantConfiguration getConfiguration() throws WindowsConfigurationException {
        String msg;
        TenantConfiguration tenantConfiguration;
        List<ConfigurationEntry> configs;
        try {
            tenantConfiguration = WindowsAPIUtils.getDeviceManagementService().
                    getConfiguration(DeviceManagementConstants.MobileDeviceTypes.MOBILE_DEVICE_TYPE_WINDOWS);
            if (tenantConfiguration != null) {
                configs = tenantConfiguration.getConfiguration();
            } else {
                tenantConfiguration = new TenantConfiguration();
                configs = new ArrayList<>();
            }

            ConfigurationEntry entry = new ConfigurationEntry();
            License license = WindowsAPIUtils.getDeviceManagementService().getLicense(
                    DeviceManagementConstants.MobileDeviceTypes.MOBILE_DEVICE_TYPE_WINDOWS,
                    PluginConstants.TenantConfigProperties.LANGUAGE_US);

            if (license != null && configs != null) {
                entry.setContentType(PluginConstants.TenantConfigProperties.CONTENT_TYPE_TEXT);
                entry.setName(PluginConstants.TenantConfigProperties.LICENSE_KEY);
                entry.setValue(license.getText());
                configs.add(entry);
                tenantConfiguration.setConfiguration(configs);
            }
        } catch (DeviceManagementException e) {
            msg = "Error occurred while retrieving the Windows tenant configuration";
            log.error(msg, e);
            throw new WindowsConfigurationException(msg, e);
        }
        return tenantConfiguration;
    }

    /**
     * Update Tenant Configurations for the specific Device type.
     *
     * @param configuration Tenant configurations to be updated.
     * @return Response message.
     * @throws WindowsConfigurationException
     */
    @PUT
    public Message updateConfiguration(TenantConfiguration configuration) throws WindowsConfigurationException {
        String message;
        Message responseMsg = new Message();
        ConfigurationEntry licenseEntry = null;
        try {
            configuration.setType(DeviceManagementConstants.MobileDeviceTypes.MOBILE_DEVICE_TYPE_WINDOWS);
            List<ConfigurationEntry> configs = configuration.getConfiguration();
            for (ConfigurationEntry entry : configs) {
                if (PluginConstants.TenantConfigProperties.LICENSE_KEY.equals(entry.getName())) {
                    License license = new License();
                    license.setName(DeviceManagementConstants.MobileDeviceTypes.MOBILE_DEVICE_TYPE_WINDOWS);
                    license.setLanguage(PluginConstants.TenantConfigProperties.LANGUAGE_US);
                    license.setVersion("1.0.0");
                    license.setText(entry.getValue().toString());
                    WindowsAPIUtils.getDeviceManagementService().addLicense(DeviceManagementConstants.
                            MobileDeviceTypes.MOBILE_DEVICE_TYPE_WINDOWS, license);
                    licenseEntry = entry;
                }
            }

            if (licenseEntry != null) {
                configs.remove(licenseEntry);
            }
            configuration.setConfiguration(configs);
            WindowsAPIUtils.getDeviceManagementService().saveConfiguration(configuration);
            Response.status(Response.Status.CREATED);
            responseMsg.setResponseMessage("Windows platform configuration succeeded.");
            responseMsg.setResponseCode(Response.Status.CREATED.toString());
        } catch (DeviceManagementException e) {
            message = "Error occurred while modifying configuration settings of Windows platform.";
            log.error(message, e);
            throw new WindowsConfigurationException(message, e);
        }
        return responseMsg;
    }
}
