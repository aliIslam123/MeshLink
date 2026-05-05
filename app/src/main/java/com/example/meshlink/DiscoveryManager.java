package com.example.meshlink;

import androidx.annotation.NonNull;
import com.google.android.gms.nearby.connection.ConnectionsClient;
import com.google.android.gms.nearby.connection.DiscoveredEndpointInfo;
import com.google.android.gms.nearby.connection.DiscoveryOptions;
import com.google.android.gms.nearby.connection.EndpointDiscoveryCallback;
import com.google.android.gms.nearby.connection.Strategy;

public class DiscoveryManager {
    private final ConnectionsClient connectionsClient;
    private final String serviceId;
    private final Strategy strategy;
    private final MeshManager meshManager;

    public DiscoveryManager(ConnectionsClient client, String serviceId, Strategy strategy, MeshManager meshManager) {
        this.connectionsClient = client;
        this.serviceId = serviceId;
        this.strategy = strategy;
        this.meshManager = meshManager;
    }

    public void startDiscovery() {
        DiscoveryOptions options = new DiscoveryOptions.Builder().setStrategy(strategy).build();
        connectionsClient.startDiscovery(serviceId, endpointDiscoveryCallback, options)
                .addOnSuccessListener(unused -> meshManager.log("Discovery started..."))
                .addOnFailureListener(e -> meshManager.log("Discovery failed: " + e.getMessage()));
    }

    public void stopDiscovery() {
        connectionsClient.stopDiscovery();
    }

    private final EndpointDiscoveryCallback endpointDiscoveryCallback = new EndpointDiscoveryCallback() {
        @Override
        public void onEndpointFound(@NonNull String endpointId, @NonNull DiscoveredEndpointInfo info) {
            meshManager.onEndpointFound(endpointId, info.getEndpointName());
        }

        @Override
        public void onEndpointLost(@NonNull String endpointId) {
            meshManager.onEndpointLost(endpointId);
        }
    };
}
