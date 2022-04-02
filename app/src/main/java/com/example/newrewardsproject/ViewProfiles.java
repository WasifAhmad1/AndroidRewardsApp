package com.example.newrewardsproject;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.newrewardsproject.recycler.Employee;
import com.example.newrewardsproject.recycler.employeeAdapter;
import com.example.newrewardsproject.volley.ViewProfilesVolley;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Set;

public class ViewProfiles extends AppCompatActivity implements View.OnLongClickListener, View.OnClickListener {
    private static RecyclerView recyclerView;
    private static String getApi;
    private static ArrayList<Employee> employeeList;
    private static String pointsToAward;
    private static String username;
    private static String password;
    private static String location;
    private static Employee origEmployee;
    private static String concat;
    private static int position;
    private static String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profiles);
        ActionBar actionBar = getSupportActionBar();
        origEmployee = (Employee) getIntent().getSerializableExtra("Employee");
        password = getIntent().getStringExtra("password");

        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setTitle("Leaderboard");
        actionBar.setIcon(R.drawable.icon);
        recyclerView = findViewById(R.id.recyclerEmployee);
        recyclerView.setOnLongClickListener(this);
        if(getIntent().hasExtra("api")) {
            getApi = getIntent().getStringExtra("api");
            username = getIntent().getStringExtra("user");
            pointsToAward = getIntent().getStringExtra("points");
            concat = getIntent().getStringExtra("UserID");
            password = getIntent().getStringExtra("password");
            token = "fromProfile";
            ViewProfilesVolley.getProfiles(this, getApi);

        }
        if(getIntent().hasExtra("listApi")) {
            getApi = getIntent().getStringExtra("listApi");
            username = getIntent().getStringExtra("listUser");
            pointsToAward = getIntent().getStringExtra("listPoints");
            password = getIntent().getStringExtra("password");
            token = "fromSendPoints";
            ViewProfilesVolley.getProfiles(this, getApi);
        }

        //location = getIntent().getStringExtra("Location");

    //now we populate the list with the data

        //employeeAdapter employeeAdapter = new employeeAdapter(
        //employeeAdapter employeeAdapter = new employeeAdapter(

    }

    @Override
    public boolean onLongClick(View view) {
        return false;
    }

    @Override
    public void onBackPressed()
    {
         Intent yourProfile= new Intent(this, YourProfile.class);
         yourProfile.putExtra("viewApi", getApi);
         yourProfile.putExtra("viewUsername", username);
         yourProfile.putExtra("viewPassword", password);
         startActivity(yourProfile);

    }

    @Override
    public void onClick(View view) {
        int pos = recyclerView.getChildLayoutPosition(view);
        Employee employee = employeeList.get(pos);
        if
            (employee.getUserName().equals(username)){
            //must fix this
            /*Intent intent = new Intent(this, YourProfile.class);
            intent.putExtra("ListApi", getApi);
            intent.putExtra("ListUser", username);
            intent.putExtra("ListPword", password);
            startActivity(intent); */

            finish();
        }
        else{
            if(token.equals("fromProfile")) {
            Intent intent = new Intent(this, SendPoints.class);
            intent.putExtra("Employee", employee);
            intent.putExtra("Points", pointsToAward);
            intent.putExtra("DonorEmployee", origEmployee);
            intent.putExtra("password", password);
            intent.putExtra("Api", getApi);
            startActivity(intent); }

            else if(token.equals("fromSendPoints")){
                Intent intent = new Intent(this, SendPoints.class);
                intent.putExtra("Employee", employee);
                intent.putExtra("Points", pointsToAward);
                intent.putExtra("DonorEmployee", origEmployee);
                intent.putExtra("password", password);
                intent.putExtra("Api", getApi);
                startActivity(intent);
            }
        }

    }

    public void handleList(ArrayList<Employee> employees) {
        if(employees.size()>1) {
            Collections.sort(employees, (new Comparator<Employee>() {
                public int compare(Employee i1, Employee i2) {
                    return i2.getPointInt() - i1.getPointInt();
                }
            }));
        }



        employeeList = new ArrayList<Employee>();
        for (Employee emp : employees) {
            if(!employeeList.contains(emp)){
                employeeList.add(emp);
            }
        }
        Set<Employee> set = new LinkedHashSet<>();
        set.addAll(employeeList);
        employeeList.clear();
        employeeList.addAll(set);

        for(Employee emp : employees){
            if (emp.getUserName().equals(username))
                position = employees.indexOf(emp);
        }

        employeeAdapter employeeAdapter = new employeeAdapter(employees,this, concat, position);
        recyclerView.setAdapter(employeeAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        //Important your profile must be highlighted!
    }
}