package com.example.chattingapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

public class VerifyCode extends AppCompatActivity {

    String id,code;
    EditText vCode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_code);

        Intent intent=getIntent();
        id=intent.getStringExtra("id");
        vCode=findViewById(R.id.code);
        code=vCode.getText().toString();
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(id,code);
        signInWithPhoneAuthCredential(credential);

    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential){

        FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener(this,new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful())
                {
                    FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
                    if(user!=null){
                        Intent intent=new Intent(VerifyCode.this,MainPageActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            }
        });
    }

}
