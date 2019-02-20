package com.example.chattingapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    private String messageReceiverId,messageReceiverName,messageReceiverImage,messageSenderId;
    private TextView userName,userStatus;
    private CircleImageView user_Image;
    private Toolbar chatToolbar;
    private ActionBar actionBar;
    private EditText message;
    private FirebaseAuth mAuth;
    private DatabaseReference RootRef;
    private ImageButton sendMessage;
    private final List<Messages> messagesList=new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private MessageAdapter adapter;
    private RecyclerView userMessageList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mAuth=FirebaseAuth.getInstance();
        messageSenderId=mAuth.getCurrentUser().getUid();
        RootRef=FirebaseDatabase.getInstance().getReference();
        messageReceiverId=getIntent().getExtras().get("visit_user_id").toString();
        messageReceiverName=getIntent().getExtras().get("visit_user_name").toString();
        messageReceiverImage=getIntent().getExtras().get("visit_user_image").toString();

        Log.e("ccccccccccc",messageReceiverName+"");

        intializeFields();

        actionBar.setTitle(messageReceiverName);
        actionBar.setSubtitle("Last Seen");

        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendPrivateMessage();
            }
        });

       // userName.setText(messageReceiverName);
       // Picasso.get().load(messageReceiverImage).placeholder(R.drawable.profile_image).into(user_Image);
    }

    private void sendPrivateMessage() {

        String messageText=message.getText().toString();

        if(messageText.trim().isEmpty()){

            Toast.makeText(this, "first write message", Toast.LENGTH_SHORT).show();
        }else{

            String messageSenderRef="Messages/"+messageSenderId+"/"+messageReceiverId;
            String messageReceiverRef="Messages/"+messageReceiverId+"/"+messageSenderId;

            DatabaseReference userMessageKeyRef=RootRef.child("Messages").child(messageSenderRef)
                    .child(messageReceiverRef).push();

            String messagePushId=userMessageKeyRef.getKey();

            Map messageTextBody=new HashMap();
            messageTextBody.put("message",messageText);
            messageTextBody.put("type","text");
            messageTextBody.put("from",messageSenderId);

            Map messageBodyDetails=new HashMap();

            messageBodyDetails.put(messageSenderRef+"/"+messagePushId,messageTextBody);
            messageBodyDetails.put(messageReceiverRef+"/"+messagePushId,messageTextBody);

            RootRef.updateChildren(messageBodyDetails).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()){

                            Toast.makeText(ChatActivity.this, "Send Message Successful", Toast.LENGTH_SHORT).show();
                        }else
                        {
                            Toast.makeText(ChatActivity.this, "Send Message Field", Toast.LENGTH_SHORT).show();
                        }
                        message.setText("");
                }
            });
        }
    }

    private void intializeFields() {

        chatToolbar=findViewById(R.id.private_chat_bar);
        setSupportActionBar(chatToolbar);

        actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        message=findViewById(R.id.input_message);
        sendMessage=findViewById(R.id.send_private_message);

        adapter=new MessageAdapter(messagesList);
        userMessageList=findViewById(R.id.chat_container);
        linearLayoutManager=new LinearLayoutManager(this);
        userMessageList.setLayoutManager(linearLayoutManager);
        userMessageList.setAdapter(adapter);


/*
       LayoutInflater layoutInflater= (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
       View actionBarView=layoutInflater.inflate(R.layout.custom_chat_bar,null);
       actionBar.setCustomView(actionBarView);

        userName=findViewById(R.id.custom_user_name);
        userStatus=findViewById(R.id.custom_user_status);
        user_Image=findViewById(R.id.custom_profile_image);


*/

    }

    @Override
    protected void onStart() {
        super.onStart();

        RootRef.child("Messages").child(messageSenderId).child(messageReceiverId)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        Messages messages=dataSnapshot.getValue(Messages.class);

                        messagesList.add(messages);
                        adapter.notifyDataSetChanged();
                        userMessageList.smoothScrollToPosition(userMessageList.getAdapter().getItemCount());
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

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
}
