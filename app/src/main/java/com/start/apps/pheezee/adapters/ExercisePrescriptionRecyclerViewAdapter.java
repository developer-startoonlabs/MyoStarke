package com.start.apps.pheezee.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import start.apps.pheezee.R;
import com.start.apps.pheezee.room.Entity.SceduledSession;
import com.start.apps.pheezee.utils.ValueBasedColorOperations;

import java.util.List;

public class ExercisePrescriptionRecyclerViewAdapter extends RecyclerView.Adapter<ExercisePrescriptionRecyclerViewAdapter.ViewHolder> {
    private Context context;
    private List<SceduledSession> sessions;
    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_body_part;
        TextView tv_session_description, tv_muscle_name;
        ViewHolder(View view) {
            super(view);
            iv_body_part = view.findViewById(R.id.iv_body_part);
            tv_session_description = view.findViewById(R.id.tv_exercise_description);
            tv_muscle_name = view.findViewById(R.id.tv_muscle_name);
        }
    }

    public ExercisePrescriptionRecyclerViewAdapter( Context context){
        this.context = context;
    }

    public void setNotes(List<SceduledSession> notes){
//        if (sessions != null) {
//            ExercisePrescriptionRecyclerViewAdapter.PostDiffCallback postDiffCallback = new ExercisePrescriptionRecyclerViewAdapter.PostDiffCallback(sessions, notes);
//            DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(postDiffCallback);
//            sessions.clear();
//            sessions.addAll(notes);
//            diffResult.dispatchUpdatesTo(this);
//        } else {
//            // first initialization
//            sessions = notes;
//        }
        this.sessions = notes;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ExercisePrescriptionRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // create a new view
        final View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.model_recycler_exercise_prescription, parent, false);
        return new ExercisePrescriptionRecyclerViewAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ExercisePrescriptionRecyclerViewAdapter.ViewHolder holder, int position) {
        SceduledSession session = sessions.get(position);
        holder.iv_body_part.setImageResource(ValueBasedColorOperations.getDrawableBasedOnBodyPart(session.getBodypart()));
        holder.tv_session_description.setText(session.getSide().substring(0,1).toUpperCase().concat(". ").concat(session.getBodypart()).concat(" ").concat(session.getExercise()));
        holder.tv_muscle_name.setText("Muscle: ".concat(session.getMuscle()));
    }

    @Override
    public int getItemCount() {
        return sessions==null?0:sessions.size();
    }

    class PostDiffCallback extends DiffUtil.Callback {

        private final List<SceduledSession> oldPosts, newPosts;

        public PostDiffCallback(List<SceduledSession> oldPosts, List<SceduledSession> newPosts) {
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
            return oldPosts.get(oldItemPosition).getSessionno() == newPosts.get(newItemPosition).getSessionno();
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            return oldPosts.get(oldItemPosition).equals(newPosts.get(newItemPosition));
        }
    }

}
