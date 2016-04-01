package com.example.davit.mapreminder;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ScrollView;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class ReadLogs extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_logs);

        File f = getFilesDir();
        String path = f.getAbsolutePath();

        TextView textView = (TextView) this.findViewById(R.id.logOutputTextView);


        // read from file
        try {
            FileInputStream fis = openFileInput("mapReminder_log.txt");
            BufferedInputStream bis = new BufferedInputStream(fis);
            StringBuffer stringBuffer = new StringBuffer();
            while ( bis.available() != 0 ){
                char c = (char) bis.read();
                stringBuffer.append(c);
            }
            textView.setText(stringBuffer);
            bis.close();
            fis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
