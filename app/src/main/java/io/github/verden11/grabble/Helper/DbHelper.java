package io.github.verden11.grabble.Helper;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

public class DbHelper extends SQLiteOpenHelper {
    private static final String TAG = "DbHelperTAG";
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 3;
    public static final String DATABASE_NAME = "Grabble.db";


    /* Inner class that defines the table contents */
    public static class UsersEntry implements BaseColumns {
        public static final String TABLE_NAME = "users";
        public static final String COLUMN_EMAIL = "email";
        public static final String COLUMN_PASSWORD = "password";
        public static final String COLUMN_NICKNAME = "nickname";
    }


    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
        Log.d(TAG, SQL_CREATE_ENTRIES);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }


    private static final String TEXT_TYPE = " TEXT";
    private static final String UNIQUE = " UNIQUE";
    private static final String NOT_NULL = " NOT NULL";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + UsersEntry.TABLE_NAME + " (" +
                    UsersEntry._ID + " INTEGER PRIMARY KEY," +
                    UsersEntry.COLUMN_NICKNAME + TEXT_TYPE + NOT_NULL + UNIQUE + COMMA_SEP +
                    UsersEntry.COLUMN_EMAIL + TEXT_TYPE + NOT_NULL + UNIQUE + COMMA_SEP +
                    UsersEntry.COLUMN_PASSWORD + TEXT_TYPE + NOT_NULL + " )";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + UsersEntry.TABLE_NAME;


}
