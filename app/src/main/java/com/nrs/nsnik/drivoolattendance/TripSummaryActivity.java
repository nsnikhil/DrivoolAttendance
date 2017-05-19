package com.nrs.nsnik.drivoolattendance;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.Button;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TripSummaryActivity extends AppCompatActivity {

    @BindView(R.id.tripToolbar) Toolbar mTripToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_summary);
        ButterKnife.bind(this);
        initialize();
        listeners();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    private void initialize(){
        setSupportActionBar(mTripToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(TripSummaryActivity.this,MainActivity.class));
    }

    private void listeners() {
    }
}
