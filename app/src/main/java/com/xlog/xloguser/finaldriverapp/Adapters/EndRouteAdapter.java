package com.xlog.xloguser.finaldriverapp.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xlog.xloguser.finaldriverapp.Interface.Attachment;
import com.xlog.xloguser.finaldriverapp.Model.ModelReservationList.Route;
import com.xlog.xloguser.finaldriverapp.R;
import com.xlog.xloguser.finaldriverapp.RoutesActivity;

import java.util.List;

public class EndRouteAdapter  extends RecyclerView.Adapter<EndRouteAdapter.MyViewHolder>{

    public EndRouteAdapter(List<Route> routes, Attachment listener) {
        this.routes = routes;
        attachment = listener;
    }

    List<Route> routes;
    Context context;
    private Attachment attachment;

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.route_list, parent, false);
        return new EndRouteAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final EndRouteAdapter.MyViewHolder holder, int position) {
        final Route reservationLists = routes.get(position);

        char[] alphabet = {'a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z'};

        holder.addressTxt.setText(reservationLists.getFormattedAddress());
        holder.route.setText("Route "+String.valueOf(alphabet[position]));
        if(reservationLists.getRoutestatus().isEmpty()){
            holder.status.setText("On-going");
            holder.status.setTextColor(Color.parseColor("#ff0000"));

        }else {
            holder.status.setText("Completed");
            holder.cv.setEnabled(false);
        }

        holder.cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("TAGGG", "PLACE_ID "+reservationLists.getId());
                attachment.unHide();
                attachment.sendID(reservationLists.getId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return routes.size();
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView route;
        public TextView addressTxt;
        public TextView status;
        CardView cv;

        public MyViewHolder(View itemView) {
            super(itemView);
            this.route = (TextView) itemView.findViewById(R.id.routeTxt);
            this.addressTxt = (TextView) itemView.findViewById(R.id.addressTxt);
            this.status = (TextView) itemView.findViewById(R.id.statusTxtRoute);
            this.cv = (CardView) itemView.findViewById(R.id.routeCv);
        }
    }

}
