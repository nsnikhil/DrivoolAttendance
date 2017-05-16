package com.nrs.nsnik.drivoolattendance.fragments;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.nrs.nsnik.drivoolattendance.R;
import com.nrs.nsnik.drivoolattendance.adapters.ListAdapter;
import com.nrs.nsnik.drivoolattendance.data.TableNames;
import com.nrs.nsnik.drivoolattendance.interfaces.PickUpInterface;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class DeliveryFragment extends Fragment {

    @BindView(R.id.deliveryRecyclerView) RecyclerView mDeliveryRecyclerView;
    @BindView(R.id.deliveryPickUp) Button mDeliveryPickFirst;
    private static final String LOG_TAG = DeliveryFragment.class.getSimpleName();
    List<String> mStudentId;
    private ListAdapter mListAdapter;
    private Unbinder mUnbinder;
    private static final int LOADER_ID = 584;

    public DeliveryFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_delivery, container, false);
        mUnbinder = ButterKnife.bind(this,v);
        initialize();
        listeners();
        return v;
    }

    private void initialize(){
        mStudentId = new ArrayList<>();
        mDeliveryRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(),2));
        mListAdapter = new ListAdapter(getActivity(),null);
        mDeliveryRecyclerView.setAdapter(mListAdapter);
    }

    private void listeners(){
        mDeliveryPickFirst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewPager pager = (ViewPager) getActivity().findViewById(R.id.mainViewPager);
                pager.setCurrentItem(0,true);
            }
        });
    }

    @Override
    public void onDestroy() {
        mUnbinder.unbind();
        super.onDestroy();
    }

}
