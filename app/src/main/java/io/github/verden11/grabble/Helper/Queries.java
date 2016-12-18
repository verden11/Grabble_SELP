package io.github.verden11.grabble.Helper;


import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class Queries {
//    private SQLiteDatabase db;

    public static int getIdByEmail(Activity activity, String email) {
        DbHelper mDbHelper = new DbHelper(activity);
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT " + DbHelper.UsersEntry._ID + " FROM " + DbHelper.UsersEntry.TABLE_NAME + " WHERE " + DbHelper.UsersEntry.COLUMN_EMAIL + " = '" + email + "';", null);
        ;
        int user_id;
        try {
            c.moveToFirst();
            user_id = c.getInt(0);
        } finally {
            c.close();
            db.close();
        }
        return user_id;
    }

    public static void addChar(Activity activity, int user_id, char ch) {
        DbHelper mDbHelper = new DbHelper(activity);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        try {
            ch = Character.toUpperCase(ch);
            int charCount = getCharCount(activity, user_id, ch);
            charCount++;

            ContentValues cv = new ContentValues();
            cv.put(ch + "", charCount);
            db.update(DbHelper.Stats.TABLE_NAME, cv, "user_id= " + user_id, null);
        } finally {
            db.close();
        }

    }

    public static int getCharCount(Activity activity, int user_id, char ch) {
        DbHelper mDbHelper = new DbHelper(activity);
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        ch = Character.toUpperCase(ch);
        Cursor c = db.rawQuery("SELECT " + ch + " FROM " + DbHelper.Stats.TABLE_NAME + " WHERE " + DbHelper.Stats.COLUMN_USER_ID + " = " + user_id + ";", null);
        int count;
        try {
            c.moveToFirst();
            count = c.getInt(0);
        } finally {
            c.close();
            db.close();
        }
        return count;
    }

    public static long getLastKMLDownloadTime(Activity activity, int user_id) {
        DbHelper mDbHelper = new DbHelper(activity);
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT " + DbHelper.UsersEntry.COLUMN_LAST_KML_DOWNLOAD_DATE + " FROM " + DbHelper.UsersEntry.TABLE_NAME + " WHERE " + DbHelper.UsersEntry._ID + " = " + user_id + ";", null);
        long epochTime;
        try {
            c.moveToFirst();
            epochTime = c.getLong(0);
        } finally {
            c.close();
            db.close();
        }
        return epochTime;
    }

    public static void saveKML(Activity activity, int user_id) {


    }


}
