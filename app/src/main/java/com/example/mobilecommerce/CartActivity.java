package com.example.mobilecommerce;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.adapters.CartAdapter;
import com.example.dals.CartDAO;
import com.example.dals.CartDAO.CartItem;
import com.example.utils.SessionManager;

import java.util.List;

public class CartActivity extends AppCompatActivity implements CartAdapter.CartItemListener {

    private ListView lvCartItems;
    private TextView tvTotalAmount;
    private Button btnCheckout;
    private CartAdapter adapter;
    private CartDAO cartDAO;
    private SessionManager sessionManager;
    private int userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        sessionManager = new SessionManager(this);
        if (!sessionManager.isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        userID = sessionManager.getUserID();
        cartDAO = new CartDAO(this);
        
        initViews();
        loadCartData();
    }

    private void initViews() {
        lvCartItems = findViewById(R.id.lvCartItems);
        tvTotalAmount = findViewById(R.id.tvTotalAmount);
        btnCheckout = findViewById(R.id.btnCheckout);

        adapter = new CartAdapter(this, this);
        lvCartItems.setAdapter(adapter);

        btnCheckout.setOnClickListener(v -> {
            performCheckout();
        });
    }

    private void performCheckout() {
        if (adapter.getCount() == 0) {
            Toast.makeText(this, R.string.cart_empty, Toast.LENGTH_SHORT).show();
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle(R.string.confirm_checkout_title)
                .setMessage(R.string.confirm_checkout_message)
                .setPositiveButton(R.string.action_ok, (dialog, which) -> {
                    if (cartDAO.checkout(userID)) {
                        Toast.makeText(this, R.string.checkout_success, Toast.LENGTH_LONG).show();
                        loadCartData(); // Tải lại để thấy giỏ hàng trống
                    } else {
                        Toast.makeText(this, R.string.checkout_error, Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton(R.string.action_cancel, null)
                .show();
    }

    private void loadCartData() {
        List<CartItem> items = cartDAO.getCartByUserID(userID);
        adapter.setCartItems(items);
        updateTotal(items);
    }

    private void updateTotal(List<CartItem> items) {
        double total = 0;
        for (CartItem item : items) {
            total += item.getPrice() * item.getQuantity();
        }
        tvTotalAmount.setText(String.format("$%.0f", total));
    }

    @Override
    public void onIncrease(CartItem item) {
        cartDAO.updateQuantity(item.getCartID(), item.getQuantity() + 1);
        loadCartData();
    }

    @Override
    public void onDecrease(CartItem item) {
        if (item.getQuantity() > 1) {
            cartDAO.updateQuantity(item.getCartID(), item.getQuantity() - 1);
            loadCartData();
        } else {
            onDelete(item);
        }
    }

    @Override
    public void onDelete(CartItem item) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.confirm_delete_title)
                .setMessage(R.string.confirm_delete_message)
                .setPositiveButton(R.string.action_yes, (dialog, which) -> {
                    cartDAO.deleteCartItem(item.getCartID());
                    loadCartData();
                })
                .setNegativeButton(R.string.action_no, null)
                .show();
    }
}