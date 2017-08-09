package com.tatait.turtleedu.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.tatait.turtleedu.model.Lesson;

import java.util.ArrayList;

/**
 * Created by Lynn on 2016/3/3.
 */
public class LessonsManager {
    private static LessonsManager sInstance = null;

    private TurtleDB mTurtleDatabase = null;

    private LessonsManager(final Context context) {
        mTurtleDatabase = TurtleDB.getInstance(context);
    }

    public static final synchronized LessonsManager getInstance(final Context context) {
        if (sInstance == null) {
            sInstance = new LessonsManager(context.getApplicationContext());
        }
        return sInstance;
    }

    //建立课时表，设置课程表id和课时表id为复合主键
    public void onCreate(final SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + LessonsColumns.TNAME + " ("
                + LessonsColumns.CID + " LONG NOT NULL," + LessonsColumns.LID + " LONG," + LessonsColumns.NAME + " CHAR,"
                + LessonsColumns.CONTENT + " CHAR," + LessonsColumns.COMMAND + " CHAR," + LessonsColumns.TIPS + " CHAR,"
                + LessonsColumns.IS_STUDY + " CHAR," + "primary key ( " + LessonsColumns.CID
                + ", " + LessonsColumns.LID + "));");
    }

    public void onUpgrade(final SQLiteDatabase db, final int oldVersion, final int newVersion) {
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + LessonsColumns.TNAME);
        onCreate(db);
    }

    public ArrayList<Lesson.LessonData> getLesson(long cid) {
        ArrayList<Lesson.LessonData> results = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = mTurtleDatabase.getReadableDatabase().query(LessonsColumns.TNAME, null,
                    LessonsColumns.CID + " = " + String.valueOf(cid), null, null, null, LessonsColumns.LID + " ASC ", null);
            if (cursor != null && cursor.moveToFirst()) {
                results.ensureCapacity(cursor.getCount());
                do {
                    Lesson.LessonData lessonData = new Lesson.LessonData();
                    lessonData.cid = cursor.getString(0);
                    lessonData.lid = cursor.getString(1);
                    lessonData.name = cursor.getString(2);
                    lessonData.content = cursor.getString(3);
                    lessonData.command = cursor.getString(4);
                    lessonData.tips = cursor.getString(5);
                    lessonData.is_study = cursor.getString(6);
                    results.add(lessonData);
                } while (cursor.moveToNext());
            }
            return results;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public int getLessonSize(long cid) {
        Cursor cursor = null;
        try {
            cursor = mTurtleDatabase.getReadableDatabase().query(LessonsColumns.TNAME, null,
                    LessonsColumns.CID + " = " + String.valueOf(cid), null, null, null, null, null);
            if (cursor == null) {
                return 0;
            }
            return cursor.getCount();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public synchronized void insertLesson(long cid, Lesson.LessonData info) {
        if (info == null) {
            return;
        }
        final SQLiteDatabase database = mTurtleDatabase.getWritableDatabase();
        database.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(LessonsColumns.CID, cid);
            values.put(LessonsColumns.LID, info.lid);
            values.put(LessonsColumns.NAME, info.name);
            values.put(LessonsColumns.CONTENT, info.content);
            values.put(LessonsColumns.COMMAND, info.command);
            values.put(LessonsColumns.TIPS, info.tips);
            values.put(LessonsColumns.IS_STUDY, "000");
            if (!isExist(cid, Long.parseLong(info.lid)))
                database.insert(LessonsColumns.TNAME, null, values);
            database.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            database.endTransaction();
        }
    }

    public synchronized void insertLists(long cid, ArrayList<Lesson.LessonData> lessonDatas) {
        final SQLiteDatabase database = mTurtleDatabase.getWritableDatabase();
        database.beginTransaction();
        int len = lessonDatas.size();
        try {
            for (int i = 0; i < len; i++) {
                Lesson.LessonData info = lessonDatas.get(i);
                ContentValues values = new ContentValues();
                values.put(LessonsColumns.CID, cid);
                values.put(LessonsColumns.LID, info.lid);
                values.put(LessonsColumns.NAME, info.name);
                values.put(LessonsColumns.CONTENT, info.content);
                values.put(LessonsColumns.COMMAND, info.command);
                values.put(LessonsColumns.TIPS, info.tips);
                values.put(LessonsColumns.IS_STUDY, "000");
                if (!isExist(cid, Long.parseLong(info.lid)))
                    database.insert(LessonsColumns.TNAME, null, values);
            }
            database.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            database.endTransaction();
        }
    }

    public synchronized void update(long cid, long lid, String is_study) {
        final SQLiteDatabase database = mTurtleDatabase.getWritableDatabase();
        database.beginTransaction();
        try {
            ContentValues values = new ContentValues(3);
            values.put(LessonsColumns.IS_STUDY, is_study);
            database.update(LessonsColumns.TNAME, values, LessonsColumns.CID + " = ?" + " AND " +
                    LessonsColumns.LID + " = ?", new String[]{cid + "", lid + ""});
            database.setTransactionSuccessful();
        } finally {
            database.endTransaction();
        }
    }

    public synchronized boolean isStudy(long cid, long lid) {
        Cursor cursor = null;
        final SQLiteDatabase database = mTurtleDatabase.getWritableDatabase();
        database.beginTransaction();
        try {
            cursor = database.query(LessonsColumns.TNAME, null, LessonsColumns.CID + " = ?" + " AND " +
                    LessonsColumns.LID + " = ?" + " AND " + LessonsColumns.IS_STUDY + " = 111", new String[]{cid + "", lid + ""}, null, null, null);
            database.setTransactionSuccessful();
            return (cursor != null && cursor.getCount() > 0);
        } finally {
            database.endTransaction();
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public synchronized String getStudyState(long cid, long lid) {
        Cursor cursor = null;
        String state = "000";
        final SQLiteDatabase database = mTurtleDatabase.getWritableDatabase();
        database.beginTransaction();
        try {
            cursor = database.query(LessonsColumns.TNAME, null, LessonsColumns.CID + " = ?" + " AND " +
                    LessonsColumns.LID + " = ?", new String[]{cid + "", lid + ""}, null, null, null);
            database.setTransactionSuccessful();
            if (cursor != null && cursor.moveToFirst()) {
                state = cursor.getString(6);
            }
            return state;
        } finally {
            database.endTransaction();
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public synchronized int getStudyNum(long cid) {
        Cursor cursor = null;
        int num = 0;
        final SQLiteDatabase database = mTurtleDatabase.getWritableDatabase();
        database.beginTransaction();
        try {
            cursor = database.query(LessonsColumns.TNAME, null, LessonsColumns.CID + " = ?" + " AND " + LessonsColumns.IS_STUDY + " = 111", new String[]{cid + ""}, null, null, null);
            database.setTransactionSuccessful();
            if (cursor != null && cursor.getCount() > 0) {
                num = cursor.getCount();
            }
            return num;
        } finally {
            database.endTransaction();
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private synchronized boolean isExist(long cid, long lid) {
        Cursor cursor = null;
        final SQLiteDatabase database = mTurtleDatabase.getWritableDatabase();
        database.beginTransaction();
        try {
            cursor = database.query(LessonsColumns.TNAME, null, LessonsColumns.CID + " = ?" + " AND " +
                    LessonsColumns.LID + " = ?", new String[]{cid + "", lid + ""}, null, null, null);
            database.setTransactionSuccessful();
            return (cursor != null && cursor.getCount() > 0);
        } finally {
            database.endTransaction();
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public void deleteAll() {
        final SQLiteDatabase database = mTurtleDatabase.getWritableDatabase();
        database.delete(LessonsColumns.NAME, null, null);
    }

    private interface LessonsColumns {
        /* Table name */
        String TNAME = "lessons";
        /* Course key */
        String CID = "cid";
        /* Lesson data */
        String LID = "lid";
        String NAME = "name";
        String CONTENT = "content";
        String COMMAND = "command";
        String TIPS = "tips";
        String IS_STUDY = "is_study";
    }
}