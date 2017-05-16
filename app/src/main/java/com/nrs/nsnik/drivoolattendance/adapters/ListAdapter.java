package com.nrs.nsnik.drivoolattendance.adapters;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
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

import com.nrs.nsnik.drivoolattendance.Objects.StudentObject;
import com.nrs.nsnik.drivoolattendance.R;
import com.nrs.nsnik.drivoolattendance.data.TableNames;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class ListAdapter extends RecyclerView.Adapter<ListAdapter.MyViewHolder>{

    private Context mContext;
    private List<StudentObject> mList;
    private Cursor mCursor;
    private GestureDetector gestureDetector;
    private int lastPosition = -1;
    private List<StudentObject> presentIds;

    public ListAdapter(Context context,Cursor cursor) {
        mContext = context;
        mCursor  = cursor;
        makeList();
        gestureDetector = new GestureDetector(context, new GestureListener());
        presentIds = new ArrayList<>();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder( LayoutInflater.from(mContext).inflate(R.layout.single_item,parent,false));
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        if(mCursor.moveToPosition(position)){
            StudentObject object = mList.get(position);
            holder.mItemName.setText(object.getmName());
        }
        setAnimation(holder.itemView, position);
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


    @Override
    public int getItemCount() {
        return mList.size();
    }

    private void makeList(){
        mList = new ArrayList<>();
        while (mCursor!=null&&mCursor.moveToNext()){
            int id = mCursor.getInt(mCursor.getColumnIndex(TableNames.table0.mId));
            String name = mCursor.getString(mCursor.getColumnIndex(TableNames.table0.mName));
            String studentId = mCursor.getString(mCursor.getColumnIndex(TableNames.table0.mStudentId));
            String parentNo = mCursor.getString(mCursor.getColumnIndex(TableNames.table0.mParentPhoneNo));
            mList.add(new StudentObject(id,name,studentId,parentNo));
        }
    }

    public StudentObject getItem(int position){
        return mList.get(position);
    }

    public void removeItem(int position) {
        mList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mList.size());
    }

    private void setAnimation(View viewToAnimate, int position) {
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.fly_in);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
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
