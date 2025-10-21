package com.example.myapplication.repository;

import android.content.Context;
import com.example.myapplication.model.chatbot.ChatbotRequest;
import com.example.myapplication.model.chatbot.ChatbotResponse;
import com.example.myapplication.network.ChatbotApiService;
import com.example.myapplication.network.RetrofitClient;
import java.io.IOException;

public class ChatbotRepository {

    private final ChatbotApiService api;

    public ChatbotRepository(Context context) {
        this.api = RetrofitClient.createWithAuth(context, ChatbotApiService.class);
    }

    public ChatbotResponse askQuestion(String question) throws IOException {
        ChatbotRequest request = new ChatbotRequest(question);
        return api.askQuestion(request).execute().body();
    }

    public String healthCheck() throws IOException {
        return api.healthCheck().execute().body();
    }
}
