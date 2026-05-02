package com.example.meshlink;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    TextView btnBack;
    Button btnSave, btnReset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        btnBack = findViewById(R.id.btnBack);
        btnSave = findViewById(R.id.btnSave);
        btnReset = findViewById(R.id.btnReset);

        // زر الرجوع
        btnBack.setOnClickListener(v -> {
            finish(); // يرجع للصفحة اللي قبلها (MainActivity)
        });

        // حفظ
        btnSave.setOnClickListener(v ->
                Toast.makeText(this, "Settings Saved", Toast.LENGTH_SHORT).show());

        // Reset
        btnReset.setOnClickListener(v ->
                Toast.makeText(this, "Reset Done", Toast.LENGTH_SHORT).show());
    }
}