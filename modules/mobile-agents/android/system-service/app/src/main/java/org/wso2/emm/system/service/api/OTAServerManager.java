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

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.Build;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.RecoverySystem;
import android.os.StatFs;
import android.os.SystemProperties;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;
import org.wso2.emm.system.service.MainActivity;
import org.wso2.emm.system.service.R;
import org.wso2.emm.system.service.services.NotificationActionReceiver;
import org.wso2.emm.system.service.utils.CommonUtils;
import org.wso2.emm.system.service.utils.Constants;
import org.wso2.emm.system.service.utils.FileUtils;
import org.wso2.emm.system.service.utils.Preference;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.security.GeneralSecurityException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * This class handles the functionality required for performing OTA updates. Basically it handles
 * all the steps required from downloading the update package to installing it on the device.
 */
public class OTAServerManager {
    private static final String TAG = "OTA_SM";
    private static final String BUILD_DATE_UTC_PROPERTY = "ro.build.date.utc";
    private static final int DEFAULT_STATE_ERROR_CODE = 0;
    private static final int DEFAULT_STATE_INFO_CODE = 0;
    private static final int DEFAULT_BYTES = 100 * 1024;
    private static final int DEFAULT_STREAM_LENGTH = 153600;
    private static final int DEFAULT_OFFSET = 0;
    private OTAStateChangeListener stateChangeListener;
    private OTAServerConfig serverConfig;
    private long cacheProgress = -1;
    private Context context;
    private WakeLock wakeLock;
    private volatile long downloadedLength = 0;
    private volatile int lengthOfFile = 0;
    private volatile boolean isProgressUpdateTerminated = false;
    private AsyncTask asyncTask = null;
    private Executor executor;

    private int corePoolSize = 60;
    private int maximumPoolSize = 80;
    private int keepAliveTime = 10;

