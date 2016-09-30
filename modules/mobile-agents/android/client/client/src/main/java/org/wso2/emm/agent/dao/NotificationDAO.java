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
import android.util.Log;

import org.wso2.emm.agent.beans.Notification;
import org.wso2.emm.agent.utils.Constants;
import org.wso2.emm.agent.utils.DatabaseHelper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * This class represents the key operations associated with persisting notification related information.
 */
public class NotificationDAO {

    private SQLiteDatabase db;
    private DatabaseHelper dbHelper;

    public NotificationDAO(Context context) {
        dbHelper = DatabaseHelper.getInstance(context);
    }

    public synchronized void open() throws SQLException {
        db = dbHelper.getWritableDatabase();
        if(db != null){
            db.beginTransaction();
        }
    }

    public void close() {
        if(db != null && db.isOpen()){
            db.setTransactionSuccessful();
            db.endTransaction();
        }
    }

    public void addNotification(Notification notification) {
        ContentValues values = new ContentValues();
        values.put(Constants.NotificationTable.ID, notification.getId());
        values.put(Constants.NotificationTable.MESSAGE_TITLE, notification.getMessageTitle());
        values.put(Constants.NotificationTable.MESSAGE_TEXT, notification.getMessageText());
        values.put(Constants.NotificationTable.RECEIVED_TIME, notification.getReceivedTime());
        values.put(Constants.NotificationTable.STATUS, notification.getStatus().toString());
        values.put(Constants.NotificationTable.RESPONSE_TIME, notification.getStatus().toString());
        db.insert(Constants.NotificationTable.NAME, null, values);
    }

    public Notification getNotification(int id) {
        Cursor result =  db.rawQuery("SELECT * FROM " + Constants.NotificationTable.NAME + " WHERE id = " + id, null);
        if (result.getCount() > 0) {
            result.moveToFirst();
            return cursorToNotification(result);
        }
        return null;
    }

    public boolean updateNotification(int id, Notification.Status status) {
        ContentValues args = new ContentValues();
        args.put(Constants.NotificationTable.STATUS, status.toString());
        args.put(Constants.NotificationTable.RESPONSE_TIME, Calendar.getInstance().getTime().toString());
        return db.update(Constants.NotificationTable.NAME, args, "id" + "=" + id, null) > 0;
    }

    public List<Notification> getAllNotifications() {
        List<Notification> notifications = new ArrayList<Notification>();
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
        notification.setMessageTitle(cursor.getString(1));
        notification.setMessageText(cursor.getString(2));
        notification.setReceivedTime(cursor.getString(3));
        notification.setStatus(Notification.Status.valueOf(cursor.getString(4)));
        notification.setReceivedTime(cursor.getString(5));
        return notification;
    }

}
