package io.github.verden11.grabble.Helper;


import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.SyncStateContract;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.github.verden11.grabble.Constants.Constants;

public class Queries {

    /**
     * Login Activity
     */

    public static boolean tryLogin(Activity activity, String email, String password) {
        DbHelper mDbHelper = new DbHelper(activity);
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        int found = 0;
        Cursor c = db.rawQuery(
                "SELECT count(1) " +
                        "FROM " + DbHelper.UsersEntry.TABLE_NAME +
                        " WHERE " + DbHelper.UsersEntry.COLUMN_EMAIL + " = '" + email + "'" +
                        " AND " + DbHelper.UsersEntry.COLUMN_PASSWORD + " = '" + password + "'", null);
        try {
            c.moveToFirst();
            found = c.getInt(0);
        } finally {
            c.close();
            db.close();
        }

        return found == 1;
    }

    /*
     * Register Activity
     */

    public static void registerUser(Activity activity, String nickname, String email, String password) {
        DbHelper mDbHelper = new DbHelper(activity);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        try {
            ContentValues valuesUser = new ContentValues();
            valuesUser.put(DbHelper.UsersEntry.COLUMN_NICKNAME, nickname);
            valuesUser.put(DbHelper.UsersEntry.COLUMN_EMAIL, email);
            valuesUser.put(DbHelper.UsersEntry.COLUMN_PASSWORD, password);
            // Insert the new row, returning the primary key value of the new row
            long rowID = db.insert(DbHelper.UsersEntry.TABLE_NAME, null, valuesUser);

            // set default statistics
            ContentValues valuesStats = new ContentValues();
            valuesStats.put(DbHelper.Stats.COLUMN_USER_ID, rowID);
            valuesStats.put(DbHelper.Stats.COLUMN_DISTANCE_WALKED, 0);
            valuesStats.put(DbHelper.Stats.COLUMN_WORDS, "");
            valuesStats.put(DbHelper.Stats.COLUMN_SCORE, 0);
            db.insert(DbHelper.Stats.TABLE_NAME, null, valuesStats);

            // set default settings
            ContentValues valuesSettings = new ContentValues();
            valuesSettings.put(DbHelper.UsersSettings.COLUMN_USER_ID, rowID);
            db.insert(DbHelper.UsersSettings.TABLE_NAME, null, valuesSettings);
        } finally {
            db.close();
        }
    }

    public static boolean isNicknameTaken(Activity activity, String nickname) {
        DbHelper mDbHelper = new DbHelper(activity);
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT count(1) FROM " + DbHelper.UsersEntry.TABLE_NAME + " WHERE " + DbHelper.UsersEntry.COLUMN_NICKNAME + " = '" + nickname + "'", null);
        int found = 0;
        try {
            c.moveToFirst();
            found = c.getInt(0);
        } finally {
            c.close();
        }

        return found == 1;
    }

    public static boolean isEmailTaken(Activity activity, String email) {
        DbHelper mDbHelper = new DbHelper(activity);
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT count(1) FROM " + DbHelper.UsersEntry.TABLE_NAME + " WHERE " + DbHelper.UsersEntry.COLUMN_EMAIL + " = '" + email + "'", null);
        int found = 0;
        try {
            c.moveToFirst();
            found = c.getInt(0);

        } finally {
            c.close();
            db.close();
        }
        return found == 1;
    }


