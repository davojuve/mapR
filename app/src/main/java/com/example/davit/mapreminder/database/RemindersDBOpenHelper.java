package com.example.davit.mapreminder.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * RemindersDBOpenHelper class
 */
public class RemindersDBOpenHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "reminders.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_REMINDER   = "reminders";
    public static final String COLUMN_ID    = "reminderId";
    public static final String COLUMN_NAME = "reminderName";
    public static final String COLUMN_DESC  = "description";
    public static final String COLUMN_TYPE = "type";
    public static final String COLUMN_IMAGE = "image";
    public static final String COLUMN_TARGETLAT = "latitude";
    public static final String COLUMN_TARGETLNG = "longitude";
    public static final String COLUMN_DISTANCE = "distance";
    public static final String COLUMN_ISACTIVE = "isActive";
    public static final String COLUMN_CREATED = "reminderCreated";
    public static final String COLUMN_FROMDATE = "fromDate";
    public static final String COLUMN_FROMTIME = "fromTime";
    public static final String COLUMN_TODATE = "toDate";
    public static final String COLUMN_TOTIME = "toTime";
    public static final String COLUMN_DISTANCE_TO_REMINDER_RADIUS = "distanceToReminderRadius";
    public static final String COLUMN_SCHEDULEOPTION = "scheduleOption";

    private static final String TABLE_CREATE =
            "CREATE TABLE " + TABLE_REMINDER + "( " +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    COLUMN_NAME + " TEXT, " +
                    COLUMN_DESC  + " TEXT, " +
                    COLUMN_TYPE  + " TEXT, " +
                    COLUMN_IMAGE  + " TEXT, " +
                    COLUMN_TARGETLAT  + " NUMERIC, " +
                    COLUMN_TARGETLNG + " NUMERIC, " +
                    COLUMN_DISTANCE + " NUMERIC, " +
                    COLUMN_ISACTIVE + " INTEGER, " +
                    COLUMN_FROMDATE + " INTEGER, " +
                    COLUMN_FROMTIME + " INTEGER, " +
                    COLUMN_TODATE + " INTEGER, " +
                    COLUMN_TOTIME + " INTEGER, " +
                    COLUMN_SCHEDULEOPTION + " INTEGER, " +
                    COLUMN_DISTANCE_TO_REMINDER_RADIUS + " NUMERIC DEFAULT -1, " +
                    COLUMN_CREATED + " INTEGER " +
                    ")";

    /**
     * CONSTRUCTOR
     * @param context Context
     */
    public RemindersDBOpenHelper( Context context ) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
        Log.i("test", "Table has been created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_REMINDER);
        onCreate(db);
    }

}
