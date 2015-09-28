package org.wso2.carbon.mdm.mobileservices.windows.operations.util;

/**
 * Maps operation codes to device specific format.
 */
public class OperationCode {
    public static enum Info {
        DEV_ID("./DevInfo/DevId"),
        MANUFACTURER("./DevInfo/Man"),
        MODEL("./DevInfo/Mod"),
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
        LOCKRESET("./Vendor/MSFT/RemoteLock/LockAndResetPIN"),
        CAMERA("./Vendor/MSFT/PolicyManager/Device/Camera/AllowCamera");

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
        LOCKRESET("./Vendor/MSFT/RemoteLock/LockAndResetPIN"),
        CAMERA("./Vendor/MSFT/PolicyManager/My/Camera/AllowCamera"),
        CAMERA_STATUS("./Vendor/MSFT/PolicyManager/Device/Camera/AllowCamera"),
        ENCRYPT_STORAGE("./Vendor/MSFT/PolicyManager/My/Security/RequireDeviceEncryption"),
        ENCRYPT_STORAGE_STATUS("./Vendor/MSFT/PolicyManager/Device/Security/RequireDeviceEncryption");

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
        PASSWORD_MAX_FAIL_ATTEMPTS("./Vendor/MSFT/DeviceLock/Provider/MobiCDMServer/MaxDevicePasswordFailedAttempts"),
        DEVICE_PASSWORD_ENABLE("./Vendor/MSFT/DeviceLock/Provider/MobiCDMServer/DevicePasswordEnabled"),
        SIMPLE_PASSWORD("./Vendor/MSFT/DeviceLock/Provider/MobiCDMServer/AllowSimpleDevicePassword"),
        MIN_PASSWORD_LENGTH("./Vendor/MSFT/DeviceLock/Provider/MobiCDMServer/MinDevicePasswordLength"),
        Alphanumeric_PASSWORD("./Vendor/MSFT/DeviceLock/Provider/MobiCDMServer/AlphanumericDevicePasswordRequired"),
        PASSWORD_EXPIRE("./Vendor/MSFT/DeviceLock/Provider/MobiCDMServer/DevicePasswordExpiration"),
        PASSWORD_HISTORY("./Vendor/MSFT/DeviceLock/Provider/MobiCDMServer/DevicePasswordHistory"),
        MAX_PASSWORD_INACTIVE_TIME("./Vendor/MSFT/DeviceLock/Provider/MobiCDMServer/MaxInactivityTimeDeviceLock"),
        MIN_PASSWORD_COMPLEX_CHARACTERS("./Vendor/MSFT/DeviceLock/Provider/MobiCDMServer/MinDevicePasswordComplexCharacters");

        private final String code;

        Configure(String code) {
            this.code = code;
        }

        public String getCode() {
            return this.code;
        }

    }
}
