package io.github.verden11.grabble.Helper;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

public class DbHelper extends SQLiteOpenHelper {
    private static final String TAG = "DbHelperTAG";
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 15;
    public static final String DATABASE_NAME = "Grabble.db";


    /* Inner class that defines the uers table contents */
    public static class UsersEntry implements BaseColumns {
        public static final String TABLE_NAME = "users";
        public static final String COLUMN_EMAIL = "email";
        public static final String COLUMN_PASSWORD = "password";
        public static final String COLUMN_NICKNAME = "nickname";
        public static final String COLUMN_LAST_KML_DOWNLOAD_DATE = "last_kml_download";
        public static final String COLUMN_KML_LIST = "kml_object";
        public static final String COLUMN_GOAL_SET = "goal_set";
        public static final String COLUMN_GOAL_ACHIEVED = "goal_achieved";
        public static final String COLUMN_DAY_WALKED = "walked_today";
        public static final String COLUMN_DAY_LETTERS_COLLECTED = "collected_today";
        public static final String COLUMN_WORD_OF_THE_DAY = "word_of_the_day";
    }

    /* Inner class that defines the settings table contents */
    public static class UsersSettings implements BaseColumns {
        public static final String TABLE_NAME = "settings";
        public static final String COLUMN_USER_ID = "user_id";
        public static final String COLUMN_BATTERY_SAVER = "battery_saver";
        public static final String COLUMN_GAME_DIFFICULTY = "difficulty";
        public static final String COLUMN_MAP_STYLE = "map_style";
    }

    /* Inner class that defines the Collection table */
    public static class Stats implements BaseColumns {
        public static final String TABLE_NAME = "statistics";
        public static final String COLUMN_USER_ID = "user_id";
        public static final String COLUMN_WORDS = "words";
        public static final String COLUMN_DISTANCE_WALKED = "distance";
        public static final String COLUMN_SCORE = "score";
        public static final String COLUMN_TOTAL_LETTERS_COLLECTED = "total_letters_collected";
        public static final String COLUMN_LETTERS_AVAILABLE = "letters_available";

        public static final String COLUMN_LETTER_A = "A";
        public static final String COLUMN_LETTER_B = "B";
        public static final String COLUMN_LETTER_C = "C";
        public static final String COLUMN_LETTER_D = "D";
        public static final String COLUMN_LETTER_E = "E";

        public static final String COLUMN_LETTER_F = "F";
        public static final String COLUMN_LETTER_G = "G";
        public static final String COLUMN_LETTER_H = "H";
        public static final String COLUMN_LETTER_I = "I";
        public static final String COLUMN_LETTER_J = "J";

        public static final String COLUMN_LETTER_K = "K";
        public static final String COLUMN_LETTER_L = "L";
        public static final String COLUMN_LETTER_M = "M";
        public static final String COLUMN_LETTER_N = "N";
        public static final String COLUMN_LETTER_O = "O";

        public static final String COLUMN_LETTER_P = "P";
        public static final String COLUMN_LETTER_Q = "Q";
        public static final String COLUMN_LETTER_R = "R";
        public static final String COLUMN_LETTER_S = "S";
        public static final String COLUMN_LETTER_T = "T";

        public static final String COLUMN_LETTER_U = "U";
        public static final String COLUMN_LETTER_V = "V";
        public static final String COLUMN_LETTER_W = "W";
        public static final String COLUMN_LETTER_X = "X";
        public static final String COLUMN_LETTER_Y = "Y";
        public static final String COLUMN_LETTER_Z = "Z";

    }


    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
        Log.d(TAG, SQL_CREATE_ENTRIES);
        db.execSQL(SQL_CREATE_STATS);
        Log.d(TAG, SQL_CREATE_STATS);
        db.execSQL(SQL_CREATE_SETTINGS);

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
    private static final String DEFAULT_0 = " DEFAULT 0";
    private static final String DEFAULT_EMPTY = " DEFAULT ''";
    private static final String COMMA_SEP = ",";

    private static final String SQL_CREATE_STATS =
            "CREATE TABLE " + Stats.TABLE_NAME + " (" +
                    Stats.COLUMN_USER_ID + " INTEGER PRIMARY KEY REFERENCES " + UsersEntry.TABLE_NAME + "(" + UsersEntry._ID + ")," +
                    Stats.COLUMN_WORDS + TEXT_TYPE + COMMA_SEP +
                    Stats.COLUMN_DISTANCE_WALKED + DECIMAL_TYPE + COMMA_SEP +
                    Stats.COLUMN_SCORE + INT_TYPE + DEFAULT_0 + COMMA_SEP +
                    Stats.COLUMN_TOTAL_LETTERS_COLLECTED + INT_TYPE + DEFAULT_0 + COMMA_SEP +
                    Stats.COLUMN_LETTERS_AVAILABLE + INT_TYPE + DEFAULT_0 + COMMA_SEP +
                    Stats.COLUMN_LETTER_A + INT_TYPE + DEFAULT_0 + COMMA_SEP +
                    Stats.COLUMN_LETTER_B + INT_TYPE + DEFAULT_0 + COMMA_SEP +
                    Stats.COLUMN_LETTER_C + INT_TYPE + DEFAULT_0 + COMMA_SEP +
                    Stats.COLUMN_LETTER_D + INT_TYPE + DEFAULT_0 + COMMA_SEP +
                    Stats.COLUMN_LETTER_E + INT_TYPE + DEFAULT_0 + COMMA_SEP +

