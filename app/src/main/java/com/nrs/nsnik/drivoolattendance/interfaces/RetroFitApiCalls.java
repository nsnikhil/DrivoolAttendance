package com.nrs.nsnik.drivoolattendance.interfaces;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.nrs.nsnik.drivoolattendance.Objects.StudentObject;

import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;

public interface RetroFitApiCalls {

    @FormUrlEncoded
    @POST("/bus_tracking_ws/student_data.php")
    Call<List<StudentObject>> getStudentList(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST
    Call<List<StudentObject>> getStudentUrl(@Url String url,@FieldMap Map<String, String> params);

    @GET("/api/MessageCompose?admin=dsouzaronald@gmail.com&user=principal@campionmumbai.org:M2L9V7B&senderID=CAMPMU")
    Call<Void> sendSms(@QueryMap Map<String, String> params);

    @GET()
    Call<Void> sendSmsUrl(@Url String url,@QueryMap Map<String, String> params);

    @GET
    Call<ResponseBody> getStudentImage(@Url String url);
}
