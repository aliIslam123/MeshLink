package com.example.meshlink;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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
import java.util.Locale;

public class ChatActivity extends Activity {

    private LinearLayout linearMessages;
    private ScrollView scrollMessages;
    private EditText etMessage;
    private ImageButton btnSend;
    private TextView tvBroadcast, tvDevices;
    private View greenDot;
    private LinearLayout btnDeviceSetup;
    private Handler handler;
    private SimpleDateFormat timeFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        linearMessages = findViewById(R.id.linearMessages);
        scrollMessages = findViewById(R.id.scrollMessages);
        etMessage = findViewById(R.id.etMessage);
        btnSend = findViewById(R.id.btnSend);
        tvBroadcast = findViewById(R.id.tvBroadcast);
        tvDevices = findViewById(R.id.tvDevices);
        greenDot = findViewById(R.id.greenDot);
        btnDeviceSetup = findViewById(R.id.btnDeviceSetup);

        handler = new Handler(Looper.getMainLooper());
        timeFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());

        styleFixedElements();
        addInitialMessages();
        setupListeners();
    }

    private void styleFixedElements() {
        GradientDrawable broadcastBg = new GradientDrawable();
        broadcastBg.setCornerRadius(dpToPx(18));
        broadcastBg.setColor(Color.parseColor("#00E5A0"));
        tvBroadcast.setBackground(broadcastBg);
        tvBroadcast.setTextColor(Color.BLACK);

        GradientDrawable devicesBg = new GradientDrawable();
        devicesBg.setCornerRadius(dpToPx(18));
        devicesBg.setColor(Color.parseColor("#0B0F14"));
        tvDevices.setBackground(devicesBg);
        tvDevices.setTextColor(Color.parseColor("#00E5A0"));

        GradientDrawable setupBg = new GradientDrawable();
        setupBg.setCornerRadius(dpToPx(18));
        setupBg.setColor(Color.parseColor("#0B0F14"));
        btnDeviceSetup.setBackground(setupBg);
        btnDeviceSetup.getChildAt(1).setVisibility(View.VISIBLE);

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

    private void addInitialMessages() {
        handler.postDelayed(() -> {
            addBubble("Hey, Is anyone there?", false);
        }, 300);

        handler.postDelayed(() -> {
            addBubble("Yes! Connection established.", true);
        }, 800);
    }

    private void setupListeners() {
        tvBroadcast.setOnClickListener(v -> {
            GradientDrawable bg = new GradientDrawable();
            bg.setCornerRadius(dpToPx(18));
            bg.setColor(Color.parseColor("#00E5A0"));
            tvBroadcast.setBackground(bg);
            tvBroadcast.setTextColor(Color.BLACK);

            GradientDrawable bg2 = new GradientDrawable();
            bg2.setCornerRadius(dpToPx(18));
            bg2.setColor(Color.parseColor("#0B0F14"));
            tvDevices.setBackground(bg2);
            tvDevices.setTextColor(Color.parseColor("#00E5A0"));

            Toast.makeText(this, "Broadcast mode", Toast.LENGTH_SHORT).show();
        });

        tvDevices.setOnClickListener(v -> {
            GradientDrawable bg = new GradientDrawable();
            bg.setCornerRadius(dpToPx(18));
            bg.setColor(Color.parseColor("#00E5A0"));
            tvDevices.setBackground(bg);
            tvDevices.setTextColor(Color.BLACK);

            GradientDrawable bg2 = new GradientDrawable();
            bg2.setCornerRadius(dpToPx(18));
            bg2.setColor(Color.parseColor("#0B0F14"));
            tvBroadcast.setBackground(bg2);
            tvBroadcast.setTextColor(Color.parseColor("#00E5A0"));

            Toast.makeText(this, "Devices mode", Toast.LENGTH_SHORT).show();
        });

        btnSend.setOnClickListener(v -> sendMessage());
    }

    private void sendMessage() {
        String text = etMessage.getText().toString().trim();
        if (text.isEmpty()) return;

        addBubble(text, true);
        etMessage.setText("");

        handler.postDelayed(() -> {
            addBubble(generateReply(text), false);
        }, 1000);
    }

    private void addBubble(String message, boolean isUser) {
        String time = timeFormat.format(new Date());

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

    private String generateReply(String msg) {
        msg = msg.toLowerCase();
        if (msg.contains("hello") || msg.contains("hi") || msg.contains("hey")) {
            return "Hey! How can I help you?";
        } else if (msg.contains("test")) {
            return "Test received! Everything is working.";
        } else if (msg.contains("how are you")) {
            return "I'm doing great, thanks!";
        } else if (msg.contains("anyone")) {
            return "Yes! Connection established.";
        } else {
            return "Got your message!";
        }
    }

    private int dpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }
}