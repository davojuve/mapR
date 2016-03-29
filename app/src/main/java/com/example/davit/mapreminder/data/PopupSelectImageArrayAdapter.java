package com.example.davit.mapreminder.data;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.davit.mapreminder.R;


/**
 * class PopupSelectImageArrayAdapter
 * for crating select list on select image popup window
 */
public class PopupSelectImageArrayAdapter extends ArrayAdapter {
    Context context;
    String[] objects;

    public PopupSelectImageArrayAdapter(Context context, int resource, String[] objects) {
        super(context, resource, objects);
        this.context = context;
        this.objects = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String iName = objects[position];

        LayoutInflater inflater =
                (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.reminder_images_on_popup, null);

        /** set image*/
        ImageView reminderImage = (ImageView) view.findViewById(R.id.popupImageListImageView);
        int res = context.getResources().getIdentifier(
                iName, "drawable", context.getPackageName()
        );
        reminderImage.setImageResource(res);

        /** set name */
        TextView reminderImageName = (TextView) view.findViewById(R.id.popupImageListImageName);
        reminderImageName.setText(iName);

        return view;
    }
}

