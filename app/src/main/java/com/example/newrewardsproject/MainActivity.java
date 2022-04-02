package com.example.newrewardsproject;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.content.SharedPreferences.Editor;


import com.example.newrewardsproject.volley.GetApiKeyVolley;
import com.example.newrewardsproject.volley.LoginAPIVolley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private static String fName;
    private static String lName;
    private static String email;
    private static String sId;
    private static String getApi;
    private static String getLogin;
    private static String getPassword;
    private SharedPreferences prefs;
    private SharedPreferences loginPref;
    private CheckBox box;
    private static String key = "DATA1_KEY";
    private static String loginKey = "LOGIN_KEY";
    private static String passwordKey = "PASSWORD_KEY";
    private FusedLocationProviderClient mFusedLocationClient;
    private static final int LOCATION_REQUEST = 111;
    private static String locationString = "Unspecified Location";






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setTitle("Rewards");
        actionBar.setIcon(R.drawable.icon);
        prefs = this.getSharedPreferences("MY_PREFS_KEY", Context.MODE_PRIVATE);
        loginPref = this.getSharedPreferences("MY_LOGIN_KEY", Context.MODE_PRIVATE);
        getApi = prefs.getString(key, "");
        getLogin = loginPref.getString(loginKey, "");
        getPassword = loginPref.getString(passwordKey, "");

        EditText username = findViewById(R.id.enterUserName);
        EditText password = findViewById(R.id.enterPassword);

        username.setText(getLogin);
        password.setText(getPassword);

        box = (CheckBox)findViewById(R.id.checkBox);
        mFusedLocationClient =
                LocationServices.getFusedLocationProviderClient(this);
        determineLocation();

        if(getApi.equals("")) {
            popupMenu();
        }


    }

    private void determineLocation () {
        if (checkPermission()) {
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location2) {
                            // Got last known location. In some situations this can be null.

                                locationString = MainActivity.this.getPlace(location2);
                                System.out.println("Break");
                        }
                    })
                    .addOnFailureListener(this, e -> Toast.makeText(MainActivity.this,
                            e.getMessage(), Toast.LENGTH_LONG).show());
        }


    }

    private boolean checkPermission () {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION
                    }, LOCATION_REQUEST);
            return false;
        }
        return true;

    }

    private String getPlace(Location loc) {

            StringBuilder sb = new StringBuilder();

            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses;

            try {
                addresses = geocoder.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);
                String city = addresses.get(0).getLocality();
                String state = addresses.get(0).getAdminArea();

                sb.append(String.format(
                        Locale.getDefault(),
                        "%s, %s ",
                        city, state));
            } catch (IOException e) {
                e.printStackTrace();
            }
            return sb.toString();


    }



    public void createProfile (View v) {
        if(!getApi.equals("")){
        Intent intent = new Intent(this, CreateProfile.class);
        intent.putExtra("location", locationString);
        intent.putExtra("api", getApi);
        startActivity(intent); }
        else{
            Toast.makeText(MainActivity.this, "You must create an API key first!", Toast.LENGTH_LONG).show();
        }


    }

    public void clearAPI (View v) {
    //function to clear API key and re-prompt entering information to generate a new API key

        Editor editor = prefs.edit();
        editor.remove(key);
        editor.apply();
        getApi = "";
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.logo);
        builder.setTitle("Remove Student API Key?");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                popupMenu();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();




    }

    public void login(View v) {
        //here we will login to get the created profile. We supply a user name and password and then based on the get response we will
        //allowed to login and the get the proper profile. We have to open the YourProfile activity in this instance when we successfully
        //login. We need references to the Edit Text boxes from the login and password
      EditText userName = findViewById(R.id.enterUserName);
      EditText password = findViewById(R.id.enterPassword);
      String user = String.valueOf(userName.getText());
      String pass = String.valueOf(password.getText());

      //Based on whether we get a true response or a false response we will either open the profile intent or post a toast message
        LoginAPIVolley.tryLogin(this, getApi, user, pass);
        //Toast.makeText(MainActivity.this, "Wrong username or password!", Toast.LENGTH_LONG).show();


    }

    public void handleCreateOrLoginSucceeded (String login, String password) {
        Intent intent = new Intent(this, YourProfile.class);
        intent.putExtra("username", login);
        intent.putExtra("password", password);
        intent.putExtra("api",getApi);

        if(box.isChecked()){
            loginPref = this.getSharedPreferences("MY_LOGIN_KEY", Context.MODE_PRIVATE);
            Editor loginEditor = loginPref.edit();
            loginEditor.putString(loginKey, login);
            loginEditor.putString(passwordKey, password);
            loginEditor.apply();
        }
        else{
            loginPref = this.getSharedPreferences("MY_LOGIN_KEY", Context.MODE_PRIVATE);
            Editor loginEditor = loginPref.edit();
            loginEditor.putString(loginKey, "");
            loginEditor.putString(passwordKey, "");
            loginEditor.apply();
        }

        //need to save the username and pssword infor
        startActivity(intent);
    }

    public void handleLoginFailure (String errorMsg) {
            System.out.println(errorMsg);
        Toast.makeText(MainActivity.this, "Wrong username or password!", Toast.LENGTH_LONG).show();

    }


    public void popupMenu() {
        LayoutInflater inflater = LayoutInflater.from(this);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        @SuppressLint("InflateParams") final View view = inflater.inflate(R.layout.api_key_generator, null);
        builder.setView(view);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

            }
        });




        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Toast.makeText(MainActivity.this, "You selected cancel", Toast.LENGTH_SHORT).show();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();


        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TextView title = view.findViewById(R.id.titleApi);
                EditText efName = view.findViewById(R.id.enterFName);
                EditText elName = view.findViewById(R.id.enterLName);
                EditText eEmail = view.findViewById(R.id.enterEmail);
                EditText eID = view.findViewById(R.id.enterID);
                fName = String.valueOf(efName.getText());
                lName = String.valueOf(elName.getText());
                email = String.valueOf(eEmail.getText());
                sId = String.valueOf(eID.getText());

                //handle error cases: if no text is entered, if the email does not contain edu in the end and if it does not have a @ symbol
                //the number has more than 20 numbers
                if("".equals(efName.getText().toString().trim()) || "".equals(elName.getText().toString().trim())
                        || "".equals(eEmail.getText().toString().trim()) || "".equals(eID.getText().toString().trim())){
                    Toast.makeText(MainActivity.this, "One of the fields is missing", Toast.LENGTH_SHORT).show();
                    return;
                }

                if((efName.getText().toString().trim() == null) || (elName.getText().toString().trim() == null)
                        || (eEmail.getText().toString().trim() == null) || (eID.getText().toString().trim() == null)){
                    Toast.makeText(MainActivity.this, "One of the fields is missing", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(eID.getText().toString().trim().length()>20) {
                    Toast.makeText(MainActivity.this, "The ID you entered is too long", Toast.LENGTH_SHORT).show();
                    return;
                }

                int length = eEmail.getText().toString().length();

                if(!eEmail.getText().toString().trim().substring(length-3, length).equals("edu")){
                    Toast.makeText(MainActivity.this, "Your email must end with edu", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(!eEmail.getText().toString().trim().substring(length-4, length).equals(".edu")){
                    Toast.makeText(MainActivity.this, "Your email must end with .edu", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(!eEmail.getText().toString().trim().contains("@")){
                    Toast.makeText(MainActivity.this, "Invalid email", Toast.LENGTH_SHORT).show();
                    return;
                }


                GetApiKeyVolley.getApiKey(MainActivity.this ,fName, lName, email, sId);
                dialog.dismiss();

            }
        });

    }

    public void handleKey (String s) {
        getApi = s;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.logo);
        builder.setTitle("API Key Received and Stored");
        builder.setMessage("Name: " + fName + " " + lName + "\n" + "Student ID: " +  sId + "\n" + "Email: "
                + email + "\n" + "API Key " + s);
        //builder.setMessage("API Key " + s);
        prefs = this.getSharedPreferences("MY_PREFS_KEY", Context.MODE_PRIVATE);
        Editor editor = prefs.edit();
        editor.putString(key ,s);
        editor.apply();
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void handleError(String s) {
       //we need a HTTP_BAD_REQUEST error here
    }
}