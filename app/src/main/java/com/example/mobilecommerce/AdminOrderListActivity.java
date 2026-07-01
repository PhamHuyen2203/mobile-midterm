package com.example.mobilecommerce;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.adapters.AdminOrderAdapter;
import com.example.dals.OrderDAO;
import com.example.dals.OrderDAO.AdminOrder;

import java.util.List;

public class AdminOrderListActivity extends AppCompatActivity {

    private ListView lvOrders;
    private TextView tvPageNumber;
    private Button btnPrev, btnNext;
    private AdminOrderAdapter adapter;
    private OrderDAO orderDAO;

    private int currentPage = 0;
    private static final int PAGE_SIZE = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_orders);

        orderDAO = new OrderDAO(this);
        initViews();
        loadOrders();
    }

    private void initViews() {
        lvOrders = findViewById(R.id.lvAdminOrders);
        tvPageNumber = findViewById(R.id.tvPageNumber);
        btnPrev = findViewById(R.id.btnPrevPage);
        btnNext = findViewById(R.id.btnNextPage);

        adapter = new AdminOrderAdapter(this);
        lvOrders.setAdapter(adapter);

        btnPrev.setOnClickListener(v -> {
            if (currentPage > 0) {
                currentPage--;
                loadOrders();
            }
        });

        btnNext.setOnClickListener(v -> {
            currentPage++;
            loadOrders();
        });
    }

    private void loadOrders() {
        int offset = currentPage * PAGE_SIZE;
        List<AdminOrder> orders = orderDAO.getAllOrdersPaginated(PAGE_SIZE, offset);
        
        adapter.setOrders(orders);
        tvPageNumber.setText(getString(R.string.page_format, currentPage + 1));
        
        btnPrev.setEnabled(currentPage > 0);
        // Disable next button if fewer than PAGE_SIZE results (simple way to check if it's the last page)
        btnNext.setEnabled(orders.size() == PAGE_SIZE);
    }
}