package com.example.chattingapp;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.net.URI;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class Settings extends AppCompatActivity {

    private Button updateAccountSettings;
    private EditText userName,userStatus;
    private CircleImageView userProfileImage;

    private StorageReference userProfileImageRef;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;
    private String userId;
    private Toolbar settingToolbar;
    private static final int Gallery_PICK=1;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==Gallery_PICK && resultCode==RESULT_OK && data!=null){
            Uri imageUri=data.getData();
            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(this);
        }
        if (requestCode ==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result=CropImage.getActivityResult(data);
            if(resultCode==RESULT_OK)
            {
                Uri resultUri=result.getUri();

                final StorageReference filePath=userProfileImageRef.child(userId+".jpg");
                Log.e("Settings",userId+"");

                filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                        if (task.isSuccessful())
                        {
                            filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    databaseReference.child("Users").child(userId).child("image")
                                            .setValue(uri.toString())
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()){
                                                        Toast.makeText(Settings.this, "Saved Photo Successfully....", Toast.LENGTH_SHORT).show();
                                                    }
                                                    else
                                                    {
                                                        Log.e("Settings",task.getException().getMessage()+"");
                                                    }
                                                }
                                            });
                                }
                            });
                            Toast.makeText(Settings.this, "Profile Image Uploaded Successfully....", Toast.LENGTH_SHORT).show();
                           //task.getResult();

                            //final String downloadUrl=task.getResult();
                          //  UploadTask.TaskSnapshot downloadUri = task.getResult();
                          //final  String downloadURL = downloadUri.toString();

                        }
                        else
                        {
                            Log.e("Settings",task.getException().getMessage()+"");
                        }
                    }
                });
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        settingToolbar=findViewById(R.id.settings_bar);
        setSupportActionBar(settingToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Account Settings");
        mAuth=FirebaseAuth.getInstance();
        userId=mAuth.getCurrentUser().getUid();
        databaseReference= FirebaseDatabase.getInstance().getReference();
        userProfileImageRef= FirebaseStorage.getInstance().getReference().child("profile Images");

        itialization();

        userName.setVisibility(View.INVISIBLE);

        updateAccountSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateSettings();
            }
        });

        retrieveSettings();

        userProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent=new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent,Gallery_PICK);
            }
        });


    }

    private void retrieveSettings() {
        databaseReference.child("Users").child(userId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if((dataSnapshot.exists()) && (dataSnapshot.hasChild("name")) && (dataSnapshot.hasChild("image"))){

                            String retrieveUserName=dataSnapshot.child("name").getValue().toString();
                            String retrieveStatus=dataSnapshot.child("status").getValue().toString();
                            String profileImage=dataSnapshot.child("image").getValue().toString();

                            Picasso.get().load(profileImage).into(userProfileImage);
                            userName.setText(retrieveUserName);
                            userStatus.setText(retrieveStatus);
                           // userProfileImage.set
                        }
                        else if((dataSnapshot.exists()) && (dataSnapshot.hasChild("name"))){

                            String retrieveUserName=dataSnapshot.child("name").getValue().toString();
                            String retrieveStatus=dataSnapshot.child("status").getValue().toString();
                            userName.setText(retrieveUserName);
                            userStatus.setText(retrieveStatus);
                        }
                        else{
                            userName.setVisibility(View.VISIBLE);
                            Toast.makeText(Settings.this, "please Update your profile", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }


    private void itialization(){
        updateAccountSettings=findViewById(R.id.update_settings);
        userName=findViewById(R.id.set_user_name);
        userStatus=findViewById(R.id.set_user_status);
        userProfileImage=findViewById(R.id.profile_image);

    }

    private void updateSettings() {

        String name=userName.getText().toString();
        String status=userStatus.getText().toString();

        if(name.trim().isEmpty()){
            Toast.makeText(this, "Please write your user name first.....", Toast.LENGTH_SHORT).show();
        }
        if(status.isEmpty()){
            Toast.makeText(this, "Please write your status first.....", Toast.LENGTH_SHORT).show();
        }
        else{
            HashMap<String,Object> profileMap=new HashMap<>();
            profileMap.put("uid",userId);
            profileMap.put("name",name);
            profileMap.put("status",status);

            databaseReference.child("Users").child(userId).updateChildren(profileMap)
                    .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                sendUserToMainActivity();
                                Toast.makeText(Settings.this, "Profile Updated Successful", Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                Toast.makeText(Settings.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
        }
        
    }
    private void sendUserToMainActivity() {

        Intent intent=new Intent(Settings.this,Main2Activity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

}
