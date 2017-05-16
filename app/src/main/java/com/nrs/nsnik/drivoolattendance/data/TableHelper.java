package com.nrs.nsnik.drivoolattendance.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.nrs.nsnik.drivoolattendance.data.TableNames.table0;


public class TableHelper extends SQLiteOpenHelper{

    private static final String mCreateTable = "CREATE TABLE " + TableNames.mTableName + " ("
            + table0.mId + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + table0.mName + " TEXT NOT NULL, "
            + table0.mStudentId + " TEXT NOT NULL, "
            + table0.mParentPhoneNo + " TEXT NOT NULL "
            + " );";

    private static final String mDropTable = "DROP TABLE IF EXISTS " + TableNames.mTableName;

    public TableHelper(Context context) {
        super(context, TableNames.mDatabaseName, null, TableNames.mDatabaseVersion);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        createTable(db);
    }

    private void createTable(SQLiteDatabase sdb) {
        sdb.execSQL(mCreateTable);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(mDropTable);
        createTable(db);
    }

}
