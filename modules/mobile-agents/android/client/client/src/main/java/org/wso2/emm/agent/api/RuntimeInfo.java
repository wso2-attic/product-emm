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

package org.wso2.emm.agent.api;

import android.app.ActivityManager;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.wso2.emm.agent.AndroidAgentException;
import org.wso2.emm.agent.beans.Application;
import org.wso2.emm.agent.beans.Device;
import org.wso2.emm.agent.utils.Constants;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RuntimeInfo {
    Context context;
    private static final String TAG = RuntimeInfo.class.getName();
    private ObjectMapper mapper;
    String[] topCommandRows;

    public RuntimeInfo(Context context) {
        this.context = context;
        mapper = new ObjectMapper();
        String resultOfTop = executeCommand(new String[]{"top", "-n", "1"});
        topCommandRows = resultOfTop.split("\n");
    }

    public RuntimeInfo(Context context, String[] command) {
        this.context = context;
        mapper = new ObjectMapper();
        String resultOfTop = executeCommand(command);
        topCommandRows = resultOfTop.split("\n");
    }

    public List<Device.Property> getCPUInfo() throws AndroidAgentException {
        List<Device.Property> properties = new ArrayList<>();
        Device.Property property;

        for (String topCommandRow : topCommandRows) {
            if (topCommandRow != null && !topCommandRow.isEmpty()) {
                String[] columns = topCommandRow.split(", ");
                for (String column : columns) {
                    String[] keyValue = column.split(" ");
                    property = new Device.Property();
                    property.setName(keyValue[0]);
                    property.setValue(keyValue[1]);
                    properties.add(property);
                }
                break;
            }
        }
        return properties;
    }

    public Map<String, Application> getAppMemory() throws AndroidAgentException {
        Map<String, Application> applications = new HashMap<>();
        Application appData;

        ActivityManager activityManager =
                (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

        for (String topCommandRow : topCommandRows) {
            if (topCommandRow != null && !topCommandRow.isEmpty()
                && !topCommandRow.contains(" root ")) {
                String[] columns = topCommandRow.replaceFirst("^\\s*", "").split(" ");
                String pidColumnValue = columns[0].trim();

                if (!pidColumnValue.isEmpty() && TextUtils.isDigitsOnly(pidColumnValue)) {

                    appData = new Application();
                    appData.setPackageName(columns[columns.length - 1]);

                    int pid = Integer.parseInt(pidColumnValue);
                    appData.setPid(Integer.parseInt(columns[0]));

                    int totalPSS = activityManager.
                            getProcessMemoryInfo(new int[]{pid})[0].getTotalPss();
                    appData.setPss(totalPSS);

                    int totalPrivateDirty = activityManager.
                            getProcessMemoryInfo(new int[]{pid})[0].getTotalPrivateDirty();
                    appData.setUss(totalPrivateDirty);

                    int totalSharedDirty = activityManager.getProcessMemoryInfo(new int[]{pid})[0].
                            getTotalSharedDirty();
                    appData.setSharedDirty(totalSharedDirty);

                    applications.put(appData.getPackageName(), appData);
                }
            }
        }
        return applications;
    }

    public Application getHighestCPU() {
        Application appData = null;

        for (String topCommandRow : topCommandRows) {
            if (topCommandRow != null && !topCommandRow.isEmpty()
                && !topCommandRow.contains(" root ")) {
                String[] columns = topCommandRow.replaceFirst("^\\s*", "").split(" ");
                String pidColumnValue = columns[0].trim();

                if (!pidColumnValue.isEmpty() && TextUtils.isDigitsOnly(pidColumnValue)) {

                    appData = new Application();
                    appData.setPackageName(columns[columns.length - 1]);
                    appData.setPid(Integer.parseInt(columns[0]));
                    for (String column : columns) {
                        if (column != null) {
                            String columnValue = column.trim();
                            if (columnValue.contains("%")) {
                                String percentage = columnValue.replace("%", "");
                                if (!percentage.isEmpty() && TextUtils.isDigitsOnly(percentage)) {
                                    appData.setCpu(Integer.parseInt(percentage));
                                }
                                break;
                            }
                        }
                    }

                }
            }
        }
        return appData;
    }

    public List<Device.Property> getRAMInfo() throws AndroidAgentException {
        List<Device.Property> properties = new ArrayList<>();
        Device.Property property;

        ActivityManager actManager =
                (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memInfo = new ActivityManager.MemoryInfo();
        actManager.getMemoryInfo(memInfo);
        long totalMemory = memInfo.totalMem;
        long availableMemory = memInfo.availMem;
        long threshold = memInfo.threshold;
        boolean lowMemory = memInfo.lowMemory;
        // The available memory on the system.
        property = new Device.Property();
        property.setName(Constants.Device.TOTAL_MEMORY);
        property.setValue(String.valueOf(totalMemory));
        properties.add(property);
        // The total memory accessible by the kernel.
        property = new Device.Property();
        property.setName(Constants.Device.AVAILABLE_MEMORY);
        property.setValue(String.valueOf(availableMemory));
        properties.add(property);
        // The threshold of availMem at which we consider memory to be low and start
        // killing background services and other non-extraneous processes.
        property = new Device.Property();
        property.setName(Constants.Device.THRESHOLD);
        property.setValue(String.valueOf(threshold));
        properties.add(property);
        // Set to true if the system considers itself to currently be in a low memory situation.
        property = new Device.Property();
        property.setName(Constants.Device.LOW_MEMORY);
        property.setValue(String.valueOf(lowMemory));
        properties.add(property);

        return properties;
    }


    public String executeCommand(String[] commands) {
        Process p;
        StringBuilder output = new StringBuilder();
        try {
            p = Runtime.getRuntime().exec(commands);
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(p.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
                p.waitFor();
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return output.toString();
    }
}
