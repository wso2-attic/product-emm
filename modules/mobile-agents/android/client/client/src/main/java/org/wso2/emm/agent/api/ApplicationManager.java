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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.pm.ApplicationInfo;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.wso2.emm.agent.AndroidAgentException;
import org.wso2.emm.agent.R;
import org.wso2.emm.agent.beans.DeviceAppInfo;
import org.wso2.emm.agent.proxy.IDPTokenManagerException;
import org.wso2.emm.agent.proxy.utils.ServerUtilities;
import org.wso2.emm.agent.utils.CommonUtils;
import org.wso2.emm.agent.utils.Constants;
import org.wso2.emm.agent.utils.StreamHandler;

import android.content.Context;
import android.content.Intent;
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

/**
 * This class handles all the functionalities required for managing application 
 * installation and un-installation.
 */
public class ApplicationManager {
	private Context context;
	private static final int SYSTEM_APPS_DISABLED_FLAG = 0;
	private static final int MAX_URL_HASH = 32;
	private static final int COMPRESSION_LEVEL = 100;
	private static final int BUFFER_SIZE = 1024;
	private static final int READ_FAILED = -1;
	private static final int BUFFER_OFFSET = 0;
	private static final String TAG = ApplicationManager.class.getName();
	private Resources resources;
	private PackageManager packageManager;

	public ApplicationManager(Context context) {
		this.context = context;
		this.resources = context.getResources();
		this.packageManager = context.getPackageManager();
	}

	/**
	 * Returns a list of all the applications installed on the device.
	 * @return - List of applications which installed on the device.
	 */
	public Map<String, DeviceAppInfo> getInstalledApps() {
		Map<String, DeviceAppInfo> appList = new HashMap<String, DeviceAppInfo>();
		List<PackageInfo> packages = packageManager.getInstalledPackages(SYSTEM_APPS_DISABLED_FLAG);
		DeviceAppInfo app;

		for (PackageInfo packageInfo : packages) {
			if(!isSystemPackage(packageInfo)) {
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
			if(packageInfo != null) {
				return true;
			}
		} catch (PackageManager.NameNotFoundException e) {
			return false;
		}
		return false;
	}

	/**
	 * Returns whether the app is a system app.
	 * @param packageInfo - Package of the app which you need the status.
	 * @return - App status.
	 */
	private boolean isSystemPackage(PackageInfo packageInfo) {
		return ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0);
	}

	/**
	 * Returns a base64 encoded string for a particular image.
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
	 * @param url - APK Url should be passed in as a String.
	 */
	public void installApp(String url) {
		AppUpdater updator = new AppUpdater();
		updator.setContext(context);
		updator.execute(url);
	}

	/**
	 * Removes an application from the device.
	 * @param packageName - Application package name should be passed in as a String.
	 */
	public void uninstallApplication(String packageName) {
		if (packageName != null &&
		    !packageName.contains(resources.getString(R.string.application_package_prefix))) {
			packageName = resources.getString(R.string.application_package_prefix) + packageName;
		}

		if (Constants.SYSTEM_APP_ENABLED) {
			CommonUtils.callSystemApp(context, Constants.Operation.SILENT_UNINSTALL_APPLICATION, null, packageName);
		} else {
			Uri packageURI = Uri.parse(packageName);
			Intent uninstallIntent = new Intent(Intent.ACTION_DELETE, packageURI);
			uninstallIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(uninstallIntent);
		}
	}

	/**
	 * Creates a webclip on the device home screen.
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

	/**
	 * Installs or updates an application to the device.
	 * @param - APK Url should be passed in as a String.
	 */
	public class AppUpdater extends AsyncTask<String, Void, Void> {
		private Context context;

		public void setContext(Context context) {
			this.context = context;
		}

		@Override
		protected Void doInBackground(String... inputData) {
			FileOutputStream outStream=null;
			InputStream inStream=null;
			try {
				HttpGet httpGet = new HttpGet(inputData[BUFFER_OFFSET]);
				HttpClient httpClient = ServerUtilities.getCertifiedHttpClient();
				HttpResponse response = httpClient.execute(httpGet);
				
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

				inStream = response.getEntity().getContent();

				byte[] buffer = new byte[BUFFER_SIZE];
				int lengthFile;
				
				while ((lengthFile = inStream.read(buffer)) != READ_FAILED) {
					outStream.write(buffer, BUFFER_OFFSET, lengthFile);
				}

				String filePath = directory + resources.getString(R.string.application_mgr_download_file_name);
				Uri fileUri =  Uri.fromFile(new File(filePath));
				if (Constants.SYSTEM_APP_ENABLED) {
					CommonUtils.callSystemApp(context, Constants.Operation.SILENT_INSTALL_APPLICATION, null, fileUri.toString());
				} else {
					Intent intent = new Intent(Intent.ACTION_VIEW);
					intent.setDataAndType(fileUri, resources.getString(R.string.application_mgr_mime));
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					context.startActivity(intent);
				}
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
	};

}
