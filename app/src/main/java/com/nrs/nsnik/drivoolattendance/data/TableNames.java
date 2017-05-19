package com.nrs.nsnik.drivoolattendance.data;


import android.net.Uri;
import android.provider.BaseColumns;

public class TableNames {

    public static final String mDatabaseName = "studentDatabase";
    public static final String mTableName = "studentTable";
    public static final String mTableAttendanceName = "attendanceTable";
    public static final String mTableSessionName = "sessionTable";
    public static final int mDatabaseVersion = 6;

    public static final String mScheme = "content://";
    public static final String mAuthority = "com.nrs.nsnik.drivoolattendance";

    public static final Uri mBaseUri = Uri.parse(mScheme + mAuthority);
    public static final Uri mContentUri = Uri.withAppendedPath(mBaseUri, mTableName);
    public static final Uri mAttendanceContentUri = Uri.withAppendedPath(mBaseUri, mTableAttendanceName);
    public static final Uri mSessionContentUri = Uri.withAppendedPath(mBaseUri, mTableSessionName);

    public class table0 implements BaseColumns {
        public static final String mId = BaseColumns._ID;
        public static final String mName = "name";
        public static final String mStudentId = "studentid";
        public static final String mParentPhoneNo = "parentphoneno";
        public static final String mPhotoUrl = "photourl";
    }

    public class table1 implements BaseColumns {
        public static final String mId = BaseColumns._ID;
        public static final String mSessionId = "sessionid";
        public static final String mStudentId = "studentid";
        public static final String mBoardingTime = "bdtime";
        public static final String mExitTime = "edtime";
    }

    public class table2 implements BaseColumns{
        public static final String mId = BaseColumns._ID;
        public static final String mSessionId = "sessionid";
        public static final String mTripStatus = "tripstatus";
    }
}
