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
package org.wso2.emm.agent.beans;

/**
 * This class represents the device application information.
 */
public class DeviceAppInfo {
	private String appName;
	private String packageName;
	private String versionName;
	private int versionCode;
	private String icon;
	private boolean isSystemApp;
	private boolean isRunning;

	public String getAppname() {
		return appName;
	}

	public void setAppname(String appname) {
		this.appName = appname;
	}

	public String getPackagename() {
		return packageName;
	}

	public void setPackagename(String pname) {
		this.packageName = pname;
	}

	public String getVersionName() {
		return versionName;
	}

	public void setVersionName(String versionName) {
		this.versionName = versionName;
	}

	public int getVersionCode() {
		return versionCode;
	}

	public void setVersionCode(int versionCode) {
		this.versionCode = versionCode;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public boolean isSystemApp() {
		return isSystemApp;
	}

	public void setIsSystemApp(boolean isSystemApp) {
		this.isSystemApp = isSystemApp;
	}

	public boolean isRunning() {
		return isRunning;
	}

	public void setIsRunning(boolean isRunning) {
		this.isRunning = isRunning;
	}
}
