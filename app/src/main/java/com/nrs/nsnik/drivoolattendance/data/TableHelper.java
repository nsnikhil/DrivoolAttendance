package com.nrs.nsnik.drivoolattendance.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.nrs.nsnik.drivoolattendance.data.TableNames.table0;
import com.nrs.nsnik.drivoolattendance.data.TableNames.table1;
import com.nrs.nsnik.drivoolattendance.data.TableNames.table2;

class TableHelper extends SQLiteOpenHelper{

    private static final String mCreateTable = "CREATE TABLE " + TableNames.mTableName + " ("
            + table0.mId + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + table0.mName + " TEXT NOT NULL, "
            + table0.mStudentId + " TEXT NOT NULL, "
            + table0.mParentPhoneNo + " TEXT NOT NULL, "
            + table0.mPhotoUrl + " TEXT NOT NULL "
            + " );";

    private static final String mCreateAttendanceTable = "CREATE TABLE " + TableNames.mTableAttendanceName + " ("
            + table1.mId + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + table1.mSessionId + " INTEGER NOT NULL, "
            + table1.mStudentId + " TEXT NOT NULL, "
            + table1.mBoardingTime + " TEXT NOT NULL, "
            + table1.mExitTime + " TEXT "
            + " );";

    private static final String mCreateSessionTable = "CREATE TABLE " + TableNames.mTableSessionName + " ("
            + table2.mId + " INTEGER DEFAULT 0, "
            + table2.mSessionId + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + table2.mTripStatus + " INTEGER DEFAULT 0"
            + " );";

    private static final String mDropTable = "DROP TABLE IF EXISTS " + TableNames.mTableName;
    private static final String mDropAttendanceTable = "DROP TABLE IF EXISTS " + TableNames.mTableAttendanceName;
    private static final String mDroSessionTable = "DROP TABLE IF EXISTS " + TableNames.mTableSessionName;

    TableHelper(Context context) {
        super(context, TableNames.mDatabaseName, null, TableNames.mDatabaseVersion);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        createTable(db);
    }

    private void createTable(SQLiteDatabase sdb) {
        sdb.execSQL(mCreateTable);
        sdb.execSQL(mCreateAttendanceTable);
        sdb.execSQL(mCreateSessionTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(mDropTable);
        db.execSQL(mDropAttendanceTable);
        db.execSQL(mDroSessionTable);
        createTable(db);
    }

}
