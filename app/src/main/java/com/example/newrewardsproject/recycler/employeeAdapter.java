package com.example.newrewardsproject.recycler;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.newrewardsproject.R;
import com.example.newrewardsproject.ViewProfiles;

import java.util.ArrayList;

public class employeeAdapter extends RecyclerView.Adapter<MyViewHolder>{
    private ArrayList<Employee> employees;
    private final ViewProfiles viewProfiles;
    private String concatString;
    private int positionFrom;

    public employeeAdapter(ArrayList<Employee> employees, ViewProfiles viewProfiles, String concatString, int positionFrom) {
        this.employees = employees;
        this.viewProfiles = viewProfiles;
        this.concatString = concatString;
        this.positionFrom = positionFrom;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.employee_list_entry, parent, false);
        itemView.setOnClickListener(viewProfiles);
        itemView.setOnLongClickListener(viewProfiles);


        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Employee employee = employees.get(position);
        holder.name.setText(employee.getlName() + ", " + employee.getfName());
        holder.deptPos.setText(employee.getPosition() + ", " + employee.getDept());
        Bitmap bitmap = processImage(employee.getImageBytes());
        holder.image.setImageBitmap(bitmap);
        holder.points.setText(employee.getPoints());

        if(employee.getfName().concat(employee.getlName()).concat(employee.getDept()).concat(employee.getPosition())
                .concat(employee.getUserName()).equals(concatString) && position==positionFrom)
            {
            holder.name.setTextColor(0xFFF06D2F);
            holder.deptPos.setTextColor(0xFFF06D2F);
            holder.points.setTextColor(0xFFF06D2F);
            }
        else{
            holder.name.setTextColor(Color.parseColor("#000000"));
            holder.deptPos.setTextColor(Color.parseColor("#000000"));
            holder.points.setTextColor(Color.parseColor("#000000"));
        }

    }



    @Override
    public int getItemCount() {
        return employees.size();
    }

    public Bitmap processImage(String imageBytesString) {
        byte[] imageBytes = Base64.decode(imageBytesString, Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        return bitmap;

    }
}
