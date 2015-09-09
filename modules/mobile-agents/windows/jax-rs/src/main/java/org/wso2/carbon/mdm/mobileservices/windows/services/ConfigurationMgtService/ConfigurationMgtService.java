package org.wso2.carbon.mdm.mobileservices.windows.services.ConfigurationMgtService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.device.mgt.common.DeviceManagementConstants;
import org.wso2.carbon.device.mgt.common.DeviceManagementException;
import org.wso2.carbon.device.mgt.common.configuration.mgt.TenantConfiguration;
import org.wso2.carbon.mdm.mobileservices.windows.common.exceptions.WindowsConfigurationException;
import org.wso2.carbon.mdm.mobileservices.windows.common.util.Message;
import org.wso2.carbon.mdm.mobileservices.windows.common.util.WindowsAPIUtils;

import javax.jws.WebService;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;

/**
 * Windows Platform Configuration REST-API implementation.
 * All end points supports JSON, XMl with content negotiation.
 */
@WebService
@Produces({"application/json", "application/xml"})
@Consumes({"application/json", "application/xml"})
public class ConfigurationMgtService {

    private static Log log = LogFactory.getLog(ConfigurationMgtService.class);

    @POST
    public Message ConfigureSettings(TenantConfiguration configuration) throws WindowsConfigurationException {

        Message responseMsg = new Message();
        configuration.setType(DeviceManagementConstants.MobileDeviceTypes.MOBILE_DEVICE_TYPE_WINDOWS);
        try {
            WindowsAPIUtils.getDeviceManagementService().saveConfiguration(configuration);
            Response.status(Response.Status.CREATED);
            responseMsg.setResponseMessage("Windows platform configuration saved successfully.");
            responseMsg.setResponseCode(Response.Status.CREATED.toString());
            return responseMsg;
        } catch (DeviceManagementException e) {
            String msg = "Error Occurred in while configuring Windows Platform.";
            log.error(msg, e);
            throw new WindowsConfigurationException(msg, e);
        }

    }

    @GET
    public TenantConfiguration getConfiguration() throws WindowsConfigurationException {
        try {
            return WindowsAPIUtils.getDeviceManagementService().getConfiguration(
                    DeviceManagementConstants.MobileDeviceTypes.MOBILE_DEVICE_TYPE_WINDOWS);
        } catch (DeviceManagementException e) {
            String msg = "Error Occurred in while retrieving windows platform configurations.";
            log.error(msg, e);
            throw new WindowsConfigurationException(msg, e);
        }
    }

    @PUT
    public Message updateConfiguration(TenantConfiguration configuration) throws WindowsConfigurationException {

        Message responseMsg = new Message();
        configuration.setType(DeviceManagementConstants.MobileDeviceTypes.MOBILE_DEVICE_TYPE_WINDOWS);
        try {
            WindowsAPIUtils.getDeviceManagementService().saveConfiguration(configuration);
            Response.status(Response.Status.CREATED);
            responseMsg.setResponseMessage("Windows platform configuration succeeded");
            responseMsg.setResponseCode(Response.Status.CREATED.toString());
            return responseMsg;
        } catch (DeviceManagementException e) {
            String msg = "Error Occurred in while modifying Windows Platform.";
            log.error(msg, e);
            throw new WindowsConfigurationException(msg, e);
        }
    }

}
