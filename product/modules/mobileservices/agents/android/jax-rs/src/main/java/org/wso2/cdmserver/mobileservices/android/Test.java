package org.wso2.cdmserver.mobileservices.android;

import org.wso2.carbon.device.mgt.common.*;
import org.wso2.carbon.device.mgt.common.Device;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

/**
 * This is a Test class
 */
@Produces({"application/json", "application/xml"})
@Consumes({"application/json", "application/xml"})
public class Test {

    @GET
    public List<org.wso2.carbon.device.mgt.common.Device> getAllDevices() throws DeviceManagementException{

        Device dev = new Device();
        dev.setName("test1");
        dev.setDateOfEnrolment(11111111L);
        dev.setDateOfLastUpdate(992093209L);
        dev.setDescription("sassasaas");

        ArrayList<Device> listdevices = new ArrayList<Device>();
        listdevices.add(dev);
        throw new DeviceManagementException("test ex");

   }
}
