package com.nrs.nsnik.drivoolattendance.fragments;


import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.nrs.nsnik.drivoolattendance.Objects.AttendanceObject;
import com.nrs.nsnik.drivoolattendance.R;
import com.nrs.nsnik.drivoolattendance.TripSummaryActivity;
import com.nrs.nsnik.drivoolattendance.adapters.ObserverAdapter;
import com.nrs.nsnik.drivoolattendance.data.TableNames;
import com.nrs.nsnik.drivoolattendance.fragments.dialogFragments.GetLocation;
import com.nrs.nsnik.drivoolattendance.interfaces.GetLocationInterface;
import com.nrs.nsnik.drivoolattendance.interfaces.NotifyInterface;
import com.nrs.nsnik.drivoolattendance.services.SendSmsService;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class DeliveryFragment extends Fragment implements NotifyInterface,GetLocationInterface{

    @BindView(R.id.deliveryRecyclerView) RecyclerView mDeliveryRecyclerView;
    private static final String ABSENT  = "ABSENT";
    @BindView(R.id.deliveryPickUp) Button mDeliveryPickFirst;
    private static final String NULL_VALUE = "N/A";
    private static final String TAG = DeliveryFragment.class.getSimpleName();
    private Unbinder mUnbinder;
    GetLocation mGetLocation;
    List<Integer> tempPosition;
    List<String> studentIds;
    private ObserverAdapter mObserverAdapter;
    Fragment mThisFragment;
    String mLocationLink;


    public DeliveryFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_delivery, container, false);
        mUnbinder = ButterKnife.bind(this,v);
        initialize();
        listeners();
        mThisFragment = this;
        setHasOptionsMenu(true);
        return v;
    }

    private void initialize(){
        tempPosition = new ArrayList<>();
        studentIds  = new ArrayList<>();
        mDeliveryRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(),2));
        mObserverAdapter = new ObserverAdapter(getActivity(),getLoaderManager(),1,this);
        mDeliveryRecyclerView.setAdapter(mObserverAdapter);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.pickup_menu,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menuPickUpSend:
                tempPosition.clear();
                studentIds.clear();
                for(int i=0;i<mDeliveryRecyclerView.getChildCount();i++){
                    View v = mDeliveryRecyclerView.getChildAt(i);
                    ImageView image = (ImageView) v.findViewById(R.id.itemCheckedStatus);
                    if(image.getVisibility()==View.VISIBLE){
                        studentIds.add(mObserverAdapter.getItem(i).getmStudentId());
                        tempPosition.add(i);
                    }else {
                        Log.d(TAG, "Gone");
                    }
                }
                if(tempPosition.size()>0) {
                    mGetLocation = new GetLocation();
                    mGetLocation.setTargetFragment(mThisFragment, 14);
                    mGetLocation.show(getFragmentManager(), "location");
                }
                break;
            case R.id.menuPickUpFinish:
                boolean statuts = PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean(getActivity().getResources().getString(R.string.prefTripStatus),false);
                if(!statuts){
                    Toast.makeText(getActivity(), "Start the trip first", Toast.LENGTH_SHORT).show();
                }else if( mDeliveryRecyclerView.getChildCount()>0){
                    Toast.makeText(getActivity(), "Deliver the picked up students", Toast.LENGTH_SHORT).show();
                } else {
                    finishTrip();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void finishTrip(){
        int sessionId=-1;
        Cursor sessionCursor = getActivity().getContentResolver().query(TableNames.mAttendanceContentUri,null,null,null,null);
        try {
            if(sessionCursor!=null&&sessionCursor.moveToLast()){
                sessionId = sessionCursor.getInt(sessionCursor.getColumnIndex(TableNames.table1.mSessionId));
                String sessionUri = "session/"+sessionId;
                Cursor current  = getActivity().getContentResolver().query(Uri.withAppendedPath(TableNames.mAttendanceContentUri,sessionUri),null,null,null,null);
                try {
                    while (current != null && current.moveToNext()) {
                        ContentValues cv = new ContentValues();
                        if (current.getString(current.getColumnIndex(TableNames.table1.mBoardingTime)).equalsIgnoreCase(NULL_VALUE)) {
                            cv.put(TableNames.table1.mBoardingTime, ABSENT);
                            cv.put(TableNames.table1.mExitTime, ABSENT);
                            String queryParam = "student/" + current.getString(current.getColumnIndex(TableNames.table1.mStudentId));
                            getActivity().getContentResolver().update(Uri.withAppendedPath(TableNames.mAttendanceContentUri, queryParam), cv, null, null);
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }finally {
                    if(current!=null){
                        current.close();
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(sessionCursor!=null){
                sessionCursor.close();
            }
        }
        Intent summary = new Intent(getActivity(), TripSummaryActivity.class);
        summary.putExtra(getResources().getString(R.string.intentKeySessionId),sessionId);
        summary.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        summary.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(summary);
    }

    @Override
    public void getLocation(Location location) {
        double mLat = location.getLatitude();
        double mLng = location.getLongitude();
        mGetLocation.dismiss();
        mLocationLink = getResources().getString(R.string.urlLocationUrl)+ mLat +","+ mLng;
        removeItems(mLocationLink);
    }

    private void removeItems(String message){
        for(int i = tempPosition.get(tempPosition.size()-1);i>tempPosition.size();i--){
            mObserverAdapter.removeItem(i);
        }
        for(String sId : studentIds){
            sendSms(sId,message);
        }
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

    private void sendSms(String sId,String messageText){
        String queryParam = "student/"+sId;
        Cursor cursor = getActivity().getContentResolver().query(Uri.withAppendedPath(TableNames.mContentUri,queryParam),null,null,null,null);
        if(cursor!=null&&cursor.moveToFirst()){
            String name = cursor.getString(cursor.getColumnIndex(TableNames.table0.mName));
            String phoneNo = cursor.getString(cursor.getColumnIndex(TableNames.table0.mParentPhoneNo));
            Intent message = new Intent(getActivity(),SendSmsService.class);
            message.putExtra(getResources().getString(R.string.intentKeyMessage),name+" Delivered at " + messageText);
            message.putExtra(getResources().getString(R.string.intentkeyPhoneNo),phoneNo);
            getActivity().startService(message);
        }else {
            Toast.makeText(getActivity(),"Error",Toast.LENGTH_LONG).show();
        }
        Calendar calendar = Calendar.getInstance();
        ContentValues contentValues = new ContentValues();
        contentValues.put(TableNames.table1.mExitTime,calendar.getTimeInMillis());
        getActivity().getContentResolver().update(Uri.withAppendedPath(TableNames.mAttendanceContentUri,queryParam),contentValues,null,null);
    }

    @Override
    public void onDestroy() {
        mUnbinder.unbind();
        super.onDestroy();
    }

    private void notified(){
        if(mDeliveryRecyclerView.getAdapter().getItemCount()<=0){
            mDeliveryPickFirst.setVisibility(View.VISIBLE);
            if(PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean(getActivity().getResources().getString(R.string.prefTripStatus),false)){
                if(tripFinished()){
                    PreferenceManager.getDefaultSharedPreferences(getActivity()).edit().putBoolean(getString(R.string.prefTripStatus),false).apply();
                }
            }
        }else {
            mDeliveryPickFirst.setVisibility(View.GONE);
        }
    }

    private boolean tripFinished(){
        int counter = 0;
        int count = -1;
        int sessionId=-1;
        Cursor sessionCursor = getActivity().getContentResolver().query(TableNames.mAttendanceContentUri,null,null,null,null);
        try{
            if(sessionCursor!=null&&sessionCursor.moveToLast()){
                sessionId  = sessionCursor.getInt(sessionCursor.getColumnIndex(TableNames.table1.mSessionId));
                String sessionUri = "session/"+sessionId;
                Cursor current  = getActivity().getContentResolver().query(Uri.withAppendedPath(TableNames.mAttendanceContentUri,sessionUri),null,null,null,null);
                if(current!=null){count = current.getCount();}
                try{
                    while (current!=null&&current.moveToNext()){
                        if(current.getString(current.getColumnIndex(TableNames.table1.mBoardingTime)).equalsIgnoreCase(NULL_VALUE)||
                                current.getString(current.getColumnIndex(TableNames.table1.mExitTime)).equalsIgnoreCase(NULL_VALUE)){
                            return false;
                        }else {
                            counter++;
                        }
                    }
                    if(counter==count){
                        mDeliveryPickFirst.setVisibility(View.GONE);
                        ContentValues contentValues = new ContentValues();
                        contentValues.put(TableNames.table2.mTripStatus, 1);
                        getActivity().getContentResolver().update(Uri.withAppendedPath(TableNames.mSessionContentUri
                                ,String.valueOf(sessionCursor.getInt(sessionCursor.getColumnIndex(TableNames.table1.mSessionId)))),contentValues,null,null);
                        PreferenceManager.getDefaultSharedPreferences(getActivity()).edit().putBoolean(getString(R.string.prefTripStatus),false).apply();
                        Intent summary = new Intent(getActivity(), TripSummaryActivity.class);
                        summary.putExtra(getResources().getString(R.string.intentKeySessionId),sessionId);
                        summary.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        summary.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(summary);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }finally {
                    if(current!=null){
                        current.close();
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(sessionCursor!=null){
                sessionCursor.close();
            }
        }
        return true;
    }

    @Override
    public void notifyChange() {
        notified();
    }

}
