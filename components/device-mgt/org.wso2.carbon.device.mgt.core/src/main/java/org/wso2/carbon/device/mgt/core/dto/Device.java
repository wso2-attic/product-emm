/*
 * Copyright (c) 2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.device.mgt.core.dto;

import java.io.Serializable;

public class Device implements Serializable {

    private static final long serialVersionUID = -8101106997837486245L;
    private Integer id;
    private String description;
    private String name;
    private Long dateOfEnrollment;
    private Long dateOfLastUpdate;
    private String deviceIdentificationId;
    private Status status;
    private String ownerId;
    private String ownerShip;
    private int tenantId;
    private Integer deviceTypeId;

    public Integer getDeviceTypeId() {
        return deviceTypeId;
    }

    public void setDeviceTypeId(Integer deviceTypeId) {
        this.deviceTypeId = deviceTypeId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getDateOfEnrollment() {
        return dateOfEnrollment;
    }

    public void setDateOfEnrollment(Long dateOfEnrollment) {
        this.dateOfEnrollment = dateOfEnrollment;
    }

    public Long getDateOfLastUpdate() {
        return dateOfLastUpdate;
    }

    public void setDateOfLastUpdate(Long dateOfLastUpdate) {
        this.dateOfLastUpdate = dateOfLastUpdate;
    }

    public String getDeviceIdentificationId() {
        return deviceIdentificationId;
    }

    public void setDeviceIdentificationId(String deviceIdentificationId) {
        this.deviceIdentificationId = deviceIdentificationId;
    }

    public void setOwnerShip(String ownerShip) {
        this.ownerShip = ownerShip;
    }

    public String getOwnerShip() {
        return ownerShip;
    }

    public Status getStatus() {
        return status;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public int getTenantId() {
        return tenantId;
    }

    public void setTenantId(int tenantId) {
        this.tenantId = tenantId;
    }
}
