/**
 *  Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.wso2.carbon.device.mgt.core;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.device.mgt.common.spi.DeviceManagerService;

public class DeviceManagementRepositoryTests {

    private DeviceManagementRepository repository;

    @BeforeClass
    public void initRepository() {
        this.repository = new DeviceManagementRepository();
    }

    @Test
    public void testAddDeviceManagementService() {
        DeviceManagerService sourceProvider = new TestDeviceManagerService();
        this.getRepository().addDeviceManagementProvider(sourceProvider);

        DeviceManagerService targetProvider =
                this.getRepository().getDeviceManagementProvider(TestDeviceManagerService.DEVICE_TYPE_TEST);

        Assert.assertEquals(targetProvider.getProviderType(), sourceProvider.getProviderType());
    }

    @Test(dependsOnMethods = "testAddDeviceManagementService")
    public void testRemoveDeviceManagementService() {
        DeviceManagerService sourceProvider = new TestDeviceManagerService();
        this.getRepository().removeDeviceManagementProvider(sourceProvider);

        DeviceManagerService targetProvider =
                this.getRepository().getDeviceManagementProvider(TestDeviceManagerService.DEVICE_TYPE_TEST);

        Assert.assertNull(targetProvider);
    }

    private DeviceManagementRepository getRepository() {
        return repository;
    }

}
