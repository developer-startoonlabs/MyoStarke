package com.startoonlabs.apps.pheezee.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.Transition;
import androidx.transition.TransitionManager;

import com.startoonlabs.apps.pheezee.R;
import com.startoonlabs.apps.pheezee.classes.CircularRevealTransition;
import com.startoonlabs.apps.pheezee.utils.MuscleOperation;
import com.startoonlabs.apps.pheezee.utils.ValueBasedColorOperations;

public class BodyPartSelectionRecyclerViewAdapter extends RecyclerView.Adapter<BodyPartSelectionRecyclerViewAdapter.ViewHolder> {
    private int selected_position = -1;
    Context context;
//    int[] myPartList = new int[]{R.drawable.elbow_part, R.drawable.knee_part,R.drawable.ankle_part,R.drawable.hip_part,
//            R.drawable.wrist_part,R.drawable.shoulder_part,R.drawable.other_body_part};
    TypedArray myPartList;
    String[] string_array_bodypart;
    private String default_max_emg = "900";
//    private List<BodyPartWithMmtSelectionModel> bodyPartsList;
    private int color_after_selected , color_nothing_selected;
    private String str_start, str_end, str_max_emg;
    onBodyPartOptionsSelectedListner listner;


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

        public void bindView(int id, String body_part){

        }
    }

    public BodyPartSelectionRecyclerViewAdapter( Context context){
        this.context = context;
        this.myPartList = context.getResources().obtainTypedArray(R.array.body_part);
        string_array_bodypart = context.getResources().getStringArray(R.array.bodyPartName);
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
//        BodyPartWithMmtSelectionModel bodyPartWithMmtSelectionModel = bodyPartsList.get(position);
        holder.iv_body_part_image.setImageResource(myPartList.getResourceId(position,-1));
        holder.tv_body_part_name.setText(string_array_bodypart[position]);


        if(selected_position!=position && selected_position!=-1){
            holder.cl_selection.setVisibility(View.GONE);
        }

        holder.cl_body_tv_and_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Transition transition = new CircularRevealTransition();
                transition.setDuration(300);
                transition.addTarget(holder.cl_selection);
                Animation aniFade = AnimationUtils.loadAnimation(context,R.anim.fade_in);
                holder.cl_body_tv_and_image.setAnimation(aniFade);
                holder.cl_body_tv_and_image.bringToFront();
                if(selected_position==position){
                    selected_position=-1;
                    TransitionManager.beginDelayedTransition((ViewGroup)holder.cl_selection.getParent(), transition);
                    holder.cl_selection.setVisibility(View.GONE);
                    if(listner!=null){
                        listner.onBodyPartSelected(null);
                        listner.onOrientationSelected(null);
                        listner.onBodyOrientationSelected(null);
                        listner.onExerciseNameSelected(null);
                        listner.onMuscleNameSelected(null);
                        listner.onGoalSelected(0);
                        listner.onMaxEmgUpdated("");
                        listner.onMaxAngleUpdated("");
                        listner.onMinAngleUpdated("");
                        listner.onBodyPartSelectedPostion(-1);
                    }
                }else{
                    if(selected_position!=-1){
                        notifyItemChanged(selected_position);
                    }
                    TransitionManager.beginDelayedTransition((ViewGroup)holder.cl_selection.getParent(), transition);
                    holder.cl_selection.setVisibility(View.VISIBLE);
                    selected_position = position;
                    if(listner!=null){
                        String bodypart = string_array_bodypart[position];
                        listner.onBodyPartSelected(bodypart);
                        listner.onBodyPartSelectedPostion(selected_position);
                    }
                    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context,R.array.setSessionGoalSpinner, R.layout.support_simple_spinner_dropdown_item);
                    adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
                    holder.sp_goal.setAdapter(adapter);

                    ArrayAdapter<CharSequence> array_muscle_names = new ArrayAdapter<CharSequence>(context, R.layout.support_simple_spinner_dropdown_item, MuscleOperation.getExerciseNames(selected_position));
                    array_muscle_names.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
                    holder.sp_exercise_name.setAdapter(array_muscle_names);

                    ArrayAdapter<String> array_exercise_names = new ArrayAdapter<String>(context, R.layout.support_simple_spinner_dropdown_item, MuscleOperation.getMusleNames(selected_position));
                    array_exercise_names.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
                    holder.sp_muscle_name.setAdapter(array_exercise_names);
                }
            }
        });

        holder.rg_orientation.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton btn = group.findViewById(checkedId);
                String orientation = btn.getText().toString();
                if (listner!=null){
                    listner.onOrientationSelected(orientation);
                }
            }
        });

        holder.rg_body_orientation.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton btn = group.findViewById(checkedId);
                String body_orientation = btn.getText().toString();
                if(listner!=null){
                    listner.onBodyOrientationSelected(body_orientation);
                }
            }
        });

        holder.sp_exercise_name.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position!=0){
                    String exercise_name = holder.sp_exercise_name.getSelectedItem().toString();
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
                    if(listner!=null){
                        listner.onExerciseNameSelected(exercise_name);
                        listner.onMinAngleUpdated(String.valueOf(normal_min));
                        listner.onMaxAngleUpdated(String.valueOf(normal_max));
                        listner.onMaxEmgUpdated(String.valueOf(900));
                        listner.onExerciseSelectedPostion(position);
                    }
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
                    if(listner!=null){
                        listner.onExerciseNameSelected(null);
                        listner.onMinAngleUpdated("");
                        listner.onMaxAngleUpdated("");
                        listner.onMaxEmgUpdated("");
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        holder.sp_muscle_name.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position!=0){
                    if(listner!=null){
                        String muscle_name = holder.sp_muscle_name.getSelectedItem().toString();
                        listner.onMuscleNameSelected(muscle_name);
                    }
                }
                else {
                    if(listner!=null){
                        listner.onMuscleNameSelected(null);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        holder.sp_goal.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position!=0){
                    String str_goal = holder.sp_goal.getSelectedItem().toString().substring(0,2);
                    str_goal = str_goal.replaceAll("\\s+","");
                    int reps_selected = Integer.parseInt(str_goal);
                    if(listner!=null){
                        listner.onGoalSelected(reps_selected);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                if(listner!=null){
                    listner.onGoalSelected(0);
                }
            }
        });

        holder.et_max_emg.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(listner!=null){
                    String s1 = s.toString();
                    listner.onMaxEmgUpdated(s1);
                }
            }
        });

        holder.et_max_angle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(listner!=null){
                    String s1 = s.toString();
                    listner.onMaxAngleUpdated(s1);
                }
            }
        });

        holder.et_min_angle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(listner!=null){
                    String s1 = s.toString();
                    listner.onMinAngleUpdated(s1);
                }
            }
        });

    }


    @Override
    public int getItemCount() {
        return string_array_bodypart==null?0:string_array_bodypart.length;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }


    public interface onBodyPartOptionsSelectedListner{
        void onBodyPartSelected(String bodypart);
        void onOrientationSelected(String orientation);
        void onBodyOrientationSelected(String body_orientation);
        void onExerciseNameSelected(String exercise_name);
        void onMuscleNameSelected(String muscle_name);
        void onGoalSelected(int reps_selected);
        void onMaxEmgUpdated(String max_emg_updated);
        void onMaxAngleUpdated(String max_angle_updated);
        void onMinAngleUpdated(String min_angle_updated);
        void onBodyPartSelectedPostion(int position);
        void onExerciseSelectedPostion(int position);
    }

    public void onSetOptionsSelectedListner(onBodyPartOptionsSelectedListner listner){
        this.listner = listner;
    }
}
