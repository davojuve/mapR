package com.example.davit.mapreminder;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import com.example.davit.mapreminder.data.MapReminderConstants;
import com.example.davit.mapreminder.data.Reminder;
import com.example.davit.mapreminder.data.ReminderArrayAdapter;
import com.example.davit.mapreminder.database.ReminderDataSource;
import com.example.davit.mapreminder.database.RemindersDBOpenHelper;
import com.example.davit.mapreminder.service.MapReminderLocateService;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


/**
 * Main Activity
 */
public class MainActivity extends AppCompatActivity {

    private ReminderDataSource dataSource;
    private List<Reminder> reminders;
    private Timer timer = new Timer();
    private EditText searchEditText;
    private ReminderArrayAdapter arrayAdapter;
    private BroadcastReceiver receiver;
    private GoogleApiClient googleApiClient;

    /** check if permission is granted for Android 6.0 Marshmallow */
    private final int PERMISSION_ACCESS_FINE_LOCATION = 100;

    /** for informing service is this activity active or not */
    public static boolean isMainActivityActive = false;

    /** for determining connection to google API */
    private static final int ERROR_DIALOG_REQUEST = 9001;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        /** check internet connection */
//        if (isNetworkAvailable()){
//            Toast.makeText(this, "You Are Connected", Toast.LENGTH_SHORT).show();
//        }else {
//            Toast.makeText(this, "No Internet connection", Toast.LENGTH_SHORT).show();
////            Intent gpsOptionsIntent = new Intent(
////                    android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
////            startActivity(gpsOptionsIntent);
//        }

        /** Check phone settings and require change settings */
        // Create request (for showing that activity needs GSM )
//        LocationRequest mLocationRequestHighAccuracy = LocationRequest.create();
//        mLocationRequestHighAccuracy.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//
//        /** connect to google api client */
//        if (googleApiClient == null) {
//            googleApiClient = new GoogleApiClient.Builder(this)
//                    .addApi(LocationServices.API)
//                    .build();
//            googleApiClient.connect();
//        }
//        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
//                .addLocationRequest(mLocationRequestHighAccuracy);
//
//        PendingResult<LocationSettingsResult> result =
//                LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
//
//        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
//            @Override
//            public void onResult(LocationSettingsResult result) {
//                final Status status = result.getStatus();
////                final LocationSettingsStates = result.getLocationSettingsStates();
//                switch (status.getStatusCode()) {
//                    case LocationSettingsStatusCodes.SUCCESS:
//                        // All location settings are satisfied. The client can initialize location
//                        // requests here.
//                        break;
//                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
//                        // Location settings are not satisfied. But could be fixed by showing the user
//                        // a dialog.
//                        try {
//                            // Show the dialog by calling startResolutionForResult(),
//                            // and check the result in onActivityResult().
//                            status.startResolutionForResult( MainActivity.this, 1000 );
//                        } catch (IntentSender.SendIntentException e) {
//                            // Ignore the error.
//                        }
//                        break;
//                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
//                        // Location settings are not satisfied. However, we have no way to fix the
//                        // settings so we won't show the dialog.
//                        break;
//                }
//            }
//        });

        /** check if still GPS is not enabled enable it */
        final LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );

        if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            buildAlertMessageNoGps();
        }


// TODO: 14-Mar-16 check which method is best

