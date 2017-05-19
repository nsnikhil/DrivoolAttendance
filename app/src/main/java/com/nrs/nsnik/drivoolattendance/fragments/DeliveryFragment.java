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
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.nrs.nsnik.drivoolattendance.Objects.AttendanceObject;
import com.nrs.nsnik.drivoolattendance.R;
import com.nrs.nsnik.drivoolattendance.TripSummaryActivity;
import com.nrs.nsnik.drivoolattendance.adapters.ObserverAdapter;
import com.nrs.nsnik.drivoolattendance.data.TableNames;
import com.nrs.nsnik.drivoolattendance.interfaces.NotifyInterface;
import com.nrs.nsnik.drivoolattendance.services.SendSmsService;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class DeliveryFragment extends Fragment implements NotifyInterface{

    @BindView(R.id.deliveryRecyclerView) RecyclerView mDeliveryRecyclerView;
    @BindView(R.id.deliveryPickUp) Button mDeliveryPickFirst;
    private static final String NULL_VALUE = "N/A";
    private static final String LOG_TAG = DeliveryFragment.class.getSimpleName();
    private Unbinder mUnbinder;
    private ObserverAdapter mObserverAdapter;
    private Paint p = new Paint();
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
        mDeliveryRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(),2));
        mObserverAdapter = new ObserverAdapter(getActivity(),getLoaderManager(),1,this);
        mDeliveryRecyclerView.setAdapter(mObserverAdapter);
    }

    private void listeners(){
        mDeliveryPickFirst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewPager pager = (ViewPager) getActivity().findViewById(R.id.mainViewPager);
                pager.setCurrentItem(0,true);
            }
        });
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                if (direction == ItemTouchHelper.LEFT){
                    mObserverAdapter.removeItem(viewHolder.getAdapterPosition());
                } else {
                    AttendanceObject object = mObserverAdapter.getItem(viewHolder.getAdapterPosition());
                    String queryParam = "student/"+object.getmStudentId();
                    Cursor cursor = getActivity().getContentResolver().query(Uri.withAppendedPath(TableNames.mContentUri,queryParam),null,null,null,null);
                    if(cursor!=null&&cursor.moveToFirst()){
                        String name = cursor.getString(cursor.getColumnIndex(TableNames.table0.mName));
                        String phoneNo = cursor.getString(cursor.getColumnIndex(TableNames.table0.mParentPhoneNo));
                        Intent message = new Intent(getActivity(),SendSmsService.class);
                        message.putExtra(getResources().getString(R.string.intentKeyMessage),name+" Delivered");
                        message.putExtra(getResources().getString(R.string.intentkeyPhoneNo),phoneNo);
                        getActivity().startService(message);
                    }else {
                        Toast.makeText(getActivity(),"Error",Toast.LENGTH_LONG).show();
                    }
                    Calendar calendar = Calendar.getInstance();
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(TableNames.table1.mExitTime,calendar.getTimeInMillis());
                    getActivity().getContentResolver().update(Uri.withAppendedPath(TableNames.mAttendanceContentUri,queryParam),contentValues,null,null);
                    mObserverAdapter.removeItem(viewHolder.getAdapterPosition());
                }
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                Bitmap icon;
                if(actionState == ItemTouchHelper.ACTION_STATE_SWIPE){
                    View itemView = viewHolder.itemView;
                    float height = (float) itemView.getBottom() - (float) itemView.getTop();
                    float width = height / 3;
                    if(dX > 0){
                        p.setColor(Color.parseColor("#388E3C"));
                        RectF background = new RectF((float) itemView.getLeft(), (float) itemView.getTop(),  itemView.getRight(),(float) itemView.getBottom());
                        c.drawRect(background,p);
                        icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_done_white_48dp);
                        RectF icon_dest = new RectF((float) itemView.getLeft()  ,(float) itemView.getTop() + width,(float) itemView.getLeft()+ width,(float)itemView.getBottom() - width);
                        c.drawBitmap(icon,null,icon_dest,p);

                    } else {
                        p.setColor(Color.parseColor("#D32F2F"));
                        RectF background = new RectF((float) itemView.getRight()  , (float) itemView.getTop(),itemView.getLeft(), (float) itemView.getBottom());
                        c.drawRect(background,p);
                        icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_close_white_48dp);
                        RectF icon_dest = new RectF((float) itemView.getRight() - width  ,(float) itemView.getTop() + width,(float) itemView.getRight() ,(float)itemView.getBottom() - width);
                        c.drawBitmap(icon,null,icon_dest,p);
                    }
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }

        }).attachToRecyclerView(mDeliveryRecyclerView);
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
        Cursor sessionCursor = getActivity().getContentResolver().query(TableNames.mAttendanceContentUri,null,null,null,null);
        try{
            if(sessionCursor!=null&&sessionCursor.moveToLast()){
                String sessionUri = "session/"+sessionCursor.getInt(sessionCursor.getColumnIndex(TableNames.table1.mSessionId));
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
