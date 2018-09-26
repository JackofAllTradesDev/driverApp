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
import com.xlog.xloguser.finaldriverapp.Model.DashboardList;
import com.xlog.xloguser.finaldriverapp.Model.DashboardTransactionsModel;
import com.xlog.xloguser.finaldriverapp.R;
import com.xlog.xloguser.finaldriverapp.TrasactionView;

import java.util.ArrayList;
import java.util.List;

public class DashboadAdapter extends RecyclerView.Adapter<DashboadAdapter.ViewHolder> {
    List<String> trNumber;

    public DashboadAdapter(List<String> trNumber) {
        this.trNumber = trNumber;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView textViewTransactionId;
        CardView cv;

        public ViewHolder(View itemView) {
            super(itemView);
            this.textViewTransactionId = (TextView) itemView.findViewById(R.id.allTransaction);
            this.cv = (CardView) itemView.findViewById(R.id.cardViewDashboard);
        }
    }


    @NonNull
    @Override
    public DashboadAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.transaction_list, parent, false);
        return new DashboadAdapter.ViewHolder(view);


    }

    @Override
    public void onBindViewHolder(@NonNull final DashboadAdapter.ViewHolder holder, int position) {

            holder.textViewTransactionId.setText(trNumber.get(position));
            holder.cv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e("TAG", "MESSAGE: "+ holder.textViewTransactionId.getText());
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
