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
package org.wso2.emm.system.service.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import java.util.List;

/**
 * This class represents all the common functions used throughout the application.
 */
public class CommonUtils {
    /**
     * Call EMM agent app to send operation updates.
     * @param context - Application context.
     * @param operation - Operation code.
     * @param operationId - Operation ID.
     * @param message - Error message.
     */
    public static void callAgentApp(Context context, String operation, int operationId, String message) {
        Intent intent =  new Intent(Constants.AGENT_APP_SERVICE_NAME);
        Intent explicitIntent = createExplicitFromImplicitIntent(context, intent);
        if (explicitIntent != null) {
            intent = explicitIntent;
        }
        intent.putExtra("code", operation);
        if (operationId != 0) {
            intent.putExtra("id", operationId);
        }
        if (message != null) {
            intent.putExtra("message", message);
        }
        intent.setPackage(Constants.PACKAGE_NAME);
        context.startService(intent);
    }

    public static Intent createExplicitFromImplicitIntent(Context context, Intent implicitIntent) {
        //Retrieve all services that can match the given intent
        PackageManager pm = context.getPackageManager();
        List<ResolveInfo> resolveInfo = pm.queryIntentServices(implicitIntent, 0);

        //Make sure only one match was found
        if (resolveInfo == null || resolveInfo.size() != 1) {
            return null;
        }

        //Get component info and create ComponentName
        ResolveInfo serviceInfo = resolveInfo.get(0);
        String packageName = serviceInfo.serviceInfo.packageName;
        String className = serviceInfo.serviceInfo.name;
        ComponentName component = new ComponentName(packageName, className);

        //Create a new intent. Use the old one for extras and such reuse
        Intent explicitIntent = new Intent(implicitIntent);

        //Set the component to be explicit
        explicitIntent.setComponent(component);

        return explicitIntent;
    }
}
