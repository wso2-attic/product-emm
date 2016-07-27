/*
 * Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.mdm.qsg;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.wso2.mdm.qsg.dto.EMMConfig;
import org.wso2.mdm.qsg.dto.HTTPResponse;
import org.wso2.mdm.qsg.dto.MobileApplication;
import org.wso2.mdm.qsg.utils.Constants;
import org.wso2.mdm.qsg.utils.HTTPInvoker;
import org.wso2.mdm.qsg.utils.QSGUtils;

import java.io.File;
import java.util.HashMap;

/**
 * Created by harshan on 7/25/16.
 */
public class AppOperations {

    public static MobileApplication uploadApplication (String platform, String appName, String appContentType) {
        String appUploadEndpoint = EMMConfig.getInstance().getEmmHost() + "/api/appm/publisher/v1.0/apps/mobile/binaries";
        String filePath = "apps" + File.separator+ platform + File.separator + appName;
        HTTPResponse
                httpResponse = HTTPInvoker.uploadFile(appUploadEndpoint, filePath, appContentType);
        if (httpResponse.getResponseCode() == 200) {
            System.out.println(httpResponse.getResponse());
            JSONObject appMeta = null;
            MobileApplication application = new MobileApplication();
            try {
                appMeta = (JSONObject) new JSONParser().parse(httpResponse.getResponse());
                application.setPackageId((String) appMeta.get("package"));
                application.setAppId(QSGUtils.getResourceId((String) appMeta.get("path")));
                application.setVersion((String) appMeta.get("version"));
                application.setPlatform(platform);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return application;
        }
        return null;
    }

    private static String uploadAsset (String path) {
        String resUploadEndpoint = EMMConfig.getInstance().getEmmHost() + "/api/appm/publisher/v1.0/apps/static-contents";
        HTTPResponse httpResponse = HTTPInvoker.uploadFile(resUploadEndpoint, path, "image/jpeg");
        if (httpResponse.getResponseCode() == 200) {
            System.out.println(httpResponse.getResponse());
            JSONObject resp = null;
            try {
                resp = (JSONObject) new JSONParser().parse(httpResponse.getResponse());
                return QSGUtils.getResourceId((String) resp.get("path"));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static MobileApplication uploadAssets (String platform, MobileApplication application) {
        String assetDir = "apps" + File.separator + platform + File.separator + "images";
        //Upload the icon file
        String imgFile = assetDir + File.separator + "icon.jpg";
        String uploadPath = uploadAsset(imgFile);
        if (uploadPath != null && !uploadPath.isEmpty()) {
            application.setIcon(uploadPath);
        } else {
            System.out.println("Unable to upload the app icon file.");
            return null;
        }

        //Upload the banner file
        imgFile = assetDir + File.separator + "banner.jpg";
        uploadPath = uploadAsset(imgFile);
        if (uploadPath != null && !uploadPath.isEmpty()) {
            application.setBanner(uploadPath);
        } else {
            System.out.println("Unable to upload the app banner file.");
            return null;
        }

        //Upload the screenshot1 file
        imgFile = assetDir + File.separator + "screen1.jpg";
        uploadPath = uploadAsset(imgFile);
        if (uploadPath != null && !uploadPath.isEmpty()) {
            application.setScreenshot1(uploadPath);
        } else {
            System.out.println("Unable to upload the app screenshot1 file.");
            return null;
        }

        //Upload the screenshot2 file
        imgFile = assetDir + File.separator + "screen2.jpg";
        uploadPath = uploadAsset(imgFile);
        if (uploadPath != null && !uploadPath.isEmpty()) {
            application.setScreenshot2(uploadPath);
        } else {
            System.out.println("Unable to upload the app screenshot2 file.");
            return null;
        }

        //Upload the screenshot3 file
        imgFile = assetDir + File.separator + "screen3.jpg";
        uploadPath = uploadAsset(imgFile);
        if (uploadPath != null && !uploadPath.isEmpty()) {
            application.setScreenshot3(uploadPath);
        } else {
            System.out.println("Unable to upload the app screenshot3 file.");
            return null;
        }
        return application;
    }

    public static boolean addApplication (String name, MobileApplication mblApp) {
        HashMap<String, String> headers = new HashMap<String, String>();
        String appEndpoint = EMMConfig.getInstance().getEmmHost() + "/api/appm/publisher/v1.0/apps/mobileapp";
        //Set the application payload
        JSONObject application = new JSONObject();
        application.put("name", name);
        application.put("description", "Sample application");
        application.put("type", "mobileapp");
        application.put("marketType", "enterprise");
        application.put("provider", "admin");
        application.put("displayName", name);
        application.put("category", "Business");
        application.put("icon", mblApp.getIcon());
        application.put("version", "1.0.0");
        application.put("banner", mblApp.getBanner());
        application.put("platform", mblApp.getPlatform());
        application.put("appType", mblApp.getPlatform());
        //application.put("appUrL", mblApp.getAppId());
        application.put("mediaType", "application/vnd.wso2-mobileapp+xml");

        //Set appMeta data
        JSONObject appMeta = new JSONObject();
        appMeta.put("path", mblApp.getAppId());
        appMeta.put("package", mblApp.getPackageId());
        appMeta.put("version", mblApp.getVersion());
        //Set screenshots
        JSONArray screenshots = new JSONArray();
        screenshots.add(mblApp.getScreenshot1());
        screenshots.add(mblApp.getScreenshot2());
        screenshots.add(mblApp.getScreenshot3());
        application.put("appmeta", appMeta);
        application.put("screenshots", screenshots);

        System.out.println(application.toString());
        //Set the headers
        headers.put(Constants.CONTENT_TYPE_HEADER, Constants.APPLICATION_JSON);
        HTTPResponse
                httpResponse = HTTPInvoker.sendHTTPPostWithOAuthSecurity(appEndpoint, application.toJSONString(), headers);
        if (httpResponse.getResponseCode() == 200) {
            return true;
        }
        return false;
    }
}
