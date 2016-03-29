package com.example.davit.mapreminder.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.davit.mapreminder.data.Reminder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Class ReminderDataSource
 */
public class ReminderDataSource {
    SQLiteOpenHelper dbHelper;
    SQLiteDatabase database;
    private Calendar calendar;
    private String dateFormat = "yyyy MMM dd H mm s";
    private SimpleDateFormat sdf = new SimpleDateFormat( dateFormat );

    private static final String[] allColumns = {
            RemindersDBOpenHelper.COLUMN_ID,
            RemindersDBOpenHelper.COLUMN_NAME,
            RemindersDBOpenHelper.COLUMN_DESC,
            RemindersDBOpenHelper.COLUMN_TYPE,
            RemindersDBOpenHelper.COLUMN_IMAGE,
            RemindersDBOpenHelper.COLUMN_TARGETLAT,
            RemindersDBOpenHelper.COLUMN_TARGETLNG,
            RemindersDBOpenHelper.COLUMN_DISTANCE,
            RemindersDBOpenHelper.COLUMN_ISACTIVE,
            RemindersDBOpenHelper.COLUMN_FROMDATE,
            RemindersDBOpenHelper.COLUMN_FROMTIME,
            RemindersDBOpenHelper.COLUMN_TODATE,
            RemindersDBOpenHelper.COLUMN_TOTIME,
            RemindersDBOpenHelper.COLUMN_SCHEDULEOPTION,
            RemindersDBOpenHelper.COLUMN_DISTANCE_TO_REMINDER_RADIUS,
            RemindersDBOpenHelper.COLUMN_CREATED
    };


    /**
     * constructor
     * @param context Context
     */
    public ReminderDataSource(Context context) {
        dbHelper = new RemindersDBOpenHelper( context );
    }

    /**
     * Open database
     */
    public void open(){
        database = dbHelper.getWritableDatabase();
//        Log.i("test", "Database opened");
    }

    /**
     * Close database
     */
    public void close(){
        dbHelper.close();
//        Log.i("test", "Database closed");
    }

    /**
     * create table row
     */
    public Reminder create(Reminder reminder){
        calendar = Calendar.getInstance();
        String dateString = sdf.format(calendar.getTime());
        Date date = null;
        Long cDate = null;
        try {
            date = sdf.parse(dateString);
            cDate = date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }



        ContentValues values = new ContentValues();
        values.put(RemindersDBOpenHelper.COLUMN_NAME, reminder.getReminderName());
        values.put(RemindersDBOpenHelper.COLUMN_DESC, reminder.getDescription());
        values.put(RemindersDBOpenHelper.COLUMN_TYPE, reminder.getType());
        values.put(RemindersDBOpenHelper.COLUMN_TARGETLAT, reminder.getTargetCordLat());
        values.put(RemindersDBOpenHelper.COLUMN_TARGETLNG, reminder.getTargetCordLng());
        values.put(RemindersDBOpenHelper.COLUMN_DISTANCE, reminder.getDistance());
        values.put(RemindersDBOpenHelper.COLUMN_ISACTIVE, reminder.getIsActive());
        values.put(RemindersDBOpenHelper.COLUMN_FROMDATE, reminder.getFromDate());
        values.put(RemindersDBOpenHelper.COLUMN_FROMTIME, reminder.getFromTime());
        values.put(RemindersDBOpenHelper.COLUMN_TODATE, reminder.getToDate());
        values.put(RemindersDBOpenHelper.COLUMN_TOTIME, reminder.getToTime());
        values.put(RemindersDBOpenHelper.COLUMN_SCHEDULEOPTION, reminder.getScheduleOption());
        values.put(RemindersDBOpenHelper.COLUMN_CREATED, cDate );
        // TODO: 14-Mar-16 remove log
        Log.i("test", "Date in create: " + String.valueOf(cDate));
        long insertId = database.insert(RemindersDBOpenHelper.TABLE_REMINDER, null, values);
        reminder.setId(insertId);

        return reminder;
    }

    /**
     * return all rows as ArrayList which contains Reminder objects
     */
    public List<Reminder> findAll(String orderBy){

        Cursor cursor = database.query(RemindersDBOpenHelper.TABLE_REMINDER, allColumns, null, null, null, null, orderBy);
//        Log.i("text", "Returned " + cursor.getCount() + " rows");

        return cursorToList(cursor);
    }


    /**
     * return filtered and ordered rows
     * replace for example selection : "name = 'test'"
     */
    public List<Reminder> findFiltered(String selection, String orderBy){

        Cursor cursor = database.query(RemindersDBOpenHelper.TABLE_REMINDER, allColumns, selection, null, null, null, orderBy);
//        Log.i("text", "Returned " + cursor.getCount() + " rows");

        return cursorToList(cursor);
    }

    /**
     * return Reminder by id
     */
    public Reminder findById(Long id){
        Cursor cursor = database.query(RemindersDBOpenHelper.TABLE_REMINDER, allColumns, RemindersDBOpenHelper.COLUMN_ID+"="+id, null, null, null, null);

        return cursorToList(cursor).get(0);
    }

