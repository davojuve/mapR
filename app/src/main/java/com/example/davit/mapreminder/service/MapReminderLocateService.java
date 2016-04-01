package com.example.davit.mapreminder.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;
import com.example.davit.mapreminder.MainActivity;
import com.example.davit.mapreminder.NotificationActivity;
import com.example.davit.mapreminder.R;
import com.example.davit.mapreminder.data.Reminder;
import com.example.davit.mapreminder.data.ReusableFunctions;
import com.example.davit.mapreminder.database.ReminderDataSource;
import com.example.davit.mapreminder.database.RemindersDBOpenHelper;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.io.File;
import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MapReminderLocateService extends Service implements
        GoogleApiClient.ConnectionCallbacks,
        SensorEventListener,
        GoogleApiClient.OnConnectionFailedListener {

    private ReminderDataSource dataSource;
    /** for refreshing MainActivity if checkbox status has changed */
//    private LocalBroadcastManager broadcaster;
    /** for my location */
    private GoogleApiClient googleApiClient;
    protected LocationListener mListener;
    boolean isNotificationActive = false;
    int notificationId;
    private NotificationCompat.Builder notificBuilder;
    public static final String REMINDER_ID = "reminderId";
    /** for my location REQUEST_TIME in milliseconds */
    private static int REQUEST_TIME = 10000;
    private static double minDistance = 10000;

    /** for movement detection */
    private SensorManager sensorMan;
    private Sensor accelerometer;
    private float mAccel;
    private float mAccelCurrent;
    private float mAccelLast;
    private static boolean movementDetected = false;

    // TODO: 10-Mar-16 countOfAttemptsIn550
        private static int countOfAttemptsIn550;
        private static int countOfAttemptsIn1000;
        private static int countOfAttemptsInLargeDistance;


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Let it continue running until it is stopped.
        /** for refreshing MainActivity if checkbox status has changed */
//        broadcaster = LocalBroadcastManager.getInstance(this);

        /** Open connection to the database */
        dataSource = new ReminderDataSource(this);
        dataSource.open();

        // TODO: 01-Apr-16 put save Log to the right place
        saveLog("Service started ");

        /** start Location update  */
        connectToGoogleApiClient();
    // TODO: 31-Mar-16 remove toast
        Toast.makeText(this, "Service Started", Toast.LENGTH_SHORT).show();
        return START_STICKY;
    }

    /** START_STICKY does not work on Kitkat */
    @Override
    public void onTaskRemoved(Intent rootIntent) {

        /** If service was stopped by user manually and there are active reminders start it */
        List<Reminder> reminders = dataSource.findFiltered(
                RemindersDBOpenHelper.COLUMN_ISACTIVE + "==1 ",
                RemindersDBOpenHelper.COLUMN_CREATED + " ASC"
        );
        if (reminders.size() != 0) {
            startService(new Intent(getBaseContext(), MapReminderLocateService.class));
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        // todo remove toast
        Toast.makeText(this, "Service Terminated", Toast.LENGTH_SHORT).show();
        REQUEST_TIME = 10000;

        /** stop Location update  */
        if (googleApiClient != null) {
            disconnectFromGoogleApiClient();
        }

        // TODO: 15-Mar-16 move sensor unregister
        sensorMan.unregisterListener(this);

        /** If service was stopped by user manually and there are active reminders start it */
        List<Reminder> reminders = dataSource.findFiltered(
                RemindersDBOpenHelper.COLUMN_ISACTIVE + "==1 ",
                RemindersDBOpenHelper.COLUMN_CREATED + " ASC"
        );
        if (reminders.size() != 0) {
            startService(new Intent(getBaseContext(), MapReminderLocateService.class));
            // TODO: 15-Mar-16 move sensor registration to appropriate place
//            sensorMan.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        }

        if (dataSource != null) {
            /** Close the database */
            dataSource.close();
            dataSource = null;
        }
    }

    /**
     * Next 3 methods for implemented interfaces (for My Location GoogleApiClient.ConnectionCallbacks )
     * GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener
     */
    @Override
    public void onConnected(Bundle bundle) {
        // TODO: 31-Mar-16 remove toast API started
        Toast.makeText(this, "API started", Toast.LENGTH_SHORT).show();
        mListener = new LocationListener() {
            /**
             * will be called each time we send out request
             *  and a new location comes back
             */
            @Override
            public void onLocationChanged(Location location) {
                locateMe(location);
            }
        };


        /** Create request Priority and interval */
        LocationRequest request = LocationRequest.create();
        // TODO: set to PRIORITY_LOW_POWER remove log
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        Log.i("test", "REQUEST_TIME (before setting): "+String.valueOf(REQUEST_TIME));
        request.setInterval(REQUEST_TIME);  // ms 1000 = 1 seconds
        request.setFastestInterval(5 * 1000);

        try {
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, request, mListener);
        } catch (SecurityException e) {
            e.printStackTrace();
            // TODO: when permission to locate is not granted
        }

    } // end of onConnected

    /** method to call when called onLocationCHanged in onConnected */
    private void locateMe(Location location) {

        onLocateNum++;

        // todo remove toast of seconds
        String t = String.valueOf(REQUEST_TIME / 1000);
        Log.i("test", "Seconds: " + t);
        Toast.makeText(getBaseContext(), "Seconds: " + t, Toast.LENGTH_SHORT).show();

        /** count the distance between current and target location */
        if (dataSource == null) {
            dataSource = new ReminderDataSource(this);
            dataSource.open();
        }
        // get from database all active reminders
        List<Reminder> reminders = dataSource.findFiltered(
                RemindersDBOpenHelper.COLUMN_ISACTIVE + "==1 ",
                RemindersDBOpenHelper.COLUMN_CREATED + " ASC"
        );

        // TODO: 14-Mar-16 id needed enable this functionality if checking internet open comment on internet status check
        /************************** GPS ***************************/
        // getting GPS status
//        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
//        boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
//        if (isGPSEnabled) {
//            Log.i("test", "enabled");
//        } else {
//            Log.i("test", "not enabled");
//            /** notify user that GPS is off */
//            //Define sound URI
//            Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//            notificBuilder = new NotificationCompat.Builder(this)
//                    .setSmallIcon( getResources().getIdentifier("alert", "drawable", getPackageName()))
//                    .setContentTitle("GPS is disabled")
//                    .setAutoCancel(true)
//                    .setLights(Color.BLUE, 500, 500)
//                    .setContentText("MapReminder will not work without internet or GPS!")
//                    .setSound(soundUri);
//
////            Intent resultIntent = new Intent(this, NotificationActivity.class);
////            resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//
//            // Because clicking the notification opens a new ("special") activity, there's
//            // no need to create an artificial back stack.
////            PendingIntent pendingIntent =
////                    PendingIntent.getActivity(this, 1, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
////            notificBuilder.setContentIntent(pendingIntent);
//
//            /* for notification */
//            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//            notificationManager.notify(1, notificBuilder.build());
//        }



//        /** PROCESS for Get Longitude and Latitude **/
//        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
//
//// Define a listener that responds to location updates
//
//        LocationListener locationListener = new LocationListener() {
//            public void onLocationChanged(Location location) {
//                // Called when a new location is found by the network location provider.
//
//                String longitude = String.valueOf(location.getLongitude());
//                String latitude = String.valueOf(location.getLatitude());
//                Log.d("msg", "changed Loc : " + longitude + ":" + latitude);
//            }
//
//        };
//
//// getting GPS status
//       boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
//
//// check if GPS enabled
//        if (isGPSEnabled) {
//
//            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                // TODO: Consider calling
//                //    ActivityCompat#requestPermissions
//                // here to request the missing permissions, and then overriding
//                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                //                                          int[] grantResults)
//                // to handle the case where the user grants the permission. See the documentation
//                // for ActivityCompat#requestPermissions for more details.
//                return;
//            }
//            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//
//            if(location != null)
//            {
//                String longitude = String.valueOf(location.getLongitude());
//                String latitude = String.valueOf(location.getLatitude());
//                Log.d("msg", "Loc : "+longitude + ":"+latitude);
//
//                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, (android.location.LocationListener) locationListener);
//            }else
//            {
//        /*
//        Criteria criteria = new Criteria();
//        criteria.setAccuracy(Criteria.ACCURACY_FINE);
//        criteria.setCostAllowed(true);
//        String provider = locationManager.getBestProvider(criteria, true);
//         */
//
//                location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
//
//                if(location != null)
//                {
//                    String longitude = String.valueOf(location.getLongitude());
//                    String latitude = String.valueOf(location.getLatitude());
//
//                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, (android.location.LocationListener) locationListener);
//                }else
//                {
//                    String longitude   = "0.00";
//                    String latitude    = "0.00";
//                }
//            }
//        }

        /**********************************************************/

        // set min distance to first reminder distance
        if (reminders.size() != 0) {
            double distanceInMeterForFirstReminder = CalculationByDistance(location.getLatitude(), location.getLongitude(),
                    reminders.get(0).getTargetCordLat(), reminders.get(0).getTargetCordLng());
            minDistance = Math.abs(distanceInMeterForFirstReminder - reminders.get(0).getDistance());


            for (Reminder reminder : reminders) {
                double distanceInMeter = CalculationByDistance(location.getLatitude(), location.getLongitude(),
                        reminder.getTargetCordLat(), reminder.getTargetCordLng());


                /** check if nearest reminder is very far, change request time */
                double distanceToReminderRadius = distanceInMeter-reminder.getDistance();

                dataSource.updateDistanceToReminderRadius(distanceToReminderRadius, reminder.getId());

                if( Math.abs(distanceToReminderRadius) < minDistance ){
                    minDistance = Math.abs(distanceToReminderRadius);
                }

                // TODO: 15-Mar-16 remove log
//                    Log.i("test", "minDistance after check = " + minDistance);
//        // todo if min of(minDistance - reminder.getDistance()) think

                /** Set notification */
                setNotification(reminder, distanceInMeter);
            } // end of for

            // TODO: 09-Mar-16 delete update distanceToRadius
            /** update screen on main activity for distance to radius (in red) */
            if(MainActivity.isMainActivityActive){
                ReusableFunctions.sendResultToMainActivity(this, "update distanceToRadius");
            }

            /**
             * 100 km/h    100000 - 3600000 -> 1m - 36 ms
             */
            // lets assume in 1s = 300m
            if( minDistance >= 550 && minDistance <= 1150 ){
                REQUEST_TIME = 7 * 1000;  // 7 sec
                countOfAttemptsIn1000++;
                countOfAttemptsIn550 = countOfAttemptsInLargeDistance = 0;

            }else if( minDistance >= 0 && minDistance <= 550 ){
                REQUEST_TIME = 5 * 1000;  // 5 sec
                countOfAttemptsIn550++;
                countOfAttemptsIn1000 = countOfAttemptsInLargeDistance = 0;
            }else{
                REQUEST_TIME = (int) minDistance * 36/2;
                countOfAttemptsInLargeDistance++;
                countOfAttemptsIn550 = countOfAttemptsIn1000 = 0;
            }

            if(countOfAttemptsIn550 == 1 || countOfAttemptsIn1000 == 1 || countOfAttemptsInLargeDistance == 1){
                sensorMan.unregisterListener(this);
            }

            // TODO: 10-Mar-16 write algorithm for sleeping longer
            /** if in 3 min 20s we are still in 550m range set REQUEST_TIME to 8 hours and register movement listener */
            if(countOfAttemptsIn550 >= 60 ){ // 60 approximately = 3min 20s
                REQUEST_TIME = 8 * 60 * 60 * 1000; // 60*60*1000 = 8 hour
                // TODO: 15-Mar-16 move sensor registration to appropriate place
                sensorMan.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
            }

            /** if in 5 min 10s we are still in 1000m range set REQUEST_TIME to 8 hours and register movement listener */
            if(countOfAttemptsIn1000 >= 60 ){ // 60 approximately = 5min 10s
                REQUEST_TIME = 8 * 60 * 60 * 1000; // 60*60*1000 = 8 hour
                // TODO: 15-Mar-16 move sensor registration to appropriate place
                sensorMan.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
            }

            /** if in 5 min 10s we are still in >1000m range set REQUEST_TIME to 8 hours and register movement listener */
            if(countOfAttemptsInLargeDistance >= 30 ){ // depends on distance
                REQUEST_TIME = 8 * 60 * 60 * 1000; // 60*60*1000 = 8 hour
                // TODO: 15-Mar-16 move sensor registration to appropriate place
                sensorMan.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
            }

//            else if(countOfAttemptsIn550 >= 30 && countOfAttemptsIn550 <= 32){ //72 = 6min
//                REQUEST_TIME = 2 * 60 * 1000; // 2 min
//            }else if(countOfAttemptsIn550 >= 180 && countOfAttemptsIn550 <= 180){ //180 = 15min
//                REQUEST_TIME = 5 * 60 * 1000; // 5 min
//                countOfAttemptsIn550 = 0;
//            }


//            Log.i("test", "countOfAttemptsIn550: " + countOfAttemptsIn550);
//            Log.i("test", "countOfAttemptsIn1000: " + countOfAttemptsIn1000);
//            Log.i("test", "countOfAttemptsInLargeDistance: " + countOfAttemptsInLargeDistance);
//            Log.i("test", "REQUEST_TIME: "+REQUEST_TIME);
//            Log.i("test", "\n");
        }

        // todo find out why need 2 stop starts for changing actual request time
        // for every reminder count the distance


//        Log.i("test", "REQUEST_TIME after check= " + REQUEST_TIME);

        /** disconnect and connect for changing REQUEST_TIME */
        if (googleApiClient != null && onLocateNum == 1) {
            disconnectFromGoogleApiClient();
            connectToGoogleApiClient();
        }
    }


    @Override
    public void onConnectionSuspended(int i) {
//        Log.d("test", "onConnectionSuspended");
//        Toast.makeText(this,"NO INTERNET",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
//        Log.d("test", "onConnectionFailed");
//        Toast.makeText(this,"NO INTERNET",Toast.LENGTH_SHORT).show();
    }


    public int onLocateNum =-1;

    /** connect to google api client */
    private void connectToGoogleApiClient() {
        /** for determining connection to google API */

        if (googleApiClient == null) {

            googleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
            googleApiClient.connect();
            // todo remove toast
//            Toast.makeText(this, "Service Started", Toast.LENGTH_SHORT).show();
        }

        /** for movement detection */
        sensorMan = (SensorManager)getSystemService(SENSOR_SERVICE);
        accelerometer = sensorMan.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mAccel = 0.00f;
        mAccelCurrent = SensorManager.GRAVITY_EARTH;
        mAccelLast = SensorManager.GRAVITY_EARTH;
        // TODO: 15-Mar-16 move sensor registration to appropriate place
//        sensorMan.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);

    }

    /** disconnect from google api client */
    private void disconnectFromGoogleApiClient() {

        /** test if mListener is the same object */
        if ( mListener == null ){
            return;
        }
        onLocateNum = -1;
        LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, mListener);

        googleApiClient.unregisterConnectionCallbacks(this);
        googleApiClient.unregisterConnectionFailedListener(this);
        googleApiClient.disconnect();
        googleApiClient = null;
        mListener=null;
        Toast.makeText(this, "API stopped", Toast.LENGTH_SHORT).show();
    }

    /** Determine Distance */
    public double CalculationByDistance(double lat1, double lng1, double lat2, double lng2) {
        int Radius = 6371;// radius of earth in Km

        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
                * Math.sin(dLon / 2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double valueResult = Radius * c;
//        double km = valueResult / 1;
        DecimalFormat newFormat = new DecimalFormat("####");
//        int kmInDec = Integer.valueOf(newFormat.format(km));
        double meter = valueResult * 1000;
//        int meterInDec = Integer.valueOf(newFormat.format(meter));
//        Log.i("test", "" + valueResult + "   KM  " + kmInDec + " Meter   " + meterInDec);

        // return distance in meters (int)
        return Integer.valueOf(newFormat.format(meter));
    }

    
    /** Build notification based on current reminder data*/
    private void buildNotification(Reminder reminder) {
        //Define sound URI
//        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        // set custom mp3
        Uri soundUri = Uri.parse("android.resource://" + this.getPackageName() + "/" + R.raw.alert);

        notificBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon( getResources().getIdentifier(reminder.getType(), "drawable", getPackageName()))
                .setContentTitle(reminder.getReminderName())
                .setAutoCancel(true)
                .setLights(Color.BLUE, 500, 500)
                .setContentText(reminder.getDescription())
                .setSound(soundUri);
    }

    /**
     * Make notification
     * @param reminder Reminder
     */
    private void makeNotification(Reminder reminder) {
        buildNotification(reminder);

        // set notification id
        notificationId = (int) reminder.getId();

        // TODO: 10-Mar-16 countOfAttemptsIn550
        countOfAttemptsIn550 = countOfAttemptsIn1000 = countOfAttemptsInLargeDistance = 0;

        /** update checkbox status */
        // set to inactive state
        dataSource.updateCheckbox(0, reminder.getId());
        if(MainActivity.isMainActivityActive){
            ReusableFunctions.sendResultToMainActivity(this, "updated");
        }

        int MY_UNIQUE_VALUE = Integer.parseInt(String.valueOf(reminder.getId()));

        Intent resultIntent = new Intent(this, NotificationActivity.class);
        resultIntent.putExtra(REMINDER_ID, reminder.getId());
        resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        // Because clicking the notification opens a new ("special") activity, there's
        // no need to create an artificial back stack.
        PendingIntent pendingIntent =
                PendingIntent.getActivity(this, MY_UNIQUE_VALUE, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        notificBuilder.setContentIntent(pendingIntent);


//        /** create stack for back
//         * The stack builder object will contain an artificial back stack for the
//         * started Activity.
//         * This ensures that navigating backward from the Activity leads out of
//         * your application to the Home screen.
//         * */
//        TaskStackBuilder tStackBuilder = TaskStackBuilder.create(this);
//        // Adds the back stack for the Intent (but not the Intent itself)
//        tStackBuilder.addParentStack(MainActivity.class);
//        // Adds the Intent that starts the Activity to the top of the stack
//        tStackBuilder.addNextIntent(infoIntent);
//        PendingIntent pendingIntent = tStackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
//        notificBuilder.setContentIntent(pendingIntent);

            /* for notification */
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(MY_UNIQUE_VALUE, notificBuilder.build());

        isNotificationActive = true;

        // set minDistance to 10000 m
        minDistance = 10000;

        /** get from database all active reminders
         *  if there are no active reminders stop service
         */
        List<Reminder> reminders = dataSource.findFiltered(
                RemindersDBOpenHelper.COLUMN_ISACTIVE + "==1 ",
                RemindersDBOpenHelper.COLUMN_CREATED + " ASC"
        );
        if(reminders.size() == 0) {
            disconnectFromGoogleApiClient();
            stopSelf();
            // TODO: 15-Mar-16 move sensor unregister
            sensorMan.unregisterListener(this);
        }
    }

    /** If it's correct time and distance set Notification */
    private void setNotification(Reminder reminder, double distanceInMeter ) {
        final Calendar calendar = Calendar.getInstance();

        // set current date
        Long cDate = getCurrentDateTime(calendar, "yyyy, MMM dd");
        // set current time
        Long cTime = getCurrentDateTime(calendar, "H:mm");

        // get schedules date and time from database
        Long fromDate = reminder.getFromDate();
        Long fromTime = reminder.getFromTime();
        Long toDate = reminder.getToDate();
        Long toTime = reminder.getToTime();
        int scheduleOption = reminder.getScheduleOption();

        // check if reminder is out of date set it unchecked
        unsetOutdatedReminderOptionOne(reminder, cDate, cTime, toDate, toTime, scheduleOption);

        // check option 2 if all requirements are right set notification
        unsetOutdatedReminderOptionTwo(reminder, cDate, cTime, toDate, toTime, scheduleOption);

        if(distanceInMeter <= reminder.getDistance()){

            // check option 1 if all requirements are right set notification
            if(scheduleOption == 1){
                // check if reminder is out of date set it unchecked
                unsetOutdatedReminderOptionOne(reminder, cDate, cTime, toDate, toTime, scheduleOption);
                if( (cDate+cTime)>=(fromDate + fromTime)&&
                        (cDate+cTime) <= (toDate + toTime) ){
                    makeNotification(reminder);
                }
            }

            // check option 2 if all requirements are right set notification
            if(scheduleOption == 2){
                // check if reminder is out of date set it unchecked
                unsetOutdatedReminderOptionTwo(reminder, cDate, cTime, toDate, toTime, scheduleOption);
                if( (cDate >= fromDate) && (cDate <= toDate) &&
                        (cTime >= fromTime) && (cTime <= toTime)){
                    makeNotification(reminder);
                }
            }
        }
    }

    /** get current Date/Time in milliseconds */
    private Long getCurrentDateTime(Calendar calendar, String dateFormat) {
        Long cDate = null;
        SimpleDateFormat sdfDate = new SimpleDateFormat( dateFormat );
        String dateString = sdfDate.format(calendar.getTime());
//        Log.i("test", "Current dataString = "+dateString);
        try {
            Date date = sdfDate.parse(dateString);
            cDate = date.getTime();
//            Log.i("test", "Current cDate = "+cDate);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return cDate;
    }

    /** check if option one reminder is not outdated */
    private void unsetOutdatedReminderOptionTwo(Reminder reminder, Long cDate, Long cTime, Long toDate, Long toTime, int scheduleOption) {
        if((scheduleOption == 2) && (cDate > toDate || ( (cDate >= toDate) && cTime>toTime))){
            // set to inactive state
            dataSource.updateCheckbox(0, reminder.getId());
            if(MainActivity.isMainActivityActive){
                ReusableFunctions.sendResultToMainActivity(this, "updated");
            }
        }
    }

    /** check if option two reminder is not outdated */
    private void unsetOutdatedReminderOptionOne(Reminder reminder, Long cDate, Long cTime, Long toDate, Long toTime, int scheduleOption) {
        if( (scheduleOption == 1) && (cDate+cTime > toDate+toTime) ){
            // set to inactive state
            dataSource.updateCheckbox(0, reminder.getId());
            if(MainActivity.isMainActivityActive){
                ReusableFunctions.sendResultToMainActivity(this, "updated");
            }
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float[] mGravity = event.values.clone();
            // Shake detection
            float x = mGravity[0];
            float y = mGravity[1];
            float z = mGravity[2];
            mAccelLast = mAccelCurrent;
            mAccelCurrent = (float) Math.sqrt(x * x + y * y + z * z);
            float delta = mAccelCurrent - mAccelLast;
            mAccel = mAccel * 0.9f + delta;
            // Make this higher or lower according to how much
            // motion you want to detect
            if (mAccel > 1) {
                // do something
                // TODO: 15-Mar-16 remove log
                Log.i("test", "Movement detected");
//                movementDetected = true;
                // TODO: 15-Mar-16 correct movement detection
//                if(movementDetected){
                    countOfAttemptsIn550 = countOfAttemptsIn1000 = countOfAttemptsInLargeDistance = 0;
                    movementDetected = false;
                    /** disconnect and connect for changing REQUEST_TIME */
                    if (googleApiClient != null) {
                        disconnectFromGoogleApiClient();
                        connectToGoogleApiClient();
                    }
//                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    /** Method to save logs */
    public void saveLog(String text) {

        final Calendar calendar = Calendar.getInstance();

        String timeFormat = "dd.MMM.yyyy, H:mm:s";
        SimpleDateFormat sdf = new SimpleDateFormat( timeFormat );
        String dateString = sdf.format(calendar.getTime());

        String FILENAME = "mapReminder_log.txt";
        String data = text + " "+ dateString + "\n";


        // write to file
        FileOutputStream outputStream = null;
        try {
            outputStream = openFileOutput(FILENAME, Context.MODE_APPEND);
            outputStream.write(data.getBytes());
            outputStream.close();
            Toast.makeText(this, "Data was saved", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



} // End of My Service