    public static int getIdByEmail(Activity activity, String email) {
        DbHelper mDbHelper = new DbHelper(activity);
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor c = db.rawQuery(
                "SELECT " + DbHelper.UsersEntry._ID +
                        " FROM " + DbHelper.UsersEntry.TABLE_NAME +
                        " WHERE " + DbHelper.UsersEntry.COLUMN_EMAIL + " = '" + email + "';", null);
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

    public static void addChar(Activity activity, int user_id, ArrayList<Character> charList) {
        DbHelper mDbHelper = new DbHelper(activity);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        try {
            for (Character ch : charList) {
                ch = Character.toUpperCase(ch);
                int charCount = getCharCount(activity, user_id, ch);
                charCount++;

                ContentValues cv = new ContentValues();
                cv.put(ch + "", charCount);
                db.update(DbHelper.Stats.TABLE_NAME, cv, "user_id= " + user_id, null);
                // change bool to int
            }

        } finally {
            db.close();
            saveAvailableLetterCount(activity, user_id, charList.size());
        }
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
        saveAvailableLetterCount(activity, user_id, true);
    }

    public static void removeChar(Activity activity, int user_id, char ch) {
        DbHelper mDbHelper = new DbHelper(activity);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        try {
            ch = Character.toUpperCase(ch);
            int charCount = getCharCount(activity, user_id, ch);
            charCount--;
            ContentValues cv = new ContentValues();
            cv.put(ch + "", charCount);
            db.update(DbHelper.Stats.TABLE_NAME, cv, "user_id= " + user_id, null);
        } finally {
            db.close();
        }

        saveAvailableLetterCount(activity, user_id, false);
    }

    public static int getCharCount(Activity activity, int user_id, char ch) {
        DbHelper mDbHelper = new DbHelper(activity);
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        ch = Character.toUpperCase(ch);
        Cursor c = db.rawQuery(
                "SELECT " + ch +
                        " FROM " + DbHelper.Stats.TABLE_NAME +
                        " WHERE " + DbHelper.Stats.COLUMN_USER_ID + " = " + user_id + ";", null);
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
        Cursor c = db.rawQuery(
                "SELECT " + DbHelper.UsersEntry.COLUMN_LAST_KML_DOWNLOAD_DATE +
                        " FROM " + DbHelper.UsersEntry.TABLE_NAME +
                        " WHERE " + DbHelper.UsersEntry._ID + " = " + user_id + ";", null);
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


    public static void saveKML(Activity activity, int user_id, List<Marker> markers) {
        DbHelper mDbHelper = new DbHelper(activity);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        String json = "";
        try {
            for (Marker m : markers) {
                json += m.getPosition().toString();
                json += m.getTitle();
            }
            cv.put(DbHelper.UsersEntry.COLUMN_KML_LIST, json);
            db.update(DbHelper.UsersEntry.TABLE_NAME, cv, "_id = " + user_id, null);
        } finally {
            db.close();
        }
    }

    public static List<Marker> loadKML(Activity activity, int user_id, GoogleMap mMap) {
        DbHelper mDbHelper = new DbHelper(activity);
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor c = db.rawQuery(
                "SELECT " + DbHelper.UsersEntry.COLUMN_KML_LIST +
                        " FROM " + DbHelper.UsersEntry.TABLE_NAME +
                        " WHERE " + DbHelper.UsersEntry._ID + " = " + user_id + ";", null);
        List<Marker> placemarks = new ArrayList<>();
        try {
            // get KML as String
            c.moveToFirst();
            String markersStr = c.getString(0);
            // get coordinates using RegEx
            Pattern r = Pattern.compile("(-\\d+|\\d+)\\.\\d+");
            Matcher m = r.matcher(markersStr);
            List<Float> allMatchesCoordinates = new ArrayList<>();
            while (m.find()) {
                allMatchesCoordinates.add(Float.parseFloat(m.group()));
            }

            // get letters using RegEx
            String[] titles = markersStr.split("lat\\/lng: \\(\\d+\\.\\d+,(-\\d+|\\d+)\\.\\d+\\)");
            int count = 0;

            // add placemarks to the map
            for (int i = 0; i < allMatchesCoordinates.size(); i += 2) {
                count++;
                LatLng latLng = new LatLng(allMatchesCoordinates.get(i), allMatchesCoordinates.get((i + 1)));
                String title = titles[count];

                // draw maker on the map
                Marker marker = mMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title(title)
                        .visible(false));
                // populate array list
                placemarks.add(marker);
            }
        } finally {
            c.close();
            db.close();
        }
        return placemarks;
    }

    public static void saveKMLDownloadTime(Activity activity, int user_id, long epochTime) {
        DbHelper mDbHelper = new DbHelper(activity);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        try {
            cv.put(DbHelper.UsersEntry.COLUMN_LAST_KML_DOWNLOAD_DATE, epochTime);
            db.update(DbHelper.UsersEntry.TABLE_NAME, cv, "_id = " + user_id, null);
        } finally {
            db.close();
        }
    }


    /**
     * UserPersonalPages
     * Save word to db
     */
    public static void saveWord(Activity activity, int user_id, String word, int score) {
        String scoreStr = "" + score;
        if (score < 10) {
            scoreStr = "00" + scoreStr;
        } else if (score < 100) {
            scoreStr = "0" + scoreStr;
        }


        String existing = getWords(activity, user_id);
        existing += word + scoreStr;
        DbHelper mDbHelper = new DbHelper(activity);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        try {
            cv.put(DbHelper.Stats.COLUMN_WORDS, existing);
            db.update(DbHelper.Stats.TABLE_NAME, cv, "user_id = " + user_id, null);
        } finally {
            db.close();
        }
    }

    /**
     * Get all words from DB
     */
    public static String getWords(Activity activity, int user_id) {
        String ret = "";
        DbHelper mDbHelper = new DbHelper(activity);
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT " + DbHelper.Stats.COLUMN_WORDS +
                " FROM " + DbHelper.Stats.TABLE_NAME +
                " WHERE " + DbHelper.Stats.COLUMN_USER_ID + " = " + user_id + ";", null);
        try {
            c.moveToFirst();
            ret = c.getString(0);
        } finally {
            c.close();
            db.close();
        }
        return ret;
    }

