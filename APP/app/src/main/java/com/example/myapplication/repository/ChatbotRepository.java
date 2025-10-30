package com.example.myapplication.repository;

import android.content.Context;
import com.example.myapplication.model.chatbot.ChatbotRequest;
import com.example.myapplication.model.chatbot.ChatbotResponse;
import com.example.myapplication.network.ChatbotApiService;
import com.example.myapplication.network.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatbotRepository {

    private final ChatbotApiService api;

    public ChatbotRepository(Context context) {
        this.api = RetrofitClient.createWithAuth(context, ChatbotApiService.class);
    }

    public void askQuestion(String question, ChatbotCallback callback) {
        ChatbotRequest request = new ChatbotRequest(question);
        api.askQuestion(request).enqueue(new Callback<ChatbotResponse>() {
            @Override
            public void onResponse(Call<ChatbotResponse> call, Response<ChatbotResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Failed: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ChatbotResponse> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public interface ChatbotCallback {
        void onSuccess(ChatbotResponse response);
        void onError(String error);
    }
}
