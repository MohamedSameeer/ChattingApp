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
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chattingapp.Contacts;
import com.example.chattingapp.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
public class RequestFragment extends Fragment {

    private View view;
    private RecyclerView recyclerView;
    private DatabaseReference myRequestRef,userRef,contactRef;
    private FirebaseAuth mAuth;
    private String currentUserId;
    public RequestFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_request, container, false);

        mAuth=FirebaseAuth.getInstance();
        currentUserId=mAuth.getCurrentUser().getUid();
        contactRef=FirebaseDatabase.getInstance().getReference().child("Contacts");
        userRef=FirebaseDatabase.getInstance().getReference().child("Users");
        myRequestRef = FirebaseDatabase.getInstance().getReference().child("Chat Requests");
        recyclerView=view.findViewById(R.id.request_container);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Contacts>options=
                new FirebaseRecyclerOptions.Builder<Contacts>()
                .setQuery(myRequestRef.child(currentUserId),Contacts.class)
                .build();

        FirebaseRecyclerAdapter<Contacts, RequestViewHolder>adapter=
                new FirebaseRecyclerAdapter<Contacts, RequestViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull final RequestViewHolder holder, final int position, @NonNull final Contacts model) {

                        holder.acceptButton.setVisibility(View.VISIBLE);
                        holder.refuseButton.setVisibility(View.VISIBLE);
                        final String list_user_id=getRef(position).getKey();
                        Log.e("RequestFragment",list_user_id+"");
                        DatabaseReference getTypeRef=getRef(position).child("request_type").getRef();
                        getTypeRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                               // Log.e("Request0",dataSnapshot.getValue().toString());
                                if (dataSnapshot.exists()){

                                    String type=dataSnapshot.getValue().toString();
                                    Log.e("Request1",type+"");
                                    if (type.equals("received")){
                                        userRef.child(list_user_id).addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                if (dataSnapshot.hasChild("image")){
                                                  final   String profileImage=dataSnapshot.child("image").getValue().toString();
                                                    Picasso.get().load(profileImage).into(holder.profileImage);
                                                }

                                                    final   String profileName=dataSnapshot.child("name").getValue().toString();
                                                    final   String profileStatus=dataSnapshot.child("status").getValue().toString();

                                                    Log.e("Request",profileName);
                                                    Log.e("Request",profileStatus);
                                                    holder.userName.setText(profileName);
                                                    holder.userStatus.setText(profileStatus);
                                                    holder.acceptButton.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            acceptRequest(list_user_id);
                                                        }
                                                    });
                                                    holder.refuseButton.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            refuseRequest(list_user_id);
                                                        }
                                                    });
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });
                                    }
                                    else if(type.equals("sent")){
                                        holder.acceptButton.setVisibility(View.GONE);
                                        holder.refuseButton.setText("Cancel Request");

                                        userRef.child(list_user_id).addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                if (dataSnapshot.hasChild("image")){
                                                    final   String profileImage=dataSnapshot.child("image").getValue().toString();
                                                    Picasso.get().load(profileImage).into(holder.profileImage);
                                                }

                                                final   String profileName=dataSnapshot.child("name").getValue().toString();
                                                final   String profileStatus=dataSnapshot.child("status").getValue().toString();

                                                Log.e("Request",profileName);
                                                Log.e("Request",profileStatus);
                                                holder.userName.setText(profileName);
                                                holder.userStatus.setText(profileStatus);

                                                holder.refuseButton.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        refuseRequest(list_user_id);
                                                    }
                                                });
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }

                    @NonNull
                    @Override
                    public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                       View view=LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.find_friend_layout,viewGroup,false);

                       return new RequestViewHolder(view);
                    }
                };

        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    private void acceptRequest(final String senderId) {

        contactRef.child(currentUserId).child(senderId).child("Contacts").setValue("Saved").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){

                    contactRef.child(senderId).child(currentUserId).child("Contacts").setValue("Saved").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                myRequestRef.child(currentUserId).child(senderId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        myRequestRef.child(senderId).child(currentUserId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()){
                                                    Toast.makeText(view.getContext(), "Accept Successful", Toast.LENGTH_SHORT).show();;
                                                }
                                            }
                                        });
                                    }
                                });
                            }
                        }
                    });
                }
            }
        });
    }

    private void refuseRequest(final String senderId){
        myRequestRef.child(currentUserId).child(senderId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                myRequestRef.child(senderId).child(currentUserId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(view.getContext(), "Accept Successful", Toast.LENGTH_SHORT).show();;
                        }
                    }
                });
            }
        });

    }

    public static class RequestViewHolder extends RecyclerView.ViewHolder{

        TextView userName,userStatus;
        CircleImageView profileImage;
        View parent;
        Button acceptButton,refuseButton;
        public RequestViewHolder(@NonNull View itemView) {
            super(itemView);

            parent=itemView;
            profileImage=itemView.findViewById(R.id.image_profile_item);
            userName=itemView.findViewById(R.id.friend_name1);
            userStatus=itemView.findViewById(R.id.friend_status);
            acceptButton=itemView.findViewById(R.id.accept_request);
            refuseButton=itemView.findViewById(R.id.refuse_request);

        }
    }
}
