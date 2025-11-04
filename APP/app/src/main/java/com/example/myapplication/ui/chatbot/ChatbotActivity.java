package com.example.myapplication.ui.chatbot;


import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication.R;
import com.example.myapplication.model.chatbot.ChatbotRequest;
import com.example.myapplication.model.chatbot.ChatbotResponse;
import com.example.myapplication.repository.ChatbotRepository;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import java.util.ArrayList;
import java.util.List;

public class ChatbotActivity extends AppCompatActivity {

    private RecyclerView rvChat;
    private TextInputEditText etMessage;
    private MaterialButton btnSend;
    private ProgressBar progressBar;
    private ChatbotRepository repo;
    private List<String> messages = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatbot);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        repo = new ChatbotRepository(this);
        rvChat = findViewById(R.id.rvChat);
        etMessage = findViewById(R.id.etMessage);
        btnSend = findViewById(R.id.btnSend);
        progressBar = findViewById(R.id.progressBar);

        rvChat.setLayoutManager(new LinearLayoutManager(this));

        btnSend.setOnClickListener(v -> send());
    }

    private void send() {
        String question = etMessage.getText() != null ? etMessage.getText().toString().trim() : "";
        if (question.isEmpty()) {
            Toast.makeText(this, "Please enter a question", Toast.LENGTH_SHORT).show();
            return;
        }
        etMessage.setText("");
        progressBar.setVisibility(View.VISIBLE);
        btnSend.setEnabled(false);

        repo.askQuestion(question, new ChatbotRepository.ChatbotCallback() {
            @Override
            public void onSuccess(ChatbotResponse resp) {
                progressBar.setVisibility(View.GONE);
                btnSend.setEnabled(true);
                if (resp != null && resp.getAnswer() != null) {
                    Toast.makeText(ChatbotActivity.this, "Answer: " + resp.getAnswer(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onError(String error) {
                progressBar.setVisibility(View.GONE);
                btnSend.setEnabled(true);
                Toast.makeText(ChatbotActivity.this, "Error: " + error, Toast.LENGTH_LONG).show();
            }
        });
    }
}

