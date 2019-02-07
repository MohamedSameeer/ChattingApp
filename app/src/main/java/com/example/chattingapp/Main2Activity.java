package com.example.chattingapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Main2Activity extends AppCompatActivity {


    private Toolbar mToolbar;
    private ViewPager mViewPager;
    private TabLayout mTabLayout;

    private DatabaseReference mRef;
    private FirebaseAuth mAuth;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_main2);

        //Firebase Intialization;
        mRef= FirebaseDatabase.getInstance().getReference();
        mAuth=FirebaseAuth.getInstance();
        user= FirebaseAuth.getInstance().getCurrentUser();


        mToolbar =findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Chatting App");

        mViewPager=findViewById(R.id.main_view_pager);
        mTabLayout=findViewById(R.id.main_tabs);

        FmAdapter fmAdapter=new FmAdapter(this,getSupportFragmentManager());
        mViewPager.setAdapter(fmAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(user==null){
            startActivity(new Intent(Main2Activity.this,LoginPage.class));
        }
        else{
            String uId=mAuth.getCurrentUser().getUid();
            mRef.child("Users").child(uId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if((dataSnapshot.child("name").exists())){

                        Toast.makeText(Main2Activity.this, "Welcome", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        sendUserToSettingActivity();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    private void sendUserToSettingActivity() {

        Intent intent=new Intent(Main2Activity.this,Settings.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()){
            case R.id.menu_find_friend:

                break;
            case R.id.menu_logout:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(Main2Activity.this,LoginPage.class));
                break;
            case R.id.create_group:
                requestNewGroup();
                break;
            case R.id.menu_settings:
                startActivity(new Intent(Main2Activity.this,Settings.class));
                break;
        }
        return true;
    }

    private void requestNewGroup() {

        final AlertDialog.Builder alertDialog=new AlertDialog.Builder(Main2Activity.this,R.style.AlertDialog);
        alertDialog.setTitle("Enter The Group Name");
        final EditText editText=new EditText(Main2Activity.this);
        editText.setHint("e.g Coders");

        alertDialog.setView(editText);

        alertDialog.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String group_name=editText.getText().toString();
                if(group_name.trim().isEmpty()){
                    Toast.makeText(Main2Activity.this, "Please Enter The Group Name", Toast.LENGTH_SHORT).show();
                }else{
                    createGroup(group_name);
                }
            }
        });

        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialog.show();
    }

    private void createGroup(final String group_name) {

        mRef.child("Groups").child(group_name).setValue("").addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(Main2Activity.this, group_name+" is Created", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}
