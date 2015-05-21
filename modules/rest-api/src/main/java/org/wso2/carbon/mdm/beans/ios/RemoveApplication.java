package org.wso2.carbon.mdm.beans.ios;

import org.codehaus.jackson.map.ObjectMapper;
import org.wso2.carbon.mdm.api.common.MDMAPIException;

import java.io.IOException;
import java.io.Serializable;

public class RemoveApplication implements Serializable {

    private String bundleId;

    public String getBundleId() {
        return bundleId;
    }

    public void setBundleId(String bundleId) {
        this.bundleId = bundleId;
    }

    public String toJSON() throws MDMAPIException {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(this);
        } catch (IOException e) {
            throw new MDMAPIException("Error generating JSON representation for remove application:", e);
        }
    }
}
