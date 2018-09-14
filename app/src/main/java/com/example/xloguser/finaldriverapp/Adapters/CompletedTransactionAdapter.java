package com.example.xloguser.finaldriverapp.Adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.xloguser.finaldriverapp.Model.CompletedTransactionModel;
import com.example.xloguser.finaldriverapp.R;

import java.util.ArrayList;

public class CompletedTransactionAdapter extends RecyclerView.Adapter<CompletedTransactionAdapter.MyViewHolder> {
    private ArrayList<CompletedTransactionModel> transactionList;
    @NonNull
    @Override
    public CompletedTransactionAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.completed_transaction_list, parent, false);

        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;

    }

    @Override
    public void onBindViewHolder(@NonNull CompletedTransactionAdapter.MyViewHolder holder, int position) {
        CompletedTransactionModel dash_board = transactionList.get(position);


        holder.textViewTransactionId.setText(String.valueOf(dash_board.getTransactionID()));
        holder.textViewContent.setText(String.valueOf(dash_board.getContentTxt()));

    }

    @Override
    public int getItemCount() {
        return
                transactionList.size();
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView textViewTransactionId;
        TextView textViewContent;

        public MyViewHolder(View itemView) {
            super(itemView);
            this.textViewTransactionId = (TextView) itemView.findViewById(R.id.pendingTransactionTxt);
            this.textViewContent = (TextView) itemView.findViewById(R.id.pendingContentTxt);
        }
    }


    public CompletedTransactionAdapter(ArrayList<CompletedTransactionModel> data) {
        this.transactionList = data;
    }
}
