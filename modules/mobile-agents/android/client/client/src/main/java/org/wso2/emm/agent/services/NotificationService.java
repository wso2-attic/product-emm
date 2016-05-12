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
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;
import org.wso2.emm.agent.AlertActivity;
import org.wso2.emm.agent.AndroidAgentException;
import org.wso2.emm.agent.R;
import org.wso2.emm.agent.beans.*;
import org.wso2.emm.agent.beans.Operation;
import org.wso2.emm.agent.dao.NotificationDAO;
import org.wso2.emm.agent.utils.Constants;
import org.wso2.emm.agent.utils.Preference;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * This class contains the all the operations that are related to
 * notification management
 */
public class NotificationService extends BroadcastReceiver {

    private NotificationDAO notificationDAO;
    private Context context;
    private static NotificationService notificationService;
    private NotificationManager notifyManager;

    private static final String TAG = NotificationService.class.getSimpleName();
    private static final int DEFAULT_VALUE = 0;
    private static final String STATUS = "status";
    private static final String TIMESTAMP = "timestamp";
    private static final String DEVICE_STATE = "deviceState";
    private static final String DEVICE_LOCKED = "locked";
    private static final String DEVICE_UNLOCKED = "unlocked";

    private NotificationService(Context context) {
        this.context = context;
        notificationDAO = new NotificationDAO(context);
        notifyManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    public static NotificationService getInstance(Context context) {
        if (notificationService == null) {
            synchronized (NotificationService.class) {
                if (notificationService == null) {
                    notificationService = new NotificationService(context);
                    context.registerReceiver(notificationService, new IntentFilter(Intent.CATEGORY_DEFAULT));
                }
            }
        }
        return notificationService;
    }

    /**
     * This method is used to add notification to the embedded db.
     * @param notificationId notification id (operation id).
     * @param message notification.
     * @param status current status of the notification.
     */
    public void addNotification(int notificationId, String message, Notification.Status status) {
        Notification notification = new Notification();
        notification.setId(notificationId);
        notification.setMessage(message);
        notification.setStatus(status);
        notification.setReceivedTime(Calendar.getInstance().getTime().toString());
        notificationDAO.open();
        if (notificationDAO.getNotification(notificationId) == null) {
            notificationDAO.addNotification(notification);
        }
        notificationDAO.close();
    }

    /**
     * This method is used to update the notification which is stored in the embedded db.
     * @param notificationId notification id (operation id).
     */
    public void updateNotification (int notificationId) {
        notificationDAO.open();
        notificationDAO.updateNotification(notificationId, Notification.Status.DISMISSED);
        notificationDAO.close();
    }

    /**
     * This method is used to post a notification in the device.
     *
     * @param operationId id of the calling notification operation.
     * @param message message to be displayed
     */
    public void showNotification(int operationId, String message) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Intent notification = new Intent(context, NotificationReceiver.class);
            notification.putExtra(Constants.OPERATION_ID, operationId);
            PendingIntent dismiss = PendingIntent.getBroadcast(context, operationId, notification, PendingIntent.FLAG_ONE_SHOT);
            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(context)
                            .setSmallIcon(R.drawable.notification)
                            .setContentTitle(context.getResources().getString(R.string.txt_notification))
                            .setContentText(message)
                            .setPriority(android.app.Notification.PRIORITY_MAX)
                            .setDefaults(android.app.Notification.DEFAULT_VIBRATE)
                            .setDefaults(android.app.Notification.DEFAULT_SOUND)
                            .setCategory(android.app.Notification.CATEGORY_CALL)
                            .setOngoing(true)
                            .setOnlyAlertOnce(true)
                            .setTicker(context.getResources().getString(R.string.txt_notification))
                            .addAction(R.drawable.abs__ic_clear, "Dismiss", dismiss);

            notifyManager.notify(operationId, mBuilder.build());

        } else {

            Intent intent = new Intent(context, AlertActivity.class);
            intent.putExtra(context.getResources().getString(R.string.intent_extra_message), message);
            intent.putExtra(context.getResources().getString(R.string.intent_extra_operation_id), operationId);
            intent.putExtra(context.getResources().getString(R.string.intent_extra_type),
                            context.getResources().getString(R.string.intent_extra_alert));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK |
                            Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
            context.startActivity(intent);
        }
    }

    /**
     * This method checks whether there are any previous notifications which were not sent
     * and send if found any.
     */
    public List<org.wso2.emm.agent.beans.Operation> checkPreviousNotifications() throws AndroidAgentException {
        notificationDAO.open();
        List<Notification> dismissedNotifications = notificationDAO.getAllDismissedNotifications();
        List<Operation> notificationOperations = new ArrayList<>();
        org.wso2.emm.agent.beans.Operation operation;
        for (Notification notification : dismissedNotifications) {
            operation = new org.wso2.emm.agent.beans.Operation();
            operation.setId(notification.getId());
            operation.setCode(Constants.Operation.NOTIFICATION);
            operation.setStatus(context.getResources().getString(R.string.operation_value_completed));
            operation.setOperationResponse(buildResponse(Notification.Status.DISMISSED));
            notificationOperations.add(operation);
            notificationDAO.updateNotification(notification.getId(), Notification.Status.SENT);
        }
        notificationDAO.close();
        return notificationOperations;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        int operationId = intent.getIntExtra(Constants.OPERATION_ID, DEFAULT_VALUE);
        if (Constants.DEBUG_MODE_ENABLED) {
            Log.d(TAG, "NotificationId: " + operationId);
        }
        updateNotification(operationId); // updating notification state to DISMISSED
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(operationId);
    }

    public String buildResponse(Notification.Status status) throws AndroidAgentException {
        JSONObject response = new JSONObject();
        try {
            boolean isLocked = Preference.getBoolean(context, Constants.IS_LOCKED);
            response.put(STATUS, status);
            response.put(TIMESTAMP, Calendar.getInstance().getTime().toString());
            if (Notification.Status.RECEIVED.equals(status)) {
                if (isLocked) {
                    response.put(DEVICE_STATE, DEVICE_LOCKED);
                } else {
                    response.put(DEVICE_STATE, DEVICE_UNLOCKED);
                }
            }
        } catch (JSONException e) {
            Log.e(TAG, "Error occurred while building notification response", e);
            throw new AndroidAgentException(e);
        }
        return response.toString();
    }
}
