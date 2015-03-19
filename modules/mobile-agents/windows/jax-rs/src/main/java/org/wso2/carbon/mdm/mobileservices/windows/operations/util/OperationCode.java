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
		SOFTWARE_VERSION("./DevDetail/SwV");

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
}
