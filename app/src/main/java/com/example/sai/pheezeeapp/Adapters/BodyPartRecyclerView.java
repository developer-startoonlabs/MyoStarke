package com.example.sai.pheezeeapp.Adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sai.pheezeeapp.Activities.BodyPartSelection;
import com.example.sai.pheezeeapp.Activities.PatientsView;
import com.example.sai.pheezeeapp.Classes.BodyPartSelectionModel;
import com.example.sai.pheezeeapp.R;


import java.util.List;

public class BodyPartRecyclerView extends RecyclerView.Adapter<BodyPartRecyclerView.ViewHolder> {

    private List<BodyPartSelectionModel> bodyPartsList;
    Context context;

    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        ImageView iv_body_part, iv_body_part_preview;
        TextView tv_exercise_name;
        RelativeLayout rl_preview;
        Spinner sp_select_goal;
        LinearLayout ll_tv_section;
        TextView tv_middle;
        LinearLayout ll_tv_plus,ll_tv_minus;
        FrameLayout fl_fab_background;

        ViewHolder(View view) {
            super(view);
            iv_body_part = view.findViewById(R.id.bodypartImage);
            iv_body_part_preview = view.findViewById(R.id.previewImage);
            tv_exercise_name = view.findViewById(R.id.tv_body_part_name);
            rl_preview = view.findViewById(R.id.rl_preview);
            sp_select_goal = view.findViewById(R.id.sp_set_goal);
            ll_tv_section = view.findViewById(R.id.ll_tv_section);
            ll_tv_minus = view.findViewById(R.id.ll_tv_minus);
            ll_tv_plus = view.findViewById(R.id.ll_tv_plus);
            //tv_section text views
            tv_middle = view.findViewById(R.id.tv_selected_goal_text);


        }
    }

    public BodyPartRecyclerView(List<BodyPartSelectionModel> bodyPartsList, Context context){
        this.bodyPartsList = bodyPartsList;
        this.context = context;
        preferences = PreferenceManager.getDefaultSharedPreferences(this.context);
    }

    @NonNull
    @Override
    public BodyPartRecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // create a new view
        final View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_body_part_selection_model, parent, false);
        return new BodyPartRecyclerView.ViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(@NonNull final BodyPartRecyclerView.ViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        BodyPartSelectionModel bodyPartSelectionModel = bodyPartsList.get(position);
        holder.iv_body_part.setImageResource(bodyPartSelectionModel.getIv_body_part());
        holder.iv_body_part_preview.setImageResource(bodyPartSelectionModel.getIv_body_part_preview());
        holder.tv_exercise_name.setText(bodyPartSelectionModel.getExercise_name());
        //}
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context,R.array.setGoalSpinner, R.layout.support_simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        holder.sp_select_goal.setAdapter(adapter);


        holder.iv_body_part_preview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View layout = ((BodyPartSelection)context).getLayoutInflater().inflate(R.layout.popup_click_to_preview,null);
                ImageView iv_click_prev = layout.findViewById(R.id.iv_click_to_preview);
                final PopupWindow window = new PopupWindow(layout,ConstraintLayout.LayoutParams.MATCH_PARENT,ConstraintLayout.LayoutParams.MATCH_PARENT);
                iv_click_prev.setImageDrawable(holder.iv_body_part_preview.getDrawable());
                window.showAtLocation(v.getRootView(), Gravity.CENTER, 0, 0);
                final RelativeLayout rl_dismiss_dialog = layout.findViewById(R.id.rl_dismiss_dialog);
                rl_dismiss_dialog.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        window.dismiss();
                    }
                });
            }
        });

        holder.sp_select_goal.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                if(!holder.sp_select_goal.getSelectedItem().toString().equals("Set Goal")) {
                    holder.ll_tv_section.setVisibility(View.VISIBLE);
                    holder.sp_select_goal.setVisibility(View.INVISIBLE);
                    holder.tv_middle.setText(holder.sp_select_goal.getSelectedItem().toString());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        holder.tv_middle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.ll_tv_section.setVisibility(View.INVISIBLE);
                holder.sp_select_goal.setVisibility(View.VISIBLE);
                holder.sp_select_goal.performClick();
            }
        });

        holder.ll_tv_minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str_time = holder.tv_middle.getText().toString();
                str_time = str_time.replaceAll("[a-zA-Z]","").trim();
                if(str_time.toLowerCase().indexOf(":")>=0){
                    int hours = Integer.parseInt(str_time.substring(0,str_time.indexOf(":")));
                    int min = Integer.parseInt(str_time.substring(str_time.indexOf(":")+1));

                    if(hours==1 && min<15){
                        min = 59;
                        str_time = ""+min+" min";
                    }
                    else {
                        if(min<15){
                            hours-=1;
                            min = 45;

                            if(hours<10){
                                str_time = "0"+hours+":"+min+" hr";
                            }
                            else {
                                str_time = hours+":"+min+" hr";
                            }
                        }
                        else {
                            min-=15;
                            if(hours<10){
                                if(min==0)
                                    str_time = "0"+hours+":"+min+"0 hr";
                                else
                                    str_time = "0"+hours+":"+min+" hr";
                            }
                            else {
                                if(min==0)
                                    str_time = hours+":"+min+"0 hr";
                                else
                                    str_time = hours+":"+min+" hr";
                            }
                        }
                    }

                    holder.tv_middle.setText(str_time);
                }
                else {
                    int i_time = Integer.parseInt(str_time);
                    if(i_time==1){
                        Toast.makeText(context, "Can not be zero or negetive..", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        i_time-=1;
                        if(i_time<10){
                            str_time = "0"+i_time+" min";
                        }
                        else {
                            str_time = i_time+" min";
                        }
                        holder.tv_middle.setText(str_time);
                    }
                }
            }
        });

        holder.ll_tv_plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str_time = holder.tv_middle.getText().toString();
                str_time = str_time.replaceAll("[a-zA-Z]","").trim();
                if(str_time.toLowerCase().indexOf(":")>=0){

                    Toast.makeText(context, ""+str_time, Toast.LENGTH_SHORT).show();
                    int hours = Integer.parseInt(str_time.substring(0,str_time.indexOf(":")));
                    int min = Integer.parseInt(str_time.substring(str_time.indexOf(":")+1));
                    if(min<45){
                        min+=15;
                        if(hours<10){
                            str_time = "0"+hours+":"+min+" hr";
                        }
                        else {
                            str_time = hours+":"+min+" hr";
                        }
                    }
                    else {
                        min = 0;
                        hours = hours+1;
                        if(hours<10){
                            str_time = 0+""+hours+":"+min+"0 hr";
                        }
                        else {
                            str_time = hours+":"+min+"0 hr";
                        }
                    }

                    holder.tv_middle.setText(str_time);
                }
                else {
                    int i_time = Integer.parseInt(str_time);
                    if(i_time==1 ){
                        int hours = i_time;
                        int min  = 15;
                        str_time = "0"+i_time+":"+min;
                        holder.tv_middle.setText(str_time+" hr");
                    }
                    else if(i_time==59){
                        int hours = 1;
                        str_time = "0"+hours+" hr";
                        holder.tv_middle.setText(str_time);
                    }
                    else {
                        i_time+=1;
                        if(i_time<10){
                            str_time = "0"+i_time+" min";
                        }
                        else {
                            str_time = i_time+" min";
                        }
                        holder.tv_middle.setText(str_time);
                    }
                }
            }
        });

        holder.iv_body_part.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((BodyPartSelection)context).setFabVisible();
                if(!preferences.getString("bodyPartClicked","").equals("")){
                    ((BodyPartSelection)context).visibilityChanged();
                }
                holder.rl_preview.setVisibility(View.VISIBLE);
                holder.iv_body_part.setVisibility(View.INVISIBLE);
                holder.sp_select_goal.setVisibility(View.VISIBLE);
                editor = preferences.edit();
                editor.putString("bodyPartClicked",position+"");
                editor.commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return bodyPartsList==null?0:bodyPartsList.size();
    }
}
