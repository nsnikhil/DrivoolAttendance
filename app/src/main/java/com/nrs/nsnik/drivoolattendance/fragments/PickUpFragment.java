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
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nrs.nsnik.drivoolattendance.Objects.AttendanceObject;
import com.nrs.nsnik.drivoolattendance.R;
import com.nrs.nsnik.drivoolattendance.adapters.ObserverAdapter;
import com.nrs.nsnik.drivoolattendance.data.TableNames;
import com.nrs.nsnik.drivoolattendance.fragments.dialogFragments.FakeListDialogFragment;
import com.nrs.nsnik.drivoolattendance.interfaces.FakeItems;
import com.nrs.nsnik.drivoolattendance.interfaces.NotifyInterface;
import com.nrs.nsnik.drivoolattendance.services.AttendanceService;
import com.nrs.nsnik.drivoolattendance.services.SendSmsService;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class PickUpFragment extends Fragment implements FakeItems, NotifyInterface {

    private static final String NULL_VALUE = "N/A";
    private static int mCount = 0;
    @BindView(R.id.pickUpStartTrip)
    Button mStartTrip;
    @BindView(R.id.pickUpCounterContainer)
    LinearLayout mCounterContainer;
    @BindView(R.id.pickUpRecyclerView)
    RecyclerView mMainRecyclerView;
    @BindView(R.id.counterTotal)
    TextView mCounterTotal;
    @BindView(R.id.counterCurrent)
    TextView mPresentTotal;
    Fragment mThisFragment;
    Intent mAttenService;
    private ObserverAdapter mObserverAdapter;
    private Paint p = new Paint();
    private Unbinder mUnbinder;

    public PickUpFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_pick_up, container, false);
        mUnbinder = ButterKnife.bind(this, v);
        initialize();
        listener();
        mThisFragment = this;
        return v;
    }

    private void initialize() {
        mMainRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        mObserverAdapter = new ObserverAdapter(getActivity(), getLoaderManager(), 0, this);
        mMainRecyclerView.setAdapter(mObserverAdapter);
    }


    private void listener() {
        mStartTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity().getContentResolver().query(TableNames.mContentUri, null, null, null, null).getCount() <= 0) {
                    FakeListDialogFragment fakeListDialogFragment = new FakeListDialogFragment();
                    fakeListDialogFragment.setCancelable(false);
                    fakeListDialogFragment.setTargetFragment(mThisFragment, 121);
                    fakeListDialogFragment.show(getFragmentManager(), "fakelist");
                } else {
                    //addRefreshedAdapter();
                    startSession();
                }
            }
        });
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                if (direction == ItemTouchHelper.LEFT) {
                    mObserverAdapter.removeItem(viewHolder.getAdapterPosition());
                } else {
                    AttendanceObject object = mObserverAdapter.getItem(viewHolder.getAdapterPosition());
                    String queryParam = "student/" + object.getmStudentId();
                    Cursor cursor = getActivity().getContentResolver().query(Uri.withAppendedPath(TableNames.mContentUri, queryParam), null, null, null, null);
                    if (cursor != null && cursor.moveToFirst()) {
                        String name = cursor.getString(cursor.getColumnIndex(TableNames.table0.mName));
                        String phoneNo = cursor.getString(cursor.getColumnIndex(TableNames.table0.mParentPhoneNo));
                        Intent message = new Intent(getActivity(), SendSmsService.class);
                        message.putExtra(getResources().getString(R.string.intentKeyMessage), name + " Picked Up");
                        message.putExtra(getResources().getString(R.string.intentkeyPhoneNo), phoneNo);
                        getActivity().startService(message);
                    } else {
                        Toast.makeText(getActivity(), "Error", Toast.LENGTH_LONG).show();
                    }
                    mObserverAdapter.removeItem(viewHolder.getAdapterPosition());
                    mPresentTotal.setText(String.valueOf(++mCount));
                    Calendar calendar = Calendar.getInstance();
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(TableNames.table1.mBoardingTime, calendar.getTimeInMillis());
                    getActivity().getContentResolver().update(Uri.withAppendedPath(TableNames.mAttendanceContentUri, queryParam), contentValues, null, null);
                }
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                Bitmap icon;
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                    View itemView = viewHolder.itemView;
                    float height = (float) itemView.getBottom() - (float) itemView.getTop();
                    float width = height / 3;
                    if (dX > 0) {
                        p.setColor(Color.parseColor("#388E3C"));
                        RectF background = new RectF((float) itemView.getLeft(), (float) itemView.getTop(), itemView.getRight(), (float) itemView.getBottom());
                        c.drawRect(background, p);
                        icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_done_white_48dp);
                        RectF icon_dest = new RectF((float) itemView.getLeft(), (float) itemView.getTop() + width, (float) itemView.getLeft() + width, (float) itemView.getBottom() - width);
                        c.drawBitmap(icon, null, icon_dest, p);

                    } else {
                        p.setColor(Color.parseColor("#D32F2F"));
                        RectF background = new RectF((float) itemView.getRight(), (float) itemView.getTop(), itemView.getLeft(), (float) itemView.getBottom());
                        c.drawRect(background, p);
                        icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_close_white_48dp);
                        RectF icon_dest = new RectF((float) itemView.getRight() - width, (float) itemView.getTop() + width, (float) itemView.getRight(), (float) itemView.getBottom() - width);
                        c.drawBitmap(icon, null, icon_dest, p);
                    }
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }

        }).attachToRecyclerView(mMainRecyclerView);
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

    private void addRefreshedTryAdapter() {
        Cursor cursor = getActivity().getContentResolver().query(TableNames.mContentUri, null, null, null, null);
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
        if (mMainRecyclerView.getAdapter().getItemCount() <= 0) {
            mStartTrip.setVisibility(View.VISIBLE);
            if(PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean(getActivity().getResources().getString(R.string.prefTripStatus),false)){
                mStartTrip.setEnabled(false);
                mStartTrip.setText(getActivity().getResources().getString(R.string.trip));
            }else {
                mStartTrip.setEnabled(true);
                mStartTrip.setText(getActivity().getResources().getString(R.string.startTrip));
            }
        } else {
            mStartTrip.setVisibility(View.GONE);
        }
    }

    @Override
    public void notifyChange() {
        notified();
    }

}
