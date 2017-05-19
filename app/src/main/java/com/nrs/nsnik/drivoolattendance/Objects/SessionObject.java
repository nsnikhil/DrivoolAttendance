package com.nrs.nsnik.drivoolattendance.Objects;


public class SessionObject {

    private int mSessionId,mSessionStatus;

    public SessionObject(int sessionId,int sessionStatus){
        mSessionId  = sessionId;
        mSessionStatus = sessionStatus;
    }

    public int getmSessionId() {
        return mSessionId;
    }

    public int getmSessionStatus() {
        return mSessionStatus;
    }
}
