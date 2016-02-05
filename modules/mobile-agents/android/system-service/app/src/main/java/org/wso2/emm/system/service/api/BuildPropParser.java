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

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * This class handles all the functionalities required for reading device build
 * properties which is required in OTA (Over The Air) update process.
 */
public class BuildPropParser {
    private static final String TAG = "OTA_BPP";
    private static final String TEMP_FILE_PREFIX = "buildprop";
    private static final String TEMP_FILE_SUFFIX = "ss";
    private File tmpFile;
    private Context context;
    private HashMap<String, String> properties = null;

    BuildPropParser(ByteArrayOutputStream out, Context context) {
        this.context = context;
        properties = new HashMap<String, String>();
        setByteArrayStream(out);
    }

    BuildPropParser(File file, Context context) throws IOException {
        this.context = context;
        properties = new HashMap<String, String>();
        setFile(file);
    }

    public HashMap<String, String> getPropMap() {
        return properties;
    }


    public String getProp(String propName) {
        if (properties != null) {
            return (String) properties.get(propName);
        } else {
            return null;
        }
    }

    public String setProp(String propName, String val) {
        if ((properties != null) && (propName != null) && (val != null)) {
            // returns previous value or null
            return properties.put(propName, val);
        }
        return null;
    }

    private void setByteArrayStream(ByteArrayOutputStream out) {
        try {
            File tmpDir = null;
            if (context != null) {
                tmpDir = context.getFilesDir();
            }
            Log.d(TAG, "tmpDir:" + tmpDir.toString());
            tmpFile = File.createTempFile(TEMP_FILE_PREFIX, TEMP_FILE_SUFFIX, tmpDir);

            tmpFile.deleteOnExit();
            FileOutputStream fileOutputStream = new FileOutputStream(tmpFile);
            out.writeTo(fileOutputStream);
            fileOutputStream.close();
            setFile(tmpFile);
            tmpFile.delete();
        } catch (IOException e) {
            Log.e(TAG, "Writing to file failed." + e);
        }
    }

    private void setFile(File file) throws IOException {
        try {
            FileReader reader = new FileReader(file);
            BufferedReader in = new BufferedReader(reader);
            String string;
            while ((string = in.readLine()) != null) {

                if (string.startsWith("#") == true) {
                    continue;
                }

                Scanner scan = new Scanner(string);
                Log.d(TAG, "Reading line: " + string);
                scan.useDelimiter("=");

                try {
                    String key;
                    if (scan.hasNext()) {
                        key = scan.next();
                    } else {
                        Log.e(TAG, "No key to read from line: " + string);
                        continue;
                    }

                    String val;
                    if (scan.hasNext()) {
                        val = scan.next();
                    } else {
                        Log.e(TAG, "No value to read for key " + key +
                                   " from line " + string);
                        continue;
                    }

                    Log.d(TAG, "Placing " + val + " into key " + key);
                    properties.put(key, val);
                } catch (NoSuchElementException e) {
                    Log.e(TAG, "Parsing Problem: " + e);
                    continue;
                }
            }

            Log.d(TAG, "Bulid Property Parser inserted " + properties.size()
                       + " into the property hashmap ");
            in.close();
        } catch (IOException e) {
            Log.e(TAG, "Reading from file failed." + e);
            throw e;
        }
    }

    public String getRelease() {
        if (properties != null) {
            return properties.get("ro.build.version.release");
        } else {
            return null;
        }
    }

    public String getNumRelease() {
        if (properties != null) {
            return properties.get("ro.build.version.incremental");
        } else {
            return null;
        }
    }

}
