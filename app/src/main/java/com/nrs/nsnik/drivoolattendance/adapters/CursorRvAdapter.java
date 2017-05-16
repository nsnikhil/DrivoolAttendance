package com.nrs.nsnik.drivoolattendance.adapters;


import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.nrs.nsnik.drivoolattendance.Objects.AttendanceObject;
import com.nrs.nsnik.drivoolattendance.Objects.StudentObject;
import com.nrs.nsnik.drivoolattendance.R;
import com.nrs.nsnik.drivoolattendance.data.TableNames;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CursorRvAdapter extends RecyclerView.Adapter<CursorRvAdapter.MyViewHolder> implements LoaderManager.LoaderCallbacks<Cursor>{

    private Context mContext;
    private List<AttendanceObject> mAttendanceList;
    private Uri mUri;
    private int lastPosition = -1;
    LoaderManager mLoaderManager;
    private static final String NULL_VALUE = "N/A";

    public CursorRvAdapter(Context context,Uri uri,LoaderManager manager){
        mContext = context;
        mUri = uri;
        mAttendanceList = new ArrayList<>();
        mLoaderManager = manager;
        mLoaderManager.initLoader(1,null,this);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder( LayoutInflater.from(mContext).inflate(R.layout.single_item,parent,false));
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        AttendanceObject object = mAttendanceList.get(position);
        String queryParam = "student/"+object.getmStudentId();
        Cursor cursor = mContext.getContentResolver().query(Uri.withAppendedPath(TableNames.mContentUri,queryParam),null,null,null,null);
        if(cursor.moveToFirst()){
            holder.mItemName.setText(cursor.getString(cursor.getColumnIndex(TableNames.table0.mName)));
        }
        setAnimation(holder.itemView, position);
    }

    @Override
    public int getItemCount() {
        return mAttendanceList.size();
    }

    public AttendanceObject getItem(int position){
        return mAttendanceList.get(position);
    }

    private void setAnimation(View viewToAnimate, int position) {
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.fly_in);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }

    public void removeItem(int position) {
        mAttendanceList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mAttendanceList.size());
    }


    private void makeAttendanceList(Cursor cursor){
        mAttendanceList.clear();
        while (cursor!=null&&cursor.moveToNext()){
            String eTime = cursor.getString(cursor.getColumnIndex(TableNames.table1.mExitTime));
            if(eTime.equalsIgnoreCase(NULL_VALUE)) {
                int id = cursor.getInt(cursor.getColumnIndex(TableNames.table1.mId));
                String sId = cursor.getString(cursor.getColumnIndex(TableNames.table1.mStudentId));
                String bTime = cursor.getString(cursor.getColumnIndex(TableNames.table1.mBoardingTime));
                mAttendanceList.add(new AttendanceObject(id, sId, bTime, eTime));
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if(mUri!=null){
            return new CursorLoader(mContext,mUri,null,null,null,null);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        makeAttendanceList(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAttendanceList.clear();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.itemName) TextView mItemName;
        @BindView(R.id.itemImage) ImageView mItemImage;
        @BindView(R.id.itemCard) CardView mItemCard;
        @BindView(R.id.itemCheckedStatus) ImageView mItemChecked;
        public MyViewHolder(View itemView) {
            super(itemView);
            itemView.setClickable(true);
            ButterKnife.bind(this,itemView);
        }
    }
}
