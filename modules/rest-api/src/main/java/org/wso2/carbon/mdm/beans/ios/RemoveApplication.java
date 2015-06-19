package org.wso2.carbon.mdm.beans.ios;

import com.google.gson.Gson;
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
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
