package com.example.newrewardsproject.recycler;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.newrewardsproject.R;

public class MyRewardHolder extends RecyclerView.ViewHolder{

    TextView date;
    TextView name;
    TextView points;
    TextView desc;

    public MyRewardHolder(@NonNull View view) {

        super(view);
        date = view.findViewById(R.id.rewardDate);
        name = view.findViewById(R.id.rewardName);
        points = view.findViewById(R.id.rewardPoints);
        desc = view.findViewById(R.id.rewardDesc);
        /*title = view.findViewById(R.id.title);
        time = view.findViewById(R.id.time);
        desc = view.findViewById(R.id.desc); */




    }


}