package org.wso2.carbon.mdm.beans.ios;

import org.codehaus.jackson.map.ObjectMapper;
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
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(this);
        } catch (IOException e) {
            throw new MDMAPIException("Error generating JSON representation for enterprise app:", e);
        }
    }

}
