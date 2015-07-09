package org.wso2.carbon.mdm.beans.ios;

import com.google.gson.Gson;
import org.wso2.carbon.mdm.api.common.MDMAPIException;
import java.io.IOException;

public class WebClip {

    private String URL;
    private String label;
    private String icon;
    private String isRemovable;
    private String UUID;

    public String getUUID() {
        return UUID;
    }

    public void setUUID(String UUID) {
        this.UUID = UUID;
    }

    public String getURL() {
        return URL;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getIsRemovable() {
        return isRemovable;
    }

    public void setIsRemovable(String isRemovable) {
        this.isRemovable = isRemovable;
    }

    public String toJSON() throws MDMAPIException {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

}
