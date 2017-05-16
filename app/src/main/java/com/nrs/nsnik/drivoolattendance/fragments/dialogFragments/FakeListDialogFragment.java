package com.nrs.nsnik.drivoolattendance.fragments.dialogFragments;


import android.content.ContentValues;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.nrs.nsnik.drivoolattendance.R;
import com.nrs.nsnik.drivoolattendance.data.TableNames;
import com.nrs.nsnik.drivoolattendance.interfaces.FakeItems;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class FakeListDialogFragment extends DialogFragment {

    @BindView(R.id.fakeListPhoneNo) TextInputEditText mPhoneNo;
    @BindView(R.id.fakeListCreate) Button mCreate;
    private static final String NULL_VALUE = "N/A";
    private FakeItems mFakeItems;
    private Unbinder mUnbinder;

    public FakeListDialogFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_fake_list_dialog, container, false);
        mUnbinder = ButterKnife.bind(this,v);
        mFakeItems = (FakeItems) getTargetFragment();
        initialize();
        listeners();
        return v;
    }

    private void initialize(){

    }

    private void listeners(){
        mCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(verifyField()){
                    PreferenceManager.getDefaultSharedPreferences(getActivity()).edit()
                            .putString(getActivity().getResources().getString(R.string.prefKeyPhoneNo),NULL_VALUE).apply();
                    makeFakeList();
                }
            }
        });
    }

    private boolean verifyField(){
        if(mPhoneNo.getText().toString().isEmpty()||mPhoneNo.getText().toString().length()<=0){
            mPhoneNo.requestFocus();
            mPhoneNo.setError("Enter the Phone no");
            return false;
        }
        return true;
    }

     private void makeFakeList(){
        if(getActivity().getContentResolver().query(TableNames.mContentUri,null,null,null,null).getCount()<=0) {
            ContentValues contentValues = new ContentValues();
            String phoneNo = mPhoneNo.getText().toString();
            for (int i = 0; i < 10; i++) {
                contentValues.put(TableNames.table0.mName, "Student " + i);
                contentValues.put(TableNames.table0.mStudentId, "IDA00" + i);
                contentValues.put(TableNames.table0.mParentPhoneNo,phoneNo);
                getActivity().getContentResolver().insert(TableNames.mContentUri, contentValues);
            }
        }
        this.dismiss();
        mFakeItems.itemAdded();
    }


    @Override
    public void onDestroy() {
        mUnbinder.unbind();
        super.onDestroy();
    }
}
