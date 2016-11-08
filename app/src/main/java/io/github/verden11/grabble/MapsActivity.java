package io.github.verden11.grabble;


import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
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
import java.util.Locale;

import io.github.verden11.grabble.Constants.Constants;
import io.github.verden11.grabble.Helper.PermissionHelper;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    // Keys for storing activity state in the Bundle.
    protected final static String REQUESTING_LOCATION_UPDATES_KEY = "requesting-location-updates-key";
    protected final static String LOCATION_KEY = "location-key";
    protected final static String LAST_UPDATED_TIME_STRING_KEY = "last-updated-time-string-key";
    private final String TAG = "MapsActivity FragmentActivity";
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
    ArrayList<Marker> kmlMarkers;
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

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PermissionHelper.checkLocationPermission(thisActivity);
        }

        //initialise variables
        rootView = findViewById(android.R.id.content);
        kmlMarkers = new ArrayList<>();
        collected_chars = new ArrayList<>();
        mRequestingLocationUpdates = true;
        mLastUpdateTime = "";

        // Update values using data stored in the Bundle.
        updateValuesFromBundle(savedInstanceState);


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


    }


    /**
     * Updates fields based on data stored in the bundle.
     *
     * @param savedInstanceState The activity state saved in the Bundle.
     */
    private void updateValuesFromBundle(Bundle savedInstanceState) {
        Log.i(TAG, "Updating values from bundle");
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
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "onMapReady");
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

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


        // Get day of the week
        Calendar sCalendar = Calendar.getInstance();
        String dayLongName = sCalendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault()).toLowerCase();

        // Download kml data for `today` - day of the week
        String fullUrl = "http://www.inf.ed.ac.uk/teaching/courses/selp/coursework/" + dayLongName + ".kml";
        DownloadTask task = new DownloadTask();
        task.execute(fullUrl);


    }


    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "onLocationChanged");

        mCurrentLocation = location;
        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
        if (mCurrentLocation != null) {
            updateUI();
        }
    }

    private void updateUI() {
        Log.d(TAG, "updateUI");
        double myLat = mCurrentLocation.getLatitude();
        double myLng = mCurrentLocation.getLongitude();
        Log.d(TAG, myLat + "");
        Log.d(TAG, myLng + "");
        if (mMap != null) {


            // remove old circle around users location
            if (circle != null) {
                circle.remove();
            }

            CircleOptions circleOptions = new CircleOptions()
                    .center(new LatLng(myLat, myLng))
                    .radius(Constants.MAX_DISTANCE_TO_MAKE_MARKER_VISIBLE)
                    .strokeWidth(2)
                    .strokeColor(Color.BLUE)
                    .fillColor(Color.argb(60, 0, 0, 128));
            circle = mMap.addCircle(circleOptions);


            // check if there are any letter nearby to collect
            if (kmlMarkers.size() > 0) {
                ArrayList<Marker> collected_here = new ArrayList<>();
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
                            Snackbar snackbar = Snackbar.make(rootView, letter + " Collected", Snackbar.LENGTH_SHORT);
                            snackbar.show();
                            collected_chars.add(letter.charAt(0));
                            collected_here.add(marker);
                            // remove current marker for the collected letter
                            marker.remove();
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

    }


    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
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

    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
        //stop location updates when Activity is no longer active
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            stopLocationUpdates();
        }
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(Constants.UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(Constants.FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    protected void startLocationUpdates() {
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }

    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(TAG, "onConnected");
        createLocationRequest();
        startLocationUpdates();
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
        Log.i(TAG, "Building GoogleApiClient");
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    public void goToPersonalPages(View view) {
        Intent i = new Intent(this, UserPersonalPages.class);
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
        Intent i = new Intent(this, LoginActivity.class);
        startActivity(i);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case Constants.MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            android.Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        // Kick off the process of building a GoogleApiClient and requesting the LocationServices
                        // API.
                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
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
                    Location loc = new Location("");
                    loc.setLatitude(latLng.latitude);
                    loc.setLongitude(latLng.longitude);


                    // draw maker on the map
                    Marker m = mMap.addMarker(new MarkerOptions()
                            .position(latLng)
                            .title(letter)
                            .visible(false));
                    // populate array list
                    kmlMarkers.add(m);

                    if (mCurrentLocation != null) {
                        float distance = mCurrentLocation.distanceTo(loc);
                        if (distance <= Constants.MAX_DISTANCE_TO_MAKE_MARKER_VISIBLE) {
                            m.setVisible(true);
                        }
                    }
                }
            }
        }
    }
}