    @NonNull
    /**
     * make cursor ArrayList
     */
    private List<Reminder> cursorToList(Cursor cursor) {
        List<Reminder> remindersArray = new ArrayList<>();
        if( cursor.getCount()>0 ){
            while (cursor.moveToNext()){
                Reminder reminder = new Reminder();

                reminder.setId(cursor.getLong(cursor.getColumnIndex(RemindersDBOpenHelper.COLUMN_ID)));
                reminder.setReminderName(cursor.getString(cursor.getColumnIndex(RemindersDBOpenHelper.COLUMN_NAME)));
                reminder.setDescription(cursor.getString(cursor.getColumnIndex(RemindersDBOpenHelper.COLUMN_DESC)));
                reminder.setType(cursor.getString(cursor.getColumnIndex(RemindersDBOpenHelper.COLUMN_TYPE)));
                reminder.setTargetCordLat(cursor.getDouble(cursor.getColumnIndex(RemindersDBOpenHelper.COLUMN_TARGETLAT)));
                reminder.setTargetCordLng(cursor.getDouble(cursor.getColumnIndex(RemindersDBOpenHelper.COLUMN_TARGETLNG)));
                reminder.setDistance(cursor.getDouble(cursor.getColumnIndex(RemindersDBOpenHelper.COLUMN_DISTANCE)));
                reminder.setDistanceToReminderRadius(cursor.getDouble(cursor.getColumnIndex(RemindersDBOpenHelper.COLUMN_DISTANCE_TO_REMINDER_RADIUS)));
                reminder.setIsActive(cursor.getInt(cursor.getColumnIndex(RemindersDBOpenHelper.COLUMN_ISACTIVE)));
                reminder.setCreated(cursor.getString(cursor.getColumnIndex(RemindersDBOpenHelper.COLUMN_CREATED)));
                reminder.setFromDate(cursor.getLong(cursor.getColumnIndex(RemindersDBOpenHelper.COLUMN_FROMDATE)));
                reminder.setFromTime(cursor.getLong(cursor.getColumnIndex(RemindersDBOpenHelper.COLUMN_FROMTIME)));
                reminder.setToDate(cursor.getLong(cursor.getColumnIndex(RemindersDBOpenHelper.COLUMN_TODATE)));
                reminder.setToTime(cursor.getLong(cursor.getColumnIndex(RemindersDBOpenHelper.COLUMN_TOTIME)));

                reminder.setScheduleOption(cursor.getInt(cursor.getColumnIndex(RemindersDBOpenHelper.COLUMN_SCHEDULEOPTION)));

                remindersArray.add(reminder);
            }
        }
        return remindersArray;
    }


    /**
     * Update set active or inactive finding reminder  by id
     */
    public boolean updateCheckbox(int newValue, Long rowId){
        ContentValues value = new ContentValues();
        value.put(RemindersDBOpenHelper.COLUMN_ISACTIVE, newValue);
        database.update(RemindersDBOpenHelper.TABLE_REMINDER, value, RemindersDBOpenHelper.COLUMN_ID + " = " + rowId, null);
        return true;
    }

    /**
     * Update reminders distanceToReminderRadius
     */
    public boolean updateDistanceToReminderRadius(double newValue, Long rowId){
        ContentValues value = new ContentValues();
        value.put(RemindersDBOpenHelper.COLUMN_DISTANCE_TO_REMINDER_RADIUS, newValue);
        database.update(RemindersDBOpenHelper.TABLE_REMINDER, value, RemindersDBOpenHelper.COLUMN_ID + " = " + rowId, null);
        return true;
    }


    /**
     * Update reminder by id ( for edit )
     */
    public void updateReminder(Reminder reminder, Long id){
        calendar = Calendar.getInstance();
        String dateString = sdf.format(calendar.getTime());
        Date date = null;
        Long cDate = null;
        try {
            date = sdf.parse(dateString);
            cDate = date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }


        ContentValues values = new ContentValues();
        values.put(RemindersDBOpenHelper.COLUMN_NAME, reminder.getReminderName());
        values.put(RemindersDBOpenHelper.COLUMN_DESC, reminder.getDescription());
        values.put(RemindersDBOpenHelper.COLUMN_TYPE, reminder.getType());
        values.put(RemindersDBOpenHelper.COLUMN_TARGETLAT, reminder.getTargetCordLat());
        values.put(RemindersDBOpenHelper.COLUMN_TARGETLNG, reminder.getTargetCordLng());
        values.put(RemindersDBOpenHelper.COLUMN_DISTANCE, reminder.getDistance());
        values.put(RemindersDBOpenHelper.COLUMN_FROMDATE, reminder.getFromDate());
        values.put(RemindersDBOpenHelper.COLUMN_FROMTIME, reminder.getFromTime());
        values.put(RemindersDBOpenHelper.COLUMN_TODATE, reminder.getToDate());
        values.put(RemindersDBOpenHelper.COLUMN_TOTIME, reminder.getToTime());
        values.put(RemindersDBOpenHelper.COLUMN_SCHEDULEOPTION, reminder.getScheduleOption());
        values.put(RemindersDBOpenHelper.COLUMN_CREATED, cDate);
        // TODO: 14-Mar-16 remove log
//        Log.i("test", "Date in update: " + String.valueOf(cDate));

        database.update(RemindersDBOpenHelper.TABLE_REMINDER, values, RemindersDBOpenHelper.COLUMN_ID + " = " + id, null);

    }

    /**
     * Delete reminder by id
     */

    public void deleteById( Long rowId ){
        database.delete(RemindersDBOpenHelper.TABLE_REMINDER, RemindersDBOpenHelper.COLUMN_ID + " = " + rowId, null);
    }

}
