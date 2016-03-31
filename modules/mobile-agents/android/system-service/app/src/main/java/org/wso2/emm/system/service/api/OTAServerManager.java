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
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.RecoverySystem;
import android.util.Log;
import org.wso2.emm.system.service.utils.Constants;
import org.wso2.emm.system.service.utils.FileUtils;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.GeneralSecurityException;

/**
 * This class handles the functionality required for performing OTA updates. Basically it handles
 * all the steps required from downloading the update package to installing it on the device.
 */
public class OTAServerManager {
    private static final String TAG = "OTA_SM";
    private static final String BUILD_DATE_UTC_PROPERTY = "ro.build.date.utc";
    private static final int DEFAULT_STATE_ERROR_CODE = 0;
    private static final int DEFAULT_STATE_INFO_CODE = 0;
    private static final int DEFAULT_CONNECTION_TIMEOUT = 10000;
    private static final int DEFAULT_BYTES = 100 * 1024;
    private static final int DEFAULT_STREAM_LENGTH = 153600;
    private static final int DEFAULT_OFFSET = 0;
    private OTAStateChangeListener stateChangeListener;
    private OTAServerConfig serverConfig;
    private BuildPropParser parser = null;
    private long cacheProgress = -1;
    private Context context;
    private WakeLock wakeLock;

    private RecoverySystem.ProgressListener recoveryVerifyListener = new RecoverySystem.ProgressListener() {
        public void onProgress(int progress) {
            Log.d(TAG, "Verify progress " + progress);
            if (stateChangeListener != null) {
                stateChangeListener.onStateOrProgress(OTAStateChangeListener.MESSAGE_VERIFY_PROGRESS,
                                                      DEFAULT_STATE_ERROR_CODE, null, progress);
            }
        }
    };

