package com.example.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.mobilecommerce.R;
import com.example.dals.ProductDAO.TopProduct;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TopProductAdapter extends BaseAdapter {
    private Context context;
    private List<TopProduct> items = new ArrayList<>();

    public TopProductAdapter(Context context) {
        this.context = context;
    }

    public void setItems(List<TopProduct> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return items.get(position).productID;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_top_product, parent, false);
        }

        TopProduct item = items.get(position);

        TextView tvName = convertView.findViewById(R.id.tvTopProductName);
        TextView tvSold = convertView.findViewById(R.id.tvTopProductSold);
        TextView tvPrice = convertView.findViewById(R.id.tvTopProductPrice);
        ImageView ivProduct = convertView.findViewById(R.id.ivTopProduct);

        tvName.setText(item.productName);
        tvSold.setText(context.getString(R.string.label_sold_count, item.totalSold));
        tvPrice.setText(String.format(Locale.getDefault(), "$%,.0f", item.price));

        Glide.with(context)
                .load(item.imageURL)
                .placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.ic_launcher)
                .into(ivProduct);

        return convertView;
    }
}