package com.example.meshlink;

import java.util.UUID;

public class MessageModel {
    private String messageId;
    private String senderId;
    private String senderName;
    private String targetId;
    private String content;
    private long timestamp;
    private int ttl;
    private int hops;

    public MessageModel(String senderId, String senderName, String targetId, String content) {
        this.messageId = UUID.randomUUID().toString();
        this.senderId = senderId;
        this.senderName = senderName;
        this.targetId = targetId;
        this.content = content;
        this.timestamp = System.currentTimeMillis();
        this.ttl = 5;
        this.hops = 0;
    }

    // Getters and Setters
    public String getMessageId() { return messageId; }
    public String getSenderId() { return senderId; }
    public String getSenderName() { return senderName; }
    public String getTargetId() { return targetId; }
    public String getContent() { return content; }
    public long getTimestamp() { return timestamp; }
    public int getTtl() { return ttl; }
    public void setTtl(int ttl) { this.ttl = ttl; }
    public int getHops() { return hops; }
    public void setHops(int hops) { this.hops = hops; }
}
