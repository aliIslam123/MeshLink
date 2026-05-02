package com.example.meshlink;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity {

    TextView txtStatus;
    Button btnStart;
    boolean isOn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // ربط العناصر
        txtStatus = findViewById(R.id.txtStatus);
        btnStart = findViewById(R.id.btnStart);

        LinearLayout btnDevices = findViewById(R.id.btnDevices);
        LinearLayout btnChat = findViewById(R.id.btnChat);
        LinearLayout btnDebug = findViewById(R.id.btnDebug);
        LinearLayout btnSettings = findViewById(R.id.btnSettings);

        // زر تشغيل الشبكة
        btnStart.setOnClickListener(v -> {
            isOn = !isOn;

            if (isOn) {
                txtStatus.setText("ON");
                btnStart.setText("Stop Network");
                Toast.makeText(this, "Network Started", Toast.LENGTH_SHORT).show();
            } else {
                txtStatus.setText("OFF");
                btnStart.setText("Start Network");
                Toast.makeText(this, "Network Stopped", Toast.LENGTH_SHORT).show();
            }
        });

        // Nearby Devices
        btnDevices.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, DevicesActivity.class);
            startActivity(intent);
        });

        // Chat (التنقل لصفحة المحادثة)
        btnChat.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, ChatActivity.class);
            startActivity(intent);
        });

        // Debug (StatusActivity)
        btnDebug.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, StatusActivity.class);
            startActivity(intent);
        });

        // 🔥 Settings (التنقل للصفحة الجديدة)
        btnSettings.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, SettingsActivity.class);
            startActivity(intent);
        });
    }
}
