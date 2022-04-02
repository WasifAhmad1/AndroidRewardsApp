package com.example.newrewardsproject.recycler;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.newrewardsproject.R;
import com.example.newrewardsproject.YourProfile;

import java.util.ArrayList;

public class rewardAdapter extends RecyclerView.Adapter<MyRewardHolder>{
    private ArrayList<RewardNote> rewardNotes;
    private final YourProfile yourProfile;


    public rewardAdapter(ArrayList<RewardNote> rewardNotes, YourProfile yourProfile) {
        this.rewardNotes = rewardNotes;
        this.yourProfile = yourProfile;
    }


    @NonNull
    @Override
    public MyRewardHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.reward_list_entry, parent, false);

        itemView.setOnClickListener(yourProfile);
        itemView.setOnLongClickListener(yourProfile);

        return new MyRewardHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyRewardHolder holder, int position) {

        RewardNote rewardNote = rewardNotes.get(position);
        String [] tokens = rewardNote.getAwardDate().split("T");
        String newDate = tokens[0];
        String [] newToken = newDate.split("-");
        holder.date.setText(newToken[1] + "/" + newToken[2] + "/" + newToken[0]);

        holder.name.setText(rewardNote.getGiverName());
        holder.points.setText(rewardNote.getAmount());
        holder.desc.setText(rewardNote.getNote());
        /*Date date = null;
        Note note = noteList.get(position);
        holder.title.setText(note.getTitle());
        holder.desc.setText(note.getDesc()); */
        //holder.time.setText(note.getTimeStamp());

    }

    @Override
    public int getItemCount() {
        return rewardNotes.size();
    }
}
