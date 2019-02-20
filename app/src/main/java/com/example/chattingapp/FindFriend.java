package com.example.chattingapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class FindFriend extends AppCompatActivity {

    private DatabaseReference mRef;
    private Toolbar mToolbar;
    private RecyclerView findFriendRecycler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friend);

        findFriendRecycler=findViewById(R.id.find_friend_container);
        mToolbar=findViewById(R.id.find_friend_bar);
        mRef=FirebaseDatabase.getInstance().getReference().child("Users");
        findFriendRecycler.setLayoutManager(new LinearLayoutManager(this));
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Find Friend");
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Contacts>options=new FirebaseRecyclerOptions.Builder<Contacts>()
                .setQuery(mRef,Contacts.class)
                .build();
        FirebaseRecyclerAdapter<Contacts,FindFreindViewHolder>adapter=
                new FirebaseRecyclerAdapter<Contacts, FindFreindViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull FindFreindViewHolder holder, final int position, @NonNull Contacts model) {

                Log.e("Friend Name",model.getName()+"");
                holder.userName.setText(model.getName());
                holder.userStatus.setText(model.getStatus());
                Picasso.get().load(model.getImage()).placeholder(R.drawable.profile_image).into(holder.circleImageView);

                holder.parent.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String visit_user_id=getRef(position).getKey();

                        Intent intent=new Intent(FindFriend.this,ProfileActivity.class);
                        intent.putExtra("userId",visit_user_id+"");
                        startActivity(intent);
                    }
                });
            }

            @NonNull
            @Override
            public FindFreindViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.find_friend_layout,viewGroup,false);
                return new FindFreindViewHolder(view);
            }
        };
        findFriendRecycler.setAdapter(adapter);
        adapter.startListening();
    }

    public static class FindFreindViewHolder extends RecyclerView.ViewHolder{

        CircleImageView circleImageView;
        TextView userName,userStatus;
        View parent;
        public FindFreindViewHolder(@NonNull View itemView) {
            super(itemView);
            parent=itemView;
            userName=itemView.findViewById(R.id.friend_name1);
            userStatus=itemView.findViewById(R.id.friend_status);
            circleImageView=itemView.findViewById(R.id.image_profile_item);
        }
    }
}