    /**
     * get int score by char
     */

    public static int getCharValue(char ch) {
        ch = Character.toUpperCase(ch);
        switch (ch) {
            // Q W E R T Y U I O P
            case 'Q':
                return Constants.score_Q;
            case 'W':
                return Constants.score_W;
            case 'E':
                return Constants.score_E;
            case 'R':
                return Constants.score_R;
            case 'T':
                return Constants.score_T;
            case 'Y':
                return Constants.score_Y;
            case 'U':
                return Constants.score_U;
            case 'I':
                return Constants.score_I;
            case 'O':
                return Constants.score_O;
            case 'P':
                return Constants.score_P;

            // A S D F G H J K L
            case 'A':
                return Constants.score_A;
            case 'S':
                return Constants.score_S;
            case 'D':
                return Constants.score_D;
            case 'F':
                return Constants.score_F;
            case 'G':
                return Constants.score_G;
            case 'H':
                return Constants.score_H;
            case 'J':
                return Constants.score_J;
            case 'K':
                return Constants.score_K;
            case 'L':
                return Constants.score_L;

            // Z X C V B N M
            case 'Z':
                return Constants.score_Z;
            case 'X':
                return Constants.score_X;
            case 'C':
                return Constants.score_C;
            case 'V':
                return Constants.score_V;
            case 'B':
                return Constants.score_B;
            case 'N':
                return Constants.score_N;
            case 'M':
                return Constants.score_M;
            default:
                return 0;
        }

    }

