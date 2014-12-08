/*
 *  Copyright (c) 2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 *
 */

package org.wso2.carbon.device.mgt.core.internal;

import org.wso2.carbon.device.mgt.core.DeviceManagementRepository;
import org.wso2.carbon.device.mgt.core.DeviceManager;
import org.wso2.carbon.user.core.service.RealmService;
import org.wso2.carbon.user.core.tenant.TenantManager;

public class DeviceManagementDataHolder {

    private RealmService realmService;
    private TenantManager tenantManager;
    private DeviceManager deviceManager;
    private static DeviceManagementDataHolder thisInstance = new DeviceManagementDataHolder();

    private DeviceManagementDataHolder() {}

    public static DeviceManagementDataHolder getInstance() {
        return thisInstance;
    }

    public RealmService getRealmService() {
        return realmService;
    }

    public void setRealmService(RealmService realmService) {
        this.realmService = realmService;
        this.setTenantManager(realmService);
    }

    private void setTenantManager(RealmService realmService) {
        if (realmService == null) {
            throw new IllegalStateException("Realm service is not initialized properly");
        }
        this.tenantManager = realmService.getTenantManager();
    }

    public TenantManager getTenantManager() {
        return tenantManager;
    }

    public DeviceManager getDeviceManager() {
        return deviceManager;
    }

    public void setDeviceManager(DeviceManager deviceManager) {
        this.deviceManager = deviceManager;
    }

}
