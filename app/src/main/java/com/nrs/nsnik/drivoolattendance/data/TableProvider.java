package com.nrs.nsnik.drivoolattendance.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

public class TableProvider extends ContentProvider{

    private static final int uAllEntities = 555;
    private static final int uSingleEntities = 556;
    private static final int uAttendanceAllEntities = 657;
    private static final int uAttendanceSingleEntities = 658;
    static UriMatcher sUriMatcher;

    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(TableNames.mAuthority, TableNames.mTableName, uAllEntities);
        sUriMatcher.addURI(TableNames.mAuthority, TableNames.mTableName + "/#", uSingleEntities);
        sUriMatcher.addURI(TableNames.mAuthority, TableNames.mTableAttendanceName, uAttendanceAllEntities);
        sUriMatcher.addURI(TableNames.mAuthority, TableNames.mTableAttendanceName + "/#", uAttendanceSingleEntities);
    }

    TableHelper helper;

    @Override
    public boolean onCreate() {
        helper = new TableHelper(getContext());
        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        Log.d("Uri", uri.toString());
        Cursor c;
        SQLiteDatabase sdb = helper.getReadableDatabase();
        switch (sUriMatcher.match(uri)) {
            case uAllEntities:
                c = sdb.query(TableNames.mTableName, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case uSingleEntities:
                selection = TableNames.table0.mId + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                c = sdb.query(TableNames.mTableName, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case uAttendanceAllEntities:
                c = sdb.query(TableNames.mTableAttendanceName, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case uAttendanceSingleEntities:
                selection = TableNames.table1.mId + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                c = sdb.query(TableNames.mTableAttendanceName, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Invalid Uri :" + uri);
        }
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        switch (sUriMatcher.match(uri)) {
            case uAllEntities:
                return insertVal(uri, values,TableNames.mTableName);
            case uAttendanceAllEntities :
                return insertVal(uri,values,TableNames.mTableAttendanceName);
            default:
                throw new IllegalArgumentException("Invalid Uri :" + uri);
        }
    }

    private Uri insertVal(Uri u, ContentValues cv,String tableName) {
        SQLiteDatabase sdb = helper.getWritableDatabase();
        long count = sdb.insert(tableName, null, cv);
        if (count > 0) {
            getContext().getContentResolver().notifyChange(u, null);
            return Uri.withAppendedPath(u, String.valueOf(count));
        } else {
            return null;
        }
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        switch (sUriMatcher.match(uri)){
            case uAllEntities:
                return deleteVal(uri,selection,selectionArgs,TableNames.mTableName);
            case uSingleEntities:
                selection = TableNames.table0.mId + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return deleteVal(uri,selection,selectionArgs,TableNames.mTableName);
            case uAttendanceAllEntities:
                return deleteVal(uri,selection,selectionArgs,TableNames.mTableAttendanceName);
            case uAttendanceSingleEntities:
                selection = TableNames.table1.mId + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return deleteVal(uri,selection,selectionArgs,TableNames.mTableAttendanceName);
            default:
                throw new IllegalArgumentException("Invalid Uri :" + uri);
        }
    }

    private int deleteVal(Uri uri,String selection,String[] selectionArgs,String tableName){
        SQLiteDatabase sdb = helper.getWritableDatabase();
        int count = sdb.delete(tableName,selection,selectionArgs);
        if (count > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
            return count;
        } else {
            return 0;
        }
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        switch (sUriMatcher.match(uri)){
            case uAllEntities:
                return updateVal(uri,values,selection,selectionArgs,TableNames.mTableName);
            case uSingleEntities:
                selection = TableNames.table0.mId + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateVal(uri,values,selection,selectionArgs,TableNames.mTableName);
            case uAttendanceAllEntities:
                return updateVal(uri,values,selection,selectionArgs,TableNames.mTableAttendanceName);
            case uAttendanceSingleEntities:
                selection = TableNames.table1.mId + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateVal(uri,values,selection,selectionArgs,TableNames.mTableAttendanceName);
            default:
                throw new IllegalArgumentException("Invalid Uri :" + uri);
        }
    }

    private int updateVal(Uri u,ContentValues cv,String s,String[] selArgs,String tableName){
        SQLiteDatabase sqLiteDatabase = helper.getWritableDatabase();
        int count = sqLiteDatabase.update(tableName,cv,s,selArgs);
        if (count > 0) {
            getContext().getContentResolver().notifyChange(u, null);
            return count;
        } else {
            return 0;
        }
    }

}
