package com.example.s2784.layout;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

public class PagerAdapter extends FragmentStatePagerAdapter {

    int mNoOfTabs;
    public PagerAdapter(FragmentManager fm,int NumberOfTabs) {
        super(fm);
        this.mNoOfTabs = NumberOfTabs;

    }


    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                Log.d("PAGE","Page:0");
                Tab1 tab1 = new Tab1();
                return  tab1;
            case 1:
                Log.d("PAGE","Page:1");
                Tab2 tab2 = new Tab2();
                return  tab2;
            case 2:
                Log.d("PAGE","Page:2");
                Tab3 tab3 = new Tab3();
                return  tab3;
            case 3:
                Log.d("PAGE","Page:3");
                Tab4 tab4 = new Tab4();
                return tab4;
            default:
                return null;
        }

    }

    @Override
    public int getCount() {
        return mNoOfTabs;
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }
}

