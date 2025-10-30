package com.example.myapplication.repository;


import android.content.Context;
import com.example.myapplication.model.account.request.AccountUpdateRequestDTO;
import com.example.myapplication.model.account.request.RegisterRequestDTO;
import com.example.myapplication.model.account.response.AccountResponseDTO;
import com.example.myapplication.network.AccountApiService;
import com.example.myapplication.network.RetrofitClient;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminAccountsRepository {

    private final AccountApiService api;

    public AdminAccountsRepository(Context context) {
        this.api = RetrofitClient.createWithAuth(context, AccountApiService.class);
    }

    public void getAll(AccountsListCallback callback) {
        api.getAll().enqueue(new Callback<List<AccountResponseDTO>>() {
            @Override
            public void onResponse(Call<List<AccountResponseDTO>> call, Response<List<AccountResponseDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Failed: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<AccountResponseDTO>> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public void createDealerByAdmin(RegisterRequestDTO request, CreateCallback callback) {
        api.saveByAdmin(request).enqueue(new Callback<Void>() {
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

    public void updateAccount(AccountUpdateRequestDTO request, AccountDetailCallback callback) {
        api.update(request).enqueue(new Callback<AccountResponseDTO>() {
            @Override
            public void onResponse(Call<AccountResponseDTO> call, Response<AccountResponseDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Failed: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<AccountResponseDTO> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public void deleteAccount(String id, DeleteCallback callback) {
        api.delete(id).enqueue(new Callback<Void>() {
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

    public interface AccountsListCallback {
        void onSuccess(List<AccountResponseDTO> response);
        void onError(String error);
    }

    public interface AccountDetailCallback {
        void onSuccess(AccountResponseDTO response);
        void onError(String error);
    }

    public interface CreateCallback {
        void onSuccess();
        void onError(String error);
    }

    public interface DeleteCallback {
        void onSuccess();
        void onError(String error);
    }
}

