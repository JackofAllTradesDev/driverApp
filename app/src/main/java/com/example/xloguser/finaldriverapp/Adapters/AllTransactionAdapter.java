package com.example.xloguser.finaldriverapp.Adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.xloguser.finaldriverapp.Model.AllTransactionModel;
import com.example.xloguser.finaldriverapp.Model.DashboardTransactionsModel;
import com.example.xloguser.finaldriverapp.R;

import java.util.ArrayList;

public class AllTransactionAdapter extends RecyclerView.Adapter<AllTransactionAdapter.MyViewHolder> {
    private ArrayList<AllTransactionModel> transactionList;
    @NonNull
    @Override
    public AllTransactionAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.all_transaction_list, parent, false);

        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;

    }

    @Override
    public void onBindViewHolder(@NonNull AllTransactionAdapter.MyViewHolder holder, int position) {
        AllTransactionModel dash_board = transactionList.get(position);


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
            this.textViewTransactionId = (TextView) itemView.findViewById(R.id.allTransactionTxt);
            this.textViewContent = (TextView) itemView.findViewById(R.id.allContentTxt);
        }
    }


    public AllTransactionAdapter(ArrayList<AllTransactionModel> data) {
        this.transactionList = data;
    }
}
