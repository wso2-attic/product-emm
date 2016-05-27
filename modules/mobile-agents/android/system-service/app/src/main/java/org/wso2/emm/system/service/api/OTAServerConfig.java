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

import android.content.Context;
import android.util.Log;
import org.wso2.emm.system.service.utils.Constants;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * This class handles the functionality required for setting OTA update server
 * configurations. On failure it sets default parameters set by the developer.
 */
public class OTAServerConfig {

    private static final String TAG = "OTA_SC";
    private URL updatePackageURL;
    private URL buildPropURL;
    private Context context;

    public OTAServerConfig(String productName, Context context) throws MalformedURLException {
        this.context = context;
        defaultConfigure(productName);
    }

    public void defaultConfigure(String product) throws MalformedURLException {
        String fileAddress, buildConfigAddress;
        if (Constants.DEFAULT_OTA_SERVER_SUB_DIRECTORY != null) {
            fileAddress = Constants.DEFAULT_OTA_SERVER_SUB_DIRECTORY + File.separator + product + File.separator + product +
                          Constants.DEFAULT_OTA_ZIP_FILE;
            buildConfigAddress = Constants.DEFAULT_OTA_SERVER_SUB_DIRECTORY + File.separator + product + File.separator +
                                 Constants.DEFAULT_OTA_BUILD_PROP_FILE;
        } else {
            fileAddress = product + File.separator + product + Constants.DEFAULT_OTA_ZIP_FILE;
            buildConfigAddress = product + File.separator + Constants.DEFAULT_OTA_BUILD_PROP_FILE;
        }
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
