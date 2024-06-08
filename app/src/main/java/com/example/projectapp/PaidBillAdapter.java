package com.example.projectapp;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PaidBillAdapter extends RecyclerView.Adapter<PaidBillAdapter.ViewHolder> {

    private List<Customer> customerList;
    private OnItemClickListener onItemClickListener;

    public PaidBillAdapter(List<Customer> customerList) {
        this.customerList = customerList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_paid_bill, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Customer customer = customerList.get(position);

        if (customer != null) {
            holder.customerNameTextView.setText("Customer Name: "+customer.getCustomerName());
            holder.phoneNumberTextView.setText("Phone Number: "+customer.getPhoneNumber());
            holder.addressTextView.setText("Address: "+customer.getAddress());
            holder.paymentStatusTextView.setVisibility(View.GONE);
            holder.totalAmountTextView.setVisibility(View.GONE);
        }

        holder.itemCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    int position = holder.getAdapterPosition();
                    onItemClickListener.onItemClickListener(customerList.get(position));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return customerList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CardView itemCard;
        TextView customerNameTextView;
        TextView phoneNumberTextView;
        TextView addressTextView;
        TextView paymentStatusTextView;
        TextView totalAmountTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemCard = itemView.findViewById(R.id.itemCard);
            customerNameTextView = itemView.findViewById(R.id.textViewCustomerNameValue);
            phoneNumberTextView = itemView.findViewById(R.id.textViewCustomerPhoneNumberValue);
            addressTextView = itemView.findViewById(R.id.textViewCustomerAddressValue);
            paymentStatusTextView= itemView.findViewById(R.id.textViewOrderPaymentValue);
            totalAmountTextView = itemView.findViewById(R.id.textViewOrderTotalAmountValue);
        }
    }

    public interface OnItemClickListener {
        void onItemClickListener(Customer customer);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
}
