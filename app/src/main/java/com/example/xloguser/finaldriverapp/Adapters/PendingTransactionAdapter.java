package com.example.xloguser.finaldriverapp.Adapters;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.xloguser.finaldriverapp.Commodity;
import com.example.xloguser.finaldriverapp.Model.PendingTransactionModel;
import com.example.xloguser.finaldriverapp.R;

import java.util.ArrayList;
/**
 * Created by Jaymon Rivera on 09/14/2018.
 */
public class PendingTransactionAdapter extends RecyclerView.Adapter<PendingTransactionAdapter.MyViewHolder> {
    private ArrayList<PendingTransactionModel> transactionList;
    @NonNull
    @Override
    public PendingTransactionAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.pending_transaction_list, parent, false);

        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;

    }

    @Override
    public void onBindViewHolder(@NonNull final PendingTransactionAdapter.MyViewHolder holder, int position) {
        PendingTransactionModel dash_board = transactionList.get(position);


        holder.textViewTransactionId.setText(String.valueOf(dash_board.getTransactionID()));
        holder.textViewContent.setText(String.valueOf(dash_board.getContentTxt()));


        holder.cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("log", "msg:"+ holder.textViewTransactionId.getText());
                Intent intent = new Intent(v.getContext(), Commodity.class);
                v.getContext().startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return
                transactionList.size();
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView textViewTransactionId;
        TextView textViewContent;
        CardView cv;

        public MyViewHolder(View itemView) {
            super(itemView);
            this.textViewTransactionId = (TextView) itemView.findViewById(R.id.pendingTransaction);
            this.textViewContent = (TextView) itemView.findViewById(R.id.pendingContent);
            this.cv = (CardView) itemView.findViewById(R.id.pendingCV);
        }
    }


    public PendingTransactionAdapter(ArrayList<PendingTransactionModel> data) {
        this.transactionList = data;
    }
}
