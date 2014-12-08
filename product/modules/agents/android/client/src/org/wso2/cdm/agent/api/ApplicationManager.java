/*
 ~ Copyright (c) 2014, WSO2 Inc. (http://wso2.com/) All Rights Reserved.
 ~
 ~ Licensed under the Apache License, Version 2.0 (the "License");
 ~ you may not use this file except in compliance with the License.
 ~ You may obtain a copy of the License at
 ~
 ~      http://www.apache.org/licenses/LICENSE-2.0
 ~
 ~ Unless required by applicable law or agreed to in writing, software
 ~ distributed under the License is distributed on an "AS IS" BASIS,
 ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 ~ See the License for the specific language governing permissions and
 ~ limitations under the License.
*/
package org.wso2.cdm.agent.api;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.wso2.cdm.agent.R;
import org.wso2.cdm.agent.models.PInfo;
import org.wso2.cdm.agent.utils.CommonUtilities;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.Browser;
import android.util.Base64;
import android.util.Log;

public class ApplicationManager {
	Context context = null;

	public ApplicationManager(Context context) {
		this.context = context;
	}

	/**
	 * Returns a list of all the applications installed on the device
	 */
	public String[] getApplicationListasArray() {
		PackageManager pm = context.getPackageManager();
		List<ApplicationInfo> apps = pm.getInstalledApplications(0);
		String applicationNames[] = new String[apps.size()];
		for (int j = 0; j < apps.size(); j++) {
			applicationNames[j] = apps.get(j).packageName;
		}
		return applicationNames;
	}

	public ArrayList<PInfo> getInstalledApps(boolean getSysPackages) {
		ArrayList<PInfo> res = new ArrayList<PInfo>();
		List<PackageInfo> packs = context.getPackageManager()
				.getInstalledPackages(0);
		for (int i = 0; i < packs.size(); i++) {
			PackageInfo p = packs.get(i);
			ApplicationInfo a = p.applicationInfo;
			//if ((!getSysPackages) && (p.versionName == null)) {
			if ((!getSysPackages) &&  ((a.flags & ApplicationInfo.FLAG_SYSTEM) == 1)) {
				continue;
			}
			PInfo newInfo = new PInfo();
			newInfo.appname = p.applicationInfo.loadLabel(
					context.getPackageManager()).toString();
			newInfo.pname = p.packageName;
			//newInfo.pname = "";
			newInfo.versionName = p.versionName;
			newInfo.versionCode = p.versionCode;
			newInfo.icon = "";
			// newInfo.icon =
			// encodeImage(p.applicationInfo.loadIcon(context.getPackageManager()));
			res.add(newInfo);
		}
		return res;
	}
	
	public String getAppNameFromPackage(String packageName) {
		boolean getSysPackages = true;
		String appName = "";
		List<PackageInfo> packs = context.getPackageManager()
				.getInstalledPackages(0);
		for (int i = 0; i < packs.size(); i++) {
			PackageInfo p = packs.get(i);
			if ((!getSysPackages) && (p.versionName == null)) {
				continue;
			}
			
			if(packageName.equals(p.packageName)){
				appName = p.applicationInfo.loadLabel(
						context.getPackageManager()).toString();
			}
		}
		return appName;
	}

	public String encodeImage(Drawable drawable) {
		Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
		// Bitmap bitmap =
		// ((BitmapDrawable)context.getResources().getDrawable(R.drawable.dot)).getBitmap();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos); // bm is the
																// bitmap object
		byte[] b = baos.toByteArray();
		String encodedImage = Base64.encodeToString(b, Base64.NO_WRAP);
		/*
		 * ByteArrayOutputStream stream = new ByteArrayOutputStream();
		 * bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream); byte[]
		 * bitMapData = stream.toByteArray();
		 */
		// Log.e("BEFORE JSON : ", encodedImage);
		return encodedImage;
	}

	/**
	 * Installs an application to the device
	 * 
	 * @param url
	 *            - APK Url should be passed in as a String
	 */
	public void installApp(String url) {
		UpdateApp updator = new UpdateApp();
		updator.setContext(context);
		updator.execute(url);
	}

	/**
	 * Uninstalls an application from the device
	 * 
	 * @param url
	 *            - Application package name should be passed in as a String
	 */
	public void unInstallApplication(String packageName)// Specific package Name
														// Uninstall.
	{
		// Uri packageURI = Uri.parse("package:com.CheckInstallApp");
		if (!packageName.contains(context.getResources().getString(R.string.application_package_prefix))) {
			packageName = context.getResources().getString(R.string.application_package_prefix) + packageName;
		}
		Uri packageURI = Uri.parse(packageName.toString());
		Intent uninstallIntent = new Intent(Intent.ACTION_DELETE, packageURI);
		uninstallIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(uninstallIntent);
	}
	
	/**
	 * Creates a webclip on the device home screen
	 * 
	 * @param url
	 *            - Url should be passed in as a String
	 *            - Title(Web app title) should be passed in as a String
	 */
	public void createWebAppBookmark(String url, String title){
		  final Intent in = new Intent();
		  final Intent shortcutIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
		  long urlHash = url.hashCode();
		  long uniqueId = (urlHash << 32) | shortcutIntent.hashCode();
		  shortcutIntent.putExtra(Browser.EXTRA_APPLICATION_ID, Long.toString(uniqueId));
		  in.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
		  in.putExtra(Intent.EXTRA_SHORTCUT_NAME, title);
		  in.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
		                    Intent.ShortcutIconResource.fromContext(
		                            context,
		                            R.drawable.ic_bookmark));
		  in.setAction(context.getResources().getString(R.string.application_package_launcher_action)); 
		//or   in.setAction(Intent.ACTION_CREATE_SHORTCUT); 

		  context.sendBroadcast(in);
	}

	/**
	 * Installs or updates an application to the device
	 * 
	 * @param url
	 *            - APK Url should be passed in as a String
	 */
	public class UpdateApp extends AsyncTask<String, Void, Void> {
		private Context context;

		public void setContext(Context contextf) {
			context = contextf;
		}

		@Override
		protected Void doInBackground(String... arg0) {
			try {
				URL url = new URL(arg0[0]);
				HttpURLConnection c = (HttpURLConnection) url.openConnection();
				c.setRequestMethod(context.getResources().getString(R.string.server_util_req_type_get));
				c.setDoOutput(true);
				c.connect();

				String PATH = Environment.getExternalStorageDirectory()
						.getPath() + context.getResources().getString(R.string.application_mgr_download_location);
				File file = new File(PATH);
				file.mkdirs();
				File outputFile = new File(file, context.getResources().getString(R.string.application_mgr_download_file_name));
				if (outputFile.exists()) {
					outputFile.delete();
				}
				FileOutputStream fos = new FileOutputStream(outputFile);

				InputStream is = c.getInputStream();

				byte[] buffer = new byte[1024];
				int len1 = 0;
				while ((len1 = is.read(buffer)) != -1) {
					fos.write(buffer, 0, len1);
				}
				fos.close();
				is.close();

				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setDataAndType(
						Uri.fromFile(new File(PATH + context.getResources().getString(R.string.application_mgr_download_file_name))),
						context.getResources().getString(R.string.application_mgr_mime));
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(intent);

			} catch (Exception e) {
				if(CommonUtilities.DEBUG_MODE_ENABLED){
					Log.e("UpdateAPP", "Update error! " + e.getMessage());
				}
			}
			return null;
		}
	};

}
