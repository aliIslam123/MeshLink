package com.example.meshlink;

import java.util.List;

public class RoutingEngine {
    private final MeshManager meshManager;

    public RoutingEngine(MeshManager meshManager) {
        this.meshManager = meshManager;
    }

    public void processIncomingMessage(MessageModel message, String fromEndpointId) {
        if (meshManager.getDuplicateManager().isDuplicate(message.getMessageId())) {
            meshManager.log("Duplicate message ignored: " + message.getMessageId());
            return;
        }

        String myDeviceId = meshManager.getIdentityManager().getDeviceId();

        if (message.getTargetId().equals(myDeviceId) || message.getTargetId().equals("BROADCAST")) {
            meshManager.deliverMessage(message);
        }

        // Forwarding logic
        if (message.getTtl() > 0) {
            message.setTtl(message.getTtl() - 1);
            message.setHops(message.getHops() + 1);
            
            meshManager.forwardMessage(message, fromEndpointId);
        } else {
            meshManager.log("TTL reached 0 for message: " + message.getMessageId());
        }
    }
}
