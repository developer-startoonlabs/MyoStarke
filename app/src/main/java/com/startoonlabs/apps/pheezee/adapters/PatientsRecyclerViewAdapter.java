package com.startoonlabs.apps.pheezee.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.startoonlabs.apps.pheezee.R;
import com.startoonlabs.apps.pheezee.retrofit.RetrofitClientInstance;
import com.startoonlabs.apps.pheezee.room.Entity.PhizioPatients;
import com.startoonlabs.apps.pheezee.utils.DateOperations;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import com.startoonlabs.apps.pheezee.pojos.PatientStatusData;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.startoonlabs.apps.pheezee.retrofit.GetDataService;
import android.util.Log;
/**
 * Home patient list recycler view adapter
 */
public class PatientsRecyclerViewAdapter extends RecyclerView.Adapter<PatientsRecyclerViewAdapter.ViewHolder> {

    private List<PhizioPatients> patientsListData;
    private List<PhizioPatients> updatedPatientList;
    private Context context;
    private JSONObject object;
    private SharedPreferences preferences;
    private String str_phizioemail;
    private onItemClickListner listner;
    GetDataService getDataService;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView patientName, patientId,patientNameContainer,tv_date_of_join;
        private ImageView patientProfilepic;
        private TextView iv_tripple_dot_red;
        private LinearLayout ll_option_patient_list;
        private Button btn_start_session;

