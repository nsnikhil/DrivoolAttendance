package com.nrs.nsnik.drivoolattendance.fragments;


import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
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
import com.nrs.nsnik.drivoolattendance.Objects.StudentObject;
import com.nrs.nsnik.drivoolattendance.R;
import com.nrs.nsnik.drivoolattendance.adapters.CursorRvAdapter;
import com.nrs.nsnik.drivoolattendance.adapters.ListAdapter;
import com.nrs.nsnik.drivoolattendance.data.TableNames;
import com.nrs.nsnik.drivoolattendance.interfaces.PickUpInterface;
import com.nrs.nsnik.drivoolattendance.services.SendSmsService;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class DeliveryFragment extends Fragment {

    @BindView(R.id.deliveryRecyclerView) RecyclerView mDeliveryRecyclerView;
    @BindView(R.id.deliveryPickUp) Button mDeliveryPickFirst;
    private static final String LOG_TAG = DeliveryFragment.class.getSimpleName();
    List<String> mStudentId;
    //private ListAdapter mListAdapter;
    private Unbinder mUnbinder;
    CursorRvAdapter mRecyclerAdapter;
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
        mStudentId = new ArrayList<>();
        mDeliveryRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(),2));
        mRecyclerAdapter = new CursorRvAdapter(getActivity(),TableNames.mAttendanceContentUri,getLoaderManager());
        mDeliveryRecyclerView.setAdapter(mRecyclerAdapter);
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
                    mRecyclerAdapter.removeItem(viewHolder.getAdapterPosition());
                } else {

                    AttendanceObject object = mRecyclerAdapter.getItem(viewHolder.getAdapterPosition());
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

                    mRecyclerAdapter.removeItem(viewHolder.getAdapterPosition());
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

}
