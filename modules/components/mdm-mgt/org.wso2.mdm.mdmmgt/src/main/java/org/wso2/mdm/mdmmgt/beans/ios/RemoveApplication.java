package org.wso2.mdm.mdmmgt.beans.ios;

import com.google.gson.Gson;
import org.wso2.mdm.mdmmgt.common.MDMException;

import java.io.Serializable;

public class RemoveApplication implements Serializable {

    private String bundleId;

    public String getBundleId() {
        return bundleId;
    }

    public void setBundleId(String bundleId) {
        this.bundleId = bundleId;
    }

    public String toJSON() throws MDMException {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
