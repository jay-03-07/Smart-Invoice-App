package com.example.projectapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Map;

public class OrderHistoryAdapter extends RecyclerView.Adapter<OrderHistoryAdapter.ViewHolder>{
    private List<Map<String,Object>> dataList;

    public OrderHistoryAdapter(List<Map<String,Object>> dataList) {
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public OrderHistoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_paid_bill, parent, false);
        return new OrderHistoryAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderHistoryAdapter.ViewHolder holder, int position) {
        Map<String,Object> data = dataList.get(position);

        if (data.get("payment") != null) {
            holder.customerNameTextView.setText("Product Name: "+data.get("productName"));
            holder.phoneNumberTextView.setText("Payment: "+data.get("payment"));
            holder.addressTextView.setText("Rate: "+data.get("rate"));
            holder.paymentStatusTextView.setText("Total Amount: "+data.get("totalAmount"));
            holder.totalAmountTextView.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView customerNameTextView;
        TextView phoneNumberTextView;
        TextView addressTextView;
        TextView paymentStatusTextView;
        TextView totalAmountTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            customerNameTextView = itemView.findViewById(R.id.textViewCustomerNameValue);
            phoneNumberTextView = itemView.findViewById(R.id.textViewCustomerPhoneNumberValue);
            addressTextView = itemView.findViewById(R.id.textViewCustomerAddressValue);
            paymentStatusTextView= itemView.findViewById(R.id.textViewOrderPaymentValue);
            totalAmountTextView = itemView.findViewById(R.id.textViewOrderTotalAmountValue);
        }
    }
}
