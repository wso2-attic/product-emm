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

package org.wso2.app.catalog.api;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.Browser;
import android.util.Base64;
import android.util.Log;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.wso2.app.catalog.AppCatalogException;
import org.wso2.app.catalog.R;
import org.wso2.app.catalog.beans.DeviceAppInfo;
import org.wso2.app.catalog.utils.CommonUtils;
import org.wso2.app.catalog.utils.Constants;
import org.wso2.app.catalog.utils.Preference;
import org.wso2.app.catalog.utils.StreamHandler;
import org.wso2.emm.agent.proxy.IDPTokenManagerException;
import org.wso2.emm.agent.proxy.utils.ServerUtilities;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class handles all the functionalities required for managing application
 * installation and un-installation.
 */
public class ApplicationManager {
    private static final int SYSTEM_APPS_DISABLED_FLAG = 0;
    private static final int MAX_URL_HASH = 32;
    private static final int COMPRESSION_LEVEL = 100;
    private static final int BUFFER_SIZE = 1024;
    private static final int READ_FAILED = -1;
    private static final int BUFFER_OFFSET = 0;
    private static final String TAG = ApplicationManager.class.getName();
    private Context context;
    private Resources resources;
    private PackageManager packageManager;
    private String downloadedAppName = null;
    private long downloadReference;

    private BroadcastReceiver downloadReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            long referenceId = intent.getLongExtra(
                    DownloadManager.EXTRA_DOWNLOAD_ID, -1);

            if (downloadReference == referenceId) {
                String downloadDirectoryPath = Environment.getExternalStoragePublicDirectory(Environment.
                                                                                                     DIRECTORY_DOWNLOADS).getPath();
                File file = new File(downloadDirectoryPath, downloadedAppName);
                if (file.exists()) {
                    PackageManager pm = context.getPackageManager();
                    PackageInfo info = pm.getPackageArchiveInfo(downloadDirectoryPath + File.separator + downloadedAppName,
                                                                PackageManager.GET_ACTIVITIES);
                    Preference.putString(context, context.getResources().getString(R.string.shared_pref_installed_app),
                                         info.packageName);
                    Preference.putString(context, context.getResources().getString(R.string.shared_pref_installed_file),
                                         downloadedAppName);
                    startInstallerIntent(Uri.fromFile(new File(downloadDirectoryPath + File.separator + downloadedAppName)));
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
            if (!isSystemPackage(packageInfo)) {
                app = new DeviceAppInfo();
                app.setAppname(packageInfo.applicationInfo.
                        loadLabel(packageManager).toString());
                app.setPackagename(packageInfo.packageName);
                app.setVersionName(packageInfo.versionName);
                app.setVersionCode(packageInfo.versionCode);
                appList.put(packageInfo.packageName, app);
            }
        }
        return appList;
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
     */
    public void installApp(String url, String packageName) {
        Preference.putString(context, resources.getString(R.string.current_downloading_app), packageName);
        if (isPackageInstalled(Constants.AGENT_PACKAGE_NAME)) {
            CommonUtils.callAgentApp(context, Constants.Operation.INSTALL_APPLICATION,
                                     url, null);
        } else if (isDownloadManagerAvailable(context)) {
            IntentFilter filter = new IntentFilter(
                    DownloadManager.ACTION_DOWNLOAD_COMPLETE);
            context.registerReceiver(downloadReceiver, filter);
            downloadViaDownloadManager(url, downloadedAppName);
        } else {
            AppUpdater updater = new AppUpdater();
            updater.execute(url);
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
     * Removes an application from the device.
     *
     * @param packageName - Application package name should be passed in as a String.
     */
    public void uninstallApplication(String packageName) {
        if (packageName != null &&
            !packageName.contains(resources.getString(R.string.application_package_prefix))) {
            packageName = resources.getString(R.string.application_package_prefix) + packageName;
        }

        if(isPackageInstalled(Constants.AGENT_PACKAGE_NAME)) {
            CommonUtils.callAgentApp(context, Constants.Operation.UNINSTALL_APPLICATION,
                                     packageName, null);
        } else {
            Uri packageURI = Uri.parse(packageName);
            Intent uninstallIntent = new Intent(Intent.ACTION_DELETE, packageURI);
            uninstallIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(uninstallIntent);
        }
    }

    /**
     * Creates a webclip on the device home screen.
     *
     * @param url   - URL should be passed in as a String.
     * @param title - Title(Web app title) should be passed in as a String.
     */
    public void manageWebAppBookmark(String url, String title, String operationType)
            throws AppCatalogException {
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
                throw new AppCatalogException("Cannot create webclip due to invalid operation type.");
            }
        } else {
            bookmarkIntent.
                    setAction(resources.getString(R.string.application_package_launcher_install_action));
        }
        context.sendBroadcast(bookmarkIntent);
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

    /**
     * Initiate downloading via DownloadManager API.
     *
     * @param url     - File URL.
     * @param appName - Name of the application to be downloaded.
     */
    private void downloadViaDownloadManager(String url, String appName) {
        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
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
        // Enqueue a new download and same the referenceId
        downloadReference = downloadManager.enqueue(request);
    }

    /**
     * Installs or updates an application to the device.
     */
    public class AppUpdater extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... inputData) {
            FileOutputStream outStream = null;
            InputStream inStream = null;
            try {
                HttpGet httpGet = new HttpGet(inputData[BUFFER_OFFSET]);
                HttpClient httpClient = ServerUtilities.getCertifiedHttpClient();
                HttpResponse response = httpClient.execute(httpGet);

                String directory = Environment.getExternalStorageDirectory().getPath() +
                                   resources.getString(R.string.application_mgr_download_location);
                File file = new File(directory);

                if (!file.mkdirs()) {
                    Log.e(TAG, "Download directory creation failed.");
                }

                File outputFile = new File(file,
                                           resources.getString(R.string.application_mgr_download_file_name));

                if (outputFile.exists()) {
                    if (!outputFile.delete()) {
                        Log.e(TAG, "Existing APK removal failed.");
                    }
                }

                outStream = new FileOutputStream(outputFile);

                inStream = response.getEntity().getContent();

                byte[] buffer = new byte[BUFFER_SIZE];
                int lengthFile;

                while ((lengthFile = inStream.read(buffer)) != READ_FAILED) {
                    outStream.write(buffer, BUFFER_OFFSET, lengthFile);
                }

                String filePath = directory + resources.getString(R.string.application_mgr_download_file_name);
                Uri fileUri = Uri.fromFile(new File(filePath));
                startInstallerIntent(fileUri);
            } catch (IDPTokenManagerException e) {
                Log.e(TAG, "Error occurred while sending 'Get' request due to IDP proxy initialization issue.");
            } catch (IOException e) {
                Log.e(TAG, "File download/save failure in AppUpdator.", e);
            } catch (IllegalArgumentException e) {
                Log.e(TAG, "Error occurred while sending 'Get' request due to empty host name");
            } finally {
                StreamHandler.closeOutputStream(outStream, TAG);
                StreamHandler.closeInputStream(inStream, TAG);
            }

            return null;
        }
    }

}
