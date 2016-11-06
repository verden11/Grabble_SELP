package io.github.verden11.grabble;


import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;

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
    Marker myLocMarker;
    ArrayList<Marker> kmlMarkers;
    View rootView;
    ArrayList<Character> collected_chars;
    private GoogleMap mMap;  // Might be null if Google Play services APK is not available.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        setContentView(R.layout.activity_maps);
        rootView = findViewById(android.R.id.content);
        kmlMarkers = new ArrayList<>();
        collected_chars = new ArrayList<>();
        mRequestingLocationUpdates = true;
        mLastUpdateTime = "";

        // Update values using data stored in the Bundle.
        updateValuesFromBundle(savedInstanceState);

        // Kick off the process of building a GoogleApiClient and requesting the LocationServices
        // API.
        buildGoogleApiClient();


//        askForPermission();
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
        double lat = mCurrentLocation.getLatitude();
        double lng = mCurrentLocation.getLongitude();
        Log.d(TAG, lat + "");
        Log.d(TAG, lng + "");
        if (mMap != null) {

            // remove old marker for mCurrentLocation location
            if (myLocMarker != null) {
                myLocMarker.remove();
            }

            // add new marker for mCurrentLocation location
            myLocMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(lat, lng))
                    .title("Your location")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));


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

                    // check if letter is within the distance
                    if (mCurrentLocation.distanceTo(loc) <= Constants.MIN_DISTANCE_TO_COLLECT_LETTER) {
                        Snackbar snackbar = Snackbar.make(rootView, letter + " Collected", Snackbar.LENGTH_SHORT);
                        snackbar.show();
                        collected_chars.add(letter.charAt(0));
                        collected_here.add(marker);
                        // remove current marker from the map
                        marker.remove();
                        Log.d(TAG, "letter " + letter + " pos " + marker.getId());
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
        mGoogleApiClient.connect();
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
        if (mGoogleApiClient.isConnected() && mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
        if (mGoogleApiClient.isConnected()) {
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
        // The final argument to {@code requestLocationUpdates()} is a LocationListener
        // (http://developer.android.com/reference/com/google/android/gms/location/LocationListener.html).
//        askForPermission();
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);

    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(TAG, "onConnected");


        // add a permission check
        if (mCurrentLocation == null) {
            // check for permission
            mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
            if (mCurrentLocation != null) {
                updateUI();
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                        new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()), 18));
            }
        }

        // Add a marker and my position
        if (mRequestingLocationUpdates) {
            startLocationUpdates();
        }
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

    public void askForPermission() {
        Log.d(TAG, "askForPermission");

        ActivityCompat.requestPermissions(this,
                new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                Constants.permissions.ACCESS_FINE_LOCATION);
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
        createLocationRequest();
    }

    public void centerMyLocation(View view) {
        if (mCurrentLocation != null) {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()), 18));

        }
    }

    public void moveToUserPeronalPagesActivity(View view) {
        Intent i = new Intent(this, UserPersonalPages.class);
        startActivity(i);
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
                            .title(letter));
                    // populate array list
                    kmlMarkers.add(m);
                }
            }
        }
    }
}
