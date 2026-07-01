package com.example.mobilecommerce;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.dals.OrderDAO;
import com.example.utils.SessionManager;

import java.util.Locale;

public class ProfileActivity extends AppCompatActivity {

    private TextView tvFullName, tvEmail, tvTotalOrders, tvProcessingOrders, tvTotalSpent;
    private SessionManager sessionManager;
    private OrderDAO orderDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        sessionManager = new SessionManager(this);
        orderDAO = new OrderDAO(this);

        initViews();
        loadProfileData();
    }

    private void initViews() {
        tvFullName = findViewById(R.id.tvProfileFullName);
        tvEmail = findViewById(R.id.tvProfileEmail);
        tvTotalOrders = findViewById(R.id.tvTotalOrders);
        tvProcessingOrders = findViewById(R.id.tvProcessingOrders);
        tvTotalSpent = findViewById(R.id.tvTotalSpent);
    }

    private void loadProfileData() {
        if (!sessionManager.isLoggedIn()) {
            finish();
            return;
        }

        int userID = sessionManager.getUserID();
        tvFullName.setText(sessionManager.getFullName());
        tvEmail.setText(sessionManager.getEmail());

        OrderDAO.OrderStats stats = orderDAO.getOrderStats(userID);

        tvTotalOrders.setText(String.valueOf(stats.totalOrders));
        tvProcessingOrders.setText(String.valueOf(stats.processingOrders));
        tvTotalSpent.setText(String.format(Locale.getDefault(), "$%,.0f", stats.totalSpentPaid));
    }
}