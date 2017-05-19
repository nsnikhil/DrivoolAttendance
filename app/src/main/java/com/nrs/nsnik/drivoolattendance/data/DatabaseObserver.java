package com.nrs.nsnik.drivoolattendance.data;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;

import com.nrs.nsnik.drivoolattendance.interfaces.ObservableInterface;
import com.nrs.nsnik.drivoolattendance.interfaces.ObserverInterface;

import java.util.ArrayList;
import java.util.List;


public class DatabaseObserver implements ObservableInterface, LoaderManager.LoaderCallbacks<Cursor>{

    private Context mContext;
    private LoaderManager mLoaderManager;
    private static final int LOADER_ID = 546;
    private List<ObserverInterface> mObserverInterfaces;

    public DatabaseObserver(Context context,LoaderManager loaderManager){
        mContext = context;
        mLoaderManager = loaderManager;
        mObserverInterfaces = new ArrayList<>();
        mLoaderManager.initLoader(LOADER_ID,null,this);
    }


    @Override
    public void add(ObserverInterface observerInterface) {
        mObserverInterfaces.add(observerInterface);
    }

    @Override
    public void remove() {

    }

    @Override
    public void notifyObserver(Cursor cursor) {
        for(ObserverInterface interfaces : mObserverInterfaces){
            interfaces.update(cursor);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id){
            case LOADER_ID:
                return new CursorLoader(mContext,TableNames.mAttendanceContentUri,null,null,null,null);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        notifyObserver(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
