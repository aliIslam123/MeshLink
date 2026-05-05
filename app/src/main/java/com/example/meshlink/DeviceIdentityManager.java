package com.example.meshlink;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import java.util.UUID;

public class DeviceIdentityManager {
    private static final String PREFS_NAME = "MeshLinkPrefs";
    private static final String KEY_DEVICE_ID = "device_id";
    private static final String KEY_DEVICE_NAME = "device_name";

    private String deviceId;
    private String deviceName;
    private SharedPreferences prefs;

    public DeviceIdentityManager(Context context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        deviceId = prefs.getString(KEY_DEVICE_ID, null);
        if (deviceId == null) {
            deviceId = UUID.randomUUID().toString();
            prefs.edit().putString(KEY_DEVICE_ID, deviceId).apply();
        }
        deviceName = prefs.getString(KEY_DEVICE_NAME, Build.MODEL);
    }

    public String getDeviceId() {
        return deviceId;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String name) {
        this.deviceName = name;
        prefs.edit().putString(KEY_DEVICE_NAME, name).apply();
    }
}
