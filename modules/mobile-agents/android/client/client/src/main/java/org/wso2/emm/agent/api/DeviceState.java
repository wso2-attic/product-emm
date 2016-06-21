/*
 * Copyright (c) 2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
package org.wso2.emm.agent.api;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import org.wso2.emm.agent.beans.Power;
import org.wso2.emm.agent.utils.Response;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * This class represents all the functionalities related to the retrieval of
 * the device state (battery, memory status etc).
 */
public class DeviceState {
    private static final String GOOD_CONDITION = "GOOD_CONDITION";
    public static final String DEAD = "DEAD";
    public static final String OVER_VOLTAGE = "OVER_VOLTAGE";
    public static final String OVER_HEAT = "OVER_HEAT";
    public static final String FAILURE = "FAILURE";
    public static final String UNKNOWN = "UNKNOWN";
    public static final String CHARGING = "CHARGING";
    public static final String DISCHARGING = "DISCHARGING";
    public static final String FULL = "FULL";
    public static final String NOT_CHARGING = "NOT_CHARGING";
    public static final String WIRELESS = "WIRELESS";
    public static final String USB = "USB";
    public static final String AC = "AC";
    public static final String TECHNOLOGY = "TECHNOLOGY";
    public static final String VOLTAGE = "BATTERY_VOLTAGE";
    public static final String TEMPERATURE = "TEMPERATURE";
    public static final String CURRENT_AVG = "CURRENT_AVG";
    public static final String HEALTH = "HEALTH";
    public static final String STATUS = "STATUS";
    public static final String PLUGGED = "PLUGGED";
    public static final String PRESENT = "PRESENT";
    public static final String COLD = "COLD";
    private Context context;
    private DeviceInfo info;
    private File dataDirectory;
    private File[] externalStorageDirectoryList;
    private static final int DEFAULT_LEVEL = -1;
    private static final int SCALE = 2;
    private static final int MEMORY_NOT_AVAILABLE = 0;
    private static final double GB_DIVIDER = 1073741824;
    private static final String INTERNAL_STORAGE_PATH = "storage/emulated/0";

    public DeviceState(Context context) {
        this.context = context;
        this.info = new DeviceInfo(context);
        this.dataDirectory = new File(context.getFilesDir().getAbsoluteFile().toString());
        if (externalMemoryAvailable()) {
            this.externalStorageDirectoryList = ContextCompat.getExternalFilesDirs(context, null);
        }
    }

    /**
     * Returns whether the external memory is available or not.
     *
     * @return - External memory status.
     */
    public boolean externalMemoryAvailable() {
        return android.os.Environment.getExternalStorageState()
                .equals(android.os.Environment.MEDIA_MOUNTED);
    }

    /**
     * Returns the available internal memory size.
     *
     * @return - Available internal memory size.
     */
    public double getAvailableInternalMemorySize() {
        long freeBytesInternal = dataDirectory.getFreeSpace();
        return formatSizeInGb(freeBytesInternal);
    }

    /**
     * Returns the total internal memory size.
     *
     * @return - Total internal memory size.
     */
    public double getTotalInternalMemorySize() {
        long totalBytesInternal = dataDirectory.getTotalSpace();
        return formatSizeInGb(totalBytesInternal);
    }

    /**
     * Returns the available external memory size.
     *
     * @return - Available external memory size.
     */
    public double getAvailableExternalMemorySize() {
        long freeBytesExternal = 0;
        if (externalMemoryAvailable()) {
            for (File dir : externalStorageDirectoryList) {
                if (dir != null && !dir.getAbsolutePath().contains(INTERNAL_STORAGE_PATH)) {
                    freeBytesExternal += dir.getFreeSpace();
                }
            }
            return formatSizeInGb(freeBytesExternal);
        } else {
            return MEMORY_NOT_AVAILABLE;
        }
    }

    /**
     * Returns the total external memory size.
     *
     * @return - Total external memory size.
     */
    public double getTotalExternalMemorySize() {
        long totalBytesExternal = 0;
        if (externalMemoryAvailable()) {
            for (File dir : externalStorageDirectoryList) {
                if (dir != null && !dir.getAbsolutePath().contains(INTERNAL_STORAGE_PATH)) {
                    totalBytesExternal += dir.getTotalSpace();
                }
            }
            return formatSizeInGb(totalBytesExternal);
        } else {
            return MEMORY_NOT_AVAILABLE;
        }
    }

    /**
     * Returns the string formatted value for the size.
     *
     * @param byteValue - Memory in bytes.
     * @return - Memory formatted into GB.
     */
    public double formatSizeInGb(double byteValue) {
        double gbValue = (byteValue / GB_DIVIDER);
        BigDecimal roundedValue = new BigDecimal(gbValue).setScale(SCALE, RoundingMode.HALF_EVEN);
        gbValue = roundedValue.doubleValue();

        return gbValue;
    }

