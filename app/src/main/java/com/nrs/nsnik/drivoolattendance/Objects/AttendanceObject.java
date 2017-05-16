package com.nrs.nsnik.drivoolattendance.Objects;

/**
 * Created by Nikhil on 16-May-17.
 */

public class AttendanceObject {

    private String mStudentId,mBTime,mETime;
    private int mId;

    public AttendanceObject(int id,String sId,String bTm,String eTm){
        mId = id;
        mStudentId = sId;
        mBTime = bTm;
        mETime = eTm;
    }

    public int getmId() {
        return mId;
    }

    public String getmStudentId() {
        return mStudentId;
    }

    public String getmBTime() {
        return mBTime;
    }

    public String getmETime() {
        return mETime;
    }
}
