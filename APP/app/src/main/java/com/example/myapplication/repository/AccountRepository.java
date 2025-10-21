package com.example.myapplication.repository;

import com.example.myapplication.model.account.request.AccountUpdateRequestDTO;
import com.example.myapplication.model.account.request.RegisterRequestDTO;
import com.example.myapplication.model.account.response.AccountResponseDTO;
import com.example.myapplication.model.account.response.AccountResponsePageDTO;
import com.example.myapplication.network.AccountApiService;
import java.util.List;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AccountRepository {

    private final AccountApiService apiService;

    public AccountRepository(AccountApiService apiService) {
        this.apiService = apiService;
    }

    // Register guest
    public void register(RegisterRequestDTO request, RepositoryCallback<Void> callback) {
        apiService.register(request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(null);
                } else {
                    callback.onError("Error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    // Save dealer by admin
    public void saveByAdmin(RegisterRequestDTO request, RepositoryCallback<Void> callback) {
        apiService.saveByAdmin(request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(null);
                } else {
                    callback.onError("Error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    // Get all accounts
    public void getAll(RepositoryCallback<List<AccountResponseDTO>> callback) {
        apiService.getAll().enqueue(new Callback<List<AccountResponseDTO>>() {
            @Override
            public void onResponse(Call<List<AccountResponseDTO>> call, Response<List<AccountResponseDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<AccountResponseDTO>> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    // Update account
    public void update(AccountUpdateRequestDTO request, RepositoryCallback<AccountResponseDTO> callback) {
        apiService.update(request).enqueue(new Callback<AccountResponseDTO>() {
            @Override
            public void onResponse(Call<AccountResponseDTO> call, Response<AccountResponseDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<AccountResponseDTO> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    // Change account status
    public void changeStatus(String email, boolean active, RepositoryCallback<Map<String, String>> callback) {
        apiService.changeAccountStatus(email, active).enqueue(new Callback<Map<String, String>>() {
            @Override
            public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Map<String, String>> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    // Delete account
    public void delete(String id, RepositoryCallback<Void> callback) {
        apiService.delete(id).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(null);
                } else {
                    callback.onError("Error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    // search account
    public void searchAccountsByUsername(String keyword, int page, int size,
                                         RepositoryCallback<AccountResponsePageDTO> callback) {
        apiService.searchAccountsByUsername(keyword, page, size).enqueue(new Callback<AccountResponsePageDTO>() {
            @Override
            public void onResponse(Call<AccountResponsePageDTO> call, Response<AccountResponsePageDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<AccountResponsePageDTO> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    // Callback interface
    public interface RepositoryCallback<T> {
        void onSuccess(T data);
        void onError(String errorMessage);
    }
}

