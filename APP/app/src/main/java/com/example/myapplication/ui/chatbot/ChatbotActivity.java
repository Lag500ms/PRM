package com.example.myapplication.ui.chatbot;


import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication.R;
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
    private ChatAdapter adapter;
    private List<ChatAdapter.ChatMessage> messages = new ArrayList<>();

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

        adapter = new ChatAdapter(messages);
        rvChat.setLayoutManager(new LinearLayoutManager(this));
        rvChat.setAdapter(adapter);

        // Welcome message
        addMessage("Xin chào! Em có thể giúp anh/chị gì ạ? 😊", false);

        btnSend.setOnClickListener(v -> send());
    }

    private void send() {
        String question = etMessage.getText() != null ? etMessage.getText().toString().trim() : "";
        if (question.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập câu hỏi", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Add user message
        addMessage(question, true);
        etMessage.setText("");
        
        progressBar.setVisibility(View.VISIBLE);
        btnSend.setEnabled(false);

        repo.askQuestion(question, new ChatbotRepository.ChatbotCallback() {
            @Override
            public void onSuccess(ChatbotResponse resp) {
                progressBar.setVisibility(View.GONE);
                btnSend.setEnabled(true);
                if (resp != null && resp.getAnswer() != null) {
                    // Add bot response
                    addMessage(resp.getAnswer(), false);
                } else {
                    addMessage("Xin lỗi, em không hiểu câu hỏi này.", false);
                }
            }

            @Override
            public void onError(String error) {
                progressBar.setVisibility(View.GONE);
                btnSend.setEnabled(true);
                addMessage("Xin lỗi, có lỗi xảy ra: " + error, false);
            }
        });
    }

    private void addMessage(String text, boolean isUser) {
        messages.add(new ChatAdapter.ChatMessage(text, isUser));
        adapter.notifyItemInserted(messages.size() - 1);
        rvChat.scrollToPosition(messages.size() - 1);
    }
}

