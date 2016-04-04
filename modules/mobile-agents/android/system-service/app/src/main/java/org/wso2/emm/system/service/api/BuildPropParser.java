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
 * This class handles all the functionality required for reading device build
 * properties which is required in OTA (Over The Air) update process.
 */
public class BuildPropParser {
    private static final String TAG = "OTA_BPP";
    private static final String TEMP_FILE_PREFIX = "buildprop";
    private static final String TEMP_FILE_SUFFIX = "ss";
    private Context context;
    private HashMap<String, String> properties = null;

    BuildPropParser(ByteArrayOutputStream out, Context context) {
        this.context = context;
        properties = new HashMap<>();
        setByteArrayStream(out);
    }

    BuildPropParser(File file, Context context) throws IOException {
        this.context = context;
        properties = new HashMap<>();
        setFile(file);
    }

    public String getProp(String propName) {
        if (properties != null) {
            return properties.get(propName);
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
        FileOutputStream fileOutputStream = null;
        File tmpFile;
        try {
            File tmpDir = null;
            if (context != null) {
                tmpDir = context.getFilesDir();
            }

            if (tmpDir != null) {
                Log.d(TAG, "tmpDir:" + tmpDir.toString());
                tmpFile = File.createTempFile(TEMP_FILE_PREFIX, TEMP_FILE_SUFFIX, tmpDir);

                tmpFile.deleteOnExit();
                fileOutputStream = new FileOutputStream(tmpFile);
                out.writeTo(fileOutputStream);
                setFile(tmpFile);
                boolean isDeleted = tmpFile.delete();
                if (!isDeleted) {
                    Log.e(TAG, "Temp file " + tmpFile.getName() + " failed to delete.");
                }
            }
        } catch (IOException e) {
            Log.e(TAG, "Writing to file failed." + e);
        } finally {
            try {
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
            } catch (IOException e) {
                Log.e(TAG, "Failed to close file output stream." + e);
            }
        }
    }

    private void setFile(File file) throws IOException {
        FileReader reader = new FileReader(file);
        BufferedReader in = new BufferedReader(reader);
        try {
            String string;
            while ((string = in.readLine()) != null) {

                if (string.startsWith("#")) {
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

                    String val = null;
                    if (scan.hasNext()) {
                        val = scan.next();
                    } else {
                        Log.e(TAG, "No value to read for key " + key +
                                   " from line " + string);
                    }

                    Log.d(TAG, "Placing " + val + " into key " + key);
                    properties.put(key, val);
                } catch (NoSuchElementException e) {
                    Log.e(TAG, "Parsing Problem: " + e);
                }
            }

            Log.d(TAG, "Build Property Parser inserted " + properties.size()
                       + " into the property map.");
        } catch (IOException e) {
            Log.e(TAG, "Reading from file failed." + e);
            throw e;
        } finally {
            reader.close();
            in.close();
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
