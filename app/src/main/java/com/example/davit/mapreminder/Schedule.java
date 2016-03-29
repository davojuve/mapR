package com.example.davit.mapreminder;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.davit.mapreminder.data.MapReminderConstants;
import com.example.davit.mapreminder.validation.Validation;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

// TODO  do refactor
/**
 * class Schedule
 */
public class Schedule extends AppCompatActivity {

    private Button editFromDate1;
    private Button editFromTime1;
    private Button editToDate1;
    private Button editToTime1;
    private Button editFromDate2;
    private Button editFromTime2;
    private Button editToDate2;
    private Button editToTime2;
    private EditText distanceEditText;
    final Calendar calendar = Calendar.getInstance();
    private int optionNumber = -1;

    private Long fromDate1;
    private Long toDate1;
    private Long fromTime1;
    private Long toTime1;

    private Long fromDate2;
    private Long toDate2;
    private Long fromTime2;
    private Long toTime2;

    private Double distance = -1.0;

    private boolean isSet = false;
    private boolean scheduleOptionTwo = false;
    private RelativeLayout rl1;
    private RelativeLayout rl2;
    private Button btnSwitchViews;
    private boolean isEdit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        initializeView();

        /** Set values for the width and height of popup window*/
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        /** if we want width to be 80% of phone screen size *0.8*/
        getWindow().setLayout((int) (displayMetrics.widthPixels * .9), (int) (displayMetrics.heightPixels * .95));

