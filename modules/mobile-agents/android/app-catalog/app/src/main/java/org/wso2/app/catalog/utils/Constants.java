/*
 * Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.app.catalog.utils;

/**
 * This class holds all the constants used throughout the application.
 */
public class Constants {
	public static final boolean DEBUG_MODE_ENABLED = false;
	public static final String AGENT_PACKAGE_NAME = "org.wso2.emm.agent";
	public static final String AGENT_APP_SERVICE_NAME = "org.wso2.emm.agent.START_SERVICE";
	public static final String AGENT_APP_ACTION_RESPONSE = "org.wso2.emm.agent.MESSAGE_PROCESSED";
	public static final String ACTION_RESPONSE = "org.wso2.app.catalog.MESSAGE_PROCESSED";
	public static final String SERVER_PORT = "9763";
	public static final String SERVER_PROTOCOL = "http://";
	public static final String SERVER_ADDRESS = "SERVER_ADDRESS";
	public static final String SERVER_APP_ENDPOINT = "/api/appm/";
	public static final String OAUTH_ENDPOINT = "/oauth2/token";
	public static final String GOOGLE_PLAY_APP_URI = "market://details?id=";
	public static final String APP_LIST_ENDPOINT = "/api/appm/publisher/v1.0/apps/mobileapp?field-filter=all";
	public static final String APP_IMAGE_ENDPOINT = "/publisher/api/mobileapp/getfile/";
	// This is set to override the server host name retrieving screen. If overriding is not
	// needed, set this to null.
	public static final String DEFAULT_HOST = null;
	public static final String DYNAMIC_CLIENT_REGISTER_ENDPOINT = SERVER_APP_ENDPOINT + "oauth/v1.0/register";
	public static final String TRUSTSTORE_PASSWORD = "wso2carbon";
	public static final String EMPTY_STRING = "";
	public static final String USERNAME = "username";
	public static final String STATUS = "status";
	public static final String RESPONSE = "response";
	public static final String CLIENT_ID = "clientId";
	public static final String CLIENT_SECRET = "clientSecret";
	public static final String CLIENT_NAME = "clientName";
	public static final String GRANT_TYPE = "password refresh_token";
	public static final String TOKEN_SCOPE = "Production";
	public static final String USER_AGENT = "Mozilla/5.0 ( compatible ), Android";
	public static final String PACKAGE_NAME = "org.wso2.app.catalog";
	public static final String INSTALL_BUTTON_COLOR = "#11375B";
	public static final String UNINSTALL_BUTTON_COLOR = "#a70d24";
	public static final String INTENT_KEY_PAYLOAD = "payload";
	public static final String INTENT_KEY_STATUS = "status";
	public static final String INTENT_KEY_SERVER = "server";

	/**
	 * Request codes.
	 */
	public static final int APP_LIST_REQUEST_CODE = 300;
	public static final int DYNAMIC_CLIENT_REGISTER_REQUEST_CODE = 301;
	public static final int DYNAMIC_CLIENT_UNREGISTER_REQUEST_CODE = 302;

	/**
	 * Operation IDs
	 */
	public final class Operation {
		private Operation() {
			throw new AssertionError();
		}
		public static final String GET_APPLICATION_LIST = "GET_APP_LIST";
		public static final String INSTALL_APPLICATION = "INSTALL_APPLICATION";
		public static final String UNINSTALL_APPLICATION = "UNINSTALL_APPLICATION";
		public static final String WEBCLIP = "WEBCLIP";
		public static final String UNINSTALL_WEBCLIP = "UNINSTALL_WEBCLIP";
		public static final String GET_APP_DOWNLOAD_PROGRESS = "APP_DOWNLOAD_PROGRESS";
	}
	/**
	 * Status codes
	 */
	public final class Status {
		private Status(){
			throw new AssertionError();
		}
		public static final String SUCCESSFUL = "200";
		public static final String CREATED = "201";
		public static final String ACCEPT = "202";
		public static final String AUTHENTICATION_FAILED = "400";
		public static final String INTERNAL_SERVER_ERROR = "500";
	}

	public final class PreferenceFlag {
		private PreferenceFlag() {
			throw new AssertionError();
		}
		public static final String IP = "ip";
		public static final String PORT = "serverPort";
		public static final String PROTOCOL = "serverProtocol";
	}

	public final class ApplicationPayload {
		private ApplicationPayload() {
			throw new AssertionError();
		}
		public static final String ID = "id";
		public static final String NAME = "name";
		public static final String CONTEXT = "context";
		public static final String TYPE = "type";
		public static final String DISPLAY_NAME = "displayName";
		public static final String VERSION = "version";
		public static final String PROVIDER = "provider";
		public static final String ICON = "icon";
		public static final String DESCRIPTION = "description";
		public static final String CATEGORY = "category";
		public static final String THUMBNAIL_URL = "thumbnailUrl";
		public static final String RECENT_CHANGES = "recentChanges";
		public static final String BANNER = "banner";
		public static final String PLATFORM = "platform";
		public static final String TAGS = "tags";
		public static final String BUNDLE_VERSION = "bundleversion";
		public static final String SCREENSHOTS = "screenshots";
		public static final String CREATED_TIME = "createdtime";
		public static final String APP_TYPE = "appType";
		public static final String APP_LIST = "appList";
		public static final String APP_META = "appmeta";
		public static final String WEB_URL = "weburl";
		public static final String PACKAGE = "package";
		public static final String TYPE_MOBILE_APP = "enterprise";
		public static final String TYPE_WEB_CLIP = "webapp";
	}
}
