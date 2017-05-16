package com.nrs.nsnik.drivoolattendance.Objects;


public class StudentObject {

    private String mName,nStudentId,mPhoneNo;
    private int mId;

    public StudentObject(int id,String name,String sId,String phoneNo){
        mId = id;
        mName = name;
        nStudentId = sId;
        mPhoneNo = phoneNo;
    }

    public int getmId() {
        return mId;
    }

    public String getmName() {
        return mName;
    }

    public String getnStudentId() {
        return nStudentId;
    }

    public String getmPhoneNo() {
        return mPhoneNo;
    }
}