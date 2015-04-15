/*
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * you may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.mdm.services.android.util;

import org.wso2.carbon.device.mgt.common.DeviceIdentifier;

import java.util.List;

/**
 * Holder class for storing valid & invalid device-ids.
 */
public class DeviceIDHolder {

	private List<String> errorDeviceIdList;
	private List<DeviceIdentifier> validDeviceIDList;

	public List<String> getErrorDeviceIdList() {
		return errorDeviceIdList;
	}

	public void setErrorDeviceIdList(List<String> errorDeviceIdList) {
		this.errorDeviceIdList = errorDeviceIdList;
	}

	public List<DeviceIdentifier> getValidDeviceIDList() {
		return validDeviceIDList;
	}

	public void setValidDeviceIDList(List<DeviceIdentifier> validDeviceIDList) {
		this.validDeviceIDList = validDeviceIDList;
	}
}
