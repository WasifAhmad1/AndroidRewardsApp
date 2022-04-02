package com.example.newrewardsproject.recycler;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.newrewardsproject.R;

public class MyViewHolder extends RecyclerView.ViewHolder {
    ImageView image;
    TextView name;
    TextView deptPos;
    TextView points;

    public MyViewHolder(@NonNull View itemView) {
        super(itemView);
        image = itemView.findViewById(R.id.employeeImage);
        name = itemView.findViewById(R.id.employeeName);
        deptPos = itemView.findViewById(R.id.empPosDept);
        points = itemView.findViewById(R.id.empPoints);
    }
}
