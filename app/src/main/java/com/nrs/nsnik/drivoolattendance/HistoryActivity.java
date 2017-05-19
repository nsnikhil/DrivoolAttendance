package com.nrs.nsnik.drivoolattendance;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.nrs.nsnik.drivoolattendance.adapters.LinearListAdapter;
import com.nrs.nsnik.drivoolattendance.data.TableNames;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HistoryActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    @BindView(R.id.historyToolbar) Toolbar mHistoryToolBar;
    @BindView(R.id.historyList) ListView mHistoryList;
    LinearListAdapter mLnearListAdapter;
    private static final int LOADER_ID  = 9;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        ButterKnife.bind(this);
        initialize();
        listeners();
        getSupportLoaderManager().initLoader(LOADER_ID,null,this);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    private void initialize() {
        setSupportActionBar(mHistoryToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mLnearListAdapter = new LinearListAdapter(HistoryActivity.this,null);
        mHistoryList.setAdapter(mLnearListAdapter);
    }

    private void listeners(){
        mHistoryList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startActivity(new Intent(HistoryActivity.this,TripSummaryActivity.class));
            }
        });
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id){
            case LOADER_ID:
                String queryUri = "status/"+1;
                return new CursorLoader(HistoryActivity.this,Uri.withAppendedPath(TableNames.mSessionContentUri,queryUri),null,null,null,null);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mLnearListAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mLnearListAdapter.swapCursor(null);
    }
}
