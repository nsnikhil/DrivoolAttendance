package com.nrs.nsnik.drivoolattendance.fragments;


import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nrs.nsnik.drivoolattendance.fragments.dialogFragments.FakeListDialogFragment;
import com.nrs.nsnik.drivoolattendance.fragments.dialogFragments.GetLocation;
import com.nrs.nsnik.drivoolattendance.Objects.AttendanceObject;
import com.nrs.nsnik.drivoolattendance.R;
import com.nrs.nsnik.drivoolattendance.adapters.ObserverAdapter;
import com.nrs.nsnik.drivoolattendance.data.TableNames;
import com.nrs.nsnik.drivoolattendance.interfaces.FakeItems;
import com.nrs.nsnik.drivoolattendance.interfaces.GetLocationInterface;
import com.nrs.nsnik.drivoolattendance.interfaces.NotifyInterface;
import com.nrs.nsnik.drivoolattendance.services.SendSmsService;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class PickUpFragment extends Fragment implements FakeItems, NotifyInterface, GetLocationInterface {

    private static final String NULL_VALUE = "N/A";
    private static final String ABSENT  = "ABSENT";
    @BindView(R.id.pickUpStartTrip) Button mStartTrip;
    @BindView(R.id.pickUpCounterContainer) LinearLayout mCounterContainer;
    @BindView(R.id.pickUpRecyclerView) RecyclerView mMainRecyclerView;
    Fragment mThisFragment;
    List<Integer> tempPosition;
    List<String> studentIds;
    GetLocation mGetLocation;
    private ObserverAdapter mObserverAdapter;
    private static final String TAG = PickUpFragment.class.getSimpleName();
    private static final int LOCATION_PERMISSION = 56;
    private Unbinder mUnbinder;
    String mLocationLink;
    private TextView mCounterText;
    private int mChildCount;

    public PickUpFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_pick_up, container, false);
        mUnbinder = ButterKnife.bind(this, v);
        initialize();
        listener();
        mThisFragment = this;
        setHasOptionsMenu(true);
        return v;
    }

    private void initialize() {
        mCounterText = (TextView) getActivity().findViewById(R.id.mainCounter);
        tempPosition = new ArrayList<>();
        studentIds  = new ArrayList<>();
        mMainRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        mObserverAdapter = new ObserverAdapter(getActivity(), getLoaderManager(), 0, this);
        mMainRecyclerView.setAdapter(mObserverAdapter);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.pickup_menu,menu);
        MenuItem item = menu.getItem(1);
        item.setVisible(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menuPickUpSend:
                tempPosition.clear();
                studentIds.clear();
                for(int i=0;i<mMainRecyclerView.getChildCount();i++){
                    View v = mMainRecyclerView.getChildAt(i);
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
        }
        return super.onOptionsItemSelected(item);
    }


    private void checkPermission(){
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                ||ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION);
        }else {
            if (getActivity().getContentResolver().query(TableNames.mContentUri, null, null, null, null).getCount() <= 0) {
                FakeListDialogFragment fakeListDialogFragment = new FakeListDialogFragment();
                fakeListDialogFragment.setCancelable(false);
                fakeListDialogFragment.setTargetFragment(mThisFragment, 121);
                fakeListDialogFragment.show(getFragmentManager(), "fakelist");
            } else {
                startSession();
            }
        }
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

    private void listener() {
        mStartTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               checkPermission();
            }
        });
    }

    private void sendSms(String sId,String messageText){
        String queryParam = "student/" + sId;
        Cursor cursor = getActivity().getContentResolver().query(Uri.withAppendedPath(TableNames.mContentUri, queryParam), null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            String name = cursor.getString(cursor.getColumnIndex(TableNames.table0.mName));
            String phoneNo = cursor.getString(cursor.getColumnIndex(TableNames.table0.mParentPhoneNo));
            Intent message = new Intent(getActivity(), SendSmsService.class);
            message.putExtra(getResources().getString(R.string.intentKeyMessage), name + " Picked Up at "+ messageText);
            message.putExtra(getResources().getString(R.string.intentkeyPhoneNo), phoneNo);
            getActivity().startService(message);
        } else {
            Toast.makeText(getActivity(), "Error", Toast.LENGTH_LONG).show();
        }
        Calendar calendar = Calendar.getInstance();
        ContentValues contentValues = new ContentValues();
        contentValues.put(TableNames.table1.mBoardingTime, calendar.getTimeInMillis());
        getActivity().getContentResolver().update(Uri.withAppendedPath(TableNames.mAttendanceContentUri, queryParam), contentValues, null, null);
    }

    private void startSession() {
        ContentValues sessionValues = new ContentValues();
        sessionValues.put(TableNames.table2.mTripStatus, 0);
        Uri uri = getActivity().getContentResolver().insert(TableNames.mSessionContentUri, sessionValues);
        if (uri != null) {
            addRefreshedTryAdapter();
        } else {
            Toast.makeText(getActivity(), "Error while starting new session", Toast.LENGTH_LONG).show();
        }
    }

    private void addRefreshedTryAdapter(){
        Cursor cursor = getActivity().getContentResolver().query(TableNames.mContentUri, null, null, null, null);
        if(cursor!=null){mChildCount = cursor.getCount();}
        Cursor sessionCursor = getActivity().getContentResolver().query(TableNames.mSessionContentUri, null, null, null, null);
        try {
            while (cursor != null && cursor.moveToNext()) {
                ContentValues contentValues = new ContentValues();
                contentValues.put(TableNames.table1.mStudentId, cursor.getString(cursor.getColumnIndex(TableNames.table0.mStudentId)));
                contentValues.put(TableNames.table1.mBoardingTime, NULL_VALUE);
                contentValues.put(TableNames.table1.mExitTime, NULL_VALUE);
                if (sessionCursor != null && sessionCursor.moveToLast()) {
                    contentValues.put(TableNames.table1.mSessionId, sessionCursor.getInt(sessionCursor.getColumnIndex(TableNames.table2.mSessionId)));
                }
                getActivity().getContentResolver().insert(TableNames.mAttendanceContentUri, contentValues);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (sessionCursor != null) {
                sessionCursor.close();
            }
        }
        mObserverAdapter = new ObserverAdapter(getActivity(), getLoaderManager(), 0, this);
        mMainRecyclerView.setAdapter(mObserverAdapter);
        mStartTrip.setVisibility(View.GONE);

        PreferenceManager.getDefaultSharedPreferences(getActivity()).edit()
                .putBoolean(getActivity().getResources().getString(R.string.prefTripStatus),true).apply();

        mCounterText.setVisibility(View.VISIBLE);
        if(mCounterText!=null){ mCounterText.setText("0/"+mChildCount);}else {Log.d(TAG, "Null");}
        //mAttenService = new Intent(getActivity(), AttendanceService.class);
        //getActivity().startService(mAttenService);

    }

    @Override
    public void onDestroy() {
        mUnbinder.unbind();
        super.onDestroy();
    }

    @Override
    public void itemAdded() {
        startSession();
    }

    private void notified() {
        int count = mChildCount-mMainRecyclerView.getAdapter().getItemCount();
        if(mCounterText!=null){  mCounterText.setText(count+"/"+mChildCount);}else {Log.d(TAG, "Null");}
        if (mMainRecyclerView.getAdapter().getItemCount() <= 0) {
            mCounterText.setVisibility(View.GONE);
            mStartTrip.setVisibility(View.VISIBLE);
            if(PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean(getActivity().getResources().getString(R.string.prefTripStatus),false)){
                mStartTrip.setEnabled(false);
                mStartTrip.setText(getActivity().getResources().getString(R.string.trip));
            }else {
                mStartTrip.setEnabled(true);
                mStartTrip.setText(getActivity().getResources().getString(R.string.startTrip));
            }
        } else {
            mCounterText.setVisibility(View.VISIBLE);
            mStartTrip.setVisibility(View.GONE);
        }
    }

    @Override
    public void notifyChange() {
        notified();
    }

}