    //Use our own thread pool executor for async task to schedule new tasks upon download failures.
    private BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<>(maximumPoolSize);
    private Executor threadPoolExecutor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.SECONDS, workQueue);

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
        if (this.stateChangeListener != null) {
            if (checkNetworkOnline()) {
                getTargetPackagePropertyList(this.serverConfig.getBuildPropURL());
            } else {
                reportCheckingError(OTAStateChangeListener.ERROR_WIFI_NOT_AVAILABLE);
                String message = "Connection failure while downloading the update.";
                Log.e(TAG, message);
                CommonUtils.sendBroadcast(context, Constants.Operation.GET_FIRMWARE_UPGRADE_PACKAGE_STATUS, Constants.Code.FAILURE,
                        Constants.Status.NETWORK_UNREACHABLE, message);
                CommonUtils.callAgentApp(context, Constants.Operation.FAILED_FIRMWARE_UPGRADE_NOTIFICATION, 0, null);
            }
        } else {
            reportCheckingError(OTAStateChangeListener.ERROR_CANNOT_FIND_SERVER);
        }
    }

    /**
     * Compares device firmware version with the latest upgrade file from the server.
     *
     * @return - Returns true if the firmware needs to be upgraded.
     */
    public boolean compareLocalVersionToServer(BuildPropParser parser) {
        if (parser == null) {
            Log.d(TAG, "compareLocalVersion Without fetch remote prop list.");
            return false;
        }

        Long buildTime = Long.parseLong(SystemProperties.get(BUILD_DATE_UTC_PROPERTY));
        String buildTimeUTC = parser.getProp(BUILD_DATE_UTC_PROPERTY);
        Long remoteBuildUTC;
        if ((buildTimeUTC != null) && (!(buildTimeUTC.equals("null")))) {
            remoteBuildUTC = Long.parseLong(buildTimeUTC);
        } else {
            remoteBuildUTC = Long.MIN_VALUE;
            Log.e(TAG, "UTC date not found in config file, config may be corrupted or missing");
        }

        Log.d(TAG, "Local Version:" + Build.VERSION.INCREMENTAL + " Server Version:" + parser.getNumRelease());
        boolean upgrade = remoteBuildUTC > buildTime;
        Log.d(TAG, "Remote build time : " + remoteBuildUTC + " Local build time : " + buildTime);
        return upgrade;
    }

    private void publishDownloadProgress(long total, long downloaded) {
        long progress = (downloaded * 100) / total;
        long published = -1L;
        if (Preference.getString(context, context.getResources().getString(R.string.firmware_download_progress)) != null) {
            published = Long.valueOf(Preference.getString(context, context.getResources().getString(
                    R.string.firmware_download_progress)));
        }

        if (progress != published) {
            publishFirmwareDownloadProgress(progress);
            Preference.putString(context, context.getResources().getString(R.string.firmware_download_progress),
                                 String.valueOf(progress));
            Log.d(TAG, "Download Progress - " + progress + "% - Downloaded:" + downloaded + "/" + total);
            if (progress == 100) {
                Preference.putString(context, context.getResources().getString(R.string.firmware_download_progress),
                        String.valueOf(DEFAULT_STATE_INFO_CODE));
            }
        }

        if (this.stateChangeListener != null && progress != cacheProgress) {
            this.stateChangeListener.onStateOrProgress(OTAStateChangeListener.MESSAGE_DOWNLOAD_PROGRESS,
                                                       DEFAULT_STATE_INFO_CODE, null, progress);
            cacheProgress = progress;
        }
    }

    private void publishFirmwareDownloadProgress(long progress) {
        JSONObject result = new JSONObject();
        try {
            result.put("progress", String.valueOf(progress));
            CommonUtils.sendBroadcast(context, Constants.Operation.UPGRADE_FIRMWARE, Constants.Code.SUCCESS,
                    Constants.Status.OTA_UPGRADE_ONGOING, result.toString());
        } catch (JSONException e) {
            Log.e(TAG, "Failed to create JSON object when publishing OTA progress.");
            CommonUtils.sendBroadcast(context, Constants.Operation.UPGRADE_FIRMWARE, Constants.Code.SUCCESS,
                    Constants.Status.INTERNAL_ERROR, e.getMessage());
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

    private class Timeout extends TimerTask {
        private AsyncTask asyncTask;

        public Timeout(AsyncTask task) {
            asyncTask = task;
        }

        @Override
        public void run() {
            String message;

            isProgressUpdateTerminated = true;
            asyncTask.cancel(true);

            Log.w(TAG,"Timed out while downloading.");

            File targetFile = new File(FileUtils.getUpgradePackageFilePath());
            if (targetFile.exists()) {
                targetFile.delete();
                Log.w(TAG,"Partially downloaded update has been deleted.");
            }

            if (checkNetworkOnline()) {
                message = "Connection failure (Socket timeout) when downloading the update package.";
                Preference.putString(context, context.getResources().getString(R.string.upgrade_download_status), Constants.Status.CONNECTION_FAILED);
                CommonUtils.sendBroadcast(context, Constants.Operation.UPGRADE_FIRMWARE, Constants.Code.FAILURE,
                        Constants.Status.CONNECTION_FAILED, message);
            } else {
                message = "Disconnected from WiFi when downloading the update package.";
                Preference.putString(context, context.getResources().getString(R.string.upgrade_download_status), Constants.Status.WIFI_OFF);
                CommonUtils.sendBroadcast(context, Constants.Operation.UPGRADE_FIRMWARE, Constants.Code.FAILURE,
                        Constants.Status.WIFI_OFF, message);
            }
            Log.e(TAG, message);
            CommonUtils.callAgentApp(context, Constants.Operation.FAILED_FIRMWARE_UPGRADE_NOTIFICATION, 0, message);
        }
    }

    private class DownloadProgressUpdateExecutor implements Executor {
        public void execute(@NonNull Runnable r) {
            new Thread(r).start();
        }
    }

    public void startDownloadUpgradePackage(final OTAServerManager serverManager) {
        if (asyncTask != null){
            asyncTask.cancel(true);
        }
        asyncTask = new AsyncTask<Void, Void, Void>() {
            protected Void doInBackground(Void... unused) {
                Preference.putString(context, context.getResources().getString(R.string.upgrade_download_status), Constants.Status.OTA_UPGRADE_ONGOING);
                File targetFile = new File(FileUtils.getUpgradePackageFilePath());
                if (targetFile.exists()) {
                    targetFile.delete();
                }
                try {
                    boolean fileStatus = targetFile.createNewFile();
                    if (!fileStatus) {
                        Log.e(TAG, "Update package file creation failed.");
                    }
                } catch (IOException e) {
                    String message = "Update package file retrieval error.";
                    Log.e(TAG, message + e);
                    reportDownloadError(OTAStateChangeListener.ERROR_WRITE_FILE_ERROR);
                    CommonUtils.sendBroadcast(context, Constants.Operation.UPGRADE_FIRMWARE, Constants.Code.FAILURE,
                                  Constants.Status.INTERNAL_ERROR, message);
                }

                try {
                    wakeLock.acquire();

                    URL url = serverConfig.getPackageURL();
                    Log.d(TAG, "Start downloading package:" + url.toString());
                    URLConnection connection = url.openConnection();
                    connection.setConnectTimeout(Constants.FIRMWARE_UPGRADE_CONNECTIVITY_TIMEOUT);
                    connection.setReadTimeout(Constants.FIRMWARE_UPGRADE_READ_TIMEOUT);
                    lengthOfFile = connection.getContentLength();
                    downloadedLength = 0;
                    InputStream input = new BufferedInputStream(url.openStream());
                    OutputStream output = new FileOutputStream(targetFile);
                    Timer timeoutTimer = new Timer();
                    Log.d(TAG, "Update package file size:" + lengthOfFile);
                    if (getFreeDiskSpace() < lengthOfFile){
                        String message = "Device does not have enough memory to download the OTA" +
                                " update";
                        CommonUtils.sendBroadcast(context, Constants.Operation.UPGRADE_FIRMWARE, Constants.Code.FAILURE,
                                Constants.Status.LOW_DISK_SPACE, message);
                        CommonUtils.callAgentApp(context, Constants.Operation.
                                FIRMWARE_UPGRADE_FAILURE, Preference.getInt(
                                context, context.getResources().getString(R.string.operation_id)), message);
                        Log.e(TAG, message);
                        return null;
                    }
                    byte data[] = new byte[DEFAULT_BYTES];
                    long count;
                    isProgressUpdateTerminated = false;
                    executor = new DownloadProgressUpdateExecutor();
                    executor.execute(new Runnable() {
                        @Override
                        public void run() {
                            while (lengthOfFile > downloadedLength && !isProgressUpdateTerminated) {
                                Preference.putString(context, context.getResources().getString(R.string.upgrade_download_status),
                                        Constants.Status.OTA_UPGRADE_ONGOING);
                                publishDownloadProgress(lengthOfFile, downloadedLength);
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException ignored) {
                                }
                            }
                        }
                    });
                    while ((count = input.read(data)) >= 0) {
                        downloadedLength += count;
                        output.write(data, DEFAULT_OFFSET, (int) count);
                        timeoutTimer.cancel();
                        timeoutTimer = new Timer();
                        timeoutTimer.schedule(new Timeout(this), Constants.FIRMWARE_UPGRADE_READ_TIMEOUT);
                    }
                    publishDownloadProgress(lengthOfFile, downloadedLength);
                    isProgressUpdateTerminated = true;
                    timeoutTimer.cancel();
                    output.flush();
                    output.close();
                    input.close();
                    Preference.putString(context, context.getResources().getString(R.string.upgrade_download_status),
                            context.getResources().getString(R.string.status_success));
                    if (serverManager.stateChangeListener != null) {
                        serverManager.stateChangeListener.onStateOrProgress(OTAStateChangeListener.STATE_IN_DOWNLOADING,
                                                                            DEFAULT_STATE_ERROR_CODE, null, DEFAULT_STATE_INFO_CODE);
                    }
                } catch (SocketTimeoutException e) {
                    String message = "Connection failure (Socket timeout) when downloading update package.";
                    Log.e(TAG, message + e);
                    CommonUtils.sendBroadcast(context, Constants.Operation.UPGRADE_FIRMWARE, Constants.Code.FAILURE,
                            Constants.Status.CONNECTION_FAILED, message);
                    CommonUtils.callAgentApp(context, Constants.Operation.FAILED_FIRMWARE_UPGRADE_NOTIFICATION, 0, null);
                    Preference.putString(context, context.getResources().getString(R.string.upgrade_download_status),
                            Constants.Status.CONNECTION_FAILED);
                } catch (IOException e) {
                    String message = "Unable to find firmware upgrade package " + serverConfig.getPackageURL().toString();
                    Log.e(TAG, message + e);
                    CommonUtils.sendBroadcast(context, Constants.Operation.UPGRADE_FIRMWARE, Constants.Code.FAILURE,
                            Constants.Status.FILE_NOT_FOUND, message);
                    CommonUtils.callAgentApp(context, Constants.Operation.FAILED_FIRMWARE_UPGRADE_NOTIFICATION, 0, null);
                    reportDownloadError(OTAStateChangeListener.ERROR_WRITE_FILE_ERROR);
                    Preference.putString(context, context.getResources().getString(R.string.upgrade_download_status),
                            Constants.Status.FILE_NOT_FOUND);
                } finally {
                    wakeLock.release();
                    wakeLock.acquire(2);
                    if (targetFile.exists() && lengthOfFile != downloadedLength) {
                        targetFile.delete();
                        String status = Preference.getString(context, context.getResources().getString(R.string.upgrade_download_status));
                        if (!Constants.Status.OTA_UPGRADE_ONGOING.equals(status)) {
                            Preference.putString(context, context.getResources().getString(R.string.upgrade_download_status),
                                    Constants.Status.OTA_DOWNLOAD_FAILED);
                        }
                    }
                }
                return null;
            }
        }.executeOnExecutor(threadPoolExecutor);
    }

    public long getFreeDiskSpace() {
        StatFs statFs = new StatFs(FileUtils.getUpgradePackageDirectory());
        long freeDiskSpace = (long) statFs.getAvailableBlocks() * (long) statFs.getBlockSize();
        Log.d(TAG, "Free disk space: " + freeDiskSpace);
        return freeDiskSpace;
    }

    public void startVerifyUpgradePackage() {
        Preference.putBoolean(context, context.getResources().getString(R.string.verification_failed_flag), false);
        File recoveryFile = new File(FileUtils.getUpgradePackageFilePath());
        try {
            wakeLock.acquire();
            Log.d(TAG, "Verifying upgrade package");
            RecoverySystem.verifyPackage(recoveryFile, recoveryVerifyListener, null);
        } catch (IOException e) {
            reportInstallError(OTAStateChangeListener.ERROR_PACKAGE_VERIFY_FAILED);
            String message = "Update verification failed due to file error.";
            Log.e(TAG, message);
        } catch (GeneralSecurityException e) {
            reportInstallError(OTAStateChangeListener.ERROR_PACKAGE_VERIFY_FAILED);
            String message = "Update verification failed due to security check failure.";
            Log.e(TAG, message);
        } finally {
            wakeLock.release();
        }
    }

    public OTAServerConfig getServerConfig() {
        return this.serverConfig;
    }

    public void startInstallUpgradePackage() {
        Preference.putString(context, context.getResources().getString(R.string.upgrade_download_status), Constants.Status.SUCCESSFUL);
        Preference.putString(context, context.getResources().getString(R.string.upgrade_install_status), Constants.Status.REQUEST_PLACED);
        File recoveryFile = new File(FileUtils.getUpgradePackageFilePath());
        try {
            wakeLock.acquire();
            boolean isAutomaticRetryEnabled = Preference.getBoolean(context, context.getResources().getString(R.string.firmware_upgrade_automatic_retry));
            if (getBatteryLevel(context) >= Constants.REQUIRED_BATTERY_LEVEL_TO_FIRMWARE_UPGRADE) {
                Log.d(TAG, "Installing upgrade package");
                if (isAutomaticRetryEnabled || Constants.SILENT_FIRMWARE_INSTALLATION) {
                    RecoverySystem.installPackage(context, recoveryFile);
                } else {
                    setNotification(context, context.getResources().getString(R.string.ask_from_user_to_install_firmware), true);
                }
            } else if (isAutomaticRetryEnabled) {
                Preference.putString(context, context.getResources().getString(R.string.upgrade_install_status),
                                     Constants.Status.BATTERY_LEVEL_INSUFFICIENT_TO_INSTALL);
                Log.e(TAG, "Upgrade installation differed due to insufficient battery level.");
                setNotification(context, context.getResources().getString(R.string.upgrade_differed_due_to_battery), false);
            } else {
                Preference.putString(context, context.getResources().getString(R.string.upgrade_install_status),
                        Constants.Status.BATTERY_LEVEL_INSUFFICIENT_TO_INSTALL);
                Log.e(TAG, "Upgrade installation failed due to insufficient battery level.");
                setNotification(context, context.getResources().getString(R.string.upgrade_failed_due_to_battery), false);
            }
        } catch (IOException e) {
            reportInstallError(OTAStateChangeListener.ERROR_PACKAGE_INSTALL_FAILED);
            String message = "Update installation failed due to file error.";
            Log.e(TAG, message + e);
            CommonUtils.sendBroadcast(context, Constants.Operation.UPGRADE_FIRMWARE, Constants.Code.FAILURE,
                    Constants.Status.OTA_IMAGE_VERIFICATION_FAILED, message);
            CommonUtils.callAgentApp(context, Constants.Operation.FAILED_FIRMWARE_UPGRADE_NOTIFICATION, Preference.getInt(
                    context, context.getResources().getString(R.string.operation_id)), message);
        } catch (SecurityException e) {
            reportInstallError(OTAStateChangeListener.ERROR_PACKAGE_INSTALL_FAILED);
            String message = "Update installation failure due to security check failure.";
            Log.e(TAG, message + e);
            CommonUtils.sendBroadcast(context, Constants.Operation.UPGRADE_FIRMWARE, Constants.Code.FAILURE,
                    Constants.Status.OTA_IMAGE_VERIFICATION_FAILED, message);
            CommonUtils.callAgentApp(context, Constants.Operation.FAILED_FIRMWARE_UPGRADE_NOTIFICATION, Preference.getInt(
                    context, context.getResources().getString(R.string.operation_id)), message);
        } finally {
            wakeLock.release();
        }

    }

    private void setNotification(Context context, String notificationMessage, boolean isUserInput) {
        int requestID = (int) System.currentTimeMillis();
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent notificationIntent = new Intent(context, MainActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent contentIntent = PendingIntent.getActivity(context, requestID,notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.notification)
                .setContentTitle("Message from EMM")
                .setStyle(new NotificationCompat.BigTextStyle()
                                  .bigText(notificationMessage))
                .setContentText(notificationMessage).setAutoCancel(true);

        if (isUserInput) {
            Intent installReceive = new Intent(context, NotificationActionReceiver.class);
            installReceive.setAction(Constants.FIRMWARE_INSTALL_CONFIRM_ACTION);
            PendingIntent installIntent = PendingIntent.getBroadcast(context, requestID, installReceive, PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.addAction(R.drawable.ic_done_black_24dp, "Install", installIntent);

            Intent cancelReceive = new Intent(context, NotificationActionReceiver.class);
            cancelReceive.setAction(Constants.FIRMWARE_INSTALL_CANCEL_ACTION);
            PendingIntent cancelIntent = PendingIntent.getBroadcast(context, requestID, cancelReceive, PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.addAction(R.drawable.ic_block_black_24dp, "Cancel", cancelIntent);
        }

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(Constants.DEFAULT_NOTIFICATION_CODE, mBuilder.build());
    }

    private int getBatteryLevel(Context context) {
        Intent batteryIntent = context.registerReceiver(null,
                                                        new IntentFilter(
                                                                Intent.ACTION_BATTERY_CHANGED));
        int level = 0;
        if (batteryIntent != null) {
            level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
        }

        return level;
    }

    /**
     * Downloads the property list from remote site, and parse it to property list.
     * The caller can parse this list and get information.
     */
    public void getTargetPackagePropertyList(final URL url) {

        final String  operation = Preference.getBoolean(context, context.getResources().getString(R.string.
                firmware_status_check_in_progress)) ? Constants.Operation.GET_FIRMWARE_UPGRADE_PACKAGE_STATUS : Constants.Operation.UPGRADE_FIRMWARE;

        if (asyncTask != null){
            asyncTask.cancel(true);
        }
        asyncTask = new AsyncTask<Void, Void, Void>() {
            protected Void doInBackground(Void... param) {
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
                    urlConnection.setConnectTimeout(Constants.FIRMWARE_UPGRADE_CONNECTIVITY_TIMEOUT);
                    urlConnection.setReadTimeout(Constants.FIRMWARE_UPGRADE_READ_TIMEOUT);
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
                    Timer timer = new Timer();
                    Log.d(TAG, "Start download: " + url.toString() + " to buffer");

                    while ((bytesRead = reader.read(buffer)) > 0) {
                        // Write current segment into byte output stream
                        writer.write(buffer, 0, bytesRead);
                        Log.d(TAG, "wrote " + bytesRead + " into byte output stream");
                        totalBufRead += bytesRead;
                        buffer = new byte[bufSize];
                        timer.cancel();
                        timer = new Timer();
                        timer.schedule(new Timeout(this), Constants.FIRMWARE_UPGRADE_READ_TIMEOUT);
                    }

                    Log.d(TAG, "Download finished: " + (Integer.toString(totalBufRead)) + " bytes downloaded");

                    parser = new BuildPropParser(writer, context);
                    timer.cancel();
                } catch (SocketTimeoutException e) {
                    String message = "Connection failure (Socket timeout) when retrieving update package size.";
                    Log.e(TAG, message + e);
                    CommonUtils.sendBroadcast(context, operation, Constants.Code.FAILURE, Constants.Status.CONNECTION_FAILED, message);
                    CommonUtils.callAgentApp(context, Constants.Operation.FAILED_FIRMWARE_UPGRADE_NOTIFICATION, 0, null);
                } catch (IOException e) {
                    String message = "Property list (build.prop) not found in the server.";
                    Log.e(TAG, message + e);
                    CommonUtils.sendBroadcast(context, operation, Constants.Code.FAILURE, Constants.Status.FILE_NOT_FOUND, message);
                    CommonUtils.callAgentApp(context, Constants.Operation.FAILED_FIRMWARE_UPGRADE_NOTIFICATION, 0, null);
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
                    if (parser != null) {
                        if (stateChangeListener != null) {
                            stateChangeListener.onStateOrProgress(OTAStateChangeListener.STATE_IN_CHECKED,
                                                                  OTAStateChangeListener.NO_ERROR, parser, DEFAULT_STATE_INFO_CODE);
                        }
                    } else {
                        reportCheckingError(OTAStateChangeListener.ERROR_CANNOT_FIND_SERVER);
                    }
                }
                return null;
            }
        }.executeOnExecutor(threadPoolExecutor);
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
