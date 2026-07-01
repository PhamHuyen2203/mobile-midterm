package com.example.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.mobilecommerce.R;
import com.example.dals.OrderDAO.CustomerSpending;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CustomerSpendingAdapter extends BaseAdapter {
    private Context context;
    private List<CustomerSpending> items = new ArrayList<>();

    public CustomerSpendingAdapter(Context context) {
        this.context = context;
    }

    public void setItems(List<CustomerSpending> items) {
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
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_customer_spending, parent, false);
        }

        CustomerSpending item = items.get(position);

        TextView tvName = convertView.findViewById(R.id.tvCustName);
        TextView tvUser = convertView.findViewById(R.id.tvCustUsername);
        TextView tvEmail = convertView.findViewById(R.id.tvCustEmail);
        TextView tvTotal = convertView.findViewById(R.id.tvCustTotalSpent);

        tvName.setText(item.fullName);
        tvUser.setText("@" + item.username);
        tvEmail.setText(item.email);
        tvTotal.setText(String.format(Locale.getDefault(), "$%,.0f", item.totalSpent));

        return convertView;
    }
}