                    Stats.COLUMN_LETTER_F + INT_TYPE + DEFAULT_0 + COMMA_SEP +
                    Stats.COLUMN_LETTER_G + INT_TYPE + DEFAULT_0 + COMMA_SEP +
                    Stats.COLUMN_LETTER_H + INT_TYPE + DEFAULT_0 + COMMA_SEP +
                    Stats.COLUMN_LETTER_I + INT_TYPE + DEFAULT_0 + COMMA_SEP +
                    Stats.COLUMN_LETTER_J + INT_TYPE + DEFAULT_0 + COMMA_SEP +

                    Stats.COLUMN_LETTER_K + INT_TYPE + DEFAULT_0 + COMMA_SEP +
                    Stats.COLUMN_LETTER_L + INT_TYPE + DEFAULT_0 + COMMA_SEP +
                    Stats.COLUMN_LETTER_M + INT_TYPE + DEFAULT_0 + COMMA_SEP +
                    Stats.COLUMN_LETTER_N + INT_TYPE + DEFAULT_0 + COMMA_SEP +
                    Stats.COLUMN_LETTER_O + INT_TYPE + DEFAULT_0 + COMMA_SEP +

                    Stats.COLUMN_LETTER_P + INT_TYPE + DEFAULT_0 + COMMA_SEP +
                    Stats.COLUMN_LETTER_Q + INT_TYPE + DEFAULT_0 + COMMA_SEP +
                    Stats.COLUMN_LETTER_R + INT_TYPE + DEFAULT_0 + COMMA_SEP +
                    Stats.COLUMN_LETTER_S + INT_TYPE + DEFAULT_0 + COMMA_SEP +
                    Stats.COLUMN_LETTER_T + INT_TYPE + DEFAULT_0 + COMMA_SEP +

                    Stats.COLUMN_LETTER_U + INT_TYPE + DEFAULT_0 + COMMA_SEP +
                    Stats.COLUMN_LETTER_V + INT_TYPE + DEFAULT_0 + COMMA_SEP +
                    Stats.COLUMN_LETTER_W + INT_TYPE + DEFAULT_0 + COMMA_SEP +
                    Stats.COLUMN_LETTER_X + INT_TYPE + DEFAULT_0 + COMMA_SEP +
                    Stats.COLUMN_LETTER_Y + INT_TYPE + DEFAULT_0 + COMMA_SEP +
                    Stats.COLUMN_LETTER_Z + INT_TYPE + DEFAULT_0 +
                    " )";


    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + UsersEntry.TABLE_NAME + " (" +
                    UsersEntry._ID + " INTEGER PRIMARY KEY," +
                    UsersEntry.COLUMN_NICKNAME + TEXT_TYPE + NOT_NULL + UNIQUE + COMMA_SEP +
                    UsersEntry.COLUMN_EMAIL + TEXT_TYPE + NOT_NULL + UNIQUE + COMMA_SEP +
                    UsersEntry.COLUMN_PASSWORD + TEXT_TYPE + NOT_NULL + COMMA_SEP +
                    UsersEntry.COLUMN_LAST_KML_DOWNLOAD_DATE + INT_TYPE + DEFAULT_0 + NOT_NULL + COMMA_SEP +
                    UsersEntry.COLUMN_KML_LIST + TEXT_TYPE + DEFAULT_EMPTY + NOT_NULL + COMMA_SEP +
                    UsersEntry.COLUMN_GOAL_SET + INT_TYPE + DEFAULT_0 + NOT_NULL + COMMA_SEP +
                    UsersEntry.COLUMN_GOAL_ACHIEVED + INT_TYPE + DEFAULT_0 + NOT_NULL + COMMA_SEP +
                    UsersEntry.COLUMN_DAY_WALKED + INT_TYPE + DEFAULT_0 + NOT_NULL + COMMA_SEP +
                    UsersEntry.COLUMN_DAY_LETTERS_COLLECTED + INT_TYPE + DEFAULT_0 + NOT_NULL + COMMA_SEP +
                    UsersEntry.COLUMN_WORD_OF_THE_DAY + TEXT_TYPE + DEFAULT_EMPTY + NOT_NULL
                    + " )";

    private static final String SQL_CREATE_SETTINGS =
            "CREATE TABLE " + UsersSettings.TABLE_NAME + " (" +
                    UsersSettings.COLUMN_USER_ID + " INTEGER PRIMARY KEY REFERENCES " + UsersEntry.TABLE_NAME + "(" + UsersEntry._ID + ")," +
                    UsersSettings.COLUMN_BATTERY_SAVER + INT_TYPE + DEFAULT_0 + NOT_NULL + COMMA_SEP +
                    UsersSettings.COLUMN_GAME_DIFFICULTY + INT_TYPE + DEFAULT_0 + NOT_NULL + COMMA_SEP +
                    UsersSettings.COLUMN_MAP_STYLE + INT_TYPE + DEFAULT_0 +
                    " )";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + UsersEntry.TABLE_NAME;


}
