/*
* Copyright (C) 2014 The CyanogenMod Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package com.tatait.turtleedu.DB;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class TurtleDB extends SQLiteOpenHelper {

    private static final String DATABASENAME = "turtle.db";
    private static final int VERSION = 1;
    private static TurtleDB sInstance = null;

    private final Context mContext;

    public TurtleDB(final Context context) {
        super(context, DATABASENAME, null, VERSION);

        mContext = context;
    }

    public static final synchronized TurtleDB getInstance(final Context context) {
        if (sInstance == null) {
            sInstance = new TurtleDB(context.getApplicationContext());
        }
        return sInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        CourseManager.getInstance(mContext).onCreate(db);
        LessonsManager.getInstance(mContext).onCreate(db);
        TempManager.getInstance(mContext).onCreate(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        CourseManager.getInstance(mContext).onUpgrade(db, oldVersion, newVersion);
        LessonsManager.getInstance(mContext).onUpgrade(db, oldVersion, newVersion);
        TempManager.getInstance(mContext).onUpgrade(db, oldVersion, newVersion);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        CourseManager.getInstance(mContext).onDowngrade(db, oldVersion, newVersion);
        LessonsManager.getInstance(mContext).onDowngrade(db, oldVersion, newVersion);
        TempManager.getInstance(mContext).onDowngrade(db, oldVersion, newVersion);
    }
}