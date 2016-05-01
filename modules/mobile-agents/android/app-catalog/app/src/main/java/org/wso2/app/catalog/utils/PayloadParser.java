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

import android.content.Context;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.wso2.app.catalog.R;
import org.wso2.app.catalog.beans.Application;
import org.wso2.app.catalog.beans.ServerConfig;

import java.util.ArrayList;

/**
 * This class handles parsing of the service payloads.
 */
public class PayloadParser {

    public static Application parseApplication (JSONObject payload, Context context) throws JSONException {
        Application application = new Application();
        ServerConfig utils = new ServerConfig();
        String serverUrl = Preference.getString(context, context.getResources().getString(R.string.emm_server_url));
        if (serverUrl == null) {
            serverUrl = utils.getAPIServerURL(context);
        }

        if (payload != null) {
            if (!payload.isNull(Constants.ApplicationPayload.ID)) {
                application.setId(payload.getString(Constants.ApplicationPayload.ID));
            }

            if (!payload.isNull(Constants.ApplicationPayload.NAME)) {
                application.setName(payload.getString(Constants.ApplicationPayload.NAME));
            }

            if (!payload.isNull(Constants.ApplicationPayload.CONTEXT)) {
                application.setContext(payload.getString(Constants.ApplicationPayload.CONTEXT));
            }

            if (!payload.isNull(Constants.ApplicationPayload.TYPE)) {
                application.setType(payload.getString(Constants.ApplicationPayload.TYPE));
            }

            if (!payload.isNull(Constants.ApplicationPayload.DISPLAY_NAME)) {
                application.setDisplayName(payload.getString(Constants.ApplicationPayload.DISPLAY_NAME));
            }

            if (!payload.isNull(Constants.ApplicationPayload.APP_TYPE)) {
                application.setAppType(payload.getString(Constants.ApplicationPayload.APP_TYPE));
            }

            JSONObject appMeta;
            if (!payload.isNull(Constants.ApplicationPayload.APP_META)) {
                appMeta = new JSONObject(payload.getString(Constants.ApplicationPayload.APP_META));
                if (!appMeta.isNull(Constants.ApplicationPayload.WEB_URL)) {
                    if(Constants.ApplicationPayload.TYPE_WEB_CLIP.equals(application.getAppType().trim())) {
                        application.setAppUrl(appMeta.getString(Constants.ApplicationPayload.WEB_URL));
                    } else {
                        application.setAppUrl(serverUrl + appMeta.getString(Constants.ApplicationPayload.WEB_URL));
                    }
                }

                if (!appMeta.isNull(Constants.ApplicationPayload.PACKAGE)) {
                    application.setPackageName(appMeta.getString(Constants.ApplicationPayload.PACKAGE));
                }
            }

            if (!payload.isNull(Constants.ApplicationPayload.VERSION)) {
                application.setVersion(payload.getString(Constants.ApplicationPayload.VERSION));
            }

            if (!payload.isNull(Constants.ApplicationPayload.PROVIDER)) {
                application.setProvider(payload.getString(Constants.ApplicationPayload.PROVIDER));
            }

            if (!payload.isNull(Constants.ApplicationPayload.ICON)) {
                application.setIcon(serverUrl + payload.getString(Constants.ApplicationPayload.ICON));
            }

            if (!payload.isNull(Constants.ApplicationPayload.DESCRIPTION)) {
                application.setDescription(payload.getString(Constants.ApplicationPayload.DESCRIPTION));
            }

            if (!payload.isNull(Constants.ApplicationPayload.CATEGORY)) {
                application.setCategory(payload.getString(Constants.ApplicationPayload.CATEGORY));
            }

            if (!payload.isNull(Constants.ApplicationPayload.THUMBNAIL_URL)) {
                application.setThumbnailUrl(serverUrl + payload.getString(Constants.ApplicationPayload.THUMBNAIL_URL));
            }

            if (!payload.isNull(Constants.ApplicationPayload.RECENT_CHANGES)) {
                application.setRecentChanges(payload.getString(Constants.ApplicationPayload.RECENT_CHANGES));
            }

            if (!payload.isNull(Constants.ApplicationPayload.BANNER)) {
                application.setBanner(serverUrl + payload.getString(Constants.ApplicationPayload.BANNER));
            }

            if (!payload.isNull(Constants.ApplicationPayload.PLATFORM)) {
                application.setPlatform(payload.getString(Constants.ApplicationPayload.PLATFORM));
            }

            if (!payload.isNull(Constants.ApplicationPayload.TAGS)) {
                JSONArray tags = new JSONArray(payload.getString(Constants.ApplicationPayload.TAGS));
                ArrayList<String> tagsList = new ArrayList<>();
                for (int i = 0; i < tags.length(); i++) {
                    String tag = tags.get(i).toString();
                    if (tag != null) {
                        tagsList.add(tag);
                    }
                }
                application.setTags(tagsList);
            }

            if (!payload.isNull(Constants.ApplicationPayload.BUNDLE_VERSION)) {
                application.setBundleversion(payload.getString(Constants.ApplicationPayload.BUNDLE_VERSION));
            }

            if (!payload.isNull(Constants.ApplicationPayload.SCREENSHOTS)) {
                JSONArray screenshots = new JSONArray(payload.getString(Constants.ApplicationPayload.SCREENSHOTS));
                ArrayList<String> screenshotsList = new ArrayList<>();
                for (int i = 0; i < screenshots.length(); i++) {
                    String screenshot = screenshots.get(i).toString();
                    if (screenshot != null) {
                        screenshotsList.add(serverUrl + screenshot);
                    }
                }
                application.setScreenshots(screenshotsList);
            }

            if (!payload.isNull(Constants.ApplicationPayload.CREATED_TIME)) {
                application.setCreatedtime(payload.getString(Constants.ApplicationPayload.CREATED_TIME));
            }
        }
        return  application;
    }
}
