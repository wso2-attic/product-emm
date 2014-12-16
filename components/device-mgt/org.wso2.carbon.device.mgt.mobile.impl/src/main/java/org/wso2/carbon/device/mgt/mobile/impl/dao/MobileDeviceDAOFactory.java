/**
 *  Copyright (c) 2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
package org.wso2.carbon.device.mgt.mobile.impl.dao;

import org.wso2.carbon.device.mgt.mobile.impl.DataSourceListener;
import org.wso2.carbon.device.mgt.mobile.impl.dao.impl.MobileDeviceDAOImpl;
import org.wso2.carbon.device.mgt.mobile.impl.dao.impl.MobileDeviceModelDAOImpl;
import org.wso2.carbon.device.mgt.mobile.impl.dao.impl.MobileDeviceVendorDAOImpl;
import org.wso2.carbon.device.mgt.mobile.impl.dao.impl.MobileOSVersionDAOImpl;
import org.wso2.carbon.device.mgt.mobile.impl.dao.util.MobileDeviceManagementDAOUtil;
import org.wso2.carbon.device.mgt.mobile.impl.internal.MobileDeviceManagementBundleActivator;

import javax.sql.DataSource;

public class MobileDeviceDAOFactory implements DataSourceListener {

    private static DataSource dataSource;

    public MobileDeviceDAOFactory() {
        MobileDeviceManagementBundleActivator.registerDataSourceListener(this);
    }

    @Override
    public void notifyObserver() {
        dataSource = MobileDeviceManagementDAOUtil.resolveDataSource();
    }

    public static MobileDeviceDAO getMobileDeviceDAO() {
        return new MobileDeviceDAOImpl(dataSource);
    }

    public static MobileDeviceModelDAO getMobileDeviceModelDAO() {
        return new MobileDeviceModelDAOImpl(dataSource);
    }

    public static MobileDeviceVendorDAO getMobileDeviceVendorDAO() {
        return new MobileDeviceVendorDAOImpl(dataSource);
    }

    public static MobileOSVersionDAO getMobileOSVersionDAO() {
        return new MobileOSVersionDAOImpl(dataSource);
    }

}
