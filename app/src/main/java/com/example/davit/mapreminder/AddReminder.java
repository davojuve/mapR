package com.example.davit.mapreminder;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import com.example.davit.mapreminder.data.MapReminderConstants;
import com.example.davit.mapreminder.data.Reminder;
import com.example.davit.mapreminder.data.ReusableFunctions;
import com.example.davit.mapreminder.database.ReminderDataSource;
import com.example.davit.mapreminder.validation.Validation;


/**
 * class AddReminder
 */
// TODO add to the database
public class AddReminder extends AppCompatActivity {

//    public static final int POPUP_REQUEST_CODE = 1001;
//    public static final int SETMARKER_REQUEST_CODE = 1002;
//    public static final int SETSCHEDULE_REQUEST_CODE = 1003;

    private ReminderDataSource dataSource;

    /** For saving reminders rows */
    private String reminderName;
    private String reminderDescription;
    private String imageName;
    private double targetLatitude = -1;
    private double targetLongitude = -1;
    /**  FOR SCHEDULE */
    private boolean isSetSchedule;
    private int scheduleOption = 1;
    private Long fromDate;
    private Long toDate;
    private Long fromTime;
    private Long toTime;
    private boolean isOptionTwo = false;
    private double reminderDistance = -1;

    /** for edit*/
    private Long reminderId;
    private Reminder currentReminder;

    private boolean editReminder = false;

    /** For views*/
    private EditText editTextReminderName;
    private EditText editTextReminderDescription;
    private ImageView imageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_reminder);

        /** Find views by id*/
        editTextReminderName = (EditText) findViewById(R.id.addReminderName);
        editTextReminderDescription = (EditText) findViewById(R.id.addReminderDescription);
        imageView = (ImageView) findViewById(R.id.addReminderImage);

        /** IF EDIT get id from detail activity */
        reminderId = getIntent().getLongExtra(MapReminderConstants.REMINDER_ID, -1);
