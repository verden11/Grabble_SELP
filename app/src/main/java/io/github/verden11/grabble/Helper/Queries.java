package io.github.verden11.grabble.Helper;


import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class Queries {
//    private SQLiteDatabase db;

    public static int getIdByEmail(String email, Activity activity) {
        DbHelper mDbHelper = new DbHelper(activity);
        // Gets the data repository in write mode
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        Cursor c = db.rawQuery("SELECT " + DbHelper.UsersEntry._ID + " FROM " + DbHelper.UsersEntry.TABLE_NAME + " WHERE " + DbHelper.UsersEntry.COLUMN_EMAIL + " = '" + email + "'", null);
        c.moveToFirst();
        int user_id = c.getInt(0);
        c.close();
        return user_id;
    }


}
