package com.startoonlabs.apps.pheezee.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.startoonlabs.apps.pheezee.R;
import com.startoonlabs.apps.pheezee.classes.SessionListClass;


import java.util.ArrayList;


public class SessionListArrayAdapter extends ArrayAdapter<SessionListClass> {

    private TextView tv_bodypart_exercise,tv_muscle_name, tv_orientation_position, tv_session_time;


    private Context context;
    public ArrayList<SessionListClass> mSessionArrayList;

    public SessionListArrayAdapter(Context context, ArrayList<SessionListClass> mSessionArrayList){
        super(context, R.layout.sessions_listview_model, mSessionArrayList);
        this.mSessionArrayList=mSessionArrayList;
        this.context = context;
    }


    public void updateList(ArrayList<SessionListClass> mSessionArrayList){
        this.mSessionArrayList.clear();
        this.mSessionArrayList.addAll(mSessionArrayList);
        this.notifyDataSetChanged();
    }

    private class ViewHolder {
        TextView code;
        CheckBox name;
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;
        Log.v("ConvertView", String.valueOf(position));

        if (convertView == null) {

            LayoutInflater vi = (LayoutInflater) context.getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            convertView = vi.inflate(R.layout.sessions_listview_model, null);

            holder = new ViewHolder();
            holder.name = (CheckBox) convertView.findViewById(R.id.checkBox1);
            holder.name.setChecked(true);
            convertView.setTag(holder);

            holder.name.setOnClickListener( new View.OnClickListener() {
                public void onClick(View v) {
                    CheckBox cb = (CheckBox) v ;
                    SessionListClass session_list_element = (SessionListClass) cb.getTag();
                    session_list_element.setSelected(cb.isChecked());
                }
            });
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        SessionListClass selected_item = mSessionArrayList.get(position);
        holder.name.setChecked(selected_item.isSelected());
        holder.name.setTag(selected_item);

        tv_bodypart_exercise = convertView.findViewById(R.id.tv_bodypart_exercise);
        tv_muscle_name = convertView.findViewById(R.id.tv_muscle_name);
        tv_orientation_position = convertView.findViewById(R.id.tv_orientation_position);
        tv_session_time = convertView.findViewById(R.id.tv_session_time);

        tv_bodypart_exercise.setText(mSessionArrayList.get(position).getBodypart() +" "+ mSessionArrayList.get(position).getExercise());
        tv_muscle_name.setText(mSessionArrayList.get(position).getMuscle_name());
        tv_orientation_position.setText(mSessionArrayList.get(position).getPosition()+" - "+mSessionArrayList.get(position).getOrientation());
        tv_session_time.setText(mSessionArrayList.get(position).getSession_time());

        ImageView image_exercise = convertView.findViewById(R.id.image_exercise);

        // Setting the proper image

        String feedback_icon = mSessionArrayList.get(position).getPosition()+"_"+mSessionArrayList.get(position).getBodypart()+"_"+mSessionArrayList.get(position).getExercise();
        feedback_icon = "ic_fb_"+feedback_icon;
        feedback_icon = feedback_icon.replace(" - ","_");
        feedback_icon = feedback_icon.replace(" ","_");
        feedback_icon = feedback_icon.replace(")","");
        feedback_icon = feedback_icon.replace("(","");
        feedback_icon = feedback_icon.toLowerCase();

        int res = context.getResources().getIdentifier(feedback_icon, "drawable",context.getPackageName());

        if(res !=0) {
            image_exercise.setImageResource(res);
        }
        CheckBox temp = (CheckBox) convertView.findViewById(R.id.checkBox1);
        temp.setChecked(true);
        selected_item.setSelected(temp.isChecked());

        return convertView;

    }
}
