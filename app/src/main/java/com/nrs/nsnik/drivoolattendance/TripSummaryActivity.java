package com.nrs.nsnik.drivoolattendance;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.nrs.nsnik.drivoolattendance.data.TableNames;

import java.text.SimpleDateFormat;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TripSummaryActivity extends AppCompatActivity {

    private static final String NULL_VALUE = "N/A";
    private static final String ABSENT = "ABSENT";
    @BindView(R.id.tripToolbar)
    Toolbar mTripToolbar;
    @BindView(R.id.summaryStartTime)
    TextView mStartTime;
    @BindView(R.id.summaryEndTime)
    TextView mEndTime;
    @BindView(R.id.summaryTotalStudent)
    TextView mTotal;
    @BindView(R.id.summaryTotalPresent)
    TextView mPresent;
    @BindView(R.id.summaryTotalAbsent)
    TextView mAbsent;
    private int mPresentCount;
    private static final String TAG = TripSummaryActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_summary);
        ButterKnife.bind(this);
        initialize();
        listeners();
        setValues();
    }

    private void setValues() {
        if (getIntent() != null && getIntent().getExtras() != null) {
            int sessionID = getIntent().getExtras().getInt(getResources().getString(R.string.intentKeySessionId));
            String queryParam = "session/" + sessionID;
            Cursor c = getContentResolver().query(Uri.withAppendedPath(TableNames.mAttendanceContentUri, queryParam), null, null, null, null);
            int end = c.getCount();
            mTotal.setText("Total Students  : " + end);
            for (int i = 0; i < c.getCount(); i++) {
                if (c.moveToPosition(i)) {
                    if (!c.getString(c.getColumnIndex(TableNames.table1.mBoardingTime)).equalsIgnoreCase(ABSENT)) {
                        mStartTime.setText("Trip Started at : " + formatDate(c.getString(c.getColumnIndex(TableNames.table1.mBoardingTime))));
                        break;
                    }
                }
            }
            for (int i = end - 1; i >= 0; i--) {
                if (c.moveToPosition(i)) {
                    if (!c.getString(c.getColumnIndex(TableNames.table1.mExitTime)).equalsIgnoreCase(ABSENT)) {
                        mEndTime.setText("Trip Ended at : " + formatDate(c.getString(c.getColumnIndex(TableNames.table1.mExitTime))));
                        break;
                    }
                }
            }
            for (int i = 0; i < c.getCount(); i++) {
                if (c.moveToPosition(i)) {
                    if (!c.getString(c.getColumnIndex(TableNames.table1.mBoardingTime)).equalsIgnoreCase(ABSENT)) {
                        mPresentCount++;
                    }
                }
            }
            mPresent.setText("Present Count : " + mPresentCount);
            int abs = c.getCount() - mPresentCount;
            mAbsent.setText("Absent Count : " + abs);
        }
    }

    private String formatDate(String date) {
        Long milliDate = Long.parseLong(date);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(milliDate);
    }

    private void setData() {

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        Intent startMain = new Intent(TripSummaryActivity.this, MainActivity.class);
        startMain.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
        return false;
    }

    private void initialize() {
        setSupportActionBar(mTripToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onBackPressed() {
        finish();
        Intent startMain = new Intent(TripSummaryActivity.this, MainActivity.class);
        startMain.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
    }

    private void listeners() {
    }
}
