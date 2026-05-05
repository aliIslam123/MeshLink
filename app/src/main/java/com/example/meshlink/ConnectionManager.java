package com.example.meshlink;

import androidx.annotation.NonNull;
import com.google.android.gms.nearby.connection.ConnectionInfo;
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback;
import com.google.android.gms.nearby.connection.ConnectionResolution;
import com.google.android.gms.nearby.connection.ConnectionsClient;
import com.google.android.gms.nearby.connection.PayloadCallback;
import java.util.HashMap;
import java.util.Map;

public class ConnectionManager {
    private final ConnectionsClient connectionsClient;
    private final MeshManager meshManager;
    private final String localEndpointName;
    private final PayloadCallback payloadCallback;
    private final Map<String, String> pendingConnections = new HashMap<>();

    public ConnectionManager(ConnectionsClient client, String localName, MeshManager meshManager, PayloadCallback payloadCallback) {
        this.connectionsClient = client;
        this.localEndpointName = localName;
        this.meshManager = meshManager;
        this.payloadCallback = payloadCallback;
    }

    public void connect(String endpointId) {
        connectionsClient.requestConnection(localEndpointName, endpointId, connectionLifecycleCallback)
                .addOnSuccessListener(unused -> meshManager.log("Connection requested to: " + endpointId))
                .addOnFailureListener(e -> meshManager.log("Connection request failed: " + e.getMessage()));
    }

    public final ConnectionLifecycleCallback connectionLifecycleCallback = new ConnectionLifecycleCallback() {
        @Override
        public void onConnectionInitiated(@NonNull String endpointId, @NonNull ConnectionInfo connectionInfo) {
            meshManager.log("Connection initiated with: " + connectionInfo.getEndpointName());
            pendingConnections.put(endpointId, connectionInfo.getEndpointName());
            connectionsClient.acceptConnection(endpointId, payloadCallback);
        }

        @Override
        public void onConnectionResult(@NonNull String endpointId, @NonNull ConnectionResolution result) {
            String name = pendingConnections.remove(endpointId);
            if (name == null) name = "Remote Peer";

            if (result.getStatus().isSuccess()) {
                meshManager.onConnected(endpointId, name);
            } else {
                meshManager.log("Connection failed with " + name + ": " + result.getStatus().getStatusMessage());
            }
        }

        @Override
        public void onDisconnected(@NonNull String endpointId) {
            meshManager.onDisconnected(endpointId);
        }
    };
}
