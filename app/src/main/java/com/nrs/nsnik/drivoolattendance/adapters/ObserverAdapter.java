package com.nrs.nsnik.drivoolattendance.adapters;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.nrs.nsnik.drivoolattendance.Objects.AttendanceObject;
import com.nrs.nsnik.drivoolattendance.R;
import com.nrs.nsnik.drivoolattendance.data.DatabaseObserver;
import com.nrs.nsnik.drivoolattendance.data.TableNames;
import com.nrs.nsnik.drivoolattendance.interfaces.NotifyInterface;
import com.nrs.nsnik.drivoolattendance.interfaces.ObserverInterface;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class ObserverAdapter  extends RecyclerView.Adapter<ObserverAdapter.MyViewHolder> implements ObserverInterface{

    private List<AttendanceObject> mAttendanceList;
    private static final String NULL_VALUE = "N/A";
    private static final String TAG = ObserverAdapter.class.getSimpleName();
    private Context mContext;
    private int mFlag;
    private static DatabaseObserver mObserver;
    private GestureDetector mGestureDetector;
    private NotifyInterface mNotifyInterface;
    private int lastPosition = -1;
    private List<String> mPresentList;

    public ObserverAdapter(Context context, LoaderManager manager, int flag,NotifyInterface notifyInterface){
        mContext = context;
        mFlag = flag;
        mAttendanceList = new ArrayList<>();
        mPresentList = new ArrayList<>();
        mNotifyInterface = notifyInterface;
        mObserver  = new DatabaseObserver(mContext,manager);
        mGestureDetector = new GestureDetector(context, new GestureListener());
        mObserver.add(this);
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
        try{
            if(cursor!=null&&cursor.moveToFirst()){
                holder.mItemName.setText(cursor.getString(cursor.getColumnIndex(TableNames.table0.mName)));
            }
        }catch (NullPointerException e){
            e.printStackTrace();
        }finally {
            if(cursor!=null){
                cursor.close();
            }
        }
        setAnimation(holder.itemView, position);
    }

    @Override
    public int getItemCount() {
        return mAttendanceList.size();
    }

    @Override
    public void update(Cursor cursor) {
        makeAttendanceList(cursor);
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
        mNotifyInterface.notifyChange();
        mAttendanceList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mAttendanceList.size());
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            float x = e.getX();
            float y = e.getY();
            Log.d("Double Tap", "Tapped at: (" + x + "," + y + ")");
            return true;
        }
    }

    private void makeAttendanceList(Cursor cursor){
        mAttendanceList.clear();
        while (cursor!=null&&cursor.moveToNext()){
            String bTime = cursor.getString(cursor.getColumnIndex(TableNames.table1.mBoardingTime));
            String eTime = cursor.getString(cursor.getColumnIndex(TableNames.table1.mExitTime));
            switch (mFlag){
                case 0:
                    if(bTime.equalsIgnoreCase(NULL_VALUE)&&eTime.equalsIgnoreCase(NULL_VALUE)) {
                        int id = cursor.getInt(cursor.getColumnIndex(TableNames.table1.mId));
                        String sId = cursor.getString(cursor.getColumnIndex(TableNames.table1.mStudentId));
                        mAttendanceList.add(new AttendanceObject(id, sId, bTime, eTime));
                    }
                    break;
                case 1:
                    if(!bTime.equalsIgnoreCase(NULL_VALUE)&&eTime.equalsIgnoreCase(NULL_VALUE)) {
                        int id = cursor.getInt(cursor.getColumnIndex(TableNames.table1.mId));
                        String sId = cursor.getString(cursor.getColumnIndex(TableNames.table1.mStudentId));
                        mAttendanceList.add(new AttendanceObject(id, sId, bTime, eTime));
                    }
                    break;
                default:
                    int id = cursor.getInt(cursor.getColumnIndex(TableNames.table1.mId));
                    String sId = cursor.getString(cursor.getColumnIndex(TableNames.table1.mStudentId));
                    mAttendanceList.add(new AttendanceObject(id, sId, bTime, eTime));
            }
        }
        mNotifyInterface.notifyChange();
        notifyDataSetChanged();
    }

    public List<String> getMarkedCount(){
        return mPresentList;
    }

    public void resetMarked(){

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
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mItemChecked.getVisibility()==View.VISIBLE){
                        mPresentList.remove(mAttendanceList.get(getAdapterPosition()).getmStudentId());
                        mItemChecked.setVisibility(View.GONE);
                    }else {
                        mPresentList.add(mAttendanceList.get(getAdapterPosition()).getmStudentId());
                        mItemChecked.setVisibility(View.VISIBLE);
                    }
                }
            });
        }
    }
}
