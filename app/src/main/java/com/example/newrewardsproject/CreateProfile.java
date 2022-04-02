package com.example.newrewardsproject;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.newrewardsproject.volley.PostProfileVolley;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class CreateProfile extends AppCompatActivity {

    private final String TAG = getClass().getSimpleName();
    private File currentImageFile;
//keep a reference to the current file we are accessing with this variable

    private ActivityResultLauncher<Intent> cameraActivityResultLauncher;
    //this launcher is used to launch the camera. It will take the shot and send back the uri.
    private ActivityResultLauncher<Intent> thumbActivityResultLauncher;
    //compresses the original image into a thumbnail size
    private ActivityResultLauncher<Intent> galleryActivityResultLauncher;

    private ImageView profile;
    private EditText editProfile;
    private static final int MAX_LEN = 360;
    private static String imageString64;
    TextView textCount;
    private static final int STORAGE_PERMISSION_CODE = 113;
    private static final int CAMERA_PERMISSION_CODE = 114;
    //private static final int

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setTitle("Create Profile");
        actionBar.setIcon(R.drawable.icon);
        profile = findViewById(R.id.profileImage);
        textCount = findViewById(R.id.textCount);
        editProfile = findViewById(R.id.editProfile);
        editProfile.setFilters(new InputFilter[]{
                new InputFilter.LengthFilter(MAX_LEN) // Specifies a max text length
        });

        editProfile.addTextChangedListener(
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
                        String countText = "Your Story: (" + len + " of " + MAX_LEN + ")";
                        textCount.setText(countText);
                    }
                });

        cameraActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                this::handleCameraResult);

        /*thumbActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                this::handleThumbResult); */

        galleryActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                this::handleGalleryResult);
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
            builder.setTitle("Save Changes?");
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    //We now will send the profile info to the next activity


                }
            });

            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    Toast.makeText(CreateProfile.this, "You selected cancel", Toast.LENGTH_SHORT).show();
                    //code to cancel out goes here
                }
            });


            AlertDialog dialog = builder.create();
            dialog.show();

            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    EditText userName = findViewById(R.id.userNameEntry);
                    EditText password = findViewById(R.id.pwordEntry);
                    EditText fName = findViewById(R.id.fNameEntry);
                    EditText lName = findViewById(R.id.lNameEntry);
                    EditText dept = findViewById(R.id.deptEntry);
                    EditText role = findViewById(R.id.roleEntry);
                    //We need to get the location information here too
                    String location = getIntent().getStringExtra("location");
                    String apiKey = getIntent().getStringExtra("api");

                    if (fName.getText().toString() == null || fName.getText().toString().length() > 20 ||
                            "".equals(fName.getText().toString().trim()) || lName.getText().toString() == null
                            || lName.getText().toString().length() > 20 ||
                            "".equals(lName.getText().toString().trim()) || userName.getText().toString() == null
                            || userName.getText().toString().length() > 20 ||
                            "".equals(userName.getText().toString().trim()) || dept.getText().toString() == null
                            || dept.getText().toString().length() > 30 ||
                            "".equals(dept.getText().toString().trim()) || editProfile.getText().toString() == null
                            || editProfile.getText().toString().length() > 360 ||
                            "".equals(editProfile.getText().toString().trim()) ||
                            role.getText().toString() == null || role.getText().toString().length() > 20 ||
                            "".equals(role.getText().toString().trim()) || password.getText().toString() == null
                            || password.getText().toString().length() > 40 || password.getText().toString().length() < 8 ||
                            "".equals(fName.getText().toString().trim()) || "".equals(imageString64) || imageString64.length() > 100000) {
                        Toast.makeText(CreateProfile.this, "One of the fields is missing or incorrect", Toast.LENGTH_SHORT).show();
                        showErrors(fName.getText().toString(), lName.getText().toString(), userName.getText().toString(), dept.getText().toString(), editProfile.getText().toString(),
                                    role.getText().toString(), password.getText().toString());
                        return;
                    }

                    //we also need to pass a base 64 encoding of the image into the volley so that it can make the api call
                    PostProfileVolley.postProfile(CreateProfile.this, fName.getText().toString(), lName.getText().toString(),
                            userName.getText().toString(), dept.getText().toString(), editProfile.getText().toString(),
                            role.getText().toString(), password.getText().toString(), "1000", location, apiKey, imageString64);

                    createProfile(userName.getText().toString(), password.getText().toString(), apiKey);

                    //we then will make the call to the volley to post the response and also to start the next activity


                }
            });
        }

        return super.onOptionsItemSelected(item);
    }

    public void showErrors(String fName, String lName, String userName, String dept, String story, String position
                            ,String password) {
        ArrayList<String> list = new ArrayList<String>();
        StringBuilder str = new StringBuilder();
        if(fName == null || fName.length()>20 || "".equals(fName)){
            list.add("First Name");
            str.append("First Name" + "\n");
        }

        if(lName == null || lName.length()>20 || "".equals(lName)){
            list.add("Last Name");
            str.append("Last Name" + "\n");
        }

        if(userName == null || userName.length()>20 ||  "".equals(userName)) {
            list.add("User Name");
            str.append("User Name" + "\n");
        }

        if(dept == null || dept.length()>30 || "".equals(dept)){
            list.add("Department");
            str.append("Department" + "\n");
        }

        if(story == null || story.length()>360 || "".equals(story)){
            list.add("Story");
            str.append("Story" + "\n");
        }

        if(position == null || position.length()>20 || "".equals(position)){
            str.append("Position" + "\n");
        }

        if(password == null || password.length()>40 || "".equals(password)) {
            str.append("Password" + "\n");
        }



        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setIcon(R.drawable.logo);

        builder.setMessage("These are the fields with errors");
        builder.setTitle("Error Fields");

        builder.setMessage(str.toString());

        AlertDialog dialog = builder.create();
        dialog.show();
        return;



    }


    public void createProfile(String username, String password, String api) {
        Intent intent = new Intent(this, YourProfile.class);
        intent.putExtra("username", username);
        intent.putExtra("password", password);
        intent.putExtra("api", api);
        startActivity(intent);
        //we open this new activity and now will get the profile information


    }

    public void loadOption(View v) {
        //clicking the profile image icon in this activity will prompt this function
        //checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE, STORAGE_PERMISSION_CODE);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.logo);
        builder.setTitle("Profile Picture");
        builder.setMessage("Take picture from:");


        builder.setPositiveButton("Camera", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //code to open up camera goes here
                if(checkCameraPermission()){
                    doCamera();
                }
                else{

                }


            }
        });

        builder.setNeutralButton("Gallery", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //code to open up gallery goes here

                if(checkStoragePermission()){
                    doGallery(); }
                else{
                    Toast.makeText(CreateProfile.this, "You do not have permission", Toast.LENGTH_SHORT).show();
                }
            }
        });


        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Toast.makeText(CreateProfile.this, "You selected cancel", Toast.LENGTH_SHORT).show();
                //code to cancel out goes here
            }
        });


        AlertDialog dialog = builder.create();
        dialog.show();

    }

    public boolean checkStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    STORAGE_PERMISSION_CODE);

        return false;
    }
    return true;
    }

    public boolean checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},
                    CAMERA_PERMISSION_CODE);

            return false;
        }
        return true;

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode==STORAGE_PERMISSION_CODE){
            if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                doGallery();
            } else{
                Toast.makeText(CreateProfile.this, "You do not have permission", Toast.LENGTH_SHORT).show();
            }
        }
        else if (requestCode == CAMERA_PERMISSION_CODE){
            if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                doCamera();
            } else{
                Toast.makeText(CreateProfile.this, "You do not have permission", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void doCamera(){
        try {
            currentImageFile = createImageFile();
        } catch (IOException e) {
            return;
        }

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        /*Uri photoURI = FileProvider.getUriForFile(
                this, "com.example.android.fileprovider3", currentImageFile);
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI); */
        cameraActivityResultLauncher.launch(takePictureIntent);

    }

    public void doGallery() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        galleryActivityResultLauncher.launch(photoPickerIntent);
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String imageFileName = "image+";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",    /* suffix */
                storageDir      /* directory */
        );
    }

    public void handleCameraResult(ActivityResult result) {
        if (result == null || result.getData() == null) {
            Log.d(TAG, "handleResult: NULL ActivityResult received");
            return;
        }

        if (result.getResultCode() == RESULT_OK) {
            try {
                Intent data = result.getData();
                processFullCameraImage(data.getExtras());
            } catch (Exception e) {
                Toast.makeText(this, "onActivityResult: " + e.getMessage(), Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }
    }

    private void processFullCameraImage(Bundle extras) {


        Bitmap imageBitmap = (Bitmap) extras.get("data");

        profile.setImageBitmap(imageBitmap);
        BitmapDrawable drawable = (BitmapDrawable) profile.getDrawable();
        Bitmap bitmap = drawable.getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] byteArray = baos.toByteArray();
        imageString64 = Base64.encodeToString(byteArray, Base64.DEFAULT);


        /// The below is not necessary - it's only done for example purposes
        Bitmap bm = ((BitmapDrawable) profile.getDrawable()).getBitmap();


    }
    private void processGallery(Intent data) {

        Uri galleryImageUri = data.getData();
        if (galleryImageUri == null)
            return;

        InputStream imageStream = null;
        try {
            imageStream = getContentResolver().openInputStream(galleryImageUri);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }



        Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
//need to use the Bitmap to get a scaled width
        double w = selectedImage.getWidth();
        double h = selectedImage.getHeight();
        double widToHgtRatio = w / h;

        int wid = profile.getWidth();
        int hgt = (int) (wid / widToHgtRatio);
        selectedImage = Bitmap.createScaledBitmap(selectedImage, wid, hgt, false);

        profile.setImageBitmap(selectedImage);
        BitmapDrawable drawable = (BitmapDrawable) profile.getDrawable();
        Bitmap bitmap = drawable.getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        byte[] byteArray = baos.toByteArray();
        imageString64 = Base64.encodeToString(byteArray, Base64.DEFAULT);

    }

    public void handleGalleryResult(ActivityResult result) {
        if (result == null || result.getData() == null) {
            Log.d(TAG, "handleResult: NULL ActivityResult received");
            return;
        }

        if (result.getResultCode() == RESULT_OK) {
            try {
                Intent data = result.getData();
                processGallery(data);
            } catch (Exception e) {
                Toast.makeText(this, "onActivityResult: " + e.getMessage(), Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }
    }


}