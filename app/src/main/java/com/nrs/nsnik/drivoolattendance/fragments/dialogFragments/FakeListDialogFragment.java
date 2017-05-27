package com.nrs.nsnik.drivoolattendance.fragments.dialogFragments;


import android.content.ContentValues;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.nrs.nsnik.drivoolattendance.Objects.StudentObject;
import com.nrs.nsnik.drivoolattendance.R;
import com.nrs.nsnik.drivoolattendance.data.TableNames;
import com.nrs.nsnik.drivoolattendance.interfaces.FakeItems;
import com.nrs.nsnik.drivoolattendance.interfaces.RetroFitApiCalls;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class FakeListDialogFragment extends DialogFragment {

    @BindView(R.id.fakeListPhoneNo)
    TextInputEditText mPhoneNo;
    @BindView(R.id.fakeListCreate)
    Button mCreate;
    private static final String NULL_VALUE = "N/A";
    private static final String TAG = FakeListDialogFragment.class.getSimpleName();
    private FakeItems mFakeItems;
    private Unbinder mUnbinder;
    private Retrofit mRetrofit;

    public FakeListDialogFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_fake_list_dialog, container, false);
        mUnbinder = ButterKnife.bind(this, v);
        mFakeItems = (FakeItems) getTargetFragment();
        initialize();
        listeners();
        return v;
    }

    private void initialize() {

    }

    private void listeners() {
        mCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (verifyField()) {
                    PreferenceManager.getDefaultSharedPreferences(getActivity()).edit()
                            .putString(getActivity().getResources().getString(R.string.prefKeyPhoneNo), NULL_VALUE).apply();
                    getStudentList();
                }
            }
        });
    }

    private Map<String, String> getParams() {
        Map<String, String> params = new HashMap<>();
        params.put("gapn", "mum-intel-stmary");
        params.put("route_id", "bus16smsicse");
        return params;
    }

    private void getStudentList(){
        RetroFitApiCalls apiClass = getStudentClient().create(RetroFitApiCalls.class);
        apiClass.getStudentList(getParams()).enqueue(new Callback<List<StudentObject>>() {
            @Override
            public void onResponse(@NonNull Call<List<StudentObject>> call, @NonNull Response<List<StudentObject>> response) {
                insertInDatabase(response.body());
            }
            @Override
            public void onFailure(@NonNull Call<List<StudentObject>> call, @NonNull Throwable t) {
                Log.d(TAG, t.getMessage());
            }
        });
    }

    private void insertInDatabase(List<StudentObject> objects){
        String phoneNo = mPhoneNo.getText().toString();
        if(objects!=null&&objects.size()>0){
            ContentValues contentValues  = new ContentValues();
            for(StudentObject object:objects){
                contentValues.put(TableNames.table0.mName, object.getmStudentName());
                contentValues.put(TableNames.table0.mParentPhoneNo,phoneNo);
                contentValues.put(TableNames.table0.mStudentId,object.getmStudentId());
                contentValues.put(TableNames.table0.mPhotoUrl,object.getmPhotoUrl());
                getActivity().getContentResolver().insert(TableNames.mContentUri, contentValues);
            }
        }
        this.dismiss();
        mFakeItems.itemAdded();
    }

    private Retrofit getStudentClient() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();
        if (mRetrofit == null) {
            mRetrofit = new Retrofit.Builder()
                    .client(client)
                    .baseUrl("http://isirs.org/")
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return mRetrofit;
    }

    private boolean verifyField() {
        if (mPhoneNo.getText().toString().isEmpty() || mPhoneNo.getText().toString().length() <= 0) {
            mPhoneNo.requestFocus();
            mPhoneNo.setError("Enter the Phone no");
            return false;
        }
        return true;
    }

    private void cleanUp(){
        if(mUnbinder!=null){
            mUnbinder.unbind();
        }
    }


    @Override
    public void onDestroy() {
        cleanUp();
        super.onDestroy();
    }
}
