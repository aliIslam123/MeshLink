package com.example.meshlink;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * StatusActivity — "Status & Debug" dashboard screen.
 */
public class StatusActivity extends AppCompatActivity {

    private int sentCount      = 12;
    private int receivedCount  =  8;
    private int forwardedCount =  4;

    private TextView     tvNetworkStatus;
    private TextView     tvSentCount;
    private TextView     tvReceivedCount;
    private TextView     tvForwardedCount;
    private RecyclerView recyclerViewLogs;
    private Button       btnClearLogs;

    private EventLogAdapter      eventLogAdapter;
    private final List<EventLog> eventLogList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        initViews();
        initRecyclerView();
        populateDummyLogs();
        updateStatistics(sentCount, receivedCount, forwardedCount);
        setupListeners();
    }

    private void initViews() {
        tvNetworkStatus  = findViewById(R.id.tvNetworkStatus);
        tvSentCount      = findViewById(R.id.tvSentCount);
        tvReceivedCount  = findViewById(R.id.tvReceivedCount);
        tvForwardedCount = findViewById(R.id.tvForwardedCount);
        recyclerViewLogs = findViewById(R.id.recyclerViewLogs);
        btnClearLogs     = findViewById(R.id.btnClearLogs);

        View btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> onBackPressed());
        }
    }

    private void initRecyclerView() {
        eventLogAdapter = new EventLogAdapter(eventLogList);
        recyclerViewLogs.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewLogs.setAdapter(eventLogAdapter);
        recyclerViewLogs.setNestedScrollingEnabled(false);
    }

    private void populateDummyLogs() {
        eventLogList.add(new EventLog("Disconnected from Device Gamma",          "08:41 PM"));
        eventLogList.add(new EventLog("Connected to Device Gamma",               "08:41 PM"));
        eventLogList.add(new EventLog("Network stopped",                         "08:36 PM"));
        eventLogList.add(new EventLog("Network stopped",                         "08:36 PM"));
        eventLogList.add(new EventLog("Network started",                         "08:35 PM"));
        eventLogList.add(new EventLog("Network started",                         "10:20 AM"));
        eventLogList.add(new EventLog("Device Beta found",                       "10:21 AM"));
        eventLogList.add(new EventLog("Connection established with Device Beta",  "10:22 AM"));
        eventLogAdapter.notifyDataSetChanged();
    }

    private void setupListeners() {
        if (btnClearLogs != null) {
            btnClearLogs.setOnClickListener(v -> clearLogsAndResetCounters());
        }
    }

    public void updateStatistics(int sent, int received, int forwarded) {
        runOnUiThread(() -> {
            sentCount      = sent;
            receivedCount  = received;
            forwardedCount = forwarded;
            if (tvSentCount != null) tvSentCount.setText(String.valueOf(sentCount));
            if (tvReceivedCount != null) tvReceivedCount.setText(String.valueOf(receivedCount));
            if (tvForwardedCount != null) tvForwardedCount.setText(String.valueOf(forwardedCount));
        });
    }

    public void setNetworkStatus(boolean isOnline) {
        runOnUiThread(() -> {
            if (tvNetworkStatus != null) {
                if (isOnline) {
                    tvNetworkStatus.setText("ON");
                    tvNetworkStatus.setTextColor(0xFF4CAF50);
                } else {
                    tvNetworkStatus.setText("OFF");
                    tvNetworkStatus.setTextColor(0xFF3A3A55);
                }
            }
        });
    }

    public void addEventLog(EventLog log) {
        runOnUiThread(() -> {
            eventLogAdapter.addLog(log);
            recyclerViewLogs.scrollToPosition(0);
        });
    }

    private void clearLogsAndResetCounters() {
        eventLogAdapter.clearLogs();
        sentCount = receivedCount = forwardedCount = 0;
        updateStatistics(0, 0, 0);
    }

    public static class EventLog {
        private final String message;
        private final String timestamp;

        public EventLog(String message, String timestamp) {
            this.message   = message;
            this.timestamp = timestamp;
        }

        public String getMessage()   { return message;   }
        public String getTimestamp() { return timestamp; }
    }

    private static class EventLogAdapter
            extends RecyclerView.Adapter<EventLogAdapter.LogViewHolder> {

        private final List<EventLog> logList;

        EventLogAdapter(List<EventLog> logList) {
            this.logList = logList;
        }

        @Override
        public LogViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            int px16 = dp(parent, 16);
            int px14 = dp(parent, 14);
            int px12 = dp(parent, 12);
            int px8  = dp(parent,  8);

            LinearLayout row = new LinearLayout(parent.getContext());
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setGravity(Gravity.CENTER_VERTICAL);
            row.setPadding(px16, px14, px16, px14);

            android.graphics.drawable.GradientDrawable bg =
                    new android.graphics.drawable.GradientDrawable();
            bg.setColor(0xFF13131C);
            bg.setCornerRadius(dp(parent, 12));
            row.setBackground(bg);

            RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.bottomMargin = px8;
            row.setLayoutParams(lp);

            TextView tvMessage = new TextView(parent.getContext());
            tvMessage.setTextColor(0xFFCCCCDD);
            tvMessage.setTextSize(android.util.TypedValue.COMPLEX_UNIT_SP, 14f);
            LinearLayout.LayoutParams msgLp = new LinearLayout.LayoutParams(
                    0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
            tvMessage.setLayoutParams(msgLp);
            row.addView(tvMessage);

            TextView tvTimestamp = new TextView(parent.getContext());
            tvTimestamp.setTextColor(0xFF44445A);
            tvTimestamp.setTextSize(android.util.TypedValue.COMPLEX_UNIT_SP, 12f);
            LinearLayout.LayoutParams tsLp = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            tsLp.setMarginStart(px12);
            tvTimestamp.setLayoutParams(tsLp);
            row.addView(tvTimestamp);

            return new LogViewHolder(row, tvMessage, tvTimestamp);
        }

        @Override
        public void onBindViewHolder(LogViewHolder holder, int position) {
            EventLog log = logList.get(position);
            holder.tvMessage.setText(log.getMessage());
            holder.tvTimestamp.setText(log.getTimestamp());
        }

        @Override
        public int getItemCount() { return logList.size(); }

        void addLog(EventLog log) {
            logList.add(0, log);
            notifyItemInserted(0);
        }

        void clearLogs() {
            logList.clear();
            notifyDataSetChanged();
        }

        private static int dp(ViewGroup parent, int dp) {
            float density = parent.getContext()
                    .getResources().getDisplayMetrics().density;
            return Math.round(dp * density);
        }

        static class LogViewHolder extends RecyclerView.ViewHolder {
            final TextView tvMessage;
            final TextView tvTimestamp;

            LogViewHolder(View root, TextView tvMessage, TextView tvTimestamp) {
                super(root);
                this.tvMessage   = tvMessage;
                this.tvTimestamp = tvTimestamp;
            }
        }
    }
}
