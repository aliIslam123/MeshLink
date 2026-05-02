package com.example.meshlink;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class DevicesActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ArrayList<Device> deviceList;
    TextView txtCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_devices);

        recyclerView = findViewById(R.id.recyclerView);
        txtCount = findViewById(R.id.txtCount);

        deviceList = new ArrayList<>();

        // بيانات تجريبية (Sample Data)
        for (int i = 1; i <= 10; i++) {
            deviceList.add(new Device("Device " + i, "D-" + (7000 + i), i == 2));
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new DevicesAdapter());

        updateCount();
    }

    void updateCount() {
        int count = 0;
        for (Device d : deviceList) {
            if (d.isConnected) count++;
        }
        if (txtCount != null) {
            txtCount.setText("Connected Devices: " + count);
        }
    }

    static class Device {
        String name, id;
        boolean isConnected;

        Device(String name, String id, boolean isConnected) {
            this.name = name;
            this.id = id;
            this.isConnected = isConnected;
        }
    }

    class DevicesAdapter extends RecyclerView.Adapter<DevicesAdapter.ViewHolder> {

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_device, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Device device = deviceList.get(position);

            holder.name.setText(device.name);
            holder.id.setText(device.id);

            if (device.isConnected) {
                holder.status.setText("Connected");
                holder.status.setTextColor(ContextCompat.getColor(DevicesActivity.this, R.color.green));
                holder.button.setText("Disconnect");
                holder.button.setBackgroundTintList(
                        ColorStateList.valueOf(ContextCompat.getColor(DevicesActivity.this, R.color.red))
                );
            } else {
                holder.status.setText("Available");
                holder.status.setTextColor(ContextCompat.getColor(DevicesActivity.this, R.color.green));
                holder.button.setText("Connect");
                holder.button.setBackgroundTintList(
                        ColorStateList.valueOf(ContextCompat.getColor(DevicesActivity.this, R.color.blue))
                );
            }

            holder.button.setOnClickListener(v -> {
                device.isConnected = !device.isConnected;
                notifyItemChanged(position);
                updateCount();
            });
        }

        @Override
        public int getItemCount() {
            return deviceList.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView name, id, status;
            Button button;

            ViewHolder(View itemView) {
                super(itemView);
                name = itemView.findViewById(R.id.deviceName);
                id = itemView.findViewById(R.id.deviceId);
                status = itemView.findViewById(R.id.deviceStatus);
                button = itemView.findViewById(R.id.btnAction);
            }
        }
    }
}
