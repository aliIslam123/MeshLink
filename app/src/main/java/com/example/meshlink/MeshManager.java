package com.example.meshlink;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class MeshManager {
    private static MeshManager instance;
    private final Context context;
    private final DeviceIdentityManager identityManager;
    private final DuplicateManager duplicateManager;
    private final RoutingEngine routingEngine;
    private NearbyService nearbyService;

    private final List<PeerModel> discoveredPeers = new CopyOnWriteArrayList<>();
    private final List<PeerModel> connectedPeers = new CopyOnWriteArrayList<>();
    private final List<MessageModel> messageHistory = new ArrayList<>();
    private final List<LogModel> logs = new ArrayList<>();

    private int sentCount = 0;
    private int receivedCount = 0;
    private int forwardedCount = 0;
    private int defaultTtl = 5;

    private MeshListener listener;
    private final Handler uiHandler = new Handler(Looper.getMainLooper());

    public interface MeshListener {
        void onPeerDiscovered(List<PeerModel> peers);
        void onPeerConnected(PeerModel peer);
        void onPeerDisconnected(PeerModel peer);
        void onMessageReceived(MessageModel message);
        void onLogUpdated(List<LogModel> logs);
        void onStatusChanged(boolean isRunning);
        void onCountersUpdated(int sent, int received, int forwarded);
    }

    private MeshManager(Context context) {
        this.context = context.getApplicationContext();
        this.identityManager = new DeviceIdentityManager(this.context);
        this.duplicateManager = new DuplicateManager();
        this.routingEngine = new RoutingEngine(this);
    }

    public static synchronized MeshManager getInstance(Context context) {
        if (instance == null) {
            instance = new MeshManager(context);
        }
        return instance;
    }

    public void setNearbyService(NearbyService nearbyService) {
        this.nearbyService = nearbyService;
    }

    public void setListener(MeshListener listener) {
        this.listener = listener;
    }

    public DeviceIdentityManager getIdentityManager() { return identityManager; }
    public DuplicateManager getDuplicateManager() { return duplicateManager; }
    public List<PeerModel> getDiscoveredPeers() { return discoveredPeers; }
    public List<PeerModel> getConnectedPeers() { return connectedPeers; }
    public List<MessageModel> getMessageHistory() { return messageHistory; }
    public List<LogModel> getLogs() { return logs; }
    
    public int getSentCount() { return sentCount; }
    public int getReceivedCount() { return receivedCount; }
    public int getForwardedCount() { return forwardedCount; }

    public int getDefaultTtl() { return defaultTtl; }
    public void setDefaultTtl(int ttl) { this.defaultTtl = ttl; }

    public void startNetwork() {
        if (nearbyService != null) {
            nearbyService.startAll();
            notifyStatusChanged(true);
            log("Mesh Network Started");
        }
    }

    public void stopNetwork() {
        if (nearbyService != null) {
            nearbyService.stopAll();
            discoveredPeers.clear();
            connectedPeers.clear();
            notifyStatusChanged(false);
            log("Mesh Network Stopped");
        }
    }

    public void sendMessage(String targetId, String content) {
        MessageModel message = new MessageModel(
                identityManager.getDeviceId(),
                identityManager.getDeviceName(),
                targetId,
                content
        );
        message.setTtl(defaultTtl);
        
        duplicateManager.isDuplicate(message.getMessageId());
        messageHistory.add(message);
        sentCount++;
        
        log("Sending message: " + content + " to " + targetId);
        broadcastToPeers(message, null);
        notifyCountersUpdated();
    }

    public void forwardMessage(MessageModel message, String excludeEndpointId) {
        log("Forwarding message: " + message.getMessageId());
        forwardedCount++;
        broadcastToPeers(message, excludeEndpointId);
        notifyCountersUpdated();
    }

    private void broadcastToPeers(MessageModel message, String excludeEndpointId) {
        String json = JsonUtil.toJson(message);
        for (PeerModel peer : connectedPeers) {
            if (excludeEndpointId == null || !peer.getEndpointId().equals(excludeEndpointId)) {
                nearbyService.sendPayload(peer.getEndpointId(), json);
            }
        }
    }

    public void deliverMessage(MessageModel message) {
        uiHandler.post(() -> {
            messageHistory.add(message);
            receivedCount++;
            if (listener != null) listener.onMessageReceived(message);
            log("Message delivered: " + message.getContent() + " from " + message.getSenderName());
            notifyCountersUpdated();
        });
    }

    public void onEndpointFound(String endpointId, String name) {
        PeerModel peer = new PeerModel(endpointId, name);
        if (!discoveredPeers.contains(peer)) {
            discoveredPeers.add(peer);
            uiHandler.post(() -> {
                if (listener != null) listener.onPeerDiscovered(discoveredPeers);
            });
            log("Peer found: " + name);
        }
    }

    public void onEndpointLost(String endpointId) {
        discoveredPeers.removeIf(p -> p.getEndpointId().equals(endpointId));
        uiHandler.post(() -> {
            if (listener != null) listener.onPeerDiscovered(discoveredPeers);
        });
        log("Peer lost: " + endpointId);
    }

    public void onConnected(String endpointId, String name) {
        PeerModel peer = new PeerModel(endpointId, name);
        peer.setConnected(true);
        if (!connectedPeers.contains(peer)) {
            connectedPeers.add(peer);
            uiHandler.post(() -> {
                if (listener != null) listener.onPeerConnected(peer);
            });
            log("Connected to: " + name);
        }
    }

    public void onDisconnected(String endpointId) {
        PeerModel peer = null;
        for (PeerModel p : connectedPeers) {
            if (p.getEndpointId().equals(endpointId)) {
                peer = p;
                break;
            }
        }
        if (peer != null) {
            connectedPeers.remove(peer);
            final PeerModel finalPeer = peer;
            uiHandler.post(() -> {
                if (listener != null) listener.onPeerDisconnected(finalPeer);
            });
            log("Disconnected from: " + peer.getDeviceName());
        }
    }

    public void onPayloadReceived(String endpointId, String json) {
        try {
            MessageModel message = JsonUtil.fromJson(json, MessageModel.class);
            routingEngine.processIncomingMessage(message, endpointId);
        } catch (Exception e) {
            log("Error parsing payload: " + e.getMessage());
        }
    }

    public void log(String message) {
        uiHandler.post(() -> {
            logs.add(new LogModel(message));
            if (logs.size() > 200) logs.remove(0);
            if (listener != null) listener.onLogUpdated(logs);
        });
    }

    private void notifyStatusChanged(boolean isRunning) {
        uiHandler.post(() -> {
            if (listener != null) listener.onStatusChanged(isRunning);
        });
    }
    
    private void notifyCountersUpdated() {
        uiHandler.post(() -> {
            if (listener != null) listener.onCountersUpdated(sentCount, receivedCount, forwardedCount);
        });
    }

    public void connectToPeer(String endpointId) {
        nearbyService.connect(endpointId);
    }

    public void disconnectFromPeer(String endpointId) {
        nearbyService.disconnect(endpointId);
    }
}
