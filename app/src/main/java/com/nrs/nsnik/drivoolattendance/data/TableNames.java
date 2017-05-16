package com.nrs.nsnik.drivoolattendance.data;


import android.net.Uri;
import android.provider.BaseColumns;

public class TableNames {

    public static final String mDatabaseName = "studentDatabase";
    public static final String mTableName = "studentTable";
    public static final int mDatabaseVersion = 2;

    public static final String mScheme = "content://";
    public static final String mAuthority = "com.nrs.nsnik.drivoolattendance";

    public static final Uri mBaseUri = Uri.parse(mScheme + mAuthority);
    public static final Uri mContentUri = Uri.withAppendedPath(mBaseUri, mTableName);

    public class table0 implements BaseColumns {
        public static final String mId = BaseColumns._ID;
        public static final String mName = "name";
        public static final String mStudentId = "studentid";
        public static final String mParentPhoneNo = "parentphoneno";
    }
}
