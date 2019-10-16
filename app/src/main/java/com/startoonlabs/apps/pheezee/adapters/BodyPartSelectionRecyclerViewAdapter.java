package com.startoonlabs.apps.pheezee.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.Transition;
import androidx.transition.TransitionManager;

import com.startoonlabs.apps.pheezee.R;
import com.startoonlabs.apps.pheezee.activities.BodyPartSelection;
import com.startoonlabs.apps.pheezee.classes.BodyPartWithMmtSelectionModel;
import com.startoonlabs.apps.pheezee.classes.CircularRevealTransition;
import com.startoonlabs.apps.pheezee.utils.MuscleOperation;
import com.startoonlabs.apps.pheezee.utils.ValueBasedColorOperations;

import java.util.List;
import java.util.logging.Handler;

import static com.startoonlabs.apps.pheezee.adapters.BodyPartWithMmtRecyclerView.selectedPosition;

public class BodyPartSelectionRecyclerViewAdapter extends RecyclerView.Adapter<BodyPartSelectionRecyclerViewAdapter.ViewHolder> {
    private int selected_position = -1;
    Context context;
    private String msucle_name, exercise_name, orientation, body_orientation, default_max_emg = "900";
    private int reps_selected, max_angle_selected, min_angle_selected, max_emg_selected;
    private List<BodyPartWithMmtSelectionModel> bodyPartsList;
    private int color_after_selected , color_nothing_selected;
    private String str_start, str_end, str_max_emg;

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_body_part_name;
        ImageView iv_body_part_image;
        ConstraintLayout cl_body_tv_and_image;
        ConstraintLayout cl_selection;
        RadioGroup rg_orientation;
        RadioGroup rg_body_orientation;
        Spinner sp_exercise_name, sp_muscle_name, sp_goal;
        EditText et_max_angle, et_min_angle, et_max_emg;
        TextView tv_start, tv_end, tv_max_emg;

