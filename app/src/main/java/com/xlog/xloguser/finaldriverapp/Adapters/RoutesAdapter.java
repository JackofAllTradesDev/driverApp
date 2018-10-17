package com.xlog.xloguser.finaldriverapp.Adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xlog.xloguser.finaldriverapp.Model.ModelReservationList.ReservationList;
import com.xlog.xloguser.finaldriverapp.Model.ModelReservationList.Route;
import com.xlog.xloguser.finaldriverapp.R;

import java.util.Arrays;
import java.util.List;

public class RoutesAdapter extends RecyclerView.Adapter<RoutesAdapter.MyViewHolder> {

    public RoutesAdapter(List<Route> routes) {
        this.routes = routes;
    }

    List<Route> routes;



    public static class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView route;
        public TextView addressTxt;
        CardView cv;

        public MyViewHolder(View itemView) {
            super(itemView);
            this.route = (TextView) itemView.findViewById(R.id.routeTxt);
            this.addressTxt = (TextView) itemView.findViewById(R.id.addressTxt);
            this.cv = (CardView) itemView.findViewById(R.id.routeCv);
        }
    }
    @NonNull
    @Override
    public RoutesAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.route_list, parent, false);
        return new RoutesAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final RoutesAdapter.MyViewHolder holder, int position) {
        Route reservationLists = routes.get(position);

        char[] alphabet = {'a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z'};

                holder.addressTxt.setText(reservationLists.getFormattedAddress());
                Log.e("TAGGG", "TAGGG+++ "+reservationLists.getName());

            holder.route.setText("Route "+String.valueOf(alphabet[position]));
    }

    @Override
    public int getItemCount() {
        return routes.size();
    }

}
