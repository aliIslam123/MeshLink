package com.example.meshlink;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class LogModel {
    private String message;
    private long timestamp;

    public LogModel(String message) {
        this.message = message;
        this.timestamp = System.currentTimeMillis();
    }

    public String getMessage() { return message; }
    public long getTimestamp() { return timestamp; }

    public String getFormattedTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }
}
