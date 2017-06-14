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

package org.wso2.emm.agent.events.listeners;

/**
 * This is used to define any new events that needs to be captured and sent to server.
 */
public interface AlertEventListener {

    /**
     * This can be used to start listening to a specific broadcast receiver.
     * Another usage would be, when there is an event that doesn't do a broadcast. For example
     * Application exceeding 75% of CPU is not broadcasted by default from the Android OS. Only way
     * to catch it is by constantly polling a specific API and check for the status. In such a
     * situation, AlarmManager can call startListening on it onReceiver method to do the polling on
     * an API.
     */
    void startListening();

    /**
     * If in case, listening to a specific receiver need to be done here. This can be a place to,
     * stop an AlarmManager.
     */
    void stopListening();

    /**
     * This is where publishing data to EMM/DAS would happen. This can ideally be called from
     * an onReceive method of a BroadcastReceiver, or from startListening method to inform the
     * results of a polling.
     *
     * @param payload JSON string payload to be published.
     * @param type type of the alert being published.
     */
    void publishEvent(String payload, String type);

}
