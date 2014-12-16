package cdm.api.android;

import org.wso2.carbon.device.mgt.common.*;
import org.wso2.carbon.device.mgt.common.Device;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;


@Produces({"application/json", "application/xml"})
@Consumes({"application/json", "application/xml"})
public class Test {

    @GET
    public List<org.wso2.carbon.device.mgt.common.Device> getAllDevices() {

        Device dev = new Device();
        dev.setName("test1");
        dev.setDateOfEnrolment(11111111L);
        dev.setDateOfLastUpdate(992093209L);
        dev.setDescription("sassasaas");

        ArrayList<Device> listdevices = new ArrayList<Device>();
        listdevices.add(dev);

        return listdevices;
    }

}
