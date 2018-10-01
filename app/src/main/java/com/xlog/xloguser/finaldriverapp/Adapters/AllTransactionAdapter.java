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
import com.xlog.xloguser.finaldriverapp.TrasactionView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jaymon Rivera on 09/14/2018.
 */
public class AllTransactionAdapter extends RecyclerView.Adapter<AllTransactionAdapter.MyViewHolder> {
    public AllTransactionAdapter(List<String> trNumber) {
        this.trNumber = trNumber;
    }

    List<String> trNumber;

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView textViewTransactionId;
        CardView cv;

        public MyViewHolder(View itemView) {
            super(itemView);
            this.textViewTransactionId = (TextView) itemView.findViewById(R.id.allTransactionTxt);
            this.cv = (CardView) itemView.findViewById(R.id.cvAllList);
        }
    }

    @NonNull
    @Override
    public AllTransactionAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.all_transaction_list, parent, false);
        return new AllTransactionAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final AllTransactionAdapter.MyViewHolder holder, int position) {
        holder.textViewTransactionId.setText(trNumber.get(position));
        holder.cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), TrasactionView.class);
                intent.putExtra("tr_number", holder.textViewTransactionId.getText());
                v.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return trNumber.size();
    }
}
