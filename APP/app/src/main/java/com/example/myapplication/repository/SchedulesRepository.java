package com.example.myapplication.repository;

import android.content.Context;
import com.example.myapplication.model.common.PageResponse;
import com.example.myapplication.model.schedules.CreateScheduleRequest;
import com.example.myapplication.model.schedules.UpdateScheduleRequest;
import com.example.myapplication.model.schedules.UpdateStatusRequest;
import com.example.myapplication.model.schedules.ScheduleResponse;
import com.example.myapplication.network.RetrofitClient;
import com.example.myapplication.network.SchedulesApiService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SchedulesRepository {

    private final SchedulesApiService api;

    public SchedulesRepository(Context context) {
        this.api = RetrofitClient.createWithAuth(context, SchedulesApiService.class);
    }

    public void list(int page, int size, String status, String start, String end, SchedulesListCallback callback) {
        api.listSchedules(page, size, status, start, end).enqueue(new Callback<PageResponse<ScheduleResponse>>() {
            @Override
            public void onResponse(Call<PageResponse<ScheduleResponse>> call, Response<PageResponse<ScheduleResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Failed: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<PageResponse<ScheduleResponse>> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public void getById(String id, ScheduleDetailCallback callback) {
        api.getSchedule(id).enqueue(new Callback<ScheduleResponse>() {
            @Override
            public void onResponse(Call<ScheduleResponse> call, Response<ScheduleResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Failed: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ScheduleResponse> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public void create(CreateScheduleRequest request, ScheduleDetailCallback callback) {
        api.createSchedule(request).enqueue(new Callback<ScheduleResponse>() {
            @Override
            public void onResponse(Call<ScheduleResponse> call, Response<ScheduleResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Failed: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ScheduleResponse> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public void update(UpdateScheduleRequest request, ScheduleDetailCallback callback) {
        api.updateSchedule(request).enqueue(new Callback<ScheduleResponse>() {
            @Override
            public void onResponse(Call<ScheduleResponse> call, Response<ScheduleResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Failed: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ScheduleResponse> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public void updateStatus(UpdateStatusRequest request, ScheduleDetailCallback callback) {
        api.updateStatus(request).enqueue(new Callback<ScheduleResponse>() {
            @Override
            public void onResponse(Call<ScheduleResponse> call, Response<ScheduleResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Failed: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ScheduleResponse> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public void delete(String id, DeleteCallback callback) {
        api.deleteSchedule(id).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess();
                } else {
                    callback.onError("Failed: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public interface SchedulesListCallback {
        void onSuccess(PageResponse<ScheduleResponse> response);
        void onError(String error);
    }

    public interface ScheduleDetailCallback {
        void onSuccess(ScheduleResponse response);
        void onError(String error);
    }

    public interface DeleteCallback {
        void onSuccess();
        void onError(String error);
    }
}
