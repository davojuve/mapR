package com.example.davit.mapreminder;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.davit.mapreminder.data.PopupSelectImageArrayAdapter;

public class PopupSelectImage extends AppCompatActivity {

    String selectedImageName;
   String[] imageList = {
           "bank",
           "cinema",
           "clothes",
           "drink",
           "food",
           "gas",
           "girl",
           "meal",
           "petrol",
           "police",
           "post",
           "pharmacy",
           "alert"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popup_select_image);

        /** Set values for the width and height of popup window*/
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        /** For darkening the background */
//        WindowManager.LayoutParams windowManager = getWindow().getAttributes();
//        windowManager.dimAmount = 0.6f;
//        getWindow().setAttributes(windowManager);

        getWindow().getAttributes().dimAmount = 0.7f;
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

        /** if we want width to be 80% of phone screen size *0.8 */
        getWindow().setLayout((int) (displayMetrics.widthPixels * .8), (int) (displayMetrics.heightPixels * .6));


        /**
         * create new adapter for preview as ListView
         * get data from DataProvider class
         * */

        ListView listView = (ListView) findViewById(R.id.popupImageList);
        listView.setAdapter(new PopupSelectImageArrayAdapter(this, 0, imageList));

        /** add on click listener on ListView */
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                view.findViewById(R.id.popupImageListImageView).setBackgroundColor(0x00FF00);
//                selectedImageName = imageList[position];

                getIntent().putExtra("imageName", imageList[position]);
                setResult(RESULT_OK, getIntent());
                finish();
//                Toast.makeText(PopupSelectImage.this, selectedImageName, Toast.LENGTH_SHORT).show();
            }
        });

    }

}
