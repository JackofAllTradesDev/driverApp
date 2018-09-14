package com.example.xloguser.finaldriverapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.example.xloguser.finaldriverapp.Adapters.CompletedTransactionAdapter;
import com.example.xloguser.finaldriverapp.Data.TrasactionListSet;
import com.example.xloguser.finaldriverapp.Model.AllTransactionModel;
import com.example.xloguser.finaldriverapp.Model.CompletedTransactionModel;

import java.util.ArrayList;

public class CompleteTransactions extends AppCompatActivity {
    private static RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private static RecyclerView recyclerView;
    private static ArrayList<CompletedTransactionModel> data;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_transactions);
        recyclerView = (RecyclerView) findViewById(R.id.completeRecycleView);
        recyclerView.setHasFixedSize(true);
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbarCompleteTransaction);
        mToolbar.setTitle("Completed Transactions");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        layoutManager = new LinearLayoutManager(getBaseContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        loadCompleteTransactions();
    }

    private void loadCompleteTransactions(){
        data = new ArrayList<CompletedTransactionModel>();

        Log.e("LOG", "element: "+ TrasactionListSet.transactionID.length);
        for (int i = 0; i < TrasactionListSet.transactionID.length; i++) {
            data.add(new CompletedTransactionModel(
                    TrasactionListSet.transactionID[i],
                    TrasactionListSet.contentList[i]
            ));
        }


        Log.e("LOG", "Msg: "+data);
        adapter = new CompletedTransactionAdapter(data);
        recyclerView.setAdapter(adapter);


    }
}
