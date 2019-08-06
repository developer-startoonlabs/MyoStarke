package com.startoonlabs.apps.pheezee.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.startoonlabs.apps.pheezee.R;
import com.startoonlabs.apps.pheezee.activities.BodyPartSelection;
import com.startoonlabs.apps.pheezee.classes.BodyPartWithMmtSelectionModel;
import com.startoonlabs.apps.pheezee.services.MqttHelper;
import com.startoonlabs.apps.pheezee.utils.MuscleOperation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class BodyPartWithMmtRecyclerView extends RecyclerView.Adapter<BodyPartWithMmtRecyclerView.ViewHolder> {

    private List<BodyPartWithMmtSelectionModel> bodyPartsList;
    private Context context;
    private MqttHelper mqttHelper;
    private String mqtt_publish_message = "phizio/mmt/addpatientsession";
    private JSONObject json_phizio;
    private JSONArray json_patients;
    private JSONArray json_patient_mmt;
    private String patientID;
    public static int selectedPosition = -1;


    public static String gradeSelected="",bodypartSelected="", orientationSelected="";

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView iv_bodypart;
        private RelativeLayout rl_left_right, rl_left, rl_right, rl_mmt_and_session, rl_left_section, rl_right_section;
        private TextView   tv_selected_goal_text, tv_body_part_name, tv_middle;



        private LinearLayout   ll_model_left, ll_model_right;
        private LinearLayout ll_tv_section, ll_tv_minus, ll_tv_plus;
        private Spinner sp_set_goal, sp_muscle_name;


        ViewHolder(View view) {
            super(view);
            iv_bodypart = view.findViewById(R.id.bodypartImage);
            //Relative Layouts
            rl_left_right = view.findViewById(R.id.rl_left_right); //layout for left and right excercise choice
            ll_model_left = view.findViewById(R.id.bp_model_left);//to choose left part
            ll_model_right = view.findViewById(R.id.bp_model_right);//to choose right part
            rl_left = view.findViewById(R.id.rl_left);  //left overlay color
            rl_right = view.findViewById(R.id.rl_right); //right overlay color
            rl_mmt_and_session = view.findViewById(R.id.rl_mmt_and_session); //left and right grade text views layout
            rl_left_section = view.findViewById(R.id.rl_left_section);  //left section of the view
            rl_right_section = view.findViewById(R.id.rl_right_section); //right section of the view

            //Linear layouts
            ll_tv_section = view.findViewById(R.id.ll_tv_section);
            ll_tv_minus = view.findViewById(R.id.ll_tv_minus);
            ll_tv_plus = view.findViewById(R.id.ll_tv_plus);


            //Text Views
            tv_selected_goal_text = view.findViewById(R.id.tv_selected_goal_text);
            tv_body_part_name = view.findViewById(R.id.tv_body_part_name);




            //spinner
            sp_set_goal = view.findViewById(R.id.sp_set_goal);
            sp_muscle_name = view.findViewById(R.id.sp_set_muscle);

        }
    }

    public BodyPartWithMmtRecyclerView(List<BodyPartWithMmtSelectionModel> bodyPartsList, Context context){
        this.bodyPartsList = bodyPartsList;
        this.context = context;
        patientID = ((BodyPartSelection)context).getPatientId();
        preferences = PreferenceManager.getDefaultSharedPreferences(this.context);
        editor = preferences.edit();
        json_phizio = new JSONObject();
        json_patients = new JSONArray();
        json_patient_mmt = new JSONArray();
        mqttHelper = new MqttHelper(context);

        try {
            json_phizio = new JSONObject(preferences.getString("phiziodetails",""));
            json_patients = new JSONArray(json_phizio.getString("phiziopatients"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if(patientID!=null){
            for (int i=0;i<json_patients.length();i++){
                try {
                    JSONObject object = json_patients.getJSONObject(i);
                    if(object.getString("patientid").equals(patientID)){
                        if(object.has("mmtsessions"))
                            json_patient_mmt = new JSONArray(object.getString("mmtsessions"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        Log.i("json mmt", json_patient_mmt.toString());
        Log.i("json mmt", patientID);
    }


    @NonNull
    @Override
    public BodyPartWithMmtRecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // create a new view
        final View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.popup_muscle_selection, parent, false);
        return new BodyPartWithMmtRecyclerView.ViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(@NonNull final BodyPartWithMmtRecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        BodyPartWithMmtSelectionModel bodyPartWithMmtSelectionModel = bodyPartsList.get(position);
        holder.iv_bodypart.setImageResource(bodyPartWithMmtSelectionModel.getIv_body_part());
        holder.tv_body_part_name.setText(bodyPartWithMmtSelectionModel.getExercise_name());

        Log.i("position",position+"");
        if(selectedPosition!=position && selectedPosition!=-1){
            if (holder.rl_left_section.getVisibility() == View.VISIBLE)
                holder.rl_left_section.setVisibility(View.INVISIBLE);

            if (holder.rl_right_section.getVisibility() == View.VISIBLE)
                holder.rl_right_section.setVisibility(View.INVISIBLE);

            if (holder.rl_left_right.getVisibility() == View.VISIBLE)
                holder.rl_left_right.setVisibility(View.INVISIBLE);
            if (holder.rl_left.getVisibility() == View.VISIBLE)
                holder.rl_left.setVisibility(View.INVISIBLE);
            if (holder.rl_right.getVisibility() == View.VISIBLE)
                holder.rl_right.setVisibility(View.INVISIBLE);
            if (holder.rl_mmt_and_session.getVisibility() == View.VISIBLE)
                holder.rl_mmt_and_session.setVisibility(View.INVISIBLE);
            if (holder.ll_tv_section.getVisibility() == View.VISIBLE)
                holder.ll_tv_section.setVisibility(View.GONE);
            if (holder.sp_set_goal.getVisibility() == View.VISIBLE) {
                holder.sp_set_goal.setSelection(0);
                holder.sp_set_goal.setVisibility(View.GONE);
            }

//                development
            if (holder.sp_muscle_name.getVisibility() == View.VISIBLE) {
                holder.sp_muscle_name.setSelection(0);
                holder.sp_muscle_name.setVisibility(View.GONE);
            }

            if (holder.iv_bodypart.getVisibility() == View.INVISIBLE)
                holder.iv_bodypart.setVisibility(View.VISIBLE);


            holder.iv_bodypart.setEnabled(true);
        }



        //Reps array
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context,R.array.setSessionGoalSpinner, R.layout.support_simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        holder.sp_set_goal.setAdapter(adapter);

        ArrayAdapter<CharSequence> array_muscle_names = new ArrayAdapter<CharSequence>(context, R.layout.support_simple_spinner_dropdown_item, MuscleOperation.getExerciseNames(position));
        array_muscle_names.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        holder.sp_muscle_name.setAdapter(array_muscle_names);

        holder.iv_bodypart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((BodyPartSelection)context).reinitializeStatics();
                ((BodyPartSelection)context).setFabVisible();
                editor = preferences.edit();
                if(selectedPosition!=-1){
                    notifyItemChanged(selectedPosition);
                }
                selectedPosition = position;
                BodyPartSelection.musclename = "";
                Log.i("clicked","clicked"+position);
                bodypartSelected = holder.tv_body_part_name.getText().toString();
                holder.iv_bodypart.setVisibility(View.INVISIBLE);
                holder.rl_left_right.setVisibility(View.VISIBLE);
                editor.putString("bodyPartClicked",position+"");
                editor.apply();
            }
        });

        holder.tv_selected_goal_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.ll_tv_section.setVisibility(View.GONE);
                holder.sp_set_goal.setVisibility(View.VISIBLE);
                holder.sp_set_goal.performClick();
            }
        });

        holder.ll_model_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //left orientation is selected
                orientationSelected = "Left";
                holder.rl_left_right.setVisibility(View.INVISIBLE);
                holder.iv_bodypart.setVisibility(View.VISIBLE);
                //Make the left overlay visivle and the spinner or mmt baset on the previous history


                holder.rl_left.setVisibility(View.VISIBLE);
                holder.rl_mmt_and_session.setVisibility(View.VISIBLE);
                holder.rl_left_section.setVisibility(View.VISIBLE);

                holder.ll_tv_section.setVisibility(View.VISIBLE);
                holder.sp_set_goal.setVisibility(View.VISIBLE);
                holder.sp_muscle_name.setVisibility(View.VISIBLE);
                holder.sp_set_goal.setSelection(0);
                holder.iv_bodypart.setEnabled(false);
            }
        });

        holder.ll_model_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Right orientation is selected
                orientationSelected = "Right";

                holder.rl_left_right.setVisibility(View.INVISIBLE);

                holder.iv_bodypart.setVisibility(View.VISIBLE);
                holder.rl_right.setVisibility(View.VISIBLE);
                holder.rl_mmt_and_session.setVisibility(View.VISIBLE);
                holder.rl_right_section.setVisibility(View.VISIBLE);

                holder.ll_tv_section.setVisibility(View.VISIBLE);
                holder.sp_set_goal.setVisibility(View.VISIBLE);
                holder.sp_muscle_name.setVisibility(View.VISIBLE);
                holder.sp_set_goal.setSelection(0);
                holder.iv_bodypart.setEnabled(false);
            }
        });





        holder.sp_muscle_name.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position!=0) {
                    BodyPartSelection.musclename = holder.sp_muscle_name.getSelectedItem().toString();
                    BodyPartSelection.exercise_selected_position=position;
                }
                else {
                    BodyPartSelection.musclename = "";
                    BodyPartSelection.exercise_selected_position=0;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        holder.sp_set_goal.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(!holder.sp_set_goal.getSelectedItem().toString().equals("Set Goal")) {
                    String str_goal = holder.sp_set_goal.getSelectedItem().toString().substring(0,2);
                    str_goal = str_goal.replaceAll("\\s+","");
                    BodyPartSelection.repsselected = Integer.parseInt(str_goal);
                    holder.ll_tv_section.setVisibility(View.VISIBLE);
                    holder.sp_set_goal.setVisibility(View.GONE);
                    holder.tv_selected_goal_text.setText(holder.sp_set_goal.getSelectedItem().toString());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        holder.ll_tv_plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int num = Integer.parseInt(holder.tv_selected_goal_text.getText().toString().substring(0,2).trim());
                if(num<25){
                    num+=5;
                }

                holder.tv_selected_goal_text.setText(String.valueOf(num).concat(" Reps"));
            }
        });

        holder.ll_tv_minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int num = Integer.parseInt(holder.tv_selected_goal_text.getText().toString().substring(0,2).trim());
                if(num>5){
                    num-=5;
                }
                holder.tv_selected_goal_text.setText(String.valueOf(num).concat(" Reps"));
            }
        });
    }



    public void removeResources(){
        if(mqttHelper!=null){
            mqttHelper.mqttAndroidClient.unregisterResources();
            mqttHelper.mqttAndroidClient.close();
        }
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
