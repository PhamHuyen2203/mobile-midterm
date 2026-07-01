package com.example.mobilecommerce;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.dals.OrderDAO;

import java.util.List;
import java.util.Locale;

public class RevenueReportActivity extends AppCompatActivity {

    private TableLayout tlRevenueReport;
    private OrderDAO orderDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_revenue_report);

        orderDAO = new OrderDAO(this);
        tlRevenueReport = findViewById(R.id.tlRevenueReport);

        loadReportData();
    }

    private void loadReportData() {
        List<OrderDAO.CategoryRevenue> data = orderDAO.getRevenueByCategory();

        for (OrderDAO.CategoryRevenue item : data) {
            TableRow row = new TableRow(this);
            row.setPadding(12, 16, 12, 16);

            TextView tvName = new TextView(this);
            tvName.setText(item.categoryName);
            tvName.setTextColor(Color.parseColor("#101828"));

            TextView tvQty = new TextView(this);
            tvQty.setText(String.valueOf(item.totalQuantity));
            tvQty.setGravity(Gravity.CENTER);
            tvQty.setTextColor(Color.parseColor("#475467"));

            TextView tvRevenue = new TextView(this);
            tvRevenue.setText(String.format(Locale.getDefault(), "$%,.0f", item.totalRevenue));
            tvRevenue.setGravity(Gravity.END);
            tvRevenue.setTextColor(Color.parseColor("#D92D20"));
            tvRevenue.setTypeface(null, android.graphics.Typeface.BOLD);

            row.addView(tvName);
            row.addView(tvQty);
            row.addView(tvRevenue);

            tlRevenueReport.addView(row);

            // Add separator line
            View separator = new View(this);
            separator.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, 1));
            separator.setBackgroundColor(Color.parseColor("#D0D5DD"));
            tlRevenueReport.addView(separator);
        }
    }
    
    // Simple helper View class for separator since I can't import android.view.View easily in a single write
    private static class View extends android.view.View {
        public View(android.content.Context context) {
            super(context);
        }
    }
}