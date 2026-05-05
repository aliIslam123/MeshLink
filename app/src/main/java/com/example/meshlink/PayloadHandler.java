package com.example.meshlink;

import androidx.annotation.NonNull;
import com.google.android.gms.nearby.connection.Payload;
import com.google.android.gms.nearby.connection.PayloadCallback;
import com.google.android.gms.nearby.connection.PayloadTransferUpdate;
import java.nio.charset.StandardCharsets;

public class PayloadHandler extends PayloadCallback {
    private final MeshManager meshManager;

    public PayloadHandler(MeshManager meshManager) {
        this.meshManager = meshManager;
    }

    @Override
    public void onPayloadReceived(@NonNull String endpointId, @NonNull Payload payload) {
        if (payload.getType() == Payload.Type.BYTES) {
            byte[] bytes = payload.asBytes();
            if (bytes != null) {
                String json = new String(bytes, StandardCharsets.UTF_8);
                meshManager.onPayloadReceived(endpointId, json);
            }
        }
    }

    @Override
    public void onPayloadTransferUpdate(@NonNull String endpointId, @NonNull PayloadTransferUpdate update) {
        // Can be used to track progress of large payloads
    }
}
