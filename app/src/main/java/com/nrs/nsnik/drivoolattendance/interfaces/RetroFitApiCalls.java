package com.nrs.nsnik.drivoolattendance.interfaces;

import com.nrs.nsnik.drivoolattendance.Objects.StudentObject;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;

public interface RetroFitApiCalls {

    @FormUrlEncoded
    @POST("/bus_tracking_ws/student_data.php")
    Call<List<StudentObject>> getStudentList(@FieldMap Map<String, String> params);

    @GET("/api/MessageCompose?admin=dsouzaronald@gmail.com&user=principal@campionmumbai.org:M2L9V7B&senderID=CAMPMU")
    Call<Void> sendSms(@QueryMap Map<String, String> params);

}
