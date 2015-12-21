package org.wso2.mdm.appmgt.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.appmgt.mobile.interfaces.MDMOperations;
import org.wso2.carbon.appmgt.mobile.mdm.App;
import org.wso2.carbon.appmgt.mobile.mdm.Device;
import org.wso2.carbon.appmgt.mobile.utils.User;
import org.wso2.carbon.device.mgt.common.DeviceIdentifier;
import org.wso2.carbon.device.mgt.common.DeviceManagementException;
import org.wso2.carbon.device.mgt.common.Platform;
import org.wso2.carbon.device.mgt.common.app.mgt.ApplicationManagementException;
import org.wso2.carbon.device.mgt.common.app.mgt.ApplicationManager;
import org.wso2.mdm.mdmmgt.beans.MobileApp;
import org.wso2.mdm.mdmmgt.beans.MobileAppTypes;
import org.wso2.mdm.mdmmgt.common.MDMException;
import org.wso2.mdm.mdmmgt.util.MDMAndroidOperationUtil;
import org.wso2.mdm.mdmmgt.util.MDMIOSOperationUtil;
import org.wso2.mdm.mdmmgt.util.MDMServiceAPIUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

public class MDMOperationsImpl implements MDMOperations {

    private static Log log = LogFactory.getLog(MDMOperationsImpl.class);

    @Override
    public void performAction(User currentUser, String action, App app, int tenantId, String type, String[] params,
            HashMap<String, String> configProperties) {

        ApplicationManager appManagerConnector;
        org.wso2.carbon.device.mgt.common.operation.mgt.Operation operation = null;

        List<String> userNameList;
        List<String> roleNameList;
        List<DeviceIdentifier> deviceIdentifiers = new ArrayList<>();
        List<org.wso2.carbon.device.mgt.common.Device> deviceList = null;

        if ("user".equals(type)) {
            String userName = null;
            for (String param : params) {
                userName = param;

                try {
                    deviceList = MDMServiceAPIUtils.getDeviceManagementService(tenantId).getDevicesOfUser(userName);
                } catch (DeviceManagementException devEx) {
                    String errorMsg =
                            "Error occurred fetch device for user " + userName + devEx.getErrorMessage() + " " +
                                    "at app installation";
                    log.error(errorMsg, devEx);
                }

                for (org.wso2.carbon.device.mgt.common.Device device : deviceList) {
                    DeviceIdentifier deviceIdentifier = new DeviceIdentifier();
                    deviceIdentifier.setId(device.getDeviceIdentifier());
                    deviceIdentifier.setType(device.getType());

                    deviceIdentifiers.add(deviceIdentifier);
                }
            }
        } else if ("role".equals(type)) {
            String userRole;
            for (String param : params) {
                userRole = param;
                try {
                    deviceList = MDMServiceAPIUtils.getDeviceManagementService(tenantId).getAllDevices();
                } catch (DeviceManagementException devMgtEx) {
                    String errorMsg = "Error occurred fetch device for user role " + userRole + devMgtEx
                            .getErrorMessage() + " " +
                            "at app installation";
                    log.error(errorMsg, devMgtEx);
                }
                for (org.wso2.carbon.device.mgt.common.Device device : deviceList) {
                    DeviceIdentifier deviceIdentifier = new DeviceIdentifier();
                    deviceIdentifier.setId(device.getDeviceIdentifier());
                    deviceIdentifier.setType(device.getType());
                    deviceIdentifiers.add(deviceIdentifier);
                }
            }
        } else {
            deviceIdentifiers = new ArrayList<>();
            DeviceIdentifier deviceIdentifier = null;
            for (String param : params) {
                deviceIdentifier = new DeviceIdentifier();
                String[] paramDevices = param.split("---");
                deviceIdentifier.setId(paramDevices[0]);
                deviceIdentifier.setType(paramDevices[1]);
                deviceIdentifiers.add(deviceIdentifier);
            }

        }
        MobileApp mobileApp = new MobileApp();
        mobileApp.setId(app.getId());
        mobileApp.setType(MobileAppTypes.valueOf(app.getType().toUpperCase()));
        mobileApp.setAppIdentifier(app.getAppIdentifier());
        mobileApp.setIconImage(app.getIconImage());
        mobileApp.setIdentifier(app.getIdentifier());
        mobileApp.setLocation(app.getLocation());
        mobileApp.setName(app.getName());
        mobileApp.setPackageName(app.getPackageName());
        mobileApp.setPlatform(app.getPlatform());
        mobileApp.setVersion(app.getVersion());

        Properties properties = new Properties();

        if ("ios".equals(app.getPlatform())) {
            if ("enterprise".equals(app.getType())) {
                properties.put("isRemoveApp", true);
                properties.put("isPreventBackup", true);
            } else if ("public".equals(app.getType())) {
                properties.put("iTunesId", Integer.parseInt(app.getIdentifier().toString()));
                properties.put("isRemoveApp", true);
                properties.put("isPreventBackup", true);
            } else if ("webapp".equals(app.getType())) {
                properties.put("label", app.getName());
                properties.put("isRemoveApp", true);
            }
        } else if ("webapp".equals(app.getPlatform())) {
            properties.put("label", app.getName());
            properties.put("isRemoveApp", true);
        }
        mobileApp.setProperties(properties);

        try {
            if (deviceIdentifiers != null) {
                for (DeviceIdentifier deviceIdentifier : deviceIdentifiers) {
                    if (deviceIdentifier.getType().equals(Platform.android.toString())) {
                        if ("install".equals(action)) {
                            operation = MDMAndroidOperationUtil.createInstallAppOperation(mobileApp);
                        } else {
                            operation = MDMAndroidOperationUtil.createAppUninstallOperation(mobileApp);
                        }
                    } else if (deviceIdentifier.getType().equals(Platform.ios.toString())) {
                        if ("install".equals(action)) {
                            operation = MDMIOSOperationUtil.createInstallAppOperation(mobileApp);
                        } else {
                            operation = MDMIOSOperationUtil.createAppUninstallOperation(mobileApp);
                        }
                    }
                    MDMServiceAPIUtils.getAppManagementService(tenantId).installApplicationForDevices(operation,
                            deviceIdentifiers);
                }
            }
        } catch (MDMException mdmExce) {
            log.error("Error in creating operation object using app", mdmExce);
        } catch (ApplicationManagementException appMgtExce) {
            log.error("Error in app installation", appMgtExce);
        }
    }

    @Override

    public List<Device> getDevices(User currentUser, int tenantId, String type, String[] params, String platform,
            String platformVersion, boolean isSampleDevicesEnabled,
            HashMap<String, String> configProperties) {

        List<Device> devices = null;
        Device device = null;
        try {
            List<org.wso2.carbon.device.mgt.common.Device> deviceList =
                    MDMServiceAPIUtils.getDeviceManagementService(tenantId).getAllDevices();
            devices = new ArrayList<>(deviceList.size());
            for (org.wso2.carbon.device.mgt.common.Device commondevice : deviceList) {
                device = new Device();
                device.setId(commondevice.getDeviceIdentifier() + "---" + commondevice.getType());
                device.setName(commondevice.getName());
                device.setModel(commondevice.getName());
                device.setType("mobileDevice");
                device.setImage("/store/extensions/assets/mobileapp/resources/models/none.png");
                device.setPlatform(commondevice.getType());
                devices.add(device);

            }

        } catch (DeviceManagementException e) {
            e.printStackTrace();
        }
        return devices;
    }
}
