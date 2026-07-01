package com.example.mobilecommerce;

import android.os.Bundle;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.adapters.CustomerSpendingAdapter;
import com.example.dals.OrderDAO;

import java.util.List;

public class CustomerSpendingActivity extends AppCompatActivity {

    private OrderDAO orderDAO;
    private CustomerSpendingAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_spending);

        orderDAO = new OrderDAO(this);
        ListView lvCustomerSpending = findViewById(R.id.lvCustomerSpending);
        
        adapter = new CustomerSpendingAdapter(this);
        lvCustomerSpending.setAdapter(adapter);

        loadData();
    }

    private void loadData() {
        List<OrderDAO.CustomerSpending> reportData = orderDAO.getCustomerSpendingReport();
        adapter.setItems(reportData);
    }
}