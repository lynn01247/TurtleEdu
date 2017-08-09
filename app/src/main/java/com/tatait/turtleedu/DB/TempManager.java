package com.tatait.turtleedu.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.tatait.turtleedu.model.Temp;
import com.tatait.turtleedu.utils.StringUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Lynn on 2016/3/3.
 */
public class TempManager {
    private static TempManager sInstance = null;
    private TurtleDB mTurtleDatabase = null;

    public TempManager(final Context context) {
        mTurtleDatabase = TurtleDB.getInstance(context);
    }

    public static final synchronized TempManager getInstance(final Context context) {
        if (sInstance == null) {
            sInstance = new TempManager(context.getApplicationContext());
        }
        return sInstance;
    }

    public void onCreate(final SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TempColumns.NAME + " ("
                + TempColumns.TID + " LONG NOT NULL," + TempColumns.TNAME + " CHAR NOT NULL,"
                + TempColumns.CODE + " CHAR NOT NULL, " + TempColumns.HISTORY + " CHAR NOT NULL, " + TempColumns.UPDATETIME + " CHAR NOT NULL);");
    }

    public void onUpgrade(final SQLiteDatabase db, final int oldVersion, final int newVersion) {
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TempColumns.NAME);
        onCreate(db);
    }

    public synchronized void addTemp(Temp temp) {
        final SQLiteDatabase database = mTurtleDatabase.getWritableDatabase();
        database.beginTransaction();

        try {
            ContentValues values = new ContentValues(4);
            values.put(TempColumns.TID, temp.getTid());
            values.put(TempColumns.TNAME, temp.getTname());
            values.put(TempColumns.CODE, temp.getCode());
            values.put(TempColumns.HISTORY, temp.getHistory());
            values.put(TempColumns.UPDATETIME, temp.getUpdate_time());

            database.insert(TempColumns.NAME, null, values);
            database.setTransactionSuccessful();
        } finally {
            database.endTransaction();
        }
    }

    public synchronized void update(long tid, String tname, String code, String history) {
        final SQLiteDatabase database = mTurtleDatabase.getWritableDatabase();
        database.beginTransaction();
        try {
            ContentValues values = new ContentValues(3);
            if (!StringUtils.isNullOrEmpty(tname)) values.put(TempColumns.TNAME, tname);
            if (!StringUtils.isNullOrEmpty(code)) values.put(TempColumns.CODE, code);
            if (!StringUtils.isNullOrEmpty(history)) values.put(TempColumns.HISTORY, history);
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
            String date = df.format(new Date());// new Date()为获取当前系统时间，也可使用当前时间戳
            values.put(TempColumns.UPDATETIME, date);
            database.update(TempColumns.NAME, values, TempColumns.TID + " = " + tid, null);
            database.setTransactionSuccessful();
        } finally {
            database.endTransaction();
        }
    }

    public synchronized ArrayList<Temp> getTempList() {
        ArrayList<Temp> results = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = mTurtleDatabase.getReadableDatabase().query(TempColumns.NAME, null,null, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                results.ensureCapacity(cursor.getCount());
                do {
                    results.add(new Temp(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4)));
                } while (cursor.moveToNext());
            }
            return results;
        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }
    }

    public synchronized int getTempCount() {
        Cursor cursor = null;
        try {
            cursor = mTurtleDatabase.getReadableDatabase().query(TempColumns.NAME, null,
                    null, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                return cursor.getCount();
            }
            return 0;
        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }
    }

    public void deleteById(String tid) {
        final SQLiteDatabase database = mTurtleDatabase.getWritableDatabase();
        database.delete(TempColumns.NAME, TempColumns.TID + " = ?", new String[]{String.valueOf(tid)});
    }
    public void deleteAll() {
        final SQLiteDatabase database = mTurtleDatabase.getWritableDatabase();
        database.delete(TempColumns.NAME, null, null);
    }

    private interface TempColumns {
        /* Table name */
        String NAME = "temp";
        /* Course key */
        String TID = "tid";
        /* column */
        String TNAME = "tname";
        String CODE = "code";
        String HISTORY = "history";
        String UPDATETIME = "update_time";
    }
}