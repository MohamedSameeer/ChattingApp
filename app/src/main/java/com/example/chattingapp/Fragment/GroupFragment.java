package com.example.chattingapp.Fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.chattingapp.GroupChat;
import com.example.chattingapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * A simple {@link Fragment} subclass.
 */
public class GroupFragment extends Fragment {

    private View view;
    private ArrayAdapter<String> arrayAdapter;
    private ListView listView;
    private ArrayList<String>listOfGroup;
    private DatabaseReference databaseReference;

    public GroupFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view= inflater.inflate(R.layout.fragment_group, container, false);

         intialize();
         databaseReference.addValueEventListener(new ValueEventListener() {
             @Override
             public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                 Iterator iterator=dataSnapshot.getChildren().iterator();
                 Set<String> set=new HashSet<>();

                 while (iterator.hasNext()){

                     set.add(((DataSnapshot)iterator.next()).getKey());
                     Log.e("GroupFragment","WTF");
                 }
                 listOfGroup.clear();
                 listOfGroup.addAll(set);
                 Log.e("Array List","  "+listOfGroup.get(0));
                 arrayAdapter.notifyDataSetChanged();
             }

             @Override
             public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e("GroupFragment",databaseError.getMessage()+"");
             }
         });

         listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
             @Override
             public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                 String groupName=parent.getItemAtPosition(position).toString();
                 Intent intent=new Intent(getContext(), GroupChat.class);
                 intent.putExtra("groupName",groupName+"");
                 startActivity(intent);
             }
         });

      return view;
    }

    private void intialize() {

        databaseReference= FirebaseDatabase.getInstance().getReference().child("Groups");
        listView=view.findViewById(R.id.list_of_groups);
        listOfGroup=new ArrayList<>();
        arrayAdapter=
                new ArrayAdapter<String>(getContext(),android.R.layout.simple_expandable_list_item_1,listOfGroup);
        listView.setAdapter(arrayAdapter);



    }

}
