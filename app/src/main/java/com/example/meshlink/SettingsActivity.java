package com.example.meshlink;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    TextView btnBack;
    Button btnSave, btnReset;
    EditText etName, etId, etTTL;
    MeshManager meshManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        meshManager = MeshManager.getInstance(this);

        btnBack = findViewById(R.id.btnBack);
        btnSave = findViewById(R.id.btnSave);
        btnReset = findViewById(R.id.btnReset);
        etName = findViewById(R.id.etName);
        etId = findViewById(R.id.etId);
        etTTL = findViewById(R.id.etTTL);

        // Load current values
        etName.setText(meshManager.getIdentityManager().getDeviceName());
        etId.setText(meshManager.getIdentityManager().getDeviceId());
        etId.setEnabled(false); // ID is usually fixed

        btnBack.setOnClickListener(v -> finish());

        btnSave.setOnClickListener(v -> {
            String newName = etName.getText().toString().trim();
            if (!newName.isEmpty()) {
                meshManager.getIdentityManager().setDeviceName(newName);
                meshManager.log("Device name changed to: " + newName);
                Toast.makeText(this, "Settings Saved", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Name cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });

        btnReset.setOnClickListener(v -> {
            etName.setText(android.os.Build.MODEL);
            Toast.makeText(this, "Values reset. Press save to apply.", Toast.LENGTH_SHORT).show();
        });
    }
}
