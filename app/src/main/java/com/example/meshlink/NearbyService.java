package com.example.meshlink;

import android.content.Context;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.AdvertisingOptions;
import com.google.android.gms.nearby.connection.ConnectionsClient;
import com.google.android.gms.nearby.connection.Payload;
import com.google.android.gms.nearby.connection.Strategy;
import java.nio.charset.StandardCharsets;

public class NearbyService {
    private static final String SERVICE_ID = "com.example.meshlink.SERVICE_ID";
    private static final Strategy STRATEGY = Strategy.P2P_CLUSTER;

    private final ConnectionsClient connectionsClient;
    private final MeshManager meshManager;
    private final String localEndpointName;

    private final DiscoveryManager discoveryManager;
    private final ConnectionManager connectionManager;
    private final PayloadHandler payloadHandler;

    public NearbyService(Context context, MeshManager meshManager) {
        this.meshManager = meshManager;
        this.connectionsClient = Nearby.getConnectionsClient(context);
        this.localEndpointName = meshManager.getIdentityManager().getDeviceName();

        this.payloadHandler = new PayloadHandler(meshManager);
        this.discoveryManager = new DiscoveryManager(connectionsClient, SERVICE_ID, STRATEGY, meshManager);
        this.connectionManager = new ConnectionManager(connectionsClient, localEndpointName, meshManager, payloadHandler);
    }

    public void startAll() {
        startAdvertising();
        discoveryManager.startDiscovery();
    }

    public void stopAll() {
        connectionsClient.stopAdvertising();
        discoveryManager.stopDiscovery();
        connectionsClient.stopAllEndpoints();
    }

    private void startAdvertising() {
        AdvertisingOptions advertisingOptions = new AdvertisingOptions.Builder().setStrategy(STRATEGY).build();
        connectionsClient.startAdvertising(localEndpointName, SERVICE_ID, connectionManager.connectionLifecycleCallback, advertisingOptions)
                .addOnSuccessListener(unused -> meshManager.log("Advertising started..."))
                .addOnFailureListener(e -> meshManager.log("Advertising failed: " + e.getMessage()));
    }

    public void connect(String endpointId) {
        connectionManager.connect(endpointId);
    }

    public void disconnect(String endpointId) {
        connectionsClient.disconnectFromEndpoint(endpointId);
        meshManager.onDisconnected(endpointId);
    }

    public void sendPayload(String endpointId, String data) {
        connectionsClient.sendPayload(endpointId, Payload.fromBytes(data.getBytes(StandardCharsets.UTF_8)));
    }
}
