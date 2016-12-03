package io.github.verden11.grabble.Helper;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

public class DbHelper extends SQLiteOpenHelper {
    private static final String TAG = "DbHelperTAG";
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 6;
    public static final String DATABASE_NAME = "Grabble.db";


    /* Inner class that defines the uers table contents */
    public static class UsersEntry implements BaseColumns {
        public static final String TABLE_NAME = "users";
        public static final String COLUMN_EMAIL = "email";
        public static final String COLUMN_PASSWORD = "password";
        public static final String COLUMN_NICKNAME = "nickname";
    }

    /* Inner class that defines the Collection table */
    public static class Stats implements BaseColumns {
        public static final String TABLE_NAME = "statistics";
        public static final String COLUMN_USER_ID = "user_id";
        public static final String COLUMN_LETTERS = "letters";
        public static final String COLUMN_WORDS = "words";
        public static final String COLUMN_DISTANCE_WALKED = "distance";
        public static final String COLUMN_SCORE = "score";
    }


    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
        Log.d(TAG, SQL_CREATE_ENTRIES);
        db.execSQL(SQL_CREATE_STATS);
        Log.d(TAG, SQL_CREATE_STATS);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }


    private static final String TEXT_TYPE = " TEXT";
    private static final String DECIMAL_TYPE = " DECIMAL";
    private static final String INT_TYPE = " INT";
    private static final String UNIQUE = " UNIQUE";
    private static final String NOT_NULL = " NOT NULL";
    private static final String COMMA_SEP = ",";

    private static final String SQL_CREATE_STATS =
            "CREATE TABLE " + Stats.TABLE_NAME + " (" +
                    Stats.COLUMN_USER_ID + " INTEGER PRIMARY KEY REFERENCES " + UsersEntry.TABLE_NAME + "(" + UsersEntry._ID + ")," +
                    Stats.COLUMN_LETTERS + TEXT_TYPE + COMMA_SEP +
                    Stats.COLUMN_WORDS + TEXT_TYPE + COMMA_SEP +
                    Stats.COLUMN_DISTANCE_WALKED + DECIMAL_TYPE + COMMA_SEP +
                    Stats.COLUMN_SCORE + INT_TYPE + " DEFAULT 0" + " )";


    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + UsersEntry.TABLE_NAME + " (" +
                    UsersEntry._ID + " INTEGER PRIMARY KEY," +
                    UsersEntry.COLUMN_NICKNAME + TEXT_TYPE + NOT_NULL + UNIQUE + COMMA_SEP +
                    UsersEntry.COLUMN_EMAIL + TEXT_TYPE + NOT_NULL + UNIQUE + COMMA_SEP +
                    UsersEntry.COLUMN_PASSWORD + TEXT_TYPE + NOT_NULL + " )";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + UsersEntry.TABLE_NAME;


}
