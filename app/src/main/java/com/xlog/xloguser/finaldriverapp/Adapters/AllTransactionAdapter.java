package com.xlog.xloguser.finaldriverapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xlog.xloguser.finaldriverapp.Commodity;
import com.xlog.xloguser.finaldriverapp.Model.AllTransactionModel;
import com.xlog.xloguser.finaldriverapp.Model.DashboardTransactionsModel;
import com.xlog.xloguser.finaldriverapp.R;

import java.util.ArrayList;
/**
 * Created by Jaymon Rivera on 09/14/2018.
 */
public class AllTransactionAdapter extends RecyclerView.Adapter<AllTransactionAdapter.MyViewHolder> {
    private ArrayList<AllTransactionModel> transactionList;
    private Context context;
    @NonNull
    @Override
    public AllTransactionAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.all_transaction_list, parent, false);

        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;

    }

    @Override
    public void onBindViewHolder(@NonNull final AllTransactionAdapter.MyViewHolder holder, int position) {
        AllTransactionModel dash_board = transactionList.get(position);


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
            this.textViewTransactionId = (TextView) itemView.findViewById(R.id.allTransactionTxt);
            this.textViewContent = (TextView) itemView.findViewById(R.id.allContentTxt);
            this.cv = (CardView) itemView.findViewById(R.id.cvAllList);
        }
    }


    public AllTransactionAdapter(ArrayList<AllTransactionModel> data) {
        this.transactionList = data;
    }
}
