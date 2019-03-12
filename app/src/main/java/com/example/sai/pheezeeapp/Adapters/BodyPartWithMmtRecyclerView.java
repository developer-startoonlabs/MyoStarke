package com.example.sai.pheezeeapp.adapters;

import android.annotation.SuppressLint;
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

import com.example.sai.pheezeeapp.Classes.BodyPartSelectionModel;
import com.example.sai.pheezeeapp.Classes.BodyPartWithMmtSelectionModel;
import com.example.sai.pheezeeapp.R;
import com.example.sai.pheezeeapp.activities.BodyPartSelection;

import java.util.List;

public class BodyPartWithMmtRecyclerView extends RecyclerView.Adapter<BodyPartWithMmtRecyclerView.ViewHolder> {

    private List<BodyPartWithMmtSelectionModel> bodyPartsList;
    Context context;

    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_bodypart;
        RelativeLayout rl_left_right, rl_left, rl_right, rl_mmt_and_session, rl_left_section, rl_right_section;
        TextView tv_model_mmt_grade_left, tv_model_mmt_grade_right, tv_selected_goal_text, tv_body_part_name, tv_middle;

        RelativeLayout rl_mmt_session;

        LinearLayout ll_model_tv_0,ll_model_tv_1, ll_model_tv_2, ll_model_tv_3, ll_model_tv_4, ll_model_tv_5, ll_model_mmt_confirm, ll_model_mmt_cancel, ll_model_left, ll_model_right;
        LinearLayout ll_tv_section, ll_tv_minus, ll_tv_plus;
        Spinner sp_set_goal;


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
            ll_model_tv_0 = view.findViewById(R.id.bp_model_tv_0);
            ll_model_tv_1 = view.findViewById(R.id.bp_model_tv_1);
            ll_model_tv_2 = view.findViewById(R.id.bp_model_tv_2);
            ll_model_tv_3 = view.findViewById(R.id.bp_model_tv_3);
            ll_model_tv_4 = view.findViewById(R.id.bp_model_tv_4);
            ll_model_tv_5 = view.findViewById(R.id.bp_model_tv_5);

            ll_model_mmt_confirm = view.findViewById(R.id.bp_model_mmt_confirm);
            ll_model_mmt_cancel = view.findViewById(R.id.bp_model_mmt_cancel);
            ll_tv_section = view.findViewById(R.id.ll_tv_section);
            ll_tv_minus = view.findViewById(R.id.ll_tv_minus);
            ll_tv_plus = view.findViewById(R.id.ll_tv_plus);


            //Text Views
            tv_model_mmt_grade_left = view.findViewById(R.id.bp_model_mmt_grade_left);
            tv_model_mmt_grade_right = view.findViewById(R.id.bp_model_mmt_grade_right);
            tv_selected_goal_text = view.findViewById(R.id.tv_selected_goal_text);
            tv_body_part_name = view.findViewById(R.id.tv_body_part_name);

            //spinner
            sp_set_goal = view.findViewById(R.id.sp_set_goal);


        }
    }

    public BodyPartWithMmtRecyclerView(List<BodyPartWithMmtSelectionModel> bodyPartsList, Context context){
        this.bodyPartsList = bodyPartsList;
        this.context = context;

        preferences = PreferenceManager.getDefaultSharedPreferences(this.context);
        editor = preferences.edit();
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
                holder.iv_bodypart.setVisibility(View.INVISIBLE);
                holder.rl_left_right.setVisibility(View.VISIBLE);
                editor.putString("bodyPartClicked",position+"");
                editor.commit();
            }
        });

        holder.ll_model_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.rl_left_right.setVisibility(View.INVISIBLE);
                holder.iv_bodypart.setVisibility(View.VISIBLE);
                //Make the left overlay visivle and the spinner or mmt baset on the previous history


                holder.rl_left.setVisibility(View.VISIBLE);
                holder.rl_mmt_and_session.setVisibility(View.VISIBLE);
                holder.rl_left_section.setVisibility(View.VISIBLE);
                holder.sp_set_goal.setVisibility(View.VISIBLE);

                holder.iv_bodypart.setEnabled(false);
            }
        });

        holder.ll_model_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.rl_left_right.setVisibility(View.INVISIBLE);

                holder.iv_bodypart.setVisibility(View.VISIBLE);
                holder.rl_right.setVisibility(View.VISIBLE);
                holder.rl_mmt_and_session.setVisibility(View.VISIBLE);
                holder.rl_right_section.setVisibility(View.VISIBLE);
                holder.sp_set_goal.setVisibility(View.VISIBLE);
                holder.iv_bodypart.setEnabled(false);
            }
        });

        //tv's fot updating the grading thing
        holder.tv_model_mmt_grade_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //invisible the spinner and text section
                holder.sp_set_goal.setVisibility(View.INVISIBLE);
                holder.ll_tv_section.setVisibility(View.INVISIBLE);
                holder.rl_mmt_session.setVisibility(View.VISIBLE);

            }
        });

        holder.tv_model_mmt_grade_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.sp_set_goal.setVisibility(View.INVISIBLE);
                holder.ll_tv_section.setVisibility(View.INVISIBLE);
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
                    holder.sp_set_goal.setVisibility(View.INVISIBLE);
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

        holder.ll_tv_plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int num = Integer.parseInt(holder.tv_selected_goal_text.getText().toString().substring(0,2).trim());
                if(num<25){
                    num+=5;
                }

                holder.tv_selected_goal_text.setText(num+" Reps");
            }
        });

        holder.ll_tv_minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int num = Integer.parseInt(holder.tv_selected_goal_text.getText().toString().substring(0,2).trim());
                if(num>0){
                    num-=5;
                }

                holder.tv_selected_goal_text.setText(num+" Reps");
            }
        });

    }

    @Override
    public int getItemCount() {
        return bodyPartsList==null?0:bodyPartsList.size();
    }
}
