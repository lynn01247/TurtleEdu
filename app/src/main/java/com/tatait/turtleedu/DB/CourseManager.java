package com.tatait.turtleedu.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.tatait.turtleedu.model.Course;

import java.util.ArrayList;

/**
 * Created by Lynn on 2016/3/3.
 */
public class CourseManager {
    private static CourseManager sInstance = null;
    private TurtleDB mTurtleDatabase = null;

    public CourseManager(final Context context) {
        mTurtleDatabase = TurtleDB.getInstance(context);
    }

    public static final synchronized CourseManager getInstance(final Context context) {
        if (sInstance == null) {
            sInstance = new CourseManager(context.getApplicationContext());
        }
        return sInstance;
    }

    public void onCreate(final SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + CourseColumns.NAME + " ("
                + CourseColumns.CID + " LONG NOT NULL," + CourseColumns.CNAME + " CHAR NOT NULL,"
                + CourseColumns.COUNT + " INT NOT NULL, " + CourseColumns.IS_STUDY + " CHAR NOT NULL);");
    }

    public void onUpgrade(final SQLiteDatabase db, final int oldVersion, final int newVersion) {
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + CourseColumns.NAME);
        onCreate(db);
    }

    public synchronized void addCourselists(ArrayList<Course.CourseData> courseDatas) {
        final SQLiteDatabase database = mTurtleDatabase.getWritableDatabase();
        database.beginTransaction();

        try {
            for (int i = 0; i < courseDatas.size(); i++) {
                ContentValues values = new ContentValues(4);
                values.put(CourseColumns.CID, courseDatas.get(i).cid);
                values.put(CourseColumns.CNAME, courseDatas.get(i).name);
                values.put(CourseColumns.COUNT, courseDatas.get(i).count);
                values.put(CourseColumns.IS_STUDY, "0");

                database.insert(CourseColumns.NAME, null, values);
            }
            database.setTransactionSuccessful();
        } finally {
            database.endTransaction();
        }
    }

    public synchronized void update(long cid, String is_study) {
        final SQLiteDatabase database = mTurtleDatabase.getWritableDatabase();
        database.beginTransaction();
        try {
            ContentValues values = new ContentValues(3);
            values.put(CourseColumns.IS_STUDY, is_study);
            database.update(CourseColumns.NAME, values, CourseColumns.CID + " = " + cid, null);
            database.setTransactionSuccessful();
        } finally {
            database.endTransaction();
        }
    }

    public synchronized int getStudyNum(long cid) {
        Cursor cursor = null;
        int num = 0;
        final SQLiteDatabase database = mTurtleDatabase.getWritableDatabase();
        database.beginTransaction();
        try {
            cursor = database.query(CourseColumns.NAME, null, CourseColumns.CID + " = ?", new String[]{cid + ""}, null, null, null);
            database.setTransactionSuccessful();
            if (cursor != null && cursor.moveToFirst()) {
                num = Integer.parseInt(cursor.getString(3));
            }
            return num;
        } finally {
            database.endTransaction();
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public synchronized ArrayList<Course.CourseData> getCourseList() {
        ArrayList<Course.CourseData> results = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = mTurtleDatabase.getReadableDatabase().query(CourseColumns.NAME, null,
                    null, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                results.ensureCapacity(cursor.getCount());
                do {
                    results.add(new Course.CourseData(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3)));
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

    public void deleteAll() {
        final SQLiteDatabase database = mTurtleDatabase.getWritableDatabase();
        database.delete(CourseColumns.NAME, null, null);
    }

    private interface CourseColumns {
        /* Table name */
        String NAME = "course";
        /* Course key */
        String CID = "cid";
        /* column */
        String CNAME = "cname";
        String COUNT = "count";
        String IS_STUDY = "is_study";
    }
}