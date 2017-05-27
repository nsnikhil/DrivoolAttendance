package com.nrs.nsnik.drivoolattendance.adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nrs.nsnik.drivoolattendance.R;
import com.nrs.nsnik.drivoolattendance.data.TableNames;

import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;


public class LinearListAdapter extends CursorAdapter {

    private static final String[] colorArray = {"#D32F2F", "#C2185B", "#7B1FA2", "#512DA8", "#303F9F", "#1976D2", "#0288D1",
            "#0097A7", "#00796B", "#388E3C", "#689F38", "#AFB42B", "#FBC02D", "#FFA000", "#F57C00", "#E64A19"};
    private Random r = new Random();
    private MyViewHolder mMyViewHolder;
    private Context mContext;

    public LinearListAdapter(Context context, Cursor c) {
        super(context, c);
        mContext = context;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.list_item, parent, false);
        mMyViewHolder = new MyViewHolder(v);
        v.setTag(mMyViewHolder);
        return v;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        mMyViewHolder = (MyViewHolder) view.getTag();
        mMyViewHolder.mItemHeader.setText(String.valueOf(cursor.getInt(cursor.getColumnIndex(TableNames.table2.mSessionId))));
    }

    private int getRandom() {
        int color = r.nextInt(colorArray.length);
        return Color.parseColor(colorArray[color]);
    }

    private ColorStateList stateList() {
        int[][] states = new int[][]{
                new int[]{android.R.attr.state_enabled},
                new int[]{-android.R.attr.state_enabled},
                new int[]{-android.R.attr.state_checked},
                new int[]{android.R.attr.state_pressed}
        };
        int color = getRandom();
        int[] colors = new int[]{color, color, color, color};
        return new ColorStateList(states, colors);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.itemHeading) TextView mItemHeader;
        @BindView(R.id.itemName) TextView mItemName;

        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mItemHeader.setBackgroundTintList(stateList());
            }
        }
    }
}
