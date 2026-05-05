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
import java.util.List;

public class DevicesActivity extends AppCompatActivity implements MeshManager.MeshListener {

    RecyclerView recyclerView;
    List<PeerModel> deviceList;
    TextView txtCount;
    DevicesAdapter adapter;
    MeshManager meshManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_devices);

        meshManager = MeshManager.getInstance(this);
        meshManager.setListener(this);

        recyclerView = findViewById(R.id.recyclerView);
        txtCount = findViewById(R.id.txtCount);

        deviceList = new ArrayList<>(meshManager.getDiscoveredPeers());

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new DevicesAdapter();
        recyclerView.setAdapter(adapter);

        updateCount();
    }

    @Override
    protected void onResume() {
        super.onResume();
        meshManager.setListener(this);
        refreshList();
    }

    private void refreshList() {
        deviceList.clear();
        deviceList.addAll(meshManager.getDiscoveredPeers());
        adapter.notifyDataSetChanged();
        updateCount();
    }

    void updateCount() {
        int count = meshManager.getConnectedPeers().size();
        if (txtCount != null) {
            txtCount.setText("Connected Devices: " + count);
        }
    }

    @Override
    public void onPeerDiscovered(List<PeerModel> peers) {
        refreshList();
    }

    @Override
    public void onPeerConnected(PeerModel peer) {
        refreshList();
    }

    @Override
    public void onPeerDisconnected(PeerModel peer) {
        refreshList();
    }

    @Override public void onMessageReceived(MessageModel message) {}
    @Override public void onLogUpdated(List<LogModel> logs) {}
    @Override public void onStatusChanged(boolean isRunning) {}
    @Override public void onCountersUpdated(int sent, int received, int forwarded) {}

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
            PeerModel device = deviceList.get(position);

            holder.name.setText(device.getDeviceName());
            holder.id.setText(device.getEndpointId());

            boolean isConnected = meshManager.getConnectedPeers().contains(device);

            if (isConnected) {
                holder.status.setText("Connected");
                holder.status.setTextColor(ContextCompat.getColor(DevicesActivity.this, R.color.green));
                holder.button.setText("Disconnect");
                holder.button.setBackgroundTintList(
                        ColorStateList.valueOf(ContextCompat.getColor(DevicesActivity.this, R.color.red))
                );
                holder.button.setEnabled(true);
            } else {
                holder.status.setText("Available");
                holder.status.setTextColor(ContextCompat.getColor(DevicesActivity.this, R.color.green));
                holder.button.setText("Connect");
                holder.button.setBackgroundTintList(
                        ColorStateList.valueOf(ContextCompat.getColor(DevicesActivity.this, R.color.blue))
                );
                holder.button.setEnabled(true);
            }

            holder.button.setOnClickListener(v -> {
                if (isConnected) {
                    meshManager.disconnectFromPeer(device.getEndpointId());
                } else {
                    meshManager.connectToPeer(device.getEndpointId());
                    holder.button.setText("Connecting...");
                    holder.button.setEnabled(false);
                }
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
