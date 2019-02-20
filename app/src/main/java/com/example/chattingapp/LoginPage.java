package com.example.chattingapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.concurrent.TimeUnit;

public class LoginPage extends AppCompatActivity {

    private EditText lEmail,lPassword;
    private Button lLogin,lPhone;
    private TextView lFpassword,lNeedAcc;
    private String email,password;
    private final String TAG="LoginPage";

    private ProgressDialog loading;
    private FirebaseAuth mAuth;
    private DatabaseReference userRef;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallBacks;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        intializeFields();
        userRef= FirebaseDatabase.getInstance().getReference().child("Users");
        lLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
        lNeedAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginPage.this,Regestration.class));
            }
        });
        lPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginPage.this,PhoneLogin.class));
            }
        });

    }





    private void login() {

        email=lEmail.getText().toString();
        password=lPassword.getText().toString();



        if(email.trim().isEmpty())
            Toast.makeText(this, "Enter Email", Toast.LENGTH_SHORT).show();

        else if(password.trim().isEmpty())
            Toast.makeText(this, "EnterPassword", Toast.LENGTH_SHORT).show();
        else
        {
            loading.setTitle("Sign In");
            loading.setMessage("Please wait...");
            loading.setCanceledOnTouchOutside(true);
            loading.show();
            mAuth.signInWithEmailAndPassword(email.trim(),password.trim())
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){

                            String currentUserId=mAuth.getCurrentUser().getUid();
                            String deviceToken= FirebaseInstanceId.getInstance().getToken();

                            userRef.child(currentUserId).child("device_token").setValue(deviceToken)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){

                                                Toast.makeText(LoginPage.this, "Login Complete", Toast.LENGTH_SHORT).show();
                                                sendUserToMainActivity();
                                                loading.dismiss();
                                            }
                                        }
                                    });

                        }
                        else {
                            String message = task.getException().toString();
                            Toast.makeText(LoginPage.this, ""+message, Toast.LENGTH_SHORT).show();
                            loading.dismiss();
                        }

                }
            });
        }

    }




    private void sendUserToMainActivity() {
        Intent i=new Intent(LoginPage.this,Main2Activity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        finish();
    }



    private void intializeFields() {

        mAuth=FirebaseAuth.getInstance();
        lEmail=findViewById(R.id.e_mail);
        lPassword=findViewById(R.id.password);
        lLogin=findViewById(R.id.login);
        lPhone=findViewById(R.id.phone);
        lFpassword=findViewById(R.id.forgetPassword);
        lNeedAcc=findViewById(R.id.needAcc);
        loading=new ProgressDialog(this);
    }
}