    public OTAServerManager(Context context) throws MalformedURLException {
        serverConfig = new OTAServerConfig(Build.PRODUCT, context);
        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "OTA Wakelock");
        this.context = context;
    }

    public void setStateChangeListener(OTAStateChangeListener stateChangeListener) {
        this.stateChangeListener = stateChangeListener;
    }

    public boolean checkNetworkOnline() {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        boolean status = false;
        if (info != null && info.isConnectedOrConnecting()) {
            status = true;
        }

        return status;
    }

    public void startCheckingVersion() {

        if (!checkURL(serverConfig.getBuildPropURL())) {
            if (this.stateChangeListener != null) {
                if (this.checkNetworkOnline()) {
                    reportCheckingError(OTAStateChangeListener.ERROR_CANNOT_FIND_SERVER);
                } else {
                    reportCheckingError(OTAStateChangeListener.ERROR_WIFI_NOT_AVAILABLE);
                }
            }
            return;
        }

        parser = getTargetPackagePropertyList(serverConfig.getBuildPropURL());

        if (parser != null) {
            if (this.stateChangeListener != null) {
                this.stateChangeListener.onStateOrProgress(OTAStateChangeListener.STATE_IN_CHECKED,
                                                           OTAStateChangeListener.NO_ERROR, parser, DEFAULT_STATE_INFO_CODE);
            }
        } else {
            reportCheckingError(OTAStateChangeListener.ERROR_WRITE_FILE_ERROR);
        }
    }

    /**
     * Compares device firmware version with the latest upgrade file from the server.
     *
     * @return - Returns true if rhe firmware needs to be upgraded.
     */
    public boolean compareLocalVersionToServer() {
        if (parser == null) {
            Log.d(TAG, "compareLocalVersion Without fetch remote prop list.");
            return false;
        }

        Long buildTime = Build.TIME;
        String buildTimeUTC = parser.getProp(BUILD_DATE_UTC_PROPERTY);
        Long remoteBuildUTC;
        if ((buildTimeUTC != null) && (!(buildTimeUTC.equals("null")))) {
            remoteBuildUTC = (Long.parseLong(buildTimeUTC)) * 1000;
        } else {
            remoteBuildUTC = Long.MIN_VALUE;
            Log.e(TAG, "UTC date not found in config file, config may be corrupted or missing");
        }

        Log.d(TAG, "Local Version:" + Build.VERSION.INCREMENTAL + " Server Version:" + parser.getNumRelease());
        boolean upgrade = remoteBuildUTC > buildTime;
        Log.d(TAG, "Remote build time : " + remoteBuildUTC + " Local build time : " + buildTime);
        return upgrade;
    }

    void publishDownloadProgress(long total, long downloaded) {
        Log.d(TAG, "Download Progress - Total: " + total + " Downloaded:" + downloaded);
        Long progress = (downloaded * 100) / total;
        if (this.stateChangeListener != null && progress != cacheProgress) {
            this.stateChangeListener.onStateOrProgress(OTAStateChangeListener.MESSAGE_DOWNLOAD_PROGRESS,
                                                       DEFAULT_STATE_INFO_CODE, null, progress);
            cacheProgress = progress;
        }
    }

    void reportCheckingError(int error) {
        if (this.stateChangeListener != null) {
            this.stateChangeListener.onStateOrProgress(OTAStateChangeListener.STATE_IN_CHECKED,
                                                       error, null, DEFAULT_STATE_INFO_CODE);
        }
    }

    void reportDownloadError(int error) {
        if (this.stateChangeListener != null) {
            this.stateChangeListener.onStateOrProgress(OTAStateChangeListener.STATE_IN_DOWNLOADING,
                                                       error, null, DEFAULT_STATE_INFO_CODE);
        }
    }

    void reportInstallError(int error) {
        if (this.stateChangeListener != null) {
            this.stateChangeListener.onStateOrProgress(OTAStateChangeListener.STATE_IN_UPGRADING,
                                                       error, null, DEFAULT_STATE_INFO_CODE);
        }
    }

    public long getUpgradePackageSize() {
        if (!checkURL(serverConfig.getPackageURL())) {
            Log.e(TAG, "getUpgradePackageSize Failed");
            return -1;
        }

        URL url = serverConfig.getPackageURL();
        URLConnection con;
        try {
            con = url.openConnection();
            return con.getContentLength();
        } catch (IOException e) {
            Log.e(TAG, "Connection failure when retrieving update package size." + e);
            return -1;
        }
    }

    public void startDownloadUpgradePackage() {

        if (!checkURL(serverConfig.getPackageURL())) {
            if (this.stateChangeListener != null) {
                reportDownloadError(OTAStateChangeListener.ERROR_CANNOT_FIND_SERVER);
            }
            return;
        }

        File targetFile = new File(FileUtils.getUpgradePackageFilePath());
        try {
            boolean fileStatus = targetFile.createNewFile();
            if (!fileStatus) {
                Log.e(TAG, "Update package file creation failed.");
            }
        } catch (IOException e) {
            Log.e(TAG, "Update package file retrieval error." + e);
            reportDownloadError(OTAStateChangeListener.ERROR_WRITE_FILE_ERROR);
            return;
        }

        try {
            wakeLock.acquire();

            URL url = serverConfig.getPackageURL();
            Log.d(TAG, "Start downloading package:" + url.toString());
            URLConnection connection = url.openConnection();
            connection.setReadTimeout(DEFAULT_CONNECTION_TIMEOUT);

            int lengthOfFile;
            lengthOfFile = connection.getContentLength();
            InputStream input = new BufferedInputStream(url.openStream());
            OutputStream output = new FileOutputStream(targetFile);

            Log.d(TAG, "Update package file size:" + lengthOfFile);
            byte data[] = new byte[DEFAULT_BYTES];
            long total = 0, count;
            while ((count = input.read(data)) >= 0) {
                total += count;
                publishDownloadProgress(lengthOfFile, total);
                output.write(data, DEFAULT_OFFSET, (int) count);
            }

            output.flush();
            output.close();
            input.close();
            if (this.stateChangeListener != null) {
                this.stateChangeListener.onStateOrProgress(OTAStateChangeListener.STATE_IN_DOWNLOADING,
                                                           DEFAULT_STATE_ERROR_CODE, null, DEFAULT_STATE_INFO_CODE);
            }
        } catch (IOException e) {
            Log.e(TAG, "Connection failure when downloading update package." + e);
            reportDownloadError(OTAStateChangeListener.ERROR_WRITE_FILE_ERROR);
        } finally {
            wakeLock.release();
            wakeLock.acquire(2);
        }
    }

    public void startVerifyUpgradePackage() {
        File recoveryFile = new File(FileUtils.getUpgradePackageFilePath());

        try {
            wakeLock.acquire();
            Log.d(TAG, "Verifying upgrade package");
            RecoverySystem.verifyPackage(recoveryFile, recoveryVerifyListener, null);
        } catch (IOException e) {
            reportInstallError(OTAStateChangeListener.ERROR_PACKAGE_VERIFY_FAILED);
            Log.e(TAG, "Update verification failed due to file error." + e);
        } catch (GeneralSecurityException e) {
            reportInstallError(OTAStateChangeListener.ERROR_PACKAGE_VERIFY_FAILED);
            Log.e(TAG, "Update verification failed due to security check failure." + e);
        } finally {
            wakeLock.release();
        }
    }

    public void startInstallUpgradePackage() {
        File recoveryFile = new File(FileUtils.getUpgradePackageFilePath());

        try {
            wakeLock.acquire();
            Log.d(TAG, "Installing upgrade package");
            RecoverySystem.installPackage(context, recoveryFile);
        } catch (IOException e) {
            reportInstallError(OTAStateChangeListener.ERROR_PACKAGE_INSTALL_FAILED);
            Log.e(TAG, "Update installation failed due to file error." + e);
        } catch (SecurityException e) {
            reportInstallError(OTAStateChangeListener.ERROR_PACKAGE_INSTALL_FAILED);
            Log.e(TAG, "Update installation failure due to security check failure." + e);
        } finally {
            wakeLock.release();
        }

    }

    private boolean checkURL(URL url) {
        // Returns true since this is a static URL case
        return true;
    }

    /**
     * Downloads the property list from remote site, and parse it to property list.
     * The caller can parse this list and get information.
     *
     * @return - Returns true if rhe firmware needs to be upgraded.
     */
    public BuildPropParser getTargetPackagePropertyList(URL url) {

        InputStream reader = null;
        ByteArrayOutputStream writer = null;
        BuildPropParser parser = null;
        final int bufSize = 1024;

        // First, trying to download the property list file. the build.prop of target image.
        try {
            URLConnection urlConnection;

			/* Use the URL configuration to open a connection
               to the OTA server */
            urlConnection = url.openConnection();

			/* Since you get a URLConnection, use it to get the
                           InputStream */
            reader = urlConnection.getInputStream();

			/* Now that the InputStream is open, get the content
                           length */
            final int contentLength = urlConnection.getContentLength();
            byte[] buffer = new byte[bufSize];

            if (contentLength != -1) {
                writer = new ByteArrayOutputStream(contentLength);
            } else {
                writer = new ByteArrayOutputStream(DEFAULT_STREAM_LENGTH);
            }

            int totalBufRead = 0;
            int bytesRead;

            Log.d(TAG, "Start download: " + url.toString() + " to buffer");

            while ((bytesRead = reader.read(buffer)) > 0) {
                // Write current segment into byte output stream
                writer.write(buffer, 0, bytesRead);
                Log.d(TAG, "wrote " + bytesRead + " into byte output stream");
                totalBufRead += bytesRead;

                buffer = new byte[bufSize];
            }

            Log.d(TAG, "Download finished: " + (Integer.toString(totalBufRead)) + " bytes downloaded");

            parser = new BuildPropParser(writer, context);

        } catch (IOException e) {
            Log.e(TAG, "Property list download failed due to connection failure." + e);
            return null;
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    Log.e(TAG, "Failed to close buffer reader." + e);
                }
            }
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    Log.e(TAG, "Failed to close buffer writer." + e);
                }
            }
        }
        return parser;
    }

    public interface OTAStateChangeListener {

        int STATE_IN_CHECKED = 1;
        int STATE_IN_DOWNLOADING = 2;
        int STATE_IN_UPGRADING = 3;
        int MESSAGE_DOWNLOAD_PROGRESS = 4;
        int MESSAGE_VERIFY_PROGRESS = 5;
        int NO_ERROR = 0;
        int ERROR_WIFI_NOT_AVAILABLE = 1;
        int ERROR_CANNOT_FIND_SERVER = 2;
        int ERROR_PACKAGE_VERIFY_FAILED = 3;
        int ERROR_WRITE_FILE_ERROR = 4;
        int ERROR_PACKAGE_INSTALL_FAILED = 6;

        void onStateOrProgress(int message, int error, BuildPropParser parser, long info);

    }

}