        ViewHolder(View view) {
            super(view);
            cl_body_tv_and_image = view.findViewById(R.id.model_cl_image_tv);
            cl_selection = view.findViewById(R.id.model_selection);
            rg_body_orientation = view.findViewById(R.id.model_rg_body_orientation);
            rg_orientation = view.findViewById(R.id.model_rg_orientation);

            //spinnsers
            sp_exercise_name = view.findViewById(R.id.model_sp_exercise_name);
            sp_muscle_name = view.findViewById(R.id.model_sp_musclename_name);
            sp_goal = view.findViewById(R.id.model_sp_set_goal);

            //textviews
            tv_body_part_name = view.findViewById(R.id.model_tv_body_part_name);

            //Imageview
            iv_body_part_image = view.findViewById(R.id.model_iv_bodypart_image);

            //Number picker
            et_max_angle = view.findViewById(R.id.model_et_max_angle);
            et_max_emg = view.findViewById(R.id.model_et_max_emg);
            et_min_angle = view.findViewById(R.id.model_et_min_angle);

            //TextView
            tv_start = view.findViewById(R.id.model_tv_start);
            tv_end = view.findViewById(R.id.model_tv_stop);
            tv_max_emg = view.findViewById(R.id.tv_max_emg);
        }
    }

    public BodyPartSelectionRecyclerViewAdapter(List<BodyPartWithMmtSelectionModel> bodyPartsList, Context context){
        this.bodyPartsList = bodyPartsList;
        this.context = context;

        this.str_start = context.getResources().getString(R.string.start);
        this.str_end = context.getResources().getString(R.string.end);
        this.str_max_emg = context.getResources().getString(R.string.max_emg);
        this.color_after_selected = context.getResources().getColor(R.color.background_green);
        this.color_nothing_selected = context.getResources().getColor(R.color.pitch_black);
    }


    @NonNull
    @Override
    public BodyPartSelectionRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // create a new view
        final View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.body_part_selection_list_model, parent, false);
        return new BodyPartSelectionRecyclerViewAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final BodyPartSelectionRecyclerViewAdapter.ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        BodyPartWithMmtSelectionModel bodyPartWithMmtSelectionModel = bodyPartsList.get(position);
        holder.iv_body_part_image.setImageResource(bodyPartWithMmtSelectionModel.getIv_body_part());
        holder.tv_body_part_name.setText(bodyPartWithMmtSelectionModel.getExercise_name());
        Transition transition = new CircularRevealTransition();
        transition.setDuration(300);
        transition.addTarget(holder.cl_selection);
        Animation slide_down = AnimationUtils.loadAnimation(context,R.anim.list_item_down);
        Animation slide_up = AnimationUtils.loadAnimation(context,R.anim.list_item_up);

        if(selected_position!=position && selected_position!=-1){
            holder.cl_selection.setVisibility(View.GONE);
        }

        //Reps array
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context,R.array.setSessionGoalSpinner, R.layout.support_simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        holder.sp_goal.setAdapter(adapter);

        ArrayAdapter<CharSequence> array_muscle_names = new ArrayAdapter<CharSequence>(context, R.layout.support_simple_spinner_dropdown_item, MuscleOperation.getExerciseNames(position));
        array_muscle_names.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        holder.sp_exercise_name.setAdapter(array_muscle_names);

        ArrayAdapter<String> array_exercise_names = new ArrayAdapter<String>(context, R.layout.support_simple_spinner_dropdown_item, MuscleOperation.getMusleNames(position));
        array_exercise_names.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        holder.sp_muscle_name.setAdapter(array_exercise_names);


        holder.cl_body_tv_and_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation aniFade = AnimationUtils.loadAnimation(context,R.anim.fade_in);
                holder.cl_body_tv_and_image.setAnimation(aniFade);
                holder.cl_body_tv_and_image.bringToFront();
                if(selected_position==position){
                    selected_position=-1;
                    TransitionManager.beginDelayedTransition((ViewGroup)holder.cl_selection.getParent(), transition);
                    holder.cl_selection.setVisibility(View.GONE);
                }else{
                    if(selected_position!=-1){
                        notifyItemChanged(selected_position);
                    }
                    TransitionManager.beginDelayedTransition((ViewGroup)holder.cl_selection.getParent(), transition);
                    holder.cl_selection.setVisibility(View.VISIBLE);
                    selected_position = position;
                }
            }
        });

        holder.sp_exercise_name.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position!=0){
                    exercise_name = holder.sp_exercise_name.getSelectedItem().toString();
                    int normal_min = ValueBasedColorOperations.getBodyPartMinValue(selected_position,position);
                    int normal_max = ValueBasedColorOperations.getBodyPartMaxValue(selected_position,position);
                    holder.tv_end.setText(str_end.concat(String.valueOf(normal_max)));
                    holder.tv_start.setText(str_start.concat(String.valueOf(normal_min)));
                    holder.tv_max_emg.setText(str_max_emg.concat(": "+String.valueOf(900)));
                    holder.et_max_angle.setText(String.valueOf(normal_max));
                    holder.et_min_angle.setText(String.valueOf(normal_min));
                    holder.et_max_emg.setText(default_max_emg);
                    holder.tv_max_emg.setTextColor(color_after_selected);
                    holder.tv_start.setTextColor(color_after_selected);
                    holder.tv_end.setTextColor(color_after_selected);
                }else {
                    holder.tv_end.setText(str_end);
                    holder.tv_start.setText(str_start);
                    holder.tv_max_emg.setText(str_max_emg);
                    holder.et_min_angle.setText("");
                    holder.et_max_angle.setText("");
                    holder.et_max_emg.setText("");
                    holder.tv_max_emg.setTextColor(color_nothing_selected);
                    holder.tv_start.setTextColor(color_nothing_selected);
                    holder.tv_end.setTextColor(color_nothing_selected);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }


    @Override
    public int getItemCount() {
        return bodyPartsList==null?0:bodyPartsList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
}
