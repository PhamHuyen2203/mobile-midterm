package com.example.mobilecommerce;

import android.os.Bundle;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.adapters.TopProductAdapter;
import com.example.dals.ProductDAO;

import java.util.List;

public class TopSellingActivity extends AppCompatActivity {

    private ListView lvTopSelling;
    private TopProductAdapter adapter;
    private ProductDAO productDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_selling);

        productDAO = new ProductDAO(this);
        lvTopSelling = findViewById(R.id.lvTopSelling);
        adapter = new TopProductAdapter(this);
        lvTopSelling.setAdapter(adapter);

        loadTopProducts();
    }

    private void loadTopProducts() {
        // Lấy Top 10 sản phẩm bán chạy nhất
        List<ProductDAO.TopProduct> topList = productDAO.getTopSellingProducts(10);
        adapter.setItems(topList);
    }
}