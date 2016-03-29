package com.example.davit.mapreminder.data;

/**
 * class for MapReminderConstants
 */
public class MapReminderConstants {

    public static final long DELAY = 2000; // in ms

    /** for transferring data from addReminder to edit */
    public static final String REMINDER_ID = "reminderId";
    public static final String REMINDER_LATITUDE = "reminderLat";
    public static final String REMINDER_LONGITUDE = "reminderLNG";
    public static final String REMINDER_DATEFROM = "reminderFROMDATE";
    public static final String REMINDER_TIMEFROM = "reminderFROMTIME";
    public static final String REMINDER_TODATE = "reminderTODATE";
    public static final String REMINDER_TOTIME = "reminderTOTIME";
    public static final String REMINDER_DISTANCE = "reminderDistance";
    public static final String REMINDER_SCHEDULEOPTION = "reminderScheduleOption";

    /** for resultIntents on addReminder */
    public static final int POPUP_REQUEST_CODE = 1001;
    public static final int SETMARKER_REQUEST_CODE = 1002;
    public static final int SETSCHEDULE_REQUEST_CODE = 1003;

    /** for updating checkboxes on MainActivity */
    public static final String CHECKBOX_UPDATE = "com.example.davit.mapreminder.service.MAPREMINDERLOCATESERVICE_REQUEST_PROCESSED";
    public static String SERVICE_MESSAGE = "com.example.davit.mapreminder.service.MAPREMINDERLOCATESERVICE_MSG";



}