    /**
     * Returns true if the device is compatible to run the agent.
     *
     * @return - Device compatibility status.
     */
    public Response evaluateCompatibility() {
        if (!(info.getSdkVersion() >= android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) &&
            info.isRooted()) {
            return Response.INCOMPATIBLE;
        } else if (info.getSdkVersion() < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            return Response.INCOMPATIBLE_OS;
        } else if (info.isRooted()) {
            return Response.INCOMPATIBLE_ROOT;
        }
        return Response.COMPATIBLE;
    }

    /**
     * Returns true if the device is compatible to Android For Work (Managed Profile Feature).
     *
     * @return - Device AndroidForWork-Compatibility status
     */
    public Response evaluateAndroidForWorkCompatibility(){
        if (info.getSdkVersion() >= Build.VERSION_CODES.LOLLIPOP){
            return Response.ANDROID_FOR_WORK_COMPATIBLE;
        }else
            return Response.ANDROID_FOR_WORK_INCOMPATIBLE;
    }

    /**
     * Returns the device battery information.
     *
     * @return Battery object representing battery data.
     */
    public Power getBatteryDetails() {
        Power power = new Power();
        Intent batteryIntent = context.registerReceiver(null,
                                                        new IntentFilter(
                                                                Intent.ACTION_BATTERY_CHANGED));
        int level = 0;
        int scale = 0;
        Bundle bundle = null;
        boolean isPresent = false;
        if (batteryIntent != null) {
            level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, DEFAULT_LEVEL);
            scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, DEFAULT_LEVEL);
            isPresent = batteryIntent.getBooleanExtra(PRESENT, false);
            bundle = batteryIntent.getExtras();
        }

        power.setLevel(level);
        power.setScale(scale);
        if (isPresent) {
            power.setTechnology(bundle.getString(TECHNOLOGY));
            power.setVoltage(bundle.getInt(VOLTAGE));
            power.setTemperature(bundle.getInt(TEMPERATURE));
            //Average battery current in micro-amperes, as an integer.
            power.setCurrentAverage(bundle.getInt(CURRENT_AVG));
            power.setHealth(getHealth(bundle.getInt(HEALTH)));
            power.setStatus(getStatus(bundle.getInt(STATUS)));
            power.setPlugged(getPlugType(bundle.getInt(PLUGGED)));

        }

        return power;
    }

    /**
     * Conversion from plugged type int to String can be done through this method.
     *
     * @param plugged integer representing the plugged type.
     * @return String representing the plugged type.
     */
    private String getPlugType(int plugged) {
        String plugType = UNKNOWN;
        switch (plugged) {
            case BatteryManager.BATTERY_PLUGGED_AC:
                plugType = AC;
                break;
            case BatteryManager.BATTERY_PLUGGED_USB:
                plugType = USB;
                break;
            case BatteryManager.BATTERY_PLUGGED_WIRELESS:
                plugType = WIRELESS;
                break;
        }
        return plugType;
    }

    /**
     * Conversion from health condition int to String can be done through this method.
     *
     * @param health integer representing the health condition.
     * @return String representing the health condition.
     */
    private String getHealth(int health) {
        String healthString = UNKNOWN;
        switch (health) {
            case BatteryManager.BATTERY_HEALTH_COLD:
                healthString = COLD;
                break;
            case BatteryManager.BATTERY_HEALTH_DEAD:
                healthString = DEAD;
                break;
            case BatteryManager.BATTERY_HEALTH_GOOD:
                healthString = GOOD_CONDITION;
                break;
            case BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE:
                healthString = OVER_VOLTAGE;
                break;
            case BatteryManager.BATTERY_HEALTH_OVERHEAT:
                healthString = OVER_HEAT;
                break;
            case BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE:
                healthString = FAILURE;
                break;
        }
        return healthString;
    }

    /**
     * Conversion from charging status int to String can be done through this method.
     *
     * @param status integer representing the charging status.
     * @return String representing the charging status.
     */
    private String getStatus(int status) {
        String statusString = UNKNOWN;
        switch (status) {
            case BatteryManager.BATTERY_STATUS_CHARGING:
                statusString = CHARGING;
                break;
            case BatteryManager.BATTERY_STATUS_DISCHARGING:
                statusString = DISCHARGING;
                break;
            case BatteryManager.BATTERY_STATUS_FULL:
                statusString = FULL;
                break;
            case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
                statusString = NOT_CHARGING;
                break;
        }
        return statusString;
    }
}
