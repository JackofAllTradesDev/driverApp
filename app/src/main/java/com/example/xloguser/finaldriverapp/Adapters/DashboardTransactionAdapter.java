package com.example.xloguser.finaldriverapp.Adapters;

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

import com.example.xloguser.finaldriverapp.Commodity;
import com.example.xloguser.finaldriverapp.MainActivity;
import com.example.xloguser.finaldriverapp.Model.DashboardTransactionsModel;
import com.example.xloguser.finaldriverapp.NavigationDrawer;
import com.example.xloguser.finaldriverapp.R;

import java.util.ArrayList;

/**
 * Created by Jaymon Rivera on 09/14/2018.
 */

public class DashboardTransactionAdapter extends RecyclerView.Adapter<DashboardTransactionAdapter.MyViewHolder> {
    private ArrayList<DashboardTransactionsModel> transactionList;
    private Context context;

    public DashboardTransactionAdapter(Context context) {
        this.context = context;
    }
    @NonNull
    @Override
    public DashboardTransactionAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.transaction_list, parent, false);

        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;

    }

    @Override
    public void onBindViewHolder(@NonNull final DashboardTransactionAdapter.MyViewHolder holder, int position) {
        DashboardTransactionsModel dash_board = transactionList.get(position);


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
            this.textViewTransactionId = (TextView) itemView.findViewById(R.id.pendingTransactionTxt);
            this.textViewContent = (TextView) itemView.findViewById(R.id.pendingContentTxt);
            this.cv = (CardView) itemView.findViewById(R.id.cardViewDashboard);


        }
    }


    public DashboardTransactionAdapter(ArrayList<DashboardTransactionsModel> data) {
        this.transactionList = data;
    }
}