        ViewHolder(View view) {
            super(view);
            patientName =   view.findViewById(R.id.patientName);
            patientId   =   view.findViewById(R.id.patientId);
            tv_date_of_join = view.findViewById(R.id.tv_patient_joindate_section_pv);
            patientProfilepic = view.findViewById(R.id.patientProfilePic);
            ll_option_patient_list = view.findViewById(R.id.options_popup_window);
            patientNameContainer  = view.findViewById(R.id.iv_patient_name_container);
            btn_start_session = view.findViewById(R.id.btn_start_session);
            iv_tripple_dot_red = view.findViewById(R.id.family_hub_tv_count);

        }

    }

    public PatientsRecyclerViewAdapter( Context context){
        this.context = context;
        preferences = PreferenceManager.getDefaultSharedPreferences(this.context);
        getDataService = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        try {
            object = new JSONObject(preferences.getString("phiziodetails",""));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            if(object!=null) {
                if (object.has("phizioemail")) {
                    str_phizioemail = object.getString("phizioemail");
                    str_phizioemail.replace("@", "%40");
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setNotes(List<PhizioPatients> notes){
        this.updatedPatientList = notes;
        this.patientsListData = notes;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PatientsRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // create a new view
        final View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.patient_layout, parent, false);
        return new PatientsRecyclerViewAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        PhizioPatients patientsList = updatedPatientList.get(position);
         String[] res_string = new String[1];
        // Haaris
        PatientStatusData data = new PatientStatusData(str_phizioemail, patientsList.getPatientid(),patientsList.getPatientname());
        Call<String> getHeldon_respone = getDataService.getHeldon(data);
        getHeldon_respone.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.code() == 200) {
                    res_string[0] = response.body();
                    if (res_string[0] != null) {
                        if(res_string[0].length()>5) {
                            String test = res_string[0].substring(1, 11);
                            test=test.replace("-","/");
                            String[] date_split = test.split("/");
                            test = date_split[2]+"/"+date_split[1]+"/"+date_split[0];
                            test = DateOperations.getDateInMonthAndDate(test);
                            date_split = test.split(",");
                            test = date_split[0];
                            holder.tv_date_of_join.setText("Last Session: " + test);

                        }else holder.tv_date_of_join.setText("Last Session: " + res_string[0].substring(1,2));


                        holder.patientName.setText(patientsList.getPatientname());
                        holder.patientId.setText("Id :"+patientsList.getPatientid());
                        holder.patientNameContainer.setVisibility(View.GONE);
                        holder.patientProfilepic.setImageResource(android.R.color.transparent);
                        if(patientsList.isSceduled()){
                            holder.iv_tripple_dot_red.setVisibility(View.VISIBLE);
                        }else {
                            holder.iv_tripple_dot_red.setVisibility(View.GONE);
                        }
                        //listners
                        holder.ll_option_patient_list.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                PhizioPatients temp = updatedPatientList.get(position);
                                PhizioPatients patients = new PhizioPatients(temp.getPatientid(),temp.getPatientname(),temp.getNumofsessions(),temp.getDateofjoin(),
                                        temp.getPatientage(),temp.getPatientgender(),temp.getPatientcasedes(),temp.getStatus(),temp.getPatientphone(),temp.getPatientprofilepicurl(), temp.isSceduled());
                                if(listner!=null  && position != RecyclerView.NO_POSITION)
                                    listner.onItemClick(patients, v);
                            }
                        });
                        holder.btn_start_session.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if(listner!=null  && position != RecyclerView.NO_POSITION)
                                    listner.onStartSessionClickListner(updatedPatientList.get(position));
                            }
                        });

                        String patientUrl = patientsList.getPatientprofilepicurl();
                        if(!patientUrl.trim().toLowerCase().equals("empty")) {
                            Glide.with(context)
                                    .load("https://s3.ap-south-1.amazonaws.com/pheezee/physiotherapist/" + str_phizioemail.replaceFirst("@", "%40") + "/patients/" + patientsList.getPatientid() + "/images/profilepic.png")
                                    .apply(new RequestOptions()
                                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                                            .skipMemoryCache(true))
                                    .into(holder.patientProfilepic);


                        }
                        else {
                            holder.patientNameContainer.setVisibility(View.VISIBLE);
                            if(holder.patientName.getText().length()<2 || holder.patientName.getText().length()==2)
                                holder.patientNameContainer.setText(holder.patientName.getText().toString().toUpperCase());
                            else{
                                holder.patientNameContainer.setText(holder.patientName.getText().toString().substring(0,2).toUpperCase());
                            }

                        }
                        res_string[0] = "-";
                    } else {
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {

            }
        });
        holder.patientName.setText(patientsList.getPatientname());
        holder.patientId.setText("Id :"+patientsList.getPatientid());
        holder.patientNameContainer.setVisibility(View.GONE);
        holder.patientProfilepic.setImageResource(android.R.color.transparent);
        if(patientsList.isSceduled()){
            holder.iv_tripple_dot_red.setVisibility(View.VISIBLE);
        }else {
            holder.iv_tripple_dot_red.setVisibility(View.GONE);
        }
        //listners
        holder.ll_option_patient_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PhizioPatients temp = updatedPatientList.get(position);
                PhizioPatients patients = new PhizioPatients(temp.getPatientid(),temp.getPatientname(),temp.getNumofsessions(),temp.getDateofjoin(),
                        temp.getPatientage(),temp.getPatientgender(),temp.getPatientcasedes(),temp.getStatus(),temp.getPatientphone(),temp.getPatientprofilepicurl(), temp.isSceduled());
                if(listner!=null  && position != RecyclerView.NO_POSITION)
                    listner.onItemClick(patients, v);
            }
        });
        holder.btn_start_session.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listner!=null  && position != RecyclerView.NO_POSITION)
                    listner.onStartSessionClickListner(updatedPatientList.get(position));
            }
        });

        String patientUrl = patientsList.getPatientprofilepicurl();
        if(!patientUrl.trim().toLowerCase().equals("empty")) {
            Glide.with(context)
                    .load("https://s3.ap-south-1.amazonaws.com/pheezee/physiotherapist/" + str_phizioemail.replaceFirst("@", "%40") + "/patients/" + patientsList.getPatientid() + "/images/profilepic.png")
                    .apply(new RequestOptions()
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true))
                    .into(holder.patientProfilepic);


        }
        else {
            holder.patientNameContainer.setVisibility(View.VISIBLE);
            if(holder.patientName.getText().length()<2 || holder.patientName.getText().length()==2)
                holder.patientNameContainer.setText(holder.patientName.getText().toString().toUpperCase());
            else{
                holder.patientNameContainer.setText(holder.patientName.getText().toString().substring(0,2).toUpperCase());
            }

        }



        // - get element from your dataset at this position
        // - replace the contents of the view with that element


    }


    public Filter getFilter(){
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String patientString = constraint.toString();
                if(patientString.isEmpty()){
                    updatedPatientList = patientsListData;
                }else {
                    List<PhizioPatients> filterList = new ArrayList<>();
                    for(PhizioPatients patientsList: patientsListData){
                        if(patientsList.getPatientname().toLowerCase().contains(patientString)){
                            filterList.add(patientsList);
                        }
                    }
                    updatedPatientList =filterList;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = updatedPatientList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                updatedPatientList = (ArrayList<PhizioPatients>)results.values;
                notifyDataSetChanged();
            }
        };
    }
    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return updatedPatientList==null?0:updatedPatientList.size();
    }

    public interface onItemClickListner{
        void onItemClick(PhizioPatients patient, View view);
        void onStartSessionClickListner(PhizioPatients patient);
    }

    public void setOnItemClickListner(onItemClickListner listner){
        this.listner = listner;
    }


    class PostDiffCallback extends DiffUtil.Callback {

        private final List<PhizioPatients> oldPosts, newPosts;

        public PostDiffCallback(List<PhizioPatients> oldPosts, List<PhizioPatients> newPosts) {
            this.oldPosts = oldPosts;
            this.newPosts = newPosts;
        }

        @Override
        public int getOldListSize() {
            return oldPosts.size();
        }

        @Override
        public int getNewListSize() {
            return newPosts.size();
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            return oldPosts.get(oldItemPosition).getPatientid().equals(newPosts.get(newItemPosition).getPatientid());
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            return (oldPosts.get(oldItemPosition).isSceduled()==newPosts.get(newItemPosition).isSceduled()) &&
                    (oldPosts.get(oldItemPosition).getDateofjoin().equals(newPosts.get(newItemPosition).getDateofjoin())) &&
                    (oldPosts.get(oldItemPosition).getPatientcasedes().equals(newPosts.get(newItemPosition).getPatientcasedes())) &&
                    (oldPosts.get(oldItemPosition).getPatientage().equals(newPosts.get(newItemPosition).getPatientage())) &&
                    (oldPosts.get(oldItemPosition).getPatientname().equals(newPosts.get(newItemPosition).getPatientname())) &&
                    (oldPosts.get(oldItemPosition).getPatientgender().equals(newPosts.get(newItemPosition).getPatientgender()));
        }

        @Override
        public Object getChangePayload(int oldItemPosition, int newItemPosition) {
            // Implement method if you're going to use ItemAnimator
            return super.getChangePayload(oldItemPosition, newItemPosition);
        }
    }
}