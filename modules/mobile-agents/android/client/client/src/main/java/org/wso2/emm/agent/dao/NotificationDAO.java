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

package org.wso2.emm.agent.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import org.wso2.emm.agent.beans.Notification;
import org.wso2.emm.agent.utils.Constants;
import org.wso2.emm.agent.utils.DatabaseHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents the key operations associated with persisting notification related information.
 */
public class NotificationDAO {

    private SQLiteDatabase db;
    private DatabaseHelper dbHelper;
    private String[] allColumns = { Constants.NotificationTable.ID, Constants.NotificationTable.MESSAGE,
                                    Constants.NotificationTable.RECEIVED_TIME, Constants.NotificationTable.STATUS,
                                    Constants.NotificationTable.RESPONSE_TIME};

    public NotificationDAO(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public void open() throws SQLException {
        db = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public void addNotification(Notification notification) {
        ContentValues values = new ContentValues();
        values.put(Constants.NotificationTable.ID, notification.getId());
        values.put(Constants.NotificationTable.MESSAGE, notification.getMessage());
        values.put(Constants.NotificationTable.RECEIVED_TIME, notification.getReceivedTime());
        values.put(Constants.NotificationTable.STATUS, notification.getStatus().toString());

        if (notification.getResponseTime() != null) {      // this can be null at the time of initiating
            values.put(Constants.NotificationTable.RESPONSE_TIME, notification.getResponseTime());
        }
        db.insert(Constants.NotificationTable.NAME, null, values);
    }

    public Notification getNotification(int id) {
        Cursor result =  db.rawQuery( "SELECT * FROM " + Constants.NotificationTable.NAME + " WHERE id = " + id, null);
        if (result.getCount() > 0) {
            result.moveToFirst();
            return cursorToNotification(result);
        }
        return null;
    }

    public void updateNotification(int id, Notification.Status status) {
        Cursor result =  db.rawQuery( "UPDATE " + Constants.NotificationTable.NAME + " SET status = '" + status.toString() +
                                      "' WHERE id = " + id, null);
        result.close();
    }

    public List<Notification> getAllNotifications() {
        List<Notification> notifications = new ArrayList<Notification>();
        //Cursor cursor = db.query(Constants.NotificationTable.NAME, allColumns, null, null, null, null, null);
        Cursor result = db.rawQuery("SELECT * FROM " + Constants.NotificationTable.NAME, null);
        result.moveToFirst();
        while (!result.isAfterLast()) {
            Notification comment = cursorToNotification(result);
            notifications.add(comment);
            result.moveToNext();
        }
        result.close();
        return notifications;
    }

    public List<Notification> getAllDismissedNotifications() {
        List<Notification> notifications = new ArrayList<Notification>();
        //Cursor cursor = db.query(Constants.NotificationTable.NAME, allColumns, null, null, null, null, null);
        Cursor result = db.rawQuery("SELECT * FROM " + Constants.NotificationTable.NAME + " WHERE status = 'DISMISSED'", null);
        result.moveToFirst();
        while (!result.isAfterLast()) {
            Notification comment = cursorToNotification(result);
            notifications.add(comment);
            result.moveToNext();
        }
        result.close();
        return notifications;
    }

    private Notification cursorToNotification(Cursor cursor) {
        Notification notification = new Notification();
        notification.setId(cursor.getInt(0));
        notification.setMessage(cursor.getString(1));
        notification.setReceivedTime(cursor.getString(2));
        notification.setStatus(Notification.Status.valueOf(cursor.getString(3)));
        notification.setReceivedTime(cursor.getString(4));
        return notification;
    }
}
