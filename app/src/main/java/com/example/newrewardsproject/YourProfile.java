package com.example.newrewardsproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.example.newrewardsproject.recycler.Employee;
import com.example.newrewardsproject.recycler.RewardNote;
import com.example.newrewardsproject.recycler.rewardAdapter;
import com.example.newrewardsproject.volley.DeleteProfileVolley;
import com.example.newrewardsproject.volley.GetProfileInfoVolley;

import java.util.ArrayList;

public class YourProfile extends AppCompatActivity implements View.OnLongClickListener, View.OnClickListener {
    private ImageView profile;
    private TextView profileName;
    private TextView userNameText;
    private TextView locationText;
    private TextView pointsAwardedText;
    private TextView deptText;
    private TextView positionText;
    private TextView pointsToAwardText;
    private TextView storyText;
    private TextView rewardCount;
    private static String username;
    private static String password;
    private static String apiKey;
    private static String pointsToAward;
    private static String sendLocation;
    private static String concatString;
    private static String fullName;
    private static Employee employee;
    private RecyclerView recyclerView;
    //private final ArrayList<RewardNote> rewardNotes = new ArrayList<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_your_profile);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setTitle("Your Profile");
        actionBar.setIcon(R.drawable.icon);

        if(getIntent().hasExtra("username")){
            username = getIntent().getStringExtra("username");
            password = getIntent().getStringExtra("password");
            apiKey = getIntent().getStringExtra("api");
            GetProfileInfoVolley.getProfile(this, username, password, apiKey); }

        if(getIntent().hasExtra("viewUsername")){
            username = getIntent().getStringExtra("viewUsername");
            password = getIntent().getStringExtra("viewPassword");
            apiKey = getIntent().getStringExtra("viewApi");
            GetProfileInfoVolley.getProfile(this, username, password, apiKey); }


        profile = findViewById(R.id.newProfileImage);
        //will have to reverse decode the base 64 string to get this image again
        rewardCount = findViewById(R.id.rewardHistory);
        profileName = findViewById(R.id.profileName);
        userNameText = findViewById(R.id.italName);
        locationText = findViewById(R.id.profileLocation);
        pointsAwardedText = findViewById(R.id.points);
        deptText = findViewById(R.id.deptName);
        positionText = findViewById(R.id.roleName);
        pointsToAwardText = findViewById(R.id.pointsToAwardAmount);
        storyText = findViewById(R.id.profileStory);

        recyclerView = findViewById(R.id.recycler);
        recyclerView.setOnLongClickListener(this);


        /*for (int i = 0; i < 3; i++) {
            rewardNotes.add(new RewardNote("foo", "barr", "int", "String"));
        }*/

    }

    public void getData(String firstName, String lastNane, String location, String dept, String position,
                        String points, String story, String imageBytes, ArrayList<RewardNote> rewardNotes) {

        employee = new Employee(firstName, lastNane, username, dept, story, position, imageBytes, points);
        sendLocation = location;
        profileName.setText(lastNane + ", " + firstName);
        fullName = firstName + " " + lastNane;
        locationText.setText(location);
        userNameText.setText("(" + username + ")");
        deptText.setText(dept);
        positionText.setText(position);
        concatString = firstName.concat(lastNane).concat(dept).concat(position).concat(username);
        int count = 0;
        for(RewardNote rewardNote : rewardNotes){
            String stringAmount = rewardNote.getAmount();
            int amount = Integer.parseInt(stringAmount);
            count += amount;
        }

        pointsAwardedText.setText(String.valueOf(count));
        pointsToAwardText.setText(points);
        pointsToAward = points;
        storyText.setText(story);
        rewardCount.setText("Reward History (" + String.valueOf(rewardNotes.size()) + "):");

        processImage(imageBytes);
        rewardAdapter rewardAdapter = new rewardAdapter(rewardNotes, this);

        recyclerView.setAdapter(rewardAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.crud_profile_menu, menu);

        return true;
    }

    public void exitApp () {
        moveTaskToBack(true);
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getTitle().equals("delete"))
        {
            //if we click delete we are just removing the profile and do not have to actually open up a activity
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setIcon(R.drawable.logo);
            builder.setTitle("Delete Profile?");
            builder.setMessage("Delete Profile for " + fullName + "? (The Rewards app will be closed upon deletion)");
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            deleteProfile();

                        }
                    });

            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    Toast.makeText(YourProfile.this, "You selected cancel", Toast.LENGTH_SHORT).show();
                    //code to cancel out goes here
                }
            });


            AlertDialog dialog = builder.create();
            dialog.show();



        }

        if(item.getTitle().equals("edit")){
            //clicking on this item should prompt the edit activity top be opened up. We have to send it a reference of
            //all of the data fields in order for it to work
            Intent editIntent = new Intent(this, EditProfile.class);
            editIntent.putExtra("username", username);
            editIntent.putExtra("password", password);
            editIntent.putExtra("api", apiKey);
            editIntent.putExtra("location", locationText.getText().toString());
            startActivity(editIntent);
        }

        if(item.getTitle().equals("view")) {
            //if we click the view button we should be able to view all of the entries. For this we will need a recycer view
            //utilizes the /Profile/GetAllProfiles call in order to get this. Hitting the intent does not need to send any reference.
            //we just have to make the api call and get the layout to display properly
            Intent viewIntent = new Intent(this, ViewProfiles.class);
            viewIntent.putExtra("api", apiKey);
            viewIntent.putExtra("user", username);
            viewIntent.putExtra("points", pointsToAward);
            viewIntent.putExtra("password", password);
            viewIntent.putExtra("Employee", employee);
            viewIntent.putExtra("Location", sendLocation);
            viewIntent.putExtra("UserID", concatString);
            startActivity(viewIntent);

        }
        return true;
    }



    public void deleteProfile () {
        DeleteProfileVolley.deleteProfile(this, username,apiKey);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        exitApp();


    }

    public void processImage(String imageBytesString) {
        byte[] imageBytes = Base64.decode(imageBytesString, Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        profile.setImageBitmap(bitmap);

    }

    @Override
    public boolean onLongClick(View view) {
        return false;
    }

    @Override
    public void onClick(View view) {

    }
}