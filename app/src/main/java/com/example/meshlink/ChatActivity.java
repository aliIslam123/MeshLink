package com.example.meshlink;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChatActivity extends Activity implements MeshManager.MeshListener {

    private LinearLayout linearMessages;
    private ScrollView scrollMessages;
    private EditText etMessage;
    private ImageButton btnSend;
    private TextView tvBroadcast, tvDevices;
    private View greenDot;
    private LinearLayout btnDeviceSetup;
    private SimpleDateFormat timeFormat;
    private MeshManager meshManager;
    private String currentTargetId = "BROADCAST";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        meshManager = MeshManager.getInstance(this);
        meshManager.setListener(this);

        linearMessages = findViewById(R.id.linearMessages);
        scrollMessages = findViewById(R.id.scrollMessages);
        etMessage = findViewById(R.id.etMessage);
        btnSend = findViewById(R.id.btnSend);
        tvBroadcast = findViewById(R.id.tvBroadcast);
        tvDevices = findViewById(R.id.tvDevices);
        greenDot = findViewById(R.id.greenDot);
        btnDeviceSetup = findViewById(R.id.btnDeviceSetup);

        timeFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());

        styleFixedElements();
        loadMessageHistory();
        setupListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        meshManager.setListener(this);
    }

    private void styleFixedElements() {
        updateTargetUi();

        GradientDrawable setupBg = new GradientDrawable();
        setupBg.setCornerRadius(dpToPx(18));
        setupBg.setColor(Color.parseColor("#0B0F14"));
        btnDeviceSetup.setBackground(setupBg);

        GradientDrawable dotBg = new GradientDrawable();
        dotBg.setShape(GradientDrawable.OVAL);
        dotBg.setColor(Color.parseColor("#00E5A0"));
        greenDot.setBackground(dotBg);

        GradientDrawable inputBg = new GradientDrawable();
        inputBg.setCornerRadius(dpToPx(22));
        inputBg.setColor(Color.parseColor("#0B0F14"));
        inputBg.setStroke(dpToPx(1), Color.parseColor("#1A1D24"));
        etMessage.setBackground(inputBg);
        etMessage.setTextColor(Color.WHITE);
        etMessage.setHintTextColor(Color.parseColor("#6E737A"));

        btnSend.setColorFilter(Color.parseColor("#00332A"));
        btnSend.setBackgroundColor(Color.parseColor("#00E5A0"));
    }

    private void updateTargetUi() {
        if ("BROADCAST".equals(currentTargetId)) {
            tvBroadcast.setBackground(getRoundedBg("#00E5A0"));
            tvBroadcast.setTextColor(Color.BLACK);
            tvDevices.setBackground(getRoundedBg("#0B0F14"));
            tvDevices.setTextColor(Color.parseColor("#00E5A0"));
        } else {
            tvDevices.setBackground(getRoundedBg("#00E5A0"));
            tvDevices.setTextColor(Color.BLACK);
            tvBroadcast.setBackground(getRoundedBg("#0B0F14"));
            tvBroadcast.setTextColor(Color.parseColor("#00E5A0"));
        }
    }

    private GradientDrawable getRoundedBg(String color) {
        GradientDrawable gd = new GradientDrawable();
        gd.setCornerRadius(dpToPx(18));
        gd.setColor(Color.parseColor(color));
        return gd;
    }

    private void loadMessageHistory() {
        linearMessages.removeAllViews();
        for (MessageModel msg : meshManager.getMessageHistory()) {
            boolean isMine = msg.getSenderId().equals(meshManager.getIdentityManager().getDeviceId());
            addBubble(msg.getSenderName() + ": " + msg.getContent(), isMine, msg.getTimestamp());
        }
    }

    private void setupListeners() {
        tvBroadcast.setOnClickListener(v -> {
            currentTargetId = "BROADCAST";
            updateTargetUi();
            Toast.makeText(this, "Broadcast mode", Toast.LENGTH_SHORT).show();
        });

        tvDevices.setOnClickListener(v -> {
            if (!meshManager.getConnectedPeers().isEmpty()) {
                currentTargetId = meshManager.getConnectedPeers().get(0).getEndpointId();
                Toast.makeText(this, "Messaging: " + meshManager.getConnectedPeers().get(0).getDeviceName(), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "No devices connected. Staying in Broadcast.", Toast.LENGTH_SHORT).show();
                currentTargetId = "BROADCAST";
            }
            updateTargetUi();
        });

        btnSend.setOnClickListener(v -> sendMessage());
    }

    private void sendMessage() {
        String text = etMessage.getText().toString().trim();
        if (text.isEmpty()) return;

        meshManager.sendMessage(currentTargetId, text);
        addBubble("Me: " + text, true, System.currentTimeMillis());
        etMessage.setText("");
    }

    private void addBubble(String message, boolean isUser, long timestamp) {
        String time = timeFormat.format(new Date(timestamp));

        LinearLayout container = new LinearLayout(this);
        LinearLayout.LayoutParams containerParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        containerParams.setMargins(0, 0, 0, dpToPx(12));
        container.setLayoutParams(containerParams);
        container.setOrientation(LinearLayout.VERTICAL);
        container.setGravity(isUser ? Gravity.END : Gravity.START);

        LinearLayout bubble = new LinearLayout(this);
        LinearLayout.LayoutParams bubbleParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        bubble.setLayoutParams(bubbleParams);
        bubble.setOrientation(LinearLayout.VERTICAL);
        int padding = dpToPx(12);
        bubble.setPadding(padding, padding, padding, padding);

        GradientDrawable bubbleBg = new GradientDrawable();
        bubbleBg.setCornerRadius(dpToPx(20));
        String bubbleColor = isUser ? "#00E5A0" : "#1A1D24";
        bubbleBg.setColor(Color.parseColor(bubbleColor));
        bubble.setBackground(bubbleBg);

        TextView tvMsg = new TextView(this);
        tvMsg.setText(message);
        tvMsg.setTextColor(isUser ? Color.parseColor("#002A22") : Color.WHITE);
        tvMsg.setTextSize(15);
        tvMsg.setMaxWidth(dpToPx(260));

        TextView tvTime = new TextView(this);
        tvTime.setText(time);
        String timeColor = isUser ? "#004D40" : "#6E737A";
        tvTime.setTextColor(Color.parseColor(timeColor));
        tvTime.setTextSize(11);
        LinearLayout.LayoutParams timeParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        timeParams.setMargins(0, dpToPx(4), 0, 0);
        tvTime.setLayoutParams(timeParams);

        bubble.addView(tvMsg);
        bubble.addView(tvTime);
        container.addView(bubble);
        linearMessages.addView(container);

        scrollMessages.post(() -> scrollMessages.fullScroll(View.FOCUS_DOWN));
    }

    private int dpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }

    @Override
    public void onMessageReceived(MessageModel message) {
        boolean isMine = message.getSenderId().equals(meshManager.getIdentityManager().getDeviceId());
        addBubble(message.getSenderName() + ": " + message.getContent(), isMine, message.getTimestamp());
    }

    @Override public void onPeerDiscovered(List<PeerModel> peers) {}
    @Override public void onPeerConnected(PeerModel peer) {}
    @Override public void onPeerDisconnected(PeerModel peer) {}
    @Override public void onLogUpdated(List<LogModel> logs) {}
    @Override public void onStatusChanged(boolean isRunning) {}
    @Override public void onCountersUpdated(int sent, int received, int forwarded) {}
}
