package com.nrs.nsnik.drivoolattendance.Objects;


public class StudentObject {

    private String mName,nStudentId,mPhoneNo,mPhotoUrl;
    private int mId;

    public StudentObject(int id,String name,String sId,String phoneNo,String photoUrl){
        mId = id;
        mName = name;
        nStudentId = sId;
        mPhoneNo = phoneNo;
        mPhotoUrl = photoUrl;
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

    public String getmPhotoUrl() {return mPhotoUrl;
    }
}