package com.example.chattingapp;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;

public class GroupChat extends AppCompatActivity {

    private EditText inputMessage;
    private Toolbar mToolbar;
    private ImageButton sendMessage;
    private ScrollView mScrollView;
    private TextView displayMessage;

    private FirebaseAuth mAuth;
    private DatabaseReference databaseUsersReference, groupNameRef, groupMessageKeyRef;
    private String groupName, currentUserId, currentUserName, currentDate, currentTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);

        groupName=getIntent().getExtras().get("groupName").toString();
        Toast.makeText(getApplicationContext(),""+groupName,Toast.LENGTH_LONG).show();
        mAuth=FirebaseAuth.getInstance();
        currentUserId=mAuth.getCurrentUser().getUid();
        databaseUsersReference= FirebaseDatabase.getInstance().getReference().child("Users");
        groupNameRef=FirebaseDatabase.getInstance().getReference().child("Groups").child(groupName);

        itialization();

        getUserInfo();

        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                saveMessageToDatabase();
                inputMessage.setText("");
                mScrollView.fullScroll(ScrollView.FOCUS_DOWN);


            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        groupNameRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                if(dataSnapshot.exists()){
                    displayData(dataSnapshot);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(dataSnapshot.exists()){
                    displayData(dataSnapshot);
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void displayData(DataSnapshot dataSnapshot) {



        Iterator iterator= dataSnapshot.getChildren().iterator();

        while (iterator.hasNext()){

            String chatDate= (String) ((DataSnapshot) iterator.next()).getValue();
            String chatMessage= (String) ((DataSnapshot) iterator.next()).getValue();
            String chatName= (String) ((DataSnapshot) iterator.next()).getValue();
            String chatTime=(String) ((DataSnapshot) iterator.next()).getValue();

            displayMessage.append(chatName+" \n"+chatMessage+" \n"+chatTime+"   "+chatDate+"\n\n\n");
            mScrollView.fullScroll(ScrollView.FOCUS_DOWN);
        }
    }

    private void itialization() {
        mToolbar=findViewById(R.id.group_chat_bar);
        inputMessage=findViewById(R.id.input_group_message);
        sendMessage=findViewById(R.id.send_message_button);
        mScrollView=findViewById(R.id.group_chat_scroll);
        displayMessage=findViewById(R.id.group_chat_message);
        mToolbar.setTitle(groupName);
    }

    private void getUserInfo() {

        databaseUsersReference.child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()){
                    currentUserName=dataSnapshot.child("name").getValue().toString();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void saveMessageToDatabase() {

        String message=inputMessage.getText().toString();
        String messageKey=groupNameRef.push().getKey();
        if(message.trim().isEmpty()){
            Toast.makeText(this, "Please Write Message First", Toast.LENGTH_SHORT).show();
        }
        else{

            Calendar calendarForDate = Calendar.getInstance();
            SimpleDateFormat currentDateFormat=new SimpleDateFormat("MMM dd, yyyy");
            currentDate=currentDateFormat.format(calendarForDate.getTime());

            Calendar calendarForTime= Calendar.getInstance();
            SimpleDateFormat currentTimeFormat= new SimpleDateFormat("hh:mm a");
            currentTime=currentTimeFormat.format(calendarForTime.getTime());

        /*  HashMap<String, Object>groupMessageKey=new HashMap<>();
            groupNameRef.updateChildren(groupMessageKey);
*/
            groupMessageKeyRef=groupNameRef.child(messageKey);

            HashMap<String, Object> messageInfoMap=new HashMap<>();

            messageInfoMap.put("name",currentUserName);
            messageInfoMap.put("message",message);
            messageInfoMap.put("date",currentDate);
            messageInfoMap.put("time",currentTime);

            groupMessageKeyRef.updateChildren(messageInfoMap);
        }
    }

}
