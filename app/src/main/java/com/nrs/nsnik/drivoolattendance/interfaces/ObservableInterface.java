package com.nrs.nsnik.drivoolattendance.interfaces;


import android.database.Cursor;

public interface ObservableInterface {
    void add(ObserverInterface observerInterface);
    void remove();
    void notifyObserver(Cursor cursor);
}
