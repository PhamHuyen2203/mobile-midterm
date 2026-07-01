package com.example.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mobilecommerce.R;
import com.example.dals.CartDAO.CartItem;

import java.util.ArrayList;
import java.util.List;

public class CartAdapter extends BaseAdapter {
    private Context context;
    private List<CartItem> cartItems = new ArrayList<>();
    private CartItemListener listener;

    public interface CartItemListener {
        void onIncrease(CartItem item);
        void onDecrease(CartItem item);
        void onDelete(CartItem item);
    }

    public CartAdapter(Context context, CartItemListener listener) {
        this.context = context;
        this.listener = listener;
    }

    public void setCartItems(List<CartItem> items) {
        this.cartItems = items;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return cartItems.size();
    }

    @Override
    public Object getItem(int position) {
        return cartItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return cartItems.get(position).getCartID();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_cart, parent, false);
        }

        CartItem item = cartItems.get(position);

        TextView tvName = convertView.findViewById(R.id.tvProductNameCart);
        TextView tvPrice = convertView.findViewById(R.id.tvProductPriceCart);
        TextView tvQuantity = convertView.findViewById(R.id.tvQuantity);
        ImageView ivProduct = convertView.findViewById(R.id.ivProductCart);
        Button btnIncrease = convertView.findViewById(R.id.btnIncrease);
        Button btnDecrease = convertView.findViewById(R.id.btnDecrease);
        ImageButton btnDelete = convertView.findViewById(R.id.btnDelete);

        tvName.setText(item.getProductName());
        tvPrice.setText(String.format("$%.0f", item.getPrice()));
        tvQuantity.setText(String.valueOf(item.getQuantity()));

        // Placeholder for image loading (if using Glide or Picasso, add here)
        // ivProduct.setImageResource(R.mipmap.ic_launcher);

        btnIncrease.setOnClickListener(v -> listener.onIncrease(item));
        btnDecrease.setOnClickListener(v -> listener.onDecrease(item));
        btnDelete.setOnClickListener(v -> listener.onDelete(item));

        return convertView;
    }
}