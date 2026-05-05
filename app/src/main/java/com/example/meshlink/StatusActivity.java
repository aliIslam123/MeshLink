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

public class StatusActivity extends AppCompatActivity implements MeshManager.MeshListener {

    private TextView     tvNetworkStatus;
    private TextView     tvSentCount;
    private TextView     tvReceivedCount;
    private TextView     tvForwardedCount;
    private RecyclerView recyclerViewLogs;
    private Button       btnClearLogs;

    private EventLogAdapter      eventLogAdapter;
    private final List<LogModel> eventLogList = new ArrayList<>();
    private MeshManager meshManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        meshManager = MeshManager.getInstance(this);
        meshManager.setListener(this);

        initViews();
        initRecyclerView();
        
        loadInitialData();
        setupListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        meshManager.setListener(this);
        loadInitialData();
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
            btnBack.setOnClickListener(v -> finish());
        }
    }

    private void initRecyclerView() {
        eventLogAdapter = new EventLogAdapter(eventLogList);
        recyclerViewLogs.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewLogs.setAdapter(eventLogAdapter);
        recyclerViewLogs.setNestedScrollingEnabled(false);
    }

    private void loadInitialData() {
        eventLogList.clear();
        eventLogList.addAll(meshManager.getLogs());
        eventLogAdapter.notifyDataSetChanged();
        
        updateStatistics(meshManager.getSentCount(), meshManager.getReceivedCount(), meshManager.getForwardedCount());
        setNetworkStatus(!meshManager.getConnectedPeers().isEmpty() || !meshManager.getDiscoveredPeers().isEmpty());
    }

    private void setupListeners() {
        if (btnClearLogs != null) {
            btnClearLogs.setOnClickListener(v -> {
                meshManager.getLogs().clear();
                eventLogList.clear();
                eventLogAdapter.notifyDataSetChanged();
            });
        }
    }

    public void updateStatistics(int sent, int received, int forwarded) {
        runOnUiThread(() -> {
            if (tvSentCount != null) tvSentCount.setText(String.valueOf(sent));
            if (tvReceivedCount != null) tvReceivedCount.setText(String.valueOf(received));
            if (tvForwardedCount != null) tvForwardedCount.setText(String.valueOf(forwarded));
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

    @Override
    public void onLogUpdated(List<LogModel> logs) {
        runOnUiThread(() -> {
            eventLogList.clear();
            eventLogList.addAll(logs);
            eventLogAdapter.notifyDataSetChanged();
            recyclerViewLogs.scrollToPosition(0);
        });
    }

    @Override
    public void onCountersUpdated(int sent, int received, int forwarded) {
        updateStatistics(sent, received, forwarded);
    }

    @Override public void onPeerDiscovered(List<PeerModel> peers) { setNetworkStatus(true); }
    @Override public void onPeerConnected(PeerModel peer) { setNetworkStatus(true); }
    @Override public void onPeerDisconnected(PeerModel peer) { setNetworkStatus(!meshManager.getConnectedPeers().isEmpty()); }
    @Override public void onMessageReceived(MessageModel message) {}
    @Override public void onStatusChanged(boolean isRunning) { setNetworkStatus(isRunning); }

    private static class EventLogAdapter extends RecyclerView.Adapter<EventLogAdapter.LogViewHolder> {
        private final List<LogModel> logList;

        EventLogAdapter(List<LogModel> logList) {
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

            android.graphics.drawable.GradientDrawable bg = new android.graphics.drawable.GradientDrawable();
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
            LinearLayout.LayoutParams msgLp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
            tvMessage.setLayoutParams(msgLp);
            row.addView(tvMessage);

            TextView tvTimestamp = new TextView(parent.getContext());
            tvTimestamp.setTextColor(0xFF44445A);
            tvTimestamp.setTextSize(android.util.TypedValue.COMPLEX_UNIT_SP, 12f);
            LinearLayout.LayoutParams tsLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            tsLp.setMarginStart(px12);
            tvTimestamp.setLayoutParams(tsLp);
            row.addView(tvTimestamp);

            return new LogViewHolder(row, tvMessage, tvTimestamp);
        }

        @Override
        public void onBindViewHolder(LogViewHolder holder, int position) {
            LogModel log = logList.get(logList.size() - 1 - position);
            holder.tvMessage.setText(log.getMessage());
            holder.tvTimestamp.setText(log.getFormattedTime());
        }

        @Override
        public int getItemCount() { return logList.size(); }

        private static int dp(ViewGroup parent, int dp) {
            float density = parent.getContext().getResources().getDisplayMetrics().density;
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