//        Log.i("test", "reminderId = " + reminderId.toString());

        /** if id from detail activity is set than update values with current values */
        if( reminderId != -1 ){
            // set editReminder to true for update
            editReminder = true;

            // Open connection to the database
            dataSource = new ReminderDataSource(this);
            dataSource.open();

            // get current reminder by id
            currentReminder = dataSource.findById(reminderId);

            // set current name and description
            editTextReminderName.setText( currentReminder.getReminderName() );
            editTextReminderDescription.setText(currentReminder.getDescription());

            // set image
            imageName = currentReminder.getType();
            int resID = getResources().getIdentifier(currentReminder.getType(), "drawable", getPackageName());
            imageView.setImageResource(resID);

            // set map
            targetLatitude = currentReminder.getTargetCordLat();
            targetLongitude = currentReminder.getTargetCordLng();

            // set schedule
            reminderDistance = currentReminder.getDistance();
            fromDate = currentReminder.getFromDate();
            fromTime = currentReminder.getFromTime();
            toDate = currentReminder.getToDate();
            toTime = currentReminder.getToTime();
            scheduleOption = currentReminder.getScheduleOption();
            isSetSchedule = true;
        }

    }

    /** called when 'Select Image' Btn Clicked on addReminder  */
    public void addReminderSelectImageBtnClickListener(View view) {
        // start activity and request a result
        // when we come back to this activity it will trigger a method onActivityResult
        Intent popupIntent = new Intent(AddReminder.this, PopupSelectImage.class);
        startActivityForResult(popupIntent, MapReminderConstants.POPUP_REQUEST_CODE);
    }

    /** called when 'Add Marker on Map' Btn Clicked on addReminder  */
    public void addReminderAddMarkerOnClickListener(View view) {
        Intent intent = new Intent(AddReminder.this, SetMarkerOnMap.class);
        if ( reminderId != -1 ) {
            intent.putExtra(MapReminderConstants.REMINDER_LATITUDE, currentReminder.getTargetCordLat());
            intent.putExtra(MapReminderConstants.REMINDER_LONGITUDE, currentReminder.getTargetCordLng());
        }
        startActivityForResult(intent, MapReminderConstants.SETMARKER_REQUEST_CODE);
    }

    /** call when clicked Schedule on addReminder activity */
    public void addReminderScheduleClickListener(View view) {
        Intent intent = new Intent(AddReminder.this, Schedule.class);
        if ( reminderId != -1 ) {
//            intent.putExtra(REMINDER_ID, currentReminder.getId());
            intent.putExtra(MapReminderConstants.REMINDER_DATEFROM, currentReminder.getFromDate());
            intent.putExtra(MapReminderConstants.REMINDER_TIMEFROM, currentReminder.getFromTime());
            intent.putExtra(MapReminderConstants.REMINDER_TODATE, currentReminder.getToDate());
            intent.putExtra(MapReminderConstants.REMINDER_TOTIME, currentReminder.getToTime());
            intent.putExtra(MapReminderConstants.REMINDER_DISTANCE, currentReminder.getDistance());
            intent.putExtra(MapReminderConstants.REMINDER_SCHEDULEOPTION, currentReminder.getScheduleOption());
        }
        startActivityForResult(intent, MapReminderConstants.SETSCHEDULE_REQUEST_CODE);
    }

    /** we requested result and when we come back to this activity this method was triggered */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        /** Check result from PopupSelectImage activity */
        if(requestCode == MapReminderConstants.POPUP_REQUEST_CODE && resultCode == RESULT_OK){
            imageName = data.getStringExtra("imageName");
            // set image on add reminder activity
            ImageView iv = (ImageView) findViewById(R.id.addReminderImage);
            int resID = getResources().getIdentifier(imageName, "drawable", getPackageName());
            iv.setImageResource(resID);
        }

        /** Check result from AddMarkerOnMap activity */
        if(requestCode == MapReminderConstants.SETMARKER_REQUEST_CODE && resultCode == RESULT_OK){
            // set target coordinates
            targetLatitude = data.getDoubleExtra("targetLatitude", 0);
            targetLongitude = data.getDoubleExtra("targetLongitude", 0);
        }

        /** Check result from Schedule activity */
        if(requestCode == MapReminderConstants.SETSCHEDULE_REQUEST_CODE && resultCode == RESULT_OK){
            // is Set?
            isSetSchedule = data.getBooleanExtra("isSetSchedule", false);
            // set date/time
            isOptionTwo = data.getBooleanExtra("isOptionTwo", false);
            // TODO INSTEAD OF -1 SET CURRENT DATE
            reminderDistance = data.getDoubleExtra("distance", -1);
            fromDate = data.getLongExtra("fromDate", -1);
            fromTime = data.getLongExtra("fromTime", -1);
            toDate = data.getLongExtra("toDate", -1);
            toTime = data.getLongExtra("toTime", -1);
        }
    }

    /**  Save Btn Click Listener */
    public void addReminderBtnSaveClickListener(View view) {
        /* for final check */
        boolean saveToDatabase = true;

        // Get reminder name
        reminderName = editTextReminderName.getText().toString().trim().toLowerCase();

        // Get reminder description
        reminderDescription = editTextReminderDescription.getText().toString().trim().toLowerCase();

        /** check if image is set, if no: set standard alert */
        if(null == imageView.getDrawable()){
            int resID = getResources().getIdentifier("alert", "drawable", getPackageName());
            imageView.setImageResource(resID);
            imageName = "alert";
        }

        /** use Validation class to validate fields */
        if (Validation.isValid(editTextReminderName, "Reminder name is blank", true)) {
            saveToDatabase = true;
        }
//        if (Validation.isValid(editTextReminderDescription, "Reminder description is required", true )) {
//            saveToDatabase = true;
//        }

        /** check lat and lng */
        if( targetLatitude == -1 || targetLongitude == -1 ){
            saveToDatabase = false;
            alertDialog("Please set marker on map");
        }

        /** check if set schedule params */
        // schedule option
        if(isOptionTwo){
            scheduleOption = 2;
        }else{
            scheduleOption = 1;
        }
        if( !isSetSchedule){
            saveToDatabase = false;
            alertDialog("Please set Schedule for this reminder");
        }


        /** Check for errors, if not found save to database*/
        if(saveToDatabase){
            /** Open connection to the database */
            dataSource = new ReminderDataSource(this);
            dataSource.open();

            /** Check if it is create or update*/
            // save to database
            if(editReminder){
                updateData();
//                /** restart service after creation of new reminder */
//                ReusableFunctions.sendResultToMainActivity(this, "checkbox selected");
            }else{
                createData();
//                /** restart service after creation of new reminder */
//                ReusableFunctions.sendResultToMainActivity(this, "checkbox selected");
            }

            AddReminder.this.finish();
        }

    }

    /** pop up alert dialog box with message */
    private void alertDialog(String msg) {
        new AlertDialog.Builder(this)
                .setMessage(msg)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {}
                })
                .show();
    }


    // TODO: refactor make usable function
    /**  Back Btn Click Listener */
    public void addReminderBtnBackClickListener(View view) {
        /** display alert are you sure? */
//        alertDialog("Are you sure you want to exit without saving?");
        // todo make reusable method for all back buttons

            new AlertDialog.Builder(this)
                    .setMessage("Are you sure you want to exit without saving?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            AddReminder.this.finish();
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();
    }


    /** create reminder */
    private void createData(){
        Reminder reminder = new Reminder();

        reminder.setReminderName(reminderName);
        reminder.setDistance(reminderDistance);
        reminder.setIsActive(1);
        reminder.setType(imageName);
        reminder.setTargetCordLat(targetLatitude);
        reminder.setTargetCordLng(targetLongitude);
        reminder.setDescription(reminderDescription);
        reminder.setFromDate(fromDate);
        reminder.setFromTime(fromTime);
        reminder.setToDate(toDate);
        reminder.setToTime(toTime);
        reminder.setScheduleOption(scheduleOption);

        // create row
        dataSource.create(reminder);
    }


    /** update reminder */
    private void updateData(){
        Reminder reminder = new Reminder();

        reminder.setReminderName(reminderName);
        reminder.setDistance(reminderDistance);
        reminder.setType(imageName);
        reminder.setTargetCordLat(targetLatitude);
        reminder.setTargetCordLng(targetLongitude);
        reminder.setDescription(reminderDescription);
        reminder.setFromDate(fromDate);
        reminder.setFromTime(fromTime);
        reminder.setToDate(toDate);
        reminder.setToTime(toTime);
        reminder.setScheduleOption(scheduleOption);

        // update row
        dataSource.updateReminder(reminder, reminderId);
    }


} //End of class AddReminder
