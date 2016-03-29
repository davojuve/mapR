package com.example.davit.mapreminder;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.davit.mapreminder.data.Reminder;
import com.example.davit.mapreminder.database.ReminderDataSource;
import com.example.davit.mapreminder.service.MapReminderLocateService;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class NotificationActivity extends AppCompatActivity implements OnMapReadyCallback {

    GoogleMap mMapDetail;
    Reminder reminder;
    private static final int ERROR_DIALOG_REQUEST = 9001;
    protected double targetCordLat = 0.0;
    protected double targetCordLng = 0.0;
    private String reminderName;
    private String reminderImageName;
    ReminderDataSource dataSource;
    private Long reminderId;
    protected static final String REMINDER_ID = "reminderId";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        /** Back button on detail page */
        Button notificationBtnBack = (Button) findViewById(R.id.notificationBtnBack);
        notificationBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        /** Open connection to the database */
        dataSource = new ReminderDataSource(this);
        dataSource.open();

        /**
         *  get reminder by id (from database)
         *  id from intent (MainActivity)
         *  set reminders Name, Image, Description, coordinates
         */
        reminderId = getIntent().getLongExtra(MapReminderLocateService.REMINDER_ID, 0);
        reminder = dataSource.findById( reminderId );

        // set name
        reminderName = reminder.getReminderName();
        TextView notificationReminderName = (TextView) findViewById(R.id.notificationReminderName);
        notificationReminderName.setText(reminderName);

        // set image
        ImageView detailImage = (ImageView) findViewById(R.id.notificationReminderImage);
        reminderImageName = reminder.getType();
        setImageResource( detailImage, reminderImageName );

        // Set description
        TextView detailDescription = (TextView) findViewById(R.id.notificationReminderDescription);
        detailDescription.setText(reminder.getDescription());

        // set map coordinates
        targetCordLat = reminder.getTargetCordLat();
        targetCordLng = reminder.getTargetCordLng();

        /** Check google map service */
        if (googleMapServicesOK()) {
            SupportMapFragment mapFragment =
                    (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.notificationMap);
            mapFragment.getMapAsync(this);
        }else{
            Toast.makeText(this, "Problem with mapping service", Toast.LENGTH_SHORT).show();
        }

    } // End of onCreate

    @Override
    protected void onResume() {
        super.onResume();
        dataSource.open();
    }

    @Override
    protected void onPause() {
        super.onPause();
        dataSource.close();
    }

    /** when map is ready */
    @Override
    public void onMapReady(GoogleMap map) {
        if (mMapDetail == null) {
            mMapDetail = map;
        }

        goToLocation(targetCordLat, targetCordLng, 15);

        if (mMapDetail != null) {
            /** add custom info window on marker */
            mMapDetail.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                @Override
                public View getInfoWindow(Marker marker) {
                    return null;
                }

                @Override
                public View getInfoContents(Marker marker) {
                    View v = getLayoutInflater().inflate(R.layout.marker_info_window, null);

                    /** add custom info on marker info window */
                    ImageView markerImageView = (ImageView) v.findViewById(R.id.markerImageView);
                    TextView markerReminderName = (TextView) v.findViewById(R.id.markerReminderName);

                    setImageResource(markerImageView, reminderImageName);
                    markerReminderName.setText(reminderName);

                    return v;
                }

            });
        } // End of if mMapDetail != null
    } // End of onMapReady

    /**
     * for checking if google mapping service is available
     * @return bool
     */
    public boolean googleMapServicesOK() {
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int isAvailable = googleAPI.isGooglePlayServicesAvailable(this);
        if (isAvailable == ConnectionResult.SUCCESS) {
            return true;
        } else if (googleAPI.isUserResolvableError(isAvailable)) {
            googleAPI.getErrorDialog(this, isAvailable, ERROR_DIALOG_REQUEST).show();
        } else {
            Toast.makeText(this, "Can't connect to mapping service", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    /**
     * set map location and zoom level
     * @param lat double
     * @param lng double
     * @param zoom float
     */
    private void goToLocation(double lat, double lng, float zoom){
        LatLng latLng = new LatLng(lat, lng);
//        CameraUpdate update = CameraUpdateFactory.newLatLng(latLng);
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(latLng, zoom);
        mMapDetail.moveCamera(update);
        // if you want to see transition use animateCamera()

        // add marker to map on detail activity
        addMarker(mMapDetail, lat, lng);

//        Toast.makeText(this, "Location of "+reminder.getType(), Toast.LENGTH_SHORT).show();
    }

    /**
     * Add marker on map method
     * @param map GoogleMap
     * @param lat double
     * @param lng double
     */
    private void addMarker(GoogleMap map, double lat, double lng){
        map.addMarker(new MarkerOptions()
                        .position(new LatLng(lat, lng))
                        .title(reminder.getType())
        );
    }

    /**
     * Set image
     * @param iv ImageView
     * @param imageName String (reminder.getType() )
     */
    private void setImageResource( ImageView iv, String imageName ){
        int resID = getResources().getIdentifier(imageName, "drawable", getPackageName());
        iv.setImageResource(resID);
    }

    /** remove Reminder Click Listener*/
    public void notificationReminderBtnRemoveClickListener(View view) {
        /** display alert are you sure? */
        new AlertDialog.Builder(this)
                .setMessage("Are you sure you want to delete?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dataSource.deleteById(reminderId);
//                                todo cancel notification if active
//                                if (isNotificationActive) {
//                                    notificationManager.cancel(notificationId);
//                                    isNotificationActive = false;
//                                }
                        NotificationActivity.this.finish();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }


    /** detailReminder Btn Edit Click Listener */
    public void notificationReminderBtnEditClickListener(View view) {
        Intent intent = new Intent(this, AddReminder.class);
        if (reminderId != null) {
            intent.putExtra(REMINDER_ID, reminderId);
        }
        startActivity(intent);
        finish();
    }
}
