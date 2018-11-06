package com.xlog.xloguser.finaldriverapp;

import android.app.ProgressDialog;
import android.arch.persistence.room.Room;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.xlog.xloguser.finaldriverapp.Adapters.AllTransactionAdapter;
import com.xlog.xloguser.finaldriverapp.Adapters.DashboadAdapter;
import com.xlog.xloguser.finaldriverapp.Adapters.PendingTransactionAdapter;
import com.xlog.xloguser.finaldriverapp.Api.Api;
import com.xlog.xloguser.finaldriverapp.Data.TrasactionListSet;
import com.xlog.xloguser.finaldriverapp.Model.AllTransactionModel;
import com.xlog.xloguser.finaldriverapp.Model.ModelReservationList.ReservationList;
import com.xlog.xloguser.finaldriverapp.Model.PendingTransactionModel;
import com.xlog.xloguser.finaldriverapp.Room.RmDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Jaymon Rivera on 09/14/2018.
 */
public class PendingTransactions extends AppCompatActivity {
    private static final String TAG = "PendingTransactions";
    private static RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private static RecyclerView recyclerView;
    List<ReservationList> transactionList;
    private RecyclerView.Adapter mAdapter;
    private Retrofit retrofit;
    private TextView warn;
    String dateString ="";
    PendingTransactionAdapter pendingTransactionAdapter;
    private ProgressDialog progressDialogdialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending_transactions);
        recyclerView = (RecyclerView) findViewById(R.id.pendingRecycleView);
        warn = (TextView) findViewById(R.id.warnTxtPending);
        recyclerView.setHasFixedSize(true);
        Toolbar mToolbar = (Toolbar) findViewById(R.id.pendingToolbar);
        mToolbar.setTitle("Pending Transactions");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        transactionList = new ArrayList<>();
        loadApi();
        internetChecking();
        SimpleDateFormat formatter
                = new SimpleDateFormat ("yyyy-MM-dd");
        Date currentTime_1 = new Date();
        dateString = formatter.format(currentTime_1);


    }
    public void loadApi(){
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.MINUTES)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .build();

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        retrofit = new Retrofit.Builder()
                .baseUrl(Api.URLQA)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }

    public void getAccesToken(){
        progressDialogdialog = new ProgressDialog(PendingTransactions.this);
        progressDialogdialog.setMessage("Fetching Data");
        progressDialogdialog.show();
        progressDialogdialog.setCancelable(false);
        progressDialogdialog.setCanceledOnTouchOutside(false);
        final RmDatabase db = Room.databaseBuilder(getApplicationContext(), RmDatabase.class,"Token")
                .build();

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                String value ="";
                for(int a = 0; a < db.rmDao().getToken().size(); a++){
                    Log.e("LOG___", "fetch_____ "+a +" "+ db.rmDao().getToken().get(a).getAccess_token());
                    value = db.rmDao().getToken().get(a).getAccess_token();

                }
                loadData(value);

            }
        });

    }
    public void loadData(String Token) {
        transactionList.clear();
        Api api = retrofit.create(Api.class);
        Call<List<ReservationList>> call = api.getReservationList(Token);

        call.enqueue(new Callback<List<ReservationList>>() {
            @Override
            public void onResponse(Call<List<ReservationList>> call, Response<List<ReservationList>> response) {
                if(response.isSuccessful()){
                    int value = response.body().size();
                    String date= "";
                    for(int t = 0; t < value; t++){
                        Log.e(TAG, "Response +"+response.body().get(t).getPrefixedId());
                        date = response.body().get(t).getDeliveryDates().get(0).getDeliveryAt().substring(0,10);
                        SimpleDateFormat formatApiDate = new SimpleDateFormat("yyyy-MM-dd");
                        SimpleDateFormat currentDate = new SimpleDateFormat("yyyy-MM-dd");
                        try {
                            Date date1 = formatApiDate.parse(date);
                            Date date2 = currentDate.parse(dateString);
                            if(date2.before(date1)) {
                                transactionList.addAll(Collections.singleton(response.body().get(t)));

                            }

                        } catch (ParseException e) {
                            e.printStackTrace();
                        }


                    }
                    if(transactionList.size() == 0){
                        warn.setVisibility(View.VISIBLE);
                        warn.setText("You don't have upcoming transactions");
                    }
                    loadPendingTransactions();
                    progressDialogdialog.dismiss();
                }

            }

            @Override
            public void onFailure(Call<List<ReservationList>> call, Throwable t) {

            }
        });

    }

    private void loadPendingTransactions(){

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        pendingTransactionAdapter = new PendingTransactionAdapter(transactionList);
        recyclerView.setAdapter(pendingTransactionAdapter);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem search = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) search.getActionView();
        searchView.setQueryHint("Search Transactions");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newQuery) {
                pendingTransactionAdapter.getFilter().filter(newQuery);
                return false;
            }
        });
        return true;
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        internetChecking();
    }
    private void internetChecking() {
        if (AppStatus.getInstance(getBaseContext()).isOnline()) {
            getAccesToken();
        } else {
            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(PendingTransactions.this);
            alertBuilder.setTitle("You're Offline");
            alertBuilder.setMessage("Please Check your network");
            String positiveText = "Retry";
            alertBuilder.setPositiveButton(positiveText,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            internetChecking();
                        }
                    });

            AlertDialog dialog = alertBuilder.create();
            dialog.show();
        }
    }
}
