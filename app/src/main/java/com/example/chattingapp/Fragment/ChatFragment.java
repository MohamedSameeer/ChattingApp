package com.example.chattingapp.Fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.chattingapp.ChatActivity;
import com.example.chattingapp.Contacts;
import com.example.chattingapp.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChatFragment extends Fragment {


    public ChatFragment() {
        // Required empty public constructor
    }

    private View view;
    private RecyclerView recyclerView;
    private FirebaseAuth mAuth;
    private String relImage;
    private DatabaseReference chatRef,userRef;
    private String currentUserId;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        view=inflater.inflate(R.layout.fragment_chat, container, false);

        mAuth=FirebaseAuth.getInstance();
        currentUserId=mAuth.getCurrentUser().getUid();
        chatRef= FirebaseDatabase.getInstance().getReference().child("Contacts").child(currentUserId);
        userRef=FirebaseDatabase.getInstance().getReference().child("Users");
        recyclerView=view.findViewById(R.id.chat_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Contacts>options=new FirebaseRecyclerOptions.Builder<Contacts>()
                .setQuery(chatRef,Contacts.class)
                .build();


        FirebaseRecyclerAdapter<Contacts,ChatViewHolder>adapter=
                new FirebaseRecyclerAdapter<Contacts, ChatViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull final ChatViewHolder holder, final int position, @NonNull Contacts model) {

                        final String usersIDs=getRef(position).getKey();

                        userRef.child(usersIDs).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                             if(dataSnapshot.exists()){
                                 if(dataSnapshot.hasChild("image")){

                                     relImage=dataSnapshot.child("image").getValue().toString();
                                     Picasso.get().load(relImage).into(holder.profileImage);
                                 }

                                 final String relName=dataSnapshot.child("name").getValue().toString();
                                 final String relStatus=dataSnapshot.child("status").getValue().toString();

                                 holder.userName.setText(relName);
                                 holder.userStatus.setText("Last Seen :"+"\n"+"Date "+" Time");

                                 holder.itemView.setOnClickListener(new View.OnClickListener() {
                                     @Override
                                     public void onClick(View v) {
                                         Intent i=new Intent(view.getContext(), ChatActivity.class);
                                         i.putExtra("visit_user_id",usersIDs+"");
                                         i.putExtra("visit_user_name",relName+"");
                                         i.putExtra("visit_user_image",relImage+"");


                                         startActivity(i);
                                     }
                                 });
                             }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }

                    @NonNull
                    @Override
                    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                        View view=LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.find_friend_layout,viewGroup,false);
                        return new ChatViewHolder(view);
                    }
                };

        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    public static class ChatViewHolder extends RecyclerView.ViewHolder{

        TextView userName,userStatus;
        CircleImageView profileImage;
        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);

            profileImage=itemView.findViewById(R.id.image_profile_item);
            userName=itemView.findViewById(R.id.friend_name1);
            userStatus=itemView.findViewById(R.id.friend_status);
        }
    }
}
