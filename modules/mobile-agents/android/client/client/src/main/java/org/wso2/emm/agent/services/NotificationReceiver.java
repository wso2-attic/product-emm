/*
 * Copyright (c) 2016, WSO2 Inc. (http:www.wso2.org) All Rights Reserved.
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
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.emm.agent.services;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import org.wso2.emm.agent.beans.Notification;
import org.wso2.emm.agent.dao.NotificationDAO;
import org.wso2.emm.agent.utils.Constants;

/**
 * This BroadcastReceiver is registered to receive the current action of a notification.
 */
public class NotificationReceiver extends BroadcastReceiver {

    private static final String TAG = NotificationReceiver.class.getName();
    private static final int DEFAULT_VALUE = 0;

    @Override
    public void onReceive(Context context, Intent intent) {
        int operationId = intent.getIntExtra(Constants.OPERATION_ID, DEFAULT_VALUE);
        if (Constants.DEBUG_MODE_ENABLED) {
            Log.d(TAG, "NotificationId: " + operationId);
        }
        updateNotification(context, operationId); // updating notification state to DISMISSED
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(operationId);
    }

    private void updateNotification (Context context, int operationId) {
        NotificationDAO notificationDAO = new NotificationDAO(context);
        notificationDAO.open();
        notificationDAO.updateNotification(operationId, Notification.Status.DISMISSED);
        notificationDAO.close();
    }

}