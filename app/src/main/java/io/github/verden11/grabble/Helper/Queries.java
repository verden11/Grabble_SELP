package io.github.verden11.grabble.Helper;


import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.StreetViewPanoramaCamera;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collector;
import java.util.stream.Collectors;

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

            ContentValues valuesStats = new ContentValues();
            valuesStats.put(DbHelper.Stats.COLUMN_USER_ID, rowID);
            valuesStats.put(DbHelper.Stats.COLUMN_DISTANCE_WALKED, 0);
            valuesStats.put(DbHelper.Stats.COLUMN_WORDS, "");
            valuesStats.put(DbHelper.Stats.COLUMN_SCORE, 0);
            db.insert(DbHelper.Stats.TABLE_NAME, null, valuesStats);
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


}
