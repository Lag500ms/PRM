package com.example.myapplication.network;

import com.example.myapplication.model.schedules.CreateScheduleRequest;
import com.example.myapplication.model.schedules.ScheduleResponse;
import com.example.myapplication.model.schedules.UpdateScheduleRequest;
import com.example.myapplication.model.schedules.UpdateStatusRequest;
import com.example.myapplication.model.common.PageResponse;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface SchedulesApiService {

    @GET("v1/dealer/schedules")
    Call<PageResponse<ScheduleResponse>> listSchedules(@Query("page") int page, @Query("size") int size,
            @Query("status") String status,
            @Query("start") String start, @Query("end") String end);

    @GET("v1/dealer/schedules/{id}")
    Call<ScheduleResponse> getSchedule(@Path("id") String id);

    @POST("v1/dealer/schedules")
    Call<ScheduleResponse> createSchedule(@Body CreateScheduleRequest request);

    @PUT("v1/dealer/schedules")
    Call<ScheduleResponse> updateSchedule(@Body UpdateScheduleRequest request);

    @PUT("v1/dealer/schedules/status")
    Call<ScheduleResponse> updateStatus(@Body UpdateStatusRequest request);

    @DELETE("v1/dealer/schedules/{id}")
    Call<Void> deleteSchedule(@Path("id") String id);
}
