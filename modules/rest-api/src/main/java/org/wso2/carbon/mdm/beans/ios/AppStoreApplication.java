package org.wso2.carbon.mdm.beans.ios;

import java.io.Serializable;

public class AppStoreApplication implements Serializable {

    private String identifier;
    private int iTunesStoreID;
    private boolean removeAppUponMDMProfileRemoval;
    private boolean preventBackupOfAppData;
    private String bundleId;
    private String UUID;

    public String getUUID() {
        return UUID;
    }

    public void setUUID(String UUID) {
        this.UUID = UUID;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public int getiTunesStoreID() {
        return iTunesStoreID;
    }

    public void setiTunesStoreID(int iTunesStoreID) {
        this.iTunesStoreID = iTunesStoreID;
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
