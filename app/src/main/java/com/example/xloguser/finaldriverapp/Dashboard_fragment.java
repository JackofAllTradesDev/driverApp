package com.example.xloguser.finaldriverapp;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.xloguser.finaldriverapp.Adapters.DashboardTransactionAdapter;
import com.example.xloguser.finaldriverapp.Data.TrasactionListSet;
import com.example.xloguser.finaldriverapp.Model.DashboardTransactionsModel;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class Dashboard_fragment extends Fragment {

    private static RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private static RecyclerView recyclerView;
    private static ArrayList<DashboardTransactionsModel> data;
    private CardView cv;
    static View.OnClickListener myOnClickListener;
    public Dashboard_fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_dashboard_fragment, container, false);


        recyclerView = (RecyclerView) v.findViewById(R.id.transactionRecycleViewer);
        cv = (CardView) v.findViewById(R.id.cardViewToday);
        recyclerView.setHasFixedSize(true);


        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        data = new ArrayList<DashboardTransactionsModel>();

        Log.e("LOG", "element: "+ TrasactionListSet.transactionID.length);
        for (int i = 0; i < TrasactionListSet.transactionID.length; i++) {
            data.add(new DashboardTransactionsModel(
                    TrasactionListSet.transactionID[i],
                    TrasactionListSet.contentList[i]
            ));
        }


        Log.e("LOG", "Msg: "+data);
        adapter = new DashboardTransactionAdapter(data);
        recyclerView.setAdapter(adapter);


        return v;
    }

}
