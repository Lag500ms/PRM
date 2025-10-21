package com.example.myapplication.model.chatbot;

import com.google.gson.annotations.SerializedName;
import java.util.Date;

public class ChatbotResponse {
    @SerializedName("answer")
    private String answer;

    @SerializedName("intent")
    private String intent;

    @SerializedName("data")
    private String data;

    @SerializedName("timestamp")
    private Date timestamp;

    public ChatbotResponse() {
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getIntent() {
        return intent;
    }

    public void setIntent(String intent) {
        this.intent = intent;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
