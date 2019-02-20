package com.example.chattingapp;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.chattingapp.Fragment.ChatFragment;
import com.example.chattingapp.Fragment.ContactFragment;
import com.example.chattingapp.Fragment.GroupFragment;
import com.example.chattingapp.Fragment.RequestFragment;

public class FmAdapter extends FragmentPagerAdapter {

    private  Context context;
    public FmAdapter(Context context, FragmentManager fm) {
        super(fm);
        this.context=context;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        String title;
        switch(position){
            case 0:
                title="Chat";
                break;
            case 1:
                title="Groups";
                break;
            case 2:
               title="Contact";
                break;
            case 3:
                title="Requests";
                break;
            default:
                title="Chat";

        }
        return title;
    }

    @Override
    public Fragment getItem(int i) {
        Fragment fragment;
        switch(i){
            case 0:
                fragment=new ChatFragment();
                break;
            case 1:
                fragment=new GroupFragment();
                break;
            case 2:
                fragment=new ContactFragment();
                break;
            case 3:
                fragment=new RequestFragment();
                break;
            default:
                fragment=new ChatFragment();

        }
        return fragment;
    }

    @Override
    public int getCount() {
        return 4;
    }
}
