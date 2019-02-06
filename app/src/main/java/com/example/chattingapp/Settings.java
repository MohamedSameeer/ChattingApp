package com.example.chattingapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class Settings extends AppCompatActivity {

    private Button updateAccountSettings;
    private EditText userName,userStatus;
    private CircleImageView userProfileImage;

    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;
    private String userId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        itialization();
        userName.setVisibility(View.INVISIBLE);
        mAuth=FirebaseAuth.getInstance();
        userId=mAuth.getCurrentUser().getUid();
        databaseReference= FirebaseDatabase.getInstance().getReference();
        updateAccountSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateSettings();
            }
        });

        retrieveSettings();
    }

    private void retrieveSettings() {
        databaseReference.child("Users").child(userId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if((dataSnapshot.exists()) && (dataSnapshot.hasChild("name")) && (dataSnapshot.hasChild("profileImage"))){

                            String retrieveUserName=dataSnapshot.child("name").getValue().toString();
                            String retrieveStatus=dataSnapshot.child("status").getValue().toString();
                            String profileImage=dataSnapshot.child("profileImage").getValue().toString();

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
            HashMap<String,String> profileMap=new HashMap<>();
            profileMap.put("uid",userId);
            profileMap.put("name",name);
            profileMap.put("status",status);

            databaseReference.child("Users").child(userId).setValue(profileMap)
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