        /** Get values from AddReminder activity if edit */
        optionNumber = getIntent().getIntExtra(MapReminderConstants.REMINDER_SCHEDULEOPTION, -1);
        /** determine if it is Create or Edit */
        if ( optionNumber != -1 ) {
            isEdit = true;
            /** values from addReminder*/
            distance = getIntent().getDoubleExtra(MapReminderConstants.REMINDER_DISTANCE, -1);
            fromDate1 = getIntent().getLongExtra(MapReminderConstants.REMINDER_DATEFROM, -1);
            toDate1 = getIntent().getLongExtra(MapReminderConstants.REMINDER_TODATE, -1);
            fromTime1 = getIntent().getLongExtra(MapReminderConstants.REMINDER_TIMEFROM, -1);
            toTime1 = getIntent().getLongExtra(MapReminderConstants.REMINDER_TOTIME, -1);

            /** Set calendar values */
            calendar.setTimeInMillis(fromDate1);
            setCurrentDateOnView(editFromDate1);
            setCurrentDateOnView(editFromDate2);

            calendar.setTimeInMillis(toDate1);
            setCurrentDateOnView(editToDate1);
            setCurrentDateOnView(editToDate2);

            calendar.setTimeInMillis(fromTime1);
            setCurrentTimeOnView(editFromTime1);
            setCurrentTimeOnView(editFromTime2);

            calendar.setTimeInMillis(toTime1);
            setCurrentTimeOnView(editToTime1);
            setCurrentTimeOnView(editToTime2);

            /** Set distance */
            distanceEditText.setText(distance.toString());

            if(optionNumber == 1){
                switchToViewNumberOne();
            }else if(optionNumber == 2){
                switchToViewNumberTwo();
            }
        }else{
            /** Set initial values for section 1 and section 2*/
            setCurrentDateOnView(editFromDate1);
            setCurrentDateOnView(editFromDate2);

            setCurrentTimeOnView(editFromTime1);
            setCurrentTimeOnView(editFromTime2);

            // set Date one day later than set it on To Date, after correct the time
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            setCurrentDateOnView(editToDate1);
            setCurrentDateOnView(editToDate2);
            calendar.add(Calendar.DAY_OF_MONTH, -1);

            calendar.add(Calendar.HOUR_OF_DAY, 1);
            setCurrentTimeOnView(editToTime1);
            setCurrentTimeOnView(editToTime2);
            calendar.add(Calendar.HOUR_OF_DAY, -1);


            // Set distance
            distanceEditText.setText("500");
        }


    } // End of onCreate


    /** For initializing view on create time */
    private void initializeView(){
        // date and time inputs from section 1
        editFromDate1 = (Button) findViewById(R.id.scheduleBtnFromDate1);
        editFromTime1 = (Button) findViewById(R.id.scheduleBtnFromTime1);
        editToDate1 = (Button) findViewById(R.id.scheduleBtnToDate1);
        editToTime1 = (Button) findViewById(R.id.scheduleBtnToTime1);

        // date and time inputs from section 2
        editFromDate2 = (Button) findViewById(R.id.scheduleBtnFromDate2);
        editFromTime2 = (Button) findViewById(R.id.scheduleBtnFromTime2);
        editToDate2 = (Button) findViewById(R.id.scheduleBtnToDate2);
        editToTime2 = (Button) findViewById(R.id.scheduleBtnToTime2);

        // Relative layouts (section 1 and 2)
        rl1 = (RelativeLayout)findViewById(R.id.scheduleSection1);
        rl2 = (RelativeLayout)findViewById(R.id.scheduleSection2);

        // set distance
        distanceEditText = (EditText) findViewById(R.id.scheduleDistance);

        btnSwitchViews = (Button)findViewById(R.id.scheduleBtnSwitchViews);

        // at initialization set 2-nd relative layout not active
        setViewAndChildrenEnabled(rl2, false);

    }


    /**
     * (next 4 methods) Create date/time on Buttons (on scheduleOptionTwo view ) by using
     *  data/time from PeckerDialog
     */

    /** set new date on From date */
    DatePickerDialog.OnDateSetListener dateOnFrom = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, monthOfYear);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            if(scheduleOptionTwo){
                setCurrentDateOnView(editFromDate2);
            }else{
                setCurrentDateOnView(editFromDate1);
            }

        }
    };

    /** set new Time on From time */
    TimePickerDialog.OnTimeSetListener timeOnFrom = new TimePickerDialog.OnTimeSetListener() {

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            calendar.set(Calendar.MINUTE, minute);
            if(scheduleOptionTwo){
                setCurrentTimeOnView(editFromTime2);
            }else{
                setCurrentTimeOnView(editFromTime1);
            }
        }
    };

    /** set new date on To date */
    DatePickerDialog.OnDateSetListener dateOnTo = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {


            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, monthOfYear);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            if(scheduleOptionTwo){
                setCurrentDateOnView(editToDate2);
            }else{
                setCurrentDateOnView(editToDate1);
            }

        }
    };

    /** set new Time on ToTime */
    TimePickerDialog.OnTimeSetListener timeOnTo = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {


            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            calendar.set(Calendar.MINUTE, minute);
            if(scheduleOptionTwo){
                setCurrentTimeOnView(editToTime2);
            }else{
                setCurrentTimeOnView(editToTime1);
            }
        }
    };



    /** (next 4 methods )Click listeners for buttons*/

    /**
     * click listener on schedule Btn From Date
     * @param view View
     */
    public void scheduleBtnFromDateClickListener(View view) {
        // check if it is edit
        if(isEdit){
            calendar.setTimeInMillis(fromDate1);
        }

        new DatePickerDialog(Schedule.this, dateOnFrom, calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    /**
     * click listener on schedule Btn From Time
     * @param view View
     */
    public void scheduleBtnFromTimeClickListener(View view) {
        // check if it is edit
        if(isEdit){
            calendar.setTimeInMillis(fromTime1);
        }
        new TimePickerDialog(Schedule.this, timeOnFrom, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();
    }

    /**
     * click listener on schedule Btn To Date
     * @param view View
     */
    public void scheduleBtnToDateClickListener(View view) {
        // check if it is edit
        if(isEdit){
            calendar.setTimeInMillis(toDate1);
        }
        new DatePickerDialog(Schedule.this, dateOnTo, calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    /**
     * click listener on schedule Btn To Time
     * @param view View
     */
    public void scheduleBtnToTimeClickListener(View view) {
        // check if it is edit
        if(isEdit){
            calendar.setTimeInMillis(toTime1);
        }
        new TimePickerDialog(Schedule.this, timeOnTo, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();
    }


    /** set Current Date On From View and set parameters to send to the database */
    private void setCurrentDateOnView( Button btnDateName){

        Long cDate=null;

        // set date on view
        String dateFormat = "yyyy, MMM dd";
        SimpleDateFormat sdf = new SimpleDateFormat( dateFormat );
        String dateString = sdf.format(calendar.getTime());
        // todo remove log
//        Log.i("test", "database dateString = "+dateString);
        btnDateName.setText(dateString);

        // parse date to Long value
        try {
            Date date = sdf.parse(dateString);
            cDate = date.getTime();
//             Log.i("date", "cDate = "+String.valueOf(cDate));

        } catch (ParseException e) {
            e.printStackTrace();
        }

        // set fromDate1 and toDate1
        if ( cDate != null ) {
            switch ( btnDateName.getId() ) {
                case R.id.scheduleBtnFromDate1:
                     fromDate1 = cDate;
                    break;
                case R.id.scheduleBtnToDate1:
                     toDate1 = cDate;
                    break;
                case R.id.scheduleBtnFromDate2:
                    fromDate2 = cDate;
                    break;
                case R.id.scheduleBtnToDate2:
                    toDate2 = cDate;
                    break;
            }
        }
//        Log.i("test", "fromDate1 = " + fromDate1);
//        Log.i("test", "toDate1 = " + toDate1);
    }

    /** Set time on view  and set parameters to send to the database */
    private void setCurrentTimeOnView(Button btnTimeName) {
        Long cTime = null;

        // set time on From time
        String timeFormat = "H:mm";
        SimpleDateFormat sdf = new SimpleDateFormat( timeFormat );
        String timeString = sdf.format(calendar.getTime());
        // todo remove log   find out minus value (1:16) current 18:30
//        Log.i("test", "database timestring = "+timeString);
        btnTimeName.setText( sdf.format(calendar.getTime()) );

        // parse date to Long value
        try {
            Date date = sdf.parse(timeString);
            cTime = date.getTime();
//            Log.i("date", "cTime = "+String.valueOf(cTime));

        } catch (ParseException e) {
            e.printStackTrace();
        }

        // set fromTime and toTime
        if ( cTime != null ) {
            switch ( btnTimeName.getId() ) {
                case R.id.scheduleBtnFromTime1:
                    fromTime1 = cTime;
                    break;
                case R.id.scheduleBtnToTime1:
                    toTime1 = cTime;
                    break;
                case R.id.scheduleBtnFromTime2:
                    fromTime2 = cTime;
                    break;
                case R.id.scheduleBtnToTime2:
                    toTime2 = cTime;
                    break;
            }
        }
    }


    /** Disable/enable all child in views*/
    private void setViewAndChildrenEnabled(View view, boolean enabled) {
        view.setEnabled(enabled);
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                View child = viewGroup.getChildAt(i);
                setViewAndChildrenEnabled(child, enabled);
            }
        }
    }

    /** toggle views */
    public void toggleViews(View view){
        if(scheduleOptionTwo){
            switchToViewNumberOne();
        }else{
            switchToViewNumberTwo();
        }
    }

    /** set active option two */
    private void switchToViewNumberTwo() {
        setViewAndChildrenEnabled(rl1, false);
        setViewAndChildrenEnabled(rl2, true);
        scheduleOptionTwo = true;
        btnSwitchViews.setText("Switch To Strategy One");
    }

    /** set active option one */
    private void switchToViewNumberOne() {
        setViewAndChildrenEnabled(rl1, true);
        setViewAndChildrenEnabled(rl2, false);
        scheduleOptionTwo = false;
        btnSwitchViews.setText("Switch To Strategy Two");
    }

    // TODO: refactor make usable function
    /**  schedule Btn Cancel Click Listener */
    public void scheduleBtnCancelClickListener(View view) {
        /** date/time is not set*/
        isSet = false;
        /** display alert are you sure? */
        new AlertDialog.Builder(this)
                .setMessage("Are you sure you want to exit without saving?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Schedule.this.finish();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    /**  schedule Btn Save Click Listener */
    public void scheduleBtnSaveClickListener(View view) {

        String text = distanceEditText.getText().toString();

        if(toDate1 < fromDate1 || toDate2 < fromDate2 ){
            showAlertDialog("Please set correct Date for \"To\" date (it must be greater than \"From\" date!)");
        }else if(!Validation.isNumeric(text) ||  text.isEmpty() ){
            showAlertDialog("Please set the distance(numeric value)");
        }else{
            distance = Double.parseDouble(distanceEditText.getText().toString());
            isSet = true;
            getIntent().putExtra("isOptionTwo", scheduleOptionTwo);
            getIntent().putExtra("isSetSchedule", isSet);
            getIntent().putExtra("distance", distance);

            // todo remove log
//            Log.i("test", "fromDate1 = " + fromDate1);
//            Log.i("test", "fromTime1 = " + fromTime1);
//            Log.i("test", "toDate1 = " + toDate1);
//            Log.i("test", "toTime1 = " + toTime1);

            if(scheduleOptionTwo){
                // TODO: 04-Mar-16 remove toast
//                Toast.makeText(this,"fromTime: "+fromTime2,Toast.LENGTH_SHORT).show();
//                Log.i("test", "fromTime: " + fromTime2 + " toTime: " + toTime2);
                if(toTime2 <= fromTime2 ){
                    showAlertDialog("Please set correct time for \"To\" time (it must be greater than \"From\" time!)");
                }else{
                    // send from/to option 2
                    getIntent().putExtra("fromDate", fromDate2);
                    getIntent().putExtra("fromTime", fromTime2);
                    getIntent().putExtra("toDate", toDate2);
                    getIntent().putExtra("toTime", toTime2);
                    setResult(RESULT_OK, getIntent());
                    finish();
                }
            }else{
                // send from/to option 1
                getIntent().putExtra("fromDate", fromDate1);
                getIntent().putExtra("fromTime", fromTime1);
                getIntent().putExtra("toDate", toDate1);
                getIntent().putExtra("toTime", toTime1);
                setResult(RESULT_OK, getIntent());
                finish();
            }
        }

    }

    private void showAlertDialog(String text) {
        new AlertDialog.Builder(this)
                .setMessage(text)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                })
                .show();
    }

}