    public static int getAvailableLetterCount(Activity activity, int user_id) {
        int ret = 0;
        DbHelper mDbHelper = new DbHelper(activity);
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT " + DbHelper.Stats.COLUMN_LETTERS_AVAILABLE +
                " FROM " + DbHelper.Stats.TABLE_NAME +
                " WHERE " + DbHelper.Stats.COLUMN_USER_ID + " = " + user_id + ";", null);
        try {
            c.moveToFirst();
            ret = c.getInt(0);
        } finally {
            c.close();
            db.close();
        }
        return ret;
    }

    public static void saveAvailableLetterCount(Activity activity, int user_id, int add) {
        int count = getAvailableLetterCount(activity, user_id);
        DbHelper mDbHelper = new DbHelper(activity);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();

        count += add;

        try {
            cv.put(DbHelper.Stats.COLUMN_LETTERS_AVAILABLE, count);
            db.update(DbHelper.Stats.TABLE_NAME, cv, "user_id = " + user_id, null);
        } finally {
            db.close();
        }

        saveTotalLetterCount(activity, user_id, add);

    }

    public static void saveAvailableLetterCount(Activity activity, int user_id, boolean add) {
        int count = getAvailableLetterCount(activity, user_id);
        DbHelper mDbHelper = new DbHelper(activity);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();

        if (add) {
            count++;
        } else {
            count--;
        }

        try {
            cv.put(DbHelper.Stats.COLUMN_LETTERS_AVAILABLE, count);
            db.update(DbHelper.Stats.TABLE_NAME, cv, "user_id = " + user_id, null);
        } finally {
            db.close();
        }

        if (add) {
            saveTotalLetterCount(activity, user_id);
        }

    }

    public static int getTotalLetterCount(Activity activity, int user_id) {
        int ret = 0;
        DbHelper mDbHelper = new DbHelper(activity);
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT " + DbHelper.Stats.COLUMN_TOTAL_LETTERS_COLLECTED +
                " FROM " + DbHelper.Stats.TABLE_NAME +
                " WHERE " + DbHelper.Stats.COLUMN_USER_ID + " = " + user_id + ";", null);
        try {
            c.moveToFirst();
            ret = c.getInt(0);
        } finally {
            c.close();
            db.close();
        }
        return ret;
    }

    public static void saveTotalLetterCount(Activity activity, int user_id, int add) {
        int count = getTotalLetterCount(activity, user_id);
        DbHelper mDbHelper = new DbHelper(activity);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        count += add;

        try {
            cv.put(DbHelper.Stats.COLUMN_TOTAL_LETTERS_COLLECTED, count);
            db.update(DbHelper.Stats.TABLE_NAME, cv, "user_id = " + user_id, null);
        } finally {
            db.close();
        }

    }

    public static void saveTotalLetterCount(Activity activity, int user_id) {
        int count = getTotalLetterCount(activity, user_id);
        DbHelper mDbHelper = new DbHelper(activity);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();

        try {
            cv.put(DbHelper.Stats.COLUMN_TOTAL_LETTERS_COLLECTED, ++count);
            db.update(DbHelper.Stats.TABLE_NAME, cv, "user_id = " + user_id, null);
        } finally {
            db.close();
        }

    }

    public static void updateDistanceWalked(Activity activity, int user_id, float distance) {
        float alreadyWalked = getDistanceWalked(activity, user_id);
        float totalWalked = alreadyWalked + distance;
        DbHelper mDbHelper = new DbHelper(activity);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        try {
            cv.put(DbHelper.Stats.COLUMN_DISTANCE_WALKED, totalWalked);
            db.update(DbHelper.Stats.TABLE_NAME, cv, "user_id = " + user_id, null);
        } finally {
            db.close();
        }
    }

    public static float getDistanceWalked(Activity activity, int user_id) {
        float ret = 0;
        DbHelper mDbHelper = new DbHelper(activity);
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT " + DbHelper.Stats.COLUMN_DISTANCE_WALKED +
                " FROM " + DbHelper.Stats.TABLE_NAME +
                " WHERE " + DbHelper.Stats.COLUMN_USER_ID + " = " + user_id + ";", null);
        try {
            c.moveToFirst();
            ret = c.getFloat(0);
        } finally {
            c.close();
            db.close();
        }
        return ret;
    }

    public static boolean isGoalSet(Activity activity, int user_id) {
        int ret = 0;
        DbHelper mDbHelper = new DbHelper(activity);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT " + DbHelper.UsersEntry.COLUMN_GOAL_SET +
                " FROM " + DbHelper.UsersEntry.TABLE_NAME +
                " WHERE " + DbHelper.UsersEntry._ID + " = " + user_id + ";", null);
        try {
            c.moveToFirst();
            ret = c.getInt(0);
        } finally {
            c.close();
            db.close();
        }
        return ret > 0;
    }

    public static void setGoal(Activity activity, int user_id, int goalInt) {
        DbHelper mDbHelper = new DbHelper(activity);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        try {
            cv.put(DbHelper.UsersEntry.COLUMN_GOAL_SET, goalInt);
            db.update(DbHelper.UsersEntry.TABLE_NAME, cv, "_id = " + user_id, null);
        } finally {
            db.close();
        }
    }


    public static int getGoal(Activity activity, int user_id) {
        int ret = 0;
        DbHelper mDbHelper = new DbHelper(activity);
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT " + DbHelper.UsersEntry.COLUMN_GOAL_SET +
                " FROM " + DbHelper.UsersEntry.TABLE_NAME +
                " WHERE " + DbHelper.UsersEntry._ID + " = " + user_id + ";", null);
        try {
            c.moveToFirst();
            ret = c.getInt(0);
        } finally {
            c.close();
            db.close();
        }
        return ret;
    }

    public static boolean isGoalAchieved(Activity activity, int user_id) {
        int ret = 0;
        DbHelper mDbHelper = new DbHelper(activity);
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT " + DbHelper.UsersEntry.COLUMN_GOAL_ACHIEVED +
                " FROM " + DbHelper.UsersEntry.TABLE_NAME +
                " WHERE " + DbHelper.UsersEntry._ID + " = " + user_id + ";", null);
        try {
            c.moveToFirst();
            ret = c.getInt(0);
        } finally {
            c.close();
            db.close();
        }
        return ret > 0;
    }

    public static void setGoalAchieved(Activity activity, int user_id, int achieved) {
        DbHelper mDbHelper = new DbHelper(activity);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        try {
            cv.put(DbHelper.UsersEntry.COLUMN_GOAL_ACHIEVED, achieved);
            db.update(DbHelper.UsersEntry.TABLE_NAME, cv, "_id = " + user_id, null);
        } finally {
            db.close();
        }
    }


    // Queries for settings
    public static int[] getSettings(Activity activity, int user_id) {
        int ret[] = {0, 0, 0};
        DbHelper mDbHelper = new DbHelper(activity);
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT " + "*" +
                " FROM " + DbHelper.UsersSettings.TABLE_NAME +
                " WHERE " + DbHelper.UsersSettings.COLUMN_USER_ID + " = " + user_id + ";", null);
        try {
            c.moveToFirst();
            ret[0] = c.getInt(1);
            ret[1] = c.getInt(2);
            ret[2] = c.getInt(3);
        } finally {
            c.close();
            db.close();
        }
        return ret;
    }

    public static void setSettings(Activity activity, int user_id, int[] settings) {
        DbHelper mDbHelper = new DbHelper(activity);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        try {
            cv.put(DbHelper.UsersSettings.COLUMN_BATTERY_SAVER, settings[0]);
            cv.put(DbHelper.UsersSettings.COLUMN_GAME_DIFFICULTY, settings[1]);
            cv.put(DbHelper.UsersSettings.COLUMN_MAP_STYLE, settings[2]);
            db.update(DbHelper.UsersSettings.TABLE_NAME, cv, "user_id = " + user_id, null);
        } finally {
            db.close();
        }
    }


    public static int getWalkedToday(Activity activity, int user_id) {
        int ret = 0;
        DbHelper mDbHelper = new DbHelper(activity);
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT " + DbHelper.UsersEntry.COLUMN_DAY_WALKED +
                " FROM " + DbHelper.UsersEntry.TABLE_NAME +
                " WHERE " + DbHelper.UsersEntry._ID + " = " + user_id + ";", null);
        try {
            c.moveToFirst();
            ret = c.getInt(0);
        } finally {
            c.close();
            db.close();
        }
        return ret;
    }

    public static void updateDistanceWalkedToday(Activity activity, int user_id, int distance) {
        int alreadyWalked = getWalkedToday(activity, user_id);
        int totalWalked = alreadyWalked + distance;
        DbHelper mDbHelper = new DbHelper(activity);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        try {
            cv.put(DbHelper.UsersEntry.COLUMN_DAY_WALKED, totalWalked);
            db.update(DbHelper.UsersEntry.TABLE_NAME, cv, "_id = " + user_id, null);
        } finally {
            db.close();
        }
    }

}
