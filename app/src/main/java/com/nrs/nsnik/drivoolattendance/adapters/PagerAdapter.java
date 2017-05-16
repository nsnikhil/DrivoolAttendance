package com.nrs.nsnik.drivoolattendance.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.nrs.nsnik.drivoolattendance.fragments.DeliveryFragment;
import com.nrs.nsnik.drivoolattendance.fragments.PickUpFragment;


public class PagerAdapter extends FragmentStatePagerAdapter{

    private CharSequence[] mTitles = new CharSequence[]{"Pick Up","Delivery"};

    public PagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        if(position==0){
            return new PickUpFragment();
        }else if(position==1){
            return new DeliveryFragment();
        }
        return null;
    }


    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if(position==0){
            return mTitles[0];
        }else if(position==1){
            return mTitles[1];
        }
        return null;
    }
}
