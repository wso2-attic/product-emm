/*
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
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

package org.wso2.carbon.mdm.mobileservices.windows.operations.util;

/**
 * Maps operation codes to device specific format.
 */
public class OperationCode {
    public static enum Info {
        DEV_ID("./DevInfo/DevId"),
        MANUFACTURER("./DevInfo/Man"),
        DEVICE_MODEL("./DevInfo/Mod"),
        DM_VERSION("./DevInfo/DmV"),
        LANGUAGE("./DevInfo/Lang"),
        IMSI("./Vendor/MSFT/DeviceInstanceService/Identity/Identity1/IMSI"),
        IMEI("./Vendor/MSFT/DeviceInstanceService/Identity/Identity1/IMEI"),
        SOFTWARE_VERSION("./DevDetail/SwV"),
        VENDER("./DevDetail/OEM"),
        MAC_ADDRESS("./DevDetail/Ext/WLANMACAddress"),
        RESOLUTION("./DevDetail/Ext/Microsoft/Resolution"),
        DEVICE_NAME("./DevDetail/Ext/Microsoft/DeviceName"),
        CHANNEL_URI("./Vendor/MSFT/DMClient/Provider/MobiCDMServer/Push/ChannelURI"),
        LOCK_PIN("./Vendor/MSFT/RemoteLock/NewPINValue"),
        LOCK_RESET("./Vendor/MSFT/RemoteLock/LockAndResetPIN"),
        CAMERA("./Vendor/MSFT/PolicyManager/My/Camera/AllowCamera"),
        CAMERA_STATUS("./Vendor/MSFT/PolicyManager/Device/Camera/AllowCamera"),
        ENCRYPT_STORAGE_STATUS("./Vendor/MSFT/PolicyManager/Device/Security/RequireDeviceEncryption"),
        DEVICE_PASSWORD_STATUS("./Vendor/MSFT/PolicyManager/Device/DeviceLock/DevicePasswordEnabled"),
        DEVICE_PASSCODE_DELETE("./Vendor/MSFT/PolicyManager/My/DeviceLock"),
        LONGITUDE("./Vendor/MSFT/RemoteFind/Location/Longitude"),
        LATITUDE("./Vendor/MSFT/RemoteFind/Location/Latitude");

        private final String code;

        Info(String code) {
            this.code = code;
        }

        public String getCode() {
            return this.code;
        }

    }

    public static enum Command {
        DEVICE_RING("./Vendor/MSFT/RemoteRing/Ring"),
        DEVICE_LOCK("./Vendor/MSFT/RemoteLock/Lock"),
        WIPE_DATA("./Vendor/MSFT/RemoteWipe/doWipe"),
        DISENROLL("./Vendor/MSFT/DMClient/Unenroll"),
        LOCK_RESET("./Vendor/MSFT/RemoteLock/LockAndResetPIN"),
        CAMERA("./Vendor/MSFT/PolicyManager/My/Camera/AllowCamera"),
        ENCRYPT_STORAGE("./Vendor/MSFT/PolicyManager/My/Security/RequireDeviceEncryption"),
        CAMERA_STATUS("./Vendor/MSFT/PolicyManager/Device/Camera/AllowCamera"),
        ENCRYPT_STORAGE_STATUS("./Vendor/MSFT/PolicyManager/Device/Security/RequireDeviceEncryption"),
        DEVICE_PASSWORD_ENABLE("./Vendor/MSFT/PolicyManager/My/DeviceLock/DevicePasswordEnabled");

        private final String code;

        Command(String code) {
            this.code = code;
        }

        public String getCode() {
            return this.code;
        }

    }

    public static enum Configure {
        WIFI("./Vendor/MSFT/WiFi/Profile/MyNetwork/WlanXml"),
        CAMERA("./Vendor/MSFT/PolicyManager/My/Camera/AllowCamera"),
        CAMERA_STATUS("./Vendor/MSFT/PolicyManager/Device/Camera/AllowCamera"),
        ENCRYPT_STORAGE("./Vendor/MSFT/PolicyManager/My/Security/RequireDeviceEncryption"),
        ENCRYPT_STORAGE_STATUS("./Vendor/MSFT/PolicyManager/Device/Security/RequireDeviceEncryption"),
        PASSWORD_MAX_FAIL_ATTEMPTS("./Vendor/MSFT/PolicyManager/My/DeviceLock/MaxDevicePasswordFailedAttempts"),
        DEVICE_PASSWORD_ENABLE("./Vendor/MSFT/PolicyManager/My/DeviceLock/DevicePasswordEnabled"),
        SIMPLE_PASSWORD("./Vendor/MSFT/PolicyManager/My/DeviceLock/AllowSimpleDevicePassword"),
        MIN_PASSWORD_LENGTH("./Vendor/MSFT/PolicyManager/My/DeviceLock/MinDevicePasswordLength"),
        Alphanumeric_PASSWORD("./Vendor/MSFT/PolicyManager/My/DeviceLock/AlphanumericDevicePasswordRequired"),
        PASSWORD_EXPIRE("./Vendor/MSFT/PolicyManager/My/DeviceLock/DevicePasswordExpiration"),
        PASSWORD_HISTORY("./Vendor/MSFT/PolicyManager/My/DeviceLock/DevicePasswordHistory"),
        MAX_PASSWORD_INACTIVE_TIME("./Vendor/MSFT/PolicyManager/My/DeviceLock/MaxInactivityTimeDeviceLock"),
        MIN_PASSWORD_COMPLEX_CHARACTERS("./Vendor/MSFT/PolicyManager/My/DeviceLock/MinDevicePasswordComplexCharacters");

        private final String code;

        Configure(String code) {
            this.code = code;
        }

        public String getCode() {
            return this.code;
        }

    }
}
