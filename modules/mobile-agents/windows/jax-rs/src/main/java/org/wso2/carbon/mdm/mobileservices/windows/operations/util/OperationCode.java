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
		CHANNEL_URI("./Vendor/MSFT/DMClient/Provider/MobiCDMServer/Push/ChannelURI");


		private final String code;

		Info(String code) {
			this.code = code;
		}

		public String getCode() {
			return this.code;
		}

	}

	public static enum Command {
		RING("./Vendor/MSFT/RemoteRing/Ring"),
		RING2("./Vendor/MSFT/RemoteRing/Ring");

		private final String code;

		Command(String code) {
			this.code = code;
		}

		public String getCode() {
			return this.code;
		}

	}

	public static enum Configure {
		WIFI("./Vendor/MSFT/WiFi/Profile/MyNetwork/WlanXml");

		private final String code;

		Configure(String code) {
			this.code = code;
		}

		public String getCode() {
			return this.code;
		}

	}
}
