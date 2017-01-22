package io.github.verden11.grabble;


import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.kml.KmlLayer;
import com.google.maps.android.kml.KmlPlacemark;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.github.verden11.grabble.Constants.Constants;
import io.github.verden11.grabble.Helper.General;
import io.github.verden11.grabble.Helper.PermissionHelper;
import io.github.verden11.grabble.Helper.Queries;
import io.github.verden11.grabble.Helper.Validate;

import static io.github.verden11.grabble.Constants.Constants.user_id;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    // Keys for storing activity state in the Bundle.
    protected final static String REQUESTING_LOCATION_UPDATES_KEY = "requesting-location-updates-key";
    protected final static String LOCATION_KEY = "location-key";
    protected final static String LAST_UPDATED_TIME_STRING_KEY = "last-updated-time-string-key";
    private final String TAG = "MapsActivity";
    private SQLiteDatabase db;
    private float distance_walked;
    private ProgressBar progressBar;
    private boolean isGoalSet = false;
    private boolean isGoalAchieved = false;

    /**
     * Settings array
     * settings[0] = battery saver
     * settings[1] = game difficulty
     * settings[2] = map style
     */
    private int[] settings;
    /**
     * Provides the entry point to Google Play services.
     */
    protected GoogleApiClient mGoogleApiClient;
    /**
     * Stores parameters for requests to the FusedLocationProviderApi.
     */
    protected LocationRequest mLocationRequest;
    /**
     * Represents a geographical location.
     */
    protected Location mCurrentLocation;
    /**
     * Tracks the status of the location updates request. Value changes when the user presses the
     * Start Updates and Stop Updates buttons.
     */
    protected Boolean mRequestingLocationUpdates;
    /**
     * Time when the location was updated represented as a String.
     */
    protected String mLastUpdateTime;
    List<Marker> kmlMarkers;
    View rootView;
    ArrayList<Character> collected_chars;
    Circle circle; // a circle with certain radius where placemarks become visible
    Activity thisActivity;
    private GoogleMap mMap;  // Might be null if Google Play services APK is not available.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        setContentView(R.layout.activity_maps);
        thisActivity = this;
        getSettings();
        setSettings();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PermissionHelper.checkLocationPermission(thisActivity);
        }

        //initialise variables
        rootView = findViewById(android.R.id.content);
        kmlMarkers = new ArrayList<>();
        collected_chars = new ArrayList<>();
        mRequestingLocationUpdates = true;
        mLastUpdateTime = "";
        distance_walked = 0;

        // Update values using data stored in the Bundle.
        updateValuesFromBundle(savedInstanceState);


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        // check if the Goal is set
        isGoalSet = Queries.isGoalSet(thisActivity, user_id);
        isGoalAchieved = Queries.isGoalAchieved(thisActivity, user_id);
        progressBar = (ProgressBar) findViewById(R.id.goalProgressBar);
        if (isGoalAchieved) {
            progressBar.setProgress(100);
        }

    }

    private void setSettings() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(thisActivity);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("battery_saving_mode_switch", settings[0] == 1);
        editor.putString("map_style", String.valueOf(settings[2]));
        editor.putString("difficulty_list", String.valueOf(settings[1]));
        editor.commit();
    }


    /**
     * Updates fields based on data stored in the bundle.
     *
     * @param savedInstanceState The activity state saved in the Bundle.
     */
    private void updateValuesFromBundle(Bundle savedInstanceState) {
        Log.i(TAG, "updateValuesFromBundle");
        if (savedInstanceState != null) {
            // Update the value of mRequestingLocationUpdates from the Bundle, and make sure that
            // the Start Updates and Stop Updates buttons are correctly enabled or disabled.
            if (savedInstanceState.keySet().contains(REQUESTING_LOCATION_UPDATES_KEY)) {
                mRequestingLocationUpdates = savedInstanceState.getBoolean(
                        REQUESTING_LOCATION_UPDATES_KEY);
            }

            // Update the value of mCurrentLocation from the Bundle and update the UI to show the
            // correct latitude and longitude.
            if (savedInstanceState.keySet().contains(LOCATION_KEY)) {
                // Since LOCATION_KEY was found in the Bundle, we can be sure that mCurrentLocation
                // is not null.
                mCurrentLocation = savedInstanceState.getParcelable(LOCATION_KEY);
            }

            // Update the value of mLastUpdateTime from the Bundle and update the UI.
            if (savedInstanceState.keySet().contains(LAST_UPDATED_TIME_STRING_KEY)) {
                mLastUpdateTime = savedInstanceState.getString(LAST_UPDATED_TIME_STRING_KEY);
            }
            updateUI();
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "onMapReady");
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        checkMapStyle();

        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            }
        } else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }


        long epochTime = Queries.getLastKMLDownloadTime(thisActivity, user_id);
        Date LastKmlDownloadDate = new Date(epochTime);


        // Get day of the week
        Calendar sCalendar = Calendar.getInstance();
        long epochTimeNow = sCalendar.getTimeInMillis();
        epochTimeNow = epochTimeNow - (epochTimeNow % 86400000L);
        Date dateNow = new Date(epochTimeNow);

        if (!LastKmlDownloadDate.equals(dateNow)) {
            // Download KML only if it was not downloaded today
            String dayLongName = sCalendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault()).toLowerCase();

            // Download kml data for `today` - day of the week
            String fullUrl = "http://www.inf.ed.ac.uk/teaching/courses/selp/coursework/" + dayLongName + ".kml";
            DownloadTask task = new DownloadTask();
            task.execute(fullUrl);
            // new day - reset last days goal
            Queries.setGoal(thisActivity, user_id, 0);
            Queries.setGoalAchieved(thisActivity, user_id, 0);
            Queries.updateDistanceWalkedToday(thisActivity, user_id, 0);
        } else {
            kmlMarkers = Queries.loadKML(thisActivity, user_id, mMap);
            if (mCurrentLocation != null) {
                updateUI();
            }
        }
    }


    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "onLocationChanged");
        //TODO calculate distance only if in 'Square'
        if (mCurrentLocation != null && General.isInPlaySquare(mCurrentLocation)) {
            distance_walked += mCurrentLocation.distanceTo(location);
            Log.d(TAG, "Distance Walked " + distance_walked);
        }
        mCurrentLocation = location;
        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
        if (mCurrentLocation != null) {
            updateUI();
        }
    }

    private void updateUI() {
        Log.d(TAG, "updateUI");
        if (mMap == null || mCurrentLocation == null) {
            return;
        }


        if (isGoalSet && !isGoalAchieved) {
            int goal = Queries.getGoal(thisActivity, user_id);
            switch (goal) {
                case 1:
                    // Walk word
                    Log.d(TAG, "Distance walked: " + distance_walked + " meters.");
                    int progress = (int) (Queries.getWalkedToday(thisActivity, user_id) / 100);
                    progressBar.setProgress(progress);
                    if (progress > 99) {
                        Queries.setGoalAchieved(thisActivity, user_id, 2);
                        isGoalAchieved = true;
                    }
                    break;
            }
        }


        double myLat = mCurrentLocation.getLatitude();
        double myLng = mCurrentLocation.getLongitude();
        Log.d("LatLng lat", myLat + "");
        Log.d("LatLng lng", myLng + "");

        // remove old circle around users location
        if (circle != null) {
            circle.remove();
        }

        int difficulty = settings[1];
        if (difficulty == 1) {
            Constants.MAX_DISTANCE_TO_COLLECT_LETTER = 30;
            Constants.MAX_DISTANCE_TO_MAKE_MARKER_VISIBLE = 90;
        } else if (difficulty == 0) {
            Constants.MAX_DISTANCE_TO_COLLECT_LETTER = 20;
            Constants.MAX_DISTANCE_TO_MAKE_MARKER_VISIBLE = 70;
        } else {
            Constants.MAX_DISTANCE_TO_COLLECT_LETTER = 10;
            Constants.MAX_DISTANCE_TO_MAKE_MARKER_VISIBLE = 50;
        }
        Log.d(TAG, "difficulty " + difficulty);

        CircleOptions circleOptions = new CircleOptions()
                .center(new LatLng(myLat, myLng))
                .radius(Constants.MAX_DISTANCE_TO_MAKE_MARKER_VISIBLE)
                .strokeWidth(2)
                .strokeColor(Color.BLUE)
                .fillColor(Color.argb(60, 0, 0, 128));
        circle = mMap.addCircle(circleOptions);


        // check if there are any letter nearby to collect
        if (kmlMarkers.size() > 0) {
            List<Marker> collected_here = new ArrayList<>();
            for (Marker marker : kmlMarkers) {
                LatLng pos = marker.getPosition();
                String letter = marker.getTitle();

                // create a Location object of a letter placemark
                Location loc = new Location(letter);
                loc.setLatitude(pos.latitude);
                loc.setLongitude(pos.longitude);

                // Distance from users location to a placemark
                float distance = mCurrentLocation.distanceTo(loc);


                // check if marker is within the distance to be viable
                if (distance <= Constants.MAX_DISTANCE_TO_MAKE_MARKER_VISIBLE) {
                    marker.setVisible(true);
                    // check if letter is within the distance
                    if (mCurrentLocation.distanceTo(loc) <= Constants.MAX_DISTANCE_TO_COLLECT_LETTER) {
                        Snackbar.make(rootView, letter + " Collected", Snackbar.LENGTH_SHORT).show();
                        collected_chars.add(letter.charAt(0));
                        collected_here.add(marker);

                        // remove current marker for the collected letter
                        marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
                        Log.d(TAG, "letter " + letter + " pos " + marker.getId());
                    }
                } else {
                    marker.setVisible(false);
                }
            }

            //remove all collected letters
            kmlMarkers.removeAll(collected_here);
            Log.d(TAG, " remaining : " + kmlMarkers.size() + "\n Collected: \n" + collected_chars.toString());
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
        getSettings();
        Log.d(TAG, "settings: Battery saver " + settings[0] + " difficulty " + settings[1] + " map style " + settings[2]);
    }

    @Override
    protected void onStop() {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
        super.onStop();
        Log.d(TAG, "onStop");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        if (mGoogleApiClient != null) {
            if (mGoogleApiClient.isConnected() && mRequestingLocationUpdates) {
                startLocationUpdates();
            }
        }
        getSettings();
        checkMapStyle();
        isGoalSet = Queries.isGoalSet(thisActivity, user_id);
        isGoalAchieved = Queries.isGoalAchieved(thisActivity, user_id);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
        //stop location updates when Activity is no longer active
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            stopLocationUpdates();
        }
        getSettings();
    }


    protected void stopLocationUpdates() {
        Log.d(TAG, "stopLocationUpdates");
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        if (!collected_chars.isEmpty()) {
            Queries.addChar(thisActivity, user_id, collected_chars);
            // clear list as all data was saved in DB
            collected_chars.clear();
        }
        Queries.saveKML(thisActivity, user_id, kmlMarkers);
        Queries.updateDistanceWalked(thisActivity, user_id, distance_walked);
        Queries.updateDistanceWalkedToday(thisActivity, user_id, (int) distance_walked);
        // TODO not sure if this reset needed
        distance_walked = 0;
    }

    protected void createLocationRequest() {
        Log.d(TAG, "createLocationRequest");
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(Constants.UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(Constants.FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        if (settings[0] == 1) {
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        } else {
            mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        }
    }

    protected void startLocationUpdates() {
        Log.d(TAG, "startLocationUpdates");
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            if (mCurrentLocation == null) {
                mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(
                        mGoogleApiClient);
                updateUI();
                // Move the camera instantly to last known location with a zoom
                if (mCurrentLocation != null) {
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()), 17));
                }
            }
        }

    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(TAG, "onConnected");
        createLocationRequest();
        startLocationUpdates();
    }

    private void getSettings() {
        settings = Queries.getSettings(thisActivity, user_id);
        Log.d(TAG, "Settings");
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "onConnectionSuspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed");
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + connectionResult.getErrorCode());
    }

    /**
     * Stores activity data in the Bundle.
     */
    public void onSaveInstanceState(Bundle savedInstanceState) {
        Log.d(TAG, "onSaveInstanceState");
        savedInstanceState.putBoolean(REQUESTING_LOCATION_UPDATES_KEY, mRequestingLocationUpdates);
        savedInstanceState.putParcelable(LOCATION_KEY, mCurrentLocation);
        savedInstanceState.putString(LAST_UPDATED_TIME_STRING_KEY, mLastUpdateTime);
        super.onSaveInstanceState(savedInstanceState);
    }

    /**
     * Builds a GoogleApiClient. Uses the {@code #addApi} method to request the
     * LocationServices API.
     */
    protected synchronized void buildGoogleApiClient() {
        Log.i(TAG, "buildGoogleApiClient");
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    public void goToPersonalPages(View view) {
        Log.d(TAG, "goToPersonalPages");
        Intent i = new Intent(this, UserPersonalPages.class);
        i.putExtra(Constants.USER_ID, user_id);
        startActivity(i);
    }

    /**
     * If back button is pressed open a dialog box to logout or close application
     */

    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed");
        // Create dialog box
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.dialog_back_message)
                .setPositiveButton(R.string.log_out, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Go back to Login screen
                        goToLogin();
                    }
                })
                .setNegativeButton(R.string.close_app, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Close the application
                        finishAffinity();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void goToLogin() {
        Log.d(TAG, "goToLogin");
        Intent i = new Intent(this, LoginActivity.class);
        startActivity(i);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult");
        switch (requestCode) {
            case Constants.MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay!

                    if (ContextCompat.checkSelfPermission(this,
                            android.Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        // Kick off the process of building a GoogleApiClient and requesting the LocationServices
                        // API.
                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);

                        // if current location is null then set it to last know location
                        if (mCurrentLocation == null) {
                            Toast.makeText(getApplicationContext(), "Please make sure your GPS is enabled", Toast.LENGTH_SHORT);
                            mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(
                                    mGoogleApiClient);
                        }
                    }

                } else {

                    // permission denied, boo!
                    // Display snackbar indefinitely and ask for permission
                    Snackbar snackbar = Snackbar.make(rootView, R.string.location_permission_denied, Snackbar.LENGTH_INDEFINITE);
                    snackbar.setAction(R.string.retry, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            PermissionHelper.checkLocationPermission(thisActivity);
                        }
                    });
                    snackbar.show();
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    public class DownloadTask extends AsyncTask<String, Void, KmlLayer> {

        @Override
        protected KmlLayer doInBackground(String... urls) {
            URL url;
            HttpURLConnection urlConnection;

            try {
                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                Log.d(TAG, "downloading");
                KmlLayer layer = new KmlLayer(mMap, in, getApplicationContext());
                return layer;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(KmlLayer layer) {
            Log.d(TAG, "onPostExecute");
            if (layer != null) {
                for (KmlPlacemark kmlPlacemark : layer.getPlacemarks()) {
                    String letter = kmlPlacemark.getProperty("description");
                    String placemark = kmlPlacemark.getGeometry().getGeometryObject().toString();
                    int start = placemark.indexOf('(');
                    int end = placemark.indexOf(')');
                    String[] latlngStr = placemark.substring(start + 1, end).split(",");
                    double latPlace = Double.parseDouble(latlngStr[0]);
                    double lngPlace = Double.parseDouble(latlngStr[1]);
                    LatLng latLng = new LatLng(latPlace, lngPlace);

                    // draw maker on the map
                    Marker m = mMap.addMarker(new MarkerOptions()
                            .position(latLng)
                            .title(letter)
                            .visible(false));
                    // populate array list
                    kmlMarkers.add(m);
                }

                if (mCurrentLocation != null) {
                    updateUI();
                }
            }

            // save time stamp of 'last KML download time
            Calendar sCalendar = Calendar.getInstance();
            long epoch = sCalendar.getTimeInMillis();
            // get only date (no hours/minutes/seconds needed)
            epoch = epoch - (epoch % 86400000L);
            Queries.saveKMLDownloadTime(thisActivity, user_id, epoch);
        }
    }

    private void checkMapStyle() {
        if (mMap != null) {
            switch (settings[2]) {
                case 1:
                    mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style_night));
                    break;
                case 2:
                    mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style_silver));
                    break;
                case 3:
                    mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style_dark));
                    break;
                case 4:
                    mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style_aubergine));
                    break;
                case 5:
                    mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style_retro));
                    break;
                default:
                    mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style_standard));
                    break;
            }
        }
    }
}
