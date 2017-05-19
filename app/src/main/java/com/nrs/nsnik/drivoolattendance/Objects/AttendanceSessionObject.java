package com.nrs.nsnik.drivoolattendance.Objects;



public class AttendanceSessionObject {

    private String mStudentId,mBTime,mETime;
    private int mId,mSessionId,mSessionStatus;;

    public AttendanceSessionObject(int id,String sId,String bTm,String eTm,int sessionId,int sessionStatus){
        mId = id;
        mStudentId = sId;
        mBTime = bTm;
        mETime = eTm;
        mSessionId  = sessionId;
        mSessionStatus = sessionStatus;
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

    public int getmSessionId() {
        return mSessionId;
    }

    public int getmSessionStatus() {
        return mSessionStatus;
    }
}
