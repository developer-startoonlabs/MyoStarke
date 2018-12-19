package com.example.sai.pheezeeapp.patientsRecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.sai.pheezeeapp.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class PatientsRecyclerViewAdapter extends RecyclerView.Adapter<PatientsRecyclerViewAdapter.ViewHolder> {

    private List<PatientsListData> patientsListData;
    Context context;
    JSONObject object;

    SharedPreferences preferences;
    String str_phizioemail;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView patientName, patientId;
        public ImageView patientProfilepic;
        ViewHolder(View view) {
            super(view);
            patientName =   view.findViewById(R.id.patientName);
            patientId   =   view.findViewById(R.id.patientId);
            patientProfilepic = view.findViewById(R.id.patientProfilePic);
        }
    }

    public PatientsRecyclerViewAdapter(List<PatientsListData> patientsListData, Context context){
        this.patientsListData = patientsListData;
        this.context = context;
        preferences = PreferenceManager.getDefaultSharedPreferences(this.context);
        try {
            object = new JSONObject(preferences.getString("phiziodetails",""));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            str_phizioemail = object.getString("phizioemail") ;
            Log.d("phizioemail",str_phizioemail);
            str_phizioemail.replace("@","%40");
            Log.i("phizioemail",str_phizioemail);
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        PatientsListData patientsList = patientsListData.get(position);
        holder.patientName.setText(patientsList.getPatientName());
        holder.patientId.setText("Patient Id : "+patientsList.getPatientId());

        String patientUrl = patientsList.getPatientUrl();

//        if(!patientUrl.equals("empty")) {
            Glide.with(context)
                    .load("https://s3.ap-south-1.amazonaws.com/pheezee/physiotherapist/" + str_phizioemail.replaceFirst("@", "%40") + "/patients/" + patientsList.getPatientId() + "/images/profilepic.png")
                    .apply(new RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true))
                    .into(holder.patientProfilepic);
        //}
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return patientsListData==null?0:patientsListData.size();
        //https://s3.ap-south-1.amazonaws.com/pheezee/physiotherapist/ankushsharma8210%40gmail.co/patients/xzy/images/profilepic.png
        //https://s3.ap-south-1.amazonaws.com/pheezee/physiotherapist/" + str_phizioemail.replaceFirst("@", "%40") + "/patients/" + patientsList.getPatientId() + "/images/profilepic.png
    }
}
