package com.xlog.xloguser.finaldriverapp.Adapters;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.xlog.xloguser.finaldriverapp.Commodity;
import com.xlog.xloguser.finaldriverapp.Model.ModelReservationList.ReservationList;
import com.xlog.xloguser.finaldriverapp.Model.PendingTransactionModel;
import com.xlog.xloguser.finaldriverapp.R;
import com.xlog.xloguser.finaldriverapp.TrasactionView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jaymon Rivera on 09/14/2018.
 */
public class PendingTransactionAdapter extends RecyclerView.Adapter<PendingTransactionAdapter.MyViewHolder>implements Filterable {


    List<ReservationList> trNumberPending;
    List<ReservationList> trNumberPendingFiltered;

    public PendingTransactionAdapter(List<ReservationList> trNumberPending) {
        this.trNumberPending = trNumberPending;
        trNumberPendingFiltered = new ArrayList<>(trNumberPending);
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    private Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<ReservationList> filteredList = new ArrayList<>();

            if(constraint == null || constraint.length() == 0){
                filteredList.addAll(trNumberPendingFiltered);

            }else{
                String filterPattern = constraint.toString().toLowerCase().trim();

                for(ReservationList item : trNumberPendingFiltered){
                    if(item.getPrefixedId().toLowerCase().contains(filterPattern)){
                        filteredList.add(item);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            trNumberPending.clear();
            trNumberPending.addAll((List)results.values);
            notifyDataSetChanged();

        }
    };

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView textViewTransactionId;
        public TextView shipperTxt;
        public TextView consigneeTxt;
        public TextView commodityTxt;
        public TextView deliveryDate;
        CardView cv;

        public MyViewHolder(View itemView) {
            super(itemView);
            this.textViewTransactionId = (TextView) itemView.findViewById(R.id.pendingTransactions);
            this.shipperTxt = (TextView) itemView.findViewById(R.id.pendingShipper);
            this.consigneeTxt = (TextView) itemView.findViewById(R.id.consigneePendingTxt);
            this.commodityTxt = (TextView) itemView.findViewById(R.id.commodityTxtPending);
            this.deliveryDate = (TextView) itemView.findViewById(R.id.deliveryDateTxtPending);
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
        ReservationList reservationLists = trNumberPending.get(position);

        holder.textViewTransactionId.setText(reservationLists.getPrefixedId());
        holder.shipperTxt.setText(reservationLists.getReservation().getShipper().getName());
        holder.consigneeTxt.setText(reservationLists.getReservation().getConsignee().getName());
        holder.commodityTxt.setText(reservationLists.getReservation().getCommodity().getTranslation().getName());
        holder.deliveryDate.setText(reservationLists.getDeliveryDates().get(0).getDeliveryAt());
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
