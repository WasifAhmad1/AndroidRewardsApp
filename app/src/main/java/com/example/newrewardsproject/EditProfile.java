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
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.newrewardsproject.volley.GetProfileInfoVolley;
import com.example.newrewardsproject.volley.UpdateProfileVolley;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class EditProfile extends AppCompatActivity {

    private final String TAG = getClass().getSimpleName();
    private TextView userName;
    private TextView passWord;
    TextView textCount;
    private EditText fName;
    private EditText lName;
    private EditText editDept;
    private EditText editRole;
    private EditText editProfile;
    private ImageView profile;
    private static String image;
    private static String location;
    private static String getApi;
    private static String pWord;
    private static final int MAX_LEN = 360;
    private File currentImageFile;
    private static final int STORAGE_PERMISSION_CODE = 113;
    private static final int CAMERA_PERMISSION_CODE = 114;
    private ActivityResultLauncher<Intent> cameraActivityResultLauncher;
    private ActivityResultLauncher<Intent> galleryActivityResultLauncher;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setTitle("Edit Profile");
        actionBar.setIcon(R.drawable.icon);
        userName = findViewById(R.id.userNameEntry);
        passWord = findViewById(R.id.pwordEntry);
        fName = findViewById(R.id.fNameEntry);
        lName = findViewById(R.id.lNameEntry);
        textCount = findViewById(R.id.textCount);
        editDept = findViewById(R.id.deptEntry);
        editRole = findViewById(R.id.roleEntry);
        editProfile = findViewById(R.id.editProfile);
        profile = findViewById(R.id.profileImage);
        String username = getIntent().getStringExtra("username");
        pWord = getIntent().getStringExtra("password");
        getApi = getIntent().getStringExtra("api");
        location = getIntent().getStringExtra("location");
        GetProfileInfoVolley.editProfile(this, username, pWord, getApi);
        userName.setText(username);
        passWord.setText(pWord);

        cameraActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                this::handleCameraResult);

        galleryActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                this::handleGalleryResult);
        //in this class we will immediately get all fields from the previous class. Then we will

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
    }

    public void editData(String firstName, String lastName, String department, String position,
                         String story, String imageBytes) {
        fName.setText(firstName);
        lName.setText(lastName);
        editDept.setText(department);
        editRole.setText(position);
        editProfile.setText(story);
        processImage(imageBytes);
        image = imageBytes;
    }

    public void processImage(String imageBytesString) {
        byte[] imageBytes = Base64.decode(imageBytesString, Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        profile.setImageBitmap(bitmap);

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
                    Toast.makeText(EditProfile.this, "You selected cancel", Toast.LENGTH_SHORT).show();
                    //code to cancel out goes here
                }
            });


            AlertDialog dialog = builder.create();
            dialog.show();

            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String firstName = fName.getText().toString();
                    String lastName = lName.getText().toString();
                    String dept = editDept.getText().toString();
                    String profile = editProfile.getText().toString();
                    String position = editRole.getText().toString();
                    String image2 = image;

                    if(fName.getText().toString() == null  || fName.getText().toString().length() > 20 ||
                            "".equals(fName.getText().toString().trim()) || lName.getText().toString() == null
                            || lName.getText().toString().length() > 20 ||
                            "".equals(lName.getText().toString().trim()) || editDept.getText().toString() == null
                            || editDept.getText().toString().length() > 30 ||
                            "".equals(editDept.getText().toString().trim()) || editProfile.getText().toString() == null
                            || editProfile.getText().toString().length() > 360 ||
                            "".equals(editProfile.getText().toString().trim()) ||
                            editRole.getText().toString() == null || editRole.getText().toString().length() > 20 ||
                            "".equals(editRole.getText().toString().trim()) || "".equals(image)|| image.length() > 100000)
                    {
                        showErrors(fName.getText().toString(), lName.getText().toString(), editDept.getText().toString(),
                        editProfile.getText().toString(), editRole.getText().toString());
                        Toast.makeText(EditProfile.this, "One of the fields is missing", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    UpdateProfileVolley.updateProfile(EditProfile.this, fName.getText().toString(), lName.getText().toString(),
                            userName.getText().toString(), editDept.getText().toString(), editProfile.getText().toString(), editRole.getText().toString(),
                            passWord.getText().toString(), location, image, getApi);
                    createProfile(userName.getText().toString(), pWord, getApi);

                    //we make the call to the updateProfile method to update the profile. Then we will go back to the profile activity
                }
            });
        }



            return super.onOptionsItemSelected(item);


    }

    public void showErrors(String fName, String lName, String dept, String story, String position
            ) {
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


        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setIcon(R.drawable.logo);

        builder.setMessage("These are the fields with errors");
        builder.setTitle("Error Fields");

        builder.setMessage(str.toString());

        AlertDialog dialog = builder.create();
        dialog.show();
        return;



    }

    public void createProfile (String username, String password, String api) {
        Intent intent = new Intent(this, YourProfile.class);
        intent.putExtra("username", username);
        intent.putExtra("password", password);
        intent.putExtra("api", api);
        startActivity(intent);
        //we open this new activity and now will get the profile information
    }

    public void editOption(View v) {
        //clicking the profile image icon in this activity will prompt this function
        //checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE, STORAGE_PERMISSION_CODE);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.logo);
        builder.setTitle("Profile Picture");
        builder.setMessage("Take picture from:");


        builder.setPositiveButton("Camera", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
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
                }
            }
        });


        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Toast.makeText(EditProfile.this, "You selected cancel", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(EditProfile.this, "You do not have permission", Toast.LENGTH_SHORT).show();
            }
        }
        else if (requestCode == CAMERA_PERMISSION_CODE){
            if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                doCamera();
            } else{
                Toast.makeText(EditProfile.this, "You do not have permission", Toast.LENGTH_SHORT).show();
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
        image = Base64.encodeToString(byteArray, Base64.DEFAULT);


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
        image = Base64.encodeToString(byteArray, Base64.DEFAULT);

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