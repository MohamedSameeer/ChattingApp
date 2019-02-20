package com.example.chattingapp;

import android.app.ProgressDialog;
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
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class PhoneLogin extends AppCompatActivity {

    Button sendVerifyCode,sendPhoneNumber;
    EditText phoneNumber,code;
    FirebaseAuth mAuth;
    private ProgressDialog loadingBar;
    private String mVarificationId;
    private PhoneAuthProvider.ForceResendingToken mResendingToken;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_login);

        intializeFields();

        sendPhoneNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone=phoneNumber.getText().toString();


                if(phone.trim().isEmpty()){
                    phoneNumber.setError("Please Enter Phone Number");


                }
                else{
                    loadingBar.setTitle("Phone Verification");
                    loadingBar.setMessage("Please Wait, While we are authenticating your phone.... ");
                    loadingBar.setCanceledOnTouchOutside(false);
                    loadingBar.show();

                    PhoneAuthProvider.getInstance().verifyPhoneNumber(
                            phone
                            ,60
                            , TimeUnit.SECONDS
                            ,PhoneLogin.this
                            ,mCallbacks
                    );
                }


            }
        });

        sendVerifyCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendPhoneNumber.setVisibility(View.INVISIBLE);
                phoneNumber.setVisibility(View.INVISIBLE);

                String verificationCode=code.getText().toString();

                if (verificationCode.trim().isEmpty()){
                    code.setError("Invalide code");
                }
                else
                {
                    loadingBar.setTitle("Verify Code");
                    loadingBar.setMessage("Wait to Verify your Verification Code ");
                    loadingBar.setCanceledOnTouchOutside(false);
                    loadingBar.show();
                    PhoneAuthCredential credential=PhoneAuthProvider.getCredential(mVarificationId,verificationCode);
                    signInWithCredential(credential);
                }
            }
        });

        mCallbacks=new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
               signInWithCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                phoneNumber.setError("please enter your phone number with code of your Country");
                phoneNumber.requestFocus();

                phoneNumber.setVisibility(View.VISIBLE);
                sendPhoneNumber.setVisibility(View.VISIBLE);

                sendVerifyCode.setVisibility(View.GONE);
                code.setVisibility(View.GONE);
            }

            @Override
            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                loadingBar.dismiss();
                mResendingToken=forceResendingToken;
                mVarificationId=s;
                Toast.makeText(PhoneLogin.this, "Code has been sent", Toast.LENGTH_SHORT).show();
                phoneNumber.setVisibility(View.GONE);
                sendPhoneNumber.setVisibility(View.GONE);

                sendVerifyCode.setVisibility(View.VISIBLE);
                code.setVisibility(View.VISIBLE);
            }
        };


    }

    private void signInWithCredential(PhoneAuthCredential phoneAuthCredential) {

        mAuth.signInWithCredential(phoneAuthCredential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            loadingBar.dismiss();
                            sendUserToMainActivity();
                        }
                        else {
                            loadingBar.dismiss();
                            Toast.makeText(PhoneLogin.this, task.getException().getMessage()+"", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void sendUserToMainActivity() {

        Intent intent=new Intent(PhoneLogin.this,Main2Activity.class);
        startActivity(intent);
        finish();
            }

    private void intializeFields() {
        mAuth=FirebaseAuth.getInstance();
        phoneNumber=findViewById(R.id.phone_number);
        code=findViewById(R.id.varify_code);
        sendVerifyCode=findViewById(R.id.send_verify_code);
        sendPhoneNumber=findViewById(R.id.send_phone_number);
        loadingBar=new ProgressDialog(this);
    }
}
