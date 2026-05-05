package com.example.meshlink;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 123;
    TextView txtStatus;
    Button btnStart;
    boolean isNetworkRunning = false;
    MeshManager meshManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        meshManager = MeshManager.getInstance(this);

        txtStatus = findViewById(R.id.txtStatus);
        btnStart = findViewById(R.id.btnStart);

        LinearLayout btnDevices = findViewById(R.id.btnDevices);
        LinearLayout btnChat = findViewById(R.id.btnChat);
        LinearLayout btnDebug = findViewById(R.id.btnDebug);
        LinearLayout btnSettings = findViewById(R.id.btnSettings);

        btnStart.setOnClickListener(v -> {
            if (isNetworkRunning) {
                stopMeshService();
            } else {
                if (checkPermissions()) {
                    startMeshService();
                } else {
                    requestPermissions();
                }
            }
        });

        btnDevices.setOnClickListener(v -> startActivity(new Intent(this, DevicesActivity.class)));
        btnChat.setOnClickListener(v -> startActivity(new Intent(this, ChatActivity.class)));
        btnDebug.setOnClickListener(v -> startActivity(new Intent(this, StatusActivity.class)));
        btnSettings.setOnClickListener(v -> startActivity(new Intent(this, SettingsActivity.class)));

        updateUi(false);
    }

    private void startMeshService() {
        Intent serviceIntent = new Intent(this, MeshForegroundService.class);
        ContextCompat.startForegroundService(this, serviceIntent);
        isNetworkRunning = true;
        updateUi(true);
    }

    private void stopMeshService() {
        Intent serviceIntent = new Intent(this, MeshForegroundService.class);
        stopService(serviceIntent);
        isNetworkRunning = false;
        updateUi(false);
    }

    private void updateUi(boolean running) {
        txtStatus.setText(running ? "ON" : "OFF");
        btnStart.setText(running ? "Stop Network" : "Start Network");
    }

    private boolean checkPermissions() {
        List<String> permissions = getRequiredPermissions();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    private void requestPermissions() {
        List<String> permissions = getRequiredPermissions();
        ActivityCompat.requestPermissions(this, permissions.toArray(new String[0]), PERMISSION_REQUEST_CODE);
    }

    private List<String> getRequiredPermissions() {
        List<String> permissions = new ArrayList<>();
        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            permissions.add(Manifest.permission.BLUETOOTH_SCAN);
            permissions.add(Manifest.permission.BLUETOOTH_ADVERTISE);
            permissions.add(Manifest.permission.BLUETOOTH_CONNECT);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions.add(Manifest.permission.NEARBY_WIFI_DEVICES);
            permissions.add(Manifest.permission.POST_NOTIFICATIONS);
        }

        return permissions;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            boolean allGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                    break;
                }
            }
            if (allGranted) {
                startMeshService();
            } else {
                Toast.makeText(this, "Permissions are required for MeshLink to work.", Toast.LENGTH_LONG).show();
            }
        }
    }
}
