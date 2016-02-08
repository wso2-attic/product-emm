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

package org.wso2.carbon.mdm.mobileservices.windows.services.discovery.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.device.mgt.common.DeviceManagementException;
import org.wso2.carbon.device.mgt.common.configuration.mgt.ConfigurationEntry;
import org.wso2.carbon.mdm.mobileservices.windows.common.PluginConstants;
import org.wso2.carbon.mdm.mobileservices.windows.common.exceptions.WindowsDeviceEnrolmentException;
import org.wso2.carbon.mdm.mobileservices.windows.common.util.WindowsAPIUtils;
import org.wso2.carbon.mdm.mobileservices.windows.services.discovery.DiscoveryService;
import org.wso2.carbon.mdm.mobileservices.windows.services.discovery.beans.DiscoveryRequest;
import org.wso2.carbon.mdm.mobileservices.windows.services.discovery.beans.DiscoveryResponse;

import javax.jws.WebService;
import javax.ws.rs.core.Response;
import javax.xml.ws.BindingType;
import javax.xml.ws.Holder;
import javax.xml.ws.soap.Addressing;
import javax.xml.ws.soap.SOAPBinding;
import java.util.List;

/**
 * Implementation class of Discovery Request. This class implements the first two services
 * of device enrolment stage.
 */
@WebService(endpointInterface = PluginConstants.DISCOVERY_SERVICE_ENDPOINT, targetNamespace = PluginConstants
        .DISCOVERY_SERVICE_TARGET_NAMESPACE)
@Addressing(enabled = true, required = true)
@BindingType(value = SOAPBinding.SOAP12HTTP_BINDING)
public class DiscoveryServiceImpl implements DiscoveryService {

    public static final String FEDERATED = "Federated";
    private static final String DELIMITER = "@";
    private static final int DOMAIN_SEGMENT = 1;
    private static Log log = LogFactory.getLog(DiscoveryServiceImpl.class);

    /**
     * This method returns the OnPremise AuthPolicy and next two endpoint the mobile device should
     * call if this response to received successfully at the device end. This method is called by
     * device immediately after the first GET method calling for the same endpoint.
     *
     * @param discoveryRequest - Request bean comes via mobile phone
     * @param response         - DiscoveryResponse bean for response
     */
    @Override
    public void discover(DiscoveryRequest discoveryRequest, Holder<DiscoveryResponse> response)
            throws WindowsDeviceEnrolmentException {

        String emailId = discoveryRequest.getEmailId();
        String[] userDomains = emailId.split(DELIMITER);
        String domain = userDomains[DOMAIN_SEGMENT];

        DiscoveryResponse discoveryResponse = new DiscoveryResponse();
        if (FEDERATED.equals(getAuthPolicy())) {
            discoveryResponse.setAuthPolicy(FEDERATED);
            discoveryResponse.setEnrollmentPolicyServiceUrl(PluginConstants.Discovery.DEVICE_ENROLLMENT_SUBDOMAIN +
                    domain + PluginConstants.Discovery.
                    CERTIFICATE_ENROLLMENT_POLICY_SERVICE_URL);
            discoveryResponse.setEnrollmentServiceUrl(PluginConstants.Discovery.DEVICE_ENROLLMENT_SUBDOMAIN +
                    domain + PluginConstants.Discovery.
                    CERTIFICATE_ENROLLMENT_SERVICE_URL);
            discoveryResponse.setAuthenticationServiceUrl(PluginConstants.Discovery.DEVICE_ENROLLMENT_SUBDOMAIN +
                    domain + PluginConstants.Discovery.WAB_URL);
        }
        response.value = discoveryResponse;
        if (log.isDebugEnabled()) {
            log.debug("Discovery service end point was triggered via POST method");
        }
    }

    /**
     * This is the first method called through device. The device checks the availability of the
     * Service end point by calling this method.
     *
     * @return - HTTP 200OK message
     */
    @Override
    public Response discoverGet() {

        if (log.isDebugEnabled()) {
            log.debug("Discovery service end point was triggered via GET method.");
        }
        return Response.ok().build();
    }

    /**
     * Get authentication policy from the tenant configuration, otherwise set default value as Federated.
     *
     * @return Authentication policy.
     * @throws WindowsDeviceEnrolmentException
     */
    public String getAuthPolicy() throws WindowsDeviceEnrolmentException {
        String authPolicy = null;
        List<ConfigurationEntry> tenantConfigurations;
        try {
            if (WindowsAPIUtils.getTenantConfigurationData() != null) {
                tenantConfigurations = WindowsAPIUtils.getTenantConfigurationData();

                for (ConfigurationEntry configurationEntry : tenantConfigurations) {
                    if (PluginConstants.TenantConfigProperties.AUTH_POLICY.equals(configurationEntry.getName())) {
                        authPolicy = configurationEntry.getValue().toString();
                    } else {
                        authPolicy = PluginConstants.TenantConfigProperties.DEFAULT_AUTH_POLICY;
                    }
                }
            } else {
                authPolicy = PluginConstants.TenantConfigProperties.DEFAULT_AUTH_POLICY;
                String msg = "Tenant configurations are not initialized yet.";
                log.error(msg);
            }
        } catch (DeviceManagementException e) {
            String msg = "Error occurred while getting tenant configurations.";
            log.error(msg);
            throw new WindowsDeviceEnrolmentException(msg, e);
        }
        return authPolicy;
    }
}
