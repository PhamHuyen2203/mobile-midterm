package com.example.adapters;

import android.content.Context;
import android.graphics.Paint;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.mobilecommerce.R;
import com.example.models.Product;

import java.util.ArrayList;
import java.util.List;

public class ProductAdapter extends BaseAdapter {

    public interface OnAddToCartListener {

        void onAddToCart(Product product);
    }

    private final Context context;
    private final LayoutInflater inflater;
    private final OnAddToCartListener listener;

    private final List<Product> products =
            new ArrayList<>();

    public ProductAdapter(
            Context context,
            OnAddToCartListener listener
    ) {
        this.context = context;
        this.listener = listener;

        inflater =
                LayoutInflater.from(context);
    }

    public void setProducts(
            List<Product> newProducts
    ) {
        products.clear();

        if (newProducts != null) {
            products.addAll(newProducts);
        }

        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return products.size();
    }

    @Override
    public Product getItem(int position) {
        return products.get(position);
    }

    @Override
    public long getItemId(int position) {
        return getItem(position)
                .getProductID();
    }

    @Override
    public View getView(
            int position,
            View convertView,
            ViewGroup parent
    ) {
        ViewHolder holder;

        if (convertView == null) {
            convertView =
                    inflater.inflate(
                            R.layout.item_product,
                            parent,
                            false
                    );

            holder = new ViewHolder();

            holder.tvProductName =
                    convertView.findViewById(
                            R.id.tvProductName
                    );

            holder.tvCategoryName =
                    convertView.findViewById(
                            R.id.tvCategoryName
                    );

            holder.tvDescription =
                    convertView.findViewById(
                            R.id.tvDescription
                    );

            holder.tvOriginalPrice =
                    convertView.findViewById(
                            R.id.tvOriginalPrice
                    );

            holder.tvPromotionalPrice =
                    convertView.findViewById(
                            R.id.tvPromotionalPrice
                    );

            holder.tvRating =
                    convertView.findViewById(
                            R.id.tvRating
                    );

            holder.btnAddToCart =
                    convertView.findViewById(
                            R.id.btnAddToCart
                    );

            convertView.setTag(holder);

        } else {
            holder =
                    (ViewHolder) convertView.getTag();
        }

        Product product =
                getItem(position);

        holder.tvProductName.setText(
                product.getProductName()
        );

        holder.tvCategoryName.setText(
                product.getCategoryName()
        );

        String description =
                product.getDescription();

        if (TextUtils.isEmpty(description)) {
            holder.tvDescription.setText(
                    R.string.no_product_description
            );
        } else {
            holder.tvDescription.setText(
                    description
            );
        }

        holder.tvOriginalPrice.setText(
                context.getString(
                        R.string.format_price,
                        product.getOriginalPrice()
                )
        );

        holder.tvOriginalPrice.setPaintFlags(
                holder.tvOriginalPrice.getPaintFlags()
                        | Paint.STRIKE_THRU_TEXT_FLAG
        );

        holder.tvPromotionalPrice.setText(
                context.getString(
                        R.string.format_price,
                        product.getPromotionalPrice()
                )
        );

        holder.tvRating.setText(
                context.getString(
                        R.string.format_rating,
                        product.getRating()
                )
        );

        holder.btnAddToCart.setOnClickListener(
                view -> {
                    if (listener != null) {
                        listener.onAddToCart(
                                product
                        );
                    }
                }
        );

        return convertView;
    }

    private static class ViewHolder {

        TextView tvProductName;
        TextView tvCategoryName;
        TextView tvDescription;
        TextView tvOriginalPrice;
        TextView tvPromotionalPrice;
        TextView tvRating;
        Button btnAddToCart;
    }
}