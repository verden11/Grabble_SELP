package io.github.verden11.grabble.Constants;

/**
 * Created by verden on 05/11/16.
 */

public class Constants {

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
     */
    public static final long MIN_DISTANCE_TO_COLLECT_LETTER = 10;

    public class permissions {
        public final static int ACCESS_FINE_LOCATION = 1001;
    }

}
