package com.example.sai.pheezeeapp.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
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
import com.example.sai.pheezeeapp.Classes.BodyPartWithMmtSelectionModel;
import com.example.sai.pheezeeapp.R;
import com.example.sai.pheezeeapp.activities.BodyPartSelection;
import com.example.sai.pheezeeapp.services.MqttHelper;

import org.eclipse.paho.client.mqttv3.MqttMessage;
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


    public String gradeSelected="",bodypartSelected="", orientationSelected="";

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView iv_bodypart;
        private RelativeLayout rl_left_right, rl_left, rl_right, rl_mmt_and_session, rl_left_section, rl_right_section;
        private TextView tv_model_mmt_grade_left, tv_model_mmt_grade_right, tv_selected_goal_text, tv_body_part_name, tv_middle;
        private TextView tv_0,tv_1,tv_2,tv_3,tv_4,tv_5;

        private RelativeLayout rl_mmt_session;

        private LinearLayout  ll_model_mmt_confirm, ll_model_mmt_cancel, ll_model_left, ll_model_right,ll_tv_confirm, ll_tv_cancel;
        private LinearLayout ll_tv_section, ll_tv_minus, ll_tv_plus;
        private Spinner sp_set_goal;


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
            rl_mmt_session = view.findViewById(R.id.rl_mmt_section); //for doing mmt

            //Linear layouts

            ll_model_mmt_confirm = view.findViewById(R.id.bp_model_mmt_confirm);
            ll_model_mmt_cancel = view.findViewById(R.id.bp_model_mmt_cancel);
            ll_tv_section = view.findViewById(R.id.ll_tv_section);
            ll_tv_minus = view.findViewById(R.id.ll_tv_minus);
            ll_tv_plus = view.findViewById(R.id.ll_tv_plus);
            //TextView Mmt Related
            ll_tv_cancel = view.findViewById(R.id.bp_model_mmt_cancel);
            ll_tv_confirm = view.findViewById(R.id.bp_model_mmt_confirm);


            //Text Views
            tv_model_mmt_grade_left = view.findViewById(R.id.bp_model_mmt_grade_left);
            tv_model_mmt_grade_right = view.findViewById(R.id.bp_model_mmt_grade_right);
            tv_selected_goal_text = view.findViewById(R.id.tv_selected_goal_text);
            tv_body_part_name = view.findViewById(R.id.tv_body_part_name);
            tv_0 = view.findViewById(R.id.mmt_tv_0);
            tv_1 = view.findViewById(R.id.mmt_tv_1);
            tv_2 = view.findViewById(R.id.mmt_tv_2);
            tv_3 = view.findViewById(R.id.mmt_tv_3);
            tv_4 = view.findViewById(R.id.mmt_tv_4);
            tv_5 = view.findViewById(R.id.mmt_tv_5);




            //spinner
            sp_set_goal = view.findViewById(R.id.sp_set_goal);

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
                .inflate(R.layout.layout_body_part_selection_new_model, parent, false);
        return new BodyPartWithMmtRecyclerView.ViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(@NonNull final BodyPartWithMmtRecyclerView.ViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        BodyPartWithMmtSelectionModel bodyPartWithMmtSelectionModel = bodyPartsList.get(position);
        holder.iv_bodypart.setImageResource(bodyPartWithMmtSelectionModel.getIv_body_part());
        holder.tv_body_part_name.setText(bodyPartWithMmtSelectionModel.getExercise_name());

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context,R.array.setSessionGoalSpinner, R.layout.support_simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        holder.sp_set_goal.setAdapter(adapter);

        holder.iv_bodypart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((BodyPartSelection)context).setFabVisible();
                if(!preferences.getString("bodyPartClicked","").equals("")){
                    ((BodyPartSelection)context).visibilityChanged();
                }
                bodypartSelected = holder.tv_body_part_name.getText().toString();
                holder.iv_bodypart.setVisibility(View.INVISIBLE);
                holder.rl_left_right.setVisibility(View.VISIBLE);
                editor.putString("bodyPartClicked",position+"");
                editor.commit();
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
                orientationSelected = "left";
                holder.rl_left_right.setVisibility(View.INVISIBLE);
                holder.iv_bodypart.setVisibility(View.VISIBLE);
                //Make the left overlay visivle and the spinner or mmt baset on the previous history


                holder.rl_left.setVisibility(View.VISIBLE);
                holder.rl_mmt_and_session.setVisibility(View.VISIBLE);
                holder.rl_left_section.setVisibility(View.VISIBLE);

                //check weather previously mmt done or not for left part
                String grade = checkMmtDone(true, holder);
                Log.i("grade","gg"+grade);
                if(!grade.equals("")) {
                    holder.tv_model_mmt_grade_left.setText("Mmt Grade -"+grade);
                    holder.sp_set_goal.setVisibility(View.VISIBLE);
                    holder.sp_set_goal.setSelection(0);
                    holder.iv_bodypart.setEnabled(false);
                }
                else {
                    holder.ll_tv_section.setVisibility(View.GONE);
                    holder.sp_set_goal.setVisibility(View.GONE);
                    holder.rl_mmt_session.setVisibility(View.VISIBLE);
                    holder.iv_bodypart.setEnabled(false);
                }
            }
        });

        holder.ll_model_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Right orientation is selected
                orientationSelected = "right";

                holder.rl_left_right.setVisibility(View.INVISIBLE);

                holder.iv_bodypart.setVisibility(View.VISIBLE);
                holder.rl_right.setVisibility(View.VISIBLE);
                holder.rl_mmt_and_session.setVisibility(View.VISIBLE);
                holder.rl_right_section.setVisibility(View.VISIBLE);

                String grade = checkMmtDone(false, holder);
                if(!grade.equals("")) {
                    holder.tv_model_mmt_grade_right.setText("Mmt Grade -"+grade);
                    holder.sp_set_goal.setVisibility(View.VISIBLE);
                    holder.sp_set_goal.setSelection(0);
                    holder.iv_bodypart.setEnabled(false);
                }
                else {
                    holder.ll_tv_section.setVisibility(View.GONE);
                    holder.sp_set_goal.setVisibility(View.GONE);
                    holder.rl_mmt_session.setVisibility(View.VISIBLE);
                    holder.iv_bodypart.setEnabled(false);
                }
            }
        });

        //tv's fot updating the grading thing
        holder.tv_model_mmt_grade_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //invisible the spinner and text section
                holder.ll_tv_section.setVisibility(View.GONE);
                holder.sp_set_goal.setVisibility(View.GONE);
                holder.rl_mmt_session.setVisibility(View.VISIBLE);

            }
        });

        holder.tv_model_mmt_grade_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.ll_tv_section.setVisibility(View.GONE);
                holder.sp_set_goal.setVisibility(View.GONE);
                holder.rl_mmt_session.setVisibility(View.VISIBLE);

            }
        });


        holder.sp_set_goal.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(!holder.sp_set_goal.getSelectedItem().toString().equals("Set Goal")) {
                    int selected  = Integer.parseInt(holder.sp_set_goal.getSelectedItem().toString().substring(0,2).trim());
                    holder.ll_tv_section.setVisibility(View.VISIBLE);
                    holder.sp_set_goal.setVisibility(View.GONE);
                    holder.tv_selected_goal_text.setText(holder.sp_set_goal.getSelectedItem().toString());
//                    if(selected==5){
//                        holder.ll_tv_section.setBackgroundColor(R.color.red);
//                    }
//                    else if(selected==10){
//                        holder.ll_tv_section.setBackgroundColor(R.color.red);
//                    }
//                    else if(selected==15){
//                        holder.ll_tv_section.setBackgroundColor(R.color.average_blue);
//                    }
//                    else if(selected==20){
//                        holder.ll_tv_section.setBackgroundColor(R.color.pale_good_green);
//                    }
//
//                    else {
//                        holder.ll_tv_section.setBackgroundColor(R.color.yellow);
//                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        holder.ll_tv_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.rl_left.getVisibility()==View.VISIBLE){
                    holder.tv_model_mmt_grade_left.setText("Mmt Grade -"+gradeSelected);
                }
                else {
                    holder.tv_model_mmt_grade_right.setText("Mmt Grade -"+gradeSelected);
                }
                MqttMessage message = new MqttMessage();
                if(!gradeSelected.equals("")){
                    JSONObject object = new JSONObject();
                    try {
                        object.put("phizioemail", json_phizio.getString("phizioemail"));
                        object.put("patientid",patientID);
                        object.put("bodypart",bodypartSelected);
                        object.put("orientation",orientationSelected);
                        object.put("grade",gradeSelected);

                        message.setPayload(object.toString().getBytes());
                        mqttHelper.publishMqttTopic(mqtt_publish_message,message);
                        saveMmtSessionLocally();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                holder.rl_mmt_session.setVisibility(View.INVISIBLE);
                holder.sp_set_goal.setVisibility(View.VISIBLE);
                holder.sp_set_goal.setSelection(0);

            }
        });

        holder.ll_tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.rl_mmt_session.setVisibility(View.INVISIBLE);
                holder.sp_set_goal.setVisibility(View.VISIBLE);
                holder.sp_set_goal.setSelection(0);
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


        //012345 section

        holder.tv_0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gradeSelected = String.valueOf(0);
                updateView(holder);
            }
        });

        holder.tv_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gradeSelected = String.valueOf(1);
                updateView(holder);
            }
        });

        holder.tv_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gradeSelected = String.valueOf(2);
                updateView(holder);
            }
        });

        holder.tv_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gradeSelected = String.valueOf(3);
                updateView(holder);
            }
        });

        holder.tv_4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gradeSelected = String.valueOf(4);
                updateView(holder);
            }
        });

        holder.tv_5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gradeSelected = String.valueOf(5);
                updateView(holder);
            }
        });
    }

    private void saveMmtSessionLocally() {
        try {
            json_phizio = new JSONObject(preferences.getString("phiziodetails",""));
            json_patients = new JSONArray(json_phizio.getString("phiziopatients"));
        } catch (JSONException e) {
            e.printStackTrace();
        }


        JSONObject selected = new JSONObject();
        try {
            selected.put("bodypart",bodypartSelected);
            selected.put("orientation",orientationSelected);
            selected.put("grade",gradeSelected);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.i("savelocally",selected.toString());
        boolean flag_has = false;
            for (int i=0;i<json_patients.length();i++){

                try {
                    JSONObject object = json_patients.getJSONObject(i);
                    if(object.getString("patientid").equals(patientID)){
                        if(object.has("mmtsessions")){
                            json_patient_mmt = new JSONArray(object.getString("mmtsessions"));
                            for (int j=0;j<json_patient_mmt.length();j++){
                                JSONObject object1 = json_patient_mmt.getJSONObject(j);
                                if(object1.getString("bodypart").equalsIgnoreCase(bodypartSelected) && object1.getString("orientation").equalsIgnoreCase(orientationSelected)){
                                    flag_has = true;
                                    json_patient_mmt.getJSONObject(j).put("grade",gradeSelected);
                                    Log.i("json mmt",json_patient_mmt.getJSONObject(j).toString());
                                    break;
                                }
                            }
                        }
                        break;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            if(!flag_has){
                json_patient_mmt.put(selected);
            }
        for (int i=0;i<json_patients.length();i++){
            try {
                JSONObject object = json_patients.getJSONObject(i);
                if(object.getString("patientid").equals(patientID)){
                    json_patients.getJSONObject(i).put("mmtsessions",json_patient_mmt.toString());
                    json_phizio.put("phiziopatients",json_patients);
                    editor.putString("phiziodetails",json_phizio.toString());
                    editor.commit();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void updateView(ViewHolder holder) {
        makeAllBackgroundWhite(holder);
        makeSelectedBackgroundChange(holder);
    }

    private void makeSelectedBackgroundChange(ViewHolder holder) {
        int index = Integer.parseInt(gradeSelected);
        if(index==0)
            holder.tv_0.setBackgroundResource(R.drawable.drawable_mmt_grade_selected);
        else if(index==1)
            holder.tv_1.setBackgroundResource(R.drawable.drawable_mmt_grade_selected);
        else if(index==2)
            holder.tv_2.setBackgroundResource(R.drawable.drawable_mmt_grade_selected);
        else if(index==3)
            holder.tv_3.setBackgroundResource(R.drawable.drawable_mmt_grade_selected);
        else if(index==4)
            holder.tv_4.setBackgroundResource(R.drawable.drawable_mmt_grade_selected);
        else
            holder.tv_5.setBackgroundResource(R.drawable.drawable_mmt_grade_selected);
    }

    private void makeAllBackgroundWhite(ViewHolder holder) {
        holder.tv_0.setBackgroundResource(R.drawable.drawable_mmt_circular_tv);
        holder.tv_1.setBackgroundResource(R.drawable.drawable_mmt_circular_tv);
        holder.tv_2.setBackgroundResource(R.drawable.drawable_mmt_circular_tv);
        holder.tv_3.setBackgroundResource(R.drawable.drawable_mmt_circular_tv);
        holder.tv_4.setBackgroundResource(R.drawable.drawable_mmt_circular_tv);
        holder.tv_5.setBackgroundResource(R.drawable.drawable_mmt_circular_tv);
    }

    private String checkMmtDone(boolean b, final BodyPartWithMmtRecyclerView.ViewHolder holder){

        String grade = "";
            for (int i = 0; i < json_patient_mmt.length(); i++) {
                try {
                    JSONObject object = json_patient_mmt.getJSONObject(i);

                    String exerciseType = object.getString("bodypart");
                    String orientation = object.getString("orientation");
                    Log.i("type",exerciseType+" "+orientation+" "+bodypartSelected);
                    if(b) {
                        if (exerciseType.equals(bodypartSelected) && orientation.equals("left")) {
                            grade = object.getString("grade");
                        }
                    }
                    else {
                        if (exerciseType.equals(bodypartSelected) && orientation.equals("right")) {
                            grade = object.getString("grade");
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
        }
        return  grade;
    }

    @Override
    public int getItemCount() {
        return bodyPartsList==null?0:bodyPartsList.size();
    }
}
