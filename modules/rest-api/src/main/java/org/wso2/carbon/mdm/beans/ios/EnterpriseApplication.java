package org.wso2.carbon.mdm.beans.ios;

import java.io.Serializable;

public class EnterpriseApplication implements Serializable {

    private String identifier;
    private String manifestURL;
    private boolean removeAppUponMDMProfileRemoval;
    private boolean preventBackupOfAppData;
    private String bundleId;

    public String getUUID() {
        return UUID;
    }

    public void setUUID(String UUID) {
        this.UUID = UUID;
    }

    private String UUID;

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getManifestURL() {
        return manifestURL;
    }

    public void setManifestURL(String manifestURL) {
        this.manifestURL = manifestURL;
    }

    public boolean isRemoveAppUponMDMProfileRemoval() {
        return removeAppUponMDMProfileRemoval;
    }

    public void setRemoveAppUponMDMProfileRemoval(boolean removeAppUponMDMProfileRemoval) {
        this.removeAppUponMDMProfileRemoval = removeAppUponMDMProfileRemoval;
    }

    public boolean isPreventBackupOfAppData() {
        return preventBackupOfAppData;
    }

    public void setPreventBackupOfAppData(boolean preventBackupOfAppData) {
        this.preventBackupOfAppData = preventBackupOfAppData;
    }

    public String getBundleId() {
        return bundleId;
    }

    public void setBundleId(String bundleId) {
        this.bundleId = bundleId;
    }
}
