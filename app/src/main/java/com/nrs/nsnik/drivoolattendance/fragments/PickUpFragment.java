package com.nrs.nsnik.drivoolattendance.fragments;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.claudiodegio.msv.MaterialSearchView;
import com.claudiodegio.msv.OnSearchViewListener;
import com.nrs.nsnik.drivoolattendance.MainActivity;
import com.nrs.nsnik.drivoolattendance.Objects.StudentObject;
import com.nrs.nsnik.drivoolattendance.R;
import com.nrs.nsnik.drivoolattendance.adapters.ListAdapter;
import com.nrs.nsnik.drivoolattendance.data.TableNames;
import com.nrs.nsnik.drivoolattendance.fragments.dialogFragments.FakeListDialogFragment;
import com.nrs.nsnik.drivoolattendance.interfaces.FakeItems;
import com.nrs.nsnik.drivoolattendance.services.SendSmsService;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class PickUpFragment extends Fragment implements FakeItems{

    @BindView(R.id.pickUpStartTrip) Button mStartTrip;
    @BindView(R.id.pickUpCounterContainer) LinearLayout mCounterContainer;
    @BindView(R.id.pickUpRecyclerView) RecyclerView mMainRecyclerView;
    @BindView(R.id.counterTotal)TextView mCounterTotal;
    @BindView(R.id.counterCurrent)TextView mPresentTotal;
    private ListAdapter mListAdapter;
    private Paint p = new Paint();
    private static int mCount = 0;
    Fragment mThisFragment;
    private Unbinder mUnbinder;

    public PickUpFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.fragment_pick_up, container, false);
        mUnbinder = ButterKnife.bind(this,v);
        initialize();
        listener();
        mThisFragment = this;
        setHasOptionsMenu(true);
        return v;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main_menu,menu);
    }

    private void initialize(){
        mMainRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(),2));
        mListAdapter = new ListAdapter(getActivity(),null);
        mMainRecyclerView.setAdapter(mListAdapter);
    }


    private void listener(){
        mStartTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getActivity().getContentResolver().query(TableNames.mContentUri,null,null,null,null).getCount()<=0) {
                    FakeListDialogFragment fakeListDialogFragment = new FakeListDialogFragment();
                    fakeListDialogFragment.setCancelable(false);
                    fakeListDialogFragment.setTargetFragment(mThisFragment,121);
                    fakeListDialogFragment.show(getFragmentManager(), "fakelist");
                }else {
                   addRefreshedAdapter();
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
                if (direction == ItemTouchHelper.LEFT){
                    mListAdapter.removeItem(viewHolder.getAdapterPosition());
                } else {
                    StudentObject object = mListAdapter.getItem(viewHolder.getAdapterPosition());;
                    Intent message = new Intent(getActivity(),SendSmsService.class);
                    message.putExtra(getResources().getString(R.string.intentKeyMessage),object.getmName()+" picked up");
                    message.putExtra(getResources().getString(R.string.intentkeyPhoneNo),object.getmPhoneNo());
                    getActivity().startService(message);
                    mListAdapter.removeItem(viewHolder.getAdapterPosition());
                    mPresentTotal.setText(String.valueOf(++mCount));
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
                        //RectF icon_dest = new RectF((float) itemView.getLeft() + width ,(float) itemView.getTop() + width,(float) itemView.getLeft()+ 2*width,(float)itemView.getBottom() - width);
                        RectF icon_dest = new RectF((float) itemView.getLeft()  ,(float) itemView.getTop() + width,(float) itemView.getLeft()+ width,(float)itemView.getBottom() - width);
                        c.drawBitmap(icon,null,icon_dest,p);

                    } else {
                        p.setColor(Color.parseColor("#D32F2F"));
                        RectF background = new RectF((float) itemView.getRight()  , (float) itemView.getTop(),itemView.getLeft(), (float) itemView.getBottom());
                        c.drawRect(background,p);
                        icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_close_white_48dp);
                        //RectF icon_dest = new RectF((float) itemView.getRight() - 2*width ,(float) itemView.getTop() + width,(float) itemView.getRight() - width,(float)itemView.getBottom() - width);
                        RectF icon_dest = new RectF((float) itemView.getRight() - width  ,(float) itemView.getTop() + width,(float) itemView.getRight() ,(float)itemView.getBottom() - width);
                        c.drawBitmap(icon,null,icon_dest,p);
                    }
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }

        }).attachToRecyclerView(mMainRecyclerView);
    }

    private void addRefreshedAdapter(){
        mListAdapter = new ListAdapter(getActivity(),getActivity().getContentResolver().query(TableNames.mContentUri,null,null,null,null));
        mMainRecyclerView.setAdapter(mListAdapter);
        mStartTrip.setVisibility(View.GONE);
    }

    @Override
    public void onStop() {
        mUnbinder.unbind();
        super.onStop();
    }

    @Override
    public void onDestroy() {
        getActivity().getContentResolver().delete(TableNames.mContentUri,null,null);
        super.onDestroy();
    }

    @Override
    public void itemAdded() {
        addRefreshedAdapter();
    }
}
