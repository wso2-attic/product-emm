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

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.Browser;
import android.util.Base64;
import android.util.Log;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import org.wso2.emm.agent.AndroidAgentException;
import org.wso2.emm.agent.R;
import org.wso2.emm.agent.beans.DeviceAppInfo;
import org.wso2.emm.agent.beans.ServerConfig;
import org.wso2.emm.agent.proxy.IDPTokenManagerException;
import org.wso2.emm.agent.proxy.IdentityProxy;
import org.wso2.emm.agent.proxy.beans.Token;
import org.wso2.emm.agent.proxy.interfaces.TokenCallBack;
import org.wso2.emm.agent.proxy.utils.ServerUtilities;
import org.wso2.emm.agent.utils.AlarmUtils;
import org.wso2.emm.agent.utils.CommonUtils;
import org.wso2.emm.agent.utils.Constants;
import org.wso2.emm.agent.utils.Preference;
import org.wso2.emm.agent.utils.StreamHandler;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class handles all the functionalities required for managing application
 * installation and un-installation.
 */
public class ApplicationManager implements TokenCallBack {
    private static final int SYSTEM_APPS_DISABLED_FLAG = 0;
    private static final int MAX_URL_HASH = 32;
    private static final int COMPRESSION_LEVEL = 100;
    private static final int BUFFER_SIZE = 1024;
    private static final int READ_FAILED = -1;
    private static final int BUFFER_OFFSET = 0;
    private static final int DOWNLOAD_PERCENTAGE_TOTAL = 100;
    private static final int DOWNLOADER_INCREMENT = 10;
    private static final String TAG = ApplicationManager.class.getName();
    private Context context;
    private Resources resources;
    private PackageManager packageManager;
    private long downloadReference;
    private Token token;
    private String appUrl;
    private String schedule;

    private BroadcastReceiver downloadReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            long referenceId = intent.getLongExtra(
                    DownloadManager.EXTRA_DOWNLOAD_ID, -1);

