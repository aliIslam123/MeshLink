package com.example.meshlink;

public class PeerModel {
    private String endpointId;
    private String deviceName;
    private boolean isConnected;

    public PeerModel(String endpointId, String deviceName) {
        this.endpointId = endpointId;
        this.deviceName = deviceName;
        this.isConnected = false;
    }

    public String getEndpointId() { return endpointId; }
    public String getDeviceName() { return deviceName; }
    public boolean isConnected() { return isConnected; }
    public void setConnected(boolean connected) { isConnected = connected; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PeerModel peerModel = (PeerModel) o;
        return endpointId.equals(peerModel.endpointId);
    }

    @Override
    public int hashCode() {
        return endpointId.hashCode();
    }
}
