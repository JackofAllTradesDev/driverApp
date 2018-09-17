package com.example.xloguser.finaldriverapp;


import android.content.Intent;
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
 * Created by Jaymon Rivera on 09/14/2018.
 */
/**
 * A simple {@link Fragment} subclass.
 */
public class Dashboard_fragment extends Fragment {

    private static RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private static RecyclerView recyclerView;
    private static ArrayList<DashboardTransactionsModel> data;
    private CardView cvToday;
    private CardView cvUpComing;
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
        cvToday = (CardView) v.findViewById(R.id.cardViewToday);
        cvUpComing = (CardView) v.findViewById(R.id.cardViewUpComing);
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

        cvToday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), TodayUpcomingActivity.class);
                startActivity(intent);
            }
        });

        cvUpComing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), TodayUpcomingActivity.class);
                startActivity(intent);
            }
        });


        Log.e("LOG", "Msg: "+data);
        adapter = new DashboardTransactionAdapter(data);
        recyclerView.setAdapter(adapter);


        return v;
    }

}
