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

import com.nrs.nsnik.drivoolattendance.Objects.StudentObject;
import com.nrs.nsnik.drivoolattendance.R;
import com.nrs.nsnik.drivoolattendance.data.TableNames;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class CursorRecyclerViewAdapter extends RecyclerView.Adapter<CursorRecyclerViewAdapter.MyViewHolder> implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int LOADER_ID = 458;
    private Context mContext;
    private List<StudentObject> mList;
    private Uri mUri;
    private int lastPosition = -1;
    private LoaderManager mLoaderManager;

    public CursorRecyclerViewAdapter(Context context, Uri uri, LoaderManager manager) {
        mContext = context;
        mUri = uri;
        mList = new ArrayList<>();
        mLoaderManager = manager;
        loadList();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(mContext).inflate(R.layout.single_item, parent, false));
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        StudentObject object = mList.get(position);
        holder.mItemName.setText(object.getmName());
        setAnimation(holder.itemView, position);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public StudentObject getItem(int position) {
        return mList.get(position);
    }

    private void setAnimation(View viewToAnimate, int position) {
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.fly_in);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }

    public void removeItem(int position) {
        mList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mList.size());
    }

    private void makeList(Cursor cursor) {
        mList.clear();
        while (cursor != null && cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex(TableNames.table0.mId));
            String name = cursor.getString(cursor.getColumnIndex(TableNames.table0.mName));
            String studentId = cursor.getString(cursor.getColumnIndex(TableNames.table0.mStudentId));
            String parentNo = cursor.getString(cursor.getColumnIndex(TableNames.table0.mParentPhoneNo));
            mList.add(new StudentObject(id, name, studentId, parentNo));
        }
        //notifyDataSetChanged();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (mUri != null) {
            switch (id) {
                case LOADER_ID:
                    return new CursorLoader(mContext, mUri, null, null, null, null);
            }
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        makeList(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mList.clear();
    }

    private void loadList() {
        if (mLoaderManager.getLoader(LOADER_ID) == null) {
            mLoaderManager.initLoader(LOADER_ID, null, this);
        } else {
            mLoaderManager.restartLoader(LOADER_ID, null, this);
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
            ButterKnife.bind(this, itemView);
        }
    }
}