            if (downloadReference == referenceId) {
                String downloadDirectoryPath = Environment.getExternalStoragePublicDirectory(Environment.
                                                                                         DIRECTORY_DOWNLOADS).getPath();
                File file = new File(downloadDirectoryPath, resources.getString(R.string.download_mgr_download_file_name));
                if (file.exists()) {
                    PackageManager pm = context.getPackageManager();
                    PackageInfo info = pm.getPackageArchiveInfo(downloadDirectoryPath + File.separator + resources.
                                                                    getString(R.string.download_mgr_download_file_name),
                                                                PackageManager.GET_ACTIVITIES);
                    if (info != null && info.packageName != null) {
                        Preference.putString(context, context.getResources().getString(R.string.shared_pref_installed_app),
                                             info.packageName);
                    }
                    Preference.putString(context, context.getResources().getString(R.string.shared_pref_installed_file),
                                         resources.getString(R.string.download_mgr_download_file_name));
                    triggerInstallation(Uri.fromFile(new File(downloadDirectoryPath + File.separator +
                                                              resources.getString(R.string.download_mgr_download_file_name))));
                }
            }
        }
    };

    public ApplicationManager(Context context) {
        this.context = context;
        this.resources = context.getResources();
        this.packageManager = context.getPackageManager();
    }

    /**
     * Returns a list of all the applications installed on the device.
     *
     * @return - List of applications which installed on the device.
     */
    public Map<String, DeviceAppInfo> getInstalledApps() {
        Map<String, DeviceAppInfo> appList = new HashMap<>();
        List<PackageInfo> packages = packageManager.getInstalledPackages(SYSTEM_APPS_DISABLED_FLAG);
        DeviceAppInfo app;

        for (PackageInfo packageInfo : packages) {
            if (Constants.ALLOW_SYSTEM_APPS_IN_APPS_LIST_RESPONSE) {
                app = new DeviceAppInfo();
                app.setAppname(packageInfo.applicationInfo.
                        loadLabel(packageManager).toString());
                app.setPackagename(packageInfo.packageName);
                app.setVersionName(packageInfo.versionName);
                app.setVersionCode(packageInfo.versionCode);
                app.setIsSystemApp(isSystemPackage(packageInfo));
                appList.put(packageInfo.packageName, app);
            } else if (!isSystemPackage(packageInfo)) {
                app = new DeviceAppInfo();
                app.setAppname(packageInfo.applicationInfo.
                        loadLabel(packageManager).toString());
                app.setPackagename(packageInfo.packageName);
                app.setVersionName(packageInfo.versionName);
                app.setVersionCode(packageInfo.versionCode);
                app.setIsSystemApp(false);
                appList.put(packageInfo.packageName, app);
            }
        }
        return appList;
    }

    /**
     * Returns a list of all the applications installed on the device by user.
     *
     * @return - List of applications which installed on the device by user.
     */
    public List<String> getInstalledAppsByUser() {
        List<String> packagesInstalledByUser = new ArrayList<>();
        int flags = PackageManager.GET_META_DATA;
        List<ApplicationInfo> applications = packageManager.getInstalledApplications(flags);
        for (ApplicationInfo appInfo : applications) {
            if ((appInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 1) {
                packagesInstalledByUser.add(appInfo.packageName);
            }
        }
        return packagesInstalledByUser;
    }

    /**
     * Returns the app name for a particular package name.
     *
     * @param packageName - Package name which you need the app name.
     * @return - Application name.
     */
    public String getAppNameFromPackage(String packageName) {
        String appName = null;
        List<PackageInfo> packages = packageManager.
                getInstalledPackages(SYSTEM_APPS_DISABLED_FLAG);
        for (PackageInfo packageInfo : packages) {
            if (packageName.equals(packageInfo.packageName)) {
                appName = packageInfo.applicationInfo.
                        loadLabel(packageManager).toString();
                break;
            }
        }

        return appName;
    }

    public boolean isPackageInstalled(String packagename) {
        try {
            PackageInfo packageInfo = packageManager.
                    getPackageInfo(packagename, PackageManager.GET_ACTIVITIES);
            if (packageInfo != null) {
                return true;
            }
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
        return false;
    }

    public void triggerInstallation(Uri fileUri) {
        if (Constants.SYSTEM_APP_ENABLED) {
            CommonUtils.callSystemApp(context, Constants.Operation.SILENT_INSTALL_APPLICATION, schedule,
                                      fileUri.toString());
        } else {
            if (schedule != null && !schedule.trim().isEmpty() && !schedule.equals("undefined")) {
                try {
                    AlarmUtils.setOneTimeAlarm(context, schedule, Constants.Operation.INSTALL_APPLICATION,
                                               fileUri.toString());
                } catch (ParseException e) {
                    Log.e(TAG, "One time alarm time string parsing failed." + e);
                }
            } else {
                startInstallerIntent(fileUri);
            }
        }
    }

    /**
     * Installs an application to the device.
     *
     * @param fileUri - File URI should be passed in as a String.
     */
    public void startInstallerIntent(Uri fileUri) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(fileUri, resources.getString(R.string.application_mgr_mime));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**
     * Returns whether the app is a system app.
     *
     * @param packageInfo - Package of the app which you need the status.
     * @return - App status.
     */
    private boolean isSystemPackage(PackageInfo packageInfo) {
        return ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0);
    }

    /**
     * Returns a base64 encoded string for a particular image.
     *
     * @param drawable - Image as a Drawable object.
     * @return - Base64 encoded value of the drawable.
     */
    public String encodeImage(Drawable drawable) {
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, COMPRESSION_LEVEL, outStream);
        byte[] bitmapContent = outStream.toByteArray();
        String encodedImage = Base64.encodeToString(bitmapContent, Base64.NO_WRAP);
        StreamHandler.closeOutputStream(outStream, TAG);

        return encodedImage;
    }

    /**
     * Installs an application to the device.
     *
     * @param url - APK Url should be passed in as a String.
     * @param schedule - If update/installation is scheduled, schedule information should be passed.
     */
    public void installApp(String url, String schedule) {
        url = url.substring(url.lastIndexOf("/"), url.length());
        if (Constants.APP_MANAGER_HOST != null) {
            this.appUrl = Constants.APP_MANAGER_HOST + Constants.APP_DOWNLOAD_ENDPOINT + url;
        } else {
            String ipSaved = Preference.getString(context, Constants.PreferenceFlag.IP);
            ServerConfig utils = new ServerConfig();
            if (ipSaved != null && !ipSaved.isEmpty()) {
                utils.setServerIP(ipSaved);
                this.appUrl = utils.getAPIServerURL(context) + Constants.APP_DOWNLOAD_ENDPOINT + url;
            } else {
                Log.e(TAG, "There is no valid IP to contact the server");
            }
        }
        this.schedule = schedule;
        String clientKey = Preference.getString(context, Constants.CLIENT_ID);
        String clientSecret = Preference.getString(context, Constants.CLIENT_SECRET);
        if (IdentityProxy.getInstance().getContext() == null) {
            IdentityProxy.getInstance().setContext(context);
        }

        IdentityProxy.getInstance().requestToken(IdentityProxy.getInstance().getContext(), this,
                                                 clientKey,
                                                 clientSecret);
    }

    /**
     * Removes an application from the device.
     *
     * @param packageName - Application package name should be passed in as a String.
     */
    public void uninstallApplication(String packageName, String schedule) {
        if (packageName != null &&
            !packageName.contains(resources.getString(R.string.application_package_prefix))) {
            packageName = resources.getString(R.string.application_package_prefix) + packageName;
        }

        if (Constants.SYSTEM_APP_ENABLED) {
            CommonUtils.callSystemApp(context, Constants.Operation.SILENT_UNINSTALL_APPLICATION, schedule, packageName);
        } else {
            if (schedule != null && !schedule.trim().isEmpty() && !schedule.equals("undefined")) {
                try {
                    AlarmUtils.setOneTimeAlarm(context, schedule, Constants.Operation.UNINSTALL_APPLICATION, packageName);
                } catch (ParseException e) {
                    Log.e(TAG, "One time alarm time string parsing failed." + e);
                }
            } else {
                Uri packageURI = Uri.parse(packageName);
                Intent uninstallIntent = new Intent(Intent.ACTION_DELETE, packageURI);
                uninstallIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(uninstallIntent);
            }
        }
    }

    /**
     * Creates a webclip on the device home screen.
     *
     * @param url   - URL should be passed in as a String.
     * @param title - Title(Web app title) should be passed in as a String.
     */
    public void manageWebAppBookmark(String url, String title, String operationType)
            throws AndroidAgentException {
        final Intent bookmarkIntent = new Intent();
        final Intent actionIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        long urlHash = url.hashCode();
        long uniqueId = (urlHash << MAX_URL_HASH) | actionIntent.hashCode();

        actionIntent.putExtra(Browser.EXTRA_APPLICATION_ID, Long.toString(uniqueId));
        bookmarkIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, actionIntent);
        bookmarkIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, title);
        bookmarkIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
                                Intent.ShortcutIconResource.fromContext(context,
                                                                        R.drawable.ic_bookmark)
        );
        if (operationType != null) {
            if (resources.getString(R.string.operation_install).equalsIgnoreCase(operationType)) {
                bookmarkIntent.
                        setAction(resources.getString(R.string.application_package_launcher_install_action));
            } else if (resources.getString(R.string.operation_uninstall).equalsIgnoreCase(operationType)) {
                bookmarkIntent.
                        setAction(resources.getString(R.string.application_package_launcher_uninstall_action));
            } else {
                throw new AndroidAgentException("Cannot create webclip due to invalid operation type.");
            }
        } else {
            bookmarkIntent.
                    setAction(resources.getString(R.string.application_package_launcher_install_action));
        }
        context.sendBroadcast(bookmarkIntent);
    }

    public List<ApplicationInfo> getInstalledApplications() {
        return packageManager.getInstalledApplications(PackageManager.GET_META_DATA);
    }

    /**
     * Checks whether the DownloadManager is available on the device.
     *
     * @param context - Context of the calling activity.
     */
    public boolean isDownloadManagerAvailable(Context context) {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setClassName(resources.getString(R.string.android_download_manager_ui_resolver),
                            resources.getString(R.string.android_download_manager_list_resolver));
        return context.getPackageManager().queryIntentActivities(intent,
                                                                 PackageManager.MATCH_DEFAULT_ONLY).size() > 0;
    }

    private void removeExistingFile() {
        String directory = Environment.getExternalStorageDirectory().getPath() +
                           resources.getString(R.string.application_mgr_download_location);
        File file = new File(directory);
        file.mkdirs();
        File outputFile = new File(file,
                                   resources.getString(R.string.application_mgr_download_file_name));

        if (outputFile.exists()) {
            outputFile.delete();
        }
    }

    /**
     * Initiate downloading via DownloadManager API.
     *
     * @param url     - File URL.
     * @param appName - Name of the application to be downloaded.
     */
    private void downloadViaDownloadManager(String url, String appName) {
        final DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        Uri downloadUri = Uri
                .parse(url);
        DownloadManager.Request request = new DownloadManager.Request(
                downloadUri);

        // Restrict the types of networks over which this download may
        // proceed.
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI
                                       | DownloadManager.Request.NETWORK_MOBILE);
        // Set whether this download may proceed over a roaming connection.
        request.setAllowedOverRoaming(false);
        // Set the title of this download, to be displayed in notifications
        // (if enabled).
        request.setTitle(resources.getString(R.string.downloader_message_title));
        // Set a description of this download, to be displayed in
        // notifications (if enabled)
        request.setDescription(resources.getString(R.string.downloader_message_description) + appName);
        // Set the local destination for the downloaded file to a path
        // within the application's external files directory
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, appName);
        request.addRequestHeader("Authorization", "Bearer " + token.getAccessToken());
        // Enqueue a new download and same the referenceId
        downloadReference = downloadManager.enqueue(request);
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean downloading = true;
                int progress = 0;
                while (downloading) {
                    DownloadManager.Query query = new DownloadManager.Query();
                    query.setFilterById(downloadReference);
                    Cursor cursor = downloadManager.query(query);
                    cursor.moveToFirst();
                    int bytesDownloaded = cursor.getInt(cursor.getColumnIndex(DownloadManager.
                                                                                      COLUMN_BYTES_DOWNLOADED_SO_FAR));
                    int bytesTotal = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
                    if (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)) == DownloadManager.
                            STATUS_SUCCESSFUL) {
                        downloading = false;
                    }
                    int downloadProgress = (int) ((bytesDownloaded * 100l) / bytesTotal);
                    if (downloadProgress != DOWNLOAD_PERCENTAGE_TOTAL) {
                        progress += DOWNLOADER_INCREMENT;
                    } else {
                        progress = DOWNLOAD_PERCENTAGE_TOTAL;
                    }

                    Preference.putString(context, resources.getString(R.string.app_download_progress),
                                         String.valueOf(progress));
                    cursor.close();
                }
            }
        }).start();
    }

    /**
     * Installs or updates an application to the device.
     *
     * @param url - APK Url should be passed in as a String.
     */
    private void downloadApp(String url) {
        RequestQueue queue = null;
        try {
            queue = ServerUtilities.getCertifiedHttpClient();
        } catch (IDPTokenManagerException e) {
            Log.e(TAG, "Failed to retrieve HTTP client", e);
        }

        InputStreamVolleyRequest request = new InputStreamVolleyRequest(Request.Method.GET, url,
            new Response.Listener<byte[]>() {
                @Override
                public void onResponse(byte[] response) {
                    if (response != null) {
                        FileOutputStream outStream = null;
                        InputStream inStream = null;
                        try {
                            String directory = Environment.getExternalStorageDirectory().getPath() +
                                               resources.getString(R.string.application_mgr_download_location);
                            File file = new File(directory);
                            file.mkdirs();
                            File outputFile = new File(file,
                                               resources.getString(R.string.application_mgr_download_file_name));

                            if (outputFile.exists()) {
                                outputFile.delete();
                            }

                            outStream = new FileOutputStream(outputFile);
                            inStream = new ByteArrayInputStream(response);

                            byte[] buffer = new byte[BUFFER_SIZE];
                            int lengthFile;

                            while ((lengthFile = inStream.read(buffer)) != READ_FAILED) {
                                outStream.write(buffer, BUFFER_OFFSET, lengthFile);
                            }

                            String filePath = directory + resources.getString(R.string.application_mgr_download_file_name);
                            triggerInstallation(Uri.fromFile(new File(filePath)));
                        } catch (IOException e) {
                            Log.e(TAG, "File download/save failure in AppUpdator.", e);
                        } catch (IllegalArgumentException e) {
                            Log.e(TAG, "Error occurred while sending 'Get' request due to empty host name");
                        } finally {
                            StreamHandler.closeOutputStream(outStream, TAG);
                            StreamHandler.closeInputStream(inStream, TAG);
                        }
                    }
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(TAG, error.toString());
                }
            }, null)
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Accept", "*/*");
                headers.put("User-Agent", "Mozilla/5.0 ( compatible ), Android");
                headers.put("Authorization", "Bearer " + token.getAccessToken());
                return headers;
            }
        };
        queue.add(request);
    }

    @Override
    public void onReceiveTokenResult(Token token, String status) {
        this.token = token;
        if (isDownloadManagerAvailable(context) && !Constants.SERVER_PROTOCOL.equals(resources.getString(
                R.string.server_protocol_https))) {
            IntentFilter filter = new IntentFilter(
                    DownloadManager.ACTION_DOWNLOAD_COMPLETE);
            context.registerReceiver(downloadReceiver, filter);
            removeExistingFile();
            downloadViaDownloadManager(this.appUrl, resources.getString(R.string.download_mgr_download_file_name));
        } else {
            downloadApp(this.appUrl);
        }
    }
}
