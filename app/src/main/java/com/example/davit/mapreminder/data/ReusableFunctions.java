package com.example.davit.mapreminder.data;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

/**
 * Reusable Functions
 */
public class ReusableFunctions {

    /**
     * for refreshing MainActivity if checkbox status has changed
     * used in service and ReminderArrayAdapter
     * @param context Context
     * @param message String
     */
    public static void sendResultToMainActivity(Context context, String message) {
        LocalBroadcastManager broadcaster = LocalBroadcastManager.getInstance(context);
        Intent intent = new Intent(MapReminderConstants.CHECKBOX_UPDATE);
        if(message != null)
            intent.putExtra(MapReminderConstants.SERVICE_MESSAGE, message);
        broadcaster.sendBroadcast(intent);
    }
}
