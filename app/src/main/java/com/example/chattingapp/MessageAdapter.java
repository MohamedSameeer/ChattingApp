package com.example.chattingapp;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.collection.LLRBNode;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    private List<Messages> userMessagesList;
    private FirebaseAuth mAuth;
    private DatabaseReference userRef;

    public MessageAdapter(List<Messages> userMessagesList) {
        this.userMessagesList = userMessagesList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.custom_message_layout,viewGroup,false);
        mAuth=FirebaseAuth.getInstance();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {

        String messageSenderId=mAuth.getCurrentUser().getUid();
        Messages message=userMessagesList.get(i);

        String fromUserId=message.getFrom();
        String fromMessageType=message.getType();

        userRef= FirebaseDatabase.getInstance().getReference().child("Users").child(fromUserId);

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.hasChild("image")){

                    String receiverImage=dataSnapshot.child("image").getValue().toString();

                    Picasso.get().load(receiverImage).into(viewHolder.receiverProfileImage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

       if(fromMessageType.equals("text")){

           viewHolder.receiverMessageText.setVisibility(View.INVISIBLE);
           viewHolder.receiverProfileImage.setVisibility(View.INVISIBLE);
           viewHolder.senderMessageText.setVisibility(View.INVISIBLE);


           if (fromUserId.equals(messageSenderId)){

               viewHolder.senderMessageText.setVisibility(View.VISIBLE);

               viewHolder.senderMessageText.setBackgroundResource(R.drawable.sender_message_id);
               viewHolder.senderMessageText.setTextColor(Color.BLACK);
               viewHolder.senderMessageText.setText(message.getMessage());
           }
           else
           {


               viewHolder.receiverProfileImage.setVisibility(View.VISIBLE);
               viewHolder.receiverMessageText.setVisibility(View.VISIBLE);
               viewHolder.receiverMessageText.setBackgroundResource(R.drawable.receiver_message_layout);
               viewHolder.receiverMessageText.setTextColor(Color.BLACK);
               viewHolder.receiverMessageText.setText(message.getMessage());
           }


       }

    }

    @Override
    public int getItemCount() {
        if (userMessagesList==null)
            return 0;
        return userMessagesList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView senderMessageText,receiverMessageText;
        private CircleImageView receiverProfileImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            senderMessageText=itemView.findViewById(R.id.sender_message);
            receiverMessageText=itemView.findViewById(R.id.receiver_message);
            receiverProfileImage=itemView.findViewById(R.id.message_profile_image);
        }
    }
}
