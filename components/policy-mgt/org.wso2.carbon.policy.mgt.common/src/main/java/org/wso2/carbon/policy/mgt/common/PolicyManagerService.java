/*
 * Copyright (c) 2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.wso2.carbon.policy.mgt.common;

/**
 * This interface defines the policy management which should be implemented by the plugins
 */

public interface PolicyManagerService {

    /**
     * This method adds a policy to the platform
     *
     * @param policy
     * @return primary key (generated key)
     */

    int addPolicy(Policy policy);

    /**
     * This method adds a policy per device which should be implemented by the related plugins.
     *
     * @param deviceId
     * @param deviceType
     * @param policy
     * @return primary key (generated key)
     */

    int addPolicyToDevice(String deviceId, String deviceType, Policy policy) throws FeatureManagementException, PolicyManagementException;

    /**
     * This method adds a policy to device type by the related device type plugins.
     *
     * @param deviceType
     * @param policy
     * @return primary key (generated key)
     */

    int addPolicyToDeviceType(String deviceType, Policy policy) throws FeatureManagementException, PolicyManagementException;

    /**
     * This method adds the policy to specific role.
     *
     * @param roleName
     * @param policy
     * @return primary key (generated key)
     */
    int addPolicyToRole(String roleName, Policy policy) throws FeatureManagementException, PolicyManagementException;

    /**
     * This method returns the policy of whole platform
     *
     * @return
     */

    Policy getPolicy();

    /**
     * This method gives the device specific policy.
     *
     * @param deviceId
     * @param deviceType
     * @return Policy
     */

    Policy getPolicyOfDevice(String deviceId, String deviceType) throws FeatureManagementException, PolicyManagementException;

    /**
     * This method returns the device type specific policy.
     *
     * @param deviceType
     * @return Policy
     */

    Policy getPolicyOfDeviceType(String deviceType) throws FeatureManagementException, PolicyManagementException;

    /**
     * This method returns the role specific policy.
     *
     * @param roleName
     * @return
     */

    Policy getPolicyOfRole(String roleName) throws FeatureManagementException, PolicyManagementException;


    boolean isPolicyAvailableForDevice(String deviceId, String deviceType) throws PolicyManagementException;


    boolean isPolicyUsed(String deviceId, String deviceType) throws PolicyManagementException;


    void setPolicyUsed(String deviceId, String deviceType, Policy policy) throws PolicyManagementException;
}
