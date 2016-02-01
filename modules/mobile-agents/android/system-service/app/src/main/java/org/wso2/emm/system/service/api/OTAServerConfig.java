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

package org.wso2.emm.system.service.api;

import android.util.Log;
import org.wso2.emm.system.service.utils.Constants;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;

/**
 * This class handles the functionality required for setting OTA update server
 * configurations. On failure it sets default parameters set by the developer.
 */
public class OTAServerConfig {

    private static final String PROTOCOL_TAG = "protocol";
    private static final String SERVER_IP_CONFIG = "server";
    private static final String PORT_CONFIG_STR = "port";
    private static final String BUILD_TAG = "build";
    private static final String OTA_TAG = "ota";
    private static final String MONTHLY_TAG = "monthly";
    private static final String TAG = "OTA_SC";
    private static final long DEFAULT_DELAY = 2592000000L;
    private long delay;
    private URL updatePackageURL;
    private URL buildPropURL;

    public OTAServerConfig(String productname) throws MalformedURLException {

        if (loadConfigureFromFile(Constants.OTA_CONFIG_LOCATION, productname) == false) {
            Log.i(TAG, "Loading default configuration for product " + productname + ".");
            defaultConfigure(productname);
        }
    }

    public long getDelay() {
        return delay;
    }

    public boolean loadConfigureFromFile(String configFile, String product) {
        try {
            Log.d(TAG, "Loading configuration from file " +
                       configFile + " for product " + product);
            BuildPropParser parser = new BuildPropParser(new File(configFile), null);
            String protocol = parser.getProp(PROTOCOL_TAG);

            /* Retrieving properties needed to build URLs*/
            if (protocol == null) {
                Log.i(TAG, "Using default protocol " + Constants.DEFAULT_OTA_SERVER_PROTOCOL);
                protocol = Constants.DEFAULT_OTA_SERVER_PROTOCOL;
            }
            String server = parser.getProp(SERVER_IP_CONFIG);
            if (server == null) {
                Log.i(TAG, "Using default server " + Constants.DEFAULT_OTA_SERVER_ADDRESS);
                server = Constants.DEFAULT_OTA_SERVER_ADDRESS;
            }
            String portConfig = parser.getProp(PORT_CONFIG_STR);
            int port;
            if (portConfig != null) {
                port = new Long(portConfig).intValue();
            } else {
                Log.i(TAG, "Using default port " + Constants.DEFAULT_OTA_SERVER_PORT);
                port = Constants.DEFAULT_OTA_SERVER_PORT;
            }
            String updateFileName = parser.getProp(OTA_TAG);
            if (updateFileName == null) {
                Log.i(TAG, "Using default OTA suffix " + Constants.DEFAULT_OTA_ZIP_FILE);
                updateFileName = Constants.DEFAULT_OTA_ZIP_FILE;
            }
            String buildFile = parser.getProp(BUILD_TAG);
            if (buildFile == null) {
                Log.i(TAG, "Using default build config suffix " +
                           Constants.DEFAULT_OTA_BUILD_PROP_FILE);
                buildFile = Constants.DEFAULT_OTA_BUILD_PROP_FILE;
            }
            String buildMonthlyCheck = parser.getProp(MONTHLY_TAG);

            String fileAddress = new String(product + "/" + product + updateFileName);
            String buildConfigAddress = new String(product + "/" + buildFile);

            // Supported url protocols are ftp, http, https, jar, file
            updatePackageURL = new URL(protocol, server, port, fileAddress);
            buildPropURL = new URL(protocol, server, port, buildConfigAddress);

            Log.d(TAG, "Package is at URL: " + updatePackageURL);
            Log.d(TAG, "Build Property is at URL: " + buildPropURL);

            if (buildMonthlyCheck != null) {
                Calendar calendar = Calendar.getInstance();
                long checkTime = Long.parseLong(buildMonthlyCheck);
                delay = checkTime - calendar.getTimeInMillis();
                if (delay <= 0) {
                    // Determine next 30 day delay if original value expired
                    delay = DEFAULT_DELAY;
                    parser.setProp(MONTHLY_TAG, Long.toString(delay));
                }
            } else {
                delay = Long.MIN_VALUE;
            }

        } catch (IOException ie) {
            Log.e(TAG,
                  "Build property file does not meet required specification."
                  + ie);
            return false;
        }

        return true;
    }

    public void defaultConfigure(String product) throws MalformedURLException {
        String fileAddress = new String(product + "/" + product + Constants.DEFAULT_OTA_ZIP_FILE);
        String buildConfigAddress = new String(product + "/" + Constants.DEFAULT_OTA_BUILD_PROP_FILE);
        updatePackageURL = new URL(Constants.DEFAULT_OTA_SERVER_PROTOCOL, Constants.DEFAULT_OTA_SERVER_ADDRESS,
                                   Constants.DEFAULT_OTA_SERVER_PORT, fileAddress);
        buildPropURL = new URL(Constants.DEFAULT_OTA_SERVER_PROTOCOL, Constants.DEFAULT_OTA_SERVER_ADDRESS, Constants.
                DEFAULT_OTA_SERVER_PORT, buildConfigAddress);
        Log.d(TAG, "create a new server config: package url " + updatePackageURL.toString() + ":" +
                   updatePackageURL.getPort());
        Log.d(TAG, "build.prop URL:" + buildPropURL.toString());
    }

    public URL getPackageURL() {
        return updatePackageURL;
    }

    public URL getBuildPropURL() {
        return buildPropURL;
    }

}
