package io.github.verden11.grabble.Constants;


public class Constants {

    /**
     * Constance for coordinates where the game is playable
     */
    public static final float ForestHill_lat = (float) 55.946233;
    public static final float ForestHill_lng = (float) -3.192473;

    public static final float KFC_lat = (float) 55.946233;
    public static final float KFC_lng = (float) -3.184319;

    public static final float Top_Meadows_lat = (float) 55.942617;
    public static final float Top_Meadows_lng = (float) 3.192473;

    public static final float Buccleuch_lat = (float) 55.942617;
    public static final float Buccleuch_lng = (float) -3.184319;


    /**
     * Constants for passing variables using intents
     */
    public static final String USER_ID = "user_id";
    public static final String DEV_EMAIL = "your@email.com"; // email for contacting developer
    public static int user_id;

    /**
     * Constants for permission checks
     */
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 7001;


    /**
     * The desired interval for location updates. Inexact. Updates may be more or less frequent.
     */
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 4000;
    /**
     * The fastest rate for active location updates. Exact. Updates will never be more frequent
     * than this value.
     */
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;
    /**
     * The distance value in meters for a letter to be collected
     * these values will be changed depending on users settings
     */
    public static int MAX_DISTANCE_TO_COLLECT_LETTER = 10;

    /**
     * The distance for a placemark to be visible on the map
     */
    public static int MAX_DISTANCE_TO_MAKE_MARKER_VISIBLE = 50;


    /**
     * Letter scores
     */
    public static final int score_A = 3;
    public static final int score_B = 20;
    public static final int score_C = 13;
    public static final int score_D = 10;
    public static final int score_E = 1;

    public static final int score_F = 15;
    public static final int score_G = 18;
    public static final int score_H = 9;
    public static final int score_I = 5;
    public static final int score_J = 25;

    public static final int score_K = 22;
    public static final int score_L = 11;
    public static final int score_M = 14;
    public static final int score_N = 6;
    public static final int score_O = 4;

    public static final int score_P = 19;
    public static final int score_Q = 24;
    public static final int score_R = 8;
    public static final int score_S = 7;
    public static final int score_T = 2;

    public static final int score_U = 12;
    public static final int score_V = 21;
    public static final int score_W = 17;
    public static final int score_X = 23;
    public static final int score_Y = 16;
    public static final int score_Z = 26;

}
