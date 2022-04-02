package com.example.newrewardsproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.newrewardsproject.recycler.Employee;
import com.example.newrewardsproject.volley.DeleteProfileVolley;
import com.example.newrewardsproject.volley.PostProfileVolley;
import com.example.newrewardsproject.volley.SendPointsVolley;

public class SendPoints extends AppCompatActivity {
    private TextView pointsName;
    private TextView awardedPoints;
    private TextView dept;
    private TextView position;
    private TextView story;
    private TextView countPoints;
    private static EditText sendPoints;
    private EditText comment;
    private ImageView image;
    private static String points;
    private static Employee origEmployee;
    private static String username;
    private static String password;
    private static String api;
    private static String location;
    private static String reciverUser;
    private static final int MAX_LEN = 80;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_points);
        Employee employee =(Employee)getIntent().getSerializableExtra("Employee");
        reciverUser = employee.getUserName();
        origEmployee = (Employee) getIntent().getSerializableExtra("DonorEmployee");
        api = getIntent().getStringExtra("Api");
        location = getIntent().getStringExtra("Location");
        password = getIntent().getStringExtra("password");
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setTitle(employee.getfName() + " " + employee.getlName());
        actionBar.setIcon(R.drawable.icon);

        pointsName = findViewById(R.id.pointsName);
        awardedPoints = findViewById(R.id.currentPointsAwarded);
        dept = findViewById(R.id.pointsYourDept);
        position = findViewById(R.id.addPointsPosition);
        story = findViewById(R.id.yourStoryPoints);
        countPoints = findViewById(R.id.countPointsSend);

        pointsName.setText(employee.getlName() + ", " + employee.getfName());
        awardedPoints.setText(employee.getPoints());
        dept.setText(employee.getDept());
        position.setText(employee.getPosition());
        image = findViewById(R.id.pointsPicture);
        sendPoints = findViewById(R.id.editSendPoints);
        comment = findViewById(R.id.editPointsNote);
        comment.setFilters(new InputFilter[]{
                new InputFilter.LengthFilter(MAX_LEN) // Specifies a max text length
        });

        comment.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        int len = s.toString().length();
                        String countText = "Comment: (" + len + " of " + MAX_LEN + ")";
                        countPoints.setText(countText);
                    }
                });
        points = getIntent().getStringExtra("Points");
        processImage(employee.getImageBytes());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.save_profile_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        View v = item.getActionView();

        if (id == R.id.save) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setIcon(R.drawable.logo);
            builder.setTitle("Send Points?");
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    //Here we will have the code that will send the api call to send the points to the user.
                    //We need to make sure the amount is a positive integer value and that the note is not null or empty
                    //with a 8- character max
                    int digits = 11;
                    String pointsSent = sendPoints.getText().toString();
                    String commentText = comment.getText().toString();
                    if(Integer.parseInt(pointsSent) > Integer.parseInt(points) || Integer.parseInt(pointsSent) <= 0
                        || pointsSent.length()>digits) {
                        Toast.makeText(SendPoints.this, "Please enter a positive, less than 11 digits number that is less than your point total",
                                Toast.LENGTH_LONG).show(); }
                    if(commentText.equals("") || commentText.length()>80) {
                        Toast.makeText(SendPoints.this, "Please make sure your note is less than 80 characters",
                                Toast.LENGTH_LONG).show();
                    }



                    try {
                        makeCalls(pointsSent, commentText);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }


                    //Once we have a valid entry we can make the api call.



                }
            });

            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    Toast.makeText(SendPoints.this, "You selected cancel", Toast.LENGTH_SHORT).show();
                    //code to cancel out goes here
                }
            });


            AlertDialog dialog = builder.create();
            dialog.show();

        }
        return super.onOptionsItemSelected(item);
    }

    public void makeCalls(String sendPoints, String note) throws InterruptedException {
        SendPointsVolley.sendPoints(this, api, reciverUser, origEmployee.getUserName(), origEmployee.getfName()
                + " " + origEmployee.getlName(), sendPoints, note);
        Thread.sleep(2000);
        Intent intent = new Intent(this, ViewProfiles.class);
        intent.putExtra("listApi", api);
        String theUser = origEmployee.getUserName();
        intent.putExtra("listUser", theUser);
        intent.putExtra("listPoints", points);
        intent.putExtra("password", password);
        intent.putExtra("Employee", origEmployee);
        startActivity(intent);
       /* Thread.sleep(1000);
        DeleteProfileVolley.deleteProfilePoints(this, origEmployee.getUserName(), api);
        Thread.sleep(3000);
        PostProfileVolley.postProfilePoints(this, origEmployee.getfName(), origEmployee.getlName(), origEmployee.getUserName(),
                origEmployee.getDept(), origEmployee.getStory(), origEmployee.getPosition(), password,String.valueOf(Integer.valueOf(points) -
                Integer.valueOf(sendPoints)), location, api, origEmployee.getImageBytes()); */


        //need a reference to the array of values
    }

    public void processImage(String imageBytesString) {
        byte[] imageBytes = Base64.decode(imageBytesString, Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        image.setImageBitmap(bitmap);

    }
}