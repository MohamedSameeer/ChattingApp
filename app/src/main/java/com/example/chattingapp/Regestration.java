package com.example.chattingapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

public class Regestration extends AppCompatActivity {

    private  EditText rEmail,rPassword;
    private TextView rHaveAcc;
    private Button rRegestration;
    private String email,pass;

    private FirebaseAuth mAuth;
    private ProgressDialog loadingBar;
    private DatabaseReference RootRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regestration);

       intializeFields();

        rHaveAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Regestration.this,LoginPage.class));
            }
        });

        rRegestration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                creatNewAccount();
            }
        });

    }

    private void creatNewAccount() {

        email=rEmail.getText().toString();
        pass=rPassword.getText().toString();

        if(email.trim().isEmpty())
            Toast.makeText(this, "Please Enter email", Toast.LENGTH_LONG).show();
        
        else if(TextUtils.isEmpty(pass.trim()))
            Toast.makeText(this, "Please Enter PassWord", Toast.LENGTH_SHORT).show();

        else
        {
            loadingBar.setTitle("Create New Account");
            loadingBar.setMessage("Please wait , while we are creating new account for you......");
            loadingBar.setCanceledOnTouchOutside(true);
            loadingBar.show();
            mAuth.createUserWithEmailAndPassword(email,pass)
                    .addOnCompleteListener(this,new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if (task.isSuccessful()){

                        String currentUserId=mAuth.getCurrentUser().getUid();
                        RootRef.child("Users").child(currentUserId).setValue("");
                        String deviceToken= FirebaseInstanceId.getInstance().getToken();

                        RootRef.child("Users").child(currentUserId).child("device_token").setValue(deviceToken);

                        Toast.makeText(Regestration.this, "Account Created", Toast.LENGTH_SHORT).show();
                        sendUsertoMainActivity();
                        loadingBar.dismiss();
                    }
                    else
                    {
                        String message=task.getException().toString();
                        Toast.makeText(Regestration.this, ""+message, Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();
                    }
                }
            });
        }

    }

    private void intializeFields() {

        mAuth=FirebaseAuth.getInstance();
        RootRef=FirebaseDatabase.getInstance().getReference();
        rEmail=findViewById(R.id.rEmail);
        rPassword=findViewById(R.id.rPassword);
        rRegestration=findViewById(R.id.regestration);
        rHaveAcc=findViewById(R.id.haveAcc);
        loadingBar=new ProgressDialog(this);
    }

    private void sendUsertoMainActivity(){
        Intent i=new Intent(Regestration.this,Main2Activity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        finish();
    }
}
