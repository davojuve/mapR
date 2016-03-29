package com.example.davit.mapreminder.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.davit.mapreminder.database.ReminderDataSource;
import com.example.davit.mapreminder.database.RemindersDBOpenHelper;

/**
 * Start MapReminder LocateMeService After Boot
 */
public class StartMapReminderLocateMeServiceAfterBoot extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
//        if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
//            Intent pushIntent = new Intent(context, MapReminderLocateService.class);
//            context.startService(pushIntent);
//        }

        ReminderDataSource dataSource = new ReminderDataSource(context);
        dataSource.open();
        if(dataSource.findFiltered(RemindersDBOpenHelper.COLUMN_ISACTIVE + "==1 ",
                RemindersDBOpenHelper.COLUMN_CREATED + " ASC"
        )
                .size()>0){
            dataSource.close();
            Intent pushIntent = new Intent(context, MapReminderLocateService.class);
            context.startService(pushIntent);
        }else{
            dataSource.close();
        }
    }
}
