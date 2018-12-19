package com.example.sai.pheezeeapp.patientsRecyclerView;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.sai.pheezeeapp.R;

import java.util.List;

public class RecyclerViewSessionListAdapter extends RecyclerView.Adapter<RecyclerViewSessionListAdapter.ViewHolder> {

    private List<SessionListData> sessionListData;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView tv_heldon, patientId;
        ViewHolder(View view) {
            super(view);
            tv_heldon =   view.findViewById(R.id.tv_session_heldon);
            //patientId   =   view.findViewById(R.id.patientId);
        }
    }

    public RecyclerViewSessionListAdapter(List<SessionListData> sessionListData){
        this.sessionListData = sessionListData;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        final View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.session_list, viewGroup, false);
        return new RecyclerViewSessionListAdapter.ViewHolder(itemView);
    }

    public void onBindViewHolder(@NonNull RecyclerViewSessionListAdapter.ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        SessionListData listData = sessionListData.get(position);
        holder.tv_heldon.setText(listData.getHeldOn());
        //holder.patientId.setText("Patient Id : "+patientsList.getPatientId());
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return sessionListData==null?0:sessionListData.size();
    }
}
