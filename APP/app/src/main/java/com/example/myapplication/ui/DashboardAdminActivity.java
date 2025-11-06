package com.example.myapplication.ui;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;

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

        // Chatbot button - Ẩn cho Admin (chỉ Dealer mới có chatbot)
        findViewById(R.id.btnChat).setVisibility(View.GONE);

        // Categories button
        findViewById(R.id.btnCategories).setOnClickListener(v -> {
            startActivity(new Intent(this, AdminCategoriesActivity.class));
        });

        // Accounts button
        findViewById(R.id.btnAccount).setOnClickListener(v -> {
            startActivity(new Intent(this, AdminAccountsListActivity.class));
        });


        // Profile avatar - Show popup menu
        ImageView imgProfile = findViewById(R.id.imgProfile);
        imgProfile.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(this, v);
            popupMenu.getMenuInflater().inflate(R.menu.menu_profile_popup, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.menu_logout) {
                    new AuthRepository(this).logout(() -> {
                        startActivity(new Intent(this, LoginActivity.class));
                        finish();
                    });
                    return true;
                }
                return false;
            });
            popupMenu.show();
        });
    }
}



