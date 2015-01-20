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
package org.wso2.carbon.device.mgt.core.dao.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.context.CarbonContext;
import org.wso2.carbon.device.mgt.common.DeviceIdentifier;
import org.wso2.carbon.device.mgt.core.dao.DeviceManagementDAOException;
import org.wso2.carbon.device.mgt.core.dto.Device;
import org.wso2.carbon.device.mgt.core.dto.DeviceType;
import org.wso2.carbon.device.mgt.core.dto.Status;
import org.wso2.carbon.device.mgt.core.internal.DeviceManagementDataHolder;
import org.wso2.carbon.user.api.UserStoreException;
import org.wso2.carbon.user.core.tenant.TenantManager;
import org.wso2.carbon.utils.multitenancy.MultitenantConstants;

import javax.naming.InitialContext;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public final class DeviceManagementDAOUtil {

    private static final Log log = LogFactory.getLog(DeviceManagementDAOUtil.class);

    public static void cleanupResources(Connection conn, PreparedStatement stmt, ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                log.warn("Error occurred while closing result set", e);
            }
        }
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException e) {
                log.warn("Error occurred while closing prepared statement", e);
            }
        }
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                log.warn("Error occurred while closing database connection", e);
            }
        }
    }

    /**
     * Get id of the current tenant.
     *
     * @return tenant id
     * @throws DeviceManagementDAOException if an error is observed when getting tenant id
     */
    public static int getTenantId() throws DeviceManagementDAOException {
        CarbonContext context = CarbonContext.getThreadLocalCarbonContext();
        int tenantId = context.getTenantId();
        if (tenantId != MultitenantConstants.INVALID_TENANT_ID) {
            return tenantId;
        }
        String tenantDomain = context.getTenantDomain();
        if (tenantDomain == null) {
            String msg = "Tenant domain is not properly set and thus, is null";
            throw new DeviceManagementDAOException(msg);
        }
        TenantManager tenantManager = DeviceManagementDataHolder.getInstance().getTenantManager();
        try {
            tenantId = tenantManager.getTenantId(tenantDomain);
        } catch (UserStoreException e) {
            String msg = "Error occurred while retrieving id from the domain of tenant " + tenantDomain;
            throw new DeviceManagementDAOException(msg);
        }
        return tenantId;
    }

    public static DataSource lookupDataSource(String dataSourceName, final Hashtable<Object, Object> jndiProperties) {
        try {
            if (jndiProperties == null || jndiProperties.isEmpty()) {
                return (DataSource) InitialContext.doLookup(dataSourceName);
            }
            final InitialContext context = new InitialContext(jndiProperties);
            return (DataSource) context.lookup(dataSourceName);
        } catch (Exception e) {
            throw new RuntimeException("Error in looking up data source: " + e.getMessage(), e);
        }
    }

    /**
     * @param device - The DTO device object.
     * @return A Business Object.
     */
    public static org.wso2.carbon.device.mgt.common.Device convertDevice(Device device){
        org.wso2.carbon.device.mgt.common.Device deviceBO =
                new org.wso2.carbon.device.mgt.common.Device();
        deviceBO.setDateOfEnrolment(device.getDateOfEnrollment());
        deviceBO.setDateOfLastUpdate(device.getDateOfLastUpdate());
        deviceBO.setDescription(device.getDescription());
        deviceBO.setDeviceIdentifier(device.getDeviceIdentificationId());
        deviceBO.setDeviceTypeId(device.getDeviceTypeId());
        deviceBO.setName(device.getName());
        deviceBO.setId(device.getId());
        deviceBO.setOwner(device.getOwnerId());
        deviceBO.setOwnership(device.getOwnerShip());
        if (device.getStatus() == Status.ACTIVE) {
            deviceBO.setStatus(true);
        } else if (device.getStatus() == Status.INACTIVE) {
            deviceBO.setStatus(false);
        }
        return null;
    }

    /**
     * @param devices - DTO Device Object list.
     * @return  converted Business Object list.
     */
    public static List<org.wso2.carbon.device.mgt.common.Device> convertDevices(
            List<Device> devices) {
        List<org.wso2.carbon.device.mgt.common.Device> deviceBOList =
                new ArrayList<org.wso2.carbon.device.mgt.common.Device>();
        for (Device device : devices) {
            deviceBOList.add(convertDevice(device));
        }
        return deviceBOList;
    }

    public static Device convertDevice(org.wso2.carbon.device.mgt.common.Device
                                               device) throws DeviceManagementDAOException {
        Device deviceBO = new Device();
        deviceBO.setDescription(device.getDescription());
        deviceBO.setName(device.getName());
        deviceBO.setDateOfEnrollment(device.getDateOfEnrolment());
        deviceBO.setDateOfLastUpdate(device.getDateOfLastUpdate());

        if (!device.isStatus()){
            deviceBO.setStatus(Status.INACTIVE);
        }else{
            deviceBO.setStatus(Status.ACTIVE);
        }
        deviceBO.setOwnerId(device.getOwner());
        deviceBO.setOwnerShip(device.getOwnership());
        deviceBO.setTenantId(DeviceManagementDAOUtil.getTenantId());
        deviceBO.setDeviceIdentificationId(device.getDeviceIdentifier());
        return deviceBO;
    }

    public static DeviceIdentifier createDeviceIdentifier(Device device, DeviceType deviceType) {
        DeviceIdentifier deviceIdentifier = new DeviceIdentifier();
        deviceIdentifier.setType(deviceType.getName());
        deviceIdentifier.setId(device.getDeviceIdentificationId());
        return deviceIdentifier;
    }
}
