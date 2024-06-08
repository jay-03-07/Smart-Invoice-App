package com.example.projectapp;

// ProductAdapter.java
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class ProductAdapter extends BaseAdapter {

    private Context context;
    private List<Product> productList;

    public ProductAdapter(Context context, List<Product> productList) {
        this.context = context;
        this.productList = productList;
    }

    @Override
    public int getCount() {
        return productList.size();
    }

    @Override
    public Object getItem(int position) {
        return productList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_product, parent, false);
        }

        TextView textViewSrNo = convertView.findViewById(R.id.textViewSrNo);
        TextView textViewProductName = convertView.findViewById(R.id.textViewProductName);
        TextView textViewRate = convertView.findViewById(R.id.textViewRate);

        Product product = productList.get(position);

        textViewSrNo.setText(String.valueOf(position + 1));
        textViewProductName.setText(product.getProductName());
        textViewRate.setText(String.valueOf(product.getRate()));

        return convertView;
    }
}