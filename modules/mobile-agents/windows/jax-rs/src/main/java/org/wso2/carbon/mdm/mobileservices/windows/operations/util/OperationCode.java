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
        CAMERA("./Vendor/MSFT/PolicyManager/Device/Camera/AllowCamera");


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
        CAMERA("./Vendor/MSFT/PolicyManager/My/Camera/AllowCamera");


        private final String code;

        Configure(String code) {
            this.code = code;
        }

        public String getCode() {
            return this.code;
        }

    }
}
