package com.xlog.xloguser.finaldriverapp.Adapters;

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
import com.xlog.xloguser.finaldriverapp.Model.PendingTransactionModel;
import com.xlog.xloguser.finaldriverapp.R;
import com.xlog.xloguser.finaldriverapp.TrasactionView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jaymon Rivera on 09/14/2018.
 */
public class PendingTransactionAdapter extends RecyclerView.Adapter<PendingTransactionAdapter.MyViewHolder> {
    public PendingTransactionAdapter(List<String> trNumber) {
        this.trNumberPending = trNumber;
    }

    List<String> trNumberPending;

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView textViewTransactionId;
        CardView cv;

        public MyViewHolder(View itemView) {
            super(itemView);
            this.textViewTransactionId = (TextView) itemView.findViewById(R.id.pendingTxt);
            this.cv = (CardView) itemView.findViewById(R.id.pendingCV);
        }
    }

    @NonNull
    @Override
    public PendingTransactionAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.pending_transaction_list, parent, false);
        return new PendingTransactionAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final PendingTransactionAdapter.MyViewHolder holder, int position) {
        holder.textViewTransactionId.setText(trNumberPending.get(position));
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
        return trNumberPending.size();
    }
}