//        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
//                .addLocationRequest(request);
//        builder.setAlwaysShow(true);
//
//        PendingResult<LocationSettingsResult> result =
//                LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
//
//        result.setResultCallback(new ResultCallback<LocationSettingsResult>(){
//            @Override
//            public void onResult(LocationSettingsResult result){
//                final Status status = result.getStatus();
//                switch (status.getStatusCode())
//                {
//                    case LocationSettingsStatusCodes.SUCCESS:
//                        // All location settings are satisfied. The client can initialize location
//                        // requests here.
//                        Log.d("onResult", "SUCCESS");
//                        break;
//                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
//                        // Location settings are not satisfied. But could be fixed by showing the user
//                        // a dialog.
//                        Log.d("onResult", "RESOLUTION_REQUIRED");
//                        try {
//                            // Show the dialog by calling startResolutionForResult(),
//                            // and check the result in onActivityResult().
//                            status.startResolutionForResult( MainActivity.this, 1000);
//                        } catch (IntentSender.SendIntentException e) {
//                            // Ignore the error.
//                        }
//                        break;
//                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
//                        // Location settings are not satisfied. However, we have no way to fix the
//                        // settings so we won't show the dialog.
//                        Log.d("onResult", "UNAVAILABLE");
//                        break;
//                }
//            }
//
//        });


        // todo check code for Android 6.0 Marshmallow
        /** check if permission is granted for Android 6.0 Marshmallow */
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSION_ACCESS_FINE_LOCATION);
        }

        /** Open connection to the database */
        dataSource = new ReminderDataSource(this);
        dataSource.open();

        /** for refreshing MainActivity if checkbox status has changed */
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if("updated".equals( intent.getStringExtra(MapReminderConstants.SERVICE_MESSAGE) )){
                    findAllReminders();
                    // refresh display by adapter.notifyDataSetChanged
                    refreshDisplay();

//                    Log.i("test", "SERVICE_MESSAGE = "+intent.getStringExtra(MapReminderConstants.SERVICE_MESSAGE) );

                    if(servicesOK()){
                        ifAnyActiveRemindersStartService();
                    }
                    MapReminderConstants.SERVICE_MESSAGE = "";
                }else if("update distanceToRadius".equals( intent.getStringExtra(MapReminderConstants.SERVICE_MESSAGE) )){
                    findAllReminders();
                    // refresh display by adapter.notifyDataSetChanged
                    refreshDisplay();

                    // TODO: 09-Mar-16 fix updated
                    MapReminderConstants.SERVICE_MESSAGE = "";
                }else if("checkbox selected".equals( intent.getStringExtra(MapReminderConstants.SERVICE_MESSAGE) )){
                    stopLocationUpdates(null);
                    if(servicesOK()){
                        ifAnyActiveRemindersStartService();
                    }

                    // TODO: 09-Mar-16 fix updated
                    MapReminderConstants.SERVICE_MESSAGE = "";
                }
            }
        };


        /** Finds views by IDs*/
        ListView listView = (ListView) findViewById(android.R.id.list);
        searchEditText = (EditText) findViewById(R.id.search_reminder);


        /** Find all Reminders */
        findAllReminders();



        /** create new adapter for preview as ListView */
        arrayAdapter = new ReminderArrayAdapter( this, 0, reminders );
        listView.setAdapter(arrayAdapter);


        /** add on click listener on ListView */
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Reminder reminder = reminders.get(position);
                displayDetail(reminder.getId());
            }
        });

        /** add on change event listener on Edit text field for search */
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

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
                        MainActivity.this.runOnUiThread(new Runnable() {
                            public void run() {
                                if (s.toString().isEmpty()) {
                                    findAllReminders();
                                } else {
                                    // determine current view
                                    View currentView = getWindow().getDecorView();
                                    hideSoftKeyboard(currentView);

                                    // Find filtered reminders
                                    if (dataSource != null) {
                                        findRemindersByName(RemindersDBOpenHelper.COLUMN_NAME + " LIKE '%" + s.toString() + "%'");
                                    }
                                }

                                refreshDisplay();
                            }
                        });
                    }
                }, MapReminderConstants.DELAY );
            }
        });

    } // End of onCreate


    /** check if permission is granted for Android 6.0 Marshmallow */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_ACCESS_FINE_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Need your location!", Toast.LENGTH_SHORT).show();
                }

                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        dataSource.open();

        isMainActivityActive = true;

        /** Find all reminders if there is no data display a message */
        findAllReminders();

        // refresh display by adapter.notifyDataSetChanged
        refreshDisplay();

        /** restart service in case new reminder was added */
        stopLocationUpdates(null);

        /** check if there is active reminders in database, if true start service otherwise stop */
        if(servicesOK()){
            ifAnyActiveRemindersStartService();
        }

    } //End of onResume

    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(this).registerReceiver(
                (receiver),
                new IntentFilter(MapReminderConstants.CHECKBOX_UPDATE)
        );

    }

    @Override
    protected void onStop() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
        super.onStop();
    }


    @Override
    protected void onPause() {
        super.onPause();

        isMainActivityActive = false;

//        if(dataSource == null){
//            /** Open connection to the database */
//            dataSource = new ReminderDataSource(this);
//            dataSource.open();
//        }
//        /** check if there is active reminders in database, if true start service otherwise stop */
//        if(servicesOK()){
//            ifAnyActiveRemindersStartService();
//        }
        dataSource.close();
    }

    /** check if there are an active reminders return true if yes */
    private void ifAnyActiveRemindersStartService() {
        List<Reminder> activeReminders = dataSource.findFiltered(
                RemindersDBOpenHelper.COLUMN_ISACTIVE + "==1 ",
                RemindersDBOpenHelper.COLUMN_CREATED + " DESC"
        );
        if(activeReminders.size() == 0) {
            stopLocationUpdates( null );
        } else {
            startLocationUpdates( null );
        }
    }


    /** Find all reminders if there is no data display a message */
    private void findAllReminders() {
        reminders = dataSource.findAll(RemindersDBOpenHelper.COLUMN_CREATED + " DESC");
        if(reminders.size() == 0){
            Toast.makeText(this, "There is no Reminders", Toast.LENGTH_SHORT).show();
        }
    }

    /** Find reminders filtered by name */
    private void findRemindersByName(String selection) {
        reminders = dataSource.findFiltered(
                selection,
                RemindersDBOpenHelper.COLUMN_CREATED + " DESC"
        );
    }

    /**
     * startLocationUpdates btn clicked on main activity
     * Starts location services for determine my location
     * @param view View
     */
    public void startLocationUpdates(View view) {
        /** service start */
        startService(new Intent(getBaseContext(), MapReminderLocateService.class));

    } // End of startLocationUpdates

    /**
     * stopLocationUpdates btn clicked on main activity
     * Stops location services for determine my location
     * @param view View
     */
    public void stopLocationUpdates(View view) {
        /** Service stop */
        stopService(new Intent(getBaseContext(), MapReminderLocateService.class));
    }


    /**
     * create new adapter for preview as ListView
     * get data from DataProvider class
     * */
    @NonNull
    private void refreshDisplay() {
        arrayAdapter.setObjects(reminders);
        arrayAdapter.notifyDataSetChanged();
    }

    /** When is clicked reminder on ListView */
    private void displayDetail(Long id) {
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra(MapReminderConstants.REMINDER_ID, id);
        startActivity(intent);
    }

    /** When is clicked + add reminder */
    public void addReminder(View view) {
        startActivity(new Intent(this, AddReminder.class));
    }


    /**
     * search button On click listener
     * filter reminders by search word
     * @param view View
     */
    public void filterReminders(View view) {
        hideSoftKeyboard(view);
        // take search word
        String searchText = searchEditText.getText().toString();

        // check if there is a search key word
        if( searchText.isEmpty() ) {
            findAllReminders();
        }else{
            // Find filtered reminders
            if (dataSource != null) {
                findRemindersByName(RemindersDBOpenHelper.COLUMN_NAME + " LIKE '%" + searchText +"%'");
            }
        }

        refreshDisplay();
        if( searchText.isEmpty() ){
            Toast.makeText(this, "Show All", Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(this, "Search for " + searchText, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * todo add to static util class
     *  method for hiding keyBoard
     */
    private void hideSoftKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


    // TODO: 08-Mar-16 find out is there similar functionality in mainActivity (maybe even method!)
    /**
     * for checking if google mapping service is available
     * public static final int NETWORK_ERROR == 7;
     * @return bool
     */
    public boolean servicesOK() {
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int isAvailable = googleAPI.isGooglePlayServicesAvailable(this);

        if (isAvailable == ConnectionResult.SUCCESS) {
            return true;
        }else if (googleAPI.isUserResolvableError(isAvailable)) {
            googleAPI.getErrorDialog(this, isAvailable, ERROR_DIALOG_REQUEST)
                    .show();
        } else {
            Toast.makeText(this, "Can't connect to mapping service", Toast.LENGTH_SHORT)
                    .show();
        }
        return false;
    }

    /** For Determining is internet connection available */
//    private boolean isNetworkAvailable() {
//        ConnectivityManager connectivityManager
//                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
//        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
//    }

    /** alert dialog box if gps is not enabled */
    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

}
