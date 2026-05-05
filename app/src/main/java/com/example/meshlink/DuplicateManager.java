package com.example.meshlink;

import java.util.HashSet;
import java.util.Set;

public class DuplicateManager {
    private final Set<String> receivedMessageIds = new HashSet<>();

    public synchronized boolean isDuplicate(String messageId) {
        if (receivedMessageIds.contains(messageId)) {
            return true;
        }
        receivedMessageIds.add(messageId);
        // Optionally: Limit the size of the set to prevent memory issues over long periods
        if (receivedMessageIds.size() > 1000) {
            receivedMessageIds.clear(); // Simple reset for now
        }
        return false;
    }
}
