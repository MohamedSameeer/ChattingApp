package com.example.chattingapp;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private CircleImageView profileUserImage;
    private TextView profileUserName,profileUserStatus;
    private Button profileSendMessage,cancelMessageRequest;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference,chatRequestRef,contactRef,notificationChatRequest;
    private String receiverUserId,senderUserId,currentState;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        mAuth=FirebaseAuth.getInstance();
        senderUserId=mAuth.getCurrentUser().getUid();
        receiverUserId=getIntent().getStringExtra("userId");
        databaseReference= FirebaseDatabase.getInstance().getReference().child("Users");
        chatRequestRef=FirebaseDatabase.getInstance().getReference().child("Chat Requests");
        contactRef=FirebaseDatabase.getInstance().getReference().child("Contacts");
        notificationChatRequest=FirebaseDatabase.getInstance().getReference().child("Notifications");
        intializeFields();

        retriveData();
    }

    private void retriveData() {
        databaseReference.child(receiverUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.hasChild("image")){
                    String userImage=dataSnapshot.child("image").getValue().toString();
                    String userName=dataSnapshot.child("name").getValue().toString();
                    String userStatus=dataSnapshot.child("status").getValue().toString();

                    Picasso.get().load(userImage).into(profileUserImage);
                    profileUserName.setText(userName+"");
                    profileUserStatus.setText(userStatus+"");

                    manageChatRequest();

                } else{
                    String userName=dataSnapshot.child("name").getValue().toString();
                    String userStatus=dataSnapshot.child("status").getValue().toString();

                    profileUserName.setText(userName+"");
                    profileUserStatus.setText(userStatus+"");

                    manageChatRequest();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void manageChatRequest() {

        chatRequestRef.child(senderUserId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChild(receiverUserId)){
                            String reques_type=dataSnapshot.child(receiverUserId).child("request_type").getValue().toString();
                            if (reques_type.equals("sent")){
                                currentState="request_sent";
                                profileSendMessage.setText("Cancel Chat Request");

                                cancelMessageRequest.setVisibility(View.INVISIBLE);
                                cancelMessageRequest.setEnabled(false);

                            }
                            else if(reques_type.equals("received")){

                                currentState="request_received";
                                profileSendMessage.setText("Accept Chat Request");

                                cancelMessageRequest.setVisibility(View.VISIBLE);
                                cancelMessageRequest.setEnabled(true);
                                cancelMessageRequest.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        cancelRequest();
                                    }
                                });
                            }
                        }
                        else{
                                contactRef.child(senderUserId).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if(dataSnapshot.hasChild(receiverUserId)){
                                            currentState="friends";
                                            profileSendMessage.setText("Remove this From Contacts");
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        if(!senderUserId.equals(receiverUserId)){

          profileSendMessage.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                    profileSendMessage.setEnabled(false);
                    if(currentState.equals("new")){
                        sendMessageRequest();
                    }
                    if(currentState.equals("request_sent")){

                        cancelRequest();
                    }
                    if(currentState.equals("request_received")){
                        AcceptChatRequest();
                    }

                  if(currentState.equals("friends")){
                      RemoveFriend();
                  }
              }
          });
        }else{
            profileSendMessage.setVisibility(View.INVISIBLE);
        }
    }

    private void RemoveFriend() {

        contactRef.child(senderUserId).child(receiverUserId).removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            contactRef.child(receiverUserId).child(senderUserId).removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                profileSendMessage.setEnabled(true);
                                                currentState="new";
                                                profileSendMessage.setText("Send Message");

                                                cancelMessageRequest.setVisibility(View.INVISIBLE);
                                                cancelMessageRequest.setEnabled(false);
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    private void AcceptChatRequest() {

        contactRef.child(senderUserId).child(receiverUserId)
                .child("Contacts").setValue("Saved")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            contactRef.child(receiverUserId).child(senderUserId)
                                    .child("Contacts").setValue("Saved")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()){
                                                    chatRequestRef.child(senderUserId).child(receiverUserId)
                                                            .removeValue()
                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful()){
                                                                        chatRequestRef.child(receiverUserId)
                                                                                .child(senderUserId)
                                                                                .removeValue()
                                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                    @Override
                                                                                    public void onComplete(@NonNull Task<Void> task) {

                                                                                        profileSendMessage.setEnabled(true);
                                                                                        currentState="friends";
                                                                                        profileSendMessage.setText("Remove this Contact");

                                                                                        cancelMessageRequest.setVisibility(View.INVISIBLE);
                                                                                        cancelMessageRequest.setEnabled(false);

                                                                                    }
                                                                                });
                                                                    }
                                                                }
                                                            });
                                                }
                                        }
                                    });
                        }
                    }
                });

    }

    private void cancelRequest() {
            chatRequestRef.child(senderUserId).child(receiverUserId).removeValue()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                chatRequestRef.child(receiverUserId).child(senderUserId).removeValue()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()){
                                                    profileSendMessage.setEnabled(true);
                                                    currentState="new";
                                                    profileSendMessage.setText("Send Message");

                                                    cancelMessageRequest.setVisibility(View.INVISIBLE);
                                                    cancelMessageRequest.setEnabled(false);
                                                }
                                            }
                                        });
                            }
                        }
                    });

    }

    private void sendMessageRequest() {

        chatRequestRef.child(senderUserId).child(receiverUserId)
                .child("request_type").setValue("sent")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()){
                                chatRequestRef.child(receiverUserId).child(senderUserId)
                                        .child("request_type").setValue("received")
                                         .addOnCompleteListener(new OnCompleteListener<Void>() {
                                             @Override
                                             public void onComplete(@NonNull Task<Void> task) {

                                                 HashMap<String,String>chatNotificationMap=new HashMap<>();

                                                 chatNotificationMap.put("from",senderUserId);
                                                 chatNotificationMap.put("type","request");

                                                 notificationChatRequest.child(receiverUserId).push()
                                                         .setValue(chatNotificationMap)
                                                         .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                             @Override
                                                             public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful()){

                                                                        profileSendMessage.setEnabled(true);
                                                                        currentState="request_sent";
                                                                        profileSendMessage.setText("Cancel Chat Request");
                                                                    }
                                                             }
                                                         });

                                             }
                                         });
                            }
                    }
                });
    }



    private void intializeFields(){
        profileSendMessage=findViewById(R.id.profile_send_message);
        cancelMessageRequest=findViewById(R.id.cancel_send_message);
        profileUserImage=findViewById(R.id.profile_user_image);
        profileUserName=findViewById(R.id.profile_user_name);
        profileUserStatus=findViewById(R.id.profile_user_status);
        currentState="new";
    }
}
