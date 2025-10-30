package com.example.myapplication.ui;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.repository.AuthRepository;
import com.example.myapplication.ui.admin.AdminAccountsListActivity;
import com.example.myapplication.ui.admin.categories.AdminCategoriesActivity;
import com.example.myapplication.ui.admin.vehicles.AdminVehiclesListActivity;
import com.example.myapplication.ui.chatbot.ChatbotActivity;
import com.example.myapplication.utils.SharedPrefManager;
import android.widget.Toast;

public class DashboardAdminActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard_admin);

        // Vehicles button
        findViewById(R.id.btnVehicles).setOnClickListener(v -> {
            startActivity(new Intent(this, AdminVehiclesListActivity.class));
        });

        // Chatbot button
        findViewById(R.id.btnChat).setOnClickListener(v -> {
            startActivity(new Intent(this, ChatbotActivity.class));
        });

        // Categories button
        findViewById(R.id.btnCategories).setOnClickListener(v -> {
            startActivity(new Intent(this, AdminCategoriesActivity.class));
        });

        // Accounts button
        findViewById(R.id.btnAccount).setOnClickListener(v -> {
            startActivity(new Intent(this, AdminAccountsListActivity.class));
        });

        // Bottom nav - Message
        findViewById(R.id.bottomNavMessage).setOnClickListener(v -> {
            startActivity(new Intent(this, ChatbotActivity.class));
        });

        // Bottom nav - Logout (Host icon repurposed)
        findViewById(R.id.bottomNavHost).setOnClickListener(v -> {
            new AuthRepository(this).logout(() -> {
                startActivity(new Intent(this, LoginActivity.class));
                finish();
            });
        });
    }
}



