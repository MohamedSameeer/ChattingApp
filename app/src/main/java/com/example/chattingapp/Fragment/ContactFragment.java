package com.example.chattingapp.Fragment;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
public class ContactFragment extends Fragment {


    public ContactFragment() {
        // Required empty public constructor
    }
    View view;
    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;
    DatabaseReference contactRef,userRef;
    String currentUserId;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

       view =inflater.inflate(R.layout.fragment_contact, container, false);

        currentUserId= FirebaseAuth.getInstance().getCurrentUser().getUid()+"";
        contactRef= FirebaseDatabase.getInstance().getReference().child("Contacts").child(currentUserId) ;
        userRef=FirebaseDatabase.getInstance().getReference().child("Users");
       recyclerView=view.findViewById(R.id.contact_container);
       linearLayoutManager=new LinearLayoutManager(view.getContext());
       recyclerView.setLayoutManager(linearLayoutManager);


       return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions options=new FirebaseRecyclerOptions.Builder<Contacts>()
                .setQuery(contactRef,Contacts.class)
                .build();

        FirebaseRecyclerAdapter<Contacts,ContactViewHolder>adapter=
                new FirebaseRecyclerAdapter<Contacts, ContactViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull final ContactViewHolder holder, int position, @NonNull Contacts model) {

                        String userIDs=getRef(position).getKey();

                        userRef.child(userIDs).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                if(dataSnapshot.hasChild("image")){


                                    String profileImage=dataSnapshot.child("image").getValue().toString();
                                    String profileName=dataSnapshot.child("name").getValue().toString();
                                    String profileStatus=dataSnapshot.child("status").getValue().toString();

                                    holder.userName.setText(profileName);
                                    holder.userStatus.setText(profileStatus);
                                    Picasso.get().load(profileImage).into(holder.profileImage);
                                }else{

                                    String profileName=dataSnapshot.child("name").getValue().toString();
                                    String profileStatus=dataSnapshot.child("status").getValue().toString();

                                    holder.userName.setText(profileName);
                                    holder.userStatus.setText(profileStatus);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }

                    @NonNull
                    @Override
                    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

                        View view=LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.find_friend_layout,viewGroup,false);

                        return new ContactViewHolder(view);

                    }
                };

        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    public static class ContactViewHolder extends RecyclerView.ViewHolder{

        TextView userName,userStatus;
        CircleImageView profileImage;
        public ContactViewHolder(@NonNull View itemView) {
            super(itemView);

            profileImage=itemView.findViewById(R.id.image_profile_item);
            userName=itemView.findViewById(R.id.friend_name1);
            userStatus=itemView.findViewById(R.id.friend_status);
        }
    }
}
