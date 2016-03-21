/*
 * Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
package org.wso2.emm.agent.interfaces;

import org.wso2.emm.agent.AndroidAgentException;

/**
 * This interface represents all the Operations that can be done through EMM Agent
 */
public interface VersionBasedOperations {

    void wipeDevice(org.wso2.emm.agent.beans.Operation operation) throws AndroidAgentException;

    /**
     * Clear device password.
     *
     * @param operation - Operation object.
     */
    void clearPassword(org.wso2.emm.agent.beans.Operation operation);

    /**
     * Display notification.
     *
     * @param operation - Operation object.
     */
    void displayNotification(org.wso2.emm.agent.beans.Operation operation) throws AndroidAgentException;

    /**
     * Configure device WIFI profile.
     *
     * @param operation - Operation object.
     */
    void configureWifi(org.wso2.emm.agent.beans.Operation operation) throws AndroidAgentException;

    /**
     * Disable/Enable device camera.
     *
     * @param operation - Operation object.
     */
    void disableCamera(org.wso2.emm.agent.beans.Operation operation) throws AndroidAgentException;

    /**
     * Install application/bundle.
     *
     * @param operation - Operation object.
     */
    void installAppBundle(org.wso2.emm.agent.beans.Operation operation) throws AndroidAgentException;

    /**
     * Uninstall application.
     *
     * @param operation - Operation object.
     */
    void uninstallApplication(org.wso2.emm.agent.beans.Operation operation) throws AndroidAgentException;

    /**
     * Encrypt/Decrypt device storage.
     *
     * @param operation - Operation object.
     */
    void encryptStorage(org.wso2.emm.agent.beans.Operation operation) throws AndroidAgentException;

    /**
     * Ring the device.
     *
     * @param operation - Operation object.
     */
    void ringDevice(org.wso2.emm.agent.beans.Operation operation);

    /**
     * Mute the device.
     *
     * @param operation - Operation object.
     */
    void muteDevice(org.wso2.emm.agent.beans.Operation operation);

    /**
     * Create web clip (Web app shortcut on device home screen).
     *
     * @param operation - Operation object.
     */
    void manageWebClip(org.wso2.emm.agent.beans.Operation operation) throws AndroidAgentException;

    /**
     * Set device password policy.
     *
     * @param operation - Operation object.
     */
    void setPasswordPolicy(org.wso2.emm.agent.beans.Operation operation) throws AndroidAgentException;

    /**
     * Install google play applications.
     *
     * @param operation - Operation object.
     */
    void installGooglePlayApp(org.wso2.emm.agent.beans.Operation operation) throws AndroidAgentException;

    /**
     * Open Google Play store application with an application given.
     *
     * @param packageName - Application package name.
     */
    void triggerGooglePlayApp(String packageName);

    /**
     * Change device lock code.
     *
     * @param operation - Operation object.
     */
    void changeLockCode(org.wso2.emm.agent.beans.Operation operation) throws AndroidAgentException;

    /**
     * Set policy bundle.
     *
     * @param operation - Operation object.
     */
    void setPolicyBundle(org.wso2.emm.agent.beans.Operation operation) throws AndroidAgentException;

    /**
     * Monitor currently enforced policy for compliance.
     *
     * @param operation - Operation object.
     */
    void monitorPolicy(org.wso2.emm.agent.beans.Operation operation) throws AndroidAgentException;

    /**
     * Revoke currently enforced policy.
     *
     * @param operation - Operation object.
     */
    void revokePolicy(org.wso2.emm.agent.beans.Operation operation) throws AndroidAgentException;

    /**
     * Enterprise wipe the device.
     *
     * @param operation - Operation object.
     */
    void enterpriseWipe(org.wso2.emm.agent.beans.Operation operation) throws AndroidAgentException;

    /**
     * Blacklisting apps.
     *
     * @param operation - Operation object.
     */
    void blacklistApps(org.wso2.emm.agent.beans.Operation operation) throws AndroidAgentException;

    /**
     * Disenroll the device from EMM.
     */
    void disenrollDevice(org.wso2.emm.agent.beans.Operation operation);

    /**
     * Upgrading device firmware from the configured OTA server.
     *
     * @param operation - Operation object.
     */
    void upgradeFirmware(org.wso2.emm.agent.beans.Operation operation) throws AndroidAgentException;

    /**
     * Reboot the device [System app required].
     *
     * @param operation - Operation object.
     */
    void rebootDevice(org.wso2.emm.agent.beans.Operation operation) throws AndroidAgentException;

    /**
     * Execute shell commands as the super user.
     *
     * @param operation - Operation object.
     */
    void executeShellCommand(org.wso2.emm.agent.beans.Operation operation) throws AndroidAgentException;

}