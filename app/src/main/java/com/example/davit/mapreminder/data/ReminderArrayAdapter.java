package com.example.davit.mapreminder.data;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.davit.mapreminder.R;
import com.example.davit.mapreminder.database.ReminderDataSource;
import com.example.davit.mapreminder.database.RemindersDBOpenHelper;
import java.util.List;

/**
 * Reminder Array adapter class
 */
public class ReminderArrayAdapter extends ArrayAdapter<Reminder> {

    private Context context;
    private List<Reminder> objects;

    /** constructor */
    public ReminderArrayAdapter(Context context, int resource, List<Reminder> objects) {
        super(context, resource, objects);
        this.context = context;
        this.objects = objects;
    }

    /**
     * setter for objects
     * @param reminders List<Reminder>
     */
    public void setObjects(List<Reminder> reminders){
        this.objects = reminders;
    }

    /** How many items are in the data set represented by this Adapter. */
    @Override
    public int getCount() {
      return objects.size();
    }

    /** get item in this position */
    @Override
    public Reminder getItem(int position) {
        return objects.get(position);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ViewHolder holder; // to reference the child views for later actions

        if (view == null){
            LayoutInflater inflater =
                    (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

            view = inflater.inflate(R.layout.reminder_items_on_main_activity, parent, false);

            // cache view fields into the holder
            holder = new ViewHolder();

            /** Find views */
            holder.reminderImage = (ImageView) view.findViewById(R.id.reminder_image);
            holder.reminderName = (TextView) view.findViewById(R.id.reminder_text);
            holder.reminderDistance = (TextView) view.findViewById(R.id.reminder_distance);
            holder.reminderIsActive = (CheckBox) view.findViewById(R.id.reminder_isActive);
            holder.distanceToReminderRadius = (TextView) view.findViewById(R.id.reminder_distanceToReminderRadius);

            // associate the holder with the view for later lookup
            view.setTag(holder);
        }else{
            // view already exists, get the holder instance from the view
            holder = (ViewHolder) view.getTag();
        }

        /** set image*/
        int res = view.getResources().getIdentifier(
                getItem(position).getType(), "drawable", context.getPackageName()
        );
        holder.reminderImage.setImageResource(res);

        /** set name */
        holder.reminderName.setText(getItem(position).getReminderName());

        /** set distance (as String)*/
        holder.reminderDistance.setText( getItem(position).getDistanceAsString() + "m" );

        /** set DistanceToReminderRadius (as String)*/
        holder.distanceToReminderRadius.setText( getItem(position).getDistanceToReminderRadiusAsString() + "m" );

        /** Is Active*/
        if(getItem(position).getIsActive() == 1 ){
            holder.reminderIsActive.setChecked(true);
        }else{
            holder.reminderIsActive.setChecked(false);
        }

        /** Set up and reuse the same listener for each row (view) */
        holder.reminderIsActive.setOnClickListener(PopupListener);
        Integer rowPosition = position;
        holder.reminderIsActive.setTag(rowPosition);

        return view;
    }



    /** Click listener on check box update database*/
    View.OnClickListener PopupListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                /** Open connection to the database */
                ReminderDataSource dataSource = new ReminderDataSource(context);
                dataSource.open();

                /** update database and set checkbox in reverse state */
//                Log.d("test", "id = "+String.valueOf(getItem(viewPosition).getId())+" name = "+String.valueOf(getItem(viewPosition).getReminderName()));
                Integer viewPosition = (Integer) view.getTag();

                if(getItem(viewPosition).getIsActive() == 1 ){
//                    objects.get(position).setIsActive(0);
                    dataSource.updateCheckbox(0, getItem(viewPosition).getId());

                    /** for restarting service if checkbox has been selected */
                    ReusableFunctions.sendResultToMainActivity(getContext(), "checkbox selected");
                }else{
//                    objects.get(position).setIsActive(1);
                    dataSource.updateCheckbox(1, getItem(viewPosition).getId());

                    /** for restarting service if checkbox has been selected */
                    ReusableFunctions.sendResultToMainActivity(getContext(), "checkbox selected");
                }
               // objects.get(position).setIsActive();
                // todo  refactor and correct if search (find by name)
                objects = dataSource.findAll(RemindersDBOpenHelper.COLUMN_CREATED + " DESC");
                ReminderArrayAdapter.this.notifyDataSetChanged();
            }
        };


    /** static class ViewHolder for caching the child views you're looking up at runtime */
    static class ViewHolder {
        ImageView reminderImage;
        TextView reminderName;
        TextView reminderDistance;
        TextView distanceToReminderRadius;
        CheckBox reminderIsActive;
    }
}
