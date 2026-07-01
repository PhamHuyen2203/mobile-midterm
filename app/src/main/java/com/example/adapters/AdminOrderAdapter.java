package com.example.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.mobilecommerce.R;
import com.example.dals.OrderDAO.AdminOrder;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AdminOrderAdapter extends BaseAdapter {
    private Context context;
    private List<AdminOrder> orders = new ArrayList<>();

    public AdminOrderAdapter(Context context) {
        this.context = context;
    }

    public void setOrders(List<AdminOrder> orders) {
        this.orders = orders;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return orders.size();
    }

    @Override
    public Object getItem(int position) {
        return orders.get(position);
    }

    @Override
    public long getItemId(int position) {
        return orders.get(position).orderID;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_admin_order, parent, false);
        }

        AdminOrder order = orders.get(position);

        TextView tvId = convertView.findViewById(R.id.tvAdminOrderId);
        TextView tvStatus = convertView.findViewById(R.id.tvAdminOrderStatus);
        TextView tvName = convertView.findViewById(R.id.tvAdminCustomerName);
        TextView tvDate = convertView.findViewById(R.id.tvAdminOrderDate);
        TextView tvTotal = convertView.findViewById(R.id.tvAdminOrderTotal);

        tvId.setText("#" + order.orderID);
        tvStatus.setText(order.status);
        tvName.setText(order.customerName);
        tvDate.setText(order.orderDate);
        tvTotal.setText(String.format(Locale.getDefault(), "$%,.0f", order.totalAmount));

        return convertView;
    }
}