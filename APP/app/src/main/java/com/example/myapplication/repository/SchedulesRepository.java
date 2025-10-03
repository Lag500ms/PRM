package com.example.myapplication.repository;

import android.content.Context;
import com.example.myapplication.model.common.PageResponse;
import com.example.myapplication.model.schedules.CreateScheduleRequest;
import com.example.myapplication.model.schedules.UpdateScheduleRequest;
import com.example.myapplication.model.schedules.UpdateStatusRequest;
import com.example.myapplication.model.schedules.ScheduleResponse;
import com.example.myapplication.network.RetrofitClient;
import com.example.myapplication.network.SchedulesApiService;
import java.io.IOException;
import okhttp3.ResponseBody;
import retrofit2.Response;

public class SchedulesRepository {

    private final SchedulesApiService api;

    public SchedulesRepository(Context context) {
        this.api = RetrofitClient.createWithAuth(context, SchedulesApiService.class);
    }

    public PageResponse<ScheduleResponse> list(int page, int size, String status, String start, String end)
            throws IOException {
        return api.listSchedules(page, size, status, start, end).execute().body();
    }

    public ScheduleResponse getById(String id) throws IOException {
        return api.getSchedule(id).execute().body();
    }

    public ScheduleResponse create(CreateScheduleRequest request) throws IOException {
        return api.createSchedule(request).execute().body();
    }

    public ScheduleResponse update(UpdateScheduleRequest request) throws IOException {
        return api.updateSchedule(request).execute().body();
    }

    public ScheduleResponse updateStatus(UpdateStatusRequest request) throws IOException {
        return api.updateStatus(request).execute().body();
    }

    public boolean delete(String id) throws IOException {
        Response<Void> resp = api.deleteSchedule(id).execute();
        return resp.isSuccessful();
    }
}
