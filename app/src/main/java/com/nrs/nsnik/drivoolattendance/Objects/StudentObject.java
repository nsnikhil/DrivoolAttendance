package com.nrs.nsnik.drivoolattendance.Objects;


import com.google.gson.annotations.SerializedName;

public class StudentObject {


    @SerializedName("studentName")
    private String mStudentName;

    @SerializedName("contactNumber")
    private String mContactNo;

    @SerializedName("intellinect_id")
    private String mStudentId;

    @SerializedName("photo")
    private String mPhotoUrl;

    public void setmStudentName(String mStudentName) {
        this.mStudentName = mStudentName;
    }

    public void setmContactNo(String mContactNo) {
        this.mContactNo = mContactNo;
    }

    public void setmStudentId(String mStudentId) {
        this.mStudentId = mStudentId;
    }

    public void setmPhotoUrl(String mPhotoUrl) {
        this.mPhotoUrl = mPhotoUrl;
    }

    public String getmStudentName() {
        return mStudentName;
    }

    public String getmStudentId() {
        return mStudentId;
    }

    public String getmContactNo() {
        return mContactNo;
    }

    public String getmPhotoUrl() {
        return mPhotoUrl;
    }
}