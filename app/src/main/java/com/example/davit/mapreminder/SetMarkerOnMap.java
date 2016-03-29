package com.example.davit.mapreminder;

import android.Manifest;
import android.app.Fragment;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.example.davit.mapreminder.data.MapReminderConstants;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class SetMarkerOnMap extends AppCompatActivity implements OnMapReadyCallback{
    private GoogleMap mMap;
    private Marker marker;
    private static final int ERROR_DIALOG_REQUEST = 9001;
    private double targetLatitude = 40.1702798;
    private double targetLongitude = 44.5195549;
    private EditText searchText;
    private Timer timer = new Timer();

    /** use current location to set orange marker on map */
    // TODO: get current location from service
//    private double currentLat = MainActivity.currentLatitude;
//    private double currentLng = MainActivity.currentLongitude;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_marker_on_map);

        /** find view for search text on map */
        searchText = (EditText) findViewById(R.id.searchText);
        /** add on change event listener on Edit text field for search */

        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (timer != null)
                    timer.cancel();
            }

            @Override
            public void afterTextChanged(final Editable s) {
                timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        SetMarkerOnMap.this.runOnUiThread(new Runnable() {
                            public void run() {
                                if (!s.toString().isEmpty()) {
                                    View decorView = getWindow().getDecorView();
                                    if (decorView != null) {
                                        geoLocate(decorView);
                                    }
                                }
                            }
                        });
                    }
                }, MapReminderConstants.DELAY);
            }
        });


        /** Get Lat and LNG from AddReminder activity*/
        targetLatitude = getIntent().getDoubleExtra(MapReminderConstants.REMINDER_LATITUDE, -11111111);
        if ( targetLatitude != -11111111 ) {
            targetLongitude = getIntent().getDoubleExtra(MapReminderConstants.REMINDER_LONGITUDE, -11111111);
        }

        /** Set values for the width and height of popup window*/
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        /** if we want width to be 80% of phone screen size *0.8*/
        getWindow().setLayout((int) (displayMetrics.widthPixels * .9), (int) (displayMetrics.heightPixels * .8));


        // check google service availability
        if (isServicesOK()) {
            SupportMapFragment mapFragment =
                    (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.setMarkerMapFragment);
            mapFragment.getMapAsync(this);


        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        // if there is no map create one
        if (mMap == null) {
            mMap = googleMap;
        }

        // TODO: replace in goToLocation(currentLat, currentLng, 15); .position(new LatLng(currentLat, currentLng))
        // go to my current location

        /** set marker if it is edit */
        if(targetLatitude != -11111111){
            goToLocation(targetLatitude, targetLongitude, 15);
            setMarkerOnMap(targetLatitude, targetLongitude);
        }else{
            goToLocation(40.1702798, 44.5195549, 15);
        }

        /** For locating me */
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);



        // set marker on my current location with specified options
        // TODO: when current coordinates will be add set marker
//        MarkerOptions options = new MarkerOptions()
//                .title("My Location")
//                .position(new LatLng(40.1702798, 44.5195549))
//                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
//        setMarker(options);



        /**
         * add on map Long click listeners
         * adds one marker on long click on map
         */
        // put parker on long click on map
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {

                setMarkerOnMap(latLng.latitude, latLng.longitude);
                targetLatitude = marker.getPosition().latitude;
                targetLongitude = marker.getPosition().longitude;
            }
        }); // End of setOnMapLongClickListener

        /** show instructions to use */
        Toast toast= Toast.makeText(this, "Click and hold to set a marker", Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL, 0, 0);
        toast.show();

    } // End of onMapReady



    /**
     * Set marker on map with specified options
     * @param latitude double
     * @param longitude double
     */
    private void setMarkerOnMap(double latitude, double longitude) {
        MarkerOptions options = new MarkerOptions()
                .position(new LatLng(latitude, longitude))
                .draggable(true)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        if (marker != null) {
            marker.remove();
        }
        marker = mMap.addMarker(options);
    }

// TODO: 11-Mar-16 make usable function
    /**
     * For checking if google mapping service is available
     * @return bool
     */
    public boolean isServicesOK() {
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int isAvailable = googleAPI.isGooglePlayServicesAvailable(this);

        if ( isAvailable == ConnectionResult.SUCCESS ) {
            return true;
        } else if ( googleAPI.isUserResolvableError(isAvailable) ) {
            googleAPI.getErrorDialog(this, isAvailable, ERROR_DIALOG_REQUEST).show();
        }else{
            Toast.makeText(this, "Can't connect to mapping service", Toast.LENGTH_SHORT).show();
        }

        return false;
    }

    /**
     * Go to specified location with specified zoom level
     * @param latitude double
     * @param longitude double
     * @param zoom float
     */
    public void goToLocation(double latitude, double longitude, float zoom) {
        LatLng latLng = new LatLng(latitude, longitude);
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(latLng, zoom);

        mMap.moveCamera(update);
    }


    /**
     * Set marker on map with specified options
     * @param options MarkerOptions
     */
//    public void setMarker( MarkerOptions options  ){
//        mMap.addMarker(options);
//    }

    /**
     * when clicked btnSelectMarkerDone on SetMarkerOn Map activity
     * @param view View
     */
    public void btnSelectMarkerDoneClickListener(View view) {
        getIntent().putExtra("targetLatitude", targetLatitude);
        getIntent().putExtra("targetLongitude", targetLongitude);
        setResult(RESULT_OK, getIntent());
        finish();
    }



    /**
     * todo add to static util class
     *  method for hiding keyBoard
     */
    private void hideSoftKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    /** when search is clicked on activity_main_with_search*/
    public void geoLocate(View view) {
        hideSoftKeyboard(view);
        EditText editText = (EditText) findViewById(R.id.searchText);
        String searchString = editText.getText().toString();
        Toast.makeText(this, "Searching for " + searchString, Toast.LENGTH_SHORT).show();

        Geocoder gc = new Geocoder(this);
        try {
            List<Address> list = gc.getFromLocationName(searchString, 1);
            if (list.size() > 0) {
                Address address = list.get(0);
                String locality = address.getLocality();

                double lat = address.getLatitude();
                double lng = address.getLongitude();

                if(locality != null){
                    Toast.makeText(this, "Found: " + locality, Toast.LENGTH_SHORT).show();
                    goToLocation(lat, lng, 15);
                }else{
                    Toast.makeText(this, "Nothing was found, please be more specific.", Toast.LENGTH_SHORT).show();
                    lat = 40.177620;
                    lng = 44.512552;
                }


                /** for letting only one marker */
                if(marker != null){
                    marker.remove();
                }

                /** set marker on found location specified in search */
//                MarkerOptions options = new MarkerOptions()
//                        .title(locality)
//                        .position(new LatLng(lat, lng))
//                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
//
//                /** add country name to snippet (when clicked on icon) */
//                if ( address.getCountryName() != null ){
//                    if( address.getCountryName().length() > 0 ){
//                        options.snippet(address.getCountryName());
//                    }
//                    marker =  mMap.addMarker(options);
//                }

            }else{
                Toast.makeText(this, "Nothing has been found, please be more exact", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
