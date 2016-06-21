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

package org.wso2.emm.agent.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * This class is used to create database for the agent app.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = DatabaseHelper.class.getSimpleName();

    private static final String DATABASE_NAME = "emm_db";
    private static final int DATABASE_VERSION = 1;

    // Information related to the notification table
    private static final String CREATE_NOTIFICATION_TABLE = "CREATE TABLE " + Constants.NotificationTable.NAME +
                                                            "(" + Constants.NotificationTable.ID + " integer primary key, " +
                                                            Constants.NotificationTable.MESSAGE_TITLE + " text not null, " +
                                                            Constants.NotificationTable.MESSAGE_TEXT + " text not null, " +
                                                            Constants.NotificationTable.RECEIVED_TIME + " text not null, " + // sqlite does not support date
                                                            Constants.NotificationTable.STATUS + " text, " +
                                                            Constants.NotificationTable.RESPONSE_TIME + " text)";
    private static final String DROP_NOTIFICATION_TABLE = "DROP TABLE IF EXISTS " + Constants.NotificationTable.NAME;


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        if (Constants.DEBUG_MODE_ENABLED) {
            Log.d(TAG, "Adding tables");
        }
        db.execSQL(CREATE_NOTIFICATION_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (Constants.DEBUG_MODE_ENABLED) {
            Log.d(TAG, "Upgrading tables");
        }
        db.execSQL(DROP_NOTIFICATION_TABLE);
        onCreate(db);
    }

}
