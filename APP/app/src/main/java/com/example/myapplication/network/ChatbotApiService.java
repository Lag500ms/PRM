package com.example.myapplication.network;

import com.example.myapplication.model.chatbot.ChatbotRequest;
import com.example.myapplication.model.chatbot.ChatbotResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ChatbotApiService {

    @POST("v1/chatbot/ask")
    Call<ChatbotResponse> askQuestion(@Body ChatbotRequest request);

    @GET("v1/chatbot/health")
    Call<String> healthCheck();
}
